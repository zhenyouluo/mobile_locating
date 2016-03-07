package com.maloc.client.sensor.fc;

import java.util.ArrayList;
import java.util.Arrays;

import com.maloc.client.ellipsoidFit.FitPoints;
import com.maloc.client.ellipsoidFit.ThreeSpacePoint;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
/**
 * calibration sensor data collector
 * @author xhw Email:xxyx66@126.com
 */
public class CalibrationSensorDataCollector extends SensorDataCollector {

	private static final long MAX_CALIBRATION_TIME = 60*1000;
	private static final float MIN_COVER_RATE=0.3f;
	private int azimuth=8,pitch=8,roll=4;
	private boolean bitmap[][][]=new boolean[azimuth][pitch][roll];;
	private int cnt=0,total=azimuth*pitch*roll;
	
	private ArrayList<ThreeSpacePoint> CONTROL_ELLIPSOID_POINTS;
	
	private CalibrationReadyListener ready;
	private long startTime=0;
	
	public CalibrationSensorDataCollector(Context context,
			SensorDataListener listener,CalibrationReadyListener ready) {
		super(context, listener);
		this.ready=ready;
	}
	
	public CalibrationSensorDataCollector(Context context,CalibrationReadyListener ready) {
		super(context, null);
		this.ready=ready;
	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		this.timestamp=System.currentTimeMillis();
		switch (event.sensor.getType()) {
		
		case Sensor.TYPE_MAGNETIC_FIELD:
			onMagneticFieldChanged(event);
			break;
		case Sensor.TYPE_ORIENTATION:
			onOrientationChanged(event);
			break;
		
		}
		
	}
	
	
	/**
	 * use orientation sensor to estimate how much the phone was rotated.
	 * @param event
	 */
	private void onOrientationChanged(SensorEvent event) {

			int z=(int) event.values[0];
			int x=(int) event.values[1];
			int y=(int) event.values[2];
			z=z/45;
			x=(x+180)/45;
			y=(y+90)/45;
			if(bitmap[z][x][y]==false)
			{
				cnt++;
				bitmap[z][x][y]=true;
			}
			
			if(cnt>total*MIN_COVER_RATE)
			{
				this.stopCollection();
				
				FitPoints ellipsoidFit = new FitPoints();
				ellipsoidFit.fitEllipsoid(CONTROL_ELLIPSOID_POINTS);
				//CONTROL_ELLIPSOID_POINTS=null;
				ready.onCalibrationReady(ellipsoidFit);
			}
			
			if(this.timestamp-startTime>MAX_CALIBRATION_TIME)
			{
				this.stopCollection();
				ready.onCalibrationTimeOut();
			}
			if(cnt%5==0)
				ready.calibrationProgess((int) (cnt/(total*MIN_COVER_RATE)*100));
	}
	
	@Override
	public void stopCollection() {
		
		sensorManager.unregisterListener(CalibrationSensorDataCollector.this);
	}

	@Override
	public void startCollection() {

		for(int i=0;i<azimuth;i++)
		{
			for(int j=0;j<pitch;j++)
				Arrays.fill(bitmap[i][j], false);
		}
		cnt=0;
		CONTROL_ELLIPSOID_POINTS=new ArrayList<ThreeSpacePoint>();
		startTime=System.currentTimeMillis();
		
		sensorManager.registerListener(CalibrationSensorDataCollector.this, mSensor,
				SensorManager.SENSOR_DELAY_FASTEST);
		sensorManager.registerListener(CalibrationSensorDataCollector.this,orienSensor,
				SensorManager.SENSOR_DELAY_FASTEST);
		
	}
	/**
	 * magnetic changed handler
	 * @param event
	 */
	private void onMagneticFieldChanged(SensorEvent event) {

		CONTROL_ELLIPSOID_POINTS.add(new ThreeSpacePoint(event.values[0],event.values[1],event.values[2]));
		
	}
	
	
}
