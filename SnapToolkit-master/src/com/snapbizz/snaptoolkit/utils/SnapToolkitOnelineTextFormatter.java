package com.snapbizz.snaptoolkit.utils;

import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;

import com.snapbizz.snaptoolkit.R;
import com.snapbizz.snaptoolkit.domains.BillItem;
import com.snapbizz.snaptoolkit.domains.BillPrint;
import com.snapbizz.snaptoolkit.domains.Customer;
import com.snapbizz.snaptoolkit.domains.CustomerCreditPayment;
import com.snapbizz.snaptoolkit.domains.Distributor;

public class SnapToolkitOnelineTextFormatter {

	public static final String NUMBER_FORMAT = "###,##,##,###.##";
	protected static NumberFormat formatter;
	private static final String PRINT_PRICE_FORMAT = "############.##";
	private static final String PRINT_LINE_FORMAT = "############.##";
	
	private static final String BILL_ITEM_DIVIDER = "";
	
	private static int MAX_PRINTCHARS_PERLINE = 45;
	private static int MAX_PRINTCHARS_FORPRICE=21;
	private static int MAX_PRINTCHARS_FORNAME=21;
	
	public static final String BILL_PRINT_DATEFORMAT = "dd/MM/yy-HH:mm aa";

	public static String format3InchPrintBillText(List<BillItem> billItemList,
			double totalCartValue, float totalDiscount, float totalSavings,
			Customer customer, Context context, Date date, int totalQuantity,
			float totalVatAmount, boolean showVat, boolean showBillNumber,
			int invoiceNumber,double creditDue,boolean isFromBillingHistory) {
		NumberFormat formatter = new DecimalFormat(PRINT_PRICE_FORMAT);
		double totalCreditDue = 0;
		String storeName = SnapSharedUtils.getPrinterStoreName(context);
        String storeNumber = SnapSharedUtils.getPrinterStoreNumber(context);
        String storeAddress= SnapSharedUtils.getPrinterStoreAddress(context);
        String storeTinNumber = SnapSharedUtils.getPrinterStoreTinNumber(context);
        String storeCity = SnapSharedUtils.getPrinterStoreCity(context);
        String footer1 = SnapSharedUtils.getPrinterFooter1(context);
        String footer2 = SnapSharedUtils.getPrinterFooter2(context);
        String footer3 = SnapSharedUtils.getPrinterFooter3(context);
        
        if(!storeName.equals(""))
        	storeName = formatPrintLineText(storeName, Gravity.CENTER)+"\n";
        if(!storeAddress.equals(""))
        	storeAddress  = formatPrintLineText(storeAddress, Gravity.CENTER)+"\n";
        if(!storeNumber.equals(""))
        	storeNumber = formatPrintLineText(storeNumber, Gravity.CENTER)+"\n";
        if(!storeCity.equals(""))
        	storeCity = formatPrintLineText(storeCity, Gravity.CENTER)+"\n";
        if(!storeTinNumber.equals(""))
        	storeTinNumber = formatPrintLineText("TIN No : "+storeTinNumber, Gravity.CENTER)+"\n";
        
        String printText = formatPrintLineText(" ", Gravity.CENTER)+"\n"+storeName+storeAddress+storeCity+storeNumber+storeTinNumber+SnapToolkitTextFormatter.getBillDivider()+"\n"+
        		format3InchPrintLineDateText(new SimpleDateFormat(
				BILL_PRINT_DATEFORMAT, Locale.getDefault())
				.format(date).split("-")) + "\n";
		
        if (showBillNumber && invoiceNumber == 0) {
			int lastInvoiceNumber = SnapSharedUtils
					.getLastInvoiceID(SnapCommonUtils.getSnapContext(context));
			lastInvoiceNumber++;
			printText += SnapToolkitTextFormatter.getBillDivider() + "\n";
			printText += formatPrintLineText("Bill No: " + lastInvoiceNumber,
					Gravity.LEFT) + "\n";
		} else if (showBillNumber && invoiceNumber > 0) {
			printText += SnapToolkitTextFormatter.getBillDivider() + "\n";
			printText += formatPrintLineText("Bill No: " + invoiceNumber,
					Gravity.LEFT) + "\n";
		}
		printText += SnapToolkitTextFormatter.getBillDivider() + "\n";
		if (customer != null) {
			totalCreditDue = customer.getAmountDue()+creditDue;
			if (isFromBillingHistory) {
				totalCreditDue = customer.getAmountDue();
			} else {
				totalCreditDue = customer.getAmountDue()+creditDue;
			}
			printText += formatPrintLineText(customer.getCustomerPhoneNumber(),
					Gravity.CENTER) + "\n";
			if (customer.getCustomerName() != null && !customer.getCustomerName().isEmpty())
				printText += formatPrintLineText(customer.getCustomerName(),
						Gravity.CENTER) + "\n";
			if (customer.getCustomerAddress() != null && !customer.getCustomerAddress().isEmpty())
				printText += formatPrintLineText(customer.getCustomerAddress(),
						Gravity.CENTER) + "\n";
			printText += SnapToolkitTextFormatter.getBillDivider() + "\n";
		}
		String[] arr = "No NAME QTY MRP RATE TOTAL".split(" ");
		printText += format3InchPrintLineText(arr) + "\n" + SnapToolkitTextFormatter.getBillDivider() + "\n";
		int i = 1;
		for (BillItem billItem : billItemList) {
			String productName = billItem.getProductSkuName();
			printText += format3InchPrintNameText(i + ". " + productName);
			i++;
			float mrp = billItem.getProductSkuMrp();
			double salesPrice = billItem.getProductSkuSalePrice();
			double disPrice = mrp > salesPrice ? mrp : salesPrice;
			float qty = billItem.getProductSkuQuantity();
			if (billItem.getBillItemUnitType().ordinal() == SkuUnitType.GM
					.ordinal()
					|| billItem.getBillItemUnitType().ordinal() == SkuUnitType.ML
							.ordinal())
				qty = qty * 1000;
			printText += format3InchPrintLineText(qty, disPrice, salesPrice,
					billItem.getProductSkuQuantity() * salesPrice,
					billItem.getBillItemUnitType())
					+ "\n";
			printText += BILL_ITEM_DIVIDER + "\n";
		}
		printText += SnapToolkitTextFormatter.getBillDivider() + "\n";
		printText += formatPrintLineIntegerText("Items", billItemList.size())
				+ "\n";
		printText += formatPrintLineIntegerText("Quantity", totalQuantity)
				+ "\n";
		printText += SnapToolkitTextFormatter.getBillDivider() + "\n";
		if (showVat) {
			printText += formatPrintLineText("Sale Amount", totalCartValue
					- totalSavings - totalDiscount - totalVatAmount)
					+ "\n";
			printText += formatPrintLineText("VAT Amount test", totalVatAmount)
					+ "\n";
		}
		printText += formatPrintLineText("Gross Amount", totalCartValue) + "\n";
		if (totalSavings > 0)
			printText += formatPrintLineText("Savings", totalSavings) + "\n";
		if (totalDiscount > 0)
			printText += formatPrintLineText("Discount", totalDiscount) + "\n";
		printText += formatPrintLineText("Net Amount", Math.round(totalCartValue
				- totalSavings - totalDiscount))
				+ "\n";
		double cashReceived = (totalCartValue - totalSavings - totalDiscount - creditDue);
		if (cashReceived > 0) {
			printText += formatPrintLineText("Cash Received", cashReceived)+"\n";
		} else {
			cashReceived = 0;
			printText += formatPrintLineText("Cash Received", cashReceived)+"\n";
		}
		if(null != customer && creditDue > 0)
        {
        	printText += formatPrintLineText("Current Credit Due", creditDue)+"\n";
        }
		if(null != customer && totalCreditDue != 0)
        {
			Date ceridtDate = new Date();
			printText += formatPrintLineText("Total Credit Due (on "+formatDateText(new SimpleDateFormat(
    				BILL_PRINT_DATEFORMAT, Locale.getDefault())
    				.format(ceridtDate).split("-"))+")", totalCreditDue)+"\n";
        }
		printText += SnapToolkitTextFormatter.getBillDivider() + "\n";
		if (totalSavings > 0) {
			printText += formatPrintLineText("Total Savings Today",
					Gravity.CENTER)
					+ "\n"
					+ formatPrintLineText(
							"Rs "
									+ formatter.format(totalSavings
											+ totalDiscount), Gravity.CENTER)
					+ "\n" + SnapToolkitTextFormatter.getBillDivider() + "\n";
		}
		if(!footer1.equals(""))
        	printText += formatPrintLineText(footer1, Gravity.CENTER)+"\n";
        if(!footer2.equals(""))
        	printText += formatPrintLineText(footer2, Gravity.CENTER)+"\n";
        if(!footer3.equals(""))
        	printText += formatPrintLineText(footer3, Gravity.CENTER)+"\n";
        printText += formatPrintLineText(context.getString(R.string.snapbizz_footer), Gravity.CENTER)+"\n";
        printText += SnapToolkitTextFormatter.getBillDivider();
        
        Log.d("SnapToolkitOnelineTextFormatter", "printText-->"+printText);
        
		return printText;
	}
	
