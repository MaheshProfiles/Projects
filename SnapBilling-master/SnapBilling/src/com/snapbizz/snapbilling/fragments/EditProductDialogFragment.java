package com.snapbizz.snapbilling.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.utils.SnapBillingTextFormatter;
import com.snapbizz.snaptoolkit.domains.ProductSku;
import com.snapbizz.snaptoolkit.utils.CustomToast;
import com.snapbizz.snaptoolkit.utils.SkuUnitType;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;

public class EditProductDialogFragment extends DialogFragment {

    private ProductSku productSku;
    private EditProductDialogListener editProductDialogListener;
    private EditText qtyEditText;

    public void setProductSku(ProductSku productSku) {
        this.productSku = productSku;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            editProductDialogListener = (EditProductDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + getString(R.string.exc_implementnavigation));
        }
    }

    View.OnClickListener onQtyEditClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if(qtyEditText.length() == 0) {
                return;
            }
            if(v.getId() == R.id.decrease_qty_button) {
                float qty = Float.parseFloat(qtyEditText.getText().toString());
                if(qty >= 1) {
                    if(qty == ((int) qty))
                        qtyEditText.setText(((int)qty - 1)+"");
                    else
                        qtyEditText.setText((qty - 1)+"");
                }
                qtyEditText.setSelection(qtyEditText.length());
            } else {
                float qty = Float.parseFloat(qtyEditText.getText().toString());
                if(qty == ((int) qty))
                    qtyEditText.setText(((int)qty + 1)+"");
                else
                    qtyEditText.setText((qty + 1)+"");
                qtyEditText.setSelection(qtyEditText.length());
            }
        }
    };

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View view = inflater.inflate(R.layout.layout_addproduct, null);
        ((TextView) view.findViewById(R.id.productsku_name_textview)).setText(productSku.getProductSkuName());
        ((TextView) view.findViewById(R.id.productsku_mrp_textview)).setText(SnapBillingTextFormatter.formatPriceText(productSku.getProductSkuMrp(), getActivity()));
        qtyEditText = (EditText) view.findViewById(R.id.qty_edittext);
        view.findViewById(R.id.increase_qty_button).setOnClickListener(onQtyEditClickListener);
        view.findViewById(R.id.decrease_qty_button).setOnClickListener(onQtyEditClickListener);
        builder.setView(view);
        builder
        .setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                SnapCommonUtils.hideSoftKeyboard(getActivity(), qtyEditText.getWindowToken());
                if(qtyEditText.length() == 0) {
                    CustomToast.showCustomToast(getActivity(), getString(R.string.msg_enter_qty), Toast.LENGTH_SHORT, CustomToast.INFORMATION);
                    return;
                }
                float qty = 0;
                if(productSku.getProductSkuUnits().ordinal() == SkuUnitType.PC.ordinal()) {
                    try {
                        qty = Integer.parseInt(qtyEditText.getText().toString());
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        CustomToast.showCustomToast(getActivity(), getString(R.string.msg_integer_qty), Toast.LENGTH_SHORT, CustomToast.WARNING);
                        return;
                    }
                } else {
                    qty = Float.parseFloat(qtyEditText.getText().toString());
                }
                if(qty > 0) {
                    editProductDialogListener.onAddProduct(productSku, qty);
                }
            }
        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            	SnapCommonUtils.hideSoftKeyboard(getActivity(), qtyEditText.getWindowToken());
            }
        });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public interface EditProductDialogListener {
        public void onAddProduct(ProductSku productSku, float quantity);
    }

}
