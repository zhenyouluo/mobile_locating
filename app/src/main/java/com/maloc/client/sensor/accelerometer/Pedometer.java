package com.maloc.client.sensor.accelerometer;

import java.util.*;

import com.maloc.client.util.GlobalProperties;

import android.util.Log;
/**
 * 计步器
 * @author xhw
 *
 */
public class Pedometer {
	
	private static final List<InflectionPoint> nullList=new LinkedList<InflectionPoint>();
	private List<InflectionPoint> preList=nullList;
	
	/**
	 * 查找加速度峰值
	 * @param data
	 * @param timestamp
	 * @param s
	 * @param t
	 * @return
	 */
	public List<InflectionPoint> listPeakPoints(float data[], long timestamp[],int s,int t)
	{
		return listPeakPoints(null,data,timestamp,s,t,GlobalProperties.thresholdAcc[1], GlobalProperties.thresholdAcc[0], 
				GlobalProperties.thresholdTime[1], GlobalProperties.thresholdTime[0]);
	}
	/**
	 * 查找加速度峰值
	 * @param data
	 * @param timestamp
	 * @param vUp
	 * @param vLow
	 * @param tUp
	 * @param tLow
	 * @return
	 */
	public List<InflectionPoint> listPeakPoints(float data[], long timestamp[],float vUp,float vLow,int tUp,int tLow)
	{
		List<InflectionPoint> inflectionList=listInflectionPoint(data,timestamp,vUp,vLow,tUp,tLow);
		if(inflectionList.size()<1)
		{
			Log.e("inflection size", "0");
			return null;
		}
		List<InflectionPoint> peakList=new ArrayList<InflectionPoint>();
		boolean flag=false;
		for (int i=0;i<inflectionList.size();i++)
		{
			InflectionPoint tempPoint=inflectionList.get(i);
			if(tempPoint.flag==flag)
			{
				peakList.add(tempPoint);
			}
		}
		
		return peakList;
	}
	
	/**
	 * 查找峰值点
	 * @param pre，前一次查找得到峰值
	 * @param data，加速度数据缓存数组
	 * @param timestamp，加速度数据对应的时间戳
	 * @param s，起始位置
	 * @param t，终止位置
	 * @param vUp，峰值上限
	 * @param vLow，峰值下限
	 * @param tUp，两个峰值之间时间间距上限
	 * @param tLow，两个峰值之间时间间距下限
	 * @return
	 */
	public List<InflectionPoint> listPeakPoints(InflectionPoint pre,float data[], long timestamp[],int s,int t,float vUp,float vLow,int tUp,int tLow)
	{
		List<InflectionPoint> inflectionList=listInflectionPoint(data,timestamp,s,t);
		if(inflectionList==null||inflectionList.size()<1)
			return inflectionList;
		int last=inflectionList.size()-1;
		for(;last>=0;last--)
		{
			if(inflectionList.get(last).flag==true)
			{
				break;
			}
		}
		if(last==0)
			return new LinkedList<InflectionPoint>();
		
		List<InflectionPoint> result=new LinkedList<InflectionPoint>();
		for(int i=1;i<last;i++)
		{
			if(inflectionList.get(i).value<vUp&&inflectionList.get(i).value>vLow)
			{
				result.add(inflectionList.get(i));
			}
		}
		for(int i=1;i<result.size();i++)
		{
			if(result.get(i).flag==true)
			{
				result.get(i).flag=false;
				if(i<result.size()-1)
					result.remove(i+1);
				result.remove(i-1);
				i--;
				
			}
			/*if(i>=result.size())
			{
				break;
			}*/
			if(i<=0)
				continue;
			long td=result.get(i).timestamp-result.get(i-1).timestamp;
			if(td<tLow)
			{
				result.remove(i);
			}
			else if(td>tUp)
			{
				result.remove(i-1);
			}
		}
		
		if(pre!=null&&result.size()>0)
		{
			long td=result.get(0).timestamp-pre.timestamp;
			if(td<tLow)
			{
				result.remove(0);
			}
		}
		
		return result;
	}
	/*public List<InflectionPoint> listPeakPoints(float data[], long timestamp[],int s,int t,float vUp,float vLow,int tUp,int tLow)
	{
		//Log.i("lastAcc,accCnt", s+","+t);
		List<InflectionPoint> inflectionList=listInflectionPoint(data,timestamp,0,t,vUp,vLow,tUp,tLow);
		if(s==0)
			preList=nullList;
		else
			preList=listInflectionPoint(data,timestamp,0,s,vUp,vLow,tUp,tLow);
		if(inflectionList.size()<=0||(inflectionList.size()-preList.size()<1&&s>0))
		{
			Log.e("inflection size", "0");
			return null;
		}
		//Log.i("preList,curList", preList+";"+inflectionList);
		List<InflectionPoint> peakList=new LinkedList<InflectionPoint>();
		boolean flag=false;
		int start=preList.size()-1;
		if(start<0||s==0)
			start=0;
		for(int i=start;i<inflectionList.size()-1;i++)
		{
			if(inflectionList.get(i).flag==flag)
			{
				peakList.add(inflectionList.get(i));
			}
		}
		//前后两端相接处，如果前一段最后一个也是峰点，值大于当前段的第一个峰点，并且中间没有谷点
		int preListSize=preList.size();
		if(preListSize>0&&preList.get(preListSize-1).flag==flag&&inflectionList.get(0).flag==flag
				&&preList.get(preListSize-1).value>peakList.get(0).value)
		{
			peakList.set(0, preList.get(preListSize-1));
		}
		if(s==0&&preListSize>0&&inflectionList.size()>0)
		{
			
			if(preList.get(preListSize-1).flag!=flag&&inflectionList.get(0).flag==flag)
			{
					peakList.remove(0);
				
			}
		}
		preList=inflectionList;
		
		return peakList;
	}
	*/
	
