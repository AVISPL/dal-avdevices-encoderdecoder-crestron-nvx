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
	IGMP_SUPPORT(CrestronPropertyList.IGMP_SUPPORT.getName(), "{\"Device\":{\"Ethernet\": %s }}", CrestronUri.ETHERNET_API, CrestronPropertyList.IGMP_SUPPORT.getApiPropertyName(), true),
  CLOUD_CONFIGURATION(CrestronPropertyList.CLOUD_CONFIGURATION_SERVICE_CONNECTION.getName(), "{\"Device\":{\"CloudSettings\":{\"XioCloud\": %s }}}", CrestronUri.XIO_CLOUD_STATUS_API, CrestronPropertyList.CLOUD_CONFIGURATION_SERVICE_CONNECTION.getApiPropertyName(), true),
	ANALOG_VOLUME(CrestronPropertyList.ANALOG_AUDIO_VOLUME.getName(), "{\"Device\":{\"AudioVideoInputOutput\":{\"Outputs\":[{\"Ports\":[{\"Audio\": %s }]}]}}}", CrestronUri.AUDIO_OUTPUT_PORT_API, CrestronPropertyList.ANALOG_AUDIO_VOLUME.getApiPropertyName(), false),
	AUTO_UPDATE(CrestronPropertyList.AUTO_UPDATE.getName(),"{\"Device\":{\"AutoUpdateMaster\": %s }}", CrestronUri.AUTO_UPDATE_API, CrestronPropertyList.AUTO_UPDATE.getApiPropertyName(), false),
	DATE(CrestronPropertyList.DATE.getName(), "{ \"Device\": { \"SystemClock\": %s } }", CrestronUri.SYSTEM_CLOCK_API, CrestronPropertyList.DATE.getApiPropertyName(), false),
	TIME(CrestronPropertyList.TIME.getName(), "{ \"Device\": { \"SystemClock\": %s } }", CrestronUri.SYSTEM_CLOCK_API, CrestronPropertyList.TIME.getApiPropertyName(), false),
	TIMEZONE(CrestronPropertyList.TIMEZONE.getName(), "{ \"Device\": { \"SystemClock\": %s } }", CrestronUri.SYSTEM_CLOCK_API, CrestronPropertyList.TIMEZONE.getApiPropertyName(), false),
	DISCOVERY_AGENT(CrestronPropertyList.DISCOVERY_AGENT.getName(), "{\"Device\":{\"DiscoveryConfig\": %s }}", CrestronUri.DISCOVERY_API, CrestronPropertyList.DISCOVERY_AGENT.getApiPropertyName(), false),
	MODE(CrestronPropertyList.TRANSMIT_MODE.getName(), "{\"Device\":{\"DeviceSpecific\": %s}}", CrestronUri.DEVICE_SPECIFIC_API, CrestronPropertyList.TRANSMIT_MODE.getApiPropertyName(), true),
	AUTOMATIC_INPUT_ROUTING(CrestronPropertyList.AUTOMATIC_INPUT_ROUTING.getName(), "{\"Device\":{\"DeviceSpecific\": %s}}", CrestronUri.DEVICE_SPECIFIC_API, CrestronPropertyList.AUTOMATIC_INPUT_ROUTING.getApiPropertyName(), false),
	AUDIO_MODE(CrestronPropertyList.ANALOG_AUDIO_MODE.getName(), "{\"Device\":{\"DeviceSpecific\": %s}}", CrestronUri.DEVICE_SPECIFIC_API, CrestronPropertyList.ANALOG_AUDIO_MODE.getApiPropertyName(), false),
	REBOOT(CrestronPropertyList.REBOOT_BUTTON.getName(), "{ \"Device\": { \"DeviceOperations\": %s }}", CrestronUri.DEVICE_OPERATIONS_API, CrestronPropertyList.REBOOT_BUTTON.getApiPropertyName(), false),
	SYNCHRONIZE_NOW(CrestronPropertyList.SYNCHRONIZE_NOW.getName(), "{ \"Device\": { \"SystemClock\": { \"Ntp\": %s } } }", CrestronUri.SYSTEM_CLOCK_API, CrestronPropertyList.SYNCHRONIZE_NOW.getApiPropertyName(), false),
	TTL(CrestronPropertyList.TTL.getName(), "{ \"Device\": { \"DiscoveryConfig\": %s } }", CrestronUri.DISCOVERY_API, CrestronPropertyList.TTL.getApiPropertyName(), false),
	;
	private String name;
	private String param;
	private String url;
	private String apiProperty;
	private boolean isRequireReboot;

	CrestronControlCommand(final String name, final String param, final String url, final String value, boolean isRequireReboot) {
		this.name = name;
		this.param = param;
		this.url = url;
		this.apiProperty = value;
		this.isRequireReboot = isRequireReboot;
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
	 * Retrieves {@link #isRequireReboot}
	 *
	 * @return value of {@link #isRequireReboot}
	 */
	public boolean isRequireReboot() {
		return isRequireReboot;
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
