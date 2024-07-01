/*
 *  Copyright (c) 2024 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.encoderdecoder.crestron.nvx.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Stream represents stream object retrieve from Crestron device.
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 5/5/2024
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Stream {
	@JsonProperty("UUID")
	private String uuid;
	@JsonProperty("StreamLocation")
	private String streamLocation;
	@JsonProperty("MulticastAddress")
	private String multicastAddress;
	@JsonProperty("Status")
	private String status;
	@JsonProperty("HorizontalResolution")
	private String horizontalResolution;
	@JsonProperty("VerticalResolution")
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
