package com.nerve24.doctor.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.nerve24.doctor.ApiTask.API_Get_AppointmentFor;
import com.nerve24.doctor.ApiTask.API_Get_AppointmentStatus;
import com.nerve24.doctor.ApiTask.API_Get_AppointmentType;
import com.nerve24.doctor.ApiTask.API_Get_Clinics;
import com.nerve24.doctor.ApiTask.API_Get_Doctor;
import com.nerve24.doctor.ApiTask.API_Get_Encounter;
import com.nerve24.doctor.ApiTask.API_Get_Gender;
import com.nerve24.doctor.ApiTask.API_Get_PaymentMethod;
import com.nerve24.doctor.ApiTask.API_Get_Profile_Picture;
import com.nerve24.doctor.ApiTask.API_Get_Salutation;
import com.nerve24.doctor.AppConfig.Constants;
import com.nerve24.doctor.AppConfig.Constants_API;
import com.nerve24.doctor.Fragments.FragmentCalendarDayView;
import com.nerve24.doctor.Fragments.FragmentCalendarWeekView;
import com.nerve24.doctor.Fragments.FragmentChoosePriceOrSlotSetup;
import com.nerve24.doctor.Fragments.FragmentPartnerSetup;
import com.nerve24.doctor.Fragments.FragmentPatientHistory;
import com.nerve24.doctor.Fragments.FragmentSendReferral;
import com.nerve24.doctor.Listeners.Listener_Get_Clinics;
import com.nerve24.doctor.Listeners.Listener_Get_Profile_Picture;
import com.nerve24.doctor.R;
import com.nerve24.doctor.SessionManager.Session;
import com.nerve24.doctor.Utility.CustomMultipartRequest;
import com.nerve24.doctor.Utility.Utils;
import com.nerve24.doctor.pojo.Clinic;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;

