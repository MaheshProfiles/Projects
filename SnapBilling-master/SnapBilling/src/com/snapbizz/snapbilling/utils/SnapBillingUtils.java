package com.snapbizz.snapbilling.utils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import junit.framework.Assert;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.adapters.ProductGroupIdAdapter;
import com.snapbizz.snapbilling.domains.SmartStore;
import com.snapbizz.snaptoolkit.domains.Campaigns;
import com.snapbizz.snaptoolkit.domains.Company;
import com.snapbizz.snaptoolkit.domains.Distributor;
import com.snapbizz.snaptoolkit.domains.InventorySku;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.gdb.dao.Products;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
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
				+ SnapBillingConstants.CHART_WIDGET_IMAGE;
	}

	public static String getHotProdWidgetImagepath(Context context) {
		return Environment.getExternalStorageDirectory().getAbsolutePath()
				+ File.separator + context.getPackageName() + File.separator
				+ SnapToolkitConstants.WIDGET_PATH + File.separator
				+ SnapBillingConstants.HOTPROD_WIDGET_IMAGE;
	}

	public static SnapBizzDatabaseHelper getHelper(Context context) {
		if (databaseHelper == null || !databaseHelper.isOpen()) {
            databaseHelper = SnapCommonUtils.getWritableDatabaseHelper(context);
		}
		return databaseHelper;
	}

	public static boolean isNewSku(String productName){
		try  
		  {  
		    double d = Double.parseDouble(productName);  
		  }  
		  catch(NumberFormatException nfe)  
		  {  
			  
		    return false;  
		  }  
		  return true;  
		
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
				+ SnapBillingConstants.ALERT_WIDGET_IMAGE;
	}

	public static String getSummaryWidgetImagePath(Context context) {
		return Environment.getExternalStorageDirectory().getAbsolutePath()
				+ File.separator + context.getPackageName() + File.separator
				+ SnapToolkitConstants.WIDGET_PATH + File.separator
				+ SnapBillingConstants.SUMMARY_WIDGET_IMAGE;
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
		productSku.setImageUrl(campaigns.getImage_uid());
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
	
	public static InputFilter[] setTextLength(int maxLength) {
		InputFilter[] fArray = new InputFilter[1];
		fArray[0] = new InputFilter.LengthFilter(maxLength);
		return fArray;
	}
	
	public static List<SmartStore> getSmartStoreList(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(SnapToolkitConstants.SMARTSTORE_FILE, Context.MODE_PRIVATE);
		Gson gson = new Gson();
		return gson.fromJson(mPrefs.getString(SnapToolkitConstants.SMART_STORE_KEY,""), new TypeToken<List<SmartStore>>() {}.getType());
	}

	public static void storeSmartStoreList(List<SmartStore> smartStoreList, Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences(SnapToolkitConstants.SMARTSTORE_FILE,Context.MODE_PRIVATE);
		Gson gson = new Gson();
		mPrefs.edit().putString(SnapToolkitConstants.SMART_STORE_KEY, gson.toJson(smartStoreList)).apply();
	}
	
	public static int getDrawable(String name, Context context) {
		Assert.assertNotNull(context);
		Assert.assertNotNull(name);
		return context.getResources().getIdentifier(name, "drawable", context.getPackageName());
	}
	
	public static void showGroupIdDetails(Context context, List<Products> groupDetails) {
		final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		dialog.setContentView(R.layout.groupid_details_dialog);
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);
		ListView dialog_ListView = (ListView) dialog.findViewById(R.id.dialoglist);
		TextView groupIdText = (TextView) dialog.findViewById(R.id.group_id_text);
		groupIdText.setText(String.valueOf(groupDetails.get(0).getProductGid()));
		dialog_ListView.addHeaderView(((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
																							.inflate(R.layout.header_groupid_details, null));
		ProductGroupIdAdapter adapter = new ProductGroupIdAdapter(context, groupDetails);
		dialog_ListView.setAdapter(adapter);
		dialog.findViewById(R.id.imageView_close).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	
	public static int getVisibleStatus(boolean isVisivle, int column, Context context) {
		if (isVisivle)
			return View.VISIBLE;
		if (getColumnCount(context) != column )
			return View.INVISIBLE;
		return View.GONE;
	}
	
	public static int getColumnCount(Context context) {
		return getValue(SnapSharedUtils.isMRPHeader(context)) + getValue(SnapSharedUtils.isPurchasePriceHeader(context)) + 
				getValue(SnapSharedUtils.isQtyHeader(context)) + getValue(SnapSharedUtils.isVatRateHeader(context)) +
				getValue(SnapSharedUtils.isUnitTypeHeader(context)) + getValue(SnapSharedUtils.isCategoryHeader(context)) +
				getValue(SnapSharedUtils.isSubCategoryHeader(context)) + getValue(SnapSharedUtils.isActionsHeader(context));
	}
	
	private static int getValue(boolean value) {
		if (value) 
			return 1;
		return 0;
	}
}
