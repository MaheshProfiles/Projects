package com.snapbizz.snapstock.fragments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
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
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.snapbizz.snapstock.R;
import com.snapbizz.snapstock.adapters.OrderListAdapter;
import com.snapbizz.snapstock.adapters.OrderListAdapter.InventoryOrderEditListener;
import com.snapbizz.snapstock.asynctasks.FilterProductsByBrandsTask;
import com.snapbizz.snapstock.asynctasks.FilterProductsByCategoryTask;
import com.snapbizz.snapstock.asynctasks.GetBrandsByDistributorTask;
import com.snapbizz.snapstock.asynctasks.GetCompaniesTaggedToDistributorTask;
import com.snapbizz.snapstock.asynctasks.GetDistributorOrderListTask;
import com.snapbizz.snapstock.asynctasks.GetDistributorProductMapByDistributorTask;
import com.snapbizz.snapstock.asynctasks.GetGlobalProductsTask;
import com.snapbizz.snapstock.asynctasks.SaveInventoryDistributorOrder;
import com.snapbizz.snapstock.domains.ProductBean;
import com.snapbizz.snapstock.fragments.FilterFragment.OnFilterSelectedListener;
import com.snapbizz.snapstock.interfaces.OnAddNewProductListener;
import com.snapbizz.snapstock.utils.ProductFilterType;
import com.snapbizz.snapstock.utils.SnapInventoryConstants;
import com.snapbizz.snaptoolkit.customviews.SoftInputEditText;
import com.snapbizz.snaptoolkit.domains.Brand;
import com.snapbizz.snaptoolkit.domains.Company;
import com.snapbizz.snaptoolkit.domains.Distributor;
import com.snapbizz.snaptoolkit.domains.DistributorProductMap;
import com.snapbizz.snaptoolkit.domains.InventorySku;
import com.snapbizz.snaptoolkit.domains.ProductCategory;
import com.snapbizz.snaptoolkit.fragments.NumKeypadFragment;
import com.snapbizz.snaptoolkit.fragments.NumKeypadFragment.NumberKeypadEnterListener;
import com.snapbizz.snaptoolkit.interfaces.OnActionbarNavigationListener;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.CustomToast;
import com.snapbizz.snaptoolkit.utils.SnapToolkitTextFormatter;

