package com.maloc.client.fc.ui;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
/**
 * 室内地图图片控制类，控制图片的旋转，缩放，拖拽等等
 * @author xhw Email:xxyx66@126.com
 */
public class ImageViewOnTouchListener implements OnTouchListener{

	private Matrix imageMatrix = new Matrix();
	private Matrix savedMatrix = new Matrix();
	
	private State state;
	// 第一个按下的手指的点
	private PointF startPoint = new PointF();
	// 两个按下的手指的触摸点的中点
	private PointF midPoint = new PointF();
	// 初始的两个手指按下的触摸点的距离
	private float oriDis = 1f;
	
	public ImageViewOnTouchListener(State state)
	{
		this.state=state;
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		ImageView view = (ImageView) v;

		// 进行与操作是为了判断多点触摸
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			// 第一个手指按下事件
			imageMatrix.set(view.getImageMatrix());
			savedMatrix.set(imageMatrix);
			startPoint.set(event.getX(), event.getY());
			state.currentTouchState = TouchState.DRAG;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			// 第二个手指按下事件
			oriDis = distance(event);
			if (oriDis > 10f) {
				savedMatrix.set(imageMatrix);
				midPoint = middle(event);
				state.currentTouchState = TouchState.ZOOM;
			}
			break;
		case MotionEvent.ACTION_UP:
			if(state.curentControlState==ControlState.DRAW_START)
			{
				state.curentControlState=ControlState.DRAW_END;
				
			}
		case MotionEvent.ACTION_POINTER_UP:
			// 手指放开事件
			state.currentTouchState = TouchState.NONE;
			break;
		case MotionEvent.ACTION_MOVE:
			// 手指滑动事件
			if (state.currentTouchState == TouchState.DRAG) {
				// 是一个手指拖动
				imageMatrix.set(savedMatrix);
				imageMatrix.postTranslate(event.getX() - startPoint.x,
						event.getY() - startPoint.y);
			} else if (state.currentTouchState == TouchState.ZOOM) {
				// 两个手指滑动
				float newDist = distance(event);
				if (newDist > 10f) {
					imageMatrix.set(savedMatrix);
					float scale = newDist / oriDis;
					imageMatrix.postScale(scale, scale, midPoint.x, midPoint.y);
				}
			}
			break;
		}

		// 设置ImageView的Matrix
		view.setImageMatrix(imageMatrix);
		return true;
	}

	// 计算两个触摸点之间的距离
	private float distance(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	// 计算两个触摸点的中点
	private PointF middle(MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		return new PointF(x / 2, y / 2);
	}
}
