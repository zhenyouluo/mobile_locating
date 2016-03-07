package com.maloc.server.particlefilter;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;

import com.maloc.client.bean.MagneticVector;
import com.maloc.server.floormap.PositionValidator;
import com.maloc.server.magmap.MagneticQuerier;
/**
 * 鲁棒性增强的粒子滤波
 * @author xhw Email:xxyx66@126.com
 */
public class RobustParticleFilter extends ParticleFilter{

	protected int randomNum=0;//启发式随机粒子的数量
	//protected float preStepLen=0;
	//估计步长的缓存队列
	protected LinkedList<Float> stepLenQueue=new LinkedList<Float>();
	//缓存大小
	protected int cacheSize=5;
	protected EnumeratedIntegerDistribution resampleEID=null;//重采样过程中离散分布
	public static float ORIENTATION_CHANGE_THRESH=0.1f;//朝向变化上限，小于这个上限时进行动态步长估计
	public static float RANDOM_NUM_FACTOR=0.55f;//启发式重采样敏感系数
	public static float RANDOM_RANGE=3.0f;//随机粒子的分布范围
	public static int RANDOM_SAMPLE_MAX_CNT=10;
	
	public static final int MIN_SAMPLE_EACH_BIN=2;//占据一个bin所需要的粒子数量
	public static final double BIN_WIDTH=0.5;//bin对应状态x,y的长度
	public static final int BIN_DEPTH=30;//bin对应状态空间的朝向theta的大小
	
	public static final int RESULT_CACHE_SIZE=10;
	
	protected int refreshGridX[],refreshGridY[],refreshGridT[];//记录被占据的bin
	protected int refreshNum=0;//记录占据的bin的数量
	protected int valideBins[];
	
	//protected LinkedList<Particle> resultQueue=new LinkedList<Particle>();
	//protected LinkedList<Float> deltaQueue=new LinkedList<Float>();
	
	/**
	 * 构造方法
	 * @param weightComputer
	 * @param mapWidth
	 * @param mapHeight
	 */
	public RobustParticleFilter(double xSigma, double ySigma,
			double thetaSigma, WeightComputer weightComputer,
			float mapWidth,float mapHeight) {
		super(xSigma, ySigma, thetaSigma, weightComputer);
		grid=new byte[(int)((mapWidth+1)/BIN_WIDTH)][(int)((mapHeight+1)/BIN_WIDTH)][360/BIN_DEPTH];
		//this.magneticQuerier=magneticQuerier;
		//System.out.println("RobustParticleFilter");
	}

	public void initialize(int pn,float xMin,float xMax,float yMin,float yMax,
			float[] thetas,PositionValidator positionValidator)
	{
		this.particleNum=pn;
		MAX_PARTICLE_NUM=pn;
		refreshGridX=new int[MAX_PARTICLE_NUM];
		refreshGridY=new int[MAX_PARTICLE_NUM];
		refreshGridT=new int[MAX_PARTICLE_NUM];
		valideBins=new int[MAX_PARTICLE_NUM];
		Arrays.fill(valideBins, 0);
		//distance=new double[particleNum];
		this.xMin=xMin;
		this.xMax=xMax;
		this.yMin=yMin;
		this.yMax=yMax;
		this.poistionValidator=positionValidator;
		
		/*for(int i=0;i<this.particleNum;i++)
		{
			this.particleOriginMap.put(i, i);
		}*/
		randomGenerateParticles(pn,xMin,xMax,yMin,yMax,thetas);
	}
	
	public void initialize(int pn,float xMin,float xMax,float yMin,float yMax,
			float thetaMin,float thetaMax,PositionValidator positionValidator)
	{
		this.particleNum=pn;
		MAX_PARTICLE_NUM=pn;
		refreshGridX=new int[MAX_PARTICLE_NUM];
		refreshGridY=new int[MAX_PARTICLE_NUM];
		refreshGridT=new int[MAX_PARTICLE_NUM];
		valideBins=new int[MAX_PARTICLE_NUM];
		Arrays.fill(valideBins, 0);
		//distance=new double[particleNum];
		this.xMin=xMin;
		this.xMax=xMax;
		this.yMin=yMin;
		this.yMax=yMax;
		this.thetaMin=thetaMin;
		this.thetaMax=thetaMax;
		this.poistionValidator=positionValidator;
		
		/*for(int i=0;i<this.particleNum;i++)
		{
			this.particleOriginMap.put(i, i);
		}*/
		
		randomGenerateParticles(pn,xMin,xMax,yMin,yMax,thetaMin,thetaMax);
	}
	
/*	public void initialize(int pn,float x, float y, float r,float thetaMin,float thetaMax)
	{
		this.particleNum=pn;
		MAX_PARTICLE_NUM=pn;
		refreshGridX=new int[MAX_PARTICLE_NUM];
		refreshGridY=new int[MAX_PARTICLE_NUM];
		refreshGridT=new int[MAX_PARTICLE_NUM];
		valideBins=new int[MAX_PARTICLE_NUM];
		Arrays.fill(valideBins, 0);
		//distance=new double[particleNum];
		randomGenerateParticlesFromCircle(x,y,r,thetaMin,thetaMax);
		for(int i=0;i<this.particleNum;i++)
		{
			this.particleOriginMap.put(i, i);
		}
	}*/
	
