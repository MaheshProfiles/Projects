package com.snapbizz.snaptoolkit.domains;

import com.google.gson.annotations.SerializedName;

public class SnapMoneyPieData extends ResponseContainer {

	@SerializedName("dataPoints")
	private double [] dataPoints;
	@SerializedName("labels")
	private String [] labels;
	@SerializedName("colors")
	private int [] colors;
	
	public int[] getColors() {
		return colors;
	}
	public void setColors(int[] colors) {
		this.colors = colors;
	}
	public double[] getDataPoints() {
		return dataPoints;
	}
	public void setDataPoints(double[] dataPoints) {
		this.dataPoints = dataPoints;
	}
	public String[] getLabels() {
		return labels;
	}
	public void setLabels(String[] labels) {
		this.labels = labels;
	}
	
}
