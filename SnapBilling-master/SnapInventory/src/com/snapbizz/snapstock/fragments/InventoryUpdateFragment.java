package com.snapbizz.snapstock.fragments;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sqlcipher.Cursor;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.snapbizz.snapstock.R;
import com.snapbizz.snapstock.adapters.BrandSelectionAdapter.OnBrandEditListener;
import com.snapbizz.snapstock.adapters.InventoryBatchAdapter;
import com.snapbizz.snapstock.adapters.InventoryBatchAdapter.OnInventoryBatchActionListener;
import com.snapbizz.snapstock.adapters.InventoryProductCursorAdapter;
import com.snapbizz.snapstock.adapters.InventoryProductsAdapter;
import com.snapbizz.snapstock.adapters.InventoryProductsAdapter.OnInventoryActionListener;
import com.snapbizz.snapstock.asynctasks.AddProductToInventoryTask;
import com.snapbizz.snapstock.asynctasks.GetBrandGlobalProductsTask;
import com.snapbizz.snapstock.asynctasks.GetBrandInventoryProductsTask;
import com.snapbizz.snapstock.asynctasks.GetInventoryBatchesTask;
import com.snapbizz.snapstock.asynctasks.GetSelectedInvSkuTask;
import com.snapbizz.snapstock.asynctasks.GetSubCategoriesBrandsTask;
import com.snapbizz.snapstock.asynctasks.RemoveInventoryProductTask;
import com.snapbizz.snapstock.asynctasks.SearchInventoryCursorTask;
import com.snapbizz.snapstock.asynctasks.UpdateInventoryBatchTask;
import com.snapbizz.snapstock.asynctasks.UpdateInventoryTask;
import com.snapbizz.snapstock.fragments.FilterFragment.OnFilterSelectedListener;
import com.snapbizz.snapstock.interfaces.GetResponseBackToUpdateFragment;
import com.snapbizz.snapstock.interfaces.OnAddNewProductListener;
import com.snapbizz.snapstock.utils.ProductFilterType;
import com.snapbizz.snapstock.utils.SnapInventoryConstants;
import com.snapbizz.snaptoolkit.asynctasks.QueryProductCategories;
import com.snapbizz.snaptoolkit.asynctasks.QueryProductSubCategories;
import com.snapbizz.snaptoolkit.domains.Brand;
import com.snapbizz.snaptoolkit.domains.InventoryBatch;
import com.snapbizz.snaptoolkit.domains.InventorySku;
import com.snapbizz.snaptoolkit.domains.ProductCategory;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.fragments.NumKeypadFragment;
import com.snapbizz.snaptoolkit.fragments.NumKeypadFragment.NumberKeypadEnterListener;
import com.snapbizz.snaptoolkit.interfaces.OnActionbarNavigationListener;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.CustomToast;
import com.snapbizz.snaptoolkit.utils.SkuUnitType;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;
import com.snapbizz.snaptoolkit.utils.SnapToolkitTextFormatter;


