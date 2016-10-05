package com.snapbizz.snapbilling.fragments;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.stmt.QueryBuilder;
import com.jess.ui.TwoWayAbsListView.RecyclerListener;
import com.jess.ui.TwoWayAdapterView;
import com.jess.ui.TwoWayGridView;
import com.snapbizz.plugin.PrinterPlugin.BTPrinterPlugin;
import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.adapters.BarcodeProductSearchListAdapter;
import com.snapbizz.snapbilling.adapters.BillListAdapter;
import com.snapbizz.snapbilling.adapters.BillListAdapter.BillItemEditListener;
import com.snapbizz.snapbilling.adapters.CustomerSearchListAdapter;
import com.snapbizz.snapbilling.adapters.DistributorSearchListAdapter;
import com.snapbizz.snapbilling.adapters.ProductSearchListAdapter;
import com.snapbizz.snapbilling.adapters.QuickAddCategoryAdapter;
import com.snapbizz.snapbilling.adapters.QuickAddProductAdapter;
import com.snapbizz.snapbilling.adapters.UnitTypeAdapter;
import com.snapbizz.snapbilling.asynctasks.AddEditDistributorTask;
import com.snapbizz.snapbilling.asynctasks.GetCustomerSuggestedProductsTask;
import com.snapbizz.snapbilling.asynctasks.GetProductOffersTask;
import com.snapbizz.snapbilling.asynctasks.GetQuickAddProductsTask;
import com.snapbizz.snapbilling.asynctasks.GetQuickAddSubCategoriesTask;
import com.snapbizz.snapbilling.asynctasks.QueryDistributorInfoTask;
import com.snapbizz.snapbilling.asynctasks.SearchProductSkuTask;
import com.snapbizz.snapbilling.fragments.ProductKeypadFragment.KeyboardListener;
import com.snapbizz.snapbilling.interfaces.SearchedProductClickListener;
import com.snapbizz.snapbilling.utils.GoogleAnalyticsTracker;
import com.snapbizz.snapbilling.utils.SnapBillingConstants;
import com.snapbizz.snapbilling.utils.SnapBillingTextFormatter;
import com.snapbizz.snapbilling.utils.SnapBillingUtils;
import com.snapbizz.snapbilling.utils.SnapBillingValidationUtils;
import com.snapbizz.snaptoolkit.adapters.VirtualStoreProductAdapter;
import com.snapbizz.snaptoolkit.customviews.SoftInputEditText;
import com.snapbizz.snaptoolkit.db.SnapbizzDB;
import com.snapbizz.snaptoolkit.db.InvoiceHelper.PaymentMode;
import com.snapbizz.snaptoolkit.db.InvoiceHelper.PaymentType;
import com.snapbizz.snaptoolkit.db.dao.CustomerDetails;
import com.snapbizz.snaptoolkit.db.dao.Customers;
import com.snapbizz.snaptoolkit.db.dao.Transactions;
import com.snapbizz.snaptoolkit.domains.Distributor;
import com.snapbizz.snaptoolkit.domains.ProductCategory;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.domains.ShoppingCart;
import com.snapbizz.snaptoolkit.domains.VAT;
import com.snapbizz.snaptoolkit.domainsV2.Models.BillItem;
import com.snapbizz.snaptoolkit.domainsV2.Models.UOM;
import com.snapbizz.snaptoolkit.fragments.NumKeypadFragment;
import com.snapbizz.snaptoolkit.fragments.NumKeypadFragment.NumberKeypadEnterListener;
import com.snapbizz.snaptoolkit.interfaces.OnActionbarNavigationListener;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snapbilling.services.PrinterService;
import com.snapbizz.snaptoolkit.utils.CustomToast;
import com.snapbizz.snaptoolkit.utils.KeyPadMode;
import com.snapbizz.snaptoolkit.utils.PrinterFactory;
import com.snapbizz.snaptoolkit.utils.PrinterFormatter;
import com.snapbizz.snaptoolkit.utils.PrinterManager;
import com.snapbizz.snaptoolkit.utils.PrinterType;
import com.snapbizz.snaptoolkit.utils.SkuUnitType;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;
import com.snapbizz.snaptoolkit.utils.TransactionType;
import com.snapbizz.snaptoolkit.utils.ValidationUtils;

