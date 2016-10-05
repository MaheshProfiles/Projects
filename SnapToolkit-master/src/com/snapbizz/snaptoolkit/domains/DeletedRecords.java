package com.snapbizz.snaptoolkit.domains;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.snapbizz.snaptoolkit.utils.TableType;

@DatabaseTable(tableName="deleted_records")
public class DeletedRecords {
	@SerializedName("tabletDbId")
    @DatabaseField(columnName="deleted_record_id", generatedId=true)
    private int deletedRecordId;
    @DatabaseField(columnName="record_id")
    private String recordId;
    @DatabaseField(columnName="table_name")
    private TableType tableType;
    
    public int getDeletedRecordId() {
        return deletedRecordId;
    }
    public void setDeletedRecordId(int deletedRecordId) {
        this.deletedRecordId = deletedRecordId;
    }
    public String getRecordId() {
        return recordId;
    }
    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }
    public TableType getTableType() {
        return tableType;
    }
    public void setTableType(TableType tableType) {
        this.tableType = tableType;
    }

}
