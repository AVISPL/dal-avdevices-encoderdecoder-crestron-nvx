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

}