	public Particle update(float length,float delta,MagneticVector pre,MagneticVector cur)
	{
		super.delta=delta;
		computeStepLength(length);
		System.out.print(stepLen+"\t");
		motionpredict(stepLen,delta);
		computeWeight(pre,cur);
		resample();
		resultParticle=computeAverageParticle();
		//resultParticle.setMagVector(cur);
		System.out.println(cur);
		//failureDetect(resultParticle,delta);
		//computeDistances();
		return resultParticle;
		
	}
	
	/**
	 * 动态步长估计
	 * @param length
	 * @return
	 */

	public float computeStepLength(float length)
	{
		
		if(super.stepLen==0)
		{
			stepLen=length;
		}
		
		if(stepLenQueue.size()<cacheSize)
		{
			stepLenQueue.add(stepLen);
		}
		
		else
		{
			stepLenQueue.add(stepLen);
			stepLen=mean(stepLenQueue);
			//stepLen=mean(stepLenQueue);
			stepLenQueue.poll();
			
		}
		//System.out.print(stepLen+"\t");
		return stepLen;
	}
	/**
	 * 求平均值
	 * @param LList
	 * @return
	 */
	private float mean(LinkedList<Float> LList) {

		float sum=0;
		for(int i=0;i<LList.size();i++)
			sum+=LList.get(i);
		sum/=LList.size();
		return sum;
	}
	

