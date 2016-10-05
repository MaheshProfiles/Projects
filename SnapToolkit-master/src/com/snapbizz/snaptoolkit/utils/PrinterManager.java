package com.snapbizz.snaptoolkit.utils;

import java.util.Date;

import android.content.Context;

import com.snapbizz.snaptoolkit.domains.BillPrint;
import com.snapbizz.snaptoolkit.domains.CustomerCreditPayment;
import com.snapbizz.snaptoolkit.domains.ShoppingCart;


public abstract class PrinterManager {
    
    public abstract PrinterManager createPrinterManager(Context context);
    public abstract boolean isPrinterConnected();
    public abstract boolean printText(BillPrint billPrint);
    public abstract boolean printText(String printText);
    public abstract boolean printShoppingCart(ShoppingCart shoppingCart, Date date);
    public abstract boolean printCustomerCreditPayment(CustomerCreditPayment customerCreditPayment, Date date);
    public abstract PrinterType getPrinterType();
    public abstract void connect();
    
}
