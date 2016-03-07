package com.maloc.client.fc.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.maloc.client.util.FileOperator;
import com.maloc.client.util.GlobalProperties;
import com.maloc.client.util.HttpUtil;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
/**
 * 上传Fingerprint
 * @author xhw Email:xxyx66@126.com
 */
public class UploadClickListener implements OnClickListener {

	private static final String TAG=UploadClickListener.class.getName();
	private static ProgressDialog pd; 
	private static String urlBase="http://"+GlobalProperties.Host+":"+GlobalProperties.Port+"/MaLocDitu/";
	static Handler handler = new Handler() {  
        @Override  
        public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法  
            if(pd!=null)
            	pd.dismiss();// 关闭ProgressDialog  
            switch(msg.what)
            {
            case 0:
            	new Thread(new Runnable(){

					@Override
					public void run() {
						updateFC();
					}
            		
            	}).start();
            }
        }  
    };  
    
    /**
     * 更新指纹元信息
     */
    private static void updateFC()
    {
    	RequestParams params = new RequestParams();
		params.put("venueId", GlobalProperties.currentFloor.getVenueId());
		params.put("floorIndex", GlobalProperties.currentFloor.getFloorIndex());
		HttpUtil.post(urlBase+"fingerprint!update", params, new JsonHttpResponseHandler(){

			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				Log.i(TAG, "update fail");
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				Log.i(TAG, "update success");
				File conf=new File(Environment.getExternalStorageDirectory().getPath()
						+ "/"+GlobalProperties.FINGERPRINTS_BASE_DIR+"/"+GlobalProperties.currentFloor.getFloorPath()+"config");
				
				if(!conf.getParentFile().exists())
					conf.mkdirs();
				try {
					stampTimeOnFingerprint(conf,response.getString("updateTime"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
		});
    }
    
    private static void stampTimeOnFingerprint(File conf,String timestmap)
	{
		Properties pro=new Properties();
		FileInputStream fin=null;
		FileOutputStream fout=null;
		try {
			if(conf.exists())
			{
				fin=new FileInputStream(conf);
				pro.load(fin);
			}
			
			//Log.i("UploadClickListern", pro.getProperty("timestamp"));
			pro.setProperty("fc_timestamp", timestmap);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally
		{
			try {
				if(fin!=null)
					fin.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			fout=new FileOutputStream(conf);
			pro.store(fout, "config");
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally
		{
			try {
				fout.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
    
	class Uploader implements Runnable
	{
		
		@Override
		public void run() {
			String dir = Environment.getExternalStorageDirectory().getPath()
					+ "/"+GlobalProperties.FINGERPRINTS_BASE_DIR+"/"+GlobalProperties.currentFloor.getFloorPath();
			File dirFile=new File(dir);
			String lines[]=dirFile.list();
			File mf=mergeMagneticFiles(lines,dir);
			File wf=mergeWiFiFiles(lines,dir);
			File conf=new File(Environment.getExternalStorageDirectory().getPath()
					+ "/"+GlobalProperties.MAP_BASE_DIR+"/"+GlobalProperties.currentFloor.getFloorPath()+"config");
			
			upload(mf);
			upload(wf);
			upload(conf);
			handler.sendEmptyMessage(0);
		}
		
		

		private void upload(File wf) {

			 RequestParams params = new RequestParams();
			 params.put("venueId", GlobalProperties.currentFloor.getVenueId());
			 params.put("floorIndex", GlobalProperties.currentFloor.getFloorIndex());
			 try {
				 
				params.put("fc", wf);
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return;
			}
			 HttpUtil.post(urlBase+"uploadFC", params, new AsyncHttpResponseHandler(){

				@Override
				public void onFailure(int arg0, Header[] arg1, byte[] arg2,
						Throwable arg3) {
					Log.i("uploadListener", "upload failed.");
					handler.sendEmptyMessage(1);
				}

				@Override
				public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
					Log.i("uploadListener", "upload success.");
				}});
		}
		/**
		 * 合并所有线段上的WiFi指纹到一个文件内
		 * @param lines
		 * @param dir
		 * @return
		 * @throws IOException
		 */
		private File mergeWiFiFiles(String[] lines, String dir) {

			StringBuilder sb=new StringBuilder();
			
			for(String line:lines)
			{
				String subDir=dir+line;
				File file=new File(subDir);
				if(!file.isDirectory())
					continue;
				String content="";
				try {
					content = FileOperator.readToString(subDir+"/wifi.txt");
				} catch (IOException e) {
					e.printStackTrace();
				}
				sb.append(content);
			}
			
			FileOperator.write(dir+"wifi.txt", sb.toString());
			return new File(dir+"wifi.txt");
			
		}
		/**
		 * 合并所有的地磁指纹到一个文件内
		 * @param lines
		 * @param dir
		 * @return
		 * @throws IOException
		 */
		private File mergeMagneticFiles(String[] lines, String dir) {

			StringBuilder sb=new StringBuilder();
			
			for(String line:lines)
			{
				String subDir=dir+line;
				File file=new File(subDir);
				if(!file.isDirectory())
					continue;
				String content="";
				try {
					content = FileOperator.readToString(subDir+"/magnetic_geo.txt");
				} catch (IOException e) {
					e.printStackTrace();
				}
				sb.append("# ").append(scaledLine(line)).append("\n");
				sb.append(content);
			}
			FileOperator.write(dir+"magnetic_geo.txt", sb.toString());
			return new File(dir+"magnetic_geo.txt");
		}

		private String scaledLine(String line) {

			String t[]=line.split(" ");
			StringBuilder sb=new StringBuilder();
			for(int i=0;i<t.length;i++)
			{
				sb.append(Float.parseFloat(t[i])/GlobalProperties.MAP_SCALE).append(" ");
			}
			return sb.toString();
		}
		
	}
	
	@Override
	public void onClick(final View v) {

		new AlertDialog.Builder(v.getContext()) 
		.setTitle("Upload to Cloud?")
		//.setMessage("确定吗？")
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {

				pd = ProgressDialog.show(v.getContext(), "Upload Fingerpints", "Wait……"); 
				new Thread(new Uploader()).start();
				
			}
		})
		.setNegativeButton("No", null)
		.show();
		
	}
	
	

}
