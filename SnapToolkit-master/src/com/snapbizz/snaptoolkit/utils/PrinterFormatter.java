package com.snapbizz.snaptoolkit.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.support.v4.util.LongSparseArray;
import android.view.Gravity;

import com.snapbizz.plugin.PrinterPlugin;
import com.snapbizz.snaptoolkit.domainsV2.Models.BillItem;
import com.snapbizz.snaptoolkit.R;
import com.snapbizz.snaptoolkit.db.dao.Customers;
import com.snapbizz.snaptoolkit.domains.ShoppingCart;

public class PrinterFormatter {
	
	private Context mContext;
	private HeaderFormat header;
	private InvoiceDetails invoiceDetails;
	private ArrayList<PrinterBillItems> items;
	private Taxes taxes;
	private PaymentDetails paymentDetails;
	private Footer footer;
	private Locale locale = Locale.US;
	private int max_printchars_perline;
	private static final String BILL_PRINT_DATEFORMAT = "dd/MM/yy-HH:mm aa";
	private float maxMRP = 0;
	private float maxSalesPrice = 0;
	private float maxQty = 0;
	private float maxTotalAmt = 0;
	private final String maxTax = "A";
	private PrinterPlugin.FONT_TYPE defaultFont = PrinterPlugin.FONT_TYPE.FONT_NORMAL;
	private boolean bTwoLineFormat = false;
	private HashMap<String, String> vatRatesBlock = new HashMap<String, String>();

	// TODO: Make this as Invoice instead of shoppingCart
	// TODO: Provide similar helpers for CustomerTransactions/Credit history, etc
	public PrinterFormatter(ShoppingCart shoppingCart, Context context, int maxLength) {
		this.mContext = context;
		this.max_printchars_perline = maxLength;
		if(maxLength <= 38)
			bTwoLineFormat = true;
		addHeader(null, SnapSharedUtils.getPrinterStoreName(context), 
				SnapSharedUtils.getPrinterStoreAddress(context), 
				SnapSharedUtils.getPrinterStoreNumber(context),
				SnapSharedUtils.getPrinterStoreTinNumber(context));
		addFooter(SnapSharedUtils.getPrinterFooter1(context), 
				SnapSharedUtils.getPrinterFooter2(context), 
				SnapSharedUtils.getPrinterFooter3(context));
		addInvoiceDetails(false, shoppingCart.getInvoiceNumber(), Calendar.getInstance().getTime(),
							shoppingCart.getCustomer());
		float currentOutStanding = 0;
		// TODO: Check this Get due amount, from customer Details
		//if (shoppingCart.getCustomer() != null)
		//	currentOutStanding = shoppingCart.getCustomer().dueAmount;
		addPaymentDetails(shoppingCart.getTotalCartValue(), shoppingCart.getTotalDiscount(),
							shoppingCart.getTotalSavings(), shoppingCart.getCashPaid(), currentOutStanding);
		addTaxes(shoppingCart.getTotalVatAmount(), shoppingCart.getVatRates());
		addBillItems(shoppingCart.getCartItems());
	}

	private void addHeader(String logoUrl, String storeName,
			String storeAddress, String storeNumber, String tinNumber) {
		header = new HeaderFormat(logoUrl, storeName, storeAddress, storeNumber, tinNumber);
	}

	private void addInvoiceDetails(boolean isEstimate, int billNumber,
			Date date, Customers customer) {
		invoiceDetails = new InvoiceDetails(isEstimate, billNumber, date, customer);
	}

	private void addBillItems(LongSparseArray<BillItem> billItemList) {
		items = new ArrayList<PrinterBillItems>();
		for (int i = 0; i < billItemList.size(); i++) {
			BillItem billItem = billItemList.valueAt(i);
			float vatRate = billItem.product.vatRate;
			String vatDenoter = "";
            for (String key : vatRatesBlock.keySet()) {
				String[] vatValues = vatRatesBlock.get(key).split("=");
				if (vatRate == Float.parseFloat(vatValues[0]))
					vatDenoter = key;
			}
			PrinterBillItems billItemArray = new PrinterBillItems(billItem.name,
																  billItem.mrp/100,
																  billItem.sellingPrice/100, // TODO: Check for KG and L 
																  billItem.quantity,
																  billItem.sellingPrice * billItem.quantity,
																  vatDenoter);
			items.add(billItemArray);
		}
	}

