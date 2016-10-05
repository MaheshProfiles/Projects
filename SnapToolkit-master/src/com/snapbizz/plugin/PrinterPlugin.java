package com.snapbizz.plugin;

import java.util.List;

import com.snapbizz.snaptoolkit.utils.PrinterFormatter;
import com.snapbizz.snaptoolkit.utils.PrinterFormatter.PrintLine;
import com.snapbizz.snaptoolkit.utils.PrinterFormatter.PrintLinePart;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.Bitmap;

public class PrinterPlugin {
	protected BluetoothSocket socket = null;
	
	public enum PRINT_ALIGNMENT {
		LEFT_ALIGN,
		CENTER_ALIGN,
		RIGHT_ALIGN
	};
	
	public enum FONT_TYPE {
		FONT_NORMAL,
		FONT_CALIBRI,
		FONT_TAHOMA,
		FONT_VERDANA,
		FONT_FIXED,
		FONT_COURIER
	};
	
	public enum FONT_SIZE {
		FONT_RESET,
		FONT_NORMAL,
		FONT_SMALL,
		FONT_MEDIUM
	};
	
	public enum FONT_STYLE {
		FONT_REGULAR,
		FONT_BOLD,
		FONT_UNDERLINE,
		FONT_ITALICS
	}

	public interface BTPrinterPlugin {
		public boolean checkPluginSupport(BluetoothDevice device);
		public void reInitBtSocket(BluetoothSocket socket);
		public boolean printImage(Bitmap bitmap, PRINT_ALIGNMENT alignment);
		public boolean printText(String text);
		public boolean printUnicode(String text);
		public boolean setFont(FONT_TYPE font, FONT_SIZE size, FONT_STYLE[] styles, boolean bReset);
		public boolean sendLineFeed(int n);
		public boolean sendCarriageReturn();
		public int getMaxLength();
		public boolean printAll(PrinterFormatter formatter);
	}
	
	public boolean isPrinterConnected() {
		if(socket != null && socket.isConnected())
			return true;
		return false;
	}
	
	private boolean checkStyles(FONT_STYLE[] styles1, FONT_STYLE[] styles2) {
		if(styles1 == null && styles2 == null)
			return true;
		if(styles1 == null || styles2 == null)
			return false;
		if(styles1.length != styles2.length)
			return false;
		for(int i = 0; i < styles1.length; i++) {
			if(styles1[i] != styles2[i])
				return false;
		}
		return true;
	}
	
	public boolean printAll(PrinterFormatter formatter) {
		if(!(this instanceof BTPrinterPlugin))
			return false;

		FONT_TYPE prevFont = null;
		FONT_SIZE prevSize = null;
		FONT_STYLE[] prevStyles = null;

		BTPrinterPlugin plugin = (BTPrinterPlugin)this;
		List<PrintLine> lines = formatter.convertLines();
		for(PrintLine line : lines) {
			for(PrintLinePart part : line.lineParts) {
				if(part.isImage) {
					// TODO Implement this
					continue;
				}
				FONT_TYPE font = prevFont != part.font ? part.font : null;
				FONT_SIZE size = prevSize != part.fontSize ? part.fontSize : null;
				FONT_STYLE[] styles = !checkStyles(prevStyles, part.styles) ? part.styles : null;
				
				prevFont = font;
				prevSize = size;
				prevStyles = styles;

				// TODO: Check this
				 plugin.setFont(font, size, styles, false);
				if(part.isUnicode) {
					if(!plugin.printUnicode(part.content))
						return false;
				} else {
					if(!plugin.printText(part.content))
						return false;
				}
			}
			plugin.sendCarriageReturn();
			//plugin.setFont(null, null, null, true);
		}
		plugin.sendLineFeed(4);
		return true;
	}
}
