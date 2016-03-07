package com.maloc.client.filter;

/**
 * 均值滤波
 * @author xhw
 *
 */
public class MeanFilter {
	
	public static void filter(float raw[],int n,float y[])
	{
		float sum=0;
		for(int i=0;i<n-1;i++)
		{
			y[i]=raw[i];
			sum+=raw[i];
		}
		for(int i=n-1;i<raw.length;i++)
		{
			sum+=raw[i];
			y[i]=sum/n;
			sum-=raw[i-n+1];
		}
	}
	
	/*public static void filter(float raw[],int s,int t,int n,float y[])
	{
		
		float sum=0;
		int start=0;
		if(s==0)
		{
			for(int i=0;i<n-1;i++)
			{
				y[i]=raw[i];
				sum+=raw[0];
			}
			start=n-1;
		}
		else
		{
			for(int i=1;i<n;i++)
			{
				//y[i]=raw[i];
				sum+=raw[s-i];
			}
			start=s;
		}
		
		for(int i=start;i<t;i++)
		{
			sum+=raw[i];
			y[i]=sum/n;
			sum-=raw[i-n+1];
		}
	}*/
	/**
	 * 均值过滤
	 * @param raw，原始数据
	 * @param s，滤波开始位置
	 * @param t，滤波结束位置
	 * @param n，窗口大小
	 * @param y，输出结果
	 * @param pre，开始位置前n-1个数据
	 */
	public static void filter(float raw[],int s,int t,int n,float y[],float pre[])
	{
		
		float sum=0;
		
		for(int i=0;i<n-1;i++)
		{
				
			sum+=pre[i];
		}
		
		for(int i=s;i<t;i++)
		{
			sum+=raw[i];
			y[i]=sum/n;
			if(i>=n-1)
				sum-=raw[i-n+1];
			else
			{
				sum-=pre[i];
			}
		}
	}
}
