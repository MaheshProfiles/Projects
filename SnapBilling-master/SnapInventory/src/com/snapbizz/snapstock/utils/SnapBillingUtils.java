package com.snapbizz.snapstock.utils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.snapbizz.snaptoolkit.domains.Campaigns;
import com.snapbizz.snaptoolkit.domains.Company;
import com.snapbizz.snaptoolkit.domains.Distributor;
import com.snapbizz.snaptoolkit.domains.InventorySku;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class SnapBillingUtils extends SnapCommonUtils {

	private static SnapBizzDatabaseHelper databaseHelper;

	public static int getShoppingCartId(String shoppingCartCode) {
		return Integer.parseInt(shoppingCartCode.charAt(shoppingCartCode
				.length() - 1) + "") - 1;
	}

	public static String getChartWidgetImagepath(Context context) {
		return Environment.getExternalStorageDirectory().getAbsolutePath()
				+ File.separator + context.getPackageName() + File.separator
				+ SnapToolkitConstants.WIDGET_PATH + File.separator
				+ SnapInventoryConstants.CHART_WIDGET_IMAGE;
	}

	public static String getHotProdWidgetImagepath(Context context) {
		return Environment.getExternalStorageDirectory().getAbsolutePath()
				+ File.separator + context.getPackageName() + File.separator
				+ SnapToolkitConstants.WIDGET_PATH + File.separator
				+ SnapInventoryConstants.HOTPROD_WIDGET_IMAGE;
	}

	public static SnapBizzDatabaseHelper getHelper(Context context) {
		if (databaseHelper == null) {
			databaseHelper = OpenHelperManager.getHelper(context,
					SnapBizzDatabaseHelper.class);
		}
		return databaseHelper;
	}

	public static void closeDatabaseHelper() {
		if (databaseHelper != null) {
			OpenHelperManager.releaseHelper();
			databaseHelper = null;
		}
	}

	public static String[] getVatReportDates(int range, String year) {

		String startDate = null;
		String endDate = null;
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, Integer.parseInt(year));
		switch (range) {
		case 12:
			startDate = getStartDate(calendar, 0);
			endDate = getEndDate(calendar, 2);
			break;

		case 13:
			startDate = getStartDate(calendar, 3);
			endDate = getEndDate(calendar, 5);
			break;

		case 14:
			startDate = getStartDate(calendar, 6);
			endDate = getEndDate(calendar, 8);
			break;

		case 15:
			startDate = getStartDate(calendar, 9);
			endDate = getEndDate(calendar, 11);
			break;

		default:
			startDate = getStartDate(calendar, range);
			endDate = getEndDate(calendar, range);
			break;
		}
		String[] stringArray = { startDate, endDate };
		return stringArray;
	}

	private static String getStartDate(Calendar cal, int month) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				SnapToolkitConstants.STANDARD_DATEFORMAT, Locale.getDefault());
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.MINUTE, 00);
		cal.set(Calendar.HOUR_OF_DAY, 00);
		cal.set(Calendar.SECOND, 00);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return dateFormat.format(cal.getTime());
	}

	private static String getEndDate(Calendar cal, int month) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				SnapToolkitConstants.STANDARD_DATEFORMAT, Locale.getDefault());
		cal.set(Calendar.MONTH, month);
		cal.add(Calendar.MONTH, 1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.DATE, -1);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.SECOND, 59);
		return dateFormat.format(cal.getTime());
	}

	public static String convertDateFormat(String inputDate,
			String inputDatePattern, String outputDatePattern) {

		try {
			SimpleDateFormat formatter = new SimpleDateFormat(inputDatePattern,
					Locale.US);
			Date date = formatter.parse(inputDate);
			formatter = new SimpleDateFormat(outputDatePattern, Locale.US);
			return formatter.format(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static String getAlertWidgetImagePath(Context context) {
		return Environment.getExternalStorageDirectory().getAbsolutePath()
				+ File.separator + context.getPackageName() + File.separator
				+ SnapToolkitConstants.WIDGET_PATH + File.separator
				+ SnapInventoryConstants.ALERT_WIDGET_IMAGE;
	}

	public static String getSummaryWidgetImagePath(Context context) {
		return Environment.getExternalStorageDirectory().getAbsolutePath()
				+ File.separator + context.getPackageName() + File.separator
				+ SnapToolkitConstants.WIDGET_PATH + File.separator
				+ SnapInventoryConstants.SUMMARY_WIDGET_IMAGE;
	}

	public static int[] getCompanyGrid(List<Company> companyAdapterList,
			int maxRows) {
		int totalSize = 0;
		// Log.d("", "total adapterlist size " + companyAdapterList.size());
		for (int i = 0; i < companyAdapterList.size(); i++) {
			if (companyAdapterList.get(i).isHeader()) {
				if (i > 0 && totalSize % maxRows != 0)
					totalSize += (maxRows - (totalSize % maxRows)) + 1;
				else
					totalSize++;
			} else
				totalSize++;
		}
		// Log.d("", "total grid size " + totalSize);
		int[] totalGrid = new int[totalSize];
		int total = totalSize;
		totalSize = 0;
		for (int i = 0, j = 0; i < total && j < companyAdapterList.size(); i++, j++) {
			if (companyAdapterList.get(j).isHeader()) {
				while (i % maxRows != 0) {
					totalGrid[i] = -1;
					// Log.d(" grid ", totalGrid[i] + "");
					i++;
				}
			}
			totalSize++;
			totalGrid[i] = j;
			// Log.d(" grid ", totalGrid[i] + "");
		}
		return totalGrid;
	}

	public static int[] getDistributorGrid(
			List<Distributor> distributorAdapterList) {
		int totalSize = 0;
		final int DISTRIUTOR_ROW_SIZE = 6;
		Log.d("", "total adapterlist size " + distributorAdapterList.size());
		for (int i = 0; i < distributorAdapterList.size(); i++) {
			if (distributorAdapterList.get(i).getIsHeader()) {
				if (i > 0 && totalSize % DISTRIUTOR_ROW_SIZE != 0)
					totalSize += (DISTRIUTOR_ROW_SIZE - (totalSize % DISTRIUTOR_ROW_SIZE)) + 1;
				else
					totalSize++;
			} else
				totalSize++;
		}
		Log.d("", "total grid size " + totalSize);
		int[] totalGrid = new int[totalSize];
		int total = totalSize;
		totalSize = 0;
		for (int i = 0, j = 0; i < total && j < distributorAdapterList.size(); i++, j++) {
			if (distributorAdapterList.get(j).getIsHeader()) {
				while (i % DISTRIUTOR_ROW_SIZE != 0) {
					totalGrid[i] = -1;
					Log.d(" grid ", totalGrid[i] + "");
					i++;
				}
			}
			totalSize++;
			totalGrid[i] = j;
			Log.d(" grid ", totalGrid[i] + "");
		}
		return totalGrid;
	}

	public static float roundOffFloatNumberDecimals(Float number) {
		return Float.parseFloat(String.format("%.2f", number));
	}

	public static float roundOffDecimalPoints(float number) {
		return Float.parseFloat(String.format("%.2f", number));
	}

	public static InventorySku createVisibilityObject(Campaigns campaigns) {
		InventorySku inventorySku = new InventorySku();
		ProductSku productSku = new ProductSku();
		productSku.setProductSkuCode(campaigns.getCode());
		productSku.setProductSkuName(campaigns.getName());
		productSku.setImageUrl(campaigns.getCode());
		inventorySku.setProductSku(productSku);
		return inventorySku;

	}

	public static List<InventorySku> removeNoImageItems(
			List<InventorySku> inventorySkuList, Context context) {
		for (Iterator<InventorySku> iter = inventorySkuList.listIterator(); iter
				.hasNext();) {
			InventorySku inventorySku = iter.next();
			;
			if (checkProductLargeDrawable(inventorySku.getProductSku()
					.getImageUrl(), context) == null
					|| checkProductLargeDrawable(
							inventorySku.getProductSku().getImageUrl(), context)
							.equals(null)) {
				iter.remove();
			}
		}

		return inventorySkuList;
	}
}
