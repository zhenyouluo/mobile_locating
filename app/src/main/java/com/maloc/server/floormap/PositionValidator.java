package com.maloc.server.floormap;

public interface PositionValidator {
	/**
	 * 检测位置是否合理存在
	 * @param x
	 * @param y
	 * @return true，合理；false不合理
	 */
	public boolean isValidePosition(float x,float y);
	/**
	 * 根据位置返回合理的初始区域
	 * @param position
	 * @return
	 */
	public float[] initArea(float[] position);
		
}
