package com.nerve24.doctor.Listeners;


import com.nerve24.doctor.pojo.PriceSetup;

import java.util.ArrayList;

public interface Listener_Get_Price_Setup
{
    public void onGetPriceSetup(ArrayList<PriceSetup> priceSetupList);
    public void onGetPriceSetupError(String res);
}
