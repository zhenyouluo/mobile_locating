package com.maloc.server.localization;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.os.Environment;

import com.maloc.client.bean.MagneticVector;
import com.maloc.client.bean.RSSIData;
import com.maloc.client.util.FileLog;
import com.maloc.client.util.GlobalProperties;
import com.maloc.server.floormap.PositionValidator;
import com.maloc.server.magmap.MagneticQuerierEngine;

import com.maloc.server.particlefilter.AdaptiveRobustParticleFilter2;
import com.maloc.server.particlefilter.Particle;
import com.maloc.server.particlefilter.ParticleFilter;
import com.maloc.server.particlefilter.RobustParticleFilter;
import com.maloc.server.particlefilter.WeightComputer;

import com.maloc.server.wifi.WiFiLocalizationEngine;
import com.maloc.server.wifi.WiFiLocationQuerier;
import com.maloc.server.wifi.WiFiPosition;
/**
 * 地磁定位器
 * @author xhw Email:xxyx66@126.com
 */
public class MagneticLocalizor {

	private MagneticQuerierEngine magneticQuerier;//地磁指纹查询接口
	private float stepLen = GlobalProperties.STEPLENGTH;//默认步长
	//private int particleNum;
	private float noise[];//行为模型中高斯噪声
	private float initOrientation[];//初始朝向范围
	private WeightComputer wComputer;//权重评估接口
	private PositionValidator positionValidator;//位置合理验证
	private WiFiLocationQuerier wifiLocationQuerier;//WiFi指纹查询接口
	private ParticleFilter PF;//粒子滤波实体
	private float preGyroValue = 0;//前一时刻的朝向
	private MagneticVector preMagneticVector = null;//前一时刻磁场强度
	private Particle predict = null;//地磁定位结果
	//private Properties config;

	private int inConsistency = 0;//不匹配计数器
	private boolean restart=false;//重启标志
	private boolean positionInitialized=false;//位置初始化标志
	
	//Particle groundtruth[] = GroundTruthCreater.createParticlesOfWholePath(67);
	private int locCnt;//迭代次数计数器

	//public static final String fingerprintDir = "/usr/share/fingerprints";

	public static final String fingerprintDir=Environment.getExternalStorageDirectory().getPath()+"/"+
												GlobalProperties.FINGERPRINTS_BASE_DIR;

	public MagneticLocalizor(String magFile, String wifiFile, String configFile)
			throws FileNotFoundException, IOException {
		init(magFile, wifiFile, configFile);
	}

