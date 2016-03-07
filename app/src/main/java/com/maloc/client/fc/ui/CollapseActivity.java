package com.maloc.client.fc.ui;

import com.maloc.client.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
/**
 * 异常发生显示界面
 * @author xhw Email:xxyx66@126.com
 */
public class CollapseActivity extends Activity {
    private Button btnExit;
    private TextView exceptionText;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);
        //AppManage.getInstance().addActivity(this);
       // btnRestart = (Button) findViewById();
        Intent info=getIntent();
        exceptionText= (TextView) findViewById(R.id.error_info);
        exceptionText.setText(info.getExtras().getString("exception"));
        btnExit = (Button) findViewById(R.id.exit_btn);
        btnExit.setOnClickListener(new OnClickListener() {
 
            @Override
            public void onClick(View v) {
            	CollapseActivity.this.finish();
            }
        });
    }
}
