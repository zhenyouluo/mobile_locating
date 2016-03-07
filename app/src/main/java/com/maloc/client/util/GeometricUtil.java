package com.maloc.client.util;

//import org.junit.Test;

import android.graphics.Matrix;
/**
 * 几何工具类
 * @author xhw Email:xxyx66@126.com
 */
public class GeometricUtil {

	private static float[] corssVector = new float[3];
	
	private static float[] srcC={0,0},src={0,1},dstC={0,0},dst={0,0};

	
	private static float[] orignVector=new float[3];
	private static float[] curVector=new float[3];
	/**
	 * 提交矩阵的缩放比例
	 * @param matrix
	 * @return
	 */
	public static double extractScale(Matrix matrix)
	{
		matrix.mapPoints(dstC, srcC);
		matrix.mapPoints(dst,src);
		double sum=0;
		for(int i=0;i<2;i++)
		{
			sum+=(dstC[i]-dst[i])*(dstC[i]-dst[i]);
		}
		return Math.sqrt(sum);
	}
	/**
	 * 提取矩阵的旋转角度
	 * @param matrix
	 * @return
	 */
	public static double extractRotate(Matrix matrix)
	{
		matrix.mapPoints(dstC, srcC);
		matrix.mapPoints(dst,src);
		for(int i=0;i<2;i++)
		{
			orignVector[i]=srcC[i]-src[i];
		}
		for(int i=0;i<2;i++)
		{
			curVector[i]=dstC[i]-dst[i];
			
		}
		orignVector[2]=0;
		curVector[2]=0;
		double d=intersectionAngle(orignVector, curVector);
		if(Math.abs(d)>0.000000001)
		{
			return d;
		}
		else
			return 0;
	}
	
	// 求两个向量的夹角，单位是0.0-pi
	public static double intersectionAngle(float v1[], float v2[]) {
		double innerCross = 0;
		double sum1 = 0, sum2 = 0;
		for (int i = 0; i < v1.length; i++) {
			innerCross += v1[i] * v2[i];
			sum1 += v1[i] * v1[i];
			sum2 += v2[i] * v2[i];
		}

		double cos = innerCross / (Math.sqrt(sum1) * Math.sqrt(sum2));
		float[] cross = cross(v1, v2);
		if (cross[2] >= 0)
			return Math.acos(cos);
		else
			return -Math.acos(cos);
	}

	// 求两个向量的叉乘
	public static float[] cross(float[] v1, float[] v2) {
		if (v1.length != 3 || v2.length != 3)
			return null;

		corssVector[0] = v2[2] * v1[1] - v2[1] * v1[2];
		corssVector[1] = -(v2[2] * v1[0] - v2[0] * v1[2]);
		corssVector[2] = v2[1] * v1[0] - v2[0] * v1[1];

		return corssVector;
	}
/*
	@Test
	public void test()
	{
		double d=GeometricUtil.extractRotate(new Matrix());
		System.out.println(d);
	}
	*/
}
