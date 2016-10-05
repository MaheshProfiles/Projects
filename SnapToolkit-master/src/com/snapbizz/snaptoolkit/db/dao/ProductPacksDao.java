package com.snapbizz.snaptoolkit.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.snapbizz.snaptoolkit.db.dao.ProductPacks;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "PRODUCT_PACKS".
*/
public class ProductPacksDao extends AbstractDao<ProductPacks, Void> {

    public static final String TABLENAME = "PRODUCT_PACKS";

    /**
     * Properties of entity ProductPacks.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property ProductCode = new Property(0, long.class, "productCode", false, "PRODUCT_CODE");
        public final static Property PackSize = new Property(1, Integer.class, "packSize", false, "PACK_SIZE");
        public final static Property SalePrice1 = new Property(2, Integer.class, "salePrice1", false, "SALE_PRICE1");
        public final static Property SalePrice2 = new Property(3, Integer.class, "salePrice2", false, "SALE_PRICE2");
        public final static Property SalePrice3 = new Property(4, Integer.class, "salePrice3", false, "SALE_PRICE3");
        public final static Property IsDefault = new Property(5, boolean.class, "isDefault", false, "IS_DEFAULT");
        public final static Property CreatedAt = new Property(6, java.util.Date.class, "createdAt", false, "CREATED_AT");
        public final static Property UpdatedAt = new Property(7, java.util.Date.class, "updatedAt", false, "UPDATED_AT");
    };


    public ProductPacksDao(DaoConfig config) {
        super(config);
    }
    
    public ProductPacksDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"PRODUCT_PACKS\" (" + //
                "\"PRODUCT_CODE\" INTEGER NOT NULL ," + // 0: productCode
                "\"PACK_SIZE\" INTEGER," + // 1: packSize
                "\"SALE_PRICE1\" INTEGER," + // 2: salePrice1
                "\"SALE_PRICE2\" INTEGER," + // 3: salePrice2
                "\"SALE_PRICE3\" INTEGER," + // 4: salePrice3
                "\"IS_DEFAULT\" INTEGER NOT NULL ," + // 5: isDefault
                "\"CREATED_AT\" INTEGER NOT NULL ," + // 6: createdAt
                "\"UPDATED_AT\" INTEGER NOT NULL );"); // 7: updatedAt
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"PRODUCT_PACKS\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, ProductPacks entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getProductCode());
 
        Integer packSize = entity.getPackSize();
        if (packSize != null) {
            stmt.bindLong(2, packSize);
        }
 
        Integer salePrice1 = entity.getSalePrice1();
        if (salePrice1 != null) {
            stmt.bindLong(3, salePrice1);
        }
 
        Integer salePrice2 = entity.getSalePrice2();
        if (salePrice2 != null) {
            stmt.bindLong(4, salePrice2);
        }
 
        Integer salePrice3 = entity.getSalePrice3();
        if (salePrice3 != null) {
            stmt.bindLong(5, salePrice3);
        }
        stmt.bindLong(6, entity.getIsDefault() ? 1L: 0L);
        stmt.bindLong(7, entity.getCreatedAt().getTime());
        stmt.bindLong(8, entity.getUpdatedAt().getTime());
    }

    /** @inheritdoc */
    @Override
    public Void readKey(Cursor cursor, int offset) {
        return null;
    }    

    /** @inheritdoc */
    @Override
    public ProductPacks readEntity(Cursor cursor, int offset) {
        ProductPacks entity = new ProductPacks( //
            cursor.getLong(offset + 0), // productCode
            cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1), // packSize
            cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2), // salePrice1
            cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3), // salePrice2
            cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4), // salePrice3
            cursor.getShort(offset + 5) != 0, // isDefault
            new java.util.Date(cursor.getLong(offset + 6)), // createdAt
            new java.util.Date(cursor.getLong(offset + 7)) // updatedAt
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, ProductPacks entity, int offset) {
        entity.setProductCode(cursor.getLong(offset + 0));
        entity.setPackSize(cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1));
        entity.setSalePrice1(cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2));
        entity.setSalePrice2(cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3));
        entity.setSalePrice3(cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4));
        entity.setIsDefault(cursor.getShort(offset + 5) != 0);
        entity.setCreatedAt(new java.util.Date(cursor.getLong(offset + 6)));
        entity.setUpdatedAt(new java.util.Date(cursor.getLong(offset + 7)));
     }
    
    /** @inheritdoc */
    @Override
    protected Void updateKeyAfterInsert(ProductPacks entity, long rowId) {
        // Unsupported or missing PK type
        return null;
    }
    
    /** @inheritdoc */
    @Override
    public Void getKey(ProductPacks entity) {
        return null;
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
