package com.nerve24.doctor.Listeners;



public interface Listener_ChangeAppointmentStatus
{
    public void onChangedStatus(String res);
    public void onChangedStatusFailure(String res);
}
