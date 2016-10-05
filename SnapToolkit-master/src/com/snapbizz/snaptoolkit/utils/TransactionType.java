package com.snapbizz.snaptoolkit.utils;

public enum TransactionType {

	BILL("bill"), 
	MEMO("memo"),
	DELIVERY_NOTE("delivery_note"),
	DELIVERY_MEMO("delivery_memo"), 
	DELIVERY_INVOICE("delivery_invoice"),
	ALL("all");
	
	private String transactionType;

	private TransactionType (String transactionType){
		this.transactionType = transactionType;
	}

	public String getTransactionType(){
		return this.transactionType;
	}

	public static TransactionType getEnum(String val) {
		for(TransactionType transactionType : TransactionType.values()) {
			if(transactionType.transactionType.equals(val)){
				return transactionType;
			}
		}
		return null;
	}
	
	public static TransactionType getNext(TransactionType transactionType) {
		
	     return transactionType.ordinal() < TransactionType.values().length - 1
	         ?  TransactionType.values()[transactionType.ordinal() + 1]
	         : TransactionType.values()[0];
	   }

}
