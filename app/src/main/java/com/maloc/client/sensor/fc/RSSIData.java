package com.maloc.client.sensor.fc;

import java.util.HashMap;
import java.util.Map;
/**
 * WiFi RSSI数据
 * @author xhw
 *
 */
public class RSSIData {
	
	
	float x,y;
	Map<String,Integer> rssi=new HashMap<String,Integer>();
	
	public RSSIData(String[] ids,int values[])
	{
		for(int i=0;i<ids.length;i++)
		{
			if(ids!=null&&ids.length>0)
			{
				rssi.put(ids[i], values[i]);
			}
		}
	}

	public RSSIData(float x,float y,String[] ids,int values[])
	{
		this(ids,values);
		this.x=x;
		this.y=y;
	}
	
	public RSSIData(String rssiStr) {

		if(rssiStr!=null)
		{
			String str[]=rssiStr.split(";");
			String coordinate[]=str[0].split(",");
			this.x=Float.parseFloat(coordinate[0]);
			this.y=Float.parseFloat(coordinate[1]);
			for(int i=1;i<str.length;i++)
			{
				String values[]=str[i].split("=");
				this.add(values[0], Integer.parseInt(values[1]));
			}
		}
		
	}
	
	public RSSIData(float x,float y)
	{
		this.x=x;
		this.y=y;
	}
	
	public void add(String key,int value)
	{
		this.rssi.put(key, value);
	}
	
	public String toString()
	{
		StringBuilder sb=new StringBuilder();
		sb.append(x).append(",").append(y).append(";");
		for(String key:this.rssi.keySet())
		{
			sb.append(key).append("=").append(this.rssi.get(key)).append(";");
		}
		return sb.toString();
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public Map<String, Integer> getRssi() {
		return rssi;
	}

	public void setRssi(Map<String, Integer> rssi) {
		this.rssi = rssi;
	}
}
