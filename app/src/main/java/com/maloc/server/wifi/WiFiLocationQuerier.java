package com.maloc.server.wifi;

import com.maloc.client.bean.RSSIData;
/**
 * WiFi指纹查询接口
 * @author xhw Email:xxyx66@126.com
 */
public interface WiFiLocationQuerier {
	
	public WiFiPosition queryLocationByRSSI(RSSIData r);

}
