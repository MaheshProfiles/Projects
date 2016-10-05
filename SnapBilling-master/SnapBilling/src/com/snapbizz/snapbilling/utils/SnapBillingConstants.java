package com.snapbizz.snapbilling.utils;

import com.snapbizz.snaptoolkit.utils.PrinterType;
import com.snapbizz.snaptoolkit.utils.SkuUnitType;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class SnapBillingConstants extends SnapToolkitConstants {
	
	public static final String BILL_DATEFORMAT = "dd/MM/yyyy";
	public static final String SELECT_PRINTER = "Select Printer";
	public static final int BILLING_LIST_LEFT_MARGIN=835;
    
	public static final int [] STORE_CATEGORY_ARRAY = new int[]{5, 55, 53, 54, 67, 68, 70, 4, 31, 32, 38, 75, 36, 44, 39};
	public static final String [] STORE_FORGOTTEN_PRODUCTS_ARRAY = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12","13","14","15","16","17","18","19","20"};
	public static final String [] WEIGHT_UNIT_TYPES = new String[]{SkuUnitType.KG.getUnitValue(), SkuUnitType.GM.getUnitValue()};
	public static final String [] VOLUME_UNIT_TYPES = new String[]{SkuUnitType.LTR.getUnitValue(), SkuUnitType.ML.getUnitValue()};
	public static final String [] PACKET_UNIT_TYPES = new String[]{SkuUnitType.PC.getUnitValue()};
	public static final String [] PRINTER_TYPES = new String[]{PrinterType.AEM_USB_PRINTER.getPrinterType(), PrinterType.TVS_USB_PRINTER.getPrinterType(), PrinterType.AEM_3INCH_USB_PRINTER.getPrinterType(), PrinterType.AEM_BLUETOOTH_PRINTER.getPrinterType(), PrinterType.AEM_3INCH_BLUETOOTH_PRINTER.getPrinterType(),PrinterType.NGX_3INCH_PRINTER.getPrinterType(),PrinterType.NGX_2INCH_PRINTER.getPrinterType()};
	
	public static final String INTENT_EXTRA_KEY_SHOW_DASHBOARD = "key_show_dashboard";
	public static final String INTENT_EXTRA_SHOW_DASHBOARD = "show_dashboard";
	//widget constants
	public static final String WIDGET_UPDATE_ACTION_HOTPRODS ="com.snapbizz.snapbilling.hotprods.intent.action.UPDATE_WIDGET";
	public static final String WIDGET_UPDATE_ACTION_CHARTS ="com.snapbizz.snapbilling.charts.intent.action.UPDATE_WIDGET";
	
	public static final String CHART_WIDGET_IMAGE ="chart_widget.png";
	public static final String HOTPROD_WIDGET_IMAGE ="hotprod_widget.png";
	public static final String ALERT_WIDGET_IMAGE ="alert_widget.png";
	public static final String SUMMARY_WIDGET_IMAGE ="summary_widget.png";
	public static final int KEY_STROKE_TIMEOUT = 700;
	public static final String CONFIRM_BUTTON_CLICKED = "confirm_button_clicked";
	
	public static final String STORE_IMAGE ="storeimage.png";
	public static final String STORE_LOCATION ="image";
	
	public static final int TIN_MAX_LENGTH = 11;
	public static final int CUSTOMER_ADDR_MAX_LENGTH = 100;
	public static final int DISCOUNT_MENU_ID = 5;
	
	public static final String GDB_SYNC_SERVER = "http://52.192.71.130/v2/api/";        
	public static final String APK_MIME_TYME = "application/vnd.android.package-archive";
	public static final String GOOGLE_MARKET_LINK = "market://details?id=";
	public static final String GOOGLE_PLAYSTORE_LINK = "https://play.google.com/store/apps/details?id=";
	public static final String CATEGORY_OTHERS ="OTHERS";
	public static final String CATEGORY_MISCELLANEOUS ="MISCELLANEOUS";
	
	public static final int APK_SYNC_INTERVAL = 60*60*24;
	public static final int APK_FILE_BUFFER = 8192;        
	public static final String REQUEST_HEADER_FIELD = "Range";

	public static final String APK_VERSION_DATEFORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	public static final String[] APK_PACKAGES = new String[] { SnapToolkitConstants.SNAP_PACKAGENAME,
                                                               SnapToolkitConstants.SNAP_INVENTORY_PACKAGENAME,
                                                               SnapToolkitConstants.SNAP_DASHBOARD_PACKAGENAME,
                                                               SnapToolkitConstants.SNAP_PUSHOFFERS_PACKAGENAME,
                                                               SnapToolkitConstants.SNAP_VISIBILITY_PACKAGENAME };
	public static final int DEFAULT_CONNECTION_TIMEOUT = 120;			// Two minutes
	public static final int DEFAULT_READ_TIMEOUT = 120;					// Two minutes
	public static final int API_OFFSET_LIMIT = 1000;
	public static final int VISIBILITY_TIMER_INTERVAL = 60*1000*30; // Thirty  minutes
	public static final int FIRST_COLUMN = 1;
   	public static final int SECOND_COLUMN = 2;
   	public static final int THIRD_COLUMN = 3;
   	public static final int FOURTH_COLUMN = 4;
   	public static final int FIFTH_COLUMN = 5;
   	public static final int SIXTH_COLUMN = 6;
   	public static final int SEVENTH_COLUMN = 7;
   	public static final int EIGHTH_COLUMN = 8;
}
