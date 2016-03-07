package com.maloc.client.fc.ui;

import com.maloc.client.util.GeometricUtil;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
/**
 * IndoorImageView里的旋转回正按钮控制类
 * @author xhw Email:xxyx66@126.com
 */
public class RotateButtonOnClickListener implements OnClickListener{

	private IndoorMapImageView imageView;

	private float center[]=new float[2];
	public RotateButtonOnClickListener(IndoorMapImageView view)
	{
		this.imageView=view;
		//rotateCnt=0;
	}
	
	@Override
	public void onClick(View v) {

		//rotateCnt++;
		center[0]=imageView.getBitmap().getWidth()/2.0f;
		center[1]=imageView.getBitmap().getHeight()/2.0f;
		imageView.getImageMatrix().mapPoints(center);
		
		double angleChanged=GeometricUtil.extractRotate(imageView.getImageMatrix())/Math.PI*180;
		
		if(Math.abs(angleChanged)>1)
		{
			imageView.getImageMatrix().postRotate((float) -angleChanged,center[0] ,center[1]);
			imageView.invalidate();
		}
		
		
	}

}
