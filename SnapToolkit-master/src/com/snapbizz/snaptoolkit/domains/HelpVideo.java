package com.snapbizz.snaptoolkit.domains;

import com.google.gson.annotations.SerializedName;

public class HelpVideo {
    
    @SerializedName("id")
    private int id;
    @SerializedName("created_at")
    private String createdAtDate;
    @SerializedName("updated_at")    
    private String updatedAtDate;
    @SerializedName("file_file_name")
    private String fileName;
    @SerializedName("file_content_type")
    private String fileContentType;
    @SerializedName("file_file_size")
    private String fileSize;
    @SerializedName("file_updated_at")
    private String fileUpdatedAtDate;
    @SerializedName("video_type")
    private String videoType;
    @SerializedName("timestamp")
    private String timestamp;
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getCreatedAtDate() {
        return createdAtDate;
    }
    public void setCreatedAtDate(String createdAtDate) {
        this.createdAtDate = createdAtDate;
    }
    public String getUpdatedAtDate() {
        return updatedAtDate;
    }
    public void setUpdatedAtDate(String updatedAtDate) {
        this.updatedAtDate = updatedAtDate;
    }
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public String getFileContentType() {
        return fileContentType;
    }
    public void setFileContentType(String fileContentType) {
        this.fileContentType = fileContentType;
    }
    public String getFileSize() {
        return fileSize;
    }
    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }
    public String getFileUpdatedAtDate() {
        return fileUpdatedAtDate;
    }
    public void setFileUpdatedAtDate(String fileUpdatedAtDate) {
        this.fileUpdatedAtDate = fileUpdatedAtDate;
    }
    public String getType() {
        return videoType;
    }
    public void setType(String type) {
        this.videoType = type;
    }
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getVideoType() {
		return videoType;
	}
	public void setVideoType(String videoType) {
		this.videoType = videoType;
	}
    
}
