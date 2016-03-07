package com.maloc.client.bean;
/**
 * 指纹收集地磁向量
 * @author xhw
 *
 */
public class FCMagneticVector {

	float vector[];
	long timestamp;

	public FCMagneticVector(float x, float y, float z, long timestamp) {
		this.vector = new float[3];
		vector[0] = x;
		vector[1] = y;
		vector[2] = z;
		this.timestamp = timestamp;
	}

	public FCMagneticVector(float[] vector,long timestamp) {
		this.vector = new float[3];
		for(int i=0;i<3;i++)
			this.vector[i]=vector[i];
		this.timestamp=timestamp;
	}

	public FCMagneticVector(String str) {
		String vStrs[] = str.split("\t");
		this.timestamp=Long.parseLong(vStrs[0]);
		vector = new float[3];
		for (int i = 1; i <= 3; i++) {
			vector[i-1] = Float.parseFloat(vStrs[i]);
		}
	}

	public String toString() {
		return timestamp+"\t"+vector[0] + "\t" + vector[1] + "\t" + vector[2];
	}
	
	public void merge(FCMagneticVector mv)
	{
		for(int i=0;i<3;i++)
		{
			vector[i]=(mv.vector[i]+this.vector[i])/2;
		}
	}

	public double module() {
		double sum = 0;
		for (int i = 0; i < 3; i++) {
			sum += vector[i] * vector[i];
		}
		return Math.sqrt(sum);
	}

	public float[] getVector() {
		return vector;
	}

	public void setVector(float[] vector) {
		this.vector = vector;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}
