package com.piq.erstieNavi.model;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class MyItemizedOverlay extends ItemizedOverlay<MyOverlayItem> {
	
	private Context mContext;
	
	private ArrayList<MyOverlayItem> mOverlays = new ArrayList<MyOverlayItem>();
	
	public MyItemizedOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}
	
	public void addOverlay(MyOverlayItem overlay) {
		mOverlays.add(overlay);
		populate();
	}
	
	@Override
	protected MyOverlayItem createItem(int i) {
		return mOverlays.get(i);
	}
	
	@Override
	public int size() {
		return mOverlays.size();
	}
	
	public MyItemizedOverlay(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		mContext = context;
	}
	
	@Override
	protected boolean onTap(int index) {
		OverlayItem item = mOverlays.get(index);
		AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
		dialog.setTitle(item.getTitle());
		dialog.setMessage(item.getSnippet());
		dialog.show();
		return true;
	}
	
	public ArrayList<MyOverlayItem> getmOverlays() {
		return mOverlays;
	}
	
}