	public static BillPrint format3InchPrintBillFont(List<BillItem> billItemList,
			double totalCartValue, float totalDiscount, float totalSavings,
			Customer customer, Distributor distributor, Context context, Date date, int totalQuantity,
			float totalVatAmount, HashMap<String, Float> vatRates, boolean showVat, boolean showBillNumber,
			int invoiceNumber, double creditDue, boolean isFromBillingHistory, String receiveInvoiceNumber, 
			boolean isCustomerPayment, int shoppingCartId) {
		
		
		BillPrint billPrint=new BillPrint();
		NumberFormat formatter = new DecimalFormat(PRINT_PRICE_FORMAT);
		double totalCreditDue = 0;
        String billBody = "\n" + SnapToolkitTextFormatter.getBillDivider()+"\n"+
        		format3InchPrintLineDateText(new SimpleDateFormat(
				BILL_PRINT_DATEFORMAT, Locale.getDefault())
				.format(date).split("-")) + "\n";

        if(!showBillNumber && receiveInvoiceNumber == null && (SnapSharedUtils.getPrintEstimate(context))) {
        	billBody += SnapToolkitTextFormatter.getBillDivider() + "\n";
        	billBody += formatPrintLineText(context.getResources().getString(R.string.estimate), Gravity.CENTER) + "\n";
		}
        int currentFY = SnapCommonUtils.getCurrentFY(date);
        if (showBillNumber && invoiceNumber == 0) {
			int lastInvoiceNumber = SnapSharedUtils
					.getLastInvoiceID(SnapCommonUtils.getSnapContext(context));
			lastInvoiceNumber++;
			billBody += SnapToolkitTextFormatter.getBillDivider() + "\n";
			billBody += formatPrintLineText("Bill No: " + lastInvoiceNumber  + " (" + currentFY + "-" + (currentFY + 1) + ")",
					Gravity.LEFT) + "\n";
		} else if (showBillNumber && invoiceNumber > 0) {
			billBody += SnapToolkitTextFormatter.getBillDivider() + "\n";
			billBody += formatPrintLineText("Bill No: " + invoiceNumber  + " (" + currentFY + "-" + (currentFY + 1) + ")",
					Gravity.LEFT) + "\n";
		}
        if (receiveInvoiceNumber!= null) {
			billBody += SnapToolkitTextFormatter.getBillDivider() + "\n";
			billBody += formatPrintLineText("Dist. Invoice No: " + receiveInvoiceNumber,
					Gravity.LEFT) + "\n";
		}
        billBody += SnapToolkitTextFormatter.getBillDivider() + "\n";
		if (customer != null) {
			totalCreditDue = customer.getAmountDue()+creditDue;
			if (isFromBillingHistory || (!isCustomerPayment && creditDue <= 0)) {
								totalCreditDue = customer.getAmountDue();
							} else {
								totalCreditDue = customer.getAmountDue()+creditDue;
							}
			totalCreditDue = (double) Math.round(totalCreditDue * 100) / 100;
			billBody += formatPrintLineText(customer.getCustomerPhoneNumber(),
					Gravity.CENTER) + "\n";
			if (customer.getCustomerName() != null && !customer.getCustomerName().isEmpty())
				billBody += formatPrintLineText(customer.getCustomerName(),
						Gravity.CENTER) + "\n";
			if (customer.getCustomerAddress() != null && !customer.getCustomerAddress().isEmpty())
				billBody += formatPrintLineText(customer.getCustomerAddress(),
						Gravity.CENTER) + "\n";
			billBody += SnapToolkitTextFormatter.getBillDivider() + "\n";
		}else if(distributor!=null){
			billBody += formatPrintLineText("Purchase Invoice",
					Gravity.CENTER) + "\n";
			if (distributor.getAgencyName() != null)
				billBody += formatPrintLineText(distributor.getAgencyName(),
						Gravity.CENTER) + "\n";
			if (distributor.getPhoneNumber() != null)
				billBody += formatPrintLineText(distributor.getPhoneNumber(),
					Gravity.CENTER) + "\n";
			if (distributor.getTinNumber() != null && !distributor.getTinNumber().isEmpty())
				billBody += formatPrintLineText("Tin No :"+distributor.getTinNumber(),
						Gravity.CENTER) + "\n";
			billBody += SnapToolkitTextFormatter.getBillDivider() + "\n";
			//totalSavings=0;
		} else if (distributor == null && shoppingCartId == SnapToolkitConstants.LAST_SHOPPING_CART) {
			billBody += formatPrintLineText("Purchase Invoice", Gravity.CENTER) + "\n";
			billBody += SnapToolkitTextFormatter.getBillDivider() + "\n";
		}
		
		String[] arr;
		if (shoppingCartId == SnapToolkitConstants.LAST_SHOPPING_CART) {
			arr = "No NAME QTY MRP COST TOTAL".split(" ");
		} else {
			arr = "No NAME QTY MRP RATE TOTAL".split(" ");
		}
		billBody += format3InchPrintLineText(arr) + "\n" + SnapToolkitTextFormatter.getBillDivider() + "\n";
		int i = 1;
		for (BillItem billItem : billItemList) {
			String productName = billItem.getProductSkuName();
			billBody += format3InchPrintNameText(i + ". " + productName);
			i++;
			float mrp = billItem.getProductSkuMrp();
			double salesPrice = billItem.getProductSkuSalePrice();
			double disPrice = mrp > salesPrice ? mrp : salesPrice;
			float qty = billItem.getProductSkuQuantity();
			if (billItem.getBillItemUnitType().ordinal() == SkuUnitType.GM
					.ordinal()
					|| billItem.getBillItemUnitType().ordinal() == SkuUnitType.ML
							.ordinal())
				qty = qty * 1000;
			billBody += format3InchPrintLineText(qty, disPrice, salesPrice,
					billItem.getProductSkuQuantity() * salesPrice,
					billItem.getBillItemUnitType())
					+ "\n";
			if(SnapSharedUtils.getExtraLinePrintValue(context))
				billBody += BILL_ITEM_DIVIDER + "\n";
		}
		billBody += SnapToolkitTextFormatter.getBillDivider() + "\n";
		billBody += formatPrintLineIntegerText("Items", billItemList.size())
				+ "\n";
		billBody += formatPrintLineIntegerText("Quantity", totalQuantity)
				+ "\n";
		billBody += SnapToolkitTextFormatter.getBillDivider() + "\n";
		if (showVat) {
			String amountText="Sale Amount";
			if (shoppingCartId == SnapToolkitConstants.LAST_SHOPPING_CART) {
				amountText="Purchase Amount";
			}
			billBody += formatPrintLineText(amountText, totalCartValue- totalSavings - totalDiscount - totalVatAmount)+ "\n";		
			
			for ( Map.Entry<String, Float> entry : vatRates.entrySet()) {
			    String key = entry.getKey();
			    float value = entry.getValue();
			    if(!key.equalsIgnoreCase("0.0") && value>0 && !String.valueOf(value).contains("E"))
			    {
			    billBody += formatPrintLineText("VAT @ "+key, value)+ "\n";			
			    }
			}
			if(totalVatAmount > 0) {
			    billBody += formatPrintLineText("Net VAT Amount", totalVatAmount)
					     + "\n";
			}
		}
		billBody += formatPrintLineText("Gross Amount", totalCartValue) + "\n";
		if (totalSavings > 0 && shoppingCartId != SnapToolkitConstants.LAST_SHOPPING_CART)
			billBody += formatPrintLineText("Savings", totalSavings) + "\n";
		if (totalDiscount > 0)
			billBody += formatPrintLineText("Discount", totalDiscount) + "\n";

		double netAmount = totalCartValue - totalSavings - totalDiscount;
		String billNetAmount = formatPrintLineText("Net Amount", Math.round(netAmount)) + "\n";
		String billBottom = "" ;
		double cashReceived = netAmount;
		/* 
		 * creditDue < 0 => Customer paid more
		 * creditDue > 0 => Customer paid lesser
		 * 
		 * if CustomerPayment is false and creditDue < 0 implies
		 * Change is to be returned. This is not printed.
		 * isFromBillingHistory => CustomerPayment
		 */
		if (isCustomerPayment || creditDue > 0 || isFromBillingHistory)
			cashReceived = cashReceived - creditDue;
		if (cashReceived < 0)
			cashReceived = 0;
			billBottom += formatPrintLineText("Paid By Cash", Math.round(cashReceived)) + "\n";
		if ((null != customer || distributor != null) && creditDue > 0)
			billBottom += formatPrintLineText("Paid By Credit", creditDue)+"\n";
		if (null != customer && totalCreditDue != 0) {
			Date ceridtDate = new Date();
			billBottom += formatPrintLineText("Total Credit Due (on "+formatDateText(new SimpleDateFormat(
    				BILL_PRINT_DATEFORMAT, Locale.getDefault())
    				.format(ceridtDate).split("-"))+")", totalCreditDue)+"\n";
        }
		billBottom += SnapToolkitTextFormatter.getBillDivider() + "\n";
		String billSaving="";
		if (totalSavings > 0 && shoppingCartId != SnapToolkitConstants.LAST_SHOPPING_CART) {
			billSaving = formatPrintLineText("Total Savings Today Rs "+formatter.format(totalSavings + totalDiscount),Gravity.CENTER)+ "\n" + SnapToolkitTextFormatter.getBillDivider() + "\n";
			
		}
		billPrint.setBillSaving(billSaving);
		billPrint.setBillBody(billBody);
		billPrint.setBillNetAmount(billNetAmount);
		billPrint.setBillBottom(billBottom);
		billPrint.setMaxPrintchars(MAX_PRINTCHARS_PERLINE);
		return billPrint;
	}

