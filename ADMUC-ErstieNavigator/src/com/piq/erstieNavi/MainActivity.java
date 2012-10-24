package com.piq.erstieNavi;

import java.io.File;

import com.piq.erstieNavi.services.AppStatus;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private AutoCompleteTextView editFrom;
	private AutoCompleteTextView editTo;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		File appLocation = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + java.io.File.separator + "Erstie-Navigator");
		appLocation.mkdirs();
		
		ScrollView sv = new ScrollView(this);
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL);
		sv.addView(ll);
		sv.setLayoutParams(new ScrollView.LayoutParams(ScrollView.LayoutParams.FILL_PARENT, ScrollView.LayoutParams.FILL_PARENT));

		ScrollView.LayoutParams params = new ScrollView.LayoutParams(ScrollView.LayoutParams.FILL_PARENT, ScrollView.LayoutParams.FILL_PARENT);
		LinearLayout.LayoutParams paramsll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
		ll.setLayoutParams(params);

		{
			LinearLayout row = new LinearLayout(this);
			row.setLayoutParams(paramsll);
			row.setOrientation(LinearLayout.HORIZONTAL);

			TextView tvFrom = new TextView(this);
			tvFrom.setText("FROM");
			row.addView(tvFrom);

			editFrom = new AutoCompleteTextView(this);
			String[] buildings = getResources().getStringArray(R.array.buildings);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, buildings);
			editFrom.setAdapter(adapter);
			editFrom.setLayoutParams(paramsll);
			row.addView(editFrom);
			ll.addView(row);

			Button getCurrentLocationButton = new Button(this);
			getCurrentLocationButton.setText("Use current Location");
			getCurrentLocationButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					editFrom.setText("use current Location");
				}
			});
			ll.addView(getCurrentLocationButton);

		}
		{
			LinearLayout row = new LinearLayout(this);
			row.setOrientation(LinearLayout.HORIZONTAL);
			row.setLayoutParams(paramsll);

			TextView tvTo = new TextView(this);
			tvTo.setText("TO");
			row.addView(tvTo);

			editTo = new AutoCompleteTextView(this);
			String[] buildings = getResources().getStringArray(R.array.buildings);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, buildings);
			editTo.setAdapter(adapter);
			editTo.setLayoutParams(paramsll);
			row.addView(editTo);
			ll.addView(row);
		}

		TextView tv = new TextView(this);
		tv.setText("Please select your way, you want to navigate with...");
		ll.addView(tv);

		Button naviWithInternetButton = new Button(this);
		naviWithInternetButton.setText(R.string.start_winet);
		naviWithInternetButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (AppStatus.getInstance(getApplicationContext()).haveGPSConnection()) {
					if (AppStatus.getInstance(getApplicationContext()).haveNetworkConnection()) {
						Intent i = new Intent(v.getContext(), NaviWithInternetActivity.class);
						i.putExtra("from", editFrom.getText().toString());
						i.putExtra("to", editTo.getText().toString());
						startActivity(i);
					} else {
						toastL("You are probably not connected to the internet. Neither over WLAN nor Mobile.\n\nMaybe activate internet, or use the other method. Thanks!");
					}
				} else {
					showGPSDisabledAlertToUser();
					toastL("Please try again, after enabling GPS in your device.");
				}
			}
		});
		ll.addView(naviWithInternetButton);

		Button naviWithoutInternetButton = new Button(this);
		naviWithoutInternetButton.setText(R.string.start_woinet);
		naviWithoutInternetButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (AppStatus.getInstance(getApplicationContext()).haveGPSConnection()) {
						Intent i = new Intent(v.getContext(), NaviWithoutInternetActivity.class);
						i.putExtra("from", editFrom.getText().toString());
						i.putExtra("to", editTo.getText().toString());
						startActivity(i);
				} else {
					showGPSDisabledAlertToUser();
					toastL("Please try again, after enabling GPS in your device.");
				}
			}
		});
		ll.addView(naviWithoutInternetButton);

		Button tempButton = new Button(this);
		tempButton.setText("TEMP");
		tempButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
			}
		});
		ll.addView(tempButton);

		this.setContentView(sv);
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

	/**
	 * Show a string on the screen via Toast.
	 * 
	 * @param msg
	 *            String
	 * @return void
	 */

	public void toast(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	} // end toast

	public void toastL(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
	} // end toast
}
