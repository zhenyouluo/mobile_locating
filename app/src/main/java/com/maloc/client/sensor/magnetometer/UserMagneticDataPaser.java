package com.maloc.client.sensor.magnetometer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.util.Log;

import com.maloc.client.bean.MagneticVector;
import com.maloc.client.sensor.accelerometer.InflectionPoint;
/**
 * 计步后得到峰值列表，查找每个峰值，即每步，对应的磁场强度
 * @author xhw
 *
 */
public class UserMagneticDataPaser {

	public static List<MagneticVector> load(List<InflectionPoint> list,float magnetic[][],long timestamp[])
	{
		List<MagneticVector> magList=new LinkedList<MagneticVector>();
		list.add(new InflectionPoint(Long.MAX_VALUE,false,0));
		int index=0;
		for(int i=1;i<magnetic.length;i++)
		{
			if(index>=list.size()-1)
				break;
			
			if(timestamp[i]>=list.get(index).getTimestamp()&&timestamp[i]<=list.get(index+1).getTimestamp())
			{
				
				MagneticVector mv=new MagneticVector(magnetic[i],timestamp[i]);
				magList.add(mv);
				index++;
			}
		}
		list.remove(list.size()-1);
		//Log.i("magList", magList.size()+"\t"+list.size());
		/*if(magList.size()!=list.size())
			throw new RuntimeException();*/
		return magList;
	}
}
