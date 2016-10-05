package com.snapbizz.snapbilling.asynctasks;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sqlcipher.Cursor;
import android.content.Context;
import android.os.AsyncTask;

import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;
import com.snapbizz.snapbilling.utils.SnapBillingConstants;
import com.snapbizz.snapbilling.utils.SnapBillingUtils;
import com.snapbizz.snaptoolkit.domains.Customer;
import com.snapbizz.snaptoolkit.domains.CustomerCreditPayment;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.TransactionType;

public class GetCustomerCreditBillsTask extends AsyncTask<String, Void, Object> {
	private Context context;
	private int taskCode;
	private String startDate;
	private String endDate;
	private SimpleDateFormat dateFormat;
	private int customerid;
	private OnQueryCompleteListener onQueryCompleteListener;
	private final String errorMessage = "Could not get customer payment";
	private TransactionType transactionType;
	Cursor cursor;
	Date date;

	public GetCustomerCreditBillsTask(Context context,
			OnQueryCompleteListener onQueryCompleteListener, String startDate,
			String endDate, int customerId, int taskCode,
			TransactionType transactionType) {
		this.context = context;
		this.onQueryCompleteListener = onQueryCompleteListener;
		this.startDate = startDate;
		this.endDate = endDate;
		this.taskCode = taskCode;
		this.customerid = customerId;
		this.transactionType = transactionType;
		dateFormat = new SimpleDateFormat(SnapBillingConstants.BILL_DATEFORMAT);
	}

	@Override
	protected Object doInBackground(String... customercreditpayment) {
		try {
			QueryBuilder<Customer, Integer> customerQueryBuilder = SnapBillingUtils
					.getHelper(context).getCustomerDao().queryBuilder();
			customerQueryBuilder
					.where()
					.like("customer_name", "%" + customercreditpayment[0] + "%")
					.or()
					.like("customer_phone", customercreditpayment[0] + "%");
			List<CustomerCreditPayment> transactionList = new ArrayList<CustomerCreditPayment>();
			if (customercreditpayment[0] != null && !customercreditpayment[0].isEmpty()) {
					String  rawQuery="Select trans.transaction_id"+" AS _id,"+"inv.invoice_id,cast(trans.total_amount as text),null as trans,trans.total_discount,cast(trans.total_savings as text), trans.transaction_timestamp, cast(trans.pending_payment as text) from transactions trans left join invoice inv on inv.transaction_id= trans.transaction_id "+"join customer on customer.customer_id = '"+ customerid+ "' where trans.transaction_timestamp >='"+startDate+ "' and trans.transaction_timestamp <='"+endDate+"' and trans.customer_id = '"+customerid + "' and trans.pending_payment > 0 "
										+" UNION "
										+" Select cuspay.customer_payment_id"+" AS _id,"+"null as cuspay,null as cuspay, cast(cuspay.payment_amount as text) as total_amount,null as cuspay,null as cuspay, cuspay.payment_date as transaction_timestamp,null as cuspay from customer_payment cuspay where cuspay.payment_date >='"+startDate+ "' and cuspay.payment_date <='"+endDate+"' and cuspay.customer_id = '"+customerid+"'order by trans.transaction_timestamp asc " ;
					GenericRawResults<String[]> rawResults = SnapBillingUtils.getHelper(context).getInvoiceDao().queryRaw(rawQuery);
					List<String[]> invoiceStringList = rawResults.getResults();
					for(int i=0; i < invoiceStringList.size(); i++) {
                        CustomerCreditPayment transaction = new CustomerCreditPayment();
                        if (invoiceStringList.get(i)[1] != null) {
                            transaction.setInvoiceNumber(Integer.parseInt(invoiceStringList.get(i)[1]));
                        } else {
                            transaction.setInvoiceNumber(0);
                        }
                        if (invoiceStringList.get(i)[2] != null) {
                            try {
                                transaction.setTotalAmount(Double.parseDouble(invoiceStringList.get(i)[2]));
                            } catch (NumberFormatException e) {
                                transaction.setTotalAmount(0);
                            }
                        }
                        if (invoiceStringList.get(i)[3] != null) {
                            try {
                                transaction.setPaymentAmount((float) (Double.parseDouble(invoiceStringList.get(i)[3])));
                            } catch (NumberFormatException e) {
                                transaction.setPaymentAmount(0);
                            }
                        }
                        if (invoiceStringList.get(i)[4] != null) {
                            try {
                                transaction.setTotalDiscount(Float.parseFloat(invoiceStringList.get(i)[4].toString()));
                            } catch (NumberFormatException e) {
                                transaction.setTotalDiscount(0);
                            }
                        }
                        if (invoiceStringList.get(i)[5] != null) {
                            try {
                                transaction.setTotalSavings(Float.parseFloat(invoiceStringList.get(i)[5].toString()));
                            } catch (NumberFormatException e) {
                                transaction.setTotalSavings(0);
                            }
                        }
                        transaction.setTransactionTimeStamp(invoiceStringList.get(i)[6]);
                        if (invoiceStringList.get(i)[7] != null) {
                            try {
                                transaction.setPendingPayment(Float.parseFloat(invoiceStringList.get(i)[7].toString()));
                            } catch (NumberFormatException e) {
                                transaction.setPendingPayment(0);
                            }
                        }
                        transactionList.add(transaction);
                    }
				}
			return transactionList;
		} catch (Exception e) {
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
