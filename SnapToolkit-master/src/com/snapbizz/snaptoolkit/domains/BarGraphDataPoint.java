package com.snapbizz.snaptoolkit.domains;

public class BarGraphDataPoint {

	private double dataPoint1;
	private double dataPoint2;
	private String label;
	private int bar1Height;
	private int bar2Height;
	
	public BarGraphDataPoint(String label, double dataPoint1, double dataPoint2) {
		this.label = label;
		this.dataPoint1 = dataPoint1;
		this.dataPoint2 = dataPoint2;
	}
	
	public BarGraphDataPoint(String label, double dataPoint1) {
		this.label = label;
		this.dataPoint1 = dataPoint1;
	}

	public int getBar1Height() {
		return bar1Height;
	}

	public void setBar1Height(int bar1Height) {
		this.bar1Height = bar1Height;
	}

	public int getBar2Height() {
		return bar2Height;
	}

	public void setBar2Height(int bar2Height) {
		this.bar2Height = bar2Height;
	}

	public double getDataPoint1() {
		return dataPoint1;
	}

	public void setDataPoint1(double dataPoint1) {
		this.dataPoint1 = dataPoint1;
	}

	public double getDataPoint2() {
		return dataPoint2;
	}

	public void setDataPoint2(double dataPoint2) {
		this.dataPoint2 = dataPoint2;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
}
