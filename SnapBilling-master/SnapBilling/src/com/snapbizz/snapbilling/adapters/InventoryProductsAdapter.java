package com.snapbizz.snapbilling.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.fragments.OfferAndStoreFragment;
import com.snapbizz.snaptoolkit.domains.InventorySku;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitTextFormatter;


public class InventoryProductsAdapter extends ArrayAdapter<InventorySku> {
    private Context context;
    private LayoutInflater inflater;
    private OnInventoryActionListener inventoryActionListener;
    private int lastSelectedPos = -1;
    public View lastSelectedView;

    public InventoryProductsAdapter(Context context, List<InventorySku> inventoryProducts, OnInventoryActionListener inventoryActionListener) {
        super(context, android.R.id.text1, inventoryProducts);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.inventoryActionListener = inventoryActionListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) 
            convertView = inflater.inflate(R.layout.listitem_inventory_update, null);
            ((TextView) convertView.findViewById(R.id.inventory_prodquantity_textview)).setOnClickListener(inventoryProdClickListener);
            ((TextView) convertView.findViewById(R.id.inventory_prodtax_textview)).setOnClickListener(inventoryProdClickListener);
            ((TextView) convertView.findViewById(R.id.inventory_mrp_textview)).setOnClickListener(inventoryProdClickListener);
            ((TextView) convertView.findViewById(R.id.inventory_purchaseprice_textview)).setOnClickListener(inventoryProdClickListener);
            ((TextView) convertView.findViewById(R.id.inventory_salesprice_textview)).setOnClickListener(inventoryProdClickListener);
            ((TextView) convertView.findViewById(R.id.inventory_prod_subcat_textview)).setOnClickListener(inventoryProdClickListener);
            ((TextView) convertView.findViewById(R.id.inventory_prodname_textview)).setOnClickListener(inventoryProdClickListener);
            ((ImageView) convertView.findViewById(R.id.inventory_store_select_image)).setOnClickListener(inventoryProdClickListener);
     
        InventorySku inventorySku = getItem(position);
        ProductSku prod = inventorySku.getProductSku();
        if (!inventorySku.getProductSku().isSelected()) {
            ((TextView) convertView.findViewById(R.id.inventory_prod_subcat_textview)).setEnabled(false);
            ((ImageView) convertView.findViewById(R.id.inventory_store_select_image)).setEnabled(false);
        } else {
            ((TextView) convertView.findViewById(R.id.inventory_prod_subcat_textview)).setEnabled(true);
            ((ImageView) convertView.findViewById(R.id.inventory_store_select_image)).setEnabled(true);
        }
        ((TextView) convertView.findViewById(R.id.inventory_mrp_textview)).setTag(position);
        ((ImageView) convertView.findViewById(R.id.inventory_prod_imageview)).setTag(position);
        ((TextView) convertView.findViewById(R.id.inventory_prod_subcat_textview)).setTag(position);
        ((TextView) convertView.findViewById(R.id.inventory_prod_cat_textview)).setTag(position);
        ((TextView) convertView.findViewById(R.id.inventory_purchaseprice_textview)).setTag(position);
        ((TextView) convertView.findViewById(R.id.inventory_salesprice_textview)).setTag(position);
        ((TextView) convertView.findViewById(R.id.inventory_prodquantity_textview)).setTag(position);
        ((TextView) convertView.findViewById(R.id.inventory_prodtax_textview)).setTag(position);
        ((TextView) convertView.findViewById(R.id.inventory_produnit_spinner)).setTag(position);
        ((ImageView) convertView.findViewById(R.id.inventory_store_select_image)).setTag(position);
        ((ImageView) convertView.findViewById(R.id.update_global_prod_button_selected)).setTag(position);
        ((TextView) convertView.findViewById(R.id.inventory_prodname_textview)).setTag(position);
        
