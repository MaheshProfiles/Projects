package com.snapbizz.snaptoolkit.db;

import java.util.Date;
import java.util.List;

import android.util.Log;

import com.snapbizz.snaptoolkit.db.dao.DaoSession;
import com.snapbizz.snaptoolkit.db.dao.ProductPacksDao;
import com.snapbizz.snaptoolkit.domainsV2.Models.ProductInfo;
import com.snapbizz.snaptoolkit.db.dao.ProductPacks;

public class ProductPacksHelper {
	private static int DEFAULT_PACK_SIZE = 1;
	private static int MAX_RESULTS = 20;
	private ProductPacksDao productPacksDao = null;

	public ProductPacksHelper(DaoSession daoSession) {
		productPacksDao = daoSession.getProductPacksDao();
	}
	
	public List<ProductPacks> getProductPacks(long productCode) {
		 return productPacksDao.queryBuilder().where(ProductPacksDao.Properties.ProductCode.eq(productCode))
					 						  .orderAsc(ProductPacksDao.Properties.PackSize).limit(MAX_RESULTS).list();
	}
	
	public void createDefaultPack(ProductInfo product) {
		ProductPacks packs = new ProductPacks(product.productGid, DEFAULT_PACK_SIZE,
											  product.mrp, product.mrp, product.mrp,
											  true, new Date(), new Date());
		try {
			productPacksDao.insert(packs);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<ProductPacks> getProductPacks(ProductInfo product) {
		List<ProductPacks> packs = getProductPacks(product.productGid);
		if(packs == null || packs.size() <= 0) {
			createDefaultPack(product);
			packs = getProductPacks(product.productGid);
		}
		return packs;
	}
	
	public static Integer[] getSellingPricesAsArray(ProductPacks pack, int mrp) {
		Integer[] prices = new Integer[4];
		prices[0] = mrp;
		prices[1] = pack.getSalePrice1();
		prices[2] = pack.getSalePrice2();
		prices[3] = pack.getSalePrice3();
		return prices;
	}
	
	public void insertOrReplaceProductPack(ProductPacks productPack) {
		productPacksDao.insertOrReplace(productPack);
	}
	
	public void updateProductPack(ProductPacks productPack) {
		Log.d("TAG", "v.getText()--3--"+productPack.getSalePrice1());
		productPacksDao.update(productPack);
	}
	
	public void deleteProductPack(Long productCode) {
		productPacksDao.deleteByKey(productCode);
	}
	
	public void insertProductPacks(List<ProductPacks> productPackList) {
		productPacksDao.insertInTx(productPackList);
	}

	public List<ProductPacks> getPendingProductPacksList(Date date) {
		return productPacksDao.queryBuilder().where(ProductPacksDao.Properties.UpdatedAt.lt(date)).list();
	}

	public List<ProductPacks> getProductPackByProductCode(long productCode) {
		return productPacksDao.queryBuilder().where(ProductPacksDao.Properties.ProductCode.eq(productCode)).list();
	}

	public void updateProductPackSyncTime(List<ProductPacks> productPackList) {
		if (productPackList != null && !productPackList.isEmpty()) {
			for (ProductPacks productPack : productPackList) {
				productPack.setUpdatedAt(new Date());
	 			productPacksDao.insertOrReplace(productPack);
	 		}
		}
	}
}
