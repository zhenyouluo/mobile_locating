package com.maloc.client.localizationService;

import java.io.Serializable;
/**
 * 定位结果
 * @author xhw
 *
 */
public class Position implements Serializable{

	private static final long serialVersionUID = 886545592566451719L;

	private float x,y,theta,range;
	private PositionType type;
	/**
	 * general constructor
	 * @param x, localization position x coordinate
	 * @param y, localization position y coordinate
	 * @param theta, user walking direction
	 * @param range, localization precision
	 * @param type, localization result type
	 */
	public Position(float x,float y,float theta,float range,PositionType type)
	{
		this.x=x;
		this.y=y;
		this.theta=theta;
		this.range=range;
		this.type=type;
	}
	/**
	 * wifi localization result
	 * @param x, localization position x coordinate
	 * @param y, localization position y coordinate
	 * @param theta, user walking direction and phone orientation
	 * @param range, localization precision
	 */
	public Position(float x,float y,float range)
	{
		this(x,y,0,range,PositionType.WIFI);
	}
	/**
	 * magnetic localization result
	 * @param x,localization position x coordinate
	 * @param y, localization position y coordinate
	 * @param walkDirection, user walking direction
	 * @param phoneOrientation, phone orientation
	 * @param range, localization precision
	 */
	public Position(float x,float y,float theta,float range)
	{
		this(x,y,theta,range,PositionType.MAGNETIC);
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
	public float getRange() {
		return range;
	}
	public void setRange(float range) {
		this.range = range;
	}
	public PositionType getType() {
		return type;
	}
	public void setType(PositionType type) {
		this.type = type;
	}
	
	
	
	
}
