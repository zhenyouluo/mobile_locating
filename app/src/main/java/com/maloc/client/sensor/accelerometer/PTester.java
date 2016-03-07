package com.maloc.client.sensor.accelerometer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

//import org.junit.Test;

import com.maloc.client.util.FileOperator;
import com.maloc.client.util.GlobalProperties;
/**
 * Pedometer单元测试
 * @author xhw
 *
 */
/*
public class PTester {
	
	
	public static String readToString(File file) throws IOException
	{
		StringBuilder sb=new StringBuilder();
		BufferedReader reader=null;
		try {
			reader=new BufferedReader(new FileReader(file));
			String s=reader.readLine();
			while(s!=null)
			{
				sb.append(s).append("\n");
				s=reader.readLine();
			}
		
		} finally
		{
			if(reader!=null)
				reader.close();
		}
		return sb.toString();
	}
	
	public static String readToString(String filename) throws IOException
	{
		return readToString(new File(filename));
	}
	public static void main(String args[]) throws IOException
	{
		PTester pt=new PTester();
		pt.testListPeak();
	}
	
	@Test
	public void testListPeak() throws IOException
	{
		String str=readToString("C:\\Users\\xianping Tao\\Desktop\\accAll.txt");
		String lines[]=str.split("\n");
		float acc[]=new float[lines.length];
		long timestamp[]=new long[lines.length];
		for(int i=0;i<acc.length;i++)
		{
			String terms[]=lines[i].split("\t");
			acc[i]=Float.parseFloat(terms[0]);
			timestamp[i]=Long.parseLong(terms[1]);
		}
		Pedometer pedometer=new Pedometer();
		List<InflectionPoint> list=pedometer.listPeakPoints(null,acc, timestamp, 0,acc.length,GlobalProperties.thresholdAcc[1],
				GlobalProperties.thresholdAcc[0],GlobalProperties.thresholdTime[1], GlobalProperties.thresholdTime[0]);
		
		//List<InflectionPoint> list=pedometer.listInflectionPoint(acc, timestamp);
		for(InflectionPoint p:list)
		{
			System.out.println(p);
		}
	}
	
	@Test
	public void testListPeakSegment() throws IOException
	{

		String str=readToString("C:\\Users\\xianping Tao\\Desktop\\accAll.txt");
		String lines[]=str.split("\n");
		float acc[]=new float[lines.length];
		long timestamp[]=new long[lines.length];
		for(int i=0;i<acc.length;i++)
		{
			String terms[]=lines[i].split("\t");
			acc[i]=Float.parseFloat(terms[0]);
			timestamp[i]=Long.parseLong(terms[1]);
		}
		Pedometer pedometer=new Pedometer();
		
		//for(int i=0;i+200<acc.length;i+=200)
		//{
			List<InflectionPoint> list=pedometer.listPeakPoints(null,acc, timestamp, 2400,2600,GlobalProperties.thresholdAcc[1],
					GlobalProperties.thresholdAcc[0],GlobalProperties.thresholdTime[1], GlobalProperties.thresholdTime[0]);
			
			//List<InflectionPoint> list=pedometer.listInflectionPoint(acc, timestamp,2400,2600);
			for(InflectionPoint p:list)
			{
				System.out.println(p);
			}
		//}
	}
	
	@Test
	public void testListPeakSegments() throws IOException
	{

		String str=readToString("C:\\Users\\xianping Tao\\Desktop\\accAll.txt");
		String lines[]=str.split("\n");
		float acc[]=new float[lines.length];
		long timestamp[]=new long[lines.length];
		for(int i=0;i<acc.length;i++)
		{
			String terms[]=lines[i].split("\t");
			acc[i]=Float.parseFloat(terms[0]);
			timestamp[i]=Long.parseLong(terms[1]);
		}
		Pedometer pedometer=new Pedometer();
		InflectionPoint pre=null;
		for(int i=0;i+200<=acc.length;i+=200)
		{
			List<InflectionPoint> list=pedometer.listPeakPoints(pre,acc, timestamp, i,i+200,GlobalProperties.thresholdAcc[1],
					GlobalProperties.thresholdAcc[0],GlobalProperties.thresholdTime[1], GlobalProperties.thresholdTime[0]);
			
			//List<InflectionPoint> list=pedometer.listInflectionPoint(acc, timestamp,i,i+200);
			for(InflectionPoint p:list)
			{
				System.out.println(p);
			}
			if(list.size()>0)
			pre=list.get(list.size()-1);
		}
	}

}*/