public class InventoryUpdateFragment extends Fragment implements
		OnQueryCompleteListener, OnInventoryActionListener,
		NumberKeypadEnterListener, OnInventoryBatchActionListener,
		OnBrandEditListener, OnFilterSelectedListener,
		GetResponseBackToUpdateFragment {

	private final int GET_CATEGORIES_TASKCODE = 0;
	private final int GET_SUBCATEGORIES_TASKCODE = 1;
	private final int GET_BRANDS_TASKCODE = 2;
	private final int GET_BRAND_INVENTORY_PRODS_TASKCODE = 4;
	private final int ADD_INVENTORY_PROD_TASKCODE = 5;
	private final int UPDATE_INVENTORY_PRODS_TASKCODE = 6;
	private final int SEARCH_INVENTORY_TASKCODE = 7;
	private final int REMOVE_INVENTORY_PROD_TASKCODE = 9;
	private final int GET_INVENTORY_BATCH_TASKCODE = 10;
	private final int UPADTE_INVENTORY_BATCH_TASKCODE = 11;
	private final int GET_SELECTED_INVENTORY_TASKCODE = 12;
	private final int GET_SELECTED_INVENTORY_DEL_TASKCODE = 13;
	private final int GET_SELECTED_INVENTORY_BATCH_TASKCODE = 14;

	public static final int MRP_CONTEXT = 0;
	public static final int PURCHASE_PRICE_CONTEXT = 1;
	public static final int SALES_PRICE_CONTEXT = 2;
	public static final int STOCKQTY_CONTEXT = 4;
	public static final int BATCH_MRP_CONTEXT = 6;
	public static final int BATCH_SALESPRICE_CONTEXT = 7;
	public static final int BATCH_PURCHASEPRICE_CONTEXT = 8;
	public static final int BATCH_EXPDATE_CONTEXT = 9;
	public static final int BATCH_AVAILABLEQTY_CONTEXT = 10;
	public static final int SALES_PRICE_TWO_CONTEXT = 12;
	public static final int SALES_PRICE_THREE_CONTEXT = 13;

	private TextView subcategoryTextView;
	private TextView categoryTextView;
	private ProductCategory selectedSubcategory;
	private OnActionbarNavigationListener onActionbarNavigationListener;
	private OnAddNewProductListener onAddNewProductListener;
	private InventoryProductsAdapter inventoryProductsAdapter;
	private InventoryProductCursorAdapter inventoryProductCursorAdapter;
	private InventoryBatchAdapter inventoryBatchAdapter;
	private NumKeypadFragment keypadFragment;
	private ArrayList<InventorySku> inventorySkuUpdateList;
	private ArrayList<InventoryBatch> inventoryBatchUpdateList;
	private EditText productSearchEditText;
	private SearchInventoryCursorTask searchInventorySkuTask;
	private View keypadContainerLayout;
	private ListView inventoryBatchListView;
	private Button clearSearchButton;
	private RelativeLayout productSearchLayout;
	private FilterFragment filterFragment;
	private ArrayList<Brand> brandList;

	private QueryProductCategories queryProdCategory;
	private QueryProductSubCategories queryProductSubCategories;
	private GetBrandGlobalProductsTask getBrandGlobalProductsTask;
	private GetBrandInventoryProductsTask getBrandInventoryProductsTask;
	private GetSubCategoriesBrandsTask getSubCategoriesBrandsTask;
	private InventorySku deleteSku;
	private RelativeLayout brandButton;
	private RelativeLayout categorySelectorLayout;
	private TextView updateHeaderTextView;
	private RelativeLayout actionbarHeaderLayout;
	public Menu menu;
	boolean isUpdateRequired = false;
	private String toUpdateFieldName;
	private String toUpdateFieldVal;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		SnapToolkitConstants.IS_REPORT = "0";
		View view = inflater.inflate(R.layout.fragment_inventory_update, null);
		((ListView) view.findViewById(R.id.inventory_products_list)).addHeaderView(inflater.inflate(R.layout.header_product_stockupdate, null));
		keypadContainerLayout = view.findViewById(R.id.keypad_container_layout);
		inventoryBatchListView = (ListView) view.findViewById(R.id.inventory_batches_listview);
		inventoryBatchListView.addHeaderView(inflater.inflate(R.layout.header_inventory_batch, null));
		view.findViewById(R.id.inventorybatch_cancel_button).setOnClickListener(onInventoryBatchDismissListener);
		view.findViewById(R.id.inventorybatch_done_button).setOnClickListener(onInventoryBatchUpdateClickListener);
		updateHeaderTextView = (TextView) view.findViewById(R.id.update_header_name_textview);
		return view;
	}

	View.OnClickListener onInventoryBatchUpdateClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (inventoryBatchUpdateList != null)
				new UpdateInventoryBatchTask(getActivity(),
						InventoryUpdateFragment.this,
						UPADTE_INVENTORY_BATCH_TASKCODE)
						.execute(inventoryBatchUpdateList);
			else
				onInventoryBatchDismissListener.onClick(getView().findViewById(
						R.id.inventory_batches_relativelayout));
			inventoryBatchAdapter.lastSelectedPos = -1;
		}
	};

	View.OnClickListener onInventoryBatchDismissListener = new View.OnClickListener() {

		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			getView().findViewById(R.id.inventory_batches_relativelayout)
					.setVisibility(View.GONE);
		}
	};

	View.OnClickListener onAddNewProductDismissListener = new View.OnClickListener() {

		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
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
					+ "must implement OnActionbarNavigationListener");
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
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		getActivity().getActionBar().show();
		// if (getActivity().getActionBar().getCustomView().getId() !=
		// R.id.actionbar_update_layout)
		getActivity().getActionBar().setCustomView(
				R.layout.actionbar_update_layout);
		if (inventoryProductCursorAdapter != null) {
			((ListView) getView().findViewById(R.id.inventory_products_list))
					.setAdapter(inventoryProductCursorAdapter);
			((TextView) getView()
					.findViewById(R.id.inventory_totalqty_textview))
					.setText(SnapToolkitTextFormatter
							.formatNumberText(inventoryProductCursorAdapter
									.getCount())
							+ " items");

		}
		subcategoryTextView = (TextView) getActivity().findViewById(
				R.id.update_sub_category_name_textview);
		categoryTextView = ((TextView) getActivity().findViewById(
				R.id.update_category_name_textview));
		categorySelectorLayout = (RelativeLayout) getActivity().findViewById(
				R.id.update_category_layout);
		categorySelectorLayout
				.setOnClickListener(onCategoryChooseClickListener);
		((TextView) getActivity().getActionBar().getCustomView()
				.findViewById(R.id.owner_name_textview))
				.setText(SnapSharedUtils.getStoreDetails(
						SnapCommonUtils.getSnapContext(getActivity()))
						.getStoreName());
		brandButton = (RelativeLayout) getActivity().findViewById(
				R.id.update_brands_layout);
		brandButton.setOnClickListener(brandButtonClickListener);
		actionbarHeaderLayout = (RelativeLayout) getActivity().findViewById(
				R.id.actionbar_update_information_layout);
		actionbarHeaderLayout.setSelected(true);
		actionbarHeaderLayout.setOnClickListener(onInventoryClickListner);
		if (queryProdCategory != null && !queryProdCategory.isCancelled()) {
			queryProdCategory.cancel(true);
		}
		if (SnapToolkitConstants.IS_REPORT.equalsIgnoreCase("0")) {
			queryProdCategory = new QueryProductCategories(getActivity(), this,
					GET_CATEGORIES_TASKCODE);

			queryProdCategory.execute();
			
			
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

		/*
		 * ((Button) getView().findViewById(R.id.inventory_prod_done_button))
		 * .setOnClickListener(onInventoryClickListner);
		 */
		((Button) getView().findViewById(R.id.inventory_prod_add_button))
				.setOnClickListener(onInventoryClickListner);

		keypadContainerLayout.setOnClickListener(keypadDismissListener);
		if (inventoryBatchAdapter != null)
			inventoryBatchListView.setAdapter(inventoryBatchAdapter);

	}

	OnClickListener onCategoryChooseClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			keypadDismissListener.onClick(keypadContainerLayout);
			brandButton.setSelected(false);
			brandButton.setVisibility(View.GONE);
			actionbarHeaderLayout.setSelected(false);

			if (categorySelectorLayout.isSelected()) {
				getFragmentManager().popBackStack();
				actionbarHeaderLayout.setSelected(true);

			} else {
				productSearchLayout.setVisibility(View.GONE);
				menu.findItem(R.id.search_meuitem).setVisible(false);
				if (filterFragment == null) {
					filterFragment = new FilterFragment();
					filterFragment
							.setOnFilterSelectedListener(InventoryUpdateFragment.this);
				}
				filterFragment.setShowAllCategories(false);
				filterFragment
						.setProductFilterType(ProductFilterType.CATEGORY_FILTER);
				FragmentTransaction ft = getFragmentManager()
						.beginTransaction();
				ft.replace(R.id.second_content_framelayout, filterFragment,
						"filter");
				if (!filterFragment.isAdded())
					ft.addToBackStack(filterFragment.getTag());
				ft.commit();
			}
			categorySelectorLayout.setSelected(!categorySelectorLayout
					.isSelected());
		}
	};

	OnClickListener clearSearchClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			productSearchEditText.setText("");
		}
	};

	public boolean isKeyPadVisible() {
		if (keypadFragment != null)
			return keypadFragment.isVisible();
		else
			return false;
	}

	OnClickListener keypadDismissListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (keypadContainerLayout != null && keypadContainerLayout.getVisibility() == View.VISIBLE)
				keypadContainerLayout.setVisibility(View.GONE);
			if (inventoryProductCursorAdapter != null
					&& inventoryProductCursorAdapter.lastSelectedView != null) {
				if (inventoryProductCursorAdapter.getLastSelectedPos() >= inventoryProductCursorAdapter
						.getCount()
						&& keypadFragment != null
						&& keypadFragment.isVisible()) {
					inventoryProductCursorAdapter.setLastSelectedPos(0);
					getFragmentManager().popBackStack();
					return;
				}
				if (inventoryProductCursorAdapter.lastSelectedView.getId() == R.id.inventory_salesprice_textview
						&& inventoryProductCursorAdapter.getLastSelectedItem() != null) {
					if (inventoryProductCursorAdapter.getLastSelectedItem()
							.isOffer()
							&& inventoryProductCursorAdapter
									.getLastSelectedItem().getProductSku()
									.getProductSkuSalePrice() >= inventoryProductCursorAdapter
									.getLastSelectedItem().getProductSku()
									.getProductSkuMrp()) {
						inventoryProductCursorAdapter.getLastSelectedItem()
								.setOffer(false);
						if (!isUpdateRequired) {
							updateInventorySku(
									(inventoryProductCursorAdapter
											.getLastSelectedItem()),
									"is_offer", false);
						}
					}
				}
				inventoryProductCursorAdapter.lastSelectedView
						.setSelected(false);
				inventoryProductCursorAdapter.notifyDataSetChanged();
				inventoryProductCursorAdapter.lastSelectedView = null;
			}
			if (keypadFragment != null && keypadFragment.isVisible())
				getFragmentManager().popBackStack();
			if (inventoryBatchAdapter != null
					&& inventoryBatchAdapter.lastSelectedView != null) {
				inventoryBatchAdapter.lastSelectedView.setSelected(false);
				inventoryBatchAdapter.notifyDataSetChanged();
				inventoryBatchAdapter.lastSelectedView = null;
			}
		}
	};

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
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		productSearchEditText.setText("");
		if (inventorySkuUpdateList != null) {
			inventorySkuUpdateList.clear();
		}
	}

	public void onDestroy() {
		super.onDestroy();
		filterFragment = null;
		keypadDismissListener.onClick(keypadContainerLayout);
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
		if (queryProdCategory != null && !queryProdCategory.isCancelled()) {
			queryProdCategory.cancel(true);
		}
		if (getSubCategoriesBrandsTask != null
				&& !getSubCategoriesBrandsTask.isCancelled()) {
			getSubCategoriesBrandsTask.cancel(true);
		}
		if (queryProductSubCategories != null
				&& !queryProductSubCategories.isCancelled()) {
			queryProductSubCategories.cancel(true);
		}
		if (getBrandGlobalProductsTask != null
				&& !getBrandGlobalProductsTask.isCancelled()) {
			getBrandGlobalProductsTask.cancel(true);
		}

	};

	TextWatcher productSearchTextWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
			// TODO Auto-generated method stub
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
				brandButton.setVisibility(View.GONE);
				categorySelectorLayout.setVisibility(View.VISIBLE);
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
						getActivity(), InventoryUpdateFragment.this,
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
			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable arg0) {
			// TODO Auto-generated method stub

		}
	};

	CountDownTimer productSearchKeyStrokeTimer = new CountDownTimer(
			SnapInventoryConstants.KEY_STROKE_TIMEOUT,
			SnapInventoryConstants.KEY_STROKE_TIMEOUT) {

		@Override
		public void onTick(long arg0) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onFinish() {
			// TODO Auto-generated method stub
			String keyword = productSearchEditText.getText().toString();
			if (getBrandInventoryProductsTask != null
					&& !getBrandInventoryProductsTask.isCancelled()) {
				getBrandInventoryProductsTask.cancel(true);
			}
			if (getBrandGlobalProductsTask != null
					&& !getBrandGlobalProductsTask.isCancelled()) {
				getBrandGlobalProductsTask.cancel(true);
			}
			if (searchInventorySkuTask != null)
				searchInventorySkuTask.cancel(true);
			searchInventorySkuTask = new SearchInventoryCursorTask(
					getActivity(), InventoryUpdateFragment.this,
					SEARCH_INVENTORY_TASKCODE);
			searchInventorySkuTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,
					keyword);
		}
	};

	OnClickListener brandButtonClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			keypadDismissListener.onClick(keypadContainerLayout);
			categorySelectorLayout.setSelected(false);
			actionbarHeaderLayout.setSelected(false);
			if (brandButton.isSelected()) {
				menu.findItem(R.id.search_meuitem).setVisible(false);
				ArrayList<Brand> brandList = (ArrayList<Brand>) filterFragment
						.getSelectedBrand();
				if (brandList != null) {
					Integer[] brandIds = new Integer[brandList.size()];
					int i = 0;
					for (Brand brand : brandList) {
						brandIds[i] = brand.getBrandId();
						i++;
					}
					if (getBrandInventoryProductsTask != null
							&& !getBrandInventoryProductsTask.isCancelled()) {
						getBrandInventoryProductsTask.cancel(true);
					}
					getBrandInventoryProductsTask = new GetBrandInventoryProductsTask(
							getActivity(), InventoryUpdateFragment.this,
							GET_BRAND_INVENTORY_PRODS_TASKCODE,
							selectedSubcategory.getCategoryId());
					getBrandInventoryProductsTask.executeOnExecutor(
							AsyncTask.THREAD_POOL_EXECUTOR, brandIds);
				} else {
					if (getBrandInventoryProductsTask != null
							&& !getBrandInventoryProductsTask.isCancelled()) {
						getBrandInventoryProductsTask.cancel(true);
					}
					if (null != selectedSubcategory) {
						getBrandInventoryProductsTask = new GetBrandInventoryProductsTask(
								getActivity(), InventoryUpdateFragment.this,
								GET_BRAND_INVENTORY_PRODS_TASKCODE,
								selectedSubcategory.getCategoryId());
						getBrandInventoryProductsTask
								.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					}
				}
				getFragmentManager().popBackStack();
				actionbarHeaderLayout.setSelected(true);
			} else {
				productSearchLayout.setVisibility(View.GONE);
				menu.findItem(R.id.search_meuitem).setVisible(false);
				if (filterFragment == null) {
					filterFragment = new FilterFragment();
					filterFragment
							.setOnFilterSelectedListener(InventoryUpdateFragment.this);
				}
				filterFragment
						.setSelectedProductSubCategory(selectedSubcategory);
				filterFragment
						.setProductFilterType(ProductFilterType.BRAND_FILTER);
				FragmentTransaction ft = getFragmentManager()
						.beginTransaction();
				ft.replace(R.id.second_content_framelayout, filterFragment,
						"filter");
				if (!filterFragment.isAdded())
					ft.addToBackStack(filterFragment.getTag());
				ft.commit();
			}
			brandButton.setSelected(!brandButton.isSelected());
		}
	};

	OnClickListener onInventoryClickListner = new OnClickListener() {

		@Override
		public void onClick(View v) {
			/*
			 * if (v.getId() == R.id.inventory_prod_done_button) { if
			 * (inventorySkuUpdateList != null && inventorySkuUpdateList.size()
			 * > 0) { new UpdateInventoryTask(getActivity(),
			 * InventoryUpdateFragment.this, UPDATE_INVENTORY_PRODS_TASKCODE)
			 * .execute(inventorySkuUpdateList); } } else
			 */
			if (v.getId() == R.id.inventory_prod_add_button) {
				if (queryProdCategory != null
						&& !queryProdCategory.isCancelled()) {
					queryProdCategory.cancel(true);
				}
				if (getSubCategoriesBrandsTask != null
						&& !getSubCategoriesBrandsTask.isCancelled()) {
					getSubCategoriesBrandsTask.cancel(true);
				}
				if (queryProductSubCategories != null
						&& !queryProductSubCategories.isCancelled()) {
					queryProductSubCategories.cancel(true);
				}
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
			} else if (v.getId() == R.id.actionbar_update_information_layout) {
				if (!actionbarHeaderLayout.isSelected()) {
					actionbarHeaderLayout.setSelected(true);
					categorySelectorLayout.setSelected(false);
					productSearchLayout.setVisibility(View.VISIBLE);
					brandButton.setSelected(false);
					if (productSearchLayout.getVisibility() != View.VISIBLE) {
						productSearchEditText.setText("");
						menu.findItem(R.id.search_meuitem).setVisible(false);
					}
					if (filterFragment != null && filterFragment.isAdded()) {
						getFragmentManager().popBackStack();
					}
				}
			}
		}
	};

	public void addProductToInventory(InventorySku inventorySku) {

		inventorySku.getProductSku().setSelected(true);
		if (getActivity().getActionBar() != null
				&& getActivity().getActionBar().getCustomView().getId() == R.id.actionbar_update_layout
				&& getActivity().findViewById(R.id.search_meuitem) != null)
			getActivity().findViewById(R.id.search_meuitem).setVisibility(
					View.INVISIBLE);
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
		if (menuItem.getItemId() == R.id.search_meuitem) {
			// if(SnapToolkitConstants.IS_REPORT.equalsIgnoreCase("1")){
			setGoneSerach();
			// }
			menuItem.setVisible(false);
			
			productSearchLayout.setVisibility(View.VISIBLE);
			productSearchEditText.requestFocus();
			InputMethodManager imm = (InputMethodManager) getActivity()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(productSearchEditText,
					InputMethodManager.SHOW_IMPLICIT);
			return true;
		}
		SnapToolkitConstants.IS_REPORT = "0";
		onActionbarNavigationListener.onActionbarNavigation("",
				menuItem.getItemId());
		return true;
	}

	@Override
	public void onTaskSuccess(Object responseList, int taskCode) {
		if (getActivity() == null)
			return;
		if (taskCode == GET_CATEGORIES_TASKCODE) {
			Log.d(InventoryUpdateFragment.class.getName(),
					"category response list size "
							+ ((List<ProductCategory>) responseList).size());
			List<ProductCategory> productCategoryList = (List<ProductCategory>) responseList;
			SnapToolkitConstants.CAT_VALUE = productCategoryList.get(0)
					.getCategoryName();
			categoryTextView.setText(productCategoryList.get(0)
					.getCategoryName());
			if (productSearchEditText != null
					&& productSearchTextWatcher != null) {
				productSearchEditText
						.removeTextChangedListener(productSearchTextWatcher);
				productSearchEditText.setText("");
				productSearchEditText
						.addTextChangedListener(productSearchTextWatcher);
			}

			if (queryProductSubCategories != null
					&& !queryProductSubCategories.isCancelled()) {
				queryProductSubCategories.cancel(true);
			}
			queryProductSubCategories = new QueryProductSubCategories(
					getActivity(), InventoryUpdateFragment.this,
					GET_SUBCATEGORIES_TASKCODE);
			queryProductSubCategories.execute(productCategoryList.get(0)
					.getCategoryId());
		} else if (taskCode == GET_SUBCATEGORIES_TASKCODE) {
			Log.d(InventoryUpdateFragment.class.getName(),
					"subcategory response list size "
							+ ((List<ProductCategory>) responseList).size());
			SnapToolkitConstants.SUB_CAT_VALUE = ((List<ProductCategory>) responseList)
					.get(0).getCategoryName().toString();
			subcategoryTextView.setText(((List<ProductCategory>) responseList)
					.get(0).getCategoryName().toString());
			updateHeaderTextView.setText(((List<ProductCategory>) responseList)
					.get(0).getCategoryName().toString());

			if (productSearchEditText != null
					&& productSearchTextWatcher != null) {
				productSearchEditText
						.removeTextChangedListener(productSearchTextWatcher);
				productSearchEditText.setText("");
				productSearchEditText
						.addTextChangedListener(productSearchTextWatcher);
			}
			// subCategoryAdapter.lastSelectedPos = 0;

			if (getSubCategoriesBrandsTask != null
					&& !getSubCategoriesBrandsTask.isCancelled()) {
				getSubCategoriesBrandsTask.cancel(true);
			}
			getSubCategoriesBrandsTask = new GetSubCategoriesBrandsTask(
					getActivity(), InventoryUpdateFragment.this,
					GET_BRANDS_TASKCODE);
			selectedSubcategory = ((List<ProductCategory>) responseList).get(0);
			getSubCategoriesBrandsTask.execute(selectedSubcategory
					.getCategoryId());
		} else if (taskCode == GET_BRANDS_TASKCODE) {
			if (inventoryProductsAdapter != null)
				// if(SnapToolkitConstants.IS_REPORT.equalsIgnoreCase("0")){
				inventoryProductsAdapter.clear();
			ArrayList<Brand> brandList = (ArrayList<Brand>) responseList;
			for (int i = 0; i < brandList.size(); i++) {
				brandList.get(i).setSelected(false);
			}
			if (this.brandList == null) {
				this.brandList = new ArrayList<Brand>();
				this.brandList.addAll(brandList);
			} else {
				this.brandList.clear();
				this.brandList.addAll(brandList);
			}
			Integer[] brandIds = new Integer[brandList.size()];
			int i = 0;
			for (Brand brand : (ArrayList<Brand>) responseList) {
				brandIds[i] = brand.getBrandId();
				i++;
			}
			if (getBrandInventoryProductsTask != null
					&& !getBrandInventoryProductsTask.isCancelled()) {
				getBrandInventoryProductsTask.cancel(true);
			}
			if (selectedSubcategory != null)
				getBrandInventoryProductsTask = new GetBrandInventoryProductsTask(
						getActivity(), InventoryUpdateFragment.this,
						GET_BRAND_INVENTORY_PRODS_TASKCODE,
						selectedSubcategory.getCategoryId());
			else
				getBrandInventoryProductsTask = new GetBrandInventoryProductsTask(
						getActivity(), InventoryUpdateFragment.this,
						GET_BRAND_INVENTORY_PRODS_TASKCODE, -5);
			getBrandInventoryProductsTask.executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR, brandIds);

			
		} else if (taskCode == GET_BRAND_INVENTORY_PRODS_TASKCODE) {
			if (inventoryProductCursorAdapter == null) {
				inventoryProductCursorAdapter = new InventoryProductCursorAdapter(
						getActivity(), (Cursor) responseList, true, this,
						this.menu, this);
				if (getView() != null)
					((ListView) getView().findViewById(
							R.id.inventory_products_list))
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

		} else if (taskCode == ADD_INVENTORY_PROD_TASKCODE) {
			InventorySku invProd = (InventorySku) responseList;
			invProd.getProductSku().setSelected(true);
			if (toUpdateFieldName != null && !toUpdateFieldName.equals("")) {
				new UpdateInventoryTask(getActivity(),
						InventoryUpdateFragment.this,
						UPDATE_INVENTORY_PRODS_TASKCODE, toUpdateFieldName,
						invProd.getProductSku().getProductSkuCode())
						.execute(toUpdateFieldVal);
				toUpdateFieldName = "";
				toUpdateFieldVal = "";
			}
			/*
			 * for(int i=0;i<inventoryProductCursorAdapter.getCount();i++){
			 * if(invProd
			 * .getProductSku().getProductSkuCode().equals(inventoryProductsAdapter
			 * .getItem(i).getProductSku().getProductSkuCode())){
			 * inventoryProductsAdapter
			 * .getItem(i).getProductSku().setSelected(true); break; } }
			 */
			inventoryProductCursorAdapter.getCursor().requery();
			inventoryProductCursorAdapter.notifyDataSetChanged();
		} else if (taskCode == UPDATE_INVENTORY_PRODS_TASKCODE) {
			// inventorySkuUpdateList.clear();
			if (inventoryProductCursorAdapter != null) {
				inventoryProductCursorAdapter.getCursor().requery();
				inventoryProductCursorAdapter.notifyDataSetChanged();
			}
		} else if (taskCode == SEARCH_INVENTORY_TASKCODE) {
			if (inventorySkuUpdateList != null
					&& inventorySkuUpdateList.size() > 0)
				SnapCommonUtils.showAlert(getActivity(), "",
						getString(R.string.msg_update_changes),
						positiveClickListener, negativeClickListener, true);
			brandButton.setVisibility(View.GONE);
			categorySelectorLayout.setVisibility(View.INVISIBLE);
			updateHeaderTextView.setVisibility(View.INVISIBLE);
			keypadDismissListener.onClick(keypadContainerLayout);
			if (inventoryProductCursorAdapter == null) {
				inventoryProductCursorAdapter = new InventoryProductCursorAdapter(
						getActivity(), (Cursor) responseList, true, this, menu,
						this);
				if (getView() != null)
					((ListView) getView().findViewById(
							R.id.inventory_products_list))
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
		} else if (taskCode == GET_INVENTORY_BATCH_TASKCODE) {
			if (getView() != null)
				getView().findViewById(R.id.inventory_batches_relativelayout)
						.setVisibility(View.VISIBLE);
			if (inventoryBatchAdapter == null) {
				inventoryBatchAdapter = new InventoryBatchAdapter(
						getActivity(), (List<InventoryBatch>) responseList,
						this);
				inventoryBatchListView.setAdapter(inventoryBatchAdapter);
			} else {
				inventoryBatchAdapter.clear();
				inventoryBatchAdapter
						.addAll((Collection<? extends InventoryBatch>) responseList);
			}
		} else if (taskCode == UPADTE_INVENTORY_BATCH_TASKCODE) {
			onInventoryBatchDismissListener.onClick(getView().findViewById(
					R.id.inventory_batches_relativelayout));
			if (inventoryProductCursorAdapter != null) {
				inventoryProductCursorAdapter.getCursor().requery();

			}
			/*
			 * for (int i = 0; i < inventoryProductCursorAdapter.getCount();
			 * i++) { if (inventorySku.getSlNo() ==
			 * inventoryProductCursorAdapter.getItem( i).getSlNo()) {
			 * InventorySku inventoryUpdateSku = inventoryProductsAdapter
			 * .getItem(i); inventoryUpdateSku.setPurchasePrice(inventorySku
			 * .getPurchasePrice());
			 * inventorySku.getProductSku().setProductCategory(
			 * inventoryUpdateSku.getProductSku() .getProductCategory());
			 * inventorySku.getProductSku().setSelected(true);
			 * inventoryUpdateSku.setProductSku(inventorySku .getProductSku());
			 * inventoryUpdateSku.setQuantity(inventorySku.getQuantity());
			 * inventoryUpdateSku.setTaxRate(inventorySku.getTaxRate());
			 * inventoryProductsAdapter.notifyDataSetChanged(); break; } }
			 */
			inventoryBatchUpdateList.clear();
		} else if (taskCode == GET_SELECTED_INVENTORY_TASKCODE) {
			onAddNewProductListener
					.showAddProductLayout((InventorySku) responseList);
		} else if (taskCode == GET_SELECTED_INVENTORY_DEL_TASKCODE) {
			new RemoveInventoryProductTask(getActivity(),
					InventoryUpdateFragment.this,
					REMOVE_INVENTORY_PROD_TASKCODE)
					.execute((InventorySku) responseList);
		} else if (taskCode == GET_SELECTED_INVENTORY_BATCH_TASKCODE) {
			new GetInventoryBatchesTask(getActivity(), this,
					GET_INVENTORY_BATCH_TASKCODE)
					.execute(((InventorySku) responseList).getProductSku());
		}
		
	}

	@Override
	public void onTaskError(String errorMessage, int taskCode) {
		// TODO Auto-generated method stub
		if (getActivity() == null)
			return;
		CustomToast.showCustomToast(getActivity(), errorMessage,
				Toast.LENGTH_SHORT, CustomToast.ERROR);
		if (taskCode == GET_SUBCATEGORIES_TASKCODE) {

		} else if (taskCode == GET_BRANDS_TASKCODE) {
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
			keypadDismissListener.onClick(keypadContainerLayout);
			brandButton.setVisibility(View.GONE);
			categorySelectorLayout.setVisibility(View.INVISIBLE);
			if (inventoryProductsAdapter != null) {
				inventoryProductsAdapter.clear();
				if (getView() != null)
					((TextView) getView().findViewById(
							R.id.inventory_totalqty_textview))
							.setText("0 items");
			}
		} else if (taskCode == UPADTE_INVENTORY_BATCH_TASKCODE) {
			onInventoryBatchDismissListener.onClick(getView().findViewById(
					R.id.inventory_batches_relativelayout));
			inventoryBatchUpdateList.clear();
		} else if (taskCode == GET_SELECTED_INVENTORY_TASKCODE) {

		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		this.menu = menu;
		menu.findItem(R.id.search_meuitem).setVisible(false);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onProductValueEdit(float value, int context) {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		if (keypadFragment == null) {
			keypadFragment = new NumKeypadFragment();
			keypadFragment.setKeypadEnterListener(this);
		}
		keypadFragment.setValue(String.valueOf(value));
		keypadFragment.setContext(context);
		keypadFragment.setNewEdit(true);
		ft.replace(R.id.keypad_frame_layout, keypadFragment);

		keypadContainerLayout.setVisibility(View.VISIBLE);
		if (!keypadFragment.isAdded()) {
			ft.addToBackStack("");
		}
		ft.commit();
	}

	@Override
	public void onNumKeyPadEnter(String value, int context) {

		if (inventoryProductCursorAdapter.getLastSelectedPos() >= inventoryProductCursorAdapter
				.getCount()) {
			inventoryProductCursorAdapter.setLastSelectedPos(0);
			getFragmentManager().popBackStack();
			return;
		}
		InventorySku updateSku = inventoryProductCursorAdapter
				.getLastSelectedItem();
		InventoryBatch updateBatch = null;
		if (inventoryBatchAdapter != null)
			updateBatch = inventoryBatchAdapter.getLastSelectedItem();
		switch (context) {
		case MRP_CONTEXT:
			updateSku.getProductSku().setProductSkuMrp(Float.parseFloat(value));
			if (updateSku.isOffer()) {
				if (updateSku.getProductSku().getProductSkuSalePrice() >= updateSku
						.getProductSku().getProductSkuMrp()) {
					updateInventorySku(updateSku, "is_offer", false);
				}
			}
			if (!updateSku.getProductSku().isSelected()) {
				onAddItemToInventory(updateSku);
				toUpdateFieldName = "sku_mrp";
				toUpdateFieldVal = value + "";
			} else {
				new UpdateInventoryTask(getActivity(),
						InventoryUpdateFragment.this,
						UPDATE_INVENTORY_PRODS_TASKCODE, "sku_mrp", updateSku
								.getProductSku().getProductSkuCode())
						.execute(value + "");
			}
			if (updateSku.getProductSku().getProductSkuSalePrice() > updateSku
					.getProductSku().getProductSkuMrp()) {
				updateSku
						.getProductSku()
						.setProductSkuSalePrice(
								updateSku.getProductSku()
										.getProductSkuSalePrice() > updateSku
										.getProductSku().getProductSkuMrp() ? updateSku
										.getProductSku().getProductSkuMrp()
										: updateSku.getProductSku()
												.getProductSkuSalePrice());
				new UpdateInventoryTask(getActivity(),
						InventoryUpdateFragment.this,
						UPDATE_INVENTORY_PRODS_TASKCODE, "sku_saleprice",
						updateSku.getProductSku().getProductSkuCode())
						.execute(updateSku.getProductSku()
								.getProductSkuSalePrice() + "");
			}
			break;
		case STOCKQTY_CONTEXT:
			if (updateSku.getProductSku().getProductSkuUnits()
					.equals(SkuUnitType.PC)) {
				value = String.valueOf(Math.floor(Float.parseFloat(value)));
				updateSku.setQuantity(Float.parseFloat(value));
			} else
				updateSku.setQuantity(Float.parseFloat(value));
			if (!updateSku.getProductSku().isSelected()) {
				onAddItemToInventory(updateSku);
				toUpdateFieldName = "inventory_sku_quantity";
				toUpdateFieldVal = value + "";
			} else
				new UpdateInventoryTask(getActivity(),
						InventoryUpdateFragment.this,
						UPDATE_INVENTORY_PRODS_TASKCODE,
						"inventory_sku_quantity", updateSku.getProductSku()
								.getProductSkuCode()).execute(value + "");
			break;
		case PURCHASE_PRICE_CONTEXT:
			updateSku.setPurchasePrice(Float.parseFloat(value));
			if (!updateSku.getProductSku().isSelected()) {
				onAddItemToInventory(updateSku);
				toUpdateFieldName = "purchase_price";
				toUpdateFieldVal = value + "";
			} else {
				if (updateSku.getPurchasePrice() > updateSku.getProductSku().getProductSkuMrp()) {
					value = String.valueOf(updateSku.getProductSku().getProductSkuMrp());
				}
				new UpdateInventoryTask(getActivity(),
						InventoryUpdateFragment.this,
						UPDATE_INVENTORY_PRODS_TASKCODE, "purchase_price",
						updateSku.getProductSku().getProductSkuCode())
						.execute(value + "");
			}
			break;
		case SALES_PRICE_CONTEXT:
			updateSku.getProductSku().setProductSkuSalePrice(
					Float.parseFloat(value));
			if (updateSku.isOffer()) {
				if (updateSku.getProductSku().getProductSkuSalePrice() >= updateSku
						.getProductSku().getProductSkuMrp()) {
					updateInventorySku(updateSku, "is_offer", false);
				}
			}
			if (!updateSku.getProductSku().isSelected()) {
				onAddItemToInventory(updateSku);
			} else {
				if (updateSku.getProductSku().getProductSkuSalePrice() > updateSku
						.getProductSku().getProductSkuMrp()) {
					value = updateSku.getProductSku().getProductSkuMrp() + "";
				}
				new UpdateInventoryTask(getActivity(),
						InventoryUpdateFragment.this,
						UPDATE_INVENTORY_PRODS_TASKCODE, "sku_saleprice",
						updateSku.getProductSku().getProductSkuCode())
						.execute(value + "");
			}
			break;
		case SALES_PRICE_TWO_CONTEXT:
			updateSku.getProductSku().setProductSkuSalePrice(
					Float.parseFloat(value));
			if (updateSku.isOffer()) {
				if (updateSku.getProductSku().getProductSkuSalePrice2() >= updateSku
						.getProductSku().getProductSkuMrp()) {
					updateInventorySku(updateSku, "is_offer", false);
				}
			}
			if (!updateSku.getProductSku().isSelected()) {
				onAddItemToInventory(updateSku);
			} else {
				if (updateSku.getProductSku().getProductSkuSalePrice() > updateSku
						.getProductSku().getProductSkuMrp()) {
					value = updateSku.getProductSku().getProductSkuMrp() + "";
				}
				new UpdateInventoryTask(getActivity(),
						InventoryUpdateFragment.this,
						UPDATE_INVENTORY_PRODS_TASKCODE, "sku_saleprice_two",
						updateSku.getProductSku().getProductSkuCode())
						.execute(value + "");
			}
			break;
		case SALES_PRICE_THREE_CONTEXT:
			updateSku.getProductSku().setProductSkuSalePrice(
					Float.parseFloat(value));
			if (updateSku.isOffer()) {
				if (updateSku.getProductSku().getProductSkuSalePrice() >= updateSku
						.getProductSku().getProductSkuMrp()) {
					updateInventorySku(updateSku, "is_offer", false);
				}
			}
			if (!updateSku.getProductSku().isSelected()) {
				onAddItemToInventory(updateSku);
			} else {
				if (updateSku.getProductSku().getProductSkuSalePrice() > updateSku
						.getProductSku().getProductSkuMrp()) {
					value = updateSku.getProductSku().getProductSkuMrp() + "";
				}
				new UpdateInventoryTask(getActivity(),
						InventoryUpdateFragment.this,
						UPDATE_INVENTORY_PRODS_TASKCODE, "sku_saleprice_three",
						updateSku.getProductSku().getProductSkuCode())
						.execute(value + "");
			}
			break;
		case BATCH_AVAILABLEQTY_CONTEXT:
			updateBatch.setBatchAvailableQty(Integer.parseInt(value));
			if (Integer.parseInt(value) > updateBatch.getBatchQty())
				updateBatch.setBatchQty(Integer.parseInt(value));
			updateInventoryBatch(updateBatch);
			break;
		case BATCH_MRP_CONTEXT:
			updateBatch.setBatchMrp(Float.parseFloat(value));
			updateInventoryBatch(updateBatch);
			break;
		case BATCH_PURCHASEPRICE_CONTEXT:
			updateBatch.setBatchPurchasePrice(Float.parseFloat(value));
			updateInventoryBatch(updateBatch);
			break;
		case BATCH_SALESPRICE_CONTEXT:
			updateBatch.setBatchSalesPrice(Float.parseFloat(value));
			updateInventoryBatch(updateBatch);
			break;
		}
		if (keypadContainerLayout.getVisibility() == View.VISIBLE) {
			keypadContainerLayout.setVisibility(View.GONE);
			getFragmentManager().popBackStack();
		}
		if (inventoryProductCursorAdapter != null
				&& inventoryProductCursorAdapter.lastSelectedView != null) {
			inventoryProductCursorAdapter.lastSelectedView.setSelected(false);
			inventoryProductCursorAdapter.notifyDataSetChanged();
			inventoryProductCursorAdapter.lastSelectedView = null;
		}
		if (inventoryBatchAdapter != null
				&& inventoryBatchAdapter.lastSelectedView != null) {
			inventoryBatchAdapter.lastSelectedView.setSelected(false);
			inventoryBatchAdapter.notifyDataSetChanged();
			inventoryBatchAdapter.lastSelectedView = null;
		}

	}

	@Override
	public void onRemoveItem(InventorySku product) {
		// TODO Auto-generated method stub
		inventoryProductCursorAdapter.notifyDataSetChanged();
		SnapCommonUtils
				.showDeleteAlert(getActivity(), "", "Confirm Delete?",
						positiveDeleteClickListener,
						negativeDeleteClickListener, false);
		deleteSku = product;
	}

	@Override
	public void onShowBatches(int position) {
		// TODO Auto-generated method stub
		ProductSku productSku = inventoryProductCursorAdapter
				.getLastSelectedItem().getProductSku();
		new GetSelectedInvSkuTask(getActivity(), InventoryUpdateFragment.this,
				GET_SELECTED_INVENTORY_BATCH_TASKCODE).execute(productSku
				.getProductSkuCode());

	}

	@Override
	public void onBatchValueEdit(float value, int context) {
		// TODO Auto-generated method stub
		onProductValueEdit(value, context);
	}

	@Override
	public void onBatchDateEdit(String date) {
		// TODO Auto-generated method stub

	}

	public void updateInventoryBatch(InventoryBatch inventoryBatch) {
		// TODO Auto-generated method stub
		if (inventoryBatchUpdateList == null) {
			inventoryBatchUpdateList = new ArrayList<InventoryBatch>();
		}
		if (!inventoryBatchUpdateList.contains(inventoryBatch))
			inventoryBatchUpdateList.add(inventoryBatch);
	}

	@Override
	public void onEditProduct(InventorySku inventorySku) {
		// TODO Auto-generated method stub
		/*
		 * if (inventorySkuUpdateList != null && inventorySkuUpdateList.size() >
		 * 0) SnapCommonUtils.showAlert(getActivity(), "",
		 * "Do you want to update the changes?", positiveClickListener,
		 * negativeClickListener, true); else
		 */
		new GetSelectedInvSkuTask(getActivity(), this,
				GET_SELECTED_INVENTORY_TASKCODE).execute(inventorySku
				.getProductSku().getProductSkuCode());
	}

	public void dismissNumpad() {
		keypadDismissListener.onClick(keypadContainerLayout);
		// keypadFragment = null;
	}

	OnClickListener positiveClickListener = new OnClickListener() {

		@SuppressWarnings("unchecked")
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			/*
			 * new UpdateInventoryTask(getActivity(),
			 * InventoryUpdateFragment.this, UPDATE_INVENTORY_PRODS_TASKCODE)
			 * .execute(inventorySkuUpdateList); SnapCommonUtils.dismissAlert();
			 */
		}
	};

	OnClickListener negativeClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (inventorySkuUpdateList != null)
				inventorySkuUpdateList.clear();
			SnapCommonUtils.dismissAlert();
		}
	};

	OnClickListener positiveDeleteClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (deleteSku != null)
				new GetSelectedInvSkuTask(getActivity(),
						InventoryUpdateFragment.this,
						GET_SELECTED_INVENTORY_DEL_TASKCODE).execute(deleteSku
						.getProductSku().getProductSkuCode());

			deleteSku = null;
			SnapCommonUtils.dismissAlert();
		}
	};

	OnClickListener negativeDeleteClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
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
		if (inventorySkuUpdateList != null && inventorySkuUpdateList.size() > 0)
			SnapCommonUtils.showAlert(getActivity(), "",
					getString(R.string.msg_update_changes),
					positiveClickListener, negativeClickListener, true);
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
		// TODO Auto-generated method stub

	}

	@Override
	public void onAddItemToInventory(InventorySku inventorySku) {
		new AddProductToInventoryTask(getActivity(),
				InventoryUpdateFragment.this, ADD_INVENTORY_PROD_TASKCODE)
				.execute(inventorySku);
	}

	@Override
	public void onProductSubCategorySelected(
			ProductCategory selectedProductSubCategory) {
		menu.findItem(R.id.search_meuitem).setVisible(false);
		selectedSubcategory = selectedProductSubCategory;
		if (selectedSubcategory != null) {

			categoryTextView.setText(selectedSubcategory.getParenCategory()
					.getCategoryName());
			subcategoryTextView.setText(selectedSubcategory.getCategoryName());
			SnapToolkitConstants.CAT_VALUE = selectedSubcategory
					.getParenCategory().getCategoryName();
			SnapToolkitConstants.SUB_CAT_VALUE = selectedSubcategory
					.getCategoryName();
			updateHeaderTextView.setText(selectedSubcategory.getCategoryName());
			getFragmentManager().popBackStack();
			categorySelectorLayout.setSelected(false);
			if (getSubCategoriesBrandsTask != null
					&& !getSubCategoriesBrandsTask.isCancelled()) {
				getSubCategoriesBrandsTask.cancel(true);
			}
			getSubCategoriesBrandsTask = new GetSubCategoriesBrandsTask(
					getActivity(), InventoryUpdateFragment.this,
					GET_BRANDS_TASKCODE);
			getSubCategoriesBrandsTask.execute(selectedSubcategory
					.getCategoryId());
		}
	}

	public boolean isFilterVisible() {
		if (filterFragment != null)
			return filterFragment.isVisible();
		else
			return false;
	}

	public void popFilterFragmentandResetButtonStates() {
		menu.findItem(R.id.search_meuitem).setVisible(false);
		categorySelectorLayout.setSelected(false);
		brandButton.setSelected(false);
		getFragmentManager().popBackStack();
		actionbarHeaderLayout.setSelected(true);
		productSearchLayout.setVisibility(View.VISIBLE);
	}

	@Override
	public void updateInventorySku(InventorySku inventorySku, String fieldName,
			boolean value) {
		// TODO Auto-generated method stub
		new UpdateInventoryTask(getActivity(), InventoryUpdateFragment.this,
				UPDATE_INVENTORY_PRODS_TASKCODE, fieldName, inventorySku
						.getProductSku().getProductSkuCode()).execute(value
				+ "");
	}

	public void setGoneSerach() {
		SnapToolkitConstants.IS_REPORT = "0";
		productSearchLayout = (RelativeLayout) getActivity().getActionBar()
				.getCustomView().findViewById(R.id.search_layout);
		productSearchEditText = (EditText) getActivity().findViewById(
				R.id.search_edittext);
		productSearchLayout.setVisibility(View.VISIBLE);
		productSearchEditText.addTextChangedListener(productSearchTextWatcher);

		productSearchEditText.requestFocus();
		InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(productSearchEditText,
				InputMethodManager.SHOW_IMPLICIT);
	}

	@Override
	public void getResponse() {
		SnapToolkitConstants.SKU_REPORT_COUNT = 0;
		subcategoryTextView = (TextView) getActivity().findViewById(
				R.id.update_sub_category_name_textview);
		categoryTextView = ((TextView) getActivity().findViewById(
				R.id.update_category_name_textview));
		categorySelectorLayout = (RelativeLayout) getActivity().findViewById(
				R.id.update_category_layout);
		categorySelectorLayout
				.setOnClickListener(onCategoryChooseClickListener);

		brandButton = (RelativeLayout) getActivity().findViewById(
				R.id.update_brands_layout);
		brandButton.setOnClickListener(brandButtonClickListener);
		actionbarHeaderLayout = (RelativeLayout) getActivity().findViewById(
				R.id.actionbar_update_information_layout);
		actionbarHeaderLayout.setSelected(true);
		actionbarHeaderLayout.setOnClickListener(onInventoryClickListner);
		if (queryProdCategory != null && !queryProdCategory.isCancelled()) {
			queryProdCategory.cancel(true);
		}

		((TextView) getActivity().getActionBar().getCustomView()
				.findViewById(R.id.owner_name_textview))
				.setText(SnapSharedUtils.getStoreDetails(
						SnapCommonUtils.getSnapContext(getActivity()))
						.getStoreName());

	}

}
