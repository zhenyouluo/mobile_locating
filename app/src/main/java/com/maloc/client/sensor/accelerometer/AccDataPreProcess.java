package com.maloc.client.sensor.accelerometer;

import com.maloc.client.filter.LowPassFilter;
import com.maloc.client.filter.MeanFilter;
import com.maloc.client.util.GlobalProperties;
/**
 * 加速度数据预处理，先进行低通滤波，在进行均值滤波
 * @author xhw
 *
 */
public class AccDataPreProcess {

	private static float tmp[]=new float[GlobalProperties.CACHESIZE];
	private static float lastTmp[]=new float[100];
	public static float[] process(float data[],float dt, float RC,int windowSize)
	{
		LowPassFilter.filter(data, dt, RC, tmp);
		MeanFilter.filter(tmp, windowSize, data);
		return data;
	}
	
	/*public static float[] process(float data[],float pre[],int s,int t,float dt, float RC,int windowSize)
	{
		LowPassFilter.filter(data,s,t, dt, RC, tmp);
		MeanFilter.filter(tmp,pre, s,t,windowSize, data);
		return data;
	}*/
	/**
	 * 预处理
	 * @param data，原始加速度数据缓存数组
	 * @param s，预处理起始点
	 * @param t，预处理结束点
	 * @param dt，低通滤波参数
	 * @param RC，低通滤波参数
	 * @param windowSize，均值滤波窗口大小
	 * @param pre，均值滤波前一个窗口的数据
	 * @return
	 */
	public static float[] process(float data[],int s,int t,float dt, float RC,int windowSize,float pre[])
	{
		LowPassFilter.filter(data,s,t, dt, RC, tmp,pre[windowSize-2]);
		MeanFilter.filter(tmp, s,t,windowSize, data,pre);
		for(int i=0;i<windowSize-1;i++)
		{
			lastTmp[i]=tmp[t-(windowSize-1)+i];
		}
		/*StringBuilder sb=new StringBuilder();
		for(int i=s;i<t;i++)
		{
			sb.append(data[i]).append("\n");
		}
		String baseDir=Environment.getExternalStorageDirectory().getPath()+"/MaLoc/"+"acc.log";
		FileOperator.write(baseDir, sb.toString(),true);*/
		return lastTmp;
	}
	
}
