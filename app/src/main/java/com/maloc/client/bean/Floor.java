package com.maloc.client.bean;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;
/**
 * 楼层信息
 * @author xhw
 *
 */
public class Floor implements Serializable,Comparable<Floor>{

	private int floorIndex;
	private int venueId;
	private String floorName;
	private String floorDescription;
	private float floorWidth;
	private float floorHeight;
	private String mapPath;
	private String floorPath;
	private String mapFileType;
	private String timestamp;
	
	public Floor(int floorIndex, int venueId, String floorName,
			String floorDescription, float floorWidth, float floorHeight,
			String mapType,String timestamp) {
		super();
		this.floorIndex = floorIndex;
		this.venueId = venueId;
		this.floorName = floorName;
		this.floorDescription = floorDescription;
		this.floorWidth = floorWidth;
		this.floorHeight = floorHeight;
		this.mapFileType=mapType;
		this.timestamp=timestamp;
		this.floorPath=this.venueId+"/"+this.floorIndex+"/";
		this.mapPath=this.floorPath+"map."+this.mapFileType;
		
	}


	public Floor(JSONObject json) throws JSONException
	{
		this(json.getInt("floorIndex"),json.getInt("venueId"),json.getString("floorName"),
				json.getString("floorDescription"),(float)json.getDouble("floorWidth"),
				(float)json.getDouble("floorHeight"),json.getString("mapFileType"),json.getString("timestamp"));
	}
	
	
	public int getFloorIndex() {
		return floorIndex;
	}
	public void setFloorIndex(int floorIndex) {
		this.floorIndex = floorIndex;
	}
	public String getFloorName() {
		return floorName;
	}
	public void setFloorName(String floorName) {
		this.floorName = floorName;
	}
	public String getFloorDescription() {
		return floorDescription;
	}
	public void setFloorDescription(String floorDescription) {
		this.floorDescription = floorDescription;
	}
	public float getFloorWidth() {
		return floorWidth;
	}
	public void setFloorWidth(float floorWidth) {
		this.floorWidth = floorWidth;
	}
	public float getFloorHeight() {
		return floorHeight;
	}
	public void setFloorHeight(float floorHeight) {
		this.floorHeight = floorHeight;
	}


	public int getVenueId() {
		return venueId;
	}


	public void setVenueId(int venueId) {
		this.venueId = venueId;
	}


	@Override
	public int compareTo(Floor other) {

		if(other==null)
			return 1;
		return this.floorIndex-other.floorIndex;
	}


	public String getMapPath() {
		return mapPath;
	}


	public void setMapPath(String mapPath) {
		this.mapPath = mapPath;
	}


	public String getMapFileType() {
		return mapFileType;
	}


	public void setMapFileType(String mapType) {
		this.mapFileType = mapType;
	}


	public String getTimestamp() {
		return timestamp;
	}


	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}


	public String getFloorPath() {
		return floorPath;
	}


	public void setFloorPath(String floorPath) {
		this.floorPath = floorPath;
	}
	
	
}
