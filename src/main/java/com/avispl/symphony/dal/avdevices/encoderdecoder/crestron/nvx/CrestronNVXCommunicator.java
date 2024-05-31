/*
 *  Copyright (c) 2024 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.encoderdecoder.crestron.nvx;


import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.time.OffsetDateTime;
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

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.security.auth.login.FailedLoginException;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import com.avispl.symphony.api.dal.control.Controller;
import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty;
import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.dto.monitor.Statistics;
import com.avispl.symphony.api.dal.error.CommandFailureException;
import com.avispl.symphony.api.dal.error.ResourceNotReachableException;
import com.avispl.symphony.api.dal.monitor.Monitorable;
import com.avispl.symphony.dal.avdevices.encoderdecoder.crestron.nvx.common.CrestronCommand;
import com.avispl.symphony.dal.avdevices.encoderdecoder.crestron.nvx.common.CrestronConstant;
import com.avispl.symphony.dal.avdevices.encoderdecoder.crestron.nvx.common.CrestronPropertyList;
import com.avispl.symphony.dal.avdevices.encoderdecoder.crestron.nvx.common.DeviceModel;
import com.avispl.symphony.dal.avdevices.encoderdecoder.crestron.nvx.common.PingMode;
import com.avispl.symphony.dal.avdevices.encoderdecoder.crestron.nvx.common.TimeZone;
import com.avispl.symphony.dal.avdevices.encoderdecoder.crestron.nvx.common.routing.Routing;
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

	private String authenticationCookie = CrestronConstant.NONE;

	private String deviceMode = CrestronConstant.NONE;

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

	private JsonNode currentStream = null;

//	private JsonNode currentRoute = null;

	private JsonNode currentControlSystem = null;

	private DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd/YYYY");
	private DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");

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
			Map<String, String> statistics = new HashMap<>();
			ExtendedStatistics extendedStatistics = new ExtendedStatistics();
			List<AdvancedControllableProperty> advancedControllableProperties = new ArrayList<>();

			retrieveMonitoringAndControllableProperties();
			if (!isEmergencyDelivery) {
				if (countMonitoringAndControllingCommand == CrestronCommand.values().length) {
					throw new ResourceNotReachableException("There was an error while retrieving monitoring data for all properties.");
				}
				populateMonitoringAndControllableProperties(statistics, advancedControllableProperties, false, CrestronConstant.EMPTY);
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
			String groupName = property;
			if (property.contains(CrestronConstant.HASH)) {
				propertyName = propertyList[1];
				groupName = propertyList[0];
			}

			if (propertyName.equals(CrestronConstant.IPID) || propertyName.contains(CrestronConstant.UUID) || propertyName.equals(CrestronConstant.UNIQUE_ID) || propertyName.equals(CrestronConstant.ADDRESS_SCHEMA)) {
				cacheFilterValue.put(property, value);
				updateFilterCache(groupName.concat(CrestronConstant.HASH));
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
		headers.set("Content-Type", "application/text");
		headers.set("Content-Type", "application/json");
		if (StringUtils.isNotNullOrEmpty(this.authenticationCookie)) {
			headers.set(CrestronConstant.COOKIE, this.authenticationCookie);
		}
		return super.putExtraRequestHeaders(httpMethod, uri, headers);
	}

//	public void sendControl() throws Exception {
//		checkValidCookieSession();
//		String deviceSpecificCommand = "{ \"Device\": { \"DeviceSpecific\": %s } }";
//		HashMap<String, Object> param = new HashMap<>();
//		param.put("AudioSource", "Input3");
//		String body = String.format(deviceSpecificCommand, objectMapper.writeValueAsString(param));
//		String body2 = "{ \"Device\": { \"SystemClock\": {\"CurrentTimeWithOffset\": \"2024-05-30T14:36:18+07:00\" } } }";
//		try {
//			JsonNode tam = objectMapper.readTree(body2);
//			String response = this.doPost(CrestronUri.SYSTEM_CLOCK_API, tam, String.class);
//			System.out.println(response);
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//		}
//	}

	private void retrieveMonitoringAndControllableProperties() throws Exception {
		if (!checkValidCookieSession()) {
			throw new FailedLoginException("Invalid cookie session. Please check credentials");
		}
		for (CrestronCommand command: CrestronCommand.values()) {
			String groupName = command.getGroupCommand();
			String apiGroup = command.getCommand();
//			String deviceMode = command.getDeviceMode();
			if (groupName.equals(CrestronCommand.DEVICE_SPECIFIC.getGroupCommand()) || groupName.equals(CrestronCommand.INPUT_ROUTING.getGroupCommand())) continue;
//			if  (!Objects.equals(deviceMode, CrestronConstant.EMPTY) && (!deviceMode.equals(this.deviceMode) && !this.deviceMode.equals(CrestronConstant.NONE)) || this.deviceMode.equals(CrestronConstant.NONE)) continue;
			JsonNode response = sendGetCommand(apiGroup);
			cacheKeyAndValue.put(groupName, extractApiResponseByGroup(apiGroup, response));
		}
	}

	private void detectDeviceMode() throws FailedLoginException {
		JsonNode response = sendGetCommand(CrestronCommand.DEVICE_SPECIFIC.getCommand());
		cacheKeyAndValue.put(CrestronCommand.DEVICE_SPECIFIC.getGroupCommand(), extractApiResponseByGroup(CrestronCommand.DEVICE_SPECIFIC.getCommand(), response));
		cacheKeyAndValue.put(CrestronCommand.INPUT_ROUTING.getGroupCommand(), cacheKeyAndValue.get(CrestronCommand.DEVICE_SPECIFIC.getGroupCommand()));
		JsonNode deviceSpecific = cacheKeyAndValue.get(CrestronCommand.DEVICE_SPECIFIC.getGroupCommand());
		if (deviceSpecific != null) {
			this.deviceMode = getDefaultValueForNullData(deviceSpecific.get("DeviceMode"));
		}
	}

	private void populateMonitoringAndControllableProperties(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties, boolean isForcePopulate, String forcePopulateGroup) throws Exception {
		for (CrestronPropertyList property: CrestronPropertyList.values()) {
			String groupName = property.getGroup().concat(property.getName());
			String apiGroupName = property.getApiGroupName();
			JsonNode apiResponse = cacheKeyAndValue.get(apiGroupName);
			if (apiResponse == null || apiResponse.asText().contains(CrestronConstant.UNSUPPORT_RESTAPI) || (isForcePopulate && !forcePopulateGroup.equals(property.getGroup())) || (!this.deviceMode.equals(CrestronConstant.NONE)&&!this.deviceMode.equals(property.getDeviceMode())&&!property.getDeviceMode().equals(CrestronConstant.EMPTY))) continue;
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
				case FRONT_PANEL_LOCKOUT:
				case HOST_NAME:
				case DOMAIN_NAME:
				case FIRMWARE_UPGRADED_STATUS:
				case ENCRYPT_CONNECTION:
				case TTL:
					JsonNode propertyValue = apiResponse.get(property.getApiPropertyName());
					stats.put(groupName, getDefaultValueForNullData(propertyValue));
					break;
				case ADDRESS_SCHEMA:
//				case LINK_ACTIVE:
//				case MAC_ADDRESS:
//				case IP_ADDRESS:
//				case SUBNET_MASK:
//				case DEFAULT_GATEWAY:
//				case PRIMARY_STATIC_DNS:
//				case SECONDARY_STATIC_DNS:
					if (!apiResponse.has(CrestronConstant.ADAPTERS)) break;
					List<JsonNode> adapters = objectMapper.readerFor(new TypeReference<List<JsonNode>>(){}).readValue(apiResponse.get(CrestronConstant.ADAPTERS));

					String schemaValue = cacheFilterValue.get(groupName);
					List<String> schemas = new ArrayList<>();
					JsonNode currentAdapter = null;
					for (JsonNode adapter: adapters) {
						String schema = adapter.get(CrestronConstant.ADDRESS_SCHEMA).asText();
						schemas.add(schema);
						if (Objects.equals(schemaValue, schema)) {
							currentAdapter = adapter;
						}
					}

					if (currentAdapter == null && !adapters.isEmpty()) {
						currentAdapter = adapters.get(0);
					}

					if (currentAdapter == null) break;
					JsonNode currentSchema = currentAdapter.get(currentAdapter.get(CrestronConstant.ADDRESS_SCHEMA).asText());
					stats.put(CrestronConstant.NETWORK_GROUP + CrestronPropertyList.LINK_ACTIVE.getName(), getDefaultValueForNullData(currentAdapter.get(CrestronPropertyList.LINK_ACTIVE.getApiPropertyName())));
					stats.put(CrestronConstant.NETWORK_GROUP + CrestronPropertyList.MAC_ADDRESS.getName(), getDefaultValueForNullData(currentAdapter.get(CrestronPropertyList.MAC_ADDRESS.getApiPropertyName())));

					if (currentSchema == null) break;
					stats.put(CrestronConstant.NETWORK_GROUP + CrestronPropertyList.DHCP_ENABLED.getName(), getDefaultValueForNullData(currentSchema.get(CrestronPropertyList.DHCP_ENABLED.getApiPropertyName())));
					stats.put(CrestronConstant.NETWORK_GROUP + CrestronPropertyList.DEFAULT_GATEWAY.getName(), getDefaultValueForNullData(currentSchema.get(CrestronPropertyList.DEFAULT_GATEWAY.getApiPropertyName())));

					// Get address information
					if(currentSchema.has("Addresses")) {
						List<JsonNode> addresses = objectMapper.readerFor(new TypeReference<List<JsonNode>>(){}).readValue(currentSchema.get("Addresses"));
						for (JsonNode address: addresses) {
							stats.put(CrestronConstant.NETWORK_GROUP + CrestronPropertyList.IP_ADDRESS.getName(), getDefaultValueForNullData(address.get(CrestronPropertyList.IP_ADDRESS.getApiPropertyName())));
							stats.put(CrestronConstant.NETWORK_GROUP + CrestronPropertyList.SUBNET_MASK.getName(), getDefaultValueForNullData(address.get(CrestronPropertyList.SUBNET_MASK.getApiPropertyName())));
						}
					}

					// Get Static DNS information
					if (currentSchema.has("DnsServers")) {
						List<JsonNode> staticDns = objectMapper.readerFor(new TypeReference<List<JsonNode>>(){}).readValue(currentSchema.get("DnsServers"));
						if (staticDns != null && staticDns.size() >= 2) {
							stats.put(CrestronConstant.NETWORK_GROUP + CrestronPropertyList.PRIMARY_STATIC_DNS.getName(), getDefaultValueForNullData(staticDns.get(0)));
							stats.put(CrestronConstant.NETWORK_GROUP + CrestronPropertyList.SECONDARY_STATIC_DNS.getName(), getDefaultValueForNullData(staticDns.get(1)));
						}
					}

					String value = currentAdapter.get(CrestronConstant.ADDRESS_SCHEMA).asText();
					stats.put(groupName, value);
//					addAdvancedControlProperties(advancedControllableProperties, stats, createDropdown(groupName, schemas.toArray(new String[0]), value), value);
					break;
				case IP_ID:
					JsonNode maxEntries = apiResponse.get("MaxEntries");
					if (maxEntries == null || maxEntries.asInt() <= 0) break;

					JsonNode entries = apiResponse.get(CrestronConstant.ENTRIES);
					String currentIpId = cacheFilterValue.get(CrestronConstant.CONTROL_SYSTEM_GROUP + CrestronPropertyList.IP_ID.getName());
					List<String> ipIds = new ArrayList<>();
					this.currentControlSystem = null;

					if (entries != null && entries.isArray()) {
						for (JsonNode item : entries) {
							String ipId = item.get("IpId").asText();
							ipIds.add(ipId);
							if (Objects.equals(ipId, currentIpId)) {
								this.currentControlSystem = item;
								break;
							}
						}

						if (this.currentControlSystem == null) {
							this.currentControlSystem = entries.get(0);
							currentIpId = this.currentControlSystem.get("IpId").asText();
						}
						addAdvancedControlProperties(advancedControllableProperties, stats, createDropdown(groupName, ipIds.toArray(new String[0]), currentIpId), currentIpId);
					}
				case IGMP_SUPPORT:
					String currentVersion = getDefaultValueForNullData(apiResponse.get(property.getApiPropertyName()));
					if (!CrestronConstant.NONE.equals(currentVersion)) {
						String[] supportVersion = { "v2", "v3" };
						addAdvancedControlProperties(advancedControllableProperties, stats, createDropdown(groupName, supportVersion, currentVersion), currentVersion);
					}
					break;
				case CLOUD_CONFIGURATION_SERVICE_CONNECTION:
					String isEnabled = getDefaultValueForNullData(apiResponse.get(property.getApiPropertyName()));
					if (!CrestronConstant.NONE.equals(isEnabled)) {
						int status = isEnabled.equals(CrestronConstant.TRUE) ? 1 : 0;
						addAdvancedControlProperties(advancedControllableProperties, stats, createSwitch(groupName, status, CrestronConstant.OFF, CrestronConstant.ON), String.valueOf(status));
					}
					break;
				case ROOM_ID:
				case IP_ADDRESS_HOSTNAME:
				case TYPE:
				case SERVER_PORT:
				case CONNECTION:
				case STATUS:
					if (this.currentControlSystem == null) break;
					stats.put(groupName, getDefaultValueForNullData(this.currentControlSystem.get(property.getApiPropertyName())));
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
					populateAudioVideoInput(stats, advancedControllableProperties, apiResponse, groupName, property);
					break;
				case OUTPUT_NAME:
				case SINK_CONNECTED:
				case RESOLUTION:
				case OUTPUT_SOURCE_HDCP:
				case DISABLED_BY_HDCP:
				case OUTPUT_ASPECT_RATIO:
				case ANALOG_AUDIO_VOLUME:
				case OUTPUT_NO:
					populateAudioVideoOutput(stats, advancedControllableProperties, apiResponse, groupName, property);
				  break;
				case AUTO_UPDATE:
					String isAutoUpdate = getDefaultValueForNullData(apiResponse.get(property.getApiPropertyName()));
					if (!CrestronConstant.NONE.equals(isAutoUpdate)) {
						int status = isAutoUpdate.equals(CrestronConstant.TRUE) ? 1 : 0;
						addAdvancedControlProperties(advancedControllableProperties, stats, createSwitch(groupName, status, CrestronConstant.OFF, CrestronConstant.ON), String.valueOf(status));
					}
				case SCHEDULE_DAY_OF_WEEK:
				case SCHEDULE_POLL_INTERVAL:
				case SCHEDULE_TIME_OF_DAY:
					JsonNode autoUpdateSchedule = apiResponse.get(CrestronConstant.AUTO_UPDATE_SCHEDULE);
					if (autoUpdateSchedule != null) {
						stats.put(groupName, getDefaultValueForNullData(autoUpdateSchedule.get(property.getApiPropertyName())));
					}
					break;
				case TIMEZONE:
//				case TIMESYNCHRONIZE:
//				case SYNCHRONIZE:
				case DATE:
				case TIME:
				case NTPTIMESERVERS:
					populateDateTime(stats, advancedControllableProperties,  apiResponse, groupName, property);
					break;
				case DISCOVERY_AGENT:
					String discoveryAgent = getDefaultValueForNullData(apiResponse.get(property.getApiPropertyName()));
					if (!CrestronConstant.NONE.equals(discoveryAgent)) {
						int status = discoveryAgent.equals(CrestronConstant.TRUE) ? 1 : 0;
						addAdvancedControlProperties(advancedControllableProperties, stats, createSwitch(groupName, status, CrestronConstant.OFF, CrestronConstant.ON), String.valueOf(status));
					}
					break;
				case RECEIVE_UUID:
				case TRANSMIT_UUID:
					Streams stream = objectMapper.treeToValue(apiResponse, Streams.class);
					if (!stream.getStreams().isEmpty()) {
						this.currentStream = objectMapper.valueToTree(stream.getStreams().get(0));
//						if (property == CrestronPropertyList.TRANSMIT_UUID) {
//							String currentStreamId = cacheFilterValue.get(groupName);
//							if (currentStreamId == null && !stream.getStreams().isEmpty()) {
//								currentStreamId = stream.getStreams().get(0).getUuid();
//							}
//							if (!StringUtils.isNullOrEmpty(currentStreamId)) {
//								for (int i = 0 ; i < stream.getStreams().size() ; i++) {
//									if (stream.getStreams().get(i).getUuid().equals(currentStreamId)) {
//										this.currentStream = objectMapper.valueToTree(stream.getStreams().get(i));
//										break;
//									}
//								}
//								stats.put(groupName, currentStreamId);
//								if (!Objects.equals(currentStreamId, CrestronConstant.NONE)) {
//									String[] values = stream.getStreams().stream().map(Stream::getUuid).toArray(String[]::new);
//									addAdvancedControlProperties(advancedControllableProperties, stats, createDropdown(groupName, values, currentStreamId), currentStreamId);
//								}
//							}
//						}
					} else {
						this.currentStream = null;
					}
					break;
				case TRANSMIT_MODE:
				case RECEIVE_MODE:
					String model = stats.get(CrestronPropertyList.MODEL.getName());
					if (!CrestronConstant.NONE.equals(model)) {
						DeviceModel deviceModel = DeviceModel.getDeviceModelByName(model);
						if (deviceModel == DeviceModel.DM_NVX_350 || deviceModel == DeviceModel.DM_NVX_352) {
							if (!CrestronConstant.NONE.equals(this.deviceMode)) {
								String[] modes = { "Transmitter", "Receiver" };
								addAdvancedControlProperties(advancedControllableProperties, stats, createDropdown(groupName, modes, this.deviceMode), this.deviceMode);
							}
						} else {
							stats.put(groupName, this.deviceMode);
						}
					}
					break;
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
					if (this.currentStream == null) break;
					stats.put(groupName, getDefaultValueForNullData(currentStream.get(property.getApiPropertyName())));
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
						this.currentStream = apiResponse.get(CrestronConstant.STREAMS).get(fields.next().getKey());
						streamType = apiResponse.get(CrestronConstant.STREAMS);
					}

					if (fields == null) break;
					while (fields.hasNext()) {
						Map.Entry<String, JsonNode> field = fields.next();
						uniqueIds.add(field.getKey());
						if(uniqueId == null) {
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
					if (this.currentStream != null) {
						stats.put(groupName, getDefaultValueForNullData(this.currentStream.get(property.getApiPropertyName())));
					}
					break;
				case AUDIO_SOURCE:
				case VIDEO_SOURCE:
				case ACTIVE_AUDIO_SOURCE:
				case ACTIVE_VIDEO_SOURCE:
				case ANALOG_AUDIO_MODE:
				case AUTOMATIC_INPUT_ROUTING:
					String routeValue = getDefaultValueForNullData(apiResponse.get(property.getApiPropertyName()));
					Routing route = Routing.getEnumByName(property.getName());
					if (property == CrestronPropertyList.AUTOMATIC_INPUT_ROUTING && !CrestronConstant.NONE.equals(routeValue)) {
						int status = routeValue.equals(CrestronConstant.TRUE) ? 1 : 0;
						addAdvancedControlProperties(advancedControllableProperties, stats, createSwitch(groupName, status, CrestronConstant.OFF, CrestronConstant.ON), String.valueOf(status));
					}

					if (route == null) break;
					if (property.isControl() && (!CrestronConstant.NONE.equals(routeValue) ||  property == CrestronPropertyList.VIDEO_SOURCE)) {
						addAdvancedControlProperties(advancedControllableProperties, stats, createDropdown(groupName, Routing.getRouteValue(route).toArray(new String[0]), Routing.getSpecificRouteNameByValue(route, routeValue)), Routing.getSpecificRouteNameByValue(route, routeValue));
					} else {
						stats.put(groupName, Routing.getSpecificRouteNameByValue(route, routeValue));
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
		} catch(Exception e) {
			countMonitoringAndControllingCommand++;
			logger.error("Error when send api request to " + uri + " with error message: " + e.getMessage(), e);
		}
		return null;
	}

	private void populateAudioVideoInput(Map<String,String> stats, List<AdvancedControllableProperty> advancedControllableProperties, JsonNode apiResponse, String groupName, CrestronPropertyList property)
			throws IOException {
		String number = cacheFilterValue.get(CrestronConstant.INPUT_GROUP + CrestronPropertyList.INPUT_NO.getName());
		if (!apiResponse.has("Inputs")) return;
		List<JsonNode> inputJson = objectMapper.readerFor(new TypeReference<List<JsonNode>>(){}).readValue(apiResponse.get("Inputs"));
		JsonNode input1 = !inputJson.isEmpty()? inputJson.get(0) : null;
		JsonNode portInput1 = null;
		List<JsonNode> portInputs = null;

		if (!inputJson.isEmpty()) {
			if (number == null) number = "1";

			int index = Integer.parseInt(number) - 1;
			if (index <= inputJson.size()) {
				input1 = inputJson.get(index);
			}

			if (input1 != null && input1.has("Ports")) {
				portInputs = objectMapper.readerFor(new TypeReference<List<JsonNode>>() {}).readValue(input1.get("Ports"));
				portInput1 = !portInputs.isEmpty() ? portInputs.get(0) : null;
			}

			if (portInput1 != null) {
				switch (property) {
					case INPUT_NAME:
					case INPUT_SOURCE_HDCP:
					case HDCP_RECEIVER_CAPABILITY:
						JsonNode hdmi = portInput1.get(CrestronConstant.HDMI);
						String hdmiValue = getDefaultValueForNullData(hdmi.get(property.getApiGroupName()));
						stats.put(groupName, hdmiValue);
						break;
					case AUDIO_FORMAT:
					case AUDIO_CHANNELS:
						JsonNode digital = portInput1.get(CrestronConstant.AUDIO).get(CrestronConstant.DIGITAL);
						stats.put(groupName, getDefaultValueForNullData(digital.get(property.getApiPropertyName())));
						break;
					case EDID:
						JsonNode Edid = portInput1.get(CrestronConstant.EDID);
						if (Edid != null) {
							stats.put(groupName, getDefaultValueForNullData(Edid.get(property.getApiPropertyName())));
						}
						break;
					case INPUT_NO:
						String[] values = Arrays.stream(new int[inputJson.size()]).mapToObj( i -> String.valueOf(i + 1)).toArray(String[]::new);
						addAdvancedControlProperties(advancedControllableProperties, stats, createDropdown(groupName, values, number), number);
						break;
					case VERTICAL_RESOLUTION:
					case HORIZONTAL_RESOLUTION:
					case INTERLACED:
					case INPUT_ASPECT_RATIO:
					case SYNC_DETECTED:
						String value = getDefaultValueForNullData(portInput1.get(property.getApiPropertyName()));
						if (property == CrestronPropertyList.INPUT_ASPECT_RATIO && !CrestronConstant.NONE.equals(value)) {
							stats.put(groupName, value.equals(CrestronConstant.TRUE) ? CrestronConstant.YES : CrestronConstant.NO);
						} else {
							stats.put(groupName, value);
						}
						break;
				}
			}
		}
	}

	private void populateAudioVideoOutput(Map<String,String> stats, List<AdvancedControllableProperty> advancedControllableProperties, JsonNode apiResponse, String groupName, CrestronPropertyList property)
			throws IOException {
		String number = cacheFilterValue.get(CrestronConstant.OUTPUT_GROUP + CrestronPropertyList.OUTPUT_NO.getName());
		if (!apiResponse.has("Outputs")) return;
		List<JsonNode> outputJson = objectMapper.readerFor(new TypeReference<List<JsonNode>>(){}).readValue(apiResponse.get("Outputs"));
		JsonNode output1 = !outputJson.isEmpty()? outputJson.get(0) : null;
		JsonNode portOutput1 = null;
		List<JsonNode> portOutputs = null;

		if (!outputJson.isEmpty()) {
			if (number == null) number = "1";

			int index = Integer.parseInt(number) - 1;
			if (index <= outputJson.size()) {
				output1 = outputJson.get(index);
			}

			if (output1 != null && output1.has("Ports")) {
				portOutputs = objectMapper.readerFor(new TypeReference<List<JsonNode>>() {}).readValue(output1.get("Ports"));
				portOutput1 = !portOutputs.isEmpty() ? portOutputs.get(0) : null;
			}

			if (portOutput1 != null) {
				switch (property) {
					case OUTPUT_NAME:
					case OUTPUT_SOURCE_HDCP:
					case DISABLED_BY_HDCP:
						JsonNode hdmi = portOutput1.get(CrestronConstant.HDMI);
						String hdmiValue = getDefaultValueForNullData(hdmi.get(property.getApiPropertyName()));
						if (property == CrestronPropertyList.DISABLED_BY_HDCP && !CrestronConstant.NONE.equals(hdmiValue)){
							stats.put(groupName, hdmiValue.equals(CrestronConstant.TRUE) ? CrestronConstant.YES : CrestronConstant.NO);
						} else {
							stats.put(groupName, hdmiValue);
						}
						break;
					case SINK_CONNECTED:
						String value = getDefaultValueForNullData(portOutput1.get(property.getApiPropertyName()));
						if (!Objects.equals(value, CrestronConstant.NONE)) {
							stats.put(groupName, value.equals(CrestronConstant.TRUE) ? CrestronConstant.YES : CrestronConstant.NO);
						} else {
							stats.put(groupName, value);
						}
						break;
					case RESOLUTION:
					case OUTPUT_ASPECT_RATIO:
					case ANALOG_AUDIO_VOLUME:
						if (property == CrestronPropertyList.ANALOG_AUDIO_VOLUME) {
							String volume = getDefaultValueForNullData(portOutput1.get("Audio").get("Volume"));
							if (!CrestronConstant.NONE.equals(volume)) {
								addAdvancedControlProperties(advancedControllableProperties, stats, createSlider(stats, groupName, "-100", "100", -100F, 100F, Float.parseFloat(volume)), volume);
								stats.put("Output#AnanlogAudioCurrentVolume", volume);
							}
						}
						stats.put(groupName,getDefaultValueForNullData(portOutput1.get(property.getApiPropertyName())));
						break;
					case OUTPUT_NO:
						String[] values = Arrays.stream(new int[outputJson.size()]).mapToObj( i -> String.valueOf(i + 1)).toArray(String[]::new);
						addAdvancedControlProperties(advancedControllableProperties, stats, createDropdown(groupName, values, number), number);
						break;
				}
			}
		}
	}

	private void populateDateTime(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties, JsonNode apiResponse, String groupName, CrestronPropertyList property)
			throws IOException {
		JsonNode ntpJson = apiResponse.get(CrestronConstant.NTP);
		if (ntpJson != null) {
			String value =  getDefaultValueForNullData(apiResponse.get(property.getApiPropertyName()));
			switch (property) {
				case NTPTIMESERVERS:
					if(!ntpJson.has(CrestronConstant.SERVERS_CURRENT_KEY_LIST)) break;
					List<JsonNode> serverCurrentKeyListJson = objectMapper.readerFor(new TypeReference<List<JsonNode>>(){}).readValue(ntpJson.get(CrestronConstant.SERVERS_CURRENT_KEY_LIST));
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
							addAdvancedControlProperties(advancedControllableProperties, stats, createDropdown(groupName, timezones, zone.getName()), zone.getName());
						}
					}
					break;
				case DATE:
				case TIME:
					if (!CrestronConstant.NONE.equals(value)) {
						DateTimeFormatter format = property == CrestronPropertyList.DATE ? dateFormat : timeFormat;
						OffsetDateTime offsetDateTime = OffsetDateTime.parse(value);
						String displayValue = format.format(offsetDateTime);
						addAdvancedControlProperties(advancedControllableProperties, stats, createText(groupName, displayValue), displayValue);
					}
					break;
			}
		}
	}

	/**
	 * Update monitoring data base on filter value
	 *
	 */
  private void updateFilterCache(String groupName) throws Exception {
		Map<String, String> stats = this.localExtendedStatistics.getStatistics();
		List<AdvancedControllableProperty> advancedControllableProperties = this.localExtendedStatistics.getControllableProperties();
		populateMonitoringAndControllableProperties(stats, advancedControllableProperties, true, groupName);
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
	private boolean getCookieSession(){
		try {
			if (StringUtils.isNullOrEmpty(this.getLogin()) || StringUtils.isNullOrEmpty(this.getPassword())) {
				throw new ResourceNotReachableException("Failed to authentication please check the credentials");
			}

	    HttpClient client = this.obtainHttpClient(true);
			HttpPost httpPost = new HttpPost(buildDeviceFullPath(CrestronConstant.LOGIN_URL));
			httpPost.setEntity(new StringEntity(String.format(CrestronConstant.AUTHENTICATION_PARAM, this.getLogin(), this.getPassword())));

			HttpResponse response = client.execute(httpPost);
			StringBuilder sb = new StringBuilder();
			Header[] headers = response.getHeaders(CrestronConstant.SET_COOKIE);
			Arrays.stream(headers).forEach(item -> sb.append(removeAttributes(item.getValue())));

			this.authenticationCookie = sb.toString();
		} catch (Exception e) {
			throw new ResourceNotReachableException("Failed to send login request to device");
		}
		return true;
	}

	/**
	 * Remove Secure, Path, HttpOnly attributes in cookie
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
	private boolean checkValidCookieSession() throws Exception {
		try {
			// detect device mode
			detectDeviceMode();
		} catch (Exception e) {
			boolean isAuthenticated = getCookieSession();
			if (isAuthenticated) {
				detectDeviceMode();
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
	private JsonNode extractApiResponseByGroup(String groupName, JsonNode response){
		if (response == null) return response;
		String[] groups = groupName.split("/");
		JsonNode result = response;
		for (String group : groups) {
			result = result.get(group);
			if (result == null)
				return result;
		}
		return result;
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

	/**
	 * Create switch is control property for metric
	 *
	 * @param name the name of property
	 * @param status initial status (0|1)
	 * @return AdvancedControllableProperty switch instance
	 */
	private AdvancedControllableProperty createSwitch(String name, int status, String labelOff, String labelOn) {
		AdvancedControllableProperty.Switch toggle = new AdvancedControllableProperty.Switch();
		toggle.setLabelOff(labelOff);
		toggle.setLabelOn(labelOn);

		AdvancedControllableProperty advancedControllableProperty = new AdvancedControllableProperty();
		advancedControllableProperty.setName(name);
		advancedControllableProperty.setValue(status);
		advancedControllableProperty.setType(toggle);
		advancedControllableProperty.setTimestamp(new Date());

		return advancedControllableProperty;
	}

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

	/***
	 * Create AdvancedControllableProperty slider instance
	 *
	 * @param stats extended statistics
	 * @param name name of the control
	 * @param initialValue initial value of the control
	 * @return AdvancedControllableProperty slider instance
	 */
	private AdvancedControllableProperty createSlider(Map<String, String> stats, String name, String labelStart, String labelEnd, Float rangeStart, Float rangeEnd, Float initialValue) {
		stats.put(name, initialValue.toString());
		AdvancedControllableProperty.Slider slider = new AdvancedControllableProperty.Slider();
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
		AdvancedControllableProperty.Text text = new AdvancedControllableProperty.Text();
		return new AdvancedControllableProperty(name, new Date(), text, stringValue);
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
