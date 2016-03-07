package com.maloc.client.localization;

import java.util.List;

import com.maloc.client.bean.MagneticVector;
import com.maloc.client.sensor.accelerometer.InflectionPoint;
/**
 * 地磁定位信息单元
 * @author xhw Email:xxyx66@126.com
 */
public class LocalizationInfo {

	private  List<InflectionPoint> accList=null;
	private  List<Float> gyroValue=null;
	private  List<MagneticVector> magList=null;
	
	public LocalizationInfo()
	{
		
	}

	public List<InflectionPoint> getAccList() {
		return accList;
	}

	public void setAccList(List<InflectionPoint> accList) {
		this.accList = accList;
	}

	public List<Float> getGyroValue() {
		return gyroValue;
	}

	public void setGyroValue(List<Float> gyroValue) {
		this.gyroValue = gyroValue;
	}

	public List<MagneticVector> getMagList() {
		return magList;
	}

	public void setMagList(List<MagneticVector> magList) {
		this.magList = magList;
	}
	
	public String toString()
	{
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<this.accList.size();i++)
		{
			InflectionPoint p=this.accList.get(i);
			sb.append(p.getTimestamp()).append("\t").append(p.getValue()).append("\t");
			sb.append(this.gyroValue.get(i)).append("\t");
			sb.append(this.magList.get(i).module()).append("\n");
			
		}
		return sb.toString();
	}
	
}
