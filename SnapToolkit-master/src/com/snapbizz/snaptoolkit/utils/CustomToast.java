package com.snapbizz.snaptoolkit.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.snapbizz.snaptoolkit.R;

public class CustomToast {    
    
    public static final int INFORMATION = 1;
    public static final int ERROR = 2;
    public static final int SUCCESS = 3;
    public static final int WARNING = 4;
    
    private static LayoutInflater inflater;
    private static View view;
    
   public static void showCustomToast(Context context, String toastMessage, int toastLength, int toastType){
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.custom_toast_layout, null);
        ((TextView) view.findViewById(R.id.custom_toast_textview)).setText(toastMessage);
        switch (toastType) {
        case INFORMATION:
            view.setBackground(context.getResources().getDrawable(R.drawable.toast_background_information));
            ((ImageView) view.findViewById(R.id.custom_toast_imageview)).setImageDrawable(context.getResources().getDrawable(R.drawable.icon_toast_info));            
            break;

        case ERROR:
            view.setBackground(context.getResources().getDrawable(R.drawable.toast_background_error));
            ((ImageView) view.findViewById(R.id.custom_toast_imageview)).setImageDrawable(context.getResources().getDrawable(R.drawable.icon_toast_error));
            break;
            
        case SUCCESS:
            view.setBackground(context.getResources().getDrawable(R.drawable.toast_background_success));
            ((ImageView) view.findViewById(R.id.custom_toast_imageview)).setImageDrawable(context.getResources().getDrawable(R.drawable.icon_toast_success));
            break;
            
        case WARNING:
            view.setBackground(context.getResources().getDrawable(R.drawable.toast_background_warning));
            ((ImageView) view.findViewById(R.id.custom_toast_imageview)).setImageDrawable(context.getResources().getDrawable(R.drawable.icon_toast_warning));
            break;
        
        }
        Toast toast = new Toast(context);
        toast.setView(view);
        toast.setDuration(toastLength);
        toast.show();
    }
}
