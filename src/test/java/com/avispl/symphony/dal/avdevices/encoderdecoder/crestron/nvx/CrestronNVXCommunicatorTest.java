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
import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
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
		crestronNVXCommunicator.setTrustAllCertificates(true);
		crestronNVXCommunicator.setHost("10.7.55.104");
		crestronNVXCommunicator.setLogin("admin");
		crestronNVXCommunicator.setPassword("admin");
		crestronNVXCommunicator.setPort(443);
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
//		 crestronNVXCommunicator.sendControl();
		extendedStatistics = (ExtendedStatistics) crestronNVXCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		List<AdvancedControllableProperty> controls = extendedStatistics.getControllableProperties();
		Assert.assertEquals(95, stats.size());
		Assert.assertEquals(10, controls.size());
	}

	@Test
	void testControlStreamTransmit() throws Exception {
		crestronNVXCommunicator.getMultipleStatistics();
		Thread.sleep(5000);
		crestronNVXCommunicator.getMultipleStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = "StreamTransmit#UUID";
		String value = "2";
	  controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		crestronNVXCommunicator.controlProperty(controllableProperty);
		extendedStatistics = (ExtendedStatistics) crestronNVXCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		Assert.assertEquals("2", stats.get(property));
	}
}
