package com.maloc.client.localization;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.util.Log;

import com.maloc.client.bean.RSSIData;
import com.maloc.client.localizationService.LocalizationService;
/**
 * WiFi消费者线程，进行WiFi定位
 * @author xhw
 *
 */
public class WiFiLocRunner implements Runnable {

	private BlockingQueue<RSSIData> queue;
	private LocalizationService service;
	private volatile boolean flag=true;
	public WiFiLocRunner(BlockingQueue<RSSIData> queue,LocalizationService service)
	{
		this.queue=queue;
		this.service=service;
	}
	
	@Override
	public void run() {

		while(flag)
		{
			try {
				service.localize(queue.take());
			} catch (InterruptedException e) {
				//Thread.currentThread().interrupt();
				Log.w("WiFiRunner", "queue is clear.");
				queue.clear();
				e.printStackTrace();
			}
			/*finally
			{
				Log.w("WiFiRunner", "queue is clear.");
				queue.clear();
			}*/
			Log.i("WiFiRunner", "submit a rssi.");
		}
		flag=true;
	}
	
	public void stop()
	{
		flag=false;
		Thread.currentThread().interrupt();
	}

}
