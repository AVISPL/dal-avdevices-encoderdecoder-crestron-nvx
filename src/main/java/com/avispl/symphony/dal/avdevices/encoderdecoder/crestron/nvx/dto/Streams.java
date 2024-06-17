/*
 *  Copyright (c) 2024 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.encoderdecoder.crestron.nvx.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Streams represents a list of stream from Crestron device
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 5/12/2024
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Streams {
	@JsonAlias("Streams")
	private List<Stream> streams;

	/**
	 * Retrieves {@link #streams}
	 *
	 * @return value of {@link #streams}
	 */
	public List<Stream> getStreams() {
		return streams;
	}
}
