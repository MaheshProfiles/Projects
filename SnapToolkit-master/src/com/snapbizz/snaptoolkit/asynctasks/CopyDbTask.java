package com.snapbizz.snaptoolkit.asynctasks;

import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteStatement;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.snapbizz.snaptoolkit.R;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapDBUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class CopyDbTask extends AsyncTask<Void, Void, Boolean> {
    private Context context;
    private int taskCode;
    private OnQueryCompleteListener onQueryCompleteListener;
    private String errorMessage = "unable to backup db";

    public CopyDbTask(Context context,
            OnQueryCompleteListener onQueryCompleteListener, int taskCode) {
        this.context = SnapCommonUtils.getSnapContext(context);
        this.onQueryCompleteListener = onQueryCompleteListener;
        this.taskCode = taskCode;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
    	boolean isCopied = false;
    	try {
    		storeTimestampIntoDB();
     		isCopied = SnapDBUtils.copyOrRestoreDB(String.valueOf(context.getDatabasePath(SnapToolkitConstants.DB_NAME)),
    				SnapToolkitConstants.DB_EXTPATH + SnapSharedUtils.getDbBackUpName(SnapCommonUtils.getSnapContext(context)), false);
    		isCopied = SnapDBUtils.copyOrRestoreDB(String.valueOf(context.getDatabasePath(SnapToolkitConstants.DB_NAME_V2)),
    				SnapToolkitConstants.DB_EXTPATH + SnapToolkitConstants.DB_NAME_V2, false);
    		return isCopied;
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
		return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            onQueryCompleteListener.onTaskSuccess(result, taskCode);
        } else {
            onQueryCompleteListener.onTaskError(errorMessage, taskCode);
        }
    }
    
    public void storeTimestampIntoDB() throws Exception {
		String[] tableName = SnapCommonUtils.getDownloadSyncKeys();
		SnapBizzDatabaseHelper sbHelper = SnapCommonUtils.getDatabaseHelper(context);
		if (sbHelper == null)
			return;
		SQLiteDatabase db = sbHelper.getWritableDatabase(context.getResources().getString(R.string.passkey));
		if (db == null || !db.isOpen())
			return;
		db.beginTransaction();
        String sql = "Insert or Replace into sync_timestamp (name, lastmodified_timestamp) values(?,?)";
        SQLiteStatement insert = null;
        try {
			insert = db.compileStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			insert = null;
		}
        if (insert == null) {
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
            Log.d("[CopyDbTask]", "Creating sync_timestamp tables - onUpgrade failed?");
			SnapCommonUtils.getDatabaseHelper(context).createTableSyncTimestamp();
			SnapSharedUtils.storeProductSkuListUpdate(false, context);
			return;
        }
        for (int i = 0; i < tableName.length; i++) {
        	String timestamp = null;
            timestamp = context.getSharedPreferences(SnapToolkitConstants.SYNC_FILE, Context.MODE_MULTI_PROCESS)
        				       .getString(tableName[i], SnapToolkitConstants.OLDEST_SYNC_DATE);
            if (timestamp == null)
            	continue;
            insert.bindString(1, tableName[i]);
    	    insert.bindString(2, timestamp);
    	    insert.execute();

        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
	}
}
