package com.snapbizz.snaptoolkit.fragments;

import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.snapbizz.snaptoolkit.R;
import com.snapbizz.snaptoolkit.R.drawable;
import com.snapbizz.snaptoolkit.asynctasks.CreateNewInventoryProduct;
import com.snapbizz.snaptoolkit.asynctasks.GetStateVatRatesTask;
import com.snapbizz.snaptoolkit.asynctasks.PopulateQuickAddVatRate;
import com.snapbizz.snaptoolkit.asynctasks.QueryProductCategories;
import com.snapbizz.snaptoolkit.asynctasks.QueryProductSubCategories;
import com.snapbizz.snaptoolkit.domains.Brand;
import com.snapbizz.snaptoolkit.domains.Company;
import com.snapbizz.snaptoolkit.domains.Distributor;
import com.snapbizz.snaptoolkit.domains.InventoryBatch;
import com.snapbizz.snaptoolkit.domains.InventorySku;
import com.snapbizz.snaptoolkit.domains.ProductCategory;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.domains.State;
import com.snapbizz.snaptoolkit.domains.VAT;
import com.snapbizz.snaptoolkit.interfaces.OnActionbarNavigationListener;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.CustomToast;
import com.snapbizz.snaptoolkit.utils.SkuUnitType;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;
import com.snapbizz.snaptoolkit.utils.ToolkitV2;

