package com.maloc.server.wifi;

import java.util.Arrays;
/**
 * kNN查询的WiFi定位结果
 * @author xhw Email:xxyx66@126.com
 */
public class WiFiPosition implements Comparable<WiFiPosition>{

	//要查询的rssi和查询结果之间的距离
		private int distance;
		//查询到的WiFi指纹和要查询的rssi中相同的WiFi热点数量
		private int sameBssidNum=0;
		//查询结果的位置
		private float position[];
	//private RSSIData rssi;
	
	public WiFiPosition()
	{
		position=new float[2];
		//position[0]=0;
		//position[1]=0;
		distance=Integer.MAX_VALUE;
	}

	public void setPositions(float x,float y)
	{
		this.position[0]=x;
		this.position[1]=y;
	}
	
	public void copy(WiFiPosition pos)
	{
		this.distance=pos.distance;
		this.sameBssidNum=pos.sameBssidNum;
		this.position=Arrays.copyOf(pos.getPosition(),this.position.length);
		//this.rssi=pos.rssi;
	}
	
	@Override
	public int compareTo(WiFiPosition o) {

		if(this.distance>o.distance)
			return 1;
		else if(this.distance<o.distance)
			return -1;
		return 0;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public int getSameBssidNum() {
		return sameBssidNum;
	}

	public void setSameBssidNum(int sameBssidNum) {
		this.sameBssidNum = sameBssidNum;
	}

	public float[] getPosition() {
		return position;
	}

	public void setPosition(float[] position) {
		this.position = position;
	}

	/*public RSSIData getRssi() {
		return rssi;
	}

	public void setRssi(RSSIData rssi) {
		this.rssi = rssi;
	}*/
}
