package com.snapbizz.snapbilling.fragments;

import java.util.ArrayList;
import java.util.List;

import net.sqlcipher.Cursor;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.adapters.BrandSelectionAdapter.OnBrandEditListener;
import com.snapbizz.snapbilling.adapters.CampaignAdapter;
import com.snapbizz.snapbilling.adapters.InventoryProductsAdapter.OnInventoryActionListener;
import com.snapbizz.snapbilling.adapters.OfferStoreCurserAdapter;
import com.snapbizz.snapbilling.asynctasks.AddProductToInventoryTask;
import com.snapbizz.snapbilling.asynctasks.CampaignTask;
import com.snapbizz.snapbilling.asynctasks.GetBrandGlobalProductsTask;
import com.snapbizz.snapbilling.asynctasks.GetBrandInventoryProductsTask;
import com.snapbizz.snapbilling.asynctasks.GetSelectedInvSkuTask;
import com.snapbizz.snapbilling.asynctasks.RemoveInventoryProductTask;
import com.snapbizz.snapbilling.asynctasks.RemoveStoreOfferTask;
import com.snapbizz.snapbilling.asynctasks.SearchInventoryCursorTask;
import com.snapbizz.snapbilling.asynctasks.UpdateInventoryTask;
import com.snapbizz.snapbilling.fragments.FilterFragment.OnFilterSelectedListener;
import com.snapbizz.snapbilling.interfaces.OnAddNewProductListener;
import com.snapbizz.snapbilling.utils.GoogleAnalyticsTracker;
import com.snapbizz.snapbilling.utils.SnapBillingConstants;
import com.snapbizz.snaptoolkit.domains.Brand;
import com.snapbizz.snaptoolkit.domains.Campaigns;
import com.snapbizz.snaptoolkit.domains.InventoryBatch;
import com.snapbizz.snaptoolkit.domains.InventorySku;
import com.snapbizz.snaptoolkit.domains.ProductCategory;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.fragments.NumKeypadFragment;
import com.snapbizz.snaptoolkit.interfaces.OnActionbarNavigationListener;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.services.SnapVisibilityService;
import com.snapbizz.snaptoolkit.utils.CustomToast;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitTextFormatter;

