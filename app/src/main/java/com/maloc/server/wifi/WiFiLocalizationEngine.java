package com.maloc.server.wifi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.maloc.client.bean.RSSIData;
import com.maloc.client.util.GlobalProperties;
/**
 * WiFi RSSI指纹定位
 * @author xhw Email:xxyx66@126.com
 */
public class WiFiLocalizationEngine implements WiFiLocationQuerier{
	//WiFi指纹库生成器
	WiFiMapGenerator generator;
	
	public WiFiLocalizationEngine()
	{
		generator=new WiFiMapGenerator();
	}
	/**
	 * 构造方法
	 * @param generator，WiFi指纹生成器
	 */
	public WiFiLocalizationEngine(WiFiMapGenerator generator)
	{
		this.generator=generator;
	}
	
	public WiFiLocalizationEngine(String wifiData)
	{
		generator=new WiFiMapGenerator();
		this.addData(wifiData);
	}
	/**
	 * 导入WiFi指纹文件到内存
	 * @param wifiFile, wifi指纹文件路径
	 */
	public void addData(String wifiFile)
	{
		try {
			this.generator.load(wifiFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 获取要查找的rssi距离最近的k个WiFi热点
	 * @param bssidMap，wifi指纹库
	 * @param rssi, 要查找的rssi
	 * @param k
	 * @return wifi热点bssid数组
	 */
	private String[] getNearestWiFiBSSID(Map<String,Integer> rssi,int k)
	{
		String[] result=new String[k];
		int[] strength=new int[k];
		Arrays.fill(strength, -100);
		for(String id:rssi.keySet())
		{
			int v=rssi.get(id);
			int index=-1;
			int dis=0;
			for(int i=0;i<k;i++)
			{
				if(v>strength[i])
				{
					if(v-strength[i]>dis)
					{
						dis=v-strength[i];
						index=i;
					}
				}
			}
			if(index>-1)
			{
				result[index]=id;
				strength[index]=v;
			}
		}
		return result;
	}
	/**
	 * 为了避免在整个WiFi指纹库中搜索k最近邻，先根据要查找rssi指纹找到相关的WiFi指纹，只在这些相关的WiFi指纹中搜索。
	 * 相关的WiFi指纹是指包含在要查询rssi信号强度的k个WiFi热点的指纹
	 * @param bssidMap
	 * @param rd
	 * @return
	 */
	private List<RSSIData> getRelavantRSSIList(Map<String,Set<RSSIData>> bssidMap,RSSIData rd)
	{
		List<RSSIData> result=new LinkedList<RSSIData>();
		if(rd.rssi.size()<GlobalProperties.MINIMUM_RSSI_NUM)
		{
			return result;
		}
		
		int k=GlobalProperties.MINIMUM_RSSI_NUM;
		String[] knnId=getNearestWiFiBSSID(rd.getRssi(),k);
		Set<RSSIData> tmp=bssidMap.get(knnId[0]);
		List<Set<RSSIData>> list=new ArrayList<Set<RSSIData>>(k-1);
		
		for(int i=1;i<k;i++)
		{
			String bssid=knnId[i];
			Set<RSSIData> set=bssidMap.get(bssid);
			if(set!=null)
				list.add(set);
			
		}
		int start=0;
		if(tmp==null&&list.size()==0)
		{
			return result;
		}
		
		if(tmp==null&&list.size()!=0)
		{
			tmp=list.get(0);
			start=1;
		}
		
		for(RSSIData r:tmp)
		{
			int i;
			for(i=start;i<list.size();i++)
			{
				if(list.get(i)!=null&&!list.get(i).contains(r))
					break;
			}
			if(i==list.size())
				result.add(r);
		}
		return result;
	}
	
	@Override
	public WiFiPosition queryLocationByRSSI(RSSIData r) {

		//List<RSSIData> rssiList=this.generator.getRssi();
		List<RSSIData> rssiList=getRelavantRSSIList(this.generator.getBssidMap(),r);
		Set<String> keySet=r.rssi.keySet();
		
		//int minDis[]={Integer.MAX_VALUE,Integer.MAX_VALUE,Integer.MAX_VALUE};
		//RSSIData result[]=new RSSIData[minDis.length];
		WiFiPosition knn[]=new WiFiPosition[3];
		for(int i=0;i<knn.length;i++)
		{
			knn[i]=new WiFiPosition();
		}
		
		for(RSSIData rd:rssiList)
		{
			int sum=0;
			int cnt=0;
			for(String mac:keySet)
			{
				Integer value=rd.rssi.get(mac);
				if(value==null)
				{
					value=-100;
				}
				else
				{
					cnt++;
				}
				int rValue=r.rssi.get(mac);
				sum+=(value-rValue)*(value-rValue);
			}
			int index=-1;
			int dis=0;
			for(int i=0;i<knn.length;i++)
			{
				if(sum<knn[i].getDistance())
				{
					if(knn[i].getDistance()-sum>dis)
					{
						dis=knn[i].getDistance()-sum;
						index=i;
					}
				}
				
			}
			if(index>=0)
			{
				knn[index].setDistance(sum);
				knn[index].setSameBssidNum(cnt);
				knn[index].setPositions(rd.x, rd.y);
			}
			
		}
		
		return merge(knn,r.rssi.size());
	}

	private WiFiPosition merge(WiFiPosition[] knn,int totalRssiNum) {

		WiFiPosition pos=new WiFiPosition();
		float x=0,y=0;
		double sumW=0;
		int sumD=0;
		int minB=totalRssiNum;
		//sameBssidnum*sameBssidnum/distance为权重
		for(int i=0;i<knn.length;i++)
		{
			if(knn[i].getSameBssidNum()<GlobalProperties.MINIMUM_RSSI_NUM||knn[i].getDistance()==Integer.MAX_VALUE)
				continue;
			float p[]=knn[i].getPosition();
			
			//double dis=Math.sqrt(knn[i].getDistance());
			double weight=100.0/(knn[i].getDistance()+1);
			x+=p[0]*weight;
			y+=p[1]*weight;
			sumW+=weight;
			sumD+=knn[i].getDistance();
			if(minB>knn[i].getSameBssidNum())
			{
				minB=knn[i].getSameBssidNum();
			}
		}
		if(sumW==0)
		{
			pos.setSameBssidNum(0);
		}
		else
		{
			pos.setPositions((float)(x/sumW), (float)(y/sumW));
			pos.setDistance((int) Math.sqrt(sumD/knn.length));
			pos.setSameBssidNum(minB);
		}
		
		return pos;
	}

	public WiFiMapGenerator getGenerator() {
		return generator;
	}

	public void setGenerator(WiFiMapGenerator generator) {
		this.generator = generator;
	}

}
