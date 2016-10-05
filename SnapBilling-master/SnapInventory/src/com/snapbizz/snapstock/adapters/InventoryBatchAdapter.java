package com.snapbizz.snapstock.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.snapbizz.snapstock.R;
import com.snapbizz.snapstock.fragments.InventoryUpdateFragment;
import com.snapbizz.snaptoolkit.domains.InventoryBatch;
import com.snapbizz.snaptoolkit.utils.SnapToolkitTextFormatter;

public class InventoryBatchAdapter extends ArrayAdapter<InventoryBatch> {

    private LayoutInflater inflater;
    private Context context;
    private OnInventoryBatchActionListener onInventoryBatchActionListener;
    public int lastSelectedPos = -1;
    public View lastSelectedView;

    public InventoryBatchAdapter(Context context, List<InventoryBatch> objects, OnInventoryBatchActionListener onInventoryBatchActionListener) {
        super(context, android.R.id.text1, objects);
        // TODO Auto-generated constructor stub
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.onInventoryBatchActionListener = onInventoryBatchActionListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        InventoryBatchAdapterWrapper inventoryBatchAdapterWrapper;
        if (convertView == null) {
            inventoryBatchAdapterWrapper = new InventoryBatchAdapterWrapper();
            convertView = inflater.inflate(R.layout.listitem_inventory_batch, null);
            inventoryBatchAdapterWrapper.inventoryBatchAvailableQtyTextView = (TextView) convertView.findViewById(R.id.inventory_batch_availableqty_textview);
            inventoryBatchAdapterWrapper.inventoryBatchDateTextView = (TextView) convertView.findViewById(R.id.inventory_batchdate_textview);
            inventoryBatchAdapterWrapper.inventoryBatchExpDateTextView = (TextView) convertView.findViewById(R.id.inventory_batchexpdate_textview);
            inventoryBatchAdapterWrapper.inventoryBatchMrpTextView = (TextView) convertView.findViewById(R.id.inventory_batchmrp_textview);
            inventoryBatchAdapterWrapper.inventoryBatchPurchasePriceTextView = (TextView) convertView.findViewById(R.id.inventory_batch_purchaseprice_textview);
            inventoryBatchAdapterWrapper.inventoryBatchQtyTextView = (TextView) convertView.findViewById(R.id.inventory_batchqty_textview);
            inventoryBatchAdapterWrapper.inventoryBatchSalesPriceTextView = (TextView) convertView.findViewById(R.id.inventory_batch_salesprice_textview);
            inventoryBatchAdapterWrapper.inventoryBatchProductNameTextView = (TextView) convertView.findViewById(R.id.inventory_prodname_textview);
            inventoryBatchAdapterWrapper.inventoryBatchAvailableQtyTextView.setOnClickListener(onInventoryBatchClickListener);
            inventoryBatchAdapterWrapper.inventoryBatchExpDateTextView.setOnClickListener(onInventoryBatchClickListener);
            inventoryBatchAdapterWrapper.inventoryBatchMrpTextView.setOnClickListener(onInventoryBatchClickListener);
            inventoryBatchAdapterWrapper.inventoryBatchPurchasePriceTextView.setOnClickListener(onInventoryBatchClickListener);
            inventoryBatchAdapterWrapper.inventoryBatchSalesPriceTextView.setOnClickListener(onInventoryBatchClickListener);
            convertView.setTag(inventoryBatchAdapterWrapper);
        } else {
            inventoryBatchAdapterWrapper = (InventoryBatchAdapterWrapper) convertView.getTag();
        }
        InventoryBatch inventoryBatch = getItem(position);
        inventoryBatchAdapterWrapper.inventoryBatchAvailableQtyTextView.setText(inventoryBatch.getBatchAvailableQty()+"");
        inventoryBatchAdapterWrapper.inventoryBatchQtyTextView.setText(inventoryBatch.getBatchQty()+"");
        if(inventoryBatch.getBatchTimeStamp() != null)
            inventoryBatchAdapterWrapper.inventoryBatchDateTextView.setText(inventoryBatch.getBatchTimeStamp().substring(0, 11));
        if(inventoryBatch.getBatchExpiryDate() != null)
            inventoryBatchAdapterWrapper.inventoryBatchExpDateTextView.setText(inventoryBatch.getBatchExpiryDate().substring(0, 11));
        inventoryBatchAdapterWrapper.inventoryBatchMrpTextView.setText(SnapToolkitTextFormatter.formatPriceText(inventoryBatch.getBatchMrp(), context));
        inventoryBatchAdapterWrapper.inventoryBatchPurchasePriceTextView.setText(SnapToolkitTextFormatter.formatPriceText(inventoryBatch.getBatchPurchasePrice(), context));
        inventoryBatchAdapterWrapper.inventoryBatchSalesPriceTextView.setText(SnapToolkitTextFormatter.formatPriceText(inventoryBatch.getBatchSalesPrice(), context));
        inventoryBatchAdapterWrapper.inventoryBatchProductNameTextView.setText(inventoryBatch.getProductSku().getProductSkuName());
        inventoryBatchAdapterWrapper.inventoryBatchAvailableQtyTextView.setTag(position);
        inventoryBatchAdapterWrapper.inventoryBatchMrpTextView.setTag(position);
        inventoryBatchAdapterWrapper.inventoryBatchPurchasePriceTextView.setTag(position);
        inventoryBatchAdapterWrapper.inventoryBatchSalesPriceTextView.setTag(position);
        inventoryBatchAdapterWrapper.inventoryBatchExpDateTextView.setTag(position);
        return convertView;
    }

