package com.maloc.client.util;


import Jama.Matrix;
/**
 * 坐标系变化
 * @author xhw Email:xxyx66@126.com
 */
public class CoordinateSystemRotation {
	
	public static void main(String args[])
	{
		float axis[]={1,1,1};
		float omegaMagnitude=10;
		float vector[]={1,2,3};
		Matrix m1=coorinateSystemRotate(axis,-(float)(Math.PI/4),vector);
		Matrix m2=coorinateSystemRotate(axis,(float)Math.PI/2,vector);
		
		float v1[]=new float[vector.length];
		for(int i=0;i<vector.length;i++)
		{
			v1[i]=(float)m1.getArray()[i][0];
		}
		
		Matrix m3=coorinateSystemRotate(axis,(float)Math.PI/4,v1);
		
		//Calibration.showMatrix(m2);
		System.out.println();
		//Calibration.showMatrix(m3);
	    
	}
	
	/**
	 * 围绕轴axis，根据右手法则，旋转omega度（pi表示法），在原坐标系内的向量vector，返回变换后坐标系vector的值。vector为三维向量。
	 * 
	 */
	public static Matrix coorinateSystemRotate(float axis[],float omega,float vector[])
	{
		double myRotationM[][]=getRotationMatrix(axis, omega);
	    Matrix mR=new Matrix(myRotationM);
	    Matrix inverMR=mR.inverse();
	    
	    Matrix gravM=new Matrix(vector.length,1);
	    for(int i=0;i<vector.length;i++)
	    	gravM.set(i, 0, vector[i]);
	    
	    Matrix gravEar=inverMR.times(gravM);
	    return gravEar;
	}
	/**
	 * 计算围绕向量v1转动angle角度的旋转矩阵
	 * @param v1
	 * @param angle
	 * @return
	 */
	public static double[][] getRotationMatrix(float v1[],float angle)
	{
		float cost =(float) Math.cos(angle);
		float v[] =normalise(v1);
		float sint =  (float) Math.sin(angle);
		float one_sub_cost = 1 - cost;
		double matrix[][]={{v[0]*v[0]*one_sub_cost+cost,v[0]*v[1]*one_sub_cost-v[2]*sint,v[0]*v[2]*one_sub_cost+v[1]*sint},
						  {v[0]*v[1]*one_sub_cost+v[2]*sint,v[1]*v[1]*one_sub_cost+cost,v[1]*v[2]*one_sub_cost-v[0]*sint},
						  {v[0]*v[2]*one_sub_cost-v[1]*sint,v[1]*v[2]*one_sub_cost+v[0]*sint,v[2]*v[2]*one_sub_cost+cost}};
		
		return matrix;
	}
	/**
	 * 归一化，计算单位向量
	 * @param v1
	 * @return
	 */
	private static float[] normalise(float v1[])
	{
		float v[]=new float[3];
		float sum=0;
		for(int i=0;i<v.length;i++)
		{
			v[i]=v1[i];
			sum+=v[i]*v[i];
		}
		double module=Math.sqrt(sum);
		for(int i=0;i<v.length;i++)
		{
			v[i]/=module;
		}
		return v;
	}
}

