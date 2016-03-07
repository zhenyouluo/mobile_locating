package com.maloc.client.fc.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.maloc.client.R;
import com.maloc.client.fc.graphics.Line;
import com.maloc.client.fc.ui.PopMenu.OnItemClickListener;
import com.maloc.client.sensor.fc.FCSensorDataCollector;
import com.maloc.client.sensor.fc.FCSensorDataListener;
import com.maloc.client.sensor.fc.LineSensorCollectEngine;
import com.maloc.client.sensor.fc.SensorDataCollector;
import com.maloc.client.sensor.fc.SensorDataMerger;
import com.maloc.client.sensor.fc.SensorsData;
import com.maloc.client.util.FileOperator;
import com.maloc.client.util.GlobalProperties;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 室内地图显示界面
 * @author xhw Email:xxyx66@126.com
 */
public class IndoorMapActivity extends Activity {

	public static final int REQUEST_ELLIPSOID_PARAMS = 100;

	private IndoorMapImageView indoorMap;
	private ImageButton controlButton, rotateButton, uploadButton,
			settingButton, localizeButton;
	private TextView floorView;
	private PopMenu popMenu;
	private State state;
	private ControlButtonOnClickListener controlListener;
	private LocalizeClickListener localizeListener;
	private String sceneDir;
	private LineSensorCollectEngine collectEngine;
	private WakeLock wakeLock;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_indoormap);
		acquireWakeLock();;
		
		floorView = (TextView) findViewById(R.id.title);
		floorView.setText(GlobalProperties.currentFloor.getFloorIndex()+"-"+GlobalProperties.currentFloor.getFloorName());

		// Bitmap bitmap=BitmapFactory.decodeResource(getResources(),
		// R.drawable.floor2);
		String pathName = Environment.getExternalStorageDirectory().getPath()
				+ "/" + GlobalProperties.MAP_BASE_DIR + "/"
				+ GlobalProperties.currentFloor.getFloorPath();
		/*Properties p = new Properties();
		try {
			p.load(new FileInputStream(pathName + "config"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
*/
		Bitmap bitmap = BitmapFactory.decodeFile(pathName + "map."+GlobalProperties.currentFloor.getMapFileType());
		GlobalProperties.MAP_SCALE = Math.min(
				bitmap.getWidth() / GlobalProperties.currentFloor.getFloorWidth(),
				bitmap.getHeight()/ GlobalProperties.currentFloor.getFloorHeight());
		Log.i("map scale", GlobalProperties.MAP_SCALE + "");

		String baseDir = Environment.getExternalStorageDirectory().getPath()
				+ "/" + GlobalProperties.FINGERPRINTS_BASE_DIR + "/";
		sceneDir = baseDir + GlobalProperties.currentFloor.getFloorPath();
		FileOperator.directMakeDir(sceneDir);

		state = new State();
		// state.curentControlState=ControlState.NONE;
		collectEngine=new LineSensorCollectEngine(this);
		controlButton = (ImageButton) findViewById(R.id.begin);
		controlListener = new ControlButtonOnClickListener(controlButton,
				state, handler);
		controlButton.setOnClickListener(controlListener);

		indoorMap = (IndoorMapImageView) findViewById(R.id.indoormap);

		indoorMap.init(state, bitmap, controlListener, sceneDir);
		//controlListener.linkToImageView(indoorMap);

		rotateButton = (ImageButton) findViewById(R.id.rotate);
		rotateButton.setOnClickListener(new RotateButtonOnClickListener(
				indoorMap));

		popMenu = new PopMenu(this);
		popMenu.addItems(new String[] { "Calibration", "Delete all paths",
				"Setting", "Exit" });
		popMenu.setOnItemClickListener(new PopMunuOnItemClickListener(this));

		settingButton = (ImageButton) findViewById(R.id.menu);
		settingButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popMenu.showAsDropDown(v);
			}
		});

		uploadButton = (ImageButton) findViewById(R.id.upload);
		uploadButton.setOnClickListener(new UploadClickListener());

		localizeButton = (ImageButton) findViewById(R.id.localize);
		localizeListener = new LocalizeClickListener(this, state, indoorMap,localizeButton);
		localizeButton.setOnClickListener(localizeListener);
	}

	
	private void acquireWakeLock() {  
        if (null == wakeLock) {  
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);  
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK  
                    | PowerManager.ON_AFTER_RELEASE, getClass()  
                    .getCanonicalName());  
            if (null != wakeLock) {  
                Log.i("IndoorAcitivty", "call acquireWakeLock");  
                wakeLock.acquire();  
            }  
        }  
    }  
  
    // 释放设备电源锁  
    private void releaseWakeLock() {  
        if (null != wakeLock && wakeLock.isHeld()) {  
            Log.i("IndoorActivity", "call releaseWakeLock");  
            wakeLock.release();  
            wakeLock = null;
        }
    }
        
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == IndoorMapActivity.REQUEST_ELLIPSOID_PARAMS) {
			switch (resultCode) {
			case CalibrationActivity.CALIBRATION_SUCCESS:
				new AlertDialog.Builder(this.indoorMap.getContext())
						.setMessage("Calibration success.")
						.setPositiveButton("OK", null).show();
				break;
			case CalibrationActivity.CALIBRATION_TIMEOUT:

				new AlertDialog.Builder(this.indoorMap.getContext())
						.setMessage(
								"Calibration is taking a long time. It is possible that the sensors of the device are not"
										+ "working properly. You may still try positioning but the accuracy may not be good. "
										+ "Mapping is disabled.")
						.setPositiveButton("OK", null).show();
				break;
			default:
				Toast.makeText(getApplicationContext(),
						"Calibration cancelled.", Toast.LENGTH_SHORT).show();
			}

		}
	}

	public static final int STOP_LOCALIZE = 1;
	public static final int REPAINT = 2;
	public static final int START_COLLECT=3;
	public static final int STOP_COLLECT=4;
	public static final int MERGE_DATA = 5;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case STOP_LOCALIZE:
				localizeListener.onStop();
				break;
			case REPAINT:
				indoorMap.invalidate();
				break;
			case START_COLLECT:
				Line line=indoorMap.getLine();
				line.setMoveForwar(msg.getData().getBoolean("moveForward"));
				collectEngine.startCollect(line);
				break;
			case STOP_COLLECT:
				collectEngine.stopCollect();
				break;
			case MERGE_DATA:
				collectEngine.mergeData(indoorMap.getLine());
				break;
			
			}
		}
	};

	public IndoorMapImageView getIndoorMap() {
		return indoorMap;
	}

	public void setIndoorMap(IndoorMapImageView indoorMap) {
		this.indoorMap = indoorMap;
	}

	public ImageButton getControlButton() {
		return controlButton;
	}

	public void setControlButton(ImageButton controlButton) {
		this.controlButton = controlButton;
	}

	public ImageButton getRotateButton() {
		return rotateButton;
	}

	public void setRotateButton(ImageButton rotateButton) {
		this.rotateButton = rotateButton;
	}

	public ImageButton getUploadButton() {
		return uploadButton;
	}

	public void setUploadButton(ImageButton uploadButton) {
		this.uploadButton = uploadButton;
	}

	public ImageButton getSettingButton() {
		return settingButton;
	}

	public void setSettingButton(ImageButton settingButton) {
		this.settingButton = settingButton;
	}

	public PopMenu getPopMenu() {
		return popMenu;
	}

	public void setPopMenu(PopMenu popMenu) {
		this.popMenu = popMenu;
	}

	public void onBackPressed() {
		stopUnCompletedCollecting();
		clearUnCompletedCollecting();
		this.finish();
	}

	@Override
	public void onStart()
	{
		super.onStart();
		
	}
	
	@Override
	public void onStop() {
		super.onStop();
	}
	
	@Override
	public void onDestroy()
	{
		stopUnCompletedCollecting();
		clearUnCompletedCollecting();
		releaseWakeLock();
		super.onDestroy();
	}

	@Override
	public void onPause() {
		//stopUnCompletedCollecting();
		//clearUnCompletedCollecting();
		super.onPause();
	}

	private void clearUnCompletedCollecting()
	{
		if (state.curentControlState == ControlState.START_COLLECT
				|| state.curentControlState==ControlState.STOP_COLLECT
				|| state.curentControlState == ControlState.START_VALIDATE) {
			//collectEngine.onStop();
			File file = new File(sceneDir + indoorMap.getLine().toFileName());
			if (file.exists()) {
				FileOperator.deleteFile(file);
			}
			state.curentControlState = ControlState.NONE;
			controlButton.setImageDrawable(controlButton.getResources().getDrawable(R.drawable.begin));
		} 
		
		//this.finish();
	}
	private void stopUnCompletedCollecting() {
		if (state.curentControlState == ControlState.START_COLLECT
				|| state.curentControlState == ControlState.START_VALIDATE) {
			collectEngine.onStop();
			/*File file = new File(sceneDir + indoorMap.getLine().toFileName());
			if (file.exists()) {
				FileOperator.deleteFile(file);
			}*/
		} else if (state.curentControlState == ControlState.LOCALIZE) {
			localizeListener.onStop();
			
		}
		
	}

}
