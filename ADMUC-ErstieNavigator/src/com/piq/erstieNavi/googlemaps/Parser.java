package com.piq.erstieNavi.googlemaps;

import com.google.android.maps.GeoPoint;

public interface Parser {
    public Route parse(GeoPoint start, GeoPoint dest);
}
