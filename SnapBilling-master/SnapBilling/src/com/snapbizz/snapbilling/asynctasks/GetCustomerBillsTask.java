package com.snapbizz.snapbilling.asynctasks;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.os.AsyncTask;

import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;
import com.snapbizz.snapbilling.utils.SnapBillingUtils;
import com.snapbizz.snaptoolkit.domains.Customer;
import com.snapbizz.snaptoolkit.domains.Invoice;
import com.snapbizz.snaptoolkit.domains.Transaction;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;
import com.snapbizz.snaptoolkit.utils.TransactionType;

public class GetCustomerBillsTask extends AsyncTask<String, Void, Object> {

    private Context context;
    private OnQueryCompleteListener onQueryCompleteListener;
    private final String errorMessage = "No Transactions Found.";
    private String startDate;
    private String endDate;
    private int taskCode;
    private boolean isOnlyCredit;
    private TransactionType transactionType;

    public GetCustomerBillsTask(Context context,
            OnQueryCompleteListener onQueryCompleteListener, String startDate,
            String endDate, boolean isOnlyCredit, int taskCode,TransactionType transactionType) {
        this.context = context;
        this.onQueryCompleteListener = onQueryCompleteListener;
        this.startDate = startDate;
        this.endDate = endDate;
        this.taskCode = taskCode;
        this.isOnlyCredit = isOnlyCredit;
        this.transactionType = transactionType;
    }
    
    private List<Transaction> getTransactionListFromInvoices(List<Invoice> invoiceList) {
    	List<Transaction> transactionList = new ArrayList<Transaction>();
        for (Invoice invoice : invoiceList) {
            Transaction transaction = new Transaction();
            transaction.setInvoiceNumber(invoice.getInvoiceID());
            transaction.setCustomer(invoice.getCustomer());
            transaction.setIsInvoice(true);
            transaction.setIsPaid(invoice.isPaid());
            transaction.setVisible(invoice.isVisible());
            transaction.setLastModifiedTimestamp(invoice.getLastModifiedTimestamp());
            transaction.setPendingPayment(invoice.getPendingPayment());
            transaction.setTotalAmount(invoice.getTotalAmount());
            transaction.setTotalDiscount(invoice.getTotalDiscount());
            transaction.setTotalQty(invoice.getTotalQty());
            transaction.setTotalSavings(invoice.getTotalSavings());
            transaction.setTransactionTimeStamp(invoice.getTransactionTimeStamp());
            transaction.setTransactionType(invoice.getTransactionType());
            transaction.setVAT(invoice.getVAT());
            transaction.setTransactionId(invoice.getTransactionId());
            transactionList.add(transaction);
        }
        return transactionList;
    }