public class InventoryOrderFragment extends Fragment implements
		OnQueryCompleteListener, InventoryOrderEditListener,
		NumberKeypadEnterListener, OnFilterSelectedListener {

	private final String TAG = InventoryOrderFragment.class.getSimpleName();
	private final int STOCK_TO_ORDER = 0;
	private final int GET_DISTRIBUTOR_BRAND_TASKCODE = 1;
	private final int GET_DISTRIBUTOR_COMPANIES_TASKCODE = 2;
	private final int DISTRIBUTOR_PRODUCT_TASK = 3;
	private final int SAVE_DISTRIBUTOR_PURCHASE_ORDER = 4;
	private final int FILTER_PRODUCTS_BY_CATEGORY_TASKCODE = 7;
	private final int FILTER_PRODUCTS_BY_BRANDS_TASKCODE = 8;
	private final int SEARCH_PRODUCT_TASK = 9;
	private final int GET_DISTRIBUTOR_PRODUCT_MAP_TASKCODE = 10;
	private float totalOrderAmount;
	private float headerTotalValue;
	private float headerValueByCategory;

	private GetCompaniesTaggedToDistributorTask getCompaniesTaggedToDistributorTask;
	private GetBrandsByDistributorTask getBrandsByDistributorTask;
	private FilterProductsByBrandsTask filterDistributorProductsByBrandsTask;
	private SaveInventoryDistributorOrder saveInventoryDistributorOrder;
	private GetDistributorOrderListTask getDistributorOrderListTask;
	private FilterProductsByCategoryTask filterProductsByCategoryTask;
	private GetGlobalProductsTask getGlobalProductsTask;
	private GetDistributorProductMapByDistributorTask getDistributorProductMapByDistributorTask;

	private OrderListAdapter orderListAdapter;
	private SimpleDateFormat dateFormat;
	private FilterFragment filterFragment;
	private NumKeypadFragment keypadFragment;
	private ListView orderListView;
	private TextView categoryNameTextView;
	private TextView subCategoryNameTextView;
	private TextView headerValueTextView;
	private TextView headerCountTextView;
	private TextView headerNameTextView;
	private TextView summaryDistributorNameTextView;
	private TextView summaryDistributorBrandsTextView;
	private TextView summaryDistributorNumberTextView;
	private TextView summaryLineItemsTextView;
	private TextView summaryQuantityTextView;
	private TextView summaryMRPTextView;
	private TextView summaryAgencyNameTextView;
	private TextView summaryPaymentAmountTextView;
	private SoftInputEditText searchProductEditText;
	private HashMap<String, ProductBean> productBeanMap;
	private HashMap<Integer, Brand> brandMap;
	private HashMap<Integer, Company> companyMap;
	private HashMap<String, DistributorProductMap> distributorProductHashMap;
	private List<Brand> filterBrandList;
	private List<ProductBean> productList;
	private List<ProductBean> selectedItemList;
	private List<ProductBean> filteredListByCategory;
	private ProductBean productBean;
	private Distributor distributor;
	private ProductCategory productCategory;
	private Menu menu;
	private OnActionbarNavigationListener onActionbarNavigationListener;
	private String stringKeypadFragment = "keypad_order_fragment";
	private String stringFilterFragment = "filter_fragment";
	private String allCategories = "All Categories";
	private String currentDate;
	private Boolean isToOrder;
	private Button summaryCancelBtn;
	private Button summaryConfirmBtn;
	private Button clearSearchButton;
	private FrameLayout keypadLayout;
	private RelativeLayout actionbarBrandFilterLayout;
	private RelativeLayout actionbarCategoryLayout;
	private RelativeLayout actionbarDistributorInfoLayout;
	private RelativeLayout summaryLayout;
	private RelativeLayout transparentOverlay;
	private RelativeLayout productSearchLayout;
	private OnAddNewProductListener onAddNewProductListener;

	public void setPurchaseOrderDistributorFragmentData(
			Distributor distributor, Boolean isToOrder) {
		this.distributor = distributor;
		this.isToOrder = isToOrder;

	}

	public void setNewProductAdded(InventorySku inventorySku) {
		if (!brandMap.containsKey(inventorySku.getProductSku()
				.getProductBrand().getBrandId())) {
			brandMap.put(inventorySku.getProductSku().getProductBrand()
					.getBrandId(), inventorySku.getProductSku()
					.getProductBrand());
		}
		if (!companyMap.containsKey(inventorySku.getProductSku()
				.getProductBrand().getCompany().getCompanyId())) {
			companyMap.put(inventorySku.getProductSku().getProductBrand()
					.getCompany().getCompanyId(), inventorySku.getProductSku()
					.getProductBrand().getCompany());
		}
		if (!distributorProductHashMap.containsKey(inventorySku.getProductSku()
				.getProductSkuCode())) {
			DistributorProductMap distributorProductMap = new DistributorProductMap();
			distributorProductMap.setDistributor(distributor);
			distributorProductMap.setDistributorProductSku(inventorySku
					.getProductSku());
			distributorProductHashMap.put(inventorySku.getProductSku()
					.getProductSkuCode(), distributorProductMap);
		}
		addNewCreatedProduct(inventorySku);
		addToOrderList();
	}

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.distributor_order_fragment,
				container, false);

		view.findViewById(R.id.btnShowOverLay).setOnClickListener(
				onOverLayButtonClicked);
		view.findViewById(R.id.order_summary_overlay_button)
				.setOnClickListener(onOverLayButtonClicked);
		transparentOverlay = (RelativeLayout) view
				.findViewById(R.id.clickable_overlay);
		transparentOverlay.setOnClickListener(onTransparentOverlayClicked);
		orderListView = (ListView) view
				.findViewById(R.id.order_product_listview);
		keypadLayout = (FrameLayout) view
				.findViewById(R.id.overlay_distributor_order_fragment);

		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		try {
			onActionbarNavigationListener = (OnActionbarNavigationListener) activity;
		} catch (Exception e) {
			throw new ClassCastException("activity " + activity.toString()
					+ " must implement OnActionbarNavigationListener =");
		}
		try {
			onAddNewProductListener = (OnAddNewProductListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ "must implement OnAddNewProductListener");
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		this.menu = menu;
		menu.findItem(R.id.search_meuitem).setVisible(false);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {

		if (R.id.search_meuitem == menuItem.getItemId()) {
			menuItem.setVisible(false);
			productSearchLayout.setVisibility(View.VISIBLE);
			searchProductEditText.requestFocus();
			((InputMethodManager) getActivity().getSystemService(
					Context.INPUT_METHOD_SERVICE)).showSoftInput(
					searchProductEditText, InputMethodManager.SHOW_FORCED);
			return true;
		}
		onActionbarNavigationListener.onActionbarNavigation("",
				menuItem.getItemId());

		return true;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);

		if (getActivity().getActionBar().getCustomView().getId() != R.id.actionbar_order_id_layout)
			getActivity().getActionBar().setCustomView(
					R.layout.actionbar_order_layout);

		getActivity().findViewById(R.id.add_prod_button).setOnClickListener(
				onAddProductClickListener);
		((TextView) getActivity().getActionBar().getCustomView()
				.findViewById(R.id.distributor_name_textview))
				.setText(distributor.getAgencyName());
		((TextView) getActivity().getActionBar().getCustomView()
				.findViewById(R.id.distributor_company_name_textview))
				.setText(distributor.getCompanyNames());
		actionbarCategoryLayout = (RelativeLayout) getActivity().getActionBar()
				.getCustomView()
				.findViewById(R.id.actionbar_order_category_layout);
		actionbarCategoryLayout.setOnClickListener(onClickListeners);
		actionbarBrandFilterLayout = (RelativeLayout) getActivity()
				.getActionBar().getCustomView()
				.findViewById(R.id.actionbar_order_brand_filter_layout);
		actionbarBrandFilterLayout.setOnClickListener(onClickListeners);
		categoryNameTextView = (TextView) getActivity().getActionBar()
				.getCustomView()
				.findViewById(R.id.order_category_name_textview);
		subCategoryNameTextView = (TextView) getActivity().getActionBar()
				.getCustomView()
				.findViewById(R.id.order_sub_category_name_textview);
		actionbarCategoryLayout.setOnClickListener(onClickListeners);
		productSearchLayout = (RelativeLayout) getActivity().getActionBar()
				.getCustomView().findViewById(R.id.search_layout);
		clearSearchButton = (Button) getActivity().getActionBar()
				.getCustomView().findViewById(R.id.clear_search_text_button);
		clearSearchButton.setOnClickListener(onClickListeners);
		searchProductEditText = (SoftInputEditText) getActivity()
				.getActionBar().getCustomView()
				.findViewById(R.id.search_edittext);
		searchProductEditText.addTextChangedListener(textWatcherProductSearch);
		actionbarDistributorInfoLayout = (RelativeLayout) getActivity()
				.getActionBar()
				.getCustomView()
				.findViewById(
						R.id.actionbar_order_distributor_information_layout);
		actionbarDistributorInfoLayout.setOnClickListener(onClickListeners);
		headerNameTextView = (TextView) getView().findViewById(
				R.id.order_header_name_text_view);
		headerCountTextView = (TextView) getView().findViewById(
				R.id.order_header_count_textview);
		headerValueTextView = (TextView) getView().findViewById(
				R.id.order_header_total_value_textview);
		summaryLayout = (RelativeLayout) getView().findViewById(
				R.id.order_summary_overlay);
		summaryDistributorNameTextView = (TextView) summaryLayout
				.findViewById(R.id.order_summary_distributor_name_text_view);
		summaryDistributorBrandsTextView = (TextView) summaryLayout
				.findViewById(R.id.order_summary_distributor_brands_text_view);
		summaryDistributorNumberTextView = (TextView) summaryLayout
				.findViewById(R.id.order_summary_distributor_number_text_view);
		summaryLineItemsTextView = (TextView) summaryLayout
				.findViewById(R.id.order_summary_line_item_text_view);
		summaryQuantityTextView = (TextView) summaryLayout
				.findViewById(R.id.order_summary_quantity_text_view);
		summaryMRPTextView = (TextView) summaryLayout
				.findViewById(R.id.order_summary_mrp_text_view);
		summaryAgencyNameTextView = (TextView) summaryLayout
				.findViewById(R.id.order_summary_agency_name_text_view);
		summaryPaymentAmountTextView = (TextView) summaryLayout
				.findViewById(R.id.order_summary_payment_amount_text_view);
		summaryCancelBtn = (Button) summaryLayout
				.findViewById(R.id.order_summary_cancel_button);
		summaryCancelBtn.setOnClickListener(onClickListeners);
		summaryConfirmBtn = (Button) summaryLayout
				.findViewById(R.id.order_summary_confirm_button);
		summaryConfirmBtn.setOnClickListener(onClickListeners);
		actionbarDistributorInfoLayout.setSelected(true);
		dateFormat = new SimpleDateFormat(
				SnapInventoryConstants.DATE_FORMAT_DD_MM_YYYY, Locale.US);
		currentDate = dateFormat.format(Calendar.getInstance().getTime());
		headerCountTextView.setText(String.valueOf("0"));
		if (null == productBeanMap) {
			productBeanMap = new HashMap<String, ProductBean>();
		}
		if (null == brandMap) {
			brandMap = new HashMap<Integer, Brand>();
		}
		if (null == companyMap) {
			companyMap = new HashMap<Integer, Company>();
		}
		if (null == distributorProductHashMap) {
			distributorProductHashMap = new HashMap<String, DistributorProductMap>();
		}
		if (null == productCategory) {
			categoryNameTextView.setText(allCategories);
			subCategoryNameTextView.setText(allCategories);
			headerNameTextView.setText(allCategories);
			headerValueTextView.setText(SnapToolkitTextFormatter
					.formatPriceText(headerTotalValue, getActivity()));
			if (null != productList && productList.size() > 0) {
				headerCountTextView.setText(String.valueOf(productList.size()));
			}
		} else {
			categoryNameTextView.setText(productCategory.getParenCategory()
					.getCategoryName());
			subCategoryNameTextView.setText(productCategory.getCategoryName());
			headerNameTextView.setText(productCategory.getCategoryName());
			headerValueTextView.setText(SnapToolkitTextFormatter
					.formatPriceText(headerValueByCategory, getActivity()));
			if (null != filteredListByCategory
					&& filteredListByCategory.size() > 0) {
				headerCountTextView.setText(String
						.valueOf(filteredListByCategory.size()));
			}
		}
		if (null != orderListAdapter) {
			orderListView.setAdapter(orderListAdapter);
		} else {
			getDistributorOrderListTask = new GetDistributorOrderListTask(
					getActivity(), DISTRIBUTOR_PRODUCT_TASK,
					InventoryOrderFragment.this);
			getDistributorOrderListTask.executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR,
					distributor.getDistributorId());

			getBrandsByDistributorTask = new GetBrandsByDistributorTask(
					getActivity(), InventoryOrderFragment.this,
					GET_DISTRIBUTOR_BRAND_TASKCODE);
			getBrandsByDistributorTask.executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR,
					distributor.getDistributorId());

			getCompaniesTaggedToDistributorTask = new GetCompaniesTaggedToDistributorTask(
					getActivity(), InventoryOrderFragment.this,
					GET_DISTRIBUTOR_COMPANIES_TASKCODE);
			getCompaniesTaggedToDistributorTask.executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR,
					distributor.getDistributorId());

			getDistributorProductMapByDistributorTask = new GetDistributorProductMapByDistributorTask(
					getActivity(), GET_DISTRIBUTOR_PRODUCT_MAP_TASKCODE,
					InventoryOrderFragment.this);
			getDistributorProductMapByDistributorTask.executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR,
					distributor.getDistributorId());
		}
	}

	OnClickListener onAddProductClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			onAddNewProductListener.showAddProductLayout("", distributor);
		}
	};

	View.OnClickListener onClickListeners = new View.OnClickListener() {

		@SuppressLint("NewApi")
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			if (R.id.clear_search_text_button == v.getId()) {

				if (searchProductEditText.getText().toString().trim().length() > 0) {
					headerNameTextView.setText(allCategories);
					searchProductEditText.setText("");
					if (isKeypadVisibile()) {
						popKeypadFragment();
					}
					setListDataAfterClearSearch();
				}

			} else if (R.id.actionbar_order_distributor_information_layout == v
					.getId()) {

				if (isFilterFragmentVisible()) {
					hideFilterFragment();
				}
				actionbarDistributorInfoLayout.setSelected(true);
				menu.findItem(R.id.search_meuitem).setVisible(false);
				showActionbarFilterLayouts();
				productSearchLayout.setVisibility(View.VISIBLE);
				searchProductEditText.setText("");
				headerNameTextView.setText(allCategories);
				headerValueTextView.setText(SnapToolkitTextFormatter
						.formatPriceText(headerTotalValue, getActivity()));
				categoryNameTextView.setText(allCategories);
				subCategoryNameTextView.setText(allCategories);
				if (null != filterFragment) {
					filterFragment.resetFilters();
				}
				if (null != orderListAdapter) {
					orderListAdapter.clear();
					if (null != productList && productList.size() > 0) {
						headerCountTextView.setText(String.valueOf(productList
								.size()));
						orderListAdapter.addAll(productList);
					}
					orderListAdapter.notifyDataSetChanged();
				}

			} else if (R.id.actionbar_order_brand_filter_layout == v.getId()) {

				if (null != filterFragment
						&& filterFragment.isAdded()
						&& filterFragment.getProductFilterType().ordinal() == ProductFilterType.BRAND_FILTER
								.ordinal()) {
					getFragmentManager().popBackStack();
					actionbarBrandFilterLayout.setSelected(false);
					menu.findItem(R.id.search_meuitem).setVisible(false);
					productSearchLayout.setVisibility(View.GONE);
					if (null != brandMap && brandMap.size() > 0) {
						filterBrandList = filterFragment.getSelectedBrand();
					}
					if (null != filterBrandList && filterBrandList.size() > 0) {
						headerNameTextView.setText("Brand Filter");
						if (null != productList && productList.size() > 0) {
							filterDistributorProductsByBrandsTask = new FilterProductsByBrandsTask(
									InventoryOrderFragment.this,
									FILTER_PRODUCTS_BY_BRANDS_TASKCODE,
									filterBrandList, productList);
							filterDistributorProductsByBrandsTask.execute();
						}
					} else {
						if (null == productCategory) {
							headerNameTextView.setText(allCategories);
							if (null != orderListAdapter) {
								orderListAdapter.clear();
								if (null != productList
										&& productList.size() > 0) {
									headerCountTextView.setText(String
											.valueOf(productList.size()));
									headerValueTextView
											.setText(SnapToolkitTextFormatter
													.formatPriceText(
															headerTotalValue,
															getActivity()));
									orderListAdapter.addAll(productList);
								}
								orderListAdapter.notifyDataSetChanged();
							}
						} else {
							headerNameTextView.setText(productCategory
									.getCategoryName());
							headerValueTextView
									.setText(SnapToolkitTextFormatter
											.formatPriceText(
													headerValueByCategory,
													getActivity()));
							if (null != orderListAdapter) {
								orderListAdapter.clear();
								if (null != filteredListByCategory
										&& filteredListByCategory.size() > 0) {
									headerCountTextView.setText(String
											.valueOf(filteredListByCategory
													.size()));
									orderListAdapter
											.addAll(filteredListByCategory);
								}
								orderListAdapter.notifyDataSetChanged();
							}
						}
					}
				} else {
					if (isKeypadVisibile()) {
						popKeypadFragment();
					}
					FragmentTransaction ft = getFragmentManager()
							.beginTransaction();
					if (null == filterFragment) {
						filterFragment = new FilterFragment();
						filterFragment.setShowAllCategories(true);
						ft.add(R.id.second_content_framelayout, filterFragment,
								stringFilterFragment);
						ft.addToBackStack(stringFilterFragment);
					} else {
						ft.replace(R.id.second_content_framelayout,
								filterFragment, stringFilterFragment);
					}
					filterFragment.setDistributor(distributor);
					filterFragment
							.setProductFilterType(ProductFilterType.BRAND_FILTER);
					filterFragment
							.setOnFilterSelectedListener(InventoryOrderFragment.this);
					if (!filterFragment.isAdded()) {
						ft.addToBackStack(stringFilterFragment);
					}
					ft.commit();
					actionbarBrandFilterLayout.setSelected(true);
					actionbarBrandFilterLayout.setVisibility(View.GONE);
					actionbarCategoryLayout.setSelected(false);
					actionbarDistributorInfoLayout.setSelected(false);
					menu.findItem(R.id.search_meuitem).setVisible(false);
					productSearchLayout.setVisibility(View.VISIBLE);
					searchProductEditText.setText("");
				}

			} else if (R.id.order_summary_cancel_button == v.getId()) {

				summaryLayout.setVisibility(View.GONE);

			} else if (R.id.order_summary_confirm_button == v.getId()) {

				summaryConfirmBtn.setClickable(false);
				saveInventoryDistributorOrder = new SaveInventoryDistributorOrder(
						getActivity(), InventoryOrderFragment.this,
						SAVE_DISTRIBUTOR_PURCHASE_ORDER, totalOrderAmount,
						currentDate, selectedItemList, distributor, brandMap,
						companyMap, distributorProductHashMap);
				saveInventoryDistributorOrder.execute();

			} else if (R.id.actionbar_order_category_layout == v.getId()) {

				if (null != filterFragment
						&& filterFragment.isAdded()
						&& filterFragment.getProductFilterType().ordinal() == ProductFilterType.CATEGORY_FILTER
								.ordinal()) {
					getFragmentManager().popBackStack();
					actionbarCategoryLayout.setSelected(false);
					menu.findItem(R.id.search_meuitem).setVisible(false);
					productSearchLayout.setVisibility(View.GONE);
				} else {
					if (isKeypadVisibile()) {
						popKeypadFragment();
					}
					FragmentTransaction ft = getFragmentManager()
							.beginTransaction();
					if (null == filterFragment) {
						filterFragment = new FilterFragment();
						filterFragment.setShowAllCategories(true);
						ft.add(R.id.second_content_framelayout, filterFragment,
								stringFilterFragment);
						ft.addToBackStack(stringFilterFragment);
					} else {
						ft.replace(R.id.second_content_framelayout,
								filterFragment, stringFilterFragment);
					}
					filterFragment
							.setProductFilterType(ProductFilterType.CATEGORY_FILTER);
					filterFragment
							.setOnFilterSelectedListener(InventoryOrderFragment.this);
					if (!filterFragment.isAdded()) {
						ft.addToBackStack(stringFilterFragment);
					}
					ft.commit();
					actionbarCategoryLayout.setSelected(true);
					actionbarBrandFilterLayout.setSelected(false);
					actionbarBrandFilterLayout.setVisibility(View.GONE);
					actionbarDistributorInfoLayout.setSelected(false);
					menu.findItem(R.id.search_meuitem).setVisible(false);
					productSearchLayout.setVisibility(View.GONE);
					searchProductEditText.setText("");
				}

			}
		}
	};

	public void orderScannedProduct(InventorySku inventorySku) {
		if (null != productList && productList.size() > 0) {
			boolean isProductFound = false;
			loop: for (int i = 0; i < productList.size(); i++) {
				if (productList
						.get(i)
						.getProductSkuID()
						.equalsIgnoreCase(
								inventorySku.getProductSku()
										.getProductSkuCode())) {
					productBean = productList.get(i);
					isProductFound = true;
					if (!productBean.isSelected()) {
						selectProduct();
					} else {
						productBean.incProductToOrder();
						orderListAdapter.notifyDataSetChanged();
						CustomToast.showCustomToast(getActivity(),
								getString(R.string.increase_order_qty),
								Toast.LENGTH_SHORT, CustomToast.INFORMATION);
					}
					if (i > 0) {
						productList.remove(i);
						productList.add(0, productBean);
						orderListAdapter.clear();
						orderListAdapter.addAll(productList);
						orderListView.smoothScrollToPosition(0);
					}
					break loop;
				}
			}
			if (!isProductFound) {
				addNewCreatedProduct(inventorySku);
				addToOrderList();
			}
		} else {
			addNewCreatedProduct(inventorySku);
			addToOrderList();
		}
	}

	TextWatcher textWatcherProductSearch = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			if (s.length() > 0) {
				keyStrokeTimer.cancel();
				keyStrokeTimer.start();
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub

		}
	};

	CountDownTimer keyStrokeTimer = new CountDownTimer(
			SnapInventoryConstants.KEY_STROKE_TIMEOUT,
			SnapInventoryConstants.KEY_STROKE_TIMEOUT) {

		@Override
		public void onTick(long arg0) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onFinish() {
			// TODO Auto-generated method stub
			String keyword = searchProductEditText.getText().toString().trim();

			if (keyword.length() > 0) {
				if (isKeypadVisibile()) {
					popKeypadFragment();
				}
				headerNameTextView.setText(keyword);
				getGlobalProductsTask = new GetGlobalProductsTask(
						getActivity(), SEARCH_PRODUCT_TASK,
						InventoryOrderFragment.this, productBeanMap);
				getGlobalProductsTask.execute(keyword);
			} else {
				setListDataAfterClearSearch();
			}
		}
	};

	@SuppressWarnings("unchecked")
	@SuppressLint("NewApi")
	@Override
	public void onTaskSuccess(Object responseList, int taskCode) {
		// TODO Auto-generated method stub
		if (null == getActivity())
			return;

		if (DISTRIBUTOR_PRODUCT_TASK == taskCode) {

			productList = (List<ProductBean>) responseList;
			if (null == orderListAdapter) {
				orderListAdapter = new OrderListAdapter(getActivity(),
						new ArrayList<ProductBean>(),
						InventoryOrderFragment.this);
				orderListAdapter.addAll(productList);
				orderListView.setAdapter(orderListAdapter);
			} else {
				orderListAdapter.clear();
				orderListAdapter.addAll(productList);
				orderListAdapter.notifyDataSetChanged();
			}
			headerTotalValue = 0;
			headerCountTextView.setText(String.valueOf(productList.size()));
			for (ProductBean object : productList) {
				if (object.getProductQty() > 0) {
					headerTotalValue += object.getProductPrice()
							* object.getProductQty();
				}
				productBeanMap.put(object.getProductSkuID(), object);
			}
			headerValueTextView.setText(SnapToolkitTextFormatter
					.formatPriceText(headerTotalValue, getActivity()));
			if (null != selectedItemList && selectedItemList.size() > 0) {
				selectedItemList.clear();
			}

		} else if (FILTER_PRODUCTS_BY_BRANDS_TASKCODE == taskCode) {

			float filteredHeaderValue = 0;
			List<ProductBean> filteredList = (List<ProductBean>) responseList;
			headerCountTextView.setText(String.valueOf(filteredList.size()));
			orderListAdapter.clear();
			orderListAdapter.addAll(filteredList);
			orderListAdapter.notifyDataSetChanged();
			for (ProductBean bean : filteredList) {
				if (bean.getProductQty() > 0) {
					filteredHeaderValue += bean.getProductQty()
							* bean.getProductPrice();
				}
			}
			headerValueTextView.setText(SnapToolkitTextFormatter
					.formatPriceText(filteredHeaderValue, getActivity()));

		} else if (FILTER_PRODUCTS_BY_CATEGORY_TASKCODE == taskCode) {

			headerValueByCategory = 0;
			filteredListByCategory = (List<ProductBean>) responseList;
			headerCountTextView.setText(String.valueOf(filteredListByCategory
					.size()));
			orderListAdapter.clear();
			orderListAdapter.addAll(filteredListByCategory);
			orderListAdapter.notifyDataSetChanged();
			for (ProductBean bean : filteredListByCategory) {
				if (bean.getProductQty() > 0) {
					headerValueByCategory += bean.getProductQty()
							* bean.getProductPrice();
				}
			}
			headerValueTextView.setText(SnapToolkitTextFormatter
					.formatPriceText(headerValueByCategory, getActivity()));

		} else if (taskCode == SAVE_DISTRIBUTOR_PURCHASE_ORDER) {

			CustomToast.showCustomToast(getActivity(),
					getString(R.string.order_successful), Toast.LENGTH_SHORT,
					CustomToast.SUCCESS);
			onActionbarNavigationListener.onActionbarNavigation(
					SnapInventoryConstants.CONFIRM_BUTTON_CLICKED,
					android.R.id.home);
			onActionbarNavigationListener.onActionbarNavigation(
					SnapInventoryConstants.CONFIRM_BUTTON_CLICKED,
					android.R.id.home);

		} else if (SEARCH_PRODUCT_TASK == taskCode) {

			hideActionbarFilterLayouts();
			float filteredHeaderValue = 0;
			List<ProductBean> filteredList = (List<ProductBean>) responseList;
			if (null == orderListAdapter) {
				orderListAdapter = new OrderListAdapter(getActivity(),
						filteredList, InventoryOrderFragment.this);
				orderListView.setAdapter(orderListAdapter);
			} else {
				orderListAdapter.clear();
				orderListAdapter.addAll((List<ProductBean>) responseList);
				orderListAdapter.notifyDataSetChanged();
			}
			headerCountTextView.setText(String.valueOf(filteredList.size()));
			for (ProductBean bean : filteredList) {
				if (bean.getProductQty() > 0) {
					filteredHeaderValue += bean.getProductQty()
							* bean.getProductPrice();
				}
			}
			headerValueTextView.setText(SnapToolkitTextFormatter
					.formatPriceText(filteredHeaderValue, getActivity()));

		} else if (GET_DISTRIBUTOR_BRAND_TASKCODE == taskCode) {

			brandMap = (HashMap<Integer, Brand>) responseList;

		} else if (GET_DISTRIBUTOR_COMPANIES_TASKCODE == taskCode) {

			companyMap = (HashMap<Integer, Company>) responseList;

		} else if (GET_DISTRIBUTOR_PRODUCT_MAP_TASKCODE == taskCode) {

			distributorProductHashMap = (HashMap<String, DistributorProductMap>) responseList;

		}
	}

	@Override
	public void onTaskError(String errorMessage, int taskCode) {
		// TODO Auto-generated method stub
		if (getActivity() == null)
			return;

		if (FILTER_PRODUCTS_BY_BRANDS_TASKCODE == taskCode) {

			orderListAdapter.clear();
			orderListAdapter.notifyDataSetChanged();

		} else if (FILTER_PRODUCTS_BY_CATEGORY_TASKCODE == taskCode) {

			orderListAdapter.clear();
			orderListAdapter.notifyDataSetChanged();
			if (null != filteredListByCategory
					&& filteredListByCategory.size() > 0) {
				filteredListByCategory.clear();
			}

		} else if (SEARCH_PRODUCT_TASK == taskCode) {

			hideActionbarFilterLayouts();
			if (null != orderListAdapter) {
				orderListAdapter.clear();
				orderListAdapter.notifyDataSetChanged();
			}

		} else if (GET_DISTRIBUTOR_PRODUCT_MAP_TASKCODE == taskCode) {
			if (null != distributorProductHashMap) {
				distributorProductHashMap.clear();
			}
		} else if (SAVE_DISTRIBUTOR_PURCHASE_ORDER == taskCode) {
			summaryConfirmBtn.setClickable(true);
		}
		headerValueTextView.setText(SnapToolkitTextFormatter.formatPriceText(0,
				getActivity()));
		headerCountTextView.setText("0");
		if (null != getActivity()) {
			CustomToast.showCustomToast(getActivity(), errorMessage,
					Toast.LENGTH_SHORT, CustomToast.ERROR);
		}

	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		searchProductEditText
				.removeTextChangedListener(textWatcherProductSearch);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (null != searchProductEditText)
			;
		searchProductEditText.addTextChangedListener(textWatcherProductSearch);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d("purchase order distributor fragment", "inside on destory");
		searchProductEditText.setText("");
		totalOrderAmount = 0;
		headerTotalValue = 0;

		clearAllListandMap();

		if (null != productCategory) {
			productCategory = null;
		}
		if (null != orderListAdapter) {
			orderListAdapter = null;
		}
		if (null != distributor) {
			distributor = null;
		}
		if (null != filterFragment) {
			filterFragment = null;
		}
		if (null != filterDistributorProductsByBrandsTask
				&& !filterDistributorProductsByBrandsTask.isCancelled()) {
			filterDistributorProductsByBrandsTask.cancel(true);
		}
		if (null != saveInventoryDistributorOrder
				&& !saveInventoryDistributorOrder.isCancelled()) {
			saveInventoryDistributorOrder.cancel(true);
		}
		if (null != getBrandsByDistributorTask
				&& !getBrandsByDistributorTask.isCancelled()) {
			getBrandsByDistributorTask.cancel(true);
		}
		if (null != getCompaniesTaggedToDistributorTask
				&& !getCompaniesTaggedToDistributorTask.isCancelled()) {
			getCompaniesTaggedToDistributorTask.cancel(true);
		}
		if (null != getGlobalProductsTask
				&& !getGlobalProductsTask.isCancelled()) {
			getGlobalProductsTask.cancel(true);
		}
	}

	@Override
	public void onStockQuantityEdit(int position) {
		// TODO Auto-generated method stub

		productBean = orderListAdapter.getItem(position);
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		if (keypadFragment == null) {
			keypadFragment = new NumKeypadFragment();
			keypadFragment.setKeypadEnterListener(InventoryOrderFragment.this);
		}
		ft.replace(R.id.overlay_distributor_order_fragment, keypadFragment,
				stringKeypadFragment);
		if (!keypadFragment.isAdded()) {
			ft.addToBackStack(stringKeypadFragment);
		}
		ft.commit();
		fm.executePendingTransactions();
		keypadFragment.setContext(STOCK_TO_ORDER);
		keypadFragment
				.setValue(String.valueOf(productBean.getProductToOrder()));
		transparentOverlay.setVisibility(View.VISIBLE);
		keypadLayout.setVisibility(View.VISIBLE);
		RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		rlParams.setMargins(750, 170, 0, 0);
		keypadLayout.setLayoutParams(rlParams);
	}

	@Override
	public void onCheckViewClicked(int position) {

		productBean = orderListAdapter.getItem(position);
		if (productBean.isSelected()) {
			removeFromToOrderList();
		} else {
			selectProduct();
		}
	}

	@Override
	public void onProductNameClicked(int position) {

		productBean = orderListAdapter.getItem(position);
		if (productBean.isSelected()) {
			removeFromToOrderList();
		} else {
			selectProduct();
		}

	}

	@Override
	public void onProductImageClicked(int position) {

		productBean = orderListAdapter.getItem(position);
		if (productBean.isSelected()) {
			removeFromToOrderList();
		} else {
			selectProduct();
		}

	}

	@Override
	public void onNumKeyPadEnter(String value, int contextKeypad) {
		// TODO Auto-generated method stub
		int inputValue = (int) Double.parseDouble(value);
		if (contextKeypad == STOCK_TO_ORDER) {
			if (inputValue == 0) {
				CustomToast.showCustomToast(getActivity(),
						getString(R.string.change_order_qty), Toast.LENGTH_SHORT,
						CustomToast.INFORMATION);
			} else {

				productBean.setProductToOrder(inputValue);

				if (!productBean.isSelected()) {
					selectProduct();
				} else {
					if (null != orderListAdapter) {
						orderListAdapter.notifyDataSetChanged();
					}
				}
				popKeypadFragment();
			}
		}
	}

	private void hideActionbarFilterLayouts() {

		actionbarBrandFilterLayout.setVisibility(View.INVISIBLE);
		actionbarCategoryLayout.setVisibility(View.INVISIBLE);
	}

	private void showActionbarFilterLayouts() {

		actionbarBrandFilterLayout.setVisibility(View.GONE);
		actionbarCategoryLayout.setVisibility(View.VISIBLE);
	}

	private void addToOrderList() {
		if (null == selectedItemList) {
			selectedItemList = new ArrayList<ProductBean>();
		}
		if (!productBeanMap.containsKey(productBean.getProductSkuID())) {
			if (null == productList) {
				productList = new ArrayList<ProductBean>();
			}
			productList.add(0, productBean);
			productBeanMap.put(productBean.getProductSkuID(), productBean);
		}
		productBean.setSelected(true);
		selectedItemList.add(productBean);
		headerNameTextView.setText(allCategories);
		categoryNameTextView.setText(allCategories);
		subCategoryNameTextView.setText(allCategories);
		if (null != orderListAdapter) {
			orderListAdapter.clear();
			if (null != productList && productList.size() > 0) {
				headerCountTextView.setText(String.valueOf(productList.size()));
				headerValueTextView.setText(SnapToolkitTextFormatter
						.formatPriceText(headerTotalValue, getActivity()));
				orderListAdapter.addAll(productList);
			}
			orderListAdapter.notifyDataSetChanged();
		} else {
			orderListAdapter = new OrderListAdapter(getActivity(),
					new ArrayList<ProductBean>(), InventoryOrderFragment.this);
			orderListAdapter.addAll(productList);
			orderListView.setAdapter(orderListAdapter);
		}
		if (searchProductEditText.getText().toString().trim().length() > 0) {
			searchProductEditText.setText("");
		}
	}

	private void removeFromToOrderList() {

		productBean.setSelected(false);
		selectedItemList.remove(productBean);
		orderListAdapter.notifyDataSetChanged();
	}

	private void selectProduct() {

		showActionbarFilterLayouts();
		if (null != productBean && productBean.getProductToOrder() > 0) {
			addToOrderList();
		} else {
			if (null != productBean) {
				productBean.setProductToOrder(1);
			}
			addToOrderList();
		}
	}

	public void popKeypadFragment() {
		getFragmentManager().popBackStack(stringKeypadFragment,
				FragmentManager.POP_BACK_STACK_INCLUSIVE);
		transparentOverlay.setVisibility(View.GONE);
		orderListAdapter.selectedView.setEnabled(true);
		orderListAdapter.notifyDataSetChanged();
	}

	public boolean isKeypadVisibile() {
		if (null != keypadFragment && keypadFragment.isAdded()) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isFilterFragmentVisible() {
		if (null != filterFragment && filterFragment.isAdded())
			return true;
		else
			return false;
	}

	public void hideFilterFragment() {
		actionbarBrandFilterLayout.setSelected(false);
		actionbarBrandFilterLayout.setVisibility(View.GONE);
		actionbarCategoryLayout.setSelected(false);
		actionbarDistributorInfoLayout.setSelected(true);
		menu.findItem(R.id.search_meuitem).setVisible(false);
		getFragmentManager().popBackStack();
		productSearchLayout.setVisibility(View.VISIBLE);
	}

	private void clearAllListandMap() {
		if (null != filterBrandList && filterBrandList.size() > 0) {
			filterBrandList.clear();
		}
		if (null != productList && productList.size() > 0) {
			productList.clear();
		}
		if (null != productBeanMap && productBeanMap.size() > 0) {
			productBeanMap.clear();
		}
		if (null != brandMap && brandMap.size() > 0) {
			brandMap.clear();
		}
		if (null != companyMap && companyMap.size() > 0) {
			companyMap.clear();
		}
		if (null != selectedItemList && selectedItemList.size() > 0) {
			selectedItemList.clear();
		}
		if (null != distributorProductHashMap
				&& distributorProductHashMap.size() > 0) {
			distributorProductHashMap.clear();
		}
	}

	private void addNewCreatedProduct(InventorySku inventorySku) {
		productBean = new ProductBean();
		productBean.setProductName(inventorySku.getProductSku()
				.getProductSkuName());
		productBean.setProductSkuID(inventorySku.getProductSku()
				.getProductSkuCode());
		productBean.setProductQty(inventorySku.getQuantity());
		productBean.setProductPrice(inventorySku.getProductSku()
				.getProductSkuMrp());
		productBean.setProductCategoryID(inventorySku.getProductSku()
				.getProductCategory().getCategoryId());
		productBean.setProductBrandID(inventorySku.getProductSku()
				.getProductBrand().getBrandId());
		productBean.setImageUri(inventorySku.getProductSku().getImageUrl());
		productBean.setGDB(inventorySku.getProductSku().isGDB());
		productBean.setInventorySerialNumber(inventorySku.getSlNo());
		productBean.setProductCompanyID(inventorySku.getProductSku()
				.getProductBrand().getCompany().getCompanyId());
		productBean.setProductToOrder(1);
	}

	private void setListDataAfterClearSearch() {
		showActionbarFilterLayouts();
		if (null == productCategory) {
			headerNameTextView.setText(allCategories);
			if (null != orderListAdapter) {
				orderListAdapter.clear();
				headerValueTextView.setText(SnapToolkitTextFormatter
						.formatPriceText(headerTotalValue, getActivity()));
				if (null != productList && productList.size() > 0) {
					headerCountTextView.setText(String.valueOf(productList
							.size()));
					orderListAdapter.addAll(productList);
				} else {
					headerCountTextView.setText("0");
				}
				orderListAdapter.notifyDataSetChanged();
			}
		} else {
			headerNameTextView.setText(productCategory.getCategoryName());
			if (null != orderListAdapter) {
				orderListAdapter.clear();
				headerValueTextView.setText(SnapToolkitTextFormatter
						.formatPriceText(headerValueByCategory, getActivity()));
				if (null != filteredListByCategory
						&& filteredListByCategory.size() > 0) {
					headerCountTextView.setText(String
							.valueOf(filteredListByCategory.size()));
					orderListAdapter.addAll(filteredListByCategory);
				} else {
					headerCountTextView.setText("0");
				}
				orderListAdapter.notifyDataSetChanged();
			}
		}
	}

	View.OnClickListener onTransparentOverlayClicked = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (isKeypadVisibile()) {
				popKeypadFragment();
			}
			if (null != transparentOverlay && transparentOverlay.isShown()) {
				transparentOverlay.setVisibility(View.GONE);
			}
			if (null != orderListAdapter) {
				orderListAdapter.notifyDataSetChanged();
			}
		}
	};

	View.OnClickListener onOverLayButtonClicked = new View.OnClickListener() {

		@SuppressLint("NewApi")
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			if (R.id.btnShowOverLay == v.getId()) {
				if (isKeypadVisibile()) {
					popKeypadFragment();
				}
				summaryLayout.setVisibility(View.VISIBLE);
				summaryDistributorNameTextView.setText(distributor
						.getSalesmanName());
				summaryDistributorBrandsTextView.setText(distributor
						.getCompanyNames());
				summaryDistributorNumberTextView.setText(distributor
						.getPhoneNumber());
				int totalQuantity = 0;
				totalOrderAmount = 0;
				summaryLineItemsTextView.setText("0");
				if (null != selectedItemList && selectedItemList.size() > 0) {
					summaryLineItemsTextView.setText(String
							.valueOf(selectedItemList.size()));
					for (ProductBean bean : selectedItemList) {
						totalQuantity += bean.getProductToOrder();
						totalOrderAmount += bean.getProductPrice()
								* bean.getProductToOrder();
					}
				}
				summaryQuantityTextView.setText(String.valueOf(totalQuantity));
				summaryMRPTextView.setText(SnapToolkitTextFormatter
						.formatPriceText(totalOrderAmount, getActivity()));
				summaryAgencyNameTextView.setText(distributor.getAgencyName());
				summaryPaymentAmountTextView.setText(SnapToolkitTextFormatter
						.formatPriceText(totalOrderAmount, getActivity()));

			} else {
				summaryLayout.setVisibility(View.GONE);
			}

		}
	};

	@Override
	public void onProductSubCategorySelected(
			ProductCategory selectedProductSubCategory) {
		// TODO Auto-generated method stub

		getFragmentManager().popBackStack();
		productCategory = selectedProductSubCategory;
		actionbarCategoryLayout.setSelected(false);
		menu.findItem(R.id.search_meuitem).setVisible(false);
		if (null != selectedProductSubCategory) {
			headerNameTextView.setText(selectedProductSubCategory
					.getCategoryName());
			categoryNameTextView.setText(selectedProductSubCategory
					.getParenCategory().getCategoryName());
			subCategoryNameTextView.setText(selectedProductSubCategory
					.getCategoryName());
			if (null != productList && productList.size() > 0) {
				filterProductsByCategoryTask = new FilterProductsByCategoryTask(
						FILTER_PRODUCTS_BY_CATEGORY_TASKCODE,
						InventoryOrderFragment.this, productList);
				filterProductsByCategoryTask.execute(selectedProductSubCategory
						.getCategoryId());
			}
		} else {
			headerNameTextView.setText(allCategories);
			categoryNameTextView.setText(allCategories);
			subCategoryNameTextView.setText(allCategories);
			headerValueTextView.setText(SnapToolkitTextFormatter
					.formatPriceText(headerTotalValue, getActivity()));
			if (null != orderListAdapter) {
				orderListAdapter.clear();
				if (null != productList && productList.size() > 0) {
					orderListAdapter.addAll(productList);
					headerCountTextView.setText(String.valueOf(productList
							.size()));
				} else {
					headerCountTextView.setText("0");
				}
				orderListAdapter.notifyDataSetChanged();
			}
		}
	}

}
