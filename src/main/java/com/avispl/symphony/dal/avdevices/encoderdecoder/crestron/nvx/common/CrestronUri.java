/*
 *  Copyright (c) 2024 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.encoderdecoder.crestron.nvx.common;

/**
 * CrestronUri represents uri endpoints to make API request to Crestron device.
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 5/5/2024
 * @since 1.0.0
 */
public class CrestronUri {
	public static final String DEVICE_INFO_API = "Device/DeviceInfo";
	public static final String DEVICE_OPERATIONS_API = "Device/DeviceOperations";
	public static final String DEVICE_SPECIFIC_API = "Device/DeviceSpecific";

	public static final String ETHERNET_API = "Device/Ethernet";
	public static final String IP_TABLE_API = "Device/IpTable";
	public static final String AUDIO_INPUT_OUTPUT_API = "Device/AudioVideoInputOutput";
	public static final String AUDIO_OUTPUT_PORT_API = "Device/AudioVideoInputOutput/Outputs/%s/Ports/%s";
	public static final String AUTO_UPDATE_API = "Device/AutoUpdateMaster";
	public static final String SYSTEM_CLOCK_API = "Device/SystemClock";
	public static final String DISCOVERY_API = "Device/DiscoveryConfig";
	public static final String LOCALIZATION_API = "Device/Localization";

	public static final String STREAM_RECEIVE_API = "Device/StreamReceive";
	public static final String STREAM_TRANSMIT_API = "Device/StreamTransmit";
	public static final String STREAM_SUBSCRIPTION_API = "Device/XioSubscription";
	public static final String STREAM_AVAILABLE_API = "Device/DiscoveredStreams";
	public static final String INPUT_ROUTING_API = "Device/AvRouting";
	public static final String XIO_CLOUD_STATUS_API = "Device/CloudSettings/XioCloud";
	public static final String LOGIN_API = "/userlogin.html";
}
