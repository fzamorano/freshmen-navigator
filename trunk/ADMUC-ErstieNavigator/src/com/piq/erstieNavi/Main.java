package com.piq.erstieNavi;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.piq.Adapter.BuildingsAdapter;
import com.piq.erstieNavi.services.AppStatus;
import com.piq.erstieNavi.services.BuildingsManager;

public class Main extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		File appLocation = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + java.io.File.separator + "Erstie-Navigator");
		appLocation.mkdirs();
		
		BuildingsManager bm = BuildingsManager.getInstance();
		
		Spinner spinFrom = (Spinner) findViewById(R.id.spinFrom);
		Spinner spinTo = (Spinner) findViewById(R.id.spinTo);
		
		spinFrom.setAdapter(new BuildingsAdapter(bm.getBuildingsList(), true));
		spinTo.setAdapter(new BuildingsAdapter(bm.getBuildingsList(), false));
		
		Button start = (Button) findViewById(R.id.startButton);
		start.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Spinner spinFrom = (Spinner) findViewById(R.id.spinFrom);
				Spinner spinTo = (Spinner) findViewById(R.id.spinTo);
				RadioGroup kind = (RadioGroup) findViewById(R.id.radioG);
				
				switch (kind.getCheckedRadioButtonId()) {
					case R.id.radioInet: {
						if (AppStatus.getInstance(getApplicationContext()).haveGPSConnection()) {
							if (AppStatus.getInstance(getApplicationContext()).haveNetworkConnection()) {
								Intent i = new Intent(v.getContext(), NaviWithInternetActivity.class);
								i.putExtra("from", spinFrom.getSelectedItem().toString());
								i.putExtra("to", spinTo.getSelectedItem().toString());
								startActivity(i);
							} else {
								toastL("You are probably not connected to the internet. Neither over WLAN nor Mobile.\n\nMaybe activate internet, or use the other method. Thanks!");
							}
						} else {
							showGPSDisabledAlertToUser();
							toastL("Please try again, after enabling GPS on your device.");
						}
						break;
					}
					case R.id.radioCompass: {
						if (AppStatus.getInstance(getApplicationContext()).haveGPSConnection()) {
							Intent i = new Intent(v.getContext(), CompassActivity.class);
							i.putExtra("from", spinFrom.getSelectedItem().toString());
							i.putExtra("to", spinTo.getSelectedItem().toString());
							startActivity(i);
						} else {
							showGPSDisabledAlertToUser();
							toastL("Please try again, after enabling GPS in your device.");
						}
						break;
					}
				}
				
			}
			
		});
		
		Button add = (Button) findViewById(R.id.addNaviPointButton);
		add.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(v.getContext(), NaviPointsActivity.class);
				startActivity(i);
			}
		});
		
	}
	
	private void showGPSDisabledAlertToUser() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?").setCancelable(false).setPositiveButton("Goto Settings Page To Enable GPS", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(callGPSSettingIntent);
			}
		});
		alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alert = alertDialogBuilder.create();
		alert.show();
	}
	
	public void toast(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	} // end toast
	
	public void toastL(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
	} // end toast
	
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu_general, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle all of the possible menu actions.
		switch (item.getItemId()) {
			case R.id.menu_backtohome:
				startActivity(new Intent(this, Main.class));
				return true;
			case R.id.menu_exit:
				finish();
				return true;
			case R.id.menu_settings:
				startActivity(new Intent(this, SettingsActivity.class));
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
}
