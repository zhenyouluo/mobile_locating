package com.maloc.client.bean;
/**
 * 朝向信息
 * @author xhw
 *
 */
public class DirectionVector {

	public AngularData angularData;
	public float orientationOfKF,orientation;
	public long timstamp;
	
	public DirectionVector(AngularData angularData,  float orientation,float orientationOfKF,long timestamp)
	{
		this.angularData=angularData;
		this.orientationOfKF=orientationOfKF;
		this.orientation=orientation;
		this.timstamp=timestamp;
	}
}
