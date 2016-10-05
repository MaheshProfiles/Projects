package com.snapbizz.snaptoolkit.services;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sqlcipher.database.SQLiteDatabase;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.snapbizz.snaptoolkit.asynctasks.StoreImageBitmapTask;
import com.snapbizz.snaptoolkit.domains.Order;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.domains.Request;
import com.snapbizz.snaptoolkit.domains.ResponseContainer;
import com.snapbizz.snaptoolkit.domains.StreamResponseContainer;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.interfaces.OnServiceCompleteListener;
import com.snapbizz.snaptoolkit.utils.RequestCodes;
import com.snapbizz.snaptoolkit.utils.RequestMethod;
import com.snapbizz.snaptoolkit.utils.ResponseFormat;
import com.snapbizz.snaptoolkit.utils.SnapBizzDatabaseHelper;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class SnapImageDownloadService extends IntentService implements OnServiceCompleteListener, OnQueryCompleteListener {

    private String accessToken;
    private String storeId;
    private String deviceId;
    private List<String> imageUrlQueue; 
    private static boolean isRunning;
    private Context commonContext;
    private Handler handler;

    public SnapImageDownloadService() {
        super("");
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onHandleIntent(Intent arg0) {
        // TODO Auto-generated method stub
        if(isRunning) {
            stopSelf();
            return;
        }
        isRunning = true;
        SQLiteDatabase.loadLibs(this);
        handler = new Handler();
        commonContext = SnapCommonUtils.getSnapContext(this);
        accessToken = SnapSharedUtils.getStoreAuthKey(commonContext);
        storeId = SnapSharedUtils.getStoreId(commonContext);
        deviceId = SnapSharedUtils.getDeviceId(commonContext);
        imageUrlQueue = new ArrayList<String>();
        Log.d("Store", "image service store id "+storeId+" "+"Device "+deviceId+" access token "+accessToken);
        SnapBizzDatabaseHelper databaseHelper = SnapCommonUtils.getDatabaseHelper(commonContext);
        try {
            List<ProductSku> productSkuList = databaseHelper.getProductSkuDao().queryBuilder().where().eq("is_gdb", false).and().isNotNull("product_imageurl").query();
            Log.d("Dl Image : ",productSkuList.size()+ " ");
            Request imageRequest = new Request();
            imageRequest.setRequestMethod(RequestMethod.GET);
            HashMap<String, String> requestParamMap = new HashMap<String, String>();
            requestParamMap.put(SnapToolkitConstants.STORE_ID, storeId);
            requestParamMap.put(SnapToolkitConstants.DEVICE_ID, deviceId);
            requestParamMap.put(SnapToolkitConstants.STORE_AUTH_KEY, accessToken);
            for(ProductSku productSku : productSkuList) {
                Log.d("Dl Image : ",productSku.getImageUrl());
                imageUrlQueue.add(productSku.getImageUrl());
                requestParamMap.put(SnapToolkitConstants.TABLET_DB_ID, productSku.getProductSkuCode());
                imageRequest.setRequestParams(requestParamMap);
                ServiceRequest serviceRequest = new ServiceRequest(imageRequest, this);
                serviceRequest.setMethod(SnapToolkitConstants.DOWNLOAD_METHOD);
                serviceRequest.setPath(SnapToolkitConstants.PRODUCTS_PATH);
                serviceRequest.setRequestCode(RequestCodes.REQUEST_CODE_ONE);
                serviceRequest.setResponsibleClass(StreamResponseContainer.class);
                serviceRequest.setResponseFormat(ResponseFormat.FILE);
                ServiceThread serviceThread = new ServiceThread(this, this, false);
                serviceThread.execute(serviceRequest);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //        List<Brand> brandList;
        //        try {
        //            brandList = databaseHelper.getBrandDao().queryBuilder().where().eq("is_gdb", false).and().isNotNull("brand_imageurl").query();
        //            ImageDownloadRequest imageRequest = new ImageDownloadRequest();
        //            imageRequest.setRequestMethod(RequestMethod.GET);
        //            HashMap<String, String> requestParamMap = new HashMap<String, String>();
        //            requestParamMap.put(SnapToolkitConstants.STORE_ID, storeId);
        //            requestParamMap.put(SnapToolkitConstants.DEVICE_ID, deviceId);
        //            requestParamMap.put(SnapToolkitConstants.STORE_AUTH_KEY, accessToken);
        //            for(Brand brand : brandList) {
        //                imageUrlQueue.add(brand.getBrandImageUrl());
        //                requestParamMap.put(SnapToolkitConstants.TABLET_DB_ID, brand.getBrandId()+"");
        //                imageRequest.setRequestParams(requestParamMap);
        //                imageRequest.setImageUrl(brand.getBrandImageUrl());
        //                ServiceRequest serviceRequest = new ServiceRequest(imageRequest, this);
        //                serviceRequest.setMethod(SnapToolkitConstants.DOWNLOAD_METHOD);
        //                serviceRequest.setPath(SnapToolkitConstants.BRANDS_PATH);
        //                serviceRequest.setRequestCode(RequestCodes.REQUEST_CODE_ONE);
        //                serviceRequest.setResponsibleClass(StreamResponseContainer.class);
        //                serviceRequest.setResponseFormat(ResponseFormat.FILE);
        //                ServiceThread serviceThread = new ServiceThread(this, this, false);
        //                serviceThread.execute(serviceRequest);
        //            }
        //        } catch (SQLException e) {
        //            // TODO Auto-generated catch block
        //            e.printStackTrace();
        //        }
        List<Order> orderList;
        try {
            orderList = databaseHelper.getOrderDao().queryBuilder().where().isNotNull("invoice_imageurl").query();
            Request imageRequest = new Request();
            imageRequest.setRequestMethod(RequestMethod.GET);
            HashMap<String, String> requestParamMap = new HashMap<String, String>();
            requestParamMap.put(SnapToolkitConstants.STORE_ID, storeId);
            requestParamMap.put(SnapToolkitConstants.DEVICE_ID, deviceId);
            requestParamMap.put(SnapToolkitConstants.STORE_AUTH_KEY, accessToken);
            for(Order order : orderList) {
                if(order.getImage() != null) {
                    imageUrlQueue.add(order.getImage());
                    requestParamMap.put(SnapToolkitConstants.TABLET_DB_ID, order.getOrderNumber()+"");
                    imageRequest.setRequestParams(requestParamMap);
                    ServiceRequest serviceRequest = new ServiceRequest(imageRequest, this);
                    serviceRequest.setMethod(SnapToolkitConstants.DOWNLOAD_METHOD);
                    serviceRequest.setPath(SnapToolkitConstants.PO_PATH);
                    serviceRequest.setRequestCode(RequestCodes.REQUEST_CODE_ONE);
                    serviceRequest.setResponsibleClass(StreamResponseContainer.class);
                    serviceRequest.setResponseFormat(ResponseFormat.FILE);
                    ServiceThread serviceThread = new ServiceThread(this, this, false);
                    serviceThread.execute(serviceRequest);
                }
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onSuccess(ResponseContainer response) {
        // TODO Auto-generated method stub
        if(response.getRequestCode().ordinal() == RequestCodes.REQUEST_CODE_ONE.ordinal()) {
            StreamResponseContainer imageDownloadResponseContainer = (StreamResponseContainer) response;
            // TODO Auto-generated method stub
            new StoreImageBitmapTask(this, this, imageDownloadResponseContainer.getInputStream(), imageUrlQueue.remove(0), 0).execute();
        }
    }


    @Override
    public void onError(ResponseContainer response, RequestCodes requestCode) {
        // TODO Auto-generated method stub
        imageUrlQueue.remove(0);
    }

    @Override
    public void onTaskSuccess(Object responseList, int taskCode) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        isRunning = false;
    }

    @Override
    public void onTaskError(String errorMessage, int taskCode) {
        // TODO Auto-generated method stub
        Log.d(SnapImageDownloadService.class.getName(), errorMessage);
    }


}
