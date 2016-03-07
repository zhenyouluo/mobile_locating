package com.maloc.client.sensor.localize;

import java.util.List;

import com.maloc.client.bean.AngularData;
import com.maloc.client.bean.RSSIData;
import com.maloc.client.util.FileLog;
import com.maloc.client.util.GlobalProperties;
import com.maloc.client.util.GyroComputer;
import com.maloc.client.util.KalmanFilter;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
/**
 * localization sensor data collector
 * @author xhw Email:xxyx66@126.com
 */
public class LocalizationSensorDataCollection implements SensorEventListener {

	protected SensorManager sensorManager;
	protected Sensor mSensor, aSensor,/* stepCounter,gSensor,gyroSensor,*/orienSensor;

	protected WifiManager mainWifi;
	protected WifiReceiver receiverWifi;
	protected List<ScanResult> wifiList;
	protected boolean wifiScanFlag=false;
	protected long wifiScanInterval=GlobalProperties.wifiScanInterval;

	//protected TextView recordFile;

	float orientation[]=new float[3];
	//float gravity[]=new float[3];
	float geomagnetic[]=new float[3];
	//float orientationValues[]=new float[3];
	//float Rot[]=new float[9];
    //float I[]=new float[9];
	long timestamp;
	long wifiTimestamp=0;
	
	/*protected GyroComputer gyroComputer=new GyroComputer();
	private KalmanFilter kfilter=new KalmanFilter();
	private double gyroChangedSum=0;
	*/
	private boolean gravityStartFlag=false,magneticStartFlag=false;
	
	protected SensorDataStream stream;
	protected Activity activity;

	private RSSIData rssi;
	public LocalizationSensorDataCollection(Activity activity,SensorDataStream stream)
	{
		this.activity=activity;
		this.stream=stream;
		initSensorConfig();
	}
	
	public RSSIData getCurrentWiFiRSSI()
	{
		return rssi;
	}

	/**
	 * 开始扫描WiFi
	 */
	public void startWifiScan()
	{
		if(!mainWifi.isWifiEnabled())
			mainWifi.setWifiEnabled(true);
		mainWifi.startScan();
		handler.postDelayed(runnable, GlobalProperties.wifiScanInterval);
		wifiScanFlag=true;
		wifiTimestamp=System.currentTimeMillis();
		sensorManager.registerListener(LocalizationSensorDataCollection.this, orienSensor,
				SensorManager.SENSOR_DELAY_FASTEST);
	}
	/**
	 * 停止扫描WiFi
	 */
	private void stopWifiScan()
	{
		handler.removeCallbacks(runnable);
		wifiScanFlag=false;
	}
	/**
	 * 开始采集传感器数据进行定位
	 */
	public void startCollection()
	{

		sensorManager.registerListener(LocalizationSensorDataCollection.this, mSensor,
				SensorManager.SENSOR_DELAY_FASTEST);
		sensorManager.registerListener(LocalizationSensorDataCollection.this, aSensor,
				SensorManager.SENSOR_DELAY_FASTEST);
		/*sensorManager.registerListener(LocalizationSensorDataCollection.this, gSensor,
				SensorManager.SENSOR_DELAY_FASTEST);*/
		
		//sensorManager.registerListener(LocalizationSensorDataCollection.this, stepCounter,
		//		SensorManager.SENSOR_DELAY_FASTEST);
		
		/*gyroComputer.initialize(); 
		sensorManager.registerListener(LocalizationSensorDataCollection.this, gyroSensor,
				SensorManager.SENSOR_DELAY_FASTEST);*/
		
		
	}
	/**
	 * 停止数据采集
	 */
	public void stopCollection()
	{
		this.stopWifiScan();
		gravityStartFlag=false;
		magneticStartFlag=false;
		//wifiScanInterval=Integer.MAX_VALUE;
		//mainWifi.setWifiEnabled(false);
		//wifiAdmin.closeWifi();
		//mainWifi.setWifiEnabled(false);
		sensorManager.unregisterListener(LocalizationSensorDataCollection.this);
	}
	
	/**
	 * init sensors
	 */
	protected void initSensorConfig() {
		
		sensorManager = (SensorManager) this.activity.getSystemService(Context.SENSOR_SERVICE);
		mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		aSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		//gSensor =sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
		//gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		orienSensor=sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		//stepCounter=sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
		receiverWifi = new WifiReceiver();
		//wifiAdmin=new WifiAdmin(this);
		//wifiAdmin.openWifi();
		
		mainWifi=(WifiManager) this.activity.getSystemService(Context.WIFI_SERVICE); 
		//if(mainWifi.isWifiEnabled())
			//mainWifi.setWifiEnabled(false);
		//mainWifi.setWifiEnabled(false);
		//Log.i("LocalizatoinSensorDataCollector sensor init", "init sensor success");
	}


	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		
		
