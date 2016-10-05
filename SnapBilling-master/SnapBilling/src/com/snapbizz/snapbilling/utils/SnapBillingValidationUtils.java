package com.snapbizz.snapbilling.utils;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.widget.Toast;

import com.snapbizz.snapbilling.R;
import com.snapbizz.snaptoolkit.domains.ShoppingCart;
import com.snapbizz.snaptoolkit.domainsV2.Models.BillItem;
import com.snapbizz.snaptoolkit.utils.CustomToast;

public class SnapBillingValidationUtils {

    public static boolean isBillValid(ShoppingCart shoppingCart, Context context) {
        if(shoppingCart.getCartItems() == null || shoppingCart.getCartItems().size() == 0) {
            CustomToast.showCustomToast(context, context.getString(R.string.err_bill_empty), Toast.LENGTH_SHORT, CustomToast.ERROR);
            return false;
        }else {
            double credit = shoppingCart.getTotalCartValue() - shoppingCart.getTotalDiscount() - shoppingCart.getTotalSavings() - shoppingCart.getCashPaid();
            if(credit < 0.5)
                credit = 0;
            if(credit > 0 && shoppingCart.getCustomer() == null && shoppingCart.getShoppingCartId() != SnapBillingConstants.LAST_SHOPPING_CART) {
            	CustomToast.showCustomToast(context, context.getString(R.string.err_tag_customer), Toast.LENGTH_SHORT, CustomToast.ERROR);            	
                return false;
            }
            if(credit > 0 && shoppingCart.getDistributor() == null && shoppingCart.getShoppingCartId() == SnapBillingConstants.LAST_SHOPPING_CART) {
            	CustomToast.showCustomToast(context, context.getString(R.string.err_tag_distributor), Toast.LENGTH_SHORT, CustomToast.ERROR);            	
                return false;
            }

            for(int i = 0; i < shoppingCart.getCartItems().size(); i++) {
                BillItem billItem = shoppingCart.getCartItems().valueAt(i);
//                if((billItem.getProductSkuSalePrice() == 0)) {
//                    CustomToast.showCustomToast(context, "Item No "+(i+1)+" needs a Price", Toast.LENGTH_SHORT, CustomToast.WARNING);
//                    return false;
//                }
                if((billItem.product.bNew && billItem.sellingPrice == 0)) {
                    CustomToast.showCustomToast(context, context.getString((R.string.info_valid_price), (i+1)), Toast.LENGTH_SHORT, CustomToast.WARNING);
                    return false;
                }
                if(billItem.name != null && billItem.name.isEmpty()) {
                    CustomToast.showCustomToast(context, context.getString((R.string.info_valid_name), (i + 1)), Toast.LENGTH_SHORT, CustomToast.WARNING);
                    return false;
                }
                /*if((billItem.isHasMultipleSp())) {
                    CustomToast.showCustomToast(context, context.getString((R.string.info_valid_multiple_sp), (i + 1)), Toast.LENGTH_SHORT, CustomToast.WARNING);
                    return false;
                }*/
                if((billItem.mrp ==0)) {
                    CustomToast.showCustomToast(context, context.getString((R.string.info_valid_mrp), (i + 1)), Toast.LENGTH_SHORT, CustomToast.WARNING);
                    return false;
                }
            }
        }
        return true;
    }

    public static class InputFilterMinMax implements InputFilter {
        private double min, max;
        String inr;

        public InputFilterMinMax(double min, double max, String inr) {
            this.min = min;
            this.max = max;
            this.inr = inr;
        }

        public InputFilterMinMax(String min, String max, String inr) {
            this.min = Float.parseFloat(min);
            this.max = Float.parseFloat(max);
            this.inr = inr;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {   
            try {
                float input = Float.parseFloat(dest.toString().replaceAll(inr+"|,", "") + source.toString().replaceAll(inr+"|,", ""));
                if (isInRange(min, max, input))
                    return null;
            } catch (NumberFormatException nfe) {
            }     
            return "";
        }

        private boolean isInRange(double a, double b, double c) {
            return b > a ? c >= a && c <= b : c >= b && c <= a;
        }
    }
}
