package com.snapbizz.snapbilling.asynctasks;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;
import com.snapbizz.snapbilling.utils.SnapBillingUtils;
import com.snapbizz.snaptoolkit.domains.Customer;
import com.snapbizz.snaptoolkit.domains.CustomerCreditPayment;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;

public class GetMonthlyOpeningBalanceReportTask extends
		AsyncTask<String, Void, Object> {
	private OnQueryCompleteListener onQueryCompleteListener;
	private Context context;
	private int taskCode;
	private int customerid;
	private String startDate = "";
	private String endDate = "";
	private final String errorMessage = "Unable to retrieve Balance Amount.";

	public GetMonthlyOpeningBalanceReportTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, String startDate, String endDate,
			int customerId,int taskCode) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.taskCode = taskCode;
		this.customerid = customerId;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	@Override
	protected Object doInBackground(String... customerPhoneNumber) {
	    List<CustomerCreditPayment> transactionList = new ArrayList<CustomerCreditPayment>();
	    try {
	        QueryBuilder<Customer, Integer> customerQueryBuilder = SnapBillingUtils.getHelper(context)
			        .getCustomerDao().queryBuilder();
	        customerQueryBuilder.where().like("customer_name", "%" + customerPhoneNumber[0] + "%")
			        .or().like("customer_phone", customerPhoneNumber[0] + "%");
	        if (customerPhoneNumber[0] != null && !customerPhoneNumber[0].isEmpty()) {
	            String rawQuery="Select trans.transaction_id" + " AS _id,"
	                    + "null as trans, cast(trans.total_amount as text), "
	                    + "null as trans, trans.total_discount, cast(trans.total_savings as text),"
	                    + "trans.transaction_timestamp,cast(trans.pending_payment as text) "
	                    + "from "
	                    + "transactions trans left join "
	                    + "invoice inv on inv.transaction_id = trans.transaction_id "
	                    + "join customer on customer.customer_id = '" + customerid
	                    + "' where trans.transaction_timestamp >='"
	                    + startDate + "' and trans.transaction_timestamp <='"
	                    + endDate +"' and trans.customer_id = '"
	                    + customerid + "' and trans.pending_payment > 0 "
	                    + " UNION "
	                    + " Select cuspay.customer_payment_id" + " AS _id,"
	                    + "null as cuspay,null as cuspay,"
	                    + "cast(cuspay.payment_amount as text) as total_amount,"
	                    + "null as cuspay,null as cuspay,"
	                    + " cuspay.payment_date as transaction_timestamp,"
	                    + "null as cuspay from customer_payment cuspay "
	                    + "where cuspay.payment_date >='"
	                    + startDate
	                    +"' and cuspay.payment_date <='"
	                    + endDate
	                    + "' and cuspay.customer_id = '"
	                    + customerid +"'order by trans.transaction_timestamp asc " ;
	            GenericRawResults<String[]> rawResults = SnapBillingUtils.getHelper(context).getInvoiceDao().queryRaw(rawQuery);
	            List<String[]> invoiceStringList = rawResults.getResults();
	            for(int i=0;i<invoiceStringList.size();i++){
	                CustomerCreditPayment transaction = new CustomerCreditPayment();
	                if(invoiceStringList.get(i)[3]!=null){
	                    try {
	                        transaction.setPaymentAmount(Float.parseFloat(invoiceStringList.get(i)[3].toString()));
	                        } catch (NumberFormatException e) {
	                            transaction.setPaymentAmount(0);
	                            e.printStackTrace();
	                            }
	                    }
	                if(invoiceStringList.get(i)[7]!=null){
	                    try {
	                        transaction.setPendingPayment(Float.parseFloat(invoiceStringList.get(i)[7].toString()));
	                        } catch (NumberFormatException e) {
	                            transaction.setPendingPayment(0);
	                            e.printStackTrace();
	                            }
	                    }
	                transactionList.add(transaction);
	                }
	            }
	        return transactionList;
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	            }
	    }

	@Override
	protected void onPostExecute(Object result) {
	    if (result != null) {
	        onQueryCompleteListener.onTaskSuccess(result, taskCode);
	        } else {
	            onQueryCompleteListener.onTaskError(errorMessage, taskCode);
	            }
	    }

}
