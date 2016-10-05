package com.snapbizz.snapstock.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;

public class UpgradeAppTask extends AsyncTask<Void, Void, Boolean> {

    private Context context;
    private int taskCode;
    private OnQueryCompleteListener onQueryCompleteListener;
    private String errorMessage = "unable to upgrade app";
    private int oldVersion;
    private int newVersion;

    public UpgradeAppTask(Context context,
            OnQueryCompleteListener onQueryCompleteListener, int oldVersion, int newVersion, int taskCode) {
        this.context = SnapCommonUtils.getSnapContext(context);
        this.onQueryCompleteListener = onQueryCompleteListener;
        this.taskCode = taskCode;
        this.oldVersion = oldVersion;
        this.newVersion = newVersion;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        upgradeVersion(oldVersion, newVersion);
        return true;
    }

    public void upgradeVersion(int oldVersion, int newVersion) {
        switch (oldVersion) {
        case 7:
            break;
        default:
            if(++oldVersion <= newVersion)
                upgradeVersion(oldVersion, newVersion);
            break;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        // TODO Auto-generated method stub
        if (result) {
            onQueryCompleteListener.onTaskSuccess(result, taskCode);
        } else {
            onQueryCompleteListener.onTaskError(errorMessage, taskCode);
        }
    }

}
