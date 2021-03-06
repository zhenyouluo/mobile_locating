package com.maloc.client.util;
/**
 * 卡尔曼滤波算法，用于融合陀螺仪和罗盘，提高对朝向变换测量的精度。
 * @author xhw
 *
 */
public class KalmanFilter {
	
	private float predictDirection=Float.MAX_VALUE;
	private double predictCov=0.5;
	private double gyro_cov=0.3f;
	private double compass_cov=0.5f;
	public static double GYRO_MAX=0.5;
	public static double COMPASS_MAX=3.14f;
	public static double ORIENTATION_PERIOD=Math.PI*2;
	public static double GYRO_RATE_LOW=3;//3 rad/s
	public static double GYRO_RATE_MEDIUM=6;// 6 rad/s
	public static double GYRO_NOISE=50;
	public static double COMPASS_COV_NORMAL=0.5;
	public static double COMPASS_COV_HUGE=1000;
	
	public void init(float obv,double cov)
	{
		predictDirection=obv;
		predictCov=cov;
	}
	
	public float filt(double gyro,float compass,float dt)
	{
		dynamicCov(gyro/dt);
		predictDirection+=gyro;
		predictCov+=gyro_cov;
		double k=predictCov/(predictCov+compass_cov);
		double obv_diff=compass-predictDirection;
		while(Math.abs(obv_diff)>COMPASS_MAX)
		{
			if(obv_diff>0)
				obv_diff-=ORIENTATION_PERIOD;
			else
				obv_diff+=ORIENTATION_PERIOD;
		}
		predictDirection=(float) (predictDirection+k*obv_diff);
		predictCov=(1-k)*predictCov;
		return predictDirection;
	}
	
	private void dynamicCov(double gyro) {
		
		gyro=Math.abs(gyro);
		if(gyro<GYRO_RATE_LOW)
		{
			this.compass_cov=COMPASS_COV_HUGE;
			this.gyro_cov=gyro;
		}
		else if(gyro<GYRO_RATE_MEDIUM)
		{
			this.compass_cov=COMPASS_COV_NORMAL;
			this.gyro_cov=1+gyro*GYRO_NOISE;
		}
		else
		{
			this.compass_cov=COMPASS_COV_NORMAL;
			this.gyro_cov=1+gyro*GYRO_NOISE*10;
		}
		
	}
}
