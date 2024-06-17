/*
 *  Copyright (c) 2024 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.encoderdecoder.crestron.nvx.common;

/**
 * CrestronCommand represents monitoring commands sent to a Crestron device.
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 5/8/2024
 * @since 1.0.0
 */
public enum CrestronCommand {
	DEVICE_INFO(CrestronConstant.DEVICE_INFO_API_GROUP, CrestronUri.DEVICE_INFO_API, CrestronConstant.EMPTY),
	DEVICE_SPECIFIC(CrestronConstant.DEVICE_SPECIFIC_API_GROUP, CrestronUri.DEVICE_SPECIFIC_API, CrestronConstant.EMPTY),
	DEVICE_OPERATION(CrestronConstant.DEVICE_OPERATION_API_GROUP, CrestronUri.DEVICE_OPERATIONS_API, CrestronConstant.EMPTY),
	NETWORK(CrestronConstant.NETWORK_GROUP, CrestronUri.ETHERNET_API, CrestronConstant.EMPTY),
	CLOUD_SETTING(CrestronConstant.DEVICE_XIO_CLOUD_API_GROUP, CrestronUri.XIO_CLOUD_STATUS_API, CrestronConstant.EMPTY),
	CONTROL_SYSTEM(CrestronConstant.CONTROL_SYSTEM_GROUP, CrestronUri.IP_TABLE_API, CrestronConstant.EMPTY),
	AUDIO_VIDEO_INPUT_OUTPUT(CrestronConstant.DEVICE_AUDIO_VIDEO_INPUT_OUTPUT_API_GROUP, CrestronUri.AUDIO_INPUT_OUTPUT_API, CrestronConstant.EMPTY),
	AUTO_UPDATE(CrestronConstant.AUTO_UPDATE_GROUP, CrestronUri.AUTO_UPDATE_API, CrestronConstant.EMPTY),
	DATE_TIME(CrestronConstant.DATE_TIME_GROUP, CrestronUri.SYSTEM_CLOCK_API, CrestronConstant.EMPTY),
	DISCOVERY_CONFIG(CrestronConstant.DISCOVERY_CONFIG_GROUP, CrestronUri.DISCOVERY_API, CrestronConstant.EMPTY),
	LOCALIZATION(CrestronConstant.LOCALIZATION_GROUP,CrestronUri.LOCALIZATION_API, CrestronConstant.EMPTY),
	STREAM_RECEIVE(CrestronConstant.STREAM_RECEIVE_GROUP, CrestronUri.STREAM_RECEIVE_API, CrestronConstant.RECEIVER),
	STREAM_TRANSMIT(CrestronConstant.STREAM_TRANSMIT_GROUP, CrestronUri.STREAM_TRANSMIT_API, CrestronConstant.TRANSMITTER),
	STREAM_SUBSCRIPTION(CrestronConstant.STREAM_SUBSCRIPTION_GROUP, CrestronUri.STREAM_SUBSCRIPTION_API, CrestronConstant.RECEIVER),
	STREAM_AVAILABLE(CrestronConstant.STREAM_AVAILABLE_GROUP, CrestronUri.STREAM_AVAILABLE_API, CrestronConstant.RECEIVER),
	INPUT_ROUTING(CrestronConstant.INPUT_ROUTING_GROUP, CrestronUri.INPUT_ROUTING_API, CrestronConstant.EMPTY),
	;

	private String groupCommand;
	private String command;
	private String deviceMode;

  CrestronCommand(final String groupCommand, final String command, final String deviceMode) {
		this.groupCommand = groupCommand;
		this.command = command;
		this.deviceMode = deviceMode;
	}

	/**
	 * Retrieves {@link #groupCommand}
	 *
	 * @return value of {@link #groupCommand}
	 */
	public String getGroupCommand() {
		return groupCommand;
	}

	/**
	 * Retrieves {@link #command}
	 *
	 * @return value of {@link #command}
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * Retrieves {@link #deviceMode}
	 *
	 * @return value of {@link #deviceMode}
	 */
	public String getDeviceMode() {
		return deviceMode;
	}
}
