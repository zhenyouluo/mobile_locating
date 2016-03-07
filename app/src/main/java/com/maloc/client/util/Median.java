package com.maloc.client.util;

import java.util.List;
import java.util.Random;
/**
 * 求中位数
 * @author xhw
 *
 * @param <T>
 */
public class Median<T extends Comparable<T>> {
	
	private static final Random rand=new Random(47);
	
	public T getMedian(List<T> list,int size)
	{
		int m=(size-1)/2;
		return this.findKthElements(list, m, 0, size-1);
	}
	
	public T findKthElements(List<T> list,int k,int l,int u)
	{
		if(l>=u)
			return null;
		swap(list,l,l+rand.nextInt(u-l+1));
		int m=l;
		for(int i=l+1;i<=u;i++)
		{
			if(list.get(i).compareTo(list.get(l))<0)
			{
				swap(list,++m,i);
			}
		}
		swap(list,l,m);
		if(m<k)
			return findKthElements(list,k,m+1,u);
		else if(m>k)
			return findKthElements(list,k,l,m-1);
		else
			return list.get(m);
	}
	
	public T findKthElements(T array[],int k,int l,int u)
	{
		if(l>=u)
			return null;
		swap(array,l,l+rand.nextInt(u-l+1));
		int m=l;
		for(int i=l+1;i<=u;i++)
		{
			if(array[i].compareTo(array[l])<0)
			{
				swap(array,++m,i);
			}
		}
		swap(array,l,m);
		if(m<k)
			return findKthElements(array,k,m+1,u);
		else if(m>k)
			return findKthElements(array,k,l,m-1);
		else
			return array[m];
	}
	
	public void swap(T array[],int i,int j)
	{
		T temp=array[i];
		array[i]=array[j];
		array[j]=temp;
	}
	
	public void swap(List<T> array,int i,int j)
	{
		T temp=array.get(i);
		array.set(i, array.get(j));
		array.set(j,temp);
	}
	
	
	public static double findKthElements(double array[],int k,int l,int u)
	{
		if(l<u)
		{
			swap(array,l,l+rand.nextInt(u-l+1));
			int m=l;
			for(int i=l+1;i<=u;i++)
			{
				if(array[i]<array[l])
				{
					swap(array,++m,i);
				}
			}
			swap(array,l,m);
			if(m<k)
				return findKthElements(array,k,m+1,u);
			else if(m>k)
				return findKthElements(array,k,l,m-1);
			else
				return array[m];
		}
		return -1;
	}
	
	public static void swap(double array[],int i,int j)
	{
		double temp=array[i];
		array[i]=array[j];
		array[j]=temp;
	}

	
	public static void main(String args[])
	{
		double array[]={-1,-1,-1,1,2,3,3,4,4};
		System.out.println(findKthElements(array,array.length/2,0,array.length-1));
		
	}
}
