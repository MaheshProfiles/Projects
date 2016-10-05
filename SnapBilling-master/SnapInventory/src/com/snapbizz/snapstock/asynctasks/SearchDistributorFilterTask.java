package com.snapbizz.snapstock.asynctasks;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.j256.ormlite.stmt.QueryBuilder;
import com.snapbizz.snapstock.utils.SnapInventoryUtils;
import com.snapbizz.snaptoolkit.domains.CompanyDistributorMap;
import com.snapbizz.snaptoolkit.domains.Distributor;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;

public class SearchDistributorFilterTask extends AsyncTask<String, Void, List<Distributor>> {

	private int taskCode;
	private OnQueryCompleteListener onQueryCompleteListener;
	private final String errorMessage = "No Company Found";
	private Context context;
	private String queryString;

	public SearchDistributorFilterTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
		this.context = context;
		queryString = "Select ";
	}

	@Override
	protected List<Distributor> doInBackground(String... searchTerm) {
		// TODO Auto-generated method stub
		try {
		 QueryBuilder<Distributor, Integer> distQueryBuilder = SnapInventoryUtils.getDatabaseHelper(context).getDistributorDao()
					.queryBuilder();
		 searchTerm[0] = searchTerm[0].replaceAll(" ", "%");
		 distQueryBuilder.where().like("agency_name","%"+searchTerm[0]+"%");
	     List<Distributor> distributorList=distQueryBuilder.orderBy("distributor_id", true).query();
			List<CompanyDistributorMap> resultList= SnapInventoryUtils.getDatabaseHelper(context).getCompanyDistributorDao().queryBuilder().join(distQueryBuilder).orderBy("distributor_id", true).query();
			if(resultList.size()>0){
	            int prevDistId=distributorList.get(0).getDistributorId();
	            //distributorList.add(resultList.get(0).getDistributor());
	            int j=0;
	            for(int i=0;i<resultList.size();i++){
	                if(distributorList.get(j).getDistributorId()==resultList.get(i).getDistributor().getDistributorId()){
	                    if(distributorList.get(j).getCompanyNames()!=null){
	                        distributorList.get(j).setCompanyNames(distributorList.get(j).getCompanyNames()+", "+ resultList.get(i).getCompany().getCompanyName());
	                    }else{
	                        distributorList.get(j).setCompanyNames(resultList.get(i).getCompany().getCompanyName());
	                    }
	                }else{
	                    j++;
	                    i--;
	                }
	            }
			}
			Collections.sort(distributorList, new Comparator<Distributor>() {
				@Override
				public int compare(Distributor lhs, Distributor rhs) {
					// TODO Auto-generated method stub
					return lhs.getAgencyName().compareTo(rhs.getAgencyName());
				}
         });
			return distributorList;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	@Override
	protected void onPostExecute(List<Distributor> result) {
		// TODO Auto-generated method stub
		if (null != result && result.size() > 0) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}
}
