package com.snapbizz.snaptoolkit.utils;

import android.content.Context;

/**
 * Database helper class used to manage the creation and upgrading of your
 * database. This class also usually provides the DAOs used by the other
 * classes.
 */
public class SnapBizzSyncDatabaseHelper extends SnapBizzDatabaseHelper {

    public SnapBizzSyncDatabaseHelper(Context context) {
        super(context, SnapToolkitConstants.SYNC_DB_NAME);
        this.context = context;
    }

    /**
     * This is called when the database is first created. Usually you should
     * call createTable statements here to create the tables that will store
     * your data.
     */
   
}