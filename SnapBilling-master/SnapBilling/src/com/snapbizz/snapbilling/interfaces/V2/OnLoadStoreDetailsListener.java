package com.snapbizz.snapbilling.interfaces.V2;

import com.snapbizz.v2.sync.LocalSyncData.ApiDeviceRegistrationResponse;

public interface OnLoadStoreDetailsListener {
	public void loadAndSaveStoreDetails(ApiDeviceRegistrationResponse apiDeviceRegistrationResponse);
}
