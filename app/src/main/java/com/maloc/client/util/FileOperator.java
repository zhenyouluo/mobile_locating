package com.maloc.client.util;

import java.io.*;

import android.os.Environment;
/**
 * 文件操作工具类
 * @author xhw Email:xxyx66@126.com
 */
public class FileOperator {

	private static String sdState = Environment.getExternalStorageState();
	/**
	 * 读取文件到String
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public synchronized static String readToString(File file) throws IOException
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
	/**
	 * 读取文件到String
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public synchronized static String readToString(String filename) throws IOException
	{
		return readToString(new File(filename));
	}
	/**
	 * 将String写入文件
	 * @param filename
	 * @param message
	 */
	public synchronized static void write(String filename,String message)
	{
		
		try {
			FileWriter fw=new FileWriter(filename,false);
			fw.write(message);
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 将String写入文件
	 * @param file
	 * @param message
	 */
	public synchronized static void write(File file,String message)
	{
		
		try {
			FileWriter fw=new FileWriter(file,false);
			fw.write(message);
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 将String写入文件
	 * @param filename
	 * @param message
	 * @param flag true， append模式，false， 覆盖模式
	 */
	public synchronized static void write(String filename,String message,boolean flag)
	{
		
		try {
			FileWriter fw=new FileWriter(filename,flag);
			fw.write(message);
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 将String写入文件
	 * @param file
	 * @param message
	 * @param flag true， append模式，false， 覆盖模式
	 */
	public synchronized static void write(File file,String message,boolean flag)
	{
		
		try {
			FileWriter fw=new FileWriter(file,flag);
			fw.write(message);
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 将二进制byte[]写入文件
	 * @param filename
	 * @param data
	 * @throws IOException
	 */
	public synchronized static void write(String filename,byte[] data) throws IOException
	{
		File file=new File(filename);
		if(file.exists())
			file.delete();
		file.createNewFile();
		FileOutputStream fo=new FileOutputStream(file);
		fo.write(data);
		fo.close();
	}
	/**
	 * 将二进制byte[]写入文件
	 * @param file
	 * @param data
	 * @throws IOException
	 */
	public synchronized static void write(File file,byte[] data) throws IOException
	{
		if(file.exists())
			file.delete();
		file.createNewFile();
		FileOutputStream fo=new FileOutputStream(file);
		fo.write(data);
		fo.close();
	}
	
	/**
	 * 创建文件夹
	 * @param filename
	 * @return
	 */
	public synchronized static File directMakeDir(String filename)
	{
		File f=new File(filename);
		if(!f.exists())
			f.mkdirs();
		return f;
	}
	/**
	 * 删除文件夹下所有文件
	 * @param dir
	 */
	public synchronized static void  clearDir(File dir)
	{
		File[] files=dir.listFiles();
		for(File file:files)
		{
			deleteFile(file);
		}
	}
	/**
	 * 删除文件以及子文件
	 * @param file
	 */
	public synchronized static void  deleteFile(File file)
    {
     if(sdState.equals(Environment.MEDIA_MOUNTED))
     {
      if (file.exists())
      {
       if (file.isFile())
       {
        file.delete();
       }
       // 如果它是一个目录
       else if (file.isDirectory())
       {
        // 声明目录下所有的文件 files[];
        File files[] = file.listFiles();
        for (int i = 0; i < files.length; i++)
        { // 遍历目录下所有的文件
         deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
        }
       }
       file.delete();
      }
     }
    }
	
}
