package com.maloc.client.localizationService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.maloc.client.bean.RSSIData;
import com.maloc.client.localization.LocalizationInfo;
import com.maloc.client.localization.LocalizationMessageType;
import com.maloc.client.util.FileLog;
import com.maloc.client.util.FileOperator;
import com.maloc.client.util.GlobalProperties;
import com.maloc.client.util.HttpUtil;
import com.maloc.server.floormap.MagneticPositionValidator;
import com.maloc.server.floormap.PositionValidator;
import com.maloc.server.localization.MagneticLocalizor;
import com.maloc.server.particlefilter.ModuleDiffWeightComputer;
import com.maloc.server.particlefilter.Particle;
import com.maloc.server.particlefilter.WeightComputer;
import com.maloc.server.wifi.WiFiPosition;
/**
 * 本地定位服务
 * @author xhw Email:xxyx66@126.com
 */
public class AndroidLocalizationService implements LocalizationService {

	private MagneticLocalizor maloc = null;
	private int venueId, floorIndex;
	private Handler handler;
	private static String urlBase = "http://" + GlobalProperties.Host + ":"
			+ GlobalProperties.Port + "/MaLocDitu/";
	private static final String TAG = AndroidLocalizationService.class
			.getName();
	private volatile boolean initSuccess = true;

	public AndroidLocalizationService(Handler handler) {
		this.handler = handler;

	}

	@Override
	public void localize(LocalizationInfo info) {

		if (maloc == null) {
			handler.sendEmptyMessage(LocalizationMessageType.MAG_FAIL);
			return;
		}
		Particle predict = maloc.localize(info.getAccList().size(),
				info.getMagList(), info.getGyroValue());
		if (predict == null) {
			handler.sendEmptyMessage(LocalizationMessageType.MAG_FAIL);
			return;
		} 
		
		Position pos = new Position(predict.getX(), predict.getY(),
				predict.getTheta(), (float) maloc.getPF().getWeightedRange());
		Message msg = new Message();
		msg.what = LocalizationMessageType.MAG_SUCCESS;
		msg.getData().putSerializable("position", pos);
		//FileLog.dataLog("result", predict.toString()+"\n");
		handler.sendMessage(msg);
	}

	@Override
	public void localize(RSSIData rd) {

		if (rd == null) {
			handler.sendEmptyMessage(LocalizationMessageType.WIFI_NOT_READY);
			return;
		}
		if (maloc == null) {
			handler.sendEmptyMessage(LocalizationMessageType.WIFI_FAIL);
			return;
		}
		WiFiPosition position = maloc.localize(rd);
		if (GlobalProperties.Solution_Magnetic==true&&maloc.isRestart()) {
			Position pos = new Position(position.getPosition()[0],
					position.getPosition()[1], GlobalProperties.RANGE);
			Message msg = new Message();
			msg.what = LocalizationMessageType.WIFI_SUCCESS;
			msg.getData().putSerializable("position", pos);
			handler.sendMessage(msg);
		}
		else if(GlobalProperties.Solution_Magnetic==false&&GlobalProperties.Solution_WiFi==true)
		{
			Position pos = new Position(position.getPosition()[0],
					position.getPosition()[1], GlobalProperties.RANGE);
			Message msg = new Message();
			msg.what = LocalizationMessageType.WIFI_SUCCESS;
			msg.getData().putSerializable("position", pos);
			handler.sendMessage(msg);
		}

	}

	@Override
	public void initPosition(RSSIData rd) {

		initSuccess = true;
		if (maloc == null) {
			createMaLocInstance();
		} else {
			if (this.venueId != GlobalProperties.currentFloor.getVenueId()
					|| this.floorIndex != GlobalProperties.currentFloor
							.getFloorIndex()) {
				createMaLocInstance();
			}
		}

		if (initSuccess == false) {
			handler.sendEmptyMessage(LocalizationMessageType.INIT_FAIL);
			return;
		}

		WeightComputer wComputer = new ModuleDiffWeightComputer(
				GlobalProperties.R, maloc.getQuerier());
		PositionValidator positionValidator = new MagneticPositionValidator(
				maloc.getQuerier());
		maloc.initParticleFilter(GlobalProperties.noise, wComputer,positionValidator);
		WiFiPosition wifiPosition = maloc.initPosition(rd);
		if (wifiPosition.getSameBssidNum() <= 0) {
			handler.sendEmptyMessage(LocalizationMessageType.OUT_SERVICE_RANGE);
			return;
		} else if (wifiPosition.getSameBssidNum() <= GlobalProperties.MINIMUM_RSSI_NUM) {
			handler.sendEmptyMessage(LocalizationMessageType.BAD_WIFI_SCAN);
			return;
		} else {
			Message msg = new Message();
			msg.what = LocalizationMessageType.INIT_SUCCESS;
			msg.getData().putSerializable(
					"position",
					new Position(wifiPosition.getPosition()[0], wifiPosition
							.getPosition()[1], GlobalProperties.RANGE));
			handler.sendMessage(msg);
		}

	}

