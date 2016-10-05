package com.snapbizz.snapbilling.domainsV2;

import java.util.Date;

public class SyncGDBData {
	
	public class GDBProduct {
		public Long barcode = null;
		public Long product_gid = null;
		public String name = null;
		public int mrp = 0;
		public String alt_mrp = null;
		public String uom = null;
		public int measure = 0;
		public int brand_id = 0;
		public int category_id = 0;
		public Integer vat_id = null;
		public boolean is_disabled = false;
		public String image_url = null;
		public String local_name = null;
		public Date created_at = null;
		public Date updated_at = null;
		public boolean is_pc = true;
	}
		
	public class Brand {
		public long id = 0;
		public int parent_id = 0;
		public String name = null;
		public int company_id = 0;
		public String image_url = null;
		public Date created_at = null;
		public Date updated_at = null;
	}
	
	public class GDBCompanies {
		public long id = 0;
		public String name = null;
		public String image_url = null;
		public Date created_at = null;
		public Date updated_at = null;
	}
		
	public class GDBProductCategories {
		public long id = 0;
		public String name = null;
		public int parent_id = 0;
		public int vat_id = 0;
		public boolean is_quick_add = false;
		public Date created_at = null;
		public Date updated_at = null;		
	}
	
	public class GDBVatCategories {
		public long id = 0;
		public int state = 0;
		public float vat_rate = 0;
		public Date created_at = null;
		public Date updated_at = null;
	}
	
	public class SnapShot {
		public final Integer id = 0;
		public final Date timestamp = null;
		public final String file = null;
		public final String language = null;
	}

}
