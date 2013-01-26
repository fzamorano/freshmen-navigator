package com.piq.widget;

import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.GeomagneticField;
import android.location.Location;
import android.location.LocationManager;
import android.util.AttributeSet;
import android.view.View;

import com.piq.erstieNavi.R;
import com.piq.erstieNavi.model.Building;
import com.piq.erstieNavi.services.BuildingsManager;

public class Compass extends View {
	private Paint paint;
	private Bitmap bitmap;
	private float north = (float) (-Math.PI / 6);
	private Location currentLocation;
	BuildingsManager bm = BuildingsManager.getInstance();
	private Building building;
	private Path path = null;
	
	public Compass(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.TRANSPARENT);
		
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.FILL);
		
		renderCompass(canvas);
		renderDirection(canvas, building);
		renderCircle(canvas);
		
	}
	
	private void renderCompass(Canvas canvas) {
		int width = this.getWidth();
		int height = this.getHeight();
		int centerX = width / 2;
		int centerY = height / 2;
		
		if (bitmap == null) {
			bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.kompass);
		}
		canvas.save();
		canvas.translate(centerX, centerY);
		
		canvas.rotate(north);
		float maxwidth = (float) (bitmap.getWidth() * Math.sqrt(2));
		float maxheight = (float) (bitmap.getHeight() * Math.sqrt(2));
		float ratio = Math.min(width / maxwidth, height / maxheight);
		int widthr = (int) (bitmap.getWidth() * ratio);
		int heightr = (int) (bitmap.getHeight() * ratio);
		
		canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), new Rect(-widthr / 2, -heightr / 2, widthr / 2, heightr / 2), paint);
		canvas.restore();
		
	}
	
	private void renderDirection(Canvas canvas, Building building) {
		int w = getWidth();
		int h = getHeight();
		int centerX = w / 2;
		int centerY = h / 2;
		
		if (currentLocation != null && building != null) {
			if (path == null) {
				int height = (int) (Math.min(w / 2, h / 2));
				int width = height / 10;
				int dirWidth = 2 * width;
				int arrowHeight = dirWidth / 2;
				path = new Path();
				
				path.moveTo(-width / 2, height / 3);
				path.lineTo(width / 2, height / 3);
				path.lineTo(width / 2, -height * 2 / 3);
				path.lineTo(dirWidth / 2, -height * 2 / 3);
				path.lineTo(0, -height * 2 / 3 - arrowHeight);
				path.lineTo(-dirWidth / 2, -height * 2 / 3);
				path.lineTo(-width / 2, -height * 2 / 3);
				path.lineTo(-width / 2, height / 3);
				path.close();
			}
			
			canvas.save();
			canvas.translate(centerX, centerY);
			
			Location loc = new Location(LocationManager.GPS_PROVIDER);
			loc.setLatitude(building.getLatitude());
			loc.setLongitude(building.getLongitude());
			float bearing = currentLocation.bearingTo(loc);
			
			float declination = 0.0f;
			GeomagneticField geoMagneticField = new GeomagneticField((float) currentLocation.getLatitude(), (float) currentLocation.getLongitude(), (float) currentLocation.getAltitude(), new Date().getTime());
			declination = geoMagneticField.getDeclination();
			
			float MNBearing = (float) (north + declination + bearing);
			
			canvas.rotate(MNBearing);
			
			paint.setColor(Color.TRANSPARENT);
			paint.setStyle(Style.FILL);
			canvas.drawPath(path, paint);
			paint.setColor(Color.RED);
			paint.setStyle(Style.STROKE);
			paint.setStrokeWidth(2);
			canvas.drawPath(path, paint);
			
			canvas.restore();
			
		}
	}
	
	private void renderCircle(Canvas canvas) {
		int w = getWidth();
		int h = getHeight();
		int cx = w / 2;
		int cy = h / 2;
		
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		int radius = w / 30;
		paint.setColor(Color.RED);
		paint.setStyle(Style.FILL);
		canvas.drawArc(new RectF(cx - radius, cy - radius, cx + radius, cy + radius), 0, 360, false, paint);
		paint.setColor(Color.BLACK);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(2);
		canvas.drawArc(new RectF(cx - radius, cy - radius, cx + radius, cy + radius), 0, 360, false, paint);
	}
	
	public void setNorth(float north) {
		this.north = north;
	}
	
	public void setCurrentLocation(Location location) {
		this.currentLocation = location;
	}
	
	public void setCurrentPlace(Building building) {
		this.building = building;
	}
	
}