public class OfferAndStoreFragment extends Fragment implements
		OnQueryCompleteListener, OnInventoryActionListener,
		OnBrandEditListener, OnFilterSelectedListener {
	private final int GET_CAMPAIGN_TASKCODE = 1;
	private final int GET_BRANDS_TASKCODE = 2;
	private final int GET_BRAND_INVENTORY_PRODS_TASKCODE = 4;
	private final int ADD_INVENTORY_PROD_TASKCODE = 5;
	private final int UPDATE_INVENTORY_PRODS_TASKCODE = 6;
	private final int SEARCH_INVENTORY_TASKCODE = 7;
	private final int DELETE_OFFER_TASKCODE = 20;
	private final int REMOVE_INVENTORY_PROD_TASKCODE = 9;
	private final int GET_SELECTED_INVENTORY_TASKCODE = 12;
	private final int GET_SELECTED_INVENTORY_DEL_TASKCODE = 13;
	private final int GET_SELECTED_INVENTORY_BATCH_TASKCODE = 14;
	public static final int MRP_CONTEXT = 0;
	public static final int PURCHASE_PRICE_CONTEXT = 1;
	public static final int SALES_PRICE_CONTEXT = 2;
	public static final int TAX_CONTEXT = 3;
	public static final int STOCKQTY_CONTEXT = 4;
	public static final int BATCH_MRP_CONTEXT = 6;
	public static final int BATCH_SALESPRICE_CONTEXT = 7;
	public static final int BATCH_PURCHASEPRICE_CONTEXT = 8;
	public static final int BATCH_EXPDATE_CONTEXT = 9;
	public static final int BATCH_AVAILABLEQTY_CONTEXT = 10;
	private final String TAG = "[OfferAndStoreFragment]";
	private RelativeLayout campaignButton;
	private RelativeLayout syncCampaignButton;
	private TextView stockupdate_header_textview;
	private ProductCategory selectedSubcategory;
	private OnActionbarNavigationListener onActionbarNavigationListener;
	private OnAddNewProductListener onAddNewProductListener;
	private OfferStoreCurserAdapter inventoryProductCursorAdapter;
	private CampaignAdapter campaignAdapter;
	private NumKeypadFragment keypadFragment;
	private ArrayList<InventorySku> inventorySkuUpdateList;
	private ArrayList<InventoryBatch> inventoryBatchUpdateList;
	private EditText productSearchEditText;
	private SearchInventoryCursorTask searchInventorySkuTask;
	private CampaignTask campaignTask;
	private View keypadContainerLayout;
	private Button clearSearchButton;
	private RelativeLayout productSearchLayout;
	private FilterFragment filterFragment;
	private ArrayList<Brand> brandList;
	private TextView subcategoryTextView;
	private TextView categoryTextView;
	private RelativeLayout storeLayout;
	private GetBrandGlobalProductsTask getBrandGlobalProductsTask;
	private GetBrandInventoryProductsTask getBrandInventoryProductsTask;
	private HorizontalScrollView storeOfferView;
	private HorizontalScrollView campaignView;
	private InventorySku deleteSku;
	private TextView updateHeaderTextView;
	boolean isUpdateRequired = false;
	public BroadcastReceiver mReceiver;
	private ProgressDialog progress = null;
	private BroadcastReceiver downloadReceiver;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		GoogleAnalyticsTracker.getInstance(getActivity()).fragmentLoaded(getClass().getSimpleName(), getActivity());
		View view = inflater.inflate(R.layout.fragment_inventory_update, null);
		((ListView) view.findViewById(R.id.visibility_products_list))
				.addHeaderView(inflater.inflate(
						R.layout.header_product_stockupdate, null));
		((ListView) view.findViewById(R.id.campaign_list))
				.addHeaderView(inflater.inflate(R.layout.header_campaign_list,
						null));
		keypadContainerLayout = view.findViewById(R.id.keypad_container_layout);
		updateHeaderTextView = (TextView) view
				.findViewById(R.id.update_header_name_textview);
		storeOfferView = (HorizontalScrollView) view
				.findViewById(R.id.storeoffer_view);
		campaignView = (HorizontalScrollView) view
				.findViewById(R.id.campaign_view);
		return view;
	}

	View.OnClickListener onAddNewProductDismissListener = new View.OnClickListener() {

		@Override
		public void onClick(View view) {
			view.setVisibility(View.GONE);
		}
	};

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			onActionbarNavigationListener = (OnActionbarNavigationListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ getString(R.string.exc_implementnavigation));
		}
		try {
			onAddNewProductListener = (OnAddNewProductListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ "must implement OnAddNewProductListener");
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		progress = new ProgressDialog(getActivity());
		progress.setMessage(getResources().getString(
				R.string.downloadingCampaign));
		progress.setCanceledOnTouchOutside(false);
		progress.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.alert_sync_restart) + "\n" + getString(R.string.alert_multiple_download), new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    	DownloadManager downloadManager = (DownloadManager) getActivity().getSystemService(getActivity().DOWNLOAD_SERVICE);
		    	downloadManager.remove(SnapSharedUtils.getDownloadingId(SnapCommonUtils.getSnapContext(getActivity())));
		    		SnapSharedUtils.setCampaignSyncStatus(false,SnapCommonUtils.getSnapContext(getActivity()));
		    		SnapSharedUtils.storeDownloadingId(SnapCommonUtils.getSnapContext(getActivity()),0);
		    		syncCampaignButton.setClickable(true);
		    		CustomToast.showCustomToast(getActivity()
						.getApplicationContext(), getString(R.string.alert_stop_download),
						Toast.LENGTH_SHORT, CustomToast.ERROR);
		    		dialog.dismiss();
		    }
		});
		IntentFilter intentFilter = new IntentFilter(
				"android.intent.action.taskcompleted");

		mReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				Log.e("recv_broadcast", "recv_broadcast");
				Log.d("TAG", "Campaign Log----- mReceiver-- Clossing progrssbar-->");
				try {
					campaignAdapter.dismissProgressDialog();
					campaignAdapter.progress.dismiss();
				} catch (Exception e) {
				}
				try {
					if (progress != null) {
						progress.dismiss();
						syncCampaignButton.setClickable(true);
						Intent visibilityServiceIntent = new Intent(
								getActivity(), SnapVisibilityService.class);
						getActivity().stopService(visibilityServiceIntent);
						
						new CampaignTask(getActivity(), OfferAndStoreFragment.this,GET_CAMPAIGN_TASKCODE).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				       
					}
				} catch (NullPointerException e) {
					e.printStackTrace();
				}

			}

		};
		getActivity().registerReceiver(mReceiver, intentFilter);
		
		
		downloadReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				new CampaignTask(getActivity(), OfferAndStoreFragment.this,GET_CAMPAIGN_TASKCODE).execute();
		       
			}
		};
		
		IntentFilter downloadIntentFilter = new IntentFilter(
				"android.intent.action.downloadcompleted");
		getActivity().registerReceiver(downloadReceiver, downloadIntentFilter);
		
		getActivity().getActionBar().setCustomView(
				R.layout.actionbar_update_layout);
		subcategoryTextView = (TextView) getActivity().findViewById(
				R.id.update_sub_category_name_textview);
		categoryTextView = ((TextView) getActivity().findViewById(
				R.id.update_category_name_textview));
		storeLayout = (RelativeLayout) getActivity().findViewById(
				R.id.update_category_layout);
		storeLayout.setOnClickListener(onCategoryChooseClickListener);
		campaignButton = (RelativeLayout) getActivity().findViewById(
				R.id.update_brands_layout);
		syncCampaignButton = (RelativeLayout) getActivity().findViewById(
				R.id.sync_campaign_layout);
		campaignButton.setOnClickListener(brandButtonClickListener);
		syncCampaignButton.setOnClickListener(syncCampaignClickListener);
		stockupdate_header_textview = (TextView) getActivity().findViewById(
				R.id.stockupdate_header_textview);
		stockupdate_header_textview.setText(getResources().getString(
				R.string.my_store));

		if (inventoryProductCursorAdapter != null) {
			((ListView) getView().findViewById(R.id.visibility_products_list))
					.setAdapter(inventoryProductCursorAdapter);
			((TextView) getView()
					.findViewById(R.id.inventory_totalqty_textview))
					.setText(SnapToolkitTextFormatter
							.formatNumberText(inventoryProductCursorAdapter
									.getCount())
							+ " items");
		}
		selectedSubcategory = new ProductCategory();
		selectedSubcategory.setCategoryId(2);
		if (filterFragment != null) {
			filterFragment.setBrandFilteredBySubcategory(false);
		}
		productSearchLayout = (RelativeLayout) getActivity().getActionBar()
				.getCustomView().findViewById(R.id.search_layout);
		clearSearchButton = (Button) getActivity().getActionBar()
				.getCustomView().findViewById(R.id.clear_search_text_button);
		clearSearchButton.setOnClickListener(clearSearchClickListener);

		productSearchEditText = (EditText) getActivity().findViewById(
				R.id.search_edittext);
		if (searchInventorySkuTask != null)
			searchInventorySkuTask.cancel(true);

		if (SnapSharedUtils.isOfferUpdated(getActivity())) {
			RemoveStoreOfferTask removeStoreOfferTask = new RemoveStoreOfferTask(
					getActivity(), OfferAndStoreFragment.this,
					DELETE_OFFER_TASKCODE);
			removeStoreOfferTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,
					"");

		} else {
			searchInventorySkuTask = new SearchInventoryCursorTask(
					getActivity(), OfferAndStoreFragment.this,
					SEARCH_INVENTORY_TASKCODE);
			searchInventorySkuTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,
					"");
		}

	}

	OnClickListener clearSearchClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			productSearchEditText.setText("");
		}
	};

	public boolean isKeyPadVisible() {
		if (keypadFragment != null)
			return keypadFragment.isVisible();
		else
			return false;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (productSearchEditText != null)
			productSearchEditText
					.addTextChangedListener(productSearchTextWatcher);
	};

	@Override
	public void onPause() {
		super.onPause();
		productSearchEditText
				.removeTextChangedListener(productSearchTextWatcher);
	};

	@Override
	public void onStop() {
		super.onStop();
		try {
			if (this.mReceiver != null) {
				getActivity().unregisterReceiver(this.mReceiver);
			}
			if (this.mReceiver != null) {
				getActivity().unregisterReceiver(downloadReceiver);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		productSearchEditText.setText("");
		if (inventorySkuUpdateList != null) {
			inventorySkuUpdateList.clear();
		}
		if (inventoryProductCursorAdapter != null) {
			inventoryProductCursorAdapter = null;
			((ListView) getView().findViewById(R.id.visibility_products_list))
					.setAdapter(null);
		}
		if (campaignAdapter != null) {
			campaignAdapter = null;
			((ListView) getView().findViewById(R.id.campaign_list))
					.setAdapter(null);
		}

	}

	public void onDestroy() {
		super.onDestroy();
		filterFragment = null;
		if (inventoryProductCursorAdapter != null
				&& inventoryProductCursorAdapter.getCursor() != null) {
			inventoryProductCursorAdapter.getCursor().close();
		}
		if (searchInventorySkuTask != null
				&& !searchInventorySkuTask.isCancelled()) {
			searchInventorySkuTask.cancel(true);
		}
		if (getBrandInventoryProductsTask != null
				&& !getBrandInventoryProductsTask.isCancelled()) {
			getBrandInventoryProductsTask.cancel(true);
		}
		if (getBrandGlobalProductsTask != null
				&& !getBrandGlobalProductsTask.isCancelled()) {
			getBrandGlobalProductsTask.cancel(true);
		}
		if (campaignTask != null && !campaignTask.isCancelled()) {
			campaignTask.cancel(true);
		}
		if (inventoryProductCursorAdapter != null) {
			inventoryProductCursorAdapter = null;
		}
		if (inventoryProductCursorAdapter != null) {
			inventoryProductCursorAdapter = null;
			((ListView) getView().findViewById(R.id.visibility_products_list))
					.setAdapter(null);
		}
		if (campaignAdapter != null) {
			campaignAdapter = null;
			((ListView) getView().findViewById(R.id.campaign_list))
					.setAdapter(null);
		}

	};

	TextWatcher productSearchTextWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
			productSearchKeyStrokeTimer.cancel();
			if (productSearchEditText.getText().toString().length() == 0) {
				if (inventorySkuUpdateList != null
						&& inventorySkuUpdateList.size() > 0)
					SnapCommonUtils.showAlert(getActivity(), "",
							getString(R.string.msg_update_changes),
							positiveClickListener, negativeClickListener, true);
				if (isKeyPadVisible()) {
					dismissNumpad();
				}
				campaignButton.setVisibility(View.VISIBLE);
				storeLayout.setVisibility(View.VISIBLE);
				Integer[] brandIds;
				subcategoryTextView.setVisibility(View.VISIBLE);
				categoryTextView.setVisibility(View.VISIBLE);
				updateHeaderTextView.setVisibility(View.VISIBLE);
				if (brandList != null) {
					brandIds = new Integer[brandList.size()];
					for (int i = 0; i < brandList.size(); i++) {
						brandIds[i] = brandList.get(i).getBrandId();
					}
				} else {
					brandIds = new Integer[0];
				}
				if (getBrandInventoryProductsTask != null
						&& !getBrandInventoryProductsTask.isCancelled()) {
					getBrandInventoryProductsTask.cancel(true);
				}
				getBrandInventoryProductsTask = new GetBrandInventoryProductsTask(
						getActivity(), OfferAndStoreFragment.this,
						GET_BRAND_INVENTORY_PRODS_TASKCODE,
						(selectedSubcategory.getCategoryId()));
				getBrandInventoryProductsTask.executeOnExecutor(
						AsyncTask.THREAD_POOL_EXECUTOR, brandIds);
			} else {
				productSearchKeyStrokeTimer.start();
			}
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {

		}

		@Override
		public void afterTextChanged(Editable arg0) {

		}
	};

	CountDownTimer productSearchKeyStrokeTimer = new CountDownTimer(
			SnapBillingConstants.KEY_STROKE_TIMEOUT,
			SnapBillingConstants.KEY_STROKE_TIMEOUT) {

		@Override
		public void onTick(long arg0) {
		}

		@Override
		public void onFinish() {
			String keyword = productSearchEditText.getText().toString();
			if (getBrandInventoryProductsTask != null
					&& !getBrandInventoryProductsTask.isCancelled()) {
				getBrandInventoryProductsTask.cancel(true);
			}
			if (getBrandGlobalProductsTask != null
					&& !getBrandGlobalProductsTask.isCancelled()) {
				getBrandGlobalProductsTask.cancel(true);
			}
			if (campaignTask != null && !campaignTask.isCancelled()) {
				campaignTask.cancel(true);
			}

			if (searchInventorySkuTask != null)
				searchInventorySkuTask.cancel(true);
			searchInventorySkuTask = new SearchInventoryCursorTask(
					getActivity(), OfferAndStoreFragment.this,
					SEARCH_INVENTORY_TASKCODE);
			searchInventorySkuTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,
					keyword);
		}
	};

	OnClickListener onCategoryChooseClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			stockupdate_header_textview.setText(getResources().getString(
					R.string.my_store));

			storeLayout.setSelected(true);
			campaignButton.setSelected(false);
			storeOfferView.setVisibility(View.VISIBLE);
			campaignView.setVisibility(View.GONE);
		}
	};

	OnClickListener brandButtonClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			stockupdate_header_textview.setText(getResources().getString(
					R.string.campaign));

			campaignButton.setSelected(true);
			storeLayout.setSelected(false);
			storeOfferView.setVisibility(View.GONE);
			campaignView.setVisibility(View.VISIBLE);
			Log.d("TAG", "Campaign Log-----  inside SnapVisibilityService--1-"+SnapSharedUtils.getDownloadingId(SnapCommonUtils.getSnapContext(getActivity())));
			Log.d("TAG", "Campaign Log-----  inside SnapVisibilityService--2-"+SnapSharedUtils.getCampaignSyncStatus(SnapCommonUtils.getSnapContext(getActivity())));
			if(SnapSharedUtils.getDownloadingId(SnapCommonUtils.getSnapContext(getActivity()))!=0||SnapSharedUtils.getCampaignSyncStatus(SnapCommonUtils.getSnapContext(getActivity()))){
				syncCampaignButton.setClickable(false);
				progress.show();
			}
			new CampaignTask(getActivity(), OfferAndStoreFragment.this,
					GET_CAMPAIGN_TASKCODE).execute();

		}
	};

	OnClickListener syncCampaignClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(getActivity(), v, TAG);
			if (SnapCommonUtils.isNetworkAvailable(getActivity())) {
				Log.d("TAG", "Going to download images campList------");
				if(SnapSharedUtils.getDownloadingId(SnapCommonUtils.getSnapContext(getActivity()))==0){
					Log.d("TAG", "Going to download images campList-----in-");
					syncCampaignButton.setClickable(false);
					progress.show();
					Log.d("TAG", "Going to download images campList-----0-");
					Intent visibilityServiceIntent = new Intent(getActivity(),SnapVisibilityService.class);
					getActivity().startService(visibilityServiceIntent);
					Log.d("TAG", "Going to download images campList-----1-");
					Log.e("service started", "service started");
					}else{
						syncCampaignButton.setSelected(false);
					}
				
			} else {
				CustomToast.showCustomToast(getActivity()
						.getApplicationContext(), getString(R.string.alert_network_connection),
						Toast.LENGTH_SHORT, CustomToast.ERROR);

			}

		}
	};

	OnClickListener onMyStoreClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Toast.makeText(getActivity(), getString(R.string.mystore), Toast.LENGTH_SHORT).show();
		}
	};

	OnClickListener onMyCAmpaignClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Toast.makeText(getActivity(), getString(R.string.mycampaign), Toast.LENGTH_SHORT)
					.show();

		}
	};

	OnClickListener onInventoryClickListner = new OnClickListener() {

		@Override
		public void onClick(View v) {

			stockupdate_header_textview.setText(getResources().getString(
					R.string.my_store));

			if (getBrandInventoryProductsTask != null
					&& !getBrandInventoryProductsTask.isCancelled()) {
				getBrandInventoryProductsTask.cancel(true);
			}
			if (getBrandGlobalProductsTask != null
					&& !getBrandGlobalProductsTask.isCancelled()) {
				getBrandGlobalProductsTask.cancel(true);
			}
			if (inventorySkuUpdateList != null
					&& inventorySkuUpdateList.size() > 0)
				SnapCommonUtils.showAlert(getActivity(), "",
						getString(R.string.msg_update_changes),
						positiveClickListener, negativeClickListener, true);
			else
				onAddNewProductListener.showAddProductLayout("");

		}
	};

	public void addProductToInventory(InventorySku inventorySku) {

		inventorySku.getProductSku().setSelected(true);
		if (productSearchLayout != null) {
			productSearchLayout.setVisibility(View.VISIBLE);
			productSearchEditText.setText(inventorySku.getProductSku()
					.getProductSkuCode());
		}
	}

	public boolean isBatchesShowing() {
		return keypadContainerLayout.getVisibility() == View.VISIBLE ? false
				: getView().findViewById(R.id.inventory_batches_relativelayout)
						.getVisibility() == View.VISIBLE ? true : false;
	}

	public void hidebatches() {
		getView().findViewById(R.id.inventory_batches_relativelayout)
				.setVisibility(View.GONE);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		onActionbarNavigationListener.onActionbarNavigation("",
				menuItem.getItemId());
		return true;
	}

	public void onAddNewProductComplete(InventorySku newSku, Boolean isEditProd) {

	}

	@SuppressWarnings("deprecation")
	@Override
	public void onTaskSuccess(Object responseList, int taskCode) {

		Log.d("TAG", "Going to download images campList--------taskCode="
				+ taskCode);

		Log.d("OfferAndStoreFragment", "taskCode---------------------->"
				+ taskCode);
		if (getActivity() == null)
			return;
		if (taskCode == UPDATE_INVENTORY_PRODS_TASKCODE) {
			if (inventoryProductCursorAdapter != null) {
				inventoryProductCursorAdapter.getCursor().requery();
				inventoryProductCursorAdapter.notifyDataSetChanged();
			}
		} else if (taskCode == GET_CAMPAIGN_TASKCODE) {

			((ListView) getView().findViewById(R.id.campaign_list))
					.setAdapter(null);
			campaignAdapter = new CampaignAdapter(getActivity(),
					(List<Campaigns>) responseList);

			if (getView() != null){
				((ListView) getView().findViewById(R.id.campaign_list))
						.setAdapter(campaignAdapter);
			 }
			 if (campaignAdapter != null) {
		            campaignAdapter.notifyDataSetChanged();
			 }

		} else if (taskCode == SEARCH_INVENTORY_TASKCODE) {
			if (inventorySkuUpdateList != null
					&& inventorySkuUpdateList.size() > 0)
				SnapCommonUtils.showAlert(getActivity(), "",
						getString(R.string.msg_update_changes),
						positiveClickListener, negativeClickListener, true);

			updateHeaderTextView.setVisibility(View.INVISIBLE);

			if (inventoryProductCursorAdapter == null) {
				inventoryProductCursorAdapter = new OfferStoreCurserAdapter(
						getActivity(), (Cursor) responseList, true, this);
				if (getView() != null)
					((ListView) getView().findViewById(
							R.id.visibility_products_list))
							.setAdapter(inventoryProductCursorAdapter);
			} else {
				if (inventoryProductCursorAdapter != null
						&& inventoryProductCursorAdapter.getCursor() != null)
					inventoryProductCursorAdapter.getCursor().close();
				inventoryProductCursorAdapter.swapCursor((Cursor) responseList);
				inventoryProductCursorAdapter.notifyDataSetChanged();
			}
			if (getView() != null)
				((TextView) getView().findViewById(
						R.id.inventory_totalqty_textview))
						.setText(SnapToolkitTextFormatter
								.formatNumberText(inventoryProductCursorAdapter
										.getCount())
								+ " items");
		} else if (taskCode == REMOVE_INVENTORY_PROD_TASKCODE) {
			if (inventoryProductCursorAdapter != null) {
				InventorySku inventorySku = ((InventorySku) responseList);
				inventorySku.getProductSku().setSelected(false);
				inventorySku.setOffer(false);
				inventorySku.setStore(false);
				inventoryProductCursorAdapter.getCursor().requery();

				if (!inventorySku.getProductSku().isGDB()) {
					inventoryProductCursorAdapter.getCursor().requery();
				}
				inventoryProductCursorAdapter.notifyDataSetChanged();
				if (inventorySkuUpdateList != null
						&& inventorySkuUpdateList.contains(inventorySku)) {
					inventorySkuUpdateList.remove(inventorySku);
				}
				if (getView() != null)
					((TextView) getView().findViewById(
							R.id.inventory_totalqty_textview))
							.setText(inventoryProductCursorAdapter.getCount()
									+ " items");
			}

			else if (taskCode == GET_SELECTED_INVENTORY_DEL_TASKCODE) {
				new RemoveInventoryProductTask(getActivity(),
						OfferAndStoreFragment.this,
						REMOVE_INVENTORY_PROD_TASKCODE)
						.execute((InventorySku) responseList);
			} else if (taskCode == DELETE_OFFER_TASKCODE) {
				SnapSharedUtils.setOfferUpdated(getActivity(), true);
				searchInventorySkuTask = new SearchInventoryCursorTask(
						getActivity(), OfferAndStoreFragment.this,
						SEARCH_INVENTORY_TASKCODE);
				searchInventorySkuTask.executeOnExecutor(
						AsyncTask.SERIAL_EXECUTOR, "");
			}

		}
	}

	@Override
	public void onTaskError(String errorMessage, int taskCode) {
		if (getActivity() == null)
			return;
		CustomToast.showCustomToast(getActivity(), errorMessage,
				Toast.LENGTH_SHORT, CustomToast.ERROR);
		if (taskCode == GET_BRANDS_TASKCODE) {
			if (inventoryProductCursorAdapter != null)
				inventoryProductCursorAdapter.swapCursor(null);

		} else if (taskCode == UPDATE_INVENTORY_PRODS_TASKCODE) {
			CustomToast.showCustomToast(getActivity(), "update failed",
					Toast.LENGTH_SHORT, CustomToast.ERROR);
		} else if (taskCode == SEARCH_INVENTORY_TASKCODE) {
			updateHeaderTextView.setVisibility(View.INVISIBLE);
			if (inventorySkuUpdateList != null
					&& inventorySkuUpdateList.size() > 0)
				SnapCommonUtils.showAlert(getActivity(), "",
						getString(R.string.msg_update_changes),
						positiveClickListener, negativeClickListener, true);
		} else if (taskCode == DELETE_OFFER_TASKCODE) {
			searchInventorySkuTask = new SearchInventoryCursorTask(
					getActivity(), OfferAndStoreFragment.this,
					SEARCH_INVENTORY_TASKCODE);
			searchInventorySkuTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,
					"");
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		menu.findItem(R.id.campaign_menuitem).setVisible(false);

	}

	@Override
	public void onRemoveItem(InventorySku product) {

	}

	@Override
	public void onShowBatches(int position) {
		ProductSku productSku = inventoryProductCursorAdapter
				.getLastSelectedItem().getProductSku();
		new GetSelectedInvSkuTask(getActivity(), OfferAndStoreFragment.this,
				GET_SELECTED_INVENTORY_BATCH_TASKCODE).execute(productSku
				.getProductSkuCode());

	}

	public void updateInventoryBatch(InventoryBatch inventoryBatch) {
		if (inventoryBatchUpdateList == null) {
			inventoryBatchUpdateList = new ArrayList<InventoryBatch>();
		}
		if (!inventoryBatchUpdateList.contains(inventoryBatch))
			inventoryBatchUpdateList.add(inventoryBatch);
	}

	@Override
	public void onEditProduct(InventorySku inventorySku) {

		new GetSelectedInvSkuTask(getActivity(), this,
				GET_SELECTED_INVENTORY_TASKCODE).execute(inventorySku
				.getProductSku().getProductSkuCode());
	}

	public void dismissNumpad() {
	}

	OnClickListener positiveClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
		}
	};

	OnClickListener negativeClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (inventorySkuUpdateList != null)
				inventorySkuUpdateList.clear();
			SnapCommonUtils.dismissAlert();
		}
	};

	OnClickListener positiveDeleteClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (deleteSku != null)
				new GetSelectedInvSkuTask(getActivity(),
						OfferAndStoreFragment.this,
						GET_SELECTED_INVENTORY_DEL_TASKCODE).execute(deleteSku
						.getProductSku().getProductSkuCode());

			deleteSku = null;
			SnapCommonUtils.dismissAlert();
		}
	};

	OnClickListener negativeDeleteClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			deleteSku = null;
			SnapCommonUtils.dismissAlert();
		}
	};

	public boolean isUpdateListPresent() {
		if (inventorySkuUpdateList != null) {
			if (inventorySkuUpdateList.isEmpty()) {
				return false;
			} else
				return true;
		} else {
			return false;
		}
	}

	public void updateInventoryProducts() {
	}

	public boolean isFilterFragmentVisible() {
		if (null != filterFragment && filterFragment.isAdded()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onBrandEdit(int position) {

	}

	@Override
	public void onAddItemToInventory(InventorySku inventorySku) {
		new AddProductToInventoryTask(getActivity(),
				OfferAndStoreFragment.this, ADD_INVENTORY_PROD_TASKCODE)
				.execute(inventorySku);
	}

	@Override
	public void onProductSubCategorySelected(
			ProductCategory selectedProductSubCategory) {

	}

	public boolean isFilterVisible() {
		if (filterFragment != null)
			return filterFragment.isVisible();
		else
			return false;
	}

	public void popFilterFragmentandResetButtonStates() {
		getFragmentManager().popBackStack();
	}

	@Override
	public void updateInventorySku(InventorySku inventorySku, String fieldName,
			boolean value) {
		new UpdateInventoryTask(getActivity(), OfferAndStoreFragment.this,
				UPDATE_INVENTORY_PRODS_TASKCODE, fieldName, inventorySku
						.getProductSku().getProductSkuCode()).execute(value
				+ "");
	}

	@Override
	public void onProductValueEdit(float value, int context) {

	}

	public static boolean isNetworkAvailable(Context context) {
		boolean outcome = false;

		if (context != null) {
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);

			// For 3G check
			if (cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
					.isConnectedOrConnecting()) {
				outcome = true;
			}
			// For WiFi Check
			if (cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
					.isConnectedOrConnecting()) {
				outcome = true;
			}

		}

		return outcome;
	}
}
