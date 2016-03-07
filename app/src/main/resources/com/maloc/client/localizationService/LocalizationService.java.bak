package com.maloc.client.localizationService;

import java.util.List;

import com.maloc.client.bean.MagneticVector;
import com.maloc.client.bean.Particle;
import com.maloc.client.bean.RSSIData;
import com.maloc.client.localization.LocalizationInfo;
/**
 * Localization service interface
 * @author xhw Email:xxyx66@126.com
 */
public interface LocalizationService {
	/**
	 * magnetic localization
	 * @param info, magnetic localization info
	 */
	public void localize(LocalizationInfo info);
	/**
	 * wifi localization
	 * @param rd, wifi rssi data
	 */
	public void localize(RSSIData rd);
	/**
	 * init position 
	 * @param rd
	 */
	public void initPosition(RSSIData rd);
	
}
