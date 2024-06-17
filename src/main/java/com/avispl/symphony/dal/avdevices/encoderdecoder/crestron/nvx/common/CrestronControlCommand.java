/*
 *  Copyright (c) 2024 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.encoderdecoder.crestron.nvx.common;

import java.util.Arrays;

/**
 * CrestronControlCommand represents controlling commands sent to a Crestron device.
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 5/29/2024
 * @since 1.0.0
 */
public enum CrestronControlCommand {
	IGMP_SUPPORT(CrestronPropertyList.IGMP_SUPPORT.getName(), "{\"Device\":{\"Ethernet\": %s }}", CrestronUri.ETHERNET_API, CrestronPropertyList.IGMP_SUPPORT.getApiPropertyName()),
  CLOUD_CONFIGURATION(CrestronPropertyList.CLOUD_CONFIGURATION_SERVICE_CONNECTION.getName(), "{\"Device\":{\"CloudSettings\":{\"XioCloud\": %s }}}", CrestronUri.XIO_CLOUD_STATUS_API, CrestronPropertyList.CLOUD_CONFIGURATION_SERVICE_CONNECTION.getApiPropertyName()),
	ANALOG_VOLUME(CrestronPropertyList.ANALOG_AUDIO_VOLUME.getName(), "{\"Device\":{\"AudioVideoInputOutput\":{\"Outputs\":[{\"Ports\":[{\"Audio\": %s }]}]}}}", CrestronUri.AUDIO_OUTPUT_PORT_API, CrestronPropertyList.ANALOG_AUDIO_VOLUME.getApiPropertyName()),
	AUTO_UPDATE(CrestronPropertyList.AUTO_UPDATE.getName(),"{\"Device\":{\"AutoUpdateMaster\": %s }}", CrestronUri.AUTO_UPDATE_API, CrestronPropertyList.AUTO_UPDATE.getApiPropertyName()),
	DATE(CrestronPropertyList.DATE.getName(), "{ \"Device\": { \"SystemClock\": %s } }", CrestronUri.SYSTEM_CLOCK_API, CrestronPropertyList.DATE.getApiPropertyName()),
	TIME(CrestronPropertyList.TIME.getName(), "{ \"Device\": { \"SystemClock\": %s } }", CrestronUri.SYSTEM_CLOCK_API, CrestronPropertyList.TIME.getApiPropertyName()),
	TIMEZONE(CrestronPropertyList.TIMEZONE.getName(), "{ \"Device\": { \"SystemClock\": %s } }", CrestronUri.SYSTEM_CLOCK_API, CrestronPropertyList.TIMEZONE.getApiPropertyName()),
	DISCOVERY_AGENT(CrestronPropertyList.DISCOVERY_AGENT.getName(), "{\"Device\":{\"DiscoveryConfig\": %s }}", CrestronUri.DISCOVERY_API, CrestronPropertyList.DISCOVERY_AGENT.getApiPropertyName()),
	MODE(CrestronPropertyList.TRANSMIT_MODE.getName(), "{\"Device\":{\"DeviceSpecific\": %s}}", CrestronUri.DEVICE_SPECIFIC_API, CrestronPropertyList.TRANSMIT_MODE.getApiPropertyName()),
	AUTOMATIC_INPUT_ROUTING(CrestronPropertyList.AUTOMATIC_INPUT_ROUTING.getName(), "{\"Device\":{\"DeviceSpecific\": %s}}", CrestronUri.DEVICE_SPECIFIC_API, CrestronPropertyList.AUTOMATIC_INPUT_ROUTING.getApiPropertyName()),
//	AUDIO_SOURCE(CrestronPropertyList.AUDIO_SOURCE.getName(), "{\"Device\":{\"DeviceSpecific\": %s}}", CrestronUri.DEVICE_SPECIFIC_API, CrestronPropertyList.AUDIO_SOURCE.getApiPropertyName()),
//	VIDEO_SOURCE(CrestronPropertyList.VIDEO_SOURCE.getName(), "{\"Device\":{\"DeviceSpecific\": %s}}", CrestronUri.DEVICE_SPECIFIC_API, CrestronPropertyList.VIDEO_SOURCE.getApiPropertyName()),
	AUDIO_MODE(CrestronPropertyList.ANALOG_AUDIO_MODE.getName(), "{\"Device\":{\"DeviceSpecific\": %s}}", CrestronUri.DEVICE_SPECIFIC_API, CrestronPropertyList.ANALOG_AUDIO_MODE.getApiPropertyName()),
	REBOOT(CrestronPropertyList.REBOOT_BUTTON.getName(), "{ \"Device\": { \"DeviceOperations\": %s }}", CrestronUri.DEVICE_OPERATIONS_API, CrestronPropertyList.REBOOT_BUTTON.getApiPropertyName()),
	SYNCHRONIZE_NOW(CrestronPropertyList.SYNCHRONIZE_NOW.getName(), "{ \"Device\": { \"SystemClock\": { \"Ntp\": %s } } }", CrestronUri.SYSTEM_CLOCK_API, CrestronPropertyList.SYNCHRONIZE_NOW.getApiPropertyName()),
	TTL(CrestronPropertyList.TTL.getName(), "{ \"Device\": { \"DiscoveryConfig\": %s } }", CrestronUri.DISCOVERY_API, CrestronPropertyList.TTL.getApiPropertyName()),
	;
	private String name;
	private String param;
	private String url;
	private String apiProperty;

	CrestronControlCommand(final String name, final String param, final String url, final String value) {
		this.name = name;
		this.param = param;
		this.url = url;
		this.apiProperty = value;
	}

	/**
	 * Retrieves {@link #param}
	 *
	 * @return value of {@link #param}
	 */
	public String getParam() {
		return param;
	}

	/**
	 * Retrieves {@link #name}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Retrieves {@link #url}
	 *
	 * @return value of {@link #url}
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Retrieves {@link #apiProperty}
	 *
	 * @return value of {@link #apiProperty}
	 */
	public String getApiProperty() {
		return apiProperty;
	}

	/**
	 * Get specific control by given value
	 */
	public static CrestronControlCommand getEnumByName(String name) {
		return Arrays.stream(values())
				.filter(item -> item.getName().equals(name))
				.findFirst()
				.orElse(null);
	}
}
