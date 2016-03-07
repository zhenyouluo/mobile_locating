package com.maloc.client.localization;
/**
 * 缓存队列接口
 * @author xhw
 *
 */
public interface LocalizationInfoCache {

	public void put(LocalizationInfo info) throws InterruptedException;
	public LocalizationInfo take() throws InterruptedException;
	public void clear();
}
