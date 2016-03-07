package com.maloc.server.floormap;

import com.maloc.client.util.GlobalProperties;

/**
 * 验证一个位置是否是合理的，比如是否是在地图范围内等。
 * @author xhw Email:xxyx66@126.com
 */
public class DefaultPositionValidator implements PositionValidator {

	@Override
	public boolean isValidePosition(float x, float y) {
		return true;
	}

	@Override
	public float[] initArea(float[] location) {

		float area[]=new float[4];
		float x1=location[0]-GlobalProperties.RANGE;
		float x2=location[0]+GlobalProperties.RANGE;
		float y1=location[1]-GlobalProperties.RANGE;
		float y2=location[1]+GlobalProperties.RANGE;
		/*if(x1<0)
			x1=0;
		if(x2>65)
			x2=65;
		if(y1<0)
			y1=0;
		if(y2>65)
			y2=65;*/
		area[0]=x1;
		area[1]=x2;
		area[2]=y1;
		area[3]=y2;
		return area;
	}

}
