package com.maloc.client.util;


import Jama.Matrix;

import com.maloc.client.bean.Floor;
import com.maloc.client.bean.Venue;
import com.maloc.client.ellipsoidFit.FitPoints;

public class GlobalProperties {

	
	public static final String AK="psTKb0ztjGL9Ee2B620hvE48";
	public static final int GEO_TABLE_ID=124830;
	public static String username;
	public static long FC_WIFI_SCAN_INTERVAL=1000l;
	public static final int MINIMUM_RSSI_NUM = 2;
	public static final int INCONSISTENCY = 3;
	
	public static String FINGERPRINTS_BASE_DIR="sensor_records/fingerprints";
	public static String MAP_BASE_DIR="sensor_records/map";
	
	public static Venue currentVenue;
	//public static String SCENE_NAME;
	//public static int FLOOR;
	public static Floor currentFloor;
	public static double MAP_SCALE=1.0;
	
	public static FitPoints ellipsoidFit;
	public static Matrix invertW;
	public static double[] center;
	
	public static int MAXSTEPS=100;
	public static int CACHESIZE=2000;
	public static int UPDATEINTERVAL=CACHESIZE/10;
	public static float STEPLENGTH=0.6f;
	public static int PARTICLENUM=2000;
	public static int RANGE=7;
	public static float noise[]={0.2f,0.2f,0.05f};
	public static float initAngle[]={(float) (0),(float) (Math.PI*2)};
	public static float orienNoise=0.3f;
	public static double R[][]={{1.2}};
	public static float dt=1;
	public static float RC=5;
	public static int meanFilterWindowSize=25;
	public static float pre[];
	public static float thresholdAcc[]={0.5f,5f};
	public static int thresholdTime[]={300,1100};
	public static int wifiScanInterval=3000;
	
	public static boolean Solution_WiFi=true;
	public static boolean Solution_Magnetic=true;
	
	public static String Host="10.130.137.24";
	//public static String Host="192.168.1.4";
	//public static final String Host="10.130.189.11";
	public static final int Port = 8080;
	
}
