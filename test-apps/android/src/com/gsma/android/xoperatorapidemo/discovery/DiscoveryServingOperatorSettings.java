package com.gsma.android.xoperatorapidemo.discovery;


public class DiscoveryServingOperatorSettings {
	
	private static final ServingOperatorSetting etel=new ServingOperatorSetting("ETel Sandbox (000-01)", false, "000", "01", null);
	private static final ServingOperatorSetting dialog_lk=new ServingOperatorSetting("Dialog Sri Lanka (413-02)", false, "413", "02", null);
	private static final ServingOperatorSetting mobitel_lk=new ServingOperatorSetting("Mobitel Sri Lanka (413-01)", false, "413", "01", null);
	private static final ServingOperatorSetting telia_se=new ServingOperatorSetting("Telia Sweden (240-01)", false, "240", "01", null);
	private static final ServingOperatorSetting auto=new ServingOperatorSetting("Auto (device MCC/MNC)", true, null, null, "5.5.5.5");
	private static final ServingOperatorSetting none=new ServingOperatorSetting("No auto assist", false, null, null, "5.5.5.5");
	
	private static final ServingOperatorSetting[] operators={auto, etel, telia_se, dialog_lk, mobitel_lk, none};
	
	private static String[] operatorNames=null;
	
	static {
		operatorNames=new String[operators.length];
		int index=0;
		for (ServingOperatorSetting operator:operators) {
			operatorNames[index++]=operator.getName();
		}
	}
	
	public static String[] getOperatorNames() { return operatorNames; }
	
	public static ServingOperatorSetting getOperator(int index) { return operators[index]; }
}