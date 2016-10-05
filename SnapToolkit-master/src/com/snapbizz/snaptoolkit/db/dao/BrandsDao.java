package com.snapbizz.snaptoolkit.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.snapbizz.snaptoolkit.db.dao.Brands;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table BRANDS.
*/
public class BrandsDao extends AbstractDao<Brands, Integer> {

    public static final String TABLENAME = "BRANDS";

    /**
     * Properties of entity Brands.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Integer.class, "id", true, "ID");
        public final static Property ParentId = new Property(1, Integer.class, "parentId", false, "PARENT_ID");
        public final static Property Name = new Property(2, String.class, "name", false, "NAME");
        public final static Property CompanyId = new Property(3, int.class, "companyId", false, "COMPANY_ID");
        public final static Property Image = new Property(4, String.class, "image", false, "IMAGE");
        public final static Property CreatedAt = new Property(5, java.util.Date.class, "createdAt", false, "CREATED_AT");
        public final static Property UpdatedAt = new Property(6, java.util.Date.class, "updatedAt", false, "UPDATED_AT");
    };


    public BrandsDao(DaoConfig config) {
        super(config);
    }
    
    public BrandsDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'BRANDS' (" + //
                "'ID' INTEGER PRIMARY KEY ," + // 0: id
                "'PARENT_ID' INTEGER," + // 1: parentId
                "'NAME' TEXT NOT NULL ," + // 2: name
                "'COMPANY_ID' INTEGER NOT NULL ," + // 3: companyId
                "'IMAGE' TEXT," + // 4: image
                "'CREATED_AT' INTEGER NOT NULL ," + // 5: createdAt
                "'UPDATED_AT' INTEGER NOT NULL );"); // 6: updatedAt
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'BRANDS'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Brands entity) {
        stmt.clearBindings();
 
        Integer id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Integer parentId = entity.getParentId();
        if (parentId != null) {
            stmt.bindLong(2, parentId);
        }
        stmt.bindString(3, entity.getName());
        stmt.bindLong(4, entity.getCompanyId());
 
        String image = entity.getImage();
        if (image != null) {
            stmt.bindString(5, image);
        }
        stmt.bindLong(6, entity.getCreatedAt().getTime());
        stmt.bindLong(7, entity.getUpdatedAt().getTime());
    }

    /** @inheritdoc */
    @Override
    public Integer readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getInt(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Brands readEntity(Cursor cursor, int offset) {
        Brands entity = new Brands( //
            cursor.isNull(offset + 0) ? null : cursor.getInt(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1), // parentId
            cursor.getString(offset + 2), // name
            cursor.getInt(offset + 3), // companyId
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // image
            new java.util.Date(cursor.getLong(offset + 5)), // createdAt
            new java.util.Date(cursor.getLong(offset + 6)) // updatedAt
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Brands entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getInt(offset + 0));
        entity.setParentId(cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1));
        entity.setName(cursor.getString(offset + 2));
        entity.setCompanyId(cursor.getInt(offset + 3));
        entity.setImage(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setCreatedAt(new java.util.Date(cursor.getLong(offset + 5)));
        entity.setUpdatedAt(new java.util.Date(cursor.getLong(offset + 6)));
     }
    
    /** @inheritdoc */
    @Override
    protected Integer updateKeyAfterInsert(Brands entity, long rowId) {
        return entity.getId();
    }
    
    /** @inheritdoc */
    @Override
    public Integer getKey(Brands entity) {
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
