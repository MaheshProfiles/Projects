package com.snapbizz.snaptoolkit.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.snapbizz.snaptoolkit.db.dao.DaoSession;
import com.snapbizz.snaptoolkit.db.dao.Invoices;
import com.snapbizz.snaptoolkit.db.dao.InvoicesDao;
import com.snapbizz.snaptoolkit.db.dao.Items;
import com.snapbizz.snaptoolkit.db.dao.ItemsDao;
import com.snapbizz.snaptoolkit.db.dao.ProductPacksDao;
import com.snapbizz.snaptoolkit.db.dao.Transactions;
import com.snapbizz.snaptoolkit.db.dao.TransactionsDao;
import com.snapbizz.snaptoolkit.domainsV2.Models.BillItem;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

import android.database.Cursor;
import android.support.v4.util.LongSparseArray;
import android.util.Log;
import de.greenrobot.dao.query.Query;

public class InvoiceHelper {
	private final static String TAG = "[InvoiceHelper]";
	private final static int MAX_RESULTS = 20;
	private InvoicesDao invoicesDao = null;
	private TransactionsDao transactionsDao = null;
	private ItemsDao itemsDao = null;
	private DaoSession daoSession = null;
	
	public static enum PaymentMode {
		CASH,
		WALLET,					/*< Using the previously paid advance */
		CREDIT_CARD,
		COUPONS,
		REL_JIO;
		
		public PaymentMode getPaymentMode(String mode) {
			for(PaymentMode pm : PaymentMode.values()) {
				if(pm.name().equalsIgnoreCase(mode))
					return pm;
			}
			return CASH;
		}
	}
	
	public static enum PaymentType {
		ADVANCE,				/*< When paying as an advance (in Wallet) */ 
		CURRENT,				/*< When paying for the current invoice/transaction */
		CREDIT;					/*< When paying for the older credit bill(s) */
		
		public PaymentType getPaymentType(String pType) {
			for(PaymentType pt : PaymentType.values()) {
				if(pt.name().equalsIgnoreCase(pType))
					return pt;
			}
			return CURRENT;
		}
	}

	public InvoiceHelper(DaoSession daoSession) {
		invoicesDao = daoSession.getInvoicesDao();
		transactionsDao = daoSession.getTransactionsDao();
		itemsDao = daoSession.getItemsDao();
		this.daoSession = daoSession;
	}
	
	/**
     * Generates the new transaction/invoice id.
     * The new invoice calculations kick in for FY16-17 or for new stores.
     * 
     *         -----------------------
     * Format: |P|P|I|Y|Y|1|2|3|4|5|6|
     *         -----------------------
     *         P: POS Id
     *         I: 0 => Memo 1 => Invoice
     *         Y: Year
     * 
     * @param bIsInvoice Memo if false
     * @return The invoice/memo new number
     * @throws SQLException
     */
    private long getNextInvoiceId(boolean bIsInvoice) throws NumberFormatException {
    	final long posPrefix = 10 * SnapToolkitConstants.POS_ID_MULTIPLIER;		// TODO: POS ID: 10 for example 
    	final long addForInvoice = bIsInvoice ? SnapToolkitConstants.ADD_FOR_INVOICE : 0;

    	final String rawQuery = "select max(" + InvoicesDao.Properties.Id.columnName
    										  + ") from " + InvoicesDao.TABLENAME + " where "
    										  + InvoicesDao.Properties.IsMemo.columnName + " = "
    										  + (!bIsInvoice ? "1" : "0");
    	// Fetch from database;
        Cursor c = daoSession.getDatabase().rawQuery(rawQuery , null);
        long previousInvoice = 0;
        if(c != null) {
        	if(c.moveToFirst())
        		previousInvoice = c.getLong(0);
        	c.close();
        }

    	long newInvoiceId = posPrefix + addForInvoice
    								  + (SnapCommonUtils.getCurrentFY() * SnapToolkitConstants.YEAR_MULTIPLIER) + 1;
		if(previousInvoice <= 0)
			return newInvoiceId;
        long previousInvoiceYear = SnapCommonUtils.getInvoiceOrMemoYear(previousInvoice, bIsInvoice);
		if(previousInvoiceYear < 0)
			 return SnapCommonUtils.getCurrentFY() <= 15 ? previousInvoice + 1 : newInvoiceId;
		if(previousInvoiceYear < SnapCommonUtils.getCurrentFY())
			return newInvoiceId;
    	return previousInvoice + 1;
    }
	
