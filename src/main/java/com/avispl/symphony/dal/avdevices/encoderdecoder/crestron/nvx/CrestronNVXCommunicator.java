/*
 *  Copyright (c) 2024 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.encoderdecoder.crestron.nvx;


import static com.avispl.symphony.dal.avdevices.encoderdecoder.crestron.nvx.common.CrestronPropertyList.RECEIVE_DEVICE_NAME;
import static com.avispl.symphony.dal.avdevices.encoderdecoder.crestron.nvx.common.CrestronPropertyList.TRANSMIT_DEVICE_NAME;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import javax.security.auth.login.FailedLoginException;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import com.avispl.symphony.api.dal.control.Controller;
import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty;
import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty.Button;
import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty.DropDown;
import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty.Slider;
import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty.Switch;
import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty.Text;
import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.dto.monitor.Statistics;
import com.avispl.symphony.api.dal.error.CommandFailureException;
import com.avispl.symphony.api.dal.error.ResourceNotReachableException;
import com.avispl.symphony.api.dal.monitor.Monitorable;
import com.avispl.symphony.dal.avdevices.encoderdecoder.crestron.nvx.common.CrestronCommand;
import com.avispl.symphony.dal.avdevices.encoderdecoder.crestron.nvx.common.CrestronConstant;
import com.avispl.symphony.dal.avdevices.encoderdecoder.crestron.nvx.common.CrestronControlCommand;
import com.avispl.symphony.dal.avdevices.encoderdecoder.crestron.nvx.common.CrestronPropertyList;
import com.avispl.symphony.dal.avdevices.encoderdecoder.crestron.nvx.common.CrestronUri;
import com.avispl.symphony.dal.avdevices.encoderdecoder.crestron.nvx.common.DeviceModel;
import com.avispl.symphony.dal.avdevices.encoderdecoder.crestron.nvx.common.PingMode;
import com.avispl.symphony.dal.avdevices.encoderdecoder.crestron.nvx.common.TimeZone;
import com.avispl.symphony.dal.avdevices.encoderdecoder.crestron.nvx.common.routing.AudioMode;
import com.avispl.symphony.dal.avdevices.encoderdecoder.crestron.nvx.dto.Streams;
import com.avispl.symphony.dal.communicator.RestCommunicator;
import com.avispl.symphony.dal.util.StringUtils;

/**
 * CrestronNVXCommunicator
 * Supported features are:
 * General
 * <ul>
 *   <li>Model</li>
 *   <li>Firmware Version</li>
 *   <li>Firmware Build Date</li>
 *   <li>Serial Number</li>
 *   <li>Device Manufacturer</li>
 *   <li>Device Id</li>
 *   <li>Device Name</li>
 *   <li>PUF Version</li>
 *   <li>Device Key</li>
 *   <li>Device Ready</li>
 *   <li>Front Panel Lockout</li>
 *   <li>Reboot Button*</li>
 *   <li>Reboot Reason</li>
 *   <li>Firmware Upgraded Status</li>
 * </ul>
 *
 * Network group:
 * <ul>
 *   <li>Host Name</li>
 *   <li>Domain Name</li>
 *   <li>DHCP Enabled</li>
 *   <li>IP address</li>
 *   <li>Subnet Mask</li>
 *   <li>Default Gateway</li>
 *   <li>Primary Static DNS</li>
 *   <li>Secondary Static DNS</li>
 *   <li>Link Active</li>
 *   <li>MAC Address</li>
 *   <li>IGMP Support*</li>
 *   <li>Cloud Configuration Service Connection*</li>
 * </ul>
 *
 * Control System Connection group:
 * <ul>
 *   <li>Encrypt Connection</li>
 *   For each control system connection:
 *     <ul>
 *       <li>IP ID </li>
 *       <li>Room ID</li>
 *       <li>IP Address/Hostname</li>
 *       <li>Type</li>
 *       <li>Server Port </li>
 *       <li>Connection</li>
 *       <li>Status</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * Input group: (Encoder)
 * <ul>
 *   <li>Name</li>
 *   <li>Sync</li>
 *   <li>Resolution</li>
 *   <li>Source HDCP</li>
 *   <li>Interlaced</li>
 *   <li>Aspect Ratio</li>
 *   <li>Audio Format</li>
 *   <li>Audio Channels</li>
 *   <li>EDID</li>
 *   <li>HDCP Receiver Capability</li>
 * </ul>
 *
 * Output group: (Decoder)
 * <ul>
 *   <li>Name</li>
 *   <li>Sink Connected</li>
 *   <li>Resolution</li>
 *   <li>Source HDCP</li>
 *   <li>Disabled by HDCP</li>
 *   <li>Aspect Ratio</li>
 *   <li>Analog Audio Volume*</li>
 * </ul>
 *
 * Auto Update Group
 * <ul>
 *   <li>Auto Update* </li>
 *   <li>Custom URL</li>
 *   <li>Custom URL Path </li>
 *   <li>Schedule Day of Week</li>
 *   <li>Schedule Time of Day</li>
 *   <li>Schedule Poll Interval</li>
 * </ul>
 *
 * Date Time group:
 * <ul>
 *   <li>Synchronize Now Button* (not found)</li>
 *   <li>NTP Time Servers </li>
 *   <li>Time Zone*</li>
 *   <li>Date*</li>
 *   <li>Time*</li>
 * </ul>
 *
 * Discovery group:
 * <ul>
 *   <li>Discovery Agent*</li>
 *   <li>TTL</li>
 * </ul>
 *
 * Stream group:
 * <ul>
 *   <li>Mode*</li>
 *   <li>Device Name</li>
 *   <li>Stream Location</li>
 *   <li>Multicast Address</li>
 *   <li>Status</li>
 *   <li>Resolution</li>
 * </ul>
 *
 * Stream Subscription group: (Receiver)
 * <ul>
 *   <li>For each subscribed stream:
 *     <ul>
 *       <li>Device Name </li>
 *       <li>Session Name</li>
 *       <li>RTSP Address</li>
 *       <li>Multicast Address</li>
 *       <li>Resolution</li>
 *       <li>Audio Format</li>
 *       <li>Bitrate</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * Streams Available group: (Receiver)
 * <ul>
 *   <li>For each available stream:
 *     <ul>
 *       <li>Device Name (SessionName)</li>
 *       <li>Session Name</li>
 *       <li>RTSP Address</li>
 *       <li>Multicast Address</li>
 *       <li>Resolution</li>
 *       <li>Audio Format</li>
 *       <li>Bitrate</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * Input Routing group:
 * <ul>
 *   <li>Automatic Input Routing*</li>
 *   <li>Audio Source</li>
 *   <li>Active Audio Source</li>
 *   <li>Video Source</li>
 *   <li>Active Video Source</li>
 *   <li>Analog Audio Mode* </li>
 * </ul>
 *
 * <li>Note: Properties marked with an asterisk (*) should also be controllable</li>
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 11/6/2024
 * @since 1.0.0
 */
public class CrestronNVXCommunicator extends RestCommunicator implements Monitorable, Controller {

	/**
	 * A mapper for reading and writing JSON using Jackson library.
	 * ObjectMapper provides functionality for converting between Java objects and JSON.
	 * It can be used to serialize objects to JSON format, and deserialize JSON data to objects.
	 */
	ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * cache to store key and value
	 */
	private final Map<String, JsonNode> cacheKeyAndValue = new HashMap<>();

