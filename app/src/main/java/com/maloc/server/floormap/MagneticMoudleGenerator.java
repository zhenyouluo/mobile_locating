package com.maloc.server.floormap;

import java.util.*;
import java.io.*;

import com.maloc.server.magmap.LocMagPair;
import com.maloc.server.magmap.MagneticQuerierEngine;
/**
 * 测试工具
 * @author xhw
 *
 */
public class MagneticMoudleGenerator {
	
	public static void main(String args[])
	{
		MagneticQuerierEngine querier=new MagneticQuerierEngine();
		querier.addMapData("./data/2013-10-24_02-45-32/magnetic_geo.txt");
		Map<String,List<LocMagPair>> map=querier.getGenerator().getDirectionMap();
		float v[]={0,1,0};
		String k=map.keySet().iterator().next();
		LocMagPair mv=map.get(k).get(0);
		for(String key:map.keySet())
		{
			List<LocMagPair> list=map.get(key);
			for(int i=0;i<list.size();i++)
			{
				//System.out.print(list.get(i).getVector().getVector()[2]+"\t");
				System.out.print(list.get(i).getVector().angle(mv.getVector())+"\t");
			}
			System.out.println();
		}
	}


}