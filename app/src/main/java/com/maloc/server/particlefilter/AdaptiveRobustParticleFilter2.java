package com.maloc.server.particlefilter;


import com.maloc.client.bean.MagneticVector;


/**
 * 自适应鲁棒粒子滤波算法
 * @author xhw Email:xxyx66@126.com
 */
public class AdaptiveRobustParticleFilter2 extends RobustParticleFilter{

	
	//bundle的大小
		private static final int BLOCK_SIZE=50;
		//滑动窗口的大小
		private static final int WINDOW_SIZE=4;
		//窗口内占据bundle的大小下限，低于或等于这个值后停止采样
		public static int BIN_INC_THRESHOLD=2;
	
	
	int bins[]=null;
	/**
	 * 构造函数
	 * @param weightComputer
	 * @param mapWidth
	 * @param mapHeight
	 */
	public AdaptiveRobustParticleFilter2(double xSigma, double ySigma,
			double thetaSigma, WeightComputer weightComputer,float mapWidth,float mapHeight) {
		super(xSigma, ySigma, thetaSigma, weightComputer,mapWidth,mapHeight);
		//System.out.println("AdaptiveRobustParticleFilter2");
	}
	
	@Override
	public Particle update(float length,float delta,MagneticVector pre,MagneticVector cur)
	{
		super.delta=delta;
		//System.out.print(delta+"\t");
		computeStepLength(length);
		motionpredict(stepLen,delta);
		computeWeight(pre,cur);
		resample();
		resultParticle=computeAverageParticle();
		//resultParticle.setMagVector(cur);
		//System.out.println(cur);
		//boolean isFail=failureDetect(resultParticle,delta);
		//computeDistances();
		return resultParticle;
		
	}
	
	@Override
	protected void resample() {

		resampleEID=super.buildDiscreteDistribution();
		weightedAverageStepLength();
		randomNum=heuristicResample();
		//System.out.print("random="+randomNum+"\t");
		
		resize();
		
	}
	
	protected int heuristicResample()
	{
		if(super.resultParticle!=null)
		{
			if(Math.abs(delta)<=Math.PI*2)
			{
				randomNum=(int) (particleNum*(Math.abs(delta)/Math.PI)*RANDOM_NUM_FACTOR);
				//System.out.print("delta="+delta+"\t");
			}
			else
			{
				randomNum=this.particleNum;
			}
			if(randomNum>=this.particleNum)
				randomNum=this.particleNum-BLOCK_SIZE;
			fillRandomParticles(0,randomNum,preList);
		}
		return randomNum;
	}
	/**
	 * 动态重采样
	 */
	private void resize()
	{
		int binSize=0;
		resetGrid();
		int maxBlockNum=(MAX_PARTICLE_NUM-randomNum)/BLOCK_SIZE;
		if(bins==null)
			bins=new int[MAX_PARTICLE_NUM/BLOCK_SIZE];
		int pi=randomNum;
		int block=1;
		for(;block<=maxBlockNum;block++)
		{
			int preBinSize=binSize;
			for(;pi<randomNum+block*BLOCK_SIZE;pi++)
			{
				int gen=resampleEID.sample();
				Particle p1=preList.get(pi);
				Particle p2=curList.get(gen);
				p1.copy(p2);
				binSize+=occupyNewBin(p1);
			}
			bins[block-1]=binSize-preBinSize;
			if(block>=WINDOW_SIZE)
			{
				int localBinSize=0;
				for(int i=1;i<=WINDOW_SIZE;i++)
				{
					localBinSize+=bins[block-i];
				}
				if(localBinSize<=BIN_INC_THRESHOLD)
					break;
			}
		}
		particleNum=pi;
		this.weightedRange=particleNum*5.0/MAX_PARTICLE_NUM;
		/*System.out.print("(");
		if(block>maxBlockNum)
			block--;
		for(int i=0;i<block;i++)
		{
			System.out.print(bins[i]+",");
		}
		System.out.print(")\t");*/
		//System.out.print(particleNum+"\t");
		
	}
}