	public List<InflectionPoint> listPeakAndValleyPoints(float data[], long timestamp[],int s,int t,float vUp,float vLow,int tUp,int tLow)
	{
		//Log.i("lastAcc,accCnt", s+","+t);
		List<InflectionPoint> inflectionList=listInflectionPoint(data,timestamp,0,t,vUp,vLow,tUp,tLow);
		/*if(s==0)
			preList=nullList;
		else
			preList=listInflectionPoint(data,timestamp,0,s,vUp,vLow,tUp,tLow);*/
		if(inflectionList.size()-preList.size()<1&&s>0)
		{
			Log.e("inflection size", "0");
			return null;
		}
		//Log.i("preList,curList", preList+";"+inflectionList);
		List<InflectionPoint> peakList=new LinkedList<InflectionPoint>();
		boolean flag=false;
		int start=preList.size()-1;
		if(start<0||s==0)
			start=0;
		for(int i=start;i<inflectionList.size()-1;i++)
		{
			
			peakList.add(inflectionList.get(i));
			
		}
		//前后两端相接处，如果前一段最后一个也是峰点，值大于当前段的第一个峰点，并且中间没有谷点
		int preListSize=preList.size();
		if(s==0&&preListSize>0)
		{
			
			if(preList.get(preListSize-1).flag!=inflectionList.get(0).flag)
			{
					peakList.remove(0);
				
			}
		}
		
		
		preList=inflectionList;
		
		return peakList;
	}
	
	
	/**
	 * 峰点为false，谷点为true
	 * @param filename
	 * @param type
	 * @return
	 */
	public List<InflectionPoint> listInflectionPoint(float data[],long timestamp[],int s,int t)
	{
		List<InflectionPoint> pointList=new LinkedList<InflectionPoint>();
		boolean flag=true;
		for(int i=s+1;i<t-1;i++)
		{
			float temp=data[i]-data[i-1];
			if(temp>0)
			{
				if(flag==false||i==s+1)
				{
					InflectionPoint ip=new InflectionPoint(timestamp[i-1],true,data[i-1]);
					pointList.add(ip);
					flag=true;
					
				}
				
			}
			else if(temp<0)
			{
				if(flag==true||i==s+1)
				{
					InflectionPoint ip=new InflectionPoint(timestamp[i-1],false,data[i-1]);
					pointList.add(ip);
					flag=false;
					
				}
				
			}
			
		}
		if(t>0)
		{
			InflectionPoint ip=new InflectionPoint(timestamp[t-1],!flag,data[t-1]);
			pointList.add(ip);
		}
		
		
		return pointList;
	}
	
