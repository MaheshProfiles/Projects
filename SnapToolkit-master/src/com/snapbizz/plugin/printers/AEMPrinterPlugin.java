package com.snapbizz.plugin.printers;

import java.io.IOException;

import com.snapbizz.plugin.PrinterPlugin;
import com.snapbizz.plugin.PrinterPlugin.BTPrinterPlugin;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.aem.api.AEMPrinter;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.graphics.Bitmap;

public class AEMPrinterPlugin extends PrinterPlugin implements BTPrinterPlugin {
	private static final int MAX_LENGTH_2_INCH = 32;
	private static final int MAX_LENGTH_3_INCH = 45;
	private AEMPrinter mDevice;
	private Context context;
	private int maxLength = MAX_LENGTH_2_INCH;
	
	public AEMPrinterPlugin(Context context) {
		this.context = context;
		if(SnapSharedUtils.getSelectedPrinterWidth(context) == 3)
			maxLength = MAX_LENGTH_3_INCH;
		else
			maxLength = MAX_LENGTH_2_INCH;
	}
	
	@Override
	public void reInitBtSocket(BluetoothSocket socket) {
		mDevice = new AEMPrinter(socket);
		this.socket = socket;
	}

	@Override
	public boolean printImage(Bitmap bitmap, PRINT_ALIGNMENT alignment) {
		if(!isPrinterConnected() || mDevice == null)
			return false;

		byte align = AEMPrinter.IMAGE_LEFT_ALIGNMENT;
		if(alignment == PRINT_ALIGNMENT.CENTER_ALIGN)
			align = AEMPrinter.IMAGE_CENTER_ALIGNMENT;
		if(alignment == PRINT_ALIGNMENT.RIGHT_ALIGN)
			align = AEMPrinter.IMAGE_RIGHT_ALIGNMENT;

		try {
			mDevice.printImage(bitmap, context, align);
		} catch(IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	@Override
	public boolean printText(String text) {
		if(!isPrinterConnected() || mDevice == null)
			return false;
		try {
			socket.getOutputStream().write(text.getBytes(), 0,  text.getBytes().length);
		} catch(IOException e) {
			return false;
		}
		return true;
	}

	@Override
	public boolean printUnicode(String text) {
		if(!isPrinterConnected() || mDevice == null)
			return false;
		// TODO: Implement this
		return false;
	}
	
	private void setFontType(FONT_TYPE font_type) throws IOException {
		final byte[] FONT_004 = {0X1B, 0x30}; //FixedSys Font
		final byte[] FONT_005 = {0X1B, 0x31}; //Courier Font
		
		byte font = AEMPrinter.FONT_NORMAL;
		if(font_type != null) {
			switch(font_type) {
				case FONT_CALIBRI:
					font = AEMPrinter.FONT_001;
					break;
				case FONT_TAHOMA:
					font = AEMPrinter.FONT_002;
					break;
				case FONT_VERDANA:
					font = AEMPrinter.FONT_003;
					break;

				case FONT_FIXED:
					socket.getOutputStream().write(FONT_004);
					return;
					
				case FONT_COURIER:
					socket.getOutputStream().write(FONT_005);
					return;

				case FONT_NORMAL:
				default:
					font = AEMPrinter.FONT_NORMAL;
					break;
			}
		}
		mDevice.setFontType(font);
	}
	
	private void setFontSize(FONT_SIZE size) throws IOException {
		final byte FONT_SIZE_NORMAL[] = {29, 68, 3};
		// final byte FONT_SIZE_LOW[] = {29, 68, 3};
		// Font Size
		final byte FONT_SMALL[] = {29, 33, 0};
		final byte FONT_MEDIUM[] = {29, 33, 1};
		
		byte font_size[] = FONT_SIZE_NORMAL;
		if(size != null) {
			switch(size) {
				default:
				case FONT_RESET:
				case FONT_NORMAL:
					font_size = FONT_SIZE_NORMAL;
					break;
				case FONT_SMALL:
					font_size = FONT_SMALL;
					break;
				case FONT_MEDIUM:
					font_size = FONT_MEDIUM;
					break;
			}
		}
		socket.getOutputStream().write(font_size);
	}

	private void setFontStyle(FONT_STYLE style) throws IOException {
		byte REGULAR[] = {27, 69, 0};
		final byte[] BOLD = {27, 69, 1};

		byte font_style = AEMPrinter.FONT_NORMAL;
		if(style != null) {
			switch(style) {
				case FONT_REGULAR:
					socket.getOutputStream().write(REGULAR);
					return;
				case FONT_BOLD:
					socket.getOutputStream().write(BOLD);
					return;
				case FONT_UNDERLINE:
					mDevice.enableUnderline();
					return;
				case FONT_ITALICS:
					// TODO
					break;
			}
		}
		// Reuse the same sdk function for font_style as well
		mDevice.setFontSize(font_style);
	}

	@Override
	public boolean setFont(FONT_TYPE font, FONT_SIZE size, FONT_STYLE[] styles, boolean bReset) {
		try {
			// Reset font size before writing new font
			if(bReset)
				setFontSize(FONT_SIZE.FONT_RESET);
			if(font != null)
				setFontType(font);
			if(size != null)
				setFontSize(size);
			if(styles != null) {
				for(FONT_STYLE style : styles)
					setFontStyle(style);
			}
		} catch(IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean sendLineFeed(int n) {
		try {
			mDevice.setLineFeed(n);
		} catch(IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean sendCarriageReturn() {
		try {
			mDevice.setCarriageReturn();
		} catch(IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean checkPluginSupport(BluetoothDevice device) {
		String deviceName = device.getName();
		if(deviceName != null && deviceName.contains("BT"))	// Or check for 'rinter'
			return true;
		return false;
	}

	@Override
	public int getMaxLength() {
		return maxLength;
	}
}