    @Override
    protected Object doInBackground(String... params) {
        try {
            QueryBuilder<Customer, Integer> customerQueryBuilder = SnapBillingUtils.getHelper(
                    context).getCustomerDao().queryBuilder();
            customerQueryBuilder.where().like("customer_name", "%" + params[0] + "%")
            							.or().like("customer_phone", "%" + params[0] + "%");
            int latestYear = lastInvoiceYear();
            if(params[0] != null && !params[0].isEmpty()) {
                if(isOnlyCredit) {
                    if(transactionType.equals(TransactionType.BILL)){
                        QueryBuilder<Invoice, Integer> invoiceQueryBuilder = SnapBillingUtils.getHelper(context).getInvoiceDao().queryBuilder();
                    List<Invoice> invoiceList =  invoiceQueryBuilder.join(customerQueryBuilder).orderBy("invoice_id", false)
                            .where().le("transaction_timestamp", endDate).and().eq("transaction_type", TransactionType.BILL).or().eq("transaction_type",TransactionType.DELIVERY_INVOICE).and()
                            .ge("transaction_timestamp", startDate).and().eq("is_visible", true).and().gt("pending_payment", 0).query();
                    return getTransactionListFromInvoices(invoiceList);
                    } else if(transactionType.equals(TransactionType.MEMO)){
                          return SnapBillingUtils.getHelper(context).getTransactionDao().queryBuilder().join(customerQueryBuilder).orderBy("transaction_id", false)
                                  .where().le("transaction_timestamp", endDate).and().eq("transaction_type", TransactionType.MEMO).or().eq("transaction_type",TransactionType.DELIVERY_MEMO).or().eq("transaction_type",TransactionType.DELIVERY_NOTE).and()
                                  .ge("transaction_timestamp", startDate).and().eq("is_visible", true).and().gt("pending_payment", 0).query();

                    } else {
                        
//                       return SnapBillingUtils.getHelper(context).getInvoiceDao().queryBuilder().join(customerQueryBuilder).orderBy("invoice_id", false)
//                                 .where().le("transaction_timestamp", endDate).and().eq("transaction_type", TransactionType.BILL).or().eq("transaction_type",TransactionType.DELIVERY_INVOICE).and()
//                                 .ge("transaction_timestamp", startDate).and().eq("is_visible", true).and().gt("pending_payment", 0).query();
                        
                         String rawQuery = "Select invoice.invoice_id,transactions.transaction_id,transactions.total_amount,transactions.transaction_timestamp,transactions.total_discount,transactions.total_qty,transactions.vat,transactions.transaction_type,transactions.customer_id,transactions.is_paid,transactions.total_savings,transactions.lastmodified_timestamp,transactions.pending_payment,transactions.transaction_start_timestamp from transactions left join invoice on transactions.transaction_id=invoice.transaction_id join customer on customer.customer_id = transactions.customer_id where transactions.transaction_timestamp >='"+startDate+"' and transactions.transaction_timestamp <='"+endDate+"' and transactions.is_visible ='"+1+"' and (customer.customer_name like '%"+params[0]+"%' or customer.customer_phone like '%"+params[0]+"%') and transactions.pending_payment > 0 order by transactions.transaction_id desc";
                         GenericRawResults<String[]> rawResults = SnapBillingUtils.getHelper(context).getInvoiceDao().queryRaw(rawQuery);
                         List<String[]> invoiceStringList = rawResults.getResults();
                         List<Transaction> transactionList = new ArrayList<Transaction>();
                         for(int i=0;i<invoiceStringList.size();i++){
                             Transaction transaction = new Transaction();
                                if(invoiceStringList.get(i)[0]!=null){
                                 transaction.setInvoiceNumber(Integer.parseInt(invoiceStringList.get(i)[0]));
                                 transaction.setIsInvoice(true);
                                }
                                else
                                 transaction.setInvoiceNumber(0);
                                
                                transaction.setTransactionId(Integer.parseInt(invoiceStringList.get(i)[1]));
                                transaction.setTotalAmount(Double.parseDouble(invoiceStringList.get(i)[2]));
                                transaction.setTransactionTimeStamp(invoiceStringList.get(i)[3]);
                                transaction.setTotalDiscount(Float.parseFloat(invoiceStringList.get(i)[4]));
                                transaction.setTotalQty(Integer.parseInt(invoiceStringList.get(i)[5]));
                                if (null == invoiceStringList.get(i)[6]) {
                                 transaction.setVAT(0);
                                } else {
                                 transaction.setVAT(Float.parseFloat(invoiceStringList.get(i)[6]));
                                }
                                transaction.setTransactionType(TransactionType.getEnum(invoiceStringList.get(i)[7]));
                                if(invoiceStringList.get(i)[8]!=null){
                                 transaction.setCustomerId(Integer.parseInt(invoiceStringList.get(i)[8]));
                                transaction.setCustomer(SnapBillingUtils.getHelper(context).getCustomerDao().queryBuilder().where().eq("customer_id", Integer.parseInt(invoiceStringList.get(i)[8])).queryForFirst());
                                }
                                transaction.setIsPaid(invoiceStringList.get(i)[9].equals("1")?true:false);
                                if(invoiceStringList.get(i)[10]!=null && !invoiceStringList.get(i)[10].isEmpty())
                                 transaction.setTotalSavings(Float.parseFloat(invoiceStringList.get(i)[10]));
                                transaction.setLastModifiedTimestamp(new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT, Locale.getDefault()).parse(invoiceStringList.get(i)[11]));
                                transaction.setPendingPayment(Float.parseFloat(invoiceStringList.get(i)[12]));
                                transactionList.add(transaction);
                         }
                         return transactionList;
                    }
                } else {
                    if(transactionType.equals(TransactionType.BILL)){
                    	QueryBuilder<Invoice, Integer> invoiceQueryBuilder = SnapBillingUtils.getHelper(context).getInvoiceDao().queryBuilder();
                    List<Invoice> customerNameNumberList = invoiceQueryBuilder.join(customerQueryBuilder).orderBy("invoice_id", false)
                            .where().le("transaction_timestamp", endDate).and().eq("transaction_type", TransactionType.BILL).or().eq("transaction_type",TransactionType.DELIVERY_INVOICE).and()
                            .ge("transaction_timestamp", startDate).and().eq("is_visible", true).query();

                    return mergeTransactions(getTransactionListFromInvoices(customerNameNumberList), getTransactionListFromInvoices(searchInvoicesById(params[0], latestYear)));
                    } else if(transactionType.equals(TransactionType.MEMO)) {
                         return SnapBillingUtils.getHelper(context).getTransactionDao().queryBuilder().join(customerQueryBuilder).orderBy("transaction_id", false)
                                 .where().le("transaction_timestamp", endDate).and().eq("transaction_type", TransactionType.MEMO).or().eq("transaction_type",TransactionType.DELIVERY_MEMO).or().eq("transaction_type",TransactionType.DELIVERY_NOTE).and()
                                 .ge("transaction_timestamp", startDate).and().eq("is_visible", true).query();
                         
                    } else {
                    	String rawQuery = "Select invoice.invoice_id,transactions.transaction_id,transactions.total_amount,transactions.transaction_timestamp,transactions.total_discount,transactions.total_qty,transactions.vat,transactions.transaction_type,transactions.customer_id,transactions.is_paid,transactions.total_savings,transactions.lastmodified_timestamp,transactions.pending_payment,transactions.transaction_start_timestamp from transactions left join invoice on transactions.transaction_id=invoice.transaction_id join customer on customer.customer_id = transactions.customer_id where transactions.transaction_timestamp >='"+startDate+"' and transactions.transaction_timestamp <='"+endDate+"' and transactions.is_visible ='"+1+"' and (customer.customer_name like '%"+params[0]+"%' or customer.customer_phone like '%"+params[0]+"%') order by transactions.transaction_id desc";
                    	GenericRawResults<String[]> rawResults = SnapBillingUtils.getHelper(context).getInvoiceDao().queryRaw(rawQuery);
                    	List<String[]> invoiceCustomerList = rawResults.getResults();

                    	List<Transaction> transactionList = new ArrayList<Transaction>();
                    	for(int i=0; i<invoiceCustomerList.size(); i++) {
                    		Transaction transaction = new Transaction();
                    		if(invoiceCustomerList.get(i)[0]!=null) {
                    			transaction.setInvoiceNumber(Integer.parseInt(invoiceCustomerList.get(i)[0]));
                    			transaction.setIsInvoice(true);
                    		} else {
                    			transaction.setInvoiceNumber(0);
                    		}
                    		transaction.setTransactionId(Integer.parseInt(invoiceCustomerList.get(i)[1]));
                    		transaction.setTotalAmount(Double.parseDouble(invoiceCustomerList.get(i)[2]));
                    		transaction.setTransactionTimeStamp(invoiceCustomerList.get(i)[3]);
                    		transaction.setTotalDiscount(Float.parseFloat(invoiceCustomerList.get(i)[4]));
                    		transaction.setTotalQty(Integer.parseInt(invoiceCustomerList.get(i)[5]));
                    		if (null == invoiceCustomerList.get(i)[6]) {
                    			transaction.setVAT(0);
                    		} else {
                    			transaction.setVAT(Float.parseFloat(invoiceCustomerList.get(i)[6]));
                    		}
                    		transaction.setTransactionType(TransactionType.getEnum(invoiceCustomerList.get(i)[7]));
                    		if(invoiceCustomerList.get(i)[8] != null) {
                    			transaction.setCustomerId(Integer.parseInt(invoiceCustomerList.get(i)[8]));
                    			transaction.setCustomer(SnapBillingUtils.getHelper(context).getCustomerDao().queryBuilder().where().eq("customer_id", Integer.parseInt(invoiceCustomerList.get(i)[8])).queryForFirst());
                    		}
                    		transaction.setIsPaid(invoiceCustomerList.get(i)[9].equals("1")?true:false);
                    		if(invoiceCustomerList.get(i)[10] != null && !invoiceCustomerList.get(i)[10].isEmpty())
                    			transaction.setTotalSavings(Float.parseFloat(invoiceCustomerList.get(i)[10]));
                    		transaction.setLastModifiedTimestamp(new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT, Locale.getDefault()).parse(invoiceCustomerList.get(i)[11]));
                    		transaction.setPendingPayment(Float.parseFloat(invoiceCustomerList.get(i)[12]));
                    		transactionList.add(transaction);
                    	}
                    	return mergeTransactions(transactionList, getTransactionListFromInvoices(searchInvoicesById(params[0], latestYear)));
                    }
                }
            } else {
                if(isOnlyCredit) {
                    if(transactionType.equals(TransactionType.BILL)){
                        QueryBuilder<Invoice, Integer> invoiceQueryBuilder = SnapBillingUtils.getHelper(context).getInvoiceDao().queryBuilder(); 
                    List<Invoice> invoiceList =  SnapBillingUtils.getHelper(context).getInvoiceDao().queryBuilder().orderBy("invoice_id", false)
                            .where().le("transaction_timestamp", endDate).and().eq("transaction_type", TransactionType.BILL).or().eq("transaction_type",TransactionType.DELIVERY_INVOICE).and()
                            .ge("transaction_timestamp", startDate).and().eq("is_visible", true)
                            .and().gt("pending_payment", 0).query();
                    return getTransactionListFromInvoices(invoiceList);
                    }else if(transactionType.equals(TransactionType.MEMO)){
                          return SnapBillingUtils.getHelper(context).getTransactionDao().queryBuilder().orderBy("transaction_id", false)
                                  .where().le("transaction_timestamp", endDate).and().eq("transaction_type", TransactionType.MEMO).or().eq("transaction_type",TransactionType.DELIVERY_MEMO).and()
                                  .ge("transaction_timestamp", startDate).and().eq("is_visible", true)
                                  .and().gt("pending_payment", 0).query();
                    }
                    else{
//                        return SnapBillingUtils.getHelper(context).getTransactionDao().queryBuilder().orderBy("transaction_id", false)
//                                  .where().le("transaction_timestamp", endDate).and()
//                                  .ge("transaction_timestamp", startDate).and().eq("is_visible", true)
//                                  .and().gt("pending_payment", 0).query();
                         String rawQuery = "Select invoice.invoice_id,transactions.transaction_id,transactions.total_amount,transactions.transaction_timestamp,transactions.total_discount,transactions.total_qty,transactions.vat,transactions.transaction_type,transactions.customer_id,transactions.is_paid,transactions.total_savings,transactions.lastmodified_timestamp,transactions.pending_payment,transactions.transaction_start_timestamp from transactions left join invoice on transactions.transaction_id=invoice.transaction_id where transactions.transaction_timestamp >='"+startDate+"' and transactions.transaction_timestamp <='"+endDate+"' and transactions.is_visible ='"+1+"' and transactions.pending_payment > 0 order by transactions.transaction_id desc";
                         GenericRawResults<String[]> rawResults = SnapBillingUtils.getHelper(context).getInvoiceDao().queryRaw(rawQuery);
                         List<String[]> invoiceStringList = rawResults.getResults();
                         List<Transaction> transactionList= new ArrayList<Transaction>();
                         for(int i=0;i<invoiceStringList.size();i++){
                             Transaction transaction = new Transaction();
                            if(invoiceStringList.get(i)[0]!=null){
                            transaction.setInvoiceNumber(Integer.parseInt(invoiceStringList.get(i)[0]));
                            transaction.setIsInvoice(true);
                            }
                            else
                            transaction.setInvoiceNumber(0);
                            
	                        transaction.setTransactionId(Integer.parseInt(invoiceStringList.get(i)[1]));
	                        transaction.setTotalAmount(Double.parseDouble(invoiceStringList.get(i)[2]));
	                        transaction.setTransactionTimeStamp(invoiceStringList.get(i)[3]);
	                        transaction.setTotalDiscount(Float.parseFloat(invoiceStringList.get(i)[4]));
	                        transaction.setTotalQty(Integer.parseInt(invoiceStringList.get(i)[5]));
                            if (null == invoiceStringList.get(i)[6]) {
                            transaction.setVAT(0);
                            } else {
                                transaction.setVAT(Float.parseFloat(invoiceStringList.get(i)[6]));
                            }
                          transaction.setTransactionType(TransactionType.getEnum(invoiceStringList.get(i)[7]));
                            if(invoiceStringList.get(i)[8]!=null){
                            transaction.setCustomerId(Integer.parseInt(invoiceStringList.get(i)[8]));
                          transaction.setCustomer(SnapBillingUtils.getHelper(context).getCustomerDao().queryBuilder().where().eq("customer_id", Integer.parseInt(invoiceStringList.get(i)[8])).queryForFirst());
                            }
                          transaction.setIsPaid(invoiceStringList.get(i)[9].equals("1")?true:false);
                            if(invoiceStringList.get(i)[10]!=null && !invoiceStringList.get(i)[10].isEmpty())
                             transaction.setTotalSavings(Float.parseFloat(invoiceStringList.get(i)[10]));
                            transaction.setLastModifiedTimestamp(new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT, Locale.getDefault()).parse(invoiceStringList.get(i)[11]));
                            transaction.setPendingPayment(Float.parseFloat(invoiceStringList.get(i)[12]));
                            transactionList.add(transaction);
                         }
                         return transactionList;
                          
                    }
                } else {
                    if(transactionType.equals(TransactionType.BILL)){
                        QueryBuilder<Invoice, Integer> invoiceQueryBuilder = SnapBillingUtils.getHelper(context).getInvoiceDao().queryBuilder();
                        List<Invoice> invoiceList = invoiceQueryBuilder.orderBy("invoice_id", false)
                                .where().le("transaction_timestamp", endDate).and().eq("transaction_type", TransactionType.BILL).or().eq("transaction_type",TransactionType.DELIVERY_INVOICE).and()
                                .ge("transaction_timestamp", startDate).and().eq("is_visible", true)
                                .query();
                        return getTransactionListFromInvoices(invoiceList);
                        }else if(transactionType.equals(TransactionType.MEMO)){
                              return SnapBillingUtils.getHelper(context).getTransactionDao().queryBuilder().orderBy("transaction_id", false)
                                      .where().le("transaction_timestamp", endDate).and().eq("transaction_type", TransactionType.MEMO).or().eq("transaction_type",TransactionType.DELIVERY_MEMO).and()
                                      .ge("transaction_timestamp", startDate).and().eq("is_visible", true)
                                      .query();

                        }
                        else{
                            
                            String rawQuery = "Select invoice.invoice_id,transactions.transaction_id,transactions.total_amount,transactions.transaction_timestamp,transactions.total_discount,transactions.total_qty,transactions.vat,transactions.transaction_type,transactions.customer_id,transactions.is_paid,transactions.total_savings,transactions.lastmodified_timestamp,transactions.pending_payment,transactions.transaction_start_timestamp from transactions left join invoice on transactions.transaction_id=invoice.transaction_id where transactions.transaction_timestamp >='"+startDate+"' and transactions.transaction_timestamp <='"+endDate+"' and transactions.is_visible ='"+1+"' order by transactions.transaction_id desc";
                             GenericRawResults<String[]> rawResults = SnapBillingUtils.getHelper(context).getInvoiceDao().queryRaw(rawQuery);
                             List<String[]> invoiceStringList = rawResults.getResults();
                             List<Transaction> transactionList = new ArrayList<Transaction>();
                             for(int i=0;i<invoiceStringList.size();i++){
                                 Transaction transaction = new Transaction();
                                if(invoiceStringList.get(i)[0]!=null){
                                transaction.setInvoiceNumber(Integer.parseInt(invoiceStringList.get(i)[0]));
                                transaction.setIsInvoice(true);
                                }
                                else
                                transaction.setInvoiceNumber(0);
                                
                               transaction.setTransactionId(Integer.parseInt(invoiceStringList.get(i)[1]));
                               if(invoiceStringList.get(i)[2]!=null){
                            	   try {
       								transaction.setTotalAmount(Double.parseDouble(invoiceStringList.get(i)[2]));
       							} catch (NumberFormatException e) {
       								transaction.setTotalAmount(0);
       								e.printStackTrace();
       							}
                               }
                            
                             transaction.setTransactionTimeStamp(invoiceStringList.get(i)[3]);
                            transaction.setTotalDiscount(Float.parseFloat(invoiceStringList.get(i)[4]));
                            transaction.setTotalQty(Integer.parseInt(invoiceStringList.get(i)[5]));
                                if (null == invoiceStringList.get(i)[6]) {
                                transaction.setVAT(0);
                                } else {
                                transaction.setVAT(Float.parseFloat(invoiceStringList.get(i)[6]));
                                }
                               transaction.setTransactionType(TransactionType.getEnum(invoiceStringList.get(i)[7]));
                                if(invoiceStringList.get(i)[8]!=null){
                                transaction.setCustomerId(Integer.parseInt(invoiceStringList.get(i)[8]));
                               transaction.setCustomer(SnapBillingUtils.getHelper(context).getCustomerDao().queryBuilder().where().eq("customer_id", Integer.parseInt(invoiceStringList.get(i)[8])).queryForFirst());
                                }
                               transaction.setIsPaid(invoiceStringList.get(i)[9].equals("1")?true:false);
                                if(invoiceStringList.get(i)[10]!=null && !invoiceStringList.get(i)[10].isEmpty())
                                 transaction.setTotalSavings(Float.parseFloat(invoiceStringList.get(i)[10]));
                                transaction.setLastModifiedTimestamp(new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT, Locale.getDefault()).parse(invoiceStringList.get(i)[11]));
                                transaction.setPendingPayment(Float.parseFloat(invoiceStringList.get(i)[12]));
                                transactionList.add(transaction);
                             }
//                          QueryBuilder<Transaction, Integer> transactionQueryBuilder= SnapBillingUtils.getHelper(context).getTransactionDao().queryBuilder();
//                             transactionQueryBuilder.orderBy("transaction_id", false)
//                                      .where().le("transaction_timestamp", endDate).and()
//                                      .ge("transaction_timestamp", startDate).and().eq("is_visible", true);
//                             return SnapBillingUtils.getHelper(context).getInvoiceDao().queryBuilder().leftJoin(transactionQueryBuilder).orderBy("invoice_id", false)
//                                       .where().le("transaction_timestamp", endDate).and().eq("transaction_type", TransactionType.BILL).or().eq("transaction_type",TransactionType.DELIVERY_INVOICE).and()
//                                       .ge("transaction_timestamp", startDate).and().eq("is_visible", true)
//                                       .query();
                            return transactionList;
                        }
                }
            }

        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return null;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Object result) {
        if (result != null) {
            onQueryCompleteListener.onTaskSuccess(result, taskCode);
        } else
            onQueryCompleteListener.onTaskError(errorMessage, taskCode);
    }
    
    private int lastInvoiceYear() {
        int year = SnapCommonUtils.getCurrentFY();
		try {
    	    String rawQuery = "select max(invoice.invoice_id) from invoice";
			GenericRawResults<String[]> rawResults = SnapBillingUtils.getHelper(context).getInvoiceDao().queryRaw(rawQuery);
			List<String[]> result = rawResults.getResults();
            if(result.size() <= 0 || result.get(0) == null || result.get(0)[0] == null)
                return year;
            try {
                int invoiceId = Integer.parseInt(result.get(0)[0].toString());
                int invoiceYear = (int)SnapCommonUtils.getInvoiceOrMemoYear(invoiceId, true);
                if(invoiceYear < 0)
                    return year;
                return invoiceYear;
            } catch(NumberFormatException e) { }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return year;
    }

	private List<Invoice> searchInvoicesById(String invoice, int latestYear) {
    	List<Invoice> invoiceNumberList = new ArrayList<Invoice>();
    	if(invoice.length() <= 6 && SnapCommonUtils.isInteger(invoice)) {
        	for (int i = SnapToolkitConstants.OLDEST_NEW_INVOICE_YEAR; i <= latestYear; i++) {
        			int newInvoiceId = (int)(SnapToolkitConstants.ADD_FOR_INVOICE + (i * SnapToolkitConstants.YEAR_MULTIPLIER) + Integer.parseInt(invoice));
        			invoiceNumberList.addAll(rawInvoiceQuery(String.valueOf(newInvoiceId)));
        	}
        }
    	invoiceNumberList.addAll(rawInvoiceQuery(invoice));
    	return invoiceNumberList;
    }
    
	private List<Invoice> rawInvoiceQuery(String invoiceId) {
    	try {
    		if(SnapCommonUtils.isInteger(invoiceId)){
    			QueryBuilder<Invoice, Integer> invoiceIdQueryBuilder = SnapBillingUtils.getHelper(context).getInvoiceDao().queryBuilder();
    			return invoiceIdQueryBuilder.where().le("transaction_timestamp", endDate).and().eq("transaction_type", TransactionType.BILL).or().eq("transaction_type",TransactionType.DELIVERY_INVOICE).and()
    							.ge("transaction_timestamp", startDate).and().eq("is_visible", true).and().eq("invoice_id", invoiceId).query();
    		}
    	} catch (SQLException e) {
			e.printStackTrace();
		}
    	return new ArrayList<Invoice>();
    }
	
	private boolean isTransactionPresent(Transaction transaction, List<Transaction> transactionList) {
		for(Transaction dup : transactionList) {
			if(dup.getTransactionId() == 0)
				continue;
			if(dup.getTransactionId() == transaction.getTransactionId())
				return true;
		}
		return false;
	}
	
	private List<Transaction> mergeTransactions(List<Transaction> transactionList1, List<Transaction> transactionList2) {
    	List<Transaction> largerList = transactionList1;
    	List<Transaction> smallerList = transactionList2;
    	if(largerList.size() < smallerList.size()) {
    		largerList = transactionList2;
    		smallerList = transactionList1;
    	}
    	// Both empty
    	if(largerList.size() == 0)
    		return largerList;
    	for(Transaction invoice : smallerList) {
    		if(invoice.getTransactionId() == 0)
    			continue;
    		if(!isTransactionPresent(invoice, largerList))
				largerList.add(invoice);
    	}
        return largerList;
	}
}
