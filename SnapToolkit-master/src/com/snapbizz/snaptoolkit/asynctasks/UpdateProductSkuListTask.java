package com.snapbizz.snaptoolkit.asynctasks;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;

import com.snapbizz.snaptoolkit.domains.Brand;
import com.snapbizz.snaptoolkit.domains.BrandContainer;
import com.snapbizz.snaptoolkit.domains.Company;
import com.snapbizz.snaptoolkit.domains.CompanyContainer;
import com.snapbizz.snaptoolkit.domains.ProductCategory;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.domains.ProductSkuContainer;
import com.snapbizz.snaptoolkit.domains.ProductSkuOld;
import com.snapbizz.snaptoolkit.domains.RetrieveBrandResponseContainer;
import com.snapbizz.snaptoolkit.domains.RetrieveCompanyResponseContainer;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.JsonParser;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class UpdateProductSkuListTask extends AsyncTask<Void, Integer, Boolean>{

	private Context activityContext;
	private Context mContext;
	private String mErrorMessage = "Unable to update Product List";
	private final String JSON_FILE_NAME = "newProdJson.txt";
	private ProgressDialog mProgressDialog;
	private OnQueryCompleteListener mOnQueryCompleteListener;
	private int mTaskCode;
	
	public UpdateProductSkuListTask(Context activityContext, Context context, OnQueryCompleteListener mOnQueryCompleteListener, int mTaskCode) {
		this.activityContext = activityContext;
		this.mContext = context;
		this.mOnQueryCompleteListener = mOnQueryCompleteListener;
		this.mTaskCode = mTaskCode;
	}
	
	@Override
	protected void onPreExecute() {
		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setTitle("Updating product sku list");
		mProgressDialog.setMessage("Please Wait...this would take 30-45min...Do not close application.");
		mProgressDialog.setCancelable(false);
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.show();
	};
	
	@Override
	protected Boolean doInBackground(Void... arg0) {
		try {
			//insertNewCompany() ;
			String timestamp = SnapSharedUtils.getLastProductSkuSyncTimestamp(activityContext);
			SimpleDateFormat dateFormat = new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT, Locale.getDefault());
			Date date = dateFormat.parse(timestamp);
			date.setTime(date.getTime() - 1000);
			SnapBizzDatabaseHelper helper = SnapCommonUtils.getDatabaseHelper(mContext);
            AssetManager am = mContext.getAssets();
            InputStream is = am.open(JSON_FILE_NAME);
            ProductSkuContainer productSkuContainer = (ProductSkuContainer) JsonParser.parseJson(is, ProductSkuContainer.class);
            List<ProductSkuOld> prodList = productSkuContainer.getProductSkuList();
            ProductSku newProductSku;
            for(ProductSkuOld productSku : prodList) {
                try {
                	newProductSku = new ProductSku();
                	newProductSku.setGDB(true);
                	newProductSku.setProductSkuName(productSku.getProductSkuName());
                	newProductSku.setProductSkuCode(productSku.getProductSkuCode());
                	newProductSku.setProductSkuMrp(productSku.getProductSkuMrp());
                	newProductSku.setProductSkuSalePrice(productSku.getProductSkuMrp());
                	newProductSku.setProductSkuAlternateMrp(productSku.getProductSkuMrp());
                	newProductSku.setHasImage(false);
                	Log.d("","productSku.brandId)---->"+helper.getProductCategoryDao().queryForEq("product_category_id", productSku.subcategoryId).get(0));
                    ProductCategory category = helper.getProductCategoryDao().queryForEq("product_category_id", productSku.subcategoryId).get(0);
                    newProductSku.setProductCategory(category);
                    newProductSku.setSubcategoryId(category.getCategoryId());
                    Log.d("","productSku.brandId)---->"+productSku.brandId);
                    Log.d("","productSku.brandId)---->"+helper.getBrandDao().queryForEq("brand_id", productSku.brandId).size());
                    List<Brand> brandList = helper.getBrandDao().queryForEq("brand_id", productSku.brandId);
                    if(brandList.isEmpty()) // Some error, needs restarting
                        return false;
                    Brand brand = (Brand) brandList.get(0);
                    newProductSku.setProductBrand(brand);
                    newProductSku.setLastModifiedTimestamp(dateFormat.parse(SnapSharedUtils.getLastProductSkuSyncTimestamp(activityContext)));
                    helper.getProductSkuDao().createOrUpdate(newProductSku);
                    System.out.println("created" + productSku.getProductSkuCode()+" has image "+productSku.isHasImage());
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            SnapSharedUtils.storeProductSkuListUpdate(true, activityContext);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e1) {
			e1.printStackTrace();
		}
	    return false;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		try{
			if (mProgressDialog!=null && mProgressDialog.isShowing())
				mProgressDialog.dismiss();
		  } catch(Exception e) {}	
		mProgressDialog = null;
		if (result) {
			mOnQueryCompleteListener.onTaskSuccess(result, mTaskCode);
		} else {
			mOnQueryCompleteListener.onTaskError(mErrorMessage, mTaskCode);
		}
		
	}
	
    
}
