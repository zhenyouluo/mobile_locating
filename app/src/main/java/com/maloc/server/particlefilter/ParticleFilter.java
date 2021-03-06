package com.maloc.server.particlefilter;

import java.util.*;

import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;

import com.maloc.client.bean.MagneticVector;
import com.maloc.client.util.FileOperator;
import com.maloc.client.util.Median;
import com.maloc.server.floormap.PositionValidator;
import com.maloc.server.magmap.MagneticQuerier;
/**
 * 粒子滤波算法
 * @author xhw Email:xxyx66@126.com
 */
public class ParticleFilter {

	protected Particle resultParticle=null;
	protected int particleNum=0;
	protected static int MAX_PARTICLE_NUM=0;//最大粒子数目上限
	protected List<Particle> preList;//粒子群，更新前
	protected List<Particle> curList;//粒子群，更新后
	//private double xSigma=0.1,ySigma=0.1,thetaSigma=0.1;
	protected NormalDistribution xND;
	protected NormalDistribution yND;
	protected NormalDistribution thetaND;
	protected WeightComputer weightComputer;
	//protected MagneticQuerier magneticQuerier;
	protected UniformRealDistribution resamplerUD; 
	//初始化时粒子状态空间范围，xMin代表横坐标最小值，xMax代表横坐标最大值，yMin纵坐标最小值，yMax是纵坐标最大值，thetaMin代表朝向最小值，thetaMax代表朝向最大值
	protected float xMin,xMax,yMin,yMax,thetaMin,thetaMax;
	protected PositionValidator poistionValidator;
	//步长
	protected float stepLen=0;
	protected float delta=0;//朝向变化
	
	//protected Map<Integer,Integer> particleOriginMap=new HashMap<Integer,Integer>();
	protected int currentDivParticleNum=0;
	
	//protected double distance[];
	//定位误差评估
	protected double weightedRange=0;
	protected double clusterDegree=0;
	protected int spaceStateSize=0;
	protected double predictError=0;
	//protected Map<Particle,Integer> gridMap=new HashMap<Particle,Integer>();
	protected byte grid[][][] =null;//状态空间网格化
	
