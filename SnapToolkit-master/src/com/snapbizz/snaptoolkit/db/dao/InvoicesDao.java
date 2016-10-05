package com.snapbizz.snaptoolkit.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.snapbizz.snaptoolkit.db.dao.Invoices;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "INVOICES".
*/
public class InvoicesDao extends AbstractDao<Invoices, Long> {

    public static final String TABLENAME = "INVOICES";

    /**
     * Properties of entity Invoices.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property CustomerPhone = new Property(1, Long.class, "customerPhone", false, "CUSTOMER_PHONE");
        public final static Property IsMemo = new Property(2, boolean.class, "isMemo", false, "IS_MEMO");
        public final static Property TotalAmount = new Property(3, int.class, "totalAmount", false, "TOTAL_AMOUNT");
        public final static Property PendingAmount = new Property(4, int.class, "pendingAmount", false, "PENDING_AMOUNT");
        public final static Property TotalDiscount = new Property(5, int.class, "totalDiscount", false, "TOTAL_DISCOUNT");
        public final static Property TotalSavings = new Property(6, int.class, "totalSavings", false, "TOTAL_SAVINGS");
        public final static Property MemoId = new Property(7, Long.class, "memoId", false, "MEMO_ID");
        public final static Property IsCredit = new Property(8, boolean.class, "isCredit", false, "IS_CREDIT");
        public final static Property TotalQuantity = new Property(9, int.class, "totalQuantity", false, "TOTAL_QUANTITY");
        public final static Property TotalItems = new Property(10, int.class, "totalItems", false, "TOTAL_ITEMS");
        public final static Property TotalVatAmount = new Property(11, int.class, "totalVatAmount", false, "TOTAL_VAT_AMOUNT");
        public final static Property IsDeleted = new Property(12, boolean.class, "isDeleted", false, "IS_DELETED");
        public final static Property IsDelivery = new Property(13, boolean.class, "isDelivery", false, "IS_DELIVERY");
        public final static Property BillerName = new Property(14, String.class, "billerName", false, "BILLER_NAME");
        public final static Property PosName = new Property(15, String.class, "posName", false, "POS_NAME");
        public final static Property IsSync = new Property(16, boolean.class, "isSync", false, "IS_SYNC");
        public final static Property IsUpdated = new Property(17, boolean.class, "isUpdated", false, "IS_UPDATED");
        public final static Property BillStartedAt = new Property(18, java.util.Date.class, "billStartedAt", false, "BILL_STARTED_AT");
        public final static Property CreatedAt = new Property(19, java.util.Date.class, "createdAt", false, "CREATED_AT");
        public final static Property UpdatedAt = new Property(20, java.util.Date.class, "updatedAt", false, "UPDATED_AT");
    };


    public InvoicesDao(DaoConfig config) {
        super(config);
    }
    
    public InvoicesDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"INVOICES\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"CUSTOMER_PHONE\" INTEGER," + // 1: customerPhone
                "\"IS_MEMO\" INTEGER NOT NULL ," + // 2: isMemo
                "\"TOTAL_AMOUNT\" INTEGER NOT NULL ," + // 3: totalAmount
                "\"PENDING_AMOUNT\" INTEGER NOT NULL ," + // 4: pendingAmount
                "\"TOTAL_DISCOUNT\" INTEGER NOT NULL ," + // 5: totalDiscount
                "\"TOTAL_SAVINGS\" INTEGER NOT NULL ," + // 6: totalSavings
                "\"MEMO_ID\" INTEGER," + // 7: memoId
                "\"IS_CREDIT\" INTEGER NOT NULL ," + // 8: isCredit
                "\"TOTAL_QUANTITY\" INTEGER NOT NULL ," + // 9: totalQuantity
                "\"TOTAL_ITEMS\" INTEGER NOT NULL ," + // 10: totalItems
                "\"TOTAL_VAT_AMOUNT\" INTEGER NOT NULL ," + // 11: totalVatAmount
                "\"IS_DELETED\" INTEGER NOT NULL ," + // 12: isDeleted
                "\"IS_DELIVERY\" INTEGER NOT NULL ," + // 13: isDelivery
                "\"BILLER_NAME\" TEXT," + // 14: billerName
                "\"POS_NAME\" TEXT NOT NULL ," + // 15: posName
                "\"IS_SYNC\" INTEGER NOT NULL ," + // 16: isSync
                "\"IS_UPDATED\" INTEGER NOT NULL ," + // 17: isUpdated
                "\"BILL_STARTED_AT\" INTEGER NOT NULL ," + // 18: billStartedAt
                "\"CREATED_AT\" INTEGER NOT NULL ," + // 19: createdAt
                "\"UPDATED_AT\" INTEGER NOT NULL );"); // 20: updatedAt
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"INVOICES\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Invoices entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long customerPhone = entity.getCustomerPhone();
        if (customerPhone != null) {
            stmt.bindLong(2, customerPhone);
        }
        stmt.bindLong(3, entity.getIsMemo() ? 1L: 0L);
        stmt.bindLong(4, entity.getTotalAmount());
        stmt.bindLong(5, entity.getPendingAmount());
        stmt.bindLong(6, entity.getTotalDiscount());
        stmt.bindLong(7, entity.getTotalSavings());
 
        Long memoId = entity.getMemoId();
        if (memoId != null) {
            stmt.bindLong(8, memoId);
        }
        stmt.bindLong(9, entity.getIsCredit() ? 1L: 0L);
        stmt.bindLong(10, entity.getTotalQuantity());
        stmt.bindLong(11, entity.getTotalItems());
        stmt.bindLong(12, entity.getTotalVatAmount());
        stmt.bindLong(13, entity.getIsDeleted() ? 1L: 0L);
        stmt.bindLong(14, entity.getIsDelivery() ? 1L: 0L);
 
        String billerName = entity.getBillerName();
        if (billerName != null) {
            stmt.bindString(15, billerName);
        }
        stmt.bindString(16, entity.getPosName());
        stmt.bindLong(17, entity.getIsSync() ? 1L: 0L);
        stmt.bindLong(18, entity.getIsUpdated() ? 1L: 0L);
        stmt.bindLong(19, entity.getBillStartedAt().getTime());
        stmt.bindLong(20, entity.getCreatedAt().getTime());
        stmt.bindLong(21, entity.getUpdatedAt().getTime());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Invoices readEntity(Cursor cursor, int offset) {
        Invoices entity = new Invoices( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // customerPhone
            cursor.getShort(offset + 2) != 0, // isMemo
            cursor.getInt(offset + 3), // totalAmount
            cursor.getInt(offset + 4), // pendingAmount
            cursor.getInt(offset + 5), // totalDiscount
            cursor.getInt(offset + 6), // totalSavings
            cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7), // memoId
            cursor.getShort(offset + 8) != 0, // isCredit
            cursor.getInt(offset + 9), // totalQuantity
            cursor.getInt(offset + 10), // totalItems
            cursor.getInt(offset + 11), // totalVatAmount
            cursor.getShort(offset + 12) != 0, // isDeleted
            cursor.getShort(offset + 13) != 0, // isDelivery
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14), // billerName
            cursor.getString(offset + 15), // posName
            cursor.getShort(offset + 16) != 0, // isSync
            cursor.getShort(offset + 17) != 0, // isUpdated
            new java.util.Date(cursor.getLong(offset + 18)), // billStartedAt
            new java.util.Date(cursor.getLong(offset + 19)), // createdAt
            new java.util.Date(cursor.getLong(offset + 20)) // updatedAt
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Invoices entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setCustomerPhone(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setIsMemo(cursor.getShort(offset + 2) != 0);
        entity.setTotalAmount(cursor.getInt(offset + 3));
        entity.setPendingAmount(cursor.getInt(offset + 4));
        entity.setTotalDiscount(cursor.getInt(offset + 5));
        entity.setTotalSavings(cursor.getInt(offset + 6));
        entity.setMemoId(cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7));
        entity.setIsCredit(cursor.getShort(offset + 8) != 0);
        entity.setTotalQuantity(cursor.getInt(offset + 9));
        entity.setTotalItems(cursor.getInt(offset + 10));
        entity.setTotalVatAmount(cursor.getInt(offset + 11));
        entity.setIsDeleted(cursor.getShort(offset + 12) != 0);
        entity.setIsDelivery(cursor.getShort(offset + 13) != 0);
        entity.setBillerName(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
        entity.setPosName(cursor.getString(offset + 15));
        entity.setIsSync(cursor.getShort(offset + 16) != 0);
        entity.setIsUpdated(cursor.getShort(offset + 17) != 0);
        entity.setBillStartedAt(new java.util.Date(cursor.getLong(offset + 18)));
        entity.setCreatedAt(new java.util.Date(cursor.getLong(offset + 19)));
        entity.setUpdatedAt(new java.util.Date(cursor.getLong(offset + 20)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Invoices entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Invoices entity) {
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
