package com.yaron.NotificationBar;

public class AdapterExtraIntent {
	private String name;
	private String val;
	
	public AdapterExtraIntent(String name, String val) {
		this.name=name;
		this.val=val;
	}
	
	public String getName() {
		return(name);
	}
	
	public String getValue() {
		return(val);
	}
}
