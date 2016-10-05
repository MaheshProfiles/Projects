package com.snapbizz.snapbilling.activity;

import java.io.File;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarFile;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaRouter;
import android.media.MediaRouter.RouteInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.adapters.BillingMonitorAdapter.CartActionListener;
import com.snapbizz.snapbilling.asynctasks.GetQuickAddSubCategoriesTask;
import com.snapbizz.snapbilling.db.ProductsHelper;
import com.snapbizz.snapbilling.domainsV2.ApkData;
import com.snapbizz.snapbilling.fragments.BillCheckoutFragment;
import com.snapbizz.snapbilling.fragments.BillCheckoutFragment.OnCreateEditQuickAddProductClickListener;
import com.snapbizz.snapbilling.fragments.BillingHistoryFragment;
import com.snapbizz.snapbilling.fragments.BillingMonitorFragment;
import com.snapbizz.snapbilling.fragments.CustomerPaymentFragment;
import com.snapbizz.snapbilling.fragments.EditProductDialogFragment.EditProductDialogListener;
import com.snapbizz.snapbilling.fragments.InventoryFragment;
import com.snapbizz.snapbilling.fragments.SettingsFragment;
import com.snapbizz.snapbilling.fragments.TemplateFragment;
import com.snapbizz.snapbilling.fragments.OfferAndStoreFragment;
import com.snapbizz.snapbilling.fragments.VatSummaryFragment;
import com.snapbizz.snapbilling.fragments.VisibilityPresentation;
import com.snapbizz.snapbilling.interfaces.OnAddNewProductListener;
import com.snapbizz.snapbilling.interfaces.OnRecreateAdapterListener;
import com.snapbizz.snapbilling.interfaces.SearchedProductClickListener;
import com.snapbizz.snapbilling.services.PrinterService;
import com.snapbizz.snapbilling.services.MultiPos;
import com.snapbizz.snapbilling.services.WeighingMachine;
import com.snapbizz.snapbilling.utils.GoogleAnalyticsTracker;
import com.snapbizz.snapbilling.utils.SnapBillingConstants;
import com.snapbizz.snapbilling.utils.SnapBillingUtils;
import com.snapbizz.snaptoolkit.asynctasks.PendingSyncTask;
import com.snapbizz.snaptoolkit.customviews.SoftInputEditText.OnExternalKeyInputListener;
import com.snapbizz.snaptoolkit.db.GlobalDB;
import com.snapbizz.snaptoolkit.db.SnapbizzDB;
import com.snapbizz.snaptoolkit.domains.Distributor;
import com.snapbizz.snaptoolkit.domains.InventorySku;
import com.snapbizz.snaptoolkit.domains.ProductCategory;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.domains.ShoppingCart;
import com.snapbizz.snaptoolkit.domainsV2.Models.ProductInfo;
import com.snapbizz.snaptoolkit.fragments.AddEditProductFragment;
import com.snapbizz.snaptoolkit.fragments.AddEditProductFragment.OnAddProductSuccess;
import com.snapbizz.snaptoolkit.fragments.HelpVideosFragment;
import com.snapbizz.snaptoolkit.fragments.SyncSummaryFragment;
import com.snapbizz.snaptoolkit.interfaces.OnActionbarNavigationListener;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.CustomToast;
import com.snapbizz.snaptoolkit.utils.SkuUnitType;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;
import com.snapbizz.v2.sync.GlobalSync;
import com.snapbizz.v2.sync.LocalSyncData.ApiOtpGeneration;
import com.snapbizz.v2.sync.ToolsAPI;