	private void createMaLocInstance() {
		prepareFingerprints(GlobalProperties.currentFloor.getFloorPath());
		if (initSuccess == false) {
			return;
		}
		try {
			maloc = new MagneticLocalizor(
					GlobalProperties.currentFloor.getFloorPath());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			handler.sendEmptyMessage(LocalizationMessageType.INIT_FAIL);
			return;
		} catch (IOException e) {
			e.printStackTrace();
			handler.sendEmptyMessage(LocalizationMessageType.INIT_FAIL);
			return;
		}
		this.venueId = GlobalProperties.currentFloor.getVenueId();
		this.floorIndex = GlobalProperties.currentFloor.getFloorIndex();
	}

	private void prepareFingerprints(String floorPath) {

		final File conf = new File(Environment.getExternalStorageDirectory()
				.getPath()
				+ "/"
				+ GlobalProperties.FINGERPRINTS_BASE_DIR
				+ "/"
				+ floorPath + "config");

		RequestParams params = new RequestParams();
		params.put("venueId", GlobalProperties.currentFloor.getVenueId());
		params.put("floorIndex", GlobalProperties.currentFloor.getFloorIndex());
		HttpUtil.post(urlBase + "fingerprint!checkUpdateTime", params,
				new JsonHttpResponseHandler() {

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						Log.i(TAG, "check update time fail");
						if(!conf.exists())
							initSuccess = false;
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						Log.i(TAG, "check update time success");

						try {
							String updateTime = response
									.getString("updateTime");

							if(updateTime==null)
							{
								initSuccess = false;
								return;
							}
							if(!conf.exists())
							{
								downloadFingerprint(GlobalProperties.currentFloor.getVenueId(),
										GlobalProperties.currentFloor.getFloorIndex(),
										"magnetic_geo.txt");
								downloadFingerprint(GlobalProperties.currentFloor.getVenueId(),
										GlobalProperties.currentFloor.getFloorIndex(),
										"wifi.txt");
								FileOperator.write(conf, "fc_timestamp="+updateTime);
							}
							else
							{
								Properties pro = new Properties();
								FileInputStream fin = null;
								FileOutputStream fout = null;
								try {
									fin = new FileInputStream(conf);
									pro.load(fin);
								} catch (FileNotFoundException e) {
									e.printStackTrace();
								} catch (IOException e) {
									e.printStackTrace();
								} finally {
									try {
										fin.close();

									} catch (IOException e) {
										e.printStackTrace();
									}
								}
								String timestamp = pro.getProperty("fc_timestamp",
										"0");
								if (!timestamp.equals(updateTime)) {
									pro.setProperty("fc_timestamp", updateTime);
									try {
										fout = new FileOutputStream(conf);
										pro.store(fout, "config");
									} catch (FileNotFoundException e) {
										e.printStackTrace();
									} catch (IOException e) {
										e.printStackTrace();
									} finally {
										try {
											fout.close();
										} catch (IOException e) {
											e.printStackTrace();
										}
									}
									downloadFingerprint(
											GlobalProperties.currentFloor
													.getVenueId(),
											GlobalProperties.currentFloor
													.getFloorIndex(),
											"magnetic_geo.txt");
									downloadFingerprint(
											GlobalProperties.currentFloor
													.getVenueId(),
											GlobalProperties.currentFloor
													.getFloorIndex(), "wifi.txt");
								}
							}
							
							

						} catch (JSONException e) {

							initSuccess=false;
							e.printStackTrace();
						}
					}

					@Override
					public void setUseSynchronousMode(boolean sync) {
						super.setUseSynchronousMode(true);
					}

				});

	}

	private void downloadFingerprint(int venueId, int floorIndex,
			String filename) {

		RequestParams params = new RequestParams();
		params.put("venueId", venueId);
		params.put("floorIndex", floorIndex);
		params.put("requestFileName", filename);
		String urlString = urlBase + "fcDownload";
		final File file = new File(Environment.getExternalStorageDirectory().getPath()+
				"/"+GlobalProperties.FINGERPRINTS_BASE_DIR + "/"
				+ venueId + "/" + floorIndex + "/" + filename);
		if (!file.getParentFile().exists()) {
			file.mkdirs();
		}
		HttpUtil.get(urlString, params, new BinaryHttpResponseHandler() {

			@Override
			public String[] getAllowedContentTypes() {
				// Allowing all data for debug purposes
				return new String[] { ".*" };
			}

			@Override
			public void onFailure(int arg0, Header[] head, byte[] data,
					Throwable throwable) {
				StringBuilder str = new StringBuilder();
				for (Header h : head) {
					str.append(h.toString()).append(" ");
				}
				Log.i(TAG, "download fail," + arg0 + "," + str + "," + data);
				initSuccess = false;
			}

			@Override
			public void onSuccess(int arg0, Header[] head, byte[] data) {
				Log.i(TAG, "download success");
				try {
					FileOperator.write(file, data);
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}

			}

			@Override
			public void setUseSynchronousMode(boolean sync) {
				super.setUseSynchronousMode(true);
			}

		});
	}

}
