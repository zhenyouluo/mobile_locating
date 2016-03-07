package com.maloc.client.localization;

import com.maloc.client.localizationService.AndroidLocalizationService;
import com.maloc.client.localizationService.LocalizationService;
import com.maloc.client.localizationService.RemoteLocalizationService;
import com.maloc.client.sensor.localize.LocalizationSensorDataCollection;
import com.maloc.client.sensor.localize.SensorDataStream;
import com.maloc.client.util.GlobalProperties;

import android.app.Activity;
import android.os.Handler;
/**
 * Indoor Localizor
 * @author xhw Email:xxyx66@126.com
 */
public class IndoorLocalizor {

	private LocalizationService localizationService;
	LocalizationEngine localizationEngine;
	SensorDataStream stream;
	private LocalizationSensorDataCollection collection;
	public IndoorLocalizor(Activity activity,Handler handler)
	{
		//localizationService=new RemoteLocalizationService(handler);
		localizationService=new AndroidLocalizationService(handler);
		localizationEngine=new LocalizationEngine(localizationService);
		stream=new SensorDataStream(localizationEngine);
		collection=new LocalizationSensorDataCollection(activity,stream);
	}
	/**
	 * 利用WIFi进行定位初始化
	 */
	public void init()
	{
		collection.startWifiScan();
	}
	/**
	 * 开始定位
	 */
	public void start()
	{
		collection.startCollection();
		localizationEngine.start();
	}
	/**
	 * 结束定位
	 */
	public void stop()
	{
		collection.stopCollection();
		localizationEngine.stop();
	}
	
}
