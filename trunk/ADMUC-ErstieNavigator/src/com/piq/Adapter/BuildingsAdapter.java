package com.piq.Adapter;

import java.util.ArrayList;
import java.util.List;

import com.piq.erstieNavi.model.Building;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class BuildingsAdapter implements SpinnerAdapter
{
	private List<Object> items = new ArrayList<Object>();
	private static String ucl = "Use Current Location";
	
	public BuildingsAdapter (List<Building> items, boolean bool)
	{
		if(bool)
		{
			this.items.add(ucl);
			transform(items);
			
		}
		else transform(items);
		
	}
	
	public int getCount() 
	{
		return items.size();
	}

	public Object getItem(int position) 
	{
		Object o = null;;
		if(items.get(position) instanceof Building) o = ((Building)items.get(position)).getAbbrev();
        else if(items.get(position) instanceof String) o=(String)items.get(position);
		return o;
	}

	public long getItemId(int position) 
	{
		return position;
	}

	public int getItemViewType(int position) {
		
		return android.R.layout.simple_spinner_dropdown_item;
	}

	public View getView(int position, View convertView, ViewGroup parent) 
	{

		TextView v = new TextView(parent.getContext());
        v.setTextColor(Color.BLACK);
        if(items.get(position) instanceof Building)v.setText(((Building)items.get(position)).getAbbrev());
        else if(items.get(position) instanceof String) v.setText((String)items.get(position));
        return v;
	}

	public int getViewTypeCount() {
		
		return 1;
	}

	public boolean hasStableIds() {
		
		return false;
	}

	public boolean isEmpty() {
		
		return false;
	}

	public void registerDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub
		
	}

	public void unregisterDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub
		
	}

	public View getDropDownView(int position, View convertView, ViewGroup parent) 
	{
		return this.getView(position, convertView, parent);
	}
	
	private void transform(List<Building> items)
	{
		for(Building b: items)
		{
			this.items.add(b);
		}
	}

}
