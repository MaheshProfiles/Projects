
package com.snapbizz.snapbilling.asynctasks;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.os.AsyncTask;

import com.j256.ormlite.stmt.QueryBuilder;
import com.snapbizz.snapbilling.utils.SnapBillingUtils;
import com.snapbizz.snaptoolkit.domains.Brand;
import com.snapbizz.snaptoolkit.domains.InventorySku;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;

public class QueryForgottenProducts extends
AsyncTask<ArrayList<String>, Void, List<List<InventorySku>> > {
    private Context context;
    private int taskCode;
    private OnQueryCompleteListener onQueryCompleteListener;
    private final String errorMessage = "No Inventory Found";

    public QueryForgottenProducts(Context context,
            OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
        this.context = context;
        this.onQueryCompleteListener = onQueryCompleteListener;
        this.taskCode = taskCode;
    }

    @Override
    protected List<List<InventorySku>> doInBackground(
            ArrayList<String>... params) {
        try {
            QueryBuilder<ProductSku, Integer> productSkuQueryBuilder = SnapBillingUtils.getHelper(
                    context).getProductSkuDao().queryBuilder();
            productSkuQueryBuilder.where().eq("has_image", true);
            QueryBuilder<Brand, Integer> brandQueryBuilder = SnapBillingUtils.getHelper(
                    context).getBrandDao().queryBuilder();
            brandQueryBuilder.where().eq("has_image", true);
            productSkuQueryBuilder.join(brandQueryBuilder);
            List<List<InventorySku>> resultList = new ArrayList<List<InventorySku>>();
            List<InventorySku> innerResultList = new ArrayList<InventorySku>();
            List<InventorySku> ansList = SnapBillingUtils.getHelper(context).getInventorySkuDao()
                    .queryBuilder().join(productSkuQueryBuilder).query();
            InventorySku inv=null;
            Random random= new  Random();
            if (params[0] != null) {
                for (int i = 0; i <30; i++) {
                    if(i%5>=3 && i!=0){
                        if(ansList.size()>0)
                            inv=ansList.get(random.nextInt(ansList.size()));
                        else{
                            inv=null;
                        }
                    }
                    else if(i==0){
                            inv=new InventorySku();
                            inv.setForgotten(true);
                    }
                    else{
                        inv=new InventorySku();
                        inv.setForgotten(true);
                    }
                    innerResultList.add(inv);
                }
                resultList.add(innerResultList);
            }
            int i = 0;
            int size = resultList.get(0).size();
            while (i < size) {
                if (i != 0 && i % 2 == 0) {
                    ArrayList<InventorySku> tempInventorySkuList = new ArrayList<InventorySku>();
                    int limit;
                    int ressize=resultList.get(0).size();
                    if(ressize<=4){
                        if(ressize%2==0){
                            limit=4;
                        }else{
                            limit=(resultList.get(0).size()%2)+2;
                        }
                    }else{
                        limit=4;
                    }
                    for (int j = 2; j < limit;i++) {
                        if (resultList.get(0) != null
                                && resultList.get(0).size() > 2) {
                            tempInventorySkuList.add(resultList.get(0)
                                    .get((j)));
                            resultList.get(0).remove(j);
                            limit--;
                        }else{
                            j++;
                        }
                    }
                    resultList.add(tempInventorySkuList);
                 /*   
                    int limit = resultList.get(0).size() - 11;
                    for (int j = resultList.get(0).size() - 1; j > limit; j--, i++) {
                        if (resultList.get(0) != null
                                && resultList.get(0).size() > 10) {
                            tempInventorySkuList.add(resultList.get(0)
                                    .get((j)));
                            resultList.get(0).remove(j);
                        }
                    }*/
                } else {
                    i++;
                }
            }
            return resultList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<List<InventorySku>>  result) {
        if (result != null && result.size() != 0) {
            System.out.println("got result success");
            onQueryCompleteListener.onTaskSuccess(result, taskCode);
        } else {
            System.out.println(" result.size() " + result.size());
            onQueryCompleteListener.onTaskError(errorMessage, taskCode);
        }
    }

}