		switch (event.sensor.getType()) {

		case Sensor.TYPE_ACCELEROMETER:
			timestamp=System.currentTimeMillis();
			onAccelerationChanged(event);
			break;
		case Sensor.TYPE_MAGNETIC_FIELD:
			onMagneticFieldChanged(event);
			break;
		/*case Sensor.TYPE_GRAVITY:
			onGravityChanged(event);
			break;*/
		/*case Sensor.TYPE_GYROSCOPE:
			onGyroscopeChanged(event);
			break;*/
		case Sensor.TYPE_ORIENTATION:
			onOrientationChanaged(event);
			break;
		/*case Sensor.TYPE_STEP_COUNTER:
			onStepCounter(event);
			break;*/
		}

	}

	private void onStepCounter(SensorEvent event) {

		Log.i("LocalizationSensorDataCollection stepCounter", event.values[0]+"");
	}

	private void onOrientationChanaged(SensorEvent event) {

		for(int i=0;i<3;i++)
		{
			this.orientation[i]=(float) (event.values[i]/180.0*Math.PI);
		}
		if(this.magneticStartFlag==true)
			stream.addOrientationData(orientation[0], timestamp);
	}

	/*protected void onGyroscopeChanged(SensorEvent event) {
		
		if (!gravityStartFlag || !magneticStartFlag)
			return;

		AngularData data = this.gyroComputer.angleChangedAroundGravity(event,
				gravity);
		if (data == null)
			return;
		double totalChanged = data.totalAngleChanged;
		//SensorManager.getRotationMatrix(Rot, I, gravity, geomagnetic);
		//SensorManager.getOrientation(Rot, orientationValues);
		if (gyroChangedSum == 0) {
			gyroChangedSum = orientation[0];
			kfilter.init(orientation[0], 1);
			return;
		}
		float orientationOfKF = kfilter.filt(gyroChangedSum - totalChanged,
				orientation[0], data.dT);
		gyroChangedSum = totalChanged;
		
		stream.addGyroscopeData(orientationOfKF, timestamp);
	}*/
	
	/*protected void onGravityChanged(SensorEvent event) {

		for(int i=0;i<3;i++)
			gravity[i]=event.values[i];
		gravityStartFlag=true;
	}
*/
	protected void onMagneticFieldChanged(SensorEvent event) {
		stream.addMagneticData(event.values, timestamp);
		magneticStartFlag = true;
	}

	
	protected void onAccelerationChanged(SensorEvent event) {
		
		stream.addAccelerationData(this.magnitude(event.values)-10, this.timestamp);
	}
	/**
	 * 计算模值
	 * @param value
	 * @return
	 */
	protected float magnitude(float value[])
	{
		float f=0;
		for(int i=0;i<value.length;i++)
		{
			f+=value[i]*value[i];
		}
		return (float)Math.sqrt(f);
	}
	
	class WifiReceiver extends BroadcastReceiver {

		public void onReceive(Context c, Intent intent) {
			
			if(!wifiScanFlag)
			{
				return;
			}
			long current=System.currentTimeMillis();
			rssi=new RSSIData(wifiTimestamp,current);
			wifiTimestamp=current;
			wifiList = mainWifi.getScanResults();
			for (int i = 0; i < wifiList.size(); i++) {
				rssi.add(wifiList.get(i).BSSID, wifiList.get(i).level);
			}
			mainWifi.startScan();
			Log.i("LocalizationSensorDataCollector","scan wifi.");
			try {
				rssi.setOrientation(orientation[0]);
				//FileLog.dataLog("orien", orientation[0]+"\n");
				stream.addWiFiRSSIData(rssi);
				if(GlobalProperties.Solution_WiFi==false)
				{
					stopWifiScan();
				}
			} catch (InterruptedException e) {
				stopWifiScan();
				e.printStackTrace();
			}
		}
	}
	
	protected Handler handler=new Handler();
	Runnable runnable=new Runnable(){
		@Override
		public void run() {
		LocalizationSensorDataCollection.this.receiverWifi.onReceive(null, null);
		handler.postDelayed(this, wifiScanInterval);
		}
	};
	public float[] getOrientation() {
		return orientation;
	}


}

