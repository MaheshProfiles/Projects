package com.snapbizz.snaptoolkit.asynctasks;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.table.TableUtils;
import com.snapbizz.snaptoolkit.domains.BillItem;
import com.snapbizz.snaptoolkit.domains.Brand;
import com.snapbizz.snaptoolkit.domains.BrandContainer;
import com.snapbizz.snaptoolkit.domains.Company;
import com.snapbizz.snaptoolkit.domains.CompanyContainer;
import com.snapbizz.snaptoolkit.domains.ProductCategory;
import com.snapbizz.snaptoolkit.domains.ProductCategoryContainer;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.JsonParser;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitTextFormatter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;

public class UpdateBrandListTask extends AsyncTask<Void, Integer, Boolean> {
	//private Context activityContext;
	private Activity mActivity;
	private String mErrorMessage = "Unable to update Brand List";
	private ProgressDialog mProgressDialog;
	private OnQueryCompleteListener mOnQueryCompleteListener;
	private int mTaskCode;

	public UpdateBrandListTask(Activity activity,
			OnQueryCompleteListener mOnQueryCompleteListener, int mTaskCode) {
		//this.activityContext = activityContext;
		this.mActivity = activity;
		this.mOnQueryCompleteListener = mOnQueryCompleteListener;
		this.mTaskCode = mTaskCode;
	}

	@Override
	protected void onPreExecute() {
        mProgressDialog = new ProgressDialog(mActivity);
        mProgressDialog.setTitle("Updating Brand list");
        mProgressDialog.setMessage("Please Wait...Do not close application..");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
	};

