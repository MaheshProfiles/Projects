package com.snapbizz.snaptoolkit.utils;

import java.io.File;

import android.os.Environment;

public class SnapToolkitConstants {

	public static final String DB_NAME = "SnapBizz.db";
	public static final String SYNC_DB_NAME = "SnapBizzSync.db";
	public static final String GLOBAL_DB_NAME = "global.db";
	public static final String DB_NAME_V2 = "snapbizzv2.db";
	
	public static int SNAP_VERSION = 1;
	public static final int BILLING_VERSION = 33;
	public static final int DB_VERSION = 14;
	
	public static final int CHUNK_SIZE=1; // number of days
	
	public static final int OFFER_LIMIT=48;
	
	// Production or Test
	public static final boolean PRODUCTION_BUILD = false;

	public static final String BASE_URL = PRODUCTION_BUILD ? "http://backend.snapbizz.com:100/v1" :
															 "http://backend-test.snapbizz.com/v1";
	public static final String BASE_SECURE_URL = PRODUCTION_BUILD ? "https://backend.snapbizz.com:100/v1":
																	"https://backend-test.snapbizz.com/v1";
	public static final String BASE_CAMPAIGN_URL = PRODUCTION_BUILD ? "http://snapbizzbackend.blob.core.windows.net/snapbizz-campaign/":
																	  "http://snapbizzbackend.blob.core.windows.net/snapbizz-campaign-development/";
	public static final String TOOLKIT_CRITTERCISM_ID = PRODUCTION_BUILD ? "61619ce6729d499fb06a45be1bbfb2ff00444503" :
																		   "0d34399791554aa9954534d2d6acf11500444503";
	public static final String BILLING_CRITTERCISM_ID = PRODUCTION_BUILD ? "0dbf9a3a87e1496da41c5f58fc89076c00444503" :
																		   "01db5e750e24442c94ddabaca5a5795400444503";
	public static final String INVENTORY_CRITTERCISM_ID = PRODUCTION_BUILD ? "0028bc6329e64363bbf188f6487dd13500444503" :
																			 "dc002b0dab2a47dfaddcc77a95b1427f00444503";
	public static final String BASE_URL_V2 = PRODUCTION_BUILD ? "http://api.snapbizz.com/v2/api/" :
																"http://api-test.snapbizz.com/v2/api/";
	public static final String BASE_URL_STORE_OTP_APK = PRODUCTION_BUILD ? "http://api.snapbizz.com/v2/api/" :
																		   "http://api-test.snapbizz.com/v2/api/";
	
	public static final String SYNC_PATH = "/sync";
	public static final String UPDATE_PATH = "/app_updates";
	public static final String STOCK_DASHBOARD_PATH = "/reports";
	public static final String AUTHENTICATION_PATH = "/authentications";
	public static final String DISTIBUTORS_PATH = "/distributors";
    public static final String BRANDS_PATH = "/brands";
    public static final String PRODUCTS_PATH = "/products";
    public static final String INVENTORY_PATH = "/inventory";
    public static final String COMPANY_PATH = "/company";
    public static final String PO_PATH = "/purchase_orders";
    public static final String DEVICE_PATH = "/devices";
    public static final String CUSTOMER_SUGGESTIONS_PATH = "/customer_suggestions";
    public static final String VIDEOS_PATH = "/videos";
	public static final String STORES_PATH = "/stores";


    public static final String DB_EXTPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+ "Android" + File.separator + "data" + File.separator + "Snapbizz" + File.separator;
    public static final String EXTERNAL_LOG_FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+ "Android" + File.separator + "data" + File.separator + "Snapbizz" + File.separator;
    
    public static final String DB_RESTORE_EXTPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+ "Android" + File.separator + "data" + File.separator + "Snapbizz" + File.separator+ "DB_Restore" + File.separator; 
    public static final String UPGRADE_EXTPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+ "Android" + File.separator + "data" + File.separator + "Snapbizz" + File.separator+ "Upgrade" + File.separator;
    
