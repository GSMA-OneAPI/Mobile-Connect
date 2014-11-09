package com.gsma.android.xoperatorapidemo.discovery;


public class DiscoveryDeveloperOperatorSettings {

	private static final DeveloperOperatorSetting testDev=new DeveloperOperatorSetting("Integration Env", 
			"https://integration-sb1.apiexchange.org/v1/discovery", "sb1-dev3-app3-prod-app-prod-key", "sb1-dev3-app3-prod-app-prod-secret", "https://integration-sb1.apiexchange.org/v1/logo");	

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