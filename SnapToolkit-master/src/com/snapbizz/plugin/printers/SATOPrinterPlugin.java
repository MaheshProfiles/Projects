package com.snapbizz.plugin.printers;

import com.snapbizz.plugin.PrinterPlugin;
import com.snapbizz.plugin.PrinterPlugin.BTPrinterPlugin;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.graphics.Bitmap;

public class SATOPrinterPlugin extends PrinterPlugin implements BTPrinterPlugin {
	private static final int MAX_LENGTH_2_INCH = 32;
	private static final int MAX_LENGTH_3_INCH = 45;

	private Context context = null;
	private int maxLength = MAX_LENGTH_2_INCH;
	
	public SATOPrinterPlugin(Context context) {
		this.context = context;
		if(SnapSharedUtils.getSelectedPrinterWidth(context) == 3)
			maxLength = MAX_LENGTH_3_INCH;
		else
			maxLength = MAX_LENGTH_2_INCH;
	}

	@Override
	public boolean checkPluginSupport(BluetoothDevice device) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void reInitBtSocket(BluetoothSocket socket) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean printImage(Bitmap bitmap, PRINT_ALIGNMENT alignment) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean printText(String text) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean printUnicode(String text) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setFont(FONT_TYPE font, FONT_SIZE size, FONT_STYLE[] styles, boolean bReset) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean sendLineFeed(int n) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean sendCarriageReturn() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getMaxLength() {
		// TODO Auto-generated method stub
		return 0;
	}
	

}
