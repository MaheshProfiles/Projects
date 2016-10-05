package com.snapbizz.snapbilling.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.fragments.OfferAndStoreFragment;
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
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.onInventoryBatchActionListener = onInventoryBatchActionListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) 
            	convertView = inflater.inflate(R.layout.listitem_inventory_batch, null);
       ((TextView) convertView.findViewById(R.id.inventory_batch_availableqty_textview)).setOnClickListener(onInventoryBatchClickListener);
       ((TextView) convertView.findViewById(R.id.inventory_batchexpdate_textview)).setOnClickListener(onInventoryBatchClickListener);
       ((TextView) convertView.findViewById(R.id.inventory_batchmrp_textview)).setOnClickListener(onInventoryBatchClickListener);
       ((TextView) convertView.findViewById(R.id.inventory_batch_purchaseprice_textview)).setOnClickListener(onInventoryBatchClickListener);
       ((TextView) convertView.findViewById(R.id.inventory_batch_salesprice_textview)).setOnClickListener(onInventoryBatchClickListener); 
       InventoryBatch inventoryBatch = getItem(position);
       ((TextView) convertView.findViewById(R.id.inventory_batch_availableqty_textview)).setText(String.valueOf(inventoryBatch.getBatchAvailableQty()));
       ((TextView) convertView.findViewById(R.id.inventory_batchqty_textview)).setText(String.valueOf(inventoryBatch.getBatchQty()));
       if(inventoryBatch.getBatchTimeStamp() != null)
            ((TextView) convertView.findViewById(R.id.inventory_batchdate_textview)).setText(inventoryBatch.getBatchTimeStamp().substring(0, 11));
       if(inventoryBatch.getBatchExpiryDate() != null)
            ((TextView) convertView.findViewById(R.id.inventory_batchexpdate_textview)).setText(inventoryBatch.getBatchExpiryDate().substring(0, 11));
       ((TextView) convertView.findViewById(R.id.inventory_batchmrp_textview)).setText(SnapToolkitTextFormatter.formatPriceText(inventoryBatch.getBatchMrp(), context));
       ((TextView) convertView.findViewById(R.id.inventory_batch_purchaseprice_textview)).setText(SnapToolkitTextFormatter.formatPriceText(inventoryBatch.getBatchPurchasePrice(), context));
       ((TextView) convertView.findViewById(R.id.inventory_batch_salesprice_textview)).setText(SnapToolkitTextFormatter.formatPriceText(inventoryBatch.getBatchSalesPrice(), context));
       ((TextView) convertView.findViewById(R.id.inventory_prodname_textview)).setText(inventoryBatch.getProductSku().getProductSkuName());
       ((TextView) convertView.findViewById(R.id.inventory_batch_availableqty_textview)).setTag(position);
       ((TextView) convertView.findViewById(R.id.inventory_batchmrp_textview)).setTag(position);
       ((TextView) convertView.findViewById(R.id.inventory_batch_purchaseprice_textview)).setTag(position);
       ((TextView) convertView.findViewById(R.id.inventory_batch_salesprice_textview)).setTag(position);
       ((TextView) convertView.findViewById(R.id.inventory_batchexpdate_textview)).setTag(position);
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
                onInventoryBatchActionListener.onBatchValueEdit(getItem(lastSelectedPos).getBatchAvailableQty(), OfferAndStoreFragment.BATCH_AVAILABLEQTY_CONTEXT);
            } else if (v.getId() == R.id.inventory_batchmrp_textview) {
                onInventoryBatchActionListener.onBatchValueEdit(getItem(lastSelectedPos).getBatchMrp(), OfferAndStoreFragment.BATCH_MRP_CONTEXT);
            } else if (v.getId() == R.id.inventory_batch_purchaseprice_textview) {
                onInventoryBatchActionListener.onBatchValueEdit(getItem(lastSelectedPos).getBatchPurchasePrice(), OfferAndStoreFragment.BATCH_PURCHASEPRICE_CONTEXT);
            } else if (v.getId() == R.id.inventory_batch_salesprice_textview) {
                onInventoryBatchActionListener.onBatchValueEdit(getItem(lastSelectedPos).getBatchSalesPrice(), OfferAndStoreFragment.BATCH_SALESPRICE_CONTEXT);
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

    public interface OnInventoryBatchActionListener {
        public void onBatchValueEdit(float value, int context);
        public void onBatchDateEdit(String date);
    }
}
