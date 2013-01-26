package com.piq.erstieNavi.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.os.Environment;

import com.piq.erstieNavi.model.Building;
import com.piq.erstieNavi.services.BuildingsManager;

public class XMLBuilder {
	
	public void saveItems(ArrayList<Building> items) {
		DocumentBuilderFactory docBFac;
		DocumentBuilder docBuild;
		Element item;
		Document doc = null;
		try {
			docBFac = DocumentBuilderFactory.newInstance();
			docBuild = docBFac.newDocumentBuilder();
			doc = docBuild.newDocument();
		} catch (Exception e) {
			System.out.println("Dokument konnte nicht erzeugt werden");
		}
		if (doc != null) {
			Element root = doc.createElement("buildings");
			for (Building i : items) {
				item = doc.createElement("building");
				
				Element name = doc.createElement("name");
				name.appendChild(doc.createTextNode("" + i.getName()));
				item.appendChild(name);
				
				Element abbrev = doc.createElement("abbrev");
				abbrev.appendChild(doc.createTextNode(i.getAbbrev()));
				item.appendChild(abbrev);
				
				Element lat = doc.createElement("latitude");
				lat.appendChild(doc.createTextNode("" + i.getLatitude()));
				item.appendChild(lat);
				
				Element lng = doc.createElement("longitude");
				lng.appendChild(doc.createTextNode("" + i.getLongitude()));
				item.appendChild(lng);
				
				root.appendChild(item);
			}
			doc.appendChild(root);
		}
		
		writeXmlFile(doc, getPath() + java.io.File.separator + "buildings.xml");
	}
	
	// This method writes a DOM document to a file
	public void writeXmlFile(Document doc, String filename) {
		try {
			// Prepare the DOM document for writing
			Source source = new DOMSource(doc);
			
			// Prepare the output file
			File file = new File(filename);
			Result result = new StreamResult(file);
			
			// Write the DOM document to the file
			Transformer xformer = TransformerFactory.newInstance().newTransformer();
			xformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<Building> readXmlFile() {
		try {
			
			File fXmlFile = new File(getPath() + java.io.File.separator + "buildings.xml");
			if (!fXmlFile.exists()) {
				BuildingsManager.getInstance().initAllBuildings();
				System.out.println("Maybe no external Stoage - could not read from builingsfile ... creating default");
				return new ArrayList<Building>();
			}
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			
			NodeList nList = doc.getElementsByTagName("building");
			
			ArrayList<Building> buildingsList = new ArrayList<Building>();
			
			for (int temp = 0; temp < nList.getLength(); temp++) {
				
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					
					Element eElement = (Element) nNode;
					
					String name = getTagValue("name", eElement);
					String abbrev = getTagValue("abbrev", eElement);
					String latitude = getTagValue("latitude", eElement);
					String longitude = getTagValue("longitude", eElement);
					
					buildingsList.add(new Building(name, abbrev, Double.parseDouble(latitude), Double.parseDouble(longitude)));
				}
			}
			
			return buildingsList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private String getTagValue(String sTag, Element eElement) {
		NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
		
		Node nValue = (Node) nlList.item(0);
		
		return nValue.getNodeValue();
	}
	
	private String getPath() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			try {
				new File(Environment.getExternalStorageDirectory().getAbsolutePath() + java.io.File.separator + "Erstie-Navigator" + java.io.File.separator + "buildings.xml").createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return Environment.getExternalStorageDirectory().getAbsolutePath() + java.io.File.separator + "Erstie-Navigator";
		}
		return "";
	}
	
}