	public long createInvoice(Long customerPhone, boolean bIsInvoice, int totalAmount,
								 int pendingAmount, int totalDiscount, int totalSavings,
								 Long convertedMemoId, boolean bCredit, int totalQuantity,
								 int totalItems, int totalVatAmount, boolean bDelivery,
								 String billerName, String posName, Date billingStartedAt) {
		Invoices invoice = new Invoices(getNextInvoiceId(bIsInvoice),
										customerPhone,
										!bIsInvoice,
										totalAmount,
										pendingAmount,
										totalDiscount,
										totalSavings,
										convertedMemoId,
										bCredit,
										totalQuantity,
										totalItems,
										totalVatAmount,
										false, // isDeleted
										bDelivery,
										billerName,
										posName,
										false, //isSync
										billingStartedAt,
										new Date(),
										new Date());
		invoicesDao.insert(invoice);
		return invoice.getId();
	}
	
	private Items createItemFromBillItem(long invoiceId, final BillItem billItem) {
		if(billItem == null)
			return null;
		return new Items(null,
							   invoiceId,
							   billItem.name,
							   billItem.product.barcode,
							   billItem.uom.name(),
							   1,	// measure
							   billItem.quantity,
							   billItem.mrp,
							   billItem.sellingPrice,
							   billItem.product.vatRate,
							   billItem.vatAmount, // vatAmount
							   (int)billItem.getItemSavings(false),
							   (int)billItem.getTotal(false), // totalAmount
							   billItem.packSize,
							   new Date(),
							   new Date());
	}
	
	public boolean insertBillItems(long invoiceId, final LongSparseArray<BillItem> billItems) {
		ArrayList<Items> items = new ArrayList<Items>();
		for(int i = 0; i < billItems.size(); i++) {
			Items item = createItemFromBillItem(invoiceId, billItems.valueAt(i));
			if(item != null)
				items.add(item);
		}
		if(!items.isEmpty())
			itemsDao.insertInTx(items);
		return true;
	}
	
	public Transactions createTransaction(Long invoiceId, int amount, int remainingAmount, Long customerPhone,
									 PaymentType paymentType, PaymentMode paymentMode,
									 String paymentReference, Long parentTransactionId) {
		if(customerPhone == null && paymentType == PaymentType.ADVANCE) {
			Log.e(TAG, "Advance payment being made, but customer phone is missing!!!");
			return null;
		}
		Transactions transaction = new Transactions(null,
													invoiceId,
													paymentType.name(),
													paymentMode.name(),
													paymentReference,
													amount,
													remainingAmount,	// Amount that is paid as advance and that is unused
													customerPhone,
													parentTransactionId,
													false,
													new Date(),
													new Date());
		transactionsDao.insert(transaction);
		return transaction;
	}
	
	public int processCreditPayment(long customerPhone, int amount, PaymentMode paymentMode,
									String paymentReference, Long parentTransactionId) {
		int remainingAmount = amount;
		while(remainingAmount > 0) {
			List<Invoices> invoices = invoicesDao.queryBuilder().where(InvoicesDao.Properties.CustomerPhone.eq(customerPhone),
											 						   InvoicesDao.Properties.PendingAmount.gt(0),
											 						   InvoicesDao.Properties.IsCredit.eq(true))
															    .limit(1).build().list();
			if(invoices != null && !invoices.isEmpty()) {
				Invoices invoice = invoices.get(0);
				int newPendingAmount = 0;
				int transactionAmount = 0;
				if(invoice.getPendingAmount() > remainingAmount) {
					newPendingAmount = invoice.getPendingAmount() - remainingAmount;
					transactionAmount = remainingAmount;
					remainingAmount = 0;
				} else {
					remainingAmount = remainingAmount - invoice.getPendingAmount();
					transactionAmount = invoice.getPendingAmount();
				}
				invoice.setPendingAmount(newPendingAmount);
				invoice.setUpdatedAt(new Date());
				// invoice.setIsSync(false); // TODO: Check this
				invoicesDao.update(invoice);
				
				createTransaction(invoice.getId(),
								  transactionAmount,
								  0, //remainingAmount,
								  customerPhone,
								  PaymentType.CREDIT,
								  paymentMode,
								  paymentReference,
								  parentTransactionId);
			} else {
				break;	// No credit invoice present
			}
		}
		return remainingAmount;
	}
	
