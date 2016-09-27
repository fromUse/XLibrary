package com.library.bean;

public class Version {

	private Integer versionID;
	private String downloadURL;
	private String describe;
	public String getDescribe() {
		return describe;
	}
	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public String getDownloadURL() {
		return downloadURL;
	}
	public void setDownloadURL(String downloadURL) {
		this.downloadURL = downloadURL;
	}

	public Integer getVersionID() {
		return versionID;
	}

	public void setVersionID(Integer versionID) {
		this.versionID = versionID;
	}
}
