package com.maloc.client.fc.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.maloc.client.R;
import com.maloc.client.bean.Floor;
import com.maloc.client.bean.Venue;
import com.maloc.client.util.FileOperator;
import com.maloc.client.util.GlobalProperties;
import com.maloc.client.util.HttpUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 测试界面
 * 主要为了方便不能使用百度地图的地区使用，以及测试楼层定位
 * @author xhw Email:xxyx66@126.com
 */
public class IndoorTestActivity extends Activity{
	public static final int LOAD_FLOOR_SUCESS = 0;
	public static final int LOAD_FLOOR_FAIL = 1;
	public static final int LOAD_FLOOR_MAP_SUCCESS = 2;
	public static final int LOAD_FLOOR_MAP_FAIL = 3;
	public static final int OUTDOOR_LOCALIZATION_SUCCESS = 4;
	public static final int LOAD_WRONG_VENUE = 5;
	private static final String LTAG = IndoorTestActivity.class.getSimpleName();
	private String urlBase = "http://" + GlobalProperties.Host
			+ ":8080/MaLocDitu/";
	private static ProgressDialog pd;
	private Floor[] currentFloors = null;
	//private File venueFile = null;
	private Button enter;
	private EditText venueIdET;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			if(pd!=null)
				pd.dismiss();
			switch (msg.what) {
			case LOAD_FLOOR_SUCESS:
				if (currentFloors != null && currentFloors.length > 0) {
					selectFloor(currentFloors, GlobalProperties.currentVenue);
				} else {
					new AlertDialog.Builder(IndoorTestActivity.this)
							.setTitle(GlobalProperties.currentVenue.getVenueId())
							.setMessage("No Floors in This Venue.").show();
				}

				break;
			case LOAD_FLOOR_FAIL:
				Toast.makeText(IndoorTestActivity.this, "Network error. Retry!",
						Toast.LENGTH_SHORT).show();
				
				break;
			case LOAD_FLOOR_MAP_SUCCESS:
				Intent intent = new Intent();
				intent.setClass(IndoorTestActivity.this, IndoorMapActivity.class);
				IndoorTestActivity.this.startActivity(intent);
				// BaiduMapActivity.this.finish();
				break;
			case LOAD_FLOOR_MAP_FAIL:
				Toast.makeText(IndoorTestActivity.this, "Network error. Retry!",
						Toast.LENGTH_SHORT).show();
				break;
			case OUTDOOR_LOCALIZATION_SUCCESS:
				
				break;
			case LOAD_WRONG_VENUE:
				Toast.makeText(IndoorTestActivity.this, "Wrong Venue Id!",
						Toast.LENGTH_SHORT).show();
				break;
			}

