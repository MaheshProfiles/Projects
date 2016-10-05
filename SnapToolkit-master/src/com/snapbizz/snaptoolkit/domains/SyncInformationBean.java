package com.snapbizz.snaptoolkit.domains;

public class SyncInformationBean {

	private String syncTitle;
	private String syncDate;
	private String syncRemaining;
	
	public SyncInformationBean(String syncTitle, String syncDate , String syncRemaining) {
		this.syncDate = syncDate;
		this.syncTitle = syncTitle;
		this.syncRemaining = syncRemaining;
	}
	
	public String getSyncTitle() {
		return syncTitle;
		
	}
	public String getSyncDate() {
		return syncDate;
	}
	public String getSyncRemaining() {
		return syncRemaining;
	}
}
