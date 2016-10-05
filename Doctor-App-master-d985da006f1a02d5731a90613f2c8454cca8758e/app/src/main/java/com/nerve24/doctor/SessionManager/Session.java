package com.nerve24.doctor.SessionManager;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nerve24.doctor.pojo.AppointmentFor;
import com.nerve24.doctor.pojo.AppointmentStatus;
import com.nerve24.doctor.pojo.AppointmentType;
import com.nerve24.doctor.pojo.Clinic;
import com.nerve24.doctor.pojo.Encounter;
import com.nerve24.doctor.pojo.Gender;
import com.nerve24.doctor.pojo.Nerver24Member;
import com.nerve24.doctor.pojo.PaymentMethod;
import com.nerve24.doctor.pojo.Salutation;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by selva on 6/29/2016.
 */
public class Session {

    Context context;
    SharedPreferences pref1, pref2;

    public Session(Context con) {
        context = con;
        pref1 = con.getSharedPreferences("Session", Context.MODE_PRIVATE);
        pref2 = con.getSharedPreferences("RememberSession", Context.MODE_PRIVATE);

    }

    public void saveToken(String value) {
        pref2.edit().putString("token", value).commit();
    }

    public String getToken() {
        return pref2.getString("token", "");
    }


    public void saveLoginId(String value) {
        pref2.edit().putString("loginID", value).commit();
    }

    public String getLoginId() {
        return pref2.getString("loginID", "");
    }

    public void savePassword(String value) {
        pref2.edit().putString("pwd", value).commit();
    }

    public String getPassword() {
        return pref2.getString("pwd", "");
    }

    public void saveNerve24Id(String value) {
        pref1.edit().putString("Nerve24ID", value).commit();
    }

    public String getNerve24Id() {
        // return "MP4SU30000";
        return pref1.getString("Nerve24ID", "0");
    }

    public void setScreenName(String value) {
        pref1.edit().putString("screenName", value).commit();
    }

    public String getScreenName() {
        return pref1.getString("screenName", "0");
    }

    public void saveSession(String value) {
        pref1.edit().putString("sessionval", value).commit();
    }

    public String getSession() {
        return pref1.getString("sessionval", "0");
    }

    public void saveSlots(String value) {
        pref1.edit().putString("slots", value).commit();
    }

    public String getSlots() {
        return pref1.getString("slots", "");
    }

    public void savePriceSetup(String value) {
        pref1.edit().putString("price_setup", value).commit();
    }

    public String getPriceSetup() {
        return pref1.getString("price_setup", "");
    }

    public void saveDailyWiseParams(String value) {
        pref1.edit().putString("daily_wise_params", value).commit();
    }

    public String getDailyWiseParams() {
        return pref1.getString("daily_wise_params", "");
    }

    public void saveDailyWise(String value) {
        pref1.edit().putString("daily_wise", value).commit();
    }

    public String getDailyWise() {
        return pref1.getString("daily_wise", "");
    }


    public void saveFirstName(String firstName) {
        pref1.edit().putString("firstName", firstName).commit();
    }

    public void saveUserName(String username) {
        pref1.edit().putString("username", username).commit();

    }

    public String getFirstName() {
        return pref1.getString("firstName", "");
    }

    public String getUserName() {
        return pref1.getString("username", "");
    }

    public void clearAll() {
        pref1.edit().clear().commit();
    }

    public void saveAppointmentStatus(ArrayList<AppointmentStatus> appointmentStatusArrayList) {
        Gson gson = new Gson();
        String json = gson.toJson(appointmentStatusArrayList);
        pref1.edit().putString("AppointmentStatus", json).commit();
    }


    public List<AppointmentStatus> getAppointmentStatus() {
        Gson gson = new Gson();
        String json = pref1.getString("AppointmentStatus", "");
        Type type = new TypeToken<List<AppointmentStatus>>() {
        }.getType();
        List<AppointmentStatus> appointmentStatusList = gson.fromJson(json, type);
        return appointmentStatusList;
    }
    public void saveSalutation(String salutation) {
        pref1.edit().putString("salutationUser", salutation).commit();
    }

    public String getSalutationUser() {
        return pref1.getString("salutationUser", "");
    }


    public void saveSalutation(ArrayList<Salutation> salutationArrayList) {
        Gson gson = new Gson();
        String json = gson.toJson(salutationArrayList);
        pref1.edit().putString("Salutation", json).commit();
    }

