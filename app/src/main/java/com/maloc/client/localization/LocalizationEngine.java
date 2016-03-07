package com.maloc.client.localization;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import android.util.Log;

import com.maloc.client.bean.RSSIData;
import com.maloc.client.localizationService.LocalizationService;
import com.maloc.client.sensor.localize.SensorDataStream;
import com.maloc.client.sensor.process.SensorDataProcessor;
/**
 * 定位引擎，负责控制定位流程
 * @author xhw Email:xxyx66@126.com
 */
public class LocalizationEngine {

	private final ExecutorService producer;
	private final ExecutorService consumer;
	private final ExecutorService wifi;
	//private SensorDataProcessRunner runner=null;
	//private int size=1;
	private final LocalizationInfoCache cache;
	private final SensorDataProcessor sensorProcessor;
	private LocalizationService service ;
	private LocalizationRunner locRuner;
	private volatile boolean initFlag=false;
	private BlockingQueue<RSSIData> rssiQueue;
	private WiFiLocRunner wifiRunner;
	
	public LocalizationEngine(LocalizationService service)
	{
		producer= Executors.newSingleThreadExecutor();
		consumer=Executors.newSingleThreadExecutor();
		wifi=Executors.newFixedThreadPool(2);
		sensorProcessor=new SensorDataProcessor();
		cache=new BlockingLocalizationInfoCache();
		locRuner=new LocalizationRunner(service,cache);
		rssiQueue=new LinkedBlockingQueue<RSSIData>();
		wifiRunner=new WiFiLocRunner(rssiQueue,service);
		this.service=service;
		this.initFlag=false;
	}
	/**
	 * 启动消费者线程进行定位
	 */
	public void start()
	{
		consumer.execute(locRuner);
	}
	/**
	 * 停止定位
	 */
	public void stop()
	{
		this.initFlag=false;
		locRuner.stop();
		wifiRunner.stop();
	}
	/**
	 * 清空数据
	 */
	public void shutdown()
	{
		producer.shutdown();
		wifi.shutdown();
		locRuner.stop();
		consumer.shutdown();
	}
	/**
	 * 利用WiFi定位重置起始位置
	 * @param rd
	 */
	public void resetPosition(final RSSIData rd)
	{
		this.initFlag=true;
		//service.initPosition(rd);
		/*new Thread(new Runnable(){

			@Override
			public void run() {
				service.initPosition(rd);
			}
			
		}).start();*/
		wifi.execute(new Runnable(){

			@Override
			public void run() {
				service.initPosition(rd);
			}
			
		});
		
		wifi.execute(wifiRunner);
		
		
		
	}
	/**
	 * feed WiFi RSSI数据
	 * @param rd
	 */
	public void submit(final RSSIData rd)
	{
		if(this.initFlag==false)
		{
			Log.i("LocalizationEngine","init position");
			resetPosition(rd);
			return;
		}
		//rssiQueue.put(rd);
		//service.localize(rd);
		wifi.execute(new Runnable(){

			@Override
			public void run() {
				try {
					rssiQueue.put(rd);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		});
	}
	/**
	 * feed地磁定位数据
	 * @param info
	 */
	public void submit(SensorDataStream stream)
	{
		producer.execute(new SensorDataProcessRunner(stream,sensorProcessor,
				cache,stream.getCurrentIndicator(),stream.getLastAccCnt(),stream.getAccCnt()));
	}
	
}
