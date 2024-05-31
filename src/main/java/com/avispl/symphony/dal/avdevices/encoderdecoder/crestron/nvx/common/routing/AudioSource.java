/*
 *  Copyright (c) 2024 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.encoderdecoder.crestron.nvx.common.routing;

import java.util.Arrays;

/**
 * AudioSource
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 5/29/2024
 * @since 1.0.0
 */
public enum AudioSource {
	AUDIO_FOLLOWS_VIDEO("Audio Follows Video", "AudioFollowsVideo"),
	INPUT_1("Input 1", "Input1"),
	INPUT_2("Input 2", "Input2"),
	ANALOG_AUDIO("Analog Audio", "AnalogAudio"),
	PRIMARY_STREAM_AUDIO("Primary Stream Audio", "PrimaryStreamAudio"),
	SECONDARY_STREAM_AUDIO("Secondary Stream Audio", "SecondaryStreamAudio"),
	;
	private String name;
	private String value;

	AudioSource(String name, String value) {
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
	 * Get AudioSource value by given value
	 */
	public static AudioSource getEnumByValue(String value) {
		return Arrays.stream(values())
				.filter(item -> item.getValue().equals(value))
				.findFirst()
				.orElse(null);
	}
}
