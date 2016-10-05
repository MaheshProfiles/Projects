package com.snapbizz.snapstock;

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

import android.app.ActionBar;
import android.app.DialogFragment;
import android.app.FragmentManager.BackStackEntry;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.snapbizz.snapstock.asynctasks.AddScannedProductToInventoryTask;
import com.snapbizz.snapstock.fragments.DashboardFragment;
import com.snapbizz.snapstock.fragments.DistributorListFragment;
import com.snapbizz.snapstock.fragments.DistributorListFragment.DistributorSelectListener;
import com.snapbizz.snapstock.fragments.DistributorListFragment.NewDistributorListener;
import com.snapbizz.snapstock.fragments.DistributorPaymentFragment;
import com.snapbizz.snapstock.fragments.InventoryMainMenuFragment;
import com.snapbizz.snapstock.fragments.InventoryMainMenuFragment.MenuOptionSelectListener;
import com.snapbizz.snapstock.fragments.InventoryOrderFragment;
import com.snapbizz.snapstock.fragments.InventoryReceiveFragment;
import com.snapbizz.snapstock.fragments.InventoryUpdateFragment;
import com.snapbizz.snapstock.fragments.NewDistributorFragment;
import com.snapbizz.snapstock.fragments.NewDistributorFragment.onNewDistributorAddedListener;
import com.snapbizz.snapstock.interfaces.OnAddNewProductListener;
import com.snapbizz.snapstock.utils.SnapInventoryConstants;
import com.snapbizz.snapstock.utils.SnapInventoryUtils;
import com.snapbizz.snapstock.utils.ViewServer;
import com.snapbizz.snaptoolkit.activity.SnapActivity;
import com.snapbizz.snaptoolkit.asynctasks.PendingSyncTask;
import com.snapbizz.snaptoolkit.customviews.SoftInputEditText.OnExternalKeyInputListener;
import com.snapbizz.snaptoolkit.domains.Distributor;
import com.snapbizz.snaptoolkit.domains.InventorySku;
import com.snapbizz.snaptoolkit.domains.ResponseContainer;
import com.snapbizz.snaptoolkit.fragments.AddEditProductFragment;
import com.snapbizz.snaptoolkit.fragments.AddEditProductFragment.OnAddProductSuccess;
import com.snapbizz.snaptoolkit.fragments.HelpVideosFragment;
import com.snapbizz.snaptoolkit.fragments.SyncSummaryFragment;
import com.snapbizz.snaptoolkit.interfaces.OnActionbarNavigationListener;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.interfaces.OnServiceCompleteListener;
import com.snapbizz.snaptoolkit.utils.CustomToast;
import com.snapbizz.snaptoolkit.utils.RequestCodes;
import com.snapbizz.snaptoolkit.utils.SkuUnitType;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class InventoryActivity extends SnapActivity implements
		OnQueryCompleteListener, DistributorSelectListener,
		MenuOptionSelectListener, NewDistributorListener,
		OnActionbarNavigationListener, OnExternalKeyInputListener,
		onNewDistributorAddedListener, OnAddNewProductListener, OnAddProductSuccess {

	private final String TAG = InventoryActivity.class.getName();
	private final int BARCODE_DELIMITER_KEYCODE = 61;
	private final int GET_SCANNEDSKU_TASKCODE = 1;
	private final int GET_PENDING_TASKCODE=78;
	private final int GET_BRAND_BY_COMPANY_TASKCODE = 12;
	private final int GET_COMPANY_BY_BRAND_TASKCODE = 13;
	private final int ADD_INVENTORY_NEW_PROD_TASKCODE = 7;

	private String barCode = "";
	private InventoryMainMenuFragment inventoryMainMenuFragment;
	private InventoryUpdateFragment inventoryUpdateFragment;
	private DistributorListFragment inventoryOrderFragment;
	private NewDistributorFragment newDistributorFragment;
	private AddEditProductFragment addEditProductFragment;
	private InventoryOrderFragment distributorOrderFragment;
	private InventoryReceiveFragment orderReceiveFragment;
	private HelpVideosFragment helpVideoFragment;

	private DashboardFragment dashboardFragment;
	private boolean isToOrder;

	private InventorySku editInventorySku;
	private boolean isEditProd;
	
	private LinkedBlockingQueue<ScannedKeyInput> keyQueue = new LinkedBlockingQueue<ScannedKeyInput>();
	
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
		setContentView(R.layout.activity_inventory);
		inventoryMainMenuFragment = new InventoryMainMenuFragment();
		getActionBar().setHomeButtonEnabled(true);
		ActionBar actionBar = getActionBar();
		actionBar.setIcon(R.drawable.icon_back_normal);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setCustomView(R.layout.actionbar_layout);
		setContentView(R.layout.activity_inventory);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.add(R.id.content_framelayout, inventoryMainMenuFragment,
				getString(R.string.mainmenufragment_tag));
		ft.commit();
		ViewServer.get(this).addWindow(this);

		String showDashboardIntentExtra = getIntent().getStringExtra(
				SnapInventoryConstants.INTENT_EXTRA_KEY_SHOW_DASHBOARD);
		if (showDashboardIntentExtra != null
				&& showDashboardIntentExtra
						.equals(SnapInventoryConstants.INTENT_EXTRA_SHOW_DASHBOARD)) {
		}
		// Create thread for scan processing
		startScanThread();
	}
	
	private void startScanThread() {
		Thread thread = new Thread() {
			@Override
		    public void run() {
		        try {
		        	String barCode = null;
		        	while(true)  {
			        	final ScannedKeyInput input = keyQueue.take();
			        	final int deviceId = input.event.getDevice().getId();
			        	final int keyCode = input.keyCode;
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
								barCode = null;
								continue;
							}
							
							try {
								long bc = Long.parseLong(barCode);
								barCode = String.valueOf(bc);
							} catch(Exception e) { }
							Log.d(TAG, " formatted code " + barCode);
							final String scannedBarcode = barCode;
							InventoryActivity.this.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									new AddScannedProductToInventoryTask(InventoryActivity.this, InventoryActivity.this,
											GET_SCANNEDSKU_TASKCODE).execute(scannedBarcode);
								}
							});
							barCode = null;
						} else if (keyCode != KeyEvent.KEYCODE_SHIFT_LEFT) {
							int ch = input.event.getUnicodeChar();
							if( /*ch != 0 */ ch >=32 && ch < 127)
								barCode += ((char) ch);
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
	
	@Override
	protected void onResume() {
		SnapToolkitConstants.IS_REPORT = "0";
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		menu.findItem(R.id.search_meuitem).setVisible(true);
		return true;
	}

	@Override
	protected void onStop() {
		super.onStop();
		SnapInventoryUtils.closeDatabaseHelper();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
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

	public boolean isAddNewProductShowing() {
		return findViewById(R.id.add_new_product_layout).getVisibility() == View.VISIBLE ? true : false;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem buildVersionMenuItem = menu.findItem(R.id.build_version);
		if (buildVersionMenuItem != null)
			buildVersionMenuItem.setTitle(SnapCommonUtils.getCurrentVersion(this));
		return true;
	}

	@Override
	public void onBackPressed() {

		if (inventoryUpdateFragment != null
				&& inventoryUpdateFragment.isVisible()
				&& inventoryUpdateFragment.isBatchesShowing()) {
			
			inventoryUpdateFragment.hidebatches();
		} else if (inventoryUpdateFragment != null
				&& inventoryUpdateFragment.isVisible()
				&& inventoryUpdateFragment.isKeyPadVisible()) {
			inventoryUpdateFragment.dismissNumpad();
		} else if (inventoryUpdateFragment != null
				&& inventoryUpdateFragment.isVisible()
				&& inventoryUpdateFragment.isUpdateListPresent()) {
			inventoryUpdateFragment.updateInventoryProducts();
		} else if (inventoryMainMenuFragment != null
				&& inventoryMainMenuFragment.isVisible()) {
			SnapCommonUtils.showAlert(InventoryActivity.this, "",
					getString(R.string.exit_app), positiveClickListener,
					negativeClickListener, false);
		} else if (null != distributorOrderFragment
				&& distributorOrderFragment.isVisible()
				&& distributorOrderFragment.isKeypadVisibile()) {
			distributorOrderFragment.popKeypadFragment();
		} else if (null != orderReceiveFragment
				&& orderReceiveFragment.isVisible()
				&& orderReceiveFragment.isKeypadVisible()) {
			orderReceiveFragment.popKeypadFragment();
		} else if (null != distributorOrderFragment
				&& distributorOrderFragment.isVisible()
				&& distributorOrderFragment.isFilterFragmentVisible()) {
			distributorOrderFragment.hideFilterFragment();
		} else if (null != orderReceiveFragment
				&& orderReceiveFragment.isVisible()
				&& orderReceiveFragment.isFilterFragmentVisible()) {
			orderReceiveFragment.hideFilterFragment();
		} else if (null != inventoryUpdateFragment
				&& inventoryUpdateFragment.isVisible()
				&& inventoryUpdateFragment.isFilterFragmentVisible()) {
			inventoryUpdateFragment.popFilterFragmentandResetButtonStates();
		} else if (helpVideoFragment != null
				&& helpVideoFragment.isVideoPlaying()) {
			helpVideoFragment.dismissVideoListener.onClick(null);
		} else if (null != orderReceiveFragment
				&& orderReceiveFragment.isVisible()) {
			SnapCommonUtils.showAlert(InventoryActivity.this, "",
					getString(R.string.exit_receive), positiveButtonClicked,
					negativeButtonClicked, false);
		} else if (null != distributorOrderFragment
				&& distributorOrderFragment.isVisible()) {
			SnapCommonUtils.showAlert(InventoryActivity.this, "",
					getString(R.string.exit_order), positiveButtonClicked,
					negativeButtonClicked, false);
		} else {
			super.onBackPressed();
		}
	    
	}

	OnClickListener positiveClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			finish();
			SnapCommonUtils.dismissAlert();
		}
	};

	OnClickListener negativeClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			SnapCommonUtils.dismissAlert();
		}
	};

	OnClickListener positiveButtonClicked = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			SnapCommonUtils.dismissAlert();
			getFragmentManager().popBackStack();
		}
	};

	OnClickListener negativeButtonClicked = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			SnapCommonUtils.dismissAlert();
		}
	};

	@Override
	public void onTaskSuccess(Object responseList, int taskCode) {
		// TODO Auto-generated method stub
		if (taskCode == GET_SCANNEDSKU_TASKCODE
				&& inventoryUpdateFragment != null
				&& inventoryUpdateFragment.isAdded()) {
			inventoryUpdateFragment
					.addProductToInventory((InventorySku) responseList);
		} else if (taskCode == GET_SCANNEDSKU_TASKCODE
				&& orderReceiveFragment != null
				&& orderReceiveFragment.isAdded()) {
			orderReceiveFragment
					.receiveScannedProduct((InventorySku) responseList);
		} else if (taskCode == GET_SCANNEDSKU_TASKCODE
				&& null != distributorOrderFragment
				&& distributorOrderFragment.isAdded()) {
			distributorOrderFragment
					.orderScannedProduct((InventorySku) responseList);
		}
		else if(taskCode == GET_PENDING_TASKCODE){
        	Log.d("pending task", "started pending");
        	HashMap<String, Integer> resultMap=(HashMap<String, Integer>) responseList;
        	SnapCommonUtils.setResultMap(resultMap);
        	FragmentTransaction ft = getFragmentManager().beginTransaction();
            DialogFragment newFragment = new SyncSummaryFragment();
            newFragment.show(ft, "dialog");
            Log.d("pending task", "finished pending");
        }
		
	}

	public void hideSoftKeyboard() {
		if (getCurrentFocus() != null) {
			SnapCommonUtils.hideSoftKeyboard(getApplicationContext(), getCurrentFocus().getWindowToken());
		}
	}

	@Override
	public void onTaskError(String errorMessage, int taskCode) {
		// TODO Auto-generated method stub
		if (taskCode == GET_SCANNEDSKU_TASKCODE) {
			if (null != distributorOrderFragment
					&& distributorOrderFragment.isVisible()
					&& distributorOrderFragment.isFilterFragmentVisible()) {
				distributorOrderFragment.hideFilterFragment();
			} else if (null != orderReceiveFragment
					&& orderReceiveFragment.isVisible()
					&& orderReceiveFragment.isFilterFragmentVisible()) {
				orderReceiveFragment.hideFilterFragment();
			} else if (null != inventoryUpdateFragment
					&& inventoryUpdateFragment.isVisible()
					&& inventoryUpdateFragment.isFilterFragmentVisible()) {
				inventoryUpdateFragment.popFilterFragmentandResetButtonStates();
			}
			showAddProductLayout(errorMessage);
			/*
			 * new QueryProductCategories(this, this,
			 * GET_CATEGORIES_TASKCODE).execute();
			 */
			
			 /* addEditProductFragment= new AddEditProductFragment();
			  FragmentTransaction ft = getFragmentManager().beginTransaction();
			  ft.replace(R.id.content_framelayout, addEditProductFragment,
			  getString(R.string.addEditProductfragment_tag));
			  ft.addToBackStack(addEditProductFragment.getTag()); ft.commit();*/
			 
		} else if (taskCode == ADD_INVENTORY_NEW_PROD_TASKCODE) {
			hideSoftKeyboard();
			findViewById(R.id.add_new_product_layout).setVisibility(View.GONE);
			CustomToast.showCustomToast(getApplicationContext(), "add inv prod"
					+ errorMessage, Toast.LENGTH_SHORT, CustomToast.ERROR);
		} else if (taskCode == GET_BRAND_BY_COMPANY_TASKCODE) {
			findViewById(R.id.brands_listview).setVisibility(View.GONE);
		} else if (taskCode == GET_COMPANY_BY_BRAND_TASKCODE) {
			findViewById(R.id.companies_listview).setVisibility(View.GONE);
		} else {
			CustomToast.showCustomToast(getApplicationContext(), errorMessage,
					Toast.LENGTH_SHORT, CustomToast.ERROR);
		}
	}
	
	@Override
	public void onMenuOptionSelectListener(View view) {
		// TODO Auto-generated method stub
		if (view.getId() == R.id.order_button) {
			isToOrder = true;
			if (inventoryOrderFragment == null)
				inventoryOrderFragment = new DistributorListFragment();
			inventoryOrderFragment.setIsToOrder(isToOrder);
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.content_framelayout, inventoryOrderFragment,
					getString(R.string.order));
			ft.addToBackStack(inventoryOrderFragment.getTag());
			ft.commit();
			getFragmentManager().executePendingTransactions();
			
		} else if (view.getId() == R.id.receive_button) {
			isToOrder = false;
			if (inventoryOrderFragment == null)
				inventoryOrderFragment = new DistributorListFragment();
			inventoryOrderFragment.setIsToOrder(isToOrder);
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.content_framelayout, inventoryOrderFragment,
					getString(R.string.order));
			ft.addToBackStack(inventoryOrderFragment.getTag());
			ft.commit();
			getFragmentManager().executePendingTransactions();
		} else if (view.getId() == R.id.update_button) {
			if (inventoryUpdateFragment == null)
				inventoryUpdateFragment = new InventoryUpdateFragment();
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.content_framelayout, inventoryUpdateFragment,
					getString(R.string.updatefragment_tag));
			ft.addToBackStack(inventoryUpdateFragment.getTag());
			ft.commit();
			getFragmentManager().executePendingTransactions();
		}
	}

	@Override
	public void onNewDistributorClick() {
		// TODO Auto-generated method stub
		if (newDistributorFragment == null)
			newDistributorFragment = new NewDistributorFragment();
		else if (newDistributorFragment.getEditDistributor() != null)
			newDistributorFragment = new NewDistributorFragment();

		newDistributorFragment.setEditDistributor(null);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.content_framelayout, newDistributorFragment,
				getString(R.string.newdistributorfragment_tag));
		ft.addToBackStack(newDistributorFragment.getTag());
		ft.commit();
	}

	@Override
	public void onActionbarNavigation(String tag, int menuItemId) {
		// TODO Auto-generated method stub
		SnapCommonUtils.hideSoftKeyboard(getApplicationContext(), findViewById(R.id.content_framelayout).getWindowToken());
		if (menuItemId == android.R.id.home
				&& !tag.equalsIgnoreCase(SnapInventoryConstants.CONFIRM_BUTTON_CLICKED)) {
			onBackPressed();
		} else if (menuItemId == R.id.dashboard_menuitem) {
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			if (inventoryUpdateFragment != null
					&& inventoryUpdateFragment.isVisible()
					&& inventoryUpdateFragment.isUpdateListPresent()) {
				inventoryUpdateFragment.updateInventoryProducts();
			} else {
				if (null != distributorOrderFragment
						&& distributorOrderFragment.isVisible()
						&& distributorOrderFragment.isKeypadVisibile()) {
					distributorOrderFragment.popKeypadFragment();
				} else if (null != orderReceiveFragment
						&& orderReceiveFragment.isVisible()
						&& orderReceiveFragment.isKeypadVisible()) {
					orderReceiveFragment.popKeypadFragment();
				} else if (null != inventoryUpdateFragment
						&& inventoryUpdateFragment.isVisible()
						&& inventoryUpdateFragment.isKeyPadVisible()) {
					inventoryUpdateFragment.dismissNumpad();
				}
				if (null != distributorOrderFragment
						&& distributorOrderFragment.isVisible()
						&& distributorOrderFragment.isFilterFragmentVisible()) {
					distributorOrderFragment.hideFilterFragment();
				} else if (null != orderReceiveFragment
						&& orderReceiveFragment.isVisible()
						&& orderReceiveFragment.isFilterFragmentVisible()) {
					orderReceiveFragment.hideFilterFragment();
				} else if (null != inventoryUpdateFragment
						&& inventoryUpdateFragment.isVisible()
						&& inventoryUpdateFragment.isFilterVisible()) {
					inventoryUpdateFragment
							.popFilterFragmentandResetButtonStates();
				}
				 
				// if (dashboardFragment == null)
				dashboardFragment = new DashboardFragment();
				ft.replace(R.id.content_framelayout, dashboardFragment,
						getString(R.string.dashboardfragment_tag));
				ft.addToBackStack(getString(R.string.dashboardfragment_tag));
				ft.commit();
			}
		}
		else if(menuItemId == R.id.distributor_payment_menuitem) {
			if (null != distributorOrderFragment
					&& distributorOrderFragment.isVisible()
					&& distributorOrderFragment.isKeypadVisibile()) {
				distributorOrderFragment.popKeypadFragment();
			} else if (null != orderReceiveFragment
					&& orderReceiveFragment.isVisible()
					&& orderReceiveFragment.isKeypadVisible()) {
				orderReceiveFragment.popKeypadFragment();
			} else if (null != inventoryUpdateFragment
					&& inventoryUpdateFragment.isVisible()
					&& inventoryUpdateFragment.isKeyPadVisible()) {
				inventoryUpdateFragment.dismissNumpad();
			}
			if (null != distributorOrderFragment
					&& distributorOrderFragment.isVisible()
					&& distributorOrderFragment.isFilterFragmentVisible()) {
				distributorOrderFragment.hideFilterFragment();
			} else if (null != orderReceiveFragment
					&& orderReceiveFragment.isVisible()
					&& orderReceiveFragment.isFilterFragmentVisible()) {
				orderReceiveFragment.hideFilterFragment();
			} else if (null != inventoryUpdateFragment
					&& inventoryUpdateFragment.isVisible()
					&& inventoryUpdateFragment.isFilterVisible()) {
				inventoryUpdateFragment.popFilterFragmentandResetButtonStates();
			}
	         FragmentTransaction ft = getFragmentManager().beginTransaction();
	         DistributorPaymentFragment distributorPaymentFragment = new DistributorPaymentFragment();
	         ft.replace(R.id.content_framelayout, distributorPaymentFragment,getString(R.string.distributorpaymentfragment_tag));
	         ft.addToBackStack(getString(R.string.distributorpaymentfragment_tag));
	         ft.commit();
	     }
		
		/*else if (menuItemId == R.id.help_videos_menuitem) {
			if (null != distributorOrderFragment
					&& distributorOrderFragment.isVisible()
					&& distributorOrderFragment.isKeypadVisibile()) {
				distributorOrderFragment.popKeypadFragment();
			} else if (null != orderReceiveFragment
					&& orderReceiveFragment.isVisible()
					&& orderReceiveFragment.isKeypadVisible()) {
				orderReceiveFragment.popKeypadFragment();
			} else if (null != inventoryUpdateFragment
					&& inventoryUpdateFragment.isVisible()
					&& inventoryUpdateFragment.isKeyPadVisible()) {
				inventoryUpdateFragment.dismissNumpad();
			}
			if (null != distributorOrderFragment
					&& distributorOrderFragment.isVisible()
					&& distributorOrderFragment.isFilterFragmentVisible()) {
				distributorOrderFragment.hideFilterFragment();
			} else if (null != orderReceiveFragment
					&& orderReceiveFragment.isVisible()
					&& orderReceiveFragment.isFilterFragmentVisible()) {
				orderReceiveFragment.hideFilterFragment();
			} else if (null != inventoryUpdateFragment
					&& inventoryUpdateFragment.isVisible()
					&& inventoryUpdateFragment.isFilterVisible()) {
				inventoryUpdateFragment.popFilterFragmentandResetButtonStates();
			}
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			helpVideoFragment = new InventoryHelpVideoFragment();
			helpVideoFragment
					.setFragmentType(HelpVideosFragment.FRAGMENT_TYPE_INVENTORY);
			ft.replace(R.id.content_framelayout, helpVideoFragment, "HelpVideo");
			ft.addToBackStack("HelpVideo");
			ft.commit();
			getFragmentManager().executePendingTransactions();
		}*/ else if (menuItemId == android.R.id.home
				&& tag.equalsIgnoreCase(SnapInventoryConstants.CONFIRM_BUTTON_CLICKED)) {
			getFragmentManager().popBackStack();
			getFragmentManager().popBackStack();
		} else if (menuItemId == R.id.sync_summary_menuitem) {
			Log.d("size=", "result size--2-productPending-->0123");
        	new PendingSyncTask(this, this, GET_PENDING_TASKCODE).execute();
//        	Log.d("size=", "result size--2-productPending-->123");
//        	 Log.d("billItemList","billItemList size----- ");
		}
//		else if (menuItemId == R.id.sync_summary_menuitem) {
//			FragmentTransaction ft = getFragmentManager().beginTransaction();
//
//			DialogFragment newFragment = new SyncSummaryFragment();
//			newFragment.show(ft, "dialog");
//		}
	}

	@Override
	public void onDistributorClicked(Distributor distributor, boolean isToOrder) {
		// TODO Auto-generated method stub
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		if (isToOrder) {
			if (null == distributorOrderFragment)
				distributorOrderFragment = new InventoryOrderFragment();
			distributorOrderFragment.setPurchaseOrderDistributorFragmentData(
					distributor, isToOrder);
			ft.replace(R.id.content_framelayout, distributorOrderFragment,
					getString(R.string.distributor_order_fragment));
			ft.addToBackStack(distributorOrderFragment.getTag());
		} else {
			if (null == orderReceiveFragment)
				orderReceiveFragment = new InventoryReceiveFragment();
			orderReceiveFragment.setPurchaseOrderReceiveFragmentData(
					distributor, isToOrder);
			ft.replace(R.id.content_framelayout, orderReceiveFragment,
					getString(R.string.order_receive_fragment_tag));
			ft.addToBackStack(orderReceiveFragment.getTag());
		}
		ft.commit();
	}

	@Override
	public boolean onExternalKeyDown(View view, int keyCode, KeyEvent event) {
		return onKeyDown(keyCode, event);
	}

	@Override
	public void addDistributortoList(Distributor distributor) {
		// TODO Auto-generated method stub
		getFragmentManager().popBackStack();
		if (inventoryOrderFragment != null) {
			inventoryOrderFragment.addDistributortoList(distributor);
		}
	}

	@Override
	public void onEditDistributorList(Distributor distributor) {
		// TODO Auto-generated method stub
		getFragmentManager().popBackStack();
		if (inventoryOrderFragment != null) {
			inventoryOrderFragment.deleteDistributorList(distributor);
			inventoryOrderFragment.addDistributortoList(distributor);
			inventoryOrderFragment.refreshDistributorList();
		}
	}

	@Override
	public void onDeleteDistributor(Distributor distributor) {
		// TODO Auto-generated method stub
		getFragmentManager().popBackStack();
		if (inventoryOrderFragment != null) {
			inventoryOrderFragment.deleteDistributorList(distributor);
		}
	}

	@Override
	public void showAddProductLayout(String barcode) {
		// TODO Auto-generated method stub
		if (addEditProductFragment != null && addEditProductFragment.isAdded()) {
			addEditProductFragment.setProductCode(barcode);
		} else {
			addEditProductFragment = new AddEditProductFragment();
			addEditProductFragment.setProductCodeText(barcode);
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.content_framelayout, addEditProductFragment,
					getString(R.string.addEditProductfragment_tag));
			ft.addToBackStack(addEditProductFragment.getTag());
			ft.commit();
		}
	}

	@Override
	public void showAddProductLayout(String barcode, Distributor distributor) {
		// TODO Auto-generated method stub
		addEditProductFragment = new AddEditProductFragment();
		addEditProductFragment.setProductCodeText(barcode);
		addEditProductFragment.setTaggedDistributor(distributor);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.content_framelayout, addEditProductFragment,
				getString(R.string.addEditProductfragment_tag));
		ft.addToBackStack(addEditProductFragment.getTag());
		ft.commit();
	}

	@Override
	public void showAddProductLayout(InventorySku inventorySku) {
		// TODO Auto-generated method stub
		this.editInventorySku = inventorySku;
		addEditProductFragment = new AddEditProductFragment();
		addEditProductFragment.setEditProduct(editInventorySku);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.content_framelayout, addEditProductFragment,
				getString(R.string.addEditProductfragment_tag));
		ft.addToBackStack(addEditProductFragment.getTag());
		ft.commit();
	}

	@Override
	public void onDistributorEditClicked(Distributor distributor) {
		// TODO Auto-generated method stub
		// if (newDistributorFragment == null)
		newDistributorFragment = new NewDistributorFragment();
		/*
		 * else if(newDistributorFragment.getEditDistributor()==null)
		 * newDistributorFragment = new NewDistributorFragment();
		 */

		newDistributorFragment.setEditDistributor(distributor);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.content_framelayout, newDistributorFragment,
				getString(R.string.newdistributorfragment_tag));
		ft.addToBackStack(newDistributorFragment.getTag());
		ft.commit();
	}

	@Override
	public void onAddProductSuccess(InventorySku newProd, boolean isFromXtraProducts) {
		// TODO Auto-generated method stub

		int backStackEntryCount = getFragmentManager().getBackStackEntryCount();
		backStackEntryCount = backStackEntryCount - 2;
		if (backStackEntryCount >= 0) {
			BackStackEntry backStackEntry = getFragmentManager()
					.getBackStackEntryAt(backStackEntryCount);
			String str = backStackEntry.getName();
			if (null != str) {
				if (null != distributorOrderFragment
						&& getString(R.string.distributor_order_fragment)
								.equalsIgnoreCase(str)) {
					distributorOrderFragment.setNewProductAdded(newProd);
				} else if (null != orderReceiveFragment
						&& getString(R.string.order_receive_fragment_tag)
								.equalsIgnoreCase(str)) {
					orderReceiveFragment.setNewProductAdded(newProd);
				}
			}
		}

		try {
			getFragmentManager().popBackStack();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		 /* if (inventoryUpdateFragment != null)
		  inventoryUpdateFragment.onAddNewProductComplete(newProd, isEditProd);*/
		 
	}
	
}