	public static BillPrint format3InchCustomerCreditPayment(List<CustomerCreditPayment> customerPaymentList,
			double totalCartValue, float totalDiscount, int selectedMonth,
			Customer customer, Context context, Date date, int totalQuantity, float openingBalance) {
		BillPrint billPrint = new BillPrint();
		NumberFormat formatter = new DecimalFormat(PRINT_PRICE_FORMAT);
		double totalCreditDue = 0;
		String[] dateSplitMonth ;
		int month = 0;
		Date presentDate = new Date();
		presentDate.getTime();
		String namemon = "";
		double totalCreditDueMonth = 0;
        String billBody = formatPrintLineText(" ", Gravity.CENTER)+"\n"+SnapToolkitTextFormatter.getBillDivider()+"\n"+
        		format3InchPrintLineDateText(new SimpleDateFormat(
				BILL_PRINT_DATEFORMAT, Locale.getDefault())
				.format(presentDate).split("-")) + "\n";
		for (CustomerCreditPayment customerPayment : customerPaymentList) {
			dateSplitMonth= customerPayment.getTransactionTimeStamp().substring(0,
					customerPayment.getTransactionTimeStamp()
							.indexOf(" ")).split("/");
			 month=Integer.parseInt(dateSplitMonth[1]);
		}
        
        if(month!=0){
   		 namemon=getMonth(month);
   		}
        billBody += SnapToolkitTextFormatter.getBillDivider() + "\n";
		if (customer != null) {
			totalCreditDue = customer.getAmountDue();
			totalCreditDue = (double) Math.round(totalCreditDue * 100) / 100;
			billBody += formatPrintLineText("CREDIT HISTORY FOR : "+namemon.toUpperCase(),
					Gravity.CENTER) + "\n";
			billBody += formatPrintLineText(customer.getCustomerPhoneNumber(),
					Gravity.CENTER) + "\n";
			if (customer.getCustomerName() != null && !customer.getCustomerName().isEmpty())
				billBody += formatPrintLineText(customer.getCustomerName(),
						Gravity.CENTER) + "\n";
			if (customer.getCustomerAddress() != null && !customer.getCustomerAddress().isEmpty())
				billBody += formatPrintLineText(customer.getCustomerAddress(),
						Gravity.CENTER) + "\n";
			billBody += SnapToolkitTextFormatter.getBillDivider() + "\n";
		}
		String[] arr = "No DATE CREDIT  PAYMENT".split(" ");
		billBody += format3InchCreditPrintLineText(arr) + "\n" + SnapToolkitTextFormatter.getBillDivider() + "\n";
//		billBody += formatPrintLineText("OPENING BALANCE          " + openingBalance + "", Gravity.LEFT)+ "\n";
		billBody += "\n";
		int i = 1;
		String[] dateSplitString ;
		float sumCredit = 0;
		float sumPaid = 0;
		double openingbalance = 0;
		for (CustomerCreditPayment customerPayment : customerPaymentList) {
			dateSplitString = customerPayment.getTransactionTimeStamp().substring(0, customerPayment
								.getTransactionTimeStamp().indexOf(" ")).split("/");
			billBody += format3InchPrintNameText(i + ". " +dateSplitString[2] + "/"+ dateSplitString[1] + "/" + dateSplitString[0]);
			i++;
			String creditGiven ;
			String Paid ;
			if (customerPayment.getPaymentAmount() == 0) {
				Paid = ("--");
			}else{
				Paid = formatter.format(customerPayment.getPaymentAmount());
			}
			if (customerPayment.getPendingPayment()== 0) {
				creditGiven = "--";
			}else{
				creditGiven = formatter.format(customerPayment.getPendingPayment());
			}
			openingbalance = openingbalance + (customerPayment.getPendingPayment()-customerPayment.getPaymentAmount());
			sumPaid += customerPayment.getPaymentAmount();
			sumCredit += customerPayment.getPendingPayment();
			billBody += format3InchCreditPrintLineText( creditGiven, Paid)+ "\n";
			billBody += BILL_ITEM_DIVIDER + "\n";
		}
		totalCreditDueMonth = openingbalance;
		billBody += SnapToolkitTextFormatter.getBillDivider() + "\n";
		String SumCredit = formatter.format(sumCredit);
		String billNetAmount = " " + namemon.substring(0, 3).toUpperCase() + "                      (" + (SumCredit) + ")     " + formatter.format(sumPaid) + "\n";
		String billBottom = "";
//		billBottom += SnapToolkitTextFormatter.getBillDivider() + "\n";
//		billBottom += formatPrintLineText("CLOSING BALANCE : " + (SumCredit) + " - " + formatter.format(sumPaid) + " = ", (sumCredit - sumPaid))+"\n";
//		billBottom += SnapToolkitTextFormatter.getBillDivider() + "\n";
		if(null != customer && totalCreditDue != 0)
        {
			Date ceridtDate = new Date();
			billBottom += SnapToolkitTextFormatter.getBillDivider() + "\n";
			billBottom += formatPrintLineText("Total Credit Due (on " + formatDateText(new SimpleDateFormat(
    				BILL_PRINT_DATEFORMAT, Locale.getDefault())
					.format(ceridtDate).split("-")) + ")", totalCreditDue) + "\n";
        }
		billBottom += SnapToolkitTextFormatter.getBillDivider() + "\n";
		billBottom += formatPrintLineText("Thank you for being a loyal customer !!", Gravity.CENTER) + "\n";
		billBottom += SnapToolkitTextFormatter.getBillDivider() + "\n";
		String billSaving = "";
		billPrint.setBillSaving(billSaving);
		billPrint.setBillBody(billBody);
		billPrint.setBillNetAmount(billNetAmount);
		billPrint.setBillBottom(billBottom);
		billPrint.setMaxPrintchars(MAX_PRINTCHARS_PERLINE);
		return billPrint;
	}
	