	/*@Override
	protected void computeDistances() {
		int binSize=0;
		resetGrid();
		for(int i=0;i<this.particleNum;i++)
		{
			Particle pi=this.preList.get(i);
			this.distance[i]=this.resultParticle.distance(pi);
			binSize+=occupyNewBin(pi);
		}
		this.spaceStateSize=binSize;
		this.weightedRange=percentile.evaluate(distance,0,particleNum,95);
		//this.weightedRange=this.weightedRange*this.weightedRange*Math.PI;
		
		int max=0;
		int tmp=0;
		for(int i=0;i<refreshNum;i++)
		{
			tmp=grid[refreshGridX[i]][refreshGridY[i]][refreshGridT[i]];
			valideBins[tmp]++;
			if(tmp>max)
			{
				max=tmp;
			}
		}
		tmp=0;
		boolean flag=false;
		int binCnt=0;
		for(int i=max;i>=1;i--)
		{
			for(int j=0;j<valideBins[i];j++)
			{
				tmp+=i;
				binCnt++;
				if(tmp>(this.particleNum)*0.8)
				{
					this.clusterDegree=binCnt;
					flag=true;
					break;
				}
			}
			if(flag)
				break;
			
		}
		Arrays.fill(valideBins,0, max,0);
		
	}*/
	/**
	 * 将粒子放进空间桶里
	 * @param p1
	 * @return
	 */
	protected int occupyNewBin(Particle p1)
	{
		int hx=(int) (p1.x/BIN_WIDTH);
		int hy=(int) (p1.y/BIN_WIDTH);
		int theta=((int)(p1.theta/Math.PI*180))%360;
		if(theta<0)
			theta+=360;
		theta/=BIN_DEPTH;
		if(hx<0||hy<0||theta<0)
			return 0;
		if(grid[hx][hy][theta]==0)
		{
			refreshGridX[refreshNum]=hx;
			refreshGridY[refreshNum]=hy;
			refreshGridT[refreshNum]=theta;
			refreshNum++;
		}
		grid[hx][hy][theta]++;
		if(grid[hx][hy][theta]==MIN_SAMPLE_EACH_BIN)
		{
			return 1;
		}
		else if(grid[hx][hy][theta]>=Byte.MAX_VALUE)
		{
			grid[hx][hy][theta]--;
		}
		return 0;
	}
	/**
	 * 重置所有空间桶为未被占有状态
	 */
	protected void resetGrid() {

		for(int i=0;i<refreshNum;i++)
				grid[refreshGridX[i]][refreshGridY[i]][refreshGridT[i]]=0;
		refreshNum=0;
	}
	/**
	 * 计算粒子群重采样加权平均步长
	 */
	protected void weightedAverageStepLength()
	{
		if(Math.abs(delta)<ORIENTATION_CHANGE_THRESH)
		{
			float len=0,sumW=0;
			for(int i=0;i<particleNum;i++)
			{
				int gen=resampleEID.sample();
				Particle p1=preList.get(gen);
				Particle p2=curList.get(gen);
				len+=p1.distance(p2)*p2.weight;
				sumW+=p2.weight;
			}
			super.stepLen=len/sumW;
		}
	}
	/**
	 * 启发式重采样算法
	 * @return 随机启发粒子数目
	 */
	protected int heuristicResample()
	{
		if(super.resultParticle!=null)
		{
			if(Math.abs(delta)<=Math.PI*2)
			{
				randomNum=(int) (particleNum*(Math.abs(delta)/Math.PI)*RANDOM_NUM_FACTOR);
				
			}
			else
			{
				randomNum=this.particleNum;
			}
			if(randomNum>=this.particleNum)
				randomNum=this.particleNum-50;
			fillRandomParticles(0,randomNum,preList);
		}
		return randomNum;
	}
	/**
	 * 重采样
	 */
	protected void resample() {

		resampleEID=super.buildDiscreteDistribution();
		weightedAverageStepLength();
		//randomNum=0;
		randomNum=heuristicResample();
		//System.out.print("random="+randomNum+"\t");
		for(int i=randomNum;i<particleNum;i++)
		{
			int gen=resampleEID.sample();
			Particle p1=preList.get(i);
			Particle p2=curList.get(gen);
			p1.copy(p2);
		}
	}
	/**
	 * 启发式重采样
	 * @param start，粒子群队列的起点
	 * @param randomNum，随机启发粒子数量
	 * @param preList2，要填充的粒子群队列
	 * @return
	 */
	protected void fillRandomParticles(int start, int randomNum,
			List<Particle> preList2) {

		UniformRealDistribution xURD=new UniformRealDistribution(super.resultParticle.x-RANDOM_RANGE,super.resultParticle.x+RANDOM_RANGE+0.0001);
		UniformRealDistribution yURD=new UniformRealDistribution(super.resultParticle.y-RANDOM_RANGE,super.resultParticle.y+RANDOM_RANGE+0.0001);
		UniformRealDistribution thetaURD=null;
		if(delta>=0)
		{
			thetaURD=new UniformRealDistribution(super.resultParticle.theta,super.resultParticle.theta+2*delta+0.00001);
		}
		else
		{
			thetaURD=new UniformRealDistribution(super.resultParticle.theta+2*delta,super.resultParticle.theta+0.00001);
		}
		int counter=0;
		int miss=0;
		for(int i=start;i<randomNum;i++)
		{
			float x=(float)xURD.sample();
			float y=(float)yURD.sample();
			/*Particle p1=new Particle((float)xURD.sample(),(float)yURD.sample(),(float)thetaURD.sample());
			p1.weight=0;*/
			if(super.poistionValidator.isValidePosition(x ,y))
			{
				//preList2.get(i).copy(p1);
				preList2.set(i, new Particle(x,y,(float)thetaURD.sample()));
				counter=0;
			}
			else
			{
				counter++;
				if(counter<RANDOM_SAMPLE_MAX_CNT)
					i--;
				else
				{
					//preList2.get(i).copy(super.resultParticle);
					preList2.get(i).setX((float) (resultParticle.x+xND.sample()));
					preList2.get(i).setY((float) (resultParticle.y+yND.sample()));
					preList2.get(i).setTheta((float)thetaURD.sample());
					preList2.get(i).setWeight(0);
					counter=0;
					miss++;
				}
			}
		}
		//System.out.print(miss+"\t");
	}
	
	
	/*
	protected void computeDistances() {
		//double max=0;
		int binSize=0;
		resetGrid();
		for(int i=0;i<this.particleNum;i++)
		{
			Particle pi=this.preList.get(i);
			this.distance[i]=this.resultParticle.distance(pi);
			//sum+=this.distance[i];
			//if(this.distance[i]>max)
			//	max=this.distance[i];
			binSize+=occupyNewBin(pi);
		}
		//sum/=this.particleNum;
		this.weightedRange=percentile.evaluate(distance,0,particleNum,95);
		//this.weightedRange=this.weightedRange*this.weightedRange*Math.PI;
		
		this.spaceStateSize=binSize;
		
		for(int i=0;i<refreshNum;i++)
		{
			valideBins[i]=-grid[refreshGridX[i]][refreshGridY[i]][refreshGridT[i]];
		}
		Arrays.sort(valideBins,0,refreshNum);
		int tmp=0;
		for(int i=0;i<refreshNum;i++)
		{
			tmp+=(-valideBins[i]);
			if(tmp>this.particleNum*0.8)
			{
				this.clusterDegree=i*1.0/this.spaceStateSize;
				break;
			}
		}
		
	}
	
	*/
	
}