			super.handleMessage(msg);
		}

	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_indoor_test);
		venueIdET=(EditText)findViewById(R.id.venue);
		enter=(Button)findViewById(R.id.enter);
		
		enter.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {

				String str=venueIdET.getText().toString();
				if(str.length()==0)
				{
					Log.d(LTAG,"venueId=null");
					return;
				}
				int vd=Integer.parseInt(str);
				GlobalProperties.currentVenue=new Venue(vd);
				Log.d(LTAG,"venueId="+vd);
				loadFloors(GlobalProperties.currentVenue);
			}
			
		});
		
		
		
	}
	
	/**
	 * 从服务器获取楼层信息
	 * @param venue
	 */
	private void loadFloors(final Venue venue) {
		pd = ProgressDialog.show(enter.getContext(),
				"Load Floor Information", "Wait……");
		new Thread(new Runnable() {

			@Override
			public void run() {
				RequestParams params = new RequestParams();
				params.put("venueId", venue.getVenueId());
				String urlString = urlBase + "floor!listFloorsByVenue";
				HttpUtil.get(urlString, params, new JsonHttpResponseHandler() {

					
					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable,JSONObject response) {

						
						Message msg = new Message();
						msg.what = LOAD_FLOOR_FAIL;
						if(response!=null)
						{
							msg.getData().putString("response", response.toString());
							Log.w(LTAG, response.toString());
						}
						else
						{
							Log.w(LTAG, "response is null.");
							
						}
						handler.sendMessage(msg);
						// super.onFailure(statusCode, headers, responseString,
						// throwable);
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {

						Log.i(LTAG, response.toString());
						Message msg = new Message();
						msg.what = LOAD_FLOOR_SUCESS;
						parseFloorJson(response);
						handler.sendMessage(msg);

					}

					private void parseFloorJson(JSONObject response) {

						try {
							if (response.getInt("status") == 0) {
								JSONArray floorArray = response
										.getJSONArray("floor");
								currentFloors = new Floor[floorArray.length()];
								for (int i = 0; i < floorArray.length(); i++) {
									currentFloors[i] = new Floor(floorArray
											.getJSONObject(i));
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void setUseSynchronousMode(boolean sync) {
						// TODO Auto-generated method stub
						super.setUseSynchronousMode(true);
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							String response, Throwable throwable) {

						Message msg = new Message();
						msg.what = LOAD_FLOOR_FAIL;
						if(response!=null)
						{
							msg.getData().putString("response", response.toString());
							Log.w(LTAG, response.toString());
						}
						else
						{
							Log.w(LTAG, "response is null.");
							
						}
						handler.sendMessage(msg);
					}

				});
			}
		}).start();
	}

	/**
	 * 选择楼层。弹出窗口供用户选择楼层，点击后加载楼层地图进入室内地图。
	 * @param floors
	 * @param venue
	 */
	private void selectFloor(final Floor[] floors, final Venue venue) {
		String fnames[] = getFloorSimpleNames(floors);
		new AlertDialog.Builder(IndoorTestActivity.this)
				.setTitle(venue.getVenueName())
				.setItems(fnames, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int index) {
						GlobalProperties.currentFloor = floors[index];
						loadFloorMap(GlobalProperties.currentVenue,
								floors[index]);
					}
				}).show();
	}
	
	/**
	 * 加载楼层地图
	 * @param currentVenue
	 * @param floor
	 */
	private void loadFloorMap(Venue currentVenue, Floor floor) {

		String dir = Environment.getExternalStorageDirectory().getPath() + "/"
				+ GlobalProperties.MAP_BASE_DIR + "/";
		String configFile = dir + GlobalProperties.currentFloor.getFloorPath()
				+ "config";
		File file = new File(configFile);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		if (file.exists()) {
			Properties p = new Properties();
			FileInputStream fin=null;
			try {
				fin=new FileInputStream(file);
				p.load(fin);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			finally
			{
				try {
					fin.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			String timestamp = p.getProperty("timestamp", "0");
			if (timestamp.equals(floor.getTimestamp())) {
				handler.sendEmptyMessage(LOAD_FLOOR_MAP_SUCCESS);
				return;
			}
		}
		this.downloadFloorMap(floor, dir + floor.getMapPath(), configFile);

	}

	/**
	 * 更新地图图片信息和时间戳
	 * @param floor
	 * @param configFile
	 */
	private void rewriteConfig(Floor floor, String configFile) {

		StringBuilder sb = new StringBuilder();
		sb.append("width=").append(floor.getFloorWidth()).append("\n");
		sb.append("height=").append(floor.getFloorHeight()).append("\n");
		sb.append("timestamp=").append(floor.getTimestamp()).append("\n");
		FileOperator.write(configFile, sb.toString());

	}
	/**
	 * 从服务器上下载楼层地图
	 * @param floor
	 */
	private void downloadFloorMap(final Floor floor, final String mapFile,
			final String configFile) {

		pd = ProgressDialog.show(enter.getContext(), "Load Floor Map",
				"Wait……");
		new Thread(new Runnable() {

			@Override
			public void run() {

				RequestParams params = new RequestParams();
				params.put("venueId", floor.getVenueId());
				params.put("floorIndex", floor.getFloorIndex());
				params.put("ext", floor.getMapFileType());
				String urlString = urlBase + "floorMapDownload";
				HttpUtil.get(urlString, params,
						new BinaryHttpResponseHandler() {

							@Override
							public String[] getAllowedContentTypes() {
								// Allowing all data for debug purposes
								return new String[] { ".*" };
							}

							@Override
							public void onFailure(int arg0, Header[] head,
									byte[] data, Throwable throwable) {
								StringBuilder str = new StringBuilder();
								for (Header h : head) {
									str.append(h.toString()).append(" ");
								}
								Log.i(LTAG, "download fail," + arg0 + "," + str
										+ "," + data);
								handler.sendEmptyMessage(LOAD_FLOOR_MAP_FAIL);
							}

							@Override
							public void onSuccess(int arg0, Header[] head,
									byte[] data) {
								Log.i(LTAG, "download success");
								try {
									FileOperator.write(mapFile, data);
								} catch (IOException e) {
									e.printStackTrace();
									handler.sendEmptyMessage(LOAD_FLOOR_MAP_FAIL);
									return;
								}
								rewriteConfig(floor, configFile);
								handler.sendEmptyMessage(LOAD_FLOOR_MAP_SUCCESS);
							}

							@Override
							public void setUseSynchronousMode(boolean sync) {
								super.setUseSynchronousMode(true);
							}

						});
			}

		}).start();
	}

	private static String[] getFloorSimpleNames(Floor[] floors) {

		String[] floorNames = new String[floors.length];
		Arrays.sort(floors);
		for (int i = 0; i < floorNames.length; i++) {
			floorNames[i] = floors[i].getFloorIndex() + "-"
					+ floors[i].getFloorName();
		}
		return floorNames;
	}
}
