package com.snapbizz.snaptoolkit.db;

import java.util.Date;
import java.util.List;

import com.snapbizz.snaptoolkit.db.dao.DaoSession;
import com.snapbizz.snaptoolkit.db.dao.Inventory;
import com.snapbizz.snaptoolkit.db.dao.InventoryDao;

public class InventoryHelper {
	
	private InventoryDao inventoryDao = null;
	
	public InventoryHelper(DaoSession daoSession) {
		inventoryDao = daoSession.getInventoryDao();
	}
	
	public void insertOrReplaceInventory(Inventory inventory) {
		inventoryDao.insertOrReplace(inventory);
	}
	
	public List<Inventory> getAllInventory() {
		return inventoryDao.loadAll();
	}
	
	public List<Inventory> getInventoryByCode(Long productCode) {
		List<Inventory> inventoryList = inventoryDao.queryBuilder().where(InventoryDao.Properties.ProductCode.eq(productCode)).list();
		if (inventoryList != null && inventoryList.size() > 0)
			return inventoryList;
		return null;
	}

	public void insertInventory(List<Inventory> inventoryList) {
		inventoryDao.insertInTx(inventoryList);
	}
	
	public List<Inventory> getInventoryList(Date date) {
	 	return inventoryDao.queryBuilder().where(InventoryDao.Properties.UpdatedAt.ge(date)).list();
    }

	public List<Inventory> getInventoryByPIds(List<Long> productCodes) {
		List<Inventory> inventoryList = inventoryDao.queryBuilder().where(InventoryDao.Properties.ProductCode.in(productCodes)).list();
		if (inventoryList != null && inventoryList.size() > 0)
			return inventoryList;
		return null;
	}
	
	public void updateInventoryByPId(Long productCode, Inventory inventory) {
		if (getInventoryByCode(productCode).size() > 0)
			inventoryDao.insertOrReplace(inventory);
	}

	public void updateInventoryQuantity(Long productCode, Integer quantity) {
		updateInventory(productCode, quantity, null, null, null);
	}
	
	public void updateInventoryMBQ(Long productCode, Integer minimumBaseQuantity) {
		updateInventory(productCode, null, minimumBaseQuantity, null, null);
	}
	
	public void updateInventoryReOrderPoint(Long productCode, Integer reOrderPoint) {
		updateInventory(productCode, null, null, reOrderPoint, null);
	}
	
	public void deleteInventoryByPId(Long productCode) {
		updateInventory(productCode, null, null, null,  true);
	}
	
	private void updateInventory(Long productCode, Integer quantity, Integer minimumBaseQuantity, 
			Integer reOrderPoint, Boolean isDeleted) {
		if (getInventoryByCode(productCode).size() > 0) {
			Inventory inventory = getInventoryByCode(productCode).get(0);
			if (quantity != null)
				inventory.setQuantity(quantity);
			if (minimumBaseQuantity != null) 
				inventory.setMinimumBaseQuantity(minimumBaseQuantity);
			if (reOrderPoint != null)
				inventory.setMinimumBaseQuantity(reOrderPoint);
			if (isDeleted != null)
				inventory.setIsDeleted(isDeleted);
			inventory.setUpdatedAt(new Date());
			inventoryDao.update(inventory);
		}
	}
	
	public List<Inventory> getPendingInventoryList(Date date) {
 		return inventoryDao.queryBuilder().where(InventoryDao.Properties.UpdatedAt.lt(date)).list();
 	}
}
