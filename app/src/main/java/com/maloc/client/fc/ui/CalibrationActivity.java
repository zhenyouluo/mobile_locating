package com.maloc.client.fc.ui;

import com.maloc.client.R;
import com.maloc.client.bean.AccelerationVector;
import com.maloc.client.bean.AngularData;
import com.maloc.client.bean.FCMagneticVector;
import com.maloc.client.bean.OrientationVector;
import com.maloc.client.bean.RSSIData;
import com.maloc.client.ellipsoidFit.FitPoints;
import com.maloc.client.sensor.fc.CalibrationReadyListener;
import com.maloc.client.sensor.fc.CalibrationSensorDataCollector;
import com.maloc.client.sensor.fc.SensorDataCollector;
import com.maloc.client.sensor.fc.SensorDataListener;
import com.maloc.client.util.GlobalProperties;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * 磁强计校准界面.
 * 
 * @author xhw Email:xxyx66@126.com
 */
public class CalibrationActivity extends Activity implements OnClickListener{

	public static final int CALIBRATION_ERROR=0;
	public static final int CALIBRATION_SUCCESS=1;
	public static final int CALIBRATION_TIMEOUT=2;
	
	private ImageView calibrationImage;
	private TextView progress;
	private SensorDataCollector collector;
	private boolean started=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_calibration);
		calibrationImage=(ImageView) findViewById(R.id.calibration_img);
		calibrationImage.setOnClickListener(this);
		progress =(TextView) findViewById(R.id.progress);
		
		collector=new CalibrationSensorDataCollector(this,new CalibrationReadyListener(){

			@Override
			public void onCalibrationReady(FitPoints ellipsoidFit) {

				GlobalProperties.ellipsoidFit=ellipsoidFit;
				GlobalProperties.invertW=ellipsoidFit.getInvertedW();
				GlobalProperties.center=ellipsoidFit.center.toArray();
				Log.i("calibration center",ellipsoidFit.center.toString());
				Log.i("calibration W",GlobalProperties.invertW.get(0, 0)+","+GlobalProperties.invertW.get(0, 1));
				CalibrationActivity.this.setResult(CalibrationActivity.CALIBRATION_SUCCESS,new Intent());
				CalibrationActivity.this.finish();
			}

			@Override
			public void onCalibrationTimeOut() {
				CalibrationActivity.this.setResult(CalibrationActivity.CALIBRATION_TIMEOUT,new Intent());
				CalibrationActivity.this.finish();
			}

			@Override
			public void calibrationProgess(int percent) {

				progress.setText(percent+"%");
			}
			
		});
		
	}
	

	@Override
	public void onClick(View arg0) {

		if(started==false)
		{
			started=true;
			progress.setText("0%");
			collector.startCollection();
		}
		
	}
	
	@Override
	protected void onStop()
	{
		started=false;
		collector.stopCollection();
		super.onStop();
	}
	
}
