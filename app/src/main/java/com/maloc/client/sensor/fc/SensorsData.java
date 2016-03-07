package com.maloc.client.sensor.fc;

import java.io.File;
import java.util.*;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.maloc.client.bean.AccelerationVector;
import com.maloc.client.bean.DirectionVector;
import com.maloc.client.bean.FCMagneticVector;
import com.maloc.client.bean.RSSIData;
import com.maloc.client.fc.graphics.Line;
import com.maloc.client.util.FileOperator;
import com.maloc.client.util.GlobalProperties;
/**
 * 线段上的指纹数据存储单元
 * @author xhw Email:xxyx66@126.com
 */
public class SensorsData{

	public List<AccelerationVector> accelerationList=new ArrayList<AccelerationVector>();
	public List<FCMagneticVector> magneticList=new ArrayList<FCMagneticVector>();
	public List<FCMagneticVector> geoMagneticList=new ArrayList<FCMagneticVector>();
	public List<DirectionVector> directionList=new ArrayList<DirectionVector>();
	public List<RSSIData> rssiList=new ArrayList<RSSIData>();
	
	private Line line;
	private String address;
	
	//private ProgressDialog pd;  
	//private Context context;
	public SensorsData(Line line)
	{
		//this.context=context;
		this.line=line;
		String phaseName= line.isMoveForwar()? "collect":"validate";
		String baseDir = Environment.getExternalStorageDirectory().getPath()
				+ "/"+GlobalProperties.FINGERPRINTS_BASE_DIR+"/";
		address=baseDir+"/"+GlobalProperties.currentFloor.getFloorPath()
				+line.toFileName()+"/"+phaseName+"/";
		File dir=new File(address);
		if(!dir.exists())
			dir.mkdirs();
		
	}
	
	
	/*
	@Override
	public void run() {

		
		handler.sendEmptyMessage(0);
		//this.notifyAll();
	}
	*/
	public void save()
	{
		//saveAccelerationData("acceleration.txt",this.accelerationList);
				//saveMagneticData("magnetic.txt",this.magneticList);
				saveMagneticData("magnetic_geo.txt",this.geoMagneticList);
				//saveDirectionData();
				saveRSSIData("wifi.txt",this.rssiList);
		// pd = ProgressDialog.show(context, "Saving Fingerpints", "Wait……");  
		//new Thread(this).start();
	}
/*
	Handler handler = new Handler() {  
        @Override  
        public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法  
            pd.dismiss();// 关闭ProgressDialog  
        }  
    };  
	*/
	private void saveRSSIData(String filename,List<RSSIData> rssiList) {

		StringBuilder sb=new StringBuilder();
		for(RSSIData rd:rssiList)
		{
			sb.append(rd.toString());
			sb.append("\n");
		}
		FileOperator.write(address+filename, sb.toString());
	}

	private void saveDirectionData(String filename) {

		
	}

	private void saveMagneticData(String filename,List<FCMagneticVector> list) {

		Log.i("SensorData","save: "+filename);
		StringBuilder sb=new StringBuilder();
		for(FCMagneticVector mv:list)
		{
			sb.append(mv.toString());
			sb.append("\n");
		}
		FileOperator.write(address+filename, sb.toString());
	}

	private void saveAccelerationData(String filename,List<AccelerationVector> list) {

		StringBuilder sb=new StringBuilder();
		for(AccelerationVector av:list)
		{
			sb.append(av.toString());
			sb.append("\n");
		}
		FileOperator.write(address+filename, sb.toString());
	}

	public List<AccelerationVector> getAccelerationList() {
		return accelerationList;
	}

	public void setAccelerationList(List<AccelerationVector> accelerationList) {
		this.accelerationList = accelerationList;
	}

	public List<FCMagneticVector> getMagneticList() {
		return magneticList;
	}

	public void setMagneticList(List<FCMagneticVector> magneticList) {
		this.magneticList = magneticList;
	}

	public List<FCMagneticVector> getGeoMagneticList() {
		return geoMagneticList;
	}

	public void setGeoMagneticList(List<FCMagneticVector> geoMagneticList) {
		this.geoMagneticList = geoMagneticList;
	}

	public List<DirectionVector> getDirectionList() {
		return directionList;
	}

	public void setDirectionList(List<DirectionVector> directionList) {
		this.directionList = directionList;
	}

	public List<RSSIData> getRssiList() {
		return rssiList;
	}

	public void setRssiList(List<RSSIData> rssiList) {
		this.rssiList = rssiList;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Line getLine() {
		return line;
	}

	public void setLine(Line line) {
		this.line = line;
	}
}
