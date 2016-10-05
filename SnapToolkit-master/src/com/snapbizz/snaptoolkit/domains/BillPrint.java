package com.snapbizz.snaptoolkit.domains;

public class BillPrint {
	
	private String billHead;
	private String billBody;
	private String billNetAmount;
	private String billCashStatus;
	private String billSaving;
	private String billBottom;
	private int maxPrintchars;
	public String getBillHead() {
		return billHead;
	}
	public void setBillHead(String billHead) {
		this.billHead = billHead;
	}
	public String getBillBody() {
		return billBody;
	}
	public void setBillBody(String billBody) {
		this.billBody = billBody;
	}
	public String getBillNetAmount() {
		return billNetAmount;
	}
	public void setBillNetAmount(String billNetAmount) {
		this.billNetAmount = billNetAmount;
	}
	
	public String getBillSaving() {
		return billSaving;
	}
	public void setBillSaving(String billSaving) {
		this.billSaving = billSaving;
	}
	public String getBillBottom() {
		return billBottom;
	}
	public void setBillBottom(String billBottom) {
		this.billBottom = billBottom;
	}
	public String getBillCashStatus() {
		return billCashStatus;
	}
	public void setBillCashStatus(String billCashStatus) {
		this.billCashStatus = billCashStatus;
	}
	public int getMaxPrintchars() {
		return maxPrintchars;
	}
	public void setMaxPrintchars(int maxPrintchars) {
		this.maxPrintchars = maxPrintchars;
	}
	
	
	
}