	public List<Transactions> getWalletTransactions(long customerPhone) {
		return transactionsDao.queryBuilder().where(
										TransactionsDao.Properties.CustomerPhone.eq(customerPhone),
				    					TransactionsDao.Properties.PaymentType.eq(PaymentType.ADVANCE.name()),
				    					TransactionsDao.Properties.RemainingAmount.gt(0))
							.orderAsc(TransactionsDao.Properties.CreatedAt).build().list();
	}
	
	public Invoices payFromWalletTransaction(Invoices invoice, Transactions transaction, PaymentType paymentType) {
		if(transaction.getRemainingAmount() > 0 && transaction.getCustomerPhone() == invoice.getCustomerPhone()) {
			int amount = 0;
			if(transaction.getRemainingAmount() > invoice.getPendingAmount()) {
				amount = invoice.getPendingAmount();
				transaction.setRemainingAmount(transaction.getRemainingAmount() - amount);
				invoice.setPendingAmount(0);
			} else {
				amount = transaction.getRemainingAmount();
				invoice.setPendingAmount(amount - transaction.getRemainingAmount());
				transaction.setRemainingAmount(0);
			}
			transaction.setUpdatedAt(new Date());
			transactionsDao.update(transaction);
			
			// Create new transaction for this
			createTransaction(invoice.getId(),
					  amount,
					  0, //remainingAmount,
					  invoice.getCustomerPhone(),
					  paymentType,
					  PaymentMode.WALLET,
					  null,
					  transaction.getId());
		}
		return invoice;
	}
	
	/**
	 *
	 *
	 * Updates:
	 * 		Invoice->updatedAt, pendingAmount, 
	 * 		Transaction->updatedAt, remainingAmount,
	 * @param invoiceId
	 * @param customerPhone
	 * @return
	 */
	public boolean payFromWallet(long invoiceId, long customerPhone, PaymentType paymentType) {
		Invoices invoice = getInvoiceById(invoiceId);
		if(invoice == null)
			return false;

		if(invoice.getCustomerPhone() == null || invoice.getCustomerPhone() != customerPhone)
			return false;
		
		if(invoice.getPendingAmount() <= 0)
			return false;

		List<Transactions> transactions = getWalletTransactions(customerPhone);
		if(transactions != null && !transactions.isEmpty()) {
			int i = 0;
			while(invoice.getPendingAmount() > 0 && i < transactions.size()) {
				Transactions transaction = transactions.get(i);
				invoice = payFromWalletTransaction(invoice, transaction, paymentType);
			}
			invoice.setUpdatedAt(new Date());
			invoicesDao.update(invoice);
		}

		return true;
	}
	
	public int getWalletBalance(long customerPhone) {
		int balance = 0;
		List<Transactions> transactions = getWalletTransactions(customerPhone); 
		if(transactions != null && !transactions.isEmpty()) {
			for(Transactions transaction : transactions)
				balance += transaction.getRemainingAmount();
		}
		return balance;
	}

	/**Get Customer credit Bills**/
	public List<Invoices> getCustomerCreditBillByPhone(long customerPhone) {
		return invoicesDao.queryBuilder().orderAsc(InvoicesDao.Properties.CreatedAt)
						   .where(InvoicesDao.Properties.CustomerPhone.eq(customerPhone), (InvoicesDao.Properties.PendingAmount.gt(0)), (InvoicesDao.Properties.IsCredit.eq(true))).limit(1).list();
	}
	
	public List<Invoices> getCustomerInvoicesByPhone(long customerPhone, Date startDate, Date endDate) {
		return invoicesDao.queryBuilder().orderAsc(InvoicesDao.Properties.CreatedAt)
						   .where(InvoicesDao.Properties.CustomerPhone.eq(customerPhone), InvoicesDao.Properties.CreatedAt.ge(startDate),
								   InvoicesDao.Properties.CreatedAt.lt(endDate)).list();
	}
	
