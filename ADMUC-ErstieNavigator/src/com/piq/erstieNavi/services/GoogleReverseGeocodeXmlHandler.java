package com.piq.erstieNavi.services;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class GoogleReverseGeocodeXmlHandler extends DefaultHandler {
	private boolean inAddress = false;
	private boolean finished = false;
	private StringBuilder builder;
	private String address;
	
	public String getAddress() {
		return this.address;
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);
		if (this.inAddress && !this.finished) {
			if ((ch[start] != '\n') && (ch[start] != ' ')) {
				builder.append(ch, start, length);
			}
		}
	}
	
	@Override
	public void endElement(String uri, String address, String name) throws SAXException {
		super.endElement(uri, address, name);
		
		if (!this.finished) {
			if (address.equalsIgnoreCase("address")) {
				this.address = builder.toString();
				this.finished = true;
			}
			
			if (builder != null) {
				builder.setLength(0);
			}
		}
	}
	
	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		builder = new StringBuilder();
	}
	
	@Override
	public void startElement(String uri, String address, String name, Attributes attributes) throws SAXException {
		super.startElement(uri, address, name, attributes);
		
		if (address.equalsIgnoreCase("address")) {
			this.inAddress = true;
		}
	}
}
