package com.nerve24.doctor.Activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.nerve24.doctor.R;
import com.nerve24.doctor.SessionManager.Session;
import com.nerve24.doctor.Utility.Utils;

import io.fabric.sdk.android.Fabric;

/**
 * Created by mahesha on 23-08-16.
 */
public class ActivityUploadPrescription extends AppCompatActivity implements View.OnClickListener {
    private static int RESULT_LOAD_IMG = 1;
    String imgDecodableString;
    private String TAG = "", MODULE = "ActivityUploadPrescription";
    private Activity mActivity;
    private Utils utils;
    private Session session;
    private ImageView ivUseCamera;
    private ImageView ivPhotoGallery;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_prescription);
        initUI();
        Fabric.with(this, new Crashlytics());
    }

    private void initUI() {
        TAG = "initUI";
        mActivity = ActivityUploadPrescription.this;
        session = new Session(mActivity);
        utils = new Utils(mActivity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Upload Prescription");
        ImageView mCancel = (ImageView) toolbar.findViewById(R.id.toolbar_cancel);
        mCancel.setOnClickListener(this);
        ivPhotoGallery = (ImageView) findViewById(R.id.iv_use_gallery);
        ivUseCamera = (ImageView) findViewById(R.id.iv_use_camera);
        initListeners();
    }

    private void initListeners() {
        try {
            TAG = "initListeners";
            ivUseCamera.setOnClickListener(this);
            ivPhotoGallery.setOnClickListener(this);
        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return false;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_use_camera:
                openCamera();
                break;
            case R.id.iv_use_gallery:
                loadImagefromGallery(view);
                break;
            case R.id.toolbar_cancel:
                finish();
                break;
        }
    }

    public void loadImagefromGallery(View view) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    public void openCamera() {
        Intent intent = new Intent(ActivityUploadPrescription.this, ActivityAddingPrescription.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null != data) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                Log.e("imgDecodableString", imgDecodableString);
//                ImageView imgView = (ImageView) findViewById(R.id.imgView);
//                imgView.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));
            } else {
                Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }
}
