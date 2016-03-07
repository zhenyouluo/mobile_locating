package com.maloc.client.util;

import android.os.Environment;
/**
 * Log工具类
 * @author xhw Email:xxyx66@126.com
 */
public class FileLog {

	
	public static void dataLog(String name,String message)
	{
		String logFile=Environment.getExternalStorageDirectory().getPath()
				+ "/"+GlobalProperties.FINGERPRINTS_BASE_DIR+"/"+GlobalProperties.currentFloor.getFloorPath()+name;
		FileOperator.write(logFile, System.currentTimeMillis()+"\t"+message, true);
	}
	
	public static void errorLog(String name,String message)
	{
		String logFile=Environment.getExternalStorageDirectory().getPath()
				+ "/sensor_records/"+name;
		FileOperator.write(logFile, System.currentTimeMillis()+"\t"+message, true);
	}
}
