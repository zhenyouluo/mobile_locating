package com.maloc.client.fc.ui;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.maloc.client.util.FileLog;

import android.app.Application;
import android.content.Intent;

/**
 * 异常控制，
 * 当未知的异常发生后，将异常信息写入文件
 * @author xhw Email:xxyx66@126.com
 */
public class CrapApplication extends Application {
   
    public static final String NAME = getCurrentDateString() + ".txt";
 
    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);
    }
 
    /**
     * 捕获错误信息的handler
     */
    private UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler() {
 
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
           
            String info = null;
            ByteArrayOutputStream baos = null;
            PrintStream printStream = null;
            try {
                baos = new ByteArrayOutputStream();
                printStream = new PrintStream(baos);
                ex.printStackTrace(printStream);
                byte[] data = baos.toByteArray();
                info = new String(data);
                data = null;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (printStream != null) {
                        printStream.close();
                    }
                    if (baos != null) {
                        baos.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            FileLog.errorLog(NAME, info);
            Intent intent = new Intent(getApplicationContext(),
                    CollapseActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            
            intent.getExtras().putString("exception", info);
            startActivity(intent);
        }
    };
 
    
    /**
     * 获取当前日期
     * 
     * @return
     */
    private static String getCurrentDateString() {
        String result = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",
                Locale.getDefault());
        Date nowDate = new Date();
        result = sdf.format(nowDate);
        return result;
    }
}
