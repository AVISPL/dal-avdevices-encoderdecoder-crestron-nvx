/*
 *  Copyright (c) 2024 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.encoderdecoder.crestron.nvx.common;

import java.util.Arrays;

/**
 * DeviceModel represents supported model of Crestron adapter.
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 5/30/2024
 * @since 1.0.0
 */
public enum DeviceModel {
	DM_NVX_E30("DM-NVX-E30"),
	DM_NVX_D30("DM-NVX-D30"),
	DM_NVX_350("DM-NVX-350"),
	DM_NVX_352("DM-NVX-352"),
	;

	private String name;

	DeviceModel(final String name) {
		this.name = name;
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
	 * Get model of device by name
	 *
	 * @param name of model
	 */
	public static DeviceModel getDeviceModelByName(final String name) {
		return Arrays.stream(values())
				.filter(item -> item.getName().equals(name))
				.findFirst()
				.orElse(null);
	}
}
