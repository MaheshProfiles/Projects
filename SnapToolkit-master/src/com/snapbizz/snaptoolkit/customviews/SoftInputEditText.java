package com.snapbizz.snaptoolkit.customviews;

import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

public class SoftInputEditText extends EditText {

	public SoftInputEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public SoftInputEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private void init(){
	    setOnKeyListener(onKeyListener);   
        int mInputType = this.getInputType();
        this.setInputType(mInputType | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
	}
	
	OnKeyListener onKeyListener = new OnKeyListener() {

		@Override
		public boolean onKey(View view, int keyCode, KeyEvent event) {
			if(null != event && null != event.getDevice() && event.getDevice().getId() >= 0)
				return ((OnExternalKeyInputListener) getContext()).onExternalKeyDown(view, keyCode, event);
		    return false;
		}
	};
	
	public SoftInputEditText(Context context) {
		super(context);
		init();
	}

	public interface OnExternalKeyInputListener {
		public boolean onExternalKeyDown(View view, int keyCode, KeyEvent event);
	}
	
	@Override
	protected void onDetachedFromWindow() {
	    setOnKeyListener(null);
	    setOnFocusChangeListener(null);
	    super.onDetachedFromWindow();
	}

}
