package com.snapbizz.snapstock.fragments;

import java.io.FileNotFoundException;
import java.io.IOException;
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
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.snapbizz.snapstock.R;
import com.snapbizz.snapstock.adapters.OrderReceiveOrderNumberAdapter;
import com.snapbizz.snapstock.adapters.ReceiveListAdapter;
import com.snapbizz.snapstock.adapters.ReceiveListAdapter.OrderReceiveEditListener;
import com.snapbizz.snapstock.asynctasks.AddProductToExistingPO;
import com.snapbizz.snapstock.asynctasks.FilterProductsByBrandsTask;
import com.snapbizz.snapstock.asynctasks.FilterProductsByCategoryTask;
import com.snapbizz.snapstock.asynctasks.GetBrandsByDistributorTask;
import com.snapbizz.snapstock.asynctasks.GetCompaniesTaggedToDistributorTask;
import com.snapbizz.snapstock.asynctasks.GetDistributorPOTask;
import com.snapbizz.snapstock.asynctasks.GetDistributorProductMapByDistributorTask;
import com.snapbizz.snapstock.asynctasks.GetGlobalProductsTask;
import com.snapbizz.snapstock.asynctasks.GetProductsToReceiveTask;
import com.snapbizz.snapstock.asynctasks.GetPurchaseOrderListTask;
import com.snapbizz.snapstock.asynctasks.ReceiveWithoutPOTask;
import com.snapbizz.snapstock.asynctasks.UpdatePurchaseOrderReceiveTask;
import com.snapbizz.snapstock.domains.ProductBean;
import com.snapbizz.snapstock.fragments.FilterFragment.OnFilterSelectedListener;
import com.snapbizz.snapstock.interfaces.OnAddNewProductListener;
import com.snapbizz.snapstock.utils.ProductFilterType;
import com.snapbizz.snapstock.utils.SnapInventoryConstants;
import com.snapbizz.snaptoolkit.customviews.HorizontalListView;
import com.snapbizz.snaptoolkit.customviews.SoftInputEditText;
import com.snapbizz.snaptoolkit.domains.Brand;
import com.snapbizz.snaptoolkit.domains.Company;
import com.snapbizz.snaptoolkit.domains.Distributor;
import com.snapbizz.snaptoolkit.domains.DistributorProductMap;
import com.snapbizz.snaptoolkit.domains.InventorySku;
import com.snapbizz.snaptoolkit.domains.Order;
import com.snapbizz.snaptoolkit.domains.ProductCategory;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.fragments.NumKeypadFragment;
import com.snapbizz.snaptoolkit.fragments.NumKeypadFragment.NumberKeypadEnterListener;
import com.snapbizz.snaptoolkit.interfaces.OnActionbarNavigationListener;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.CustomToast;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitTextFormatter;


public class InventoryReceiveFragment extends Fragment implements OnQueryCompleteListener, OrderReceiveEditListener, NumberKeypadEnterListener, OnFilterSelectedListener{

    private final String TAG = InventoryReceiveFragment.class.getSimpleName();
	private final int GET_DISTRIBUTOR_PO_TASK = 1;
	private final int GET_PURCHASE_ORDER_SUB_LIST_TASK = 2;
	private final int FILTER_PRODUCTS_BY_BRANDS_TASKCODE = 3;
	private final int GET_DISTRIBUTORS_BRAND_TASKCODE = 4;
	private final int KEYPAD_FRAGMENT_RECEIVE_QTY_TASK_CODE = 5;
	private final int KEYPAD_FRAGMENT_PRODUCT_PRICE_TASK_CODE = 6;
	private final int KEYPAD_FRAGMENT_PRODUCT_DISCOUNT_TASK_CODE = 7;
	private final int UPDATE_PURCHASE_ORDER_TASK = 10;
	private final int KEYPAD_FRAGMENT_BILLED_QTY_TASK_CODE = 11;
	private final int KEYPAD_FRAGMENT_TOTAL_TASK_CODE = 12;
	private final int KEYPAD_FRAGMENT_TAX_RATE_TASK_CODE = 14;
	private final int GET_ALL_PRODUCTS_TASK_CODE = 15;
	private final int KEYPAD_MRP_TASK_CODE = 16;
	private final int GET_DISTRIBUTOR_PRODUCT_MAP_TASKCODE = 17;
	private final int RECEIVE_WITHOUT_PO_TASKCODE = 19;
	private final int ADD_PRODUCT_TO_EXISTING_PO_TASKCODE = 20;
	private final int FILTER_PRODUCTS_BY_CATEGORY_TASKCODE = 21;
	private final int CAPTURE_IMAGE_TASK_CODE = 22;
	private final int SEARCH_PRODUCT_TASK = 23;
	private final int GET_DISTRIBUTOR_COMPANIES_TASKCODE = 24;
	
	private float totalOrderAmount;
	private float summaryDiscountValue;
	private float summaryNetPayable;
	private float headerTotalValue;
	private float headerValueByCategory;

	private GetBrandsByDistributorTask getBrandsByDistributorTask;
	private GetDistributorPOTask getDistributorPOTask;
	private FilterProductsByBrandsTask filterDistributorProductsByBrandsTask;
	private UpdatePurchaseOrderReceiveTask updatePurchaseOrderReceiveTask;
	private ReceiveWithoutPOTask receiveWithoutPOTask;
	private AddProductToExistingPO addProductToExistingPO;
	private FilterProductsByCategoryTask filterProductsByCategoryTask;
	private GetProductsToReceiveTask getProductsToReceiveTask;
	private GetPurchaseOrderListTask getPurchaseOrderListTask;
	private GetGlobalProductsTask getGlobalProductsTask;
	private GetCompaniesTaggedToDistributorTask getCompaniesTaggedToDistributorTask;
	private GetDistributorProductMapByDistributorTask getDistributorProductMapByDistributorTask;
	
	private String stringKeypadFragment = "keypad_receive_fragment";
	private String allCategories = "All Categories";
	private String filterFragmentString = "filter_fragment";
	private String invoiceNumber;
	private SimpleDateFormat dateFormat;
	private OrderReceiveOrderNumberAdapter distributorPOAdapter;
	private ReceiveListAdapter receiveListAdapter;

	private ListView receiveListView;
	private HorizontalListView horizontalDistributorPOListView;
	private ImageView invoiceImageViewThumbnail;
	private ImageView invoiceImageView;
	private View leftShadowView;
	private View rightShadowView;
	private TextView purchaseOrdertext;
	private TextView headerValueTextView;
    private TextView headerCountTextView;
    private TextView headerNameTextView;
	private TextView categoryNameTextView;
    private TextView subCategoryNameTextView;
	private TextView summaryDistributorNameTextView;
    private TextView summaryDistributorBrandsTextView;
    private TextView summaryDistributorNumberTextView;
    private TextView summaryLineItemsTextView;
    private TextView summaryQuantityTextView;
    private TextView summaryMRPTextView;
    private TextView summaryAgencyNameTextView;
    private TextView summaryPaymentAmountTextView;
    private TextView summaryPurchaseValueTextView;
    private TextView summaryTaxTextView;
    private TextView summaryDiscountTextView;
    private TextView summaryNetPayableTextView;
    private SoftInputEditText invoiceNumberEditText;
	
    private Bitmap invoiceImageBitmap;
	
	private OnActionbarNavigationListener onActionbarNavigationListener;
	private SoftInputEditText searchProductEditText;
	private NumKeypadFragment keypadFragment;
	private FilterFragment filterFragment;
	
	private boolean isToOrder;
	
	/*
	 * list of brands according to which the 
	 * productList needs to be 
	 * filtered
	 */
	private List<Brand> filterBrandList;
	
	/*
	 * the list of the products that are being received is added to
	 * the selectedItemlist
	 */
	private List<ProductBean> selectedItemList;
	
	/*
	 *it contains all products that are being displayed
	 */
	private List<ProductBean> productList;
	/*
	 * contains all the products filtered according 
	 * to the filter brand list
	 */
	private List<ProductBean> filteredListByCategory;
	/*
	 * contains all the products that are tagged to the selected distributor
	 */
	private HashMap<String, DistributorProductMap> distributorProductHashMap;
	/*
	 * contains all products which are currently displayed
	 * this map is used for a quick look up to check when the
	 * search is performed
	 */
	private HashMap<String, ProductBean> productBeanMap;
	
	/*
	 * contains the IDs of the brands currently tagged to the 
	 * distributor
	 */
	private HashMap<Integer, Brand> brandMap;
	
	/*
     * contains the IDs of the companies currently tagged to the 
     * distributor
     */
	private HashMap<Integer, Company> companyMap;
	
	/*
	 * POJO used to display and store product details
	 */
	private ProductBean productBean;
	private Distributor distributor;
	private ProductCategory productCategory;
    private Order order;
	private Menu menu;
	private Button clearSearchButton;
	private Button summaryCancelBtn;
    private Button summaryConfirmBtn;
    private Button deleteInvoiceImageBtn;
	private Button recaptureInvoiceImageBtn;
	private Button invoiceImageDoneBtn;
	private FrameLayout keypadLayout;
	private RelativeLayout actionbarBrandFilterLayout;
	private RelativeLayout actionbarCategoryLayout;
	private RelativeLayout actionbarDistributorInfoLayout;
	private RelativeLayout summaryLayout;
	private RelativeLayout invoiceImageLayout;
	private RelativeLayout transparentOverlay;
	private RelativeLayout productSearchLayout;
	private OnAddNewProductListener onAddNewProductListener;

