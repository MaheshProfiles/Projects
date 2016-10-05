package com.nerve24.doctor.AppConfig;

/**
 * Created by Satheesh on 4/15/2016.
 */
public class Constants_API {
    // DEV URL
    public static String baseUrl = "http://192.168.7.119:9090";

    //QA URL
    // public static String baseUrl = "http://192.168.7.58:8080";

    //Public URL
     //public static String baseUrl = "http://61.12.69.243:9107";

    public static String ApiRegister = baseUrl + "/Admin-Core/users/userRegistration";
    public static String ApiCreateUser = baseUrl + "/Admin-Core/users/createUser";
    public static String ApiGenerateOTP = baseUrl + "/Admin-Core/users/generateOtp";
    public static String ApiChangePassword = baseUrl + "/Admin-Core/users/changePassword";
    public static String ApiForgetPassword = baseUrl + "/Admin-Core/users/forgotPassword";
    public static String ApiLogin = baseUrl + "/nerve24/mobileLogin";
    public static String ApiGetSlots = baseUrl + "/Doctor-Core/doctor/Slots/";
    public static String ApiSaveSlots = baseUrl + "/Doctor-Core/doctor/slotSetups";
    public static String ApiApplyForAll = baseUrl + "/Doctor-Core/doctor/applyForAll";
    public static String ApiGetpriceSetup = baseUrl + "/Doctor-Core/doctor/priceSetups/";
    public static String ApiEditPriceClinic = baseUrl + "/Doctor-Core/doctor/priceSetups";
    public static String ApiGetDailyWiseSlots = baseUrl + "/Doctor-Core/doctor/dayWiseView";
    public static String ApiGetPremiumSlots = baseUrl + "/Doctor-Core/doctor/premiumSlots";
    public static String ApiSavePremiumSlots = baseUrl + "/Doctor-Core/doctor/savePremiumSlots";
    public static String ApiGetClinicsById = baseUrl + "/Doctor-Core/appointment/fetchClinicsByReceptionistNerve24Id/";
    public static String ApiGetSlots_with_Appointments = baseUrl + "/Doctor-Core/appointment/slotsWithAppointments";
    public static String ApiGetSalutation= baseUrl + "/Common-Services/common/salutation";
    public static String ApiGetDoctors= baseUrl + "/Admin-Core/mgr/getUsersByRole/DOCTOR";
    public static String ApiGetGender= baseUrl + "/Common-Services/common/gender";
    public static String ApiSearchPatient= baseUrl + "/Admin-Core/users/search";
    public static String ApiSlotsFromClinics= baseUrl + "/Doctor-Core/appointment/slotsFromClinics";
    public static String ApiSaveAppointments= baseUrl + "/Doctor-Core/appointment/saveAppointment";
    public static String ApiCancelAppoinments = baseUrl + "/Doctor-Core/appointment/cancelAppointments";
    public static String ApiChangeStatus = baseUrl + "/Doctor-Core/appointment/updateAppointmentStatus";
    public static String ApiSearchByType = baseUrl + "/Admin-Core/users/searchByType";
    public static String ApiGetPatientHistory = baseUrl + "/Doctor-Core/appointment/patientHistoryFilter";
    public static String ApiAccountDetails =baseUrl + "/Doctor-Core/doctor/accountDetails";
    public static String ApiPaymentMethods=baseUrl+"/Doctor-Core/doctor/paymentMethods";
    public static String ApiReferDoctor=baseUrl+"/Doctor-Core/referToDoctor";
    public static String ApiGetAppointmentType= baseUrl + "/Doctor-Core/param/AppointmentType";
    public static String ApiGetAppointmentFor= baseUrl + "/Doctor-Core/param/AppointmentFor";
    public static String ApiGetEncounter= baseUrl + "/Doctor-Core/param/Encounter";
    public static String ApiGet_AppointmentStatus= baseUrl + "/Doctor-Core/param/AppointmentStatus";
    public static String ApiGet_Episodes= baseUrl + "/Doctor-Core/appointment/fetchEpisodesByPatientNerve24Id/";
    public static String ApiGet_ProfilePicture=baseUrl+"/Doctor-Core/users/getProfilePicture/";
    public static String ApiSave_ProfilePicture=baseUrl+"/Doctor-Core/RegistrationController/uploadProfilePic";





    //public static final String DATA_LOCALITY = "http://192.168.7.58:9090//Common-Services/common/locality/192.168.7.58:8080";


}


