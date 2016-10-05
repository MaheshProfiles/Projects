package com.snapbizz.snaptoolkit.utils;

import java.util.HashMap;

import android.content.Context;
import android.util.Log;

public class PrinterFactory {

    private static HashMap<PrinterType, PrinterManager> printerFactoryMap;

    public static void registerPrinter(PrinterManager printerManager) {
        if(printerFactoryMap == null)
            printerFactoryMap = new HashMap<PrinterType, PrinterManager>();
        printerFactoryMap.put(printerManager.getPrinterType(), printerManager);
    }

    public static PrinterManager createPrinterManager(PrinterType printerType, Context context) {
        if(printerFactoryMap == null)
            return null;
        else
            return printerFactoryMap.get(printerType).createPrinterManager(context);
    }

}
