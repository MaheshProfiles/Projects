package com.snapbizz.snapstock.asynctasks;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.j256.ormlite.dao.GenericRawResults;
import com.snapbizz.snaptoolkit.R;
import com.snapbizz.snaptoolkit.domains.Brand;
import com.snapbizz.snaptoolkit.domains.InventorySku;
import com.snapbizz.snaptoolkit.domains.ProductCategory;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SkuUnitType;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class SearchInventorySkuTask extends
AsyncTask<String, Void, List<InventorySku>> {

    private int taskCode;
    private OnQueryCompleteListener onQueryCompleteListener;
    private final String errorMessage = "No Product Found";
    private Context context;

    public SearchInventorySkuTask(Context context,
            OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
        this.onQueryCompleteListener = onQueryCompleteListener;
        this.taskCode = taskCode;
        this.context = context;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    protected List<InventorySku> doInBackground(
            String... params) {
        // TODO Auto-generated method stub
        try {
            String excludedProducts= "";
            if (context==null)
                return null;
            int size=context.getResources().getStringArray(R.array.quickadd_array).length;
            for(int i =0;i<size;i++){
                if(i!=size-1)
                    excludedProducts = excludedProducts +"'"+ context.getResources().getStringArray(R.array.quickadd_array)[i]+"', ";
                else
                    excludedProducts = excludedProducts +"'"+context.getResources().getStringArray(R.array.quickadd_array)[i]+"'";

            }
            String sqlQuery = "select inventory_sku.inventory_sku_id,inventory_sku.inventory_sku_quantity,inventory_sku.is_offer,inventory_sku.purchase_price,inventory_sku.tax_rate,inventory_sku.show_store,inventory_sku.creation_timestamp,inventory_sku.inventory_slno,inventory_sku.lastmodified_timestamp," +
                    "product_sku.sku_id,product_sku.sku_name,product_sku.sku_mrp,product_sku.sku_subcategory_id,product_sku.brand_id,product_sku.product_imageurl,product_sku.sku_saleprice,product_sku.alternate_mrp,product_sku.sku_alternate_saleprice,product_sku.sku_units,product_sku.is_gdb,product_sku.lastmodified_timestamp,product_sku.is_quickadd_product,inventory_sku._id,product_sku._id,product_sku.is_gdb from product_sku " +
                    "left join inventory_sku on inventory_sku.inventory_sku_id=product_sku.sku_id where (product_sku.sku_name like('%"+params[0]+"%') or product_sku.sku_id like ('%"+params[0]+"%')) and product_sku.sku_id not in ("+excludedProducts+") order by inventory_sku.inventory_slno desc limit 100";
            
            GenericRawResults<String[]> rawResults = SnapCommonUtils.getDatabaseHelper(context).getProductSkuDao().queryRaw(sqlQuery);
            
            List<InventorySku> resultList = new ArrayList<InventorySku>();
            SnapBizzDatabaseHelper databaseHelper = SnapCommonUtils.getDatabaseHelper(context);
            for(String [] row : rawResults.getResults()) {
                InventorySku invObj;
                ProductSku prodObj;
                ProductCategory category = databaseHelper.getProductCategoryDao().queryBuilder().where().eq("product_category_id",Integer.parseInt(row[12])).queryForFirst();
                Brand brand= new  Brand();
                brand.setBrandId(Integer.parseInt(row[13]));
                SkuUnitType unittype = SkuUnitType.valueOf(row[18]);
                Date lastModifiedDate=Calendar.getInstance().getTime();
                try{
                    lastModifiedDate = new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).parse(row[20]);
                }catch(Exception e){
                    e.printStackTrace();
                }
                if(row[0]==null || row[0].isEmpty()){
                    prodObj = new ProductSku(row[10], Float.parseFloat(row[11]), row[9], row[14], unittype, false, brand, category, lastModifiedDate, row[19].equals("1")? true: false,Float.parseFloat(row[15]));
                    if(row[16]!= null && !row[16].isEmpty())
                        prodObj.setProductSkuAlternateMrp(Float.parseFloat(row[16]));
                    if(row[17]!= null && !row[17].isEmpty())
                        prodObj.setProductSkuAltSalePrice(Float.parseFloat(row[17]));
                    invObj = new InventorySku(prodObj, 0, Calendar.getInstance().getTime(), prodObj.getProductSkuCode(), 0, 0);
                    invObj.setStore(false);
                    invObj.setOffer(false);
                }else {
                    Date lastModifiedTimeStamp=Calendar.getInstance().getTime();
                    try{
                        lastModifiedTimeStamp = new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).parse(row[20]);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    prodObj = new ProductSku(row[10], Float.parseFloat(row[11]), row[9], row[14], unittype, true, brand, category,lastModifiedTimeStamp, row[19].equals("1")? true: false,Float.parseFloat(row[15]));
                    if(row[21]!=null)
                        prodObj.setQuickAddProduct(row[21].equals("1")? true: false);
                    Date date = Calendar.getInstance().getTime();
                    try {
                        date = new SimpleDateFormat(SnapToolkitConstants.STANDARD_DATEFORMAT).parse(row[6]);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                    if(row[16]!= null && !row[16].isEmpty())
                        prodObj.setProductSkuAlternateMrp(Float.parseFloat(row[16]));
                    if(row[17]!= null && !row[17].isEmpty())
                        prodObj.setProductSkuAltSalePrice(Float.parseFloat(row[17]));
                    invObj = new InventorySku(prodObj,Float.parseFloat(row[1]) , date, prodObj.getProductSkuCode(),Float.parseFloat(row[3]) , Float.parseFloat(row[4]));
                    invObj.setStore(row[5].equals("1")? true: false);
                    invObj.setOffer(row[2].equals("1")? true: false);
                    invObj.setSlNo(Integer.parseInt(row[7]));
                    try {
                        invObj.setLastModifiedTimestamp(new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT).parse(row[8]));
                    } catch (Exception e) {
                        invObj.setLastModifiedTimestamp(Calendar.getInstance().getTime());
                    }
                }
                resultList.add(invObj);
            }
            /*
            QueryBuilder<ProductSku, Integer> productSkuQueryBuilder = databaseHelper.getProductSkuDao().queryBuilder();
            productSkuQueryBuilder.setWhere(productSkuQueryBuilder.where().like("sku_name", "%"+params[0]+"%").or().like("sku_id", "%"+params[0]+"%"));
            Log.d(SearchInventorySkuTask.class.getName(), "query "+databaseHelper.getInventorySkuDao().queryBuilder().join(productSkuQueryBuilder).prepareStatementString());
            List<InventorySku> resultList = databaseHelper.getInventorySkuDao().queryBuilder().leftJoin(productSkuQueryBuilder).query();*/
            List<ProductCategory> parentProdList = new ArrayList<ProductCategory>();
            for(int i=0;i<resultList.size();i++){
                Log.d("product name", resultList.get(i).getProductSku().getProductSkuName() + " : " + resultList.get(i).getProductSku().getProductSkuCode() );
                parentProdList.add(databaseHelper.getProductCategoryDao().queryBuilder().where().eq("product_category_id", resultList.get(i).getProductSku().getProductCategory().getParenCategory().getCategoryId()).queryForFirst());
            }for(int i=0;i<resultList.size();i++){
                resultList.get(i).getProductSku().getProductCategory().setParenCategory(parentProdList.get(i));
            }
            return resultList;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<InventorySku> result) {
        // TODO Auto-generated method stub
        if (null != result && result.size() > 0) {
            onQueryCompleteListener.onTaskSuccess(result, taskCode);
        } else {
            onQueryCompleteListener.onTaskError(errorMessage, taskCode);
        }
    }
}
