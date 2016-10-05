package com.nerve24.doctor.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.nerve24.doctor.Adapter.AdapterSlotEditPager;
import com.nerve24.doctor.ApiTask.API_Save_Slots;
import com.nerve24.doctor.Fragments.Fragment_Slot_Edit;
import com.nerve24.doctor.Listeners.Listener_Save_Slots;
import com.nerve24.doctor.R;
import com.nerve24.doctor.SessionManager.Session;
import com.nerve24.doctor.Utility.Utils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import io.fabric.sdk.android.Fabric;

public class ActivitySlotEdit extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener, Listener_Save_Slots {
    private String TAG = "", MODULE = "ActivitySlotEdit";
    private ViewPager pager;
    private Activity mActivity;
    private Utils utils;
    private Session session;
    private EditText etValidFrom;
    public Button btnMon, btnTue, btnWed, btnThu, btnFri, btnSat, btnSun, btnDone, btnCancel;
    public ArrayList<String> daysList = new ArrayList<>();
    private int colorDarkGreen, colorLightGreen, colorGrey;
    private String clinicId, clinicName, conflictClinicName, conflictDay, validFrom = "";
    public JSONArray jsonArraySlot,jsonArraySlotConstant;
    public JSONObject jsonObjectSlot, jsonObjectSlotConstant;
    public boolean sameForAlldays = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slot_edit);
        initUI();
        Fabric.with(this, new Crashlytics());
    }

    private void initUI() {
        try {
            TAG = "initUI";
            mActivity = ActivitySlotEdit.this;
            utils = new Utils(mActivity);
            session = new Session(mActivity);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            btnMon = (Button) findViewById(R.id.btn_mon);
            btnTue = (Button) findViewById(R.id.btn_tue);
            btnWed = (Button) findViewById(R.id.btn_wed);
            btnThu = (Button) findViewById(R.id.btn_thu);
            btnFri = (Button) findViewById(R.id.btn_fri);
            btnSat = (Button) findViewById(R.id.btn_sat);
            btnSun = (Button) findViewById(R.id.btn_sun);
            btnDone = (Button) findViewById(R.id.btn_save);
            btnCancel = (Button) findViewById(R.id.btn_cancel);
            etValidFrom = (EditText) findViewById(R.id.et_valid_from);
            pager = (ViewPager) findViewById(R.id.viewpager);

            initListeners();
            getAndSetValues();
        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
    }


    private void initListeners() {
        try {
            TAG = "initListeners";
            btnMon.setOnClickListener(this);
            btnTue.setOnClickListener(this);
            btnWed.setOnClickListener(this);
            btnThu.setOnClickListener(this);
            btnFri.setOnClickListener(this);
            btnSat.setOnClickListener(this);
            btnSun.setOnClickListener(this);
            btnDone.setOnClickListener(this);
            btnCancel.setOnClickListener(this);
            etValidFrom.setOnClickListener(this);
            etValidFrom.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        selectValidFrom();
                    }
                }
            });

        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
    }

    private void selectValidFrom() {

        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                ActivitySlotEdit.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.YEAR, 100);
        dpd.setMaxDate(maxDate);
        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.DAY_OF_MONTH, 1);
        dpd.setMinDate(minDate);
        dpd.show(getFragmentManager(), "Datepickerdialog");

    }

    private void getAndSetValues() {
        try {
            TAG = "getAndSetValues";
            String[] dayArray = getResources().getStringArray(R.array.array_days_full_name_lowercase);
            daysList = new ArrayList<>(Arrays.asList(dayArray));

            colorDarkGreen = getResources().getColor(R.color.colorPrimaryDark);
            colorLightGreen = getResources().getColor(R.color.colorPrimary);
            colorGrey = getResources().getColor(android.R.color.darker_gray);

            clinicId = getIntent().getStringExtra("clinicId");
            String strJson = session.getSlots();
            jsonArraySlot = new JSONArray(strJson);
            jsonArraySlotConstant= new JSONArray(strJson);
            replaceNullToArray(strJson);
            for (int i = 0; i < jsonArraySlot.length(); i++) {
                String cId = jsonArraySlot.getJSONObject(i).getString("clinicId");
                if (cId.equals(clinicId)) {
                    clinicName = jsonArraySlot.getJSONObject(i).getString("clinicName");
                    setTitle(clinicName);
                    jsonObjectSlot = jsonArraySlot.getJSONObject(i);
                    jsonObjectSlotConstant = jsonArraySlotConstant.getJSONObject(i);
                    break;
                }
            }

            getSameForAllDay();
            setColorForDays();
            setTabs();
            setDarkGreenForSelectedday(daysList.get(0));

        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }

    }

    private void replaceNullToArray(String res) {
        try {
            TAG = "replaceNullToArray";
            JSONArray response = new JSONArray(res);
            for (int i = 0; i < response.length(); i++) {
                JSONObject jsonObject = response.getJSONObject(i);
                JSONObject timeObj = jsonObject.getJSONObject("timingShedule");
                for (int j = 0; j < daysList.size(); j++) {
                    if (timeObj.get(daysList.get(j)) instanceof JSONArray) {

                    } else {
                        JSONArray jsonArray = new JSONArray();
                        timeObj.put(daysList.get(j), jsonArray);
                    }
                }
            }
            jsonArraySlot = response;
        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
    }

    private void getSameForAllDay() {
        try {
            if (jsonObjectSlot.has("sameForAllDays")) {
                if (jsonObjectSlot.getBoolean("sameForAllDays"))
                    sameForAlldays = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setColorForDays() {
        try {
            TAG = "setColorForDays";
            JSONObject timingSheduleObj = jsonObjectSlot.getJSONObject("timingShedule");
            setAllButtonsAsGrey();
            for (int i = 0; i < daysList.size(); i++) {
                if (timingSheduleObj.get(daysList.get(i)) instanceof JSONArray) {
                    JSONArray dayArray = timingSheduleObj.getJSONArray(daysList.get(i));
                    if (daysList.get(i).equals(daysList.get(0)) && dayArray.length() > 0)
                        btnMon.setBackgroundColor(colorLightGreen);
                    else if (daysList.get(i).equals(daysList.get(1)) && dayArray.length() > 0)
                        btnTue.setBackgroundColor(colorLightGreen);
                    else if (daysList.get(i).equals(daysList.get(2)) && dayArray.length() > 0)
                        btnWed.setBackgroundColor(colorLightGreen);
                    else if (daysList.get(i).equals(daysList.get(3)) && dayArray.length() > 0)
                        btnThu.setBackgroundColor(colorLightGreen);
                    else if (daysList.get(i).equals(daysList.get(4)) && dayArray.length() > 0)
                        btnFri.setBackgroundColor(colorLightGreen);
                    else if (daysList.get(i).equals(daysList.get(5)) && dayArray.length() > 0)
                        btnSat.setBackgroundColor(colorLightGreen);
                    else if (daysList.get(i).equals(daysList.get(6)) && dayArray.length() > 0)
                        btnSun.setBackgroundColor(colorLightGreen);

                }
            }
        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
    }

    private void setAllButtonsAsGrey() {
        try {
            TAG = "setAllButtonsAsGrey";
            btnMon.setBackgroundColor(colorGrey);
            btnTue.setBackgroundColor(colorGrey);
            btnWed.setBackgroundColor(colorGrey);
            btnThu.setBackgroundColor(colorGrey);
            btnFri.setBackgroundColor(colorGrey);
            btnSat.setBackgroundColor(colorGrey);
            btnSun.setBackgroundColor(colorGrey);
        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
    }


    public void setDarkGreenForSelectedday(String selectedDay) {
        try {
            setColorForDays();
            TAG = "setAllButtonsAsGreen";

            if (selectedDay.equals(daysList.get(0)))
                btnMon.setBackgroundColor(colorDarkGreen);
            else if (selectedDay.equals(daysList.get(1)))
                btnTue.setBackgroundColor(colorDarkGreen);
            else if (selectedDay.equals(daysList.get(2)))
                btnWed.setBackgroundColor(colorDarkGreen);
            else if (selectedDay.equals(daysList.get(3)))
                btnThu.setBackgroundColor(colorDarkGreen);
            else if (selectedDay.equals(daysList.get(4)))
                btnFri.setBackgroundColor(colorDarkGreen);
            else if (selectedDay.equals(daysList.get(5)))
                btnSat.setBackgroundColor(colorDarkGreen);
            else if (selectedDay.equals(daysList.get(6)))
                btnSun.setBackgroundColor(colorDarkGreen);
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
            case R.id.btn_save:
                saveSlots();
                break;
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.et_valid_from:
                selectValidFrom();
                break;

            case R.id.btn_sun:
                pager.setCurrentItem(6);
                break;
            case R.id.btn_mon:
                pager.setCurrentItem(0);
                break;
            case R.id.btn_tue:
                pager.setCurrentItem(1);
                break;
            case R.id.btn_wed:
                pager.setCurrentItem(2);
                break;
            case R.id.btn_thu:
                pager.setCurrentItem(3);
                break;
            case R.id.btn_fri:
                pager.setCurrentItem(4);
                break;
            case R.id.btn_sat:
                pager.setCurrentItem(5);
                break;
        }

    }


    private void setTabs() {
        try {
            TAG = "setTabs";
            if (getSupportFragmentManager().getFragments() != null) {
                getSupportFragmentManager().getFragments().clear();
            }

            final AdapterSlotEditPager adapterSlotEditPager = new AdapterSlotEditPager(getSupportFragmentManager(),
                    daysList);
            pager.setAdapter(adapterSlotEditPager);
            pager.setOffscreenPageLimit(1);
            pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    setDarkGreenForSelectedday(daysList.get(position));
                    Fragment_Slot_Edit fragment = (Fragment_Slot_Edit) adapterSlotEditPager.instantiateItem(pager, position);
                    fragment.setValues();
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }

    }


    public synchronized void removeJSONobject(String day, int index) {
        try {
            TAG = "removeJSONobject";
            JSONObject timingSheduleObj = jsonObjectSlot.getJSONObject("timingShedule");
            JSONArray dayArray = timingSheduleObj.getJSONArray(day);

            if ((index == 0 && dayArray.length() > 0) || (index == 1 && dayArray.length() > 1)) {
                dayArray = Utils.remove(index, dayArray);
                timingSheduleObj.remove(day);
                timingSheduleObj.put(day, dayArray);
            }
            Log.e("timingSheduleObj", "" + timingSheduleObj);
        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
    }

    public void removeAll(String day) {
        removeJSONobject(day, 1);
        removeJSONobject(day, 0);
    }

    public void changeArray(String day, int pos, String paramName, Object value) {
        Log.e("paramName", "" + paramName);
        try {
            if (!day.equals(daysList.get(0)))
                sameForAllDays(false);

            int length = jsonObjectSlot
                    .getJSONObject("timingShedule")
                    .getJSONArray(day).length();
            if (pos == 1 && length == 2) {
                jsonObjectSlot
                        .getJSONObject("timingShedule")
                        .getJSONArray(day)
                        .getJSONObject(pos)
                        .put("checked", true);
            }

            if ((pos == 0 && length > 0) || (pos == 1 && length == 2)) {
                jsonObjectSlot
                        .getJSONObject("timingShedule")
                        .getJSONArray(day)
                        .getJSONObject(pos)
                        .put(paramName, value);

            } else {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("slotId", 0);
                jsonObject.put("duration", 0);
                jsonObject.put("startTime", "");
                jsonObject.put("endTime", "");
                jsonObject.put("fee", 0);
                jsonObject.put("premium", false);
                jsonObject.put("checked", false);
                jsonObject.put("parentId", 0);
                jsonObject.put(paramName, value);
                jsonObjectSlot
                        .getJSONObject("timingShedule")
                        .getJSONArray(day).put(pos, jsonObject);
            }

            if (day.equals(daysList.get(0)) && sameForAlldays)
                sameForAllDays(true);

            Log.e("jsonObjectSlot", "" + jsonObjectSlot);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sameForAllDays(boolean flag) {
        try {
            sameForAlldays = flag;
            jsonObjectSlot.put("sameForAllDays", flag);
            if (flag) {
                JSONArray slotArray = jsonObjectSlot
                        .getJSONObject("timingShedule")
                        .getJSONArray(daysList.get(0));
                if (slotArray.length() > 0) {
                    replaceFirstSlot();
                }
                if (slotArray.length() == 2) {
                    replaceSecondSlot();
                } else {
                    replaceEmptyForAllSecondSlot();
                }

                setDarkGreenForSelectedday(daysList.get(0));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void replaceEmptyForAllSecondSlot() {
        try {
            for (int i = 1; i < daysList.size(); i++) {
                String dayName = daysList.get(i);

                if (jsonObjectSlot
                        .getJSONObject("timingShedule")
                        .getJSONArray(dayName).length() == 2) {
                    JSONArray jsonArray = Utils.remove(1, jsonObjectSlot
                            .getJSONObject("timingShedule")
                            .getJSONArray(dayName));
                    Log.e("jsonArray", "" + jsonArray);
                    jsonObjectSlot
                            .getJSONObject("timingShedule").put(dayName, jsonArray);
                }
            }
            Log.e("jsonObjectSlot", "" + jsonObjectSlot);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void replaceFirstSlot() {
        try {
            JSONObject slot1 = jsonObjectSlot
                    .getJSONObject("timingShedule")
                    .getJSONArray(daysList.get(0)).getJSONObject(0);
            String startTime = slot1.getString("startTime");
            String endTime = slot1.getString("endTime");
            int duration = slot1.getInt("duration");
            boolean checked = slot1.getBoolean("checked");


            for (int i = 1; i < daysList.size(); i++) {
                String dayName = daysList.get(i);
                JSONArray slotArray = jsonObjectSlot
                        .getJSONObject("timingShedule")
                        .getJSONArray(dayName);
                JSONObject slot = null;
                if (slotArray.length() > 0) {
                    JSONObject slotdup = slotArray.getJSONObject(0);
                    slot = new JSONObject();
                    slot.put("slotId", slotdup.getInt("slotId"));
                    slot.put("checked", checked);
                    slot.put("duration", duration);
                    slot.put("startTime", startTime);
                    slot.put("endTime", endTime);
                    slot.put("fee", slotdup.getInt("fee"));
                    slot.put("premium", slotdup.getBoolean("premium"));
                    slot.put("parentId", slotdup.getInt("parentId"));
                } else {
                    slot = new JSONObject();
                    slot.put("slotId", 0);
                    slot.put("checked", checked);
                    slot.put("duration", duration);
                    slot.put("startTime", startTime);
                    slot.put("endTime", endTime);
                    slot.put("fee", 0);
                    slot.put("premium", false);
                    slot.put("parentId", 0);
                }

                jsonObjectSlot
                        .getJSONObject("timingShedule")
                        .getJSONArray(dayName).put(0, slot);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void replaceSecondSlot() {
        try {
            JSONObject slot2 = jsonObjectSlot
                    .getJSONObject("timingShedule")
                    .getJSONArray(daysList.get(0)).getJSONObject(1);
            String startTime = slot2.getString("startTime");
            String endTime = slot2.getString("endTime");
            int duration = slot2.getInt("duration");
            boolean checked = slot2.getBoolean("checked");


            for (int i = 1; i < daysList.size(); i++) {
                String dayName = daysList.get(i);
                JSONArray slotArray = jsonObjectSlot
                        .getJSONObject("timingShedule")
                        .getJSONArray(dayName);
                JSONObject slot = null;
                if (slotArray.length() > 1) {
                    JSONObject slotdup = slotArray.getJSONObject(1);
                    slot = new JSONObject();
                    slot.put("slotId", slotdup.getInt("slotId"));
                    slot.put("checked", checked);
                    slot.put("duration", duration);
                    slot.put("startTime", startTime);
                    slot.put("endTime", endTime);
                    slot.put("fee", slotdup.getInt("fee"));
                    slot.put("premium", slotdup.getBoolean("premium"));
                    slot.put("parentId", slotdup.getInt("parentId"));
                } else {
                    slot = new JSONObject();
                    slot.put("slotId", 0);
                    slot.put("checked", checked);
                    slot.put("duration", duration);
                    slot.put("startTime", startTime);
                    slot.put("endTime", endTime);
                    slot.put("fee", 0);
                    slot.put("premium", false);
                    slot.put("parentId", 0);
                }

                jsonObjectSlot
                        .getJSONObject("timingShedule")
                        .getJSONArray(dayName).put(1, slot);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveSlots() {
        try {
            TAG = "saveSlots";
            modifySlotId();
            if (validation()) {
                try {
                    utils.showProgress("", "loading..");
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("slotSetupDtoList", jsonArraySlot);
                    jsonObject.put("isUserConfirmed", false);
                    API_Save_Slots apiSaveSlots = new API_Save_Slots(jsonObject, ActivitySlotEdit.this, mActivity);
                    apiSaveSlots.save();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
    }

    private boolean validation() {
        boolean flag = false;
        try {
            TAG = "validation";
            if (validFrom.length() == 0) {
                Utils.alertBox(mActivity, "Please select Valid From Date for " + clinicName);
            } else if (!checkEmpty()) {
            } else if (checkConflict()) {
                String mes = "Slots for " + conflictClinicName + " - " + conflictDay + " and " + clinicName + " - " + conflictDay + " overlap";
                Utils.alertBox(mActivity, mes);
            } else {
                flag = true;
            }
        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }

        return flag;
    }

    private boolean checkEmpty() {
        boolean flag = false;
        try {
            for (int i = 0; i < daysList.size(); i++) {
                String dayName = daysList.get(i);
                JSONArray slotArray = jsonObjectSlot
                        .getJSONObject("timingShedule")
                        .getJSONArray(dayName);

                if (slotArray.length() > 0) {
                    JSONObject slot1 = slotArray.getJSONObject(0);
                    String startTime = slot1.getString("startTime");
                    String endTime = slot1.getString("endTime");
                    boolean checked = slot1.getBoolean("checked");
                    int duration=slot1.getInt("duration");

                    if (duration==0) {
                        Utils.alertBox(mActivity, "Select duration for " + dayName);
                        break;
                    }
                    else if (startTime.length() == 0 && checked) {
                        Utils.alertBox(mActivity, "From time is empty for " + dayName);
                        break;
                    } else if (endTime.length() == 0 && checked) {
                        Utils.alertBox(mActivity, "To time is empty for " + dayName);
                        break;
                    } else if (slotArray.length() > 1) {
                        JSONObject slot2 = slotArray.getJSONObject(1);
                        String startTime1 = slot2.getString("startTime");
                        String endTime1 = slot2.getString("endTime");
                        int duration2=slot2.getInt("duration");

                        if (duration2==0) {
                            Utils.alertBox(mActivity, "Select duration for " + dayName);
                            break;
                        }
                        else
                        if (startTime1.length() == 0 && checked) {
                            Utils.alertBox(mActivity, "From time is empty for " + dayName);
                            break;
                        } else if (endTime1.length() == 0 && checked) {
                            Utils.alertBox(mActivity, "To time is empty for " + dayName);
                            break;
                        } else {
                            flag = true;
                        }
                    } else {
                        flag = true;
                    }
                }

                else
                {
                    JSONArray slotArrayConstant = jsonObjectSlotConstant
                            .getJSONObject("timingShedule")
                            .getJSONArray(dayName);
                    if (slotArrayConstant.length() > 0) {
                        flag = true;
                    }
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    private boolean checkConflict() {
        boolean flag = false;
        try {
            TAG = "checkConflict";
            for (int i = 0; i < jsonArraySlot.length(); i++) {
                String cId = jsonArraySlot.getJSONObject(i).getString("clinicId");
                if (cId.equals(clinicId)) {
                    jsonObjectSlot = jsonArraySlot.getJSONObject(i);
                    break;
                }
            }

            JSONArray array = jsonArraySlot;
            if (array.length() > 0) {
                Log.e("jsonObjectSlot", "" + jsonObjectSlot);
                JSONObject obj1TimeSc = jsonObjectSlot.getJSONObject("timingShedule");
                JSONArray monArray = null, tueArray = null, wedArray = null, thuArray = null, friArray = null, satArray = null, sunArray = null;
                if (obj1TimeSc.get(daysList.get(0)) instanceof JSONArray)
                    monArray = obj1TimeSc.getJSONArray(daysList.get(0));
                if (obj1TimeSc.get(daysList.get(1)) instanceof JSONArray)
                    tueArray = obj1TimeSc.getJSONArray(daysList.get(1));
                if (obj1TimeSc.get(daysList.get(2)) instanceof JSONArray)
                    wedArray = obj1TimeSc.getJSONArray(daysList.get(2));
                if (obj1TimeSc.get(daysList.get(3)) instanceof JSONArray)
                    thuArray = obj1TimeSc.getJSONArray(daysList.get(3));
                if (obj1TimeSc.get(daysList.get(4)) instanceof JSONArray)
                    friArray = obj1TimeSc.getJSONArray(daysList.get(4));
                if (obj1TimeSc.get(daysList.get(5)) instanceof JSONArray)
                    satArray = obj1TimeSc.getJSONArray(daysList.get(5));
                if (obj1TimeSc.get(daysList.get(6)) instanceof JSONArray)
                    sunArray = obj1TimeSc.getJSONArray(daysList.get(6));

                for (int i = 0; i < array.length(); i++) {
                    String cId = array.getJSONObject(i).getString("clinicId");
                    conflictClinicName = array.getJSONObject(i).getString("clinicName");
                    if (!cId.equals(clinicId)) {
                        JSONObject obj2 = array.getJSONObject(i);

                        JSONObject obj2TimeSc = obj2.getJSONObject("timingShedule");
                        for (int j = 0; j < daysList.size(); j++) {
                            String day = daysList.get(j);
                            conflictDay = daysList.get(j);
                            JSONArray dayArrayFirst = null;
                            if (j == 0) {
                                dayArrayFirst = monArray;
                            }
                            if (j == 1) {
                                dayArrayFirst = tueArray;
                            }
                            if (j == 2) {
                                dayArrayFirst = wedArray;
                            }
                            if (j == 3) {
                                dayArrayFirst = thuArray;
                            }
                            if (j == 4) {
                                dayArrayFirst = friArray;
                            }
                            if (j == 5) {
                                dayArrayFirst = satArray;
                            }
                            if (j == 6) {
                                dayArrayFirst = sunArray;
                            }


                            JSONArray dayArray = null;
                            if (obj2TimeSc.get(day) instanceof JSONArray)
                                dayArray = obj2TimeSc.getJSONArray(day);


                            for (int z = 0; z < dayArray.length(); z++) {
                                JSONObject dayObj = dayArray.getJSONObject(z);
                                String startTime = dayObj.getString("startTime");
                                String endTime = dayObj.getString("endTime");


                                for (int y = 0; y < dayArrayFirst.length(); y++) {
                                    JSONObject dayObj1 = dayArrayFirst.getJSONObject(y);
                                    String startTime1 = dayObj1.getString("startTime");
                                    String endTime1 = dayObj1.getString("endTime");
                                    boolean isLies = Utils.isFromLays(startTime, endTime, startTime1);
                                    boolean isLies1 = Utils.isToLays(startTime, endTime, endTime1);
                                    boolean isLies2 = Utils.isFromLays(startTime1, endTime1, startTime);
                                    boolean isLies3 = Utils.isToLays(startTime1, endTime1, endTime);

                                    // 6 PM 7 PM
                                    // 5 PM 6 PM

                                    if (endTime.equals(startTime1) && !isLies1) {

                                    } else if (endTime1.equals(startTime) && !isLies3) {

                                    } else if (isLies || isLies1 || isLies2 || isLies3) {
                                        flag = true;
                                        break;
                                    }

                                }
                                if (flag) {
                                    break;
                                }
                            }
                            if (flag) {
                                break;
                            }

                        }
                        if (flag) {
                            break;
                        }

                    }
                }

            }
        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }

        return flag;

    }

    @Override
    public void onSlotsSaved(String res) {
        utils.hideProgress();
        successAlert("Slot saved successfully");
    }

    @Override
    public void onSlotsSavedError(String res) {
        utils.hideProgress();
        Utils.alertBox(mActivity, res);
    }

    private void successAlert(String res) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(mActivity);
        builder.title("Success");
        builder.content(res);
        builder.positiveText("Ok");
        builder.show();
        builder.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                finish();
            }
        });

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        try {
            monthOfYear++;
            etValidFrom.setText("" + dayOfMonth + "/" + monthOfYear + "/" + year);
            String day = "" + dayOfMonth;
            String month = "" + monthOfYear;
            String yearStr = "" + year;
            if (day.length() == 1) day = "0" + day;
            if (month.length() == 1) month = "0" + month;
            validFrom = day + "-" + month + "-" + yearStr;
            jsonObjectSlot.put("validFrom", validFrom);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void modifySlotId() {

        try {
            JSONObject jsonObject = jsonObjectSlot
                    .getJSONObject("timingShedule");
            JSONObject jsonObjectConstant = jsonObjectSlotConstant
                    .getJSONObject("timingShedule");
            for (int i = 0; i < daysList.size(); i++) {
                JSONArray array = jsonObject.getJSONArray(daysList.get(i));
                JSONArray arrayConstant = jsonObjectConstant.getJSONArray(daysList.get(i));
                if (array.length() > 0 && arrayConstant.length() > 0) {
                    for (int j = 0; j < arrayConstant.length(); j++) {
                        JSONObject objConstant = arrayConstant.getJSONObject(j);
                        if (!array.isNull(j)) {
                            String slotId = "";
                            String fee = "";
                            String premium = "";
                            String checked ="";
                            String parentId = "";
                            String version = "";

                            if(objConstant.has("slotId"))
                             slotId = objConstant.getString("slotId");
                            if(objConstant.has("fee"))
                             fee = objConstant.getString("fee");
                            if(objConstant.has("premium"))
                             premium = objConstant.getString("premium");
                            if(objConstant.has("checked"))
                             checked = objConstant.getString("checked");
                            if(objConstant.has("parentId"))
                             parentId = objConstant.getString("parentId");
                            if(objConstant.has("version"))
                             version = objConstant.getString("version");

                            jsonObjectSlot
                                    .getJSONObject("timingShedule")
                                    .getJSONArray(daysList.get(i))
                                    .getJSONObject(j).put("slotId", slotId);
                            jsonObjectSlot
                                    .getJSONObject("timingShedule")
                                    .getJSONArray(daysList.get(i))
                                    .getJSONObject(j).put("fee", fee);
                            jsonObjectSlot
                                    .getJSONObject("timingShedule")
                                    .getJSONArray(daysList.get(i))
                                    .getJSONObject(j).put("premium", premium);
                            jsonObjectSlot
                                    .getJSONObject("timingShedule")
                                    .getJSONArray(daysList.get(i))
                                    .getJSONObject(j).put("checked", checked);
                            jsonObjectSlot
                                    .getJSONObject("timingShedule")
                                    .getJSONArray(daysList.get(i))
                                    .getJSONObject(j).put("parentId", parentId);
                            jsonObjectSlot
                                    .getJSONObject("timingShedule")
                                    .getJSONArray(daysList.get(i))
                                    .getJSONObject(j).put("version", version);

                         /*   Log.e("jsonObjectSlot",""+jsonObjectSlot);
                            Log.e("jsonArraySlot",""+jsonArraySlot);*/
                        }
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}