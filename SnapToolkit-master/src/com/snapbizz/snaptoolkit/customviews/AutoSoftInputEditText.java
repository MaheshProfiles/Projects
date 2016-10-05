package com.snapbizz.snaptoolkit.customviews;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;

public class AutoSoftInputEditText extends AutoCompleteTextView {
	
	private boolean mIsKeyboardVisible;

	public AutoSoftInputEditText(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public AutoSoftInputEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		setOnKeyListener(onKeyListener);
		int mInputType = this.getInputType();
		this.setInputType(mInputType | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS |InputType.TYPE_CLASS_TEXT);
	}
	
	

	@Override
	public boolean onKeyPreIme(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN){

			InputMethodManager imm = (InputMethodManager) getContext()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			if(mIsKeyboardVisible ){
				Log.d("TAG","Back button pressed inside if");
			imm.hideSoftInputFromWindow(getWindowToken(), 0);
			mIsKeyboardVisible = false;
			return true;
			}
			else{
				return false;
			}
		}
		return false;

	}



	OnKeyListener onKeyListener = new OnKeyListener() {

		@Override
		public boolean onKey(View view, int keyCode, KeyEvent event) {
			if (event.getDeviceId() == -1)
				return false;
			else if (event.getAction() == KeyEvent.ACTION_DOWN) {
				((OnAutoExternalKeyInputListener) getContext()).onAutoExternalKeyDown(view, keyCode, event);
				return true;
			} else
				return false;
		}
	};

	
	public interface OnAutoExternalKeyInputListener {
		public void onAutoExternalKeyDown(View view, int keyCode, KeyEvent event);
	}
	
	public AutoSoftInputEditText(Context context) {
		super(context);
		init();
	}

	@Override
	protected void onDetachedFromWindow() {
		setOnKeyListener(null);
		setOnFocusChangeListener(null);
		super.onDetachedFromWindow();
	}

}