	/**
	 * 构造方法
	 * @param weightComputer，粒子权重评估接口
	 */
	public ParticleFilter(double xSigma,double ySigma,double thetaSigma,WeightComputer weightComputer)
	{
		xND=new NormalDistribution(0,xSigma);
		yND=new NormalDistribution(0,ySigma);
		thetaND=new NormalDistribution(0,thetaSigma);
		this.weightComputer=weightComputer;
		
		//System.out.println("ParticleFilter");
	}
	/**
	 * 初始化粒子群，在给定的初始范围内随机生成粒子
	 * @param xMin
	 * @param xMax
	 * @param yMin
	 * @param yMax
	 * @param thetaMin
	 * @param thetaMax
	 * @param positionValidator
	 */
	public void initialize(int pn,float xMin,float xMax,float yMin,float yMax,
			float[] thetas,PositionValidator positionValidator)
	{
		this.particleNum=pn;
		MAX_PARTICLE_NUM=pn;
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
	/**
	 * 随机生成粒子群
	 * @param pn
	 * @param xMin
	 * @param xMax
	 * @param yMin
	 * @param yMax
	 * @param thetaMin
	 * @param thetaMax
	 */
	protected void randomGenerateParticles(int pn, float xMin, float xMax,
			float yMin, float yMax, float thetas[]) {

		UniformRealDistribution xURD=new UniformRealDistribution(xMin,xMax);
		UniformRealDistribution yURD=new UniformRealDistribution(yMin,yMax);
		UniformIntegerDistribution thetaURD=new UniformIntegerDistribution(0,thetas.length-1);
		preList=new ArrayList<Particle>(pn);
		curList=new ArrayList<Particle>(pn);
		for(int i=0;i<pn;i++)
		{
			Particle p1=new Particle((float)xURD.sample(),(float)yURD.sample(),thetas[thetaURD.sample()]);
			
			if(!this.poistionValidator.isValidePosition(p1.x, p1.y))
			{
				i--;
				continue;
			}
			preList.add(p1);
			Particle p2=new Particle();
			curList.add(p2);
		}
		
	}
	
	public void initialize(int pn,float xMin,float xMax,float yMin,float yMax,
			float thetaMin,float thetaMax,PositionValidator positionValidator)
	{
		this.particleNum=pn;
		MAX_PARTICLE_NUM=pn;
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
	
	/*public void initialize(int pn,float x, float y, float r,float thetaMin,float thetaMax)
	{
		this.particleNum=pn;
		MAX_PARTICLE_NUM=pn;
		//distance=new double[particleNum];
		randomGenerateParticlesFromCircle(x,y,r,thetaMin,thetaMax);
		for(int i=0;i<this.particleNum;i++)
		{
			this.particleOriginMap.put(i, i);
		}
	}*/

	/*protected void randomGenerateParticlesFromCircle(float x, float y, float r,
			float thetaMin, float thetaMax) {

		
	}
*/
	protected void randomGenerateParticles(int pn, float xMin, float xMax,
			float yMin, float yMax, float thetaMin, float thetaMax) {

		UniformRealDistribution xURD=new UniformRealDistribution(xMin,xMax);
		UniformRealDistribution yURD=new UniformRealDistribution(yMin,yMax);
		UniformRealDistribution thetaURD=new UniformRealDistribution(thetaMin,thetaMax);
		preList=new ArrayList<Particle>(pn);
		curList=new ArrayList<Particle>(pn);
		
		for(int i=0;i<pn;i++)
		{
			Particle p1=new Particle((float)xURD.sample(),(float)yURD.sample(),(float)thetaURD.sample());
			
			if(!this.poistionValidator.isValidePosition(p1.x, p1.y))
			{
				i--;
				continue;
			}
			preList.add(p1);
			Particle p2=new Particle();
			curList.add(p2);
		}
		
	}
	
	/*public int getOriginDiverseParticleNum()
	{
		HashSet<Integer> set=new HashSet<Integer>();
		for(int key:this.particleOriginMap.keySet())
			set.add(this.particleOriginMap.get(key));
		return set.size();
	}
	*/
	/**
	 * 粒子群迭代更新
	 * @param length，步长
	 * @param delta，朝向变化
	 * @param pre，前一步磁场强度向量
	 * @param cur，当前不磁场强度向量
	 * @return 定位结果
	 */
	public Particle update(float length,float delta,MagneticVector pre,MagneticVector cur)
	{
		this.delta=delta;
		motionpredict(length,delta);
		computeWeight(pre,cur);
		resample();
		//computeDiversity();
		//saveParticles();
		//Particle ave=computeAverageParticle();
		resultParticle=computeWeightedParticle(this.particleNum);
		//computeDistances();
		return resultParticle;
		
	}
	
	/*
	protected Percentile percentile=new Percentile();
	protected void computeDistances() {
		double sum=0,max=0;
		int binSize=0;
		gridMap.clear();
		for(int i=0;i<this.particleNum;i++)
		{
			this.distance[i]=this.resultParticle.distance(this.preList.get(i));
			//sum+=this.distance[i];
			if(this.distance[i]>max)
				max=this.distance[i];
			Integer value=this.gridMap.get(this.preList.get(i));
			if(value==null)
			{
				gridMap.put(this.preList.get(i), 1);
				binSize++;
			}
			else
			{
				if(value==3)
					binSize++;
				gridMap.put(this.preList.get(i), value+1);
			}
		}
		//sum/=this.particleNum;
		this.weightedRange=percentile.evaluate(distance,90);
		this.clusterDegree=max;
		this.spaceStateSize=binSize;
	}*/

	/*public Particle update(float length,float delta,float nextDelta,MagneticVector pre,MagneticVector cur)
	{
		this.delta=Math.abs(nextDelta);
		motionpredict(length,delta);
		computeWeight(pre,cur);
		resample();
		resultParticle=computeWeightedParticle(this.particleNum);
		return resultParticle;
		
	}*/

	Median<Particle> median=new Median<Particle>();
	
	protected void computeDiversity() {

		double max=0,ave=0;
		int counter=0;
		for(int i=0;i<this.particleNum;i++)
		{
			if(this.preList.get(i).weight>max)
			{
				max=this.preList.get(i).weight;
			}
		}
		
		Particle p=median.getMedian(preList, particleNum);
		
		for(int i=0;i<this.particleNum;i++)
		{
		
			if(this.preList.get(i).weight>0)
			{
				counter++;
				ave+=this.preList.get(i).weight;
			}
		}
		ave/=counter;
		System.out.println(5*(max-ave)/ave);
		
	}
	/**
	 * 计算权重最大的num个粒子的加权平均值
	 * @param num，最大的num个粒子
	 * @return，加权平均粒子
	 */
	protected Particle computeWeightedParticle(int num) {

		Particle ps[]=new Particle[num];
		for(int i=0;i<ps.length;i++)
		{
			ps[i]=new Particle(0,0,0);
			ps[i].weight=0;
		}
			
		for(int i=0;i<preList.size();i++)
		{
			Particle p=preList.get(i);
			for(int j=0;j<num;j++)
			{
				if(ps[j].weight<p.weight)
				{
					ps[j].copy(p);
					break;
				}
			}
		}
		float sumX=0l,sumY=0l,sumTheta=0l,sumWei=0l;
		for(int i=0;i<num;i++)
		{
			Particle p1=ps[i];
			sumX+=p1.x*p1.weight;
			sumY+=p1.y*p1.weight;
			sumTheta+=p1.theta*p1.weight;
			sumWei+=p1.weight;
		}
		sumX/=sumWei;
		sumY/=sumWei;
		sumTheta/=sumWei;
		Particle p=new Particle(sumX,sumY,sumTheta);
		p.weight=sumWei;
		return p;
	}

	protected void saveParticles() {

		String file="./data/particle/"+System.currentTimeMillis()+".txt";
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<preList.size();i++)
		{
			sb.append(preList.get(i).toString()+"\n");
			
		}
		FileOperator.write(file, sb.toString());
	}
	/**
	 * 计算粒子群的加权平均值
	 * @return
	 */
	protected Particle computeAverageParticle() {

		float sumX=0,sumY=0,sumTheta=0,sumWei=0;
		for(int i=0;i<particleNum;i++)
		{
			Particle p1=preList.get(i);
			sumX+=p1.x*p1.weight;
			sumY+=p1.y*p1.weight;
			sumTheta+=p1.theta*p1.weight;
			sumWei+=p1.weight;
		}
		sumX/=sumWei;
		sumY/=sumWei;
		sumTheta/=sumWei;
		Particle p=new Particle(sumX,sumY,sumTheta);
		p.weight=sumWei;
		
		return p;
	}
	/**
	 * 根据行为模型进行粒子群的更新
	 * @param length，步长
	 * @param delta，朝向变化
	 */
	protected void motionpredict(float length,float delta) {

		for(int i=0;i<particleNum;i++)
		{
			Particle p1=preList.get(i);
			Particle p2=curList.get(i);
			p2.x=(float) (p1.x+length*Math.sin(p1.theta+delta)+xND.sample());
			p2.y=(float)(p1.y+length*Math.cos(p1.theta+delta)+yND.sample());
			p2.theta=(float) (p1.theta+delta+thetaND.sample());
		}
	}

	protected EnumeratedIntegerDistribution buildDiscreteDistribution()
	{
		int index[]=new int[particleNum];
		double probability[]=new double[particleNum];		//randomNum=0;
		for(int i=0;i<particleNum;i++)
		{
			index[i]=i;
			probability[i]=curList.get(i).weight;
			if(probability[i]>0)
				probability[i]=probability[i];
			else
				probability[i]=0;
		}
		return new EnumeratedIntegerDistribution(index,probability);
	}
	
	/**
	 * resample by discrete distribution 
	 */
	protected void resample() {

		HashSet<Integer> set=new HashSet<Integer>();
		//Map<Integer,Integer> map=new HashMap<Integer,Integer>();
		EnumeratedIntegerDistribution EID=this.buildDiscreteDistribution();
		for(int i=0;i<particleNum;i++)
		{
			int gen=EID.sample();
			set.add(gen);
			//map.put(i, this.particleOriginMap.get(gen));
			Particle p1=preList.get(i);
			Particle p2=curList.get(gen);
			p1.copy(p2);
		}
		this.currentDivParticleNum=set.size();
		//this.particleOriginMap=map;
	}
	
	protected void lowVarianceResample()
	{
		double aveweight=averageWeight(curList);
		this.resamplerUD=new UniformRealDistribution(0,aveweight);
		preList=new ArrayList<Particle>();
		double r=resamplerUD.sample();
		Particle p=curList.get(0);
		double c=p.weight;
		int i=0;
		for(int m=0;m<this.particleNum;m++)
		{
			double u=r+(m-1)*aveweight;
			while(u>c)
			{
				i=(i+1)%this.particleNum;
				c=c+curList.get(i).weight;
			}
			preList.add(curList.get(i));
		}
		
	}

	protected double averageWeight(List<Particle> curList2) {

		double sum=0;
		for(int i=0;i<this.particleNum;i++)
		{
			Particle p=curList2.get(i);
			sum+=p.weight;
		}
		return sum/this.particleNum;
	}
	/**
	 * 计算每个粒子的权重
	 * @param pre
	 * @param cur
	 */
	protected void computeWeight(MagneticVector pre,MagneticVector cur) {

		
		for(int i=0;i<particleNum;i++)
		{
			Particle p1=preList.get(i);
			Particle p2=curList.get(i);
			double weight=this.weightComputer.computeWeight(p1, p2,pre,cur);
			if(weight>=0)
			{
				
			}
			else
			{
				weight=0;
			}
			p2.weight=weight;
			
		}
		//normalize(curList);
	}

	public void normalize(List<Particle> list) {

		double sum=0;
		for(int i=0;i<particleNum;i++)
		{
			sum+=list.get(i).weight;
		}
		for(int i=0;i<particleNum;i++)
		{
			Particle p2=list.get(i);
			p2.weight/=sum;
		}
	}

	public WeightComputer getWeightComputer() {
		return weightComputer;
	}

	public void setWeightComputer(WeightComputer weightComputer) {
		this.weightComputer = weightComputer;
	}

	public int getCurrentDivParticleNum() {
		return currentDivParticleNum;
	}

	public void setCurrentDivParticleNum(int currentDivParticleNum) {
		this.currentDivParticleNum = currentDivParticleNum;
	}

	public double getWeightedRange() {
		return weightedRange;
	}

	public void setWeightedRange(double weightedRange) {
		this.weightedRange = weightedRange;
	}

	public double getClusterDegree() {
		return clusterDegree;
	}

	public void setClusterDegree(double maxRange) {
		this.clusterDegree = maxRange;
	}

	

	public float getStepLen() {
		return stepLen;
	}

	public void setStepLen(float stepLen) {
		this.stepLen = stepLen;
	}

	public int getSpaceStateSize() {
		return spaceStateSize;
	}

	public int getParticleNum() {
		return particleNum;
	}

	public void setParticleNum(int particleNum) {
		this.particleNum = particleNum;
	}

	public double getPredictError() {
		return predictError;
	}

	public void setPredictError(double predictError) {
		this.predictError = predictError;
	}

	
	
}
