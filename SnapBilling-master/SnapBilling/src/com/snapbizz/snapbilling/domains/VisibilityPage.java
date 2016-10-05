package com.snapbizz.snapbilling.domains;

import java.util.ArrayList;

public class VisibilityPage {

	
	private ArrayList<VisibilityItem> products;
	private String pageName;
	private int pageNumber;
	private int pageType;
	public final static int PAGE_TYPE_STORE=1;
	public final static int PAGE_TYPE_OFFERS=2;
	public final static int PAGE_TYPE_FORGET=3;
	public final static int PAGE_TYPE_SUGGESTIONS=4;
	
	public VisibilityPage(ArrayList<VisibilityItem> products,int pageType){
		this.products=products;
		this.pageType=pageType;
	}
	
	public ArrayList<VisibilityItem> getProducts() {
		return products;
	}
	public void setProducts(ArrayList<VisibilityItem> products) {
		this.products = products;
	}
	public String getPageName() {
		return pageName;
	}
	public void setPageName(String pageName) {
		this.pageName = pageName;
	}
	public int getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
	public int getPageType() {
		return pageType;
	}
	public void setPageType(int pageType) {
		this.pageType = pageType;
	}
	
	
}
