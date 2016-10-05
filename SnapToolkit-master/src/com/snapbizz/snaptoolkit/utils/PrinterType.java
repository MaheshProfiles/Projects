package com.snapbizz.snaptoolkit.utils;

public enum PrinterType {

	AEM_USB_PRINTER("AEM USB Printer"),
	AEM_BLUETOOTH_PRINTER("AEM | DYNO | LS Bluetooth 2\" Printer"),
	AEM_3INCH_BLUETOOTH_PRINTER("AEM | DYNO | LS Bluetooth 3\" Printer"),
	AEM_3INCH_USB_PRINTER("AEM 3 inch USB Printer"),
	NGX_3INCH_PRINTER("NGX 3 Inch Printer"),
	NGX_2INCH_PRINTER("NGX 2 Inch Printer"),
	TVS_USB_PRINTER("TVS USB Printer");
    
    private String printerType;

    private PrinterType(String printerType){
        this.printerType = printerType;
    }
    
    public String getPrinterType(){
        return this.printerType;
    }
    
    public static PrinterType getPrinterEnum(String PrinterValue){
        for (PrinterType printerType : PrinterType.values()) {
            if(printerType.printerType.equals(PrinterValue))
                return printerType;
        }
        return null;
    }
    
}
