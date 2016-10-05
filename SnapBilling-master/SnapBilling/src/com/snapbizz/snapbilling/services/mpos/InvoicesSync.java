package com.snapbizz.snapbilling.services.mpos;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.snapbizz.snapbilling.services.MultiPos.ServerSyncService;
import com.snapbizz.snaptoolkit.db.SnapbizzDB;

import fi.iki.elonen.NanoHTTPD.Method;

import com.snapbizz.v2.sync.LocalSyncData.ApiInvoice;

import android.util.Log;

public class InvoicesSync extends ServerSyncService {
	private static final String TAG = "[InvoicesSync]";
	private static final String URI = BASE_URL_V2+"invoices"; 
	private SnapbizzDB db = null;

	public InvoicesSync(SnapbizzDB db) {
		this.db = db;
	}

	@Override
	public String processData(Method method, String json) {
		Gson gson = new Gson();
		String response = getJsonStatus("Unknown Error");
		if(!Method.POST.equals(method))
			return null;
		List<ApiInvoice> invoices = null;
		try {
			invoices = gson.fromJson(json, new TypeToken<List<ApiInvoice>>(){}.getType());
		} catch(Exception e) {
			e.printStackTrace();
		}
		if(invoices != null) {
			Log.d(TAG, "Received "+invoices.size()+" invoices");
			for(ApiInvoice invoice : invoices) {
				// TODO: Insert the invoice into database
				// TODO: Also reduce inventory (as in Billing)
			}
			response = getJsonStatus(null);
		}
		return response;
	}

	@Override
	public String getListenUri() {
		return URI;
	}
}
