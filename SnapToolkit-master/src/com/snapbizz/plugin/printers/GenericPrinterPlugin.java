package com.snapbizz.plugin.printers;

import com.snapbizz.plugin.PrinterPlugin;
import com.snapbizz.plugin.PrinterPlugin.BTPrinterPlugin;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.Bitmap;

public class GenericPrinterPlugin extends PrinterPlugin implements BTPrinterPlugin {

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