    public static final String REGISTRATION_METHOD = "/retailers";
    public static final String PRODUCT_SYNC_METHOD = "/store_product_sku_list";
    public static final String BRAND_SYNC_METHOD = "/store_brand_list";
    public static final String COMPANY_SYNC_METHOD = "/store_company_list";
    public static final String TRANSACTION_SYNC_METHOD = "/store_customer_transaction_list";
    public static final String BILLITEM_SYNC_METHOD = "/store_customer_transaction_items_list";
    public static final String CUSTOMER_SYNC_METHOD = "/store_customer_list";
    public static final String INVENTORY_SYNC_METHOD = "/store_inventory_list";
    public static final String DISTRIBUTOR_SYNC_METHOD = "/store_distributor_list";
    public static final String PO_SYNC_METHOD = "/store_purchase_order_list";
    public static final String PODETAILS_SYNC_METHOD = "/store_purchase_order_items_list";
    public static final String CUSTOMERPAYMENTS_SYNC_METHOD = "/store_customer_payment_list";
    public static final String INVENTORY_BATCH_SYNC_METHOD = "/store_inventory_batches";
    public static final String DISTRIBUTOR_BRANDMAP_SYNC_METHOD = "/store_distributor_brand_map";
    public static final String RECEIVE_ITEMS_SYNC_METHOD = "/store_received_items_list";
    public static final String INVOICE_LIST_SYNC_METHOD = "/store_invoice_list";
    public static final String PAYMENTS_SYNC_METHOD="/store_payment_list";
    public static final String SETTINGS_SYNC_METHOD="/store_settings_list";
    public static final String CATEGORY_SYNC_METHOD="/store_product_category_list";
    public static final String APPVERSION_SYNC_METHOD="/store_app_version_list";
    public static final String REMIND_CREDIT_CUSTOMER = "http://snapservertest.cloudapp.net/v1/remind_sms";
    
    public static final String APP_UPDATE_METHOD="/latest_apk_version";
    public static final String STOCK_REPORT_METHOD="/stock_report";
    public static final String SNAP_MONEY_METHOD="/stock_money";
    public static final String AUTHENTICATION_METHOD="/get_access_token";
    public static final String PUSHOFFER_METHOD="/push_offers";
    public static final String RETRIEVE_BRAND_METHOD="/retrieve_brand_list";
    public static final String RETRIEVE_COMPANY_METHOD="/retrieve_company_list";
    public static final String RETRIEVE_PRODUCTSKU_METHOD="/retrieve_product_sku_list";
    public static final String RETRIEVE_TRANSACTION_METHOD="/retrieve_customer_transaction_list";
    public static final String RETRIEVE_TRANSACTIONDETAILS_METHOD="/retrieve_customer_transaction_items_list";
    public static final String RETRIEVE_CUSTOMER_METHOD ="/retrieve_customer_list";
    public static final String RETRIEVE_INVENTORY_METHOD = "/retrieve_inventory_list";
    public static final String RETRIEVE_DISTRIBUTOR_METHOD = "/retrieve_distributor_list";
    public static final String RETRIEVE_DISTRIBUTORBRANDMAP_METHOD = "/retrieve_distributor_brand_map";
    public static final String RETRIEVE_ORDER_METHOD = "/retrieve_purchase_order_list";
    public static final String RETRIEVE_ORDERDETAILS_METHOD = "/retrieve_purchase_order_items_list";
    public static final String RETRIEVE_INVENTORYBATCH_METHOD = "/retrieve_inventory_batches";
    public static final String RETRIEVE_PAYMENTS_METHOD = "/retrieve_payment_list";
    public static final String RETRIEVE_SETTINGS_METHOD = "/retrieve_settings_list";
    public static final String RETRIEVE_CATEGORY_METHOD = "/retrieve_product_category_list";
    public static final String RETRIEVE_CUSTOMERPAYMENTS_METHOD = "/retrieve_customer_payment_list";
    public static final String RETRIEVE_RECEIVE_ITEMS_METHOD = "/retrieve_received_items_list";
    public static final String RETRIEVE_INVOICE__METHOD = "/retrieve_invoice_list";
    //public static final String RETRIEVE_CUSTOMER_SUGGESTIONS_METHOD = "/suggestions";
    public static final String RETRIEVE_NEWBRAND_METHOD="/retrieve_brand_list_after";
    public static final String RETRIEVE_NEWCOMPANY_METHOD="/retrieve_company_list_after";
    public static final String RETRIEVE_NEWPRODUCTSKU_METHOD="/retrieve_product_sku_list_after";
    public static final String RETRIEVE_DISTRIBUTOR_PRODUCT_MAP = "/retrieve_distributor_product_map";
    public static final String LOCAL_DISTRIBUTORS_METHOD = "/list_by_pincode";
    public static final String UPLOAD_METHOD = "/upload_image";
    public static final String DOWNLOAD_METHOD = "/download_image";
    public static final String DOWNLOAD_ONLY_METHOD = "/download";
    public static final String DEVICE_STATUS_METHOD = "/check_status";
    public static final String DOWNLOAD_VIDEO_METHOD = "/videos";
    public static final String STORE_STATE_METHOD = "/store_state";
    public static final String UPDATE_STATE_METHOD="/update_state";
    public static final String CAMPAIGN_SYNC_METHOD="/retrieve_campaign_list";
    
