package com.snapbizz.snapbilling.utils;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;

public class TooltipWindow {

	private static final int MSG_DISMISS_TOOLTIP = 100;
	private PopupWindow tipWindow;
	private View contentView;
	
	public TooltipWindow(Context ctx, View convertView) {
		tipWindow = new PopupWindow(ctx);
		this.contentView = convertView;
	}

	public void showToolTip(View anchor) {

		tipWindow.setHeight(LayoutParams.WRAP_CONTENT);
		tipWindow.setWidth(LayoutParams.WRAP_CONTENT);

		tipWindow.setOutsideTouchable(true);
		tipWindow.setTouchable(true);
		tipWindow.setFocusable(true);
		tipWindow.setContentView(contentView);
		tipWindow.setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));

		int screen_pos[] = new int[2];
		// Get location of anchor view on screen
		anchor.getLocationOnScreen(screen_pos);

		// Get rectangle for anchor view
		Rect anchor_rect = new Rect(screen_pos[0], screen_pos[1], screen_pos[0]
				+ anchor.getWidth(), screen_pos[1] + anchor.getHeight());
		contentView.measure(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);

		int contentViewHeight = contentView.getMeasuredHeight();
		int contentViewWidth = contentView.getMeasuredWidth();
		int position_x = anchor_rect.centerX() - (contentViewWidth / 2);
		int position_y = anchor_rect.bottom - (contentViewHeight / 2) - 95;

		tipWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, position_x,
				position_y);

		// send message to handler to dismiss tipWindow after X milliseconds
		handler.sendEmptyMessageDelayed(MSG_DISMISS_TOOLTIP, 40000);
	}

	boolean isTooltipShown() {
		if (tipWindow != null && tipWindow.isShowing())
			return true;
		return false;
	}

	void dismissTooltip() {
		if (tipWindow != null && tipWindow.isShowing())
			tipWindow.dismiss();
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case MSG_DISMISS_TOOLTIP:
					if (tipWindow != null && tipWindow.isShowing())
						tipWindow.dismiss();
					break;
			}
		};
	};

}
