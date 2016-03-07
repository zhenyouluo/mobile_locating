package com.maloc.client.fc.ui;

import java.io.File;

import com.maloc.client.R;
import com.maloc.client.fc.graphics.Line;
import com.maloc.client.sensor.fc.FCSensorDataCollector;
import com.maloc.client.sensor.fc.FCSensorDataListener;
import com.maloc.client.sensor.fc.SensorDataCollector;
import com.maloc.client.sensor.fc.SensorDataMerger;
import com.maloc.client.sensor.fc.SensorsData;
import com.maloc.client.util.GlobalProperties;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;
/**
 * 控制按钮，室内地图界面左1按钮的控制类
 * @author xhw Email:xxyx66@126.com
 */
public class ControlButtonOnClickListener implements OnClickListener {

	
	
	private State state;
	//private IndoorMapImageView imageView;
	private ImageButton button;
	
	private Handler indoorMapHandler;
	public ControlButtonOnClickListener(ImageButton button,State state,Handler handler)
	{
		this.state=state;
		this.button=button;
		this.indoorMapHandler=handler;
	}
	
	
	@Override
	public void onClick(View v) {
		//ImageButton button =(ImageButton)v;
		switch(state.curentControlState)
		{
		case LOCALIZE:
		case NONE:
		case STOP_VALIDATE:
			
			if(state.curentControlState==ControlState.LOCALIZE)
			{
				state.curentControlState=ControlState.DRAW_START;
				this.indoorMapHandler.sendEmptyMessage(IndoorMapActivity.STOP_LOCALIZE);
				this.indoorMapHandler.sendEmptyMessage(IndoorMapActivity.REPAINT);
			}
			state.curentControlState=ControlState.DRAW_START;
			button.setImageDrawable(button.getResources().getDrawable(R.drawable.ready));
			Toast.makeText(v.getContext(), "Draw Collection Path.",Toast.LENGTH_SHORT).show();
			button.setEnabled(false);
			break;
		case DRAW_START:
			state.curentControlState=ControlState.DRAW_END;
			button.setImageDrawable(button.getResources().getDrawable(R.drawable.end));
			break;
		case DRAW_END:
			button.setEnabled(true);
			state.curentControlState=ControlState.DRAW_DONE;
			button.setImageDrawable(button.getResources().getDrawable(R.drawable.record));
			break;
		case DRAW_DONE:
			state.curentControlState=ControlState.START_COLLECT;
			button.setImageDrawable(button.getResources().getDrawable(R.drawable.stop));
			Toast.makeText(v.getContext(), "Walk to collect.",Toast.LENGTH_SHORT).show();
			startCollect(true);
			break;
		case START_COLLECT:
			state.curentControlState=ControlState.STOP_COLLECT;
			button.setImageDrawable(button.getResources().getDrawable(R.drawable.validate));
			this.indoorMapHandler.sendEmptyMessage(IndoorMapActivity.REPAINT);
			stopCollect();
			Toast.makeText(v.getContext(), "Turn back and press button to start validate.",Toast.LENGTH_SHORT).show();
			break;
		case STOP_COLLECT:
			state.curentControlState=ControlState.START_VALIDATE;
			button.setImageDrawable(button.getResources().getDrawable(R.drawable.stop));
			Toast.makeText(v.getContext(), "Walk to validate.",Toast.LENGTH_SHORT).show();
			startCollect(false);
			break;
		case START_VALIDATE:
			state.curentControlState=ControlState.STOP_VALIDATE;
			this.indoorMapHandler.sendEmptyMessage(IndoorMapActivity.REPAINT);
			button.setImageDrawable(button.getResources().getDrawable(R.drawable.begin));
			stopCollect();
			this.indoorMapHandler.sendEmptyMessage(IndoorMapActivity.MERGE_DATA);
			break;
		
		default:
			break;
		}
		Log.i("state", state.curentControlState.toString());
	}
	

	private void stopCollect() {

		this.indoorMapHandler.sendEmptyMessage(IndoorMapActivity.STOP_COLLECT);
	}

	/**
	 * moveForward=true, collecting. moveForward=false, validating;
	 * @param phase
	 */
	private void startCollect(boolean moveForward) {

		Message msg=new Message();
		msg.what=IndoorMapActivity.START_COLLECT;
		msg.getData().putBoolean("moveForward", moveForward);
		this.indoorMapHandler.sendMessage(msg);
		
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}
	

}
