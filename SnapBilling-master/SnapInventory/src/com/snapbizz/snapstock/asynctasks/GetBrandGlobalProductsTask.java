package com.snapbizz.snapstock.asynctasks;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.j256.ormlite.dao.GenericRawResults;
import com.snapbizz.snapstock.utils.SnapInventoryUtils;
import com.snapbizz.snaptoolkit.domains.Brand;
import com.snapbizz.snaptoolkit.domains.ProductCategory;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SkuUnitType;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class GetBrandGlobalProductsTask extends
AsyncTask<Integer, Void, List<ProductSku>> {

    private OnQueryCompleteListener onQueryCompleteListener;
    private Context context;
    private int taskCode;
    private final String errorMessage = "";
    private int subcategoryId;	
    public GetBrandGlobalProductsTask(Context context,
            OnQueryCompleteListener onQueryCompleteListener, int taskCode, int subcategoryId) {
        this.context = context;
        this.onQueryCompleteListener = onQueryCompleteListener;
        this.taskCode = taskCode;
        this.subcategoryId=subcategoryId;
    }

    @Override
    protected List<ProductSku> doInBackground(Integer... brand) {
        try {
            ArrayList<ProductSku> prodList= new ArrayList<ProductSku>();
            GenericRawResults<String[]> rawResults;
            String globalProductsQuery = "SELECT product_sku.sku_name,product_sku.sku_mrp,product_sku.sku_id," +
                    "product_sku.product_imageurl,product_sku.sku_units,brand.brand_id,brand.brand_name,brand.brand_imageurl,inventory_sku.inventory_sku_id,product_sku.lastmodified_timestamp,product_category.product_parentcategory_id, product_sku.is_gdb FROM product_sku INNER JOIN brand"
                    + " ON product_sku.brand_id = brand.brand_id inner join product_category on product_category.product_category_id = product_sku.sku_subcategory_id LEFT JOIN inventory_sku ON "
                    + "product_sku.sku_id = inventory_sku.inventory_sku_id  "
                    + "WHERE product_sku.sku_subcategory_id ='"+ subcategoryId +"' and product_sku.brand_id IN (";
            for(int i = 0; i < brand.length; i++) {
                globalProductsQuery +="'"+brand[i]+"',";
            }
            rawResults = SnapInventoryUtils
                    .getDatabaseHelper(context)
                    .getProductSkuDao()
                    .queryRaw(globalProductsQuery.substring(0, globalProductsQuery.length() - 1)+")");

            for (String[] strings : rawResults.getResults()) {
                boolean isSelected=false;
                Brand prodBrand= new Brand();
                prodBrand.setBrandId(Integer.parseInt(strings[5]));
                prodBrand.setBrandName(strings[6]);
                prodBrand.setBrandImageUrl(strings[7]);
                ProductCategory prodCategory= new ProductCategory();
                prodCategory.setCategoryId(subcategoryId);
                ProductCategory parentCategory=new ProductCategory();
                parentCategory.setCategoryId(Integer.parseInt(strings[10]));
                prodCategory.setParenCategory(parentCategory);
                SkuUnitType skuUnitType=SkuUnitType.valueOf(strings[4]);
                if(strings[8]!=null){
                    isSelected=true;
                }
                Date lastmodDate = null;
                try {
                    lastmodDate = new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).parse(strings[9]);
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                prodList.add(new ProductSku(strings[0],Float.parseFloat(strings[1]), strings[2], strings[3],skuUnitType, isSelected,prodBrand,prodCategory,lastmodDate, Integer.parseInt(strings[11]) == 0 ? false : true));
            }
            return prodList;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<ProductSku> result) {
        // TODO Auto-generated method stub
        if (result != null) {
            onQueryCompleteListener.onTaskSuccess(result, taskCode);
        } else
            onQueryCompleteListener.onTaskError(errorMessage, taskCode);
    }

}
