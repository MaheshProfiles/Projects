package com.snapbizz.snaptoolkit.gdb.dao;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import com.snapbizz.snaptoolkit.gdb.dao.Products;
import com.snapbizz.snaptoolkit.gdb.dao.Brands;
import com.snapbizz.snaptoolkit.gdb.dao.Companies;
import com.snapbizz.snaptoolkit.gdb.dao.ProductCategories;
import com.snapbizz.snaptoolkit.gdb.dao.VatCategories;
import com.snapbizz.snaptoolkit.gdb.dao.Distributors;
import com.snapbizz.snaptoolkit.gdb.dao.DistributorBranches;
import com.snapbizz.snaptoolkit.gdb.dao.DistributorProducts;
import com.snapbizz.snaptoolkit.gdb.dao.LastSyncTime;

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
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig productsDaoConfig;
    private final DaoConfig brandsDaoConfig;
    private final DaoConfig companiesDaoConfig;
    private final DaoConfig productCategoriesDaoConfig;
    private final DaoConfig vatCategoriesDaoConfig;
    private final DaoConfig distributorsDaoConfig;
    private final DaoConfig distributorBranchesDaoConfig;
    private final DaoConfig distributorProductsDaoConfig;
    private final DaoConfig lastSyncTimeDaoConfig;

    private final ProductsDao productsDao;
    private final BrandsDao brandsDao;
    private final CompaniesDao companiesDao;
    private final ProductCategoriesDao productCategoriesDao;
    private final VatCategoriesDao vatCategoriesDao;
    private final DistributorsDao distributorsDao;
    private final DistributorBranchesDao distributorBranchesDao;
    private final DistributorProductsDao distributorProductsDao;
    private final LastSyncTimeDao lastSyncTimeDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        productsDaoConfig = daoConfigMap.get(ProductsDao.class).clone();
        productsDaoConfig.initIdentityScope(type);

        brandsDaoConfig = daoConfigMap.get(BrandsDao.class).clone();
        brandsDaoConfig.initIdentityScope(type);

        companiesDaoConfig = daoConfigMap.get(CompaniesDao.class).clone();
        companiesDaoConfig.initIdentityScope(type);

        productCategoriesDaoConfig = daoConfigMap.get(ProductCategoriesDao.class).clone();
        productCategoriesDaoConfig.initIdentityScope(type);

        vatCategoriesDaoConfig = daoConfigMap.get(VatCategoriesDao.class).clone();
        vatCategoriesDaoConfig.initIdentityScope(type);

        distributorsDaoConfig = daoConfigMap.get(DistributorsDao.class).clone();
        distributorsDaoConfig.initIdentityScope(type);

        distributorBranchesDaoConfig = daoConfigMap.get(DistributorBranchesDao.class).clone();
        distributorBranchesDaoConfig.initIdentityScope(type);

        distributorProductsDaoConfig = daoConfigMap.get(DistributorProductsDao.class).clone();
        distributorProductsDaoConfig.initIdentityScope(type);

        lastSyncTimeDaoConfig = daoConfigMap.get(LastSyncTimeDao.class).clone();
        lastSyncTimeDaoConfig.initIdentityScope(type);

        productsDao = new ProductsDao(productsDaoConfig, this);
        brandsDao = new BrandsDao(brandsDaoConfig, this);
        companiesDao = new CompaniesDao(companiesDaoConfig, this);
        productCategoriesDao = new ProductCategoriesDao(productCategoriesDaoConfig, this);
        vatCategoriesDao = new VatCategoriesDao(vatCategoriesDaoConfig, this);
        distributorsDao = new DistributorsDao(distributorsDaoConfig, this);
        distributorBranchesDao = new DistributorBranchesDao(distributorBranchesDaoConfig, this);
        distributorProductsDao = new DistributorProductsDao(distributorProductsDaoConfig, this);
        lastSyncTimeDao = new LastSyncTimeDao(lastSyncTimeDaoConfig, this);

        registerDao(Products.class, productsDao);
        registerDao(Brands.class, brandsDao);
        registerDao(Companies.class, companiesDao);
        registerDao(ProductCategories.class, productCategoriesDao);
        registerDao(VatCategories.class, vatCategoriesDao);
        registerDao(Distributors.class, distributorsDao);
        registerDao(DistributorBranches.class, distributorBranchesDao);
        registerDao(DistributorProducts.class, distributorProductsDao);
        registerDao(LastSyncTime.class, lastSyncTimeDao);
    }
    
    public void clear() {
        productsDaoConfig.getIdentityScope().clear();
        brandsDaoConfig.getIdentityScope().clear();
        companiesDaoConfig.getIdentityScope().clear();
        productCategoriesDaoConfig.getIdentityScope().clear();
        vatCategoriesDaoConfig.getIdentityScope().clear();
        distributorsDaoConfig.getIdentityScope().clear();
        distributorBranchesDaoConfig.getIdentityScope().clear();
        distributorProductsDaoConfig.getIdentityScope().clear();
        lastSyncTimeDaoConfig.getIdentityScope().clear();
    }

    public ProductsDao getProductsDao() {
        return productsDao;
    }

    public BrandsDao getBrandsDao() {
        return brandsDao;
    }

    public CompaniesDao getCompaniesDao() {
        return companiesDao;
    }

    public ProductCategoriesDao getProductCategoriesDao() {
        return productCategoriesDao;
    }

    public VatCategoriesDao getVatCategoriesDao() {
        return vatCategoriesDao;
    }

    public DistributorsDao getDistributorsDao() {
        return distributorsDao;
    }

    public DistributorBranchesDao getDistributorBranchesDao() {
        return distributorBranchesDao;
    }

    public DistributorProductsDao getDistributorProductsDao() {
        return distributorProductsDao;
    }

    public LastSyncTimeDao getLastSyncTimeDao() {
        return lastSyncTimeDao;
    }

}