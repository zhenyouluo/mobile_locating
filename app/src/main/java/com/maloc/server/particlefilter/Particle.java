package com.maloc.server.particlefilter;

import com.maloc.client.bean.MagneticVector;


/**
 * 粒子实体，粒子滤波算法的基本单位
 * @author xhw Email:xxyx66@126.com
 */
public class Particle implements Comparable<Particle>{

	public static final int SUCCESS=0;
	public static final int NOT_INIT=1;
	
	float x;
	float y;
	float theta;
	double weight=0;
	//MagneticVector magVector;
	//int status=0;
	public Particle()
	{
		
	}
	
	/*public Particle(int status)
	{
		this.status=status;
	}*/
	
	public Particle(float x, float y, float theta) {
		super();
		this.x = x;
		this.y = y;
		this.theta = theta;
		this.weight=0;
	}
	
	public void copy(Particle p)
	{
		x=p.x;
		y=p.y;
		theta=p.theta;
		weight=p.weight;
	}
	
	public String toString()
	{
		return  x+"\t"+y+"\t"+theta+"\t"+weight;
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

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	@Override
	public int compareTo(Particle o) {

		if(o.weight==this.weight)
			return 0;
		else if(this.weight<o.weight)
			return -1;
		else
			return 1;
	}
	
	public double distance(Particle p)
	{
		return Math.sqrt((p.x-x)*(p.x-x)+(p.y-y)*(p.y-y));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime + (int)(x*2);
		result = prime * result + (int)(y*2);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Particle other = (Particle) obj;
		if ((int)(x*2)!=(int)(other.x*2))
			return false;
		if ((int)(y*2)!=(int)(other.y*2))
			return false;
		return true;
	}
/*
	public MagneticVector getMagVector() {
		return magVector;
	}

	public void setMagVector(MagneticVector magVector) {
		this.magVector = magVector;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	*/
}