    OnClickListener onInventoryBatchClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            v.setSelected(true);
            if(lastSelectedView != null)
                lastSelectedView.setSelected(false);
            lastSelectedView = v;
            lastSelectedPos = (Integer) v.getTag();
            if (v.getId() == R.id.inventory_batch_availableqty_textview) {
                onInventoryBatchActionListener.onBatchValueEdit(getItem(lastSelectedPos).getBatchAvailableQty(), InventoryUpdateFragment.BATCH_AVAILABLEQTY_CONTEXT);
            } else if (v.getId() == R.id.inventory_batchmrp_textview) {
                onInventoryBatchActionListener.onBatchValueEdit(getItem(lastSelectedPos).getBatchMrp(), InventoryUpdateFragment.BATCH_MRP_CONTEXT);
            } else if (v.getId() == R.id.inventory_batch_purchaseprice_textview) {
                onInventoryBatchActionListener.onBatchValueEdit(getItem(lastSelectedPos).getBatchPurchasePrice(), InventoryUpdateFragment.BATCH_PURCHASEPRICE_CONTEXT);
            } else if (v.getId() == R.id.inventory_batch_salesprice_textview) {
                onInventoryBatchActionListener.onBatchValueEdit(getItem(lastSelectedPos).getBatchSalesPrice(), InventoryUpdateFragment.BATCH_SALESPRICE_CONTEXT);
            } else if (v.getId() == R.id.inventory_batchexpdate_textview) {
                onInventoryBatchActionListener.onBatchDateEdit(getItem(lastSelectedPos).getBatchExpiryDate());
            }
        }
    };

    public InventoryBatch getLastSelectedItem() {
        if(lastSelectedPos != -1)
            return getItem(lastSelectedPos);
        else
            return null;
    }

    private static class InventoryBatchAdapterWrapper {
        public TextView inventoryBatchProductNameTextView;
        public TextView inventoryBatchMrpTextView;
        public TextView inventoryBatchPurchasePriceTextView;
        public TextView inventoryBatchSalesPriceTextView;
        public TextView inventoryBatchQtyTextView;
        public TextView inventoryBatchAvailableQtyTextView;
        public TextView inventoryBatchDateTextView;
        public TextView inventoryBatchExpDateTextView;
    }

    public interface OnInventoryBatchActionListener {
        public void onBatchValueEdit(float value, int context);
        public void onBatchDateEdit(String date);
    }

}
