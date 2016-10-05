package com.snapbizz.snaptoolkit.utils;

public enum SkuUnitType {
	
	PC("Pc"),
	KG("Kg"),
	GM("gm"),
	LTR("L"),
	ML("ml");
	
	private String value;

	private SkuUnitType (String value){
		this.value = value;
	}

	public String getUnitValue(){
		return this.value;
	}

	public static SkuUnitType getEnum(String val) {
		for(SkuUnitType skuUnitType : SkuUnitType.values()) {
			if(skuUnitType.value.equalsIgnoreCase(val)){
				return skuUnitType;
			}
		}
		return null;
	}
}