	// TODO: Pick this from customerDetails table
	// TODO: Both this and totalCustomerVisitsPerMonth are the same
	public long getCustomerVisitsByPhone(long customerPhone) {
		return invoicesDao.queryBuilder().where(InvoicesDao.Properties.CustomerPhone.eq(customerPhone)).count();
	}

	// TODO: Pick this from customerDetails table
	public long totalCustomerVisitsPerMonth(long customerPhone) {
		return invoicesDao.queryBuilder().where(InvoicesDao.Properties.CustomerPhone.eq(customerPhone)).count();
	}

	public List<Transactions> getCustomerTransactionsByPhone(long customerPhone, Date startDate, Date endDate) {
		return transactionsDao.queryBuilder().orderAsc(TransactionsDao.Properties.CreatedAt).
				where(TransactionsDao.Properties.CustomerPhone.eq(customerPhone),
				TransactionsDao.Properties.CreatedAt.ge(startDate),
				TransactionsDao.Properties.CreatedAt.lt(endDate))
				.whereOr((TransactionsDao.Properties.PaymentType.eq(PaymentType.CREDIT)),
						TransactionsDao.Properties.PaymentType.eq(PaymentType.ADVANCE)).list();
	}
	
	public List<Transactions> getCustomerCashPaid(long invoiceNo) {
		return transactionsDao.queryBuilder().orderAsc(TransactionsDao.Properties.CreatedAt).
				where(TransactionsDao.Properties.InvoiceId.eq(invoiceNo),
						TransactionsDao.Properties.PaymentType.eq(PaymentType.CURRENT)).list();
	}
	
	public List<Transactions> getTransactionsByInvoiceNo(long invoiceNo) {
		return transactionsDao.queryBuilder().where(TransactionsDao.Properties.InvoiceId.eq(invoiceNo)).list();
	}
	
	public void insertTransaction(Transactions transactiondetails) {
		transactionsDao.insertInTx(transactiondetails);
	}
	
	/**Bill History Helper Methods**/
	public List<Invoices> getInvoiceOrMemoBills(boolean isMemo, Date startDate, Date endDate) {
		return invoicesDao.queryBuilder().orderAsc(InvoicesDao.Properties.CreatedAt)
				.where(InvoicesDao.Properties.IsMemo.eq(isMemo), InvoicesDao.Properties.CreatedAt.ge(startDate),
						InvoicesDao.Properties.CreatedAt.lt(endDate), InvoicesDao.Properties.IsDeleted.eq(false)).list();
	}
	
	public List<Invoices> getBills(Date startDate, Date endDate) {
		return invoicesDao.queryBuilder().orderAsc(InvoicesDao.Properties.CreatedAt)
				.where(InvoicesDao.Properties.CreatedAt.ge(startDate),
						InvoicesDao.Properties.CreatedAt.lt(endDate), InvoicesDao.Properties.IsDeleted.eq(false)).list();
	}
	
	public List<Items> getInvoiceItemsByInvoiceNo(long invoiceNo) {
		return itemsDao.queryBuilder().where(ItemsDao.Properties.InvoiceId.eq(invoiceNo)).list();
	}
	
	////////////////////// Invoices Module////////////////////////////////////////////////
	
	public void insertOrReplaceInvoice(List<Invoices> invoiceList) {
 		invoicesDao.insertOrReplaceInTx(invoiceList);
 	}
 	
 	public List<Invoices> getAllInvoices() {
 		return invoicesDao.loadAll();
 	}
 	
 	public void updateCreditedInvoice(long customerPhone, int amount) {
 		List<Invoices> invoiceList = getCreditedInvoice(customerPhone);
 		if (invoiceList != null && invoiceList.size() > 0) {
 			Invoices invoice = invoiceList.get(0);
 			if (invoice != null) {
 				invoice.setPendingAmount(amount);
 				invoice.setUpdatedAt(new Date());
 				invoicesDao.insertOrReplace(invoice);
 			}
 		}
 	}
 	
 	public List<Invoices> getCreditedInvoice(long customerPhone) {
 		return invoicesDao.queryBuilder().where(InvoicesDao.Properties.CustomerPhone.eq(customerPhone),
 												InvoicesDao.Properties.IsCredit.eq(true),
 												InvoicesDao.Properties.PendingAmount.gt(0))
 										 .orderAsc(InvoicesDao.Properties.UpdatedAt).list();
 	}	 
 	
