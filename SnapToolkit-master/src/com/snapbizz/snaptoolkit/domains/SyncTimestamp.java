package com.snapbizz.snaptoolkit.domains;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName = "sync_timestamp")
public class SyncTimestamp {
	
	
	@SerializedName("name")
	@DatabaseField(columnName = "name")
	private String name;

	@SerializedName("lastmodified_timestamp")
	@DatabaseField(columnName = "lastmodified_timestamp")
	private String lastModifiedTimestamp;

	public SyncTimestamp() {
    }

    public void setLastModifiedTimestamp(String lastModifiedTimestamp) {
        this.lastModifiedTimestamp = lastModifiedTimestamp;
    }
    public String getLastModifiedTimestamp() {
        return lastModifiedTimestamp;
    }
    public void setName(String name) {
    	this.name = name;
    }
    public String getName() {
		return name;
    }
}