	@Override
	protected Boolean doInBackground(Void... arg0) {
		try {
			insertNewCompany();
			if(!insertNewBrand()){
				return false;
			}
			updateCategory();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
        return false;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if (mActivity.isDestroyed()) {
			mOnQueryCompleteListener.onTaskError(mErrorMessage, mTaskCode);
			return;
		}
		Log.d("TAG", "onPostExecute---this.mProgressDialog->" + this.mProgressDialog);
		Log.d("TAG", "insertNewBrand---mProgressDialog->" + this.mProgressDialog.isShowing());
		try {
            if(this.mProgressDialog != null && this.mProgressDialog.isShowing())
                this.mProgressDialog.dismiss();
		} catch(Exception e) {}
		mProgressDialog = null;
		if (result) {
			mOnQueryCompleteListener.onTaskSuccess(result, mTaskCode);
		} else {
			mOnQueryCompleteListener.onTaskError(mErrorMessage, mTaskCode);
		}
	}

	private boolean insertNewBrand() {
		AssetManager am = mActivity.getAssets();
		try {
			String[] resultArray = null;
			try {
				GenericRawResults<String[]> rawResults = SnapCommonUtils.getDatabaseHelper(mActivity).getBrandDao()
					                                     .queryRaw("select brand_name from brand where is_mystore = 1");
				Log.d("TAG", "insertNewBrand---rawResults->" + rawResults);
				List<String[]> results = rawResults.getResults();
				resultArray = new String[results.size()];
				for(int i=0;i< results.size();i++) {
					String[] result = results.get(i);
					resultArray[i] = result[0];
				}
			}catch(Exception e){
				Log.e("TAG", "insertNewBrand---rawResults->" ,e);	
				return false;
			}
			TableUtils.dropTable(SnapCommonUtils.getDatabaseHelper(mActivity)
					.getConnectionSource(), Brand.class, true);
			TableUtils.createTable(SnapCommonUtils.getDatabaseHelper(mActivity)
					.getConnectionSource(), Brand.class);

			InputStream is = am.open("newbrand.txt");
			BrandContainer brandContainer = (BrandContainer) JsonParser
					.parseJson(is, BrandContainer.class);
			Log.d("", "insertNewBrand---brand->"
					+ brandContainer.getBrandList().size());
			List<Brand> brandList = brandContainer.getBrandList();
			for (int i = 0; i < brandList.size(); i++) {
				Brand brand = brandList.get(i);
				Log.d("", "insertNewBrand---brand->" + brand.getBrandName());
				Company company = new Company();
				brand.setCompany(company);
				company.setCompanyId(brand.getCompnayId());
				brand.setHasImage(SnapCommonUtils.hasBrandDrawable(brand, mActivity));
				Log.d("", "has brand drawable " + brand.isHasImage());
				if (brand.isHasImage())
					brand.setBrandImageUrl("brands/"
							+ brand.getBrandName().toLowerCase() + ".jpg");
				brand.setGDB(true);
				StringBuffer stringbf = new StringBuffer();
				Matcher m = Pattern.compile("([a-z])([a-z]*)",
						Pattern.CASE_INSENSITIVE).matcher(brand.getBrandName());
				while (m.find()) {
					m.appendReplacement(stringbf, m.group(1).toUpperCase()
							+ m.group(2).toLowerCase());
				}

				if (resultArray != null && Arrays.asList(resultArray).contains(
						m.appendTail(stringbf).toString())||Arrays.asList(resultArray).contains(brand.getBrandName())) {
					Log.d("", "insertNewBrand---resultArray-> true---->"+brand.getBrandName());
					brand.setMyStore(true);
				}

				SnapCommonUtils.getDatabaseHelper(mActivity).getBrandDao().createOrUpdate(brand);

				Log.d("", "created brand " + brand.getBrandName()
						+ " company id " + brand.getCompany().getCompanyId());
			}
			Log.d("", "insertNewBrand---Done->");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	private void insertNewCompany() {
		AssetManager am = mActivity.getAssets();
		try {
			InputStream is = am.open("newcompany.txt");
			CompanyContainer companyContainer = (CompanyContainer) JsonParser
					.parseJson(is, CompanyContainer.class);
			List<Company> companyList = companyContainer.getCompanyList();
			for (Company company : companyList) {
				List<Company> currentCompanyList = SnapCommonUtils.getDatabaseHelper(mActivity).getCompanyDao().queryBuilder()
            			.where().eq("company_name",new SelectArg(company.getCompanyName())).query();
            	if (currentCompanyList.isEmpty()) {
            		company.setGDB(true);
    				SnapCommonUtils.getDatabaseHelper(mActivity).getCompanyDao().createOrUpdate(company);
    				Log.d("", "created company " + company.getCompanyName());
            	}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 private void updateCategory() {
	        Log.d("","update category");
	        AssetManager am = mActivity.getAssets();
	        try {
	            InputStream is = am.open("category.txt");
	            ProductCategoryContainer productCategoryContainer = (ProductCategoryContainer) JsonParser.parseJson(is, ProductCategoryContainer.class);
	            List<ProductCategory> categoryList = productCategoryContainer.getProductCategoryList();
	            for (ProductCategory productCategory : categoryList) {
	            	List<ProductCategory> productCategoryList = SnapCommonUtils.getDatabaseHelper(mActivity).getProductCategoryDao().queryBuilder()
	            			.where().like("product_category_name",new SelectArg(productCategory.getCategoryName())).query();
	            	if (productCategoryList.isEmpty()) {
	            		productCategory.setCategoryName(SnapToolkitTextFormatter.capitalseText(productCategory.getCategoryName()));
	            		int quickadd = productCategory.isQuickAddCategory() ? 1 : 0;	            	
	            		SnapCommonUtils.getDatabaseHelper(mActivity).getProductCategoryDao()
	            		.executeRaw("INSERT INTO product_category (product_category_name,product_parentcategory_id," +
					   		"is_quickadd_category,profit_margin) VALUES ('" + productCategory.getCategoryName() + "',"+productCategory.getParentId() 
					   		+ ","+quickadd + ","+productCategory.getProfitMargin() + ")");
	            		Log.d("", "created Category " + productCategory.getCategoryName());
	            	}
	            	
	            }
	            Log.d("","update category success");
	        } catch (Exception e1) {
	            e1.printStackTrace();
	        }
	    }
}
