package com.maloc.client.util;

import java.io.FileWriter;
import java.io.IOException;

import android.util.Log;
/**
 * 记录辅助工具类
 * @author xhw
 *
 */
public class Record {

	public static void record(String filename,String record)
	{
		try {
			FileWriter fw=new FileWriter(filename,true);
			fw.write(record);
			fw.close();
		} catch (IOException e) {
			Log.e("IOException", e.toString());
		} 
	}
	
	

	
}
