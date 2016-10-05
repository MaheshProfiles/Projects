/*
package com.nerve24.doctor.ApiTask;


import android.app.Activity;

import com.nerve24.doctor.AppConfig.Constants_API;
import com.nerve24.doctor.Listeners.Listener_Save_Profile_Picture;
import com.nerve24.doctor.Listeners.Listener_Save_Slots;
import com.nerve24.doctor.Utility.CustomMultipartRequest;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;

*/
/**
 * Created by selva on 9/22/2016.
 *//*

public class API_Upload_Profile_Picture {

    private static final String MODULE = "API_Upload_Profile_Picture";
    private static String TAG = "", Str_Msg = "";

    private String Str_Url = Constants_API.ApiSave_ProfilePicture;
    private Activity mActivity;

    private Listener_Save_Profile_Picture mCallBack;
    private String fileURI, nerve24Id;


    public API_Upload_Profile_Picture(String fileURI, String nerve24Id, Listener_Save_Profile_Picture mCallBack, Activity mActivity) {
        this.fileURI = fileURI;
        this.nerve24Id = nerve24Id;
        this.mActivity = mActivity;
        this.mCallBack = mCallBack;
    }


    public void upload() {
        try {
            File final_file = new File(fileURI);
            MultipartEntity entity = new MultipartEntity();
            FileBody fileBody = new FileBody(final_file);
            entity.addPart("profilePicture", fileBody);
            entity.addPart("nerve24Id", new StringBody(nerve24Id));

            CustomMultipartRequest req = new CustomMultipartRequest(
                    Str_Url,  //url where you want to send the multipart request
                    mActivity, //instance of onErrorResponse Listener
                    mActivity,  //instance of onResponse Listener
                    entity);
            req.setShouldCache(false);
// add the request object to the queue to be executed
            MyApplication.getInstance().addToRequestQueue(req, tag);

        } catch (NoSuchMethodError e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
    }
}
*/
