package com.snapbizz.v2.sync;

import java.util.List;

public class LocalSyncData {

	public static class ApiInventory {
		public int minimum_base_quantity;
		public int reorder_point;
		public int quantity;
		public long product_code;
		public boolean is_deleted;
		public String created_at;
		public String updated_at;
		public ApiInventory(int minimumBaseQuantity, int reorderPoint, int quantity,
				long productCode, boolean isDeleted, String createdAt, String updatedAt) {
			minimum_base_quantity = minimumBaseQuantity;
			reorder_point = reorderPoint;
			this.quantity = quantity;
			product_code = productCode;
			is_deleted = isDeleted;
			created_at = createdAt;
			updated_at = updatedAt;
		}
	}
	
	public static class ApiCustomer {
		public String address;
		public String name;
		public long phone;
		public String email;
		public int credit_limit;
		public boolean is_disabled;
		public String created_at;
		public String updated_at;
		public ApiCustomer(String address, String name, long phone, String email,
				int creditLimit, boolean isDisabled, String createdAt, String updatedAt) {
			this.address = address;
			this.name = name;
			this.phone = phone;
			this.email = email;
			credit_limit = creditLimit;
			is_disabled = isDisabled;
			created_at = createdAt;
			updated_at = updatedAt;
		}
	}
	
	public static class ApiCustomerMonthlySummary {
		public long phone;
		public int amount_due;
		public int purchase_value;
		public int amount_paid;
		public int month;
		public String created_at;
		public String updated_at;
		public ApiCustomerMonthlySummary(long phone, int amountDue, int purchaseValue, 
				int amountPaid, int month, String createdAt, String updatedAt) {
			this.phone = phone;
			amount_due = amountDue;
			purchase_value = purchaseValue;
			amount_paid = amountPaid;
			this.month = month;
			created_at = createdAt;
			updated_at = updatedAt;
		}
	}

	public static class ApiCustomerDetails {
		public long phone;
		public int amount_due;
		public int amount_saved;
		public int purchase_value;
		public String last_purchase_date;
		public String last_payment_date;
		public int last_purchase_amount;
		public int last_payment_amount;
		public float avg_visits;
		public int avg_purchase;
		public String created_at;
		public String updated_at;
		public ApiCustomerDetails(long phone, int amountDue, int purchaseValue,int amount_saved, 
				String lastPurchaseDate, String lastPaymentDate, int lastPurchaseAmount,
				int lastPaymentAmount, float avgVisits, int avgPurchase, String createdAt,
				String updatedAt) {
			this.phone = phone;
			amount_due = amountDue;
			purchase_value = purchaseValue;
			last_purchase_date = lastPurchaseDate;
			last_payment_date = lastPaymentDate;
			last_purchase_amount = lastPurchaseAmount;
			last_payment_amount = lastPaymentAmount;
			avg_visits = avgVisits;
			avg_purchase = avgPurchase;
			created_at = createdAt;
			updated_at = updatedAt;
		}
	}
	
	public static class ApiDistributor {
		long phone;
		String name;
		String sales_person_name;
		long tin;
		String address;
		String city;
		String email;
		boolean is_disabled;
		String created_at;
		String updated_at;
	}
	
	public static class ApiDistributorsPayment {
		int id;
		long invoice_id;
		int amount;
		String mode;
		String reference;
		boolean is_diasbled;
		String created_at;
		String updated_at;
	}
	
	
	
	public static class ApiTransaction {
		public long id;
		public Long customer_phone;
		public Long invoice_id;
		public String payment_type;
		public String payment_mode;
		public int paid_amount;
		public int remaining_amount;
		public Long parent_transaction_id;
		public String payment_reference;
		public String created_at;
		public String updated_at;
		public ApiTransaction(long id, Long customerPhone, Long invoiceId, String paymentType,
				String paymentMode, int paidAmount, int remainingAmount, Long parentTransactionId,
				String paymentReference, String createdAt, String updatedAt) {
			this.id = id;
			customer_phone = customerPhone;
			invoice_id = invoiceId;
			payment_type = paymentType;
			payment_mode = paymentMode;
			paid_amount = paidAmount;
			remaining_amount = remainingAmount;
			parent_transaction_id = parentTransactionId;
			payment_reference = paymentReference;
			created_at = createdAt;
			updated_at = updatedAt;
		}
	}
	
	public static class ApiProduct {
		public long product_code;
		public String name;
		public int mrp;
		public String uom;
		public int measure;
		public String image;
		public float vat_rate;
		public boolean is_gdb;
		public boolean is_pc;
		public String created_at;
		public String updated_at;
		public ApiProduct(long productCode, String name, int mrp, String uom, int measure,
				String image, float vatRate, boolean isGdb, String createdAt, String updatedAt) {
			product_code = productCode;
			this.name = name;
			this.mrp = mrp;
			this.uom = uom;
			this.measure = measure;
			this.image = image;
			vat_rate = vatRate;
			is_gdb = isGdb;
			created_at = createdAt;
			updated_at = updatedAt;
		}
	}
	
	public static class ApiOtpGeneration {
		public long store_id;
		public String device_id;
	}
	
	public static class ApiSettingsInfo {
		
	}
	
	public static class ApiTabletSettings extends ApiOtpGeneration {
		ApiSettingsInfo settings_info;
		String created_at;
		String updated_at;
	}

