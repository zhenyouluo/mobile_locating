package com.maloc.client.localization;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
/**
 * 定位服务缓存队列，用于实现生产者-消费者模式
 * @author xhw Email:xxyx66@126.com
 */
public class BlockingLocalizationInfoCache implements LocalizationInfoCache {

	private BlockingQueue<LocalizationInfo> queue=new LinkedBlockingQueue<LocalizationInfo>();
	
	@Override
	public void put(LocalizationInfo info) throws InterruptedException {

		queue.put(info);
	}

	@Override
	public LocalizationInfo take() throws InterruptedException {
		return queue.take();
	}

	@Override
	public void clear() {

		queue.clear();
	}

}
