/*
 *  Copyright (c) 2024 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.encoderdecoder.crestron.nvx.common;

import java.util.Arrays;

/**
 * AudioSource represents audio source support by Crestron.
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 6/25/2024
 * @since 1.0.0
 */
public enum AudioSource {
	AUDIO_FOLLOWS_VIDEO("AudioFollowsVideo", "AudioFollowsVideo"),
	INPUT_1("Input1", "Input1"),
	INPUT_2("Input2", "Input2"),
	ANALOG_AUDIO("AnalogAudio", "AnalogAudio"),
	NO_AUDIO_SELECTED("NoAudioSelected", "NoAudioSelected"),
	PRIMARY_STREAM_AUDIO("PrimaryStreamAudio", "PrimaryStreamAudio"),
	SECONDARY_STREAM_AUDIO("DM NAX(AES67) Audio", "SecondaryStreamAudio"),
	DANTE_AES67("Dante/AES-67Audio", "Dante/AES-67Audio")
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
