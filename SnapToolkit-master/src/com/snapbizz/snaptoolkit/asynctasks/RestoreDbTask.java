package com.snapbizz.snaptoolkit.asynctasks;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import net.sqlcipher.database.SQLiteDatabase;

import com.j256.ormlite.dao.GenericRawResults;
import com.snapbizz.snaptoolkit.R;
import com.snapbizz.snaptoolkit.domains.SyncTimestamp;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapDBUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

import android.content.Context;
import android.os.AsyncTask;

public class RestoreDbTask extends AsyncTask<Void, Void, Boolean> {

    private Context context;
    private int taskCode;
    private OnQueryCompleteListener onQueryCompleteListener;

    public RestoreDbTask(Context context,
            OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
        this.context = SnapCommonUtils.getSnapContext(context);
        this.onQueryCompleteListener = onQueryCompleteListener;
        this.taskCode = taskCode;
    }
    
    @Override
    protected Boolean doInBackground(Void... params) {
    	boolean isDBRestored = false;
    	isDBRestored = SnapDBUtils.copyOrRestoreDB(SnapToolkitConstants.DB_RESTORE_EXTPATH + SnapToolkitConstants.DB_NAME,
    			String.valueOf(context.getDatabasePath(SnapToolkitConstants.DB_NAME)), true);
    	if (isDBRestored) {
    		try {
        		CheckSyncTimestampTableExistOrCreate();
        		return isDBRestored;
    		} catch (SQLException e2) {
    			e2.printStackTrace();
    		}
    	} else {
	    	isDBRestored = SnapDBUtils.copyOrRestoreDB(
	    			SnapToolkitConstants.DB_RESTORE_EXTPATH + SnapToolkitConstants.DB_NAME_V2,
	    			String.valueOf(context.getDatabasePath(SnapToolkitConstants.DB_NAME_V2)), true);
    	}
    	return isDBRestored;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            onQueryCompleteListener.onTaskSuccess(result, taskCode);
        } else {
            onQueryCompleteListener.onTaskError(context.getResources()
            		.getString(R.string.db_backup_error), taskCode);
        }
    }
    
    public void restoreSyncTimetampFromDB(SnapBizzDatabaseHelper dbHelper) throws SQLException {
		if (dbHelper == null)
			return;
		String sqlQuery = "select * from sync_timestamp;";
	    GenericRawResults<SyncTimestamp> timestampList = dbHelper.getDownloadSyncTimestampDao()
	        		                                                 .queryRaw(sqlQuery,dbHelper.getDownloadSyncTimestampDao()
	        		                                                 .getRawRowMapper());
	        
	    if (timestampList != null) {
	    	for (SyncTimestamp timestamp : timestampList.getResults()) {
				if (timestamp != null) {
					context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,Context.MODE_MULTI_PROCESS).edit()
			           .putString(timestamp.getName(), timestamp.getLastModifiedTimestamp()).commit();
				}
	    	}
	    }
    }
    
    private void createDefaultSyncTimestamps() {
    	String[] timestamps = SnapCommonUtils.getDownloadSyncKeys();
    	for (String key : timestamps) {
    		context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE,Context.MODE_MULTI_PROCESS).edit()
	           .putString(key, new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT, 
	        		        Locale.getDefault()).format(Calendar.getInstance().getTime())).commit();
    	}
    }
    
    public void CheckSyncTimestampTableExistOrCreate() throws SQLException {
    	SnapBizzDatabaseHelper sbHelper = SnapCommonUtils.getDatabaseHelper(context);
		if (sbHelper == null)
			return;
		SQLiteDatabase db = sbHelper.getWritableDatabase(context.getResources().getString(R.string.passkey));
		if (db == null || !db.isOpen())
			return;

	    if (sbHelper.getDownloadSyncTimestampDao().isTableExists()) {
	    	restoreSyncTimetampFromDB(sbHelper);
	    } else {
	    	createDefaultSyncTimestamps();
	    	sbHelper.createTableSyncTimestamp();	
	    	SnapSharedUtils.storeProductSkuListUpdate(false, context);
	    }
	    SnapDBUtils.checkAndUpdateVat(context, sbHelper, true);
		SnapDBUtils.addXtraProductQuickAddCategory(context, sbHelper, true);
		SnapDBUtils.checkAndUpdateLooseItemsUom(context, sbHelper, true);
    }
}
