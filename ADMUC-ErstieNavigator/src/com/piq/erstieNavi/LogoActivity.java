package com.piq.erstieNavi;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

public class LogoActivity extends Activity
{
	private final int ms = 5000;
	private MediaPlayer player;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.logo);
		
		player = MediaPlayer.create(this, R.raw.intro);
		player.start();
		
		Thread timer = new Thread()
		{
			public void run()
			{
				try
				{
					int timer = 0;
					
					while (timer< ms)
					{
						sleep(50);
						timer += 100;
					}
					//startActivity(new Intent("com.piq.startMain"));
					Intent i = new Intent(getApplicationContext(), Main.class);
					startActivity(i);
					//startActivity(new Intent(this, Main.class));
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				
				finally
				{
					finish();
				}
			}
		};
		
		timer.start();	
	}

	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		player.release();
	}

	@Override
	protected void onPause() 
	{
		super.onPause();
		player.pause();
	}

	@Override
	protected void onResume() 
	{
		super.onResume();
		player.start();
	}
	
	
}
