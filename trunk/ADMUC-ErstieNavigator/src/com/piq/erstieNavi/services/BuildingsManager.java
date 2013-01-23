package com.piq.erstieNavi.services;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import android.location.Location;
import android.os.Environment;

import com.piq.erstieNavi.model.Building;
import com.piq.erstieNavi.util.XMLBuilder;

public class BuildingsManager {

	private static BuildingsManager instance;
	private ArrayList<Building> buildingsList = new ArrayList<Building>();

	private BuildingsManager() {
		if (new File(getPath() + java.io.File.separator + "buildings.xml").exists()) {
			System.out.println("readIn buildings from File");
			buildingsList = new XMLBuilder().readXmlFile();
			if (buildingsList == null) {
				System.out.println("Something went wrong with readIn the Buildings. Recreating original file");
				initAllBuildings();
				new XMLBuilder().saveItems(buildingsList);
			}
		} else {
			System.out.println("creating Builingsfile");
			initAllBuildings();
			new XMLBuilder().saveItems(buildingsList);
		}
	}

	public static BuildingsManager getInstance() {
		if (instance == null) {
			instance = new BuildingsManager();
		}
		return instance;
	}

	private String getPath() {
		return Environment.getExternalStorageDirectory().getAbsolutePath() + java.io.File.separator + "Erstie-Navigator";
	}

	// http://itouchmap.com/latlong.html
	// TODO maybe write extraprogramm to create xml-file
	//String name, String abbrev, double latitude, double longitude
	public void initAllBuildings() {
		buildingsList.add(new Building("Hörsaalzentrum", "HSZ", 51.028906, 13.730013));
		buildingsList.add(new Building("Neue Mensa", "Neue Mensa", 51.028947, 13.731827));
		buildingsList.add(new Building("Informatik-Fakultät", "INF", 51.02587, 13.722643));
		buildingsList.add(new Building("Willersbau", "WIL", 51.029035, 13.733747));
		
		buildingsList.add(new Building("Beyer-Bau", "BEY", 51.029722, 13.729444));
		buildingsList.add(new Building("Fritz-Foerster-Bau", "FOE", 51.02748, 13.728632));
		buildingsList.add(new Building("Alte Mensa", "Alte Mensa", 51.027015, 13.72653));
		buildingsList.add(new Building("Andreas-Schubert-Bau", "ASB", 51.028895, 13.741087));
		buildingsList.add(new Building("Barkhausen-Bau", "BAR", 51.027084, 13.724613));
		buildingsList.add(new Building("von Gerber-Bau", "GER", 51.028147, 13.731440));
		buildingsList.add(new Building("Georg Schumann-Bau", "SCH", 51.029538, 13.722460));
		buildingsList.add(new Building("Binder-Bau", "BIN", 51.027281, 13.726329));
		buildingsList.add(new Building("Görges-Bau", "GÖR", 51.027873, 13.725965));
		buildingsList.add(new Building("Merkel-Bau", "MER", 51.028155, 13.724847));
		
		Collections.sort(buildingsList);

	}

	public Building getRequestedBuilding(String abbrev) {
		for (Building b : buildingsList) {
			if (b.getAbbrev().toLowerCase().equals(abbrev.toLowerCase())) {
				return b;
			}
		}
		return new Building("not found", "nf", 0, 0);
	}

	public Building getRequestedBuilding(Location loc) {
		for (Building b : buildingsList) {
			if (b.getLatitude() == loc.getLatitude() && b.getLongitude() == loc.getLongitude()) {
				return b;
			}
		}
		return new Building("not found", "nf", 0, 0);
	}

	public ArrayList<Building> getBuildingsList() {
		return buildingsList;
	}

	public void setBuildingsList(ArrayList<Building> buildingsList) {
		this.buildingsList = buildingsList;
	}

	public void addBuilding(Building building) {
		buildingsList.add(building);
		Collections.sort(buildingsList);
		new XMLBuilder().saveItems(buildingsList);
	}

	public String[] getAbbrevs() {
		String[] s = new String[buildingsList.size()];
		for (int i = 0; i < buildingsList.size(); i++) {
			s[i] = buildingsList.get(i).getAbbrev();
		}
		return s;
	}
	
}