    public static final String CAMPAIGN_ID = "campaign_id";
    public static final String CAMPAIGN_PATH = "/campaigns";

    public static final String STOCK_REPORT_KEY = "stock_report_type";
    public static final String WIDGET_PATH = "snapwidgets";

    public static final int MAX_CONNECTIONS = 30;
    public static final int MAX_CONNECTIONS_PERROUTE = 1;
    public static final int MAX_CONNECTION_TIMEOUT = 1800000;

    public static final String EMAIL_REGEX = "^[_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{2,4})$";
    public static final String NAME_REGEX = "^[A-Za-z ]{2,256}$";
    public static final String ALPHANUMERIC_REGEX = "^[a-zA-Z0-9 ]*$";
    public static final String MOBILE_REGEX = "^[0-9]{10}$";
    public static final String LANDLINE_REGEX = "^[0-9]{11}$";
    public static final String NUMBER_REGEX = "^[0-9]*$";
    public static final String TIN_NUMBER_REGEX = "^[0-9]{11}$";
    public static final String ADDRESS_REGEX="^[a-zA-Z0-9#,\\s]{1,256}$";
    public static final String ZIP_REGEX = "^[0-9]{6}$";
    
    public static final int MAX_UNSYNCED_DAYS = 2;
    public static final String BILLING_FILE = "billing";
    public static final String DEVICE_FILE = "device";
    public static final String DEVICE_ID="device_id";
    public static final String DEVICE_STATE="device_state";
    public static final String DEVICE_SYNC_TIME="device_sync_time";
    public static final String DEVICE_DB_NAME="device_db_name";
    public static final String DEVICE_SKU_UPDATE="device_sku_update";
    public static final String APP_VERSION_CODE="version_code";
    public static final String DEVICE_DISABLED_MESSAGE = "Your Account has been disabled please contact our customer care executive";
    public static final String DEVICE_DISABLED_TITLE = "Account Disabled";
    public static final String DEVICE_UNSYNCED_MESSAGE = "Connect your device to the network";
    public static final String SYNC_FILE_V2 = "sync_file_v2";
    public static final String INVENTORY_HEADER = "inventory_header";
    public static final String INVENTORY_MRP_HEADER = "mrp";
    public static final String INVENTORY_PURCHASEPRICE_HEADER = "purchaseprice";
    public static final String INVENTORY_QTY_HEADER = "qty";
    public static final String INVENTORY_VAT_HEADER = "vatrate";
    public static final String INVENTORY_UNIT_HEADER = "unittype";
    public static final String INVENTORY_CATEGORY_HEADER = "category";
    public static final String INVENTORY_SUBCATEGORY_HEADER = "subcategory";
    public static final String INVENTORY_ACTIONS_HEADER = "actions";

    public static final String DASHBOARD_FILE = "dashboard";
    public static final String SMARTSTORE_FILE = "smartstore";
    
    public static final String STOCK_REPORT_DISTRIBUTOR_KEY="stock_report_distributor";
    public static final String STOCK_REPORT_CATEGORY_KEY="stock_report_category";
    public static final String STOCK_MONEY_KEY="stock_money";
    public static final String SMART_STORE_KEY="smart_store";
    

    public static final String PRINTER_FILE = "printer_file";
    public static final String PRINTER_TYPE = "printer_type";
    public static final String PRINTER_NOT_CONNECTED_ERRORMSG = "Printer Not Connected";
    
    public static final String PRINTER_STORE_NAME_KEY = "printer_store_name";
    public static final String PRINTER_STORE_ADDRESS_KEY = "printer_store_address";
    public static final String PRINTER_STORE_NUMBER_KEY = "printer_store_number";
    public static final String PRINTER_STORE_TIN_NUMBER_KEY = "printer_store_tin_number";
    public static final String PRINTER_STORE_CITY_KEY = "printer_store_city";
    public static final String PRINTER_FOOTER1_KEY = "printer_footer1";
    public static final String PRINTER_FOOTER2_KEY = "printer_footer2";
    public static final String PRINTER_FOOTER3_KEY = "printer_footer3";
    public static final String PRINTER_FOOTER4_KEY = "printer_footer4";
    
