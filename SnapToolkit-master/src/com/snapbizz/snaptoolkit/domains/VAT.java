package com.snapbizz.snaptoolkit.domains;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="vat")
public class VAT {

    @DatabaseField(columnName="vat_value")
    private float vat;
    @DatabaseField(columnName="subcategory_id", foreign = true, foreignColumnName = "product_category_id")
    private ProductCategory productCategory;
    @DatabaseField(columnName="state_id", foreign = true, foreignColumnName = "state_id")
    private State state;
	public float getVat() {
		return vat;
	}
	public void setVat(float vat) {
		this.vat = vat;
	}
	public ProductCategory getProductCategory() {
		return productCategory;
	}
	public void setProductCategory(ProductCategory productCategory) {
		this.productCategory = productCategory;
	}
	public State getState() {
		return state;
	}
	public void setState(State state) {
		this.state = state;
	}
    
    
}
