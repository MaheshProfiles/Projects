package com.snapbizz.snapbilling.fragments;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.adapters.InventoryGDBProductAdapter;
import com.snapbizz.snapbilling.adapters.InventoryGDBProductAdapter.OnGdbProductSelectListener;
import com.snapbizz.snapbilling.adapters.InventoryProductAdapter;
import com.snapbizz.snapbilling.adapters.InventoryProductAdapter.OnInventoryActionListener;
import com.snapbizz.snapbilling.adapters.ProductCategoriesAdapter;
import com.snapbizz.snapbilling.adapters.ProductCategoriesAdapter.OnCategoryActionListener;
import com.snapbizz.snapbilling.adapters.SubCategoriesGridAdapter;
import com.snapbizz.snapbilling.adapters.SubCategoriesGridAdapter.OnSubCategoryActionListener;
import com.snapbizz.snapbilling.domainsV2.InventoryDetails;
import com.snapbizz.snapbilling.interfaces.OnAddNewProductListener;
import com.snapbizz.snapbilling.utils.SnapBillingConstants;
import com.snapbizz.snapbilling.utils.SnapBillingUtils;
import com.snapbizz.snaptoolkit.db.GlobalDB;
import com.snapbizz.snaptoolkit.db.SnapbizzDB;
import com.snapbizz.snaptoolkit.db.dao.Inventory;
import com.snapbizz.snaptoolkit.db.dao.ProductPacks;
import com.snapbizz.snaptoolkit.db.dao.Products;
import com.snapbizz.snaptoolkit.gdb.dao.ProductCategories;
import com.snapbizz.snaptoolkit.interfaces.OnActionbarNavigationListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
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
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class InventoryFragment extends Fragment implements OnGdbProductSelectListener, OnInventoryActionListener, OnCategoryActionListener, OnSubCategoryActionListener {

	private OnAddNewProductListener onAddNewProductListener;
	private OnActionbarNavigationListener onActionbarNavigationListener;
	private CheckBox checkboxBarcode;
	private CheckBox checkboxDiscription;
	private EditText searchEdittext;
	private InventoryProductAdapter inventoryProductAdapter;
	private InventoryGDBProductAdapter inventoryGDBProductAdapter;
	private RelativeLayout myStoreTab;
	private RelativeLayout gdbTab;
	private RelativeLayout categoriesTab;
	private HorizontalScrollView mystoreView;
	private HorizontalScrollView gdbView;
	private GridView subcategoryGrid;
	private ImageView newProdAdd;
	private Long deleteSku;
	private RelativeLayout showColumnsLayout;
	private ImageView mrpSelector;
	private ImageView purchasePriceSelector;
	private ImageView qtySelector;
	private ImageView vatRateSelector;
	private ImageView unitTypeSelector;
	private ImageView categorySelector;
	private ImageView subcategorySelector;
	private ImageView actionsSelector;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.inventory_update, container, false);
		((ListView) view.findViewById(R.id.inventory_list)).addHeaderView(inflater.inflate(R.layout.header_inventory, null));
		((ListView) view.findViewById(R.id.gdb_list)).addHeaderView(inflater.inflate(R.layout.header_inventory_gdb, null));
		mystoreView = (HorizontalScrollView) view.findViewById(R.id.mystore_view);
		gdbView = (HorizontalScrollView) view.findViewById(R.id.gdb_view);
		subcategoryGrid = (GridView) view.findViewById(R.id.subcategory_grid);
		return view;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			onActionbarNavigationListener = (OnActionbarNavigationListener) activity;
		} catch (Exception e) {
			throw new ClassCastException("activity " + activity.toString() + getString(R.string.exc_implementnavigation));
		}
		try {
			onAddNewProductListener = (OnAddNewProductListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnAddNewProductListener");
		}
	}
	
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
	    ActionBar actionBar = getActivity().getActionBar();
	    if (inventoryProductAdapter != null)
			((ListView) getView().findViewById(R.id.inventory_list)).setAdapter(inventoryProductAdapter);
	    if (inventoryGDBProductAdapter != null)
			((ListView) getView().findViewById(R.id.gdb_list)).setAdapter(inventoryGDBProductAdapter);
	    if (!actionBar.isShowing())
            actionBar.show();
        setHasOptionsMenu(true);
        actionBar.setCustomView(R.layout.inventory_header);
	    ((Button) getView().findViewById(R.id.inventory_prod_add_button)).setOnClickListener(onInventoryClickListner);
	    ((Button) getView().findViewById(R.id.button_search)).setOnClickListener(onSearchClickListner);
	    checkboxBarcode = (CheckBox)getView().findViewById(R.id.checkbox_barcode);
	    checkboxDiscription = (CheckBox)getView().findViewById(R.id.checkbox_discription);
	    myStoreTab = (RelativeLayout) getActivity().findViewById(R.id.my_store_layout);
		gdbTab = (RelativeLayout) getActivity().findViewById(R.id.gdb_tab_layout);
		categoriesTab = (RelativeLayout) getActivity().findViewById(R.id.categories_tab_layout);
		searchEdittext = (EditText)getActivity().findViewById(R.id.search_edittext);
		newProdAdd = (ImageView) getView().findViewById(R.id.new_prod_add);
		newProdAdd.setOnClickListener(onFooterClickListener);
		((ImageView) getView().findViewById(R.id.screen_back)).setOnClickListener(onFooterClickListener);
		searchEdittext.addTextChangedListener(productSearchTextWatcher);
		myStoreTab.setOnClickListener(onMyStoreTabClickListener);
		gdbTab.setOnClickListener(onGdbTabClickListener);
		categoriesTab.setOnClickListener(onCategoriesTabClickListener);
		showColumnsLayout = (RelativeLayout) getView().findViewById(R.id.show_columns_layout);
		((Button) getView().findViewById(R.id.selector_save)).setOnClickListener(selectorSaveClickListener);
		((ImageView) getView().findViewById(R.id.icon_show_columns)).setOnClickListener(showColumnsClickListener);
		((ImageView) getView().findViewById(R.id.btn_show_category)).setOnClickListener(overlayClickListener);
		((ImageView) getView().findViewById(R.id.btn_hide_category)).setOnClickListener(overlayClickListener);
		loadMyStoreTab();
		loadCategoryOverlay();
		setColumnsDisplayStatus();
	}
	
	OnClickListener selectorSaveClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			SnapSharedUtils.setMRPHeader( getActivity(), mrpSelector.isSelected());
			SnapSharedUtils.setPurchasePriceHeader( getActivity(), purchasePriceSelector.isSelected());
			SnapSharedUtils.setQtyHeader( getActivity(), qtySelector.isSelected());
			SnapSharedUtils.setVatRateHeader( getActivity(), vatRateSelector.isSelected());
			SnapSharedUtils.setUnitTypeHeader( getActivity(), unitTypeSelector.isSelected());
			SnapSharedUtils.setCategoryHeader( getActivity(), categorySelector.isSelected());
			SnapSharedUtils.setSubCategoryHeader( getActivity(), subcategorySelector.isSelected());
			SnapSharedUtils.setActionsHeader( getActivity(), actionsSelector.isSelected());
			showColumnsLayout.setVisibility(View.GONE);
			setColumnsDisplayStatus();
			loadMyStoreTab();
		}
	};
	
	OnClickListener showColumnsClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (showColumnsLayout.getVisibility() == View.GONE) 
				showColumnsLayout.setVisibility(View.VISIBLE);
			 else 
				showColumnsLayout.setVisibility(View.GONE);
		}
	};
	
	OnClickListener overlayClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.btn_show_category) {
				((ImageView) getView().findViewById(R.id.btn_show_category)).setVisibility(View.GONE);
				((RelativeLayout) getView().findViewById(R.id.category_overlay)).setVisibility(View.VISIBLE);
			} else {
				((ImageView) getView().findViewById(R.id.btn_show_category)).setVisibility(View.VISIBLE);
				((RelativeLayout) getView().findViewById(R.id.category_overlay)).setVisibility(View.GONE);
			}
		}
	};
	
	OnClickListener onMyStoreTabClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			loadMyStoreTab();
		}
	};
	
	OnClickListener onFooterClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.new_prod_add) 
				onEditProduct(null);
			else if (v.getId() == R.id.screen_back) 
				onActionbarNavigationListener.onActionbarNavigation("", android.R.id.home);
		}
	};
	
	OnClickListener onGdbTabClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Log.d("TAG", "onGdbTabClickListener onClick---> ");
			enableGDBTab();
			List<InventoryDetails> productDetails = getGDBProducts();
			Log.d("TAG", "onGdbTabClickListener button click productDetails---> "+productDetails.size());
			if (inventoryGDBProductAdapter == null && productDetails != null) {
				inventoryGDBProductAdapter = new InventoryGDBProductAdapter(getActivity(), productDetails, InventoryFragment.this);
				if (getView() != null)
					((ListView) getView().findViewById(R.id.gdb_list)).setAdapter(inventoryGDBProductAdapter);
			} else {
				if (inventoryGDBProductAdapter != null && productDetails != null)
					inventoryGDBProductAdapter.notifyDataSetChanged();
			}
		}
	};
	
	OnClickListener onCategoriesTabClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			categoriesTab.setSelected(true);	
			gdbTab.setSelected(false);
			myStoreTab.setSelected(false);
			mystoreView.setVisibility(View.GONE);
			gdbView.setVisibility(View.GONE);
		}
	};
	
	OnClickListener onSearchClickListner = new OnClickListener() {
		@Override
		public void onClick(View v) {
			String searchInput = searchEdittext.getText().toString();
			Log.d("TAG", "onActivityCreated button click searchInput---> "+searchInput);
			if (searchInput != null) {
				List<InventoryDetails> productDetails = searchProducts(searchInput, checkboxBarcode.isChecked(), checkboxDiscription.isChecked());
				if (gdbTab.isSelected()) {
					inventoryGDBProductAdapter = new InventoryGDBProductAdapter(getActivity(), productDetails, InventoryFragment.this);
					if (getView() != null)
						((ListView) getView().findViewById(R.id.gdb_list)).setAdapter(inventoryGDBProductAdapter);
				} else if (myStoreTab.isSelected()) {
					inventoryProductAdapter = new InventoryProductAdapter(getActivity(), productDetails, InventoryFragment.this);
					if (getView() != null)
						((ListView) getView().findViewById(R.id.inventory_list)).setAdapter(inventoryProductAdapter);
			  }
			}
		}
	};
	
	TextWatcher productSearchTextWatcher = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable s) {	
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if (searchEdittext.getText().toString().length() > 0) {	
				productSearchKeyStrokeTimer.start();
			} 
		}
	};
	
	CountDownTimer productSearchKeyStrokeTimer = new CountDownTimer(SnapBillingConstants.KEY_STROKE_TIMEOUT, 
																				SnapBillingConstants.KEY_STROKE_TIMEOUT) {
		@Override
		public void onTick(long arg0) {
		}

		@Override
		public void onFinish() {
			String searchInput = searchEdittext.getText().toString();
			Log.d("TAG", "onActivityCreated button click searchInput---> "+searchInput);
			if (searchInput != null) {
				List<InventoryDetails> productDetails = searchProducts(searchInput, checkboxBarcode.isChecked(), checkboxDiscription.isChecked());
				if (gdbTab.isSelected()) {
					inventoryGDBProductAdapter = new InventoryGDBProductAdapter(getActivity(), productDetails, InventoryFragment.this);
					if (getView() != null)
						((ListView) getView().findViewById(R.id.gdb_list)).setAdapter(inventoryGDBProductAdapter);
				} else if (myStoreTab.isSelected()) {
					inventoryProductAdapter = new InventoryProductAdapter(getActivity(), productDetails, InventoryFragment.this);
					if (getView() != null)
						((ListView) getView().findViewById(R.id.inventory_list)).setAdapter(inventoryProductAdapter);
			  }
			}
		}
	};
	
	OnClickListener onInventoryClickListner = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Log.d("TAG", "onActivityCreated button click");
			onAddNewProductListener.showAddProductLayout("");
		}
	};
	
	private void loadMyStoreTab(){
		enableMyStoreTab();
		List<InventoryDetails> productDetails = getProductDetails() ;
		if (productDetails != null) {
			inventoryProductAdapter = new InventoryProductAdapter(getActivity(), productDetails, InventoryFragment.this);
			if (getView() != null)
				((ListView) getView().findViewById(R.id.inventory_list)).setAdapter(inventoryProductAdapter);
		} 
	}
	
	private void loadCategoryOverlay() {
		GlobalDB globalDB = GlobalDB.getInstance(getActivity(), true);
		final List<ProductCategories> categoriesList = globalDB.getAllCategory();
		if (categoriesList != null) {
			ProductCategoriesAdapter productCategoriesAdapter = new ProductCategoriesAdapter(getActivity(), categoriesList, R.layout.listitem_category_overlay, InventoryFragment.this);
			((ListView) getView().findViewById(R.id.list_category)).setAdapter(productCategoriesAdapter);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		onActionbarNavigationListener.onActionbarNavigation("", menuItem.getItemId());
		return true;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.findItem(R.id.inventory_update).setVisible(false);
	}
	
	private List<InventoryDetails> getGDBProducts() {
		GlobalDB globalDB = GlobalDB.getInstance(getActivity(), true);
		return addGDBDetailsToInventory(globalDB.getAllProducts());
	}
	
	private List<InventoryDetails> getProductDetails() {
		GlobalDB globalDB = GlobalDB.getInstance(getActivity(), true);
		List<com.snapbizz.snaptoolkit.gdb.dao.Products> inventoryProducts = null ;
		List<Long> groupIdList = getInventory();
		if (groupIdList != null && !groupIdList.isEmpty())
			inventoryProducts = globalDB.getProductsByBarcodes(groupIdList);
		if (inventoryProducts != null)
			return addGDBDetailsToInventory(inventoryProducts);
		return null;
	}
	private List<InventoryDetails> getProductDetails(Long productCode) {
		GlobalDB globalDB = GlobalDB.getInstance(getActivity(), true);
		List<com.snapbizz.snaptoolkit.gdb.dao.Products> inventoryProducts = null ;
		List<Long> groupIdList = getInventory(productCode);
		if (groupIdList != null && !groupIdList.isEmpty())
			inventoryProducts = globalDB.getProductsByBarcodes(groupIdList);
		if (inventoryProducts != null)
			return addGDBDetailsToInventory(inventoryProducts);
		return null;
	}
	
	private List<InventoryDetails> searchProducts(String productName, boolean barCode, boolean discription) {
		SnapbizzDB snapbizzDB = SnapbizzDB.getInstance(getActivity().getApplicationContext(), false);
		List<Products> products = snapbizzDB.searchProducts(productName, barCode, discription);
		Log.d("TAG", "onActivityCreated button click products ldb---> "+products);
		if (products == null || products.isEmpty()) {
			GlobalDB globalDB = GlobalDB.getInstance(getActivity(), true);
			List<com.snapbizz.snaptoolkit.gdb.dao.Products> gdbProducts = globalDB.searchProducts(productName, barCode, discription);
			Log.d("TAG", "onActivityCreated button click gdbProducts ---> "+gdbProducts);
			if (gdbProducts != null && !gdbProducts.isEmpty()) 
				return addGDBDetailsToInventory(gdbProducts);
		} else if (myStoreTab.isSelected()) {
			if (products == null || products.isEmpty()) {
				GlobalDB globalDB = GlobalDB.getInstance(getActivity(), true);
				List<com.snapbizz.snaptoolkit.gdb.dao.Products> gdbProducts = globalDB.searchProducts(productName, barCode, discription);
				List<Long> gidList = getProductGids(gdbProducts);
				Log.d("TAG", "searchProducts button click gidList ---> "+gidList.size());
				if (gidList != null && !gidList.isEmpty()) {
					List<Inventory> inventoryList = snapbizzDB.inventoryHelper.getInventoryByPIds(gidList);
					Log.d("TAG", "searchProducts button click inventoryList ---> "+inventoryList.size());
					List<Long> productCodeList = getProductCodes(inventoryList);
					Log.d("TAG", "searchProducts button click productCodeList ---> "+productCodeList.size());
					if (productCodeList != null && !productCodeList.isEmpty()) {
						gdbProducts = globalDB.getProductsByBarcodes(productCodeList);
					}
				}
				Log.d("TAG", "onActivityCreated button click gdbProducts ---> "+gdbProducts);
				if (gdbProducts != null && !gdbProducts.isEmpty()) 
					return addGDBDetailsToInventory(gdbProducts);
			} else {
				return addLDBDetailsToInventory(products);
			}
		}
		return null;
	}
	
	private List<Long> getInventory() {
		SnapbizzDB snapbizzDB = SnapbizzDB.getInstance(getActivity().getApplicationContext(), false);
		return snapbizzDB.getInventory();
	}
	
	private List<Long> getInventory(Long productCode) {
		SnapbizzDB snapbizzDB = SnapbizzDB.getInstance(getActivity().getApplicationContext(), false);
		return snapbizzDB.getInventory();
	}
	
	private List<Long> getProductGids(List<com.snapbizz.snaptoolkit.gdb.dao.Products> gdbProducts) {
		ArrayList<Long> result = new ArrayList<Long>();
		if (gdbProducts != null && !gdbProducts.isEmpty()) {
			for (com.snapbizz.snaptoolkit.gdb.dao.Products products  : gdbProducts) 
				result.add(products.getProductGid());
		}
		if (result != null && result.size() > 0)
			return result;
		else
			return null;
	}
	
	private List<Long> getProductCodes(List<Inventory> inventoryList) {
		ArrayList<Long> result = new ArrayList<Long>();
		if (inventoryList != null && !inventoryList.isEmpty()) {
			for (Inventory inventory  : inventoryList)
				result.add(inventory.getProductCode());
		}
		if (result != null && result.size() > 0)
			return result;
		else
			return null;
	}
	
	private List<InventoryDetails> addGDBDetailsToInventory(List<com.snapbizz.snaptoolkit.gdb.dao.Products> productsList) {
		GlobalDB globalDB = GlobalDB.getInstance(getActivity(), true);
		List<InventoryDetails> listInventoryDetails = new ArrayList<InventoryDetails>();
		SnapbizzDB snapbizzDB = SnapbizzDB.getInstance(getActivity(), true);
		for (int i = 0; productsList != null && i < productsList.size(); i++) {
			InventoryDetails inventoryDetails = new InventoryDetails();
			com.snapbizz.snaptoolkit.gdb.dao.Products products = productsList.get(i);
			inventoryDetails.setProductSkuCode(products.getBarcode());
			inventoryDetails.setProductGid(products.getProductGid());
			inventoryDetails.setProductSkuName(products.getName());
			inventoryDetails.setProductSkuMrp(products.getMrp());
			inventoryDetails.setLocalName(products.getLocalName());
			ProductCategories productCategories = globalDB.getCategory((long) products.getCategoryId());
			inventoryDetails.setProductSubCategoryName(productCategories.getName());
			inventoryDetails.setProductCategoryName(globalDB.getCategory((long)productCategories.getParentId()).getName());
			inventoryDetails.setVAT(globalDB.getVatRate(products.getVatId()));
			inventoryDetails.setUom(products.getUom());
			inventoryDetails.setImageUrl(products.getImage());
			if (myStoreTab.isSelected()) {
				List<ProductPacks> productPacksList =new ArrayList<ProductPacks>();
				// TODO: Check this -> seems like it is not handling gid/barcode/localproducts
				productPacksList = snapbizzDB.packsHelper.getProductPacks(inventoryDetails.getProductSkuCode());
				inventoryDetails.setProductPacksList(productPacksList);
				Log.d("TAG", "onActivityCreated productPacksList ---> "+productPacksList.size());
			}
			listInventoryDetails.add(inventoryDetails);
		}
		Log.d("TAG", "onActivityCreated button click gdbProducts ---> "+listInventoryDetails.size());
		return listInventoryDetails;
	}
	
	private List<InventoryDetails> addLDBDetailsToInventory(List<Products> productsList) {
		List<InventoryDetails> listInventoryDetails = new ArrayList<InventoryDetails>();
		SnapbizzDB snapbizzDB = SnapbizzDB.getInstance(getActivity(), true);
		for (int i = 0; productsList != null && i < productsList.size(); i++) {
			InventoryDetails inventoryDetails = new InventoryDetails();
			Products products = productsList.get(i);
			inventoryDetails.setProductSkuCode(products.getProductCode());
			inventoryDetails.setProductSkuName(products.getName());
			inventoryDetails.setProductSkuMrp(products.getMrp());
			inventoryDetails.setProductSubCategoryName(SnapBillingConstants.CATEGORY_MISCELLANEOUS);
			inventoryDetails.setProductCategoryName(SnapBillingConstants.CATEGORY_OTHERS);
			inventoryDetails.setVAT(products.getVatRate());
			inventoryDetails.setUom(products.getUom());
			inventoryDetails.setImageUrl(products.getImage());
			if (myStoreTab.isSelected()) {
				List<ProductPacks> productPacksList = new ArrayList<ProductPacks>();
				productPacksList = snapbizzDB.packsHelper.getProductPacks(inventoryDetails.getProductSkuCode());
				inventoryDetails.setProductPacksList(productPacksList);
				Log.d("TAG", "onActivityCreated productPacksList ---> "+productPacksList.size());
			}
			listInventoryDetails.add(inventoryDetails);
		}
		return listInventoryDetails;
	}


	@Override
	public void onProductSelectedToInventory(InventoryDetails itemDetails) {
		Log.d("TAG", "onActivityCreated button click productCode ---> "+itemDetails.getProductSkuCode());
		Inventory inventory = new Inventory();
		inventory.setProductCode(itemDetails.getProductSkuCode());
		inventory.setQuantity(1);
		inventory.setIsDeleted(false);
		inventory.setUpdatedAt(new Date());
		inventory.setCreatedAt(new Date());
		SnapbizzDB snapbizzDB = SnapbizzDB.getInstance(getActivity(), false);
		snapbizzDB.inventoryHelper.insertOrReplaceInventory(inventory);
		insertProductPacks(itemDetails);
	}
	
	private void insertProductPacks(InventoryDetails itemDetails) {
		ProductPacks productPack = new ProductPacks();
		productPack.setProductCode(itemDetails.getProductSkuCode());
		productPack.setPackSize(1);
		productPack.setSalePrice1(itemDetails.getProductSkuMrp());
		productPack.setSalePrice2(itemDetails.getProductSkuMrp());
		productPack.setSalePrice3(itemDetails.getProductSkuMrp());
		productPack.setIsDefault(true);
		productPack.setUpdatedAt(new Date());
		productPack.setCreatedAt(new Date());
		SnapbizzDB snapbizzDB = SnapbizzDB.getInstance(getActivity(), false);
		snapbizzDB.packsHelper.insertOrReplaceProductPack(productPack);
	}

	@Override
	public void onRemoveItem(Long productSkuCode) {
		inventoryProductAdapter.notifyDataSetChanged();
		SnapCommonUtils.showDeleteAlert(getActivity(), "", "Confirm Delete?", positiveDeleteClickListener, negativeDeleteClickListener, false);
		deleteSku = productSkuCode;
		
	}

	@Override
	public void onEditProduct(Long productSkuCode) {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
        AddEditInventoryFragment addEditInventoryFragment = new AddEditInventoryFragment();
        if (getProductDetails(productSkuCode) != null && getProductDetails(productSkuCode).size() > 0) 
        	 addEditInventoryFragment.setEditProduct(getProductDetails(productSkuCode).get(0));
		ft.replace(R.id.content_framelayout, addEditInventoryFragment, getString(R.string.template_one_tag));
		ft.addToBackStack(addEditInventoryFragment.getTag());
		ft.commit();
	}
	
	@Override
	public void onProdPackValueEdit(ProductPacks selectedProductPack) {
		Log.d("TAG", "v.getText()--2--"+selectedProductPack.getSalePrice1());
		SnapbizzDB snapbizzDB = SnapbizzDB.getInstance(getActivity(), false);
		snapbizzDB.packsHelper.updateProductPack(selectedProductPack);
		loadMyStoreTab();
		InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
	}
	
	OnClickListener positiveDeleteClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (deleteSku != null) {
				SnapbizzDB snapbizzDB = SnapbizzDB.getInstance(getActivity(), false);
				snapbizzDB.packsHelper.deleteProductPack(deleteSku);
				snapbizzDB.inventoryHelper.deleteInventoryByPId(deleteSku);
			}
			deleteSku = null;
			SnapCommonUtils.dismissAlert();
			loadMyStoreTab();
			inventoryProductAdapter.notifyDataSetChanged();
		}
	};

	OnClickListener negativeDeleteClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			deleteSku = null;
			SnapCommonUtils.dismissAlert();
		}
	};

	@Override
	public void onSelectItem(long parentId) {
		GlobalDB globalDB = GlobalDB.getInstance(getActivity(), true);
		List<ProductCategories> subCategories = globalDB.getSubCategory(parentId);
		SubCategoriesGridAdapter subCategoriesGridAdapter = new SubCategoriesGridAdapter(getActivity(), subCategories, InventoryFragment.this);
		subcategoryGrid.setAdapter(subCategoriesGridAdapter);
		enableSubcategoryGrid();
	}

	@Override
	public void onSubCategorySelected(long categoryId) {
		Log.d("TAG", "parentId -------> "+categoryId);
		GlobalDB globalDB = GlobalDB.getInstance(getActivity(), true);
		List<com.snapbizz.snaptoolkit.gdb.dao.Products> productsList = globalDB.getProductBySubCategory(categoryId);
		List<InventoryDetails> productDetails = addGDBDetailsToInventory(productsList);
		if (gdbTab.isSelected()) {
			inventoryGDBProductAdapter = new InventoryGDBProductAdapter(getActivity(), productDetails, InventoryFragment.this);
			if (getView() != null)
				((ListView) getView().findViewById(R.id.gdb_list)).setAdapter(inventoryGDBProductAdapter);
		} else if (myStoreTab.isSelected()) {
			inventoryProductAdapter = new InventoryProductAdapter(getActivity(), productDetails, InventoryFragment.this);
			if (getView() != null)
				((ListView) getView().findViewById(R.id.inventory_list)).setAdapter(inventoryProductAdapter);
			enableMyStoreTab();
	  }
	}
	
	private void enableMyStoreTab() {
		myStoreTab.setSelected(true);
		gdbTab.setSelected(false);
		categoriesTab.setSelected(false);
		mystoreView.setVisibility(View.VISIBLE);
		newProdAdd.setVisibility(View.VISIBLE);
		gdbView.setVisibility(View.GONE);
		subcategoryGrid.setVisibility(View.GONE);
	}
	
	private void enableSubcategoryGrid() {
		mystoreView.setVisibility(View.GONE);
		newProdAdd.setVisibility(View.GONE);
		gdbView.setVisibility(View.GONE);
		subcategoryGrid.setVisibility(View.VISIBLE);
	}
	
	private void enableGDBTab() {
		gdbTab.setSelected(true);
		myStoreTab.setSelected(false);
		categoriesTab.setSelected(false);
		mystoreView.setVisibility(View.GONE);
		gdbView.setVisibility(View.VISIBLE);
		newProdAdd.setVisibility(View.GONE);
		subcategoryGrid.setVisibility(View.GONE);
	}
	
	private void setColumnsDisplayStatus() {
		mrpSelector = (ImageView) getView().findViewById(R.id.mrp_selector);
		purchasePriceSelector = (ImageView) getView().findViewById(R.id.purchase_price_selector);
		qtySelector = (ImageView) getView().findViewById(R.id.qty_selector);
		vatRateSelector = (ImageView) getView().findViewById(R.id.vat_rate_selector);
		unitTypeSelector = (ImageView) getView().findViewById(R.id.unit_type_selector);
		categorySelector = (ImageView) getView().findViewById(R.id.category_selector);
		subcategorySelector = (ImageView) getView().findViewById(R.id.subcategory_selector);
		actionsSelector = (ImageView) getView().findViewById(R.id.actions_selector);
		mrpSelector.setSelected(SnapSharedUtils.isMRPHeader( getActivity()));
		purchasePriceSelector.setSelected(SnapSharedUtils.isPurchasePriceHeader( getActivity()));
		qtySelector.setSelected(SnapSharedUtils.isQtyHeader( getActivity()));
		vatRateSelector.setSelected(SnapSharedUtils.isVatRateHeader( getActivity()));
		unitTypeSelector.setSelected(SnapSharedUtils.isUnitTypeHeader( getActivity()));
		categorySelector.setSelected(SnapSharedUtils.isCategoryHeader( getActivity()));
		subcategorySelector.setSelected(SnapSharedUtils.isSubCategoryHeader( getActivity()));
		actionsSelector.setSelected(SnapSharedUtils.isActionsHeader( getActivity()));	
		mrpSelector.setOnClickListener(headingSelectorClickListner);
		purchasePriceSelector.setOnClickListener(headingSelectorClickListner);
		qtySelector.setOnClickListener(headingSelectorClickListner);
		vatRateSelector.setOnClickListener(headingSelectorClickListner);
		unitTypeSelector.setOnClickListener(headingSelectorClickListner);
		categorySelector.setOnClickListener(headingSelectorClickListner);
		subcategorySelector.setOnClickListener(headingSelectorClickListner);
		actionsSelector.setOnClickListener(headingSelectorClickListner);
		setVisibleStatus(getActivity());
		
	}
	
	private void setVisibleStatus(Context context) {
		((TextView) getView().findViewById(R.id.mrp_header)).setVisibility(SnapBillingUtils.getVisibleStatus
																				(SnapSharedUtils.isMRPHeader(getActivity()), SnapBillingConstants.FIRST_COLUMN, context));
		((TextView) getView().findViewById(R.id.pp_header)).setVisibility(SnapBillingUtils.getVisibleStatus
																				(SnapSharedUtils.isPurchasePriceHeader(getActivity()), SnapBillingConstants.SECOND_COLUMN, context));
		((TextView) getView().findViewById(R.id.qty_header)).setVisibility(SnapBillingUtils.getVisibleStatus
																				(SnapSharedUtils.isQtyHeader(getActivity()), SnapBillingConstants.THIRD_COLUMN, context));
		((TextView) getView().findViewById(R.id.vat_header)).setVisibility(SnapBillingUtils.getVisibleStatus
																				(SnapSharedUtils.isVatRateHeader(getActivity()), SnapBillingConstants.FOURTH_COLUMN, context));
		((TextView) getView().findViewById(R.id.unit_header)).setVisibility(SnapBillingUtils.getVisibleStatus
																				(SnapSharedUtils.isUnitTypeHeader(getActivity()), SnapBillingConstants.FIFTH_COLUMN,context));
		((TextView) getView().findViewById(R.id.category_header)).setVisibility(SnapBillingUtils.getVisibleStatus
																				(SnapSharedUtils.isCategoryHeader(getActivity()), SnapBillingConstants.SIXTH_COLUMN, context));
		((TextView) getView().findViewById(R.id.sub_category_header)).setVisibility(SnapBillingUtils.getVisibleStatus
																				(SnapSharedUtils.isSubCategoryHeader(getActivity()), SnapBillingConstants.SEVENTH_COLUMN, context));
		((TextView) getView().findViewById(R.id.actions_header)).setVisibility(SnapBillingUtils.getVisibleStatus
																				(SnapSharedUtils.isActionsHeader(getActivity()), SnapBillingConstants.EIGHTH_COLUMN, context));
	}
	
	OnClickListener headingSelectorClickListner = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.isSelected())
				v.setSelected(false);
			else 
				v.setSelected(true);
		}
	};
}
