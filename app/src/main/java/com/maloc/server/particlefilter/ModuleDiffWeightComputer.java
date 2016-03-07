package com.maloc.server.particlefilter;

import com.maloc.client.bean.MagneticVector;
import com.maloc.server.magmap.MagneticQuerier;

import Jama.Matrix;
/**
 * 粒子权重评估，根据地磁指纹向量模值的差值进行权重评估
 * @author xhw Email:xxyx66@126.com
 */
public class ModuleDiffWeightComputer implements WeightComputer{

	private Matrix R;
	private MagneticQuerier mQuerier;
	/**
	 * 构造方法
	 * @param r, 协方差
	 * @param querier, 地磁指纹查询接口
	 * @param mapWidth, 室内地图宽度
	 * @param mapHeight, 室内地图高度
	 */
	public ModuleDiffWeightComputer (double r[][],MagneticQuerier querier)
	{
		R=new Matrix(r);
		this.mQuerier=querier;
	}
	
	@Override
	public double computeWeight(Particle preP,Particle curP,MagneticVector preM,MagneticVector curM) {

		if(curP.x<0||curP.y<0)
			return 0;
		MagneticVector vectorCur=mQuerier.queryMagneticVectorByLoc(curP.x, curP.y);
		if(vectorCur==null)
			return 0;
		
		if(preP==null||preM==null)
		{
			double dVector[][]=new double[1][1];
			double hVector[][]=new double[1][1];
			dVector[0][0]=curM.module();
			hVector[0][0]=vectorCur.module();
			double r=R.get(0, 0);
			R=new Matrix(new double[1][1]);
			R.set(0, 0, r);
			return GaussianDistribution.gaussian(dVector,hVector,R);
		}
		
		MagneticVector vectorPre=mQuerier.queryMagneticVectorByLoc(preP.x, preP.y);
		if(vectorCur==null||vectorPre==null)
			return 0;
		double dVector[][]=new double[1][1];
		double hVector[][]=new double[1][1];
		
		dVector[0][0]=curM.module()-preM.module();
		
		hVector[0][0]=vectorCur.module()-vectorPre.module();
		
		return GaussianDistribution.gaussian(dVector,hVector,R);
		
	}

	@Override
	public MagneticQuerier getMagneticQuerier() {
		return this.mQuerier;
	}
	
	

}