public class ActivityMain extends AppCompatActivity
        implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, Listener_Get_Clinics, Listener_Get_Profile_Picture, Response.ErrorListener, Response.Listener<String> {
    private String TAG = "", MODULE = "ActivityMain";
    private Activity mActivity;
    private Toolbar toolbar;
    private TextView tvName;
    private ImageView ivProfile, ivProfileSmall;
    private Utils utils;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        initUI();
        Fabric.with(this, new Crashlytics());
        EasyImage.configuration(this)
                .saveInAppExternalFilesDir()
                .setCopyExistingPicturesToPublicLocation(true);
    }

    private void initUI() {
        try {
            TAG = "initUI";
            mActivity = ActivityMain.this;
            utils = new Utils(mActivity);
            session = new Session(mActivity);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle("Onboarding");
            setSupportActionBar(toolbar);

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            View headerView = navigationView.inflateHeaderView(R.layout.nav_header_activity_main);
            navigationView.setItemIconTintList(null);

            tvName = (TextView) headerView.findViewById(R.id.tv_name);
            ivProfile = (ImageView) headerView.findViewById(R.id.iv_profile);
            ivProfileSmall = (ImageView) headerView.findViewById(R.id.iv_profile_small);
            //ivProfile.setOnClickListener(this);
            //ivProfileSmall.setOnClickListener(this);
            String fullName = session.getFullName();
            String uName = session.getUserName();
            String salutation = session.getSalutationUser();
            if (fullName != null)
                fullName = fullName.length() > 1 ? fullName : uName;
            if (salutation != null)
                fullName = salutation.length() > 1 && !salutation.equals("null")? salutation + " " + fullName : fullName;

            tvName.setText(fullName);

            navigation();
            navigationView.setCheckedItem(R.id.nav_price_setup);
            getValues();

        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
    }

    private void navigation() {
        try {
            String screenList[] = getResources().getStringArray(R.array.screen_names);
            String navigatePage = session.getScreenName();
            Log.e("navigatePage", "" + navigatePage);
            if (navigatePage.equals(screenList[0]) || navigatePage.equals(screenList[1])) {
                goToCalendar();
            } else if (navigatePage.equals(screenList[2]) || navigatePage.equals(screenList[3])) {
                goToCalendar();
            } else if (navigatePage.equals(screenList[4]) || navigatePage.equals(screenList[5])) {
                goToCalendarWeekView();
            } else if (navigatePage.equals(screenList[6])) {
                goToPriceSetup();
            } else if (navigatePage.equals(screenList[7])) {
                goToPatientHistory();
            } else if (navigatePage.equals(screenList[8])) {
                goToCrossReference();
            } else {
                goToPriceSetup();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getValues() {
        try {
            new API_Get_AppointmentStatus(mActivity).getAppointmentStatus();
            new API_Get_Salutation(mActivity).getSalutation();
            new API_Get_Doctor(mActivity).getDoctors();
            new API_Get_Encounter(mActivity).getEncounter();
            new API_Get_Gender(mActivity).getGender();
            new API_Get_AppointmentType(mActivity).getAppointmentType();
            new API_Get_AppointmentFor(mActivity).getAppointmentFor();
            String nerve24Id = session.getNerve24Id();
            new API_Get_Clinics(nerve24Id, ActivityMain.this, mActivity).getClinics();
            new API_Get_PaymentMethod(mActivity).getPaymentMethods();
            getProfilePicture();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getProfilePicture() {
        try {
            utils.showProgress("", "loading..");
            String nerve24Id = session.getNerve24Id();
            API_Get_Profile_Picture apiGetProfilePicture = new API_Get_Profile_Picture(nerve24Id, ActivityMain.this, mActivity);
            apiGetProfilePicture.getPicture();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void setTitle(String title) {
        toolbar.setTitle(title);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        try {
            TAG = "onNavigationItemSelected";
            int id = item.getItemId();
            if (id == R.id.nav_calendar) {
                goToCalendar();
            } else if (id == R.id.nav_patient_history) {
                goToPatientHistory();
            } else if (id == R.id.nav_partner_setup) {
                goToPartnerSetup();
            } else if (id == R.id.nav_cross_ref) {
                goToCrossReference();
            } else if (id == R.id.nav_upload_presc) {
                goToUploadPrescription();
            } else if (id == R.id.nav_price_setup) {
                goToPriceSetup();
            } else if (id == R.id.nav_account_settings) {
            } else if (id == R.id.nav_change_password) {
                goToChangePassword();
            } else if (id == R.id.nav_sign_out) {
                logout();
            }

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
        return true;
    }

    private void goToCalendar() {
        Fragment fragment = FragmentCalendarDayView.newInstance();
        setFragment(fragment, Constants.FragmentCalendarDayView);
    }

    private void goToCalendarWeekView() {
        Fragment fragment = FragmentCalendarWeekView.newInstance();
        setFragment(fragment, Constants.FragmentCalendarWeekView);
    }

    private void goToPatientHistory() {
        Fragment fragment = new FragmentPatientHistory();
        setFragment(fragment, "");
    }

    private void goToPartnerSetup() {
        Fragment fragment = new FragmentPartnerSetup();
        setFragment(fragment, "");
    }

    private void goToCrossReference() {
        Fragment fragment = new FragmentSendReferral();
        setFragment(fragment, "");
    }

    private void goToUploadPrescription() {
        Intent uploadPrescription = new Intent(mActivity, ActivityUploadPrescription.class);
        startActivity(uploadPrescription);
    }

    private void goToChangePassword() {
        Intent i11 = new Intent(mActivity, ActivityChangePassword.class);
        startActivity(i11);
    }

    private void goToPriceSetup() {
        Fragment fragment = new FragmentChoosePriceOrSlotSetup();
        setFragment(fragment, "");
    }


    private void setFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, fragment, tag);
        fragmentTransaction.commit();
    }


    private void logout() {
        session.clearAll();
        Intent i11 = new Intent(mActivity, ActivityLogin.class);
        finish();
        startActivity(i11);
    }

    public void upload(File final_file) {
        try {
            MultipartEntity entity = new MultipartEntity();
            FileBody fileBody = new FileBody(final_file);
            entity.addPart("profilePicture", fileBody);
            String nerve24Id = session.getNerve24Id();
            entity.addPart("nerve24Id", new StringBody(nerve24Id));

            CustomMultipartRequest req = new CustomMultipartRequest(
                    Constants_API.ApiSave_ProfilePicture,
                    ActivityMain.this,
                    ActivityMain.this,
                    entity);
            req.setShouldCache(false);

            int socketTimeout = 60000;// 30 seconds - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            req.setRetryPolicy(policy);
            Volley.newRequestQueue(mActivity).add(req);

        } catch (NoSuchMethodError e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void onGetClinics(ArrayList<Clinic> clinicsList) {
    }

    @Override
    public void onGetClinicsError(String res) {
    }

    @Override
    public void onGetPicture(String res) {
        utils.hideProgress();
        res = res.substring(res.indexOf(",") + 1);
        byte[] decodedString = Base64.decode(res, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        ivProfile.setImageBitmap(decodedByte);
        ivProfileSmall.setImageBitmap(decodedByte);
    }

    @Override
    public void onGetPictureError(String res) {
        utils.hideProgress();
    }


    @Override
    public void onErrorResponse(VolleyError error) {
        utils.hideProgress();
        error.printStackTrace();
    }

    @Override
    public void onResponse(String response) {
        utils.hideProgress();
        Log.e("onSave Picture", "" + response);
    }

    private void selectOption() {
        final String[] array = {"Camera", "Gallery"};
        new MaterialDialog.Builder(mActivity)
                .title("Select option")
                .items(array)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (which >= 0) {
                            String value = text.toString();
                            if (value.equals(array[0])) {
                                onTakePhotoClicked();
                            } else {
                                onPickFromGaleryClicked();
                            }

                        }
                        return true;
                    }

                })
                .positiveText("Ok")
                .negativeText("Cancel")
                .show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    protected void onTakePhotoClicked() {

        /**Permission check only required if saving pictures to root of sdcard*/
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            EasyImage.openCamera(this, 0);
        } else {
            Nammu.askForPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, new PermissionCallback() {
                @Override
                public void permissionGranted() {
                    EasyImage.openCamera(mActivity, 0);
                }

                @Override
                public void permissionRefused() {

                }
            });
        }
    }


    protected void onPickFromGaleryClicked() {
        EasyImage.openGallery(this, 0);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
            }

            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                //Handle the image
                upload(imageFile);
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                //Cancel handling, you might wanna remove taken photo if it was canceled
                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(mActivity);
                    if (photoFile != null) photoFile.delete();
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        EasyImage.clearConfiguration(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_profile:
                selectOption();
                break;
            case R.id.iv_profile_small:
                selectOption();
                break;
        }
    }
}