    public static final String PRINTER_HEADER_FONT = "printer_header_font";
    public static final String PRINTER_FOOTER1_FONT = "printer_footer1_font";
    public static final String PRINTER_FOOTER2_FONT = "printer_footer2_font";
    public static final String PRINTER_FOOTER3_FONT = "printer_footer3_font";
    public static final String PRINTER_FOOTER4_FONT = "printer_footer4_font";
    public static final String PRINTER_CONTENT_FONT = "printer_content_font";
    public static final String PRINTER_AUTO_CONNECT = "printer_auto_connect";
    public static final String PRINTER_SPACING = "printer_spacing";
    public static final String PRINTER_SERIAL_NUM = "printer_serial_num";
    public static final String PRINTER_VAT = "printer_vat";
    public static final String PRINTER_MRP = "printer_mrp";
    public static final String PRINTER_SUMMARY = "printer_summary";
    
    public static final String HOTFIX_KEY = "hotfix";
    public static final String PRINT_SAVINGS = "print_savings";
    public static final String PRINT_EXTRA_LINE = "print_extra_line";
    public static final String SAVE_SAVINGS = "save_savings";
    public static final String SORT_LOOSEITEMS = "sort_looseitems";
    public static final String DISPLAY_VISIBLITY = "display_visiblity";
    public static final String MULTIPOS_CLIENT = "client_checkout_screen";
    public static final String DISPLAY_OFFER_TAGS = "display_offer_tags";
    public static final String ROUNDOFF_LOOSEITEMS = "roundoff_looseitems";
    public static final String PRINT_ESTIMATE = "print_estimate";
    public static final String STORE_FILE = "store";
    public static final String STORE_ID="store_id";
    public static final String STORE_AUTH_KEY = "access_token";
    public static final String STORE_API_KEY = "store_api";
    public static final String STORE_NAME_KEY = "store_name";
    public static final String STORE_NUMBER_KEY = "store_number";
    public static final String STORE_ADDRESS_KEY = "store_address";
    public static final String STORE_TIN_KEY = "store_tin";
    public static final String STORE_CITY="store_city";
    public static final String STORE_STATE="store_state";
    public static final String STORE_STATE_ID="store_state_id";
    public static final String STORE_ZIP="store_zip";
    public static final String RETAILER_ID="retailer_id";
    public static final String RETAILER_OWNER_NAME="owner_name";
    public static final String RETAILER_SALES_PERSON_NUMBER="sales_person_number";
    public static final String RETAILER_OWNER_NUMBER="owner_person_number";
    public static final String RETAILER_OWNER_EMAIL="owner_email";
    public static final String SALESMAN_NUMBER_KEY = "salesman_number";
    public static final String INVOICE_ID = "invoice_id";
    public static final String UPDATE_PROD_LIST_KEY = "update_prod_list";
    public static final String UPDATE_VAT_RATE_KEY = "update_vat_rate";
    public static final String UPDATE_OFFER_KEY = "update_offer";
    public static final String XTRA_QUICK_ADD_PRODUCT = "xtra_quick_add_product";
    
    public static final String INVOICE_VERSION_NAME = "invoice_version_name";
    public static final String BILLING_VERSION_NAME = "billing_version_name";
    public static final String VISIBILITY_VERSION_NAME = "visibility_version_name";
    public static final String PUSHOFFERS_VERSION_NAME = "pushoffers_version_name";
    public static final String DASHBOARD_VERSION_NAME = "dashboard_version_name";
    public static final String TABLET_DB_VERSION = "tablet_db_version";
    public static final String IMG_DOWNLOADING_ID = "img_downloading_id";
    public static final String CURRENT_CAMPAIGN = "current_campaign";
    public static final String CURRENT_CAMPAIGN_LIST = "current_campaign_list";
    public static final String CAMPAIGN_SYNC_STATUS = "campaign_sync_status";
    
    
    
    public static final String STORE_INVENTORY_HELP_VIDEOS_KEY = "store_inventory_help_videos";

