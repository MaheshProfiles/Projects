package com.snapbizz.snaptoolkit.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.snapbizz.snaptoolkit.db.dao.OrderDetails;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table ORDER_DETAILS.
*/
public class OrderDetailsDao extends AbstractDao<OrderDetails, Long> {

    public static final String TABLENAME = "ORDER_DETAILS";

    /**
     * Properties of entity OrderDetails.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property PoNumber = new Property(1, Integer.class, "poNumber", false, "PO_NUMBER");
        public final static Property TotalAmount = new Property(2, Integer.class, "totalAmount", false, "TOTAL_AMOUNT");
        public final static Property Discount = new Property(3, Integer.class, "discount", false, "DISCOUNT");
        public final static Property VatAmount = new Property(4, Integer.class, "vatAmount", false, "VAT_AMOUNT");
        public final static Property VatRate = new Property(5, Float.class, "vatRate", false, "VAT_RATE");
        public final static Property Mrp = new Property(6, Integer.class, "mrp", false, "MRP");
        public final static Property Name = new Property(7, String.class, "name", false, "NAME");
        public final static Property Uom = new Property(8, String.class, "uom", false, "UOM");
        public final static Property Measure = new Property(9, Integer.class, "measure", false, "MEASURE");
        public final static Property OrderedQuantity = new Property(10, Integer.class, "orderedQuantity", false, "ORDERED_QUANTITY");
        public final static Property AcceptedQuantity = new Property(11, Integer.class, "acceptedQuantity", false, "ACCEPTED_QUANTITY");
        public final static Property ReceivedQuantity = new Property(12, Integer.class, "receivedQuantity", false, "RECEIVED_QUANTITY");
        public final static Property ReturnedQuantity = new Property(13, Integer.class, "returnedQuantity", false, "RETURNED_QUANTITY");
        public final static Property PickedupQuantity = new Property(14, Integer.class, "pickedupQuantity", false, "PICKEDUP_QUANTITY");
        public final static Property ProductCode = new Property(15, Long.class, "productCode", false, "PRODUCT_CODE");
        public final static Property PurchasePrice = new Property(16, Integer.class, "purchasePrice", false, "PURCHASE_PRICE");
        public final static Property CreatedAt = new Property(17, java.util.Date.class, "createdAt", false, "CREATED_AT");
        public final static Property UpdatedAt = new Property(18, java.util.Date.class, "updatedAt", false, "UPDATED_AT");
    };


    public OrderDetailsDao(DaoConfig config) {
        super(config);
    }
    
    public OrderDetailsDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'ORDER_DETAILS' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'PO_NUMBER' INTEGER," + // 1: poNumber
                "'TOTAL_AMOUNT' INTEGER," + // 2: totalAmount
                "'DISCOUNT' INTEGER," + // 3: discount
                "'VAT_AMOUNT' INTEGER," + // 4: vatAmount
                "'VAT_RATE' REAL," + // 5: vatRate
                "'MRP' INTEGER," + // 6: mrp
                "'NAME' TEXT," + // 7: name
                "'UOM' TEXT," + // 8: uom
                "'MEASURE' INTEGER," + // 9: measure
                "'ORDERED_QUANTITY' INTEGER," + // 10: orderedQuantity
                "'ACCEPTED_QUANTITY' INTEGER," + // 11: acceptedQuantity
                "'RECEIVED_QUANTITY' INTEGER," + // 12: receivedQuantity
                "'RETURNED_QUANTITY' INTEGER," + // 13: returnedQuantity
                "'PICKEDUP_QUANTITY' INTEGER," + // 14: pickedupQuantity
                "'PRODUCT_CODE' INTEGER," + // 15: productCode
                "'PURCHASE_PRICE' INTEGER," + // 16: purchasePrice
                "'CREATED_AT' INTEGER NOT NULL ," + // 17: createdAt
                "'UPDATED_AT' INTEGER NOT NULL );"); // 18: updatedAt
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'ORDER_DETAILS'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, OrderDetails entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Integer poNumber = entity.getPoNumber();
        if (poNumber != null) {
            stmt.bindLong(2, poNumber);
        }
 
        Integer totalAmount = entity.getTotalAmount();
        if (totalAmount != null) {
            stmt.bindLong(3, totalAmount);
        }
 
        Integer discount = entity.getDiscount();
        if (discount != null) {
            stmt.bindLong(4, discount);
        }
 
        Integer vatAmount = entity.getVatAmount();
        if (vatAmount != null) {
            stmt.bindLong(5, vatAmount);
        }
 
        Float vatRate = entity.getVatRate();
        if (vatRate != null) {
            stmt.bindDouble(6, vatRate);
        }
 
        Integer mrp = entity.getMrp();
        if (mrp != null) {
            stmt.bindLong(7, mrp);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(8, name);
        }
 
        String uom = entity.getUom();
        if (uom != null) {
            stmt.bindString(9, uom);
        }
 
        Integer measure = entity.getMeasure();
        if (measure != null) {
            stmt.bindLong(10, measure);
        }
 
        Integer orderedQuantity = entity.getOrderedQuantity();
        if (orderedQuantity != null) {
            stmt.bindLong(11, orderedQuantity);
        }
 
        Integer acceptedQuantity = entity.getAcceptedQuantity();
        if (acceptedQuantity != null) {
            stmt.bindLong(12, acceptedQuantity);
        }
 
        Integer receivedQuantity = entity.getReceivedQuantity();
        if (receivedQuantity != null) {
            stmt.bindLong(13, receivedQuantity);
        }
 
        Integer returnedQuantity = entity.getReturnedQuantity();
        if (returnedQuantity != null) {
            stmt.bindLong(14, returnedQuantity);
        }
 
        Integer pickedupQuantity = entity.getPickedupQuantity();
        if (pickedupQuantity != null) {
            stmt.bindLong(15, pickedupQuantity);
        }
 
        Long productCode = entity.getProductCode();
        if (productCode != null) {
            stmt.bindLong(16, productCode);
        }
 
        Integer purchasePrice = entity.getPurchasePrice();
        if (purchasePrice != null) {
            stmt.bindLong(17, purchasePrice);
        }
        stmt.bindLong(18, entity.getCreatedAt().getTime());
        stmt.bindLong(19, entity.getUpdatedAt().getTime());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public OrderDetails readEntity(Cursor cursor, int offset) {
        OrderDetails entity = new OrderDetails( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1), // poNumber
            cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2), // totalAmount
            cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3), // discount
            cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4), // vatAmount
            cursor.isNull(offset + 5) ? null : cursor.getFloat(offset + 5), // vatRate
            cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6), // mrp
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // name
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // uom
            cursor.isNull(offset + 9) ? null : cursor.getInt(offset + 9), // measure
            cursor.isNull(offset + 10) ? null : cursor.getInt(offset + 10), // orderedQuantity
            cursor.isNull(offset + 11) ? null : cursor.getInt(offset + 11), // acceptedQuantity
            cursor.isNull(offset + 12) ? null : cursor.getInt(offset + 12), // receivedQuantity
            cursor.isNull(offset + 13) ? null : cursor.getInt(offset + 13), // returnedQuantity
            cursor.isNull(offset + 14) ? null : cursor.getInt(offset + 14), // pickedupQuantity
            cursor.isNull(offset + 15) ? null : cursor.getLong(offset + 15), // productCode
            cursor.isNull(offset + 16) ? null : cursor.getInt(offset + 16), // purchasePrice
            new java.util.Date(cursor.getLong(offset + 17)), // createdAt
            new java.util.Date(cursor.getLong(offset + 18)) // updatedAt
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, OrderDetails entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setPoNumber(cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1));
        entity.setTotalAmount(cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2));
        entity.setDiscount(cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3));
        entity.setVatAmount(cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4));
        entity.setVatRate(cursor.isNull(offset + 5) ? null : cursor.getFloat(offset + 5));
        entity.setMrp(cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6));
        entity.setName(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setUom(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setMeasure(cursor.isNull(offset + 9) ? null : cursor.getInt(offset + 9));
        entity.setOrderedQuantity(cursor.isNull(offset + 10) ? null : cursor.getInt(offset + 10));
        entity.setAcceptedQuantity(cursor.isNull(offset + 11) ? null : cursor.getInt(offset + 11));
        entity.setReceivedQuantity(cursor.isNull(offset + 12) ? null : cursor.getInt(offset + 12));
        entity.setReturnedQuantity(cursor.isNull(offset + 13) ? null : cursor.getInt(offset + 13));
        entity.setPickedupQuantity(cursor.isNull(offset + 14) ? null : cursor.getInt(offset + 14));
        entity.setProductCode(cursor.isNull(offset + 15) ? null : cursor.getLong(offset + 15));
        entity.setPurchasePrice(cursor.isNull(offset + 16) ? null : cursor.getInt(offset + 16));
        entity.setCreatedAt(new java.util.Date(cursor.getLong(offset + 17)));
        entity.setUpdatedAt(new java.util.Date(cursor.getLong(offset + 18)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(OrderDetails entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(OrderDetails entity) {
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
