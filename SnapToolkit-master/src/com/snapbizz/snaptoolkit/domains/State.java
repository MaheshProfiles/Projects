package com.snapbizz.snaptoolkit.domains;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="state")
public class State {
            
    @DatabaseField(columnName="state_id")
    @SerializedName("id")
    private int stateID;
    @DatabaseField(columnName="name")
    private String name;
	public int getStateID() {
		return stateID;
	}
	public void setStateID(int stateID) {
		this.stateID = stateID;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
    @Override
    public String toString() {
    	// TODO Auto-generated method stub
    	return name;
    }
    
}
