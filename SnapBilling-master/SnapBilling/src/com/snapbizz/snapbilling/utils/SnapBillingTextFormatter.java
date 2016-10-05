package com.snapbizz.snapbilling.utils;

import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;

import com.snapbizz.snaptoolkit.R;
import com.snapbizz.snaptoolkit.utils.SnapToolkitTextFormatter;


public class SnapBillingTextFormatter extends SnapToolkitTextFormatter {

    private static final String UNFORMATTED_PRICE_FORMAT = "############.####";

    public static String formatDiscountText(double discount, Context context) {
        return "Savings: "+formatPriceText(discount, context);
    }

    public static String formatCustomerMembershipDate(Date membershipDate) {
        if(membershipDate == null)
            return "";
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(membershipDate);
        int year = calendar.get(Calendar.YEAR);
        return new DateFormatSymbols().getMonths()[membershipDate.getMonth()] + " "+year;
    }

    public static String getUnformattedPriceText(double price, Context context) {
        String fmtPrice = new DecimalFormat(UNFORMATTED_PRICE_FORMAT).format(Math.abs(price));
        String rupee = context.getString(R.string.rupee_symbol);
        if(price < 0)
            return "- "+rupee+fmtPrice;
        else
            return  rupee + fmtPrice;
    }

}