	/**
	 * 峰点为false，谷点为true
	 * @param filename
	 * @param type
	 * @return
	 */
	public List<InflectionPoint> listInflectionPoint(float data[],long timestamp[])
	{
		
		List<InflectionPoint> pointList=new LinkedList<InflectionPoint>();
		boolean flag=true;
		//float previous=data[0];
		for(int i=1;i<data.length;i++)
		{
			float temp=data[i]-data[i-1];
			if(temp>0)
			{
				if(flag==false||i==1)
				{
					InflectionPoint ip=new InflectionPoint(timestamp[i-1],true,data[i-1]);
					pointList.add(ip);
					flag=true;
					
				}
				//previous=data[i];
				
			}
			else if(temp<0)
			{
				if(flag==true||i==1)
				{
					InflectionPoint ip=new InflectionPoint(timestamp[i-1],false,data[i-1]);
					pointList.add(ip);
					flag=false;
					
				}
				//previous=data[i];
				
			}
			
		}
		
		InflectionPoint ip=new InflectionPoint(timestamp[timestamp.length-1],!flag,data[data.length-1]);
		pointList.add(ip);
		
		return pointList;
	}
	
	public List<InflectionPoint> listInflectionPoint(float data[],long timestamp[],float vUp,float vLow,int tUp,int tLow)
	{
		return filter(listInflectionPoint(data,timestamp),vUp,vLow,tUp,tLow);
	}
	
	public List<InflectionPoint> listInflectionPoint(float data[],long timestamp[],int s,int t,float vUp,float vLow,int tUp,int tLow)
	{
		return filter(listInflectionPoint(data,timestamp,s,t),vUp,vLow,tUp,tLow);
	}
	
	private List<InflectionPoint> filter(List<InflectionPoint> list,float vUp,float vLow,int tUp,int tLow)
	{
		List<InflectionPoint> result=new LinkedList<InflectionPoint>();
		for(int i=1;i<list.size();i++)
		{
			float vBias=0;
			int tBias=0;
			if(result.size()==0)
			{
				vBias=Math.abs(list.get(i).value-list.get(i-1).value);
				tBias=(int) (list.get(i).timestamp-list.get(i-1).timestamp);
			}
			else
			{
				vBias=Math.abs(list.get(i).value-result.get(result.size()-1).value);
				tBias=(int) (list.get(i).timestamp-result.get(result.size()-1).timestamp);
			}
			if(vBias<=vUp&&vBias>=vLow&&tBias<=tUp&&tBias>=tLow)
			{
				//System.out.println(i);
				if(result.size()==0)
				{
					//System.out.println(i-1+":"+list.get(i-1).value);
					result.add(list.get(i-1));
				}
				/*if(list.get(i).flag==result.get(result.size()-1).flag)
				{
					result.remove(result.size()-1);
				}*/
				if(list.get(i).flag!=result.get(result.size()-1).flag)
				{
					result.add(list.get(i));
				}
				
				
			}
			else if(tBias>tUp)
			{
				vBias=list.get(i).value-list.get(i-1).value;
				tBias=(int) (list.get(i).timestamp-list.get(i-1).timestamp);
				if(vBias<=vUp&&vBias>=vLow&&tBias<=tUp&&tBias>=tLow)
				{
					result.add(list.get(i));
				}
			}
			else if(result.size()>0)
			{
				if(list.get(i).flag==result.get(result.size()-1).flag)
				{
					if(list.get(i).flag==true&&list.get(i).value<result.get(result.size()-1).value)
					{
						result.set(result.size()-1, list.get(i));
					}
					else if(list.get(i).flag==false&&list.get(i).value>result.get(result.size()-1).value)
					{
						result.set(result.size()-1, list.get(i));
					}
				}
			}
		}
		
		list=null;
		return result;
		
	}

	

}
