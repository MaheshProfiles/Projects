package com.snapbizz.snaptoolkit.utils;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import android.content.Context;

import com.snapbizz.snaptoolkit.R;
import com.snapbizz.snaptoolkit.domains.Retailer;
import com.snapbizz.snaptoolkit.domains.VAT;

public class WriteToXls {

	public static boolean writeRegularReportToXls(String xlsName, String fileName, String[] vatCaption,
			HashMap<Float, String[]> salesValues, HashMap<Float, String[]> purchaseValue, List<VAT> vatList,
			float totalSalesVat, float outputPayable, float totalPurchaseVat, float inputPayable,
			Double turnover, Context context) {
		int sheetIndex = 0;
		WritableWorkbook writableWorkbook;
		try {
			writableWorkbook = Workbook.createWorkbook(new File(android.os.Environment.getExternalStorageDirectory(), fileName + xlsName + ".xls"));
			WritableSheet writableSheet = writableWorkbook.createSheet(fileName, sheetIndex);
			Label label;
			Number number;
			int countColumn = 0;
			int countRow = 1;
			Retailer retailer = SnapSharedUtils.getRetailer(SnapCommonUtils.getSnapContext(context));
			label = new Label(0, 0, "Vat Report");
			writableSheet.addCell(label);
			label = new Label(countColumn + 0, countRow + 0, "Store Name");
			writableSheet.addCell(label);
			label = new Label(countColumn + 1, countRow + 0, retailer.getStore().getStoreName());
			writableSheet.addCell(label);
			label = new Label(countColumn + 0, countRow + 1, "Mobile Number");
			writableSheet.addCell(label);
			label = new Label(countColumn + 1, countRow + 1, retailer.getRetailerPhoneNumber());
			writableSheet.addCell(label);
			label = new Label(countColumn + 0, countRow + 2, "Store Address");
			writableSheet.addCell(label);
			label = new Label(countColumn + 1, countRow + 2, retailer.getStore().getStoreAddress());
			writableSheet.addCell(label);
			label = new Label(countColumn + 0, countRow + 3, "TIN Number");
			writableSheet.addCell(label);
			label = new Label(countColumn + 1, countRow + 3, retailer.getStore().getStoreTinNumber());
			writableSheet.addCell(label);
			if (retailer.getRetailerEmailId().length() > 0) {
				label = new Label(countColumn + 0, countRow + 4, "Email ID");
				writableSheet.addCell(label);
				label = new Label(countColumn + 1, countRow + 4, retailer.getRetailerEmailId());
				writableSheet.addCell(label);
			}
			label = new Label(countColumn + 0, countRow + 5, "Period");
			writableSheet.addCell(label);
			label = new Label(countColumn + 1, countRow + 5, fileName);
			writableSheet.addCell(label);
			label = new Label (countColumn + 0, countRow + 6, "Turnover");
			writableSheet.addCell(label);
			number = new Number(countColumn + 1, countRow + 6, SnapCommonUtils.formatDecimalValue(totalSalesVat));
			writableSheet.addCell(number);
			for (int i = 0; i < vatCaption.length; i++) {
				label = new Label(countColumn + 0, countRow + i + 8, vatCaption[i]);
				writableSheet.addCell(label);
			}
			for (int i = 0; vatList != null && i < vatList.size(); i++) {
				label = new Label(countColumn + i + 2, countRow + 7, vatList.get(i).getVat() + "%");
				writableSheet.addCell(label);
				if (null != salesValues && salesValues.size() > 0 && null != salesValues.get(vatList.get(i).getVat())) {
					label = new Label(countColumn + i + 2, countRow + 8, SnapCommonUtils.roundOffDecimalPoints(Float.parseFloat(salesValues.get(vatList.get(i).getVat())[0])));
					writableSheet.addCell(label);
					label = new Label(countColumn + i + 2, countRow + 9, SnapCommonUtils.roundOffDecimalPoints(Float.parseFloat(salesValues.get(vatList.get(i).getVat())[1])));
					writableSheet.addCell(label);
					label = new Label(countColumn + i + 2, countRow + 12, SnapCommonUtils.roundOffDecimalPoints(Float.parseFloat(salesValues.get(vatList.get(i).getVat())[1])));
					writableSheet.addCell(label);
				}
				if (null != purchaseValue && purchaseValue.size() > 0 && null != purchaseValue.get(vatList.get(i).getVat())) {
					label = new Label(countColumn + i + 2, countRow + 10, SnapCommonUtils.roundOffDecimalPoints(Float.parseFloat(purchaseValue.get(vatList.get(i).getVat())[0])));
					writableSheet.addCell(label);
					label = new Label(countColumn + i + 2, countRow + 11, SnapCommonUtils.roundOffDecimalPoints(Float.parseFloat(purchaseValue.get(vatList.get(i).getVat())[1])));
					writableSheet.addCell(label);
                    float salesValue = 0.0f;
					if (salesValues != null && !salesValues.isEmpty() && salesValues.get(vatList.get(i).getVat()) != null)
                        salesValue = Float.parseFloat(salesValues.get(vatList.get(i).getVat())[1]);

					label = new Label(countColumn + i + 2, countRow + 12,
								      SnapCommonUtils.roundOffDecimalPoints(
                                                      salesValue - Float.parseFloat(purchaseValue.get(vatList.get(i).getVat())[1])));
					writableSheet.addCell(label);
				}
			}
			label = new Label(6, countRow + 7, "Total");
			writableSheet.addCell(label);
			number = new Number(6, countRow + 8, SnapCommonUtils.formatDecimalValue(totalSalesVat));
			writableSheet.addCell(number);
			number = new Number(6, countRow + 9, SnapCommonUtils.formatDecimalValue(outputPayable));
			writableSheet.addCell(number);
			number = new Number(6, countRow + 10, SnapCommonUtils.formatDecimalValue(totalPurchaseVat));
			writableSheet.addCell(number);
			number = new Number(6, countRow + 11, SnapCommonUtils.formatDecimalValue(inputPayable));
			writableSheet.addCell(number);
			number = new Number(6, countRow + 12, SnapCommonUtils.formatDecimalValue(outputPayable - inputPayable));
			writableSheet.addCell(number);
			writableWorkbook.write();
			writableWorkbook.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RowsExceededException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean writeCompositeReportToXls(String xlsName, String fileName, Double compositeValue, Double assessableValue, Context context) {
		int sheetIndex = 0;
		WritableWorkbook writableWorkbook;
		try {
			writableWorkbook = Workbook.createWorkbook(new File(android.os.Environment.getExternalStorageDirectory(), fileName + xlsName + ".xls"));
			WritableSheet writableSheet = writableWorkbook.createSheet(fileName, sheetIndex);
			Label label;
			Number number;
			int countColumn = 0;
			int countRow = 1;
			Retailer retailer = SnapSharedUtils.getRetailer(SnapCommonUtils.getSnapContext(context));
			label = new Label(0, 0, "Vat Report");
			writableSheet.addCell(label);
			label = new Label(countColumn + 0, countRow + 0, "Store Name");
			writableSheet.addCell(label);
			label = new Label(countColumn + 1, countRow + 0, retailer.getStore().getStoreName());
			writableSheet.addCell(label);
			label = new Label(countColumn + 0, countRow + 1, "Mobile Number");
			writableSheet.addCell(label);
			label = new Label(countColumn + 1, countRow + 1, retailer.getRetailerPhoneNumber());
			writableSheet.addCell(label);
			label = new Label(countColumn + 0, countRow + 2, "Store Address");
			writableSheet.addCell(label);
			label = new Label(countColumn + 1, countRow + 2, retailer.getStore().getStoreAddress());
			writableSheet.addCell(label);
			label = new Label(countColumn + 0, countRow + 3, "TIN Number");
			writableSheet.addCell(label);
			label = new Label(countColumn + 1, countRow + 3, retailer.getStore().getStoreTinNumber());
			writableSheet.addCell(label);
			if (retailer.getRetailerEmailId().length() > 0) {
				label = new Label(countColumn + 0, countRow + 4, "Email ID");
				writableSheet.addCell(label);
				label = new Label(countColumn + 1, countRow + 4, retailer.getRetailerEmailId());
				writableSheet.addCell(label);
			}
			label = new Label(countColumn + 0, countRow + 5, "Period");
			writableSheet.addCell(label);
			label = new Label(countColumn + 1, countRow + 5, fileName);
			writableSheet.addCell(label);
			label = new Label (countColumn + 0, countRow + 6, "Turnover");
			writableSheet.addCell(label);
			number = new Number(countColumn + 1, countRow + 6, assessableValue);
			writableSheet.addCell(number);
			label = new Label(countColumn + 0, countRow + 7, "Sales Assessable Value");
			writableSheet.addCell(label);
			number = new Number(countColumn + 1, countRow + 7, assessableValue);
			writableSheet.addCell(number);
			label = new Label(countColumn + 0, countRow + 8, "VAT Payable");
			writableSheet.addCell(label);
			number = new Number(countColumn + 1, countRow + 8, compositeValue);
			writableSheet.addCell(number);
			writableWorkbook.write();
			writableWorkbook.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RowsExceededException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public static boolean writeRegularDetailedReportToXls(String xlsName, String fileName, List<String[]> listOfStringArray, Double turnover, Context context) {
		int sheetIndex = 0;
		int countRow = 1;
		int countColumn = 0;
		WritableWorkbook writableWorkbook;
		try {
			writableWorkbook = Workbook.createWorkbook(new File(android.os.Environment.getExternalStorageDirectory(), fileName + xlsName + ".xls"));
			WritableSheet writableSheet = writableWorkbook.createSheet(fileName, sheetIndex);
			Label label;
			Number number;
			java.util.Date date;
			String dateString = "";
			String invoiceID = "";
			Retailer retailer = SnapSharedUtils.getRetailer(SnapCommonUtils.getSnapContext(context));
			label = new Label(0, 0, "Sales Vat Report");
			writableSheet.addCell(label);
			label = new Label(countColumn + 0, countRow + 0, "Store Name");
			writableSheet.addCell(label);
			label = new Label(countColumn + 1, countRow + 0, retailer.getStore().getStoreName());
			writableSheet.addCell(label);
			label = new Label(countColumn + 0, countRow + 1, "Mobile Number");
			writableSheet.addCell(label);
			label = new Label(countColumn + 1, countRow + 1, retailer.getRetailerPhoneNumber());
			writableSheet.addCell(label);
			label = new Label(countColumn + 0, countRow + 2, "Store Address");
			writableSheet.addCell(label);
			label = new Label(countColumn + 1, countRow + 2, retailer.getStore().getStoreAddress());
			writableSheet.addCell(label);
			label = new Label(countColumn + 0, countRow + 3, "TIN Number");
			writableSheet.addCell(label);
			label = new Label(countColumn + 1, countRow + 3, retailer.getStore().getStoreTinNumber());
			writableSheet.addCell(label);
			if (retailer.getRetailerEmailId().length() > 0) {
				label = new Label(countColumn + 0, countRow + 4, "Email ID");
				writableSheet.addCell(label);
				label = new Label(countColumn + 1, countRow + 4, retailer.getRetailerEmailId());
				writableSheet.addCell(label);
			}
			label = new Label(countColumn + 0, countRow + 5, "Period");
			writableSheet.addCell(label);
			label = new Label(countColumn + 1, countRow + 5, fileName);
			writableSheet.addCell(label);
			label = new Label (countColumn + 0, countRow + 6, "Turnover");
			writableSheet.addCell(label);
			label = new Label(countColumn + 0, countRow + 7, "Date");
			writableSheet.addCell(label);
			label = new Label(countColumn + 1, countRow + 7, "Invoice");
			writableSheet.addCell(label);
			label = new Label(countColumn + 2, countRow + 7, "Basic Value");
			writableSheet.addCell(label);
			label = new Label(countColumn + 3, countRow + 7, "VAT %");
			writableSheet.addCell(label);
			label = new Label(countColumn + 4, countRow + 7, "Output VAT");
			writableSheet.addCell(label);
			label = new Label(countColumn + 5, countRow + 7, "Total");
			writableSheet.addCell(label);
			double turnoverAmount = 0;
			for (int i = 0; i < listOfStringArray.size(); i++) {
				if (!dateString.equals(listOfStringArray.get(i)[0])) {
					dateString = listOfStringArray.get(i)[0];
					SimpleDateFormat dateFormat = new SimpleDateFormat(SnapToolkitConstants.DAY_DATEFORMAT, Locale.getDefault());
					date = dateFormat.parse(dateString);
					dateFormat = new SimpleDateFormat(SnapToolkitConstants.EXCEL_DATEFORMAT, Locale.getDefault());
					label = new Label(countColumn + 0, countRow + i + 8, dateFormat.format(date));
					writableSheet.addCell(label);
				}
				if (!invoiceID.equals(listOfStringArray.get(i)[1])) {
					invoiceID = listOfStringArray.get(i)[1];
					label = new Label(countColumn + 1, countRow + i + 8, invoiceID);
					writableSheet.addCell(label);
				}
				turnoverAmount += (Float.parseFloat(listOfStringArray.get(i)[2]) - Float.parseFloat(listOfStringArray.get(i)[4]));
				label = new Label(countColumn + 2, countRow + i + 8, SnapCommonUtils.roundOffDecimalPoints(Float.parseFloat(listOfStringArray.get(i)[2]) - Float.parseFloat(listOfStringArray.get(i)[4])));
				writableSheet.addCell(label);
				label = new Label(countColumn + 3, countRow + i + 8, listOfStringArray.get(i)[3]);
				writableSheet.addCell(label);
				label = new Label(countColumn + 4, countRow + i + 8, SnapCommonUtils.roundOffDecimalPoints(Float.parseFloat(listOfStringArray.get(i)[4])));
				writableSheet.addCell(label);
				label = new Label(countColumn + 5, countRow + i + 8, listOfStringArray.get(i)[2]);
				writableSheet.addCell(label);
			}
			number = new Number(countColumn + 1, countRow + 6, SnapCommonUtils.formatDecimalValue(turnoverAmount));
			writableSheet.addCell(number);
			writableWorkbook.write();
			writableWorkbook.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RowsExceededException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}
	public static boolean writeDetailedPurchaseReportToXls(String xlsName, String fileName, List<String[]> listOfStringArray, Context context) {
		int sheetIndex = 0;
		int countRow = 1;
		int countColumn = 0;
		WritableWorkbook writableWorkbook;
		try {
			writableWorkbook = Workbook.createWorkbook(new File(android.os.Environment.getExternalStorageDirectory(), fileName + xlsName + ".xls"));
			WritableSheet writableSheet = writableWorkbook.createSheet(fileName, sheetIndex);
			Label label;
			java.util.Date date;
			String dateString = "";
			String invoiceID = "";
			Retailer retailer = SnapSharedUtils.getRetailer(SnapCommonUtils.getSnapContext(context));
			label = new Label(0, 0, "Purchase Vat Report");
			writableSheet.addCell(label);
			String[] storeHeader = context.getResources().getStringArray(R.array.xl_store_information);
			String[] storeDetails = {retailer.getStore().getStoreName(), retailer.getRetailerPhoneNumber(),
										retailer.getStore().getStoreAddress(), retailer.getStore().getStoreTinNumber(),
										retailer.getRetailerEmailId(), fileName};
			String[] tableHeaders = context.getResources().getStringArray(R.array.xl_table_headers);
			for (int i = 0; i < storeHeader.length; i++) {
				for (int j = 0; j < 2; j++) {
					if (!storeDetails[i].toString().trim().isEmpty()) {
						label = new Label(countColumn + j, countRow + i,
								(j == 0 ? storeHeader[i].toString() : storeDetails[i].toString()));
						writableSheet.addCell(label);
					}
				}
			}
			for (int i = 0; i < tableHeaders.length; i++) {
				label = new Label(countColumn + i, countRow + 7, tableHeaders[i]);
				writableSheet.addCell(label);
			}
			for (int i = 0; i < listOfStringArray.size(); i++) {
				if (!dateString.equals(listOfStringArray.get(i)[0])) {
					label = new Label(countColumn + 0, countRow + i + 8, listOfStringArray.get(i)[0]);
					writableSheet.addCell(label);
				}
				if (!invoiceID.equals(listOfStringArray.get(i)[1])) {
					invoiceID = listOfStringArray.get(i)[1];
					label = new Label(countColumn + 1, countRow + i + 8, invoiceID);
					writableSheet.addCell(label);
				}
				if (listOfStringArray.get(i)[3] != null && listOfStringArray.get(i)[4] != null) {
					label = new Label(countColumn + 2, countRow + i + 8, SnapCommonUtils.roundOffDecimalPoints(Float.parseFloat(listOfStringArray.get(i)[2]) - Float.parseFloat(listOfStringArray.get(i)[4])));
					writableSheet.addCell(label);
					label = new Label(countColumn + 3, countRow + i + 8, listOfStringArray.get(i)[3]);
					writableSheet.addCell(label);
					label = new Label(countColumn + 4, countRow + i + 8, SnapCommonUtils.roundOffDecimalPoints(Float.parseFloat(listOfStringArray.get(i)[4])));
					writableSheet.addCell(label);
					label = new Label(countColumn + 5, countRow + i + 8, listOfStringArray.get(i)[2]);
					writableSheet.addCell(label);
				}
			}
			writableWorkbook.write();
			writableWorkbook.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RowsExceededException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
		return false;
	}
}
