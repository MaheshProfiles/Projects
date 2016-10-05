package com.snapbizz.snapbilling.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.snapbizz.snapbilling.R;

public class HorzNumKeypadFragment extends Fragment {

	private String value = "";
	private int context;
	private NumberKeypadListener numberKeypadListener;
	private boolean isNewEdit = true;
	private float price = 0;
	private boolean isDecimalUsed = false;
	private boolean showDecimal;
	private int maxChars = 0;
	
	public void setMaxCharacters(int maxCharacters) {
		this.maxChars = maxCharacters;
	}

	public boolean isDecimalUsed() {
		return isDecimalUsed;
	}

	public void setDecimalUsed(boolean isDecimalUsed) {
		this.isDecimalUsed = isDecimalUsed;
	}

	public void setShowDecimal(boolean showDecimal) {
		this.showDecimal = showDecimal;
		if(getView() != null) {
			if(!showDecimal)
				getView().findViewById(R.id.buttondot).setVisibility(View.GONE);
			else
				getView().findViewById(R.id.buttondot).setVisibility(View.VISIBLE);
		}
	}

	public boolean isNewEdit() {
		return isNewEdit;
	}

	public void setNewEdit(boolean isNewEdit) {
		this.isNewEdit = isNewEdit;
	}

	public void setValue(String value) {
		if(value == null) {
			this.value = "";
		} else
			this.value = value;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_horznumpad, null);
		view.findViewById(R.id.button0).setOnClickListener(onKeyClickListener);
		view.findViewById(R.id.button1).setOnClickListener(onKeyClickListener);
		view.findViewById(R.id.button2).setOnClickListener(onKeyClickListener);
		view.findViewById(R.id.button3).setOnClickListener(onKeyClickListener);
		view.findViewById(R.id.button4).setOnClickListener(onKeyClickListener);
		view.findViewById(R.id.button5).setOnClickListener(onKeyClickListener);
		view.findViewById(R.id.button6).setOnClickListener(onKeyClickListener);
		view.findViewById(R.id.button7).setOnClickListener(onKeyClickListener);
		view.findViewById(R.id.button8).setOnClickListener(onKeyClickListener);
		view.findViewById(R.id.button9).setOnClickListener(onKeyClickListener);
		if(!showDecimal)
			view.findViewById(R.id.buttondot).setVisibility(View.GONE);
		else
			view.findViewById(R.id.buttondot).setOnClickListener(onKeyClickListener);
		view.findViewById(R.id.buttonenter).setOnClickListener(onKeyClickListener);
		view.findViewById(R.id.button_clear).setOnClickListener(onClearTextClickListener);
		return view;
	}

	public void setContext(int context) {
		this.context = context;
	}

	public void setKeypadEnterListener(NumberKeypadListener numberKeypadListener) {
		this.numberKeypadListener = numberKeypadListener;
	}

	public float getPrice() {
		return price;
	}

	View.OnClickListener onClearTextClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View view) {
			value = "";
			isDecimalUsed = false;
			isNewEdit = true;
			numberKeypadListener.onNumKeyDown(value, context);
		}
	};


	View.OnClickListener onKeyClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			int bttnValue = Integer.parseInt((String) v.getTag());
			if(bttnValue == getResources().getInteger(R.integer.button_enter)) {
				isNewEdit = true;
				isDecimalUsed = false;
				try {
					if(value.length() == 0)
						throw new NumberFormatException();
					price = Float.parseFloat(value);
				} catch(NumberFormatException e) {
					value = price +"";
					return;
				}
				numberKeypadListener.onNumKeyEnter(value, context);
			} else {
				if(value.length() == maxChars) {
					return;
				}
				if(isNewEdit) {
					value = "";
					isNewEdit = false;
				}
				String val;
				if(bttnValue == getResources().getInteger(R.integer.button_dot)) {
					if(!isDecimalUsed) {
						val = ".";
						isDecimalUsed = true;
						if(value.length() == 0)
							value = "0";
						value += val;
					}
				} else {
					value += bttnValue;
				}
				numberKeypadListener.onNumKeyDown(value, context);
			}
		}
	};

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	public interface NumberKeypadListener {
		public void onNumKeyDown(String value, int context);
		public void onNumKeyEnter(String value, int context);
	}

}