    public List<Salutation> getSalutation() {
        Gson gson = new Gson();
        String json = pref1.getString("Salutation", "");
        Type type = new TypeToken<List<Salutation>>() {
        }.getType();
        List<Salutation> salutationList = gson.fromJson(json, type);
        return salutationList;
    }

    public void saveDoctorList(ArrayList<Nerver24Member> doctorList) {
        Gson gson = new Gson();
        String json = gson.toJson(doctorList);
        pref1.edit().putString("doctorList", json).commit();
    }


    public List<Nerver24Member> getDoctorList() {
        Gson gson = new Gson();
        String json = pref1.getString("doctorList", "");
        Type type = new TypeToken<List<Nerver24Member>>() {
        }.getType();
        List<Nerver24Member> list = gson.fromJson(json, type);
        return list;
    }

    public void saveEncounter(ArrayList<Encounter> encounterArrayList) {
        Gson gson = new Gson();
        String json = gson.toJson(encounterArrayList);
        pref1.edit().putString("encounterArrayList", json).commit();
    }

    public List<Encounter> getEncounter() {
        Gson gson = new Gson();
        String json = pref1.getString("encounterArrayList", "");
        Type type = new TypeToken<List<Encounter>>() {
        }.getType();
        List<Encounter> list = gson.fromJson(json, type);
        return list;
    }

    public void savegender(ArrayList<Gender> genderArrayList) {
        Gson gson = new Gson();
        String json = gson.toJson(genderArrayList);
        pref1.edit().putString("genderArrayList", json).commit();
    }

    public List<Gender> getgender() {
        Gson gson = new Gson();
        String json = pref1.getString("genderArrayList", "");
        Type type = new TypeToken<List<Gender>>() {
        }.getType();
        List<Gender> list = gson.fromJson(json, type);
        return list;
    }

    public void saveAppointmentType(ArrayList<AppointmentType> appointmentTypeArrayList) {
        Gson gson = new Gson();
        String json = gson.toJson(appointmentTypeArrayList);
        pref1.edit().putString("appointmentTypeArrayList", json).commit();
    }

    public List<AppointmentType> getAppointmentType() {
        Gson gson = new Gson();
        String json = pref1.getString("appointmentTypeArrayList", "");
        Type type = new TypeToken<List<AppointmentType>>() {
        }.getType();
        List<AppointmentType> list = gson.fromJson(json, type);
        return list;
    }

    public void saveAppointmentFor(ArrayList<AppointmentFor> appointmentForArrayList) {
        Gson gson = new Gson();
        String json = gson.toJson(appointmentForArrayList);
        pref1.edit().putString("appointmentForArrayList", json).commit();
    }


    public List<AppointmentFor> getAppointmentFor() {
        Gson gson = new Gson();
        String json = pref1.getString("appointmentForArrayList", "");
        Type type = new TypeToken<List<AppointmentFor>>() {
        }.getType();
        List<AppointmentFor> list = gson.fromJson(json, type);
        return list;

    }

    public void saveClinics(ArrayList<Clinic> clinicsList) {
        Gson gson = new Gson();
        String json = gson.toJson(clinicsList);
        pref1.edit().putString("clinicsList", json).commit();
    }

    public List<Clinic> getClinics() {
        Gson gson = new Gson();
        String json = pref1.getString("clinicsList", "");
        Type type = new TypeToken<List<Clinic>>() {
        }.getType();
        List<Clinic> list = gson.fromJson(json, type);
        return list;
    }

    public void savePaymentMethods(ArrayList<PaymentMethod> paymentMethodArrayList) {
        Gson gson = new Gson();
        String json = gson.toJson(paymentMethodArrayList);
        pref1.edit().putString("paymentMethod", json).commit();
    }

    public List<PaymentMethod> getPaymentMethod() {
        Gson gson = new Gson();
        String json = pref1.getString("paymentMethod", "");
        Type type = new TypeToken<List<PaymentMethod>>() {
        }.getType();
        List<PaymentMethod> list = gson.fromJson(json, type);
        return list;
    }

    public void saveCalendarFilters(String value) {
        pref1.edit().putString("calendar_filter", value).commit();
    }

    public String getCalendarFilters() {
        return pref1.getString("calendar_filter", "");
    }

    public void savePatientFilters(String value) {
        pref1.edit().putString("patient_filter", value).commit();
    }

    public String getPatientFilters() {
        return pref1.getString("patient_filter", "");
    }

    public void saveFullName(String fullName) {
        pref1.edit().putString("fullName", fullName).commit();
    }

    public String getFullName() {
        return pref1.getString("fullName", "");
    }

}
