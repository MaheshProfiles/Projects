package com.snapbizz.snapbilling.fragments;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.domainsV2.InventoryDetails;
import com.snapbizz.snapbilling.interfaces.OnAddNewProductListener;
import com.snapbizz.snaptoolkit.R.drawable;
import com.snapbizz.snaptoolkit.db.GlobalDB;
import com.snapbizz.snaptoolkit.db.SnapbizzDB;
import com.snapbizz.snaptoolkit.db.dao.ProductPacks;
import com.snapbizz.snaptoolkit.db.dao.Products;
import com.snapbizz.snaptoolkit.domains.InventorySku;
import com.snapbizz.snaptoolkit.gdb.dao.ProductCategories;
import com.snapbizz.snaptoolkit.gdb.dao.VatCategories;
import com.snapbizz.snaptoolkit.interfaces.OnActionbarNavigationListener;
import com.snapbizz.snaptoolkit.utils.CustomToast;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

public class AddEditInventoryFragment extends Fragment {
	
	private EditText editProductName;
	private EditText editProductCode;
	private EditText editProductMRP;
	private EditText editProductQty;
	private EditText editSellingPriceOne;
	private EditText editSellingPriceTwo;
	private EditText editSellingPriceThree;
	private EditText editPurchasePrice;
	private Spinner spinnerSubCategory;
	private Spinner spinnerVatCategory;
	private Spinner spinnerUOM;
	private Spinner spinnerCategory;
	private InventoryDetails editProduct;
	private Bitmap productBitmap;
	
