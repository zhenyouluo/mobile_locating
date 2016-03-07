package com.maloc.client.fc.graphics;

import java.util.*;
/**
 * 线段集合
 * @author xhw Email:xxyx66@126.com
 */
public class LineSet {

	List<Line> set=new LinkedList<Line>();
	
	public void clear()
	{
		set.clear();
	}
	
	public Line get(int index)
	{
		return this.set.get(index);
	}
	
	public void add(Line line)
	{
		this.set.add(line);
	}
	
	public void remove(Line line)
	{
		set.remove(line);
	}

	public List<Line> getSet() {
		return set;
	}

	public void setSet(List<Line> set) {
		this.set = set;
	}
	
	
}
