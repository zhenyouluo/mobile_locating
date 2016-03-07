package com.maloc.client.localization;
/**
 * 定位结果
 * @author xhw
 *
 */
public class LocalizationResult implements Comparable<LocalizationResult>{

	float x;
	float y;
	float theta;
	float range;
	long timestamp;
	
	public LocalizationResult()
	{
		
	}
	
	public LocalizationResult(float x, float y, float theta,float range,long timestamp) {
		super();
		this.x = x;
		this.y = y;
		this.theta = theta;
		this.range=range;
		this.timestamp=timestamp;
	}
	
	
	
	

	public String toString()
	{
		return  x+","+y+","+theta+","+range+","+timestamp;
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

	public float getTheta() {
		return theta;
	}

	public void setTheta(float theta) {
		this.theta = theta;
	}

	

	@Override
	public int compareTo(LocalizationResult o) {

		if(o.timestamp==this.timestamp)
			return 0;
		else if(this.timestamp<o.timestamp)
			return -1;
		else
			return 1;
	}
	
	public double distance(LocalizationResult p)
	{
		return Math.sqrt((p.x-x)*(p.x-x)+(p.y-y)*(p.y-y));
	}
	
}
