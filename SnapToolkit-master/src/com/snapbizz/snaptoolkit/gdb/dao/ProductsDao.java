package com.snapbizz.snaptoolkit.gdb.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.snapbizz.snaptoolkit.gdb.dao.Products;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "PRODUCTS".
*/
public class ProductsDao extends AbstractDao<Products, Long> {

    public static final String TABLENAME = "PRODUCTS";

    /**
     * Properties of entity Products.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Barcode = new Property(0, long.class, "barcode", true, "BARCODE");
        public final static Property ProductGid = new Property(1, long.class, "productGid", false, "PRODUCT_GID");
        public final static Property Name = new Property(2, String.class, "name", false, "NAME");
        public final static Property Mrp = new Property(3, int.class, "mrp", false, "MRP");
        public final static Property AlternateMrp = new Property(4, String.class, "alternateMrp", false, "ALTERNATE_MRP");
        public final static Property Uom = new Property(5, String.class, "uom", false, "UOM");
        public final static Property Measure = new Property(6, int.class, "measure", false, "MEASURE");
        public final static Property BrandId = new Property(7, int.class, "brandId", false, "BRAND_ID");
        public final static Property CategoryId = new Property(8, int.class, "categoryId", false, "CATEGORY_ID");
        public final static Property VatId = new Property(9, Integer.class, "vatId", false, "VAT_ID");
        public final static Property IsDisabled = new Property(10, boolean.class, "isDisabled", false, "IS_DISABLED");
        public final static Property Image = new Property(11, String.class, "image", false, "IMAGE");
        public final static Property LocalName = new Property(12, String.class, "localName", false, "LOCAL_NAME");
        public final static Property IsPc = new Property(13, boolean.class, "isPc", false, "IS_PC");
        public final static Property CreatedAt = new Property(14, java.util.Date.class, "createdAt", false, "CREATED_AT");
        public final static Property UpdatedAt = new Property(15, java.util.Date.class, "updatedAt", false, "UPDATED_AT");
    };


    public ProductsDao(DaoConfig config) {
        super(config);
    }
    
    public ProductsDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"PRODUCTS\" (" + //
                "\"BARCODE\" INTEGER PRIMARY KEY NOT NULL ," + // 0: barcode
                "\"PRODUCT_GID\" INTEGER NOT NULL ," + // 1: productGid
                "\"NAME\" TEXT NOT NULL ," + // 2: name
                "\"MRP\" INTEGER NOT NULL ," + // 3: mrp
                "\"ALTERNATE_MRP\" TEXT," + // 4: alternateMrp
                "\"UOM\" TEXT NOT NULL ," + // 5: uom
                "\"MEASURE\" INTEGER NOT NULL ," + // 6: measure
                "\"BRAND_ID\" INTEGER NOT NULL ," + // 7: brandId
                "\"CATEGORY_ID\" INTEGER NOT NULL ," + // 8: categoryId
                "\"VAT_ID\" INTEGER," + // 9: vatId
                "\"IS_DISABLED\" INTEGER NOT NULL ," + // 10: isDisabled
                "\"IMAGE\" TEXT," + // 11: image
                "\"LOCAL_NAME\" TEXT," + // 12: localName
                "\"IS_PC\" INTEGER NOT NULL ," + // 13: isPc
                "\"CREATED_AT\" INTEGER NOT NULL ," + // 14: createdAt
                "\"UPDATED_AT\" INTEGER NOT NULL );"); // 15: updatedAt
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"PRODUCTS\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Products entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getBarcode());
        stmt.bindLong(2, entity.getProductGid());
        stmt.bindString(3, entity.getName());
        stmt.bindLong(4, entity.getMrp());
 
        String alternateMrp = entity.getAlternateMrp();
        if (alternateMrp != null) {
            stmt.bindString(5, alternateMrp);
        }
        stmt.bindString(6, entity.getUom());
        stmt.bindLong(7, entity.getMeasure());
        stmt.bindLong(8, entity.getBrandId());
        stmt.bindLong(9, entity.getCategoryId());
 
        Integer vatId = entity.getVatId();
        if (vatId != null) {
            stmt.bindLong(10, vatId);
        }
        stmt.bindLong(11, entity.getIsDisabled() ? 1L: 0L);
 
        String image = entity.getImage();
        if (image != null) {
            stmt.bindString(12, image);
        }
 
        String localName = entity.getLocalName();
        if (localName != null) {
            stmt.bindString(13, localName);
        }
        stmt.bindLong(14, entity.getIsPc() ? 1L: 0L);
        stmt.bindLong(15, entity.getCreatedAt().getTime());
        stmt.bindLong(16, entity.getUpdatedAt().getTime());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Products readEntity(Cursor cursor, int offset) {
        Products entity = new Products( //
            cursor.getLong(offset + 0), // barcode
            cursor.getLong(offset + 1), // productGid
            cursor.getString(offset + 2), // name
            cursor.getInt(offset + 3), // mrp
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // alternateMrp
            cursor.getString(offset + 5), // uom
            cursor.getInt(offset + 6), // measure
            cursor.getInt(offset + 7), // brandId
            cursor.getInt(offset + 8), // categoryId
            cursor.isNull(offset + 9) ? null : cursor.getInt(offset + 9), // vatId
            cursor.getShort(offset + 10) != 0, // isDisabled
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // image
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // localName
            cursor.getShort(offset + 13) != 0, // isPc
            new java.util.Date(cursor.getLong(offset + 14)), // createdAt
            new java.util.Date(cursor.getLong(offset + 15)) // updatedAt
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Products entity, int offset) {
        entity.setBarcode(cursor.getLong(offset + 0));
        entity.setProductGid(cursor.getLong(offset + 1));
        entity.setName(cursor.getString(offset + 2));
        entity.setMrp(cursor.getInt(offset + 3));
        entity.setAlternateMrp(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setUom(cursor.getString(offset + 5));
        entity.setMeasure(cursor.getInt(offset + 6));
        entity.setBrandId(cursor.getInt(offset + 7));
        entity.setCategoryId(cursor.getInt(offset + 8));
        entity.setVatId(cursor.isNull(offset + 9) ? null : cursor.getInt(offset + 9));
        entity.setIsDisabled(cursor.getShort(offset + 10) != 0);
        entity.setImage(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setLocalName(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setIsPc(cursor.getShort(offset + 13) != 0);
        entity.setCreatedAt(new java.util.Date(cursor.getLong(offset + 14)));
        entity.setUpdatedAt(new java.util.Date(cursor.getLong(offset + 15)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Products entity, long rowId) {
        entity.setBarcode(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Products entity) {
        if(entity != null) {
            return entity.getBarcode();
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