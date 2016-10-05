package com.snapbizz.snapbilling.domainsV2;

import com.google.gson.annotations.SerializedName;

public class ApkData {
	
	public static class ApkURL {
		@SerializedName("status")
		public String status;
		@SerializedName("version_name")
		public String version_name;
		@SerializedName("apk_url")
		public String apk_url;
		@SerializedName("apk_size")
		public Long apk_size;
	}
	
	public static class AppVersionCode {
		@SerializedName("package_name")
		public String package_name;
		@SerializedName("current_version_code")
		public int current_version_code;
		@SerializedName("proposed_version_code")
		public int proposed_version_code;
		@SerializedName("upgrade_before")
		public String upgrade_before = null;	
	}
	
	public static class CurrentAppVersions {
		@SerializedName("package_name")
		String package_name = null;
		@SerializedName("current_version_code")
		int current_version_code = 0;
		
		public CurrentAppVersions(String package_name, int current_version_code) {
			this.package_name = package_name;
			this.current_version_code = current_version_code;
		}
	}
}
