package com.snapbizz.snaptoolkit.interfaces;


public interface OnQueryCompleteListener {
	
	public void onTaskSuccess(Object responseList, int taskCode);
	public void onTaskError(String errorMessage, int taskCode);

}
