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

import android.R.string;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;

import com.snapbizz.snaptoolkit.R;
import com.snapbizz.snaptoolkit.domains.BillItem;
import com.snapbizz.snaptoolkit.domains.BillPrint;
import com.snapbizz.snaptoolkit.domains.Customer;
import com.snapbizz.snaptoolkit.domains.CustomerCreditPayment;
import com.snapbizz.snaptoolkit.domains.Distributor;

public class SnapToolkitTextFormatter {

	public static final String NUMBER_FORMAT = "###,##,##,###.##";
	protected static NumberFormat formatter;
	public static final String PRINT_PRICE_FORMAT = "############.##";
	private static final String BILL_ITEM_DIVIDER = "";
	private static int MAX_PRINTCHARS_PERLINE = 30;
	public static final String BILL_PRINT_DATEFORMAT = "dd/MM/yy-HH:mm aa";

	// private static final String BILL_ITEM_DIVIDER =
	// "------------------------------";

	public static int getMAX_PRINTCHARS_PERLINE() {
		return MAX_PRINTCHARS_PERLINE;
	}

	public static void setMAX_PRINTCHARS_PERLINE(int mAX_PRINTCHARS_PERLINE) {
		MAX_PRINTCHARS_PERLINE = mAX_PRINTCHARS_PERLINE;
	}
	
	public static String formatPriceText(double price, Context context, boolean bDisplayZero, boolean bDisplayRupee) {
		if (formatter == null)
			formatter = new DecimalFormat(NUMBER_FORMAT);
		String rupee = context.getString(R.string.rupee_symbol);
		String fmtPrice = (bDisplayRupee ? rupee : "") + formatter.format(Math.abs(price));

		if(Math.abs(price) <= SnapToolkitConstants.MIN_PRICE) {
			if(bDisplayZero)
				return fmtPrice;
			return "--";
		}

		if (price < 0)
			fmtPrice = "- " + fmtPrice;
		return fmtPrice;
	}

    public static String formatPriceText(double price, Context context, boolean bDisplayZero) {
		return formatPriceText(price, context, bDisplayZero, true);
    }

	public static String formatPriceText(double price, Context context) {
		return formatPriceText(price, context, true, true);
	}

	public static String formatNumberText(double number, Context context) {
		if (formatter == null)
			formatter = new DecimalFormat(NUMBER_FORMAT);
		return formatter.format(number);
	}

	public static String formatRoundedPriceText(double price, Context context) {
		NumberFormat formatter = new DecimalFormat(NUMBER_FORMAT);
		price = Math.round(price);
		String fmtPrice = formatter.format(Math.abs(price));
		String rupee = context.getString(R.string.rupee_symbol);

		if (price < 0)
			return "-" + rupee + fmtPrice;
		else
			return rupee + fmtPrice;
	}

	public static String formatPriceText(float price, Context context) {
		NumberFormat formatter = new DecimalFormat(NUMBER_FORMAT);
		String fmtPrice = formatter.format(Math.abs(price));
		String rupee = context.getString(R.string.rupee_symbol);

		if (price < 0)
			return "-" + rupee + fmtPrice;
		else
			return rupee + fmtPrice;
	}

	public static String formatPriceTextCheckoutCash(float price,
			Context context) {
		NumberFormat formatter = new DecimalFormat(NUMBER_FORMAT);
		String fmtPrice = formatter.format(Math.round(Math.abs(price)));
		String rupee = context.getString(R.string.rupee_symbol);

		if (price < 0)
			return "-" + rupee + fmtPrice;
		else
			return rupee + fmtPrice;
	}

	public static String formatNumberText(int number) {
		return new DecimalFormat(NUMBER_FORMAT).format(number);
	}

	public static String formatNumberText(float number) {
		return new DecimalFormat(NUMBER_FORMAT).format(number);
	}
	
	public static String formatRoundedPriceText(float price, Context context) {
		NumberFormat formatter = new DecimalFormat(NUMBER_FORMAT);
		String fmtPrice = formatter.format(Math.abs(price));
		String rupee = context.getString(R.string.rupee_symbol);

		if (price < 0)
			return "-" + rupee + fmtPrice;
		else
			return rupee + fmtPrice;
	}

	public static String capitalseText(String name) {
		String formattedName = "";
		if (name == null)
			return formattedName;
		for (String word : name.split(" ")) {
			if (word.length() != 0)
				formattedName += word.substring(0, 1).trim()
						.toUpperCase(Locale.US)
						+ word.substring(1).trim().toLowerCase(Locale.US) + " ";
		}
		return formattedName.trim();
	}

	public static String getBillDivider() {
		String divider = "";
		for (int i = 0; i < MAX_PRINTCHARS_PERLINE; i++) {
			divider += "-";
		}
		return divider;
	}

