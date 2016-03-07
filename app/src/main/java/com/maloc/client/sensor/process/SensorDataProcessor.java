package com.maloc.client.sensor.process;

import java.util.List;

import android.os.Environment;
import android.util.Log;

import com.maloc.client.bean.MagneticVector;
import com.maloc.client.localization.LocalizationInfo;
import com.maloc.client.sensor.accelerometer.AccDataPreProcess;
import com.maloc.client.sensor.accelerometer.InflectionPoint;
import com.maloc.client.sensor.accelerometer.Pedometer;
import com.maloc.client.sensor.gyroscope.GyroscopeDataPaser;
import com.maloc.client.sensor.magnetometer.UserMagneticDataPaser;
import com.maloc.client.util.FileLog;
import com.maloc.client.util.FileOperator;
import com.maloc.client.util.GlobalProperties;
/**
 * 传感器数据处理类，当SensorStream收集一个窗口数据后，提交到这里进行处理。
 * 首先，对加速度数据进行计步，然后根据计步信息查找每步（时间上）对应的朝向和磁场强度。
 * @author xhw
 *
 */
public class SensorDataProcessor {

	/*private  List<InflectionPoint> accList=null;
	private  List<Float> gyroValue=null;
	private  List<MagneticVector> magList=null;*/
	private LocalizationInfo info;
	private  Pedometer pedometer=new Pedometer();
	private  float pre[]=null;
	private InflectionPoint prePeak=null;
	
	int stepCount=0;
	
	public LocalizationInfo process(float acc[],long accTimestamp[],int s,int t,float magnetic[][],long magneticTimestamp[],
			float rawGyroValue[],long gyroTimestamp[])
	{
		info=new LocalizationInfo();
		if(pre==null)
		{
			pre=new float[GlobalProperties.meanFilterWindowSize-1];
			for(int i=0;i<GlobalProperties.meanFilterWindowSize-1;i++)
			{
				pre[i]=acc[i];
			}
		}
		pre=AccDataPreProcess.process(acc,s,t,GlobalProperties.dt, 
				GlobalProperties.RC, GlobalProperties.meanFilterWindowSize,pre);
		/*
		StringBuilder sb=new StringBuilder();
		for(int i=s;i<t;i++)
		{
			sb.append(acc[i]).append("\t").append(accTimestamp[i]).append("\n");
		}
		FileLog.dataLog("accAll.txt", sb.toString());
		*/
		
		info.setAccList(pedometer.listPeakPoints(prePeak,acc, accTimestamp,s,t, 
				GlobalProperties.thresholdAcc[1],GlobalProperties.thresholdAcc[0],
				GlobalProperties.thresholdTime[1] , GlobalProperties.thresholdTime[0]));
		
		
		if(info.getAccList()==null||info.getAccList().size()<1)
		{
			
			return null;
		}
		prePeak=info.getAccList().get(info.getAccList().size()-1);
		stepCount+=info.getAccList().size();
		Log.i("SensorDataProcess stepCounter", stepCount+"");
		/*sb=new StringBuilder();
		for(InflectionPoint f:info.getAccList())
		{
			sb.append(f).append("\n");
		}
		FileOperator.write(Environment.getExternalStorageDirectory().getPath()
				+ "/"+GlobalProperties.FINGERPRINTS_BASE_DIR+"/"+GlobalProperties.SCENE_NAME
				+"/"+GlobalProperties.FLOOR+"/acc.txt", sb.toString(),true);
		*/
		info.setGyroValue(GyroscopeDataPaser.parse(rawGyroValue, gyroTimestamp, info.getAccList()));
		
		/*sb=new StringBuilder();
		for(float f:info.getGyroValue())
		{
			sb.append(f).append("\n");
		}
		FileOperator.write(Environment.getExternalStorageDirectory().getPath()
				+ "/"+GlobalProperties.FINGERPRINTS_BASE_DIR+"/"+GlobalProperties.SCENE_NAME
				+"/"+GlobalProperties.FLOOR+"/gyro.txt", sb.toString(),true);
		*/
		info.setMagList(UserMagneticDataPaser.load(info.getAccList(), magnetic, magneticTimestamp));
		/*
		sb=new StringBuilder();
		for(MagneticVector f:info.getMagList())
		{
			sb.append(f).append("\n");
		}
		FileOperator.write(Environment.getExternalStorageDirectory().getPath()
				+ "/"+GlobalProperties.FINGERPRINTS_BASE_DIR+"/"+GlobalProperties.SCENE_NAME
				+"/"+GlobalProperties.FLOOR+"/mag.txt", sb.toString(),true);
		*/
		return info;
	}
	
}