	public static String getMonth(int month) {
	    return new DateFormatSymbols().getMonths()[month-1];
	}
	
	private static String format3InchPrintLineText(float qty, double price,
			double discount, double total, SkuUnitType skuUnitType) {
		
		NumberFormat formatter = new DecimalFormat(PRINT_LINE_FORMAT);
		String qtyString = "" ;
		qtyString =formatter.format(qty)+"."+skuUnitType.getUnitValue() ;
		qtyString=paddingLineItem(qtyString);
		String priceString = paddingLineItem(formatter.format(price));
		String discountString = paddingLineItem(formatter.format(discount));
		String totalString =paddingLineItem( formatter.format(total));
		
		return format3InchPrintLineItem(new String[] { qtyString, priceString,
				discountString, totalString });
	}

	private static String format3InchCreditPrintLineText( String creditgiven,
			String cashpaid) {
		
		NumberFormat formatter = new DecimalFormat(PRINT_LINE_FORMAT);
		String qtyString = "" ;
//		qtyString=paddingCreditLineItem(formatter.format(billamt));
		String priceString = paddingCreditLineItem(creditgiven);
		String discountString = paddingCreditLineItem((cashpaid));
		
		return format3InchPrintLineItem(new String[] {priceString,
				discountString });
	}
	
	private static String formatPrintLineText(String printLineText,
			double printLineNumber) {
		if(Math.abs(printLineNumber) <= SnapToolkitConstants.MIN_PRICE)
			printLineNumber = 0;
		String printLineNumberText = new DecimalFormat(PRINT_PRICE_FORMAT)
				.format(printLineNumber);
		int len = MAX_PRINTCHARS_PERLINE - printLineText.length()
				- printLineNumberText.length();
		printLineText = " " + printLineText;
		for (int i = 0; i < len; i++)
			printLineText += " ";
		printLineText += printLineNumberText;
		return printLineText + " ";
	}

