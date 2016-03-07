package com.maloc.server.magmap;

import com.maloc.client.bean.MagneticVector;

/**
 * 磁场Fingerprint查询接口
 * @author XHW
 *
 */
public interface MagneticQuerier {

	/**
	 * 输入坐标，返回该坐标在Fingerprint数据库中记录的磁场向量值
	 * @param x
	 * @param y
	 * @return
	 */
	public MagneticVector queryMagneticVectorByLoc(float x,float y);
}
