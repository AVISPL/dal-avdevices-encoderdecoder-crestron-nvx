/*
 *  Copyright (c) 2024 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.encoderdecoder.crestron.nvx;


import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.security.auth.login.FailedLoginException;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;

import com.avispl.symphony.api.dal.control.Controller;
import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty;
import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.dto.monitor.Statistics;
import com.avispl.symphony.api.dal.error.CommandFailureException;
import com.avispl.symphony.api.dal.error.ResourceNotReachableException;
import com.avispl.symphony.api.dal.monitor.Monitorable;
import com.avispl.symphony.dal.avdevices.encoderdecoder.crestron.nvx.common.AuthenticationInfo;
import com.avispl.symphony.dal.avdevices.encoderdecoder.crestron.nvx.common.CrestronCommand;
import com.avispl.symphony.dal.avdevices.encoderdecoder.crestron.nvx.common.CrestronConstant;
import com.avispl.symphony.dal.avdevices.encoderdecoder.crestron.nvx.common.CrestronPropertyList;
import com.avispl.symphony.dal.avdevices.encoderdecoder.crestron.nvx.common.PingMode;
import com.avispl.symphony.dal.avdevices.encoderdecoder.crestron.nvx.common.TimeZone;
import com.avispl.symphony.dal.avdevices.encoderdecoder.crestron.nvx.dto.Stream;
import com.avispl.symphony.dal.avdevices.encoderdecoder.crestron.nvx.dto.Streams;
import com.avispl.symphony.dal.communicator.RestCommunicator;
import com.avispl.symphony.dal.util.StringUtils;


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

	private final Map<String, String> cacheFilterValue = new HashMap<>();

	private int countMonitoringAndControllingCommand = 0;

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
	 * configManagement imported from the user interface
	 */
	private String configManagement;

	private JsonNode currentStream = null;

	private JsonNode currentRoute = null;

	/**
	 * Used to authentication
	 */
	private AuthenticationInfo authenticationInfo;

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
	 * Retrieves {@link #configManagement}
	 *
	 * @return value of {@link #configManagement}
	 */
	public String getConfigManagement() {
		return configManagement;
	}

	/**
	 * Sets {@link #configManagement} value
	 *
	 * @param configManagement new value of {@link #configManagement}
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
			Map<String, String> statistics = new HashMap<>();
			ExtendedStatistics extendedStatistics = new ExtendedStatistics();
			final List<AdvancedControllableProperty> advancedControllableProperties = new ArrayList<>();
		  if (this.authenticationInfo == null) {
				authenticationInfo = new AuthenticationInfo();
			}
			isLogin();
			retrieveMonitoringAndControllableProperties();
			if (!isEmergencyDelivery) {
				if (countMonitoringAndControllingCommand == CrestronCommand.values().length) {
					throw new ResourceNotReachableException("There was an error while retrieving monitoring data for all properties.");
				}
				populateMonitoringAndControllableProperties(statistics, advancedControllableProperties);
				extendedStatistics.setStatistics(statistics);
				extendedStatistics.setControllableProperties(advancedControllableProperties);
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
//			Map<String, String> stats = this.localExtendedStatistics.getStatistics();
			String property = controllableProperty.getProperty();
			String value = String.valueOf(controllableProperty.getValue());

			String[] propertyList = property.split(CrestronConstant.HASH);
			String propertyName = property;
//			String groupName = property;
			if (property.contains(CrestronConstant.HASH)) {
				propertyName = propertyList[1];
//				groupName = propertyList[0];
			}

			if (propertyName.equals(CrestronConstant.IPID) || propertyName.contains(CrestronConstant.UUID) || propertyName.equals(CrestronConstant.UNIQUE_ID)) {
				cacheFilterValue.put(property, value);
				updateFilterCache();
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
		headers.set("Content-Type", "text/xml");
		headers.set("Content-Type", "application/json");
		if (authenticationInfo != null && authenticationInfo.getAuthorizeStatus().equals(CrestronConstant.AUTHORIZED)) {
			headers.set(CrestronConstant.X_CREST_XSRF_TOKEN, authenticationInfo.getToken());
			headers.set(CrestronConstant.COOKIE, authenticationInfo.getTrackId());
		}
		return super.putExtraRequestHeaders(httpMethod, uri, headers);
	}

	private void retrieveMonitoringAndControllableProperties() throws Exception {
		for (CrestronCommand command: CrestronCommand.values()) {
			String groupName = command.getGroupCommand();
			String uri = command.getCommand();
			JsonNode response = sendGetCommand(uri);
			if (response != null) {
				cacheKeyAndValue.put(groupName, response);
			}
		}
	}

	private void populateMonitoringAndControllableProperties(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) throws Exception {
		for (CrestronPropertyList property: CrestronPropertyList.values()) {
			String groupName = property.getGroup().concat(property.getName());
			String apiGroupName = property.getApiGroupName();
			JsonNode apiResponse = cacheKeyAndValue.get(apiGroupName);
			if (apiResponse == null) return;
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
//				case REBOOT_BUTTON:
				case CUSTOM_URL:
				case CUSTOM_URL_PATH:
				case AUTO_UPDATE:
				case FRONT_PANEL_LOCKOUT:
				case HOST_NAME:
				case DOMAIN_NAME:
				case FIRMWARE_UPGRADED_STATUS:
				case IGMP_SUPPORT:
				case ENCRYPT_CONNECTION:
				case CLOUD_CONFIGURATION_SERVICE_CONNECTION:
				case DISCOVERY_AGENT:
				case TTL:
					//				if (deviceOperation.getName().equals(DeviceOperation.REBOOT_BUTTON.getName()) && isConfigManagement) {
//					stats.put(deviceOperation.getName(), CrestronConstant.EMPTY);
//					controlStats.put(deviceOperation.getName(), CrestronConstant.EMPTY);
//					AdvancedControllableProperty videoMuteControl = createButton(CrestronConstant.SYSTEM_REBOOT, CrestronConstant.SYSTEM_REBOOT, "", 1L);
//					advancedControllableProperty.add(videoMuteControl);
//					continue;
//				}
					JsonNode propertyValue = apiResponse.get(property.getApiPropertyName());
					if (property == CrestronPropertyList.AUTO_UPDATE && propertyValue != null) {
						stats.put(groupName, propertyValue.asText().equals(CrestronConstant.TRUE)? CrestronConstant.ENABLED: CrestronConstant.DISABLED);
						continue;
					}
					stats.put(groupName, getDefaultValueForNullData(propertyValue));
					break;

				case DHCP_ENABLED:
//				case LINK_ACTIVE:
//				case MAC_ADDRESS:
//				case IP_ADDRESS:
//				case SUBNET_MASK:
//				case DEFAULT_GATEWAY:
//				case PRIMARY_STATIC_DNS:
//				case SECONDARY_STATIC_DNS:
					List<JsonNode> adapters = objectMapper.readerFor(new TypeReference<List<JsonNode>>(){}).readValue(apiResponse.get(CrestronConstant.ADAPTERS));
					for (JsonNode ipv4Schema: adapters) {
						JsonNode ipv4 = ipv4Schema.get("IPv4");
						stats.put(CrestronConstant.NETWORK_GROUP + CrestronPropertyList.LINK_ACTIVE.getName(), getDefaultValueForNullData(ipv4Schema.get(CrestronPropertyList.LINK_ACTIVE.getApiPropertyName())));
						stats.put(CrestronConstant.NETWORK_GROUP + CrestronPropertyList.MAC_ADDRESS.getName(), getDefaultValueForNullData(ipv4Schema.get(CrestronPropertyList.MAC_ADDRESS.getApiPropertyName())));

						if (ipv4 != null) {
							stats.put(CrestronConstant.NETWORK_GROUP + CrestronPropertyList.DHCP_ENABLED.getName(), getDefaultValueForNullData(ipv4.get(CrestronPropertyList.DHCP_ENABLED.getApiPropertyName())));
							stats.put(CrestronConstant.NETWORK_GROUP + CrestronPropertyList.DEFAULT_GATEWAY.getName(), getDefaultValueForNullData(ipv4.get(CrestronPropertyList.DEFAULT_GATEWAY.getApiPropertyName())));

							// Get address information
							List<JsonNode> addresses = objectMapper.readerFor(new TypeReference<List<JsonNode>>(){}).readValue(ipv4.get("Addresses"));
							for (JsonNode address: addresses) {
								stats.put(CrestronConstant.NETWORK_GROUP + CrestronPropertyList.IP_ADDRESS.getName(), getDefaultValueForNullData(address.get(CrestronPropertyList.IP_ADDRESS.getApiPropertyName())));
								stats.put(CrestronConstant.NETWORK_GROUP + CrestronPropertyList.SUBNET_MASK.getName(), getDefaultValueForNullData(address.get(CrestronPropertyList.SUBNET_MASK.getApiPropertyName())));
							}

							// Get Static DNS information
							List<JsonNode> staticDns = objectMapper.readerFor(new TypeReference<List<JsonNode>>(){}).readValue(ipv4.get("StaticDns"));
							if (staticDns != null && staticDns.size() >= 2) {
								stats.put(CrestronConstant.NETWORK_GROUP + CrestronPropertyList.PRIMARY_STATIC_DNS.getName(), getDefaultValueForNullData(staticDns.get(0)));
								stats.put(CrestronConstant.NETWORK_GROUP + CrestronPropertyList.SECONDARY_STATIC_DNS.getName(), getDefaultValueForNullData(staticDns.get(1)));
							}
						}
					}
					break;

				case IP_ID:
//				case ROOM_ID:
				case IP_ADDRESS_HOSTNAME:
				case TYPE:
				case SERVER_PORT:
				case CONNECTION:
				case STATUS:
					JsonNode entries = apiResponse.get(CrestronConstant.ENTRIES);
					List<JsonNode> currenKey = objectMapper.readerFor(new TypeReference<List<JsonNode>>(){}).readValue(apiResponse.get(CrestronConstant.ENTRIES_CURRENT_KEY_LIST));
					String currentIpId = cacheFilterValue.get(CrestronConstant.CONTROL_SYSTEM_GROUP + CrestronPropertyList.IP_ID.getName());
					if (currentIpId == null && currenKey.isEmpty()) return;

					if (currentIpId == null) {
							currentIpId = currenKey.get(0).asText();
					}

					JsonNode controlSystem = entries.get(currentIpId);
					if (property == CrestronPropertyList.IP_ID) {
						List<String> ipIds = currenKey.stream().map(JsonNode::asText).collect(Collectors.toList());
						if (!ipIds.contains(currentIpId) && !ipIds.isEmpty()) {
							currentIpId = ipIds.get(0);
						}
						String propertyValue1 = currentIpId == null ? CrestronConstant.NONE : currentIpId;
						stats.put(groupName, propertyValue1);
						addAdvancedControlProperties(advancedControllableProperties, stats, createDropdown(groupName, ipIds.toArray(new String[0]), propertyValue1), propertyValue1);
					}  else {
						stats.put(groupName, getDefaultValueForNullData(controlSystem.get(property.getApiPropertyName())));
					}
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
				case INPUT_UUID:
				case PORT_UUID:
					// input
					populateAudioVideoInput(stats, advancedControllableProperties, apiResponse, groupName, property);
					break;
				case OUTPUT_NAME:
				case SINK_CONNECTED:
				case RESOLUTION:
				case OUTPUT_SOURCE_HDCP:
				case DISABLED_BY_HDCP:
				case OUTPUT_ASPECT_RATIO:
//				case ANALOG_AUDIO_VOLUME:
				case OUTPUT_UUID:
				case OUTPUT_PORT_UUID:
					populateAudioVideoOutput(stats, advancedControllableProperties, apiResponse, groupName, property);
				  break;
				case SCHEDULE_DAY_OF_WEEK:
				case SCHEDULE_POLL_INTERVAL:
				case SCHEDULE_TIME_OF_DAY:
					JsonNode autoUpdateSchedule = apiResponse.get(CrestronConstant.AUTO_UPDATE_SCHEDULE);
					if (autoUpdateSchedule != null) {
						stats.put(groupName, getDefaultValueForNullData(autoUpdateSchedule.get(property.getApiPropertyName())));
					}
					break;
				case TIMEZONE:
				case SYNCHRONIZE:
				case CURRENT_TIME:
				case NTPTIMESERVERS:
					populateDateTime(stats, apiResponse, groupName, property);
					break;
				case RECEIVE_UUID:
//				case RECEIVE_MODE:
				case TRANSMIT_UUID:
					Streams stream = objectMapper.treeToValue(apiResponse, Streams.class);
					if (!stream.getStreams().isEmpty()) {
						this.currentStream = objectMapper.valueToTree(stream.getStreams().get(0));
						if (property == CrestronPropertyList.TRANSMIT_UUID) {
							String currentStreamId = cacheFilterValue.get(groupName);
							if (currentStreamId == null && !stream.getStreams().isEmpty()) {
								currentStreamId = stream.getStreams().get(0).getUuid();
							}
							if (!StringUtils.isNullOrEmpty(currentStreamId)) {
								for (int i = 0 ; i < stream.getStreams().size() ; i++) {
									if (stream.getStreams().get(i).getUuid().equals(currentStreamId)) {
										this.currentStream = objectMapper.valueToTree(stream.getStreams().get(i));
										break;
									}
								}
								stats.put(groupName, currentStreamId);
								if (!Objects.equals(currentStreamId, CrestronConstant.NONE)) {
									String[] values = stream.getStreams().stream().map(Stream::getUuid).toArray(String[]::new);
									addAdvancedControlProperties(advancedControllableProperties, stats, createDropdown(groupName, values, currentStreamId), currentStreamId);
								}
							}
						}
					} else {
						this.currentStream = null;
					}
					break;
				case RECEIVE_STREAM_LOCATION:
				case RECEIVE_MULTICAST_ADDRESS:
				case RECEIVE_RECEIVE_STATUS:
				case RECEIVE_RECEIVE_HORIZONTAL_RESOLUTION:
				case RECEIVE_RECEIVE_VERTICAL_RESOLUTION:
				case TRANSMIT_MODE:
				case RECEIVE_MODE:
				case TRANSMIT_STREAM_LOCATION:
				case TRANSMIT_MULTICAST_ADDRESS:
				case TRANSMIT_RECEIVE_STATUS:
				case TRANSMIT_RECEIVE_HORIZONTAL_RESOLUTION:
				case TRANSMIT_RECEIVE_VERTICAL_RESOLUTION:
					if (this.currentStream != null) {
						stats.put(groupName, getDefaultValueForNullData(currentStream.get(property.getApiPropertyName())));
					}
					break;
				case SUB_UNIQUE_ID:
				case AVAILABLE_UNIQUE_ID:
					String uniqueId = cacheFilterValue.get(groupName);
					List<String> uniqueIds = new ArrayList<>();
					Iterator<Entry<String, JsonNode>> fields = null;
					JsonNode streamType = null;
					if (property == CrestronPropertyList.SUB_UNIQUE_ID) {
						JsonNode subscriptionJson = apiResponse.get(CrestronConstant.SUBSCRIPTIONS);
						this.currentStream = subscriptionJson.get(uniqueId);
						streamType = subscriptionJson;
						fields = subscriptionJson.fields();
					} else {
						fields = apiResponse.get(CrestronConstant.STREAMS).fields();
						this.currentStream = apiResponse.get(uniqueId);
						streamType = apiResponse;
					}
					if (fields == null) return;
					while (fields.hasNext()) {
						Map.Entry<String, JsonNode> field = fields.next();
						uniqueIds.add(field.getKey());
						if(uniqueId == null && !uniqueIds.isEmpty()) {
							uniqueId = uniqueIds.get(0);
							this.currentStream = streamType.get(uniqueId);
						}
					}
					if (uniqueIds.contains(uniqueId)) {
						addAdvancedControlProperties(advancedControllableProperties, stats, createDropdown(groupName, uniqueIds.toArray(new String[0]), uniqueId), uniqueId);
					}
					break;
				case SUB_SESSION_NAME:
				case SUB_RTSP_ADDRESS:
				case SUB_MULTICAST_ADDRESS:
				case SUB_RESOLUTION:
				case SUB_AUDIO_FORMAT:
				case SUB_BITRATE:
				case AVAILABLE_SESSION_NAME:
				case AVAILABLE_RTSP_ADDRESS:
				case AVAILABLE_MULTICAST_ADDRESS:
				case AVAILABLE_RESOLUTION:
				case AVAILABLE_AUDIO_FORMAT:
				case AVAILABLE_BITRATE:
					if (this.currentStream != null) {
						stats.put(groupName, getDefaultValueForNullData(this.currentStream.get(property.getApiPropertyName())));
					}
					break;
				case INPUT_ROUTING_UNIQUE_ID:
					List<JsonNode> routesJson = objectMapper.readerFor(new TypeReference<List<JsonNode>>(){}).readValue(apiResponse.get(CrestronConstant.ROUTES));
					String id = cacheFilterValue.get(groupName);
					if (id == null && !routesJson.isEmpty()) {
						id = routesJson.get(0).get(property.getApiPropertyName()).asText();
					}

					for (JsonNode item : routesJson) {
						if (item.get(property.getApiPropertyName()).asText().equals(id)) {
							this.currentRoute = item;
							break;
						} else {
							this.currentRoute = routesJson.get(0);
						}
					}

					if (id != null && !routesJson.isEmpty()) {
						String[] values = routesJson.stream().map(r -> r.get(property.getApiPropertyName()).asText()).toArray(String[]::new);
						addAdvancedControlProperties(advancedControllableProperties, stats, createDropdown(groupName, values, id), id);
					}
					break;
				case AUDIO_SOURCE:
				case VIDEO_SOURCE:
				case AUTOMATIC_STREAM_ROUTING:
					if (this.currentRoute != null) {
						stats.put(groupName, getDefaultValueForNullData(this.currentRoute.get(property.getApiPropertyName())));
					}
					break;
				default:
					break;

			}
		}
	}

	private JsonNode sendGetCommand(String uri) throws FailedLoginException {
		try {
				return this.doGet(uri, JsonNode.class);
		} catch (FailedLoginException e) {
			throw new FailedLoginException("Error when login, please check the credentials");
		} catch (CommandFailureException e) {
			throw new ResourceNotReachableException("Error when send api request to " + uri);
		} catch(Exception e) {
			countMonitoringAndControllingCommand++;
			logger.error("Error when send api request to " + uri + " with error message: " + e.getMessage(), e);
		}
		return null;
	}

	private void populateAudioVideoInput(Map<String,String> stats, List<AdvancedControllableProperty> advancedControllableProperties, JsonNode apiResponse, String groupName, CrestronPropertyList property)
			throws IOException {
		String inputUUID = cacheFilterValue.get(CrestronConstant.INPUT_GROUP + CrestronPropertyList.INPUT_UUID.getName());
		String inputPortUUID = cacheFilterValue.get(CrestronConstant.INPUT_GROUP + CrestronPropertyList.PORT_UUID.getName());
		List<JsonNode> inputJson = objectMapper.readerFor(new TypeReference<List<JsonNode>>(){}).readValue(apiResponse.get("Inputs"));
		List<JsonNode> portInputs = new ArrayList<>();
		JsonNode input1 = !inputJson.isEmpty()? inputJson.get(0) : null;
		JsonNode portInput1 = null;
		if (!inputJson.isEmpty()) {
			for (JsonNode item: inputJson) {
				if (item.get("Uuid").asText().equals(inputUUID)) {
					input1 = item;
					break;
				}
			}

			if (input1 != null) {
				portInputs = objectMapper.readerFor(new TypeReference<List<JsonNode>>(){}).readValue(input1.get("Ports"));
				portInput1 = !portInputs.isEmpty() ? portInputs.get(0) : null;
				for (JsonNode item : portInputs) {
					if (item.get("Uuid").asText().equals(inputPortUUID)) {
						portInput1 = item;
						break;
					}
				}
			}

			if (portInput1 != null && !portInputs.isEmpty()) {
				switch (property) {
					case INPUT_NAME:
						stats.put(groupName, getDefaultValueForNullData(input1.get(property.getApiPropertyName())));
						break;
					case INPUT_SOURCE_HDCP:
					case HDCP_RECEIVER_CAPABILITY:
						JsonNode hdmi = portInput1.get(CrestronConstant.HDMI);
						if (hdmi != null) {
							stats.put(groupName, getDefaultValueForNullData(hdmi.get(property.getApiGroupName())));
						}
						break;
					case AUDIO_FORMAT:
					case AUDIO_CHANNELS:
						JsonNode digital = portInput1.get(CrestronConstant.AUDIO).get(CrestronConstant.DIGITAL);
						if (digital != null) {
							stats.put(groupName, getDefaultValueForNullData(digital.get(property.getApiPropertyName())));
						}
						break;
					case EDID:
						JsonNode Edid = portInput1.get(CrestronConstant.EDID);
						if (Edid != null) {
							stats.put(groupName, getDefaultValueForNullData(Edid.get(property.getApiPropertyName())));
						}
						break;
					case INPUT_UUID:
						String propertyValue = getDefaultValueForNullData(input1.get(property.getApiPropertyName()));
						if (!Objects.equals(propertyValue, CrestronConstant.NONE)) {
							String[] values = inputJson.stream().map(item -> item.get(property.getApiPropertyName()).asText()).toArray(String[]::new);
							addAdvancedControlProperties(advancedControllableProperties, stats, createDropdown(groupName, values, propertyValue), propertyValue);
						}
						break;
					case PORT_UUID:
						String val = getDefaultValueForNullData(portInput1.get(property.getApiPropertyName()));
						if (!Objects.equals(val, CrestronConstant.NONE)) {
							String[] values = portInputs.stream().map(item -> item.get(property.getApiPropertyName()).asText()).toArray(String[]::new);
							addAdvancedControlProperties(advancedControllableProperties, stats, createDropdown(groupName, values, val), val);
						}
					 	break;
					case SYNC_DETECTED:
						String value = getDefaultValueForNullData(portInput1.get(property.getApiPropertyName()));
						if (!Objects.equals(value, CrestronConstant.NONE)) {
							stats.put(groupName, value.equals(CrestronConstant.TRUE) ? CrestronConstant.YES : CrestronConstant.NO);
						} else {
							stats.put(groupName, value);
						}
						break;
					case VERTICAL_RESOLUTION:
					case HORIZONTAL_RESOLUTION:
					case INTERLACED:
					case INPUT_ASPECT_RATIO:
						stats.put(groupName, getDefaultValueForNullData(portInput1.get(property.getApiPropertyName())));
						break;
				}
			}
		}
	}

	private void populateAudioVideoOutput(Map<String,String> stats, List<AdvancedControllableProperty> advancedControllableProperties, JsonNode apiResponse, String groupName, CrestronPropertyList property)
			throws IOException {
		String outputUUID =  cacheFilterValue.get(CrestronConstant.OUTPUT_GROUP + CrestronPropertyList.OUTPUT_UUID.getName());
		String outputPortUUID  =  cacheFilterValue.get(CrestronConstant.OUTPUT_GROUP + CrestronPropertyList.PORT_UUID.getName());
		List<JsonNode> outputJson = objectMapper.readerFor(new TypeReference<List<JsonNode>>(){}).readValue(apiResponse.get("Outputs"));
		List<JsonNode> portOutputs = new ArrayList<>();
		JsonNode output1 = !outputJson.isEmpty()? outputJson.get(0) : null;
		JsonNode portOutput1 = null;
		if (!outputJson.isEmpty()) {
			for (JsonNode item: outputJson) {
				if (item.get("Uuid").asText().equals(outputUUID)) {
					output1 = item;
					break;
				}
			}

			if (output1 != null) {
				portOutputs = objectMapper.readerFor(new TypeReference<List<JsonNode>>() {}).readValue(output1.get("Ports"));
				portOutput1 = !portOutputs.isEmpty() ? portOutputs.get(0) : null;
				for (JsonNode item : portOutputs) {
					if (item.get("Uuid").asText().equals(outputPortUUID)) {
						portOutput1 = item;
						break;
					}
				}
			}

			if (portOutput1 != null && !portOutputs.isEmpty()) {
				switch (property) {
					case OUTPUT_NAME:
						stats.put(groupName, getDefaultValueForNullData(output1.get(property.getApiPropertyName())));
						break;
					case SINK_CONNECTED:
						String value = getDefaultValueForNullData(portOutput1.get(property.getApiPropertyName()));
						if (!Objects.equals(value, CrestronConstant.NONE)) {
							stats.put(groupName, value.equals(CrestronConstant.TRUE) ? CrestronConstant.YES : CrestronConstant.NO);
						} else {
							stats.put(groupName, value);
						}
						break;
					case DISABLED_BY_HDCP:
					case OUTPUT_SOURCE_HDCP:
						JsonNode hdmi = portOutput1.get(CrestronConstant.HDMI);
						if (hdmi != null) {
							if (property == CrestronPropertyList.DISABLED_BY_HDCP) {
								String hdmiValue = getDefaultValueForNullData(hdmi.get(property.getApiPropertyName()));
								if (!Objects.equals(hdmiValue, CrestronConstant.NONE)) {
									stats.put(groupName, hdmiValue.equals(CrestronConstant.TRUE) ? CrestronConstant.YES : CrestronConstant.NO);
								} else {
									stats.put(groupName, hdmiValue);
								}
							} else {
								stats.put(groupName, getDefaultValueForNullData(hdmi.get(property.getApiPropertyName())));
							}
						}
						break;
					case OUTPUT_UUID:
						String propertyValue = getDefaultValueForNullData(output1.get(property.getApiPropertyName()));
						if (!Objects.equals(propertyValue, CrestronConstant.NONE)) {
							String[] values = outputJson.stream().map(item -> item.get(property.getApiPropertyName()).asText()).toArray(String[]::new);
							addAdvancedControlProperties(advancedControllableProperties, stats, createDropdown(groupName, values, propertyValue), propertyValue);
						}
						break;
					case OUTPUT_PORT_UUID:
						String val = getDefaultValueForNullData(portOutput1.get(property.getApiPropertyName()));
						if (!Objects.equals(val, CrestronConstant.NONE)) {
							String[] values = portOutputs.stream().map(item -> item.get(property.getApiPropertyName()).asText()).toArray(String[]::new);
							addAdvancedControlProperties(advancedControllableProperties, stats, createDropdown(groupName, values, val), val);
						}
						break;
					case RESOLUTION:
					case OUTPUT_ASPECT_RATIO:
						stats.put(groupName, getDefaultValueForNullData(portOutput1.get(property.getApiPropertyName())));
						break;
					case ANALOG_AUDIO_VOLUME:
						break;
				}
			}
		}
	}

	private void populateDateTime(Map<String, String> stats,JsonNode apiResponse, String groupName, CrestronPropertyList property)
			throws IOException {
		JsonNode ntpJson = apiResponse.get(CrestronConstant.NTP);
		if (ntpJson != null) {
			switch (property) {
				case NTPTIMESERVERS:
					List<JsonNode> serverCurrentKeyListJson = objectMapper.readerFor(new TypeReference<List<JsonNode>>(){}).readValue(ntpJson.get(CrestronConstant.SERVERS_CURRENT_KEY_LIST));
					for (JsonNode serverCurrentKey : serverCurrentKeyListJson) {
						JsonNode server = ntpJson.get(CrestronConstant.SERVERS).get(serverCurrentKey.asText());
						String propertyName = groupName + serverCurrentKey.asText();
						String propertyValue = getDefaultValueForNullData(server.get(property.getApiPropertyName()));
						stats.put(propertyName, propertyValue);
					}
					break;
				case CURRENT_TIME:
					String propertyValue = getDefaultValueForNullData(apiResponse.get(property.getApiPropertyName()));
					stats.put(groupName, propertyValue);
					break;
				case TIMEZONE:
					String value = getDefaultValueForNullData(apiResponse.get(property.getApiPropertyName()));
					if (!Objects.equals(value, CrestronConstant.NONE)) {
						TimeZone timeZoneString = TimeZone.getEnumByValue(value);
						if (timeZoneString != null) {
							stats.put(groupName, timeZoneString.getName());
						}
					}
					break;
			}
		}
	}

	/**
	 * Update monitoring data base on filter value
	 *
	 */
  private void updateFilterCache() throws Exception {
		Map<String, String> stats = this.localExtendedStatistics.getStatistics();
		List<AdvancedControllableProperty> advancedControllableProperties = this.localExtendedStatistics.getControllableProperties();
		populateMonitoringAndControllableProperties(stats, advancedControllableProperties);
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
	private boolean isLogin(){
		try {
			Map<String,String> headers = new HashMap<>();
			String loginUrl = CrestronConstant.LOGIN_URL + CrestronConstant.QUESTION_MARK + String.format(CrestronConstant.AUTHENTICATION_PARAM, this.getLogin(), this.getPassword());
			if (StringUtils.isNullOrEmpty(this.getLogin()) || StringUtils.isNullOrEmpty(this.getPassword())) {
				throw new ResourceNotReachableException("Failed to authentication please check the credentials");
			}

			if (authenticationInfo.getAuthorizeStatus().equals(CrestronConstant.UNAUTHORIZED)) {
				// Get Track id
				getTrackId();

        // authenticate
				headers.put("Content-Type", "application/json");
				headers.put(CrestronConstant.COOKIE, authenticationInfo.getTrackId());
				headers.put(CrestronConstant.ORIGIN, authenticationInfo.getOrigin());
				headers.put(CrestronConstant.REFERER, authenticationInfo.getReferer());

				HttpResponse httpResponse = sendPostRequest(buildDeviceFullPath(loginUrl), headers);
				if (httpResponse == null || httpResponse.getStatusLine().getStatusCode() != 200) {
					throw new FailedLoginException("Failed to authentication please check the credentials");
				}

				Header cookie = httpResponse.getFirstHeader(CrestronConstant.SET_COOKIE);
				Header token = httpResponse.getFirstHeader(CrestronConstant.CREST_XSRF_TOKEN);

				if (cookie != null && token != null) {
					authenticationInfo.setAuthenticationCookies(cookie.getValue());
					authenticationInfo.setToken(token.getValue());
				} else {
					throw new FailedLoginException("Failed to authentication please check the credentials");
				}
			}
		} catch (Exception e) {
			throw new ResourceNotReachableException("Failed to authentication please check the credentials");
		}
		return true;
	}

	/**
	 * Get TRACKID cookie for authentication
	 */
	private void getTrackId() {
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json");
		HttpResponse response = sendGetRequest(buildDeviceFullPath(CrestronConstant.LOGIN_URL), headers);

		Header[] cookies = response.getHeaders(CrestronConstant.SET_COOKIE);
		if (cookies != null) {
			for (Header cookie : cookies) {
				if (cookie.getName().equals(CrestronConstant.TRACKID) && cookie.getValue() != null) {
					String trackId = CrestronConstant.TRACKID.concat(CrestronConstant.EQUAL).concat(cookie.getValue());
					String origin = CrestronConstant.HTTPS.concat(this.getHost());
					String referer = origin.concat(CrestronConstant.LOGIN_URL);
					authenticationInfo = new AuthenticationInfo(trackId, origin, referer);
					break;
				}
			}
		} else {
			logger.error("Unable to get track id from response with null cookies");
		}
	}

	/**
	 * Custom get request
	 */
	private HttpResponse sendGetRequest(String url, Map<String, String> headers) {
		HttpResponse response = null;
		try {
			HttpClient httpClient = this.obtainHttpClient(true);
			HttpGet httpGet = new HttpGet(url);
			addCustomHeaders(httpGet, headers);
			try {
				response = httpClient.execute(httpGet);
			} finally {
				if (response instanceof CloseableHttpResponse) {
					((CloseableHttpResponse) response).close();
				}
			}
		} catch (Exception e) {
			throw new ResourceNotReachableException(String.format("Error when sending custom GET request to %s", url), e);
		}
		return response;
	}

	/**
	 * Custom post request
	 */
	private HttpResponse sendPostRequest(String url, Map<String, String> headers) {
		HttpResponse response = null;
		try {
			HttpClient httpClient = this.obtainHttpClient(true);
			HttpPost httpPost = new HttpPost(url);
			addCustomHeaders(httpPost, headers);
			try {
				response = httpClient.execute(httpPost);
			} finally {
				if (response instanceof CloseableHttpResponse) {
					((CloseableHttpResponse) response).close();
				}
			}
		} catch (Exception e) {
			throw new ResourceNotReachableException(String.format("Error when sending custom POST request to %s", url), e);
		}
		return response;
	}

	/**
	 * Put header into request
	 */
	private void addCustomHeaders(HttpRequestBase request, Map<String, String> headers) {
		if (headers != null) {
			headers.forEach((key, value) -> request.setHeader((String) key, (String) value));
		}
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

//	/**
//	 * Create a button.
//	 *
//	 * @param name name of the button
//	 * @param label label of the button
//	 * @param labelPressed label of the button after pressing it
//	 * @param gracePeriod grace period of button
//	 * @return This returns the instance of {@link AdvancedControllableProperty} type Button.
//	 */
//	private AdvancedControllableProperty createButton(String name, String label, String labelPressed, long gracePeriod) {
//		AdvancedControllableProperty.Button button = new AdvancedControllableProperty.Button();
//		button.setLabel(label);
//		button.setLabelPressed(labelPressed);
//		button.setGracePeriod(gracePeriod);
//		return new AdvancedControllableProperty(name, new Date(), button, CrestronConstant.EMPTY);
//	}
//
//	/**
//	 * Create switch is control property for metric
//	 *
//	 * @param name the name of property
//	 * @param status initial status (0|1)
//	 * @return AdvancedControllableProperty switch instance
//	 */
//	private AdvancedControllableProperty createSwitch(String name, int status, String labelOff, String labelOn) {
//		AdvancedControllableProperty.Switch toggle = new AdvancedControllableProperty.Switch();
//		toggle.setLabelOff(labelOff);
//		toggle.setLabelOn(labelOn);
//
//		AdvancedControllableProperty advancedControllableProperty = new AdvancedControllableProperty();
//		advancedControllableProperty.setName(name);
//		advancedControllableProperty.setValue(status);
//		advancedControllableProperty.setType(toggle);
//		advancedControllableProperty.setTimestamp(new Date());
//
//		return advancedControllableProperty;
//	}

	/***
	 * Create dropdown advanced controllable property
	 *
	 * @param name the name of the control
	 * @param initialValue initial value of the control
	 * @return AdvancedControllableProperty dropdown instance
	 */
	private AdvancedControllableProperty createDropdown(String name, String[] values, String initialValue) {
		AdvancedControllableProperty.DropDown dropDown = new AdvancedControllableProperty.DropDown();
		dropDown.setOptions(values);
		dropDown.setLabels(values);

		return new AdvancedControllableProperty(name, new Date(), dropDown, initialValue);
	}

//	/***
//	 * Create AdvancedControllableProperty slider instance
//	 *
//	 * @param stats extended statistics
//	 * @param name name of the control
//	 * @param initialValue initial value of the control
//	 * @return AdvancedControllableProperty slider instance
//	 */
//	private AdvancedControllableProperty createSlider(Map<String, String> stats, String name, String labelStart, String labelEnd, Float rangeStart, Float rangeEnd, Float initialValue) {
//		stats.put(name, initialValue.toString());
//		AdvancedControllableProperty.Slider slider = new AdvancedControllableProperty.Slider();
//		slider.setLabelStart(labelStart);
//		slider.setLabelEnd(labelEnd);
//		slider.setRangeStart(rangeStart);
//		slider.setRangeEnd(rangeEnd);
//
//		return new AdvancedControllableProperty(name, new Date(), slider, initialValue);
//	}
//
//	/**
//	 * Create text is control property for metric
//	 *
//	 * @param name the name of the property
//	 * @param stringValue character string
//	 * @return AdvancedControllableProperty Text instance
//	 */
//	private AdvancedControllableProperty createText(String name, String stringValue) {
//		AdvancedControllableProperty.Text text = new AdvancedControllableProperty.Text();
//		return new AdvancedControllableProperty(name, new Date(), text, stringValue);
//	}

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
