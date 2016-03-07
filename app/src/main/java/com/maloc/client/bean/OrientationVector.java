package com.maloc.client.bean;
/**
 * 朝向向量
 * @author xhw
 *
 */
public class OrientationVector {

	float vector[];
	long timestamp;
	
	public OrientationVector(float x,float y,float z,long timestamp) {
		this.vector = new float[3];
		vector[0]=x;
		vector[1]=y;
		vector[2]=z;
		this.timestamp=timestamp;
	}

	public OrientationVector(float vector[],long timestamp)
	{
		this.vector = new float[3];
		for(int i=0;i<3;i++)
			this.vector[i]=vector[i];
		this.timestamp=timestamp;
	}
	
	public String toString() {
		return vector[0] + "\t" + vector[1] + "\t" + vector[2]+"\t"+timestamp;
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
