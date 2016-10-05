package com.snapbizz.snapbilling.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.adapters.UnitTypeAdapter;
import com.snapbizz.snapbilling.utils.SnapBillingConstants;
import com.snapbizz.snapbilling.utils.SnapBillingTextFormatter;
import com.snapbizz.snaptoolkit.utils.KeyPadMode;
import com.snapbizz.snaptoolkit.utils.SkuUnitType;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class ProductKeypadFragment extends Fragment {
	
	static final String TAG=ProductKeypadFragment.class.getSimpleName();

    private int context;
    private float qty = 1;
    private double price= 0;
    private double rate = 0;
    private double totalPrice = 0;
    private double startingPrice = 0;
    private boolean isNewEdit = true;
    private boolean isDecimalUsed;
    private boolean isQuickAdd;
    private boolean enableDiscount;
    private View increaseButton;
    private View decreaseButton;
    private View discountView;
    private View arrowImageView;
    private LinearLayout parentView,mainParentView;
    private TextView mValueTextView;
    private TextView mqtyTextView;
    private TextView mpriceTextView;
    private TextView mrateTextView;
    private TextView mtotalTextView;
    private TextView currentSelectedTextView;
    private KeyPadMode keyPadMode = KeyPadMode.PRICE;
    private KeyboardListener keyboardEnterListener;
    private int arrowPosition = 0;
    private int arrowVisibility;
    public static int POSITION_LEFT = 0;
    public static int POSITION_RIGHT = 1;
    private UnitTypeAdapter spinnerArrayAdapter;
    private Handler assignTasksHandler = new Handler();
    private Button unitTypeSpinner;
    private ListView unitTypeListView;
    private SkuUnitType skuUnitType;
    private List<String> unitList;
    private boolean isQTY=false;
    private boolean isRate=false;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editkeypad, null);
        return view;
    }

    public void updateTextValues() {
        mqtyTextView.setText(qty + "");
        mpriceTextView.setText(SnapBillingTextFormatter.getUnformattedPriceText(price, mpriceTextView.getContext()));
        mrateTextView.setText(SnapBillingTextFormatter.getUnformattedPriceText(rate, mrateTextView.getContext()));
        mtotalTextView.setText(SnapBillingTextFormatter.getUnformattedPriceText(totalPrice, mtotalTextView.getContext()));
    }
    
    public void updateViews() {
        if(keyPadMode.ordinal() == KeyPadMode.QTY.ordinal()) {
            mqtyTextView.setSelected(true);
            mainParentView.setVisibility(View.VISIBLE);
            increaseButton.setVisibility(View.VISIBLE);
            decreaseButton.setVisibility(View.VISIBLE);
            mValueTextView.setText(qty + "");
            currentSelectedTextView = mqtyTextView;
            parentView.setGravity(Gravity.LEFT);
        } else if(keyPadMode.ordinal() == KeyPadMode.PRICE.ordinal()) {
            mpriceTextView.setSelected(true);
            mainParentView.setVisibility(View.VISIBLE);
            increaseButton.setVisibility(View.INVISIBLE);
            decreaseButton.setVisibility(View.INVISIBLE);
            if(enableDiscount)
                discountView.setVisibility(View.GONE);
            mValueTextView.setText(mpriceTextView.getText().toString().substring(1));
            currentSelectedTextView = mpriceTextView;
            parentView.setGravity(Gravity.CENTER);
        } else if(keyPadMode.ordinal() == KeyPadMode.RATE.ordinal()) {
            mrateTextView.setSelected(true);
            mainParentView.setVisibility(View.VISIBLE);
            increaseButton.setVisibility(View.INVISIBLE);
            decreaseButton.setVisibility(View.INVISIBLE);
            if(enableDiscount)
                discountView.setVisibility(View.GONE);
            mValueTextView.setText(mrateTextView.getText().toString().substring(1));
            currentSelectedTextView = mrateTextView;
            parentView.setGravity(Gravity.CENTER);
        }else if(keyPadMode.ordinal() == KeyPadMode.TOTAL.ordinal()) {
            mtotalTextView.setSelected(true);
            mainParentView.setVisibility(View.VISIBLE);
            increaseButton.setVisibility(View.INVISIBLE);
            decreaseButton.setVisibility(View.INVISIBLE);
            if(enableDiscount)
                discountView.setVisibility(View.GONE);
            mValueTextView.setText(totalPrice + "");
            currentSelectedTextView = mtotalTextView;
            parentView.setGravity(Gravity.RIGHT);
        }
    }
    
    public void setUnitType(SkuUnitType unitType, Context context) {
        this.skuUnitType = unitType;
        if(unitList == null)
            unitList = new ArrayList<String>();
        else
            unitList.clear();
        if(unitType == null) {
            for(String unit : SnapBillingConstants.ALL_UNIT_TYPES)
                unitList.add(unit);
            this.skuUnitType = SkuUnitType.PC;
            isQuickAdd = true;
        } else if(unitType.ordinal() == SkuUnitType.KG.ordinal() || unitType.ordinal() == SkuUnitType.GM.ordinal()) {
            for(String unit : SnapBillingConstants.WEIGHT_UNIT_TYPES)
                unitList.add(unit);
            isQuickAdd = false;
        } else if(unitType.ordinal() == SkuUnitType.LTR.ordinal() || unitType.ordinal() == SkuUnitType.ML.ordinal()) {
            for(String unit : SnapBillingConstants.VOLUME_UNIT_TYPES)
                unitList.add(unit);
            isQuickAdd = false;
        } else if(unitType.ordinal() == SkuUnitType.PC.ordinal()) {
            for(String unit : SnapBillingConstants.PACKET_UNIT_TYPES)
                unitList.add(unit);
            isQuickAdd = false;
        }
        if(spinnerArrayAdapter == null)
            spinnerArrayAdapter  = new UnitTypeAdapter(context, unitList);
        else
            spinnerArrayAdapter.notifyDataSetChanged();

        if(skuUnitType.ordinal() == SkuUnitType.GM.ordinal() || skuUnitType.ordinal() == SkuUnitType.ML.ordinal()) {
        	// TODO: Check this
            /*this.qty *= 1000;
            this.startingPrice = this.startingPrice / 1000;*/
            if(unitTypeSpinner != null) 
                unitTypeSpinner.setText(spinnerArrayAdapter.getItem(1));
        } else {
            if(unitTypeSpinner != null) 
                unitTypeSpinner.setText(spinnerArrayAdapter.getItem(0));
        }

    }

    View.OnClickListener onUnitTypeClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            v.setSelected(!v.isSelected());
            unitTypeListView.setVisibility(v.isSelected() ? View.VISIBLE : View.GONE);
        }
    };


    AdapterView.OnItemClickListener onUnitTypeSelectListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
            unitTypeListView.setVisibility(View.GONE);
            SkuUnitType skuSelectedUnitType = SkuUnitType.getEnum(spinnerArrayAdapter.getItem(pos).toString());
            unitTypeSpinner.setText(spinnerArrayAdapter.getItem(pos));
            unitTypeSpinner.setSelected(false);
            if(skuSelectedUnitType.ordinal() != ProductKeypadFragment.this.skuUnitType.ordinal()) {
                mqtyTextView.setSelected(false);
                onPropertyEditClickListener.onClick(mqtyTextView);
                if((skuSelectedUnitType.ordinal() == SkuUnitType.KG.ordinal() || skuSelectedUnitType.ordinal() == SkuUnitType.LTR.ordinal()) && !isQuickAdd) {
                    startingPrice *= 1000;
                    qty /= 1000;
                    totalPrice = rate * qty;
                } else if((skuSelectedUnitType.ordinal() == SkuUnitType.GM.ordinal() || skuSelectedUnitType.ordinal() == SkuUnitType.ML.ordinal()) && !isQuickAdd) {
                    startingPrice /= 1000;
                    qty *= 1000;
                    totalPrice = rate * qty/1000;
                }
                mValueTextView.setText(qty + "");
            }
            skuUnitType = skuSelectedUnitType;
            updateTextValues();
        }
    };
    
   

    public void setArrowVisibility(int visiblity) {
        this.arrowVisibility = visiblity;
        if(arrowImageView != null) {
            arrowImageView.setVisibility(visiblity);
        }
    }

    public boolean isEnableDiscount() {
        return enableDiscount;
    }

    public void setEnableDiscount(boolean enableDiscount) {
        this.enableDiscount = enableDiscount;
    }
    public void setContext(int context) {
        this.context = context;
    }

    public void setKeypadEnterListener(KeyboardListener keyboardEnterListener) {
        this.keyboardEnterListener = keyboardEnterListener;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
        this.startingPrice = price;
        if (getActivity() != null && mpriceTextView != null) 
            mpriceTextView.setText(SnapBillingTextFormatter.getUnformattedPriceText(price, getActivity()));
    }
    
    public double getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
        if (getActivity() != null && mpriceTextView != null) 
            mrateTextView.setText(SnapBillingTextFormatter.getUnformattedPriceText(rate, getActivity()));
    }

    public void setStartingPrice(float startingPrice) {
        this.startingPrice = startingPrice;
    }
    

    public float getQty() {
        return qty;
    }

    public void setQty(float qty) {
        this.qty = qty;
        if(getActivity() != null) {
            if(skuUnitType.ordinal() == SkuUnitType.GM.ordinal() || skuUnitType.ordinal() == SkuUnitType.ML.ordinal()) {
                qty = qty*1000;
            }
            if(mqtyTextView != null)
            	mqtyTextView.setText(qty + "");
        }
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
        if(getActivity() != null && mtotalTextView != null) {
            mtotalTextView.setText(SnapBillingTextFormatter.getUnformattedPriceText(totalPrice, getActivity()));
        }
    }

    public void setKeyPadMode(KeyPadMode keyPadMode) {
        isNewEdit = true;
        isDecimalUsed = false;

        if (getActivity() != null) {
        	if (currentSelectedTextView != null)
        		currentSelectedTextView.setSelected(false);
            if (keyPadMode.ordinal() == KeyPadMode.QTY.ordinal()) {
                mqtyTextView.setVisibility(View.VISIBLE);
                mtotalTextView.setVisibility(View.VISIBLE);
                mrateTextView.setVisibility(View.VISIBLE);
                discountView.setVisibility(View.GONE);
                onPropertyEditClickListener.onClick(mqtyTextView);
            } else if (keyPadMode.ordinal() == KeyPadMode.PRICE.ordinal() && mqtyTextView != null) {
                mqtyTextView.setVisibility(View.VISIBLE);
                mtotalTextView.setVisibility(View.VISIBLE);
                mrateTextView.setVisibility(View.VISIBLE);
                discountView.setVisibility(View.GONE);
                onPropertyEditClickListener.onClick(mpriceTextView);
            } else if (keyPadMode.ordinal() == KeyPadMode.RATE.ordinal()) {
                mqtyTextView.setVisibility(View.VISIBLE);
                mtotalTextView.setVisibility(View.VISIBLE);
                mrateTextView.setVisibility(View.VISIBLE);
                discountView.setVisibility(View.GONE);
                onPropertyEditClickListener.onClick(mrateTextView);
            } else if (keyPadMode.ordinal() == KeyPadMode.TOTAL.ordinal()) {
                mqtyTextView.setVisibility(View.VISIBLE);
                mtotalTextView.setVisibility(View.VISIBLE);
                mrateTextView.setVisibility(View.VISIBLE);
                discountView.setVisibility(View.GONE);
                onPropertyEditClickListener.onClick(mtotalTextView);
            }
        }
        this.keyPadMode = keyPadMode;
    }

    View.OnClickListener onPropertyEditClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
        	
        	 Log.d("TAG", "onPropertyEditClickListener -- price->"+price+"rate->"+rate+"totalPrice->"+totalPrice+" qty--->"+qty);
            if(!view.isSelected()) {
                isNewEdit = true;
                view.setSelected(true);
                isDecimalUsed = false;
                if(currentSelectedTextView.getId() != view.getId())
                    currentSelectedTextView.setSelected(false);
                if(keyPadMode.ordinal() == KeyPadMode.QTY.ordinal()) {
                	isQTY=true;
                    try {
                        if(currentSelectedTextView.length() == 0)
                            throw new NumberFormatException();
                        float tempQty = Float.parseFloat(currentSelectedTextView.getText().toString());
                        if(tempQty == 0)
                            throw new NumberFormatException();
                        qty = tempQty;
                        if(skuUnitType.ordinal() == SkuUnitType.GM.ordinal() || skuUnitType.ordinal() == SkuUnitType.ML.ordinal()){
                        	totalPrice = rate * qty/1000;
                        }else{
                        	totalPrice = rate * qty;	
                        }    
                        mtotalTextView.setText(SnapBillingTextFormatter.getUnformattedPriceText(totalPrice, getActivity()));
                    } catch(NumberFormatException e) {
                        currentSelectedTextView.setText(qty + "");
                    }
                    
                } else if(keyPadMode.ordinal() == KeyPadMode.PRICE.ordinal()) {
                    try {
                        if(currentSelectedTextView.length() == 0)
                            throw new NumberFormatException();
                        price = Float.parseFloat(currentSelectedTextView.getText().toString().substring(1));
                        updateTextValues();
                    } catch(NumberFormatException e) {
                        currentSelectedTextView.setText(SnapBillingTextFormatter.getUnformattedPriceText(price, getActivity()));
                    }
                } else if(keyPadMode.ordinal() == KeyPadMode.TOTAL.ordinal()) {
                    try {
                        if(currentSelectedTextView.length() == 0)
                            throw new NumberFormatException();
                        totalPrice = Float.parseFloat(currentSelectedTextView.getText().toString().substring(1));
                        if(skuUnitType.ordinal() == SkuUnitType.GM.ordinal() || skuUnitType.ordinal() == SkuUnitType.ML.ordinal()){
                        	totalPrice = rate * qty/1000;

                        }else{
                        	 rate = totalPrice / qty;	
                        }   
                        updateTextValues();
                    } catch(NumberFormatException e) {
                        currentSelectedTextView.setText(SnapBillingTextFormatter.getUnformattedPriceText(totalPrice, getActivity()));
                    }
                } else if(keyPadMode.ordinal() == KeyPadMode.RATE.ordinal()) {
                	
                    try {
                        if(currentSelectedTextView.length() == 0)
                            throw new NumberFormatException();
                        rate = Float.parseFloat(currentSelectedTextView.getText().toString().substring(1)); 
                        
                        if(skuUnitType.ordinal() == SkuUnitType.GM.ordinal() || skuUnitType.ordinal() == SkuUnitType.ML.ordinal()){
                        	totalPrice = rate * qty/1000;
                        }else{
                        	totalPrice = rate * qty;	
                        }  
                        updateTextValues();
                    } catch(NumberFormatException e) {
                        currentSelectedTextView.setText(SnapBillingTextFormatter.getUnformattedPriceText(rate, getActivity()));
                    }
                }
                Log.d("TAG", "onPropertyEditClickListener --1 price->"+price+"rate->"+rate+"totalPrice->"+totalPrice);
                if(view.getId() == R.id.qty_value_textview) {
                    keyPadMode = KeyPadMode.QTY;
                    mValueTextView.setText(mqtyTextView.getText());
                    discountView.setVisibility(View.GONE);
                    increaseButton.setVisibility(View.VISIBLE);
                    decreaseButton.setVisibility(View.VISIBLE);
                    parentView.setGravity(Gravity.LEFT);
                } else if(view.getId() == R.id.price_value_textview) {
                    keyPadMode = KeyPadMode.PRICE;
                    mValueTextView.setText(mpriceTextView.getText().toString().substring(1));
                    if(enableDiscount)
                        discountView.setVisibility(View.GONE);
                    increaseButton.setVisibility(View.INVISIBLE);
                    decreaseButton.setVisibility(View.INVISIBLE);
                    parentView.setGravity(Gravity.CENTER_HORIZONTAL);
                } else if(view.getId() == R.id.rate_value_textview) {
                	isRate=true;
                    keyPadMode = KeyPadMode.RATE;
                    mValueTextView.setText(mrateTextView.getText().toString().substring(1));
                    if(enableDiscount)
                        discountView.setVisibility(View.GONE);
                    increaseButton.setVisibility(View.INVISIBLE);
                    decreaseButton.setVisibility(View.INVISIBLE);
                    parentView.setGravity(Gravity.CENTER_HORIZONTAL);
                }  else if(view.getId() == R.id.total_value_textview) {
                    keyPadMode = KeyPadMode.TOTAL;
                    mValueTextView.setText(mtotalTextView.getText().toString().substring(1));
                    if(enableDiscount)
                        discountView.setVisibility(View.GONE);
                    increaseButton.setVisibility(View.INVISIBLE);
                    decreaseButton.setVisibility(View.INVISIBLE);
                    parentView.setGravity(Gravity.RIGHT);
                }
                currentSelectedTextView = (TextView)view;
            }
            Log.d("TAG", "onPropertyEditClickListener --2 price->"+price+"rate->"+rate+"totalPrice->"+totalPrice);
        }
    };

    public void setArrowPosition(int position) {
        this.arrowPosition = position;
        if(getView() != null) {
            if(arrowPosition == POSITION_LEFT) {
                ((LinearLayout)getView()).setGravity(Gravity.LEFT);
            } else {
                ((LinearLayout)getView()).setGravity(Gravity.RIGHT);
            }
        }
    }

    View.OnClickListener onClearTextClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            mValueTextView.setText("");
            currentSelectedTextView.setText("");
            isNewEdit = true;
            isDecimalUsed = false;
        }
    };

    View.OnClickListener onDiscountClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            isNewEdit = true;
            isDecimalUsed = false;
            if(price == 0)
                return;
            rate = price - (price * Integer.parseInt((String) v.getTag()) / 100);
            totalPrice = rate * qty;
            Log.d("","totalPrice---->"+totalPrice);
            Log.d("","totalPrice---rate->"+rate);
            mrateTextView.setText(SnapBillingTextFormatter.getUnformattedPriceText(rate, getActivity()));
            if(skuUnitType.ordinal() == SkuUnitType.GM.ordinal() || skuUnitType.ordinal() == SkuUnitType.ML.ordinal()){
            	mtotalTextView.setText(SnapBillingTextFormatter.getUnformattedPriceText(totalPrice/1000, getActivity()));
            }else{ 
            	mtotalTextView.setText(SnapBillingTextFormatter.getUnformattedPriceText(totalPrice, getActivity()));
            }
            if(currentSelectedTextView.getId() == mpriceTextView.getId()) {
            	mrateTextView.setText(rate + "");
            } else
                mValueTextView.setText(totalPrice + "");
            
            Log.d("","totalPrice---->"+totalPrice);
            Log.d("","totalPrice---rate->"+rate);
        }
    };

    View.OnClickListener onValueChangeClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            isNewEdit = true;
            isDecimalUsed = false;
            float value = 0;
            try {
                value = Float.parseFloat(currentSelectedTextView.getText().toString());
            } catch(NumberFormatException e) {
                return;
            }
            if(v.getId() == R.id.button_increase) {
            	if(value>=9999999)
            	{
            		increaseButton.setEnabled(false);
            		increaseButton.setClickable(false);
            		decreaseButton.setEnabled(true);
            		decreaseButton.setClickable(true);
                    mValueTextView.setText(currentSelectedTextView.getText());
            	}
            	else
            	{
            	increaseButton.setEnabled(true);
            	increaseButton.setClickable(true);
            	decreaseButton.setEnabled(true);
            	decreaseButton.setClickable(true);
                currentSelectedTextView.setText((value+1) + "");
                mValueTextView.setText(currentSelectedTextView.getText());
              }
            } else if(v.getId() ==R.id.button_decrease) {
                if(value > 1) {
                	increaseButton.setEnabled(true);
                	increaseButton.setClickable(true);
                    currentSelectedTextView.setText((value-1) + "");
                    mValueTextView.setText(currentSelectedTextView.getText());
                }
            }
        }
    };

    View.OnClickListener onKeyClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            int value = Integer.parseInt((String) v.getTag());
            if(value == getResources().getInteger(R.integer.button_enter)) {
                isNewEdit = true;
                isDecimalUsed = false;

                if(keyPadMode.ordinal() == KeyPadMode.QTY.ordinal()) {
                    try {
                        if(currentSelectedTextView.length() == 0)
                            throw new NumberFormatException();
                        float tempQty = Float.parseFloat(currentSelectedTextView.getText().toString());
                        if(tempQty == 0)
                            throw new NumberFormatException();
                        if(skuUnitType.ordinal() == SkuUnitType.PC.ordinal()&&tempQty < 1)
                        	throw new NumberFormatException();
                        qty = tempQty;
                        if(skuUnitType.ordinal() == SkuUnitType.GM.ordinal() || skuUnitType.ordinal() == SkuUnitType.ML.ordinal()){
                        	totalPrice = rate * qty/1000;
                        }else{
                        	totalPrice = rate * qty;	
                        }     
                    
                    } catch(NumberFormatException e) {
                        currentSelectedTextView.setText(qty + "");
                        mValueTextView.setText(currentSelectedTextView.getText());
                        closeKeyPad();
                        return;
                    }
                } else if(keyPadMode.ordinal() == KeyPadMode.PRICE.ordinal()) {
                    try {
                        if(currentSelectedTextView.length() == 0)
                            throw new NumberFormatException();
                        price = Float.parseFloat(mValueTextView.getText().toString());
                        totalPrice = rate * qty;
                    } catch(NumberFormatException e) {
                        currentSelectedTextView.setText(SnapBillingTextFormatter.getUnformattedPriceText(price, getActivity()));
                        mValueTextView.setText(price + "");
                        closeKeyPad();
                        return;
                    }
                } else if(keyPadMode.ordinal() == KeyPadMode.RATE.ordinal()) {
                	isRate=true;
                	 try {
                         if(currentSelectedTextView.length() == 0)
                             throw new NumberFormatException();
                         rate = Float.parseFloat(mValueTextView.getText().toString());   
                         totalPrice = rate * qty;
                         Log.d("TAG", "onPropertyEditClickListener --KeyBoar- 3--- price->"+price+"rate->"+rate+"totalPrice->"+totalPrice+" qty--->"+qty); 
                     } catch(NumberFormatException e) {
                         currentSelectedTextView.setText(SnapBillingTextFormatter.getUnformattedPriceText(rate, getActivity()));
                         mValueTextView.setText(rate + ""); 
                         closeKeyPad();
                         return;
                     }
                } else if(keyPadMode.ordinal() == KeyPadMode.TOTAL.ordinal()) {
                    try {
                        if(currentSelectedTextView.length() == 0)
                            throw new NumberFormatException();
                        totalPrice = Float.parseFloat(mValueTextView.getText().toString());
                        if(skuUnitType.ordinal() == SkuUnitType.GM.ordinal() || skuUnitType.ordinal() == SkuUnitType.ML.ordinal()){
                       	 rate = totalPrice / qty*1000;
                        }else{
                       	 rate = totalPrice / qty;	
                        }  
                    } catch(NumberFormatException e) {
                        currentSelectedTextView.setText(SnapBillingTextFormatter.getUnformattedPriceText(totalPrice, getActivity()));
                        mValueTextView.setText(totalPrice + ""); 
                        closeKeyPad();
                        return;
                    }
                } 
                if(rate>price){
                		 rate=price; 
                }
                
                Log.d("TAG", "onPropertyEditClickListener --KeyBoar-- price->"+price+"rate->"+rate+"totalPrice->"+totalPrice+" qty--->"+qty);
                if(isQTY && isRate){
                	SnapToolkitConstants.IS_RATE_QTY_CHANGED=1;
                }
                else{
                	SnapToolkitConstants.IS_RATE_QTY_CHANGED=0;

                }
                keyboardEnterListener.onKeyBoardEnter(qty, price,rate, totalPrice, skuUnitType, context);

            } else {
                if(mValueTextView.length() > 6) {
                    return;
                }
                Log.d("length", "length " + mValueTextView.length() + "");
                if(isNewEdit) {
                    mValueTextView.setText("");
                    isNewEdit = false;
                }
                String val;
                if(value == getResources().getInteger(R.integer.button_dot)) {
                    if(!isDecimalUsed) {
                        val = ".";
                        isDecimalUsed = true;
                        if(mValueTextView.length() == 0)
                            mValueTextView.setText("0");
                        mValueTextView.append(val);
                    }
                } else {
                    val = ""+value;
                    mValueTextView.append(val);
                }
                if(keyPadMode.ordinal() == KeyPadMode.PRICE.ordinal() || keyPadMode.ordinal() == KeyPadMode.TOTAL.ordinal()||keyPadMode.ordinal() == KeyPadMode.RATE.ordinal()) {
                    currentSelectedTextView.setText(getString(R.string.rupee_symbol)+mValueTextView.getText());
                } else {
                    currentSelectedTextView.setText(mValueTextView.getText());
                }
            }
        }
    };
    private Runnable assignTasksRunnable;

    @Override
    public void onDetach() {
        super.onDetach();
        keyboardEnterListener.onKeyBoardDetach();
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        assignTasksRunnable =  new Runnable() {
            
            @Override
            public void run() {
                getView().findViewById(R.id.button0).setOnClickListener(onKeyClickListener);
                getView().findViewById(R.id.button1).setOnClickListener(onKeyClickListener);
                getView().findViewById(R.id.button2).setOnClickListener(onKeyClickListener);
                getView().findViewById(R.id.button3).setOnClickListener(onKeyClickListener);
                getView().findViewById(R.id.button4).setOnClickListener(onKeyClickListener);
                getView().findViewById(R.id.button5).setOnClickListener(onKeyClickListener);
                getView().findViewById(R.id.button6).setOnClickListener(onKeyClickListener);
                getView().findViewById(R.id.button7).setOnClickListener(onKeyClickListener);
                getView().findViewById(R.id.button8).setOnClickListener(onKeyClickListener);
                getView().findViewById(R.id.button9).setOnClickListener(onKeyClickListener);
                getView().findViewById(R.id.buttondot).setOnClickListener(onKeyClickListener);
                getView().findViewById(R.id.buttonenter).setOnClickListener(onKeyClickListener);
                getView().findViewById(R.id.button_discount1).setOnClickListener(onDiscountClickListener);
                getView().findViewById(R.id.button_discount2).setOnClickListener(onDiscountClickListener);
                getView().findViewById(R.id.button_discount3).setOnClickListener(onDiscountClickListener);
                getView().findViewById(R.id.button_clear).setOnClickListener(onClearTextClickListener);
                unitTypeSpinner = (Button) getView().findViewById(R.id.unittype_spinner);
                unitTypeListView = (ListView) getView().findViewById(R.id.units_listview);
                unitTypeSpinner.setOnClickListener(onUnitTypeClickListener);
                increaseButton = getView().findViewById(R.id.button_increase);
                increaseButton.setOnClickListener(onValueChangeClickListener);
                decreaseButton = getView().findViewById(R.id.button_decrease);
                decreaseButton.setOnClickListener(onValueChangeClickListener);
                mqtyTextView = (TextView) getView().findViewById(R.id.qty_value_textview);
                mpriceTextView = (TextView) getView().findViewById(R.id.price_value_textview);
                mrateTextView = (TextView) getView().findViewById(R.id.rate_value_textview);
                
                mtotalTextView = (TextView) getView().findViewById(R.id.total_value_textview);
                mqtyTextView.setOnClickListener(onPropertyEditClickListener);
                mtotalTextView.setOnClickListener(onPropertyEditClickListener);
                mpriceTextView.setOnClickListener(onPropertyEditClickListener);
                mrateTextView.setOnClickListener(onPropertyEditClickListener);
                discountView = getView().findViewById(R.id.discount_linearlayout);
                mValueTextView = (TextView) getView().findViewById(R.id.value_textview);
                arrowImageView = getView().findViewById(R.id.keypad_arrow_imageview);
                parentView = (LinearLayout) getView().findViewById(R.id.keypad_linearlayout);
                mainParentView = (LinearLayout) getView().findViewById(R.id.fragment_editkeypad_mainlayout);
                arrowImageView.setVisibility(arrowVisibility);
                if(arrowPosition == POSITION_LEFT) {
                    ((LinearLayout) getView()).setGravity(Gravity.LEFT);
                } else {
                    ((LinearLayout) getView()).setGravity(Gravity.RIGHT);
                }
                updateTextValues();
                mainParentView.setVisibility(View.GONE);
                updateViews();
                unitTypeListView.setAdapter(spinnerArrayAdapter);
                if(skuUnitType.ordinal() == SkuUnitType.GM.ordinal() || skuUnitType.ordinal() == SkuUnitType.ML.ordinal()) {
                    unitTypeSpinner.setText(spinnerArrayAdapter.getItem(1));
                } else {
                    unitTypeSpinner.setText(spinnerArrayAdapter.getItem(0));
                }
                unitTypeListView.setOnItemClickListener(onUnitTypeSelectListener);
            }
        };
        assignTasksHandler.post(assignTasksRunnable);
    }
    
    @Override
    public void onStop() {
        assignTasksHandler.removeCallbacks(assignTasksRunnable);
        assignTasksRunnable = null;
        super.onStop();
    }

    public interface KeyboardListener {
        public void onKeyBoardEnter(float qty, double price, double rate, double totalPrice, SkuUnitType skuUnitType, int context);
        public void onKeyBoardDetach();
    }
public void closeKeyPad(){
	try {
		getActivity().getFragmentManager().beginTransaction().remove(this)
		.commit();
	} catch (NullPointerException e) {
		e.printStackTrace();
	}
}
}
