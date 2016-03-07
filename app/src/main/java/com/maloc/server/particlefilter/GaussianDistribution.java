package com.maloc.server.particlefilter;

import Jama.Matrix;
/**
 * 高斯分布实现，已经弃用
 * @author xhw Email:xxyx66@126.com
 */
public class GaussianDistribution {
	public static double gaussian(double dVector[][],double hVector[][],Matrix R)
	{
		Matrix z=new Matrix(dVector);
		Matrix h=new Matrix(hVector);
		
		Matrix zMinusH=z.minus(h);
		Matrix temp=(zMinusH.transpose().times(R.inverse()).times(zMinusH));
		
		double p=(1/(Math.pow(2*Math.PI, dVector.length/2.0)*Math.sqrt(R.det())))*Math.exp(-0.5*temp.get(0, 0));
		return p;
	}

}
