/*
 *  Copyright (c) 2024 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.encoderdecoder.crestron.nvx;


import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.security.auth.login.FailedLoginException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
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
		crestronNVXCommunicator.setHost("10.7.55.103");
		crestronNVXCommunicator.setLogin("admin");
		crestronNVXCommunicator.setPassword("admin");
		crestronNVXCommunicator.setPort(443);
		crestronNVXCommunicator.init();
		crestronNVXCommunicator.connect();
		crestronNVXCommunicator.setConfigManagement("true");
	}

	@AfterEach
	void destroy() throws Exception {
		crestronNVXCommunicator.disconnect();
		crestronNVXCommunicator.destroy();
	}

	@Test
	void testLoginSuccess() throws Exception {
		crestronNVXCommunicator.getMultipleStatistics();
	}

	@Test
	void testLoginFailed() throws Exception {
		crestronNVXCommunicator.destroy();
		crestronNVXCommunicator.setPassword("Incorrect-password");
		crestronNVXCommunicator.init();
		crestronNVXCommunicator.connect();
		assertThrows(FailedLoginException.class, () -> crestronNVXCommunicator.getMultipleStatistics());
	}

	@Test
	void testGetDataSuccess() throws Exception {
		crestronNVXCommunicator.setConfigManagement("true");
		extendedStatistics = (ExtendedStatistics) crestronNVXCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		List<AdvancedControllableProperty> controls = extendedStatistics.getControllableProperties();
		Assertions.assertEquals(87, stats.size()); // DM-NVX-D30
		Assertions.assertEquals(15, controls.size());
	}

	@Test
	void testControlDiscoveryAgent() throws Exception {
		crestronNVXCommunicator.getMultipleStatistics();
		Thread.sleep(5000);
		ControllableProperty control = new ControllableProperty();
		String name = "DiscoveryConfig#DiscoveryAgent";
		int value = 1;
		control.setProperty(name);
		control.setValue(value);
		crestronNVXCommunicator.controlProperty(control);
		extendedStatistics = (ExtendedStatistics) crestronNVXCommunicator.getMultipleStatistics().get(0);
		Optional<AdvancedControllableProperty> property = ((ExtendedStatistics) crestronNVXCommunicator.getMultipleStatistics().get(0))
				.getControllableProperties().stream().filter(item -> item.getName().equals(name))
				.findFirst();
		Assertions.assertEquals(value, property.get().getValue());
	}

	@Test
	void testControlTTL() throws Exception {
		crestronNVXCommunicator.getMultipleStatistics();
		Thread.sleep(5000);
		ControllableProperty control = new ControllableProperty();
		String name = "DiscoveryConfig#TTL";
		String currentTTLValue = "DiscoveryConfig#TTLCurrentValue";
		int value = 10;
		control.setProperty(name);
		control.setValue(value);
		crestronNVXCommunicator.controlProperty(control);

		extendedStatistics = (ExtendedStatistics) crestronNVXCommunicator.getMultipleStatistics().get(0);
		Optional<AdvancedControllableProperty> property = extendedStatistics
				.getControllableProperties().stream().filter(item -> item.getName().equals(name))
				.findFirst();
		Assertions.assertEquals("10", property.get().getValue().toString());
		Assertions.assertEquals("10", extendedStatistics.getStatistics().get(currentTTLValue));
	}

	@Test
	void testAnalogAudio() throws Exception {
		crestronNVXCommunicator.getMultipleStatistics();
		Thread.sleep(5000);
		ControllableProperty control = new ControllableProperty();
		String name = "Output#AnalogAudioVolume";
		int value = 30;
		control.setProperty(name);
		control.setValue(value);
		crestronNVXCommunicator.controlProperty(control);

		Optional<AdvancedControllableProperty> property = ((ExtendedStatistics) crestronNVXCommunicator.getMultipleStatistics().get(0))
				.getControllableProperties().stream().filter(item -> item.getName().equals(name))
				.findFirst();
		Assertions.assertEquals("-24.0", property.get().getValue().toString());
	}

	@Test
	void testAudioMode() throws Exception {
		crestronNVXCommunicator.getMultipleStatistics();
		Thread.sleep(5000);
		ControllableProperty control = new ControllableProperty();
		String name = "InputRouting#AnalogAudioMode";
		String value = "Insert";
		control.setProperty(name);
		control.setValue(value);
		crestronNVXCommunicator.controlProperty(control);
	}

	@Test
	void testControlDateTime() throws Exception {
		crestronNVXCommunicator.getMultipleStatistics();
		Thread.sleep(5000);
		ControllableProperty control = new ControllableProperty();
		String name = "DateTime#Date";
		String value = "06/08/2024";
		control.setProperty(name);
		control.setValue(value);
		crestronNVXCommunicator.controlProperty(control);

		Optional<AdvancedControllableProperty> property = ((ExtendedStatistics) crestronNVXCommunicator.getMultipleStatistics().get(0))
				.getControllableProperties().stream().filter(item -> item.getName().equals(name))
				.findFirst();
		Assertions.assertEquals(value, property.get().getValue().toString());
	}

	@Test
	void testControlIPID() throws Exception {
		crestronNVXCommunicator.getMultipleStatistics();
		Thread.sleep(5000);
		ControllableProperty control = new ControllableProperty();
		String name = "ControlSystem#IPID";
		String value = "FA";
		control.setProperty(name);
		control.setValue(value);
		crestronNVXCommunicator.controlProperty(control);
		extendedStatistics = (ExtendedStatistics) crestronNVXCommunicator.getMultipleStatistics().get(0);
		Assertions.assertEquals(value, extendedStatistics.getStatistics().get("ControlSystem#IPID"));
	}

	@Test
	void testControlStreamAvailableUniqueId() throws Exception {
		crestronNVXCommunicator.getMultipleStatistics();
		Thread.sleep(5000);
		ControllableProperty control = new ControllableProperty();
		String name = "StreamAvailable#UniqueId";
		String value = "da6b5f24-54df-40a3-86c1-3135b0c82fdc";
		control.setProperty(name);
		control.setValue(value);
		crestronNVXCommunicator.controlProperty(control);
		extendedStatistics = (ExtendedStatistics) crestronNVXCommunicator.getMultipleStatistics().get(0);
		Assertions.assertEquals(value, extendedStatistics.getStatistics().get("StreamAvailable#UniqueId"));
	}

	@Test
	void testControlStreamSubscriptionUniqueId() throws Exception {
		crestronNVXCommunicator.getMultipleStatistics();
		Thread.sleep(5000);
		ControllableProperty control = new ControllableProperty();
		String name = "StreamSubscription#UniqueId";
		String value = "00000000-0000-4002-0059-e00410b1fd05";
		control.setProperty(name);
		control.setValue(value);
		crestronNVXCommunicator.controlProperty(control);
		extendedStatistics = (ExtendedStatistics) crestronNVXCommunicator.getMultipleStatistics().get(0);
		Assertions.assertEquals(value, extendedStatistics.getStatistics().get("StreamSubscription#UniqueId"));
	}

	@Test
	void testControlInputNo() throws Exception {
		crestronNVXCommunicator.getMultipleStatistics();
		Thread.sleep(5000);
		ControllableProperty control = new ControllableProperty();
		String name = "Input#No";
		String value = "1";
		control.setProperty(name);
		control.setValue(value);
		crestronNVXCommunicator.controlProperty(control);
		extendedStatistics = (ExtendedStatistics) crestronNVXCommunicator.getMultipleStatistics().get(0);
		Assertions.assertEquals(value, extendedStatistics.getStatistics().get("Input#No"));
	}

	@Test
	void testReboot() throws Exception {
		crestronNVXCommunicator.getMultipleStatistics();
		Thread.sleep(5000);
		ControllableProperty control = new ControllableProperty();
		String name = "Reboot";
		String value = "";
		control.setProperty(name);
		control.setValue(value);
		crestronNVXCommunicator.controlProperty(control);
	}

	@Test
	void testCloudConfigurationConnection() throws Exception {
		crestronNVXCommunicator.getMultipleStatistics();
		Thread.sleep(5000);
		ControllableProperty control = new ControllableProperty();
		String name = "Network#CloudConfigurationServiceConnection";
		String value = "1";
		control.setProperty(name);
		control.setValue(value);
		crestronNVXCommunicator.controlProperty(control);
	}

	@Test
	void testIGMPSupport() throws Exception {
		crestronNVXCommunicator.getMultipleStatistics();
		Thread.sleep(5000);
		ControllableProperty control = new ControllableProperty();
		String name = "Network#IGMPSupport";
		String value = "v3";
		control.setProperty(name);
		control.setValue(value);
		crestronNVXCommunicator.controlProperty(control);
	}

}
