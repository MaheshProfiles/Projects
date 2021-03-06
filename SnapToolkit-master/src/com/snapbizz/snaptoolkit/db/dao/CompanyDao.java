package com.snapbizz.snaptoolkit.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.snapbizz.snaptoolkit.db.dao.Company;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table COMPANY.
*/
public class CompanyDao extends AbstractDao<Company, Long> {

    public static final String TABLENAME = "COMPANY";

    /**
     * Properties of entity Company.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Name = new Property(1, String.class, "name", false, "NAME");
        public final static Property Image = new Property(2, String.class, "image", false, "IMAGE");
        public final static Property CreatedAt = new Property(3, java.util.Date.class, "createdAt", false, "CREATED_AT");
        public final static Property UpdatedAt = new Property(4, java.util.Date.class, "updatedAt", false, "UPDATED_AT");
    };


    public CompanyDao(DaoConfig config) {
        super(config);
    }
    
    public CompanyDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'COMPANY' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'NAME' TEXT," + // 1: name
                "'IMAGE' TEXT," + // 2: image
                "'CREATED_AT' INTEGER NOT NULL ," + // 3: createdAt
                "'UPDATED_AT' INTEGER NOT NULL );"); // 4: updatedAt
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'COMPANY'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Company entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(2, name);
        }
 
        String image = entity.getImage();
        if (image != null) {
            stmt.bindString(3, image);
        }
        stmt.bindLong(4, entity.getCreatedAt().getTime());
        stmt.bindLong(5, entity.getUpdatedAt().getTime());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Company readEntity(Cursor cursor, int offset) {
        Company entity = new Company( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // name
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // image
            new java.util.Date(cursor.getLong(offset + 3)), // createdAt
            new java.util.Date(cursor.getLong(offset + 4)) // updatedAt
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Company entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setImage(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setCreatedAt(new java.util.Date(cursor.getLong(offset + 3)));
        entity.setUpdatedAt(new java.util.Date(cursor.getLong(offset + 4)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Company entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Company entity) {
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
