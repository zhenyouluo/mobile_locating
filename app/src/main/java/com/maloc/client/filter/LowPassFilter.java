package com.maloc.client.filter;
/**
 * 低通滤波
 * @author xhw
 *
 */
public class LowPassFilter {
	
	public static float[] filter(float raw[],float dt,float RC,float y[])
	{
		float a=dt/(dt+RC);
		y[0]=raw[0];
		for(int i=1;i<raw.length;i++)
		{
			y[i]=y[i-1]+a*(raw[i]-y[i-1]);
		}
		return y;
	}
	/**
	 * 低通过滤
	 * @param raw,原始数据
	 * @param s,滤波开始位置
	 * @param t，滤波结束位置
	 * @param dt，过滤参数
	 * @param RC，过滤参数
	 * @param y，结果输出
	 * @param pre，开始s的前一个数据
	 * @return
	 */
	public static float[] filter(float raw[],int s,int t,float dt,float RC,float y[],float pre)
	{
		float a=dt/(dt+RC);
		
		if(s==0)
		{
			y[s]=pre+a*(raw[s]-pre);
			for(int i=s+1;i<t;i++)
			{
				y[i]=y[i-1]+a*(raw[i]-y[i-1]);
			}
		}
		else
		{
			for(int i=s;i<t;i++)
			{
				y[i]=y[i-1]+a*(raw[i]-y[i-1]);
			}
		}
		
		return y;
	}

}
