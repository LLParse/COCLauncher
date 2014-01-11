package com.zarniwoop.coc.gameloader;

public class Credential {

	private long id;
	private String name;
	private String low;
	private String high;
	private String pass;
	private String thLevel;
	private String locale;
	private Long notifyTime;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLow() {
		return low;
	}
	public void setLow(String low) {
		this.low = low;
	}
	public String getHigh() {
		return high;
	}
	public void setHigh(String high) {
		this.high = high;
	}
	public String getPass() {
		return pass;
	}
	public void setPass(String pass) {
		this.pass = pass;
	}
	public String getThLevel() {
		return thLevel;
	}
	public void setThLevel(String thLevel) {
		this.thLevel = thLevel;
	}
	public String getLocale() {
		return locale;
	}
	public void setLocale(String locale) {
		this.locale = locale;
	}
	public Long getNotifyTime() {
		return notifyTime;
	}
	public void setNotifyTime(Long notifyTime) {
		this.notifyTime = notifyTime;
	}

}
