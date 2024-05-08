/*
 *  Copyright (c) 2024 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.encoderdecoder.crestron.nvx;


import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;


/**
 * CrestronNVXCommunicatorTest
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 13/5/2024
 * @since 1.0.0
 */
public class CrestronNVXCommunicatorTest {
	private ExtendedStatistics extendedStatistics;
	private CrestronNVXCommunicator crestronNVXCommunicator;

	@BeforeEach
	void setUp() throws Exception {
		crestronNVXCommunicator = new CrestronNVXCommunicator();
		crestronNVXCommunicator.setHost("172.31.15.63");
		crestronNVXCommunicator.setLogin("");
		crestronNVXCommunicator.setPassword("");
		crestronNVXCommunicator.setPort(8000);
		crestronNVXCommunicator.init();
		crestronNVXCommunicator.connect();
	}

	@AfterEach
	void destroy() throws Exception {
		crestronNVXCommunicator.disconnect();
		crestronNVXCommunicator.destroy();
	}

	@Test
	void testLogin() throws Exception {
		crestronNVXCommunicator.getMultipleStatistics();
	}

	@Test
	void testGetDataSuccess() throws Exception {
		extendedStatistics = (ExtendedStatistics) crestronNVXCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		List<AdvancedControllableProperty> controls = extendedStatistics.getControllableProperties();
		Assert.assertEquals(94, stats.size());
		Assert.assertEquals(9, controls.size());
	}
}