	public MagneticLocalizor(String location) throws FileNotFoundException,
			IOException {

		String magFile = fingerprintDir + "/" + location  + "magnetic_geo.txt";
		String wifiFile = fingerprintDir + "/" + location  + "wifi.txt";
		String configFile = fingerprintDir + "/" + location + "config";
		// String accFile=fingerprintDir+"/"+location+"/"+"acceleration.txt";
		init(magFile, wifiFile, configFile);
	}
	/**
	 * 导入指纹数据到内存中
	 * @param magData
	 * @param wifiData
	 */
	private void init(String magFile, String wifiFile, String configFile)
			throws FileNotFoundException, IOException {
		magneticQuerier = new MagneticQuerierEngine();
		magneticQuerier.addMapData(magFile);
		wifiLocationQuerier = new WiFiLocalizationEngine(wifiFile);
		//config = new Properties();
		//config.load(new FileReader(configFile));
	}
	/**
	 * 地磁定位，运行粒子滤波定位算法
	 * @param info
	 * @return
	 */
	public Particle localize(int stepCnt, List<MagneticVector> magList,List<Float> gyroList) {
		
		/*if(positionInitialized==false)
		{	
			this.predict=new Particle(Particle.NOT_INIT);
			return this.predict;
		
		}*/
		
		if (stepCnt != magList.size() || stepCnt != gyroList.size()) {
			FileLog.errorLog("sensor_error.txt", "Error stepCnt:" + stepCnt + "|"
					+ magList.size() + "|" + gyroList.size()+"\n");
		}
		int length = Math.min(stepCnt, magList.size());
		length = Math.min(length, gyroList.size());

		try {

			synchronized(this)
			{
				restart=false;
				for (int i = 0; i < length; i++) {
					if (preMagneticVector == null) {
						preGyroValue = gyroList.get(i);
						preMagneticVector = magList.get(i);
						continue;
					}
					locCnt++;
					float diff=(float)((gyroList.get(i) - preGyroValue));
					while(Math.abs(diff)>3.14)
					{
						if(diff>0)
							diff-=6.28;
						else
							diff+=6.28;
					}
					diff=-diff;
					predict = PF.update(this.stepLen, diff,preMagneticVector, magList.get(i));
					// double dis=predict.distance(this.groundtruth[locCnt]);
					/*System.out.print("update\t" + diff
							+ "\t" + preMagneticVector.module() + "\t"
							+ magList.get(i).module());
					System.out.println("\tpredict\t" + predict);*/
					preGyroValue = gyroList.get(i);
					preMagneticVector = magList.get(i);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			// this.initParticleFilter(particleNum, noise, initAngle, wComputer,
			// positionValidator);
			return null;
		}

		return predict;
	}
	/**
	 * WiFi定位功能
	 * @param rd
	 * @return
	 */
	public WiFiPosition localize(RSSIData rd) {
		if(positionInitialized==false)
			return null;
		WiFiPosition position = this.wifiLocationQuerier.queryLocationByRSSI(rd);
		if(position.getSameBssidNum()<GlobalProperties.MINIMUM_RSSI_NUM)
			return position;
		synchronized (this) {
			if (inConsistent(position.getPosition())) {

				//positionInitialized = false;
				restart = true;
				this.initPosition(position.getPosition(),(float)(Math.PI-rd.getOrientation()));
				this.predict.setX(position.getPosition()[0]);
				this.predict.setY(position.getPosition()[1]);
			}
		}
		return position;
	}
	/**
	 * 计算WiFi定位结果和地磁定位是否匹配
	 * @param position
	 * @return
	 */
	private boolean inConsistent(float[] position) {

		if (this.predict == null||GlobalProperties.Solution_WiFi==false)
			return false;
		double sum = 0;
		sum += (position[0] - predict.getX()) * (position[0] - predict.getX());
		sum += (position[1] - predict.getY()) * (position[1] - predict.getY());
		if (Math.sqrt(sum) > GlobalProperties.RANGE) {
			inConsistency++;
		} else if (inConsistency > 0) {
			inConsistency--;
		}

		if (inConsistency >= GlobalProperties.INCONSISTENCY) {
			inConsistency = 0;
			return true;
		}
		return false;
	}
	/**
	 * 初始化粒子滤波器
	 * @return
	 */
	public ParticleFilter initParticleFilter(float noise[],
			WeightComputer wComputer,
			PositionValidator positionValidator) {
		this.noise = noise;
		//this.initAngle = initAngle;
		this.wComputer = wComputer;
		this.positionValidator = positionValidator;
		//this.particleNum = particleNum;
		this.PF = new AdaptiveRobustParticleFilter2(noise[0], noise[1],
				noise[2], wComputer, GlobalProperties.currentFloor.getFloorWidth(),
				GlobalProperties.currentFloor.getFloorHeight());
		return PF;
	}
	/**
	 * 根据WiFi rssi先进行定位得到初始位置，然后运行地磁定位
	 * @param rd
	 * @return
	 */
	public WiFiPosition initPosition(RSSIData rd) {
		// float position[]={58.9f,11.1f};
		WiFiPosition position = this.wifiLocationQuerier.queryLocationByRSSI(rd);
		if(position.getSameBssidNum()<GlobalProperties.MINIMUM_RSSI_NUM)
			return position;
		initPosition(position.getPosition(),(float)(Math.PI-rd.getOrientation()));
		return position;
	}
	/**
	 * 根据初始位置和朝向开始运行地磁定位
	 * @param position
	 * @param orientation
	 */
	public void initPosition(float position[],float orientation) {
		predict = new Particle(position[0], position[1], orientation);
		locCnt = 0;
		float area[] = this.positionValidator.initArea(position);
		this.preMagneticVector = null;
		this.initOrientation=initOrientationRange(orientation);
		PF.setStepLen(GlobalProperties.STEPLENGTH);
		PF.initialize(GlobalProperties.PARTICLENUM, area[0], area[1], area[2], area[3],
				initOrientation[0], initOrientation[1], positionValidator);
		positionInitialized=true;
	}

	/**
	 * 初始朝向范围
	 * @param orientation
	 * @return
	 */
	private float[] initOrientationRange(float orientation) {

		this.initOrientation=new float[2];
		this.initOrientation[0]=orientation-GlobalProperties.orienNoise;
		this.initOrientation[1]=orientation+GlobalProperties.orienNoise;
		return this.initOrientation;
	}

	public MagneticQuerierEngine getQuerier() {
		return magneticQuerier;
	}

	public void setQuerier(MagneticQuerierEngine querier) {
		this.magneticQuerier = querier;
	}

	public float getStepLen() {
		return stepLen;
	}

	public void setStepLen(float stepLen) {
		this.stepLen = stepLen;
	}

	public MagneticQuerierEngine getMagneticQuerier() {
		return magneticQuerier;
	}

	public void setMagneticQuerier(MagneticQuerierEngine magneticQuerier) {
		this.magneticQuerier = magneticQuerier;
	}


	public float[] getNoise() {
		return noise;
	}

	public void setNoise(float[] noise) {
		this.noise = noise;
	}


	public WeightComputer getwComputer() {
		return wComputer;
	}

	public void setwComputer(WeightComputer wComputer) {
		this.wComputer = wComputer;
	}

	public PositionValidator getPositionValidator() {
		return positionValidator;
	}

	public void setPositionValidator(PositionValidator positionValidator) {
		this.positionValidator = positionValidator;
	}

	public WiFiLocationQuerier getWifiLocationQuerier() {
		return wifiLocationQuerier;
	}

	public void setWifiLocationQuerier(WiFiLocationQuerier wifiLocationQuerier) {
		this.wifiLocationQuerier = wifiLocationQuerier;
	}

	public ParticleFilter getPF() {
		return PF;
	}

	public void setPF(ParticleFilter pF) {
		PF = pF;
	}

	public float getPreGyroValue() {
		return preGyroValue;
	}

	public void setPreGyroValue(float preGyroValue) {
		this.preGyroValue = preGyroValue;
	}

	public MagneticVector getPreMagneticVector() {
		return preMagneticVector;
	}

	public void setPreMagneticVector(MagneticVector preMagneticVector) {
		this.preMagneticVector = preMagneticVector;
	}

	public static String getFingerprintdir() {
		return fingerprintDir;
	}

	public boolean isRestart() {
		return restart;
	}

	public void setRestart(boolean restart) {
		this.restart = restart;
	}

	public int getLocCnt() {
		return locCnt;
	}

	public void setLocCnt(int locCnt) {
		this.locCnt = locCnt;
	}
}
