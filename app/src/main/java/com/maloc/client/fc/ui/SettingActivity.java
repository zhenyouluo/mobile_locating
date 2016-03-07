package com.maloc.client.fc.ui;

import com.maloc.client.R;
import com.maloc.client.util.GlobalProperties;
import com.maloc.client.util.WifiAdmin;
import com.maloc.server.particlefilter.AdaptiveRobustParticleFilter2;
import com.maloc.server.particlefilter.RobustParticleFilter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
/**
 * 设置界面
 * @author xhw Email:xxyx66@126.com
 */
public class SettingActivity extends Activity{

	private CheckBox wifiCheckBox,magneticCheckBox;
	private SeekBar seekBar;
	private Button confirm;
	private EditText heuRange,particleNum,binThreshold;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		wifiCheckBox=(CheckBox) findViewById(R.id.wifi_fingerprint);
		magneticCheckBox=(CheckBox) findViewById(R.id.magnetic_fingerprint);
		wifiCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton v, boolean checked) {

				if(checked==false&&GlobalProperties.Solution_Magnetic==false)
				{
					new AlertDialog.Builder(v.getContext())
					.setTitle("Warn")
					.setMessage("Choose at least one solution!")
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {

							wifiCheckBox.setChecked(true);
						}
						
					})
					.show();
				}
				else
				{
					GlobalProperties.Solution_WiFi=checked;
				}
			}
			
		});
		
		magneticCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton v, boolean checked) {

				if(checked==false&&GlobalProperties.Solution_WiFi==false)
				{
					new AlertDialog.Builder(v.getContext())
					.setTitle("Warn")
					.setMessage("Choose at least one Solution!")
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {

							magneticCheckBox.setChecked(true);
						}
						
					})
					.show();
				}
				else
				{
					GlobalProperties.Solution_Magnetic=checked;
				}
			}
			
		});
		seekBar=(SeekBar)findViewById(R.id.heu_sensitivity);
		seekBar.setProgress((int)(RobustParticleFilter.RANDOM_NUM_FACTOR*100));
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() //调音监听器  
        {

			@Override
			public void onProgressChanged(SeekBar arg0, int progress, boolean fromUser) {

				RobustParticleFilter.RANDOM_NUM_FACTOR=progress/100.0f;
				Log.i("SettingAcitity", RobustParticleFilter.RANDOM_NUM_FACTOR+"");
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {

			}  
        });
		
		heuRange=(EditText)findViewById(R.id.range);
		particleNum=(EditText)findViewById(R.id.particle_num);
		binThreshold=(EditText)findViewById(R.id.adaptive_threshold);
		
		heuRange.setText(String.valueOf(RobustParticleFilter.RANDOM_RANGE));
		particleNum.setText(String.valueOf(GlobalProperties.PARTICLENUM));
		binThreshold.setText(String.valueOf(AdaptiveRobustParticleFilter2.BIN_INC_THRESHOLD));
		
		confirm=(Button)findViewById(R.id.set_confirm);
		confirm.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {

				String range=heuRange.getText().toString();
				if(range!=null&&range.length()>0)
				{
					RobustParticleFilter.RANDOM_RANGE=Float.parseFloat(range);
				} 
				
				String pn=particleNum.getText().toString();
				if(pn!=null&&pn.length()>0)
				{
					GlobalProperties.PARTICLENUM=Integer.parseInt(pn);
				}
				String bt=binThreshold.getText().toString();
				if(bt!=null&&bt.length()>0)
				{
					AdaptiveRobustParticleFilter2.BIN_INC_THRESHOLD=Integer.parseInt(bt);
				}
				SettingActivity.this.finish();
			}
			
		});
	}
}
