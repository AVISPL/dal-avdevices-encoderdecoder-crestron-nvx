/*
 *  Copyright (c) 2024 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.encoderdecoder.crestron.nvx.common.routing;

import java.util.Arrays;

/**
 * VideoSource represents source for video
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 5/29/2024
 * @since 1.0.0
 */
public enum VideoSource {
	INPUT_1("Input 1", "Input1"),
	INPUT_2("Input 2", "Input2"),
	NONE("None", "None"),
	STREAM("Stream", "Stream"),
	;

	private String name;
	private String value;

	VideoSource(String name, String value) {
		this.name = name;
		this.value = value;
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
	 * Retrieves {@link #value}
	 *
	 * @return value of {@link #value}
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Get specific VideoSource value by given value
	 */
	public static VideoSource getEnumByValue(String value) {
		return Arrays.stream(values())
				.filter(item -> item.getValue().equals(value))
				.findFirst()
				.orElse(null);
	}

	/**
	 * Get VideoSource value by given name
	 */
	public static VideoSource getEnumByName(String name) {
		return Arrays.stream(values())
				.filter(item -> item.getName().equals(name))
				.findFirst()
				.orElse(null);
	}
}
