package com.maloc.client.sensor.accelerometer;
/**
 * 加速度曲线上的极值点
 * @author xhw
 *
 */
public class InflectionPoint {
	
	long timestamp;
	boolean flag;
	float value;
	
	/**
	 * flag=true 谷值， flag=false 峰值
	 * @param timestamp
	 * @param flag
	 * @param value
	 */
	public InflectionPoint(long timestamp, boolean flag, float value) {
		super();
		this.timestamp = timestamp;
		this.flag = flag;
		this.value = value;
	}	
	

	public String toString()
	{
		return timestamp+"\t"+value+"\t"+flag;
	}


	public long getTimestamp() {
		return timestamp;
	}


	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}


	public boolean isFlag() {
		return flag;
	}


	public void setFlag(boolean flag) {
		this.flag = flag;
	}


	public double getValue() {
		return value;
	}


	public void setValue(float value) {
		this.value = value;
	}
	
}
