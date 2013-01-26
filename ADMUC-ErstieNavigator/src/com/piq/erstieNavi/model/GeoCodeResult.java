package com.piq.erstieNavi.model;

public class GeoCodeResult {
	
	public String line1;
	public String line2;
	public String line3;
	public String line4;
	
	@Override
	public String toString() {
		
		StringBuilder builder = new StringBuilder();
		
		if (line1 != null)
			builder.append("\n" + line1 + "\n");
		if (line2 != null)
			builder.append(line2 + "\n");
		if (line3 != null)
			builder.append(line3 + "\n");
		if (line4 != null)
			builder.append(line4);
		
		return builder.toString();
		
	}
	
}
