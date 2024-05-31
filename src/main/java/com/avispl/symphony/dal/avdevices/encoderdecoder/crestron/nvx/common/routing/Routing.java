/*
 *  Copyright (c) 2024 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.encoderdecoder.crestron.nvx.common.routing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.avispl.symphony.dal.avdevices.encoderdecoder.crestron.nvx.common.CrestronConstant;
import com.avispl.symphony.dal.avdevices.encoderdecoder.crestron.nvx.common.CrestronPropertyList;

/**
 * Routing
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 5/29/2024
 * @since 1.0.0
 */
public enum Routing {

	AUDIO_MODE(CrestronPropertyList.ANALOG_AUDIO_MODE.getName()),
	VIDEO_SOURCE(CrestronPropertyList.VIDEO_SOURCE.getName()),
	AUDIO_SOURCE(CrestronPropertyList.AUDIO_SOURCE.getName()),
	ACTIVE_AUDIO_SOURCE(CrestronPropertyList.ACTIVE_AUDIO_SOURCE.getName()),
	ACTIVE_VIDEO_SOURCE(CrestronPropertyList.ACTIVE_VIDEO_SOURCE.getName()),
	;

	private String name;

	Routing(String name) {
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
	 * Get specific Routing value by given name
	 */
	public static Routing getEnumByName(String name) {
		return Arrays.stream(values())
				.filter(item -> item.getName().equals(name))
				.findFirst()
				.orElse(null);
	}

	public static List<String> getRouteValue(Routing routeName) {
		switch (routeName) {
			case AUDIO_MODE:
				return Arrays.stream(AudioMode.values()).map(AudioMode::getName).collect(Collectors.toList());
			case VIDEO_SOURCE:
				return Arrays.stream(VideoSource.values()).map(VideoSource::getName).collect(Collectors.toList());
			case AUDIO_SOURCE:
				return Arrays.stream(AudioSource.values()).map(AudioSource::getName).collect(Collectors.toList());
		}
		return new ArrayList<>();
	}

	public static String getSpecificRouteNameByValue(Routing routeName, String value) {
		switch (routeName) {
			case AUDIO_SOURCE:
			case ACTIVE_AUDIO_SOURCE:
				AudioSource audioSource = AudioSource.getEnumByValue(value);
				return audioSource == null ? CrestronConstant.NONE : audioSource.getName();
			case VIDEO_SOURCE:
			case ACTIVE_VIDEO_SOURCE:
				VideoSource videoSource = VideoSource.getEnumByName(value);
				return videoSource == null ? CrestronConstant.NONE : videoSource.getName();
			case AUDIO_MODE:
				AudioMode audioMode = AudioMode.getEnumByValue(value);
				return audioMode == null ? CrestronConstant.NONE : audioMode.getName();
		}
		return CrestronConstant.NONE;
	}
}
