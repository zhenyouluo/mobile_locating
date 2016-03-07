package com.maloc.client.sensor.fc;

import java.util.List;

import com.maloc.client.bean.RSSIData;
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
 * 传感器收集基类
 * @author xhw Email:xxyx66@126.com
 */
public abstract class SensorDataCollector implements SensorEventListener {

	protected SensorManager sensorManager;
	protected Sensor mSensor, aSensor, gravitySensor, gyroSensor,orienSensor;

	protected WifiManager mainWifi;
	protected WifiReceiver receiverWifi;
	protected List<ScanResult> wifiList;
	protected boolean wifiScanFlag = false;
	protected long wifiScanInterval = GlobalProperties.FC_WIFI_SCAN_INTERVAL;

	// protected TextView recordFile;
	protected float orientation_values[]=new float[3];
	protected float orientation[] = new float[3];
	protected float gravity[] = new float[3];
	protected float geomagnetic[] = new float[3];
	protected float orientationValues[] = new float[3];
	protected float Rot[] = new float[9];
	protected float I[] = new float[9];
	protected long timestamp;
	protected long wifiTimestamp = 0;
	protected GyroComputer gyroComputer= new GyroComputer();

	protected KalmanFilter kfilter=new KalmanFilter();
	protected double gyroChangedSum=0;
	protected boolean gravityStartFlag=false,magneticStartFlag=false;
	
	protected Context context;
	protected RSSIData rssi;
	
	//private SensorsData sensorsData;
	
	//private SenseMode mode=SenseMode.FINGERPRINTS_COLLECTION;

	protected SensorDataListener listener;
	public SensorDataCollector(Context context,SensorDataListener listener) {
		this.context = context;
		this.listener=listener;
		initSensorConfig();
	}
	
	
	public RSSIData getCurrentWiFiRSSI() {
		return rssi;
	}

	public void startWifiScan() {
		if (!mainWifi.isWifiEnabled())
			mainWifi.setWifiEnabled(true);
		mainWifi.startScan();
		handler.postDelayed(runnable, GlobalProperties.FC_WIFI_SCAN_INTERVAL);
		wifiScanFlag = true;
		wifiTimestamp = System.currentTimeMillis();
	}

	public void startCollection(Line line){};
	public abstract void startCollection();

	public void stopCollection() {
		
		wifiScanFlag = false;
		handler.removeCallbacks(runnable);
		sensorManager.unregisterListener(SensorDataCollector.this);
	}

	protected void initSensorConfig() {
		sensorManager = (SensorManager) this.context
				.getSystemService(Context.SENSOR_SERVICE);
		mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		aSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
		gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		orienSensor=sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		receiverWifi = new WifiReceiver();
		mainWifi = (WifiManager) this.context
				.getSystemService(Context.WIFI_SERVICE);

		//Log.i("sensor init", "init sensor success");
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	
	protected void onGravityChanged(SensorEvent event) {

		for (int i = 0; i < 3; i++)
			gravity[i] = event.values[i];
		gravityStartFlag = true;
	}

	class WifiReceiver extends BroadcastReceiver {

		public void onReceive(Context c, Intent intent) {

			if (!wifiScanFlag) {
				return;
			}
			long current = System.currentTimeMillis();
			rssi = new RSSIData(wifiTimestamp, current);
			wifiTimestamp = current;
			wifiList = mainWifi.getScanResults();
			mainWifi.startScan();
			for (int i = 0; i < wifiList.size(); i++) {
				rssi.add(wifiList.get(i).BSSID, wifiList.get(i).level);
			}
			if(listener!=null)
				listener.onWiFiRSSIReceived(rssi);
		}
	}

	protected Handler handler = new Handler();
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			SensorDataCollector.this.receiverWifi.onReceive(null, null);
			handler.postDelayed(this, wifiScanInterval);
		}
	};
	
	
	public SensorDataListener getListener() {
		return listener;
	}


	public void setListener(SensorDataListener listener) {
		this.listener = listener;
	}

}