	public void setPurchaseOrderReceiveFragmentData(Distributor distributor,
			Boolean isToOrder) {
		this.distributor = distributor;
		this.isToOrder = isToOrder;
	}
	
	/*
	 * this method is called when a product is created with add new button
	 * the distributor is already tagged to the company and brand of the product created
	 * modifying the brand and company hash map accordingly
	 */
	public void setNewProductAdded(InventorySku inventorySku){
		 if(!brandMap.containsKey(inventorySku.getProductSku().getProductBrand().getBrandId())){
			 brandMap.put(inventorySku.getProductSku().getProductBrand().getBrandId(), inventorySku.getProductSku().getProductBrand());
		 }
		 if(!companyMap.containsKey(inventorySku.getProductSku().getProductBrand().getCompany().getCompanyId())){
			 companyMap.put(inventorySku.getProductSku().getProductBrand().getCompany().getCompanyId(), inventorySku.getProductSku().getProductBrand().getCompany());
		 }
		 if (!distributorProductHashMap.containsKey(inventorySku.getProductSku().getProductSkuCode())) {
		     DistributorProductMap distributorProductMap = new DistributorProductMap();
		     distributorProductMap.setDistributor(distributor);
		     distributorProductMap.setDistributorProductSku(inventorySku.getProductSku());
		     distributorProductHashMap.put(inventorySku.getProductSku().getProductSkuCode(), distributorProductMap);
        }
		addNewCreatedProduct(inventorySku);
		addToSelectedItemList();
	}

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		View view = inflater.inflate(R.layout.order_receive_fragment_layout,container, false);

