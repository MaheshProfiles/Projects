package com.snapbizz.snaptoolkit.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.snapbizz.snaptoolkit.db.dao.CustomerMonthlySummary;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "CUSTOMER_MONTHLY_SUMMARY".
*/
public class CustomerMonthlySummaryDao extends AbstractDao<CustomerMonthlySummary, Void> {

    public static final String TABLENAME = "CUSTOMER_MONTHLY_SUMMARY";

    /**
     * Properties of entity CustomerMonthlySummary.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Phone = new Property(0, long.class, "phone", false, "PHONE");
        public final static Property Month = new Property(1, int.class, "month", false, "MONTH");
        public final static Property AmountDue = new Property(2, Integer.class, "amountDue", false, "AMOUNT_DUE");
        public final static Property PurchaseValue = new Property(3, Integer.class, "purchaseValue", false, "PURCHASE_VALUE");
        public final static Property AmountPaid = new Property(4, Integer.class, "amountPaid", false, "AMOUNT_PAID");
        public final static Property CreatedAt = new Property(5, java.util.Date.class, "createdAt", false, "CREATED_AT");
        public final static Property UpdatedAt = new Property(6, java.util.Date.class, "updatedAt", false, "UPDATED_AT");
    };


    public CustomerMonthlySummaryDao(DaoConfig config) {
        super(config);
    }
    
    public CustomerMonthlySummaryDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"CUSTOMER_MONTHLY_SUMMARY\" (" + //
                "\"PHONE\" INTEGER NOT NULL ," + // 0: phone
                "\"MONTH\" INTEGER NOT NULL ," + // 1: month
                "\"AMOUNT_DUE\" INTEGER," + // 2: amountDue
                "\"PURCHASE_VALUE\" INTEGER," + // 3: purchaseValue
                "\"AMOUNT_PAID\" INTEGER," + // 4: amountPaid
                "\"CREATED_AT\" INTEGER NOT NULL ," + // 5: createdAt
                "\"UPDATED_AT\" INTEGER NOT NULL );"); // 6: updatedAt
        // Add Indexes
        db.execSQL("CREATE UNIQUE INDEX " + constraint + "IDX_CUSTOMER_MONTHLY_SUMMARY_MONTH_PHONE ON CUSTOMER_MONTHLY_SUMMARY" +
                " (\"MONTH\",\"PHONE\");");
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"CUSTOMER_MONTHLY_SUMMARY\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, CustomerMonthlySummary entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getPhone());
        stmt.bindLong(2, entity.getMonth());
 
        Integer amountDue = entity.getAmountDue();
        if (amountDue != null) {
            stmt.bindLong(3, amountDue);
        }
 
        Integer purchaseValue = entity.getPurchaseValue();
        if (purchaseValue != null) {
            stmt.bindLong(4, purchaseValue);
        }
 
        Integer amountPaid = entity.getAmountPaid();
        if (amountPaid != null) {
            stmt.bindLong(5, amountPaid);
        }
        stmt.bindLong(6, entity.getCreatedAt().getTime());
        stmt.bindLong(7, entity.getUpdatedAt().getTime());
    }

    /** @inheritdoc */
    @Override
    public Void readKey(Cursor cursor, int offset) {
        return null;
    }    

    /** @inheritdoc */
    @Override
    public CustomerMonthlySummary readEntity(Cursor cursor, int offset) {
        CustomerMonthlySummary entity = new CustomerMonthlySummary( //
            cursor.getLong(offset + 0), // phone
            cursor.getInt(offset + 1), // month
            cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2), // amountDue
            cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3), // purchaseValue
            cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4), // amountPaid
            new java.util.Date(cursor.getLong(offset + 5)), // createdAt
            new java.util.Date(cursor.getLong(offset + 6)) // updatedAt
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, CustomerMonthlySummary entity, int offset) {
        entity.setPhone(cursor.getLong(offset + 0));
        entity.setMonth(cursor.getInt(offset + 1));
        entity.setAmountDue(cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2));
        entity.setPurchaseValue(cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3));
        entity.setAmountPaid(cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4));
        entity.setCreatedAt(new java.util.Date(cursor.getLong(offset + 5)));
        entity.setUpdatedAt(new java.util.Date(cursor.getLong(offset + 6)));
     }
    
    /** @inheritdoc */
    @Override
    protected Void updateKeyAfterInsert(CustomerMonthlySummary entity, long rowId) {
        // Unsupported or missing PK type
        return null;
    }
    
    /** @inheritdoc */
    @Override
    public Void getKey(CustomerMonthlySummary entity) {
        return null;
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
