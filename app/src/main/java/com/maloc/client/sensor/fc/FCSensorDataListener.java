package com.maloc.client.sensor.fc;

import com.maloc.client.bean.AccelerationVector;
import com.maloc.client.bean.AngularData;
import com.maloc.client.bean.FCMagneticVector;
import com.maloc.client.bean.OrientationVector;
import com.maloc.client.bean.RSSIData;
/**
 * 指纹数据收集监听接口
 * @author xhw Email:xxyx66@126.com
 */
public class FCSensorDataListener implements SensorDataListener {

	private SensorsData sensorsData;
	
	public FCSensorDataListener(){
		
	}
	public FCSensorDataListener(SensorsData data){
		this.sensorsData=data;
	}
	
	@Override
	public void onAccelerationDataReady(AccelerationVector av) {

		sensorsData.accelerationList.add(av);
	}

	@Override
	public void onGravityDataReady(AccelerationVector av) {

		
	}

	@Override
	public void onAngularDataReady(AngularData ad) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMagneticDataReady(FCMagneticVector mv) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGeoMagneticDataReady(FCMagneticVector mv) {

		sensorsData.geoMagneticList.add(mv);
	}

	@Override
	public void onWiFiRSSIReceived(RSSIData rssi) {

		sensorsData.rssiList.add(rssi);
	}
	public SensorsData getSensorsData() {
		return sensorsData;
	}
	public void setSensorsData(SensorsData sensorsData) {
		this.sensorsData = sensorsData;
	}
	@Override
	public void onOrientationChanged(OrientationVector ov) {
		// TODO Auto-generated method stub
		
	}

}
