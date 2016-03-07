package com.maloc.client.fc.ui;

import android.content.Intent;

import com.maloc.client.fc.ui.PopMenu.OnItemClickListener;
/**
 * Setting按钮弹出列表Item
 * @author xhw Email:xxyx66@126.com
 */
public class PopMunuOnItemClickListener implements OnItemClickListener {

	private IndoorMapActivity activity;
	
	public PopMunuOnItemClickListener(IndoorMapActivity activity)
	{
		this.activity=activity;
	}
	@Override
	public void onItemClick(int index) {

		Intent intent;
		switch(index)
		{
		case 0:
			intent = new Intent(activity, CalibrationActivity.class);
			activity.startActivityForResult(intent,IndoorMapActivity.REQUEST_ELLIPSOID_PARAMS);
			break;
		case 1:
			this.activity.getIndoorMap().deleteAllLines();
			this.activity.getIndoorMap().invalidate();
			break;
		case 2:
			intent = new Intent(activity, SettingActivity.class);
			activity.startActivity(intent);
			break;
		case 3:
			System.exit(0);
			break;
		}
		
	}

}