public class BillingActivity extends SnapActivity implements
		OnQueryCompleteListener,
		EditProductDialogListener, SearchedProductClickListener,
		OnActionbarNavigationListener, CartActionListener,
		OnExternalKeyInputListener, OnCreateEditQuickAddProductClickListener,
		OnAddProductSuccess, OnRecreateAdapterListener, OnAddNewProductListener {

	private static final String TAG = "[BillingActivity]";
	private static final int GET_SCANNEDSKU_TASKCODE = 0;
	private static final int GET_QUICKADD_CATEGORY_TASKCODE = 7;
    private static int N_SHOPPING_CARTS = 4;				// greater than 1 => Server and equal to 1 => Client

	private static final int GET_PENDING_TASKCODE = 77;
	protected static final int PRE_CREATE_QUICK_ADD = 51;

	// protected final int PRE_CREATE_QUICK_ADD = 51;
	private static final String BASKET_SWITCH_PREFIX = "SB";
	private static final String BASKET_WEIGHT_PREFIX = "SW";
	private static final String BARCODE_SPLITTER_EXPRESSION = "#";
	private SparseArray<String> barCodeMap = new SparseArray<String>();					// Scan progress string for each device(key)
	private SparseIntArray activeShoppingCartMap  = new SparseIntArray();				// ShoppingCart Id's for each device(key)
	private SparseArray<ShoppingCart> shoppingCartList = new SparseArray<ShoppingCart>();
	private LinkedBlockingQueue<ScannedBarcode> scanQueue = new LinkedBlockingQueue<ScannedBarcode>();
	private LinkedBlockingQueue<ScannedKeyInput> keyQueue = new LinkedBlockingQueue<ScannedKeyInput>();
	private BillCheckoutFragment billCheckoutFragment;
	private BillingMonitorFragment billingMonitorFragment;
	private VisibilityPresentation mPresentation;
	private MediaRouter mMediaRouter;
	private ArrayList<ProductCategory> quickAddCategories;
	private AddEditProductFragment addEditProductFragment;
	protected WeighingMachine mWeighingMachineService;
	protected PrinterService mPrinterService;
	protected MultiPos mMultiPosService;
	private static Timer visibilityConnectionStatusTimer;


	private enum FRAGMENT_TYPE {
		FRAGMENT_CHECKOUT,
		FRAGMENT_OFFERS,
		FRAGMENT_SEARCH,
		FRAGMENT_QUICKADD
	};
	// Used for tracking user manual choices and onQuickAdd
	private int activeShoppingCartId = 0;
	
	private static class ScannedBarcode {
		public int cartId = 0;
		public String barcode = "";

		public ScannedBarcode(int cartId, String barcode) {
			this.cartId = cartId;
			this.barcode = barcode;
		}
	}
	
	private static class ScannedKeyInput {
		public int keyCode;
		public KeyEvent event;
		
		public ScannedKeyInput(int keyCode, KeyEvent event) {
			this.keyCode = keyCode;
			this.event = event;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initializeApplication();

		setContentView(R.layout.activity_billing);
		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setIcon(R.drawable.icon_back_normal);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setCustomView(R.layout.actionbar_layout);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		if(SnapSharedUtils.isClient(getApplicationContext()))
			N_SHOPPING_CARTS = 1;
		billingMonitorFragment = new BillingMonitorFragment();
		ft.add(R.id.content_framelayout, billingMonitorFragment,
				getString(R.string.monitorfragment_tag));
		ft.replace(R.id.content_framelayout, billingMonitorFragment,
				getString(R.string.monitorfragment_tag));
		ft.commit();
		for(int i = 0; i < N_SHOPPING_CARTS; i++) {
			ShoppingCart shoppingCart = new ShoppingCart(i);
			if(i == 0)
				activeShoppingCartId = shoppingCart.getShoppingCartId();
			shoppingCartList.put(i, shoppingCart);
			billingMonitorFragment.onAddCart(shoppingCart, i, this, this);
		}

		mMediaRouter = (MediaRouter) getSystemService(Context.MEDIA_ROUTER_SERVICE);
		String showDashboardIntentExtra = getIntent().getStringExtra(
				SnapBillingConstants.INTENT_EXTRA_KEY_SHOW_DASHBOARD);
		if (showDashboardIntentExtra != null &&
				showDashboardIntentExtra.equals(SnapBillingConstants.INTENT_EXTRA_SHOW_DASHBOARD)) {
			onActionbarNavigation("", R.id.dashboard_menuitem);
		}
		new GetQuickAddSubCategoriesTask(this, this, GET_QUICKADD_CATEGORY_TASKCODE).execute();
		// Start GlobalSync Service
		Intent globalSyncIntent = new Intent(this, GlobalSync.class);
		startService(globalSyncIntent);
		
		// Create thread for scan processing
		startScanThread();
		startCartThread();
	}
	
	private void startScanThread() {
		Thread thread = new Thread() {
			@Override
		    public void run() {
		        try {
		        	while(true)  {
			        	final ScannedKeyInput input = keyQueue.take();
			        	final int deviceId = input.event.getDevice().getId();
			        	final int keyCode = input.keyCode;
						String barCode = barCodeMap.get(deviceId);
						if(barCode == null)
							barCode = "";
						if (keyCode == KeyEvent.KEYCODE_TAB || keyCode == KeyEvent.KEYCODE_ENTER) {
							Log.d(TAG, "scanned barcode :" + deviceId + ":" + barCode + "keycode " + keyCode + "Action: " + input.event.getAction());
							if(barCode.contains("SNAP")) { // Format SNAP
								try {
									if(barCode.contains("SNAP1") || barCode.contains("SNAP2") ||
									   barCode.contains("SNAP3") || barCode.contains("SNAP4"))
										barCode = barCode.substring(barCode.indexOf("SNAP") + 5);
								} catch(Exception e) { }
							}
							
							if (barCode.length() < 2) {
								barCodeMap.remove(deviceId);
								continue;
							}
							
							Log.d(TAG, " formatted code " + barCode);
							String[] barcodeSplit = barCode.split(BARCODE_SPLITTER_EXPRESSION);
							if (barcodeSplit.length > 1 && barcodeSplit[1].equalsIgnoreCase(BASKET_WEIGHT_PREFIX)) {
								try {
									getScannersShoppingCart(deviceId).setLastItemQty(Float.parseFloat(barcodeSplit[3]),
																					 SkuUnitType.valueOf(barcodeSplit[2]));
									// TODO: For BB
								} catch (Exception e) {
									barCodeMap.remove(deviceId);
									continue;
								}
								BillingActivity.this.runOnUiThread(new Runnable() {
									@Override
									public void run() {
										if (billCheckoutFragment != null && billCheckoutFragment.billListAdapter != null) {
											billCheckoutFragment.resetAdapter();
											billCheckoutFragment.updateBillTextViews(BillingActivity.this.getApplicationContext());
										}
									}
								});
							} else if (barcodeSplit.length > 1 && barcodeSplit[1].equalsIgnoreCase(BASKET_SWITCH_PREFIX)) {
								final String switchCode = barCode;
								BillingActivity.this.runOnUiThread(new Runnable() {
									@Override
									public void run() {
										switchScannerShoppingCart(deviceId, switchCode);
									}
								});
							} else if (addEditProductFragment != null && addEditProductFragment.isVisible()) {
								final String editBarcode = barCode;
								BillingActivity.this.runOnUiThread(new Runnable() {
									@Override
									public void run() {
										addEditProductFragment.SetBarcode(editBarcode);
									}
								});
							} else {
								scanQueue.add(new ScannedBarcode(getShoppingCartId(deviceId), barCode));
							}
							barCodeMap.remove(deviceId);
						} else if (keyCode != KeyEvent.KEYCODE_SHIFT_LEFT) {
							int ch = input.event.getUnicodeChar();
							if( /*ch != 0 */ ch >=32 && ch < 127)
								barCode += ((char) ch);
							barCodeMap.put(deviceId, barCode);
						}
			        	//sleep(10);
		        	}
		        } catch(InterruptedException e) {
		        	e.printStackTrace();
		        }
			}
		};
		thread.start();
	}
	
	private void startCartThread() {
		Thread thread = new Thread() {
		    @Override
		    public void run() {
		        try {
		        	boolean inserted = false;
		        	boolean[] scInserts = new boolean[N_SHOPPING_CARTS];
		        	SnapbizzDB db = SnapbizzDB.getInstance(getApplicationContext(), false);
		        	while(true) {
		        		if(inserted == true && scanQueue.peek() == null) {
		        			inserted = false;
		        			final boolean[] changes = scInserts;
		        			BillingActivity.this.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									boolean isForthCart = true;
									for(int shoppingCartId = 0; shoppingCartId < N_SHOPPING_CARTS; shoppingCartId++) {
										if(!changes[shoppingCartId])
											continue;
										if(shoppingCartId != SnapBillingConstants.LAST_SHOPPING_CART)
											isForthCart = false;
										billingMonitorFragment.resetAdapter(shoppingCartId);
										/*BillListAdapter billListAdapter = billingMonitorFragment.getBillingMonitorAdapter()
																								  .getBillingAdapter(shoppingCartId);*/
										if (billCheckoutFragment != null && billCheckoutFragment.billListAdapter != null &&
												billCheckoutFragment.isVisible() &&
												billCheckoutFragment.getShoppingCart().getShoppingCartId() == shoppingCartId) {
											billCheckoutFragment.resetAdapter();
											billCheckoutFragment.updateBillTextViews(BillingActivity.this.getApplicationContext());
										}
									}
									if(isForthCart && billingMonitorFragment != null)
										billingMonitorFragment.setSelection(2);
								}
			        		});
		        			scInserts = new boolean[N_SHOPPING_CARTS];
		        		}
		        			
		        		final ScannedBarcode barcode = scanQueue.take();
		        		try {
		        			ProductInfo sku = ProductsHelper.getProductInfo(Long.valueOf(barcode.barcode));
		        			shoppingCartList.get(barcode.cartId).addItemsToCart(sku, db.packsHelper.getProductPacks(sku));
		        		} catch(NumberFormatException e) { }

		        		scInserts[barcode.cartId] = true;
						inserted = true;
						
		        		sleep(100);
		            }
		        } catch (InterruptedException e) {
		            e.printStackTrace();
		        }
		    }
		};

		thread.start();
	}
	
	public void setLastItemWeight(final float weight, final boolean kg) {
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				shoppingCartList.get(activeShoppingCartId).setLastItemQty(weight, kg ? SkuUnitType.KG : SkuUnitType.GM);
				if(billCheckoutFragment != null && billCheckoutFragment.billListAdapter != null
						 						&& billCheckoutFragment.isVisible()) {
					billCheckoutFragment.resetAdapter();
					billCheckoutFragment.updateBillTextViews(BillingActivity.this.getApplicationContext());
				}
			}
		});
	}

	private final MediaRouter.SimpleCallback mMediaRouterCallback = new MediaRouter.SimpleCallback() {

		// BEGIN_INCLUDE(SimpleCallback)
		/**
		 * A new route has been selected as active. Disable the current route
		 * and enable the new one.
		 */
		@Override
		public void onRouteSelected(MediaRouter router, int type, RouteInfo info) {
			updatePresentation();
		}

		/**
		 * The route has been unselected.
		 */
		@Override
		public void onRouteUnselected(MediaRouter router, int type,
				RouteInfo info) {
			updatePresentation();

		}

		/**
		 * The route's presentation display has changed.
		 * this.getApplicationContext() callback is called when the presentation
		 * has been activated, removed or its properties have changed.
		 */
		@Override
		public void onRoutePresentationDisplayChanged(MediaRouter router,
				RouteInfo info) {
			updatePresentation();
		}
		// END_INCLUDE(SimpleCallback)
	};

	/**
	 * 
	 * Updates the displayed presentation to enable a secondary screen if it has
	 * been selected in the {@link android.media.MediaRouter} for the
	 * {@link android.media.MediaRouter#ROUTE_TYPE_LIVE_VIDEO} type. If no
	 * screen has been selected by the {@link android.media.MediaRouter}, the
	 * current screen is disabled. Otherwise a new
	 * {@link VisibilityPresentation} is initialized and shown on the secondary
	 * screen.
	 */
	@SuppressLint("NewApi")
	private void updatePresentation() {
		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR1)
			return;
		// BEGIN_INCLUDE(updatePresentationInit)
		// Get the selected route for live video
		RouteInfo selectedRoute = mMediaRouter
				.getSelectedRoute(MediaRouter.ROUTE_TYPE_LIVE_VIDEO);
		// Get its Display if a valid route has been selected
		Display selectedDisplay = null;
		if (selectedRoute != null) {
			selectedDisplay = selectedRoute.getPresentationDisplay();
		}
		// END_INCLUDE(updatePresentationInit)

		// BEGIN_INCLUDE(updatePresentationDismiss)
		/*
		 * Dismiss the current presentation if the display has changed or no new
		 * route has been selected
		 */
		if (mPresentation != null
				&& mPresentation.getDisplay() != selectedDisplay) {
			mPresentation.dismiss();
			mPresentation = null;

		}
		// END_INCLUDE(updatePresentationDismiss)

		// BEGIN_INCLUDE(updatePresentationNew)
		/*
		 * Show a new presentation if the previous one has been dismissed and a
		 * route has been selected.
		 */
		if (mPresentation == null
				&& selectedDisplay != null
				&& SnapSharedUtils.getVisiblitySwitchStatus(this
						.getApplicationContext())) {

			// Initialise a new Presentation for the Display
			mPresentation = new VisibilityPresentation(this, selectedDisplay);
			mPresentation.setOnDismissListener(mOnDismissListener);

			// Try to show the presentation, this.getApplicationContext() might
			// fail if the display has
			// gone away in the mean time
			try {
				// showNextColor();
				if (visibilityConnectionStatusTimer != null) {
					visibilityConnectionStatusTimer.cancel();
					visibilityConnectionStatusTimer = null;
				}
				mPresentation.show();
			} catch (WindowManager.InvalidDisplayException ex) {
				// Couldn't show presentation - display was already removed
				mPresentation = null;
			}
		} else if (visibilityConnectionStatusTimer == null) {
			visibilityConnectionStatusTimer = new Timer();
			visibilityConnectionStatusTimer.schedule(new TimerTask() {
			    @Override
			    public void run() {
			    	sendVisibilityStatus();
			    	visibilityConnectionStatusTimer = null;
			    }
			}, SnapBillingConstants.VISIBILITY_TIMER_INTERVAL);
		}
		// END_INCLUDE(updatePresentationNew)
	}

	private final DialogInterface.OnDismissListener mOnDismissListener = new DialogInterface.OnDismissListener() {
		@Override
		public void onDismiss(DialogInterface dialog) {
			if (dialog == mPresentation) {
				mPresentation = null;
			}
		}
	};
	
	private void sendVisibilityStatus() {
		try {
			ToolsAPI toolsAPI = new ToolsAPI(getApplicationContext());
			toolsAPI.callVisibilityStatus();
		} catch (Exception e) {
			Log.e(TAG, "Exception: "+Log.getStackTraceString(e));
		}
	}
	
    private ServiceConnection mWMConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
        	WeighingMachine.WeighingMachineBinder binder = (WeighingMachine.WeighingMachineBinder) service;
            mWeighingMachineService = binder.getService();
            mWeighingMachineService.setDeviceAddress(SnapSharedUtils.getWeighingMachineAddress(getApplicationContext()));
            mWeighingMachineService.setParent(BillingActivity.this);
            mWeighingMachineService.reinitialize();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mWeighingMachineService = null;
        }
    };
    
    private ServiceConnection mMultiPosConnection = new ServiceConnection () {
    	@Override
        public void onServiceConnected(ComponentName className, IBinder service) {
        	MultiPos.MultiPosBinder binder = (MultiPos.MultiPosBinder) service;
            mMultiPosService = binder.getService();
            mMultiPosService.initializeServices(N_SHOPPING_CARTS > 1);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        	mMultiPosService = null;
        }
    };
    
    private ServiceConnection printerConnection = new ServiceConnection() {
    	
    	@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			PrinterService.PrinterBinder binder = (PrinterService.PrinterBinder) service;
			mPrinterService = binder.getService();
			mPrinterService.setDeviceName(SnapSharedUtils.getSelectedPrinterName(getApplicationContext()));
			mPrinterService.setParent(BillingActivity.this);
			mPrinterService.reinitialize();
		}
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mPrinterService = null;
		}
	};
	
	private HelpVideosFragment helpVideoFragment;

	@Override
	protected void onResume() {
		super.onResume();
		((TextView) findViewById(R.id.app_version_text))
			.setText(SnapCommonUtils.getCurrentVersion(getApplicationContext()));
		mMediaRouter.addCallback(MediaRouter.ROUTE_TYPE_LIVE_VIDEO, mMediaRouterCallback);
		updatePresentation();		
		// Check all SnapBizz App versions
		checkVersionCode(0);
		if(SnapSharedUtils.isClient(getApplicationContext()))
			onOpenCart(0);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		GoogleAnalyticsTracker.getInstance(this).startActivity(this);
		// Start Bluetooth WeighingMachine service
		if(SnapSharedUtils.isWeighingMachine(getApplicationContext())) {
			Intent intent = new Intent(this, WeighingMachine.class);
			bindService(intent, mWMConnection, Context.BIND_AUTO_CREATE);
		}
		
		// Start Bluetooth Printer service
		Intent printerIntent = new Intent(this, PrinterService.class);
		bindService(printerIntent, printerConnection, Context.BIND_AUTO_CREATE);
		
		// Start MultiPos service
		Intent multiPosIntent = new Intent(this, MultiPos.class);
		bindService(multiPosIntent, mMultiPosConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// TestFlight.sendsLogs();
		// TestFlight.sendsCheckpoints();
		// BEGIN_INCLUDE(onPause)
		// Stop listening for changes to media routes.
		mMediaRouter.removeCallback(mMediaRouterCallback);
		// END_INCLUDE(onPause)
	}

	@SuppressLint("NewApi")
	@Override
	protected void onStop() {
		super.onStop();
		GoogleAnalyticsTracker.getInstance(this).stopActivity(this);

		// BEGIN_INCLUDE(onStop)
		// Dismiss the presentation when the activity is not visible.
		if (mPresentation != null) {
			mPresentation.dismiss();
			mPresentation = null;
		}
		if(mWeighingMachineService != null) {
			unbindService(mWMConnection);
			mWeighingMachineService = null;
		}
		
		if(mPrinterService != null) {
			unbindService(printerConnection);
			mPrinterService = null;
		}
		
		if(mMultiPosService != null) {
			unbindService(mMultiPosConnection);
			mMultiPosService = null;
		}

		try {
			SnapBillingUtils.closeDatabaseHelper();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// BEGIN_INCLUDE(onStop)
	}

	// AddEditProductFragment fragment = (AddEditProductFragment)
	// getFragmentManager().findFragmentById(id);
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//Log.d(TAG, "scanner name :" + event.getDevice().getName());
		if(event != null && event.getDevice() != null &&
				SnapCommonUtils.isScanner(event.getDevice().getName())) {
			if(event.getAction() != KeyEvent.ACTION_DOWN)
				return true;
			keyQueue.add(new ScannedKeyInput(keyCode, event));
			return true;
		} else if(event.getAction() == KeyEvent.ACTION_DOWN) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				SnapToolkitConstants.BILL_HISTORY_SEL_POS = 0;
				return super.onKeyDown(keyCode, event);
			}
		}
		return false;
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if(event != null && event.getDevice() != null &&
				SnapCommonUtils.isScanner(event.getDevice().getName()))
			return onKeyDown(event.getKeyCode(), event);
		return super.dispatchKeyEvent(event);
	}

	public ShoppingCart getScannersShoppingCart(int scannerId) {
		Log.d(TAG, "scanner id " + scannerId + "active map " + activeShoppingCartMap.get(scannerId));
		return shoppingCartList.get(getShoppingCartId(scannerId));
	}

	public void switchScannerShoppingCart(Integer scannerId, String shoppingCartCode) {
		Log.d(TAG, "switching carts " + " scannerId : " + (scannerId != null ? scannerId : "null") + " newCartCode : " + shoppingCartCode);

		int shoppingCartId = SnapBillingUtils.getShoppingCartId(shoppingCartCode);
		if(shoppingCartId >= shoppingCartList.size())
			return;

		activateCartWithConfirmation(scannerId, shoppingCartId, false, true, FRAGMENT_TYPE.FRAGMENT_CHECKOUT);
	}
	
	private int getShoppingCartId(int scannerId) {
		if(activeShoppingCartMap.indexOfKey(scannerId) < 0)
			activeShoppingCartMap.put(scannerId, activeShoppingCartId);
		return activeShoppingCartMap.get(scannerId);
	}
	
	private void activateShoppingCart(Integer scannerId, Integer shoppingCartId) {
		if(activeShoppingCartMap == null || shoppingCartId >= shoppingCartList.size())
			return;

		if (billingMonitorFragment != null &&
				billingMonitorFragment.getBillingMonitorAdapter() != null)
			billingMonitorFragment.getBillingMonitorAdapter().setActiveCart(shoppingCartId);

		activeShoppingCartId = shoppingCartId;
		if(scannerId != null) {
			ShoppingCart shoppingCart = shoppingCartList.get(shoppingCartId);
			if (SnapSharedUtils.isMultiScanner(BillingActivity.this)) {
				activeShoppingCartMap.put(scannerId, shoppingCart.getShoppingCartId());
			} else {
				for (int i = 0; i < activeShoppingCartMap.size(); i++) {
					int scanner = activeShoppingCartMap.keyAt(i);
					activeShoppingCartMap.put(scanner, shoppingCart.getShoppingCartId());
				}
			}
		}
	}
	
	private void activateCartWithConfirmation(final Integer scannerId, final Integer shoppingCartId, final boolean bNavigate,
											  final boolean bActivate, final FRAGMENT_TYPE fragmentType) {
		if(shoppingCartId != SnapBillingConstants.LAST_SHOPPING_CART) {
			if(bActivate)
				activateShoppingCart(scannerId, shoppingCartId);
			if(bNavigate)
				showBillCheckout(shoppingCartId, fragmentType);
			return;
		}
		AlertDialog dialog = new AlertDialog.Builder(BillingActivity.this).create();
        dialog.setMessage(getResources().getString(R.string.confirmation_fourth_cart));
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.done),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int buttonId) {
                    	if(bActivate)
                    		activateShoppingCart(scannerId, shoppingCartId);
                    	if(bNavigate)
                    		showBillCheckout(shoppingCartId, fragmentType);
                    	dialog.dismiss();
                    	showBillCheckout(shoppingCartId, FRAGMENT_TYPE.FRAGMENT_CHECKOUT);
                    }
                });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int buttonId) {
                    	dialog.dismiss();
                    }
                });
        dialog.show();
	}

	@Override
	public void onTaskSuccess(Object responseList, int taskCode) {
		if ((taskCode & 0xFF) == GET_SCANNEDSKU_TASKCODE) {
			try {
				List<ProductSku> productSkuList = (List<ProductSku>) responseList;
				int shoppingCartId = taskCode >> 8;
                if(shoppingCartId < 0)
                    shoppingCartId = 0;
                // TODO: Check this
				//shoppingCartList.get(shoppingCartId).addItemsToCart(productSkuList.get(0));
				billingMonitorFragment.resetAdapter(shoppingCartId);
				/*BillListAdapter billListAdapter = billingMonitorFragment.getBillingMonitorAdapter()
																		.getBillingAdapter(shoppingCartId);*/
				if (billCheckoutFragment != null && billCheckoutFragment.billListAdapter != null
												 && billCheckoutFragment.isVisible()) {
					billCheckoutFragment.resetAdapter();
					billCheckoutFragment.updateBillTextViews(this.getApplicationContext());
				}
			} catch (ClassCastException e) {
				e.printStackTrace();
			}
		} else if (taskCode == GET_QUICKADD_CATEGORY_TASKCODE) {
			quickAddCategories = (ArrayList<ProductCategory>) responseList;
		} else if (taskCode == GET_PENDING_TASKCODE) {
			try {
				Log.d("pending task", "started pending" + GET_PENDING_TASKCODE);
				HashMap<String, Integer> resultMap = (HashMap<String, Integer>) responseList;
				Log.d("TAG", "Testing --resultMap-- > " + resultMap);
				SnapCommonUtils.setResultMap(resultMap);
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				DialogFragment newFragment = new SyncSummaryFragment();
				newFragment.show(ft, "dialog");
				Log.d("pending task", "finished pending");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void onTaskError(String errorMessage, int taskCode) {
		if ((taskCode & 0xFF) == GET_SCANNEDSKU_TASKCODE) {
			int cartId = taskCode >> 8;
            if(shoppingCartList == null)
            	return;
            // TODO: Check this
			//shoppingCartList.get(cartId).addItemsToCart(new ProductSku(errorMessage, errorMessage));
			billingMonitorFragment.resetAdapter(cartId);
			if (billCheckoutFragment != null && billCheckoutFragment.billListAdapter != null) {
				billCheckoutFragment.resetAdapter();
				billCheckoutFragment.updateBillTextViews(this.getApplicationContext());
			}
		} else {
			CustomToast.showCustomToast(this.getApplicationContext(), errorMessage, Toast.LENGTH_LONG, CustomToast.ERROR);
		}
	}

	@Override
	public void onAddProduct(ProductSku productSku, float quantity) {
		/*if (shoppingCartList == null) {
			shoppingCartList = new SparseArray<ShoppingCart>();
			ShoppingCart shoppingCart = new ShoppingCart(0);
			activeShoppingCartId = shoppingCart.getShoppingCartId();
			shoppingCartList.put(activeShoppingCartId, shoppingCart);
			if (billCheckoutFragment != null)
				billCheckoutFragment.setShoppingCart(shoppingCart);
			billingMonitorFragment.onAddCart(shoppingCart, 0,
					this.getApplicationContext(), this);
		}*/
		int cartId = activeShoppingCartId;
		if(billCheckoutFragment != null && billCheckoutFragment.isVisible())
			cartId = billCheckoutFragment.getShoppingCart().getShoppingCartId();
		shoppingCartList.get(cartId).addItemToCart(productSku, quantity);
		if (billCheckoutFragment != null) {
			billCheckoutFragment.billListAdapter.notifyDataSetChanged();
			billCheckoutFragment.updateBillTextViews(this.getApplicationContext());
			billCheckoutFragment.hideSearchProducts();
		}
	}

	@Override
	public void onSearchedProductClick(ProductSku productSku) {
		onAddProduct(productSku, 1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public void navigateBillCheckout(int shoppingCartId, boolean bAddToBack) {
		SnapCommonUtils.hideSoftKeyboard(this.getApplicationContext(), findViewById(R.id.content_framelayout).getWindowToken());
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		if (billCheckoutFragment == null) {
			billCheckoutFragment = new BillCheckoutFragment();
		}
		if (shoppingCartList != null && billCheckoutFragment != null) {
			billCheckoutFragment.setShoppingCart(shoppingCartList
					.get(shoppingCartId));
		}
		if (quickAddCategories != null)
			billCheckoutFragment
					.setProductQuickAddCategories(quickAddCategories);
		ft.replace(R.id.content_framelayout, billCheckoutFragment,
				getString(R.string.billfragment_tag));
		if(bAddToBack)
			ft.addToBackStack(getString(R.string.billfragment_tag));
		ft.commit();
		try {
			getFragmentManager().executePendingTransactions();
		} catch (Exception e) {
			e.printStackTrace();
		}
		billCheckoutFragment.updateBillTextViews(this.getApplicationContext());
	}

	private void showBillCheckout(int shoppingCartId, final FRAGMENT_TYPE fragment) {
		boolean bAddToBack = true;
		if(billCheckoutFragment != null && billCheckoutFragment.isAdded()) {
			if(billCheckoutFragment.getShoppingCart() != null) {
				if(billCheckoutFragment.getShoppingCart().getShoppingCartId() == shoppingCartId)
					return;
				else
					bAddToBack = false;
			}
		}
		navigateBillCheckout(shoppingCartId, bAddToBack);
		switch(fragment) {
			case FRAGMENT_CHECKOUT:
				billCheckoutFragment.showCheckout();
				break;
			case FRAGMENT_OFFERS:
				billCheckoutFragment.showOffers(this.getApplicationContext());
				break;
			case FRAGMENT_SEARCH:
				billCheckoutFragment.showSearchProducts(this.getApplicationContext());
				break;
			case FRAGMENT_QUICKADD:
				billCheckoutFragment.showQuickAdd(this.getApplicationContext());
				break;
		}
	}

	private void onOpenCart(int shoppingCartId, FRAGMENT_TYPE fragmentType) {
		Integer scannerId = null;
		if(activeShoppingCartMap.size() > 0)
			scannerId = activeShoppingCartMap.keyAt(0);
		activateCartWithConfirmation(scannerId, shoppingCartId, true, SnapSharedUtils.isCartActiveOnTouch(this), fragmentType);
	}
	
	@Override
	public void onOpenCart(int shoppingCartId) {
		onOpenCart(shoppingCartId, FRAGMENT_TYPE.FRAGMENT_CHECKOUT);
	}

	@Override
	public void onShowOffers(int shoppingCartId) {
		onOpenCart(shoppingCartId, FRAGMENT_TYPE.FRAGMENT_OFFERS);
	}

	@Override
	public void onShowQuickAdd(int shoppingCartId) {
		onOpenCart(shoppingCartId, FRAGMENT_TYPE.FRAGMENT_QUICKADD);
	}

	@Override
	public void onShowSearchProducts(int shoppingCartId) {
		onOpenCart(shoppingCartId, FRAGMENT_TYPE.FRAGMENT_SEARCH);
	}

	@Override
	public void onActionbarNavigation(String tag, int menuItemId) {
		SnapCommonUtils.hideSoftKeyboard(this.getApplicationContext(), findViewById(R.id.content_framelayout).getWindowToken());
		if (menuItemId == android.R.id.home) {
			if (getString(R.string.billfragment_tag) != tag
				    || (billCheckoutFragment != null && billCheckoutFragment.isAdded())) {
				onBackPressed();
			}
		} else if (menuItemId == R.id.billhistory_menuitem) {
			GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(this, menuItemId, TAG);
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			BillingHistoryFragment billingHistoryFragment = new BillingHistoryFragment();
			billingHistoryFragment.setSelectedCustomerNumber(tag);
			ft.replace(R.id.content_framelayout, billingHistoryFragment,
					getString(R.string.billhistoryfragment_tag));
			ft.addToBackStack(getString(R.string.billhistoryfragment_tag));
			ft.commit();
			getFragmentManager().executePendingTransactions();
		} else if (menuItemId == R.id.dualdisplay_menuitem) {
			getFragmentManager().popBackStack(null,
					FragmentManager.POP_BACK_STACK_INCLUSIVE);
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			if (billingMonitorFragment == null)
				billingMonitorFragment = new BillingMonitorFragment();
			ft.replace(R.id.content_framelayout, billingMonitorFragment,
					getString(R.string.monitorfragment_tag));
			ft.commit();
		} else if (menuItemId == R.id.customer_payment_menuitem) {
			GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(this, menuItemId, TAG);
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			CustomerPaymentFragment customerPaymentFragment = new CustomerPaymentFragment();
			ft.replace(R.id.content_framelayout, customerPaymentFragment,
					getString(R.string.customerpaymentfragment_tag));
			ft.addToBackStack(getString(R.string.customerpaymentfragment_tag));
			ft.commit();
		} else if (R.id.vat_summary_menuitem == menuItemId) {
			GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(this, menuItemId, TAG);
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			VatSummaryFragment vatSummaryFragment = new VatSummaryFragment();
			ft.replace(R.id.content_framelayout, vatSummaryFragment,
					getString(R.string.vat_summary_fragment_tag));
			ft.addToBackStack(getString(R.string.vat_summary_fragment_tag));
			ft.commit();
		} else if (menuItemId == R.id.sync_summary_menuitem) {
			GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(this, menuItemId, TAG);
			Log.d("size=", "result size--2-productPending-->0123");
			new PendingSyncTask(this, this, GET_PENDING_TASKCODE).execute();
			Log.d("size=", "result size--2-productPending-->123");
			Log.d("billItemList", "billItemList size----- ");
		} else if (menuItemId == R.id.campaign_menuitem) {
			GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(this, menuItemId, TAG);
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			OfferAndStoreFragment offerAndStoreFragment = new OfferAndStoreFragment();
			ft.replace(R.id.content_framelayout, offerAndStoreFragment,
					getString(R.string.updatefragment_tag));
			ft.addToBackStack(offerAndStoreFragment.getTag());
			ft.commit();
		} else if (menuItemId == R.id.my_screen_menuitem) {

			GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(this, menuItemId, TAG);
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			TemplateFragment templateFragment = new TemplateFragment();
			ft.replace(R.id.content_framelayout, templateFragment,
					getString(R.string.template_one_tag));
			ft.addToBackStack(templateFragment.getTag());
			ft.commit();
		}  else if (menuItemId == R.id.inventory_update) {

			GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(this, menuItemId, TAG);
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			InventoryFragment inventoryFragment = new InventoryFragment();
			ft.replace(R.id.content_framelayout, inventoryFragment,
					getString(R.string.template_one_tag));
			ft.addToBackStack(inventoryFragment.getTag());
			ft.commit();
		} else if (menuItemId == R.id.new_settings_fragment) {
			try {
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				SettingsFragment newSettingsFragment = new SettingsFragment();
				ft.replace(R.id.content_framelayout, newSettingsFragment,
						getString(R.string.settings));
				ft.addToBackStack(getString(R.string.settings));
				ft.commit();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public boolean onExternalKeyDown(View view, int keyCode, KeyEvent event) {
		return onKeyDown(keyCode, event);
	}

	@Override
	public void onBackPressed() {
		boolean showExit = false;
		if(billingMonitorFragment != null && billingMonitorFragment.isVisible())
			showExit = true;
		if(N_SHOPPING_CARTS == 1 && billCheckoutFragment != null && billCheckoutFragment.isVisible())
			showExit = true;
		if (billCheckoutFragment != null && billCheckoutFragment.isVisible()
				&& billCheckoutFragment.isQuickAddProductsLayoutVisibile()) {
			billCheckoutFragment.showQuickAddProductCategory();
		} else if (showExit) {
			SnapCommonUtils.showAlert(BillingActivity.this, "",
					getString(R.string.exit_message), positiveClickListener,
					negativeClickListener, false);
		} else if (helpVideoFragment != null
				&& helpVideoFragment.isVideoPlaying()) {
			helpVideoFragment.dismissVideoListener.onClick(null);
		} else {
			try {
				if (billingMonitorFragment != null 
						&& billingMonitorFragment.getBillingMonitorAdapter() != null) {
					if(activeShoppingCartMap.size() > 0)
						billingMonitorFragment.getBillingMonitorAdapter().setActiveCart(activeShoppingCartMap.get(activeShoppingCartMap.keyAt(0)));
					else
						billingMonitorFragment.getBillingMonitorAdapter().setActiveCart(activeShoppingCartId);
				}
				super.onBackPressed();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	OnClickListener positiveClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(BillingActivity.this, v, TAG);
			SnapCommonUtils.dismissAlert();
			finish();
			SnapCommonUtils.forceCloseApp(BillingActivity.this);
		}
	};

	OnClickListener negativeClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(BillingActivity.this, v, TAG);
			SnapCommonUtils.dismissAlert();
		}
	};

	@Override
	public void onAddQuickAddClicked(ProductCategory productCategory) {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		addEditProductFragment = new AddEditProductFragment();
		addEditProductFragment.setIsQuickAddProduct(true);
		addEditProductFragment.setProductCategory(productCategory);
		addEditProductFragment.setEditProduct(null);
		ft.replace(R.id.content_framelayout, addEditProductFragment);
		ft.addToBackStack(getString(R.string.addeditfragment_tag));
		ft.commit();
	}

	@Override
	public void onAddProductSuccess(InventorySku newProd, boolean isFromXtraProducts) {
		onBackPressed();
		if (isFromXtraProducts && newProd.getProductSku().getProductSubCategoryName() != null)
			CustomToast.showCustomToast(BillingActivity.this, 
			getString(R.string.add_existing_product_into_quickadd_msg) + " " +
			getSubCategoryName(newProd.getProductSku().getProductSubCategoryName()), 
			Toast.LENGTH_SHORT, CustomToast.SUCCESS);
		billCheckoutFragment.addQuickAddProduct(newProd.getProductSku());
	}

	@Override
	public void onEditQuickAddClicked(ProductSku productSku) {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		addEditProductFragment = new AddEditProductFragment();
		addEditProductFragment.setIsQuickAddProduct(true);
		addEditProductFragment.setProductCategory(productSku
				.getProductCategory());
		addEditProductFragment.setEditProduct(productSku,
				this.getApplicationContext());
		ft.replace(R.id.content_framelayout, addEditProductFragment);
		ft.addToBackStack(getString(R.string.addeditfragment_tag));
		ft.commit();
	}

	@Override
	public void onRecreateADapter() {
		if (shoppingCartList != null && shoppingCartList.size() > 0) {
			for (int i = 0; i < shoppingCartList.size(); i++) {
				billingMonitorFragment.onAddCart(shoppingCartList.get(i), i,
						this, this);
			}
		}
	}

	@Override
	public void showAddProductLayout(String barcode) {

	}

	@Override
	public void showAddProductLayout(InventorySku inventorySku) {

	}

	@Override
	public void showAddProductLayout(String barcode, Distributor distributor) {

	}
	
	private void checkVersionCode(int skip) {
		String versionDetails = SnapSharedUtils.getVersionCodeDetails(this);
		if (versionDetails != null) {
			Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.TRANSIENT).create();
			Type versionListType = new TypeToken<List<ApkData.AppVersionCode>>() {}.getType();
			List<ApkData.AppVersionCode> versionCodes = gson.fromJson(versionDetails, versionListType);

			final String[] apkPackages = SnapBillingConstants.APK_PACKAGES;
			
			final String[] appNames = new String[] { getString(R.string.billing_app),
					getString(R.string.inventory_app), getString(R.string.dashboard_app), 
					getString(R.string.pushoffers_app), getString(R.string.visibility_app) };
			if (versionCodes == null)
				return;
			for (int i = skip; i < versionCodes.size(); i++) {
				if (versionCodes.get(i).upgrade_before != null) {
					int proposedVesionCode = versionCodes.get(i).proposed_version_code;
					String upgradeBefore = versionCodes.get(i).upgrade_before;
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							SnapBillingConstants.APK_VERSION_DATEFORMAT, Locale.US);
					Date upgradeDate;
					try {
						upgradeDate = dateFormat.parse(upgradeBefore);
						
						if (versionCodes.get(i).package_name.equals(apkPackages[i])) {
							checkEachAppVersion(upgradeDate, appNames[i], i,
									proposedVesionCode, versionCodes, apkPackages[i]);
							return;
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
			SnapSharedUtils.setApkUpgradeDialogLaunchDate(getApplicationContext(),
							new SimpleDateFormat(SnapBillingConstants.DAY_DATEFORMAT, Locale.US).format(new Date()));
		}
	}

	public void checkEachAppVersion(Date upgradeDate, String appName, int pos,
			int proposedVesionCode, List<ApkData.AppVersionCode> versionCodes, String appPackageName) {
		Date currentDate = new Date();
		int current_version = SnapCommonUtils.getCurrentVersionCode(this, appPackageName);
		if (current_version < proposedVesionCode) {			
			String launchDate = SnapSharedUtils.getApkUpgradeDialogLaunchDate(getApplicationContext());
			if (currentDate.before(upgradeDate) && getDaysDifference(launchDate) != 0) {
				showUpdateApkAlert(appName, getResources().getString(R.string.upgrade_version_msg,
						appName), true, proposedVesionCode, appPackageName, pos);
				return;
			} else if (currentDate.after(upgradeDate)) {
				showUpdateApkAlert(appName, getResources().getString(R.string.mandatory_upgrade_version_msg,
						appName), false, proposedVesionCode, appPackageName, pos);
				return;
			}
		}
		checkVersionCode(pos + 1);
	}
	
	public boolean checkNotCorruptApkFile(File apkFile) {
		boolean corruptedApkFile = true;
		try {
			new JarFile(apkFile);
		} catch (Exception e) {
			corruptedApkFile = false;
		}
		return corruptedApkFile;
	}

	public void showUpdateApkAlert(final String title, String msg,
			boolean isCancelable, final int proposedCode, final String appPackageName, final int pos) {
		try {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BillingActivity.this);
			alertDialogBuilder.setTitle(title);
			alertDialogBuilder
					.setMessage(msg)
					.setCancelable(false)
					.setPositiveButton(
							getResources().getString(R.string.update_now),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();

									String packageNameSplit[] = appPackageName.split("\\.");
									String apkPathBasepath = SnapToolkitConstants.UPGRADE_EXTPATH;
									String apkPath = apkPathBasepath + packageNameSplit[packageNameSplit.length-1]
																	 + "_" + proposedCode + ".apk";
									File apkFile = new File(apkPath);
									boolean retry = false;

									if (apkFile.exists() && checkNotCorruptApkFile(apkFile)) {
										Intent intent = new Intent(Intent.ACTION_VIEW);
										intent.setDataAndType(Uri.fromFile(apkFile),
												SnapBillingConstants.APK_MIME_TYME);
										intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
										BillingActivity.this.startActivity(intent);
										// If we are trying to upgrade ourselves, force-close
										if(appPackageName.equalsIgnoreCase(getApplicationContext().getPackageName()))
											SnapBillingUtils.forceCloseApp(BillingActivity.this);
									} else {
										try {											
											startActivity(new Intent(Intent.ACTION_VIEW,
													Uri.parse(SnapBillingConstants.GOOGLE_MARKET_LINK
															+ appPackageName)));
										} catch (android.content.ActivityNotFoundException anfe) {
											retry = true;
										}
										
										if (retry)
											startActivity(new Intent(Intent.ACTION_VIEW,
													Uri.parse(SnapBillingConstants.GOOGLE_PLAYSTORE_LINK
															+ appPackageName)));
									}
								}
					});
			if (isCancelable) {
				alertDialogBuilder.setNegativeButton(
						getResources().getString(R.string.update_later),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								checkVersionCode(pos+1);
							}
						});
				
			}
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int getDaysDifference(String launchDate) {
		Date currentDate = new Date();
		try {			
		    Date launchDateObj = new SimpleDateFormat(
					SnapBillingConstants.DAY_DATEFORMAT, Locale.US).parse(launchDate);
		    long diff = currentDate.getTime() - launchDateObj.getTime();
		    return (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
		} catch (ParseException e) {
		    e.printStackTrace();
		}
		return -1;
	}
	
	private String getSubCategoryName(String subCategoryName) {
		for (ProductCategory category : quickAddCategories) {
			if (category.getCategoryName().equalsIgnoreCase(subCategoryName))
				return subCategoryName;
		}
		return getString(R.string.xtra_products);
	}
	
	private void updateStatusView(final int viewId, final int imageId) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ImageView statusView = ((ImageView)findViewById(viewId));
				if(statusView != null)
					statusView.setImageResource(imageId);
			}
		});		
	}
	
	public void setWeighingMachineStatus(boolean bConnected) {
		updateStatusView(R.id.weighing_machine_connected, bConnected ? R.drawable.weighing_scale_connected :
			 														   R.drawable.weighing_scale_not_connected);
	}
	
	public void setPrinterStatus(boolean bConnected) {
		updateStatusView(R.id.printer_connected, bConnected ? R.drawable.printer_connected :
			   												  R.drawable.printer_not_connected);
	}
	
	// -----------------------------------------------------------------------------------------------------
	
	/**
	 * V2 Application initialization.
	 */
	private void initializeApplication() {
		// Initialize global.db
		GlobalDB.getInstance(getApplicationContext(), false);

		// Initialize snapbizz.db
		SnapbizzDB.getInstance(getApplicationContext(), false);
	}
}
