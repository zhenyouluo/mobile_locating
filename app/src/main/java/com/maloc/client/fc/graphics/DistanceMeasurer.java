package com.maloc.client.fc.graphics;


import android.graphics.PointF;
import android.util.Log;
/**
 * 图形工具类，距离测量
 * @author xhw Email:xxyx66@126.com
 */
public class DistanceMeasurer {
	/**
	 * 点到直线的距离
	 * @param point
	 * @param line
	 * @return
	 */
	public static double pointToLine(PointF point,Line line)
	{
		float w[]=line.getWeight();//ax+by+c=0;
		return Math.abs(w[0]*point.x+w[1]*point.y+w[2])/Math.sqrt(w[0]*w[0]+w[1]*w[1]);
	}
	/**
	 * 点到点的距离
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static double pointToPoint(PointF p1,PointF p2)
	{
		float dx=p1.x-p2.x;
		float dy=p1.y-p2.y;
		return Math.sqrt(dx*dx+dy*dy);
	}
	/**
	 * 查找是否选中了某条线段的起点或者终点
	 * @param p
	 * @param line
	 * @param maxDis
	 * @return
	 */
	public static PointF findTouchedPoint(PointF p,Line line,double maxDis)
	{
		double dis1=pointToPoint(p, line.start);
		if(line.end==null)
		{
			return null;
		}
		double dis2=pointToPoint(p,line.end);
		if(dis1>maxDis&&dis2>maxDis)
			return null;
		if(dis1<dis2)
			return line.start;
		else
			return line.end;
		
		
	}
	
	/**
	 * 查找是否选中了线段集合中的某条线段
	 * @param p
	 * @param lineSet
	 * @param maxDis
	 * @return
	 */
	public static Line findTouchedLine(PointF p,LineSet lineSet,double maxDis)
	{
		Line r=null;
		double min=Double.MAX_VALUE;
		for(Line line:lineSet.getSet())
		{
			if(!inSegmentRange(p,line,maxDis))
				continue;
			double dis=pointToLine(p, line);
			Log.i("Distance", dis+"");
			if(dis<=maxDis&&dis<min)
			{
				min=dis;
				r=line;
			}
		}
		return r;
	}
	/**
	 * 判断点是否在某条线段覆盖的矩形范围内
	 * @param p
	 * @param line
	 * @param maxDis
	 * @return
	 */
	private static boolean inSegmentRange(PointF p, Line line,double maxDis) {

		//Log.i("SegmentRange", p.x+","+p.y+";"+line.start.x+","+line.start.y+";"+line.end.x+","+line.end.y);
		if(p.x<Math.min(line.start.x, line.end.x)-maxDis||p.x>Math.max(line.start.x, line.end.x)+maxDis)
			return false;
		if(p.y<Math.min(line.start.y, line.end.y)-maxDis||p.y>Math.max(line.start.y, line.end.y)+maxDis)
			return false;
		return true;
	}
}