	private static String formatPrintLineIntegerText(String printLineText,
			int printLineNumber) {
		String printLineNumberText = printLineNumber + "";
		int len = MAX_PRINTCHARS_PERLINE - printLineText.length()
				- printLineNumberText.length();
		printLineText = " " + printLineText;
		for (int i = 0; i < len; i++)
			printLineText += " ";
		printLineText += printLineNumberText;
		return printLineText + " ";
	}

	public static String formatPrintLineText(String printLineText, int gravity) {
		int len = printLineText.length();
		if (len <= MAX_PRINTCHARS_PERLINE) {
			int padding = MAX_PRINTCHARS_PERLINE - len;
			for (int i = 0; i < padding; i++) {
				if ((i < padding / 2 && gravity == Gravity.CENTER)
						|| gravity == Gravity.RIGHT)
					printLineText = " " + printLineText;
				else
					printLineText += " ";
			}
		} else {
			Matcher printLineMatcher = Pattern.compile("(.{1,"+MAX_PRINTCHARS_PERLINE+"}(\\W|$))").matcher(printLineText);
			StringBuilder printLineBuilder = new StringBuilder();
			String printText;
			while (printLineMatcher.find()) {
				printText=printLineMatcher.group()+"\n";
				len = printText.length();
				int padding = MAX_PRINTCHARS_PERLINE - len;//
				for (int i = 0; i < padding; i++) {
					 if((i < (padding / 2)+1 && gravity == Gravity.CENTER) || gravity == Gravity.RIGHT)
						 printText = " " + printText;
					else
						printText += " ";
				}
				printLineBuilder.append(printText);
			}
			return printLineBuilder.toString();
		}
		return " " + printLineText + " ";
	}

