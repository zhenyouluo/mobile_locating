package com.maloc.client.fc.graphics;

import android.graphics.PointF;
/**
 * 指纹收集线段
 * @author xhw Email:xxyx66@126.com
 */
public class Line {

	public PointF start;
	public PointF end;
	private float w[]=new float[3];
	private boolean touched=false;
	private boolean moveForwar=true;
	public Line()
	{
		
	}
	
	public Line(PointF start,PointF end)
	{
		this.start=start;
		this.end=end;
	}
	
	public double length()
	{
		float dx=end.x-start.x;
		float dy=end.y-start.y;
		return Math.sqrt(dx*dx+dy*dy);
	}
	
	public float getGradient()
	{
		return (end.y-start.y)/(end.x-start.x);
	}
	
	public double slope()
	{
		
		return Math.atan((end.x-start.x)/(end.y-start.y));
	}
	
	//ax+by+c=0;
	public float[] getWeight()
	{
		
		w[0]=(end.y-start.y);
		w[1]=-(end.x-start.x);
		w[2]=-(w[0]*start.x+w[1]*start.y);
		//assert Math.abs(w[0]*end.x+w[1]*end.y+w[2])<=0.0000000001;
		
		return w;
	}
	
	public String toFileName()
	{
		StringBuilder sb=new StringBuilder();
		sb.append(this.start.x).append(' ').append(this.start.y).append(' ');
		sb.append(this.end.x).append(' ').append(this.end.y);
		return sb.toString();
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb=new StringBuilder();
		sb.append('(').append(this.start.x).append(',').append(this.start.y).append(')');
		sb.append("->");
		sb.append('(').append(this.end.x).append(',').append(this.end.y).append(')');
		return sb.toString();
		
	}
	
	public boolean isTouched() {
		return touched;
	}
	public void setTouched(boolean touched) {
		this.touched = touched;
	}
	public float[] getW() {
		return w;
	}
	public void setW(float[] w) {
		this.w = w;
	}

	public boolean isMoveForwar() {
		return moveForwar;
	}

	public void setMoveForwar(boolean moveForwar) {
		this.moveForwar = moveForwar;
	}
	
	
	
}