	public static class ApiProductPacks {
		public long product_code;
		public int pack_size;
		public int sale_price1;
		public int sale_price2;
		public int sale_price3;
		public boolean is_default;
		public String created_at;
		public String updated_at;
		public ApiProductPacks(long productCode, int packSize, int salePrice1, int salePrice2,
				int salePrice3, boolean isDefault, String createdAt, String updatedAt) {
			product_code = productCode;
			pack_size = packSize;
			sale_price1 = salePrice1;
			sale_price2 = salePrice2;
			sale_price3 = salePrice3;
			is_default = isDefault;
			created_at = createdAt;
			updated_at = updatedAt;
		}
	}

	public static class ApiInvoice {
		public long invoice_id;
		public long customer_phone;
		public long parent_memo_id;
		public String pos_name;
		public boolean is_memo;
		public boolean is_deleted;
		public boolean is_delivery;
		public boolean is_credit;
		public String biller_name;
		public int total_items;
		public int total_quantity;
		public int total_savings;
		public int total_discount;
		public int total_vat_amount;
		public int total_amount;
		public int pending_amount;
		public String bill_started_at;
		public String created_at;
		public String updated_at;
		public static class ApiInvoiceItem {
			public long product_code;
			public String name;
			public int quantity;
			public String uom;
			public int measure;
			public int pack_size;
			public int mrp;
			public int sale_price;
			public int total_amount;
			public int savings;
			public float vat_rate; 
			public int vat_amount;
			public ApiInvoiceItem(long productCode, String name, int quantity, String uom,
					int measure, int packSize, int mrp, int salePrice, int totalAmount,
					int savings, float vatRate, int vatAmount) {
				product_code = productCode;
				this.name = name;
				this.quantity = quantity;
				this.uom = uom;
				this.measure = measure;
				pack_size = packSize;
				this.mrp = mrp;
				this.sale_price = salePrice;
				total_amount = totalAmount;
				this.savings = savings;
				vat_rate = vatRate; 
				vat_amount = vatAmount;
			}
		}
		public List<ApiInvoiceItem> items;
		public ApiInvoice(long invoiceId, long customerPhone, long parentMemoId, String posName, 
				boolean isMemo, boolean isDeleted, boolean isDelivery, boolean isCredit,
				String billerName, int totalItems, int totalQuantity, int totalSavings,
		        int totalDiscount, int totalVatAmount, int totalAmount, int pendingAmount,
		        String billStartedAt, String createdAt, String updatedAt, List<ApiInvoiceItem> items) {
			this.invoice_id = invoiceId;
			customer_phone = customerPhone;
			parent_memo_id = parentMemoId;
			pos_name = posName;
			is_memo = isMemo;
			is_deleted = isDeleted;
			is_delivery = isDelivery;
			is_credit = isCredit;
			biller_name = billerName;
			total_items = totalItems;
			total_quantity = totalQuantity;
			total_savings = totalSavings;
			total_discount = totalDiscount;
			total_vat_amount = totalVatAmount;
			total_amount = totalAmount;
			pending_amount = pendingAmount;
			bill_started_at = billStartedAt;
			created_at = createdAt;
			updated_at = updatedAt;
			this.items = items;
		}
	}
	
//	public static class ApiDeviceRegistrationInput extends ApiOtpGeneration {
//		int otp;
//	}
	
	
	public static class DefaultAPIResponse {
		public String status;
	}
	
	public static class GetInventoryAPIResponse {
		public ApiInventory [] inventoriesList;
		public String offset;
	}
	
	public static class GetCustomerAPIResponse {
		public ApiCustomer [] customersList;
		public String offset;
	}
	
	public static class GetCustomerMonthlySummaryResonse {
		public ApiCustomerMonthlySummary [] customer_monthly_summaryList;
		public String offset;
	}
	
	public static class GetCustomerDetailsAPIResponse {
		public ApiCustomerDetails [] customer_detailsList;
		public String offset;
	}
	
	public static class GetInviceListAPIResponse {
		public ApiInvoice [] invoicesList;
		public String offset;
	}
	
	public static class GetProductListAPIResponse {
		public ApiProduct [] productsList;
		public String offset;
	}
	
	public static class GetProductPackListAPIResponse {
		public ApiProductPacks [] product_packsList;
		public String offset;
	}
	
	public static class GetTransactionsListAPIResponse {
		public ApiTransaction [] invoice_transactionsList;
		public String offset;
	}
		
	public static class ApiDeviceRegistrationResponse {
		public ApiEditStore store_details;
		public String access_token;
		public RetailerDetails retailer_details;
		
		public static class ApiEditStore {
			public long store_id;
			public boolean is_disabled;
			public long salesperson_number;
			public String city;
			public String state;
			public long tin;
			public int pincode;
			public int store_type;
			public long phone;
			public String name;
			public String address;
			public long retailer_id;			
		}
		
		public static class RetailerDetails {
			public long retailer_id;
			public String name;
			public String address;
			public String email;
		}
	}
	
	public static class GenerateStoreOTPAPIResponse {
		public String status;
	}
	
	public static class ApiOtpGenerationInput {
		public String device_id;
		public long store_phone;
	}
	
	private static class OTPResponse {
		public int responseCode = 0;
		public String reponseMessage = null;
	}
	
	public static class ApiDeviceRegistrationInput extends ApiOtpGenerationInput {
		public int otp;
	}
	
}