 	public List<Invoices> getReadyToSyncInvoiceList() {
 	   	return invoicesDao.queryBuilder().orderAsc(InvoicesDao.Properties.UpdatedAt)
 	   					  .where(InvoicesDao.Properties.IsSync.eq(0)).limit(MAX_RESULTS).list();
 	}
 	
 	public boolean markSyncCompleteForInvoices(List<Invoices> invoices) {
 		if (invoices != null && !invoices.isEmpty()) {
			for (Invoices invoice : invoices) {
				invoice.setUpdatedAt(new Date());
				invoice.setIsSync(true);
				invoicesDao.insertOrReplace(invoice);
			}
			return true;
		}
 		return false;
	 }
 	
 	public Invoices getInvoiceById(long invoiceId) {
		 List<Invoices> invoices = invoicesDao.queryBuilder().where(InvoicesDao.Properties.Id.eq(invoiceId)).build().list();
		 if(invoices == null || invoices.isEmpty())
			 return null;
		 return invoices.get(0);
	 }
	 
 	// TODO: Check this. Why is this required?
	public void updateInvoiceSyncTime(Date date, long invoiceId) {
		Invoices invoice = getInvoiceById(invoiceId);
		if (invoice != null) {
			invoice.setUpdatedAt(new Date());
			invoicesDao.insertOrReplace(invoice); // TODO: this says update while it does insertOrReplace
		}
	}

	public void updateInvoice(long phone, int pendingPayment) {
		List<Invoices> invoicedetailsList = getCustomerCreditBillByPhone(phone);
		if (!invoicedetailsList.isEmpty()) {
			Invoices invoicesDetails = invoicedetailsList.get(0);
			if (pendingPayment != 0)
				invoicesDetails.setPendingAmount(pendingPayment);
			invoicesDetails.setUpdatedAt(new Date());
			invoicesDao.update(invoicesDetails);
		}
	}
	
 	
 	//////////////////////////Items//////////////////////////////////////////////////////////////
 	
 	public List<Items> getItemsByInvoiceID(Long invoiceID) {
		return itemsDao.queryBuilder().where(ItemsDao.Properties.InvoiceId.eq(invoiceID)).list();
	}
 	
 	public void insertOrReplaceItems(List<Items> itemList) {
 		itemsDao.insertOrReplaceInTx(itemList);
	}
    ////////////////////////Transactions//////////////////////////////////////////////////
	
	public void insertOrReplaceTransaction(List<Transactions> transactionList) {
		transactionsDao.insertOrReplaceInTx(transactionList);
	}
	
	public List<Transactions> getAllTransactions() {
		return transactionsDao.loadAll();
	}
	
	public List<Transactions> getPendingTransactionList(Date date) {
		return transactionsDao.queryBuilder().where(TransactionsDao.Properties.UpdatedAt.lt(date)).list();
	}
	
	public List<Transactions> getReadyToSyncTransactionList() {
		return transactionsDao.queryBuilder().orderAsc(TransactionsDao.Properties.UpdatedAt)
					           .where(TransactionsDao.Properties.IsSync.eq(0)).limit(MAX_RESULTS).list();
	}
	 
	public boolean markSyncCompleteForTransactions(List<Transactions> transactionList) {
		if (transactionList != null && !transactionList.isEmpty()) {
			for (Transactions transaction : transactionList) {
				transaction.setUpdatedAt(new Date());
				transaction.setIsSync(true);
				transactionsDao.insertOrReplace(transaction);
			}
			return true;
		}
	 	return false;
	}

	public List<Transactions> getTransactionByInvoiceId(long invoiceId) {
		return transactionsDao.queryBuilder().where(TransactionsDao.Properties.Id.eq(invoiceId)).list();
	}

	public void updateTransactionSyncTime(Date date, long invoiceId) {
		List<Transactions> transactionsList = getTransactionByInvoiceId(invoiceId);
		if (transactionsList != null && !transactionsList.isEmpty()) {
			for (Transactions transaction : transactionsList) {
				transaction.setUpdatedAt(new Date());
				transactionsDao.insertOrReplace(transaction);
			}
		}
	}

}
