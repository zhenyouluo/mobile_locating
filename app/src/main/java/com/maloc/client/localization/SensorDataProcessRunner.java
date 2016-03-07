package com.maloc.client.localization;


import android.util.Log;

import com.maloc.client.localizationService.LocalizationService;
import com.maloc.client.sensor.localize.SensorDataStream;
import com.maloc.client.sensor.process.SensorDataProcessor;
import com.maloc.client.util.FileLog;
/**
 * 地磁定位生产者线程，负责处理传感器数据，打包生成LocalizationInfo，放入Cache队列中
 * @author xhw
 *
 */

public class SensorDataProcessRunner implements Runnable{

	private SensorDataStream stream;
	//private ResultListener call;
	private int indicator;
	private int start;
	private int end;
	private SensorDataProcessor sensorProcessor=null;
	private LocalizationInfoCache cache;
	//private LocalizationService localizationService;
	
	/*public LocalizationRunner()
	{
		sensorProcessor=new SensorDataProcessor();
	}*/
	public SensorDataProcessRunner(SensorDataStream stream,SensorDataProcessor sensorProcessor,
			LocalizationInfoCache cache,int indicator,int start,int end)
	{
		this.stream=stream;
		this.sensorProcessor=sensorProcessor;
		this.cache=cache;
		this.indicator=indicator;
		this.start=start;
		this.end=end;
		//localizationService=service;
	}
	
	@Override
	public void run() {

		//Log.i("Request for localization.", "start");
		LocalizationInfo info=sensorProcessor.process(stream.getAccData()[indicator], 
				stream.getAccTimestamps()[indicator], start, end, 
				stream.getMagneticData()[indicator], 
				stream.getMagneticTimestamps()[indicator], 
				stream.getGyroData()[indicator], 
				stream.getGyroTimestamps()[indicator]);
		if(info!=null)
		{
			Log.i("Request for localization.", info.getAccList()+";"
					+info.getGyroValue()+";"+info.getMagList());
			try {
				//FileLog.dataLog("cache_put.txt", info.toString());
				cache.put(info);
			} catch (InterruptedException e) {
				cache.clear();
				e.printStackTrace();
			}
		}
		
		
	}
	
	public SensorDataStream getStream() {
		return stream;
	}

	public void setStream(SensorDataStream stream) {
		this.stream = stream;
	}


	public int getIndicator() {
		return indicator;
	}

	public void setIndicator(int indicator) {
		this.indicator = indicator;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}


}