	private final int CAMERA_PROD_TASKCODE = 0;
	private final int RESULT_LOAD_IMAGE = 1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.addedit_inventory_layout, container, false);
		return view;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}
	
	public void onActivityCreated(Bundle savedInstanceState) {
		 super.onActivityCreated(savedInstanceState);
		((Button) getView().findViewById(R.id.product_pack_save)).setOnClickListener(onSaveClickListner);
		editProductName = (EditText) getView().findViewById(R.id.edit_product_name);
		editProductCode = (EditText) getView().findViewById(R.id.edit_product_code);
		editProductMRP = (EditText) getView().findViewById(R.id.edit_mrp);
		editProductQty = (EditText) getView().findViewById(R.id.edit_stock_qty);
		editSellingPriceOne = (EditText) getView().findViewById(R.id.edit_selling_price_one);
		editSellingPriceTwo = (EditText) getView().findViewById(R.id.edit_selling_price_two);
		editSellingPriceThree = (EditText) getView().findViewById(R.id.edit_selling_price_three);
		editPurchasePrice = (EditText) getView().findViewById(R.id.edit_purchase_price);
		spinnerSubCategory = (Spinner) getView().findViewById(R.id.spinner_sub_category);
		spinnerVatCategory = (Spinner) getView().findViewById(R.id.spinner_vat_category);
		spinnerUOM = (Spinner) getView().findViewById(R.id.spinner_uom);
		spinnerCategory = (Spinner) getView().findViewById(R.id.spinner_category);
		loadCategory();
		loadSubCategory();
		loadVatRate();
		loadUOM();
		if (editProduct != null)
			setProductDetails();
		((ImageView) getView().findViewById(R.id.camera_selector_imageview)).setOnClickListener(onChangeImageClickListner);
		((ImageView) getView().findViewById(R.id.file_selector_imageview)).setOnClickListener(onChangeImageClickListner);
		((ImageView) getView().findViewById(R.id.remove_imageview)).setOnClickListener(onChangeImageClickListner);
	}
	
	OnClickListener onSaveClickListner = new OnClickListener() {
		@Override
		public void onClick(View v) {
			String prodName = editProductName.getText().toString().trim();
			Long prodCode = Long.parseLong(editProductCode.getText().toString());
			if (prodName.length() == 0) {
				CustomToast.showCustomToast(getActivity(), getResources().getString(R.string.product_name_notempty), 
																						Toast.LENGTH_SHORT, CustomToast.WARNING);
				return;
			}
			float prodMrp = 0;
			try {
				prodMrp = Float.parseFloat(editProductMRP.getText().toString());
			} catch (NumberFormatException e) {
				CustomToast.showCustomToast(getActivity(), getResources().getString(R.string.mrp_notempty), 
																							Toast.LENGTH_SHORT, CustomToast.WARNING);
				return;
			}
			if (spinnerCategory.getSelectedItemPosition() == 0 && editProduct == null) {
				CustomToast.showCustomToast(getActivity(), getResources().getString(R.string.category_notempty), Toast.LENGTH_SHORT, 
																														CustomToast.WARNING);
				return;
			} else if (spinnerSubCategory.getSelectedItemPosition() == 0 && editProduct == null) {
				CustomToast.showCustomToast(getActivity(), getResources().getString(R.string.sub_category_notempty), Toast.LENGTH_SHORT, 
																															CustomToast.WARNING);
				return;
			}
			float prodPurchasePrice = 0;
			try {
				prodPurchasePrice = Float .parseFloat(editPurchasePrice.getText().toString());
			} catch (NumberFormatException e) {
				prodPurchasePrice = 0;
			}
			float prodTaxRate = 0;
			try {
                if (spinnerVatCategory.getSelectedItem() != null && spinnerVatCategory.getSelectedItem().toString() != null)
				    prodTaxRate = Float.parseFloat(spinnerVatCategory.getSelectedItem().toString());
			} catch (NumberFormatException e) {
				prodTaxRate = 0;
			}
			if (prodCode == 0 && editProduct == null) {
				
			} else if (prodCode == 0 && editProduct != null) {
				prodCode = editProduct.getProductSkuCode();
			}
			saveInventory();
		}
	};
	
	OnClickListener onChangeImageClickListner = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.camera_selector_imageview) {
				Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(cameraIntent, CAMERA_PROD_TASKCODE);
			} else if (v.getId() == R.id.file_selector_imageview) {
				Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, RESULT_LOAD_IMAGE);
			} else if (v.getId() == R.id.remove_imageview) {
				((ImageView) getView().findViewById(R.id.product_image)).setImageResource(R.drawable.demo_pic);
				productBitmap = null;	
				if (productBitmap == null && editProduct != null)
					editProduct.setImageUrl(null);
			}
		}
	};
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAMERA_PROD_TASKCODE && resultCode == Activity.RESULT_OK) {
			productBitmap = (Bitmap) data.getExtras().get("data");
			Bitmap.createScaledBitmap(
					productBitmap, (int) getResources().getDimension(R.dimen.new_prod_image_width), (int) getResources().getDimension(
							R.dimen.new_prod_image_height), false);
			((ImageView) getView().findViewById(R.id.product_image)).setImageBitmap(productBitmap);
		} else if (requestCode == RESULT_LOAD_IMAGE
				&& resultCode == Activity.RESULT_OK && null != data) {
			try {
				productBitmap = null;	
				if(productBitmap == null && editProduct != null)
					editProduct.setImageUrl(null);
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String picturePath = cursor.getString(columnIndex);
				cursor.close();
				File file = new File(picturePath);
				long length = file.length();
				length = length / 1024;
				Log.d("Add product", "File Path : " + file.getPath()+ ", File size : " + length + " KB");
				if (length < 150) {
					productBitmap = SnapCommonUtils.getImageBitmap(picturePath);
					((ImageView) getView().findViewById(R.id.product_image)).setImageBitmap(productBitmap);
				} else {
					productBitmap = SnapCommonUtils.scaleBitmap(SnapCommonUtils.getImageBitmap(picturePath), 500, 500);
					((ImageView) getView().findViewById(R.id.product_image)).setImageBitmap(productBitmap);
					Log.d("Add product", "After Changeing image File Path : " + file.getPath()+ ", File size : " + length + " KB");
				}
			} catch (Exception e) {}
		}
		super.onActivityResult(requestCode, resultCode, data);
	};
	
	public InventoryDetails getEditProduct() {
		return editProduct;
	}

	public void setEditProduct(InventoryDetails editProduct) {
		this.editProduct = editProduct;
	}

	private void saveInventory() {
		Products ldbProducts = new Products();
		ldbProducts.setName(editProductName.getText().toString());
		ldbProducts.setProductCode(Long.parseLong(editProductCode.getText().toString()));
		ldbProducts.setMrp(Integer.parseInt(editProductMRP.getText().toString()));
		ldbProducts.setMeasure(Integer.parseInt(editProductQty.getText().toString()));
		ldbProducts.setUom(spinnerUOM.getSelectedItem().toString());
		ldbProducts.setVatRate(Integer.parseInt(spinnerVatCategory.getSelectedItem().toString()));
		SnapbizzDB.getInstance(getActivity(), false).insertOrReplaceProducts(ldbProducts);
		ProductPacks productPacks = new ProductPacks();	
		productPacks.setSalePrice1(Integer.parseInt(editSellingPriceOne.getText().toString()));
		productPacks.setSalePrice2(Integer.parseInt(editSellingPriceTwo.getText().toString()));
		productPacks.setSalePrice3(Integer.parseInt(editSellingPriceThree.getText().toString()));
		SnapbizzDB.getInstance(getActivity(), false).packsHelper.insertOrReplaceProductPack(productPacks);
	}
	
	private void loadCategory() {
		List<ProductCategories> allCategory = GlobalDB.getInstance(getActivity(), true).getAllCategory();
		ArrayList<String> data = new ArrayList<String>();
		for (int i = 0; allCategory != null && i < allCategory.size(); i++)
			data.add(allCategory.get(i).getName());
		ArrayAdapter<String> categorySpinnerAdapater = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, data);
		spinnerCategory.setAdapter(categorySpinnerAdapater);
		categorySpinnerAdapater.notifyDataSetChanged();
	}
	
	private void loadSubCategory() {
		List<ProductCategories> allSubCategory = GlobalDB.getInstance(getActivity(), true).getAllSubCategory();
		ArrayList<String> data = new ArrayList<String>();
		for (int i = 0; allSubCategory != null && i < allSubCategory.size(); i++)
			data.add(allSubCategory.get(i).getName());
		ArrayAdapter<String> subcategorySpinnerAdapater = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, data);
		spinnerSubCategory.setAdapter(subcategorySpinnerAdapater);
		subcategorySpinnerAdapater.notifyDataSetChanged();
	}
	
	
	private void loadVatRate() {
		List<VatCategories> vatCategories = GlobalDB.getInstance(getActivity(), true).getVatRateByState(1);
		ArrayList<Float> data = new ArrayList<Float>();
		for (int i = 0; vatCategories != null && i < vatCategories.size(); i++) {
			data.add(vatCategories.get(i).getVatRate());
		}
		ArrayAdapter<Float> subcategorySpinnerAdapater = new ArrayAdapter<Float>(getActivity(), android.R.layout.simple_dropdown_item_1line, data);
		//spinnerSubCategory.setAdapter(subcategorySpinnerAdapater);
		subcategorySpinnerAdapater.notifyDataSetChanged();
	}
	
	private void loadUOM() {
		List<String> unitTypeList = Arrays.asList(SnapToolkitConstants.INVENTORY_UNIT_TYPES);
		ArrayAdapter<String> unitTypeSpinnerAdapater = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, 
																																	unitTypeList);
		spinnerUOM.setAdapter(unitTypeSpinnerAdapater);
	}
	
	private void setProductDetails() {
		editProductName.setText(editProduct.getProductSkuName());
		editProductCode.setText(String.valueOf(editProduct.getProductSkuCode()));
		editProductMRP.setText(String.valueOf(editProduct.getProductSkuMrp()));
		ProductPacks productPacks = editProduct.getProductPacksList().get(0);
		editProductQty.setText(String.valueOf(productPacks.getPackSize()));
		editSellingPriceOne.setText(String.valueOf(productPacks.getSalePrice1()));
		editSellingPriceTwo.setText(String.valueOf(productPacks.getSalePrice2()));
		editSellingPriceThree.setText(String.valueOf(productPacks.getSalePrice3()));
		/*spinnerSubCategory = (Spinner) getView().findViewById(R.id.spinner_sub_category);
		spinnerVatCategory = (Spinner) getView().findViewById(R.id.spinner_vat_category);
		spinnerUOM = (Spinner) getView().findViewById(R.id.spinner_uom);
		spinnerCategory = (Spinner) getView().findViewById(R.id.spinner_category);*/
	}
}