	/**
	 * cache to store filter value
	 */
	private final Map<String, String> cacheFilterValue = new HashMap<>();

	/**
	 * Keep track number of requests send to device
	 */
	private int countMonitoringAndControllingCommand = 0;

	/**
	 * store authentication information
	 */
	private String authenticationCookie = CrestronConstant.NONE;

	/**
	 * store current device mode (Receiver/Transmitter)
	 */
	private String deviceMode = CrestronConstant.NONE;

	/**
	 * configManagement imported from the user interface
	 */
	private String configManagement;

	/**
	 * configManagement in boolean value
	 */
	private boolean isConfigManagement;

	/**
	 * ReentrantLock to prevent telnet session is closed when adapter is retrieving statistics from the device.
	 */
	private final ReentrantLock reentrantLock = new ReentrantLock();

	/**
	 * Store previous/current ExtendedStatistics
	 */
	private ExtendedStatistics localExtendedStatistics;

	/**
	 * isEmergencyDelivery to check if control flow is trigger
	 */
	private boolean isEmergencyDelivery;

	/**
	 * ping mode
	 */
	private PingMode pingMode = PingMode.ICMP;

	/**
	 * Current Stream detail of device (StreamReceive/StreamTransmit)
	 */
	private JsonNode currentStream;

	/**
	 * Current control system displayed in adapter
	 */
	private JsonNode currentControlSystem;

	/**
	 * Date format
	 */
	private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");

	/**
	 * Time format
	 */
	private final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");

	/**
	 * Date and time format
	 */
	private final DateTimeFormatter datetimeFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");

	/**
	 * Retrieves {@link #pingMode}
	 *
	 * @return value of {@link #pingMode}
	 */
	public String getPingMode() {
		return pingMode.name();
	}

	/**
	 * Sets {@link #pingMode} value
	 *
	 * @param pingMode new value of {@link #pingMode}
	 */
	public void setPingMode(String pingMode) {
		this.pingMode = PingMode.ofString(pingMode);
	}

	/**
	 * Retrieves {@code {@link #configManagement}}
	 *
	 * @return value of {@link #configManagement}
	 */
	public String getConfigManagement() {
		return configManagement;
	}

	/**
	 * Sets {@code configManagement}
	 *
	 * @param configManagement the {@code java.lang.String} field
	 */
	public void setConfigManagement(String configManagement) {
		this.configManagement = configManagement;
	}