	public static BillPrint formatPrintBillText(List<BillItem> billItemList,
			double totalCartValue, float totalDiscount, float totalSavings,
			Customer customer, Distributor distributor, Context context, Date date, int totalQuantity,
			float totalVatAmount, HashMap<String, Float> vatRates, boolean showVat, boolean showBillNumber,
			int invoiceNumber, double creditDue, boolean isFromBillingHistory, String receiveInvoiceNumber, boolean isCustomerPayment, int shoppingCartId) {

		BillPrint billPrint = new BillPrint();
		NumberFormat formatter = new DecimalFormat(PRINT_PRICE_FORMAT);
		double totalCreditDue = 0;

		String billBody = "\n"
				+ SnapToolkitTextFormatter.getBillDivider()
				+ "\n"
				+ formatPrintLineText(new SimpleDateFormat(
						BILL_PRINT_DATEFORMAT, Locale.getDefault())
						.format(date).split("-")) + "\n";

		if(!showBillNumber && receiveInvoiceNumber == null && (SnapSharedUtils.getPrintEstimate(context))) {
			billBody += getBillDivider() + "\n";
			billBody += formatPrintLineText(context.getResources().getString(R.string.estimate), Gravity.CENTER) + "\n";
		}
		int currentFY = SnapCommonUtils.getCurrentFY(date);
		if (showBillNumber && invoiceNumber == 0) {
			int lastInvoiceNumber = SnapSharedUtils
					.getLastInvoiceID(SnapCommonUtils.getSnapContext(context));
			lastInvoiceNumber++;
			billBody += getBillDivider() + "\n";
			billBody += formatPrintLineText("Bill No: " + lastInvoiceNumber  + " (" + currentFY + "-" + (currentFY + 1) + ")",
					Gravity.LEFT) + "\n";
		} else if (showBillNumber && invoiceNumber > 0) {
			billBody += getBillDivider() + "\n";
			billBody += formatPrintLineText("Bill No: " + invoiceNumber  + " (" + currentFY + "-" + (currentFY + 1) + ")",
					Gravity.LEFT) + "\n";
		}
		if (receiveInvoiceNumber!= null) {
			billBody += getBillDivider() + "\n";
			billBody += formatPrintLineText("Dist. Invoice No: ",
					Gravity.CENTER) + "\n";
			billBody += formatPrintLineText(receiveInvoiceNumber,
					Gravity.CENTER) + "\n";
		}
		billBody += getBillDivider() + "\n";
		
		if (customer != null) {
			billBody += formatPrintLineText(customer.getCustomerPhoneNumber(),
					Gravity.CENTER) + "\n";
			if (customer.getCustomerName() != null && !customer.getCustomerName().isEmpty())
				billBody += formatPrintLineText(customer.getCustomerName(),
						Gravity.CENTER) + "\n";
			if (customer.getCustomerAddress() != null && !customer.getCustomerAddress().isEmpty())
				billBody += formatPrintLineText(customer.getCustomerAddress(),
						Gravity.CENTER) + "\n";
			totalCreditDue = customer.getAmountDue() + creditDue;
			if (isFromBillingHistory || (!isCustomerPayment && creditDue <= 0)) {
				totalCreditDue = customer.getAmountDue();
			} else {
				totalCreditDue = customer.getAmountDue() + creditDue;
			}
			totalCreditDue = (double) Math.round(totalCreditDue * 100) / 100;
			billBody += getBillDivider() + "\n";

		} else if(distributor!=null) {
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
			billBody += getBillDivider() + "\n";
			//totalSavings=0;
		} else if (distributor == null && shoppingCartId == SnapToolkitConstants.LAST_SHOPPING_CART) {
			billBody += formatPrintLineText("Purchase Invoice", Gravity.CENTER) + "\n";
			billBody += SnapToolkitTextFormatter.getBillDivider() + "\n";
		}
		String[] arr;
		if (shoppingCartId == SnapToolkitConstants.LAST_SHOPPING_CART) {
			arr = "Qty_        MRP_  Cost_ Total".split("_");
		}else{
			arr = "Qty_        MRP_  Rate_ Total".split("_");
		}
		
		billBody += formatPrintLineTextHeader(arr) + "\n" + getBillDivider() + "\n";
		int i = 1;
		for (BillItem billItem : billItemList) {
			String productName = billItem.getProductSkuName();
			if (productName.length() > 24) {
				productName = productName.substring(0, 23) + ".";
			}
			billBody += formatPrintLineText(i + ". " + productName,
					Gravity.LEFT) + "\n";
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
			billBody += formatPrintLineText(qty, disPrice, salesPrice + "",
					billItem.getProductSkuQuantity() * salesPrice,
					billItem.getBillItemUnitType()) + "\n";
			Boolean print_Savings=SnapSharedUtils.getPrintCheckValue(context);
			Log.d("print_Savings", "" + print_Savings);
			if(print_Savings == true && salesPrice - mrp < 0 && shoppingCartId != SnapToolkitConstants.LAST_SHOPPING_CART) {
				billBody += formatPrintLineText("Savings: " + formatter.format((mrp - salesPrice)
												* billItem.getProductSkuQuantity()),Gravity.RIGHT) + "\n";
			}
			if(SnapSharedUtils.getExtraLinePrintValue(context))
				billBody += BILL_ITEM_DIVIDER + "\n";
		}
		billBody += getBillDivider() + "\n";
		billBody += formatPrintLineIntegerText("Items", billItemList.size())
				+ "\n";
		billBody += formatPrintLineIntegerText("Quantity", totalQuantity)
				+ "\n";
		billBody += getBillDivider() + "\n";
		if (showVat) {
			String amountText="Sale Amount";
			if (shoppingCartId == SnapToolkitConstants.LAST_SHOPPING_CART) {
				amountText="Purchase Amount";
			}
			billBody += formatPrintLineText(amountText, totalCartValue
					- totalSavings - totalDiscount - totalVatAmount)
					+ "\n";
			
			for ( Map.Entry<String, Float> entry : vatRates.entrySet()) {
			    String key = entry.getKey();
			    float value = entry.getValue();
			    if(!key.equalsIgnoreCase("0.0") && !String.valueOf(value).contains("E")) {
			    	billBody += formatPrintLineText("VAT @ " + key, value) + "\n";			
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
		String billBottom = "";
		double cashReceived = netAmount;
		/* 
		 * creditDue < 0 => Customer paid more
		 * creditDue > 0 => Customer paid lesser
		 * 
		 * if CustomerPayment is false and creditDue < 0 implies
		 * Change is to be returned. This is not printed.
		 * isFromBillingHistory => customerPayment
		 */
		if (isCustomerPayment || creditDue > 0 || isFromBillingHistory)
			cashReceived = cashReceived - creditDue;
		if (cashReceived < 0)
			cashReceived = 0;
		billBottom += formatPrintLineText("Paid By Cash", Math.round(cashReceived)) + "\n";
		if ((null != customer || distributor != null) && creditDue > 0) {
			billBottom += formatPrintLineText("Paid By Credit", creditDue) + "\n";
		}
		if (customer != null && totalCreditDue != 0) {
			Date ceridtDate = new Date();
			billBottom += formatPrintLineText("Total Credit Due ",Gravity.LEFT) + "\n";
			billBottom += formatPrintLineText("(on "+formatDateText(new SimpleDateFormat(
    				BILL_PRINT_DATEFORMAT, Locale.getDefault())
    				.format(ceridtDate).split("-"))+")", totalCreditDue)+"\n";
			

		}
		billBottom += getBillDivider() + "\n";
		String billSaving = "";
		if (totalSavings > 0 && shoppingCartId != SnapToolkitConstants.LAST_SHOPPING_CART) {
			billSaving = formatPrintLineText("Total Savings Today",
					Gravity.CENTER)
					+ "\n"
					+ formatPrintLineText(
							"Rs "
									+ formatter.format(totalSavings
											+ totalDiscount), Gravity.CENTER)
					+ "\n" + getBillDivider() + "\n";
		}

		billPrint.setBillSaving(billSaving);
		billPrint.setBillBody(billBody);
		billPrint.setBillNetAmount(billNetAmount);
		billPrint.setBillBottom(billBottom);
		billPrint.setMaxPrintchars(MAX_PRINTCHARS_PERLINE);
		return billPrint;
	}

	public static BillPrint formatCustomerCreditPayment(List<CustomerCreditPayment> customerPaymentList,
		double totalCartValue, float totalDiscount, float totalSavings,
		Customer customer, Context context, Date date, int totalQuantity,
		 float openingBalance) {
	BillPrint billPrint=new BillPrint();
	NumberFormat formatter = new DecimalFormat(PRINT_PRICE_FORMAT);
	double totalCreditDue = 0;
	double totalCreditDueMonth = 0;
	String[] dateSplitMonth ;
	Date presentDate = new Date();
	presentDate.getTime();
	int month = 0;
	String namemon = "";
    String billBody = formatPrintLineText(" ", Gravity.CENTER)+"\n"+SnapToolkitTextFormatter.getBillDivider()+"\n"+
    		formatPrintLineText(new SimpleDateFormat(BILL_PRINT_DATEFORMAT, Locale.getDefault())
			.format(presentDate).split("-")) + "\n";
    for (CustomerCreditPayment customerPayment : customerPaymentList) {
		dateSplitMonth= customerPayment.getTransactionTimeStamp().substring(0,
				customerPayment.getTransactionTimeStamp().indexOf(" ")).split("/");
		month=Integer.parseInt(dateSplitMonth[1]);
	}
    if(month != 0) {
    	namemon = getMonth(month);
   	}
    billBody += SnapToolkitTextFormatter.getBillDivider() + "\n";
	if (customer != null) {
		totalCreditDue = customer.getAmountDue();
		totalCreditDue = (double) Math.round(totalCreditDue * 100) / 100;
		billBody += formatPrintLineText("CREDIT HISTORY :"+namemon.toUpperCase(),
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
	billBody += formatPrintLineTextHeader(arr) + "\n" + SnapToolkitTextFormatter.getBillDivider() + "\n";
	NumberFormat formatno = new DecimalFormat(PRINT_PRICE_FORMAT);
//	billBody += formatPrintLineText("OPENING BAL  "+formatno.format(openingBalance)+"", Gravity.LEFT)+ "\n";
	billBody += "\n";
	int i = 1;
	String[] dateSplitString ;
	float sumCredit = 0;
	float sumPaid = 0;
	double openingbalance = 0;
	for (CustomerCreditPayment customerPayment : customerPaymentList) {
		dateSplitString= customerPayment.getTransactionTimeStamp().substring(0,
						customerPayment.getTransactionTimeStamp()
								.indexOf(" ")).split("/");
		billBody += formatPrintLineText(i + ". "  + dateSplitString[2] + "/" + dateSplitString[1]
											+ "/" + dateSplitString[0],Gravity.LEFT) + "\n";
		i++;
		String creditGiven;
		String Paid;
		if (customerPayment.getPaymentAmount() == 0) {
			Paid = ("--");
		} else {
			Paid = formatter.format(customerPayment.getPaymentAmount());
		}
		if (customerPayment.getPendingPayment()== 0) {
			creditGiven = "--";
		} else {
			creditGiven = formatter.format(customerPayment.getPendingPayment());
		}
		openingbalance=openingbalance+(customerPayment.getPendingPayment()-customerPayment.getPaymentAmount());
		sumPaid += customerPayment.getPaymentAmount();
		sumCredit +=customerPayment.getPendingPayment();
		billBody += formatCreditLineText( creditGiven, Paid)+ "\n";
		billBody += BILL_ITEM_DIVIDER + "\n";
	}
	totalCreditDueMonth=openingbalance;
	billBody += SnapToolkitTextFormatter.getBillDivider();
	String SumCredit = formatter.format(sumCredit);
	String billNetAmount = " " + namemon.substring(0, 3).toUpperCase() + "           (" + SumCredit + ")     " + formatter.format((sumPaid));
	String billBottom = "" ;
//	billBottom += SnapToolkitTextFormatter.getBillDivider() + "\n";
//	billBottom += ("CLOSING BALANCE" + "\n");
//	billBottom += formatPrintLineText(SumCredit + " - " + formatter.format(sumPaid) + "  = ", (sumCredit - sumPaid)) + "\n";
	if (customer != null && totalCreditDue != 0) {
		Date ceridtDate = new Date();
		billBottom += getBillDivider() + "\n";
		billBottom += formatPrintLineText("Total Credit Due ",Gravity.LEFT) + "\n";
		billBottom += formatPrintLineText("(on " + formatDateText(new SimpleDateFormat(
												BILL_PRINT_DATEFORMAT, Locale.getDefault())
												.format(ceridtDate).split("-")) + ")", totalCreditDue) + "\n";
	}
	billBottom += getBillDivider() + "\n";
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
	
	public static String checkPrintBillText(List<BillItem> billItemList,
			double totalCartValue, float totalDiscount, float totalSavings,
			Customer customer, Context context, Date date, int totalQuantity,
			float totalVatAmount, boolean showVat, boolean showBillNumber,
			int invoiceNumber, double creditDue, boolean isFromBillingHistory) {
		NumberFormat formatter = new DecimalFormat(PRINT_PRICE_FORMAT);
		double totalCreditDue = 0;
		String storeName = SnapSharedUtils.getPrinterStoreName(context);
		String storeNumber = SnapSharedUtils.getPrinterStoreNumber(context);
		String storeAddress = SnapSharedUtils.getPrinterStoreAddress(context);
		String storeTinNumber = SnapSharedUtils
				.getPrinterStoreTinNumber(context);
		String storeCity = SnapSharedUtils.getPrinterStoreCity(context);
		String footer1 = SnapSharedUtils.getPrinterFooter1(context);
		String footer2 = SnapSharedUtils.getPrinterFooter2(context);
		String footer3 = SnapSharedUtils.getPrinterFooter3(context);

		if (!storeName.equals(""))
			storeName = formatPrintLineText(storeName, Gravity.CENTER) + "\n";
		if (!storeAddress.equals(""))
			storeAddress = formatPrintLineText(storeAddress, Gravity.CENTER)
					+ "\n";
		if (!storeNumber.equals(""))
			storeNumber = formatPrintLineText(storeNumber, Gravity.CENTER)
					+ "\n";
		if (!storeCity.equals(""))
			storeCity = formatPrintLineText(storeCity, Gravity.CENTER) + "\n";
		if (!storeTinNumber.equals(""))
			storeTinNumber = formatPrintLineText("TIN No : " + storeTinNumber,
					Gravity.CENTER) + "\n";

		String printText = formatPrintLineText(" ", Gravity.CENTER)
				+ "\n"
				+ storeName
				+ storeAddress
				+ storeCity
				+ storeNumber
				+ storeTinNumber
				+ getBillDivider()
				+ "\n"
				+ formatPrintLineText(new SimpleDateFormat(
						BILL_PRINT_DATEFORMAT, Locale.getDefault())
						.format(date).split("-")) + "\n";
		if (showBillNumber && invoiceNumber == 0) {
			int lastInvoiceNumber = SnapSharedUtils
					.getLastInvoiceID(SnapCommonUtils.getSnapContext(context));
			lastInvoiceNumber++;
			printText += getBillDivider() + "\n";
			printText += formatPrintLineText("Bill No: " + lastInvoiceNumber,
					Gravity.LEFT) + "\n";
		} else if (showBillNumber && invoiceNumber > 0) {
			printText += getBillDivider() + "\n";
			printText += formatPrintLineText("Bill No: " + invoiceNumber,
					Gravity.LEFT) + "\n";
		}
		printText += getBillDivider() + "\n";
		if (customer != null) {
			printText += formatPrintLineText(customer.getCustomerPhoneNumber(),
					Gravity.CENTER) + "\n";
			if (customer.getCustomerName() != null && !customer.getCustomerName().isEmpty())
				printText += formatPrintLineText(customer.getCustomerName(),
						Gravity.CENTER) + "\n";
			if (customer.getCustomerAddress() != null && !customer.getCustomerAddress().isEmpty())
				printText += formatPrintLineText(customer.getCustomerAddress(),
						Gravity.CENTER) + "\n";
			totalCreditDue = customer.getAmountDue() + creditDue;
			totalCreditDue = (double) Math.round(totalCreditDue * 100) / 100;
			printText += getBillDivider() + "\n";

		}
		String[] arr = "Qty Price Total".split(" ");
		printText += formatPrintLineText(arr) + "\n" + getBillDivider() + "\n";
		int i = 1;
		for (BillItem billItem : billItemList) {
			String productName = billItem.getProductSkuName();
			if (productName.length() > 24) {
				productName = productName.substring(0, 23) + ".";
			}
			printText += formatPrintLineText(i + ". " + productName,
					Gravity.LEFT) + "\n";
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
			printText += formatPrintLineText(qty, disPrice,salesPrice+"",
					billItem.getProductSkuQuantity() * salesPrice,
					billItem.getBillItemUnitType())
					+ "\n";
			if (salesPrice - mrp < 0)
				printText += formatPrintLineText(
						"Savings: "
								+ formatter.format((mrp - salesPrice)
										* billItem.getProductSkuQuantity()),
						Gravity.RIGHT)
						+ "\n";
			printText += BILL_ITEM_DIVIDER + "\n";
		}
		printText += getBillDivider() + "\n";
		printText += formatPrintLineIntegerText("Items", billItemList.size())
				+ "\n";
		printText += formatPrintLineIntegerText("Quantity", totalQuantity)
				+ "\n";
		printText += getBillDivider() + "\n";
		if (showVat) {
			printText += formatPrintLineText("Sale Amount", totalCartValue
					- totalSavings - totalDiscount - totalVatAmount)
					+ "\n";
			printText += formatPrintLineText("VAT Amount", totalVatAmount)
					+ "\n";
		}
		printText += formatPrintLineText("Gross Amount", totalCartValue) + "\n";
		if (totalSavings > 0)
			printText += formatPrintLineText("Savings", totalSavings) + "\n";
		if (totalDiscount > 0)
			printText += formatPrintLineText("Discount", totalDiscount) + "\n";
		printText += formatPrintLineText("Net Amount",
				Math.round(totalCartValue - totalSavings - totalDiscount))
				+ "\n";
		double cashReceived = (totalCartValue - totalSavings - totalDiscount - creditDue);
		if (cashReceived > 0) {
			printText += formatPrintLineText("Cash Received", (cashReceived))
					+ "\n";
		} else {
			cashReceived = 0;
			printText += formatPrintLineText("Cash Received", cashReceived)
					+ "\n";
		}
		if (null != customer && creditDue > 0) {
			printText += formatPrintLineText("Current Credit Due", creditDue)
					+ "\n";
		}
		if (customer != null && totalCreditDue != 0) {
			Date ceridtDate = new Date();
			printText += formatPrintLineText("Total Credit Due ",Gravity.LEFT) + "\n";
			printText += formatPrintLineText("(on "+formatDateText(new SimpleDateFormat(
    				BILL_PRINT_DATEFORMAT, Locale.getDefault())
    				.format(ceridtDate).split("-"))+")", totalCreditDue)+"\n";
		}
		printText += getBillDivider() + "\n";
		if (totalSavings > 0) {
			printText += formatPrintLineText("Total Savings Today",
					Gravity.CENTER)
					+ "\n"
					+ formatPrintLineText(
							"Rs "
									+ formatter.format(totalSavings
											+ totalDiscount), Gravity.CENTER)
					+ "\n" + getBillDivider() + "\n";
		}
		if (!footer1.equals(""))
			printText += formatPrintLineText(footer1, Gravity.CENTER) + "\n";
		if (!footer2.equals(""))
			printText += formatPrintLineText(footer2, Gravity.CENTER) + "\n";
		if (!footer3.equals(""))
			printText += formatPrintLineText(footer3, Gravity.CENTER) + "\n";
		printText += formatPrintLineText(context.getString(R.string.snapbizz_footer), Gravity.CENTER)
				+ "\n";
		printText += getBillDivider();
		return printText;
	}

	public static String format4InchPrintBillText(List<BillItem> billItemList,
			double totalCartValue, float totalDiscount, float totalSavings,
			Customer customer, Context context, Date date, int totalQuantity,
			float totalVatAmount, boolean showVat, boolean showBillNumber,
			int invoiceNumber, double creditDue) {
		NumberFormat formatter = new DecimalFormat(PRINT_PRICE_FORMAT);
		double totalCreditDue = 0;
		String storeName = SnapSharedUtils.getPrinterStoreName(context);
		String storeNumber = SnapSharedUtils.getPrinterStoreNumber(context);
		String storeAddress = SnapSharedUtils.getPrinterStoreAddress(context);
		String storeTinNumber = SnapSharedUtils
				.getPrinterStoreTinNumber(context);
		String storeCity = SnapSharedUtils.getPrinterStoreCity(context);
		String footer1 = SnapSharedUtils.getPrinterFooter1(context);
		String footer2 = SnapSharedUtils.getPrinterFooter2(context);
		String footer3 = SnapSharedUtils.getPrinterFooter3(context);

		if (!storeName.equals(""))
			storeName = formatPrintLineText(storeName, Gravity.CENTER) + "\n";
		if (!storeAddress.equals(""))
			storeAddress = formatPrintLineText(storeAddress, Gravity.CENTER)
					+ "\n";
		if (!storeNumber.equals(""))
			storeNumber = formatPrintLineText(storeNumber, Gravity.CENTER)
					+ "\n";
		if (!storeCity.equals(""))
			storeCity = formatPrintLineText(storeCity, Gravity.CENTER) + "\n";
		if (!storeTinNumber.equals(""))
			storeTinNumber = formatPrintLineText("TIN No : " + storeTinNumber,
					Gravity.CENTER) + "\n";

		String printText = formatPrintLineText(" ", Gravity.CENTER)
				+ "\n"
				+ storeName
				+ storeAddress
				+ storeCity
				+ storeNumber
				+ storeTinNumber
				+ getBillDivider()
				+ "\n"
				+ formatPrintLineText(new SimpleDateFormat(
						BILL_PRINT_DATEFORMAT, Locale.getDefault())
						.format(date).split("-")) + "\n";
		if (showBillNumber && invoiceNumber == 0) {
			int lastInvoiceNumber = SnapSharedUtils
					.getLastInvoiceID(SnapCommonUtils.getSnapContext(context));
			lastInvoiceNumber++;
			printText += getBillDivider() + "\n";
			printText += formatPrintLineText("Bill No: " + lastInvoiceNumber,
					Gravity.LEFT) + "\n";
		} else if (showBillNumber && invoiceNumber > 0) {
			printText += getBillDivider() + "\n";
			printText += formatPrintLineText("Bill No: " + invoiceNumber,
					Gravity.LEFT) + "\n";
		}
		printText += getBillDivider() + "\n";
		if (customer != null) {
			printText += formatPrintLineText(customer.getCustomerPhoneNumber(),
					Gravity.CENTER) + "\n";
			if (customer.getCustomerName() != null && !customer.getCustomerName().isEmpty())
				printText += formatPrintLineText(customer.getCustomerName(),
						Gravity.CENTER) + "\n";
			if (customer.getCustomerAddress() != null && !customer.getCustomerAddress().isEmpty())
				printText += formatPrintLineText(customer.getCustomerAddress(),
						Gravity.CENTER) + "\n";
			totalCreditDue = customer.getAmountDue() + creditDue;
			totalCreditDue = (double) Math.round(totalCreditDue * 100) / 100;
			printText += getBillDivider() + "\n";
		}
		String[] arr = "Qty Price Discount Total".split(" ");
		printText += formatPrintLineText(arr) + "\n" + getBillDivider() + "\n";
		int i = 1;
		for (BillItem billItem : billItemList) {
			String productName = billItem.getProductSkuName();
			if (productName.length() > 24) {
				productName = productName.substring(0, 23) + ".";
			}
			printText += formatPrintLineText(i + ". " + productName,
					Gravity.LEFT) + "\n";
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
			printText += formatPrintLineText(qty,disPrice,salesPrice >= mrp ? 0 : (mrp - salesPrice)* billItem.getProductSkuQuantity(),billItem.getProductSkuQuantity() * salesPrice,billItem.getBillItemUnitType())+ "\n";
			printText += BILL_ITEM_DIVIDER + "\n";
		}
		printText += getBillDivider() + "\n";
		printText += formatPrintLineIntegerText("Items", billItemList.size())
				+ "\n";
		printText += formatPrintLineIntegerText("Quantity", totalQuantity)
				+ "\n";
		printText += getBillDivider() + "\n";
		if (showVat) {
			printText += formatPrintLineText("Sale Amount", totalCartValue
					- totalSavings - totalDiscount - totalVatAmount)
					+ "\n";
			printText += formatPrintLineText("VAT Amount", totalVatAmount)
					+ "\n";
		}
		printText += formatPrintLineText("Gross Amount", totalCartValue) + "\n";
		if (totalSavings > 0)
			printText += formatPrintLineText("Savings", totalSavings) + "\n";
		if (totalDiscount > 0)
			printText += formatPrintLineText("Discount", totalDiscount) + "\n";
		printText += formatPrintLineText("Net Amount",
				Math.round(totalCartValue - totalSavings - totalDiscount))
				+ "\n";
		double cashReceived = (totalCartValue - totalSavings - totalDiscount - creditDue);
		if (cashReceived > 0) {
			printText += formatPrintLineText("Paid By Cash", cashReceived) + "\n";
		} else {
			cashReceived = 0;
			printText += formatPrintLineText("Paid By Cash", cashReceived) + "\n";
		}
		if (null != customer && creditDue > 0) {
			printText += formatPrintLineText("Paid By Credit", creditDue) + "\n";
		}
		if (customer != null && totalCreditDue != 0) {
			Date ceridtDate = new Date();
			printText += formatPrintLineText("Total Credit Due ",Gravity.LEFT) + "\n";
			printText += formatPrintLineText("(on "+formatDateText(new SimpleDateFormat(
    				BILL_PRINT_DATEFORMAT, Locale.getDefault())
    				.format(ceridtDate).split("-"))+")", totalCreditDue)+"\n";

		}
		printText += getBillDivider() + "\n";
		if (totalSavings > 0) {
			printText += formatPrintLineText("Total Savings Today Rs "
					+ formatter.format(totalSavings + totalDiscount),
					Gravity.CENTER)
					+ "\n" + getBillDivider() + "\n";
		}

		if (!footer1.equals(""))
			printText += formatPrintLineText(footer1, Gravity.CENTER) + "\n";
		if (!footer2.equals(""))
			printText += formatPrintLineText(footer2, Gravity.CENTER) + "\n";
		if (!footer3.equals(""))
			printText += formatPrintLineText(footer3, Gravity.CENTER) + "\n";
		printText += formatPrintLineText(context.getString(R.string.snapbizz_footer), Gravity.CENTER)
				+ "\n";
		printText += getBillDivider();
		return printText;
	}

	private static String formatPrintLineText(float qty, double price,String rate,
			double total, SkuUnitType skuUnitType) {
		NumberFormat formatter = new DecimalFormat(PRINT_PRICE_FORMAT);
		String qtyString = qty + " " + skuUnitType.getUnitValue();
		String rateString =formatter.format(Double.valueOf(rate));
		String priceString = formatter.format(price);
		
		if (skuUnitType.ordinal() != SkuUnitType.PC.ordinal()) {
			if (skuUnitType.ordinal() == SkuUnitType.GM.ordinal()
					|| skuUnitType.ordinal() == SkuUnitType.KG.ordinal())
				priceString += "/" + SkuUnitType.KG.getUnitValue();
			else
				priceString += "/" + SkuUnitType.LTR.getUnitValue();
		}
		String totalString = formatter.format(total);
		int totalPadding = MAX_PRINTCHARS_PERLINE - qtyString.length()
				- priceString.length() - rateString.length()- totalString.length();
		String paddingText3 = "";
		String paddingText = "";
		String paddingText1 = "";
		String paddingText2 = "";
		
		for (int i = 7; i >= qtyString.length(); i--) {
			paddingText3 += " ";
		}
		for (int i = 8; i >= priceString.length(); i--) {
            	paddingText += " ";
        }
		for (int i = 5; i >= rateString.length(); i--) {
            	paddingText1 += " ";
        }
		for (int i = 6; i >= totalString.length(); i--) {
            	paddingText2 += " ";
        }
		String printText=qtyString+paddingText3;
		printText += paddingText+priceString;
		printText += " "+paddingText1+rateString;
		printText += " "+paddingText2+totalString;
		return printText ;
	}
	
	
	private static String formatCreditLineText(String creditgiven,
			String cashpaid) {
		NumberFormat formatter = new DecimalFormat(PRINT_PRICE_FORMAT);
		String qtyString = creditgiven ;
		String rateString =cashpaid;
		
		int totalPadding = MAX_PRINTCHARS_PERLINE - qtyString.length() - rateString.length();
		String paddingText3 = "";
		String paddingText = "";
		String paddingText1 = "";
		String paddingText2 = "";
		for (int i = 9; i >= qtyString.length(); i--) {
			paddingText3 += " ";
		}
		for (int i = 9; i >= rateString.length(); i--) {
            	paddingText1 += " ";
        }
		String printText="          "+paddingText3+qtyString;
		printText += paddingText1+rateString;
		return printText + " ";
	}
	

	private static String formatPrintLineText(float qty, double price,
			double discount, double total, SkuUnitType skuUnitType) {
		NumberFormat formatter = new DecimalFormat(PRINT_PRICE_FORMAT);
		String qtyString = qty + " " + skuUnitType.getUnitValue();
		String priceString = formatter.format(price);
		String discountString = formatter.format(discount);
		if (skuUnitType.ordinal() != SkuUnitType.PC.ordinal()) {
			if (skuUnitType.ordinal() == SkuUnitType.GM.ordinal()
					|| skuUnitType.ordinal() == SkuUnitType.KG.ordinal())
				priceString += "/" + SkuUnitType.KG.getUnitValue();
			else
				priceString += "/" + SkuUnitType.LTR.getUnitValue();
		}
		String totalString = formatter.format(total);
		return formatPrintLineText(new String[] { qtyString, priceString,
				discountString, totalString });
	}

	private static String formatPrintLineText(String printLineText, double printLineNumber) {
		if(Math.abs(printLineNumber) <= SnapToolkitConstants.MIN_PRICE)
			printLineNumber = 0;
		String printLineNumberText = new DecimalFormat(PRINT_PRICE_FORMAT).format(printLineNumber);
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
			Matcher printLineMatcher = Pattern.compile(
					"(.{1," + MAX_PRINTCHARS_PERLINE + "}(\\W|$))").matcher(
					printLineText);
			StringBuilder printLineBuilder = new StringBuilder();
			String printText;
			while (printLineMatcher.find()) {
				printText = printLineMatcher.group() + "\n";
				len = printText.length();
				int padding = MAX_PRINTCHARS_PERLINE - len;
				for (int i = 0; i < padding; i++) {
					if ((i < (padding / 2) + 2 && gravity == Gravity.CENTER)
							|| gravity == Gravity.RIGHT)
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

	private static String formatPrintLineText(String[] printTextArr) {
		int length = 0;
		for (String printText : printTextArr) {
			length += printText.length();
		}
		int paddingLength = (MAX_PRINTCHARS_PERLINE - length)
				/ (printTextArr.length - 1);
		String padding = "";
		for (int i = 0; i < paddingLength; i++) {
			padding += " ";
		}
		String text = printTextArr[0];
		for (int i = 1; i < printTextArr.length; i++) {
			text += padding + printTextArr[i];
		}
		return text;
	}
	
	private static String formatDateText(String [] printTextArr) {
        String text = printTextArr[0];
       
        return text;
    }

	private static String formatPrintLineTextHeader(String[] printTextArr) {
		int length = 0;
		for (String printText : printTextArr) {
			length += printText.length();
		}
		int paddingLength = (34 - length)
				/ (printTextArr.length - 1);
		String padding = "";
		for (int i = 0; i < paddingLength; i++) {
			padding += " ";
		}
		String text = printTextArr[0];
		for (int i = 1; i < printTextArr.length; i++) {
			text += padding + printTextArr[i];
		}
		return text;
	}
	// public static String hex2rbga(String hexColor){
	// int color = Color.parseColor(hexColor);
	// int r = Color.red(color);
	// int g = Color.green(color);
	// int b = Color.blue(color);
	// int a = Color.alpha(color);
	//
	// StringBuffer sb = new StringBuffer();
	// sb.append("rgba(");
	// sb.append(r);
	// sb.append(",");
	// sb.append(g);
	// sb.append(",");
	// sb.append(b);
	// sb.append(",");
	// sb.append(a);
	// sb.append(")");
	//
	// return sb.toString();
	// }
}
