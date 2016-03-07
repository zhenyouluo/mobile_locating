package com.maloc.server.magmap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.maloc.client.bean.MagneticVector;

/**
 * 磁场强度查询引擎，实现了MagneticQurier接口
 * @author XHW
 *
 */
public class MagneticQuerierEngine implements MagneticQuerier{

	private MagneticMapGenerator generator;
	
	public MagneticQuerierEngine()
	{
		generator=new MagneticMapGenerator();
	}
	
	public MagneticQuerierEngine(String filename)
	{
		generator=new MagneticMapGenerator();
		this.addMapData(filename);
	}
	
	public MagneticQuerierEngine(MagneticMapGenerator generator)
	{
		this.generator=generator;
	}
	
	/**
	 * 添加收集的磁场数据，导入Fingerprint数据库中
	 * @param filename
	 */
	public void addMapData(String filename)
	{
		try {
			this.generator.process(filename);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public MagneticVector queryMagneticVectorByLoc(float x, float y) {

		String key=((int)x)+","+((int)y);
		List<LocMagPair> list=this.generator.getMap().get(key);
		if(list==null)
			return null;
		/*int index[]={0,-1,1};
		int i=0,j=0;
		while(list==null||(i>2&&j>2))
		{
			
			key=((int)x+index[i])+","+((int)y+index[j]+1);
			list=this.generator.getMap().get(key);
				
		}*/
		LocMagPair lmp=getCloseMagLocPair(list,x,y);
		return lmp.vector;
	}

	private LocMagPair getCloseMagLocPair(List<LocMagPair> list, float x, float y) {

		LocMagPair min=list.get(0);
		for(int i=1;i<list.size();i++)
		{
			if(list.get(i).distance(x, y)<min.distance(x, y))
			{
				min=list.get(i);
			}
		}
		return min;
	}

	public MagneticMapGenerator getGenerator() {
		return generator;
	}

	public void setGenerator(MagneticMapGenerator generator) {
		this.generator = generator;
	}
	
	public List<LocMagPair> generateLineTestSet(float steplen,float x,float y,char direction,int num)
	{
		List<LocMagPair> list=new ArrayList<LocMagPair>();
		
		for(int i=0;i<num;i++)
		{
			MagneticVector v=this.queryMagneticVectorByLoc(x, y);
			LocMagPair lmp=new LocMagPair(x,y,v);
			list.add(lmp);
			switch(direction)
			{
			case 'N':
				y+=steplen;
				break;
			case 'E':
				x+=steplen;
				break;
			case 'S':
				y-=steplen;
				break;
			case 'W':
				x-=steplen;
				break;
			}
			
		}
		return list;
		
	}

}
