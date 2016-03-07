package com.maloc.client.sensor.fc;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.maloc.client.fc.graphics.Line;
/**
 * 指纹收集控制引擎
 * @author xhw Email:xxyx66@126.com
 */
public class LineSensorCollectEngine {

	private SensorsData sensorsData=null;
	private SensorDataCollector collector;
	private FCSensorDataListener fcListener;
	private ProgressDialog pd,pd2;  
	private ExecutorService executor;
	private Activity activity;
	public LineSensorCollectEngine(Activity activity)
	{
		this.activity=activity;
		this.fcListener=new FCSensorDataListener();
		this.collector=new FCSensorDataCollector(activity,this.fcListener);
		executor=Executors.newSingleThreadExecutor();
	}
	public void startCollect(Line line)
	{
		this.sensorsData=new SensorsData(line);
		this.fcListener.setSensorsData(sensorsData);
		collector.startCollection(line);
	}
	
	public void mergeData(Line line)
	{
		final Line L=line;
		pd2 = ProgressDialog.show(activity, "Merge Fingerpints", "Wait……");
		executor.execute(new Runnable()
		{
			@Override
			public void run() {

				SensorDataMerger.mergeData(L);
				handler.sendEmptyMessage(1);
			}
			
		});
		
	}
	
	
	
	/*private Runnable saveRunner=new Runnable(){

		@Override
		public void run() {
			sensorsData.save();
			handler.sendEmptyMessage(0);
		}
		
	};*/
	
	private Handler handler=new Handler(){
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			switch(msg.what)
			
			{
			case 0:
				pd.dismiss();
				break;
			case 1:
				pd2.dismiss();
			}
			
		}
	};
	
	public void stopCollect()
	{
		collector.stopCollection();
		pd = ProgressDialog.show(activity, "Saving Fingerpints", "Wait……"); 
		executor.execute(new Runnable(){

			@Override
			public void run() {
				sensorsData.save();
				sensorsData=null;
				handler.sendEmptyMessage(0);
			}
			
		});
		
	}
	
	public void onStop()
	{
		collector.stopCollection();
		sensorsData=null;
	}
}