	/**
	 * Constructs a new instance of CrestronNVXCommunicator.
	 *
	 * @throws IOException If an I/O error occurs while loading the properties mapping YAML file.
	 */
	public CrestronNVXCommunicator() throws IOException {
		this.setTrustAllCertificates(true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void authenticate() throws Exception {

	}

	/**
	 * {@inheritDoc}
	 * <p>
	 *
	 * Check for available devices before retrieving the value
	 * ping latency information to Symphony
	 */
	@Override
	public int ping() throws Exception {
		if (this.pingMode == PingMode.ICMP) {
			return super.ping();
		} else if (this.pingMode == PingMode.TCP) {
			if (isInitialized()) {
				long pingResultTotal = 0L;

				for (int i = 0; i < this.getPingAttempts(); i++) {
					long startTime = System.currentTimeMillis();

					try (Socket puSocketConnection = new Socket(this.host, this.getPort())) {
						puSocketConnection.setSoTimeout(this.getPingTimeout());
						if (puSocketConnection.isConnected()) {
							long pingResult = System.currentTimeMillis() - startTime;
							pingResultTotal += pingResult;
							if (this.logger.isTraceEnabled()) {
								this.logger.trace(String.format("PING OK: Attempt #%s to connect to %s on port %s succeeded in %s ms", i + 1, host, this.getPort(), pingResult));
							}
						} else {
							if (this.logger.isDebugEnabled()) {
								this.logger.debug(String.format("PING DISCONNECTED: Connection to %s did not succeed within the timeout period of %sms", host, this.getPingTimeout()));
							}
							return this.getPingTimeout();
						}
					} catch (SocketTimeoutException | ConnectException tex) {
						throw new RuntimeException("Socket connection timed out", tex);
					} catch (UnknownHostException ex) {
						throw new UnknownHostException(String.format("Connection timed out, UNKNOWN host %s", host));
					} catch (Exception e) {
						if (this.logger.isWarnEnabled()) {
							this.logger.warn(String.format("PING TIMEOUT: Connection to %s did not succeed, UNKNOWN ERROR %s: ", host, e.getMessage()));
						}
						return this.getPingTimeout();
					}
				}
				return Math.max(1, Math.toIntExact(pingResultTotal / this.getPingAttempts()));
			} else {
				throw new IllegalStateException("Cannot use device class without calling init() first");
			}
		} else {
			throw new IllegalArgumentException("Unknown PING Mode: " + pingMode);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Statistics> getMultipleStatistics() throws Exception {
		reentrantLock.lock();

		try {
			Map<String, String> stats = new HashMap<>();
			Map<String, String> controlStats = new HashMap<>();
			ExtendedStatistics extendedStatistics = new ExtendedStatistics();
			List<AdvancedControllableProperty> advancedControllableProperties = new ArrayList<>();

			if (!isEmergencyDelivery) {
				convertConfigManagement();
				retrieveMonitoringAndControllableProperties();
				if (countMonitoringAndControllingCommand == CrestronCommand.values().length) {
					throw new ResourceNotReachableException("There was an error while retrieving monitoring data for all properties.");
				}
				populateMonitoringAndControllableProperties(stats, controlStats, advancedControllableProperties, false, CrestronConstant.EMPTY);
				if (isConfigManagement) {
					stats.putAll(controlStats);
					extendedStatistics.setControllableProperties(advancedControllableProperties);
				}
				extendedStatistics.setStatistics(stats);
				localExtendedStatistics = extendedStatistics;
			}
			isEmergencyDelivery = false;
		} finally {
			reentrantLock.unlock();
		}
		return Collections.singletonList(localExtendedStatistics);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void controlProperty(ControllableProperty controllableProperty) throws Exception {
		reentrantLock.lock();
		try {
			if (this.localExtendedStatistics == null || this.localExtendedStatistics.getStatistics() == null) {
				return;
			}
			isEmergencyDelivery = true;
			Map<String, String> stats = this.localExtendedStatistics.getStatistics();
			List<AdvancedControllableProperty> advancedControllableProperties = this.localExtendedStatistics.getControllableProperties();

			String property = controllableProperty.getProperty();
			String value = String.valueOf(controllableProperty.getValue());

			String[] propertyList = property.split(CrestronConstant.HASH);
			String propertyName = property;
			String groupName = property;
			if (property.contains(CrestronConstant.HASH)) {
				propertyName = propertyList[1];
				groupName = propertyList[0];
			}

			if (propertyName.equals(CrestronConstant.IPID) || propertyName.contains(CrestronConstant.NO) || propertyName.equals(CrestronConstant.UNIQUE_ID)) {
				cacheFilterValue.put(property, value);
				updateFilterCache(groupName + CrestronConstant.HASH);
			} else {
				CrestronControlCommand item = CrestronControlCommand.getEnumByName(propertyName);
				switch (item) {
					case IGMP_SUPPORT:
					case MODE:
						sendControlCommand(item.getUrl(), item.getName(), item.getParam(), item.getApiProperty(), value, true);
						updateLocalControlValue(stats, advancedControllableProperties, property, value);
						break;
					case TTL:
						float ttl = Float.parseFloat(value);
						sendControlCommand(item.getUrl(), item.getName(), item.getParam(), item.getApiProperty(), ttl, true);
						updateLocalControlValue(stats, advancedControllableProperties, property, String.valueOf((int) ttl));
						stats.put("DiscoveryConfig#TTLCurrentValue", String.valueOf((int) ttl));
						break;
					case REBOOT:
					case SYNCHRONIZE_NOW:
						sendControlCommand(item.getUrl(), item.getName(), item.getParam(), item.getApiProperty(), true, true);
						break;
					case CLOUD_CONFIGURATION:
					case AUTO_UPDATE:
					case AUTOMATIC_INPUT_ROUTING:
					case DISCOVERY_AGENT:
						boolean status = Objects.equals(value, "1");
						sendControlCommand(item.getUrl(), item.getName(), item.getParam(), item.getApiProperty(), status, true);
						updateLocalControlValue(stats, advancedControllableProperties, property, value);
						break;
					case ANALOG_VOLUME:
						String outputNumber = stats.get("Output#No");
						if (NumberUtils.isCreatable(outputNumber)) {
							float volume = Float.parseFloat(value);
							String uri = String.format(item.getUrl(), Integer.parseInt(outputNumber) - 1, "0");
							sendControlCommand(uri, item.getName(), item.getParam(), item.getApiProperty(), volume, true);
							updateLocalControlValue(stats, advancedControllableProperties, property, String.valueOf((int) volume));
							stats.put("Output#AnanalogAudioCurrentVolume", String.valueOf((int) volume));
						}
						break;
					case TIME:
					case DATE:
						controlDateTimeCommand(stats, item, value);
						updateLocalControlValue(stats, advancedControllableProperties, property, value);
						break;
					case TIMEZONE:
						TimeZone timeZone = TimeZone.getEnumByName(value);
						if (timeZone != null) {
							sendControlCommand(item.getUrl(), item.getName(), item.getParam(), item.getApiProperty(), timeZone.getValue(), true);
							updateLocalControlValue(stats, advancedControllableProperties, property, timeZone.getName());
						}
						break;
					case AUDIO_MODE:
						AudioMode source = AudioMode.getEnumByValue(value);
						if (source != null) {
							sendControlCommand(item.getUrl(), item.getName(), item.getParam(), item.getApiProperty(), source.getValue(), true);
							updateLocalControlValue(stats, advancedControllableProperties, property, value);
						}
						break;
					default:
						if (logger.isWarnEnabled()) {
							logger.warn(String.format("Unable to execute %s command on device not supported", property));
						}
						break;

				}
			}
		} finally {
			reentrantLock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void controlProperties(List<ControllableProperty> controllableProperties) throws Exception {
		if (CollectionUtils.isEmpty(controllableProperties)) {
			throw new IllegalArgumentException("ControllableProperties can not be null or empty");
		}
		for (ControllableProperty p : controllableProperties) {
			try {
				controlProperty(p);
			} catch (Exception e) {
				logger.error(String.format("Error when control property %s", p.getProperty()), e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void internalInit() throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("Internal init is called.");
		}

		super.internalInit();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void internalDestroy() {
		if (logger.isDebugEnabled()) {
			logger.debug("Internal destroy is called.");
		}

		cacheKeyAndValue.clear();
		cacheFilterValue.clear();
		super.internalDestroy();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected HttpHeaders putExtraRequestHeaders(HttpMethod httpMethod, String uri, HttpHeaders headers) throws Exception {
		headers.set("Content-Type", "application/json");
		if (StringUtils.isNotNullOrEmpty(this.authenticationCookie)) {
			headers.set(CrestronConstant.COOKIE, this.authenticationCookie);
		}
		return super.putExtraRequestHeaders(httpMethod, uri, headers);
	}

	/**
	 * Send GET request to retrieve all monitoring and controllable properties of Crestron device.
	 */
	public void retrieveMonitoringAndControllableProperties() throws Exception {
		if (!isValidCookie()) {
			throw new FailedLoginException("Invalid cookie session. Please check credentials");
		}
		for (CrestronCommand command : CrestronCommand.values()) {
			String groupName = command.getGroupCommand();
			String apiGroup = command.getCommand();
			if (Objects.equals(CrestronConstant.NONE, this.deviceMode) || (!Objects.equals(command.getDeviceMode(), CrestronConstant.EMPTY) && !Objects.equals(this.deviceMode, command.getDeviceMode()))) {
				continue;
			}
			if (groupName.equals(CrestronCommand.DEVICE_SPECIFIC.getGroupCommand()) || groupName.equals(CrestronCommand.INPUT_ROUTING.getGroupCommand())) {
				continue;
			}
			JsonNode response = sendGetCommand(apiGroup);
			cacheKeyAndValue.put(groupName, extractApiResponseByGroup(apiGroup, response));
		}
	}

	/**
	 * Send GET request to retrieve device mode
	 */
	private void retrieveDeviceMode() throws FailedLoginException {
		JsonNode response = sendGetCommand(CrestronCommand.DEVICE_SPECIFIC.getCommand());
		cacheKeyAndValue.put(CrestronCommand.DEVICE_SPECIFIC.getGroupCommand(), extractApiResponseByGroup(CrestronCommand.DEVICE_SPECIFIC.getCommand(), response));
		cacheKeyAndValue.put(CrestronCommand.INPUT_ROUTING.getGroupCommand(), cacheKeyAndValue.get(CrestronCommand.DEVICE_SPECIFIC.getGroupCommand()));
		JsonNode deviceSpecific = cacheKeyAndValue.get(CrestronCommand.DEVICE_SPECIFIC.getGroupCommand());
		if (deviceSpecific != null) {
			this.deviceMode = getDefaultValueForNullData(deviceSpecific.get("DeviceMode"));
		}
	}

	/**
	 * Populate all monitoring and controllable properties
	 *
	 * @param stats store monitoring properties
	 * @param controlStats store control properties
	 * @param advancedControllableProperties store controllable properties
	 * @param isForcePopulate force populate specific value if need
	 * @param forcePopulateGroup name of group to force populate
	 */
	private void populateMonitoringAndControllableProperties(Map<String, String> stats, Map<String, String> controlStats, List<AdvancedControllableProperty> advancedControllableProperties,
			boolean isForcePopulate, String forcePopulateGroup) throws Exception {
		for (CrestronPropertyList property : CrestronPropertyList.values()) {
			String apiGroupName = property.getApiGroupName();
			JsonNode apiResponse = cacheKeyAndValue.get(apiGroupName);

			if (CrestronConstant.NONE.equals(this.deviceMode) || (!Objects.equals(CrestronConstant.EMPTY, property.getDeviceMode()) && !Objects.equals(this.deviceMode, property.getDeviceMode()))) {
				continue;
			}
			if (apiResponse == null || apiResponse.asText().contains(CrestronConstant.UNSUPPORT_RESTAPI) || isForcePopulate && !forcePopulateGroup.equals(property.getGroup())) {
				continue;
			}
			String propertyName = property.getGroup().concat(property.getName());
			String propertyValue = getDefaultValueForNullData(apiResponse.get(property.getApiPropertyName()));
			switch (property) {
				case MODEL:
				case FIRMWARE_VERSION:
				case FIRMWARE_BUILD_DATE:
				case SERIAL_NUMBER:
				case DEVICE_MANUFACTURER:
				case DEVICE_ID:
				case DEVICE_NAME:
				case PUF_VERSION:
				case DEVICE_KEY:
				case DEVICE_READY:
				case REBOOT_REASON:
				case CUSTOM_URL:
				case CUSTOM_URL_PATH:
				case FRONT_PANEL_LOCKOUT:
				case HOST_NAME:
				case DOMAIN_NAME:
				case FIRMWARE_UPGRADED_STATUS:
				case ENCRYPT_CONNECTION:
				case AUDIO_SOURCE:
				case VIDEO_SOURCE:
				case ACTIVE_AUDIO_SOURCE:
				case ACTIVE_VIDEO_SOURCE:
					stats.put(propertyName, propertyValue);
					break;
				case REBOOT_BUTTON:
				case SYNCHRONIZE_NOW:
					addAdvancedControlProperties(advancedControllableProperties, controlStats, createButton(propertyName, "Apply", CrestronConstant.EMPTY, 0L, CrestronConstant.TRUE), CrestronConstant.TRUE);
					break;
				case ADDRESS_SCHEMA:
					populateNetwork(stats, apiResponse);
					break;
				case IGMP_SUPPORT:
					if (!CrestronConstant.NONE.equals(propertyValue)) {
						String[] supportVersion = { "v2", "v3" };
						addAdvancedControlProperties(advancedControllableProperties, controlStats, createDropdown(propertyName, supportVersion, propertyValue), propertyValue);
					}
					break;
				case TTL:
					if (!CrestronConstant.NONE.equals(propertyValue)) {
						addAdvancedControlProperties(advancedControllableProperties, controlStats, createSlider(controlStats, propertyName, "1", "255", 1f, 255f, Float.parseFloat(propertyValue)), propertyValue);
						controlStats.put("DiscoveryConfig#TTLCurrentValue", propertyValue);
					}
					break;
				case AUTOMATIC_INPUT_ROUTING:
				case DISCOVERY_AGENT:
				case AUTO_UPDATE:
				case CLOUD_CONFIGURATION_SERVICE_CONNECTION:
					if (property == CrestronPropertyList.AUTOMATIC_INPUT_ROUTING) {
						DeviceModel model = getDeviceModel(stats);
						if (model != DeviceModel.DM_NVX_350 && model != DeviceModel.DM_NVX_352) {
							break;
						}
					}

					if (!CrestronConstant.NONE.equals(propertyValue)) {
						int status = propertyValue.equals(CrestronConstant.TRUE) ? 1 : 0;
						addAdvancedControlProperties(advancedControllableProperties, controlStats, createSwitch(propertyName, status, CrestronConstant.OFF, CrestronConstant.ON), String.valueOf(status));
					}
					break;
				case IP_ID:
					JsonNode maxEntries = apiResponse.get("MaxEntries");
					if (maxEntries == null || maxEntries.asInt() <= 0) {
						break;
					}

					JsonNode entries = apiResponse.get(CrestronConstant.ENTRIES);
					String currentIpId = cacheFilterValue.get(propertyName);
					List<String> ipIds = new ArrayList<>();
					this.currentControlSystem = NullNode.getInstance();

					if (entries != null && entries.isArray() && !entries.isEmpty()) {
						for (JsonNode item : entries) {
							String ipId = item.get("IpId").asText();
							ipIds.add(ipId);
							if (Objects.equals(ipId, currentIpId)) {
								this.currentControlSystem = item;
							}
						}

						if (this.currentControlSystem == null || this.currentControlSystem.isNull()) {
							this.currentControlSystem = entries.get(0);
							currentIpId = this.currentControlSystem.get("IpId").asText();
						}
						addAdvancedControlProperties(advancedControllableProperties, controlStats, createDropdown(propertyName, ipIds.toArray(new String[0]), currentIpId), currentIpId);
					}
					break;
				case ROOM_ID:
				case IP_ADDRESS_HOSTNAME:
				case TYPE:
				case SERVER_PORT:
				case CONNECTION:
				case STATUS:
					if (this.currentControlSystem == null) {
						break;
					}
					stats.put(propertyName, getDefaultValueForNullData(this.currentControlSystem.get(property.getApiPropertyName())));
					break;
				case INPUT_NAME:
				case SYNC_DETECTED:
				case HORIZONTAL_RESOLUTION:
				case VERTICAL_RESOLUTION:
				case INPUT_SOURCE_HDCP:
				case SOURCE_CONTENT_STREAM_TYPE:
				case INTERLACED:
				case INPUT_ASPECT_RATIO:
				case AUDIO_FORMAT:
				case AUDIO_CHANNELS:
				case EDID:
				case HDCP_RECEIVER_CAPABILITY:
				case INPUT_NO:
					populateAudioVideoInput(stats, controlStats, advancedControllableProperties, apiResponse, propertyName, property);
					break;
				case OUTPUT_NAME:
				case SINK_CONNECTED:
				case OUTPUT_HORIZONTAL_RESOLUTION:
				case OUTPUT_VERTICAL_RESOLUTION:
				case OUTPUT_SOURCE_HDCP:
				case DISABLED_BY_HDCP:
				case OUTPUT_ASPECT_RATIO:
				case ANALOG_AUDIO_VOLUME:
				case OUTPUT_NO:
					populateAudioVideoOutput(stats, controlStats, advancedControllableProperties, apiResponse, propertyName, property);
					break;
				case SCHEDULE_DAY_OF_WEEK:
				case SCHEDULE_POLL_INTERVAL:
				case SCHEDULE_TIME_OF_DAY:
					JsonNode autoUpdateSchedule = apiResponse.get(CrestronConstant.AUTO_UPDATE_SCHEDULE);
					if (autoUpdateSchedule != null) {
						stats.put(propertyName, getDefaultValueForNullData(autoUpdateSchedule.get(property.getApiPropertyName())));
					}
					break;
				case TIMEZONE:
				case DATE:
				case TIME:
				case NTPTIMESERVERS:
					populateDateTime(stats, controlStats, advancedControllableProperties, apiResponse, propertyName, property);
					break;
				case RECEIVE_UUID:
				case TRANSMIT_UUID:
					Streams stream = objectMapper.treeToValue(apiResponse, Streams.class);
					if (!stream.getStreams().isEmpty()) {
						this.currentStream = objectMapper.valueToTree(stream.getStreams().get(0));
					} else {
						this.currentStream = null;
					}
					break;
				case TRANSMIT_MODE:
				case RECEIVE_MODE:
					DeviceModel deviceModel = getDeviceModel(stats);
					if (deviceModel != DeviceModel.DM_NVX_350 && deviceModel != DeviceModel.DM_NVX_352) {
						stats.put(propertyName, this.deviceMode);
						break;
					}

					if (!CrestronConstant.NONE.equals(this.deviceMode)) {
						String[] modes = { "Transmitter", "Receiver" };
						addAdvancedControlProperties(advancedControllableProperties, controlStats, createDropdown(propertyName, modes, this.deviceMode), this.deviceMode);
					}
					break;
				case TRANSMIT_DEVICE_NAME:
				case RECEIVE_DEVICE_NAME:
				case RECEIVE_STREAM_LOCATION:
				case RECEIVE_MULTICAST_ADDRESS:
				case RECEIVE_RECEIVE_STATUS:
				case RECEIVE_RECEIVE_HORIZONTAL_RESOLUTION:
				case RECEIVE_RECEIVE_VERTICAL_RESOLUTION:
				case TRANSMIT_STREAM_LOCATION:
				case TRANSMIT_MULTICAST_ADDRESS:
				case TRANSMIT_RECEIVE_STATUS:
				case TRANSMIT_RECEIVE_HORIZONTAL_RESOLUTION:
				case TRANSMIT_RECEIVE_VERTICAL_RESOLUTION:
					if (this.currentStream == null) {
						break;
					}
					if (property == TRANSMIT_DEVICE_NAME || property == RECEIVE_DEVICE_NAME) {
						stats.put(propertyName, propertyValue);
					} else {
						stats.put(propertyName, getDefaultValueForNullData(currentStream.get(property.getApiPropertyName())));
					}
					break;
				case SUB_UNIQUE_ID:
				case AVAILABLE_UNIQUE_ID:
					String uniqueId = cacheFilterValue.get(propertyName);
					List<String> uniqueIds = new ArrayList<>();
					Iterator<Entry<String, JsonNode>> fields = null;
					JsonNode streamType = null;
					this.currentStream = null;

					if (property == CrestronPropertyList.SUB_UNIQUE_ID && apiResponse.has(CrestronConstant.SUBSCRIPTIONS)) {
						JsonNode subscriptionJson = apiResponse.get(CrestronConstant.SUBSCRIPTIONS);
						streamType = subscriptionJson;
						fields = subscriptionJson.fields();
					} else if (property == CrestronPropertyList.AVAILABLE_UNIQUE_ID && apiResponse.has(CrestronConstant.STREAMS)) {
						fields = apiResponse.get(CrestronConstant.STREAMS).fields();
						streamType = apiResponse.get(CrestronConstant.STREAMS);
					}

					if (fields == null) {
						break;
					}
					while (fields.hasNext()) {
						Entry<String, JsonNode> field = fields.next();
						String key = field.getKey();
						uniqueIds.add(key);
						if (uniqueId == null) {
							uniqueId = uniqueIds.get(0);
						}
						if (uniqueId.equals(key) && streamType != null) {
							this.currentStream = streamType.get(uniqueId);
						}
					}

					if (uniqueIds.contains(uniqueId)) {
						addAdvancedControlProperties(advancedControllableProperties, controlStats, createDropdown(propertyName, uniqueIds.toArray(new String[0]), uniqueId), uniqueId);
					}
					break;
				case SUB_SESSION_NAME:
				case SUB_RTSP_ADDRESS:
				case SUB_MULTICAST_ADDRESS:
				case SUB_RESOLUTION:
				case SUB_AUDIO_FORMAT:
				case SUB_BITRATE:
				case SUB_TRANSPORT:
				case SUB_ENCRYPTION:
				case AVAILABLE_ENCRYPTION:
				case AVAILABLE_TRANSPORT:
				case AVAILABLE_SESSION_NAME:
				case AVAILABLE_RTSP_ADDRESS:
				case AVAILABLE_MULTICAST_ADDRESS:
				case AVAILABLE_RESOLUTION:
				case AVAILABLE_AUDIO_FORMAT:
				case AVAILABLE_BITRATE:
					if (this.currentStream == null) {
						break;
					}
					stats.put(propertyName, getDefaultValueForNullData(this.currentStream.get(property.getApiPropertyName())));
					break;
				case ANALOG_AUDIO_MODE:
					DeviceModel model = getDeviceModel(stats);
					if (model != DeviceModel.DM_NVX_350 && model != DeviceModel.DM_NVX_352) {
						break;
					}

					AudioMode mode = AudioMode.getEnumByValue(propertyValue);
					if (mode != null) {
						addAdvancedControlProperties(advancedControllableProperties, controlStats,
								createDropdown(propertyName, Arrays.stream(AudioMode.values()).map(AudioMode::getValue).toArray(String[]::new), mode.getValue()), mode.getValue());
					}
					break;
				default:
					break;
			}
		}
	}

	/**
	 * Send GET request to get monitoring property
	 */
	private JsonNode sendGetCommand(String uri) throws FailedLoginException {
		try {
			String response = this.doGet(uri, String.class);
			if (StringUtils.isNotNullOrEmpty(response)) {
				return objectMapper.readTree(response);
			}
		} catch (FailedLoginException e) {
			throw new FailedLoginException("Error when login, please check the credentials");
		} catch (CommandFailureException e) {
			throw new ResourceNotReachableException("Error when send api request to " + uri);
		} catch (JsonParseException e) {
			throw new FailedLoginException("Error when convert json to string");
		} catch (Exception e) {
			countMonitoringAndControllingCommand++;
			logger.error("Error when send api request to " + uri + " with error message: ", e);
		}
		return null;
	}

	/**
	 * Send POST request command to control device
	 *
	 * @param uri device api
	 * @param name of control property field
	 * @param param request body
	 * @param fieldName name of field to control
	 * @param value to set for specific filed
	 * @param isRetry retry to send control command if timeout
	 */
	private void sendControlCommand(String uri, String name, String param, String fieldName, Object value, boolean isRetry) throws Exception {
		try {
			HashMap<String, Object> body = new HashMap<>();
			body.put(fieldName, value);

			JsonNode jsonBody = objectMapper.readTree(String.format(param, objectMapper.writeValueAsString(body)));
			String response = this.doPost(uri, jsonBody, String.class);
			JsonNode jsonResponse = objectMapper.readTree(response);
			JsonNode actions = jsonResponse.get("Actions");

			if (response == null || actions == null || actions.get(0) == null) {
				throw new IllegalArgumentException(String.format("Failed to send control command %s to control %s", uri, name));
			}

			JsonNode results = actions.get(0).get("Results");
			if (results == null || !results.isArray() || results.isEmpty()) {
				return;
			}

			List<JsonNode> status = objectMapper.readerFor(new TypeReference<List<JsonNode>>() {
			}).readValue(results);
			int statusId = Integer.parseInt(status.get(0).get("StatusId").asText());
			if (statusId < 0) {
				throw new IllegalArgumentException(String.format("Failed to send control command %s to control %s with status id %s", uri, name, statusId));
			}
		} catch (Exception e) {
			if (isRetry && getCookieSession()) {
				sendControlCommand(uri, name, param, fieldName, value, false);
				return;
			}
			throw new IllegalArgumentException(String.format("Can't control %s with value is %s. ", name, value), e);
		}
	}

	/**
	 * Send POST request command to control date time
	 *
	 * @param stats store monitoring properties
	 * @param item command to be sent
	 * @param value to send to device
	 */
	private void controlDateTimeCommand(Map<String, String> stats, CrestronControlCommand item, String value) {
		try {
			if (StringUtils.isNullOrEmpty(value)) {
				throw new IllegalArgumentException("Failed to send command to control date time due to invalid value " + value);
			}

			String currentDate = stats.get("DateTime#Date");
			String currentTime = stats.get("DateTime#Time");
			String dateTimeString = "%s %s";
			if (currentDate == null || currentTime == null) {
				return;
			}
			String newDateTime = item == CrestronControlCommand.DATE ? String.format(dateTimeString, value, currentTime) : String.format(dateTimeString, currentDate, value);
			String newDateTimeFormat = LocalDateTime.parse(newDateTime, datetimeFormat).atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

			if (CrestronConstant.NONE.equals(newDateTimeFormat)) {
				return;
			}
			sendControlCommand(item.getUrl(), item.getName(), item.getParam(), item.getApiProperty(), newDateTimeFormat, true);
		} catch (Exception e) {
			throw new IllegalArgumentException("Failed to send command to control date time ", e);
		}
	}

	/**
	 * Updates devices' control value, after the control command was executed with the specified value.
	 *
	 * @param stats the stats are list of Statistics
	 * @param advancedControllableProperties the advancedControllableProperty are AdvancedControllableProperty instance
	 * @param name of the control property
	 * @param value to set to the control property
	 */
	private void updateLocalControlValue(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties, String name, String value) {
		stats.put(name, value);
		advancedControllableProperties.stream().filter(advancedControllableProperty ->
				name.equals(advancedControllableProperty.getName())).findFirst().ifPresent(advancedControllableProperty ->
				advancedControllableProperty.setValue(value));
	}

	/**
	 * Populate audio video input
	 *
	 * @param stats store monitoring properties
	 * @param advancedControllableProperties store controllable properties
	 * @param apiResponse response from API
	 * @param groupName name of group
	 * @param property name of property
	 */
	private void populateAudioVideoInput(Map<String, String> stats, Map<String, String> controlStats, List<AdvancedControllableProperty> advancedControllableProperties, JsonNode apiResponse,
			String groupName, CrestronPropertyList property)
			throws IOException {
		String number = cacheFilterValue.get(CrestronConstant.INPUT_GROUP + CrestronPropertyList.INPUT_NO.getName());
		if (!apiResponse.has("Inputs")) {
			return;
		}

		List<JsonNode> inputJson = objectMapper.readerFor(new TypeReference<List<JsonNode>>() {
		}).readValue(apiResponse.get("Inputs"));
		JsonNode input = NullNode.getInstance();
		JsonNode portInput = NullNode.getInstance();
		List<JsonNode> portInputs;

		if (inputJson == null || inputJson.isEmpty()) {
			return;
		}
		if (number == null || Integer.parseInt(number) - 1 >= inputJson.size()) {
			number = "1";
		}

		int index = Integer.parseInt(number) - 1;
		if (index >= 0 && index < inputJson.size()) {
			input = inputJson.get(index);
		}

		if (input != null && !input.isNull() && input.has("Ports")) {
			portInputs = objectMapper.readerFor(new TypeReference<List<JsonNode>>() {
			}).readValue(input.get("Ports"));
			portInput = portInputs != null && !portInputs.isEmpty() ? portInputs.get(0) : null;
		}

		if (portInput == null || portInput.isNull()) {
			return;
		}
		switch (property) {
			case INPUT_NO:
				int initialValue = Integer.parseInt(number);
				String[] values = IntStream.range(0, inputJson.size()).mapToObj(i -> String.valueOf(i + 1)).toArray(String[]::new);
				addAdvancedControlProperties(advancedControllableProperties, controlStats, createDropdown(groupName, values, String.valueOf(initialValue)), number);
				break;
			case INPUT_NAME:
			case INPUT_SOURCE_HDCP:
			case HDCP_RECEIVER_CAPABILITY:
				JsonNode hdmi = portInput.get(CrestronConstant.HDMI);
				if (hdmi != null) {
					String hdmiValue = getDefaultValueForNullData(hdmi.get(property.getApiPropertyName()));
					stats.put(groupName, hdmiValue);
				}
				break;
			case AUDIO_FORMAT:
			case AUDIO_CHANNELS:
				JsonNode digital = portInput.get(CrestronConstant.AUDIO).get(CrestronConstant.DIGITAL);
				stats.put(groupName, getDefaultValueForNullData(digital.get(property.getApiPropertyName())));
				break;
			case EDID:
				JsonNode Edid = portInput.get(CrestronConstant.EDID);
				if (Edid != null) {
					stats.put(groupName, getDefaultValueForNullData(Edid.get(property.getApiPropertyName())));
				}
				break;
			case VERTICAL_RESOLUTION:
			case HORIZONTAL_RESOLUTION:
			case INTERLACED:
			case INPUT_ASPECT_RATIO:
			case SYNC_DETECTED:
				String value = getDefaultValueForNullData(portInput.get(property.getApiPropertyName()));
				if ((property == CrestronPropertyList.SYNC_DETECTED || property == CrestronPropertyList.INTERLACED) && !CrestronConstant.NONE.equals(value)) {
					stats.put(groupName, value.equals(CrestronConstant.TRUE) ? CrestronConstant.YES : CrestronConstant.NO);
				} else {
					stats.put(groupName, value);
				}
				break;
		}
	}

	/**
	 * Populate audio video input
	 *
	 * @param stats store monitoring properties
	 * @param controlStats store control properties
	 * @param advancedControllableProperties store controllable properties
	 * @param apiResponse response from api
	 * @param groupName name of group
	 * @param property name of property
	 */
	private void populateAudioVideoOutput(Map<String, String> stats, Map<String, String> controlStats, List<AdvancedControllableProperty> advancedControllableProperties, JsonNode apiResponse,
			String groupName, CrestronPropertyList property)
			throws IOException {
		String number = cacheFilterValue.get(CrestronConstant.OUTPUT_GROUP + CrestronPropertyList.OUTPUT_NO.getName());
		if (!apiResponse.has("Outputs")) {
			return;
		}

		List<JsonNode> outputJson = objectMapper.readerFor(new TypeReference<List<JsonNode>>() {
		}).readValue(apiResponse.get("Outputs"));
		JsonNode output = NullNode.getInstance();
		JsonNode portOutput = NullNode.getInstance();
		List<JsonNode> portOutputs;

		if (outputJson == null || outputJson.isEmpty()) {
			return;
		}
		if (number == null || Integer.parseInt(number) - 1 > outputJson.size()) {
			number = "1";
		}

		int index = Integer.parseInt(number) - 1;
		if (index >= 0 && index < outputJson.size()) {
			output = outputJson.get(index);
		}

		if (output != null && !output.isNull() && output.has("Ports")) {
			portOutputs = objectMapper.readerFor(new TypeReference<List<JsonNode>>() {
			}).readValue(output.get("Ports"));
			portOutput = portOutputs != null && !portOutputs.isEmpty() ? portOutputs.get(0) : null;
		}

		if (portOutput == null || portOutput.isNull()) {
			return;
		}
		switch (property) {
			case OUTPUT_NO:
				int initialValue = Integer.parseInt((number));
				String[] values = IntStream.range(0, outputJson.size()).mapToObj(i -> String.valueOf(i + 1)).toArray(String[]::new);
				addAdvancedControlProperties(advancedControllableProperties, controlStats, createDropdown(groupName, values, String.valueOf(initialValue)), String.valueOf(initialValue));
				break;
			case OUTPUT_NAME:
			case OUTPUT_SOURCE_HDCP:
			case DISABLED_BY_HDCP:
				JsonNode hdmi = portOutput.get(CrestronConstant.HDMI);
				if (hdmi == null) {
					break;
				}

				String hdmiValue = getDefaultValueForNullData(hdmi.get(property.getApiPropertyName()));
				if (property == CrestronPropertyList.DISABLED_BY_HDCP && !CrestronConstant.NONE.equals(hdmiValue)) {
					stats.put(groupName, hdmiValue.equals(CrestronConstant.TRUE) ? CrestronConstant.YES : CrestronConstant.NO);
				} else {
					stats.put(groupName, hdmiValue);
				}
				break;
			case SINK_CONNECTED:
				String value = getDefaultValueForNullData(portOutput.get(property.getApiPropertyName()));
				if (!Objects.equals(value, CrestronConstant.NONE)) {
					stats.put(groupName, value.equals(CrestronConstant.TRUE) ? CrestronConstant.YES : CrestronConstant.NO);
				} else {
					stats.put(groupName, value);
				}
				break;
			case OUTPUT_HORIZONTAL_RESOLUTION:
			case OUTPUT_VERTICAL_RESOLUTION:
			case OUTPUT_ASPECT_RATIO:
			case ANALOG_AUDIO_VOLUME:
				if (property == CrestronPropertyList.ANALOG_AUDIO_VOLUME) {
					String volume = getDefaultValueForNullData(portOutput.get("Audio").get("Volume"));
					if (!CrestronConstant.NONE.equals(volume)) {
						addAdvancedControlProperties(advancedControllableProperties, controlStats, createSlider(stats, groupName, "-80", "24", -80f, 24f, Float.parseFloat(volume)), volume);
						stats.put("Output#AnanalogAudioCurrentVolume", volume);
					}
				} else {
					stats.put(groupName, getDefaultValueForNullData(portOutput.get(property.getApiPropertyName())));
				}
				break;
		}
	}

	/**
	 * Populate date time
	 *
	 * @param stats store monitoring properties
	 * @param controlStats store control properties
	 * @param advancedControllableProperties store controllable properties
	 * @param apiResponse response from api
	 * @param groupName name of group
	 * @param property name of property
	 */
	private void populateDateTime(Map<String, String> stats, Map<String, String> controlStats, List<AdvancedControllableProperty> advancedControllableProperties, JsonNode apiResponse, String groupName,
			CrestronPropertyList property)
			throws IOException {
		JsonNode ntpJson = apiResponse.get(CrestronConstant.NTP);
		String value = getDefaultValueForNullData(apiResponse.get(property.getApiPropertyName()));
		switch (property) {
			case NTPTIMESERVERS:
				if (!ntpJson.has(CrestronConstant.SERVERS_CURRENT_KEY_LIST)) {
					break;
				}
				List<JsonNode> serverCurrentKeyListJson = objectMapper.readerFor(new TypeReference<List<JsonNode>>() {
				}).readValue(ntpJson.get(CrestronConstant.SERVERS_CURRENT_KEY_LIST));
				for (JsonNode serverCurrentKey : serverCurrentKeyListJson) {
					JsonNode server = ntpJson.get(CrestronConstant.SERVERS).get(serverCurrentKey.asText());
					String propertyName = groupName + serverCurrentKey.asText();
					String propertyValue = getDefaultValueForNullData(server.get(property.getApiPropertyName()));
					stats.put(propertyName, propertyValue);
				}
				break;
			case TIMEZONE:
				if (!CrestronConstant.NONE.equals(value)) {
					TimeZone zone = TimeZone.getEnumByValue(value);
					if (zone != null) {
						String[] timezones = Arrays.stream(TimeZone.values()).map(TimeZone::getName).toArray(String[]::new);
						addAdvancedControlProperties(advancedControllableProperties, controlStats, createDropdown(groupName, timezones, zone.getName()), zone.getName());
					}
				}
				break;
			case DATE:
			case TIME:
				if (!CrestronConstant.NONE.equals(value)) {
					DateTimeFormatter formater = property == CrestronPropertyList.DATE ? dateFormat : timeFormat;
					String displayValue = OffsetDateTime.parse(value, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDateTime().format(formater);
					addAdvancedControlProperties(advancedControllableProperties, controlStats, createText(groupName, displayValue), displayValue);
				}
				break;
		}
	}

	/**
	 * Populate network information
	 *
	 * @param stats store monitoring properties
	 * @param apiResponse response from api
	 */
	private void populateNetwork(Map<String, String> stats, JsonNode apiResponse)
			throws IOException {
		if (!apiResponse.has(CrestronConstant.ADAPTERS)) {
			return;
		}
		List<JsonNode> adapters = objectMapper.readerFor(new TypeReference<List<JsonNode>>() {
		}).readValue(apiResponse.get(CrestronConstant.ADAPTERS));

//		String schemaValue = cacheFilterValue.get(groupName);
		JsonNode currentAdapter = NullNode.getInstance();
//		for (JsonNode adapter: adapters) {
//			String schema = adapter.get(CrestronConstant.ADDRESS_SCHEMA).asText();
//
//			if (Objects.equals(schemaValue, schema)) {
//				currentAdapter = adapter;
//			}
//		}

		if (adapters != null && !adapters.isEmpty()) {
			currentAdapter = adapters.get(0);
		}

		if (currentAdapter == null || currentAdapter.isNull()) {
			return;
		}
		JsonNode currentSchema = currentAdapter.get(currentAdapter.get(CrestronConstant.ADDRESS_SCHEMA).asText());
		stats.put(CrestronConstant.NETWORK_GROUP + CrestronPropertyList.LINK_ACTIVE.getName(), getDefaultValueForNullData(currentAdapter.get(CrestronPropertyList.LINK_ACTIVE.getApiPropertyName())));
		stats.put(CrestronConstant.NETWORK_GROUP + CrestronPropertyList.MAC_ADDRESS.getName(), getDefaultValueForNullData(currentAdapter.get(CrestronPropertyList.MAC_ADDRESS.getApiPropertyName())));

		if (currentSchema == null) {
			return;
		}
		stats.put(CrestronConstant.NETWORK_GROUP + CrestronPropertyList.DHCP_ENABLED.getName(), getDefaultValueForNullData(currentSchema.get(CrestronPropertyList.DHCP_ENABLED.getApiPropertyName())));
		stats.put(CrestronConstant.NETWORK_GROUP + CrestronPropertyList.DEFAULT_GATEWAY.getName(),
				getDefaultValueForNullData(currentSchema.get(CrestronPropertyList.DEFAULT_GATEWAY.getApiPropertyName())));

		// Get address information
		if (currentSchema.has("Addresses")) {
			List<JsonNode> addresses = objectMapper.readerFor(new TypeReference<List<JsonNode>>() {
			}).readValue(currentSchema.get("Addresses"));
			for (JsonNode address : addresses) {
				stats.put(CrestronConstant.NETWORK_GROUP + CrestronPropertyList.IP_ADDRESS.getName(), getDefaultValueForNullData(address.get(CrestronPropertyList.IP_ADDRESS.getApiPropertyName())));
				stats.put(CrestronConstant.NETWORK_GROUP + CrestronPropertyList.SUBNET_MASK.getName(), getDefaultValueForNullData(address.get(CrestronPropertyList.SUBNET_MASK.getApiPropertyName())));
			}
		}

		// Get Static DNS information
		if (currentSchema.has("DnsServers")) {
			List<JsonNode> staticDns = objectMapper.readerFor(new TypeReference<List<JsonNode>>() {
			}).readValue(currentSchema.get("DnsServers"));
			if (staticDns != null && staticDns.size() >= 2) {
				stats.put(CrestronConstant.NETWORK_GROUP + CrestronPropertyList.PRIMARY_STATIC_DNS.getName(), getDefaultValueForNullData(staticDns.get(0)));
				stats.put(CrestronConstant.NETWORK_GROUP + CrestronPropertyList.SECONDARY_STATIC_DNS.getName(), getDefaultValueForNullData(staticDns.get(1)));
			}
		}

//		JsonNode value = currentAdapter.get(CrestronConstant.ADDRESS_SCHEMA);
//		stats.put(groupName, getDefaultValueForNullData(value));
	}

	/**
	 * Update monitoring data base on filter value
	 */
	private void updateFilterCache(String groupName) throws Exception {
		Map<String, String> stats = this.localExtendedStatistics.getStatistics();
		List<AdvancedControllableProperty> advancedControllableProperties = this.localExtendedStatistics.getControllableProperties();
		populateMonitoringAndControllableProperties(stats, stats, advancedControllableProperties, true, groupName);
	}

	/**
	 * check value is null or empty
	 *
	 * @param value input value
	 * @return value after checking
	 */
	private String getDefaultValueForNullData(JsonNode value) {
		return value == null || StringUtils.isNullOrEmpty(value.asText()) ? CrestronConstant.NONE : value.asText();
	}

	/**
	 * Login to crestron nvx device
	 */
	private boolean getCookieSession() {
		try {
			HttpClient client = this.obtainHttpClient(true);
			HttpPost httpPost = new HttpPost(buildDeviceFullPath(CrestronUri.LOGIN_API));
			httpPost.setEntity(new StringEntity(String.format(CrestronConstant.AUTHENTICATION_PARAM, this.getLogin(), this.getPassword())));

			HttpResponse response = client.execute(httpPost);
			StringBuilder sb = new StringBuilder();
			Header[] headers = response.getHeaders(CrestronConstant.SET_COOKIE);

			if (response.getStatusLine().getStatusCode() == 403) {
				return false;
			}
			Arrays.stream(headers).forEach(item -> sb.append(removeAttributes(item.getValue())));

			this.authenticationCookie = sb.toString();
		} catch (Exception e) {
			throw new ResourceNotReachableException("Failed to send login request to device", e);
		}
		return true;
	}

	/**
	 * Remove Secure, Path, HttpOnly attributes in cookie
	 *
	 * @param cookie cookie value
	 */
	private String removeAttributes(String cookie) {
		// Remove 'Secure' attribute
		cookie = cookie.replaceAll(";\\s*Secure", "");

		// Remove 'Path' attribute
		cookie = cookie.replaceAll(";\\s*Path=[^;]*", "");

		// Remove 'HttpOnly' attribute
		cookie = cookie.replaceAll(";\\s*HttpOnly", "");

		return cookie;
	}

	/**
	 * @param path url of the request
	 * @return String full path of the device
	 */
	private String buildDeviceFullPath(String path) {
		Objects.requireNonNull(path);

		return CrestronConstant.HTTPS
				+ getHost()
				+ CrestronConstant.COLON
				+ getPort()
				+ path;
	}

	/**
	 * Check API token validation
	 * If the token expires, we send a request to get a new token
	 *
	 * @return boolean
	 */
	private boolean isValidCookie() throws Exception {
		try {
			retrieveDeviceMode();
		} catch (Exception e) {
			boolean isAuthenticated = getCookieSession();
			if (isAuthenticated) {
				retrieveDeviceMode();
			}
			return isAuthenticated;
		}
		return true;
	}

	/**
	 * Get api response by group
	 *
	 * @param groupName name of api group
	 * @param response raw data response from api
	 */
	private JsonNode extractApiResponseByGroup(String groupName, JsonNode response) {
		if (response == null) {
			return response;
		}
		String[] groups = groupName.split("/");
		JsonNode result = response;
		for (String group : groups) {
			result = result.get(group);
			if (result == null) {
				return result;
			}
		}
		return result;
	}

	/**
	 * Get model of device
	 *
	 * @param stats store monitoring properties
	 */
	private DeviceModel getDeviceModel(Map<String, String> stats) {
		String model = stats.get(CrestronPropertyList.MODEL.getName());
		if (CrestronConstant.NONE.equals(model)) {
			return null;
		}

		return DeviceModel.getDeviceModelByName(model);
	}

	/**
	 * This method is used to validate input config management from user
	 */
	private void convertConfigManagement() {
		isConfigManagement = StringUtils.isNotNullOrEmpty(this.configManagement) && this.configManagement.equalsIgnoreCase(CrestronConstant.TRUE);
	}

	/**
	 * Create switch is control property for metric
	 *
	 * @param name the name of property
	 * @param status initial status (0|1)
	 * @return AdvancedControllableProperty switch instance
	 */
	private AdvancedControllableProperty createSwitch(String name, int status, String labelOff, String labelOn) {
		Switch toggle = new Switch();
		toggle.setLabelOff(labelOff);
		toggle.setLabelOn(labelOn);

		AdvancedControllableProperty advancedControllableProperty = new AdvancedControllableProperty();
		advancedControllableProperty.setName(name);
		advancedControllableProperty.setValue(status);
		advancedControllableProperty.setType(toggle);
		advancedControllableProperty.setTimestamp(new Date());

		return advancedControllableProperty;
	}

	/**
	 * Create dropdown advanced controllable property
	 *
	 * @param name the name of the control
	 * @param initialValue initial value of the control
	 * @return AdvancedControllableProperty dropdown instance
	 */
	private AdvancedControllableProperty createDropdown(String name, String[] values, String initialValue) {
		DropDown dropDown = new DropDown();
		dropDown.setOptions(values);
		dropDown.setLabels(values);

		return new AdvancedControllableProperty(name, new Date(), dropDown, initialValue);
	}

	/**
	 * Create AdvancedControllableProperty slider instance
	 *
	 * @param stats extended statistics
	 * @param name name of the control
	 * @param initialValue initial value of the control
	 * @return AdvancedControllableProperty slider instance
	 */
	private AdvancedControllableProperty createSlider(Map<String, String> stats, String name, String labelStart, String labelEnd, Float rangeStart, Float rangeEnd, Float initialValue) {
		stats.put(name, initialValue.toString());
		Slider slider = new Slider();
		slider.setLabelStart(labelStart);
		slider.setLabelEnd(labelEnd);
		slider.setRangeStart(rangeStart);
		slider.setRangeEnd(rangeEnd);

		return new AdvancedControllableProperty(name, new Date(), slider, initialValue);
	}

	/**
	 * Create text is control property for metric
	 *
	 * @param name the name of the property
	 * @param stringValue character string
	 * @return AdvancedControllableProperty Text instance
	 */
	private AdvancedControllableProperty createText(String name, String stringValue) {
		Text text = new Text();
		return new AdvancedControllableProperty(name, new Date(), text, stringValue);
	}

	/**
	 * Create a button.
	 *
	 * @param name name of the button
	 * @param label label of the button
	 * @param labelPressed label of the button after pressing it
	 * @param gracePeriod grace period of button
	 * @return This returns the instance of {@link AdvancedControllableProperty} type Button.
	 */
	private AdvancedControllableProperty createButton(String name, String label, String labelPressed, long gracePeriod, String value) {
		Button button = new Button();
		button.setLabel(label);
		button.setLabelPressed(labelPressed);
		button.setGracePeriod(gracePeriod);
		return new AdvancedControllableProperty(name, new Date(), button, value);
	}

	/**
	 * Add addAdvancedControlProperties if advancedControllableProperties different empty
	 *
	 * @param advancedControllableProperties advancedControllableProperties is the list that store all controllable properties
	 * @param stats store all statistics
	 * @param property the property is item advancedControllableProperties
	 * @throws IllegalStateException when exception occur
	 */
	private void addAdvancedControlProperties(List<AdvancedControllableProperty> advancedControllableProperties, Map<String, String> stats, AdvancedControllableProperty property, String value) {
		if (property != null) {
			advancedControllableProperties.removeIf(controllableProperty -> controllableProperty.getName().equals(property.getName()));

			String propertyValue = StringUtils.isNotNullOrEmpty(value) ? value : CrestronConstant.EMPTY;
			stats.put(property.getName(), propertyValue);

			advancedControllableProperties.add(property);
		}
	}
}
