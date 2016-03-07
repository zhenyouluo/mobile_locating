package com.maloc.server.magmap;

import java.util.*;
import java.io.*;

import com.maloc.client.bean.MagneticVector;

/**
 * 读取地磁指纹文件，导入到内存中，生成方面查询的数据结构
 * @author xhw Email:xxyx66@126.com
 */
public class MagneticMapGenerator {
	// meter
	private double maxDis=1;
	private int unit=1;
	/**
	 * 单元区域->数据
	 */
	private Map<String,List<LocMagPair>> map=new HashMap<String,List<LocMagPair>>();
	/**
	 * 方向+起始位置->数据,南北方向x,y,东西方向y,x
	 */
	private Map<String,List<LocMagPair>> directionMap=new TreeMap<String,List<LocMagPair>>();
	/**
	 * 读取地磁指纹文件，导入到map中
	 * @param data,地磁文件的内容
	 * @return 地磁收集的线路数量
	 * @throws IOException
	 */
	public int process(String filename) throws IOException
	{
		int line=0;
		String location[]=null;
		BufferedReader in=new BufferedReader(new FileReader(filename));
		String str=in.readLine();
		List<String> list=new ArrayList<String>();
		while(str!=null&&str.length()>1)
		{
			if(str.startsWith("#"))
			{
				if(line>0)
					generate(location,list);
				location=str.split(" ");
				list=new ArrayList<String>();
				line++;
			}
			else
			{
				list.add(str);
			}
			str=in.readLine();
		}
		generate(location,list);
		
		System.out.println("without interpolation");
		return line;
	}

	
	/**
	 * 将一条路线上数据导入到map中
	 * @param location 路线的起点和重点
	 * @param list 路线上的地磁强度
	 */
	private void generate(String[] location, List<String> list) {

		int size=list.size();
		float points[]=new float[4];
		//x1,y1;x2,y2;
		for(int i=0;i<4;i++)
		{
			points[i]=Float.parseFloat(location[i+1]);
		}
		
		double xlen=points[2]-points[0];
		if(xlen==0)
		{
			double p=(points[3]-points[1])/size;
			double ny=0;
			for(int i=0;i<size;i++)
			{
				float x=points[0];
				float y=(float) (points[1]+ny);
				generateLocMagPair(x,y,list.get(i));
				ny+=p;
			}
		}
		
		double p=xlen/size;
		double nx=0;
		double k=(points[3]-points[1])/xlen;
		for(int i=0;i<size;i++)
		{
			float x=(float) (points[0]+nx);
			float y=(float) (points[1]+nx*k);
			generateLocMagPair(x,y,list.get(i));
			nx+=p;
		}
		
	}
	/**
	 * 将磁场强度和位置合成为LocMagPair,存入map中
	 * @param x，横坐标
	 * @param y，纵坐标
	 * @param str，地磁强度
	 * @return LocMagPair
	 */
	private LocMagPair generateLocMagPair(float x, float y, String str) {

		String terms[]=str.split("\t");
		if(terms.length<3)
		{
			System.err.println("Magnetic reverse failed!");
			return null;
		}
		float vector[]=new float[3];
		
		for(int i=0;i<3;i++)
		{
			vector[i]=Float.parseFloat(terms[i+1]);
		}
		
		LocMagPair lmp=new LocMagPair(x,y,new MagneticVector(vector));
		
		String key=((int)x/unit)+","+((int)y/unit);
		if(this.map.containsKey(key))
		{
			this.map.get(key).add(lmp);
		}
		else
		{
			List<LocMagPair> list=new ArrayList<LocMagPair>();
			list.add(lmp);
			this.map.put(key, list);
		}
		
		return lmp;
	}

	public Map<String, List<LocMagPair>> getMap() {
		return map;
	}

	public void setMap(Map<String, List<LocMagPair>> map) {
		this.map = map;
	}



	public Map<String, List<LocMagPair>> getDirectionMap() {
		return directionMap;
	}



	public void setDirectionMap(Map<String, List<LocMagPair>> directionMap) {
		this.directionMap = directionMap;
	}
	
	

}
