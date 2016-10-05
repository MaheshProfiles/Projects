package com.snapbizz.snapbilling.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.snapbizz.snapbilling.domainsV2.CustomerInvoiceTranscationDetails;
import com.snapbizz.snaptoolkit.db.SnapbizzDB;
import com.snapbizz.snaptoolkit.db.dao.Invoices;
import com.snapbizz.snaptoolkit.db.dao.Transactions;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;

public class CustomerInvoicesHelper {
	
	public static List<CustomerInvoiceTranscationDetails> addInvoiceToInvoiceTranscationDetails(
			Context context, List<Invoices> invoicesList) {
		List<CustomerInvoiceTranscationDetails> listInvoiceTranscationDetails = new ArrayList<CustomerInvoiceTranscationDetails>();
		SnapbizzDB snapbizzDB = SnapbizzDB.getInstance(context, true);
		for (int i = 0; invoicesList != null && i < invoicesList.size(); i++) {
			CustomerInvoiceTranscationDetails customerInvoiceTranscationDetails = new CustomerInvoiceTranscationDetails();
			Invoices invoices = invoicesList.get(i);
			int billAmount = invoices.getTotalAmount() - invoices.getTotalDiscount() - invoices.getTotalSavings();
			List<Transactions> transactionsList = snapbizzDB.invoiceHelper.getTransactionsByInvoiceNo(invoices.getId());
			int cashAmount = (transactionsList != null && !transactionsList.isEmpty()) ? transactionsList.get(0).getAmount() : 0;
			customerInvoiceTranscationDetails.invoiceOrTranscationDate = invoices.getCreatedAt();
			customerInvoiceTranscationDetails.invoiceNo = invoices.getId();
			customerInvoiceTranscationDetails.billAmount = billAmount;
			customerInvoiceTranscationDetails.payment = cashAmount;
			customerInvoiceTranscationDetails.paymentMode = transactionsList.get(0).getPaymentMode();
			customerInvoiceTranscationDetails.credit = billAmount - cashAmount;
			listInvoiceTranscationDetails.add(customerInvoiceTranscationDetails);
		}
		return listInvoiceTranscationDetails;
	}

	public static List<CustomerInvoiceTranscationDetails> addTranscationToInvoiceTranscationDetails(
			List<Transactions> transactionsList) {
		List<CustomerInvoiceTranscationDetails> listInvoiceTranscationDetails = new ArrayList<CustomerInvoiceTranscationDetails>();
		for (int i = 0; transactionsList != null && i < transactionsList.size(); i++) {
			CustomerInvoiceTranscationDetails customerInvoiceTranscationDetails = new CustomerInvoiceTranscationDetails();
			Transactions transactions = transactionsList.get(i);
			customerInvoiceTranscationDetails.invoiceOrTranscationDate = transactions.getCreatedAt();
			if (transactions.getInvoiceId() != null)
				customerInvoiceTranscationDetails.invoiceNo = transactions.getInvoiceId();
			customerInvoiceTranscationDetails.billAmount = 0;
			customerInvoiceTranscationDetails.payment = transactions.getAmount();
			customerInvoiceTranscationDetails.paymentMode = transactions.getPaymentMode();
			customerInvoiceTranscationDetails.credit = 0;
			listInvoiceTranscationDetails.add(customerInvoiceTranscationDetails);
		}
		return listInvoiceTranscationDetails;
	}
}
