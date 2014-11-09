package com.gsma.android.xoperatorapidemo.discovery;

import java.util.ArrayList;


public enum DiscoveryStartupSettings {
	STARTUP_OPTION_MANUAL (0, "Manually Controlled Discovery"),
	STARTUP_OPTION_PASSIVE (1, "Discover If Possible"),
	STARTUP_OPTION_PREEMPTIVE (2, "Force Discovery");
	
	public static final int DEFAULT=STARTUP_OPTION_PASSIVE.getValue();
	
	private int value;
	private String label;
	
	static ArrayList<DiscoveryStartupSettings> presentationOrder=null;
	static String[] labels=null;
	
	DiscoveryStartupSettings(int value, String label) {
		this.value=value;
		this.label=label;
	}
	
	public int getValue() { 
		return this.value;
	}
	
	public String getLabel() {
		return this.label;
	}
	
	static {
		presentationOrder=new ArrayList<DiscoveryStartupSettings>();
		presentationOrder.add(STARTUP_OPTION_MANUAL);
		presentationOrder.add(STARTUP_OPTION_PASSIVE);
		presentationOrder.add(STARTUP_OPTION_PREEMPTIVE);
		labels=new String[presentationOrder.size()];
		for (int i=0; i<presentationOrder.size(); i++) {
			labels[i]=presentationOrder.get(i).getLabel();
		}
	}
	
	public static String[] getLabels() { return labels; }
	
	public static DiscoveryStartupSettings get(int index) { return presentationOrder.get(index); }
	
	public static DiscoveryStartupSettings getByValue(int value) {
		DiscoveryStartupSettings selected=STARTUP_OPTION_PASSIVE;
		if (value==STARTUP_OPTION_MANUAL.value) {
			selected=STARTUP_OPTION_MANUAL;
		} else if (value==STARTUP_OPTION_PASSIVE.value) {
			selected=STARTUP_OPTION_PASSIVE;
		} else if (value==STARTUP_OPTION_PREEMPTIVE.value) {
			selected=STARTUP_OPTION_PREEMPTIVE;
		}
		return selected;
	}

	public static int getIndexByValue(int startupOptionValue) {
		int index=0;
		boolean found=false;
		for (int i=0; i<presentationOrder.size() && !found; i++) {
			found=(startupOptionValue==presentationOrder.get(i).value);
			if (found) index=i;
		}
		return index;
	}

}