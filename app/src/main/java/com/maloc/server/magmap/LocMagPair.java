package com.maloc.server.magmap;

import com.maloc.client.bean.MagneticVector;
/**
 * 存储位置和磁场强度的实体，地磁指纹库的基本单位
 * @author xhw Email:xxyx66@126.com
 */
public class LocMagPair {

	float x;
	float y;
	MagneticVector vector;
	
	public LocMagPair(float x, float y, MagneticVector vector) {
		super();
		this.x = x;
		this.y = y;
		this.vector = vector;
	}
	
	
	public float distance(float x1,float y1)
	{
		return (x-x1)*(x-x1)+(y-y1)*(y-y1);
	}
	
	public String toString()
	{
		return x+"\t"+y+"\t"+vector.toString();
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

	public MagneticVector getVector() {
		return vector;
	}

	public void setVector(MagneticVector vector) {
		this.vector = vector;
	}
	
	
	
	
}
