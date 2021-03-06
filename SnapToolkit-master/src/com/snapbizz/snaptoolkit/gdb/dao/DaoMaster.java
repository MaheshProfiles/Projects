package com.snapbizz.snaptoolkit.gdb.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import de.greenrobot.dao.AbstractDaoMaster;
import de.greenrobot.dao.identityscope.IdentityScopeType;

import com.snapbizz.snaptoolkit.gdb.dao.ProductsDao;
import com.snapbizz.snaptoolkit.gdb.dao.BrandsDao;
import com.snapbizz.snaptoolkit.gdb.dao.CompaniesDao;
import com.snapbizz.snaptoolkit.gdb.dao.ProductCategoriesDao;
import com.snapbizz.snaptoolkit.gdb.dao.VatCategoriesDao;
import com.snapbizz.snaptoolkit.gdb.dao.DistributorsDao;
import com.snapbizz.snaptoolkit.gdb.dao.DistributorBranchesDao;
import com.snapbizz.snaptoolkit.gdb.dao.DistributorProductsDao;
import com.snapbizz.snaptoolkit.gdb.dao.LastSyncTimeDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * Master of DAO (schema version 1): knows all DAOs.
*/
public class DaoMaster extends AbstractDaoMaster {
    public static final int SCHEMA_VERSION = 1;

    /** Creates underlying database table using DAOs. */
    public static void createAllTables(SQLiteDatabase db, boolean ifNotExists) {
        ProductsDao.createTable(db, ifNotExists);
        BrandsDao.createTable(db, ifNotExists);
        CompaniesDao.createTable(db, ifNotExists);
        ProductCategoriesDao.createTable(db, ifNotExists);
        VatCategoriesDao.createTable(db, ifNotExists);
        DistributorsDao.createTable(db, ifNotExists);
        DistributorBranchesDao.createTable(db, ifNotExists);
        DistributorProductsDao.createTable(db, ifNotExists);
        LastSyncTimeDao.createTable(db, ifNotExists);
    }
    
    /** Drops underlying database table using DAOs. */
    public static void dropAllTables(SQLiteDatabase db, boolean ifExists) {
        ProductsDao.dropTable(db, ifExists);
        BrandsDao.dropTable(db, ifExists);
        CompaniesDao.dropTable(db, ifExists);
        ProductCategoriesDao.dropTable(db, ifExists);
        VatCategoriesDao.dropTable(db, ifExists);
        DistributorsDao.dropTable(db, ifExists);
        DistributorBranchesDao.dropTable(db, ifExists);
        DistributorProductsDao.dropTable(db, ifExists);
        LastSyncTimeDao.dropTable(db, ifExists);
    }
    
    public static abstract class OpenHelper extends SQLiteOpenHelper {

        public OpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory, SCHEMA_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i("greenDAO", "Creating tables for schema version " + SCHEMA_VERSION);
            createAllTables(db, false);
        }
    }
    
    /** WARNING: Drops all table on Upgrade! Use only during development. */
    public static class DevOpenHelper extends OpenHelper {
        public DevOpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
            dropAllTables(db, true);
            onCreate(db);
        }
    }

    public DaoMaster(SQLiteDatabase db) {
        super(db, SCHEMA_VERSION);
        registerDaoClass(ProductsDao.class);
        registerDaoClass(BrandsDao.class);
        registerDaoClass(CompaniesDao.class);
        registerDaoClass(ProductCategoriesDao.class);
        registerDaoClass(VatCategoriesDao.class);
        registerDaoClass(DistributorsDao.class);
        registerDaoClass(DistributorBranchesDao.class);
        registerDaoClass(DistributorProductsDao.class);
        registerDaoClass(LastSyncTimeDao.class);
    }
    
    public DaoSession newSession() {
        return new DaoSession(db, IdentityScopeType.Session, daoConfigMap);
    }
    
    public DaoSession newSession(IdentityScopeType type) {
        return new DaoSession(db, type, daoConfigMap);
    }
    
}