public class BillCheckoutFragment extends Fragment implements
		BillItemEditListener, KeyboardListener, OnQueryCompleteListener,
		NumberKeypadEnterListener, OnKeyListener {

	private ShoppingCart shoppingCart;
	private int mode;
	private int editingProductPosition;
	private static final String TAG = "[BillCheckoutFragment]";
	private final int SAVE_TRANSACTION_TASKCODE = 1;
	private final int GET_OFFERED_PRODUCTS = 2;
	private final int SEARCH_PRODUCTS_TASKCODE = 3;
	private final int GET_CUSTOMERSUGGESTION_TASKCODE = 6;
	private final int GET_QUICKADD_CATEGORY_TASKCODE = 7;
	private final int GET_QUICKADD_PRODUCTS_TASKCODE = 8;
    private final int GET_DISTRIBUTORINFO_TASKCODE = 10;
    private final int ADD_DISTRIBUTORINFO_TASKCODE = 11;
    
	private final int EDIT_ITEM_CONTEXT = 1;
	private final int ENTER_CASH_CONTEXT = 2;
	private final int EDIT_TOTALDISCOUNT_CONTEXT = 3;
	private final int QUICKADD_CONTEXT = 4;
	private final int QUICKADD_WEIGHT_CONTEXT = 5;
	private final int BARCODE_SEARCH_CONTEXT = 9;
	private String currentQuickAddProductCode;
	private String currentQuickAddProductName;
	private View deleteReturnLayout;
	private View checkoutLayout;
	private View addCustomerLayout;
	private View customerSearchLayoutContainer;
	private View customerInformationLayout;
	private View customerDetailsLayout;
	private View quickAddRelativeLayout;
	private View billPriceChageLayout;
	private TextView rateTextView;
	private ImageView rateImageView;
	private View lastClickedQuickAddView;
	private Button discountButton;
	private Button cashButton;
	private TextView billTotalTextView;
	private TextView billVatView;
	private TextView amountTextView;
	private TextView savingsTextView;
	private TextView savingsText;
	private TextView totalTextView;
	private TextView creditTextView;
	private TextView changeTextView;
	private TextView customerPaymentTextView;
	private CheckBox customerPaymentCheck;
	private TextView totalQtyTextView;
	private TextView customerNameTextView;
	private TextView customerNumberTextView;
	private TextView customerAddressTextView;
	private TextView customerDueTextView;
	private TextView customerCreditLimitTextView;
	private TextView customerDueAmountTextView;
	private TextView customerMembershipDateTextView;
	private EditText customerSearchEditText;
	private AutoCompleteTextView barcode_product_name_search_edittext;
	private AutoCompleteTextView productSearchEditText;
	private BarcodeProductSearchListAdapter barcodeProductSearchAdapter;
	private final int SEARCH_BARCODE_PRODUCTS_TASKCODE = 15;
	private RelativeLayout searchLayout;
	private ImageButton clearProductSearch;
	private ImageButton clearCustomersSearch;
	private TwoWayGridView offersGridView;
	private TwoWayGridView suggestedProductsGridView;
	private TwoWayGridView quickAddGridView;
	private TwoWayGridView quickaddProductGridView;
	private ListView unitTypeListView;
	private ListView billListView;
	private ProductKeypadFragment keypadFragment;
	private NumKeypadFragment numKeypadFragment;
	private NumKeypadFragment barcodeKeypadFragment;
	private NumKeypadFragment quickaddKeypadFragment;
	private VirtualStoreProductAdapter offersAdapter;
	private VirtualStoreProductAdapter suggestedProductsAdapter;
	private ProductSearchListAdapter productSearchAdapter;
	private QuickAddCategoryAdapter quickaddCategoryAdapter;
	private QuickAddProductAdapter quickaddProductAdapter;
	private CustomerSearchListAdapter customerAdapter;
	private DistributorSearchListAdapter distributorAdapter;
	private ArrayList<ProductCategory> quickAddCategories;
	public BillListAdapter billListAdapter;
	private SearchedProductClickListener searchedProductClickListener;
	private OnActionbarNavigationListener onActionbarNavigationListener;
	private PrinterManager printerManager;
	private SearchProductSkuTask searchProductSkuTask;
	private ProductSku quickaddProduct;
	private ProductCategory quickaddProductCategory;
	private UnitTypeAdapter unitTypeAdapter;
	private OnCreateEditQuickAddProductClickListener onCreateEditQuickAddClickListener;
	private int quickaddCategoryPosition;
	private SkuUnitType quickaddUnitType;
	private PrinterType printerType;
	private Dialog printerTypeDialog;
	private ImageButton clearBarcodeText;
	private View overLay = null;
	private int position = 0;
	private SnapbizzDB snapbizzDB;

	public ShoppingCart getShoppingCart() {
		return shoppingCart;
	}

	public void setShoppingCart(ShoppingCart shoppingCart) {
		Log.d(TAG, "SettingShoppingCart:"+shoppingCart);
		this.shoppingCart = shoppingCart;
		if (billListAdapter != null) {
			billListAdapter.setShoppingCart(shoppingCart);
			billListAdapter.notifyDataSetChanged();
		}
		if (shoppingCart != null) {
			shoppingCart.setVATCalculated(false);
			shoppingCart.setInvoice(false);
            if (shoppingCart.getShoppingCartId() == SnapBillingConstants.LAST_SHOPPING_CART)
				shoppingCart.setVATCalculated(true);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		GoogleAnalyticsTracker.getInstance(getActivity()).fragmentLoaded(getClass().getSimpleName(), getActivity());
		View view = inflater.inflate(R.layout.fragment_bill_checkout,
				container, false);
		billTotalTextView = (TextView) view
				.findViewById(R.id.bill_totalprice_textview);
		unitTypeListView = (ListView) view.findViewById(R.id.unittype_listview);
		billVatView = (TextView) view.findViewById(R.id.vat_value_textview);
		deleteReturnLayout = view.findViewById(R.id.delete_return_layout);
		amountTextView = (TextView) view
				.findViewById(R.id.amount_value_textview);
		savingsTextView = (TextView) view.findViewById(R.id.savings_value_textview);
		savingsText=(TextView) view.findViewById(R.id.savings_textview);
		
		totalTextView = (TextView) view.findViewById(R.id.total_value_textview);
		creditTextView = (TextView) view.findViewById(R.id.change_value_textview);
		changeTextView = (TextView) view.findViewById(R.id.return_value_textview);
		customerPaymentTextView = (TextView) view.findViewById(R.id.customer_textview);
		customerPaymentCheck = (CheckBox) view.findViewById(R.id.customer_pay_check);
		discountButton = (Button) view.findViewById(R.id.discount_value_button);
		cashButton = (Button) view.findViewById(R.id.cash_value_button);
		offersGridView = ((TwoWayGridView) view
				.findViewById(R.id.checkout_offers_gridview));
		suggestedProductsGridView = ((TwoWayGridView) view
				.findViewById(R.id.checkout_suggested_gridview));
		checkoutLayout = view.findViewById(R.id.complete_bill_layout);
		customerSearchEditText = (EditText) view
				.findViewById(R.id.customer_search_edittext);
		addCustomerLayout = view.findViewById(R.id.add_customer_layout);
		customerSearchLayoutContainer = view
				.findViewById(R.id.customersearch_linearlayout);
		customerInformationLayout = view
				.findViewById(R.id.customer_info_layout);
		customerDetailsLayout = view
				.findViewById(R.id.customer_details_relativelayout);
		customerAddressTextView = (TextView) view
				.findViewById(R.id.customer_address_textview);
		customerNameTextView = (TextView) view
				.findViewById(R.id.customername_textivew);
		customerNumberTextView = (TextView) view
				.findViewById(R.id.customernumber_textivew);
		customerDueTextView = (TextView) view
				.findViewById(R.id.search_customerDue_textview);
		customerCreditLimitTextView = (TextView) view
				.findViewById(R.id.customer_creditlimit_value_textview);
		customerDueAmountTextView = (TextView) view
				.findViewById(R.id.customer_duevalue_textview);
		customerMembershipDateTextView = (TextView) view
				.findViewById(R.id.customer_memberdate_textview);
		customerDetailsLayout.setVisibility(View.GONE);
		clearCustomersSearch = (ImageButton) view
				.findViewById(R.id.clear_customer_button);
		totalQtyTextView = (TextView) view
				.findViewById(R.id.bill_totalqty_textview);
		quickAddRelativeLayout = view
				.findViewById(R.id.quickadd_relativelayout);
		billPriceChageLayout = view.findViewById(R.id.bill_header_layout);
		rateImageView = (ImageView) billPriceChageLayout
				.findViewById(R.id.rate_dropdwn_imageview);
		
		rateTextView = (TextView) billPriceChageLayout
				.findViewById(R.id.rate_textview);
		rateTextView.setOnClickListener(cartSPChangeListener);
		if (shoppingCart == null)
			Log.e(TAG, "ShoppingCart is null!!!");

		if (shoppingCart != null &&
				shoppingCart.getShoppingCartId() == SnapBillingConstants.LAST_SHOPPING_CART) {
			savingsTextView.setVisibility(View.GONE);
			savingsText.setVisibility(View.GONE);
			rateImageView.setVisibility(View.GONE);
			view.findViewById(R.id.bill_buttons_layout).setVisibility(View.GONE);
			view.findViewById(R.id.receive_complete).setVisibility(View.VISIBLE);
			/*view.findViewById(R.id.delivery_bill_button).setVisibility(View.GONE);
			view.findViewById(R.id.vat_bill_button).setVisibility(View.GONE);
			view.findViewById(R.id.invoice_bill_button).setVisibility(View.GONE);*/
		} else {
			savingsTextView.setVisibility(View.VISIBLE);
			savingsText.setVisibility(View.VISIBLE);
			rateImageView.setVisibility(View.VISIBLE);
			view.findViewById(R.id.bill_buttons_layout).setVisibility(View.VISIBLE);
			view.findViewById(R.id.receive_complete).setVisibility(View.GONE);			
			/*view.findViewById(R.id.delivery_bill_button).setVisibility(View.VISIBLE);
			view.findViewById(R.id.vat_bill_button).setVisibility(View.VISIBLE);
			view.findViewById(R.id.invoice_bill_button).setVisibility(View.VISIBLE);*/
		}
		clearCustomersSearch.setOnClickListener(onSearchClearListener);
		view.findViewById(R.id.billitem_delete_button).setOnClickListener(
				onProductDeleteReturnEditClickListener);
		view.findViewById(R.id.billitem_return_button).setOnClickListener(
				onProductDeleteReturnEditClickListener);
		view.findViewById(R.id.billitem_editname_button).setOnClickListener(
				onProductDeleteReturnEditClickListener);
		cashButton.setOnClickListener(onCashDiscountClickListener);
		discountButton.setOnClickListener(onCashDiscountClickListener);
		view.findViewById(R.id.cancel_bill_button).setOnClickListener(
				onBillCompleteClickListener);
		if (shoppingCart != null && shoppingCart.getShoppingCartId() == SnapBillingConstants.LAST_SHOPPING_CART) {
			view.findViewById(R.id.pay_receive_button).setOnClickListener(onReceiveCompleteClickListener);
			view.findViewById(R.id.offers_button).setVisibility(View.INVISIBLE);
			((TextView) view.findViewById(R.id.rate_textview)).setText(R.string.pp);
		} else {
			view.findViewById(R.id.pay_bill_button).setOnClickListener(onBillCompleteClickListener);
			view.findViewById(R.id.offers_button).setVisibility(View.VISIBLE);
			((TextView) view.findViewById(R.id.rate_textview)).setText(R.string.rate);
		}
		
		view.findViewById(R.id.delivery_bill_button).setOnClickListener(
				onBillCompleteClickListener);
		view.findViewById(R.id.offers_button).setOnClickListener(
				onBillOptionsClickListener);
		view.findViewById(R.id.quickadd_button).setOnClickListener(
				onBillOptionsClickListener);
		view.findViewById(R.id.search_button).setOnClickListener(
				onBillOptionsClickListener);
		view.findViewById(R.id.bill_footer_layout).setOnClickListener(
				onBillOptionsClickListener);
		view.findViewById(R.id.add_customer_button).setOnClickListener(
				onAddEditDelCustomerClickListener);
		view.findViewById(R.id.editcustomer_info_button).setOnClickListener(
				onAddEditDelCustomerClickListener);
		view.findViewById(R.id.remove_customer_button).setOnClickListener(
				onAddEditDelCustomerClickListener);
		view.findViewById(R.id.expand_customerdetails_button)
				.setOnClickListener(onExpandCustomerDetailsClickListener);
		view.findViewById(R.id.save_button).setOnClickListener(
				customerAddEditListener);
		view.findViewById(R.id.edit_button).setOnClickListener(
				customerAddEditListener);
		view.findViewById(R.id.cancel_button).setOnClickListener(
				customerAddEditListener);

		quickAddRelativeLayout.findViewById(
				R.id.quickadd_bathroom_relativelayout).setOnClickListener(
				onQuickAddClickListener);
		quickAddRelativeLayout.findViewById(
				R.id.quickadd_kitchen_relativelayout).setOnClickListener(
				onQuickAddClickListener);
		quickAddRelativeLayout.findViewById(R.id.quickadd_food_relativelayout)
				.setOnClickListener(onQuickAddClickListener);
		quickAddRelativeLayout
				.findViewById(R.id.quickadd_others_relativelayout)
				.setOnClickListener(onQuickAddClickListener);
		quickAddRelativeLayout.findViewById(
				R.id.quickadd_homecare_relativelayout).setOnClickListener(
				onQuickAddClickListener);
		view.findViewById(R.id.print_bill_button).setOnClickListener(
				onPrintMsgMailClickListener);
		view.findViewById(R.id.print_receive_button).setOnClickListener(
				onPrintMsgMailClickListener);
		
		view.findViewById(R.id.vat_bill_button).setOnClickListener(
				onPrintMsgMailClickListener);
		view.findViewById(R.id.invoice_bill_button).setOnClickListener(
				onPrintMsgMailClickListener);
		view.findViewById(R.id.vat_bill_button).setSelected(
				SnapSharedUtils.getIsVatSelected(SnapCommonUtils
						.getSnapContext(getActivity())));

		offersGridView.setOnItemClickListener(onOfferClickListener);
		offersGridView.setSelector(R.drawable.listitem_bg_selector);
		quickAddGridView = (TwoWayGridView) view
				.findViewById(R.id.quickadd_gridview);
		quickaddProductGridView = (TwoWayGridView) view
				.findViewById(R.id.quickadd_product_gridview);
		quickaddProductGridView
				.setOnItemClickListener(onQuickAddProductClickListener);
		suggestedProductsGridView
				.setOnItemClickListener(onSuggestedProductsClickListener);
		((TextView) view.findViewById(R.id.billcomplete_date_textview))
				.setText(new SimpleDateFormat(
						SnapBillingConstants.BILL_DATEFORMAT)
						.format(new Date()));
		view.findViewById(R.id.quickadd_weight_textview).setOnClickListener(
				onEnterCustomWeightClickListener);
		view.findViewById(R.id.quickadd_volume_textview).setOnClickListener(
				onEnterCustomWeightClickListener);
		view.findViewById(R.id.discount_clear_button).setOnClickListener(
				onClearCashDiscountClickListener);
		view.findViewById(R.id.cash_clear_button).setOnClickListener(
				onClearCashDiscountClickListener);
		deleteReturnLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				v.setVisibility(View.GONE);
				billListAdapter.setLastSelectedPosition(-1);
				billListAdapter.notifyDataSetChanged();
			}
		});
		return view;
	}
	
	OnClickListener cartSPChangeListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (shoppingCart.getShoppingCartId() !=3 ) {
			try {
				PopupMenu popup = new PopupMenu(getActivity(), v);
				popup.getMenuInflater().inflate(R.menu.mrpsp, popup.getMenu());
				popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

					public boolean onMenuItemClick(MenuItem item) {
						if (keypadFragment != null && keypadFragment.isVisible())
							onActionbarNavigationListener.onActionbarNavigation("", android.R.id.home);
						Log.e("Tag", shoppingCart.getShoppingCartId()+"");
						GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(getActivity(), item, TAG);

						int spIndex = -1;
						switch (item.getItemId()) {
							case R.id.mrp_menuitem:
								spIndex = 0;
								break;
							case R.id.sp1_menuitem:
								spIndex = 1;
								break;
							case R.id.sp2_menuitem:
								spIndex = 2;
								break;
							case R.id.sp3_menuitem:
								spIndex = 3;
								break;
	
							default:
								break;
						}
						if(spIndex >= 0) {
							shoppingCart.updateAllBillItemPrices(spIndex);
							billListAdapter.notifyDataSetChanged();
							updateBillTextViews(getActivity());
							return true;
						}
						return false;
					}
				});
				popup.show();
				
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
			}
		}
	};

	View.OnClickListener onClearCashDiscountClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(getActivity(), v, TAG);
			if (v.getId() == R.id.discount_clear_button) {
				shoppingCart.setTotalDiscount(0);
				updateBillTextViews(getActivity());
			} else if (v.getId() == R.id.cash_clear_button) {
				try {
					if (!String.valueOf(shoppingCart.getCashPaid()).contains("E")) {
						shoppingCart.setCashPaid(0);
						shoppingCart.setCreditChanged(true);
						updateBillTextViews(getActivity());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	};

	View.OnClickListener onBarcodeSearchClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.barcode_product_name_search_edittext) {
				if (deleteReturnLayout != null) {
					deleteReturnLayout.setVisibility(View.GONE);
					unitTypeListView.setVisibility(View.GONE);
				}
				SnapCommonUtils.hideSoftKeyboard(getActivity(), customerSearchLayoutContainer.getWindowToken());
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				if (barcodeKeypadFragment == null) {
					barcodeKeypadFragment = new NumKeypadFragment();
					barcodeKeypadFragment
							.setKeypadEnterListener(BillCheckoutFragment.this);

					ft.replace(R.id.overlay_framelayout, barcodeKeypadFragment);
					if (!barcodeKeypadFragment.isAdded()) {
						ft.addToBackStack(null);
					}
					barcodeKeypadFragment.setContext(BARCODE_SEARCH_CONTEXT);
					barcodeKeypadFragment.setValue("0");
					ft.commit();
					RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(
							RelativeLayout.LayoutParams.WRAP_CONTENT,
							RelativeLayout.LayoutParams.WRAP_CONTENT);
					rlParams.addRule(RelativeLayout.ALIGN_LEFT,
							barcode_product_name_search_edittext.getId());
					rlParams.addRule(RelativeLayout.BELOW,
							barcode_product_name_search_edittext.getId());
					rlParams.setMargins(
							getResources().getDimensionPixelOffset(
									R.dimen.billlist_width) + 70, 80, 0, 0);
					overLay.setLayoutParams(rlParams);
					overLay.setVisibility(View.VISIBLE);

				} else {
					ft.show(barcodeKeypadFragment);
					RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(
							RelativeLayout.LayoutParams.WRAP_CONTENT,
							RelativeLayout.LayoutParams.WRAP_CONTENT);
					rlParams.addRule(RelativeLayout.ALIGN_LEFT,
							barcode_product_name_search_edittext.getId());
					rlParams.addRule(RelativeLayout.BELOW,
							barcode_product_name_search_edittext.getId());
					rlParams.setMargins(
							getResources().getDimensionPixelOffset(
									R.dimen.billlist_width) + 70, 80, 0, 0);
					overLay.setLayoutParams(rlParams);
					overLay.setVisibility(View.VISIBLE);

				}

			} else if (v.getId() == R.id.product_name_search_edittext) {
				if (barcodeKeypadFragment != null
						&& barcodeKeypadFragment.isAdded()) {
					getFragmentManager().popBackStack();
				}
			}
		}
	};

	AdapterView.OnItemClickListener onUnitTypeClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> adapterView, View view,
				int position, long id) {
			quickaddUnitType = SkuUnitType.getEnum(unitTypeAdapter
					.getItem(position));
			unitTypeAdapter.setLastSelectedItemPosition(position);
			unitTypeAdapter.notifyDataSetChanged();

		}
	};

	View.OnClickListener onEnterCustomWeightClickListener = new View.OnClickListener() {

		@SuppressLint("NewApi")
		@Override
		public void onClick(View arg0) {
			if (quickaddProduct == null) {
				CustomToast.showCustomToast(getActivity()
						.getApplicationContext(), arg0.getContext().getString(R.string.msg_select_product),
						Toast.LENGTH_SHORT, CustomToast.INFORMATION);
				return;
			}
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			if (quickaddKeypadFragment == null) {
				quickaddKeypadFragment = new NumKeypadFragment();
				quickaddKeypadFragment
						.setKeypadEnterListener(BillCheckoutFragment.this);
				ft.replace(R.id.weight_framelayout, quickaddKeypadFragment);

			} else {
				ft.attach(quickaddKeypadFragment);
			}
			ft.commit();
			quickAddRelativeLayout.findViewById(R.id.quickadd_weight_layout)
					.setVisibility(View.GONE);
			quickAddRelativeLayout.findViewById(R.id.quickadd_volume_layout)
					.setVisibility(View.GONE);
			quickaddKeypadFragment.setContext(QUICKADD_WEIGHT_CONTEXT);
			quickaddKeypadFragment.setIsDiscount(false);
			quickaddKeypadFragment.setNewEdit(true);
			quickaddKeypadFragment.setValue("0");
			quickaddKeypadFragment.setNewEdit(true);
			quickaddKeypadFragment.setValue("0");
			unitTypeListView.setVisibility(View.VISIBLE);
			getView().findViewById(R.id.weight_framelayout).setVisibility(View.VISIBLE);
		}
	};

	public interface OnCreateEditQuickAddProductClickListener {
		public void onAddQuickAddClicked(ProductCategory productCategory);

		public void onEditQuickAddClicked(ProductSku productSku);
	}

	View.OnClickListener onPrintMsgMailClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			v.setSelected(!v.isSelected());
			GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(getActivity(), v, TAG);
			if ((v.getId() == R.id.print_bill_button || v.getId() == R.id.print_receive_button ) && v.isSelected()) {
				if(!PrinterService.isConnected()) {
					CustomToast
					.showCustomToast(
							getActivity(),
							SnapBillingConstants.PRINTER_NOT_CONNECTED_ERRORMSG,
							Toast.LENGTH_LONG, CustomToast.WARNING);
				}
			} else if (R.id.invoice_bill_button == v.getId()) {
				shoppingCart.setInvoice(v.isSelected());
			} else if (R.id.vat_bill_button == v.getId()) {
				shoppingCart.setVATCalculated(v.isSelected());
				SnapSharedUtils.setIsVatSelected(v.isSelected(), SnapCommonUtils.getSnapContext(getActivity()));
			}
		}
	};

	AdapterView.OnItemClickListener onPrinterSelectListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> adapter, View view,
				int position, long id) {
			printerType = PrinterType
					.getPrinterEnum(SnapBillingConstants.PRINTER_TYPES[position]);
			SnapSharedUtils.savePrinter(printerType, getActivity());
			printerManager = PrinterFactory.createPrinterManager(printerType,
					getActivity());
			printerTypeDialog.dismiss();
			printerManager.connect();
		}
	};

	TwoWayAdapterView.OnItemClickListener onQuickAddProductClickListener = new TwoWayAdapterView.OnItemClickListener() {

		@SuppressLint("NewApi")
		@Override
		public void onItemClick(TwoWayAdapterView<?> parent, View view,
				int position, long id) {
			quickaddProduct = quickaddProductAdapter.getItem(position);
			if (quickaddProduct == null)
				onCreateEditQuickAddClickListener
						.onAddQuickAddClicked(quickaddProductCategory);
			else {
				unitTypeListView.setVisibility(View.GONE);
				quickAddRelativeLayout.findViewById(R.id.quickadd_input_layout)
						.setVisibility(View.VISIBLE);

				if (quickaddProduct.getProductSkuUnits().ordinal() == SkuUnitType.PC
						.ordinal()) {
					FragmentTransaction ft = getFragmentManager()
							.beginTransaction();
					if (quickaddKeypadFragment == null) {
						quickaddKeypadFragment = new NumKeypadFragment();
						quickaddKeypadFragment
								.setKeypadEnterListener(BillCheckoutFragment.this);
						ft.replace(R.id.weight_framelayout,
								quickaddKeypadFragment);
					} else {

						ft.attach(quickaddKeypadFragment);

					}
					ft.commit();
					getView().findViewById(R.id.weight_framelayout)
							.setVisibility(View.VISIBLE);
					quickaddKeypadFragment.setContext(QUICKADD_WEIGHT_CONTEXT);
					quickaddKeypadFragment.setIsDiscount(false);
					quickaddKeypadFragment.setNewEdit(true);
					quickaddKeypadFragment.setValue("0");
					quickaddKeypadFragment.setNewEdit(true);
					quickaddKeypadFragment.setValue("0");
					quickAddRelativeLayout.findViewById(
							R.id.quickadd_weight_layout).setVisibility(View.GONE);
					quickAddRelativeLayout.findViewById(
							R.id.quickadd_volume_layout).setVisibility(View.GONE);
				} else {
					if (quickaddProduct.getProductSkuUnits().ordinal() == SkuUnitType.KG
							.ordinal()
							|| quickaddProduct.getProductSkuUnits().ordinal() == SkuUnitType.GM
									.ordinal()) {
						quickAddRelativeLayout.findViewById(
								R.id.quickadd_weight_layout).setVisibility(View.VISIBLE);
						quickAddRelativeLayout.findViewById(
								R.id.quickadd_volume_layout).setVisibility(View.GONE);
						getView().findViewById(R.id.weight_framelayout)
								.setVisibility(View.GONE);
					} else {
						quickAddRelativeLayout.findViewById(
								R.id.quickadd_weight_layout).setVisibility(View.GONE);
						quickAddRelativeLayout.findViewById(
								R.id.quickadd_volume_layout).setVisibility(View.VISIBLE);
						getView().findViewById(R.id.weight_framelayout)
								.setVisibility(View.GONE);
					}
				}
				quickaddProductAdapter.setLastSelectedItemPosition(position);
				quickaddProductAdapter.notifyDataSetChanged();
				quickaddProductAdapter.clear();
				unitTypeAdapter.clear();
				unitTypeAdapter.setLastSelectedItemPosition(0);
				if (quickaddProduct.getProductSkuUnits().ordinal() == SkuUnitType.PC
						.ordinal())
					unitTypeAdapter
							.addAll(SnapBillingConstants.PACKET_UNIT_TYPES);
				else if (quickaddProduct.getProductSkuUnits().ordinal() == SkuUnitType.KG
						.ordinal()
						|| quickaddProduct.getProductSkuUnits().ordinal() == SkuUnitType.GM
								.ordinal())
					unitTypeAdapter
							.addAll(SnapBillingConstants.WEIGHT_UNIT_TYPES);
				else
					unitTypeAdapter
							.addAll(SnapBillingConstants.VOLUME_UNIT_TYPES);
				quickaddUnitType = SkuUnitType.getEnum(unitTypeAdapter
						.getItem(0));
				unitTypeAdapter.notifyDataSetChanged();
			}
		}

	};

	public boolean isQuickAddProductsLayoutVisibile() {
		if ((keypadFragment != null && keypadFragment.isVisible())
				|| (numKeypadFragment != null && numKeypadFragment.isVisible()))
			return false;
		else
			return quickAddRelativeLayout.findViewById(
					R.id.quickadd_products_layout).getVisibility() == View.VISIBLE
					&& mode == 0 ? true : false;
	}

	public void showQuickAddProductCategory() {
		if (searchProductSkuTask != null && !searchProductSkuTask.isCancelled())
			searchProductSkuTask.cancel(true);
		quickAddGridView.setVisibility(View.VISIBLE);
		quickAddRelativeLayout.findViewById(R.id.quickadd_generic_linearlayout)
				.setVisibility(View.VISIBLE);
		quickAddRelativeLayout.findViewById(R.id.quickadd_products_layout)
				.setVisibility(View.GONE);
		getView().findViewById(R.id.weight_framelayout)
				.setVisibility(View.GONE);
	}

	View.OnClickListener onWeightGmClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			quickAddRelativeLayout.findViewById(R.id.quickadd_input_layout)
					.setVisibility(View.GONE);
			shoppingCart.addItemToCart(quickaddProduct,
					Float.parseFloat(v.getTag().toString()), SkuUnitType.GM);
			showProductsLayout();
			billListAdapter.notifyDataSetChanged();
			updateBillTextViews(getActivity());
		}
	};

	View.OnClickListener onWeightKgClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			quickAddRelativeLayout.findViewById(R.id.quickadd_input_layout)
					.setVisibility(View.GONE);
			shoppingCart.addItemToCart(quickaddProduct,
					Float.parseFloat(v.getTag().toString()), SkuUnitType.KG);
			showProductsLayout();
			billListAdapter.notifyDataSetChanged();
			updateBillTextViews(getActivity());
		}
	};

	View.OnClickListener onWeightLtClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			quickAddRelativeLayout.findViewById(R.id.quickadd_input_layout)
					.setVisibility(View.GONE);
			shoppingCart.addItemToCart(quickaddProduct,
					Float.parseFloat(v.getTag().toString()), SkuUnitType.LTR);
			showProductsLayout();
			billListAdapter.notifyDataSetChanged();
			updateBillTextViews(getActivity());
		}
	};

	View.OnClickListener onWeightMlClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			quickAddRelativeLayout.findViewById(R.id.quickadd_input_layout)
					.setVisibility(View.GONE);
			shoppingCart.addItemToCart(quickaddProduct,
					Float.parseFloat(v.getTag().toString()), SkuUnitType.ML);
			showProductsLayout();
			billListAdapter.notifyDataSetChanged();
			updateBillTextViews(getActivity());
		}
	};

	public void addQuickAddProduct(ProductSku productSku) {
		onQuickAddCategoryClickListener.onItemClick(null, null,
				quickaddCategoryPosition, 1);
	}

	TwoWayAdapterView.OnItemClickListener onQuickAddCategoryClickListener = new TwoWayAdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(TwoWayAdapterView<?> parent, View view,
				int position, long id) {
			try {
				quickaddCategoryPosition = position;
				quickaddProductCategory = quickaddCategoryAdapter
						.getItem(position);
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
			showProductsLayout();
		}
	};

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			searchedProductClickListener = (SearchedProductClickListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ "must implement SearchedProductClickListener");
		}
		try {
			onActionbarNavigationListener = (OnActionbarNavigationListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ getString(R.string.exc_implementnavigation));
		}
		try {
			onCreateEditQuickAddClickListener = (OnCreateEditQuickAddProductClickListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ "must implement OnAddQuickAddClickListener");
		}
	}

	TextWatcher productSearchTextWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
			productSearchKeyStrokeTimer.cancel();
			if (s.length() == 0) {
				if (productSearchAdapter != null)
					productSearchAdapter.clear();
			} else {
				productSearchKeyStrokeTimer.start();
			}
		}

		CountDownTimer productSearchKeyStrokeTimer = new CountDownTimer(
				SnapBillingConstants.KEY_STROKE_TIMEOUT,
				SnapBillingConstants.KEY_STROKE_TIMEOUT) {

			@Override
			public void onTick(long arg0) {
			}

			@Override
			public void onFinish() {
				String keyword = productSearchEditText.getText().toString();
				if (searchProductSkuTask != null)
					searchProductSkuTask.cancel(true);
				if (keyword.length() > 0) {
					if (keyword.contains("'")) {
						keyword = keyword.replace("'", "");
						productSearchEditText.setText("");

					}
					searchProductSkuTask = new SearchProductSkuTask(
							getActivity().getApplicationContext(),
							BillCheckoutFragment.this, SEARCH_PRODUCTS_TASKCODE ,shoppingCart.getShoppingCartId());
					searchProductSkuTask.execute(keyword);
				} else {
					if (productSearchAdapter != null)
						productSearchAdapter.clear();

				}
			}
		};

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

		}

		@Override
		public void afterTextChanged(Editable arg0) {

		}
	};
	TextWatcher productBarcodeSearchTextWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {

			barcodeProductSearchKeyStrokeTimer.cancel();
			if (s.length() == 0) {
				if (productSearchAdapter != null)
					productSearchAdapter.clear();
			} else {
				barcodeProductSearchKeyStrokeTimer.start();
			}
		}

		CountDownTimer barcodeProductSearchKeyStrokeTimer = new CountDownTimer(
				SnapBillingConstants.KEY_STROKE_TIMEOUT,
				SnapBillingConstants.KEY_STROKE_TIMEOUT) {

			@Override
			public void onTick(long arg0) {
			}

			@Override
			public void onFinish() {
				String keyword = barcode_product_name_search_edittext.getText()
						.toString();
				if (searchProductSkuTask != null)
					searchProductSkuTask.cancel(true);
				if (keyword.length() > 0) {
					if (keyword.contains("'")) {
						keyword = keyword.replace("'", "");
						barcode_product_name_search_edittext.setText("");

					}
					searchProductSkuTask = new SearchProductSkuTask(
							getActivity().getApplicationContext(),
							BillCheckoutFragment.this,
							SEARCH_BARCODE_PRODUCTS_TASKCODE,shoppingCart.getShoppingCartId());
					searchProductSkuTask.execute(keyword);
				} else {
					if (barcodeProductSearchAdapter != null)
						barcodeProductSearchAdapter.clear();

				}
			}
		};

		CountDownTimer keyStrokeTimer = new CountDownTimer(300, 300) {
			@Override
			public void onTick(long arg0) {
			}

			@Override
			public void onFinish() {
			}
		};

		CountDownTimer countDownTimer = new CountDownTimer(300, 300) {
			@Override
			public void onTick(long arg0) {
			}

			@Override
			public void onFinish() {
			}
		};

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {

		}

		@Override
		public void afterTextChanged(Editable arg0) {

		}
	};
	
	private void setFooterColor() {
		if (shoppingCart != null) {
			final int[] footerColors = { R.color.bill1_footer_color, R.color.bill2_footer_color,
										 R.color.bill3_footer_color, R.color.bill4_footer_color };
			getView().findViewById(R.id.bill_footer_layout).setBackgroundResource(footerColors[shoppingCart.getShoppingCartId() % 4]);
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		ActionBar actionBar = getActivity().getActionBar();
		if (!actionBar.isShowing())
			actionBar.show();
		setHasOptionsMenu(true);
		actionBar.setCustomView(
				getActivity().getLayoutInflater().inflate(
						R.layout.search_actionbar_layout, null),
				new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
						ActionBar.LayoutParams.WRAP_CONTENT,
						Gravity.CENTER_VERTICAL));
		snapbizzDB = SnapbizzDB.getInstance(getActivity(), false);
		searchLayout = (RelativeLayout) getActivity().findViewById(
				R.id.search_linear_layout);
		View v = (RelativeLayout) searchLayout.findViewById(R.id.search_layout);
		productSearchEditText = (AutoCompleteTextView) v
				.findViewById(R.id.product_name_search_edittext);
		clearProductSearch = (ImageButton) v
				.findViewById(R.id.clear_name_product_button);
		clearBarcodeText = (ImageButton) v
				.findViewById(R.id.clear_barcode_product_button);
		
		((RelativeLayout) billPriceChageLayout
				.findViewById(R.id.rate_textview_TextLayout))
				.setOnClickListener(cartSPChangeListener);
		
		productSearchEditText.addTextChangedListener(productSearchTextWatcher);
		productSearchEditText.setThreshold(1);
		productSearchEditText.setAdapter(productSearchAdapter);
		productSearchEditText.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					getFragmentManager().popBackStack();
				}
				return true;
			}

		});

		productSearchEditText.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				productSearchEditText.setEnabled(true);
				productSearchEditText.setFocusable(true);

				if (barcodeKeypadFragment != null
						&& barcodeKeypadFragment.isAdded()) {
					FragmentTransaction ft = getFragmentManager()
							.beginTransaction();
					ft.hide(barcodeKeypadFragment);
					overLay.setVisibility(View.GONE);
				}
				if (keypadFragment != null && keypadFragment.isAdded()) {
					getFragmentManager().popBackStack();

				}
				return false;
			}
		});
		

		productSearchEditText.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				getActivity().getCurrentFocus().clearFocus();
				showSoftKeyboard(getActivity(), view);
				searchedProductClickListener
						.onSearchedProductClick(productSearchAdapter
								.getItem(position));

			}
		});
		barcode_product_name_search_edittext = (AutoCompleteTextView) v
				.findViewById(R.id.barcode_product_name_search_edittext);
		barcode_product_name_search_edittext.setFocusable(false);
		barcode_product_name_search_edittext
				.addTextChangedListener(productBarcodeSearchTextWatcher);
		barcode_product_name_search_edittext.setThreshold(1);
		barcode_product_name_search_edittext
				.setAdapter(barcodeProductSearchAdapter);
		barcode_product_name_search_edittext
				.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						searchedProductClickListener
								.onSearchedProductClick(barcodeProductSearchAdapter
										.getItem(position));

					}
				});
		barcode_product_name_search_edittext
				.setOnTouchListener(new View.OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if (deleteReturnLayout != null) {
							deleteReturnLayout.setVisibility(View.GONE);
							unitTypeListView.setVisibility(View.GONE);

						}
						SnapCommonUtils.hideSoftKeyboard(getActivity(), barcode_product_name_search_edittext.getWindowToken());

						if (keypadFragment != null && keypadFragment.isAdded()) {
							getFragmentManager().popBackStack();

						}
						FragmentTransaction ft = getFragmentManager()
								.beginTransaction();

						if (barcodeKeypadFragment == null) {
							barcodeKeypadFragment = new NumKeypadFragment();
							barcodeKeypadFragment
									.setKeypadEnterListener(BillCheckoutFragment.this);
							ft.replace(R.id.weight_framelayout,
									barcodeKeypadFragment);

							ft.addToBackStack(null);

							barcodeKeypadFragment
									.setContext(BARCODE_SEARCH_CONTEXT);
							barcodeKeypadFragment.setValue("0");
							ft.commit();
							overLay.setVisibility(View.VISIBLE);
						}

						else {
							ft.show(barcodeKeypadFragment);
							RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(
									RelativeLayout.LayoutParams.WRAP_CONTENT,
									RelativeLayout.LayoutParams.WRAP_CONTENT);
							rlParams.addRule(RelativeLayout.ALIGN_LEFT,
									barcode_product_name_search_edittext
											.getId());
							rlParams.addRule(RelativeLayout.BELOW,
									barcode_product_name_search_edittext
											.getId());
							rlParams.setMargins(
									getResources().getDimensionPixelOffset(
											R.dimen.billlist_width) + 70, 80,
									0, 0);
							overLay.setLayoutParams(rlParams);
							overLay.setVisibility(View.VISIBLE);

						}

						return true;

					}
				});

		clearProductSearch.setOnClickListener(onSearchClearListener);
		clearBarcodeText.setOnClickListener(onSearchClearListener);
		customerPaymentCheck.setOnClickListener(onCustomerPaymentListener);
		if (shoppingCart != null && shoppingCart.getCartItems() != null) {
			if (billListAdapter == null)
				billListAdapter = new BillListAdapter(getActivity()
						.getApplicationContext(), shoppingCart, this,
						R.layout.listitem_bill);
			((ListView) getView().findViewById(R.id.bill_listview))
					.setAdapter(billListAdapter);
			updateBillTextViews(getActivity());
			
			if (shoppingCart.getCustomer() != null) {
				customerSearchLayoutContainer.setVisibility(View.GONE);
				customerInformationLayout.setVisibility(View.VISIBLE);
				customerNumberTextView.setText(String.valueOf(shoppingCart.getCustomer().getPhone()));
				customerNameTextView.setText(shoppingCart.getCustomer().getName());
				if (shoppingCart.getShoppingCartId() == SnapBillingConstants.LAST_SHOPPING_CART) {
					customerDueTextView.setText("");
				} else {
					customerDueTextView.setText(getActivity()
							.getApplicationContext().getString(R.string.display_due)
						+ SnapBillingTextFormatter.formatPriceText(snapbizzDB.getCustomerDuePhoneNo(shoppingCart.getCustomer()
								.getPhone()), getActivity()));
				}
				displayCustomerPaymentCheckBox(true);
				customerAddressTextView.setText(shoppingCart.getCustomer().getAddress());
				customerCreditLimitTextView.setText(SnapBillingTextFormatter
						.formatPriceText(shoppingCart.getCustomer().getCreditLimit(), getActivity()));
				customerDueAmountTextView.setText(SnapBillingTextFormatter.formatPriceText(snapbizzDB.getCustomerDuePhoneNo(shoppingCart.getCustomer()
								.getPhone()), getActivity()));
				customerMembershipDateTextView.setText(SnapBillingTextFormatter
						.formatCustomerMembershipDate(shoppingCart
								.getCustomer().getCreatedAt()));
			}
			
			if (shoppingCart.getDistributor() != null) {
				customerSearchLayoutContainer.setVisibility(View.GONE);
				customerInformationLayout.setVisibility(View.VISIBLE);
				customerNumberTextView.setText(shoppingCart.getDistributor()
						.getPhoneNumber());
				customerNameTextView.setText(shoppingCart.getDistributor()
						.getAgencyName());
				customerAddressTextView.setText(shoppingCart.getDistributor()
						.getTinNumber());
				displayCustomerPaymentCheckBox(false);
			}
		}
		setFooterColor();

		if (offersAdapter != null)
			offersGridView.setAdapter(offersAdapter);

		if (mode == 0) {
			searchLayout.setVisibility(View.GONE);
			((TextView) getActivity().findViewById(R.id.actionbar_header))
					.setVisibility(View.VISIBLE);
			getView().findViewById(R.id.quickadd_button).setEnabled(false);
			((TextView) getActivity().findViewById(R.id.actionbar_header))
					.setText("Quick Add");
			quickAddRelativeLayout.setVisibility(View.VISIBLE);
			suggestedProductsGridView.setVisibility(View.GONE);
			getView().findViewById(R.id.weight_framelayout).setVisibility(
					View.GONE);
			getView().findViewById(R.id.customer_suggestions_imageview)
					.setVisibility(View.GONE);
			checkoutLayout.setVisibility(View.GONE);
			searchLayout.setVisibility(View.GONE);
			if (!barcode_product_name_search_edittext.getText().toString()
					.trim().isEmpty())
				barcode_product_name_search_edittext.setText("");
			if (!productSearchEditText.getText().toString().trim().isEmpty())
				productSearchEditText.setText("");
			if (quickaddCategoryAdapter == null || quickAddCategories == null) {
				new GetQuickAddSubCategoriesTask(getActivity()
						.getApplicationContext(), this,
						GET_QUICKADD_CATEGORY_TASKCODE).execute();
			} else {
				quickaddCategoryAdapter = new QuickAddCategoryAdapter(
						getActivity().getApplicationContext(),
						quickAddCategories);
				if (quickAddGridView != null)
					quickAddGridView.setAdapter(quickaddCategoryAdapter);
			}
		} else if (mode == 1) {
			searchLayout.setVisibility(View.GONE);
			((TextView) getActivity().findViewById(R.id.actionbar_header))
					.setVisibility(View.VISIBLE);
			getView().findViewById(R.id.offers_button).setEnabled(false);
			((TextView) getActivity().findViewById(R.id.actionbar_header))
					.setText(getActivity()
							.getApplicationContext().getString(R.string.offers));
			offersGridView.setVisibility(View.VISIBLE);
			suggestedProductsGridView.setVisibility(View.GONE);
			checkoutLayout.setVisibility(View.GONE);
			searchLayout.setVisibility(View.GONE);
			if (!barcode_product_name_search_edittext.getText().toString()
					.trim().isEmpty())
				barcode_product_name_search_edittext.setText("");
			if (!productSearchEditText.getText().toString().trim().isEmpty())
				productSearchEditText.setText("");
			getView().findViewById(R.id.weight_framelayout).setVisibility(
					View.GONE);
			getView().findViewById(R.id.customer_suggestions_imageview)
					.setVisibility(View.GONE);
		} else if (mode == 2) {
			((TextView) getActivity().findViewById(R.id.actionbar_header))
					.setVisibility(View.VISIBLE);
			((TextView) getActivity().findViewById(R.id.actionbar_header))
			.setText(R.string.checkout);
			if (shoppingCart.getShoppingCartId() == SnapBillingConstants.LAST_SHOPPING_CART)
			 ((TextView) getActivity().findViewById(R.id.actionbar_header))
				.setText(R.string.receive);
			
			checkoutLayout.setVisibility(View.VISIBLE);
			searchLayout.setVisibility(View.GONE);
			if (!barcode_product_name_search_edittext.getText().toString()
					.trim().isEmpty())
				barcode_product_name_search_edittext.setText("");
			if (!productSearchEditText.getText().toString().trim().isEmpty())
				productSearchEditText.setText("");
			getView().findViewById(R.id.weight_framelayout).setVisibility(
					View.GONE);
			if (shoppingCart.getCustomer() != null) {
				suggestedProductsGridView.setVisibility(View.VISIBLE);
				new GetCustomerSuggestedProductsTask(getActivity()
						.getApplicationContext(), this,
						GET_CUSTOMERSUGGESTION_TASKCODE).execute(0);
				getView().findViewById(R.id.customer_suggestions_imageview)
						.setVisibility(View.GONE);
			} else {
				suggestedProductsGridView.setVisibility(View.GONE);
				getView().findViewById(R.id.customer_suggestions_imageview)
						.setVisibility(View.VISIBLE);
				displayCustomerPaymentCheckBox(false);
			}
		} else {
			((TextView) getActivity().findViewById(R.id.actionbar_header))
					.setVisibility(View.GONE);
			checkoutLayout.setVisibility(View.GONE);
			getView().findViewById(R.id.search_button).setEnabled(false);
			searchLayout.setVisibility(View.VISIBLE);
			productSearchEditText.setVisibility(View.VISIBLE);
			productSearchEditText.requestFocus();
			clearProductSearch.setVisibility(View.VISIBLE);
			suggestedProductsGridView.setVisibility(View.GONE);
			getView().findViewById(R.id.customer_suggestions_imageview)
					.setVisibility(View.GONE);
			showSoftKeyboard(getActivity(), productSearchEditText);
			getView().findViewById(R.id.weight_framelayout).setVisibility(
					View.GONE);
		}

		if (customerAdapter != null)
			((ListView) getView().findViewById(
					R.id.customer_search_result_listview))
					.setAdapter(customerAdapter);

		((ListView) getView()
				.findViewById(R.id.customer_search_result_listview))
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int pos, long arg3) {
						if (shoppingCart.getShoppingCartId() == SnapBillingConstants.LAST_SHOPPING_CART) {
							shoppingCart.setDistributor(distributorAdapter.getItem(pos));
							customerSearchLayoutContainer.setVisibility(View.GONE);
							customerSearchEditText.setText("");
							SnapCommonUtils.hideSoftKeyboard(getActivity(), customerSearchLayoutContainer.getWindowToken());
							customerInformationLayout.setVisibility(View.VISIBLE);
							customerNumberTextView.setText(shoppingCart
									.getDistributor().getPhoneNumber());
							customerNameTextView.setText(shoppingCart.getDistributor()
									.getAgencyName());
							displayCustomerPaymentCheckBox(false);
							customerAddressTextView.setText(shoppingCart
									.getDistributor().getTinNumber());
						}else{
						
						shoppingCart.setCustomer(customerAdapter.getItem(pos));
						customerSearchLayoutContainer.setVisibility(View.GONE);
						SnapCommonUtils.hideSoftKeyboard(getActivity(), customerSearchLayoutContainer.getWindowToken());
						customerInformationLayout.setVisibility(View.VISIBLE);
						customerNumberTextView.setText(String.valueOf(shoppingCart
								.getCustomer().getPhone()));
						customerNameTextView.setText(shoppingCart.getCustomer()
								.getName());
						customerDueTextView.setText(getActivity().getApplicationContext()
								.getString(R.string.display_due)
								+ SnapBillingTextFormatter.formatPriceText(
										snapbizzDB.getCustomerDuePhoneNo(shoppingCart.getCustomer()
												.getPhone()), getActivity()));
						customerAddressTextView.setText(shoppingCart
								.getCustomer().getAddress());
						customerCreditLimitTextView
								.setText(SnapBillingTextFormatter
										.formatPriceText(shoppingCart.getCustomer()
												.getCreditLimit() != null ? shoppingCart.getCustomer()
														.getCreditLimit() / 100.0f : 0, getActivity()));
						customerDueAmountTextView
								.setText(SnapBillingTextFormatter
										.formatPriceText(snapbizzDB.getCustomerDuePhoneNo(shoppingCart
												.getCustomer().getPhone()), getActivity()));
						customerMembershipDateTextView.setText(SnapBillingTextFormatter
								.formatCustomerMembershipDate(shoppingCart
										.getCustomer().getCreatedAt()));
						customerSearchEditText.setText("");
						displayCustomerPaymentCheckBox(true);
						if (mode == 2)
							suggestedProductsGridView
									.setVisibility(View.VISIBLE);
						getView().findViewById(
								R.id.customer_suggestions_imageview)
								.setVisibility(View.GONE);
						new GetCustomerSuggestedProductsTask(getActivity()
								.getApplicationContext(),
								BillCheckoutFragment.this,
								GET_CUSTOMERSUGGESTION_TASKCODE)
								.execute(0);
					}
					}
				});
		if (shoppingCart != null && shoppingCart.getShoppingCartId() != SnapBillingConstants.LAST_SHOPPING_CART)
			shoppingCart.setVATCalculated(SnapSharedUtils.getIsVatSelected(SnapCommonUtils
                            							 .getSnapContext(getActivity())));

		customerSearchLayoutContainer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				customerSearchLayoutContainer.setVisibility(View.GONE);
			}
		});

		if (shoppingCart != null && shoppingCart.getCustomer() != null) {
			customerInformationLayout.setVisibility(View.VISIBLE);
			customerNumberTextView.setText(String.valueOf(shoppingCart.getCustomer().getPhone()));
			customerNameTextView.setText(shoppingCart.getCustomer().getName());
			customerDueTextView.setText(getActivity()
						.getApplicationContext().getString(R.string.display_due)
					+ SnapBillingTextFormatter.formatPriceText(snapbizzDB.getCustomerDuePhoneNo(
							shoppingCart.getCustomer().getPhone()), getActivity()));
			customerAddressTextView.setText(shoppingCart.getCustomer().getAddress());
			customerCreditLimitTextView.setText(SnapBillingTextFormatter
					.formatPriceText(shoppingCart.getCustomer()
					.getCreditLimit() != null ? shoppingCart.getCustomer()
					.getCreditLimit() / 100.0f : 0, getActivity()));
			customerDueAmountTextView.setText(SnapBillingTextFormatter
					.formatPriceText(snapbizzDB.getCustomerDuePhoneNo(shoppingCart
											   .getCustomer().getPhone()), getActivity()));
			customerMembershipDateTextView.setText(SnapBillingTextFormatter
					.formatCustomerMembershipDate(shoppingCart.getCustomer().getCreatedAt()));
			displayCustomerPaymentCheckBox(true);
		}

		if (suggestedProductsAdapter != null) {
			suggestedProductsGridView.setAdapter(suggestedProductsAdapter);
		}

		quickAddGridView
				.setOnItemClickListener(onQuickAddCategoryClickListener);

		if (quickaddCategoryAdapter != null)
			quickAddGridView.setAdapter(quickaddCategoryAdapter);
		if (quickaddProductAdapter == null) {
			quickaddProductAdapter = new QuickAddProductAdapter(getActivity()
					.getApplicationContext(), new ArrayList<ProductSku>(),
					onCreateEditQuickAddClickListener);
		}
		quickaddProductGridView.setAdapter(quickaddProductAdapter);
		if (unitTypeAdapter == null) {
			unitTypeAdapter = new UnitTypeAdapter(getActivity()
					.getApplicationContext(), new ArrayList<String>());
		}

		quickAddRelativeLayout.findViewById(R.id.quickadd_weight1kg_button)
				.setOnClickListener(onWeightKgClickListener);
		quickAddRelativeLayout.findViewById(R.id.quickadd_weight2kg_button)
				.setOnClickListener(onWeightKgClickListener);
		quickAddRelativeLayout.findViewById(R.id.quickadd_weight3kg_button)
				.setOnClickListener(onWeightKgClickListener);
		quickAddRelativeLayout.findViewById(R.id.quickadd_weight4kg_button)
				.setOnClickListener(onWeightKgClickListener);
		quickAddRelativeLayout.findViewById(R.id.quickadd_weight5kg_button)
				.setOnClickListener(onWeightKgClickListener);
		quickAddRelativeLayout.findViewById(R.id.quickadd_weight10kg_button)
				.setOnClickListener(onWeightKgClickListener);
		quickAddRelativeLayout.findViewById(R.id.quickadd_weight15kg_button)
				.setOnClickListener(onWeightKgClickListener);
		quickAddRelativeLayout.findViewById(R.id.quickadd_weight20kg_button)
				.setOnClickListener(onWeightKgClickListener);
		quickAddRelativeLayout.findViewById(R.id.quickadd_weight25kg_button)
				.setOnClickListener(onWeightKgClickListener);
		quickAddRelativeLayout.findViewById(R.id.quickadd_weight50kg_button)
				.setOnClickListener(onWeightKgClickListener);
		quickAddRelativeLayout.findViewById(R.id.quickadd_weight10g_button)
				.setOnClickListener(onWeightGmClickListener);
		quickAddRelativeLayout.findViewById(R.id.quickadd_weight20g_button)
				.setOnClickListener(onWeightGmClickListener);
		quickAddRelativeLayout.findViewById(R.id.quickadd_weight25g_button)
				.setOnClickListener(onWeightGmClickListener);
		quickAddRelativeLayout.findViewById(R.id.quickadd_weight50g_button)
				.setOnClickListener(onWeightGmClickListener);
		quickAddRelativeLayout.findViewById(R.id.quickadd_weight100g_button)
				.setOnClickListener(onWeightGmClickListener);
		quickAddRelativeLayout.findViewById(R.id.quickadd_weight200g_button)
				.setOnClickListener(onWeightGmClickListener);
		quickAddRelativeLayout.findViewById(R.id.quickadd_weight250g_button)
				.setOnClickListener(onWeightGmClickListener);
		quickAddRelativeLayout.findViewById(R.id.quickadd_weight500g_button)
				.setOnClickListener(onWeightGmClickListener);
		quickAddRelativeLayout.findViewById(R.id.quickadd_weight750g_button)
				.setOnClickListener(onWeightGmClickListener);

		quickAddRelativeLayout.findViewById(R.id.quickadd_weight1lt_button)
				.setOnClickListener(onWeightLtClickListener);
		quickAddRelativeLayout.findViewById(R.id.quickadd_weight2lt_button)
				.setOnClickListener(onWeightLtClickListener);
		quickAddRelativeLayout.findViewById(R.id.quickadd_weight3lt_button)
				.setOnClickListener(onWeightLtClickListener);
		quickAddRelativeLayout.findViewById(R.id.quickadd_weight4lt_button)
				.setOnClickListener(onWeightLtClickListener);
		quickAddRelativeLayout.findViewById(R.id.quickadd_weight5lt_button)
				.setOnClickListener(onWeightLtClickListener);
		quickAddRelativeLayout.findViewById(R.id.quickadd_weight10lt_button)
				.setOnClickListener(onWeightLtClickListener);
		quickAddRelativeLayout.findViewById(R.id.quickadd_weight15lt_button)
				.setOnClickListener(onWeightLtClickListener);
		quickAddRelativeLayout.findViewById(R.id.quickadd_weight20lt_button)
				.setOnClickListener(onWeightLtClickListener);
		quickAddRelativeLayout.findViewById(R.id.quickadd_weight25lt_button)
				.setOnClickListener(onWeightLtClickListener);
		quickAddRelativeLayout.findViewById(R.id.quickadd_weight50lt_button)
				.setOnClickListener(onWeightLtClickListener);
		quickAddRelativeLayout.findViewById(R.id.quickadd_weight10ml_button)
				.setOnClickListener(onWeightMlClickListener);
		quickAddRelativeLayout.findViewById(R.id.quickadd_weight20ml_button)
				.setOnClickListener(onWeightMlClickListener);
		quickAddRelativeLayout.findViewById(R.id.quickadd_weight25ml_button)
				.setOnClickListener(onWeightMlClickListener);
		quickAddRelativeLayout.findViewById(R.id.quickadd_weight50ml_button)
				.setOnClickListener(onWeightMlClickListener);
		quickAddRelativeLayout.findViewById(R.id.quickadd_weight100ml_button)
				.setOnClickListener(onWeightMlClickListener);
		quickAddRelativeLayout.findViewById(R.id.quickadd_weight200ml_button)
				.setOnClickListener(onWeightMlClickListener);
		quickAddRelativeLayout.findViewById(R.id.quickadd_weight250ml_button)
				.setOnClickListener(onWeightMlClickListener);
		quickAddRelativeLayout.findViewById(R.id.quickadd_weight500ml_button)
				.setOnClickListener(onWeightMlClickListener);
		quickAddRelativeLayout.findViewById(R.id.quickadd_weight750ml_button)
				.setOnClickListener(onWeightMlClickListener);

		suggestedProductsGridView.setRecyclerListener(new RecyclerListener() {

			@Override
			public void onMovedToScrapHeap(View view) {

				((ImageView) view.findViewById(R.id.product_imageview))
						.setImageDrawable(null);
			}
		});

		offersGridView.setRecyclerListener(new RecyclerListener() {

			@Override
			public void onMovedToScrapHeap(View view) {

				((ImageView) view.findViewById(R.id.product_imageview))
						.setImageDrawable(null);
			}
		});

		quickAddGridView.setRecyclerListener(new RecyclerListener() {

			@Override
			public void onMovedToScrapHeap(View view) {

				((ImageView) view.findViewById(R.id.product_imageview))
						.setImageDrawable(null);
			}
		});

		if (unitTypeAdapter == null) {
			unitTypeAdapter = new UnitTypeAdapter(getActivity()
					.getApplicationContext(), new ArrayList<String>());
		}
		unitTypeListView.setAdapter(unitTypeAdapter);
		unitTypeListView.setOnItemClickListener(onUnitTypeClickListener);
		printerType = SnapSharedUtils.getSavedPrinterType(getActivity()
				.getApplicationContext());

		overLay = getView().findViewById(R.id.overlay_framelayout);
		RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		rlParams.addRule(RelativeLayout.ALIGN_LEFT,
				barcode_product_name_search_edittext.getId());
		rlParams.addRule(RelativeLayout.BELOW,
				barcode_product_name_search_edittext.getId());
		rlParams.setMargins(
				getResources().getDimensionPixelOffset(R.dimen.billlist_width) + 70,
				80, 0, 0);
		overLay.setLayoutParams(rlParams);

		if (SnapToolkitConstants.IS_MENU_CLK.equalsIgnoreCase("1")) {
			showSearchProducts(getActivity());
		}

	}

	OnClickListener onSearchClearListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(getActivity(), v, TAG);
			if (v.getId() == R.id.clear_customer_button) {
				customerSearchEditText.setText("");
				displayCustomerPaymentCheckBox(false);
			} else if (v.getId() == R.id.clear_name_product_button) {
				productSearchEditText.setText("");
				if (productSearchAdapter != null)
					productSearchAdapter.clear();
			} else if (v.getId() == R.id.clear_barcode_product_button) {
				barcode_product_name_search_edittext.setText("");
				if (barcodeProductSearchAdapter != null)
					barcodeProductSearchAdapter.clear();
			}
		}
	};

	OnClickListener customerAddEditListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.save_button || v.getId() == R.id.edit_button) {
				String customerName = ((SoftInputEditText) getActivity()
						.findViewById(R.id.customer_name_edittext)).getText()
						.toString();
				String customerNumber = ((SoftInputEditText) getActivity()
						.findViewById(R.id.customer_number_edittext)).getText()
						.toString();
				String customerAddress = ((SoftInputEditText) getActivity()
						.findViewById(R.id.customer_address_edittext))
						.getText().toString();
				boolean isEdit = v.getId() == R.id.edit_button ? true : false;
				if (shoppingCart.getShoppingCartId() == SnapBillingConstants.LAST_SHOPPING_CART && customerNumber.length() == 0) {
					CustomToast.showCustomToast(getActivity()
							.getApplicationContext(),
							getString(R.string.msg_validnumber), Toast.LENGTH_SHORT,
							CustomToast.WARNING);
				} else if (!ValidationUtils.validateMobileNo(customerNumber)) {
					CustomToast.showCustomToast(getActivity()
							.getApplicationContext(),
							getString(R.string.msg_validnumber), Toast.LENGTH_SHORT,
							CustomToast.WARNING);
				} else if (customerName.length() != 0
						&& !ValidationUtils.validateName(customerName)) {
					CustomToast.showCustomToast(getActivity()
							.getApplicationContext(),
							getString(R.string.msg_validname), Toast.LENGTH_SHORT,
							CustomToast.WARNING);
				} else {
					int creditLimit = 0;
					if (((SoftInputEditText) getActivity().findViewById(
							R.id.customer_creditlimit_edittext)).length() > 0) {
						try {
							creditLimit = (int)(Float
									.parseFloat(((SoftInputEditText) getActivity()
											.findViewById(
													R.id.customer_creditlimit_edittext))
											.getText().toString()) * 100.0f);
						} catch (Exception e) {
							CustomToast.showCustomToast(getActivity(),
							        getString(R.string.msg_validcredit),
									Toast.LENGTH_SHORT, CustomToast.WARNING);
							return;
						}
					}
					Customers newCustomer = null;
					Distributor distributor = null;
					if (shoppingCart.getShoppingCartId() == SnapBillingConstants.LAST_SHOPPING_CART) {
						
						if (!isEdit) {
							GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(getActivity(), v, TAG +
									" " + shoppingCart.getShoppingCartId());
							distributor = new Distributor();
							distributor.setAgencyName(customerName);
							distributor.setPhoneNumber(customerNumber);
							distributor.setTinNumber(customerAddress);
						} else {
							GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(getActivity(), v, TAG +
									" " + shoppingCart.getShoppingCartId());
							distributor = (Distributor) v.getTag();
							distributor.setAgencyName(customerName);
							distributor.setPhoneNumber(customerNumber);
							distributor.setTinNumber(customerAddress);
							
						}
						new AddEditDistributorTask(getActivity()
								.getApplicationContext(),
								BillCheckoutFragment.this,
								ADD_DISTRIBUTORINFO_TASKCODE, isEdit)
								.execute(distributor);
						SnapCommonUtils.hideSoftKeyboard(getActivity(), customerSearchLayoutContainer.getWindowToken());
						customerSearchEditText.setText("");
						
						
					} else {
						if (!isEdit) {
							GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(getActivity(), v, TAG +
									" " + shoppingCart.getShoppingCartId());
							if (snapbizzDB.getCustomerByPhoneNo(Long.parseLong(customerNumber)).size() > 0) {
			                    CustomToast.showCustomToast(getActivity(), getString(R.string.exc_customer_exists), Toast.LENGTH_SHORT, CustomToast.WARNING);
			                } else {
								snapbizzDB.insertCustomer(new Customers(Long.parseLong(customerNumber),
										customerName, customerAddress, null, 0, false, new Date(), new Date()));
								snapbizzDB.insertCustomerDetails(new CustomerDetails(Long.parseLong(customerNumber),
			                			0, 0, 0, 0, 0, 0, 0f, null, null, new Date(), new Date()));
								addEditCustomerSuccess(new Customers(Long.parseLong(customerNumber),
										customerName, customerAddress, null, 0, false, new Date(), new Date()));
			                }
						} else {
							GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(getActivity(), v, TAG +
									" " + shoppingCart.getShoppingCartId());
							newCustomer = (Customers) v.getTag();
							newCustomer.setName(customerName);
							newCustomer.setPhone(Long.parseLong(customerNumber));
							newCustomer.setAddress(customerAddress);
							newCustomer.setCreditLimit(creditLimit);
							newCustomer.setUpdatedAt(new Date());
							snapbizzDB.updateCustomerByPhoneNo(Long.parseLong(customerNumber),
									customerName, customerAddress, null, creditLimit);
							addEditCustomerSuccess(newCustomer);
						}
					}
					SnapCommonUtils.hideSoftKeyboard(getActivity(), customerSearchLayoutContainer.getWindowToken());
					customerSearchEditText.setText("");
				}

			} else {
				SnapCommonUtils.hideSoftKeyboard(getActivity(), customerSearchLayoutContainer.getWindowToken());
				hideAddCustomerLayout();
			}
		}

		private void addEditCustomerSuccess(Customers newCustomer) {
			hideAddCustomerLayout();
			if (shoppingCart != null) {
				shoppingCart.setCustomer(newCustomer);
				customerSearchLayoutContainer.setVisibility(View.GONE);
				customerInformationLayout.setVisibility(View.VISIBLE);
				customerNumberTextView.setText(String.valueOf(shoppingCart.getCustomer().getPhone()));
				customerNameTextView.setText(shoppingCart.getCustomer().getName());
				customerDueTextView.setText(getString(R.string.display_due)
						+ SnapBillingTextFormatter.formatPriceText(snapbizzDB.getCustomerDuePhoneNo
								(shoppingCart.getCustomer().getPhone()), getActivity()));
				customerAddressTextView.setText(shoppingCart.getCustomer()
						.getAddress());
				customerCreditLimitTextView.setText(SnapBillingTextFormatter
						.formatPriceText(shoppingCart.getCustomer()
								.getCreditLimit(), getActivity()));
				customerDueAmountTextView.setText(SnapBillingTextFormatter
						.formatPriceText(snapbizzDB.getCustomerDuePhoneNo
								(shoppingCart.getCustomer().getPhone()), getActivity()));
				customerMembershipDateTextView.setText(SnapBillingTextFormatter
						.formatCustomerMembershipDate(shoppingCart
								.getCustomer().getCreatedAt()));
				displayCustomerPaymentCheckBox(true);
				new GetCustomerSuggestedProductsTask(getActivity()
						.getApplicationContext(), BillCheckoutFragment.this,
						GET_CUSTOMERSUGGESTION_TASKCODE).execute(0);
			}
		}
	};

	View.OnClickListener onQuickAddClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(getActivity(), v, TAG);
			if (!v.isEnabled())
				return;
			if (billListAdapter != null)
				billListAdapter.setLastSelectedPosition(-1);
			if (lastClickedQuickAddView != null) {
				lastClickedQuickAddView.setEnabled(true);
				lastClickedQuickAddView.setSelected(false);
			}
			v.setEnabled(false);
			v.setSelected(true);
			lastClickedQuickAddView = v;
			currentQuickAddProductCode = v.getTag().toString();
			currentQuickAddProductName = ((TextView) ((RelativeLayout) v)
					.getChildAt(1)).getText().toString();
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			if (keypadFragment == null) {
				keypadFragment = new ProductKeypadFragment();
				keypadFragment
						.setKeypadEnterListener(BillCheckoutFragment.this);
			}

			ft.replace(R.id.overlay_framelayout, keypadFragment);
			if (!keypadFragment.isAdded()) {
				ft.addToBackStack(null);
			}
			ft.commit();
			keypadFragment.setContext(QUICKADD_CONTEXT);
			keypadFragment.setQty(1);
			keypadFragment.setArrowVisibility(View.VISIBLE);
			keypadFragment.setPrice(0);
			keypadFragment.setRate(0);
			keypadFragment.setTotalPrice(0);
			keypadFragment.setEnableDiscount(false);
			keypadFragment.setKeyPadMode(KeyPadMode.PRICE);
			keypadFragment.setUnitType(null, getActivity()
					.getApplicationContext());

			RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			rlParams.setMargins(
					getResources().getDimensionPixelOffset(
							R.dimen.billlist_width)
							+ 17
							+ (v.getId() == R.id.quickadd_kitchen_relativelayout
									|| v.getId() == R.id.quickadd_bathroom_relativelayout ? getResources()
									.getDimensionPixelOffset(
											R.dimen.billlist_width) + 17 - 60
									: v.getId() == R.id.quickadd_others_relativelayout ? SnapBillingConstants.BILLING_LIST_LEFT_MARGIN - 420
											: -60),
					getResources().getDimensionPixelOffset(
							R.dimen.store_keypad_margin) + 5, 0, 0);
			overLay.setLayoutParams(rlParams);
			keypadFragment
					.setArrowPosition(v.getId() == R.id.quickadd_food_relativelayout
							|| v.getId() == R.id.quickadd_kitchen_relativelayout ? ProductKeypadFragment.POSITION_LEFT
							: ProductKeypadFragment.POSITION_RIGHT);
			overLay.setVisibility(View.VISIBLE);
			deleteReturnLayout.setVisibility(View.GONE);
		}
	};

	TwoWayAdapterView.OnItemClickListener onOfferClickListener = new TwoWayAdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(TwoWayAdapterView<?> parent, View view,
				int position, long id) {
			searchedProductClickListener.onSearchedProductClick(offersAdapter
					.getItem(position));
		}

	};

	TwoWayAdapterView.OnItemClickListener onSuggestedProductsClickListener = new TwoWayAdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(TwoWayAdapterView<?> parent, View view,
				int position, long id) {
			searchedProductClickListener.onSearchedProductClick(new ProductSku(
					suggestedProductsAdapter.getItem(position)));
		}

	};

	View.OnClickListener onCashDiscountClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			if (numKeypadFragment == null) {
				numKeypadFragment = new NumKeypadFragment();
				numKeypadFragment
						.setKeypadEnterListener(BillCheckoutFragment.this);
			}
			ft.replace(R.id.overlay_framelayout, numKeypadFragment);
			if (!numKeypadFragment.isAdded()) {
				ft.addToBackStack(null);
			}
			ft.commit();
			boolean isDiscount = v.getId() == R.id.discount_value_button ? true
					: false;
			numKeypadFragment
					.setContext(isDiscount ? EDIT_TOTALDISCOUNT_CONTEXT
							: ENTER_CASH_CONTEXT);
			numKeypadFragment.setIsDiscount(isDiscount);
			numKeypadFragment.setNewEdit(true);
			if (isDiscount == true) {
				String sDiscount = "Discount";
				GoogleAnalyticsTracker.sendGoogleAnalyticsOnString(getActivity(), sDiscount, TAG);
				numKeypadFragment.setValue(String.valueOf(shoppingCart
						.getTotalDiscount()));
			} else {
				String sCash = "Cash";
				GoogleAnalyticsTracker.sendGoogleAnalyticsOnString(getActivity(), sCash, TAG);
				numKeypadFragment.setValue(String.valueOf(shoppingCart
						.getCashPaid()));
			}
			numKeypadFragment.setTotalValue(shoppingCart.getTotalCartValue()
					- shoppingCart.getTotalSavings());
			getView().findViewById(R.id.overlay_framelayout).setVisibility(
					View.VISIBLE);
			RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			rlParams.setMargins(
					getResources().getDimensionPixelOffset(
							R.dimen.billlist_width) * 2 + 40, isDiscount ? 50
							: 200, 0, 0);
			getActivity().findViewById(R.id.overlay_framelayout)
					.setLayoutParams(rlParams);
			deleteReturnLayout.setVisibility(View.GONE);
		}
	};

	View.OnClickListener onBillOptionsClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {

			if (v.getId() == R.id.offers_button) {
				GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(getActivity(), v, TAG);
				showOffers(getActivity());
			} else if (v.getId() == R.id.quickadd_button) {
				GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(getActivity(), v, TAG);
				if (null != quickaddKeypadFragment
						&& quickaddKeypadFragment.isVisible()) {
					getFragmentManager().beginTransaction()
							.detach(quickaddKeypadFragment).commit();
				}
				showQuickAdd(getActivity());
			} else if (v.getId() == R.id.bill_footer_layout) {
				showCheckout();
			} else if (v.getId() == R.id.search_button) {
				GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(getActivity(), v, TAG);
				showSearchProducts(getActivity());
			}
		}
	};

	View.OnClickListener onBillCompleteClickListener = new View.OnClickListener() {

		private AlertDialog dialog;

		@Override
		public void onClick(View v) {
			
			SnapToolkitConstants.IS_MENU_CLK = "0";

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

			View view = getActivity().getLayoutInflater().inflate(
					R.layout.dialog_completebill, null);

			GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(getActivity(), v, TAG);
			if (v.getId() == R.id.cancel_bill_button) {
				view.findViewById(R.id.ty_textview).setVisibility(View.GONE);
				view.findViewById(R.id.alert_payment_textview).setVisibility(
						View.GONE);
				builder.setPositiveButton("Yes", onCancelBillClickListener);
				((ImageView) view.findViewById(R.id.popup_icon_imageview))
						.setImageResource(R.drawable.icon_popup_list_delete);
				builder.setCancelable(true).setNegativeButton("No", null);
			} else if (v.getId() == R.id.pay_bill_button) {
				if ((shoppingCart.getTotalCartValue()
						- shoppingCart.getTotalDiscount() - shoppingCart
						.getTotalSavings()) > 100000) {
					view.findViewById(R.id.cancel_bill_textview).setVisibility(
							View.GONE);
					((ImageView) view.findViewById(R.id.popup_icon_imageview))
							.setVisibility(View.GONE);
					((TextView) view
							.findViewById(R.id.lineitems_value_textview))
							.setVisibility(View.GONE);
					((TextView) view.findViewById(R.id.saved_value_textview))
							.setVisibility(View.GONE);
					((TextView) view.findViewById(R.id.total_value_textview))
							.setVisibility(View.GONE);
					((TextView) view
							.findViewById(R.id.total_qty_value_textview))
							.setVisibility(View.GONE);
					((TextView) view.findViewById(R.id.lineitems_textview))
							.setVisibility(View.GONE);
					((TextView) view.findViewById(R.id.total_qty_textview))
							.setVisibility(View.GONE);
					((TextView) view.findViewById(R.id.total_textview))
							.setVisibility(View.GONE);
					((TextView) view.findViewById(R.id.saved_textview))
							.setVisibility(View.GONE);
					((View) view.findViewById(R.id.total_divider))
							.setVisibility(View.GONE);
					((View) view.findViewById(R.id.lineitems_divider))
							.setVisibility(View.GONE);
					((View) view.findViewById(R.id.totalqty_divider))
							.setVisibility(View.GONE);
					((View) view.findViewById(R.id.lineitems_layout_textview))
							.setVisibility(View.GONE);
					view.findViewById(R.id.ty_textview)
							.setVisibility(View.GONE);
					builder.setPositiveButton("Yes",
							onConfirmPaymentClickListener);
					builder.setCancelable(true).setNegativeButton(
							R.string.cancel, null);
				} else {
					if (!SnapBillingValidationUtils.isBillValid(shoppingCart,
							getActivity().getApplicationContext()))
						return;
					((ImageView) view.findViewById(R.id.popup_icon_imageview)).setImageResource(R.drawable.icon_popup_list_pay);
					view.findViewById(R.id.cancel_bill_textview).setVisibility(View.GONE);
					view.findViewById(R.id.alert_payment_textview).setVisibility(View.GONE);
					if (shoppingCart.getCustomer() != null) {
						((TextView) view
								.findViewById(R.id.customername_textview))
								.setText(shoppingCart.getCustomer().getName());
					}
					builder.setPositiveButton(getString(R.string.done), onCompleteBillClickListener);
					builder.setCancelable(true).setNegativeButton(R.string.cancel, null);
				}

			} else if (v.getId() == R.id.delivery_bill_button) {
				if ((shoppingCart.getTotalCartValue()
						- shoppingCart.getTotalDiscount() - shoppingCart
						.getTotalSavings()) > 100000) {
					((ImageView) view.findViewById(R.id.popup_icon_imageview))
							.setVisibility(View.GONE);
					view.findViewById(R.id.cancel_bill_textview).setVisibility(
							View.GONE);
					((TextView) view
							.findViewById(R.id.lineitems_value_textview))
							.setVisibility(View.GONE);
					((TextView) view.findViewById(R.id.saved_value_textview))
							.setVisibility(View.GONE);
					((TextView) view.findViewById(R.id.total_value_textview))
							.setVisibility(View.GONE);
					((TextView) view
							.findViewById(R.id.total_qty_value_textview))
							.setVisibility(View.GONE);
					((TextView) view.findViewById(R.id.lineitems_textview))
							.setVisibility(View.GONE);
					((TextView) view.findViewById(R.id.total_qty_textview))
							.setVisibility(View.GONE);
					((TextView) view.findViewById(R.id.total_textview))
							.setVisibility(View.GONE);
					((TextView) view.findViewById(R.id.saved_textview))
							.setVisibility(View.GONE);
					((View) view.findViewById(R.id.total_divider))
							.setVisibility(View.GONE);
					((View) view.findViewById(R.id.lineitems_divider))
							.setVisibility(View.GONE);
					((View) view.findViewById(R.id.totalqty_divider))
							.setVisibility(View.GONE);
					((View) view.findViewById(R.id.lineitems_layout_textview))
							.setVisibility(View.GONE);
					view.findViewById(R.id.ty_textview)
							.setVisibility(View.GONE);
					builder.setPositiveButton("Yes",
							onConfirmDeliveryClickListener);
					builder.setCancelable(true).setNegativeButton(
							R.string.cancel, null);

				} else {
					if (!SnapBillingValidationUtils.isBillValid(shoppingCart,
							getActivity().getApplicationContext()))
						return;
					if (shoppingCart.getCustomer() != null) {
						((TextView) view
								.findViewById(R.id.customername_textview))
								.setText(shoppingCart.getCustomer()
										.getName());
					}
					((ImageView) view.findViewById(R.id.popup_icon_imageview))
							.setImageResource(R.drawable.icon_popup_list_pay);
					view.findViewById(R.id.cancel_bill_textview).setVisibility(
							View.GONE);
					view.findViewById(R.id.alert_payment_textview)
							.setVisibility(View.GONE);
					builder.setPositiveButton(getString(R.string.done), onDeliveryClickListener);
					builder.setCancelable(true).setNegativeButton(
							R.string.cancel, null);
				}
			}
			if (numKeypadFragment != null && numKeypadFragment.isAdded())
				onActionbarNavigationListener.onActionbarNavigation("",
						android.R.id.home);
			if (keypadFragment != null && keypadFragment.isAdded())
				onActionbarNavigationListener.onActionbarNavigation("",
						android.R.id.home);
			if (barcodeKeypadFragment != null
					&& barcodeKeypadFragment.isAdded())
				onActionbarNavigationListener.onActionbarNavigation("",
						android.R.id.home);

			((TextView) view.findViewById(R.id.lineitems_value_textview))
					.setText(billListAdapter.getCount() + " Products");
			((TextView) view.findViewById(R.id.saved_value_textview))
					.setText(SnapBillingTextFormatter.formatPriceText(
							shoppingCart.getTotalSavings()
									+ shoppingCart.getTotalDiscount(),
							getActivity()));
			((TextView) view.findViewById(R.id.total_value_textview))
					.setText(SnapBillingTextFormatter.formatPriceText(
							shoppingCart.getTotalCartValue(), getActivity()));
			((TextView) view.findViewById(R.id.total_qty_value_textview))
					.setText(shoppingCart.getTotalQty() + "");
			builder.setView(view);

			if (null != dialog && dialog.isShowing()) {
				dialog.cancel();
			}
			dialog = builder.create();
			dialog.show();
		}
	};
	
	
	private void showReceiveComplete(String invoiceNo) {
		shoppingCart.setReceiveInvoiceNumber(invoiceNo);	
    	AlertDialog CompleteDialog;
    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_completebill, null);
		((ImageView) view.findViewById(R.id.popup_icon_imageview)).setImageResource(R.drawable.icon_popup_list_pay);
		if (shoppingCart.getDistributor() != null) {
			((TextView) view.findViewById(R.id.customername_textview)).setText(shoppingCart.getDistributor().getAgencyName());
		}
		view.findViewById(R.id.cancel_bill_textview).setVisibility(View.GONE);
		view.findViewById(R.id.alert_payment_textview).setVisibility(View.GONE);
		if (numKeypadFragment != null && numKeypadFragment.isAdded()) {
			onActionbarNavigationListener.onActionbarNavigation("",android.R.id.home);
		} else if (keypadFragment != null && keypadFragment.isAdded()) {
			onActionbarNavigationListener.onActionbarNavigation("", android.R.id.home);
		} else if (barcodeKeypadFragment != null && barcodeKeypadFragment.isAdded()) {
			onActionbarNavigationListener.onActionbarNavigation("", android.R.id.home);
		}
		((TextView) view.findViewById(R.id.lineitems_value_textview)).setText(billListAdapter.getCount() + " Products");
		((TextView) view.findViewById(R.id.saved_value_textview)).setText(SnapBillingTextFormatter.formatPriceText(shoppingCart.getTotalSavings()+ shoppingCart.getTotalDiscount(),getActivity()));
		((TextView) view.findViewById(R.id.total_value_textview)).setText(SnapBillingTextFormatter.formatPriceText(shoppingCart.getTotalCartValue(), getActivity()));
		((TextView) view.findViewById(R.id.total_qty_value_textview)).setText(shoppingCart.getTotalQty() + "");
		builder.setPositiveButton("OK",onCompleteBillClickListener);
		builder.setCancelable(true).setNegativeButton(R.string.cancel, null);
		builder.setView(view);
		CompleteDialog = builder.create();
		CompleteDialog.show();
	}
	
	
	View.OnClickListener onReceiveCompleteClickListener = new View.OnClickListener() {

		private AlertDialog dialog;

		@Override
		public void onClick(View v) {
			GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(getActivity(), v, TAG);
			SnapToolkitConstants.IS_MENU_CLK = "0";
			if (!SnapBillingValidationUtils.isBillValid(shoppingCart, getActivity().getApplicationContext()))
				return;
			Log.d("TAG", "ReceiveInvoiceNumber------>"+shoppingCart.getReceiveInvoiceNumber());
			if (shoppingCart.getReceiveInvoiceNumber() == null) {
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				final View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_invoiceno, null);
				builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						SnapCommonUtils.hideSoftKeyboard(getActivity(), view.getWindowToken());
						String invoiceNo=((EditText) view.findViewById(R.id.invoice_no_text)).getText().toString();
						if (invoiceNo!=null && !invoiceNo.trim().equals("")) {
							showReceiveComplete(invoiceNo);
						}else{
							SnapCommonUtils.hideSoftKeyboard(getActivity(), view.getWindowToken());
							CustomToast.showCustomToast(getActivity(), getResources().getString(R.string.invoice_number_msg), Toast.LENGTH_SHORT, CustomToast.ERROR);
						}
					}
				});
			
				builder.setCancelable(true).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						SnapCommonUtils.hideSoftKeyboard(getActivity(), view.getWindowToken());
					}
				
				});
			
				builder.setView(view);
				dialog = builder.create();
				dialog.show();
			}else{
				showReceiveComplete(shoppingCart.getReceiveInvoiceNumber());
			}
		}
	};
	
	
	

	DialogInterface.OnClickListener onCancelBillClickListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			shoppingCart.setCreditChanged(false);
			shoppingCart.deleteCart();

			onActionbarNavigationListener.onActionbarNavigation(
					getString(R.string.billfragment_tag), android.R.id.home);
		}
	};

	DialogInterface.OnClickListener onConfirmPaymentClickListener = new DialogInterface.OnClickListener() {
		public AlertDialog dialog1;

		@Override
		public void onClick(DialogInterface dialog, int which) {

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

			View view = getActivity().getLayoutInflater().inflate(
					R.layout.dialog_completebill, null);
			if (!SnapBillingValidationUtils.isBillValid(shoppingCart,
					getActivity().getApplicationContext()))
				return;
			((ImageView) view.findViewById(R.id.popup_icon_imageview))
					.setImageResource(R.drawable.icon_popup_list_pay);
			view.findViewById(R.id.cancel_bill_textview).setVisibility(
					View.GONE);
			view.findViewById(R.id.alert_payment_textview).setVisibility(
					View.GONE);
			if (shoppingCart.getCustomer() != null) {
				((TextView) view.findViewById(R.id.customername_textview))
						.setText(shoppingCart.getCustomer().getName());
			}
			builder.setPositiveButton(getString(R.string.done), onCompleteBillClickListener);
			builder.setCancelable(true)
					.setNegativeButton(R.string.cancel, null);
			((TextView) view.findViewById(R.id.lineitems_value_textview))
					.setText(billListAdapter.getCount() + " Products");
			((TextView) view.findViewById(R.id.saved_value_textview))
					.setText(SnapBillingTextFormatter.formatPriceText(
							shoppingCart.getTotalSavings()
									+ shoppingCart.getTotalDiscount(),
							getActivity()));
			((TextView) view.findViewById(R.id.total_value_textview))
					.setText(SnapBillingTextFormatter.formatPriceText(
							shoppingCart.getTotalCartValue(), getActivity()));
			((TextView) view.findViewById(R.id.total_qty_value_textview))
					.setText(shoppingCart.getTotalQty() + "");
			builder.setView(view);
			if (null != dialog1 && dialog1.isShowing()) {
				dialog.cancel();
			}
			dialog1 = builder.create();
			dialog1.show();
		}
	};

	DialogInterface.OnClickListener onConfirmDeliveryClickListener = new DialogInterface.OnClickListener() {
		public AlertDialog dialog1;

		@Override
		public void onClick(DialogInterface dialog, int which) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

			View view = getActivity().getLayoutInflater().inflate(
					R.layout.dialog_completebill, null);
			if (!SnapBillingValidationUtils.isBillValid(shoppingCart,
					getActivity().getApplicationContext()))
				return;
			if (shoppingCart.getCustomer() != null) {
				((TextView) view.findViewById(R.id.customername_textview))
						.setText(shoppingCart.getCustomer().getName());
			}
			((ImageView) view.findViewById(R.id.popup_icon_imageview))
					.setImageResource(R.drawable.icon_popup_list_pay);
			view.findViewById(R.id.cancel_bill_textview).setVisibility(
					View.GONE);
			view.findViewById(R.id.alert_payment_textview).setVisibility(
					View.GONE);
			builder.setPositiveButton(getString(R.string.done), onDeliveryClickListener);
			builder.setCancelable(true)
					.setNegativeButton(R.string.cancel, null);
			((TextView) view.findViewById(R.id.lineitems_value_textview))
					.setText(billListAdapter.getCount() + " Products");
			((TextView) view.findViewById(R.id.saved_value_textview))
					.setText(SnapBillingTextFormatter.formatPriceText(
							shoppingCart.getTotalSavings()
									+ shoppingCart.getTotalDiscount(),
							getActivity()));
			((TextView) view.findViewById(R.id.total_value_textview))
					.setText(SnapBillingTextFormatter.formatPriceText(
							shoppingCart.getTotalCartValue(), getActivity()));
			((TextView) view.findViewById(R.id.total_qty_value_textview))
					.setText(shoppingCart.getTotalQty() + "");
			builder.setView(view);
			if (null != dialog1 && dialog1.isShowing()) {
				dialog.cancel();
			}
			dialog1 = builder.create();
			dialog1.show();
		}
	};
	
	private void completeOrderTransaction(boolean bDelivery, boolean bPrint) {
		shoppingCart.setCustomerPayment(customerPaymentCheck.isChecked());

		updateCustomerDetails();
		new SaveInvoice(shoppingCart, bDelivery, bPrint).execute();
		// TODO: Check this
		/*new SaveTrasnsactionTask(getActivity(),
				BillCheckoutFragment.this,
				SAVE_TRANSACTION_TASKCODE,
				transaction, customerPaymentCheck.isChecked()).execute(shoppingCart);*/
	}

	DialogInterface.OnClickListener onCompleteBillClickListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (getView().findViewById(R.id.print_bill_button).isSelected() || getView().findViewById(R.id.print_receive_button).isSelected()) {
				try {
					// TODO: Check this
					//shoppingCart.setInvoiceNumber(SaveTrasnsactionTask.getNextTransactionId(getActivity(), true));
				} catch(Exception e) { }

				if(PrinterService.isConnected()) {
					BTPrinterPlugin plugin = PrinterService.getPrinterPlugin();
					PrinterFormatter formatter = new PrinterFormatter(shoppingCart, getActivity(), plugin.getMaxLength());
					plugin.printAll(formatter);
				}	
			}
			if(!processOnlineOrder(false))
				completeOrderTransaction(false, getView().findViewById(R.id.print_bill_button).isSelected());
		}
	};

	DialogInterface.OnClickListener onDeliveryClickListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (getView().findViewById(R.id.print_bill_button).isSelected()) {
				try {
					// TODO: Check this
					//shoppingCart.setInvoiceNumber(SaveTrasnsactionTask.getNextTransactionId(getActivity(), true));
				} catch(Exception e) { }
				
				if(PrinterService.isConnected()) {
					BTPrinterPlugin plugin = PrinterService.getPrinterPlugin();
					PrinterFormatter formatter = new PrinterFormatter(shoppingCart, getActivity(), plugin.getMaxLength());
					plugin.printAll(formatter);
				}
			}
			if(!processOnlineOrder(true))
				completeOrderTransaction(true, getView().findViewById(R.id.print_bill_button).isSelected());
		}
	};

	View.OnClickListener onProductDeleteReturnEditClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			SnapToolkitConstants.IS_MENU_CLK = "0";

			if (v.getId() == R.id.billitem_delete_button) {
				GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(getActivity(), v, TAG);
				shoppingCart.deleteCartItem(editingProductPosition);
			} else if (v.getId() == R.id.billitem_return_button) {
				GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(getActivity(), v, TAG);
				shoppingCart.returnCartItem(editingProductPosition);
			} else if (v.getId() == R.id.billitem_editname_button) {
				GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(getActivity(), v, TAG);
				AlertDialog.Builder builder = new AlertDialog.Builder(
						getActivity());
				final View view = getActivity().getLayoutInflater().inflate(
						R.layout.dialog_edit_productname, null);
				final Spinner categorySpinner = ((Spinner) view.findViewById(R.id.new_prodcategory_spinner));
				final Spinner subCategorySpinner = ((Spinner) view.findViewById(R.id.new_prodsubcategory_spinner));
				Log.d("TAG", "addItemToCart --isAnonymous--getProductSkuCode-- >= "+shoppingCart.getCartItems().valueAt(editingProductPosition).product.bNew);
				
				if (shoppingCart.getShoppingCartId() == SnapBillingConstants.LAST_SHOPPING_CART && shoppingCart.getCartItems().valueAt(editingProductPosition).product.bNew) {
					categorySpinner.setVisibility(View.VISIBLE);
					subCategorySpinner.setVisibility(View.VISIBLE);
					try {
						final List<ProductCategory> productCategoryList =SnapCommonUtils.getDatabaseHelper(getActivity()).getProductCategoryDao().queryForEq("product_parentcategory_id", -1);
						ArrayAdapter<ProductCategory> categorySpinnerAdapater = new ArrayAdapter<ProductCategory>(getActivity(), android.R.layout.simple_dropdown_item_1line,productCategoryList);
						categorySpinner.setAdapter(categorySpinnerAdapater);
						categorySpinner.setSelection(7);
						categorySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

							@Override
							public void onItemSelected(AdapterView<?> arg0, View arg1,int position, long arg3) {
								try {
									List<ProductCategory> subproductCategoryList =SnapCommonUtils.getDatabaseHelper(getActivity()).getProductCategoryDao().queryForEq("product_parentcategory_id", productCategoryList.get(position).getCategoryId());
									ArrayAdapter<ProductCategory> subcategorySpinnerAdapater = new ArrayAdapter<ProductCategory>(getActivity(),android.R.layout.simple_dropdown_item_1line,subproductCategoryList);
									subCategorySpinner.setAdapter(subcategorySpinnerAdapater);
									subCategorySpinner.setSelection(0);
									if (position == 7)
										subCategorySpinner.setSelection(1);
								} catch (Exception e) {
									e.printStackTrace();
								}
								
							}

							@Override
							public void onNothingSelected(AdapterView<?> arg0) {
								
							}	
						});	
					} catch (SQLException e) {
						e.printStackTrace();
					}
				} else {
					categorySpinner.setVisibility(View.GONE);
					subCategorySpinner.setVisibility(View.GONE);
				}
				
				((EditText) view.findViewById(R.id.editproduct_name_edittext))
						.setText(shoppingCart.getCartItems().valueAt(editingProductPosition).name);
				((EditText) view.findViewById(R.id.editproduct_name_edittext))
						.setSelection(((EditText) view.findViewById(R.id.editproduct_name_edittext))
								.getText().length());
				builder.setCancelable(true).setNegativeButton(
						getString(R.string.cancel),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								SnapCommonUtils
										.hideSoftKeyboard(
												getActivity(),
												((EditText) view
														.findViewById(R.id.editproduct_name_edittext))
														.getWindowToken());
							}
						});
				builder.setPositiveButton(R.string.save,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								if (shoppingCart.getShoppingCartId() == SnapBillingConstants.LAST_SHOPPING_CART && shoppingCart.getCartItems().valueAt(editingProductPosition).product.bNew) {
									VAT vat = new VAT();
									try {
										QueryBuilder<VAT, Integer> vatQueryBuilder;
										vatQueryBuilder = SnapCommonUtils.getDatabaseHelper(getActivity()).getVatDao().queryBuilder();
										vatQueryBuilder.distinct().selectColumns("vat_value").where().eq("state_id",SnapSharedUtils.getStoreStateId(SnapCommonUtils.getSnapContext(getActivity()))).and().eq("subcategory_id", ((ProductCategory) subCategorySpinner.getSelectedItem()).getCategoryId());
										vatQueryBuilder.orderBy("vat_value", true);
										List<VAT> vatList =vatQueryBuilder.query();
										if (vatList.size() > 0)
											vat = vatList.get(0);
									} catch (SQLException e) {
										Log.e("TAG","getCategoryId---SQLException-->" + e);
										e.printStackTrace();
									}
									ProductCategory productCategory=(ProductCategory) subCategorySpinner.getSelectedItem();
									shoppingCart.editReceiveItems(editingProductPosition,
																  ((EditText) view.findViewById(R.id.editproduct_name_edittext)).getText().toString(),
																  ((ProductCategory) categorySpinner.getSelectedItem()).getCategoryName(),
																  ((ProductCategory) subCategorySpinner.getSelectedItem()).getCategoryName(),
																  vat.getVat(), productCategory);
								}
								shoppingCart.editProductName(editingProductPosition,
															 ((EditText) view.findViewById(R.id.editproduct_name_edittext)).getText().toString());
								SnapCommonUtils.hideSoftKeyboard(getActivity(),((EditText) view.findViewById(R.id.editproduct_name_edittext)).getWindowToken());
								billListAdapter.notifyDataSetChanged();
							}
						});
				builder.setView(view);
				AlertDialog dialog = builder.create();
				dialog.show();
			}
			billListAdapter.notifyDataSetChanged();
			updateBillTextViews(getActivity());
			deleteReturnLayout.setVisibility(View.GONE);
		}
	};

	@Override
	public void onProductEdit(int position, float y) {
		this.editingProductPosition = position;
		if ((keypadFragment != null && keypadFragment.isAdded())
				|| numKeypadFragment != null && numKeypadFragment.isAdded()) {
			onActionbarNavigationListener.onActionbarNavigation("",
					android.R.id.home);
		}
		if (null != quickaddKeypadFragment
				&& quickaddKeypadFragment.isVisible()) {
			unitTypeListView.setVisibility(View.GONE);

			getFragmentManager().beginTransaction()
					.detach(quickaddKeypadFragment).commit();
		}

		if (barcodeKeypadFragment != null && barcodeKeypadFragment.isAdded())
			getView().findViewById(R.id.overlay_framelayout).setVisibility(
					View.GONE);
		LinearLayout.LayoutParams rlParams = (LinearLayout.LayoutParams) deleteReturnLayout
				.findViewById(R.id.delete_return_linearlayout)
				.getLayoutParams();
		rlParams.setMargins(
				getResources().getDimensionPixelOffset(
						R.dimen.deletereturn_margin), (int) y - 150, 0, 0);
		if (billListAdapter.getItem(position).quantity > 0) {
			((Button) deleteReturnLayout
					.findViewById(R.id.billitem_return_button))
					.setText("Return");
		} else {
			((Button) deleteReturnLayout
					.findViewById(R.id.billitem_return_button))
					.setText("Cancel Return");
		}

		if (lastClickedQuickAddView != null) {
			lastClickedQuickAddView.setSelected(false);
			lastClickedQuickAddView.setEnabled(true);
			lastClickedQuickAddView = null;
		}
		deleteReturnLayout.findViewById(R.id.billitem_editname_button)
				.setVisibility(View.VISIBLE);
		deleteReturnLayout.findViewById(R.id.delete_return_linearlayout)
				.setLayoutParams(rlParams);
		deleteReturnLayout.setVisibility(View.VISIBLE);
	}

	@Override
	public void onValueEdit(int position, KeyPadMode keypadMode) {
		if (numKeypadFragment != null && numKeypadFragment.isAdded())
			onActionbarNavigationListener.onActionbarNavigation(null,
					android.R.id.home);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		if (lastClickedQuickAddView != null) {
			lastClickedQuickAddView.setEnabled(true);
			lastClickedQuickAddView.setSelected(false);
			lastClickedQuickAddView = null;
		}
		if ((keypadFragment != null && keypadFragment.isAdded())
				|| numKeypadFragment != null && numKeypadFragment.isAdded()) {
			onActionbarNavigationListener.onActionbarNavigation("",
					android.R.id.home);
		}
		if (null != quickaddKeypadFragment
				&& quickaddKeypadFragment.isVisible()) {
			unitTypeListView.setVisibility(View.GONE);

			getFragmentManager().beginTransaction()
					.detach(quickaddKeypadFragment).commit();
		}

		if (barcodeKeypadFragment != null && barcodeKeypadFragment.isAdded())
			getView().findViewById(R.id.overlay_framelayout).setVisibility(
					View.GONE);
		if (keypadFragment == null) {
			keypadFragment = new ProductKeypadFragment();
			keypadFragment.setKeypadEnterListener(BillCheckoutFragment.this);
		}
		ft.replace(R.id.overlay_framelayout, keypadFragment);
		if (!keypadFragment.isAdded()) {
			ft.addToBackStack(null);
		}
		ft.commit();
		if(billListAdapter == null)
			return;
		BillItem billItem = billListAdapter.getItem(position);
		UOM uom = billItem.getDisplayUom();
		keypadFragment.setContext(EDIT_ITEM_CONTEXT);
		keypadFragment.setArrowVisibility(View.GONE);
		keypadFragment.setEnableDiscount(true);
		keypadFragment.setQty(billItem.getQuantity(uom));
		keypadFragment.setPrice(billItem.mrp / 100.0f);
		keypadFragment.setRate(billItem.sellingPrice / 100.0f);
		keypadFragment.setTotalPrice(billItem.getTotal(true));
		keypadFragment.setStartingPrice(billItem.mrp / 100.0f);

		keypadFragment.setUnitType(UOM.convertToOldUnits(uom), getActivity().getApplicationContext());
		keypadFragment.setKeyPadMode(keypadMode);
		getView().findViewById(R.id.overlay_framelayout).setVisibility(
				View.VISIBLE);
		RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		rlParams.setMargins(
				getResources().getDimensionPixelOffset(R.dimen.billlist_width) + 20,
				getResources().getDimensionPixelOffset(
						R.dimen.customer_search_height), 0, 0);
		getActivity().findViewById(R.id.overlay_framelayout).setLayoutParams(
				rlParams);
		editingProductPosition = position;
		deleteReturnLayout.setVisibility(View.GONE);
	}

	public void updateBillTextViews(Context context) {
		if (shoppingCart == null)
			return;

		if (!shoppingCart.isCreditChanged()) {
			shoppingCart.setCashPaid((float) (shoppingCart.getTotalCartValue()
					- shoppingCart.getTotalDiscount() - shoppingCart
					.getTotalSavings()));
		}

		// TODO: Check this
		if (SnapSharedUtils.getRoundoffCheckValue(getActivity())) {
			billTotalTextView.setText(SnapBillingTextFormatter.formatPriceText(shoppingCart.getTotalCartValue() - shoppingCart.getTotalSavings(), context));
			amountTextView.setText(SnapBillingTextFormatter.formatPriceText(shoppingCart.getTotalCartValue(), context));
			billVatView.setText(SnapBillingTextFormatter.formatRoundedPriceText(shoppingCart.getTotalVatAmount(), context));
			savingsTextView.setText(SnapBillingTextFormatter.formatRoundedPriceText(shoppingCart.getTotalSavings(), context));
			Log.d("TAG", "editCartItem --updateBillTextViews-- totalCartValue--->= " + shoppingCart.getTotalCartValue());
			totalTextView.setText(SnapBillingTextFormatter.formatPriceText(shoppingCart.getTotalCartValue() - shoppingCart.getTotalDiscount() - shoppingCart.getTotalSavings(), context));
			totalQtyTextView.setText(String.valueOf(shoppingCart.getTotalQty()));
			cashButton.setText(SnapBillingTextFormatter.formatPriceText(shoppingCart.getCashPaid(), context));
			creditTextView.setText(SnapBillingTextFormatter.formatPriceText(getCreditAmount(shoppingCart.getExtraPaid()), context));
			displayCustomerPaymentCheckBox(shoppingCart.getExtraPaid() <= 0);
			discountButton.setText(SnapBillingTextFormatter.formatPriceText(shoppingCart.getTotalDiscount(), context));	
		} else {
			billTotalTextView.setText(SnapBillingTextFormatter.formatRoundedPriceText(shoppingCart.getTotalCartValue()- shoppingCart.getTotalSavings(), context));
			amountTextView.setText(SnapBillingTextFormatter.formatRoundedPriceText(shoppingCart.getTotalCartValue(), context));
			billVatView.setText(SnapBillingTextFormatter.formatRoundedPriceText(shoppingCart.getTotalVatAmount(), context));
			savingsTextView.setText(SnapBillingTextFormatter.formatRoundedPriceText(shoppingCart.getTotalSavings(), context));
			Log.d("TAG", "editCartItem --updateBillTextViews-- totalCartValue--->= " + shoppingCart.getTotalCartValue());
			totalTextView.setText(SnapBillingTextFormatter.formatRoundedPriceText(shoppingCart.getTotalCartValue() - shoppingCart.getTotalDiscount() - shoppingCart.getTotalSavings(), context));
			totalQtyTextView.setText(String.valueOf(shoppingCart.getTotalQty()));
			cashButton.setText(SnapBillingTextFormatter.formatPriceTextCheckoutCash(shoppingCart.getCashPaid(), context));
			creditTextView.setText(SnapBillingTextFormatter.formatPriceText(getCreditAmount(shoppingCart.getExtraPaid()), context));
			displayCustomerPaymentCheckBox(shoppingCart.getExtraPaid() <= 0);
			discountButton.setText(SnapBillingTextFormatter.formatPriceText(shoppingCart.getTotalDiscount(), context));
			billListView = ((ListView) getView().findViewById(R.id.bill_listview));
		}
		if (null != billListView && null != billListAdapter)
			billListView.setSelection(shoppingCart.getCartItems().size() - 1);
	}

	public void hideSearchProducts() {

		productSearchEditText.setText("");
		barcode_product_name_search_edittext.setText("");
		SnapCommonUtils.hideSoftKeyboard(getActivity(), productSearchEditText.getWindowToken());
		SnapCommonUtils.hideSoftKeyboard(getActivity(), barcode_product_name_search_edittext.getWindowToken());
	}
	
	private void displayCustomerPaymentCheckBox(boolean isVisible) {
		boolean isCashPaidMore = (SnapBillingUtils.formatDecimalValue(shoppingCart.getCashPaid()) >
									(SnapBillingUtils.formatDecimalValue(shoppingCart.getTotalCartValue()
									- shoppingCart.getTotalDiscount() - shoppingCart.getTotalSavings())));
		if (isVisible && shoppingCart.getCustomer() != null && isCashPaidMore) {
			customerPaymentTextView.setVisibility(View.VISIBLE);
			customerPaymentCheck.setVisibility(View.VISIBLE);
		} else {
			customerPaymentCheck.setChecked(false);
			customerPaymentTextView.setVisibility(View.INVISIBLE);
			customerPaymentCheck.setVisibility(View.INVISIBLE);
		}
		updateChangeText(getActivity());
	}

	private void updateChangeText(Context context) {
		double totalCredit = getChangeReturned(shoppingCart.getExtraPaid());
		if (shoppingCart.getCustomer() != null)
			totalCredit = (!customerPaymentCheck.isChecked()) ? (getChangeReturned(shoppingCart.getExtraPaid())) : 0;
		changeTextView.setText(SnapBillingTextFormatter.formatPriceText(totalCredit, context));
	}
	View.OnClickListener onAddEditDelCustomerClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (shoppingCart != null) {
				if (v.getId() == R.id.add_customer_button) {
					showAddCustomerLayout("");
					GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(getActivity(), v, TAG);
				} else if (v.getId() == R.id.editcustomer_info_button) {
					GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(getActivity(), v, TAG);
					onExpandCustomerDetailsClickListener.onClick(getView()
							.findViewById(R.id.expand_customerdetails_button));
					if (shoppingCart.getShoppingCartId() == SnapBillingConstants.LAST_SHOPPING_CART)
						showEditDistributor(shoppingCart.getDistributor());
					else
						showEditCustomerLayout(shoppingCart.getCustomer());
					
				} else {
					suggestedProductsGridView.setVisibility(View.GONE);
					if (mode == 2)
						getView().findViewById(
								R.id.customer_suggestions_imageview)
								.setVisibility(View.VISIBLE);
					shoppingCart.setCustomer(null);
					shoppingCart.setDistributor(null);
					customerInformationLayout.setVisibility(View.GONE);
					addCustomerLayout.setVisibility(View.GONE);
					displayCustomerPaymentCheckBox(false);
				}
			}
		}
	};

	public void showEditCustomerLayout(Customers customer) {
		addCustomerLayout.setVisibility(View.VISIBLE);
		addCustomerLayout.findViewById(R.id.edit_button).setVisibility(View.VISIBLE);
		addCustomerLayout.findViewById(R.id.edit_button).setTag(customer);
		addCustomerLayout.findViewById(R.id.save_button).setVisibility(View.INVISIBLE);
		((EditText) addCustomerLayout.findViewById(R.id.customer_number_edittext)).setText(String.valueOf(customer.getPhone()));
		((EditText) addCustomerLayout.findViewById(R.id.customer_address_edittext)).setText(customer.getAddress());
		((EditText) addCustomerLayout.findViewById(R.id.customer_name_edittext)).setText(customer.getName());
		((EditText) addCustomerLayout.findViewById(R.id.customer_creditlimit_edittext)).setText(customer.getCreditLimit() + "");
		customerSearchLayoutContainer.setVisibility(View.GONE);
	}
	
	public void showEditDistributor(Distributor distributor) {
		addCustomerLayout.setVisibility(View.VISIBLE);
		addCustomerLayout.findViewById(R.id.edit_button).setVisibility(View.VISIBLE);
		addCustomerLayout.findViewById(R.id.edit_button).setTag(distributor);
		addCustomerLayout.findViewById(R.id.save_button).setVisibility(View.INVISIBLE);
		setDistributorHint();
		((EditText) addCustomerLayout.findViewById(R.id.customer_number_edittext)).setText(distributor.getPhoneNumber());
		((EditText) addCustomerLayout.findViewById(R.id.customer_address_edittext)).setText(distributor.getTinNumber());
		((EditText) addCustomerLayout.findViewById(R.id.customer_name_edittext)).setText(distributor.getAgencyName());
		customerSearchLayoutContainer.setVisibility(View.GONE);
	}
	
	private void setDistributorHint() {
		((EditText)addCustomerLayout.findViewById(R.id.customer_number_edittext)).setHint(R.string.distributor_number_hint);
		((EditText)addCustomerLayout.findViewById(R.id.customer_name_edittext)).setHint(R.string.agency_name_hint);
		((EditText)addCustomerLayout.findViewById(R.id.customer_address_edittext)).setHint(R.string.distributor_tin_hint);
		((EditText)addCustomerLayout.findViewById(R.id.customer_address_edittext)).setInputType(InputType.TYPE_CLASS_NUMBER);
		((EditText)addCustomerLayout.findViewById(R.id.customer_address_edittext)).setFilters(SnapBillingUtils.setTextLength(SnapBillingConstants.TIN_MAX_LENGTH));
	}

	public void showAddCustomerLayout(String phoneNumber) {
		
		addCustomerLayout.findViewById(R.id.edit_button).setVisibility(
				View.INVISIBLE);
		addCustomerLayout.findViewById(R.id.save_button).setVisibility(
				View.VISIBLE);
		addCustomerLayout.setVisibility(View.VISIBLE);
		if (ValidationUtils.validateNumber(phoneNumber)) {
			((EditText) addCustomerLayout
					.findViewById(R.id.customer_number_edittext))
					.setText(phoneNumber);
			((EditText) addCustomerLayout
					.findViewById(R.id.customer_number_edittext))
					.setSelection(phoneNumber.length());
		}
		((EditText) addCustomerLayout
				.findViewById(R.id.customer_number_edittext)).requestFocus();
		if (shoppingCart.getShoppingCartId() == SnapBillingConstants.LAST_SHOPPING_CART) {
			setDistributorHint();
		} else {
			((EditText)addCustomerLayout.findViewById(R.id.customer_number_edittext)).setHint(R.string.customer_number_hint);
			((EditText)addCustomerLayout.findViewById(R.id.customer_name_edittext)).setHint(R.string.customer_name_hint);
			((EditText)addCustomerLayout.findViewById(R.id.customer_address_edittext)).setHint(R.string.customer_address_hint);
			((EditText)addCustomerLayout.findViewById(R.id.customer_address_edittext)).setInputType(InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);
			((EditText)addCustomerLayout.findViewById(R.id.customer_address_edittext)).setFilters(SnapBillingUtils.setTextLength(SnapBillingConstants.CUSTOMER_ADDR_MAX_LENGTH));
		}
		
		customerSearchLayoutContainer.setVisibility(View.GONE);
	}
	
	

	TextWatcher customerSearchTextWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

			customerSearchKeyStrokeTimer.cancel();
			if (s.length() == 0) {
				if (customerAdapter != null)
					customerAdapter.clear();
			} else {
				customerSearchKeyStrokeTimer.start();
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {

		}

	};

	CountDownTimer customerSearchKeyStrokeTimer = new CountDownTimer(300, 300) {
		@Override
		public void onTick(long arg0) {
		}

		@Override
		public void onFinish() {
			if (addCustomerLayout.getVisibility() == View.VISIBLE) {
				((EditText) getActivity().findViewById(
						R.id.customer_number_edittext))
						.setText(customerSearchEditText.getText().toString());
			}
			if (customerSearchEditText.length() == 0) {
				addCustomerLayout.setVisibility(View.GONE);
				if (customerAdapter != null) {
					customerAdapter.clear();
					customerAdapter.notifyDataSetChanged();
				}
				customerSearchLayoutContainer.setVisibility(View.GONE);
			} else {
				if (shoppingCart.getShoppingCartId() == SnapBillingConstants.LAST_SHOPPING_CART) {
					  new QueryDistributorInfoTask(getActivity().getApplicationContext(), BillCheckoutFragment.this,
		                		GET_DISTRIBUTORINFO_TASKCODE).execute(customerSearchEditText.getText().toString());
				} else {
					getCustomer(customerSearchEditText.getText().toString());
				}
				
			}
		}
	};

	View.OnClickListener onExpandCustomerDetailsClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(getActivity(), v, TAG);
			v.setSelected(!v.isSelected());
			if (v.isSelected()) {
				customerDetailsLayout.setVisibility(View.VISIBLE);
			} else {
				customerDetailsLayout.setVisibility(View.GONE);
			}
		}
	};

	@Override
	public void onKeyBoardEnter(float qty, double price, double rate,
			double totalPrice, SkuUnitType skuUnitType, int context) {
		Log.d("keypad", "qty " + qty + " price " + price + " rate " + rate
				+ " total " + totalPrice + " unit type " + skuUnitType
				+ " context-->" + context);
		if (SkuUnitType.PC.ordinal() == skuUnitType.ordinal()) {
			qty = (float) Math.floor(qty);
		}
		if (context == EDIT_ITEM_CONTEXT) {
			if (shoppingCart != null) {
				overLay.setVisibility(View.GONE);
				if (keypadFragment != null && keypadFragment.isAdded()) {
					getFragmentManager().popBackStack();
				}
				shoppingCart.editCartItem(editingProductPosition, qty, (int)(price*100), (int)(rate*100), UOM.getFromOldUnits(skuUnitType));
				updateBillTextViews(getActivity());
				billListAdapter.notifyDataSetChanged();
			}
		} else if (context == QUICKADD_CONTEXT) {

			if (shoppingCart != null) {
				if (rate == 0) {
					rate = price;
				}
				ProductSku quickAddProduct = new ProductSku();
				quickAddProduct.setProductSkuCode(currentQuickAddProductCode);
				quickAddProduct.setProductSkuName(currentQuickAddProductName);
				quickAddProduct.setProductSkuMrp((float) price);
				quickAddProduct.setProductSkuSalePrice((float) rate);
				if (skuUnitType.ordinal() == SkuUnitType.GM.ordinal()) {
					quickAddProduct.setProductSkuUnits(SkuUnitType.KG);
				} else if (skuUnitType.ordinal() == SkuUnitType.ML.ordinal()) {
					quickAddProduct.setProductSkuUnits(SkuUnitType.LTR);
				} else {
					quickAddProduct.setProductSkuUnits(skuUnitType);
				}
				shoppingCart.addQuickAddItemToCart(quickAddProduct, qty, skuUnitType);
				updateBillTextViews(getActivity());
			}
			getView().findViewById(R.id.overlay_framelayout).setVisibility(
					View.GONE);
			onActionbarNavigationListener.onActionbarNavigation("",
					android.R.id.home);
		}
	}

	public void showOffers(Context context) {
		SnapToolkitConstants.IS_MENU_CLK = "0";
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		SnapCommonUtils.hideSoftKeyboard(getActivity(), customerSearchEditText.getWindowToken());
		if (offersAdapter == null) {
			new GetProductOffersTask(context, this, GET_OFFERED_PRODUCTS, shoppingCart.getShoppingCartId())
					.execute();
		}
		if (getActivity() != null) {
			if (searchProductSkuTask != null
					&& !searchProductSkuTask.isCancelled())
				searchProductSkuTask.cancel(true);
			enableOptionButtons(mode);
			getView().findViewById(R.id.offers_button).setEnabled(false);
			if ((keypadFragment != null && keypadFragment.isAdded())
					|| (numKeypadFragment != null && numKeypadFragment
							.isAdded())) {
				onActionbarNavigationListener.onActionbarNavigation(null,
						android.R.id.home);
			} else if (deleteReturnLayout.getVisibility() == View.VISIBLE) {
				deleteReturnLayout.setVisibility(View.GONE);
			}
			if (barcodeKeypadFragment != null
					&& barcodeKeypadFragment.isAdded()) {
				onActionbarNavigationListener.onActionbarNavigation("",
						android.R.id.home);
				barcodeKeypadFragment = null;
			}
			((TextView) getActivity().findViewById(R.id.actionbar_header))
					.setText("Offers");
			((TextView) getActivity().findViewById(R.id.actionbar_header))
					.setVisibility(View.VISIBLE);
			quickAddRelativeLayout.setVisibility(View.GONE);
			getView().findViewById(R.id.weight_framelayout).setVisibility(
					View.GONE);
			checkoutLayout.setVisibility(View.GONE);
			offersGridView.setVisibility(View.VISIBLE);
			if (!barcode_product_name_search_edittext.getText().toString()
					.trim().isEmpty())
				barcode_product_name_search_edittext.setText("");
			if (!productSearchEditText.getText().toString().trim().isEmpty())
				productSearchEditText.setText("");
			searchLayout.setVisibility(View.GONE);
			productSearchEditText.setVisibility(View.GONE);
			clearProductSearch.setVisibility(View.GONE);
			suggestedProductsGridView.setVisibility(View.GONE);
			getView().findViewById(R.id.customer_suggestions_imageview)
					.setVisibility(View.GONE);
		}

		mode = 1;
	}

	public void showQuickAdd(Context context) {
		SnapToolkitConstants.IS_MENU_CLK = "0";

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		SnapCommonUtils.hideSoftKeyboard(getActivity(), customerSearchEditText.getWindowToken());
		if (getActivity() != null) {
			enableOptionButtons(mode);
			getView().findViewById(R.id.quickadd_button).setEnabled(false);
			if ((keypadFragment != null && keypadFragment.isAdded())
					|| (numKeypadFragment != null && numKeypadFragment
							.isAdded())) {
				onActionbarNavigationListener.onActionbarNavigation(null,
						android.R.id.home);
			} else if (deleteReturnLayout.getVisibility() == View.VISIBLE) {
				deleteReturnLayout.setVisibility(View.GONE);
			}
			if (barcodeKeypadFragment != null
					&& barcodeKeypadFragment.isAdded()) {
				onActionbarNavigationListener.onActionbarNavigation("",
						android.R.id.home);
				barcodeKeypadFragment = null;
			}
			if (quickaddCategoryAdapter == null) {
				if (quickAddCategories != null) {
					quickaddCategoryAdapter = new QuickAddCategoryAdapter(
							getActivity(), quickAddCategories);
					if (quickAddGridView != null) {
						quickAddGridView.setAdapter(quickaddCategoryAdapter);

					}
				}
			}

			((TextView) getActivity().findViewById(R.id.actionbar_header))
					.setText(getString(R.string.quick_add));
			((TextView) getActivity().findViewById(R.id.actionbar_header))
					.setVisibility(View.VISIBLE);
			quickAddRelativeLayout.setVisibility(View.VISIBLE);
			getView().findViewById(R.id.weight_framelayout).setVisibility(
					View.VISIBLE);
			checkoutLayout.setVisibility(View.GONE);
			offersGridView.setVisibility(View.GONE);
			if (!barcode_product_name_search_edittext.getText().toString()
					.trim().isEmpty())
				barcode_product_name_search_edittext.setText("");
			if (!productSearchEditText.getText().toString().trim().isEmpty())
				productSearchEditText.setText("");
			searchLayout.setVisibility(View.GONE);
			productSearchEditText.setVisibility(View.GONE);
			clearProductSearch.setVisibility(View.GONE);
			suggestedProductsGridView.setVisibility(View.GONE);
			getView().findViewById(R.id.customer_suggestions_imageview)
					.setVisibility(View.GONE);
		}
		mode = 0;
	}

	public void showCheckout() {
		if (getActivity() != null) {
			SnapToolkitConstants.IS_MENU_CLK = "0";
			SnapCommonUtils.hideSoftKeyboard(getActivity(), customerSearchEditText.getWindowToken());
			enableOptionButtons(mode);
			if ((keypadFragment != null && keypadFragment.isAdded())
					|| (numKeypadFragment != null && numKeypadFragment
							.isAdded())) {
				onActionbarNavigationListener.onActionbarNavigation(null,
						android.R.id.home);
			} else if (deleteReturnLayout.getVisibility() == View.VISIBLE) {
				deleteReturnLayout.setVisibility(View.GONE);
			}
			if (searchProductSkuTask != null
					&& !searchProductSkuTask.isCancelled())
				searchProductSkuTask.cancel(true);
			if (barcodeKeypadFragment != null
					&& barcodeKeypadFragment.isAdded())
				onActionbarNavigationListener.onActionbarNavigation("",
						android.R.id.home);
			
			if (shoppingCart.getShoppingCartId() == SnapBillingConstants.LAST_SHOPPING_CART) {
				((TextView) getActivity().findViewById(R.id.actionbar_header)).setText(R.string.receive);
			} else {
				((TextView) getActivity().findViewById(R.id.actionbar_header)).setText(R.string.checkout);
			}
			setFooterColor();

			((TextView) getActivity().findViewById(R.id.actionbar_header))
					.setVisibility(View.VISIBLE);
			quickAddRelativeLayout.setVisibility(View.GONE);
			getView().findViewById(R.id.weight_framelayout).setVisibility(
					View.GONE);
			checkoutLayout.setVisibility(View.VISIBLE);
			offersGridView.setVisibility(View.GONE);
			if (!barcode_product_name_search_edittext.getText().toString()
					.trim().isEmpty())
				barcode_product_name_search_edittext.setText("");
			if (!productSearchEditText.getText().toString().trim().isEmpty())
				productSearchEditText.setText("");
			searchLayout.setVisibility(View.GONE);
			productSearchEditText.setVisibility(View.GONE);
			clearProductSearch.setVisibility(View.GONE);
			if (shoppingCart.getCustomer() != null) {
				suggestedProductsGridView.setVisibility(View.VISIBLE);
				new GetCustomerSuggestedProductsTask(getActivity()
						.getApplicationContext(), this,
						GET_CUSTOMERSUGGESTION_TASKCODE).execute(0);
				getView().findViewById(R.id.customer_suggestions_imageview)
						.setVisibility(View.GONE);
				displayCustomerPaymentCheckBox(true);
				customerPaymentCheck.setChecked(customerPaymentCheck.isChecked());
			} else {
				suggestedProductsGridView.setVisibility(View.GONE);
				displayCustomerPaymentCheckBox(false);
				getView().findViewById(R.id.customer_suggestions_imageview)
						.setVisibility(View.VISIBLE);
			}
		}
		mode = 2;
	}

	public void enableOptionButtons(int oldMode) {
		switch (oldMode) {
		case 0:
			getView().findViewById(R.id.quickadd_button).setEnabled(true);
			break;
		case 1:
			getView().findViewById(R.id.offers_button).setEnabled(true);
			break;
		case 3:
			getView().findViewById(R.id.search_button).setEnabled(true);
			break;
		default:
			break;
		}
	}

	public void showSearchProducts(Context context) {
		if (getActivity() != null) {
			SnapToolkitConstants.IS_MENU_CLK = "1";

			enableOptionButtons(mode);

			getView().findViewById(R.id.search_button).setEnabled(false);
			if ((keypadFragment != null && keypadFragment.isAdded())) {
				onActionbarNavigationListener.onActionbarNavigation(null,
						android.R.id.home);
			} else if (numKeypadFragment != null && numKeypadFragment.isAdded()) {
				onActionbarNavigationListener.onActionbarNavigation(null,
						android.R.id.home);
			} else if (barcodeKeypadFragment != null
					&& barcodeKeypadFragment.isAdded()) {
				onActionbarNavigationListener.onActionbarNavigation(null,
						android.R.id.home);
			} else if (deleteReturnLayout.getVisibility() == View.VISIBLE) {
				deleteReturnLayout.setVisibility(View.GONE);
			}

			barcodeKeypadFragment = null;
			onBarcodeSearchClickListener
					.onClick(barcode_product_name_search_edittext);
			((TextView) getActivity().findViewById(R.id.actionbar_header))
					.setVisibility(View.GONE);
			searchLayout.setVisibility(View.VISIBLE);

			productSearchEditText.setVisibility(View.VISIBLE);
			clearProductSearch.setVisibility(View.VISIBLE);
			quickAddRelativeLayout.setVisibility(View.GONE);
			getView().findViewById(R.id.weight_framelayout).setVisibility(
					View.GONE);
			checkoutLayout.setVisibility(View.GONE);
			offersGridView.setVisibility(View.GONE);
			suggestedProductsGridView.setVisibility(View.GONE);
			getView().findViewById(R.id.customer_suggestions_imageview)
					.setVisibility(View.GONE);
			showSoftKeyboard(getActivity(), productSearchEditText);
		}
		mode = 3;
	}

	public void getCustomer(String customerNameOrAddressOrPhone) {
    	List<Customers> customerListItems = snapbizzDB.getCustomerByNameOrAddressOrPhone(customerNameOrAddressOrPhone);
    	if (customerListItems != null && !customerListItems.isEmpty()) {
			hideAddCustomerLayout();
			ListView customerListView = (ListView) getView().findViewById(
					R.id.customer_search_result_listview);
			if (customerAdapter == null) {
				customerAdapter = new CustomerSearchListAdapter(getActivity()
						.getApplicationContext(), android.R.id.text1,
						customerListItems);
				customerListView.setAdapter(customerAdapter);
			} else {
				customerAdapter.clear();
				customerAdapter.addAll(customerListItems);
				customerAdapter.notifyDataSetChanged();
			}
			customerSearchLayoutContainer.setVisibility(View.VISIBLE);
        } else {
        	showAddCustomerLayout(getString(R.string.err_no_customer_found));
		}
	}
	
	// TODO: Check this
	public void updateCustomerDetails() {
		if (shoppingCart.getCustomer() != null) {
        	long phone; 
        	int amountDue = 0;
        	int purchaseValue = 0;
        	int amountSaved = 0;
        	Integer lastPurchaseAmount = 0;
        	Integer lastPaymentAmount = 0;
        	Integer avgPurchasePerVisit = 0;
        	Float avgVisitsPerMonth = 0f;
        	Date lastPurchaseDate = null;
        	Date lastPaymentDate = null;
        	float credit = (float)(shoppingCart.getTotalCartValue()
					- (shoppingCart.getCashPaid() + shoppingCart.getTotalDiscount()
					+ shoppingCart.getTotalSavings()));
    		phone = (shoppingCart.getCustomer().getPhone());
        	if (credit < 0 && customerPaymentCheck.isChecked()) {
        		lastPaymentDate = new Date();
        		lastPaymentAmount = (int) Math.abs(credit);
        	}
        	if (credit < 0 && !customerPaymentCheck.isChecked())
        		credit = 0;
        	if (credit > 0 || customerPaymentCheck.isChecked())
        		amountDue = ((int)(snapbizzDB.getCustomerDuePhoneNo(shoppingCart
        									 .getCustomer().getPhone()) + credit));
        	
        	lastPurchaseDate = (new Date());
        	lastPurchaseAmount = ((int) (shoppingCart.getTotalCartValue() - shoppingCart.getTotalDiscount() - shoppingCart.getTotalSavings()));
            long count = 1;
            long totalNumberPerMonth = snapbizzDB.invoiceHelper.totalCustomerVisitsPerMonth(phone);
            long visits = snapbizzDB.invoiceHelper.getCustomerVisitsByPhone(phone);
            if (visits != 0) {
            	count = visits;
            	count++;
            }
            float averageVisits = 0;
            if (totalNumberPerMonth != 0)
            	averageVisits = count / totalNumberPerMonth;
            avgVisitsPerMonth = (averageVisits);
            int averagePurchase = 0;
            averagePurchase += shoppingCart.getTotalCartValue() - shoppingCart.getTotalDiscount() - shoppingCart.getTotalSavings(); 
            if (totalNumberPerMonth != 0)
            	averagePurchase = (int) (averagePurchase / totalNumberPerMonth);
            avgPurchasePerVisit = (averagePurchase);
           	snapbizzDB.updateCustomerDetails(phone, amountDue, purchaseValue, amountSaved, lastPurchaseAmount, lastPaymentAmount, avgPurchasePerVisit, avgVisitsPerMonth, lastPurchaseDate, lastPaymentDate);
        }
	}
	
	@Override
	public void onTaskSuccess(Object responseList, int taskCode) {
		Log.d("TAG", "BillCheckoutFragment onTaskSuccess------>"+taskCode);
		if (getActivity() == null)
			return;
		Log.d("TAG", "BillCheckoutFragment onTaskSuccess-----1->"+taskCode);
		if (taskCode == SAVE_TRANSACTION_TASKCODE) {
			if (shoppingCart != null) {
				shoppingCart.setCreditChanged(false);
				shoppingCart.deleteCart();
				billListAdapter.notifyDataSetChanged();
				onActionbarNavigationListener
						.onActionbarNavigation(
								getString(R.string.billfragment_tag),
								android.R.id.home);
				customerPaymentCheck.setChecked(false);
			}
		} else if (taskCode == GET_OFFERED_PRODUCTS) {
			if (offersAdapter == null) {
				offersAdapter = new VirtualStoreProductAdapter(getActivity()
						.getApplicationContext(),
						(ArrayList<ProductSku>) responseList);
				offersGridView.setAdapter(offersAdapter);
			}
		} else if (taskCode == GET_DISTRIBUTORINFO_TASKCODE) {
			 ArrayList<Distributor> distributorListItems = (ArrayList<Distributor>) responseList;
			 hideAddCustomerLayout();
			 ListView customerListView = (ListView) getView().findViewById(R.id.customer_search_result_listview);
			 distributorAdapter = new DistributorSearchListAdapter(getActivity().getApplicationContext(), android.R.id.text1,distributorListItems);
			 customerListView.setAdapter(distributorAdapter);
		     customerSearchLayoutContainer.setVisibility(View.VISIBLE);
	
        } else if (taskCode == ADD_DISTRIBUTORINFO_TASKCODE) {
			hideAddCustomerLayout();
			if (shoppingCart != null) {
				shoppingCart.setDistributor((Distributor) responseList);
				customerSearchLayoutContainer.setVisibility(View.GONE);
				customerInformationLayout.setVisibility(View.VISIBLE);
				customerNumberTextView.setText(shoppingCart.getDistributor()
						.getPhoneNumber());
				customerNameTextView.setText(shoppingCart.getDistributor()
						.getAgencyName());
				customerAddressTextView.setText(shoppingCart.getDistributor()
						.getTinNumber());
				displayCustomerPaymentCheckBox(false);
			}
		}else if (taskCode == SEARCH_PRODUCTS_TASKCODE) {

			productSearchAdapter = new ProductSearchListAdapter(getActivity()
					.getApplicationContext(), android.R.id.text1,
					(ArrayList<ProductSku>) responseList);
			productSearchEditText.setThreshold(1);
			productSearchEditText.setAdapter(productSearchAdapter);
			productSearchEditText.showDropDown();

		}

		else if (taskCode == SEARCH_BARCODE_PRODUCTS_TASKCODE) {

			barcodeProductSearchAdapter = new BarcodeProductSearchListAdapter(
					getActivity().getApplicationContext(), android.R.id.text1,
					(ArrayList<ProductSku>) responseList);
			barcode_product_name_search_edittext.setThreshold(1);
			barcode_product_name_search_edittext
					.setAdapter(barcodeProductSearchAdapter);
			barcode_product_name_search_edittext.showDropDown();

		}

		else if (taskCode == GET_CUSTOMERSUGGESTION_TASKCODE) {
			if (mode == 2) {
				getView().findViewById(R.id.customer_suggestions_imageview)
						.setVisibility(View.GONE);
				suggestedProductsGridView.setVisibility(View.VISIBLE);
			}
			if (suggestedProductsAdapter == null) {
				suggestedProductsAdapter = new VirtualStoreProductAdapter(
						getActivity().getApplicationContext(),
						(ArrayList<ProductSku>) responseList);
				suggestedProductsGridView.setAdapter(suggestedProductsAdapter);
			} else {
				suggestedProductsAdapter.clear();
				suggestedProductsAdapter
						.addAll((Collection<? extends ProductSku>) responseList);
			}
		} else if (taskCode == GET_QUICKADD_CATEGORY_TASKCODE) {
			if (quickaddCategoryAdapter == null) {
				quickaddCategoryAdapter = new QuickAddCategoryAdapter(
						getActivity().getApplicationContext(),
						(ArrayList<ProductCategory>) responseList);
				quickAddGridView.setAdapter(quickaddCategoryAdapter);
			} else {
				quickaddCategoryAdapter.clear();
				quickaddCategoryAdapter
						.addAll((ArrayList<ProductCategory>) responseList);
			}
		} else if (taskCode == GET_QUICKADD_PRODUCTS_TASKCODE) {
			quickaddProductAdapter.clear();
			quickaddProductAdapter.addAll((ArrayList<ProductSku>) responseList);
		}
	}

	public void hideAddCustomerLayout() {
		if (addCustomerLayout == null)
			return;
		addCustomerLayout.setVisibility(View.GONE);
		((EditText) addCustomerLayout.findViewById(R.id.customer_name_edittext))
				.setText("");
		((EditText) addCustomerLayout
				.findViewById(R.id.customer_number_edittext)).setText("");
		((EditText) addCustomerLayout
				.findViewById(R.id.customer_address_edittext)).setText("");
		((EditText) addCustomerLayout
				.findViewById(R.id.customer_creditlimit_edittext)).setText("");
		displayCustomerPaymentCheckBox(false);
	}
	
	public void showProductsLayout() {
		if (quickaddProductCategory != null) {
			new GetQuickAddProductsTask(getActivity()
					.getApplicationContext(), BillCheckoutFragment.this,
					GET_QUICKADD_PRODUCTS_TASKCODE,shoppingCart.getShoppingCartId())
					.execute(quickaddProductCategory);
			((TextView) quickAddRelativeLayout
					.findViewById(R.id.quickadd_productcategory_name_textview))
					.setText(quickaddProductCategory.getCategoryName());
			((ImageView) quickAddRelativeLayout
					.findViewById(R.id.quickadd_productcategory_imageview))
					.setImageDrawable(SnapCommonUtils
							.getProductCategoryDrawable(
									quickaddProductCategory, getActivity()
											.getApplicationContext()));
			quickAddGridView.setVisibility(View.GONE);
			quickAddRelativeLayout.findViewById(
					R.id.quickadd_generic_linearlayout).setVisibility(
					View.GONE);
			quickAddRelativeLayout.findViewById(
					R.id.quickadd_products_layout).setVisibility(
					View.VISIBLE);
			quickAddRelativeLayout.findViewById(R.id.quickadd_input_layout)
					.setVisibility(View.GONE);
			if (keypadFragment != null && keypadFragment.isVisible())
				onActionbarNavigationListener.onActionbarNavigation("",
						android.R.id.home);
		}
		if (quickaddProductAdapter != null) {
			quickaddProductAdapter.setLastSelectedItemPosition(-1);
			quickaddProductAdapter.clear();
			quickaddProductAdapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		if (menuItem.getItemId() != android.R.id.home) {

			if (numKeypadFragment != null && numKeypadFragment.isAdded()) {
				getFragmentManager().popBackStack();
			}
			if (keypadFragment != null && keypadFragment.isAdded()) {
				getFragmentManager().popBackStack();
			}
			if (barcodeKeypadFragment != null
					&& barcodeKeypadFragment.isAdded())
				getFragmentManager().popBackStack();

		}
		if (lastClickedQuickAddView != null) {
			lastClickedQuickAddView.setEnabled(true);
			lastClickedQuickAddView.setSelected(false);
			lastClickedQuickAddView = null;
		}
		onActionbarNavigationListener.onActionbarNavigation(
				getString(R.string.billfragment_tag), menuItem.getItemId());
		return true;
	}

	@Override
	public void onPause() {
		super.onPause();
		if (quickaddKeypadFragment != null) {
			getFragmentManager().beginTransaction()
					.detach(quickaddKeypadFragment).commit();
		}
		if (searchProductSkuTask != null)
			searchProductSkuTask.cancel(true);
		customerSearchEditText.setText("");
		productSearchEditText.setText("");
		barcode_product_name_search_edittext.setText("");
		customerSearchEditText
				.removeTextChangedListener(customerSearchTextWatcher);
		productSearchEditText
				.removeTextChangedListener(productSearchTextWatcher);

	}

	@Override
	public void onResume() {
		super.onResume();
		productSearchEditText.addTextChangedListener(productSearchTextWatcher);
		customerSearchEditText
				.addTextChangedListener(customerSearchTextWatcher);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		clearTextControls();
		SnapCommonUtils.getDrawableCache().clearList();
		shoppingCart = null;
		searchLayout = null;
		deleteReturnLayout = null;
		checkoutLayout = null;
		addCustomerLayout = null;
		customerSearchLayoutContainer = null;
		customerInformationLayout = null;
		customerDetailsLayout = null;
		quickAddRelativeLayout = null;
		lastClickedQuickAddView = null;
		discountButton = null;
		cashButton = null;
		billTotalTextView = null;
		billVatView = null;
		amountTextView = null;
		savingsTextView = null;
		totalTextView = null;
		creditTextView = null;
		totalQtyTextView = null;
		customerNameTextView = null;
		customerDueTextView = null;
		customerNumberTextView = null;
		customerAddressTextView = null;
		customerCreditLimitTextView = null;
		customerDueAmountTextView = null;
		customerMembershipDateTextView = null;
		customerSearchEditText = null;
		productSearchEditText = null;
		clearProductSearch = null;
		clearCustomersSearch = null;
		offersGridView = null;
		suggestedProductsGridView = null;
		quickAddGridView = null;
		quickaddProductGridView = null;
		unitTypeListView = null;
		quickAddCategories = null;
		barcode_product_name_search_edittext = null;
		barcodeKeypadFragment = null;
		clearBarcodeText = null;
		clearCustomersSearch = null;
		clearProductSearch = null;

		keypadFragment = null;
		numKeypadFragment = null;
		offersAdapter = null;
		suggestedProductsAdapter = null;
		productSearchAdapter = null;
		quickaddCategoryAdapter = null;
		quickaddProductAdapter = null;
		customerAdapter = null;
		billListAdapter = null;
		searchedProductClickListener = null;
		onActionbarNavigationListener = null;
		printerManager = null;
		searchProductSkuTask = null;
		quickaddProduct = null;
		quickaddProductCategory = null;
		unitTypeAdapter = null;
		onCreateEditQuickAddClickListener = null;
		quickaddUnitType = null;
		printerType = null;
		printerTypeDialog = null;

	}

	private void clearTextControls() {
		if (customerSearchEditText == null)
			return;
		customerSearchEditText.removeTextChangedListener(customerSearchTextWatcher);

		customerSearchEditText.setBackground(null);

		customerSearchEditText = null;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		if (quickaddKeypadFragment != null) {
			try {
				getFragmentManager().beginTransaction()
									.detach(quickaddKeypadFragment).commit();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void onTaskError(String errorMessage, int taskCode) {
		if (getView() == null)
			return;
		if (taskCode == GET_DISTRIBUTORINFO_TASKCODE) {
			showAddCustomerLayout(errorMessage);
		}else if (taskCode == SEARCH_PRODUCTS_TASKCODE) {
			if (productSearchAdapter != null)
				productSearchAdapter.clear();
			CustomToast.showCustomToast(getActivity().getApplicationContext(),
					getString(R.string.msg_no_products), Toast.LENGTH_SHORT,
					CustomToast.INFORMATION);
		} else if (taskCode == GET_CUSTOMERSUGGESTION_TASKCODE) {
			getView().findViewById(R.id.customer_suggestions_imageview)
					.setVisibility(View.GONE);
			if (suggestedProductsAdapter != null) {
				suggestedProductsAdapter.clear();
			}
			CustomToast.showCustomToast(getActivity().getApplicationContext(),
					getString(R.string.msg_no_products), Toast.LENGTH_SHORT,
					CustomToast.INFORMATION);
		} else if (taskCode == GET_QUICKADD_PRODUCTS_TASKCODE) {
			if (quickaddProductAdapter != null) {
				quickaddProductAdapter.clear();
				quickaddProductAdapter.notifyDataSetChanged();
			}
		} else {
			CustomToast.showCustomToast(getActivity().getApplicationContext(),
					errorMessage, Toast.LENGTH_LONG, CustomToast.ERROR);
		}
	}

	@Override
	public void onNumKeyPadEnter(String value, int context) {
		if (context == EDIT_TOTALDISCOUNT_CONTEXT) {
			if (shoppingCart != null) {
				shoppingCart.setTotalDiscount(Float.parseFloat(value));
			}
			if (numKeypadFragment != null && numKeypadFragment.isAdded()) {
				getFragmentManager().popBackStack();
			}
			updateBillTextViews(getActivity());
		} else if (context == ENTER_CASH_CONTEXT) {
			if (shoppingCart != null) {

				shoppingCart.setCreditChanged(true);
				shoppingCart.setCashPaid(Float.parseFloat(value));
			}
			if (numKeypadFragment != null && numKeypadFragment.isAdded()) {
				getFragmentManager().popBackStack();
			}
			updateBillTextViews(getActivity());
		}  else if (context == SnapToolkitConstants.EDIT_DISCOUNT_ON_ITEM_CONTEXT) {
			if (shoppingCart != null) {
				shoppingCart.editSellingPrice(this.position, Float.valueOf(value));
     		}
			if (numKeypadFragment != null && numKeypadFragment.isAdded()) {
				getFragmentManager().popBackStack();
			}
        	billListAdapter.notifyDataSetChanged();
     		updateBillTextViews(getActivity());
		
		} else if (context == QUICKADD_WEIGHT_CONTEXT) {

			if (quickaddUnitType.ordinal() == SkuUnitType.PC.ordinal()) {
				int inputValue = (int) Float.parseFloat(value);
				if (inputValue == 0) {
					CustomToast.showCustomToast(getActivity(),
							getString(R.string.msg_greatervalue),
							Toast.LENGTH_SHORT, CustomToast.INFORMATION);
					return;
				}
				if (shoppingCart != null) {
					shoppingCart.addItemToCart(quickaddProduct, inputValue,
							quickaddUnitType);
				}
				getView().findViewById(R.id.weight_framelayout).setVisibility(
						View.GONE);
				showProductsLayout();
				billListAdapter.notifyDataSetChanged();
				updateBillTextViews(getActivity());
			} else {
				if ((Float.parseFloat(value)) == 0) {
					CustomToast.showCustomToast(getActivity(),
					        getString(R.string.msg_greatervalue),
							Toast.LENGTH_SHORT, CustomToast.INFORMATION);
					return;
				}
				if (shoppingCart != null) {
					shoppingCart.addItemToCart(quickaddProduct,
							Float.parseFloat(value), quickaddUnitType);
				}
				getView().findViewById(R.id.weight_framelayout).setVisibility(
						View.GONE);
				showProductsLayout();
				billListAdapter.notifyDataSetChanged();
				updateBillTextViews(getActivity());

			}

		} else if (context == BARCODE_SEARCH_CONTEXT) {

			barcode_product_name_search_edittext.setText(value);
			searchProductSkuTask = new SearchProductSkuTask(getActivity()
					.getApplicationContext(), BillCheckoutFragment.this,
					SEARCH_BARCODE_PRODUCTS_TASKCODE, shoppingCart.getShoppingCartId());
			searchProductSkuTask.execute(value);
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			if (barcodeKeypadFragment != null) {
				ft.hide(barcodeKeypadFragment);
				overLay.setVisibility(View.GONE);
			}
		}
	}
	View.OnClickListener onCustomerPaymentListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			updateBillTextViews(getActivity());
		}
	};

	OnFocusChangeListener onSearchFocusChangeListener = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View arg0, boolean hasFocus) {
			if (hasFocus) {
				if (barcodeKeypadFragment != null
						&& barcodeKeypadFragment.isAdded()) {

					getFragmentManager().popBackStack();
				}
			}

		}
	};

	@Override
	public void onKeyBoardDetach() {
		if (billListAdapter != null) {
			billListAdapter.setLastSelectedPosition(-1);
			billListAdapter.notifyDataSetChanged();
			if (lastClickedQuickAddView != null) {
				lastClickedQuickAddView.setEnabled(true);
				lastClickedQuickAddView.setSelected(false);
				lastClickedQuickAddView = null;
			}
		}
	}

	@Override
	public void onAltMrpSelected(int position) {
		if (shoppingCart != null) {
			shoppingCart.setAltMrp(position);
		}
		updateBillTextViews(getActivity());
	}
	
	@Override
	public void onAltRateSelected(final int position, View v) {
		if (keypadFragment != null)
			keypadFragment.closeKeyPad();
		if ((keypadFragment != null && keypadFragment.isAdded()) || (numKeypadFragment != null && numKeypadFragment.isAdded()))
			onActionbarNavigationListener.onActionbarNavigation("", android.R.id.home);
		this.position = position;
		final BillItem billItem = shoppingCart.getCartItems().valueAt(position);
		PopupMenu popup = new PopupMenu(getActivity(), v);
		String prefixes[] = { getResources().getString(R.string.mrp),
							  getResources().getString(R.string.sp1),
							  getResources().getString(R.string.sp2),
							  getResources().getString(R.string.sp3)
		};
		for(int i = 0; i < prefixes.length; i++)
			popup.getMenu().add(0, i, 0, prefixes[i] + " - " + (billItem.sps[i] / 100.0));
    	popup.getMenu().add(0, SnapBillingConstants.DISCOUNT_MENU_ID, 0, getResources().getString(R.string.discount));
    	popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {  
             public boolean onMenuItemClick(MenuItem item) {
            	 if(shoppingCart == null)
            		 return false;
            	 if(item.getItemId() <= 3) {
            		 shoppingCart.updateBillItemPrices(position, item.getItemId());
            	 } else {
            		 float sp = getAltSalesValue(billItem, item.getItemId());
            		 if(sp != -1)
            			 shoppingCart.editSellingPrice(position, sp);
            	 }
	        	 if (item.getItemId() != SnapBillingConstants.DISCOUNT_MENU_ID) {
	        		 billListAdapter.notifyDataSetChanged();
	        		 updateBillTextViews(getActivity());
	        	 }
	        	 return true;
             }  
        });  
    	popup.show();
	}

	public void setProductQuickAddCategories(
			ArrayList<ProductCategory> quickAddCategories) {
		if (quickaddCategoryAdapter != null) {
			quickaddCategoryAdapter.clear();
			quickaddCategoryAdapter.addAll(quickAddCategories);
			quickaddCategoryAdapter.notifyDataSetChanged();
		} else {
			this.quickAddCategories = quickAddCategories;
		}
	}

	public void resetAdapter() {
		if (getView() != null) {
			// TODO: Check this
			//billListAdapter.notifyDataSetChanged();
			billListView = ((ListView) getView().findViewById(R.id.bill_listview));
			billListView.setAdapter(null);
			billListView.setAdapter(billListAdapter);
		}
	}

	public static void showSoftKeyboard(Context context, View view) {
		if (context == null)
			return;
		InputMethodManager inputMethodManager = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (inputMethodManager != null)
			inputMethodManager.showSoftInput(view, 0);
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			getFragmentManager().popBackStack();
		}
		return true;
	}

	private boolean processOnlineOrder(boolean bDelivery) {
		return false;
	}

	private float getAltSalesValue(BillItem billItem, int menuItem) {
		switch(menuItem) {
		case 1:
		case 2:
		case 3:
		case 4:
			return billItem.sps[menuItem - 1];
		case 5:
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			if (numKeypadFragment == null) {
				numKeypadFragment = new NumKeypadFragment();
				numKeypadFragment.setKeypadEnterListener(BillCheckoutFragment.this);
			}
			ft.replace(R.id.overlay_framelayout, numKeypadFragment);
			if (!numKeypadFragment.isAdded()) {
				ft.addToBackStack(null);
			}
			ft.commit();
			numKeypadFragment.setContext(SnapToolkitConstants.EDIT_DISCOUNT_ON_ITEM_CONTEXT);
			numKeypadFragment.setIsDiscount(true);
			numKeypadFragment.setNewEdit(true);
			numKeypadFragment.setValue(String.valueOf((billItem.mrp - billItem.sellingPrice) / 100.0));
			numKeypadFragment.setTotalValue(billItem.mrp / 100.0);
			getView().findViewById(R.id.overlay_framelayout).setVisibility(View.VISIBLE);
			RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			rlParams.setMargins(getResources().getDimensionPixelOffset(R.dimen.billlist_width) * 2 + 40, 50, 0, 0);
			getActivity().findViewById(R.id.overlay_framelayout).setLayoutParams(rlParams);
			deleteReturnLayout.setVisibility(View.GONE);
		}
		return -1;
	}
	
	private double getChangeReturned(double extraPaid) {
		if (extraPaid < 0)
			return Math.abs(extraPaid / 100.0);
		return 0;
	}
	
	private double getCreditAmount(double extraPaid) {
		if (extraPaid > 0)
			return Math.abs(extraPaid / 100.0);
		return 0;
	}

	private class SaveInvoice extends AsyncTask<Void, String, String> {
		boolean bPrint = false;		// TODO: Handle this
		boolean bDelivery = false;
		boolean isReceive = false;	// TODO: Handle this, probably separate the task
		ShoppingCart shoppingCart = null;
		ProgressDialog progressDialog = null;
		String billerName = null;
		String posName = "Default";
		String referenceNumber = null;
		PaymentMode paymentMode = PaymentMode.CASH;

		public SaveInvoice(ShoppingCart shoppingCart, boolean bDelivery, boolean bPrint) {
			this(shoppingCart, bDelivery, bPrint, false);
		}
		
		public SaveInvoice(ShoppingCart shoppingCart, boolean bDelivery, boolean bPrint, boolean isReceive) {
			this.bPrint = bPrint;
			this.shoppingCart = shoppingCart;
			this.bDelivery = bDelivery;
			this.isReceive = isReceive;
		}

		// TODO: Use this
		public void setBillerInfo(String billerName, String posName) {
			this.billerName = billerName;
			this.posName = posName;
		}
		
		// TODO: Use this
		public void setTransactionInfo(PaymentMode paymentMode, String referenceNumber) {
			this.referenceNumber = referenceNumber;
			this.paymentMode = paymentMode;
		}

		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(getActivity());
	        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	        progressDialog.setCancelable(false);
	        progressDialog.setCanceledOnTouchOutside(false);
	        progressDialog.show();
		}
		
		void createInvoiceAndTransactions() {
			Long customerPhone = shoppingCart.getCustomer() != null ? shoppingCart.getCustomer().getPhone() : null;
			long invoiceId = snapbizzDB.invoiceHelper.createInvoice(customerPhone,
										   		   					shoppingCart.isInvoice(),
										   		   					shoppingCart.totalCartValue,
										   		   					shoppingCart.creditValue(),
										   		   					shoppingCart.totalDiscount,
										   		   					shoppingCart.totalSavings,
										   		   					null, // memo id -> used when converting to invoice
										   		   					shoppingCart.isCredit(),
										   		   					shoppingCart.getTotalQty(),
										   		   					shoppingCart.getTotalLineItems(),
										   		   					shoppingCart.totalVatAmount,
										   		   					bDelivery,
										   		   					billerName,
										   		   					posName,
										   		   					shoppingCart.getBillingStartedAt());
			createInvoiceItems(invoiceId);

			publishProgress(getActivity().getResources().getString(R.string.progress_create_transactions));
			createTransactions(invoiceId, customerPhone);
		}

		void createInvoiceItems(long invoiceId) {
			snapbizzDB.invoiceHelper.insertBillItems(invoiceId, shoppingCart.getCartItems());
		}

		void createTransactions(Long invoiceId, Long customerPhone) {
			int walletBalance = customerPhone != null ? snapbizzDB.invoiceHelper.getWalletBalance(customerPhone) : 0;

			// Create Transactions
			// Case 1: when cash payment is zero => no transaction
			// Case 2: when cash payment is eq/less than bill-net => one transaction
			// Case 3: when cash payment is greater than bill-net => one transaction for current plus
			//         case 3.0 => if change return, nothing to do
			//         case 3.1 => one / multiple previous payments
			//         case 3.2 => optionally advance payment
			// Case 4: when credit bill, check if we can pay by wallet/advance 


			// Case 2/3 Create a transaction for the cashPaid
			int amount = (shoppingCart.netValue() > shoppingCart.cashPaid) ? shoppingCart.cashPaid : shoppingCart.netValue();
			int remainingAmount = (shoppingCart.netValue() < shoppingCart.cashPaid) ? shoppingCart.cashPaid - shoppingCart.netValue() : 0;
			if(amount > 0) {
				Transactions transaction = snapbizzDB.invoiceHelper.createTransaction(
													invoiceId,
													amount,
													0, // remainingAmount,
													customerPhone,
													PaymentType.CURRENT,
													paymentMode,
													referenceNumber,
													null); // Long parentTransactionId
			
				if(transaction == null)	// creation failed ?
					return;

				// Case 3
				if(customerPhone != null && shoppingCart.isCustomerPayment() && remainingAmount > 0) {
					// Pay previous
					remainingAmount = snapbizzDB.invoiceHelper.processCreditPayment(customerPhone,
																					remainingAmount,
																					paymentMode,
																					referenceNumber,
																					null);
					// If more remaining, consider advance payment
					if(remainingAmount > 0) {
						snapbizzDB.invoiceHelper.createTransaction(
													null,
													remainingAmount,
													remainingAmount,
													shoppingCart.getCustomer().getPhone(),
													PaymentType.ADVANCE,
													paymentMode,
													referenceNumber,
													null); // Long parentTransactionId
					}
				}
			}
			
			// When credit bill, check if we can pay from wallet/advance
			if(customerPhone != null && shoppingCart.isCredit() && walletBalance > 0)
				snapbizzDB.invoiceHelper.payFromWallet(invoiceId, customerPhone, PaymentType.CURRENT);
		}

		void updateInventory() {
			
		}

		void updateEditedItems() {
			
		}

		@Override
		protected String doInBackground(Void... params) {
			publishProgress(getActivity().getResources().getString(R.string.progress_create_invoice));
			createInvoiceAndTransactions();

			publishProgress(getActivity().getResources().getString(R.string.progress_update_inventory));
			updateInventory();
			updateEditedItems();
			
			if(shoppingCart.getCustomer() != null) {
				publishProgress(getActivity().getResources().getString(R.string.progress_update_customer));
				// TODO: Update Customer related data
				// updateCustomerDetails(); // Check this
			}
			if(bPrint) {
				publishProgress(getActivity().getResources().getString(R.string.progress_printing));
				// TODO: Print
			}
			return "Invoice/Memo Created";
		}
		
		@Override
		protected void onProgressUpdate(String... message) {
			if(message != null && message[0] != null)
				progressDialog.setMessage(message[0]);
		}

		@Override
		protected void onPostExecute(String status) {
			if(status != null && !status.isEmpty())
				Toast.makeText(getActivity(), status, Toast.LENGTH_LONG).show();
			progressDialog.dismiss();
		}
	}
}
