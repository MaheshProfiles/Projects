package com.snapbizz.snaptoolkit.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.snapbizz.snaptoolkit.db.dao.DistributorOrders;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table DISTRIBUTOR_ORDERS.
*/
public class DistributorOrdersDao extends AbstractDao<DistributorOrders, Long> {

    public static final String TABLENAME = "DISTRIBUTOR_ORDERS";

    /**
     * Properties of entity DistributorOrders.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property DistributorId = new Property(1, Long.class, "distributorId", false, "DISTRIBUTOR_ID");
        public final static Property OrderType = new Property(2, Short.class, "orderType", false, "ORDER_TYPE");
        public final static Property OrderStatus = new Property(3, Short.class, "orderStatus", false, "ORDER_STATUS");
        public final static Property DistributorStatus = new Property(4, Long.class, "distributorStatus", false, "DISTRIBUTOR_STATUS");
    };


    public DistributorOrdersDao(DaoConfig config) {
        super(config);
    }
    
    public DistributorOrdersDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'DISTRIBUTOR_ORDERS' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'DISTRIBUTOR_ID' INTEGER," + // 1: distributorId
                "'ORDER_TYPE' INTEGER," + // 2: orderType
                "'ORDER_STATUS' INTEGER," + // 3: orderStatus
                "'DISTRIBUTOR_STATUS' INTEGER);"); // 4: distributorStatus
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'DISTRIBUTOR_ORDERS'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, DistributorOrders entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long distributorId = entity.getDistributorId();
        if (distributorId != null) {
            stmt.bindLong(2, distributorId);
        }
 
        Short orderType = entity.getOrderType();
        if (orderType != null) {
            stmt.bindLong(3, orderType);
        }
 
        Short orderStatus = entity.getOrderStatus();
        if (orderStatus != null) {
            stmt.bindLong(4, orderStatus);
        }
 
        Long distributorStatus = entity.getDistributorStatus();
        if (distributorStatus != null) {
            stmt.bindLong(5, distributorStatus);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public DistributorOrders readEntity(Cursor cursor, int offset) {
        DistributorOrders entity = new DistributorOrders( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // distributorId
            cursor.isNull(offset + 2) ? null : cursor.getShort(offset + 2), // orderType
            cursor.isNull(offset + 3) ? null : cursor.getShort(offset + 3), // orderStatus
            cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4) // distributorStatus
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, DistributorOrders entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setDistributorId(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setOrderType(cursor.isNull(offset + 2) ? null : cursor.getShort(offset + 2));
        entity.setOrderStatus(cursor.isNull(offset + 3) ? null : cursor.getShort(offset + 3));
        entity.setDistributorStatus(cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(DistributorOrders entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(DistributorOrders entity) {
        if(entity != null) {
            return entity.getId();
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