	private static String format3InchPrintNameText(String printLineText) {
		int len = printLineText.length();
		if (printLineText.length() > MAX_PRINTCHARS_FORNAME) {
			printLineText = printLineText.substring(0, MAX_PRINTCHARS_FORNAME-2) + "..";
		}
		if (len <= MAX_PRINTCHARS_FORNAME) {
			int padding = MAX_PRINTCHARS_FORNAME - len;
			for (int i = 0; i < padding; i++) {
					printLineText += " ";
			}
		}
		return printLineText + " ";
	}

	private static String format3InchPrintLineText(String[] printTextArr) {
		int namePadingLength = 17; // 23-(2+1+4)that is No+space+name
		String padding = "  "; // default two space
		String namePadding = " ";
		for (int i = 0; i < namePadingLength; i++) {
			namePadding += " ";
		}
		String text = printTextArr[0];
		for (int i = 1; i < printTextArr.length; i++) {
			if (null != printTextArr[i]
					&& printTextArr[i].equalsIgnoreCase("Qty")) {
				text += namePadding + printTextArr[i];
			} else {
				text += padding + printTextArr[i];
			}
		}
		return text;
	}
	
	private static String format3InchCreditPrintLineText(String[] printTextArr) {
		int namePadingLength = 17; // 23-(2+1+4)that is No+space+name
		String padding = "  "; // default two space
		String namePadding = " ";
		for (int i = 0; i < namePadingLength; i++) {
			namePadding += " ";
		}
		String text = printTextArr[0];
		for (int i = 1; i < printTextArr.length; i++) {
			if (null != printTextArr[i]
					&& printTextArr[i].equalsIgnoreCase("CREDIT")) {
				text += namePadding + printTextArr[i];
			} else {
				text += padding + printTextArr[i];
			}
		}
		return text;
	}
	