		view.findViewById(R.id.btn_overlay_order_receive).setOnClickListener(onOverLayButtonClicked);
		view.findViewById(R.id.receive_summary_overlay_button).setOnClickListener(onOverLayButtonClicked);
		view.findViewById(R.id.receive_wo_po_btn).setOnClickListener(onClickListener);
		receiveListView = (ListView) view.findViewById(R.id.po_details_list_view);
		horizontalDistributorPOListView = (HorizontalListView) view.findViewById(R.id.order_receive_distributor_PO_horizontal_list);
		horizontalDistributorPOListView.setOnItemClickListener(onPurchaseOrderNoClicked);
		transparentOverlay = (RelativeLayout) view.findViewById(R.id.clickable_overlay);
		transparentOverlay.setOnClickListener(onTransparentOverlayClicked);
		purchaseOrdertext = (TextView) view.findViewById(R.id.pending_purchase_order_textView);
		leftShadowView = (View) view.findViewById(R.id.shadow_left_textView);
		rightShadowView = (View) view.findViewById(R.id.shadow_right_textView);
		keypadLayout = (FrameLayout) view.findViewById(R.id.purchase_order_receive_keypad_layout);

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
		}try {
			onAddNewProductListener = (OnAddNewProductListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ "must implement OnAddNewProductListener");
		}
	}
	
	@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Auto-generated method stub
        this.menu=menu;
        menu.findItem(R.id.search_meuitem).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

	@SuppressLint("NewApi")
	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {

		if (menuItem.getItemId() == R.id.search_meuitem) {
				menuItem.setVisible(false);
				productSearchLayout.setVisibility(View.VISIBLE);
				searchProductEditText.requestFocus();
				((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(searchProductEditText, InputMethodManager.SHOW_FORCED);
				return true;
		}

		onActionbarNavigationListener.onActionbarNavigation("",	menuItem.getItemId());

		return true;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		
		dateFormat = new SimpleDateFormat(SnapInventoryConstants.DATE_FORMAT_DD_MM_YYYY, Locale.US);
		if (getActivity().getActionBar().getCustomView().getId() != R.id.actionbar_receive_id_layout)
			getActivity().getActionBar().setCustomView(R.layout.actionbar_receive_layout);
		
		((TextView) getActivity().getActionBar().getCustomView().findViewById(R.id.distributor_name_textview)).setText(distributor.getAgencyName());
		((TextView) getActivity().getActionBar().getCustomView().findViewById(R.id.distributor_company_name_textview)).setText(distributor.getCompanyNames());
		actionbarBrandFilterLayout = (RelativeLayout) getActivity().getActionBar().getCustomView().findViewById(R.id.receive_brands_layout);
		actionbarBrandFilterLayout.setOnClickListener(onClickListener);
        actionbarCategoryLayout = (RelativeLayout) getActivity().getActionBar().getCustomView().findViewById(R.id.receive_category_layout);
        actionbarCategoryLayout.setOnClickListener(onClickListener);
        actionbarDistributorInfoLayout = (RelativeLayout) getActivity().getActionBar().getCustomView().findViewById(R.id.actionbar_receive_distributor_information_layout);
        actionbarDistributorInfoLayout.setOnClickListener(onClickListener);
        actionbarDistributorInfoLayout.setSelected(true);
		getActivity().findViewById(R.id.add_prod_button).setOnClickListener(onAddProductClickListener);		
		productSearchLayout = (RelativeLayout) getActivity().getActionBar().getCustomView().findViewById(R.id.search_layout);
		searchProductEditText = (SoftInputEditText) getActivity().findViewById(R.id.search_edittext);
		clearSearchButton = (Button) getActivity().getActionBar().getCustomView().findViewById(R.id.clear_search_text_button);
		clearSearchButton.setOnClickListener(onClickListener);
		categoryNameTextView = (TextView) getActivity().getActionBar().getCustomView().findViewById(R.id.receive_category_name_textview);
        subCategoryNameTextView = (TextView) getActivity().getActionBar().getCustomView().findViewById(R.id.receive_sub_category_name_textview);
		searchProductEditText.addTextChangedListener(textWatcherReceiveProductSearch);
		headerNameTextView = (TextView) getView().findViewById(R.id.receive_header_name_text_view);
		headerCountTextView = (TextView) getView().findViewById(R.id.receive_header_count_textview);
        headerValueTextView = (TextView) getView().findViewById(R.id.receive_header_total_value_textview);
        headerNameTextView.setText(allCategories);
        summaryLayout = (RelativeLayout) getView().findViewById(R.id.receive_summary_overlay);
        invoiceImageLayout = (RelativeLayout) getView().findViewById(R.id.receive_summary_invoice_image_layout);
        invoiceImageView = (ImageView) invoiceImageLayout.findViewById(R.id.invoice_image_view);
        summaryDistributorNameTextView = (TextView) summaryLayout.findViewById(R.id.receive_summary_distributor_name_text_view);
        summaryDistributorBrandsTextView = (TextView) summaryLayout.findViewById(R.id.receive_summary_distributor_brands_text_view);
        summaryDistributorNumberTextView = (TextView) summaryLayout.findViewById(R.id.receive_summary_distributor_number_text_view);
        summaryLineItemsTextView = (TextView) summaryLayout.findViewById(R.id.receive_summary_line_item_text_view);
        summaryQuantityTextView = (TextView) summaryLayout.findViewById(R.id.receive_summary_quantity_text_view);
        summaryMRPTextView = (TextView) summaryLayout.findViewById(R.id.receive_summary_mrp_text_view);
        summaryAgencyNameTextView = (TextView) summaryLayout.findViewById(R.id.receive_summary_agency_name_text_view);
        summaryPaymentAmountTextView = (TextView) summaryLayout.findViewById(R.id.receive_summary_payment_amount_text_view);
        summaryPurchaseValueTextView = (TextView) summaryLayout.findViewById(R.id.receive_summary_purchase_value_text_view);
        summaryTaxTextView = (TextView) summaryLayout.findViewById(R.id.receive_summary_tax_text_view);
        summaryDiscountTextView = (TextView) summaryLayout.findViewById(R.id.receive_summary_discount_text_view);
        summaryNetPayableTextView = (TextView) summaryLayout.findViewById(R.id.receive_summary_net_payable_text_view);
        invoiceNumberEditText = (SoftInputEditText) summaryLayout.findViewById(R.id.receive_summary_invoice_number_edit_text);
        invoiceImageViewThumbnail = (ImageView) summaryLayout.findViewById(R.id.receive_summary_invoice_image_view);
        invoiceImageViewThumbnail.setOnClickListener(onClickListener);
        summaryCancelBtn = (Button) summaryLayout.findViewById(R.id.receive_summary_cancel_button);
        summaryCancelBtn.setOnClickListener(onClickListener);
        summaryConfirmBtn = (Button) summaryLayout.findViewById(R.id.receive_summary_confirm_button);
        summaryConfirmBtn.setOnClickListener(onClickListener);
        deleteInvoiceImageBtn = (Button) invoiceImageLayout.findViewById(R.id.invoice_image_delete_button);
		deleteInvoiceImageBtn.setOnClickListener(onClickListener);
		recaptureInvoiceImageBtn = (Button) invoiceImageLayout.findViewById(R.id.invoice_image_recapture_button);
		recaptureInvoiceImageBtn.setOnClickListener(onClickListener);
		invoiceImageDoneBtn = (Button) invoiceImageLayout.findViewById(R.id.invoice_image_done_button);
		invoiceImageDoneBtn.setOnClickListener(onClickListener);
		if(null == productBeanMap){
			productBeanMap = new HashMap<String, ProductBean>();
		}
		if(null == brandMap){
    		brandMap = new HashMap<Integer, Brand>();
    	}
		if(null == companyMap){
    		companyMap = new HashMap<Integer, Company>();
    	}
		if (null == productList) {
            productList = new ArrayList<ProductBean>();
        }
		if (null == distributorProductHashMap) {
            distributorProductHashMap = new HashMap<String, DistributorProductMap>();
        }
		/*
		 * if any product category is selected
		 * then data is set according to it
		 * or else data where no category is selected is displayed
		 */
		if(null == productCategory){        	
        	categoryNameTextView.setText(allCategories);
        	subCategoryNameTextView.setText(allCategories);
        	headerNameTextView.setText(allCategories);
            headerValueTextView.setText(SnapToolkitTextFormatter.formatPriceText(headerTotalValue, getActivity()));
                if(null != productList && productList.size()>0){
                    headerCountTextView.setText(String.valueOf(productList.size()));
                }             
        }else{
        	categoryNameTextView.setText(productCategory.getParenCategory().getCategoryName());
        	subCategoryNameTextView.setText(productCategory.getCategoryName());
        	headerNameTextView.setText(productCategory.getCategoryName());
            headerValueTextView.setText(SnapToolkitTextFormatter.formatPriceText(headerValueByCategory, getActivity()));
            if(null != filteredListByCategory && filteredListByCategory.size()>0){
                headerCountTextView.setText(String.valueOf(filteredListByCategory.size()));
            }
        }

		if (null != distributorPOAdapter) {
			horizontalDistributorPOListView.setAdapter(distributorPOAdapter);
		}else{
			getDistributorPOTask  = new GetDistributorPOTask(getActivity(),	InventoryReceiveFragment.this, GET_DISTRIBUTOR_PO_TASK);
			getDistributorPOTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, distributor.getDistributorId());
		}

		if (receiveListAdapter != null) {
			receiveListView.setAdapter(receiveListAdapter);
		}else{
			getBrandsByDistributorTask = new GetBrandsByDistributorTask(getActivity(), InventoryReceiveFragment.this, GET_DISTRIBUTORS_BRAND_TASKCODE);
			getBrandsByDistributorTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, distributor.getDistributorId());
			
			getCompaniesTaggedToDistributorTask = new GetCompaniesTaggedToDistributorTask(getActivity(), InventoryReceiveFragment.this, GET_DISTRIBUTOR_COMPANIES_TASKCODE);
	        getCompaniesTaggedToDistributorTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, distributor.getDistributorId());
			
			getProductsToReceiveTask = new GetProductsToReceiveTask(getActivity(), GET_ALL_PRODUCTS_TASK_CODE, InventoryReceiveFragment.this);
			getProductsToReceiveTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, distributor.getDistributorId());
			
			getDistributorProductMapByDistributorTask = new GetDistributorProductMapByDistributorTask(getActivity(), GET_DISTRIBUTOR_PRODUCT_MAP_TASKCODE, InventoryReceiveFragment.this);
			getDistributorProductMapByDistributorTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, distributor.getDistributorId());
		}		
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		searchProductEditText.setText("");
		headerTotalValue = 0;
		invoiceNumber = "";
		invoiceNumberEditText.setText("");
		
		clearAllListandMap();

		if (null != order) {
			order = null;
		}
		if(null != distributor){
			distributor = null;
		}
		if(null != productCategory){
            productCategory = null;
        }
		if(null != filterFragment){
			filterFragment = null;
		}
		if( null != receiveListAdapter ){
			receiveListAdapter = null;
		}
		if (null != distributorPOAdapter){
			distributorPOAdapter = null;
		}
        if (null != invoiceImageBitmap) {
            invoiceImageBitmap = null;
        }		
		if( null != getDistributorPOTask && !getDistributorPOTask.isCancelled() ){
			getDistributorPOTask.cancel(true);
		}
		if( null != filterDistributorProductsByBrandsTask && !filterDistributorProductsByBrandsTask.isCancelled() ){
			filterDistributorProductsByBrandsTask.cancel(true);
		}
		if( null != updatePurchaseOrderReceiveTask && !updatePurchaseOrderReceiveTask.isCancelled() ){
			updatePurchaseOrderReceiveTask.cancel(true);
		}		
		if(null != receiveWithoutPOTask && !receiveWithoutPOTask.isCancelled()){
			receiveWithoutPOTask.cancel(true);
		}		
		if(null != addProductToExistingPO && !addProductToExistingPO.isCancelled()){
			addProductToExistingPO.cancel(true);
		}
		if(null != filterProductsByCategoryTask && !filterProductsByCategoryTask.isCancelled()){
			filterProductsByCategoryTask.cancel(true);
		}
		if(null != getProductsToReceiveTask && !getProductsToReceiveTask.isCancelled()){
			getProductsToReceiveTask.cancel(true);
		}
		if(null != getPurchaseOrderListTask && !getPurchaseOrderListTask.isCancelled()){
			getPurchaseOrderListTask.cancel(true);
		}
		if(null != getGlobalProductsTask && !getGlobalProductsTask.isCancelled()){
			getGlobalProductsTask.cancel(true);
		}
		if(null != getCompaniesTaggedToDistributorTask && !getCompaniesTaggedToDistributorTask.isCancelled()){
        	getCompaniesTaggedToDistributorTask.cancel(true);
        }
		if(null != getBrandsByDistributorTask && !getBrandsByDistributorTask.isCancelled()){
			getBrandsByDistributorTask.cancel(true);
		}
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		searchProductEditText.removeTextChangedListener(textWatcherReceiveProductSearch);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		if (null != searchProductEditText)
			searchProductEditText.addTextChangedListener(textWatcherReceiveProductSearch);
	}

	@SuppressWarnings("unchecked")
	@SuppressLint("NewApi")
	@Override
	public void onTaskSuccess(Object responseList, int taskCode) {
		// TODO Auto-generated method stub
		if(getActivity() == null)
			return;
		if (GET_DISTRIBUTOR_PO_TASK == taskCode) {
		    horizontalDistributorPOListView.setVisibility(View.VISIBLE);
            purchaseOrdertext.setVisibility(View.VISIBLE); 
            leftShadowView.setVisibility(View.VISIBLE);
            rightShadowView.setVisibility(View.VISIBLE);
			List<Order> orderList = (ArrayList<Order>) responseList;
			if (null == distributorPOAdapter) {
				distributorPOAdapter = new OrderReceiveOrderNumberAdapter(getActivity(), orderList);
				horizontalDistributorPOListView.setAdapter(distributorPOAdapter);
			} else {
				distributorPOAdapter.clear();
				distributorPOAdapter.addAll(orderList);
				distributorPOAdapter.notifyDataSetChanged();
			}

		}else if(GET_PURCHASE_ORDER_SUB_LIST_TASK == taskCode){
			
		    //this is called when any purchase order number is clicked
		    //populating product list and setting it to adapter
		    if (productList.size()>0) {
                productList.clear();
            }
			productList = (List<ProductBean>) responseList;
            if(null == receiveListAdapter){
                receiveListAdapter = new ReceiveListAdapter(getActivity(), new ArrayList<ProductBean>(), InventoryReceiveFragment.this);
                receiveListAdapter.addAll(productList);
                receiveListView.setAdapter(receiveListAdapter);
            }else{
                receiveListAdapter.clear();
                receiveListAdapter.addAll(productList);
                receiveListAdapter.notifyDataSetChanged();
            }
			headerTotalValue = 0;
			headerCountTextView.setText(String.valueOf(productList.size()));
			
			//populating the product list map
			if (null != productBeanMap && productBeanMap.size()>0) {
                productBeanMap.clear();
            }
			for (ProductBean bean : productList) {
				productBeanMap.put(bean.getProductSkuID(), bean);
				if(bean.getProductQty()>0){
					headerTotalValue += bean.getProductQty() * bean.getProductPrice();
				}
			}
			headerValueTextView.setText(SnapToolkitTextFormatter.formatPriceText(headerTotalValue, getActivity()));
			
			//clearing any previously selected products to be received
			if(null != selectedItemList && selectedItemList.size()>0){
				selectedItemList.clear();
			}
		}else if (UPDATE_PURCHASE_ORDER_TASK == taskCode) {			
			
			CustomToast.showCustomToast(getActivity(), getString(R.string.purchase_order_success), Toast.LENGTH_SHORT, CustomToast.SUCCESS);	
	        invoiceNumber = "";
	        invoiceNumberEditText.setText("");				
			onActionbarNavigationListener.onActionbarNavigation(SnapInventoryConstants.CONFIRM_BUTTON_CLICKED, android.R.id.home);
			onActionbarNavigationListener.onActionbarNavigation(SnapInventoryConstants.CONFIRM_BUTTON_CLICKED, android.R.id.home);

		}else if(RECEIVE_WITHOUT_PO_TASKCODE == taskCode){
			
			CustomToast.showCustomToast(getActivity(), getString(R.string.receive_order_success), Toast.LENGTH_SHORT, CustomToast.SUCCESS);	
	        invoiceNumber = "";
	        invoiceNumberEditText.setText("");		
			onActionbarNavigationListener.onActionbarNavigation(SnapInventoryConstants.CONFIRM_BUTTON_CLICKED, android.R.id.home);
			onActionbarNavigationListener.onActionbarNavigation(SnapInventoryConstants.CONFIRM_BUTTON_CLICKED, android.R.id.home);
			
		}else if (FILTER_PRODUCTS_BY_BRANDS_TASKCODE == taskCode) {
            
			float headerValue = 0;
			List<ProductBean> filteredList = (List<ProductBean>) responseList;
            receiveListAdapter.clear();
            receiveListAdapter.addAll(filteredList);
            receiveListAdapter.notifyDataSetChanged();
			for (ProductBean bean : filteredList) {
				if(bean.getProductQty()>0){
					headerValue += bean.getProductQty() * bean.getProductPrice();
				}
			}
			headerValueTextView.setText(SnapToolkitTextFormatter.formatPriceText(headerValue, getActivity()));
			headerCountTextView.setText(String.valueOf(filteredList.size()));

        }else if(FILTER_PRODUCTS_BY_CATEGORY_TASKCODE == taskCode){
        	
            //this is called when the products are filtered according to 
            //to a category selected, this list is irrespective
        	headerValueByCategory = 0;
			filteredListByCategory = (List<ProductBean>) responseList;
            receiveListAdapter.clear();
            receiveListAdapter.addAll(filteredListByCategory);
            receiveListAdapter.notifyDataSetChanged();
			for (ProductBean bean : filteredListByCategory) {
				if(bean.getProductQty()>0){
					headerValueByCategory += bean.getProductQty() * bean.getProductPrice();
				}
			}
			headerValueTextView.setText(SnapToolkitTextFormatter.formatPriceText(headerValueByCategory, getActivity()));
			headerCountTextView.setText(String.valueOf(filteredListByCategory.size()));
        	
        }else if(GET_ALL_PRODUCTS_TASK_CODE == taskCode){
        	
            //this is called when the show all button is tapped
            //product list is being populated along with the
            //hash map associated with it
            if (productList.size()>0) {
                productList.clear();
            }
        	productList = (List<ProductBean>) responseList;
            if(null == receiveListAdapter){
                receiveListAdapter = new ReceiveListAdapter(getActivity(), new ArrayList<ProductBean>(), InventoryReceiveFragment.this);
                receiveListAdapter.addAll(productList);
                receiveListView.setAdapter(receiveListAdapter);
            }else{
                receiveListAdapter.clear();
                receiveListAdapter.addAll(productList);
                receiveListAdapter.notifyDataSetChanged();
            }
        	headerTotalValue = 0;
        	headerCountTextView.setText(String.valueOf(productList.size()));
        	
        	//clearing the old values and populating the hash map with new values
        	if (null != productBeanMap && productBeanMap.size()>0) {
                productBeanMap.clear();
            }
        	for (ProductBean bean : productList) {
				productBeanMap.put(bean.getProductSkuID(), bean);
				if(bean.getProductQty()>0){
					headerTotalValue += bean.getProductQty() * bean.getProductPrice();
				}
			}
        	headerValueTextView.setText(SnapToolkitTextFormatter.formatPriceText(headerTotalValue, getActivity()));
        	
        }else if(SEARCH_PRODUCT_TASK == taskCode){
        	
            //this is called when a search is made for any product
            hideActionbarFilterLayouts();
        	float headerValue = 0;
			List<ProductBean> filteredList = (List<ProductBean>) responseList;
            if(null == receiveListAdapter){
                receiveListAdapter = new ReceiveListAdapter(getActivity(), filteredList , InventoryReceiveFragment.this);
                receiveListView.setAdapter(receiveListAdapter);
            }else{
                receiveListAdapter.clear();
                receiveListAdapter.addAll(filteredList);
                receiveListAdapter.notifyDataSetChanged();
            }
			for (ProductBean bean : filteredList) {
				if(bean.getProductQty()>0){
					headerValue += bean.getProductQty() * bean.getProductPrice();
				}
			}
			headerValueTextView.setText(SnapToolkitTextFormatter.formatPriceText(headerValue, getActivity()));
			headerCountTextView.setText(String.valueOf(filteredList.size()));
        	
        }else if(ADD_PRODUCT_TO_EXISTING_PO_TASKCODE == taskCode){
        	
            //this is called when any product is added to
            //the currently selected purchase order
        	productBean.setSelected(true);
        	productList.add(0, productBean);
        	receiveListAdapter.clear();
        	receiveListAdapter.addAll(productList);
        	receiveListAdapter.notifyDataSetChanged();
        	
        }else if(GET_DISTRIBUTORS_BRAND_TASKCODE == taskCode){
        	
            //populating the brand map tagged to the selected distributor
        	brandMap = (HashMap<Integer, Brand>) responseList;
        	
        }else if(GET_DISTRIBUTOR_COMPANIES_TASKCODE == taskCode){
        	
            //populating the company map tagged to the selected distributor
        	companyMap = (HashMap<Integer, Company>) responseList;
        	
        } else if (GET_DISTRIBUTOR_PRODUCT_MAP_TASKCODE == taskCode) {
            //populating distributorProductHashMap
            distributorProductHashMap = (HashMap<String, DistributorProductMap>) responseList;
        }
	}

	@Override
	public void onTaskError(String errorMessage, int taskCode) {
		// TODO Auto-generated method stub
	    if(getActivity()==null)
	        return;
	    
		if (FILTER_PRODUCTS_BY_BRANDS_TASKCODE == taskCode) {
        	
    		if(null != receiveListAdapter){
    			receiveListAdapter.clear();
        		receiveListAdapter.notifyDataSetChanged();
    		}
    	
		}else if(FILTER_PRODUCTS_BY_CATEGORY_TASKCODE == taskCode){
    	
			if(null != receiveListAdapter){
    			receiveListAdapter.clear();
        		receiveListAdapter.notifyDataSetChanged();
    		}
			if(null != filteredListByCategory && filteredListByCategory.size()>0){
        		filteredListByCategory.clear();
        	}
    	
        } else if (SEARCH_PRODUCT_TASK == taskCode) {
			
		    hideActionbarFilterLayouts();
			if(null != receiveListAdapter){
				receiveListAdapter.clear();
				receiveListAdapter.notifyDataSetChanged();
			}
			
		} else if (GET_ALL_PRODUCTS_TASK_CODE == taskCode || GET_PURCHASE_ORDER_SUB_LIST_TASK == taskCode) {
            if (null != productList && productList.size()>0) {
                productList.clear();
            }
            if(null != receiveListAdapter){
                receiveListAdapter.clear();
                receiveListAdapter.notifyDataSetChanged();
            }
        } else if (GET_DISTRIBUTOR_PRODUCT_MAP_TASKCODE == taskCode) {
            if (null != distributorProductHashMap) {
                distributorProductHashMap.clear();
            }
        }
		headerCountTextView.setText("0");
		headerValueTextView.setText(SnapToolkitTextFormatter.formatPriceText(0, getActivity()));
		if(null != getActivity()){
			CustomToast.showCustomToast(getActivity(), errorMessage, Toast.LENGTH_SHORT, CustomToast.ERROR);
		}		
	}
	
	OnClickListener onAddProductClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			onAddNewProductListener.showAddProductLayout("",distributor);
		}
	};

	View.OnClickListener onClickListener = new View.OnClickListener() {

		@SuppressLint("NewApi")
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			if(R.id.receive_wo_po_btn == v.getId()){
			    
			    if (null != selectedItemList){
		            selectedItemList.clear();
		        }
				if (null != order) {
					order.setSelected(false);					   
					order = null; 
				}
				if(null != distributorPOAdapter){
					distributorPOAdapter.notifyDataSetChanged();
				}
				if(null != searchProductEditText){
					searchProductEditText.setText("");
				}
				headerNameTextView.setText(allCategories);
				categoryNameTextView.setText(allCategories);
				subCategoryNameTextView.setText(allCategories);
				getProductsToReceiveTask = new GetProductsToReceiveTask(getActivity(), GET_ALL_PRODUCTS_TASK_CODE, InventoryReceiveFragment.this);
				getProductsToReceiveTask.execute(distributor.getDistributorId());
				actionbarBrandFilterLayout.setVisibility(View.GONE);
					
			}else if(R.id.clear_search_text_button == v.getId()){
				
				if(searchProductEditText.getText().toString().trim().length()>0){
				    headerNameTextView.setText(allCategories);
	                searchProductEditText.setText("");
	                if(isKeypadVisible()){
	                    popKeypadFragment();
	                }
	                setListDataAfterClearSearch();
				}
				
			}else if(R.id.receive_brands_layout == v.getId()){
				
			    /*
			     * removing the filter fragment when multiple brands is clicked
			     * by popBackStack and displaying data if any brand(s) is selected
			     * or not
			     */
				if(null != filterFragment && filterFragment.isAdded() && filterFragment.getProductFilterType().ordinal() == ProductFilterType.BRAND_FILTER.ordinal()){
            		getFragmentManager().popBackStack();
            		actionbarBrandFilterLayout.setSelected(false);
            		menu.findItem(R.id.search_meuitem).setVisible(false);
            		productSearchLayout.setVisibility(View.GONE);
            		if(null != brandMap && brandMap.size()>0){
            			filterBrandList = filterFragment.getSelectedBrand();
            		}        
            		/*
            		 * filtering the product list according to selected brand(s)
            		 */
            		if(null != filterBrandList && filterBrandList.size()>0){
            			headerNameTextView.setText("Brand Filter");
                			if(null != productList && productList.size()>0){
                        		filterDistributorProductsByBrandsTask = new FilterProductsByBrandsTask(InventoryReceiveFragment.this, FILTER_PRODUCTS_BY_BRANDS_TASKCODE, filterBrandList, productList);
                        		filterDistributorProductsByBrandsTask.execute();            			
                    		}
            		}else{  
            		    setListDataAfterClearSearch();         			
            		}
            	}else{
            	    /*
            	     * adding filter fragment and setting it to display filter type as brand filter
            	     */
            		if(isKeypadVisible()){
            			popKeypadFragment();
            		}
            		FragmentTransaction ft = getFragmentManager().beginTransaction();
                	if(null == filterFragment){
                		filterFragment = new FilterFragment();
                        filterFragment.setShowAllCategories(true);
                		ft.add(R.id.second_content_framelayout, filterFragment, filterFragmentString);
                		ft.addToBackStack(filterFragmentString);
                	}else{
                		ft.replace(R.id.second_content_framelayout, filterFragment, filterFragmentString);
                	}
                    filterFragment.setDistributor(distributor);
                	filterFragment.setProductFilterType(ProductFilterType.BRAND_FILTER);
                	filterFragment.setOnFilterSelectedListener(InventoryReceiveFragment.this);
                	if(!filterFragment.isAdded()){
                		ft.addToBackStack(filterFragmentString);
                	}
                	ft.commit();
                	actionbarBrandFilterLayout.setSelected(true);
                	actionbarCategoryLayout.setSelected(false);
                	actionbarDistributorInfoLayout.setSelected(false);
                	menu.findItem(R.id.search_meuitem).setVisible(false);
                	productSearchLayout.setVisibility(View.VISIBLE);
                	searchProductEditText.setText("");
            	}
				
			}else if(R.id.receive_summary_cancel_button == v.getId()){
				
				summaryLayout.setVisibility(View.GONE);
				invoiceNumberEditText.setText("");
				invoiceImageBitmap = null;
				
            } else if (R.id.receive_summary_confirm_button == v.getId()) {
			
                /*
                 * this is called when the confirm button is clicked of the summary layout
                 * depending on the value of order if it is null or not
                 * update the purchase order or receive without PO tasks are called
                 */
				invoiceNumber = invoiceNumberEditText.getText().toString().trim();
				if (invoiceNumber.length() > 0){
					if(null != order){
						updatePurchaseOrderReceiveTask = new UpdatePurchaseOrderReceiveTask(getActivity(),
								UPDATE_PURCHASE_ORDER_TASK,
								InventoryReceiveFragment.this,
								order.getOrderNumber(), selectedItemList,
								order.getOrderTotalDiscount(), summaryNetPayable, summaryDiscountValue, order.getPaymentToMake(), 
								invoiceNumber, invoiceImageBitmap);
						updatePurchaseOrderReceiveTask.execute();
	                } else {
	                    //user has to select at least one product to receive without PO task
						if(null != selectedItemList && selectedItemList.size() > 0){
							receiveWithoutPOTask = new ReceiveWithoutPOTask(getActivity(), RECEIVE_WITHOUT_PO_TASKCODE, InventoryReceiveFragment.this, 
									selectedItemList,dateFormat.format(Calendar.getInstance().getTime()) , summaryDiscountValue, totalOrderAmount, 
									distributor, summaryNetPayable, invoiceNumber, invoiceImageBitmap, brandMap, companyMap, distributorProductHashMap);
							receiveWithoutPOTask.execute();
						}
					}
				} else {
				    CustomToast.showCustomToast(getActivity(), getString(R.string.alert_invoice_no), Toast.LENGTH_SHORT, CustomToast.INFORMATION);
				
				}
            } else if (R.id.receive_summary_invoice_image_view == v.getId()) {
				
                /*
                 * when invoice image is clicked
                 * if there is already image then it will be displayed
                 * else action image capture is executed
                 */
                if (null != invoiceImageBitmap) {
                    invoiceImageLayout.setVisibility(View.VISIBLE);
                    invoiceImageView.setImageBitmap(invoiceImageBitmap);
                } else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, CAPTURE_IMAGE_TASK_CODE);
                }
				
            } else if (R.id.invoice_image_delete_button == v.getId()) {
				
				SnapCommonUtils.showDeleteAlert(getActivity(), "Invoice Image", "Delete Invoice Image?", invoiceImagePositiveClickListener, invoiceImageNegativeClickListener, true);
				
            } else if (R.id.invoice_image_recapture_button == v.getId()) {
				
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent, CAPTURE_IMAGE_TASK_CODE);
				
            } else if (R.id.invoice_image_done_button == v.getId()) {
				
                if (null != invoiceImageLayout) {
                    invoiceImageLayout.setVisibility(View.GONE);
                }
				
            } else if (R.id.receive_category_layout == v.getId()) {
            	
                /*
                 * called when category filtered is clicked
                 * if filtered fragment is added then it's popped
                 * or else it is added to backStack and displayed
                 */
            	if(null != filterFragment && filterFragment.isAdded() && filterFragment.getProductFilterType().ordinal() == ProductFilterType.CATEGORY_FILTER.ordinal()){
            		getFragmentManager().popBackStack();
            		actionbarCategoryLayout.setSelected(false);
            		menu.findItem(R.id.search_meuitem).setVisible(false);
            		productSearchLayout.setVisibility(View.GONE);
                } else {
            		if(isKeypadVisible()){
            			popKeypadFragment();
            		}
            		FragmentTransaction ft = getFragmentManager().beginTransaction();
                    if (null == filterFragment) {
                		filterFragment = new FilterFragment();
                        filterFragment.setShowAllCategories(true);
                		ft.add(R.id.second_content_framelayout, filterFragment, filterFragmentString);
                		ft.addToBackStack(filterFragmentString);
                    } else {
                		ft.replace(R.id.second_content_framelayout, filterFragment, "filter_fragment");
                	}
                	filterFragment.setProductFilterType(ProductFilterType.CATEGORY_FILTER);
                	filterFragment.setOnFilterSelectedListener(InventoryReceiveFragment.this);
                    if (!filterFragment.isAdded()) {
                		ft.addToBackStack(filterFragmentString);
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
            } else if (R.id.actionbar_receive_distributor_information_layout == v.getId()) {
            	
                if (isFilterFragmentVisible()) {
                    hideFilterFragment();
                }
            	showActionbarFilterLayouts();
            	actionbarDistributorInfoLayout.setSelected(true);
            	menu.findItem(R.id.search_meuitem).setVisible(false);
            	productSearchLayout.setVisibility(View.VISIBLE);
            	searchProductEditText.setText("");
            	headerNameTextView.setText(allCategories);
                categoryNameTextView.setText(allCategories);
                subCategoryNameTextView.setText(allCategories);
                if (null != filterFragment) {
                    filterFragment.resetFilters();
                }
            	if (null != receiveListAdapter) {
                    receiveListAdapter.clear();
                        headerValueTextView.setText(SnapToolkitTextFormatter.formatPriceText(headerTotalValue, getActivity()));
                        if (null != productList && productList.size()>0) {
                            headerCountTextView.setText(String.valueOf(productList.size()));
                            receiveListAdapter.addAll(productList);
                        }                    
                    receiveListAdapter.notifyDataSetChanged();
                }
            }
		}
	};

	View.OnClickListener onTransparentOverlayClicked = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (isKeypadVisible()) {
				popKeypadFragment();
			}
		}
	};

	AdapterView.OnItemClickListener onPurchaseOrderNoClicked = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view,
				final int pos, long id) {
			// TODO Auto-generated method stub

			if (order != distributorPOAdapter.getItem(pos)){
				if (null != order){
					order.setSelected(false);
				}
			}
			order = distributorPOAdapter.getItem(pos);
			order.setSelected(true);
			distributorPOAdapter.notifyDataSetChanged();
			if (null != selectedItemList){
	            selectedItemList.clear();
	        }
			if(null != searchProductEditText){
				searchProductEditText.setText("");
			}			
			headerNameTextView.setText(allCategories);
			categoryNameTextView.setText(allCategories);
			subCategoryNameTextView.setText(allCategories);
			getPurchaseOrderListTask = new GetPurchaseOrderListTask(getActivity(), InventoryReceiveFragment.this, GET_PURCHASE_ORDER_SUB_LIST_TASK);
			getPurchaseOrderListTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, order.getOrderNumber());
		}
	};

	TextWatcher textWatcherReceiveProductSearch = new TextWatcher() {

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
				headerNameTextView.setText(keyword);
				if(isKeypadVisible()){
					popKeypadFragment();
				}
	                getGlobalProductsTask = new GetGlobalProductsTask(getActivity(), SEARCH_PRODUCT_TASK, InventoryReceiveFragment.this, productBeanMap);
	                getGlobalProductsTask.execute(keyword);
			} else {
				headerNameTextView.setText(allCategories);
				if(isKeypadVisible()){
					popKeypadFragment();
				}
				setListDataAfterClearSearch();
			}
		}
	};
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub

		try {
			super.onActivityResult(requestCode, resultCode, data);

			if (requestCode == CAPTURE_IMAGE_TASK_CODE) {
				if (resultCode == Activity.RESULT_OK) {
					invoiceImageBitmap = (Bitmap) data.getExtras().get("data");
					invoiceImageViewThumbnail.setImageBitmap(invoiceImageBitmap);
					if(null != invoiceImageLayout){
						invoiceImageLayout.setVisibility(View.GONE);
					}
				}
			} else {
				CustomToast.showCustomToast(getActivity(), getString(R.string.msg_capture_image), Toast.LENGTH_SHORT, CustomToast.ERROR);
			}
		} catch (NullPointerException e) {
		    CustomToast.showCustomToast(getActivity(), getString(R.string.msg_image_processed), Toast.LENGTH_SHORT, CustomToast.ERROR);
		}
	}

	@Override
	public void onOrderReceivedEditQty(int position) {
		// TODO Auto-generated method stub

		productBean = receiveListAdapter.getItem(position);
		addKeyPadFragment();
		keypadFragment.setContext(KEYPAD_FRAGMENT_RECEIVE_QTY_TASK_CODE);
		keypadFragment.setValue(String.valueOf(productBean.getProductPendingOrder()));
		RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		rlParams.setMargins(550, 170, 0, 0);
		keypadLayout.setLayoutParams(rlParams);

	}

	@Override
	public void onProductPurchasePriceClicked(int position) {
		// TODO Auto-generated method stub

		productBean = receiveListAdapter.getItem(position);
		addKeyPadFragment();
		keypadFragment.setContext(KEYPAD_FRAGMENT_PRODUCT_PRICE_TASK_CODE);
		keypadFragment.setValue(String.valueOf(productBean.getProductPurchasePrice()));
		RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		rlParams.setMargins(450, 170, 0, 0);
		keypadLayout.setLayoutParams(rlParams);
	}

	@Override
	public void onProductDiscountClicked(int position) {

		productBean = receiveListAdapter.getItem(position);
		addKeyPadFragment();
		keypadFragment.setContext(KEYPAD_FRAGMENT_PRODUCT_DISCOUNT_TASK_CODE);
		keypadFragment.setValue(String.valueOf(productBean.getProductDiscount()));
		RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		rlParams.setMargins(240, 170, 0, 0);
		keypadLayout.setLayoutParams(rlParams);
	}

	@Override
	public void onProductBilledQtyClicked(int position) {
		
		productBean = receiveListAdapter.getItem(position);
		addKeyPadFragment();
		keypadFragment.setContext(KEYPAD_FRAGMENT_BILLED_QTY_TASK_CODE);
		keypadFragment.setValue(String.valueOf(productBean.getProductBilledQty()));
		RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		rlParams.setMargins(720, 170, 0, 0);
		keypadLayout.setLayoutParams(rlParams);
	}

	@Override
	public void onTotalClicked(int position) {

		productBean = receiveListAdapter.getItem(position);
		addKeyPadFragment();
		keypadFragment.setContext(KEYPAD_FRAGMENT_TOTAL_TASK_CODE);
		keypadFragment.setValue(String.valueOf(productBean.getProductTotalAmount()));
		RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		rlParams.setMargins(270, 170, 0, 0);
		keypadLayout.setLayoutParams(rlParams);
	}

	@Override
	public void onMRPClicked(int position) {

		productBean = receiveListAdapter.getItem(position);
		addKeyPadFragment();
		keypadFragment.setContext(KEYPAD_MRP_TASK_CODE);
		keypadFragment.setValue(String.valueOf(productBean.getProductPrice()));
		RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		rlParams.setMargins(80, 170, 0, 0);
		keypadLayout.setLayoutParams(rlParams);

	}
	
    @Override
    public void onOrderReceiveCheckBoxChecked(int position) {
        // TODO Auto-generated method stub

        productBean = receiveListAdapter.getItem(position);
        if (!productBean.isSelected()) {
            selectProduct();
        } else {
            removeFromSelectedList();
        }
        reCalculateProductTotal();
    }	

	@Override
	public void onProductNameClicked(int position) {

		productBean = receiveListAdapter.getItem(position);
		if (productBean.isSelected()) {
			removeFromSelectedList();
		} else {
			selectProduct();
		}
		reCalculateProductTotal();
	}

	@Override
	public void onProductImageClicked(int position) {
		
		productBean = receiveListAdapter.getItem(position);
		if (productBean.isSelected()) {
			removeFromSelectedList();
		} else {
			selectProduct();
		}
		reCalculateProductTotal();
	}
	
	/*
	 * this is called when a product is scanned
	 */
	public void receiveScannedProduct(InventorySku inventorySku) {
		Log.d("PurchaseOrderReceiveFragment","inside receiveScannedProduct product sku code " + inventorySku.getProductSku().getProductSkuName());
		
		if(null != order){
		    /*
	         * the scanned product's sku is equated against all the products in the 
	         * productList and if it's a match and the product is not selected 
	         * then the product is added to the selected list
	         * if it is selected then the product is moved to the top of the 
	         * product list
	         */
			boolean isProductFound = false;
            if (null != productList && productList.size() > 0) {
                loop: for (int i = 0; i < productList.size(); i++) {
					if(productList.get(i).getProductSkuID().equalsIgnoreCase(inventorySku.getProductSku().getProductSkuCode())){
						productBean = productList.get(i);
						isProductFound = true;
                        if (!productBean.isSelected()) {
                            addToSelectedItemList();
                        }
                        if (i > 0) {
                            productList.remove(i);
                            productList.add(0, productBean);
                            receiveListView.smoothScrollToPosition(0);
                        }
                        receiveListAdapter.clear();
                        receiveListAdapter.addAll(productList);
                        receiveListAdapter.notifyDataSetChanged();
		    			break loop;
					}
				}
	    	}
			/* 
             * if the product is not found in the product list then 
             * it is created and added to the top of the list
			 */
            if (!isProductFound) {
                addNewCreatedProduct(inventorySku);
                selectProduct();
            }
        } else {
		    /*
		     * this is executed when no purchase order is selected
		     * the scanned product's sku is equated against all the 
		     * products in the product list 
		     * if the product is found in the list and if it is
		     * not selected then it is added to the selected list
		     * and the qty is increased by 1
		     * if the product found is selected then it is moved to the 
		     * top of the list and it's qty is increased by 1
		     */
            if (null != productList && productList.size() > 0) {
				boolean isProductFound = false;
                loop: for (int i = 0; i < productList.size(); i++) {
					if(productList.get(i).getProductSkuID().equalsIgnoreCase(inventorySku.getProductSku().getProductSkuCode())){
						productBean = productList.get(i);
						isProductFound = true;
                        if (!productBean.isSelected()) {
                            addToSelectedItemList();
                        }
                        if(i>0){
                            productList.remove(i);
                            productList.add(0, productBean);
                            receiveListView.smoothScrollToPosition(0);
                        }
                        incQtyByOne();
                        receiveListAdapter.clear();
                        receiveListAdapter.addAll(productList);
                        receiveListAdapter.notifyDataSetChanged();
                        CustomToast.showCustomToast(getActivity(), getString(R.string.increase_bill_qty), Toast.LENGTH_SHORT, CustomToast.INFORMATION);
						reCalculateProductTotal();
						break loop;
					}
				}
				/* 
	             * if the product is not found in the product list then 
	             * it is created and added to the top of the list
	             */
				if(!isProductFound){
					addNewCreatedProduct(inventorySku);
					selectProduct();
				}
			}else{
			    /*
			     * when the product list is either null or is empty
			     * then the product is created and added to the selected list
			     */
				addNewCreatedProduct(inventorySku);
				selectProduct();
			}
		}
	}

	@Override
	public void onNumKeyPadEnter(String value, int context) {
		// TODO Auto-generated method stub
		int inputValue=(int) Double.parseDouble(value);
		if (KEYPAD_FRAGMENT_RECEIVE_QTY_TASK_CODE == context) {
			
			if (inputValue == 0) {
				CustomToast.showCustomToast(getActivity(), getString(R.string.receive_bill_qty), Toast.LENGTH_SHORT, CustomToast.INFORMATION);
			} else {
				productBean.setProductReceivedQty(inputValue);
				productBean.setProductToOrder(inputValue);
				productBean.setProductPendingOrder(inputValue);
				if (!productBean.isSelected()) {
					addToSelectedItemList();
				}
				if(null == order){
					if(productBean.getProductBilledQty() == 0){
						productBean.setProductBilledQty(inputValue);
					}					
				}				
				receiveListAdapter.notifyDataSetChanged();
			}

		} else if (KEYPAD_FRAGMENT_PRODUCT_PRICE_TASK_CODE == context) {

			if (!productBean.isSelected()) {
				selectProduct();
			}
			productBean.setProductPurchasePrice(Float.parseFloat(value));
			reCalculateProductTotal();

		} else if (KEYPAD_FRAGMENT_PRODUCT_DISCOUNT_TASK_CODE == context) {

			productBean.setProductDiscount(Float.parseFloat(value));
			reCalculateProductTotal();

		} else if (KEYPAD_FRAGMENT_BILLED_QTY_TASK_CODE == context) {

			productBean.setProductBilledQty(inputValue);
			if(null == order){
				if(productBean.getProductReceivedQty() == 0){
					productBean.setProductReceivedQty(inputValue);
					productBean.setProductToOrder(inputValue);
					productBean.setProductPendingOrder(inputValue);
				}
			}
			if (!productBean.isSelected()) {
				addToSelectedItemList();
			}
			reCalculateProductTotal();

		} else if (KEYPAD_FRAGMENT_TOTAL_TASK_CODE == context) {

			productBean.setProductTotalAmount(Float.parseFloat(value));
			receiveListAdapter.notifyDataSetChanged();

		} else if (KEYPAD_FRAGMENT_TAX_RATE_TASK_CODE == context) {

			productBean.setVATRate(Float.parseFloat(value));
			reCalculateProductTotal();

		} else if (KEYPAD_MRP_TASK_CODE == context) {

			productBean.setProductPrice(Float.parseFloat(value));
			productBean.setMRPChanged(true);
			receiveListAdapter.notifyDataSetChanged();
		}
		popKeypadFragment();
	}
	
	private void selectProduct(){
	    showActionbarFilterLayouts();
        if (productBean.getProductBilledQty() == 0) {
            addToSelectedItemList();
            incQtyByOne();
        } else {
            addToSelectedItemList();
        }
	}
	
	private void showActionbarFilterLayouts() {
       
       actionbarBrandFilterLayout.setVisibility(View.GONE);
       actionbarCategoryLayout.setVisibility(View.VISIBLE);
    }

    private void hideActionbarFilterLayouts() {
	       
        actionbarBrandFilterLayout.setVisibility(View.INVISIBLE);
        actionbarCategoryLayout.setVisibility(View.INVISIBLE);
    }
	
	private void setListDataAfterClearSearch(){
	    showActionbarFilterLayouts();
	    /*
         * setting adapter value when all category was selected
         */
        if (null == productCategory) {
            if (null != receiveListAdapter) {
                receiveListAdapter.clear();
                    headerValueTextView.setText(SnapToolkitTextFormatter.formatPriceText(headerTotalValue, getActivity()));
                    if (null != productList && productList.size() > 0) {
                        headerCountTextView.setText(String.valueOf(productList.size()));
                        receiveListAdapter.addAll(productList);
                    } else {
                        headerCountTextView.setText("0");
                    }
                
                receiveListAdapter.notifyDataSetChanged();
            }
            /*
             * setting adapter value when any category was selected
             */
        } else {
            if (null != receiveListAdapter) {
                receiveListAdapter.clear();
                headerValueTextView.setText(SnapToolkitTextFormatter.formatPriceText(headerValueByCategory, getActivity()));
                if (null != filteredListByCategory && filteredListByCategory.size()>0) {
                    headerCountTextView.setText(String.valueOf(filteredListByCategory.size()));
                    receiveListAdapter.addAll(filteredListByCategory);
                } else {
                    headerCountTextView.setText("0");
                }
                receiveListAdapter.notifyDataSetChanged();
            }
        }
	}
	
	/*
	 * creating a new productBean POJO
	 */
	private void addNewCreatedProduct(InventorySku inventorySku){
		productBean = new ProductBean();
		productBean.setProductName(inventorySku.getProductSku().getProductSkuName());
		productBean.setProductSkuID(inventorySku.getProductSku().getProductSkuCode());
		productBean.setProductQty(inventorySku.getQuantity());
		productBean.setProductPrice(inventorySku.getProductSku().getProductSkuMrp());
		productBean.setProductCategoryID(inventorySku.getProductSku().getProductCategory().getCategoryId());
		if(inventorySku.getProductSku().getProductBrand() != null) {
            productBean.setProductBrandID(inventorySku.getProductSku().getProductBrand().getBrandId());
            productBean.setProductCompanyID(inventorySku.getProductSku().getProductBrand().getCompany().getCompanyId());
		}
		productBean.setImageUri(inventorySku.getProductSku().getImageUrl());
		productBean.setGDB(inventorySku.getProductSku().isGDB());
		productBean.setInventorySerialNumber(inventorySku.getSlNo());
		productBean.setProductPurchasePrice(inventorySku.getPurchasePrice());
		productBean.setVATRate(inventorySku.getProductSku().getVAT());
	}

	private void addToSelectedItemList() {
		if (null == selectedItemList) {
			selectedItemList = new ArrayList<ProductBean>();
		}
		/*
         * when a purchase order is selected and a product is to be added to the
         * selected item list 
         */
		if(null != order){
		    /*
		     * if the product being added is not in the product bean hash map
		     * then it is added to the product bean hash map    
		     */
			if(!productBeanMap.containsKey(productBean.getProductSkuID())){			    
	            productBeanMap.put(productBean.getProductSkuID(), productBean);
	            /*
	             * if the product brand and company isn't tagged to the selected
	             * distributor then it is tagged to the distributor
	             */
	            boolean isBrandTagged = false;
	            if (!brandMap.containsKey(productBean.getProductBrandID())) {
	                Brand brand = new Brand();
	                brand.setBrandId(productBean.getProductBrandID());
	                brandMap.put(productBean.getProductBrandID(), brand);
	            } else {
                    isBrandTagged = true;
                }
	            boolean isCompanyTagged = false;
	            if (!companyMap.containsKey(productBean.getProductCompanyID())) {
	                Company company = new Company();
	                company.setCompanyId(productBean.getProductCompanyID());
	                companyMap.put(productBean.getProductCompanyID(), company);
	            } else {
                    isCompanyTagged = true;
                }
	            boolean isProductTagged = false;
	            if (!distributorProductHashMap.containsKey(productBean.getProductSkuID())) {
	                DistributorProductMap distributorProductMap = new DistributorProductMap();
	                ProductSku productSku = new ProductSku();
	                productSku.setProductSkuCode(productBean.getProductSkuID());
	                distributorProductMap.setDistributor(distributor);
	                distributorProductMap.setDistributorProductSku(productSku);
	                distributorProductHashMap.put(productBean.getProductSkuID(), distributorProductMap);
	            } else {
                    isProductTagged = true;
                }
	        	addProductToExistingPO = new AddProductToExistingPO(getActivity(), ADD_PRODUCT_TO_EXISTING_PO_TASKCODE, InventoryReceiveFragment.this, productBean.getProductSkuID(), order, distributor, isBrandTagged, isCompanyTagged, isProductTagged);
	        	addProductToExistingPO.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } else {
            /*
             * when no purchase order is selected
             * the product to be added is checked against the product bean hash map
             * if is not present then it added to the hash map the added to the product list at location 0
             */
            if (!productBeanMap.containsKey(productBean.getProductSkuID())) {
                productList.add(0, productBean);
                productBeanMap.put(productBean.getProductSkuID(), productBean);
            }
		}
		productBean.setSelected(true);
		/*
		 * adding the product to the selected item list
		 */
		selectedItemList.add(productBean);
		/*
		 * Initializing the adapter if it is null and populating it 
		 */
        if (null != receiveListAdapter) {
            receiveListAdapter.clear();
            receiveListAdapter.addAll(productList);
        	receiveListAdapter.notifyDataSetChanged();
        } else {
                receiveListAdapter = new ReceiveListAdapter(getActivity(), new ArrayList<ProductBean>(), InventoryReceiveFragment.this);
                receiveListAdapter.addAll(productList);
                receiveListView.setAdapter(receiveListAdapter);            
        }
        headerValueTextView.setText(SnapToolkitTextFormatter.formatPriceText(headerTotalValue, getActivity()));
        headerCountTextView.setText(String.valueOf(productList.size()));
        headerNameTextView.setText(allCategories);
        if(searchProductEditText.getText().toString().trim().length()>0){
    		searchProductEditText.setText("");
    	}
        showActionbarFilterLayouts();
	}
	
	/*
	 * when a product is removed from the selected item list
	 */
	private void removeFromSelectedList(){
        productBean.setSelected(false);
        selectedItemList.remove(productBean);
        receiveListAdapter.notifyDataSetChanged();
	}

	private void reCalculateProductTotal() {
	    productBean.setVATAmount((productBean.getProductPurchasePrice() * productBean.getVATRate()) / (100 + productBean.getVATRate()) * productBean.getProductBilledQty());
	    productBean.setProductNetAmount((productBean.getProductPurchasePrice() * productBean.getProductBilledQty()) - productBean.getProductDiscount() - productBean.getVATAmount());
        productBean.setProductTotalAmount(productBean.getProductNetAmount() + productBean.getVATAmount());
		receiveListAdapter.notifyDataSetChanged();
	}

	private void addKeyPadFragment() {
	    FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		if (keypadFragment == null) {
			keypadFragment = new NumKeypadFragment();
			keypadFragment
			.setKeypadEnterListener(InventoryReceiveFragment.this);
		}
        ft.replace(R.id.purchase_order_receive_keypad_layout, keypadFragment,
                stringKeypadFragment);
		if (!keypadFragment.isAdded()) {
			ft.addToBackStack(stringKeypadFragment);
		}
		ft.commit();
		fm.executePendingTransactions();
		keypadLayout.setVisibility(View.VISIBLE);
		transparentOverlay.setVisibility(View.VISIBLE);
	}

	public void popKeypadFragment(){
	    getFragmentManager().popBackStack(stringKeypadFragment, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		transparentOverlay.setVisibility(View.GONE);
		receiveListAdapter.selectedView.setEnabled(true);
		receiveListAdapter.notifyDataSetChanged();
	}
	
    public boolean isFilterFragmentVisible(){
    	if(null != filterFragment && filterFragment.isAdded())
    		return true;
    	else
    		return false;
    }
    
    public void hideFilterFragment(){
    	actionbarBrandFilterLayout.setSelected(false);
    	actionbarBrandFilterLayout.setVisibility(View.GONE);
    	actionbarCategoryLayout.setSelected(false);
    	actionbarDistributorInfoLayout.setSelected(true);
    	menu.findItem(R.id.search_meuitem).setVisible(false);
    	productSearchLayout.setVisibility(View.VISIBLE);
    	getFragmentManager().popBackStack();
    }
	
	private void incQtyByOne(){
		productBean.incProductReceivedQty();
		productBean.incProductBilledQty();
		productBean.incPendingQty();
		receiveListAdapter.notifyDataSetChanged();
	}
	
	private void clearAllListandMap(){
		if (null != selectedItemList && selectedItemList.size() > 0) {
			selectedItemList.clear();
		}
		if(null != filterBrandList && filterBrandList.size()>0){
			filterBrandList.clear();
		}
		if(null != productList && productList.size()>0){
			productList.clear();
		}
		if(null != productBeanMap && productBeanMap.size()>0){
			productBeanMap.clear();
		}
		if(null != brandMap && brandMap.size()>0){
			brandMap.clear();
		}
		if(null != companyMap && companyMap.size()>0){
        	companyMap.clear();
        }
		if (null != distributorProductHashMap && distributorProductHashMap.size()>0) {
            distributorProductHashMap.clear();
        }
	}
	
	public boolean isKeypadVisible(){
		if(null != keypadFragment && keypadFragment.isAdded())
			return true;
		else
			return false;
	}
	
	View.OnClickListener invoiceImagePositiveClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {SnapCommonUtils.dismissAlert();
			// TODO Auto-generated method stub
			invoiceImageBitmap = null;
			invoiceImageViewThumbnail.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icon_camera));
			SnapCommonUtils.dismissAlert();
			invoiceImageLayout.setVisibility(View.GONE);
		}
	};
	
	View.OnClickListener invoiceImageNegativeClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			SnapCommonUtils.dismissAlert();
		}
	};

	View.OnClickListener onOverLayButtonClicked = new View.OnClickListener() {

		@SuppressLint("NewApi")
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			if(R.id.btn_overlay_order_receive == v.getId()){
				if(isKeypadVisible()){
					popKeypadFragment();
				}
				summaryLayout.setVisibility(View.VISIBLE);
				if(null != order){
					invoiceNumberEditText.setText(order.getInvoiceID());
					try {
						if(null != order.getImage()){
							invoiceImageBitmap = new BitmapDrawable(MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.parse(order.getImage()))).getBitmap();	
						}else{
							invoiceImageBitmap = null;
						}					
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						invoiceImageBitmap = null;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						invoiceImageBitmap = null;
					}
					if(null != invoiceImageBitmap){
						invoiceImageViewThumbnail.setImageBitmap(invoiceImageBitmap);
					}else{
						invoiceImageViewThumbnail.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icon_camera));
					}
				}else{
					invoiceImageViewThumbnail.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icon_camera));
				}
				summaryDistributorNameTextView.setText(distributor.getSalesmanName());
        		summaryDistributorBrandsTextView.setText(distributor.getCompanyNames());
        		summaryDistributorNumberTextView.setText(distributor.getPhoneNumber());
        		int totalQuantity = 0;
        		float totalOrderMrp = 0;
                float summaryPurchaseValue = 0;
                float summaryTaxValue = 0;
        		totalOrderAmount = 0;
        		summaryDiscountValue = 0;
        		summaryNetPayable = 0;
        		summaryLineItemsTextView.setText("0");        		
        		if(null != selectedItemList && selectedItemList.size()>0){
        			summaryLineItemsTextView.setText(String.valueOf(selectedItemList.size()));
        			for (ProductBean bean : selectedItemList) {
						totalQuantity += bean.getProductBilledQty();
						totalOrderAmount += bean.getProductPrice() * bean.getProductBilledQty();
						if (!bean.isPaid()) {
	                        summaryTaxValue += bean.getVATAmount();
	                        summaryDiscountValue += bean.getProductDiscount();
	                        summaryPurchaseValue += bean.getProductPurchasePrice() * bean.getProductBilledQty();
						    summaryNetPayable += bean.getProductTotalAmount();
                        }						
						totalOrderMrp += bean.getProductPrice()*bean.getProductBilledQty();
					}        			
        		}
        		summaryPurchaseValueTextView.setText(SnapToolkitTextFormatter.formatPriceText(summaryPurchaseValue - summaryTaxValue, getActivity()));
        		summaryTaxTextView.setText(SnapToolkitTextFormatter.formatPriceText(summaryTaxValue, getActivity()));
        		summaryDiscountTextView.setText(SnapToolkitTextFormatter.formatPriceText(summaryDiscountValue, getActivity()));
        		summaryNetPayableTextView.setText(SnapToolkitTextFormatter.formatPriceText(summaryNetPayable, getActivity()));
        		summaryQuantityTextView.setText(String.valueOf(totalQuantity));
    			summaryMRPTextView.setText(SnapToolkitTextFormatter.formatPriceText(totalOrderMrp, getActivity()));
    			summaryAgencyNameTextView.setText(distributor.getAgencyName());
    			summaryPaymentAmountTextView.setText(SnapToolkitTextFormatter.formatPriceText(summaryNetPayable, getActivity()));
			}else if (R.id.receive_summary_overlay_button == v.getId()){
				invoiceNumberEditText.setText("");
				invoiceImageBitmap = null;
				summaryLayout.setVisibility(View.GONE);
			}			
		}
	};

	/*
	 *called when any product category is selected using the filter fragment 
	 */
	@Override
	public void onProductSubCategorySelected(ProductCategory selectedProductSubCategory) {
		// TODO Auto-generated method stub
		
		productCategory = selectedProductSubCategory;
		getFragmentManager().popBackStack();
		actionbarCategoryLayout.setSelected(false);
		menu.findItem(R.id.search_meuitem).setVisible(false);
		/*
		 * checking if the selected category is "all category" Null or not
		 */
		if(null != selectedProductSubCategory){
			headerNameTextView.setText(selectedProductSubCategory.getCategoryName());
			categoryNameTextView.setText(selectedProductSubCategory.getParenCategory().getCategoryName());
			subCategoryNameTextView.setText(selectedProductSubCategory.getCategoryName());
			/*
			 * if selected category is not null then the product list is filtered using the selected category
			 */
				if(null != productList && productList.size()>0){
					filterProductsByCategoryTask = new FilterProductsByCategoryTask(FILTER_PRODUCTS_BY_CATEGORY_TASKCODE, InventoryReceiveFragment.this, productList);
					filterProductsByCategoryTask.execute(selectedProductSubCategory.getCategoryId());
				}
		}else{
		    /*
		     * if the selected category is null 
		     * then displaying the product list as it is
		     */
			headerNameTextView.setText(allCategories);
			categoryNameTextView.setText(allCategories);
			subCategoryNameTextView.setText(allCategories);
				if(null != receiveListAdapter){
					receiveListAdapter.clear();
					if(null != productList && productList.size()>0){
						headerCountTextView.setText(String.valueOf(productList.size()));
						headerValueTextView.setText(SnapToolkitTextFormatter.formatPriceText(headerTotalValue, getActivity()));
						receiveListAdapter.addAll(productList);						
					}
					receiveListAdapter.notifyDataSetChanged();
				}
			
		}		
	}

}
