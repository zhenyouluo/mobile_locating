package com.maloc.server.floormap;

import com.maloc.client.util.GlobalProperties;
import com.maloc.server.magmap.MagneticQuerier;
/**
 * 利用地磁指纹库验证位置是否合理存在，即只有在指纹库中存在的位置才是有效的
 * @author xhw Email:xxyx66@126.com
 */
public class MagneticPositionValidator implements PositionValidator{
	//地磁指纹查询接口
	private MagneticQuerier mQuerier;
	
	public MagneticPositionValidator(MagneticQuerier mQuerier)
	{
		this.mQuerier=mQuerier;
	}
	
	@Override
	public boolean isValidePosition(float x, float y) {

		if(this.mQuerier.queryMagneticVectorByLoc(x, y)==null)
			return false;
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
