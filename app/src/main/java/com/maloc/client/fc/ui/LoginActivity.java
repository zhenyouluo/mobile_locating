package com.maloc.client.fc.ui;

import com.maloc.client.R;
import com.maloc.client.util.GlobalProperties;
import com.maloc.client.util.WifiAdmin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
/**
 * 登录界面
 * @author xhw Email:xxyx66@126.com
 */
public class LoginActivity extends Activity{

	private EditText username,password;
	private Button mapMode,testMode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		username=(EditText)findViewById(R.id.username);
		password=(EditText)findViewById(R.id.password);
		mapMode=(Button)findViewById(R.id.map_mode);
		mapMode.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {

				GlobalProperties.username=username.getText().toString();
				Intent intent=new Intent();
				intent.setClass(LoginActivity.this, BaiduMapActivity.class);
				LoginActivity.this.startActivity(intent);
				//LoginActivity.this.finish();
			}
			
		});
		
		testMode=(Button)findViewById(R.id.test_mode);
		testMode.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {

				GlobalProperties.username=username.getText().toString();
				Intent intent=new Intent();
				intent.setClass(LoginActivity.this, IndoorTestActivity.class);
				LoginActivity.this.startActivity(intent);
				//LoginActivity.this.finish();
			}
			
		});
		
		WifiAdmin wifiAdmin=new WifiAdmin(this);
		wifiAdmin.openWifi();
	}
}
