package com.maloc.client.sensor.gyroscope;

import java.util.*;
import java.io.*;

import com.maloc.client.sensor.accelerometer.InflectionPoint;
import com.maloc.client.util.GlobalProperties;

/**
 * 提取用户在场景内行走时记录的Gyroscope的数据，提供查询接口，为定位工作提供人的转向信息。
 * @author XHW
 *
 */
public class GyroscopeDataPaser {
	
	
	public static List<Float> parse(float gyro[],long timestamp[],List<InflectionPoint> inflectionPoiontList)
	{
		List<Float> gyroValue=new LinkedList<Float>();
		//float[] gyroValue=new float[inflectionPoiontList.size()];
		inflectionPoiontList.add(new InflectionPoint(Long.MAX_VALUE,false,0));
		int index=0;
		for(int i=0;i<gyro.length;i++)
		{
			if(index>=inflectionPoiontList.size()-1)
				break;
			if(timestamp[i]>=inflectionPoiontList.get(index).getTimestamp()&&timestamp[i]<=inflectionPoiontList.get(index+1).getTimestamp())
			{
				gyroValue.add(gyro[i]);
				index++;
			}
		}
		inflectionPoiontList.remove(inflectionPoiontList.size()-1);
		/*if(gyroValue.size()!=inflectionPoiontList.size())
			throw new RuntimeException();*/
		return gyroValue;
	}
	
	
}