    public static final String SYNC_CUSTOMER_KEY = "sync_cust";
    public static final String SYNC_CUSTOMERSUGGESTIONS_KEY = "sync_cust_suggestions";
    public static final String SYNC_PRODUCTSKU_KEY = "sync_prodsku";
    public static final String SYNC_BILLITEM_KEY = "sync_billitem";
    public static final String SYNC_CUSTOMER_PAYMENT_KEY = "sync_customerpayment";
    public static final String SYNC_TRANSACTION_KEY = "sync_transaction";
    public static final String SYNC_INVENTORY_KEY = "sync_inventory";
    public static final String SYNC_INVENTORYBATCH_KEY = "sync_inventorybatch";
    public static final String SYNC_COMPANY_KEY = "sync_company";
    public static final String SYNC_BRAND_KEY = "sync_brand";
    public static final String SYNC_COMPANYBRAND_KEY = "sync_companybrand";
    public static final String SYNC_DISTRIBUTOR_KEY = "sync_distributor";
    public static final String SYNC_DISTRIBUTOR_BRAND_MAP_KEY = "sync_distributor_brand_map";
    public static final String SYNC_PAYMENTS_KEY = "sync_payments";
    public static final String SYNC_ORDERS_KEY = "sync_orders";
    public static final String SYNC_ORDERSDETAILS_KEY = "sync_orderdetails";
    public static final String SYNC_PRODUCTSKU_RETRIEVAL_KEY = "sync_prodsku_retrieval";
    public static final String SYNC_BRAND_RETRIEVAL_KEY = "sync_brand_retrieval";
    public static final String SYNC_COMPANY_RETRIEVAL_KEY = "sync_company_retrieval";
    public static final String SYNC_RECEIVE_ITEMS_KEY = "sync_received_items";
    public static final String SYNC_INVOICE_ITEMS_KEY = "sync_invoice";
    public static final String SYNC_DISTRIBUTOR_PRODUCT_MAP_KEY = "sync_distributor_product_map";
    public static final String SYNC_TIMESTAMP_KEY = "timestamp";
    public static final String SYNC_PRODUCTIMAGE_KEY = "product_image";
    public static final String SYNC_BRANDIMAGE_KEY = "brand_image";
    public static final String SYNC_FILE = "sync";
    public static final String CAMPAIGN_SYNC_FILE = "camp_sync";
    public static final String SYNC_HELPVIDEO_KEY = "help_video";
    public static final String SYNC_CAMPAIGN_KEY= "sync_campaign";
    public static final String STORE_PIN_KEY = "stored_pin";
    public static final String STORE_SELECTION_KEY = "stored_selection";
    public static final String SYNC_CATEGORY_KEY = "sync_category";
    public static final String SYNC_VERSION_CODE_DETAILS = "sync_version_code_details";
    public static final String APK_UPGRADE_DIALOG_LAUNCH_DATE = "apk_upgrade_dialog_launch_date";

    public static final String TABLET_DB_ID = "tabletDbId";

    public static final String ACTIVITY_KEY = "activity_key";
    public static final String BILLING_VERSION_KEY="billing_version_key";
    public static final String STOCK_VERSION_KEY="stock_version_key";
    public static final String BILLING_UPDATE_TIME_KEY="billing_update_time_key";
    public static final String STOCK_UPDATE_TIME_KEY="stock_update_time_key";

    public static final String STANDARD_DATEFORMAT = "yyyy/MM/dd ww HH:mm:ss";
    public static final String ORMLITE_STANDARD_DATEFORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String BACKUP_DATE_FORMAT = "yyyy-MM-dd-HH-mm-ss";
    public static final String DAY_DATEFORMAT = "yyyy/MM/dd";
    public static final String EXCEL_DATEFORMAT = "dd/MM/yyyy";
    public static final String OLDEST_SYNC_DATE = "2016-02-26 00:00:00";
    public static final String OLDEST_PRODSYNC_DATE = "2016-02-26 00:00:00";
    public static final String OLDEST_CATAGORY_SYNC_DATE = "2016-02-26 00:00:00";;
    public static final String VAT_UPDATE_DATE = "2016-02-25 00:00:00";
    public static final String OLDEST_APK_DIALOG_LAUNCH_DATE = "2016/05/31";
    
    public static final String OLDEST_RETRIEVAL_TIMESTAMP = "0";
    public static final String Pending = "";

