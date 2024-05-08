/*
 *  Copyright (c) 2024 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.encoderdecoder.crestron.nvx.common;

/**
 * CrestronCommand
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 5/8/2024
 * @since 1.0.0
 */
public enum CrestronCommand {
	DEVICE_INFO(CrestronConstant.DEVICE_INFO_API_GROUP, CrestronUri.DEVICE_INFO_API),
	DEVICE_SPECIFIC(CrestronConstant.DEVICE_SPECIFIC_API_GROUP, CrestronUri.DEVICE_SPECIFIC_API),
	DEVICE_OPERATION(CrestronConstant.DEVICE_OPERATION_API_GROUP, CrestronUri.DEVICE_OPERATIONS_API),
	NETWORK(CrestronConstant.NETWORK_GROUP, CrestronUri.ETHERNET_API),
	CONTROL_SYSTEM(CrestronConstant.CONTROL_SYSTEM_GROUP, CrestronUri.IP_TABLE_V2_API),
	AUDIO_VIDEO_INPUT_OUTPUT(CrestronConstant.DEVICE_AUDIO_VIDEO_INPUT_OUTPUT_API_GROUP, CrestronUri.AUDIO_INPUT_OUTPUT_API),
	AUTO_UPDATE(CrestronConstant.AUTO_UPDATE_GROUP, CrestronUri.AUTO_UPDATE_API),
	DATE_TIME(CrestronConstant.DATE_TIME_GROUP, CrestronUri.SYSTEM_CLOCK_API),
	DISCOVERY_CONFIG(CrestronConstant.DISCOVERY_CONFIG_GROUP, CrestronUri.DISCOVERY_API),
	STREAM_RECEIVE(CrestronConstant.STREAM_RECEIVE_GROUP, CrestronUri.STREAM_RECEIVE_API),
	STREAM_TRANSMIT(CrestronConstant.STREAM_TRANSMIT_GROUP, CrestronUri.STREAM_TRANSMIT_API),
	STREAM_SUBSCRIPTION(CrestronConstant.STREAM_SUBSCRIPTION_GROUP, CrestronUri.STREAM_SUBSCRIPTION_API),
	STREAM_AVAILABLE(CrestronConstant.STREAM_AVAILABLE_GROUP, CrestronUri.STREAM_AVAILABLE_API),
	INPUT_ROUTING(CrestronConstant.INPUT_ROUTING_GROUP, CrestronUri.INPUT_ROUTING_API),
	;

	private String groupCommand;
	private String command;

  CrestronCommand(final String groupCommand, final String command) {
		this.groupCommand = groupCommand;
		this.command = command;
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
}
