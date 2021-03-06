package com.maloc.client.sensor.localize;

import java.util.Arrays;

import android.util.Log;

import com.maloc.client.bean.RSSIData;
import com.maloc.client.localization.LocalizationEngine;
import com.maloc.client.localization.ResultListener;
import com.maloc.client.localizationService.LocalizationService;
import com.maloc.client.util.GlobalProperties;
/**
 * sensor data stream.
 * cache sensor data and control the motion model process
 * @author xhw Email:xxyx66@126.com
 */
public class SensorDataStream {

	private LocalizationEngine localizationEngine;
	//private ResultListener onResult;
	//private LocalizationService service;
	private static final int size=1;
	
	protected float accData[][]=new float[size][GlobalProperties.CACHESIZE];
	protected int accCnt=0,lastAccCnt=0;
	protected long accTimestamps[][]=new long[size][GlobalProperties.CACHESIZE];
	protected int indicator=0;
	protected int updateCnt=0;
	protected float pre[][]=new float[size][GlobalProperties.meanFilterWindowSize];
	
	protected float orienData[][]=new float[size][GlobalProperties.CACHESIZE];
	protected int orienCnt=0;
	protected long orienTimestamps[][]=new long[size][GlobalProperties.CACHESIZE];
	
	protected float magneticData[][][]=new float[size][GlobalProperties.CACHESIZE][3];
	protected int magneticCnt=0;
	protected long magneticTimestamps[][]=new long[size][GlobalProperties.CACHESIZE];
	
	//protected RSSIData rssiList[][]=new RSSIData[size][50]; 
	protected int rssiCnt=0;
	
	protected int currentIndicator=0;
	
	public SensorDataStream(LocalizationEngine localizationEngine)
	{
		this.localizationEngine=localizationEngine;
		//this.service=service;
	}
	/**
	 * feed加速度数据，当满足一个窗口大小后，提交给LocalizationEngine更新位置
	 * @param acc
	 */
	public void addAccelerationData(float acc,long timestamp)
	{
		accData[indicator][accCnt]=acc;
		accTimestamps[indicator][accCnt]=timestamp;
		accCnt++;
		//Log.i("accCnt", accCnt+"");
		if(accCnt%GlobalProperties.UPDATEINTERVAL==0)
		{
			currentIndicator=indicator;
			localizationEngine.submit(this);
			lastAccCnt=accCnt;
			
		}
		if(accCnt==GlobalProperties.CACHESIZE)
		{
			Log.w("Cache","Cache is full!");
			//synchronized(this)
			//{
				accCnt=0;
				lastAccCnt=0;
				orienCnt=0;
				magneticCnt=0;
				rssiCnt=0;
				indicator=(indicator+1)%size;
				updateCnt++;
			//}
			
		}
		
	}
	
	/**
	 * 缓存朝向数据
	 * @param gyro
	 * @param timestamp
	 */
	public void addOrientationData(float gyro,long timestamp)
	{
		if(accCnt>0&&timestamp>=accTimestamps[indicator][accCnt-1])
		{
			if(orienCnt>0&&timestamp==orienTimestamps[indicator][orienCnt-1])
				return;
			orienData[indicator][orienCnt]=gyro;
			orienTimestamps[indicator][orienCnt]=timestamp;
			orienCnt++;
		}
		
		
	}
	/**
	 * 缓存磁场强度数据
	 * @param magneticValue
	 * @param timestamp
	 */
	public void addMagneticData(float magneticValue[],long timestamp)
	{
		
		if(accCnt>0&&timestamp>=accTimestamps[indicator][accCnt-1])
		{
			if(magneticCnt>0&&timestamp==magneticTimestamps[indicator][magneticCnt-1])
				return;
			copy(magneticValue,magneticData[indicator][magneticCnt]);
			magneticTimestamps[indicator][magneticCnt]=timestamp;
			magneticCnt++;
		}
		
	}
	/**
	 * 提交给LocalizationEngine进行WiFi定位
	 * @param rssi
	 * @throws InterruptedException
	 */
	public void addWiFiRSSIData(RSSIData rssi) throws InterruptedException
	{
		this.localizationEngine.submit(rssi);
	}

	private void copy(float[] from, float[] to) {

		for(int i=0;i<from.length;i++)
		{
			to[i]=from[i];
		}
	}


	public float[][] getAccData() {
		return accData;
	}


	public void setAccData(float[][] accData) {
		this.accData = accData;
	}


	public int getAccCnt() {
		return accCnt;
	}


	public void setAccCnt(int accCnt) {
		this.accCnt = accCnt;
	}


	public long[][] getTimestamps() {
		return accTimestamps;
	}


	public void setTimestamps(long[][] timestamps) {
		this.accTimestamps = timestamps;
	}


	public synchronized int getIndicator() {
		return indicator;
	}


	public synchronized void setIndicator(int indicator) {
		this.indicator = indicator;
	}


	public float[][] getGyroData() {
		return orienData;
	}


	public void setGyroData(float[][] gyroData) {
		this.orienData = gyroData;
	}


	public int getGyroCnt() {
		return orienCnt;
	}


	public void setGyroCnt(int gyroCnt) {
		this.orienCnt = gyroCnt;
	}


	public float[][][] getMagneticData() {
		return magneticData;
	}


	public void setMagneticData(float[][][] magneticData) {
		this.magneticData = magneticData;
	}


	public int getMagneticCnt() {
		return magneticCnt;
	}


	public void setMagneticCnt(int magneticCnt) {
		this.magneticCnt = magneticCnt;
	}
/*

	public RSSIData[][] getRssiList() {
		return rssiList;
	}


	public void setRssiList(RSSIData[][] rssiList) {
		this.rssiList = rssiList;
	}
*/

	public int getRssiCnt() {
		return rssiCnt;
	}


	public void setRssiCnt(int rssiCnt) {
		this.rssiCnt = rssiCnt;
	}

	public LocalizationEngine getLocalizationEngine() {
		return localizationEngine;
	}

	public void setLocalizationEngine(LocalizationEngine localizationEngine) {
		this.localizationEngine = localizationEngine;
	}


	public long[][] getAccTimestamps() {
		return accTimestamps;
	}

	public void setAccTimestamps(long[][] accTimestamps) {
		this.accTimestamps = accTimestamps;
	}

	public long[][] getGyroTimestamps() {
		return orienTimestamps;
	}

	public void setGyroTimestamps(long[][] gyroTimestamps) {
		this.orienTimestamps = gyroTimestamps;
	}

	public long[][] getMagneticTimestamps() {
		return magneticTimestamps;
	}

	public void setMagneticTimestamps(long[][] magneticTimestamps) {
		this.magneticTimestamps = magneticTimestamps;
	}

	public synchronized int getCurrentIndicator() {
		return currentIndicator;
	}

	public synchronized void setCurrentIndicator(int currentIndicator) {
		this.currentIndicator = currentIndicator;
	}

	public int getLastAccCnt() {
		return lastAccCnt;
	}

	public void setLastAccCnt(int lastAccCnt) {
		this.lastAccCnt = lastAccCnt;
	}

	public int getSize() {
		return size;
	}
	
	
}
