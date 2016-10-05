package com.snapbizz.snaptoolkit.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.snapbizz.snaptoolkit.db.dao.Distributors;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "DISTRIBUTORS".
*/
public class DistributorsDao extends AbstractDao<Distributors, Long> {

    public static final String TABLENAME = "DISTRIBUTORS";

    /**
     * Properties of entity Distributors.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Phone = new Property(0, long.class, "phone", true, "PHONE");
        public final static Property Tin = new Property(1, Long.class, "tin", false, "TIN");
        public final static Property Address = new Property(2, String.class, "address", false, "ADDRESS");
        public final static Property Name = new Property(3, String.class, "name", false, "NAME");
        public final static Property SalespersonName = new Property(4, String.class, "salespersonName", false, "SALESPERSON_NAME");
        public final static Property IsDisabled = new Property(5, Boolean.class, "isDisabled", false, "IS_DISABLED");
        public final static Property CreatedAt = new Property(6, java.util.Date.class, "createdAt", false, "CREATED_AT");
        public final static Property UpdatedAt = new Property(7, java.util.Date.class, "updatedAt", false, "UPDATED_AT");
    };


    public DistributorsDao(DaoConfig config) {
        super(config);
    }
    
    public DistributorsDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"DISTRIBUTORS\" (" + //
                "\"PHONE\" INTEGER PRIMARY KEY NOT NULL ," + // 0: phone
                "\"TIN\" INTEGER," + // 1: tin
                "\"ADDRESS\" TEXT," + // 2: address
                "\"NAME\" TEXT," + // 3: name
                "\"SALESPERSON_NAME\" TEXT," + // 4: salespersonName
                "\"IS_DISABLED\" INTEGER," + // 5: isDisabled
                "\"CREATED_AT\" INTEGER NOT NULL ," + // 6: createdAt
                "\"UPDATED_AT\" INTEGER NOT NULL );"); // 7: updatedAt
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"DISTRIBUTORS\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Distributors entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getPhone());
 
        Long tin = entity.getTin();
        if (tin != null) {
            stmt.bindLong(2, tin);
        }
 
        String address = entity.getAddress();
        if (address != null) {
            stmt.bindString(3, address);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(4, name);
        }
 
        String salespersonName = entity.getSalespersonName();
        if (salespersonName != null) {
            stmt.bindString(5, salespersonName);
        }
 
        Boolean isDisabled = entity.getIsDisabled();
        if (isDisabled != null) {
            stmt.bindLong(6, isDisabled ? 1L: 0L);
        }
        stmt.bindLong(7, entity.getCreatedAt().getTime());
        stmt.bindLong(8, entity.getUpdatedAt().getTime());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Distributors readEntity(Cursor cursor, int offset) {
        Distributors entity = new Distributors( //
            cursor.getLong(offset + 0), // phone
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // tin
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // address
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // name
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // salespersonName
            cursor.isNull(offset + 5) ? null : cursor.getShort(offset + 5) != 0, // isDisabled
            new java.util.Date(cursor.getLong(offset + 6)), // createdAt
            new java.util.Date(cursor.getLong(offset + 7)) // updatedAt
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Distributors entity, int offset) {
        entity.setPhone(cursor.getLong(offset + 0));
        entity.setTin(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setAddress(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setName(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setSalespersonName(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setIsDisabled(cursor.isNull(offset + 5) ? null : cursor.getShort(offset + 5) != 0);
        entity.setCreatedAt(new java.util.Date(cursor.getLong(offset + 6)));
        entity.setUpdatedAt(new java.util.Date(cursor.getLong(offset + 7)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Distributors entity, long rowId) {
        entity.setPhone(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Distributors entity) {
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