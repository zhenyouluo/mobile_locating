package com.maloc.client.bean;
/**
 * 粒子
 * @author xhw
 *
 */
public class Particle implements Comparable<Particle>{

	float x;
	float y;
	float theta;
	double weight=0;
	
	public Particle()
	{
		
	}
	
	public Particle(float x, float y, float theta) {
		super();
		this.x = x;
		this.y = y;
		this.theta = theta;
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
	
}
