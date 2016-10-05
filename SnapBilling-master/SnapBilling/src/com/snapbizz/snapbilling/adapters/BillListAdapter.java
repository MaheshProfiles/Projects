package com.snapbizz.snapbilling.adapters;

import android.app.Service;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.utils.SnapBillingConstants;
import com.snapbizz.snapbilling.utils.SnapBillingTextFormatter;
import com.snapbizz.snaptoolkit.domains.ShoppingCart;
import com.snapbizz.snaptoolkit.domainsV2.Models.BillItem;
import com.snapbizz.snaptoolkit.domainsV2.Models.UOM;
import com.snapbizz.snaptoolkit.utils.KeyPadMode;

public class BillListAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private Context context;
    private BillItemEditListener billItemEditListener;
    private ShoppingCart shoppingCart;
    private int loc[] = new int[2];
    private int layoutId;
    private int lastSelectedPosition = -1;
    private boolean isBillingHistory;

    public BillListAdapter(Context context, ShoppingCart shoppingCart, BillItemEditListener billItemEditListener, int layoutId) {
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context.getApplicationContext();
        this.billItemEditListener = billItemEditListener;
        this.shoppingCart = shoppingCart;
        this.layoutId = layoutId;
    }

    public BillListAdapter(Context context, ShoppingCart shoppingCart, BillItemEditListener billItemEditListener, int layoutId, boolean isBillngHistory) {
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context.getApplicationContext();
        this.billItemEditListener = billItemEditListener;
        this.shoppingCart = shoppingCart;
        this.layoutId = layoutId;
        this.isBillingHistory = isBillngHistory;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = layoutInflater.inflate(layoutId, null);

            convertView.findViewById(R.id.bill_productname_textview).setOnClickListener(onEditProductClickListener);
            convertView.findViewById(R.id.bill_productmrp_textview).setOnClickListener(onEditProductClickListener);
            convertView.findViewById(R.id.bill_productrate_textview).setOnClickListener(onEditProductClickListener);
            convertView.findViewById(R.id.bill_producttotal_textview).setOnClickListener(onEditProductClickListener);
            convertView.findViewById(R.id.bill_productquantity_textview).setOnClickListener(onEditProductClickListener);
            convertView.findViewById(R.id.select_altmrp_button).setOnClickListener(onEditProductClickListener);
            convertView.findViewById(R.id.select_mrp_button).setOnClickListener(onEditProductClickListener);
            if(convertView.findViewById(R.id.rl_dropdwn_imageview) != null)
            	convertView.findViewById(R.id.rl_dropdwn_imageview).setOnClickListener(onEditProductClickListener);
        }
        
        RelativeLayout productRateImageview = (RelativeLayout) convertView.findViewById(R.id.rl_dropdwn_imageview);
        TextView productSlnoTextView = (TextView) convertView.findViewById(R.id.bill_productslno_textview);
        TextView productNameTextView = (TextView) convertView.findViewById(R.id.bill_productname_textview);

        TextView productQuantityTextView = (TextView) convertView.findViewById(R.id.bill_productquantity_textview);
        TextView productMrpTextView = (TextView) convertView.findViewById(R.id.bill_productmrp_textview);
        TextView productRateTextView = (TextView) convertView.findViewById(R.id.bill_productrate_textview);
        TextView productTotalTextView = (TextView) convertView.findViewById(R.id.bill_producttotal_textview);

        TextView productDiscountTextView = (TextView) convertView.findViewById(R.id.bill_discount_textview);
        LinearLayout productMrpLayout = (LinearLayout) convertView.findViewById(R.id.billitem_mrp_layout);
        Button productSelectMrpButton = (Button) convertView.findViewById(R.id.select_mrp_button);
        Button productSelectAltMrpButton = (Button) convertView.findViewById(R.id.select_altmrp_button);
         
        
        if(position == lastSelectedPosition)
            convertView.setBackgroundResource(R.color.billitem_selected_color);
        else
            convertView.setBackgroundResource(android.R.color.white);

        if (productRateImageview != null) {
	        if (shoppingCart.getShoppingCartId() == SnapBillingConstants.LAST_SHOPPING_CART)
	        	productRateImageview.findViewById(R.id.rl_dropdwn_imageview).setVisibility(View.GONE);
	        else
	        	productRateImageview.findViewById(R.id.rl_dropdwn_imageview).setVisibility(View.VISIBLE);
        }
        BillItem billItem = getItem(position);
        double mrp = billItem.mrp / 100.0;
        double salesPrice = billItem.sellingPrice / 100.0;
        double priceDifference = mrp - salesPrice;
        UOM uom = billItem.getDisplayUom();
        float qty = billItem.getQuantity(uom);
        double total = billItem.getTotal(true);

        productSlnoTextView.setText((position+1)+".");
        productNameTextView.setText(billItem.name);
        if(position == shoppingCart.getLastSelectedItemPosition())
            productNameTextView.setTypeface(Typeface.DEFAULT_BOLD);
        else
            productNameTextView.setTypeface(Typeface.DEFAULT);
        
        productMrpTextView.setText(SnapBillingTextFormatter.formatPriceText(mrp, context));
        if(billItem.uom != UOM.PC) {
            if(billItem.uom == UOM.G || billItem.uom == UOM.KG)
                productMrpTextView.append("/"+UOM.KG);
            else
                productMrpTextView.append("/"+UOM.L);
            productQuantityTextView.setText(SnapBillingTextFormatter.formatNumberText(qty, context)+uom);
        } else {
            productQuantityTextView.setText(SnapBillingTextFormatter.formatNumberText(qty, context));
            productMrpTextView.append("/"+UOM.PC);
        }
        productRateTextView.setText(String.valueOf(billItem.sellingPrice/100.0));
        String totalText = SnapBillingTextFormatter.formatPriceText(total, context);
        if(!isBillingHistory) {	// TODO: Check this
            productMrpLayout.setVisibility(View.GONE);
            productSelectMrpButton.setText(SnapBillingTextFormatter.formatPriceText(billItem.mrp/100.0, context));
            productDiscountTextView.setVisibility(View.GONE);
        } else {
            productMrpLayout.setVisibility(View.GONE);
            if(priceDifference > 0 && shoppingCart.getShoppingCartId() != SnapBillingConstants.LAST_SHOPPING_CART) {
                productDiscountTextView.setVisibility(View.VISIBLE);
                productDiscountTextView.setText(SnapBillingTextFormatter.formatDiscountText(priceDifference * Math.abs(billItem.quantity) / (uom == UOM.PC ? 1 : 1000.0), context));
            } else {
                productDiscountTextView.setVisibility(View.GONE);
            }
        }

        if(billItem.quantity < 0)
            totalText = "- "+totalText;
        productTotalTextView.setText(totalText);
        productNameTextView.setTag(position);
        productMrpTextView.setTag(position);
        productRateTextView.setTag(position);
        if (productRateImageview != null)
        	productRateImageview.setTag(position);
        productQuantityTextView.setTag(position);
        productTotalTextView.setTag(position);
        productSelectAltMrpButton.setTag(position);
        productSelectMrpButton.setTag(position);
        return convertView;
    }

    View.OnClickListener onEditProductClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            lastSelectedPosition = (Integer) v.getTag();
        	if(billItemEditListener == null)
        		return;
            if (v.getId() == R.id.bill_productname_textview) {
                v.getLocationOnScreen(loc);
                billItemEditListener.onProductEdit((Integer) v.getTag(), loc[1]);
            } else if (v.getId() == R.id.bill_productquantity_textview) {
                billItemEditListener.onValueEdit((Integer) v.getTag(), KeyPadMode.QTY);
            } else if (v.getId() == R.id.bill_productmrp_textview) {
                billItemEditListener.onValueEdit((Integer) v.getTag(), KeyPadMode.PRICE);
            } else if (v.getId() == R.id.bill_productrate_textview) {
                billItemEditListener.onValueEdit((Integer) v.getTag(), KeyPadMode.RATE);
            } else if (v.getId() == R.id.bill_producttotal_textview) {
                billItemEditListener.onValueEdit((Integer) v.getTag(), KeyPadMode.TOTAL);
            } else if (v.getId() == R.id.select_mrp_button) {
                // getItem(lastSelectedPosition).setHasMultipleSp(false); // TODO
            } else if (v.getId() == R.id.select_altmrp_button) {
                billItemEditListener.onAltMrpSelected(lastSelectedPosition);
            } else if (v.getId() == R.id.rl_dropdwn_imageview) {
            	billItemEditListener.onAltRateSelected(lastSelectedPosition,v);
            }
            notifyDataSetChanged();
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Service.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    };

    public interface BillItemEditListener {
        public void onProductEdit(int position,float y);
        public void onValueEdit(int position, KeyPadMode keypadMode);
        public void onAltMrpSelected(int position);
        public void onAltRateSelected(int position, View v);
    }

    public void setLastSelectedPosition(int lastSelectedPosition) {
        this.lastSelectedPosition = lastSelectedPosition;
    }

    public int getLastSelectedPosition() {
        return lastSelectedPosition;
    }

    @Override
    public int getCount() {
        return shoppingCart.getCartItems().size();
    }

    @Override
    public BillItem getItem(int position) {
        return shoppingCart.getCartItems().valueAt(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void setShoppingCart(ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
    }
    
    public void clear() {
        this.shoppingCart.deleteCart();
    }

    /*public void addAll(List<BillItem> productList) {
        this.shoppingCart.getBillItemList().addAll(productList);
    }

    public void remove(int position) {
        shoppingCart.getBillItemList().remove(position);
    }*/
}
