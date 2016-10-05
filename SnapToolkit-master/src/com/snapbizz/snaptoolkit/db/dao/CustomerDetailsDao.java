package com.snapbizz.snaptoolkit.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.snapbizz.snaptoolkit.db.dao.CustomerDetails;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "CUSTOMER_DETAILS".
*/
public class CustomerDetailsDao extends AbstractDao<CustomerDetails, Long> {

    public static final String TABLENAME = "CUSTOMER_DETAILS";

    /**
     * Properties of entity CustomerDetails.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Phone = new Property(0, long.class, "phone", true, "PHONE");
        public final static Property AmountDue = new Property(1, int.class, "amountDue", false, "AMOUNT_DUE");
        public final static Property PurchaseValue = new Property(2, int.class, "purchaseValue", false, "PURCHASE_VALUE");
        public final static Property AmountSaved = new Property(3, int.class, "amountSaved", false, "AMOUNT_SAVED");
        public final static Property LastPurchaseAmount = new Property(4, Integer.class, "lastPurchaseAmount", false, "LAST_PURCHASE_AMOUNT");
        public final static Property LastPaymentAmount = new Property(5, Integer.class, "lastPaymentAmount", false, "LAST_PAYMENT_AMOUNT");
        public final static Property AvgPurchasePerVisit = new Property(6, Integer.class, "avgPurchasePerVisit", false, "AVG_PURCHASE_PER_VISIT");
        public final static Property AvgVisitsPerMonth = new Property(7, Float.class, "avgVisitsPerMonth", false, "AVG_VISITS_PER_MONTH");
        public final static Property LastPurchaseDate = new Property(8, java.util.Date.class, "lastPurchaseDate", false, "LAST_PURCHASE_DATE");
        public final static Property LastPaymentDate = new Property(9, java.util.Date.class, "lastPaymentDate", false, "LAST_PAYMENT_DATE");
        public final static Property CreatedAt = new Property(10, java.util.Date.class, "createdAt", false, "CREATED_AT");
        public final static Property UpdatedAt = new Property(11, java.util.Date.class, "updatedAt", false, "UPDATED_AT");
    };


    public CustomerDetailsDao(DaoConfig config) {
        super(config);
    }
    
    public CustomerDetailsDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"CUSTOMER_DETAILS\" (" + //
                "\"PHONE\" INTEGER PRIMARY KEY NOT NULL ," + // 0: phone
                "\"AMOUNT_DUE\" INTEGER NOT NULL ," + // 1: amountDue
                "\"PURCHASE_VALUE\" INTEGER NOT NULL ," + // 2: purchaseValue
                "\"AMOUNT_SAVED\" INTEGER NOT NULL ," + // 3: amountSaved
                "\"LAST_PURCHASE_AMOUNT\" INTEGER," + // 4: lastPurchaseAmount
                "\"LAST_PAYMENT_AMOUNT\" INTEGER," + // 5: lastPaymentAmount
                "\"AVG_PURCHASE_PER_VISIT\" INTEGER," + // 6: avgPurchasePerVisit
                "\"AVG_VISITS_PER_MONTH\" REAL," + // 7: avgVisitsPerMonth
                "\"LAST_PURCHASE_DATE\" INTEGER," + // 8: lastPurchaseDate
                "\"LAST_PAYMENT_DATE\" INTEGER," + // 9: lastPaymentDate
                "\"CREATED_AT\" INTEGER NOT NULL ," + // 10: createdAt
                "\"UPDATED_AT\" INTEGER NOT NULL );"); // 11: updatedAt
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"CUSTOMER_DETAILS\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, CustomerDetails entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getPhone());
        stmt.bindLong(2, entity.getAmountDue());
        stmt.bindLong(3, entity.getPurchaseValue());
        stmt.bindLong(4, entity.getAmountSaved());
 
        Integer lastPurchaseAmount = entity.getLastPurchaseAmount();
        if (lastPurchaseAmount != null) {
            stmt.bindLong(5, lastPurchaseAmount);
        }
 
        Integer lastPaymentAmount = entity.getLastPaymentAmount();
        if (lastPaymentAmount != null) {
            stmt.bindLong(6, lastPaymentAmount);
        }
 
        Integer avgPurchasePerVisit = entity.getAvgPurchasePerVisit();
        if (avgPurchasePerVisit != null) {
            stmt.bindLong(7, avgPurchasePerVisit);
        }
 
        Float avgVisitsPerMonth = entity.getAvgVisitsPerMonth();
        if (avgVisitsPerMonth != null) {
            stmt.bindDouble(8, avgVisitsPerMonth);
        }
 
        java.util.Date lastPurchaseDate = entity.getLastPurchaseDate();
        if (lastPurchaseDate != null) {
            stmt.bindLong(9, lastPurchaseDate.getTime());
        }
 
        java.util.Date lastPaymentDate = entity.getLastPaymentDate();
        if (lastPaymentDate != null) {
            stmt.bindLong(10, lastPaymentDate.getTime());
        }
        stmt.bindLong(11, entity.getCreatedAt().getTime());
        stmt.bindLong(12, entity.getUpdatedAt().getTime());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public CustomerDetails readEntity(Cursor cursor, int offset) {
        CustomerDetails entity = new CustomerDetails( //
            cursor.getLong(offset + 0), // phone
            cursor.getInt(offset + 1), // amountDue
            cursor.getInt(offset + 2), // purchaseValue
            cursor.getInt(offset + 3), // amountSaved
            cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4), // lastPurchaseAmount
            cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5), // lastPaymentAmount
            cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6), // avgPurchasePerVisit
            cursor.isNull(offset + 7) ? null : cursor.getFloat(offset + 7), // avgVisitsPerMonth
            cursor.isNull(offset + 8) ? null : new java.util.Date(cursor.getLong(offset + 8)), // lastPurchaseDate
            cursor.isNull(offset + 9) ? null : new java.util.Date(cursor.getLong(offset + 9)), // lastPaymentDate
            new java.util.Date(cursor.getLong(offset + 10)), // createdAt
            new java.util.Date(cursor.getLong(offset + 11)) // updatedAt
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, CustomerDetails entity, int offset) {
        entity.setPhone(cursor.getLong(offset + 0));
        entity.setAmountDue(cursor.getInt(offset + 1));
        entity.setPurchaseValue(cursor.getInt(offset + 2));
        entity.setAmountSaved(cursor.getInt(offset + 3));
        entity.setLastPurchaseAmount(cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4));
        entity.setLastPaymentAmount(cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5));
        entity.setAvgPurchasePerVisit(cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6));
        entity.setAvgVisitsPerMonth(cursor.isNull(offset + 7) ? null : cursor.getFloat(offset + 7));
        entity.setLastPurchaseDate(cursor.isNull(offset + 8) ? null : new java.util.Date(cursor.getLong(offset + 8)));
        entity.setLastPaymentDate(cursor.isNull(offset + 9) ? null : new java.util.Date(cursor.getLong(offset + 9)));
        entity.setCreatedAt(new java.util.Date(cursor.getLong(offset + 10)));
        entity.setUpdatedAt(new java.util.Date(cursor.getLong(offset + 11)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(CustomerDetails entity, long rowId) {
        entity.setPhone(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(CustomerDetails entity) {
        if(entity != null) {
            return entity.getPhone();
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
