package com.gsma.android.xoperatorapidemo.discovery;

public class ServingOperatorSetting {
	String name;
	boolean automatic;
	String mcc;
	String mnc;
	String ipaddress;
	
	public ServingOperatorSetting(String name, boolean automatic, String mcc, String mnc, String ipaddress) {
		this.name=name;
		this.automatic=automatic;
		this.mcc=mcc;
		this.mnc=mnc;
		this.ipaddress=ipaddress;
	}
	
	public String getName() { return this.name; }
	public boolean isAutomatic() { return this.automatic; }
	public String getMcc() { return this.mcc; }
	public String getMnc() { return this.mnc; }
	public String getIpaddress() { return this.ipaddress; }
	
}