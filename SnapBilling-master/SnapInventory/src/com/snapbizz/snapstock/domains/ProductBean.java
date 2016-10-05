package com.snapbizz.snapstock.domains;

public class ProductBean {

    private String productSkuID;
    private String productName;
    private String imageUri;
    private int productToOrder;
    private int productPendingOrder;
    private int productBrandID;
    private int inventorySerialNumber;
    private int productToReceiveQty;
    private int productReceivedQty;
    private int productTotalReceivedQty;
    private int productBilledQty;
    private int productCategoryID;
    private int productCompanyID;
    private float productQty;
    private float productPrice;
    private float productPurchasePrice;
    private float productDiscount;
    private float productNetAmount;
    private float productTotalAmount;
    private float productSalePrice;
    private float VATRate;
    private float VATAmount;
    private boolean isOffer;
    private boolean isSelected;
    private boolean isMRPChanged;
    private boolean isPaid;
    private boolean isGDB;

    public ProductBean(){
        
    }

    public ProductBean(String productName, float productPrice,
            float productQty, String productCategoryName, int productToOrder,
            int productPendingOrder, int productBrandID,
            String productSkuID, Boolean isOffer, String imageUri, boolean isGDB) {
        this.productName = productName;
        this.productPrice = productPrice;
        this.productQty = productQty;
        this.productToOrder = productToOrder;
        this.productPendingOrder = productPendingOrder;
        this.productBrandID = productBrandID;
        this.productSkuID = productSkuID;
        this.isOffer = isOffer;
        this.imageUri = imageUri;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public float getProductQty() {
        return productQty;
    }

    public void setProductQty(float productQty) {
        this.productQty = productQty;
    }

    public float getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(float productPrice) {
        this.productPrice = productPrice;
    }

    public int getProductToOrder() {
        return productToOrder;
    }

    public void setProductToOrder(int productToOrder) {
        this.productToOrder = productToOrder;
    }

    public int getProductPendingOrder() {
        return productPendingOrder;
    }

    public void setProductPendingOrder(int productPendingOrder) {
        this.productPendingOrder = productPendingOrder;
    }

    public int getProductBrandID() {
        return productBrandID;
    }

    public void setProductBrandID(int productBrandID) {
        this.productBrandID = productBrandID;
    }

    public String getProductSkuID() {
        return productSkuID;
    }

    public void setProductSkuID(String productSkuID) {
        this.productSkuID = productSkuID;
    }

    public boolean isOffer() {
        return isOffer;
    }

    public void setOffer(boolean isOffer) {
        this.isOffer = isOffer;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public float getProductPurchasePrice() {
        return productPurchasePrice;
    }

    public void setProductPurchasePrice(float productPurchasePrice) {
        this.productPurchasePrice = productPurchasePrice;
    }

    public float getProductDiscount() {
        return productDiscount;
    }

    public void setProductDiscount(float productDiscount) {
        this.productDiscount = productDiscount;
    }

    public int getInventorySerialNumber() {
        return inventorySerialNumber;
    }

    public void setInventorySerialNumber(int inventorySerialNumber) {
        this.inventorySerialNumber = inventorySerialNumber;
    }

    public float getProductNetAmount() {
        return productNetAmount;
    }

    public void setProductNetAmount(float productNetAmount) {
        this.productNetAmount = productNetAmount;
    }

    public float getProductTotalAmount() {
        return productTotalAmount;
    }

    public void setProductTotalAmount(float productTotalAmount) {
        this.productTotalAmount = productTotalAmount;
    }

    public float getProductSalePrice() {
        return productSalePrice;
    }

    public void setProductSalePrice(float productSalePrice) {
        this.productSalePrice = productSalePrice;
    }

    public int getProductBilledQty() {
        return productBilledQty;
    }

    public void setProductBilledQty(int productBilledQty) {
        this.productBilledQty = productBilledQty;
    }

    public int getProductCategoryID() {
        return productCategoryID;
    }

    public void setProductCategoryID(int productCategoryID) {
        this.productCategoryID = productCategoryID;
    }

    public float getVATRate() {
        return VATRate;
    }

    public void setVATRate(float VATRate) {
        this.VATRate = VATRate;
    }

    public float getVATAmount() {
        return VATAmount;
    }

    public void setVATAmount(float VATAmount) {
        this.VATAmount = VATAmount;
    }

    public boolean isMRPChanged() {
        return isMRPChanged;
    }

    public void setMRPChanged(boolean isMRPChanged) {
        this.isMRPChanged = isMRPChanged;
    }
    
    public int getProductToReceiveQty() {
        return productToReceiveQty;
    }

    public void setProductToReceiveQty(int productToReceiveQty) {
        this.productToReceiveQty = productToReceiveQty;
    }

    public int getProductReceivedQty() {
        return productReceivedQty;
    }

    public void setProductReceivedQty(int productReceivedQty) {
        this.productReceivedQty = productReceivedQty;
    }

    public int getProductTotalReceivedQty() {
        return productTotalReceivedQty;
    }

    public void setProductTotalReceivedQty(int productTotalReceivedQty) {
        this.productTotalReceivedQty = productTotalReceivedQty;
    }
    
    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean isPaid) {
        this.isPaid = isPaid;
    }

    public boolean isGDB() {
        return isGDB;
    }

    public void setGDB(boolean isGDB) {
        this.isGDB = isGDB;
    }

    public int getProductCompanyID() {
        return productCompanyID;
    }

    public void setProductCompanyID(int productCompanyID) {
        this.productCompanyID = productCompanyID;
    }

    public void incPendingQty(){
        productPendingOrder++;
    }
    
    public void incProductReceivedQty(){
        productReceivedQty++;
    }
    
    public void incProductToOrder(){
        productToOrder++;
    }
    
    public void incProductBilledQty(){
        productBilledQty++;
    }
}