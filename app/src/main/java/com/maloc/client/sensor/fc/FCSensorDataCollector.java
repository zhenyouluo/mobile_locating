package com.maloc.client.sensor.fc;

import java.util.List;

import com.maloc.client.bean.AccelerationVector;
import com.maloc.client.bean.FCMagneticVector;
import com.maloc.client.bean.RSSIData;
import com.maloc.client.ellipsoidFit.FitPoints;
import com.maloc.client.fc.graphics.Line;
import com.maloc.client.util.CoordinateSystemRotation;
import com.maloc.client.util.GlobalProperties;
import com.maloc.client.util.GyroComputer;
import com.maloc.client.util.KalmanFilter;

import Jama.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
/**
 * 传感器控制类
 * @author xhw Email:xxyx66@126.com
 */
public class FCSensorDataCollector extends SensorDataCollector {

	private Line line;
	private float magneticTemp[]=new float[3];
	public FCSensorDataCollector(Context context,SensorDataListener listener) {
		super(context,listener);
	}
	

	public RSSIData getCurrentWiFiRSSI() {
		return rssi;
	}
	
	public void startCollection(Line line)
	{
		this.line=line;
		this.startCollection();
	}
	
	public void startCollection() {

		startWifiScan();
		sensorManager.registerListener(FCSensorDataCollector.this, mSensor,
				SensorManager.SENSOR_DELAY_FASTEST);
		sensorManager.registerListener(FCSensorDataCollector.this, gravitySensor,
				SensorManager.SENSOR_DELAY_FASTEST);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		timestamp = System.currentTimeMillis();
		switch (event.sensor.getType()) {
		
		case Sensor.TYPE_MAGNETIC_FIELD:
			onMagneticFieldChanged(event);
			break;
		case Sensor.TYPE_GRAVITY:
			onGravityChanged(event);
			break;
		}

	}

	

	private void onMagneticFieldChanged(SensorEvent event) {

		//MagneticVector mv=new MagneticVector(event.values,timestamp);
		//this.listener.onMagneticDataReady(mv);
		
		for (int i = 0; i < 3; i++)
			magneticTemp[i] = event.values[i];
		calibrate(magneticTemp,geomagnetic);
		
		magneticStartFlag = true;
		recordMagneticAfterCST();
	}

	
	private void calibrate(float[] temp,float[] output) {

		FitPoints fit=GlobalProperties.ellipsoidFit;
		if(fit==null)
		{
			for (int i = 0; i < 3; i++)
				output[i] = temp[i];
			return ;
		}
		
		for(int i=0;i<3;i++)
			temp[i]-=GlobalProperties.center[i];
		
		
		for(int i=0;i<3;i++)
		{
			float s=0;
			for(int j=0;j<3;j++)
			{
				s+=GlobalProperties.invertW.get(i, j)*temp[j];
			}
			output[i]=s;
		}
	}


	private void recordMagneticAfterCST() {

		this.getOrientationByLine();
		float omegaMagnitude = (float) Math.sqrt(orientation_values[0]
				* orientation_values[0] + orientation_values[1]
				* orientation_values[1] + orientation_values[2]
				* orientation_values[2]);
		Matrix newGeoMag = CoordinateSystemRotation.coorinateSystemRotate(
				orientation_values, omegaMagnitude, geomagnetic);
		FCMagneticVector mv=new FCMagneticVector((float)newGeoMag.getArray()[0][0],(float)newGeoMag.getArray()[1][0],
				(float)newGeoMag.getArray()[2][0],timestamp);
		this.listener.onGeoMagneticDataReady(mv);
	}

	private float[] getOrientationByLine() {
		float Rien[] = new float[9];
		float Inl[] = new float[9];
		SensorManager.getRotationMatrix(Rien, Inl, gravity, geomagnetic);
		SensorManager.getOrientation(Rien, orientation_values);
		
		
		orientation_values[0]=(float) line.slope();
		if(this.line.isMoveForwar()==false)
		{
			orientation_values[0]=(float) (Math.PI+orientation_values[0]);
		}
		
		float temp = orientation_values[0];
		orientation_values[0] = orientation_values[1];
		orientation_values[1] = orientation_values[2];
		orientation_values[2] = temp;
		return orientation_values;
	}

	private void onAccelerationChanged(SensorEvent event) {
		AccelerationVector av=new AccelerationVector(event.values,timestamp);
		listener.onAccelerationDataReady(av);
	}

	

}