    public static final int LINEFEED = 4;
    public static final int MAX_PHONENUMBER_LENGTH = 10;
    public static final int MAX_QTY_LENGTH = 3;
    public static final int MAX_PRICE_LENGTH = 7;
    public static final int CUSTOMER_SUGGESTIONS_SYNC_DAYS = 3;
    public static final int KEY_STROKE_TIMEOUT = 700;

    public static final String SNAP_PACKAGENAME = "com.snapbizz.snapbilling";
    public static final String SNAP_INVENTORY_PACKAGENAME = "com.snapbizz.snapstock";
    public static final String SNAP_VISIBILITY_PACKAGENAME = "com.snapbizz.snapvisibility";
    public static final String SNAP_PUSHOFFERS_PACKAGENAME = "com.snapbizz.pushoffers";
    public static final String SNAP_DASHBOARD_PACKAGENAME = "com.snapbizz.dashboard";
   
    
    
    public static final String IMAGE_PATH = "/data/data/"+SNAP_PACKAGENAME+"/images/";
    public static final String VISIBILITY_IMAGE_PATH = "/data/data/"+SNAP_PACKAGENAME+"/images/";
    
    public static final String HELP_VIDEO_PATH = "/data/data/"+SNAP_PACKAGENAME+"/videos/";
    
    public static final String HELP_VIDEO_BILLING_PATH = "/data/data/"+SNAP_PACKAGENAME+"/videos/billing/";
    public static final String HELP_VIDEO_INVENTORY_PATH = "/data/data/"+SNAP_INVENTORY_PACKAGENAME+"/videos/inventory/";
    public static final String HELP_VIDEO_PUSHOFFERS_PATH = "/data/data/"+SNAP_PACKAGENAME+"/videos/push_offers/";

    public static final String PRODUCTS_FOLDER = IMAGE_PATH+"products/";
    public static final String COMPANY_FOLDER = IMAGE_PATH+"company/";
    public static final String BRANDS_FOLDER = IMAGE_PATH+"brands/";
    public static final String PO_FOLDER = IMAGE_PATH+"po/";
    
    public static final int ACTIVE_STOCK_DAYS = 45;	
    public static final int MINIMUM_STOCK_DAYS = 2;	
    public static final int MAXIMUM_STOCK_DAYS = 15;
    public static final int SLOW_STOCK_DAYS = 14;
    public static final int NEW_STOCK_DAYS = 7;	
    public static final int AVG_MONTH_SPAN = 6;

    
    public static final String VISIBILITY_PROD_FILE="visibility";
    public static final String VISIBILITY_STORE_KEY="visibility_store";
    public static final String VISIBILITY_OFFER_KEY="visibility_offer";
    public static final String VISIBILITY_MONEY_KEY="visibility_money";
    public static final String VISIBILITY_FORGOTTEN_KEY="visibility_forgotten";

    public static final String PINCODE_KEY = "pinCode";

    public static final String [] ALL_UNIT_TYPES = new String[]{SkuUnitType.PC.getUnitValue(), SkuUnitType.KG.getUnitValue(), SkuUnitType.GM.getUnitValue(), SkuUnitType.LTR.getUnitValue(), SkuUnitType.ML.getUnitValue()};
    public static final String [] INVENTORY_UNIT_TYPES = new String[]{SkuUnitType.PC.getUnitValue(), SkuUnitType.KG.getUnitValue(), SkuUnitType.LTR.getUnitValue()};

    public static final String QUICKADD_IMAGE_PREFIX = "snapbizz_product_";
    public static final String FORGOTTEN_IMAGE_PREFIX = "forgotten/forgotten_";

    public static final int MISCELLANEOUS_CATEGORY_ID = 95;
    public static final int MISCELLANEOUS_BRAND_ID = 1302;
    public static final int MISCELLANEOUS_COMPANY_ID = 526;

