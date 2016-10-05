package com.snapbizz.snaptoolkit.asynctasks;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.snapbizz.snaptoolkit.domains.State;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;

public class GetStatesTask extends AsyncTask<Void, Void, List<State>>{

	private OnQueryCompleteListener onQueryCompleteListener;
	private final String errorMessage = "Unable to get State details.";
	private Context context;
	private int taskCode;

	public GetStatesTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
		this.context = context;
		this.taskCode = taskCode;
		this.onQueryCompleteListener = onQueryCompleteListener;
	}
	
	@Override
	protected synchronized List<State> doInBackground(Void... arg0) {
		// TODO Auto-generated method stub
		try {			
			List<State> stateList = SnapCommonUtils.getDatabaseHelper(SnapCommonUtils.getSnapContext(context)).getStateDao().queryBuilder().orderByRaw("name").query();
			return stateList;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} 
	}
	
	@Override
	protected void onPostExecute(List<State> result) {
		// TODO Auto-generated method stub
		if (result != null && result.size() != 0) {
			onQueryCompleteListener.onTaskSuccess(result, taskCode);
		} else
			onQueryCompleteListener.onTaskError(errorMessage, taskCode);
	}



}
