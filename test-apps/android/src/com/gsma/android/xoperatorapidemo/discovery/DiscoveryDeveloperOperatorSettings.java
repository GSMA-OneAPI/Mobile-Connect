package com.gsma.android.xoperatorapidemo.discovery;


public class DiscoveryDeveloperOperatorSettings {

	private static final DeveloperOperatorSetting testDev=new DeveloperOperatorSetting("Integration Env", 
			"https://stage-exchange-test.apigee.net/gsma/v2/discovery", 
			"gZJ8mEnjoLiAgrfudHCEZvufOoafvf1S", 
			"oESO7jLriPaF3qKA", 
			"https://sb1.exchange.gsma.com/v1/logo");	

	private static String[] operatorNames=null;
	private static final DeveloperOperatorSetting[] operators={testDev};
	
	static {
		operatorNames=new String[operators.length];
		int index=0;
		for (DeveloperOperatorSetting operator:operators) {
			operatorNames[index++]=operator.getName();
		}
	}
	
	public static String[] getOperatorNames() { return operatorNames; }
	
	public static DeveloperOperatorSetting getOperator(int index) { return operators[index]; }
}