    public static final String SNAP_LOCAL_PREFIX_KEY ="snaplocal";
    public static final String SNAP_LOOSE_PREFIX_KEY ="snaploose";
    public static final String SNAP_LOCAL_CAMPAIGN_PREFIX_KEY = "snaplocalcampaign";
    public static final String SNAP_CAMPAIGN_PREFIX_KEY = "snapcampaign";
    public static final String SNAP_MYSCREEN_PREFIX_KEY = "myscreen";
    
    
    public static final String SNAP_VIDEO_PASSKEY= "{\"algorithm\":\"AES\",\"key\":[-6,112,80,7,-90,113,101,-10,-96,89,-16,120,-118,-26,-79,-48,-89,52,-10,-88,44,-58,-121,-40]}"; 
    public static final String SNAP_VAT_KEY="isvatSelected";
    
    		
    public static final String FORCE_SYNC_TRANSACTION_TIMESTAMP_KEY = "forcesync_transaction";
    public static final String FORCE_SYNC_BILLITEM_TIMESTAMP_KEY = "forcesync_billitem";
    public static final String FORCE_SYNC_INV_BATCH_TIMESTAMP_KEY = "forcesync_batch_item";
    public static final String FORCE_SYNC_PRODSKU_TIMESTAMP_KEY = "forcesync_prodsku";
    public static final String FORCE_SYNC_INVSKU_TIMESTAMP_KEY = "forcesync_invsku";
    public static final String FORCE_SYNC_BRANDS_TIMESTAMP_KEY = "forcesync_brand";
    public static final String FORCE_SYNC_COMPANY_TIMESTAMP_KEY = "forcesync_company";
    public static final String FORCE_SYNC_DISTRIBUTOR_BRAND_MAP_KEY = "forcesync_distributor_brand_map";
    public static final String FORCE_SYNC_PO_KEY = "forcesync_PO";
    public static final String FORCE_SYNC_PO_DETAILS_KEY = "forcesync_po_details";
    public static final String FORCE_SYNC_DISTRIBUTOR_KEY = "forcesync_distributor";
    public static final String FORCE_SYNC_INVOICE_KEY = "forcesync_invoice";
    public static final String FORCE_SYNC_CUSTOMER_KEY = "forcesync_customer";
    public static final String FORCE_SYNC_CUSTOMER_PAYMENT_KEY = "forcesync_customer_payment";
    public static final String FORCE_SYNC_PAYMENT_KEY = "forcesync_payment";
    public static final String FORCE_SYNC_CATEGORY_KEY = "forcesync_category_payment";
    public static final String FORCE_SYNC_RECEIVE_KEY = "forcesync_receive";
    
    public static final String VAT_PATCH="vat_applied";
    public static  String IS_REPORT="0";
    public static  String CAT_VALUE="";
    public static  String SUB_CAT_VALUE="";
    public static  int SKU_REPORT_COUNT=0;
    public static final String DOWNLOAD_TIME_METHOD = "/acknowledge_download";
    public static  String IS_CAMP_CLK="0";

    public static  String CUST_LIST_SELECTED_POS="0";
    public static  String UNSOLD_PROD_DELETE_LIMIT="5";
    public static int DOWNLOADED_CAMPAIGN_COUNT=0;
    public static final String DEVICE_CAMP_LAST_SYNC_TIME="device_last_camp_sync_time";
    public static  String IS_MENU_CLK="0";
    public static  int BILL_HISTORY_SEL_POS=0;
    public static  int CAMP_IMG_DWNLD_LIMIT=0;
    public static int IS_RATE_QTY_CHANGED=0;

    public static final String PRICE_FORMAT 	= "############.##";
    public static final Double MIN_PRICE 		= 0.001;
    public static final long ADD_FOR_INVOICE 	=    100000000L;
    public static final long YEAR_MULTIPLIER 	=      1000000L;
    public static final long POS_ID_MULTIPLIER 	=   1000000000L;
    public static final int OLDEST_NEW_INVOICE_YEAR = 15;
    public static final String SCANNER_KEY = "Scan";
    public static final String SCANNER_KEY2 = "BT HID";
    public static final String SCANNER_KEY3 = "Barcode";
    public static final int EDIT_DISCOUNT_ON_ITEM_CONTEXT = 24;
    
    public static final String SKU_SALEPRICE = "sku_saleprice";
    public static final String SKU_SALEPRICE_TWO = "sku_saleprice_two";
    public static final String SKU_SALEPRICE_THREE = "sku_saleprice_three";
    public static final int XTRA_PRODUCTS_CAT_ID = 102;
    public static final int OTHERS_CAT_ID = 76;
    
    public static final String GDB_SNAPSHOT_TIMEFORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    
    
    // Billing file Constants / Properties
    public static final String ACTIVE_CART_ON_TOUCH = "active_cart_on_touch";
    public static final int LAST_SHOPPING_CART = 3;
    public static final String CONNECTED_PRINTER_NAME = "connected_printer_name";
    public static final String ENABLE_MULTI_SCANNER = "enable_multi_scanner";
}
