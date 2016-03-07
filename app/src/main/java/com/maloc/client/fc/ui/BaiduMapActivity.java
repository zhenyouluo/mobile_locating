package com.maloc.client.fc.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.cloud.CloudListener;
import com.baidu.mapapi.cloud.CloudManager;
import com.baidu.mapapi.cloud.CloudPoiInfo;
import com.baidu.mapapi.cloud.CloudSearchResult;
import com.baidu.mapapi.cloud.DetailSearchResult;
import com.baidu.mapapi.cloud.LocalSearchInfo;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.model.LatLngBounds.Builder;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.maloc.client.R;
import com.maloc.client.bean.Floor;
import com.maloc.client.bean.Venue;
import com.maloc.client.util.FileOperator;
import com.maloc.client.util.GlobalProperties;
import com.maloc.client.util.HttpUtil;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;

//G0AWCWA7YRZ4Zb91oWwq7H9R
//7INKFGhT9zrqoYjI2PQvogvS
/**
 * 百度地图API显示兴趣点
 * 首先对手机进行定位，得到所在的城市，然后查询百度地图云API，显示该城市内所有的兴趣点。
 * @author xhw Email:xxyx66@126.com
 */
public class BaiduMapActivity extends Activity implements CloudListener {
	private static final String LTAG = BaiduMapActivity.class.getSimpleName();
	// 定位相关
	//LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	//private LocationMode mCurrentMode;
	BitmapDescriptor mCurrentMarker;

	MapView mMapView;
	BaiduMap mBaiduMap;

	// UI相关
	OnCheckedChangeListener radioButtonListener;
	Button requestLocButton;
	Button ShiftButton;

	boolean isFirstLoc = true;// 是否首次定位
	BitmapDescriptor markerIcon = null;

	private String urlBase = "http://" + GlobalProperties.Host
			+ ":8080/MaLocDitu/";
	private static ProgressDialog pd;
	public static final int LOAD_FLOOR_SUCESS = 0;
	public static final int LOAD_FLOOR_FAIL = 1;
	public static final int LOAD_FLOOR_MAP_SUCCESS = 2;
	public static final int LOAD_FLOOR_MAP_FAIL = 3;
	public static final int OUTDOOR_LOCALIZATION_SUCCESS = 4;
	private Floor[] currentFloors = null;
	