        ((ImageView) convertView.findViewById(R.id.inventory_prod_imageview)).setImageDrawable(SnapCommonUtils.getProductDrawable(prod, context));
        ((TextView) convertView.findViewById(R.id.inventory_prodname_textview)).setText(prod.getProductSkuName());
        ((TextView) convertView.findViewById(R.id.inventory_mrp_textview)).setText(SnapToolkitTextFormatter.formatPriceText(prod.getProductSkuMrp(), context));
        ((TextView) convertView.findViewById(R.id.inventory_prodquantity_textview)).setText(String.valueOf(SnapCommonUtils.roundOffDecimalPoints(inventorySku.getQuantity())));
        ((TextView) convertView.findViewById(R.id.inventory_prodcode_textview)).setText(prod.getProductSkuCode());
        ((TextView) convertView.findViewById(R.id.inventory_produnit_spinner)).setText(prod.getProductSkuUnits().getUnitValue());
        ((TextView) convertView.findViewById(R.id.inventory_prodtax_textview)).setText(inventorySku.getTaxRate() + "%");
        ((TextView) convertView.findViewById(R.id.inventory_purchaseprice_textview)).setText(SnapToolkitTextFormatter.formatPriceText(inventorySku.getPurchasePrice(), context));
        ((TextView) convertView.findViewById(R.id.inventory_salesprice_textview)).setText(SnapToolkitTextFormatter.formatPriceText(inventorySku.getProductSku().getProductSkuSalePrice(), context));
        ((TextView) convertView.findViewById(R.id.inventory_prod_cat_textview)).setText(prod.getProductCategory().getParenCategory().getCategoryName());
        ((TextView) convertView.findViewById(R.id.inventory_prod_subcat_textview)).setText(prod.getProductCategory().getCategoryName());
        ((ImageView) convertView.findViewById(R.id.inventory_store_select_image)).setSelected(inventorySku.isOffer());
        ((ImageView) convertView.findViewById(R.id.inventory_store_select_image)).setSelected(inventorySku.isStore());
        ((ImageView) convertView.findViewById(R.id.update_global_prod_button_selected)).setSelected(inventorySku.getProductSku().isSelected());
        return convertView;
    }

    public InventorySku getLastSelectedItem() {
        if (lastSelectedPos == -1)
            return null;
        else if (lastSelectedPos >= getCount())
            return null;
        else
            return getItem(lastSelectedPos);
    }
    
    public int getLastSelectedPos() {
        return lastSelectedPos;
    }
    
    public void setLastSelectedPos(int lastSelectedPos) {
        this.lastSelectedPos = lastSelectedPos;
    }



    OnClickListener inventoryProdClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            v.setSelected(true);
            lastSelectedView = v;
            lastSelectedPos = (Integer) v.getTag();
            
            if (v.getId() == R.id.inventory_prodquantity_textview) {
                inventoryActionListener.onProductValueEdit(getItem(lastSelectedPos).getQuantity(), OfferAndStoreFragment.STOCKQTY_CONTEXT);
            } else if (v.getId() == R.id.inventory_prodtax_textview) {
                inventoryActionListener.onProductValueEdit(getItem(lastSelectedPos).getTaxRate(), OfferAndStoreFragment.TAX_CONTEXT);
            } else if (v.getId() == R.id.inventory_mrp_textview) {
                inventoryActionListener.onProductValueEdit(getItem(lastSelectedPos).getProductSku().getProductSkuMrp(), OfferAndStoreFragment.MRP_CONTEXT);
            } else if (v.getId() == R.id.inventory_purchaseprice_textview) {
                inventoryActionListener.onProductValueEdit(getItem(lastSelectedPos).getPurchasePrice(), OfferAndStoreFragment.PURCHASE_PRICE_CONTEXT);
            } else if (v.getId() == R.id.inventory_salesprice_textview) {
                inventoryActionListener.onProductValueEdit(getItem(lastSelectedPos).getProductSku().getProductSkuSalePrice(), OfferAndStoreFragment.SALES_PRICE_CONTEXT);
            }
            else if (v.getId() == R.id.inventory_store_select_image) {
                InventorySku inventorySku = getItem(lastSelectedPos);
                inventorySku.setStore(!inventorySku.isStore());
                notifyDataSetChanged();
                inventoryActionListener.updateInventorySku(inventorySku, context.getString(R.string.show_store), true);
                inventoryActionListener.updateInventorySku(inventorySku, context.getString(R.string.is_offer), true);
            }
            else if (v.getId() == R.id.inventory_prodname_textview) {
                if(getLastSelectedItem()!=null && !getLastSelectedItem().getProductSku().isSelected())
                inventoryActionListener.onAddItemToInventory(getLastSelectedItem());
            }
        }
    };      

    public interface OnInventoryActionListener {
        public void onProductValueEdit(float value, int context);
        public void onRemoveItem(InventorySku product);
        public void onShowBatches(int position);
        public void updateInventorySku(InventorySku inventorySku,String fieldName,boolean value);
        public void onEditProduct(InventorySku inventorySku);
        public void onAddItemToInventory(InventorySku inventorySku);
    }
}
