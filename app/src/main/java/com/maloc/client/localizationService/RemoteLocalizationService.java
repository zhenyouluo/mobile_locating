package com.maloc.client.localizationService;

import java.util.List;

import org.apache.http.Header;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.maloc.client.bean.MagneticVector;
import com.maloc.client.bean.Particle;
import com.maloc.client.bean.RSSIData;
import com.maloc.client.localization.LocalizationInfo;
import com.maloc.client.localization.LocalizationMessageType;
import com.maloc.client.localization.ResultListener;
import com.maloc.client.sensor.localize.LocalizationSensorDataCollection;
import com.maloc.client.util.FileLog;
import com.maloc.client.util.GlobalProperties;
import com.maloc.client.util.HttpUtil;
/**
 * 远程地磁定位（已废弃）
 * @author xhw
 *
 */
public class RemoteLocalizationService implements LocalizationService {

	private Handler handler;
	private String urlBase;
	private int counter = 0;

	public RemoteLocalizationService(Handler handler) {
		this.handler = handler;
		this.urlBase = "http://" + GlobalProperties.Host + ":"
				+ GlobalProperties.Port + "/MaLocServer2/";
	}

	@Override
	public void localize(LocalizationInfo info) {

		//FileLog.dataLog("cache_take.txt", info.toString());
		Log.i("RemoteLocalization", "magnetic localize.");
		// int stepCnt, List<MagneticVector> magList,float[] gyroValues, int
		// gyroLen
		String urlString = urlBase + "localization";
		RequestParams params = new RequestParams(); // 绑定参数
		int stepCnt = info.getAccList().size();
		params.put("stepCnt", stepCnt + "");
		params.put("username", GlobalProperties.username);
		params.put("sequence", counter++);
		StringBuilder gyroStr = new StringBuilder();
		for (int i = 0; i < info.getGyroValue().size(); i++) {
			gyroStr.append(info.getGyroValue().get(i)).append(",");
		}
		params.put("gyroValues", gyroStr.toString());
		StringBuilder magneticValues = new StringBuilder();
		for (int i = 0; i < info.getMagList().size(); i++) {
			for (int j = 0; j < 3; j++) {
				magneticValues.append(info.getMagList().get(i).getVector()[j])
						.append(",");
			}
			magneticValues.append(info.getMagList().get(i).getTimestamp());
			magneticValues.append(";");
		}
		params.put("magneticValues", magneticValues.toString());
		HttpUtil.get(urlString, params, new AsyncHttpResponseHandler() {
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {

				Log.e("LocalizationRunner", "localization failure.");
				handler.sendEmptyMessage(LocalizationMessageType.MAG_TIMEOUT);
			}

			@Override
			public void setUseSynchronousMode(boolean sync) {
				// TODO Auto-generated method stub
				super.setUseSynchronousMode(true);
			}
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {

				Log.w("onSuccess", "success");

				String strs[] = new String(arg2).split(",");
				if (strs.length >= 5) {

					Log.w("magnetic localization result", new String(arg2));
					Position pos=new Position(Float.parseFloat(strs[0]), Float
							.parseFloat(strs[1]), Float
							.parseFloat(strs[2]), Float
							.parseFloat(strs[3]));
					if(pos.getRange()<0)
					{
						return ;
					}
					Message msg = new Message();
					msg.what = LocalizationMessageType.MAG_SUCCESS;
					msg.getData().putSerializable(
							"position",pos);
					handler.sendMessage(msg);
				}
			}
		});
	}

	@Override
	public void initPosition(RSSIData rd) {

		// Log.i("RemoteLocalization",""+rd);
		if (rd == null) {
			handler.sendEmptyMessage(LocalizationMessageType.WIFI_NOT_READY);
			return;
		}

		String urlString = urlBase + "initPosition";
		RequestParams params = new RequestParams();
		params.put("username", GlobalProperties.username);
		params.put("location", GlobalProperties.currentFloor.getVenueId());
		params.put("floor", GlobalProperties.currentFloor.getFloorIndex());
		params.put("rssi", rd.toString());
		// params.put("position", rd.getX()+","+rd.getY());
		Log.i("Remote host", urlString);
		Log.i("Remote wifi", rd.toParams());

		Log.i("Remote initPosition",
				"Initial start:" + System.currentTimeMillis());
		HttpUtil.get(urlString, params, new AsyncHttpResponseHandler() {

			@Override
			public void setUseSynchronousMode(boolean sync) {
				// TODO Auto-generated method stub
				super.setUseSynchronousMode(true);
			}
			
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {

				Log.i("Remote initPosition",
						"Initial Failed!" + System.currentTimeMillis());
				handler.sendEmptyMessage(LocalizationMessageType.INIT_FAIL);
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {

				String res = new String(arg2);
				Log.i("Remote initPosition", "success: " + res);
				String strs[] = res.split(",");
				if(strs.length>=4)
				{
					int type=Integer.parseInt(strs[3].trim());
					if(type==-1)
					{
						handler.sendEmptyMessage(LocalizationMessageType.OUT_SERVICE_RANGE);
						return;
					}
					else if(type==0)
					{
						handler.sendEmptyMessage(LocalizationMessageType.BAD_WIFI_SCAN);
						return;
					}
					Message msg = new Message();
					msg.what = LocalizationMessageType.INIT_SUCCESS;
					msg.getData()
							.putSerializable(
									"position",
									new Position(Float.parseFloat(strs[0]), Float
											.parseFloat(strs[1]), Float
											.parseFloat(strs[2])));
					/*
					 * msg.getData().putFloat("x", Float.parseFloat(strs[0]));
					 * msg.getData().putFloat("y", Float.parseFloat(strs[1]));
					 */
					handler.sendMessage(msg);
				}
				else
				{
					handler.sendEmptyMessage(LocalizationMessageType.INIT_FAIL);
				}
				
			}

		});
		/*
		 * try { Thread.sleep(500); } catch (InterruptedException e) {
		 * e.printStackTrace(); }
		 */
	}

	@Override
	public void localize(RSSIData rd) {

		if (rd == null) {
			handler.sendEmptyMessage(LocalizationMessageType.WIFI_NOT_READY);
			return;
		}

		String urlString = urlBase + "wifiLocalization";
		RequestParams params = new RequestParams();
		params.put("username", GlobalProperties.username);
		// params.put("location", GlobalProperties.SCENE_NAME);
		// params.put("floor", GlobalProperties.FLOOR);
		params.put("rssi", rd.toString());
		// HttpUtil.get(urlString, params,null);
		HttpUtil.get(urlString, params, new AsyncHttpResponseHandler() {

			@Override
			public void setUseSynchronousMode(boolean sync) {
				// TODO Auto-generated method stub
				super.setUseSynchronousMode(true);
			}
			
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {

				Log.i("wifi localize", "Failed! " + System.currentTimeMillis());
				handler.sendEmptyMessage(LocalizationMessageType.WIFI_FAIL);
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {

				// Log.i("wifi localize", arg0);
				String strs[] = new String(arg2).split(",");
				if(strs.length!=6)
					return;
				int type=Integer.parseInt(strs[3]);
				boolean consistent=Boolean.parseBoolean(strs[4]);
				if(type==1&&consistent==false)
				{
					Position pos=new Position(Float.parseFloat(strs[0]), Float
							.parseFloat(strs[1]), Float
							.parseFloat(strs[2]));
					Message msg = new Message();
					msg.what = LocalizationMessageType.WIFI_SUCCESS;
					msg.getData()
							.putSerializable("position",pos);
					handler.sendMessage(msg);
				}
				
			}

		});

	}

}