	private void addTaxes(double totalVatAmount, HashMap<String, Integer> vatRates) {
		taxes = new Taxes(totalVatAmount, vatRates);
	}

	private void addPaymentDetails(double totalCartValue, float totalDiscount,
			float totalSavings, float cashPaid, float currentOutstanding) {
		double netAmt = totalCartValue - totalSavings - totalDiscount;
		float creditAmount = 0;
		if (cashPaid < netAmt)
			creditAmount = (float) (netAmt - cashPaid);
		
		float cashChange = 0;
		if (creditAmount <= 0)
			cashChange = (float) (cashPaid - netAmt - creditAmount);
		
		paymentDetails = new PaymentDetails(totalCartValue, totalDiscount, totalSavings, netAmt, 
						totalDiscount + totalSavings, cashPaid, "coupon", null, creditAmount, cashChange, currentOutstanding);
	}

	private void addFooter(String footerGreetings, String footerContactNum,
			String footerExchange) {
		footer = new Footer(footerGreetings, footerContactNum, footerExchange, "E&OE");
	}

	public List<PrintLine> convertLines() {
		try {
			List<PrintLine> printLines = new ArrayList<PrintLine>();
			List<PrintLinePart> headerLineParts = header.getHeaderLines();
			printLines = convertHeaderLines(headerLineParts, printLines);

			List<PrintLinePart> invoiceLineParts = invoiceDetails
					.getInvoiceDetailsLines();
			printLines = convertInvoiceDetails(invoiceLineParts, printLines);
			
			List<PrintLine> billItemsList = getBillItems(items);
			printLines.addAll(billItemsList);
			
			List<PrintLinePart> vatLineParts = taxes.getVatLines();
			printLines = convertVatDetails(vatLineParts, printLines);
			
			List<PrintLinePart> paymentLineParts = paymentDetails.getPaymentLines();
			printLines = convertPaymentDetails(paymentLineParts, printLines);
			
			List<PrintLinePart> footerLineParts = footer.getFooterLines();
			printLines = convertFooterDetails(footerLineParts, printLines);		

			return printLines;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private List<PrintLine> convertHeaderLines(List<PrintLinePart> lineParts, List<PrintLine> printLines) {
		for (PrintLinePart printLinePart : lineParts) {
			List<PrintLinePart> part = new ArrayList<PrintLinePart>();
			PrintLine line = new PrintLine();
			part.add(printLinePart);
			line.lineParts = part;
			printLines.add(line);
		}
		return printLines;
	}

	private List<PrintLine> convertInvoiceDetails(List<PrintLinePart> lineParts,
			List<PrintLine> printLines) {
		for (int i = 0; i < lineParts.size(); i++) {
			List<PrintLinePart> part = new ArrayList<PrintLinePart>();
			PrintLine line = new PrintLine();
			part.add(lineParts.get(i));
			line.lineParts = part;
			printLines.add(line);
		}
		return printLines;
	}
	
	private List<PrintLine> convertVatDetails(List<PrintLinePart> lineParts,
			List<PrintLine> printLines) {
		for (PrintLinePart printLinePart : lineParts) {
			List<PrintLinePart> part = new ArrayList<PrintLinePart>();
			PrintLine line = new PrintLine();
			part.add(printLinePart);
			line.lineParts = part;
			printLines.add(line);
		}
		return printLines;
	}
	
	private List<PrintLine> convertPaymentDetails(List<PrintLinePart> lineParts,
			List<PrintLine> printLines) {
		
		for (int i = 0; i < lineParts.size(); i++) {
			List<PrintLinePart> part = new ArrayList<PrintLinePart>();
			PrintLine line = new PrintLine();
			part.add(lineParts.get(i));
			line.lineParts = part;
			printLines.add(line);
		}
		return printLines;
	}
	
	private List<PrintLine> convertFooterDetails(List<PrintLinePart> lineParts,
			List<PrintLine> printLines) {
		
		for (PrintLinePart printLinePart : lineParts) {
			List<PrintLinePart> part = new ArrayList<PrintLinePart>();
			PrintLine line = new PrintLine();
			part.add(printLinePart);
			line.lineParts = part;
			printLines.add(line);
		}
		return printLines;
	}

	private List<PrintLine> getBillItems(ArrayList<PrinterBillItems> itemList) {
		
		for (PrinterBillItems billItem : itemList) {
			if (billItem.mrp > maxMRP)
				maxMRP = billItem.mrp;

			if (billItem.salesPrice > maxSalesPrice)
				maxSalesPrice = billItem.salesPrice;

			if (billItem.qty > maxQty)
				maxQty = billItem.qty;

			if (billItem.total > maxTotalAmt)
				maxTotalAmt = billItem.total;
		}

		int numberSpaces = getWidth(maxMRP) + getWidth(maxSalesPrice) + getWidth(maxQty)
				+ (maxTax.length() + 1) + getWidth(maxTotalAmt);
		int maxSpaceItem = max_printchars_perline - numberSpaces;

		PrintLine headerLine = new PrintLine();
		PrintLine divider = new PrintLine();
		List<PrintLinePart> headerParts = new ArrayList<PrintLinePart>();
		List<PrintLinePart> dividerParts = new ArrayList<PrintLinePart>();

		PrintLinePart dividerPart = new PrintLinePart();
		dividerPart.content = getBillDivider();
		
		if (!bTwoLineFormat) {
			headerParts.add(billItemPart(mContext.getString(R.string.item_name), maxSpaceItem, Gravity.START));
			showHeader(headerParts);
		} else {
			headerParts.add(billItemPart("", maxSpaceItem, Gravity.START));
			showHeader(headerParts);
		}
		headerLine.lineParts = headerParts;

		dividerParts.add(dividerPart);
		divider.lineParts = dividerParts;

		List<PrintLine> returnLines = new ArrayList<PrintLine>();
		returnLines.add(headerLine);
		returnLines.add(divider);

		int totalItemQty = 0;
		for (PrinterBillItems billItem : itemList) {
			List<PrintLinePart> billItemsParts = new ArrayList<PrintLinePart>();
			totalItemQty += billItem.qty;
			if (!bTwoLineFormat) {
				returnLines.add(defaultBillItemAddFormat(billItemsParts, billItem, maxSpaceItem));
			} else {
				//if printer is 2-inch, bill item will be printed in separate line.
				billItemsParts.add(billItemPart(billItem.productName, max_printchars_perline,
						Gravity.START));
				PrintLine billItemLine = new PrintLine();
				billItemLine.lineParts = billItemsParts;
				returnLines.add(billItemLine);
				
				//initialize new List<PrintLinePart> for QTY, MRP, SP, T, AMT line
				List<PrintLinePart> billItemsParts2 = new ArrayList<PrintLinePart>();
				returnLines.add(defaultBillItemAddFormat(billItemsParts2, billItem, maxSpaceItem));
			}
		}
		returnLines.add(divider);
		
		PrintLinePart part = new PrintLinePart();
		part.content = formatPrintLineText(mContext.getString(R.string.item_per_qty)
											+ itemList.size() + "/" + totalItemQty, Gravity.START);
		
		List<PrintLinePart> totalQty = new ArrayList<PrintLinePart>();
		totalQty.add(part);
		PrintLine totalQtyLine = new PrintLine();
		totalQtyLine.lineParts = totalQty;
		returnLines.add(totalQtyLine);
		returnLines.add(divider);
		
		return returnLines;
	}
	
	private PrintLine defaultBillItemAddFormat(List<PrintLinePart> billItemsParts,
												PrinterBillItems billItem, int maxSpaceItem) {
		billItemsParts.add(billItemPart(bTwoLineFormat ? "" : billItem.productName, maxSpaceItem, Gravity.START));
		billItemsParts.add(billItemPart(billItem.qty, getWidth(maxQty), Gravity.END));
		billItemsParts.add(billItemPart(billItem.mrp, getWidth(maxMRP), Gravity.END));
		billItemsParts.add(billItemPart(billItem.salesPrice, getWidth(maxSalesPrice), Gravity.END));
		billItemsParts.add(billItemPart(billItem.vat, (maxTax.length() + 1), Gravity.END));
		billItemsParts.add(billItemPart(billItem.total, getWidth(maxTotalAmt), Gravity.END));
		PrintLine billItemLine = new PrintLine();
		billItemLine.lineParts = billItemsParts;
		return billItemLine;
	}
	
	private void showHeader(List<PrintLinePart> headerParts) {
		headerParts.add(billItemPart(mContext.getString(R.string.qty), getWidth(maxQty), Gravity.END));
		headerParts.add(billItemPart(mContext.getString(R.string.mrp), getWidth(maxMRP), Gravity.END));
		headerParts.add(billItemPart(mContext.getString(R.string.sp), getWidth(maxSalesPrice), Gravity.END));
		headerParts.add(billItemPart(mContext.getString(R.string.tax), (maxTax.length() + 1), Gravity.END));
		headerParts.add(billItemPart(mContext.getString(R.string.amount), getWidth(maxTotalAmt), Gravity.END));
	}
	
	private int getWidth(float field) {
		String fieldString = String.valueOf(field);
		int fieldWidth = fieldString.length() + 1;
		return fieldWidth;
	}
	
	private PrintLinePart billItemPart(String field, int maxSpace, int gravity) {
		String product = formatBillItemField(field, maxSpace, gravity);
		PrintLinePart prodPart = new PrintLinePart();
		prodPart.content = product;
		return prodPart;
	}

	private PrintLinePart billItemPart(float field, int maxSpace, int gravity) {
		String product = formatBillItemField(field + "", maxSpace, gravity);
		PrintLinePart part = new PrintLinePart();
		part.content = product;
		return part;
	}

	private String formatBillItemField(String field, int length, int gravity) {
		if (field.length() < length) {
			length -= field.length();
			for (int i = 0; i < length; i++)
				if (gravity == Gravity.START)
					field += " ";
				else if (gravity == Gravity.END)
					field = " " + field;
			return field;
		} else {
			String printText;
			printText = field.substring(0, Math.min(length - 3, length));
			printText += "...";
			int len = 0;
			len = printText.length();
			int padding = length - len;
			for (int i = 0; i < padding; i++) {
				if ((i < (padding / 2) + 2 && gravity == Gravity.CENTER)
						|| gravity == Gravity.END)
					printText = " " + printText;
			}
			return printText;
		}
	}

	private String formatPrintLineText(String printLineText, int gravity) {
		int len = printLineText.length();
		if (len <= max_printchars_perline) {
			int padding = max_printchars_perline - len;
			for (int i = 0; i < padding; i++) {
				if ((i < padding / 2 && gravity == Gravity.CENTER)
						|| gravity == Gravity.END)
					printLineText = " " + printLineText;
			}
		} else {
			Matcher printLineMatcher = Pattern.compile(
					"(.{1," + max_printchars_perline + "}(\\W|$))").matcher(
					printLineText);
			StringBuilder printLineBuilder = new StringBuilder();
			String printText;
			while (printLineMatcher.find()) {
				printText = printLineMatcher.group() + "\n";
				len = printText.length();
				int padding = max_printchars_perline - len;
				for (int i = 0; i < padding; i++) {
					if ((i < (padding / 2) + 2 && gravity == Gravity.CENTER) || 
							gravity == Gravity.END)
						printText = " " + printText;
				}
				printLineBuilder.append(printText);
			}
			return printLineBuilder.toString();
		}
		return printLineText;
	}

	private String formatOneLine(String leftText, String rightText) {
		int len = max_printchars_perline - leftText.length()
				- rightText.length();
		for (int i = 0; i < len; i++)
			rightText = " " + rightText;
		return leftText + rightText;
	}
	
	private String formatOneLine(String leftText, double rightText) {
		String rightTextString = String.valueOf(rightText);
		int len = max_printchars_perline - leftText.length()
				- rightTextString.length();
		for (int i = 0; i < len; i++)
			rightTextString = " " + rightTextString;
		return leftText + rightTextString;
	}

	private void addLinesParts(List<PrintLinePart> linePartsList, String text,
							   PrinterPlugin.FONT_SIZE fontSize, PrinterPlugin.FONT_STYLE style,
							   PrinterPlugin.FONT_TYPE font) {
		PrintLinePart part = new PrintLinePart(null, new PrinterPlugin.FONT_STYLE[] { style }, font, fontSize);
		part.content = text;

		linePartsList.add(part);
	}

	private class HeaderFormat {
		public String logoUrl = null;
		public String storeName = null;
		public String storeAddress = null;
		public String storeNumber = null;
		public String tinNumber = null;

		private HeaderFormat(String logoUrl, String storeName,
				String storeAddress, String storeNumber, String tinNumber) {
			super();
			this.logoUrl = logoUrl;
			this.storeName = storeName;
			this.storeAddress = storeAddress;
			this.storeNumber = storeNumber;
			this.tinNumber = tinNumber;
		}

		private List<PrintLinePart> getHeaderLines() {
			// TODO: Override default font with settings for Header font
			List<PrintLinePart> headerLineParts = new ArrayList<PrintLinePart>();
			addLinesParts(headerLineParts,
					formatPrintLineText(storeName, Gravity.CENTER), PrinterPlugin.FONT_SIZE.FONT_MEDIUM,
					PrinterPlugin.FONT_STYLE.FONT_BOLD, defaultFont);
			addLinesParts(headerLineParts, formatPrintLineText(storeNumber, Gravity.CENTER), null,
					PrinterPlugin.FONT_STYLE.FONT_BOLD, defaultFont);
			addLinesParts(headerLineParts, formatPrintLineText(storeAddress, Gravity.CENTER), null,
					PrinterPlugin.FONT_STYLE.FONT_BOLD, defaultFont);
			addLinesParts(headerLineParts,
					formatPrintLineText(mContext.getString(R.string.tin)+ tinNumber,
					Gravity.CENTER), null, PrinterPlugin.FONT_STYLE.FONT_REGULAR, defaultFont);
			addLinesParts(headerLineParts, formatPrintLineText(getBillDivider(), Gravity.CENTER),
					null, PrinterPlugin.FONT_STYLE.FONT_REGULAR, defaultFont);
			return headerLineParts;
		}
	}

	private String getBillDivider() {
		String divider = "";
		for (int i = 0; i < max_printchars_perline; i++) {
			divider += "-";
		}
		return divider;
	}

	private class InvoiceDetails {
		public boolean isEstimate = false;
		public int billNumber = 0;
		public Date date = null;
		public Customers customer = null;
		
		private InvoiceDetails(boolean isEstimate, int billNumber, Date date, Customers customer) {
			this.isEstimate = isEstimate;
			this.billNumber = billNumber;
			this.date = date;
			this.customer = customer;
		}
		
		private List<PrintLinePart> getInvoiceDetailsLines() {
			List<PrintLinePart> invoiceLineParts = new ArrayList<PrintLinePart>();
			String title = null;
			if (isEstimate)
				title = formatPrintLineText(mContext.getString(R.string.estimate), Gravity.CENTER);
			else
				title = formatPrintLineText(mContext.getString(R.string.tax_invoce), Gravity.CENTER);

			// TODO: Override invoice defaultFont
			addLinesParts(invoiceLineParts, title, null, PrinterPlugin.FONT_STYLE.FONT_REGULAR, defaultFont);
			String billNo = formatPrintLineText(mContext.getString(R.string.bill_no) + billNumber, Gravity.START);
			addLinesParts(invoiceLineParts,
					formatPrintLineText(new SimpleDateFormat(BILL_PRINT_DATEFORMAT).format(date), Gravity.START),
					null, PrinterPlugin.FONT_STYLE.FONT_REGULAR, defaultFont);
			addLinesParts(invoiceLineParts, billNo, null, PrinterPlugin.FONT_STYLE.FONT_REGULAR, defaultFont);
			addLinesParts(invoiceLineParts, formatPrintLineText(getBillDivider(), Gravity.CENTER),
						null, PrinterPlugin.FONT_STYLE.FONT_REGULAR, defaultFont);
			if (customer != null) {
				addLinesParts(invoiceLineParts, formatPrintLineText(mContext.getString(R.string.customer_name) + 
						customer.getName(), Gravity.START), null, PrinterPlugin.FONT_STYLE.FONT_REGULAR, defaultFont);
				addLinesParts(invoiceLineParts, formatPrintLineText(mContext.getString(R.string.customer_no) +
						customer.getPhone(), Gravity.START), null, PrinterPlugin.FONT_STYLE.FONT_REGULAR, defaultFont);
				addLinesParts(invoiceLineParts, formatPrintLineText(mContext.getString(R.string.address) +
						customer.getAddress(), Gravity.START), null, PrinterPlugin.FONT_STYLE.FONT_REGULAR, defaultFont);
				addLinesParts(invoiceLineParts, formatPrintLineText(getBillDivider(),
						Gravity.CENTER), null, PrinterPlugin.FONT_STYLE.FONT_REGULAR, defaultFont);
			}
			return invoiceLineParts;
		}
	}

	private class PrinterBillItems {
		public int slNumber = 0;
		public String productName = null;
		public float mrp = 0;
		public float salesPrice = 0;
		public float qty = 0;
		public float total = 0;
		public String vat = null;
		
		private PrinterBillItems(String productName, float mrp, float salesPrice, float qty,
								 float total, String vatDenoter) {
			this.productName = productName;
			this.mrp = mrp;
			this.salesPrice = salesPrice;
			this.qty = qty;
			this.total = total;
			this.vat = vatDenoter;
		}
	}

	private class Taxes {
        double vatAmount = 0;
		
		private Taxes(double vatAmount, HashMap<String, Integer> vatRates) {
			this.vatAmount = vatAmount;
			
			String vatFormatKey = "A";
			int charValue = vatFormatKey.charAt(0);
			int i = 1;
			for (String key : vatRates.keySet()) {
				String vat = key + " = " + SnapCommonUtils.formatDecimalValue(vatRates.get(key));
				vatRatesBlock.put(vatFormatKey, vat);
				vatFormatKey = String.valueOf((char) (charValue + i));
				i++;
			}
		}

		private List<PrintLinePart> getVatLines() {
			List<PrintLinePart> vatLineParts = new ArrayList<PrintLinePart>();
			
			addLinesParts(vatLineParts, 
					formatOneLine(mContext.getString(R.string.vat_amount), SnapCommonUtils.formatDecimalValue(vatAmount/100)),
					null, PrinterPlugin.FONT_STYLE.FONT_REGULAR, defaultFont);
			
			for (String key : vatRatesBlock.keySet()) {
				String vat = key + " : " + vatRatesBlock.get(key);
				addLinesParts(vatLineParts, 
						formatPrintLineText(vat,
								Gravity.START), null, PrinterPlugin.FONT_STYLE.FONT_REGULAR, defaultFont);
			}
			
			addLinesParts(vatLineParts,
					formatPrintLineText(getBillDivider(), Gravity.CENTER),
					null, PrinterPlugin.FONT_STYLE.FONT_REGULAR, defaultFont);

			return vatLineParts;
		}
	}

	private class PaymentDetails {
		public double grossAmount = 0.0;
		public float savings = 0;
		public float discount = 0;
		public double netAmount = 0.0;
		public float totalSavings = 0;
		public float cashPaid = 0;
		public String coupons;
		public ArrayList<String> creditCard = null;
		public float creditAmount = 0;
		public float cashChange = 0;
		public float currentOutstanding = 0;
		
		private PaymentDetails(double grossAmount, float savings,
				float discount, double netAmount, float totalSavings,
				float cashPaid, String coupons, ArrayList<String> creditCard,
				float creditAmount, float cashChange, float currentOutstanding) {
			this.grossAmount = grossAmount;
			this.savings = savings;
			this.discount = discount;
			this.netAmount = netAmount;
			this.totalSavings = totalSavings;
			this.cashPaid = cashPaid;
			this.coupons = coupons;
			this.creditCard = creditCard;
			this.creditAmount = creditAmount;
			this.cashChange = cashChange;
			this.currentOutstanding = currentOutstanding;
		}

		private List<PrintLinePart> getPaymentLines() {
			List<PrintLinePart> paymentLineParts = new ArrayList<PrintLinePart>();
			if (grossAmount != netAmount)
				addLinesParts(paymentLineParts,
						formatPrintLineText(mContext.getString(R.string.gross_amount) + grossAmount,
						Gravity.END), null, PrinterPlugin.FONT_STYLE.FONT_REGULAR, defaultFont);
			
			addLinesParts(paymentLineParts, formatOneLine(mContext.getString(R.string.savings_label), savings), 
					null, PrinterPlugin.FONT_STYLE.FONT_REGULAR, defaultFont);
			addLinesParts(paymentLineParts, formatOneLine(mContext.getString(R.string.discount_label), discount),
					null, PrinterPlugin.FONT_STYLE.FONT_REGULAR, defaultFont);
			addLinesParts(paymentLineParts,
						formatOneLine(mContext.getString(R.string.net_amount_label), netAmount),
						PrinterPlugin.FONT_SIZE.FONT_MEDIUM, PrinterPlugin.FONT_STYLE.FONT_BOLD, defaultFont);			
			addLinesParts(paymentLineParts,
					formatPrintLineText(getBillDivider(), Gravity.CENTER),
					null, PrinterPlugin.FONT_STYLE.FONT_REGULAR, defaultFont);
			addLinesParts(paymentLineParts,
					formatOneLine(mContext.getString(R.string.total_savings), totalSavings),
					PrinterPlugin.FONT_SIZE.FONT_MEDIUM, PrinterPlugin.FONT_STYLE.FONT_BOLD, defaultFont);
			addLinesParts(paymentLineParts,
					formatPrintLineText(getBillDivider(), Gravity.CENTER),
					null, PrinterPlugin.FONT_STYLE.FONT_REGULAR, defaultFont);
			
			String cashPaidString = formatOneLine(mContext.getString(R.string.cash_paid), cashPaid);
			addLinesParts(paymentLineParts, cashPaidString, null, PrinterPlugin.FONT_STYLE.FONT_REGULAR, defaultFont);
			
			if(creditCard != null)
				addLinesParts(paymentLineParts,	formatOneLine(cashPaidString, mContext.getString(R.string.credit_card) +
						creditCard), null, PrinterPlugin.FONT_STYLE.FONT_REGULAR, defaultFont);
			
			addLinesParts(paymentLineParts,
					formatOneLine(mContext.getString(R.string.credit_amount), creditAmount),
					null, PrinterPlugin.FONT_STYLE.FONT_REGULAR, defaultFont);
			addLinesParts(paymentLineParts,
					formatOneLine(mContext.getString(R.string.cash_change), cashChange),
					null, PrinterPlugin.FONT_STYLE.FONT_REGULAR, defaultFont);
			addLinesParts(paymentLineParts,
					formatPrintLineText(getBillDivider(), Gravity.CENTER),
					null, PrinterPlugin.FONT_STYLE.FONT_REGULAR, defaultFont);
			if(currentOutstanding > 0) {
				addLinesParts(paymentLineParts,
						formatOneLine(mContext.getString(R.string.current_outstanding), currentOutstanding),
						null, PrinterPlugin.FONT_STYLE.FONT_REGULAR, defaultFont);
				addLinesParts(paymentLineParts,
						formatPrintLineText(getBillDivider(), Gravity.CENTER),
						null, PrinterPlugin.FONT_STYLE.FONT_REGULAR, defaultFont);
			}
			return paymentLineParts;
		}
	}

	private class Footer {
		public String footerGreetings;
		public String footerContactNum;
		public String footerExchange;
		public String endString = "E&OE";
		
		private Footer(String footerGreetings, String footerContactNum, String footerExchange, String endString) {
			this.footerGreetings = footerGreetings;
			this.footerContactNum = footerContactNum;
			this.footerExchange = footerExchange;
			this.endString = endString;
		}

		private List<PrintLinePart> getFooterLines() {
			List<PrintLinePart> footerLineParts = new ArrayList<PrintLinePart>();
			if (footerGreetings != null)
				addLinesParts(footerLineParts, formatPrintLineText(footerGreetings, 
					Gravity.CENTER), null, PrinterPlugin.FONT_STYLE.FONT_REGULAR, defaultFont);
			if (footerContactNum != null)
				addLinesParts(footerLineParts, formatPrintLineText(footerContactNum,
					Gravity.CENTER), null, PrinterPlugin.FONT_STYLE.FONT_REGULAR, defaultFont);
			if (footerExchange != null)
				addLinesParts(footerLineParts, formatPrintLineText(footerExchange,
					Gravity.CENTER), null, PrinterPlugin.FONT_STYLE.FONT_REGULAR, defaultFont);
			addLinesParts(footerLineParts, formatPrintLineText(endString, Gravity.CENTER),
					null, PrinterPlugin.FONT_STYLE.FONT_REGULAR, defaultFont);
			return footerLineParts;
		}
	}

	public class Settings {
		public int fontSize = 0;
		public String fontStyle = null;
		public int gravity = Gravity.CENTER;
	}

	public class PrintLinePart {
		// Font settings
		public PrinterPlugin.PRINT_ALIGNMENT alignment = null;
		public PrinterPlugin.FONT_STYLE[] styles = null; // Regular
		public PrinterPlugin.FONT_TYPE font = null;
		public PrinterPlugin.FONT_SIZE fontSize = null;

		public String content = null;
		public boolean isImage = false;
		public boolean isUnicode = false;
		
		public PrintLinePart() { }
		
		public PrintLinePart(PrinterPlugin.PRINT_ALIGNMENT alignment, PrinterPlugin.FONT_STYLE[] styles,
								  PrinterPlugin.FONT_TYPE font, PrinterPlugin.FONT_SIZE fontSize) {
			this.alignment = alignment;
			this.styles = styles;
			this.font = font;
			this.fontSize = fontSize;
		}
	}

	public class PrintLine {
		public List<PrintLinePart> lineParts;
	}
}