	/**
	 * 构造广播监听类，监听 SDK key 验证以及网络异常广播
	 */
	public class SDKReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String s = intent.getAction();
			if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
				Toast.makeText(getApplicationContext(),
						"key 验证出错! 请在 AndroidManifest.xml 文件中检查 key 设置",
						Toast.LENGTH_SHORT).show();
			} else if (s
					.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
				Toast.makeText(getApplicationContext(), "网络出错",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	private SDKReceiver mReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		SDKInitializer.initialize(this.getApplication());
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_baidumap);
		registerSDKAK();
		initBaiduMap();
		initLayers();
	}
	/**
	 * 初始化地图控件
	 */
	public void initBaiduMap() {
		
		// 地图初始化
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();

		
		/*
		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(5000);
		option.setIsNeedAddress(true);
		option.setLocationMode(LocationMode.Hight_Accuracy);//设置定位模式
		option.setNeedDeviceDirect(true);
		mLocClient.setLocOption(option);
		mLocClient.start();
		//mLocClient.requestLocation();
		 */
	}
	/**
	 * 初始化地图图层，搜索兴趣点
	 */
	public void initLayers() {
		CloudManager.getInstance().init(BaiduMapActivity.this);
		/*
		 * LocalSearchInfo info = new LocalSearchInfo(); info.ak =
		 * "B266f735e43ab207ec152deff44fec8b";
		 * //info.ak="peoaMmfVNXeXwDdIR5lcGlTq"; info.geoTableId = 31869;
		 * info.tags = ""; info.q = "天安门"; info.region = "北京市";
		 * CloudManager.getInstance().localSearch(info);
		 */

		LocalSearchInfo info = new LocalSearchInfo();
		info.ak = GlobalProperties.AK;
		info.geoTableId = GlobalProperties.GEO_TABLE_ID;
		//info.tags = "";
		info.region = "南京市";
		//info.region = location.getCity();
		CloudManager.getInstance().localSearch(info);
		

		//markerIcon = BitmapDescriptorFactory
		//		.fromResource(R.drawable.icon_gcoding);
		markerIcon = BitmapDescriptorFactory
				.fromResource(R.drawable.marker_icon);
		/*// 定义Maker坐标点
		LatLng point = new LatLng(32.11676, 118.96934);
		// 构建Marker图标

		// 构建MarkerOption，用于在地图上添加Marker
		OverlayOptions overlay = new MarkerOptions().position(point).icon(
				markerIcon);
		// 在地图上添加Marker，并显示
		Marker marker = (Marker) mBaiduMap.addOverlay(overlay);
		Bundle bundle = new Bundle();
		bundle.putString("scene", "NJU_CS");
		marker.setExtraInfo(bundle);*/
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);

		/*// 定义地图状态
		MapStatus mMapStatus = new MapStatus.Builder().target(point).zoom(12)
				.build();
		// 定义MapStatusUpdate对象，以便描述地图状态将要发生的变化

		MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory
				.newMapStatus(mMapStatus);
		// 改变地图状态
		mBaiduMap.setMapStatus(mMapStatusUpdate);*/

		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(final Marker marker) {
				Venue venue = (Venue) marker.getExtraInfo().getSerializable(
						"venue");
				GlobalProperties.currentVenue = venue;
				loadFloors(venue);
				return true;
			}

		});

	}

	
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
					new AlertDialog.Builder(BaiduMapActivity.this)
							.setTitle(
									GlobalProperties.currentVenue
											.getVenueName())
							.setMessage("No Floors in This Venue.").show();
				}

				break;
			case LOAD_FLOOR_FAIL:
				Toast.makeText(BaiduMapActivity.this, "Network error. Retry!",
						Toast.LENGTH_SHORT).show();
				
				break;
			case LOAD_FLOOR_MAP_SUCCESS:
				Intent intent = new Intent();
				/* 指定intent要启动的类 */
				intent.setClass(BaiduMapActivity.this, IndoorMapActivity.class);
				/* 启动一个新的Activity */
				BaiduMapActivity.this.startActivity(intent);
				/* 关闭当前的Activity */
				// BaiduMapActivity.this.finish();
				break;
			case LOAD_FLOOR_MAP_FAIL:

				break;
			case OUTDOOR_LOCALIZATION_SUCCESS:
				
				break;
			}

			super.handleMessage(msg);
		}

	};
	/**
	 * 选择楼层。弹出窗口供用户选择楼层，点击后加载楼层地图进入室内地图。
	 * @param floors
	 * @param venue
	 */
	private void selectFloor(final Floor[] floors, final Venue venue) {
		String fnames[] = getFloorSimpleNames(floors);
		new AlertDialog.Builder(BaiduMapActivity.this)
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

		pd = ProgressDialog.show(mMapView.getContext(), "Load Floor Map",
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
	/**
	 * 从服务器获取楼层信息
	 * @param venue
	 */
	private void loadFloors(final Venue venue) {
		pd = ProgressDialog.show(mMapView.getContext(),
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
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(location.getDirection()).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			if (isFirstLoc) {
				logLocation(location);
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
				/*Message msg=new Message();
				msg.what=OUTDOOR_LOCALIZATION_SUCCESS;
				msg.getData().putString("city", location.getCity());
				handler.sendMessage(msg);*/
				LocalSearchInfo info = new LocalSearchInfo();
				info.ak = GlobalProperties.AK;
				info.geoTableId = GlobalProperties.GEO_TABLE_ID;
				//info.tags = "";
				info.region = "南京市";
				//info.region = location.getCity();
				CloudManager.getInstance().localSearch(info);
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	private void logLocation(BDLocation location)
	{
		//Receive Location 
		StringBuffer sb = new StringBuffer(256);
		sb.append("time : ");
		sb.append(location.getTime());
		sb.append("\nerror code : ");
		sb.append(location.getLocType());
		sb.append("\nlatitude : ");
		sb.append(location.getLatitude());
		sb.append("\nlontitude : ");
		sb.append(location.getLongitude());
		sb.append("\nradius : ");
		sb.append(location.getRadius());
		if (location.getLocType() == BDLocation.TypeGpsLocation){
			sb.append("\nspeed : ");
			sb.append(location.getSpeed());
			sb.append("\nsatellite : ");
			sb.append(location.getSatelliteNumber());
			sb.append("\ndirection : ");
			sb.append("\naddr : ");
			sb.append(location.getAddrStr());
			sb.append(location.getDirection());
		} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
			sb.append("\naddr : ");
			sb.append(location.getAddrStr());
			//运营商信息
			sb.append("\noperationers : ");
			sb.append(location.getOperators());
		}
		Log.i("BaiduLocationApiDem", sb.toString());
	}
	
	private void registerSDKAK() {

		// 注册 SDK 广播监听者
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
		mReceiver = new SDKReceiver();
		registerReceiver(mReceiver, iFilter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// activity 暂停时同时暂停地图控件
		mMapView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// activity 恢复时同时恢复地图控件
		mMapView.onResume();
	}

	@Override
	protected void onStop()
	{
		//mLocClient.stop();
		super.onStop();
	}
	
	@Override
	protected void onRestart()
	{
		super.onRestart();
		//mLocClient.start();
	}
	
	@Override
	protected void onDestroy() {

		// 退出时销毁定位
		//mLocClient.stop();
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		// 取消监听 SDK 广播
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}

	@Override
	public void onGetDetailSearchResult(DetailSearchResult result, int error) {
		// TODO Auto-generated method stub
		if (result != null) {
			if (result.poiInfo != null) {
				Toast.makeText(BaiduMapActivity.this, result.poiInfo.title,
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(BaiduMapActivity.this,
						"status:" + result.status, Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(BaiduMapActivity.this, "result is null: " + error,
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onGetSearchResult(CloudSearchResult result, int error) {

		
		if (result != null && result.poiList != null
				&& result.poiList.size() > 0) {
			Log.d(LTAG,
					"onGetSearchResult, result length: "
							+ result.poiList.size());
			StringBuilder sb = new StringBuilder();
			for (CloudPoiInfo p : result.poiList) {
				sb.append(p.address).append(",");
			}
			Log.i("cloudserach result", sb.toString());
			
			mBaiduMap.clear();
			
			BitmapDescriptor bd =
			BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
			LatLng ll;
			LatLngBounds.Builder builder = new Builder();
			for (CloudPoiInfo info : result.poiList) {
				ll = new LatLng(info.latitude, info.longitude);
				OverlayOptions oo = new MarkerOptions().icon(markerIcon)
						.position(ll);
				Marker marker = (Marker) mBaiduMap.addOverlay(oo);
				Bundle bundle = new Bundle();
				bundle.putSerializable(
						"venue",
						new Venue(Integer.parseInt(String.valueOf(info.extras
								.get("venueId"))), info.title, info.address));
				marker.setExtraInfo(bundle);
				builder.include(ll);
			}
			LatLngBounds bounds = builder.build();
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(bounds);
			mBaiduMap.animateMapStatus(u);
		}
	}

}
