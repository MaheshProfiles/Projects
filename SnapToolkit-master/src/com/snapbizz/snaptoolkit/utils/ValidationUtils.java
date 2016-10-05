package com.snapbizz.snaptoolkit.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;

import com.snapbizz.snaptoolkit.R;

public class ValidationUtils {

	public static Pattern pattern;
	private static Matcher matcher;

	public static boolean validateEmailID(String email) {
		pattern = Pattern.compile(SnapToolkitConstants.EMAIL_REGEX);
		matcher = pattern.matcher(email);
		return matcher.matches();
	}

	public static boolean validateNumber(String text) {
        pattern = Pattern.compile(SnapToolkitConstants.NUMBER_REGEX);
        matcher = pattern.matcher(text);
        return matcher.matches();
    }
	
	public static boolean validateMobileNo(String text) {
		pattern = Pattern.compile(SnapToolkitConstants.MOBILE_REGEX);
		matcher = pattern.matcher(text);
		return matcher.matches();
	}
	
	public static boolean validateLandlineNo(String text) {
        pattern = Pattern.compile(SnapToolkitConstants.LANDLINE_REGEX);
        matcher = pattern.matcher(text);
        return matcher.matches();
    }
	
	public static boolean validateName(String text) {
		pattern = Pattern.compile(SnapToolkitConstants.NAME_REGEX);
		matcher = pattern.matcher(text);
		return matcher.matches();
	}
	
	public static boolean validateStoreName(String text) {
	    pattern = Pattern.compile(SnapToolkitConstants.ALPHANUMERIC_REGEX);
	    matcher = pattern.matcher(text);
	    return matcher.matches();
	}
	
	public static boolean isQuickaddProduct(String skuCode, Context context) {
		for(String quickaddCode : context.getResources().getStringArray(R.array.quickadd_array)) {
			if(quickaddCode.equalsIgnoreCase(skuCode))
				return true;
		}
		return false;
	}
	
	public static boolean validateTin(String text){
		pattern = Pattern.compile(SnapToolkitConstants.TIN_NUMBER_REGEX);
		matcher = pattern.matcher(text);
		return matcher.matches();
	}
	
	public static boolean validateAddress(String text){
		pattern = Pattern.compile(SnapToolkitConstants.ADDRESS_REGEX);
		matcher = pattern.matcher(text);
		return matcher.matches();
	}
	
	public static boolean validateZip(String text){
		pattern = Pattern.compile(SnapToolkitConstants.ZIP_REGEX);
		matcher = pattern.matcher(text);
		return matcher.matches();
	}
}
