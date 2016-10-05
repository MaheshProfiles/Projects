package com.snapbizz.snaptoolkit.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.snapbizz.snaptoolkit.db.dao.LastSyncTime;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table LAST_SYNC_TIME.
*/
public class LastSyncTimeDao extends AbstractDao<LastSyncTime, String> {

    public static final String TABLENAME = "LAST_SYNC_TIME";

    /**
     * Properties of entity LastSyncTime.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property SyncClass = new Property(0, String.class, "syncClass", true, "SYNC_CLASS");
        public final static Property Timestamp = new Property(1, java.util.Date.class, "timestamp", false, "TIMESTAMP");
    };


    public LastSyncTimeDao(DaoConfig config) {
        super(config);
    }
    
    public LastSyncTimeDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'LAST_SYNC_TIME' (" + //
                "'SYNC_CLASS' TEXT PRIMARY KEY NOT NULL ," + // 0: syncClass
                "'TIMESTAMP' INTEGER);"); // 1: timestamp
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'LAST_SYNC_TIME'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, LastSyncTime entity) {
        stmt.clearBindings();
 
        String syncClass = entity.getSyncClass();
        if (syncClass != null) {
            stmt.bindString(1, syncClass);
        }
 
        java.util.Date timestamp = entity.getTimestamp();
        if (timestamp != null) {
            stmt.bindLong(2, timestamp.getTime());
        }
    }

    /** @inheritdoc */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public LastSyncTime readEntity(Cursor cursor, int offset) {
        LastSyncTime entity = new LastSyncTime( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // syncClass
            cursor.isNull(offset + 1) ? null : new java.util.Date(cursor.getLong(offset + 1)) // timestamp
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, LastSyncTime entity, int offset) {
        entity.setSyncClass(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setTimestamp(cursor.isNull(offset + 1) ? null : new java.util.Date(cursor.getLong(offset + 1)));
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(LastSyncTime entity, long rowId) {
        return entity.getSyncClass();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(LastSyncTime entity) {
        if(entity != null) {
            return entity.getSyncClass();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
