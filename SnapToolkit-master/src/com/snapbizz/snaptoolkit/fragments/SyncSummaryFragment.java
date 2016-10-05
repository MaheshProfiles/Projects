package com.snapbizz.snaptoolkit.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.snapbizz.snaptoolkit.adapters.SyncDataAdapter;
import com.snapbizz.snaptoolkit.domains.SyncInformationBean;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;

public class SyncSummaryFragment extends DialogFragment {
    
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	
    	AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        builderSingle.setTitle("Sync Summary :");
        
        builderSingle.setPositiveButton("Close",new DialogInterface.OnClickListener() {

               @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        
        List<SyncInformationBean> SyncInfoList = new ArrayList<SyncInformationBean>();
        HashMap<String, Integer> resultMap=SnapCommonUtils.getResultMap();
        Context activityContext = SnapCommonUtils.getSnapContext(getActivity());
        SyncInfoList.add(new SyncInformationBean("Sync Type", "Date" ,"Remaining"));
        SyncInfoList.add(new SyncInformationBean("Products", SnapSharedUtils.getLastProductSkuSyncTimestamp(activityContext),resultMap.get("products")+""));
        SyncInfoList.add(new SyncInformationBean("MyStore Products", SnapSharedUtils.getLastInventorySkuSyncTimestamp(activityContext),resultMap.get("myStoreProducts")+""));
        SyncInfoList.add(new SyncInformationBean("Customer", SnapSharedUtils.getLastCustomerSyncTimestamp(activityContext),resultMap.get("customer")+""));
        SyncInfoList.add(new SyncInformationBean("Customer Payments", SnapSharedUtils.getLastCustomerPaymentSyncTimestamp(activityContext),resultMap.get("customerPayments")+""));
        SyncInfoList.add(new SyncInformationBean("Memo Bills",SnapSharedUtils.getLastTransactionSyncTimestamp(activityContext),resultMap.get("memoBills")+""));
        SyncInfoList.add(new SyncInformationBean("Invoice Bills", SnapSharedUtils.getLastInvoiceRetrievalSyncTimestamp(activityContext),resultMap.get("invoiceBills")+""));
        SyncInfoList.add(new SyncInformationBean("Bill Items", SnapSharedUtils.getLastBillItemSyncTimestamp(activityContext),resultMap.get("billItems")+""));
        SyncInfoList.add(new SyncInformationBean("Distributor", SnapSharedUtils.getLastDistributorSyncTimestamp(activityContext),resultMap.get("distributor")+""));
        SyncInfoList.add(new SyncInformationBean("Distributor Payments", SnapSharedUtils.getLastPaymentsSyncTimestamp(activityContext),resultMap.get("distributorPayments")+""));
        SyncInfoList.add(new SyncInformationBean("Orders", SnapSharedUtils.getLastOrderSyncTimestamp(activityContext),resultMap.get("orders")+""));
        SyncInfoList.add(new SyncInformationBean("Order Items", SnapSharedUtils.getLastOrderDetailsSyncTimestamp(activityContext),resultMap.get("orderItems")+""));
        //SyncInfoList.add(new SyncInformationBean("LastBatchSyncTime", "Date"));
        
        ArrayAdapter<SyncInformationBean> adapter = new SyncDataAdapter(getActivity(), SyncInfoList); 
        adapter.notifyDataSetChanged();
        builderSingle.setAdapter(adapter,null);
        adapter.notifyDataSetChanged();
        return builderSingle.create();
	}
    
}