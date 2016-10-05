package com.snapbizz.snapstock.asynctasks;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.snapbizz.snapstock.utils.DistributorFilterType;
import com.snapbizz.snaptoolkit.domains.CompanyDistributorMap;
import com.snapbizz.snaptoolkit.domains.Distributor;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;

public class GetDistributorListTask extends
AsyncTask<Void, Void, List<Distributor>> {

	private Context context;
	private int taskCode;
	private OnQueryCompleteListener onQueryCompleteListener;
	private final String errorMessage = "No Product Found";
	private DistributorFilterType distributorFilterType;
	private SnapBizzDatabaseHelper databaseHelper;

	public GetDistributorListTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int taskCode,
			DistributorFilterType distributorFilterType) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
		this.distributorFilterType = distributorFilterType;
	}

	@Override
	protected List<Distributor> doInBackground(Void... params) {
		// TODO Auto-generated method stub
		try {
			databaseHelper=SnapCommonUtils.getDatabaseHelper(context);
			List<CompanyDistributorMap> resultList=databaseHelper.getCompanyDistributorDao().queryBuilder().orderBy("distributor_id", true).query();
			List<Distributor> distributorList= databaseHelper.getDistributorDao().queryBuilder().orderBy("distributor_id", true).query();//new ArrayList<Distributor>();
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
				/*if (prevDistId==resultList.get(i).getDistributor().getDistributorId()) {
					if(distributorList.get(j).getCompanyNames()!=null){
						distributorList.get(j).setCompanyNames(distributorList.get(j).getCompanyNames()+", "+ resultList.get(i).getCompany().getCompanyName());
					}else{
						distributorList.get(j).setCompanyNames(resultList.get(j).getCompany().getCompanyName());
					}
				}else{
					j++;
					prevDistId=resultList.get(i).getDistributor().getDistributorId();
				//	distributorList.add(resultList.get(i).getDistributor());
					if(distributorList.get(j)!=null)
					distributorList.get(j).setCompanyNames(resultList.get(j).getCompany().getCompanyName());
				}*/
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
			//return SnapCommonUtils.getDatabaseHelper(context).getDistributorDao().queryBuilder().orderBy(distributorFilterType.getDistributorFilterTypeValue(), true).query();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	@Override
	protected void onPostExecute(List<Distributor> result) {
		// TODO Auto-generated method stub
		if (result != null && result.size() > 0) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else {
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
		}
	}

}
