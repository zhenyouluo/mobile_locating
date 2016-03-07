package com.maloc.client.localization;

import android.util.Log;

import com.maloc.client.localizationService.LocalizationService;
/**
 * 地磁定位线程
 * @author xhw Email:xxyx66@126.com
 */
public class LocalizationRunner implements Runnable{

	private LocalizationService service;
	private LocalizationInfoCache cache;
	private volatile boolean flag=true;
	public LocalizationRunner(LocalizationService service,LocalizationInfoCache cache)
	{
		this.service=service;
		this.cache=cache;
	}
	
	@Override
	public void run() {

		while(flag)
		{
			try {
				service.localize(cache.take());
			} catch (InterruptedException e) {
				//Thread.currentThread().interrupt();
				Log.w("LocalizationRunner", "cache is clear.");
				cache.clear();
				e.printStackTrace();
			}/*finally
			{
				Log.w("Cache", "cache is clear.");
				cache.clear();
			}*/
			Log.i("LocalizationRunner", "submit a mag loc info.");
		}
		flag=true; 
		
	}
	
	public void stop()
	{
		flag=false;
		Thread.currentThread().interrupt();
	}

}
