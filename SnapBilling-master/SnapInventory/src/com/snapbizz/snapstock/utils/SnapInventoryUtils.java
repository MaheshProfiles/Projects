package com.snapbizz.snapstock.utils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import com.snapbizz.snaptoolkit.domains.Company;
import com.snapbizz.snaptoolkit.domains.Distributor;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class SnapInventoryUtils extends SnapCommonUtils {
	
	public static String convertDateFormat(String inputDate, String inputDatePattern, String outputDatePattern){
		
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(inputDatePattern, Locale.US);
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
		return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + context.getPackageName()
				+ File.separator + SnapToolkitConstants.WIDGET_PATH + File.separator + SnapInventoryConstants.ALERT_WIDGET_IMAGE;
	}
	
	public static String getSummaryWidgetImagePath(Context context) {
		return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + context.getPackageName()
				+ File.separator + SnapToolkitConstants.WIDGET_PATH + File.separator + SnapInventoryConstants.SUMMARY_WIDGET_IMAGE;
	}
	
	public static int[] getCompanyGrid(List<Company> companyAdapterList, int maxRows) {
		int totalSize = 0;
		//Log.d("", "total adapterlist size " + companyAdapterList.size());
		for (int i = 0; i < companyAdapterList.size(); i++) {
			if (companyAdapterList.get(i).isHeader()) {
				if (i > 0 && totalSize % maxRows != 0)
					totalSize += (maxRows - (totalSize % maxRows)) + 1;
				else
					totalSize++;
			} else
				totalSize++;
		}
		//Log.d("", "total grid size " + totalSize);
		int[] totalGrid = new int[totalSize];
		int total = totalSize;
		totalSize = 0;
		for (int i = 0, j = 0; i < total && j < companyAdapterList.size(); i++, j++) {
			if (companyAdapterList.get(j).isHeader()) {
				while (i % maxRows != 0) {
					totalGrid[i] = -1;
					//Log.d(" grid ", totalGrid[i] + "");
					i++;
				}
			}
			totalSize++;
			totalGrid[i] = j;
			//Log.d(" grid ", totalGrid[i] + "");
		}
		return totalGrid;
	}
	
	public static int[] getDistributorGrid(List<Distributor> distributorAdapterList) {
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
	
	public static float roundOffFloatNumberDecimals(Float number){
		return Float.parseFloat(String.format("%.2f", number));
	}

	public static float roundOffDecimalPoints(float number){
		return Float.parseFloat(String.format("%.2f", number));
	}
	
	public static void storeIsInventoryHelpVideosStored(Context context, boolean value) {
    	context.getSharedPreferences(SnapToolkitConstants.DEVICE_FILE, Context.MODE_PRIVATE).edit().putBoolean(SnapToolkitConstants.STORE_INVENTORY_HELP_VIDEOS_KEY,value).commit();
    }
    
    public static boolean isInventoryHelpVideosStored(Context context) {
    	SharedPreferences mPrefs = context.getSharedPreferences(SnapToolkitConstants.DEVICE_FILE, Context.MODE_PRIVATE);
    	return mPrefs.getBoolean(SnapToolkitConstants.STORE_INVENTORY_HELP_VIDEOS_KEY, false);
    }
}
