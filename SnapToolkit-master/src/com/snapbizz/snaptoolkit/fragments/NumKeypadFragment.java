package com.snapbizz.snaptoolkit.fragments;

import java.text.DecimalFormat;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.snapbizz.snaptoolkit.R;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class NumKeypadFragment extends Fragment {

	private TextView mValueTextView;
	private int context;
	private NumberKeypadEnterListener numberKeypadEnterListener;
	private boolean isNewEdit = true;
	private View discountView;
	private boolean isDiscount = false;
	private String price;
	private double totalPrice;
	private boolean isDecimalUsed = false;
	private Handler assignTasksHandler = new Handler();
	private Runnable assignTasksRunnable;
	private DecimalFormat decimalFormat = new DecimalFormat("#####.##");

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_numkeypad, null);
	}

	public boolean isNewEdit() {
		return isNewEdit;
	}

	public void setNewEdit(boolean isNewEdit) {
		this.isNewEdit = isNewEdit;
		if (isNewEdit)
			isDecimalUsed = false;
	}

	public void setContext(int context) {
		this.context = context;
		setNewEdit(true);
	}

	public void setKeypadEnterListener(
			NumberKeypadEnterListener numberKeypadEnterListener) {
		this.numberKeypadEnterListener = numberKeypadEnterListener;
	}

	public String getPrice() {
		return price;
	}

	public void setValue(String price) {
		this.price = price;
		if (getActivity() != null && getView() != null
				&& mValueTextView != null) {
			mValueTextView.setText(price);
		}
	}

	public void setIsDiscount(boolean isDiscount) {

		this.isDiscount = isDiscount;
		if (getActivity() != null && discountView != null) {
			discountView.setVisibility(isDiscount ? View.VISIBLE : View.GONE);
		}
	}

	public void setTotalValue(double totalPrice) {
		this.totalPrice = totalPrice;
	}

	View.OnClickListener onClearTextClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View view) {
			mValueTextView.setText("");
			setNewEdit(true);
		}
	};

	View.OnClickListener onDiscountClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			isNewEdit = true;
			isDecimalUsed = false;
			if (totalPrice == 0)
				return;
			float newPrice = 0;
			if(v.getId() == R.id.button_discount3) {
				String curValue = mValueTextView.getText().toString();
				if(curValue != null && !curValue.trim().isEmpty()) {
					try {
						double mDiscount = Double.valueOf(curValue);
						if(mDiscount <= 100)
							newPrice = (float) (totalPrice * mDiscount / 100);
					} catch(Exception e) { }
				}
			} else {
				newPrice = (float) (totalPrice * Integer.parseInt((String) v.getTag()) / 100);
			}
			mValueTextView.setText(String.valueOf(newPrice));
		}
	};

	View.OnClickListener onKeyClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			int value = Integer.parseInt((String) v.getTag());
			if (value == getResources().getInteger(R.integer.button_enter)) {
				isNewEdit = true;
				isDecimalUsed = false;
				try {
					if (mValueTextView.length() == 0)
						throw new NumberFormatException();
					price = (mValueTextView.getText().toString());
				} catch (NumberFormatException e) {
					mValueTextView.setText(String.valueOf(price));
					return;
				}
				mValueTextView.setText("");
				if (context == SnapToolkitConstants.EDIT_DISCOUNT_ON_ITEM_CONTEXT && price != null)
					numberKeypadEnterListener.onNumKeyPadEnter(String.valueOf(decimalFormat.format(totalPrice - Float.valueOf(price))), context);
				else
					numberKeypadEnterListener.onNumKeyPadEnter(price, context);

			} else {
				if (isNewEdit) {
					mValueTextView.setText("");
					isNewEdit = false;
				}
				String val;
				if (value == getResources().getInteger(R.integer.button_dot)) {
					if (!isDecimalUsed) {
						val = ".";
						isDecimalUsed = true;
						if (mValueTextView.length() == 0)
							mValueTextView.setText("0");
						mValueTextView.append(val);
					}
				} else {
					val = "" + value;
					mValueTextView.append(val);
				}
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
		SnapCommonUtils.hideSoftKeyboard(getActivity(), getView().getWindowToken());
		assignTasksRunnable = new Runnable() {

			@Override
			public void run() {
				getView().findViewById(R.id.button0).setOnClickListener(
						onKeyClickListener);
				getView().findViewById(R.id.button1).setOnClickListener(
						onKeyClickListener);
				getView().findViewById(R.id.button2).setOnClickListener(
						onKeyClickListener);
				getView().findViewById(R.id.button3).setOnClickListener(
						onKeyClickListener);
				getView().findViewById(R.id.button4).setOnClickListener(
						onKeyClickListener);
				getView().findViewById(R.id.button5).setOnClickListener(
						onKeyClickListener);
				getView().findViewById(R.id.button6).setOnClickListener(
						onKeyClickListener);
				getView().findViewById(R.id.button7).setOnClickListener(
						onKeyClickListener);
				getView().findViewById(R.id.button8).setOnClickListener(
						onKeyClickListener);
				getView().findViewById(R.id.button9).setOnClickListener(
						onKeyClickListener);
				getView().findViewById(R.id.buttondot).setOnClickListener(
						onKeyClickListener);
				getView().findViewById(R.id.buttonenter).setOnClickListener(
						onKeyClickListener);
				getView().findViewById(R.id.button_discount1)
						.setOnClickListener(onDiscountClickListener);
				getView().findViewById(R.id.button_discount2)
						.setOnClickListener(onDiscountClickListener);
				getView().findViewById(R.id.button_discount3)
						.setOnClickListener(onDiscountClickListener);
				getView().findViewById(R.id.button_clear).setOnClickListener(
						onClearTextClickListener);
				mValueTextView = (TextView) getView().findViewById(
						R.id.value_textview);
				mValueTextView.setText(price + "");
				discountView = getView().findViewById(
						R.id.discount_linearlayout);
				if (!isDiscount)
					discountView.setVisibility(View.GONE);
				else
					discountView.setVisibility(View.VISIBLE);
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

	public interface NumberKeypadEnterListener {
		public void onNumKeyPadEnter(String value, int context);
	}

}