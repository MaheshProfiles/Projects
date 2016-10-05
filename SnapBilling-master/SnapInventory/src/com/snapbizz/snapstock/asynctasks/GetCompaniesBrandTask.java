package com.snapbizz.snapstock.asynctasks;

import java.util.List;

import net.sqlcipher.SQLException;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.SparseBooleanArray;

import com.snapbizz.snaptoolkit.domains.Brand;
import com.snapbizz.snaptoolkit.domains.Distributor;
import com.snapbizz.snaptoolkit.domains.DistributorBrandMap;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;

public class GetCompaniesBrandTask extends AsyncTask<Integer, Void, List<Brand>>{

    private Context context;
    private OnQueryCompleteListener onQueryCompleteListener;
    private final String ERROR_MESSAGE = "Unable to get Company Brands";
    private int taskCode;
    private ProgressDialog progressDialog;
    private Distributor distributor;
    private Integer[] distributorCompanyList;

    public GetCompaniesBrandTask(Context context,
            OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
        this.context = context;
        this.taskCode = taskCode;
        this.onQueryCompleteListener = onQueryCompleteListener;
        progressDialog = new ProgressDialog(context);
    }

    public GetCompaniesBrandTask(Context context,
            OnQueryCompleteListener onQueryCompleteListener, int taskCode, Distributor distributor, Integer[] distributorCompanyList) {
        this.context = context;
        this.taskCode = taskCode;
        this.onQueryCompleteListener = onQueryCompleteListener;
        progressDialog = new ProgressDialog(context);
        this.distributor = distributor;
        this.distributorCompanyList = distributorCompanyList;
    }

    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
        progressDialog.show();
    }

    @Override
    protected List<Brand> doInBackground(Integer... params) {
        try {
            SnapBizzDatabaseHelper databaseHelper = SnapCommonUtils.getDatabaseHelper(context);
            List<Brand> brandList = databaseHelper.getBrandDao().queryBuilder().orderBy("brand_name", true).where().in("company_id", params).query();
            if(distributor != null) {
            	SparseBooleanArray newCompanyArray = new SparseBooleanArray();
            	for(Integer companyId : params) {
            		boolean isContains = false;
            		for(Integer distCompanyId : distributorCompanyList) {
            			if(distCompanyId.equals(companyId)) {
            				isContains = true;
            				break;
            			}
            		}
            		if(!isContains) {
            			newCompanyArray.put(companyId, true);
            		}
            	}
            	List<DistributorBrandMap> distributorBrandList = databaseHelper.getDistributorBrandMapDao().queryForEq("distributor_id", distributor.getDistributorId());
                for(Brand selectedBrand : brandList) {
                    for(DistributorBrandMap distributorBrand : distributorBrandList) {
                        if(distributorBrand.getDistributorBrand().getBrandId() == selectedBrand.getBrandId()) {
                            selectedBrand.setSelected(true);
                            break;
                        }
                    }
                    if(!selectedBrand.isSelected()) {
                    	if(newCompanyArray.get(selectedBrand.getCompany().getCompanyId()) == true)
                    		selectedBrand.setSelected(true);
                    }
                }
            } else {
                for(Brand selectedBrand : brandList) {
                    selectedBrand.setSelected(true);
                }
            }
            return brandList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } catch (java.sql.SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

    }

    @Override
    protected void onPostExecute(List<Brand> result) {
        // TODO Auto-generated method stub
        progressDialog.dismiss();
        if (null != result && result.size() > 0) {
            onQueryCompleteListener.onTaskSuccess(result, taskCode);
        } else {
            onQueryCompleteListener.onTaskError(ERROR_MESSAGE, taskCode);
        }
    }
}