public class AddEditProductFragment extends Fragment implements
		OnQueryCompleteListener {

	private List<ProductCategory> newProductCategoryList;
	private List<ProductCategory> newSubProductCategoryList;
	private ArrayAdapter<ProductCategory> subcategorySpinnerAdapater;
	private Spinner subcategorySpinner;
	private Spinner categorySpinner;
	private final int GET_CATEGORIES_TASKCODE = 1;
	private final int GET_SUBCATEGORIES_TASKCODE = 2;
	private final int ADD_INVENTORY_NEW_PROD_TASKCODE = 7;
	private final int GET_STATE_VAT_RATE_TASKCODE = 11;
	private final int POPULATE_VAT_TASKCODE = 5;
	private PopulateQuickAddVatRate mPopulateProdSkuVatTask;
	private Context mContext;
	private final int CAMERA_PROD_TASKCODE = 0;
	private final int RESULT_LOAD_IMAGE = 1;
	private Bitmap productBitmap;
	private static InventorySku editProduct;
	private String prodCodeText = "";
	private OnActionbarNavigationListener onActionbarNavigationListener;
	private Distributor taggedDistributor;

	private GetStateVatRatesTask mGetStateVatRatesTask;
	int catPos = 0;
	int subCatpos = 0;
	List<VAT> vatList; 
	InputMethodManager imm ;
	private ArrayAdapter<String> unitTypeSpinnerAdapater;
	private ArrayAdapter<Float> mVatRateSpinnerAdapter;
	private Spinner unitTypeSpinner;
	private Spinner mVatRateSpinner;
	private EditText prodNameEditText;
	public static EditText prodCodeEditText;
	private EditText prodMrpEditText;
	private EditText prodSalePriceEditText;
	private EditText prodSalePriceEditText2;
	private EditText prodSalePriceEditText3;
	private EditText prodPurcahsePriceEditText;
	private EditText prodQuantityEditText;
	private OnAddProductSuccess onAddProductSuccessListener;
	private boolean isQuickAddProduct;
	private ProductCategory quickAddProductCategory;
	private SnapBizzDatabaseHelper databaseHelper;
	private State state;
	private boolean editMode = false;
	

	public void setProductCategory(ProductCategory productCategory) {
		this.quickAddProductCategory = productCategory;
	}

	private void recycleProductBitmap() {
		if (productBitmap != null) {
			productBitmap.recycle();
			productBitmap = null;
		}
	}
	
	public void SetBarcode(String barcode) {
		EditText existingProductcodeEdittext = ((EditText) getActivity().findViewById(
				R.id.existing_productcode_edittext));
		if (existingProductcodeEdittext != null && existingProductcodeEdittext.isShown()) {
			existingProductcodeEdittext.setText(barcode);
			addExistingProductInQuickAdd(barcode);
		} else {
			prodCodeEditText.setText(barcode);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_add_edit,
				container, false);
		view.findViewById(R.id.add_button).setOnClickListener(
				onNewProductClickListener);
		prodNameEditText = (EditText) view
				.findViewById(R.id.new_productname_edittext);
		prodCodeEditText = (EditText) view
				.findViewById(R.id.new_productcode_edittext);
		prodMrpEditText = (EditText) view
				.findViewById(R.id.new_productmrp_edittext);
		prodPurcahsePriceEditText = (EditText) view
				.findViewById(R.id.new_productpp_edittext);
		prodSalePriceEditText = (EditText) view
				.findViewById(R.id.new_productsp_edittext);
		prodSalePriceEditText2 = (EditText) view
				.findViewById(R.id.new_productsp1_edittext);
		prodSalePriceEditText3 = (EditText) view
				.findViewById(R.id.new_productsp2_edittext);
		prodQuantityEditText = (EditText) view
				.findViewById(R.id.new_productqty_edittext);
		mVatRateSpinner = (Spinner) view
				.findViewById(R.id.new_product_vat_spinner);
		categorySpinner = (Spinner) view
				.findViewById(R.id.new_prodcategory_spinner);
		subcategorySpinner = (Spinner) view
				.findViewById(R.id.new_prodsubcategory_spinner);
		subcategorySpinner.setOnItemSelectedListener(onProductSubCategorySelectedListener);
		prodMrpEditText.addTextChangedListener(mrpEditTextWatcher);
		return view;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		onActionbarNavigationListener.onActionbarNavigation("",
				menuItem.getItemId());
		return true;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		new QueryProductCategories(getActivity(), this, GET_CATEGORIES_TASKCODE).execute();
		setHasOptionsMenu(true);
		if (isQuickAddProduct) {
			getView().findViewById(R.id.brand_imageview).setVisibility(
					View.GONE);
		} else {
			getView().findViewById(R.id.brand_imageview).setVisibility(
					View.VISIBLE);
			getView().findViewById(R.id.brand_imageview).setOnClickListener(
					onNewProductClickListener);
		}
		getView().findViewById(R.id.remove_imageview).setOnClickListener(
				onNewProductClickListener);
		getView().findViewById(R.id.remove_imageview_layout).setOnClickListener(
				onNewProductClickListener);
		getView().findViewById(R.id.new_product_imageview).setOnClickListener(
				onNewProductClickListener);
		getView().findViewById(R.id.distributor_clear_button)
				.setOnClickListener(onAddCancelClickListener);
		getView().findViewById(R.id.new_brand_done_button).setOnClickListener(
				onAddCancelClickListener);
		getView().findViewById(R.id.new_company_done_button)
				.setOnClickListener(onAddCancelClickListener);
		getActivity().getActionBar().setCustomView(R.layout.actionbar_layout);
		getView().findViewById(R.id.new_brand_imageview).setOnClickListener(
				onAddCancelClickListener);
		((TextView) getActivity().getActionBar().getCustomView()
				.findViewById(R.id.actionbar_header))
				.setText(getString(R.string.add_edit_product));

		try {
			QueryBuilder<State, Integer> stateQB = SnapCommonUtils
					.getDatabaseHelper(getActivity()).getStateDao()
					.queryBuilder();
			 state = stateQB
					.where()
					.eq("name",
							SnapSharedUtils.getStoreDetails(
									SnapCommonUtils
											.getSnapContext(getActivity()))
									.getState()).queryForFirst();
			
			mGetStateVatRatesTask = new GetStateVatRatesTask(getActivity(),AddEditProductFragment.this, GET_STATE_VAT_RATE_TASKCODE);
			mGetStateVatRatesTask.execute(String.valueOf(state.getStateID()));
		} catch (Exception e) {
			e.printStackTrace();
		}

		List<String> unitTypeList = Arrays
				.asList(SnapToolkitConstants.INVENTORY_UNIT_TYPES);
		unitTypeSpinnerAdapater = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_dropdown_item_1line, unitTypeList);
		unitTypeSpinner = ((Spinner) getActivity().findViewById(
				R.id.new_produnittype_spinner));
		unitTypeSpinner.setAdapter(unitTypeSpinnerAdapater);
		getView().findViewById(R.id.addprod_next_button).setOnClickListener(
				onNewProductClickListener);
		if (quickAddProductCategory != null &&
				quickAddProductCategory.getCategoryId() == SnapToolkitConstants.XTRA_PRODUCTS_CAT_ID) {
			getView().findViewById(R.id.add_edit_top_layout).setVisibility(View.GONE);
			SnapCommonUtils.hideSoftKeyboard(getActivity(),
			getView().findViewById(R.id.existing_productcode_edittext).getWindowToken());
			Button scanBarcode = (Button) getView().findViewById(R.id.scan_barcode_btn);
			scanBarcode.setVisibility(View.VISIBLE);
			scanBarcode.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					getView().findViewById(R.id.existing_productcode_edittext).setVisibility(View.VISIBLE);
				}
			});
		}
		if (editProduct != null) {
			if (editProduct.getProductSku().isGDB()) {
				getView().findViewById(R.id.addprod_next_button)
						.setOnClickListener(onNewProductClickListener);
				prodCodeEditText.setEnabled(false);
				getView().findViewById(R.id.new_product_imageview).setEnabled(
						true);
			} else {
				prodNameEditText.setEnabled(true);
				prodCodeEditText.setEnabled(false);
				getView().findViewById(R.id.new_product_imageview).setEnabled(
						true);
			}
			((ImageView) getActivity().findViewById(R.id.new_product_imageview))
					.setImageDrawable(SnapCommonUtils.getProductDrawable(
							editProduct.getProductSku(), getActivity()));
			prodCodeEditText.setText(editProduct.getProductSku()
					.getProductSkuCode());
			prodNameEditText.setText(editProduct.getProductSku()
					.getProductSkuName());
			prodQuantityEditText.setText(editProduct.getQuantity() + "");
			prodMrpEditText.setText(editProduct.getProductSku()
					.getProductSkuMrp() + "");
			prodPurcahsePriceEditText.setText(editProduct.getPurchasePrice()+ "");
			prodSalePriceEditText.setText(editProduct.getProductSku().getProductSkuSalePrice() + "");
			prodSalePriceEditText2.setText(editProduct.getProductSku().getProductSkuSalePrice2() + "");
			prodSalePriceEditText3.setText(editProduct.getProductSku().getProductSkuSalePrice3()+ "");
			int unitSelectedPos = 0;
			for (int i = 0; i < unitTypeList.size(); i++) {
				if (editProduct.getProductSku().getProductSkuUnits()
						.getUnitValue().equals(unitTypeList.get(i))) {
					unitSelectedPos = i;
				}
			}
			unitTypeSpinner.setSelection(unitSelectedPos);
		} else {
			resetEditText();
			((ImageView) getView().findViewById(R.id.new_product_imageview))
					.setImageResource(R.drawable.icon_camera);
		}
		if (!prodCodeText.equals("")) {
			((EditText) getActivity().findViewById(
					R.id.new_productcode_edittext)).setText(prodCodeText);
			((EditText) getActivity().findViewById(
					R.id.new_productcode_edittext)).setEnabled(false);
		}
		if (isQuickAddProduct) {
			prodCodeEditText.setEnabled(false);
			categorySpinner.setEnabled(false);
			subcategorySpinner.setEnabled(false);
		}

		if (null != editProduct && editProduct.getProductSku().isGDB()) {
			prodMrpEditText.requestFocus();
			((InputMethodManager) getActivity().getSystemService(
					Context.INPUT_METHOD_SERVICE)).showSoftInput(
					prodMrpEditText, InputMethodManager.SHOW_FORCED);
		} else if (prodNameEditText!= null && prodNameEditText.isShown()) {
			prodNameEditText.requestFocus();
			((InputMethodManager) getActivity().getSystemService(
					Context.INPUT_METHOD_SERVICE)).showSoftInput(
					prodNameEditText, InputMethodManager.SHOW_FORCED);
		}
		
	}

	public void resetEditText() {
		prodCodeEditText.setText("");
		prodNameEditText.setText("");
		prodQuantityEditText.setText("");
		prodMrpEditText.setText("");
		prodPurcahsePriceEditText.setText("");
		prodSalePriceEditText.setText("");
	}

	public void resetSalePrice() {
		prodSalePriceEditText.setText("");
		prodSalePriceEditText2.setText("");
		prodSalePriceEditText3.setText("");
	}
	
	public InventorySku getEditProduct() {
		return editProduct;
	}

	public void setEditProduct(InventorySku editProduct) {
		this.editProduct = editProduct;
	}

	public void setEditProduct(final ProductSku editProduct,
			final Context context) {
		new Runnable() {

			@Override
			public void run() {
				try {
					AddEditProductFragment.editProduct = getHelper(context)
							.getInventorySkuDao()
							.queryForEq("inventory_sku_id",
									editProduct.getProductSkuCode()).get(0);
				} catch (Exception e) {
					AddEditProductFragment.editProduct = new InventorySku(
							editProduct, Calendar.getInstance().getTime());
					e.printStackTrace();
				}
			}
		}.run();
	}

	private SnapBizzDatabaseHelper getHelper(Context context) {
		if (databaseHelper == null) {
			databaseHelper = OpenHelperManager.getHelper(context,
					SnapBizzDatabaseHelper.class);
		}
		return databaseHelper;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			onActionbarNavigationListener = (OnActionbarNavigationListener) activity;
		} catch (Exception e) {
			throw new ClassCastException("activity " + activity.toString()
					+ " must implement OnActionbarNavigationListener ");
		}
		try {
			onAddProductSuccessListener = (OnAddProductSuccess) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ "must implement onAddProductSuccess");
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAMERA_PROD_TASKCODE
				&& resultCode == Activity.RESULT_OK) {
			
			productBitmap = (Bitmap) data.getExtras().get("data");
			Bitmap.createScaledBitmap(
					productBitmap,
					(int) getResources().getDimension(
							R.dimen.new_prod_image_width),
					(int) getResources().getDimension(
							R.dimen.new_prod_image_height), false);
			((ImageView) getView().findViewById(R.id.new_product_imageview))
					.setImageBitmap(productBitmap);

		} else if (requestCode == RESULT_LOAD_IMAGE
				&& resultCode == Activity.RESULT_OK && null != data) {
			try {
				productBitmap=null;	
				if(productBitmap==null&&editProduct!=null){
					editProduct.getProductSku().setImageUrl(null);
				}
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = getActivity().getContentResolver().query(
						selectedImage, filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String picturePath = cursor.getString(columnIndex);
				cursor.close();
				getView().findViewById(R.id.brand_imageview).setEnabled(true);
				File file = new File(picturePath);
				long length = file.length();
				length = length / 1024;
				Log.d("Add product", "File Path : " + file.getPath()+ ", File size : " + length + " KB");
				if (length < 150) {
					productBitmap = SnapCommonUtils.getImageBitmap(picturePath);
					((ImageView) getView().findViewById(R.id.new_product_imageview)).setImageBitmap(productBitmap);
				} else {
					productBitmap = SnapCommonUtils.scaleBitmap(SnapCommonUtils.getImageBitmap(picturePath),500,500);
					((ImageView) getView().findViewById(R.id.new_product_imageview)).setImageBitmap(productBitmap);
					Log.d("Add product", "After Changeing image File Path : " + file.getPath()+ ", File size : " + length + " KB");
				}
			} catch (Exception e) {
				
			}

		}
		super.onActivityResult(requestCode, resultCode, data);
	};

	View.OnClickListener onAddCancelClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.distributor_clear_button) {
				if (editProduct != null)
					clearFileds(editProduct.getProductSku().isGDB());
				else
					clearFileds(false);
			}
		}
	};

	public Distributor getTaggedDistributor() {
		return taggedDistributor;
	}

	public void setTaggedDistributor(Distributor taggedDistributor) {
		this.taggedDistributor = taggedDistributor;
	}
	
	TextWatcher mrpEditTextWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
			if(prodMrpEditText.getText().toString().equals("")){
				prodSalePriceEditText.setText("");
				prodSalePriceEditText2.setText( "");
				prodSalePriceEditText3.setText( "");
			}
		}
		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {

		}
		@Override
		public void afterTextChanged(Editable arg0) {
		    changeSalePrice(true);
		}
	};
	
	private void changeSalePrice(boolean mrpChanged) {
        String sMRP = prodMrpEditText.getText().toString();
        // Do not change sale price while editing an existing product.
        if(editProduct != null || sMRP == null)
            return;

        float fMRP = 0;
	    try {
	        fMRP = Float.parseFloat(sMRP);
        } catch (Exception e) {
            fMRP = 0;
            e.printStackTrace();
        }
	    if (subCatpos > 0 && !sMRP.trim().isEmpty()) {
	        EditText editTextSPs[] = { prodSalePriceEditText, prodSalePriceEditText2, prodSalePriceEditText3 };
	        float[] discount = {
                    newSubProductCategoryList.get(subCatpos).getProductCategorySalePrice()/100, 
	                newSubProductCategoryList.get(subCatpos).getProductCategorySalePriceTwo()/100, 
	                newSubProductCategoryList.get(subCatpos).getProductCategorySalePriceThree()/100
            };
	        for (int i = 0; i < 3; i++) {
	        	// If MRP is edited or when category is changed and SPs are empty
                if (editTextSPs[i].getText().toString().isEmpty() || mrpChanged)
                    editTextSPs[i].setText(String.valueOf(fMRP - (fMRP * discount[i])));
	        }
	    }
	}
	
	View.OnClickListener onNewProductClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			SnapCommonUtils.hideSoftKeyboard(getActivity(), prodCodeEditText.getWindowToken());
			InventorySku removeProduct = editProduct;
			int editPosition = 0;
			String prodCode = prodCodeEditText.getText().toString().trim();
			String prodName = prodNameEditText.getText().toString().trim();
			if (v.getId() == R.id.add_button|| v.getId() == R.id.addprod_next_button) {
				//Hashtable<String, List<ProductSku>> itemsTable = SnapCommonUtils.getResultTable();
				List<ProductSku> itemsList;
				if (prodName.length() == 0) {
					CustomToast.showCustomToast(getActivity(),
							"Product Name cannot be empty", Toast.LENGTH_SHORT,
							CustomToast.WARNING);
					return;
				}
				float prodMrp = 0;
				try {
					prodMrp = Float.parseFloat(prodMrpEditText.getText()
							.toString());
				} catch (NumberFormatException e) {
					CustomToast.showCustomToast(getActivity(),
							"Please enter valid MRP", Toast.LENGTH_SHORT,
							CustomToast.WARNING);
					return;
				}
				if (categorySpinner.getSelectedItemPosition() == 0
						&& editProduct == null) {
					CustomToast.showCustomToast(getActivity(),
							"Please select Product category",
							Toast.LENGTH_SHORT, CustomToast.WARNING);
					return;
				} else if (subcategorySpinner.getSelectedItemPosition() == 0 && editProduct == null) {
					
					CustomToast.showCustomToast(getActivity(),
							"Please select Product sub-category",
							Toast.LENGTH_SHORT, CustomToast.WARNING);
					return;
				}
				else if (subcategorySpinner.getSelectedItemPosition() == 0 && editProduct != null&&!editProduct.getProductSku().isGDB()&&!isQuickAddProduct) {
					CustomToast.showCustomToast(getActivity(),
							"Please select Product sub-category",
							Toast.LENGTH_SHORT, CustomToast.WARNING);
					return;
				}
				float prodPurchasePrice = 0;
				try {
					prodPurchasePrice = Float
							.parseFloat(prodPurcahsePriceEditText.getText()
									.toString());
				} catch (NumberFormatException e) {
					prodPurchasePrice = 0;
				}

				float prodTaxRate = 0;
				try {
                    if(mVatRateSpinner.getSelectedItem() != null && mVatRateSpinner.getSelectedItem().toString() != null)
					    prodTaxRate = Float.parseFloat(mVatRateSpinner.getSelectedItem().toString());
				} catch (NumberFormatException e) {
					prodTaxRate = 0;
				}
				if (prodCode.isEmpty() && editProduct == null) {
					prodCode = SnapToolkitConstants.SNAP_LOCAL_PREFIX_KEY;
				} else if (prodCode.isEmpty() && editProduct != null) {
					prodCode = editProduct.getProductSku().getProductSkuCode();
				}
				SkuUnitType unitType;
				float prodQuantity = 0;
				try {
					prodQuantity = Float.parseFloat(prodQuantityEditText
							.getText().toString());
				} catch (NumberFormatException e) {
					prodQuantity = 0;
				}
				float prodSalePrice = 0;
				try {
					prodSalePrice = Float.parseFloat(prodSalePriceEditText
							.getText().toString());
				} catch (NumberFormatException e) {
					prodSalePrice = prodMrp;
				}
				float prodSalePriceTwo = 0;
				try {
					prodSalePriceTwo = Float.parseFloat(prodSalePriceEditText2.getText().toString());
				} catch (NumberFormatException e) {
					prodSalePriceTwo = prodMrp;
				}
				float prodSalePriceThree = 0;
				try {
					prodSalePriceThree = Float.parseFloat(prodSalePriceEditText3
							.getText().toString());
				} catch (NumberFormatException e) {
					prodSalePriceThree = prodMrp;
				}
				if (prodSalePrice > prodMrp) {
					CustomToast.showCustomToast(getActivity(),
							getString(R.string.msg_sp_more_than_mrp),
							Toast.LENGTH_SHORT, CustomToast.WARNING);
					prodSalePriceEditText.requestFocus();
					return;
				}
				if (prodPurchasePrice > prodMrp) {
					CustomToast.showCustomToast(getActivity(),
							getString(R.string.msg_pp_more_than_mrp),
							Toast.LENGTH_SHORT, CustomToast.WARNING);
					prodSalePriceEditText.requestFocus();
					return;
				}
				if (prodSalePriceTwo > prodMrp) {
					CustomToast.showCustomToast(getActivity(),
							getString(R.string.msg_sp_two_more_than_mrp),
							Toast.LENGTH_SHORT, CustomToast.WARNING);
					prodSalePriceEditText2.requestFocus();
					return;
				}
				if (prodSalePriceThree > prodMrp) {
					CustomToast.showCustomToast(getActivity(),
							getString(R.string.msg_sp_three_more_than_mrp),
							Toast.LENGTH_SHORT, CustomToast.WARNING);
					prodSalePriceEditText3.requestFocus();
					return;
				}
				unitType = SkuUnitType.getEnum(((String) unitTypeSpinner
						.getSelectedItem()));
				ProductSku prodSku=null;
				if(editProduct != null){
					 prodSku = new ProductSku(prodName, prodMrp,
							 "", true, prodSalePrice,prodSalePriceTwo,prodSalePriceThree,unitType);
				}else{
					 prodSku = new ProductSku(prodName, prodMrp,
							prodCode, "", true, prodSalePrice,prodSalePriceTwo,prodSalePriceThree,unitType);
				}
				
				if (editProduct != null && editProduct.getProductSku().isGDB()) {
					editProduct.getProductSku().setProductSkuUnits(unitType);
					editProduct.getProductSku().setProductSkuName(prodName);

				} else {
					ProductCategory category = (ProductCategory) categorySpinner.getSelectedItem();
					ProductCategory subcategory = (ProductCategory) subcategorySpinner.getSelectedItem();
					if (editProduct != null) {
						editProduct.getProductSku().setProductSkuName(prodName);
						editProduct.getProductSku().setProductSkuCode(prodCode);
						
						if (category != null) {
							editProduct.getProductSku().getProductCategory().setParenCategory(category);
							editProduct.getProductSku().setProductCategoryName(category.getCategoryName());
						}
						if (subcategory != null) {
							editProduct.getProductSku().setProductCategory((subcategory));
							editProduct.getProductSku().setProductSubCategoryName(subcategory.getCategoryName());
						}
						editProduct.getProductSku().setProductSkuCode(prodCode);
						editProduct.getProductSku().setProductSkuUnits(unitType);
						editProduct.getProductSku().setSelected(true);
					} else {
						Brand defaultBrand = new Brand();
						defaultBrand
								.setBrandId(SnapToolkitConstants.MISCELLANEOUS_BRAND_ID);
						defaultBrand.setMyStore(true);
						Company company = new Company();
						company.setCompanyId(SnapToolkitConstants.MISCELLANEOUS_COMPANY_ID);
						defaultBrand.setCompany(company);
						prodSku.setProductBrand(defaultBrand);
						if (category != null) 
							prodSku.setProductCategoryName(category.getCategoryName());
						if (subcategory != null) 
							prodSku.setProductSubCategoryName(subcategory.getCategoryName());
					}
				}
				prodSku.setProductCategory((ProductCategory) subcategorySpinner
						.getSelectedItem());
				if (productBitmap != null) {
					if (editProduct == null)
						prodSku.setProductSkuBitmap(productBitmap);
					else
						editProduct.getProductSku().setProductSkuBitmap(productBitmap);
				} 
				else if (editProduct == null && productBitmap == null) {
					prodSku.setImageUrl(null);
				} 
				else if (editProduct != null&& editProduct.getProductSku().getImageUrl() != null) {
					prodSku.setImageUrl(editProduct.getProductSku().getImageUrl());
				}
				if (unitType.equals(SkuUnitType.PC)) {
					prodQuantity = (int) prodQuantity;
				}
				prodSku.setQuickAddProduct(isQuickAddProduct);

				if (editProduct != null) {
					String key = editProduct.getProductSku()
							.getProductCategory().getCategoryId()
							+ "";
					editProduct.getProductSku().setProductSkuMrp(prodMrp);
					editProduct.setPurchasePrice(prodPurchasePrice);
					editProduct.getProductSku().setVAT(prodTaxRate);
					editProduct.setQuantity(prodQuantity);
					if (prodSalePrice > prodMrp) {
						editProduct.getProductSku().setProductSkuSalePrice(
								prodMrp);
					} else {
						editProduct.getProductSku().setProductSkuSalePrice(
								prodSalePrice);
					}
					if (prodSalePriceTwo > prodMrp) {
						editProduct.getProductSku().setProductSkuSalePrice2(
								prodMrp);
					} else {
						editProduct.getProductSku().setProductSkuSalePrice2(
								prodSalePriceTwo);
					}
					if (prodSalePriceThree > prodMrp) {
						editProduct.getProductSku().setProductSkuSalePrice3(
								prodMrp);
					} else {
						editProduct.getProductSku().setProductSkuSalePrice3(
								prodSalePriceThree);
					}
					editMode = true;
					if (editProduct.getProductSku().getProductSkuMrp() < editProduct
							.getProductSku().getProductSkuSalePrice()) {
						editProduct.setOffer(false);
					}
					
					CreateNewInventoryProduct newInventoryProductTask = new CreateNewInventoryProduct(
							getActivity(), AddEditProductFragment.this,
							ADD_INVENTORY_NEW_PROD_TASKCODE, editMode,
							taggedDistributor);
					/*Hashtable<String, List<ProductSku>> looseItemTable = SnapCommonUtils.getResultTable();
					if (looseItemTable != null) {
						itemsList = looseItemTable.get(key);
						itemsList
								.add(editPosition, editProduct.getProductSku());
						itemsTable.put(key, itemsList);
					}
					SnapCommonUtils.setResultTable(itemsTable);*/
					newInventoryProductTask.execute(editProduct);

				} else {
					if (prodSku.getProductSkuCode().startsWith(
							SnapToolkitConstants.SNAP_LOOSE_PREFIX_KEY))
						prodSku.setQuickAddProduct(true);
					prodSku.setVAT(prodTaxRate);
					InventorySku invSku = new InventorySku(prodSku,
							prodQuantity, Calendar.getInstance().getTime(),
							prodSku.getProductSkuCode(), prodPurchasePrice,
							prodTaxRate);
					if (invSku.getProductSku().getProductSkuMrp() < invSku
							.getProductSku().getProductSkuSalePrice()) {
						invSku.setOffer(false);
					}
					CreateNewInventoryProduct newInventoryProductTask = new CreateNewInventoryProduct(
							getActivity(), AddEditProductFragment.this,
							ADD_INVENTORY_NEW_PROD_TASKCODE, editMode,
							taggedDistributor);
					String key = invSku.getProductSku().getProductCategory()
							.getCategoryId()
							+ "";
					/*Hashtable<String, List<ProductSku>> looseItemTable = SnapCommonUtils.getResultTable();
					if (looseItemTable != null) {
						itemsList = looseItemTable.get(key);
						if (itemsList != null) {
							itemsList.add(invSku.getProductSku());
							itemsTable.put(key, itemsList);
							//SnapCommonUtils.setResultTable(itemsTable);
						}
					}*/
					Log.d("djdkjsdh", "productBitmap-------->"+productBitmap);
					newInventoryProductTask.execute(invSku);
				}
				v.setEnabled(false);

			} else if (v.getId() == R.id.new_product_imageview) {
				Intent cameraIntent = new Intent(
						android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(cameraIntent, CAMERA_PROD_TASKCODE);
			} else if (v.getId() == R.id.brand_imageview) {
				
				Intent i = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, RESULT_LOAD_IMAGE);
			}
			else if (v.getId() == R.id.remove_imageview|| v.getId() == R.id.remove_imageview_layout) {
				((ImageView) getView().findViewById(R.id.new_product_imageview)).setImageResource(drawable.icon_camera);
				productBitmap=null;	
				if(productBitmap==null&&editProduct!=null){
					editProduct.getProductSku().setImageUrl(null);
				}
			}
			//v.setEnabled(false);
		}
	};

	public void setIsQuickAddProduct(boolean isQuickAddProduct) {
		this.isQuickAddProduct = isQuickAddProduct;
	}

	public void clearFileds(boolean isGdb) {
		if (!isGdb) {
			prodCodeEditText.setText("");
			prodNameEditText.setText("");
		}
		unitTypeSpinner.setSelection(0);
		prodMrpEditText.setText("");
		prodSalePriceEditText.setText("");
		prodSalePriceEditText2.setText("");
		prodSalePriceEditText3.setText("");
		prodPurcahsePriceEditText.setText("");
		if(!isQuickAddProduct){
		categorySpinner.setSelection(0);
		subcategorySpinner.setSelection(0);
		mVatRateSpinner.setSelection(0);
		}
		prodQuantityEditText.setText("");
	}

	OnItemSelectedListener onProductCategorySelectedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1,
				int position, long arg3) {
			if (newProductCategoryList.size() > 0){
				new QueryProductSubCategories(getActivity(),
						AddEditProductFragment.this, GET_SUBCATEGORIES_TASKCODE)
						.execute(newProductCategoryList.get(position)
								.getCategoryId());
			}
			
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {

		}
	};

	OnItemSelectedListener onProductSubCategorySelectedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1,
				int position, long arg3) {
		
		 if (subCatpos != position){
			 mVatRateSpinner.setSelection(0);
			Log.d("","CategoryEditSelectedListener-------1");
			if (!isQuickAddProduct && (newSubProductCategoryList.size() > 0)) {
				Log.d("","CategoryEditSelectedListener-------2");
				for (int i = 0; i < (newSubProductCategoryList).size(); i++) {
					if ((newSubProductCategoryList).get(i).getCategoryId() == ((ProductCategory) subcategorySpinner.getSelectedItem()).getCategoryId()) {
						subCatpos = i;
						break;
					}
				}
			}
			if(subCatpos>0){
				Log.d("","CategoryEditSelectedListener-------3");
				mPopulateProdSkuVatTask = new PopulateQuickAddVatRate(mContext,
				        AddEditProductFragment.this, POPULATE_VAT_TASKCODE,
				        String.valueOf(state.getStateID()),
				        newSubProductCategoryList.get(subCatpos).getCategoryId());
				mPopulateProdSkuVatTask.execute();
				changeSalePrice(false);
			}
		}
			
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {

		}
	};
	
	@Override
	public void onTaskSuccess(Object responseList, int taskCode) {
		
		if (getActivity() == null)
			return;
		if (taskCode == GET_CATEGORIES_TASKCODE) {
			List<ProductCategory> productCategoryList = (List<ProductCategory>) responseList;
			if (newProductCategoryList == null) {
				newProductCategoryList = productCategoryList;
				if (editProduct == null) {
					ProductCategory category = new ProductCategory();
					category.setCategoryName("Category");
					newProductCategoryList.add(0, category);
				}
			} else {
				newProductCategoryList.clear();
				if (editProduct == null) {
					ProductCategory category = new ProductCategory();
					category.setCategoryName("Category");
					newProductCategoryList.add(0, category);
				}
				newProductCategoryList.addAll(productCategoryList);
			}
			ArrayAdapter<ProductCategory> categorySpinnerAdapater = new ArrayAdapter<ProductCategory>(
					getActivity(), android.R.layout.simple_dropdown_item_1line,
					newProductCategoryList);
			categorySpinner = ((Spinner) getActivity().findViewById(
					R.id.new_prodcategory_spinner));
			
			if (editProduct != null) {
				for (int i = 0; i < (this.newProductCategoryList).size(); i++) {
					if (editProduct.getProductSku().getProductCategory() != null &&((this.newProductCategoryList).get(i).getCategoryId() == editProduct.getProductSku().getProductCategory().getParenCategory().getCategoryId())) {
						catPos = i;
						break;
					}
				}
				if (editProduct.getProductSku().isGDB()) {
					categorySpinner.setEnabled(false);
				} else if (!isQuickAddProduct) {
					categorySpinner.setEnabled(true);
					categorySpinner
							.setOnItemSelectedListener(onProductCategorySelectedListener);
				}
			} else if (!isQuickAddProduct) {
				prodCodeEditText.setEnabled(false);
				categorySpinner.setEnabled(true);
				categorySpinner.setOnItemSelectedListener(onProductCategorySelectedListener);
				
			}

			if (isQuickAddProduct) {
				for (int i = 0; i < (this.newProductCategoryList).size(); i++) {
					if (quickAddProductCategory != null &&
							(this.newProductCategoryList).get(i).getCategoryId() == quickAddProductCategory
								.getParenCategory().getCategoryId()) {
						catPos = i;
						break;
					}
				}
			}
			categorySpinner.setAdapter(categorySpinnerAdapater);
			categorySpinner.setSelection(catPos);
			categorySpinnerAdapater.notifyDataSetChanged();

			if (editProduct != null) {
				new QueryProductSubCategories(getActivity(), this,
						GET_SUBCATEGORIES_TASKCODE).execute(editProduct
						.getProductSku().getProductCategory()
						.getParenCategory().getCategoryId());
			} else if (isQuickAddProduct) {
				new QueryProductSubCategories(getActivity(), this,
						GET_SUBCATEGORIES_TASKCODE)
						.execute(quickAddProductCategory.getParenCategory()
								.getCategoryId());
			} else {
				if (newSubProductCategoryList == null) {
					newSubProductCategoryList = new ArrayList<ProductCategory>();
					ProductCategory category = new ProductCategory();
					category.setCategoryName("Sub-Category");
					newSubProductCategoryList.add(0, category);
					subcategorySpinnerAdapater = new ArrayAdapter<ProductCategory>(
							getActivity(),
							android.R.layout.simple_dropdown_item_1line,
							newSubProductCategoryList);
					subcategorySpinner.setAdapter(subcategorySpinnerAdapater);
					subcategorySpinnerAdapater.notifyDataSetChanged();
				}
				else{
					subcategorySpinnerAdapater = new ArrayAdapter<ProductCategory>(
							getActivity(),
							android.R.layout.simple_dropdown_item_1line,
							newSubProductCategoryList);
					subcategorySpinner.setAdapter(subcategorySpinnerAdapater);
					subcategorySpinnerAdapater.notifyDataSetChanged();
					subcategorySpinner.setSelection(0);
				}
			}
		} else if (taskCode == GET_SUBCATEGORIES_TASKCODE) {
			List<ProductCategory> subproductCategoryList = (List<ProductCategory>) responseList;
        	ToolkitV2.hidingXtraProduct(subproductCategoryList, this.getActivity());
			if (newSubProductCategoryList == null) {
				newSubProductCategoryList = subproductCategoryList;
				if (editProduct == null) {
					ProductCategory category = new ProductCategory();
					category.setCategoryName("Sub-Category");
					newSubProductCategoryList.add(0, category);
				}
			} else {
				newSubProductCategoryList.clear();
				//if (editProduct == null) {
					ProductCategory category = new ProductCategory();
					category.setCategoryName("Sub-Category");
					newSubProductCategoryList.add(0, category);
				//}
				newSubProductCategoryList.addAll(subproductCategoryList);
			}
			if (subcategorySpinnerAdapater == null)
				subcategorySpinnerAdapater = new ArrayAdapter<ProductCategory>(
						getActivity(),
						android.R.layout.simple_dropdown_item_1line,
						newSubProductCategoryList);
			subCatpos=0;
			if (editProduct != null) {
				for (int i = 0; i < (this.newSubProductCategoryList).size(); i++) {
					if ((this.newSubProductCategoryList).get(i).getCategoryId() == editProduct
							.getProductSku().getProductCategory()
							.getCategoryId()) {
						subCatpos = i;
						break;
					}
				}
				if (editProduct.getProductSku().isGDB()) {
					subcategorySpinner.setEnabled(false);
				} else if (!isQuickAddProduct) {
					subcategorySpinner.setEnabled(true);
				}
			}
			
			else if (!isQuickAddProduct&&editProduct == null) {
				subcategorySpinner.setEnabled(true);
				
			} else if (isQuickAddProduct) {
				for (int i = 0; i < (this.newSubProductCategoryList).size(); i++) {
					if (quickAddProductCategory != null &&
							(this.newSubProductCategoryList).get(i).getCategoryId() == quickAddProductCategory.getCategoryId()) {
						subCatpos = i;
						break;
					}
				}
			}
			subcategorySpinner.setAdapter(subcategorySpinnerAdapater);
			subcategorySpinner.setSelection(subCatpos);
			if (subCatpos > 0 && editProduct == null) {
				mPopulateProdSkuVatTask = new PopulateQuickAddVatRate(mContext, AddEditProductFragment.this, POPULATE_VAT_TASKCODE,String.valueOf(state.getStateID()),this.newSubProductCategoryList.get(subCatpos).getCategoryId());
				mPopulateProdSkuVatTask.execute();
        	}
			subcategorySpinnerAdapater.notifyDataSetChanged();
		} else if (taskCode == ADD_INVENTORY_NEW_PROD_TASKCODE) {
			InventorySku newSku = (InventorySku) responseList;
			onAddProductSuccessListener.onAddProductSuccess(newSku, !editMode);
		} 
		else if (GET_STATE_VAT_RATE_TASKCODE == taskCode) {
			
			vatList = (List<VAT>) responseList;
			mVatRateSpinnerAdapter = new ArrayAdapter<Float>(getActivity(),
					android.R.layout.simple_dropdown_item_1line);
			for (VAT vat : vatList) {
				mVatRateSpinnerAdapter.add(vat.getVat());
			}
			mVatRateSpinner.setAdapter(mVatRateSpinnerAdapter);
			if (null != editProduct) {
				int position = mVatRateSpinnerAdapter.getPosition(editProduct.getProductSku().getVAT());
				mVatRateSpinner.setSelection(position);
			}
		}
		else if (POPULATE_VAT_TASKCODE == taskCode) {
			VAT vatRate =   (VAT) responseList;
			mVatRateSpinnerAdapter = new ArrayAdapter<Float>(getActivity(),
					android.R.layout.simple_dropdown_item_1line);
			for (VAT vat : vatList) {
				mVatRateSpinnerAdapter.add(vat.getVat());
			}
				 int position = mVatRateSpinnerAdapter.getPosition(vatRate.getVat());
					mVatRateSpinner.setSelection(position);
					
		}
	}

	@Override
	public void onTaskError(String errorMessage, int taskCode) {
		if (null == getActivity()) {
			return;
		}
		if (taskCode == ADD_INVENTORY_NEW_PROD_TASKCODE) {
			CustomToast.showCustomToast(getActivity(), errorMessage,
					Toast.LENGTH_SHORT, CustomToast.ERROR);
		} else if (GET_STATE_VAT_RATE_TASKCODE == taskCode) {
			mVatRateSpinnerAdapter.add(0f);
			mVatRateSpinner.setAdapter(mVatRateSpinnerAdapter);
		}

	}

	public void setProductCode(String barcode) {
		EditText prodCodeEditText = ((EditText) getActivity().findViewById(
				R.id.new_productcode_edittext));
		prodCodeEditText.setEnabled(true);
		prodCodeEditText.setText("");
		prodCodeEditText.setText(barcode);
		prodCodeEditText.setEnabled(false);
	}

	public void setProductCodeText(String barcode) {
		prodCodeText = barcode;
	}

	@Override
	public void onDetach() {
		if (editProduct != null) {
			editProduct = null;
		}
		recycleProductBitmap();
		resetEditText();
		categorySpinner.setSelection(0);
		subcategorySpinner.setSelection(0);
		super.onDetach();
	}

	public void hideSoftKeyboard() {
		if (getActivity().getCurrentFocus() != null) {
			SnapCommonUtils.hideSoftKeyboard(getActivity(), getActivity().getCurrentFocus().getWindowToken());
		}
	}

	public interface OnAddProductSuccess {
		public void onAddProductSuccess(InventorySku newProd, boolean isFromXtraProducts);
	}

	private void addExistingProductInQuickAdd(String barCode) {
		
		if (barCode != null) {
			editProduct = new InventorySku();
			List<ProductSku> tempProdList = null;
			editProduct.setProductSkuCode(barCode);
			List<InventorySku> existingInventoryList = null;
			try {
				existingInventoryList = SnapCommonUtils
	                    .getDatabaseHelper(this.getActivity())
	                    .getInventorySkuDao()
	                    .queryForEq("inventory_sku_id", barCode);
				tempProdList = SnapCommonUtils
	                    .getDatabaseHelper(this.getActivity())
                        .getProductSkuDao()
                        .queryForEq("sku_id", barCode);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (tempProdList != null && !tempProdList.isEmpty()) {
				if (existingInventoryList != null && !existingInventoryList.isEmpty() && 
						tempProdList.get(0).isQuickAddProduct()) {
					CustomToast.showCustomToast(getActivity(), this.getActivity().getResources()
							   .getString(R.string.existing_quickadd_product_msg), Toast.LENGTH_SHORT, CustomToast.ERROR);
					((EditText) getActivity().findViewById(R.id.existing_productcode_edittext)).setText("");
					onAddProductSuccessListener.onAddProductSuccess(editProduct, false);
					return;
				}
				editProduct.setProductSku(tempProdList.get(0)); 
				editProduct.setLastModifiedTimestamp(Calendar.getInstance().getTime());
				editProduct.setTaxRate(14.5f);
				editProduct.setTimestamp(new SimpleDateFormat(SnapToolkitConstants.ORMLITE_STANDARD_DATEFORMAT)
						   .format(Calendar.getInstance().getTime()));
				if (AddEditProductFragment.editProduct.getProductSku() != null) {
					AddEditProductFragment.editProduct.getProductSku().setQuickAddProduct(true);
					try {
						if (tempProdList.get(0).getProductBrand() != null) {
							List<Brand> prodBrandList = SnapCommonUtils
				                    .getDatabaseHelper(this.getActivity())
			                        .getBrandDao()
			                        .queryForEq("brand_id", tempProdList.get(0).getProductBrand().getBrandId());
							if (prodBrandList != null && !prodBrandList.isEmpty()) {
								if (!prodBrandList.get(0).isMyStore()) {
									prodBrandList.get(0).setMyStore(true);
									editProduct.getProductSku().setProductBrand(prodBrandList.get(0));
									SnapCommonUtils.getDatabaseHelper(this.getActivity()).getBrandDao().update(prodBrandList.get(0));
								}
							}
						}
						SnapCommonUtils.getDatabaseHelper(this.getActivity()).getProductSkuDao()
									   .update(editProduct.getProductSku());
						if(existingInventoryList != null && !existingInventoryList.isEmpty()) 
							SnapCommonUtils.getDatabaseHelper(this.getActivity()).getInventorySkuDao().update(editProduct);
						else
							SnapCommonUtils.getDatabaseHelper(this.getActivity()).getInventorySkuDao().create(editProduct);

						SnapCommonUtils.getDatabaseHelper(this.getActivity()).getInventoryBatchDao().create(new InventoryBatch(editProduct.getProductSku().getProductSkuCode(), 
		                		editProduct.getProductSku().getProductSkuMrp(), editProduct.getProductSku().getProductSkuSalePrice(), 
		                		editProduct.getPurchasePrice(), Calendar.getInstance().getTime(), 
		                		null, editProduct.getQuantity(), -1, 0));
					} catch (SQLException e) {
						e.printStackTrace();
					}
					onAddProductSuccessListener.onAddProductSuccess(editProduct, true);
				}
			} else {
				CustomToast.showCustomToast(getActivity(), this.getActivity().getResources()
						   .getString(R.string.error_add_existing_products), Toast.LENGTH_SHORT, CustomToast.ERROR);
				((EditText) getActivity().findViewById(R.id.existing_productcode_edittext)).setText("");
			}
		}
	}
}
