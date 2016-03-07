package com.maloc.server.particlefilter;

import com.maloc.client.bean.MagneticVector;
import com.maloc.server.magmap.MagneticQuerier;
/**
 * 粒子权重评估接口
 * @author xhw Email:xxyx66@126.com
 */
public interface WeightComputer {
	/**
	 * 获取地磁指纹查询接口
	 * @return 地磁指纹查询接口MagneticQuerier
	 */
	public MagneticQuerier getMagneticQuerier();
	/**
	 * 计算权重
	 * @param preP, 前一时刻粒子
	 * @param curP, 当前时刻粒子
	 * @param preM, 前一时刻磁场强度读数
	 * @param curM, 当前时刻磁场强度读数
	 * @return
	 */
	public double computeWeight(Particle preP,Particle curP,MagneticVector preM,MagneticVector curM);
}
