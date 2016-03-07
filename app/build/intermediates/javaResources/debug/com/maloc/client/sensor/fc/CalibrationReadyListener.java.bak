package com.maloc.client.sensor.fc;

import com.maloc.client.ellipsoidFit.FitPoints;
/**
 * magnetometer calibration interface
 * @author xhw Email:xxyx66@126.com
 */
public interface CalibrationReadyListener {
	/**
	 * callback when calibration ready
	 * @param ellipsoidFit
	 */
	public void onCalibrationReady(FitPoints ellipsoidFit);
	/**
	 * call back when calibration time out
	 */
	public void onCalibrationTimeOut();
	/**
	 * show the calibration progress.
	 * @param percent
	 */
	public void calibrationProgess(int percent);
}