	private static String format3InchPrintLineDateText(String [] printTextArr) {
        int length = 0;
        for(String printText : printTextArr) {
            length += printText.length();
        }
        int paddingLength = (MAX_PRINTCHARS_PERLINE - length) / (printTextArr.length - 1);
        String padding = "";
        for(int i = 0; i < paddingLength; i++) {
            padding += " ";
        }
        String text = printTextArr[0];
        for(int i = 1; i < printTextArr.length; i++) {
            text+= padding+printTextArr[i];
        }
        return text;
    }
	
	private static String formatDateText(String [] printTextArr) {
        String text = printTextArr[0];
       
        return text;
    }

	private static String format3InchPrintLineItem(String[] printTextArr) {
		int length = 0;
		for (String printText : printTextArr) {
			length += printText.length();
		}
		String padding = "";
//		if (length < MAX_PRINTCHARS_FORPRICE) {
//			int paddingLength = (MAX_PRINTCHARS_FORPRICE - length) / (printTextArr.length - 1);
//			for (int i = 0; i < paddingLength; i++) {
//				padding += " ";
//			}
//		}
		String text = printTextArr[0];
		for (int i = 1; i < printTextArr.length; i++) {
			text += padding + printTextArr[i];
		}
		return text;
	}
	
	private static String paddingLineItem(String printText) {
		int MAX_PRINTCHARS=5;
		int length =  printText.length();
		String padding = "";
//		if (length < MAX_PRINTCHARS) {
//			int paddingLength = (MAX_PRINTCHARS - length) ;
			for (int i = MAX_PRINTCHARS; i >= length; i--) {
				padding += " ";
			}
//		}
		printText =  padding+printText;
		
		return printText;
	}
	
	private static String paddingCreditLineItem(String printText) {
		int MAX_PRINTCHARS=9;
		int length =  printText.length();
		String padding = "";
//		if (length < MAX_PRINTCHARS) {
//			int paddingLength = (MAX_PRINTCHARS - length) ;
			for (int i = MAX_PRINTCHARS; i >= length; i--) {
				padding += " ";
			}
//		}
		printText =  padding+printText;
		return printText;
	}
}
