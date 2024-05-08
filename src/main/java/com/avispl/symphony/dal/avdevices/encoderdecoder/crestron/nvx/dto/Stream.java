/*
 *  Copyright (c) 2024 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.encoderdecoder.crestron.nvx.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Stream
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 5/5/2024
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Stream {
	@JsonAlias("UUID")
	private String uuid;
	@JsonAlias("StreamLocation")
	private String streamLocation;
	@JsonAlias("MulticastAddress")
	private String multicastAddress;
	@JsonAlias("Status")
	private String status;
	@JsonAlias("HorizontalResolution")
	private String horizontalResolution;
	@JsonAlias("VerticalResolution")
	private String verticalResolution;

	/**
	 * Retrieves {@link #uuid}
	 *
	 * @return value of {@link #uuid}
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * Retrieves {@link #streamLocation}
	 *
	 * @return value of {@link #streamLocation}
	 */
	public String getStreamLocation() {
		return streamLocation;
	}

	/**
	 * Retrieves {@link #multicastAddress}
	 *
	 * @return value of {@link #multicastAddress}
	 */
	public String getMulticastAddress() {
		return multicastAddress;
	}

	/**
	 * Retrieves {@link #status}
	 *
	 * @return value of {@link #status}
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Retrieves {@link #horizontalResolution}
	 *
	 * @return value of {@link #horizontalResolution}
	 */
	public String getHorizontalResolution() {
		return horizontalResolution;
	}

	/**
	 * Retrieves {@link #verticalResolution}
	 *
	 * @return value of {@link #verticalResolution}
	 */
	public String getVerticalResolution() {
		return verticalResolution;
	}
}
