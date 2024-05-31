/*
 *  Copyright (c) 2024 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.encoderdecoder.crestron.nvx.common;

/**
 * CrestronPropertyList
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 5/8/2024
 * @since 1.0.0
 */
public enum CrestronPropertyList {
	// General group
	MODEL("Model", CrestronConstant.EMPTY, false ,CrestronConstant.DEVICE_INFO_API_GROUP, "Model", CrestronConstant.EMPTY),
	FIRMWARE_VERSION("FirmwareVersion", CrestronConstant.EMPTY, false ,CrestronConstant.DEVICE_INFO_API_GROUP,"DeviceVersion", CrestronConstant.EMPTY),
	FIRMWARE_BUILD_DATE("FirmwareBuildDate", CrestronConstant.EMPTY, false ,CrestronConstant.DEVICE_INFO_API_GROUP,  "BuildDate", CrestronConstant.EMPTY),
	SERIAL_NUMBER("SerialNumber", CrestronConstant.EMPTY, false ,CrestronConstant.DEVICE_INFO_API_GROUP,  "SerialNumber", CrestronConstant.EMPTY),
	DEVICE_MANUFACTURER("DeviceManufacturer", CrestronConstant.EMPTY, false ,CrestronConstant.DEVICE_INFO_API_GROUP,  "Manufacturer", CrestronConstant.EMPTY),
	DEVICE_ID("DeviceID", CrestronConstant.EMPTY, false ,CrestronConstant.DEVICE_INFO_API_GROUP,  "DeviceId", CrestronConstant.EMPTY),
	DEVICE_NAME("DeviceName", CrestronConstant.EMPTY, false ,CrestronConstant.DEVICE_INFO_API_GROUP,  "Name", CrestronConstant.EMPTY),
	PUF_VERSION("PUFVersion", CrestronConstant.EMPTY, false ,CrestronConstant.DEVICE_INFO_API_GROUP,  "PufVersion", CrestronConstant.EMPTY),
	DEVICE_KEY("Devicekey", CrestronConstant.EMPTY, false ,CrestronConstant.DEVICE_INFO_API_GROUP,  "Devicekey", CrestronConstant.EMPTY),
	REBOOT_REASON("RebootReason", CrestronConstant.EMPTY, false ,CrestronConstant.DEVICE_INFO_API_GROUP, "RebootReason", CrestronConstant.EMPTY),
	REBOOT_BUTTON("RebootButton",CrestronConstant.EMPTY, true ,CrestronConstant.DEVICE_INFO_API_GROUP,"", CrestronConstant.EMPTY),
	DEVICE_READY("DeviceReady", CrestronConstant.EMPTY, false ,CrestronConstant.DEVICE_SPECIFIC_API_GROUP,  "DeviceReady", CrestronConstant.EMPTY),
	FRONT_PANEL_LOCKOUT("FrontPanelLockout",CrestronConstant.EMPTY, false ,CrestronConstant.DEVICE_SPECIFIC_API_GROUP,  "IsFrontPanelLockoutEnabled", CrestronConstant.EMPTY),
	FIRMWARE_UPGRADED_STATUS("FirmwareUpgradedStatus", CrestronConstant.EMPTY, false ,CrestronConstant.DEVICE_OPERATION_API_GROUP, "UpgradeStatus", CrestronConstant.EMPTY),
	// Network group
	ADDRESS_SCHEMA("AddressSchema",CrestronConstant.NETWORK_GROUP, true,CrestronConstant.NETWORK_GROUP, "AddressSchema", CrestronConstant.EMPTY),
	HOST_NAME("HostName",CrestronConstant.NETWORK_GROUP, false,CrestronConstant.NETWORK_GROUP, "HostName", CrestronConstant.EMPTY),
	DOMAIN_NAME("DomainName",CrestronConstant.NETWORK_GROUP, false,CrestronConstant.NETWORK_GROUP, "DomainName", CrestronConstant.EMPTY),
	DHCP_ENABLED("DHCPEnabled",CrestronConstant.NETWORK_GROUP, false,CrestronConstant.NETWORK_GROUP, "IsDhcpEnabled", CrestronConstant.EMPTY),
	IP_ADDRESS("IPAddress",CrestronConstant.NETWORK_GROUP, false,CrestronConstant.NETWORK_GROUP, "Address", CrestronConstant.EMPTY),
	SUBNET_MASK("SubnetMask",CrestronConstant.NETWORK_GROUP, false,CrestronConstant.NETWORK_GROUP, "SubnetMask", CrestronConstant.EMPTY),
	DEFAULT_GATEWAY("DefaultGateway",CrestronConstant.NETWORK_GROUP, false,CrestronConstant.NETWORK_GROUP, "DefaultGateway", CrestronConstant.EMPTY),
	PRIMARY_STATIC_DNS("PrimaryStaticDNS",CrestronConstant.NETWORK_GROUP, false,CrestronConstant.NETWORK_GROUP, "StaticDns", CrestronConstant.EMPTY),
	SECONDARY_STATIC_DNS("SecondaryStaticDNS",CrestronConstant.NETWORK_GROUP, false,CrestronConstant.NETWORK_GROUP, "StaticDns", CrestronConstant.EMPTY),
	LINK_ACTIVE("LinkActive",CrestronConstant.NETWORK_GROUP, false,CrestronConstant.NETWORK_GROUP, "LinkStatus", CrestronConstant.EMPTY),
	MAC_ADDRESS("MacAddress",CrestronConstant.NETWORK_GROUP, false,CrestronConstant.NETWORK_GROUP, "MacAddress", CrestronConstant.EMPTY),
	IGMP_SUPPORT("IGMPSupport",CrestronConstant.NETWORK_GROUP, true,CrestronConstant.NETWORK_GROUP, "IgmpVersion", CrestronConstant.EMPTY),
	CLOUD_CONFIGURATION_SERVICE_CONNECTION("CloudConfigurationServiceConnection",CrestronConstant.NETWORK_GROUP, true, CrestronConstant.DEVICE_XIO_CLOUD_API_GROUP,"IsEnabled", CrestronConstant.EMPTY),
	// Control System
	ENCRYPT_CONNECTION("EncryptConnection",CrestronConstant.CONTROL_SYSTEM_GROUP, false,CrestronConstant.CONTROL_SYSTEM_GROUP, "EncryptConnection", CrestronConstant.EMPTY),
	IP_ID("IPID",CrestronConstant.CONTROL_SYSTEM_GROUP, true,CrestronConstant.CONTROL_SYSTEM_GROUP, "IpId", CrestronConstant.EMPTY),
	ROOM_ID("RoomID",CrestronConstant.CONTROL_SYSTEM_GROUP, false,CrestronConstant.CONTROL_SYSTEM_GROUP, "ProgramInstanceId", CrestronConstant.EMPTY),
	IP_ADDRESS_HOSTNAME("IPAddress",CrestronConstant.CONTROL_SYSTEM_GROUP, false,CrestronConstant.CONTROL_SYSTEM_GROUP, "Address", CrestronConstant.EMPTY),
	TYPE("Type",CrestronConstant.CONTROL_SYSTEM_GROUP, false,CrestronConstant.CONTROL_SYSTEM_GROUP, "Type", CrestronConstant.EMPTY),
	SERVER_PORT("ServerPort",CrestronConstant.CONTROL_SYSTEM_GROUP, false,CrestronConstant.CONTROL_SYSTEM_GROUP, "Port", CrestronConstant.EMPTY),
	CONNECTION("Connection",CrestronConstant.CONTROL_SYSTEM_GROUP, false,CrestronConstant.CONTROL_SYSTEM_GROUP, "ConnectionType", CrestronConstant.EMPTY),
	STATUS("Status",CrestronConstant.CONTROL_SYSTEM_GROUP, false,CrestronConstant.CONTROL_SYSTEM_GROUP, "Status", CrestronConstant.EMPTY),
	// Input
	INPUT_NAME("Name",CrestronConstant.INPUT_GROUP, false ,CrestronConstant.DEVICE_AUDIO_VIDEO_INPUT_OUTPUT_API_GROUP, "Name", CrestronConstant.TRANSMITTER),
	SYNC_DETECTED("SyncDetected",CrestronConstant.INPUT_GROUP, false ,CrestronConstant.DEVICE_AUDIO_VIDEO_INPUT_OUTPUT_API_GROUP,"IsSyncDetected", CrestronConstant.TRANSMITTER),
	HORIZONTAL_RESOLUTION("HorizontalResolution",CrestronConstant.INPUT_GROUP, false ,CrestronConstant.DEVICE_AUDIO_VIDEO_INPUT_OUTPUT_API_GROUP, "HorizontalResolution", CrestronConstant.TRANSMITTER),
	VERTICAL_RESOLUTION("VerticalResolution",CrestronConstant.INPUT_GROUP, false ,CrestronConstant.DEVICE_AUDIO_VIDEO_INPUT_OUTPUT_API_GROUP, "VerticalResolution", CrestronConstant.TRANSMITTER),
	INPUT_SOURCE_HDCP("HDCPState",CrestronConstant.INPUT_GROUP, false ,CrestronConstant.DEVICE_AUDIO_VIDEO_INPUT_OUTPUT_API_GROUP, "HdcpState", CrestronConstant.TRANSMITTER),
	SOURCE_CONTENT_STREAM_TYPE("ContentStreamType",CrestronConstant.INPUT_GROUP, false ,CrestronConstant.DEVICE_AUDIO_VIDEO_INPUT_OUTPUT_API_GROUP, "ContentStreamType", CrestronConstant.TRANSMITTER), // unknown
	INTERLACED("Interlaced",CrestronConstant.INPUT_GROUP, false ,CrestronConstant.DEVICE_AUDIO_VIDEO_INPUT_OUTPUT_API_GROUP, "IsInterlacedDetected", CrestronConstant.TRANSMITTER),
	INPUT_ASPECT_RATIO("AspectRatio",CrestronConstant.INPUT_GROUP, false ,CrestronConstant.DEVICE_AUDIO_VIDEO_INPUT_OUTPUT_API_GROUP, "AspectRatio", CrestronConstant.TRANSMITTER),
	AUDIO_FORMAT("AudioFormat",CrestronConstant.INPUT_GROUP, false ,CrestronConstant.DEVICE_AUDIO_VIDEO_INPUT_OUTPUT_API_GROUP, "Format", CrestronConstant.TRANSMITTER),
	AUDIO_CHANNELS("AudioChannel",CrestronConstant.INPUT_GROUP, false ,CrestronConstant.DEVICE_AUDIO_VIDEO_INPUT_OUTPUT_API_GROUP, "Channels", CrestronConstant.TRANSMITTER),
	EDID("EDID",CrestronConstant.INPUT_GROUP, false ,CrestronConstant.DEVICE_AUDIO_VIDEO_INPUT_OUTPUT_API_GROUP, "CurrentEdid", CrestronConstant.TRANSMITTER),
	HDCP_RECEIVER_CAPABILITY("HdcpReceiverCapability",CrestronConstant.INPUT_GROUP, false ,CrestronConstant.DEVICE_AUDIO_VIDEO_INPUT_OUTPUT_API_GROUP, "HdcpReceiverCapability", CrestronConstant.TRANSMITTER),
	INPUT_NO("No",CrestronConstant.INPUT_GROUP, true ,CrestronConstant.DEVICE_AUDIO_VIDEO_INPUT_OUTPUT_API_GROUP, "Uuid", CrestronConstant.TRANSMITTER),
	// Output
	OUTPUT_NAME("Name",CrestronConstant.OUTPUT_GROUP, false,CrestronConstant.DEVICE_AUDIO_VIDEO_INPUT_OUTPUT_API_GROUP,  "Name", CrestronConstant.RECEIVER),
	SINK_CONNECTED("SinkConnected",CrestronConstant.OUTPUT_GROUP, false,CrestronConstant.DEVICE_AUDIO_VIDEO_INPUT_OUTPUT_API_GROUP,  "IsSinkConnected", CrestronConstant.RECEIVER),
	RESOLUTION("Resolution",CrestronConstant.OUTPUT_GROUP, false,CrestronConstant.DEVICE_AUDIO_VIDEO_INPUT_OUTPUT_API_GROUP,  "Resolution", CrestronConstant.RECEIVER),
	OUTPUT_SOURCE_HDCP("SourceHDCP",CrestronConstant.OUTPUT_GROUP, false,CrestronConstant.DEVICE_AUDIO_VIDEO_INPUT_OUTPUT_API_GROUP,  "HdcpState", CrestronConstant.RECEIVER),
	DISABLED_BY_HDCP("DisabledByHDCP",CrestronConstant.OUTPUT_GROUP, false,CrestronConstant.DEVICE_AUDIO_VIDEO_INPUT_OUTPUT_API_GROUP,  "DisabledByHdcp", CrestronConstant.RECEIVER),
	OUTPUT_ASPECT_RATIO("AspectRatio",CrestronConstant.OUTPUT_GROUP, false,CrestronConstant.DEVICE_AUDIO_VIDEO_INPUT_OUTPUT_API_GROUP,  "AspectRatio", CrestronConstant.RECEIVER),
	ANALOG_AUDIO_VOLUME("AnalogAudioVolume",CrestronConstant.OUTPUT_GROUP, true,CrestronConstant.DEVICE_AUDIO_VIDEO_INPUT_OUTPUT_API_GROUP,  "Volume", CrestronConstant.RECEIVER),
	OUTPUT_NO("No",CrestronConstant.OUTPUT_GROUP, false,CrestronConstant.DEVICE_AUDIO_VIDEO_INPUT_OUTPUT_API_GROUP,  "Uuid", CrestronConstant.RECEIVER),

	// Auto update
	AUTO_UPDATE("AutoUpdate",CrestronConstant.AUTO_UPDATE_GROUP, true,CrestronConstant.AUTO_UPDATE_GROUP, "IsEnabled", CrestronConstant.EMPTY),
	CUSTOM_URL("CustomUrl",CrestronConstant.AUTO_UPDATE_GROUP, false,CrestronConstant.AUTO_UPDATE_GROUP, "IsCustomUrlEnabled", CrestronConstant.EMPTY),
	CUSTOM_URL_PATH("CustomUrlPath",CrestronConstant.AUTO_UPDATE_GROUP, false,CrestronConstant.AUTO_UPDATE_GROUP, "ManifestPath", CrestronConstant.EMPTY),
	SCHEDULE_DAY_OF_WEEK("ScheduleDayOfWeek",CrestronConstant.AUTO_UPDATE_GROUP, false,CrestronConstant.AUTO_UPDATE_GROUP, "DayOfWeek", CrestronConstant.EMPTY),
	SCHEDULE_TIME_OF_DAY( "ScheduleTimeOfDay",CrestronConstant.AUTO_UPDATE_GROUP, false,CrestronConstant.AUTO_UPDATE_GROUP, "TimeOfDay", CrestronConstant.EMPTY),
	SCHEDULE_POLL_INTERVAL("SchedulePollInterval",CrestronConstant.AUTO_UPDATE_GROUP, false,CrestronConstant.AUTO_UPDATE_GROUP, "CheckInterval", CrestronConstant.EMPTY),

	TIMESYNCHRONIZE("TimeSynchronize",CrestronConstant.DATE_TIME_GROUP, true,CrestronConstant.DATE_TIME_GROUP, "", CrestronConstant.EMPTY),
	TIMEZONE("TimeZone",CrestronConstant.DATE_TIME_GROUP, false,CrestronConstant.DATE_TIME_GROUP, "TimeZone", CrestronConstant.EMPTY),
	SYNCHRONIZE("Synchronize",CrestronConstant.DATE_TIME_GROUP, true,CrestronConstant.DATE_TIME_GROUP, "", CrestronConstant.EMPTY),
	DATE("Date",CrestronConstant.DATE_TIME_GROUP, false,CrestronConstant.DATE_TIME_GROUP, "CurrentTimeWithOffset", CrestronConstant.EMPTY),
	TIME("Time",CrestronConstant.DATE_TIME_GROUP, false,CrestronConstant.DATE_TIME_GROUP, "CurrentTimeWithOffset", CrestronConstant.EMPTY),
	NTPTIMESERVERS("NTPTimeServers",CrestronConstant.DATE_TIME_GROUP, false,CrestronConstant.DATE_TIME_GROUP, "Address", CrestronConstant.EMPTY),

	DISCOVERY_AGENT("DiscoveryAgent",CrestronConstant.DISCOVERY_CONFIG_GROUP, true,CrestronConstant.DISCOVERY_CONFIG_GROUP,  "DiscoveryAgent", CrestronConstant.EMPTY),
	CUSTOM_TTL("CustomTTL",CrestronConstant.DISCOVERY_CONFIG_GROUP, true,CrestronConstant.DISCOVERY_CONFIG_GROUP,  "CustomTTL", CrestronConstant.EMPTY),
	TTL("TTL",CrestronConstant.DISCOVERY_CONFIG_GROUP, false,CrestronConstant.DISCOVERY_CONFIG_GROUP,  "Ttl", CrestronConstant.EMPTY),

	RECEIVE_UUID("UUID",CrestronConstant.STREAM_RECEIVE_GROUP, false,CrestronConstant.STREAM_RECEIVE_GROUP, "UUID", CrestronConstant.RECEIVER),
	RECEIVE_MODE("Mode",CrestronConstant.STREAM_RECEIVE_GROUP, true,CrestronConstant.DEVICE_SPECIFIC_API_GROUP, "TransportMode", CrestronConstant.RECEIVER),
	RECEIVE_STREAM_LOCATION("StreamLocation",CrestronConstant.STREAM_RECEIVE_GROUP, false,CrestronConstant.STREAM_RECEIVE_GROUP, "StreamLocation", CrestronConstant.RECEIVER),
	RECEIVE_MULTICAST_ADDRESS("MulticastAddress",CrestronConstant.STREAM_RECEIVE_GROUP, false,CrestronConstant.STREAM_RECEIVE_GROUP, "MulticastAddress", CrestronConstant.RECEIVER),
	RECEIVE_RECEIVE_STATUS("Status",CrestronConstant.STREAM_RECEIVE_GROUP, false,CrestronConstant.STREAM_RECEIVE_GROUP, "Status", CrestronConstant.RECEIVER),
	RECEIVE_RECEIVE_HORIZONTAL_RESOLUTION("HorizontalResolution",CrestronConstant.STREAM_RECEIVE_GROUP, false,CrestronConstant.STREAM_RECEIVE_GROUP, "HorizontalResolution", CrestronConstant.RECEIVER),
	RECEIVE_RECEIVE_VERTICAL_RESOLUTION("VerticalResolution",CrestronConstant.STREAM_RECEIVE_GROUP, false,CrestronConstant.STREAM_RECEIVE_GROUP, "VerticalResolution", CrestronConstant.RECEIVER),

	TRANSMIT_UUID("UUID",CrestronConstant.STREAM_TRANSMIT_GROUP, false,CrestronConstant.STREAM_TRANSMIT_GROUP, "UUID", CrestronConstant.TRANSMITTER),
	TRANSMIT_MODE("Mode",CrestronConstant.STREAM_TRANSMIT_GROUP, true,CrestronConstant.DEVICE_SPECIFIC_API_GROUP, "TransportMode", CrestronConstant.TRANSMITTER),
	TRANSMIT_STREAM_LOCATION("StreamLocation",CrestronConstant.STREAM_TRANSMIT_GROUP, false,CrestronConstant.STREAM_TRANSMIT_GROUP, "StreamLocation", CrestronConstant.TRANSMITTER),
	TRANSMIT_MULTICAST_ADDRESS("MulticastAddress",CrestronConstant.STREAM_TRANSMIT_GROUP, false,CrestronConstant.STREAM_TRANSMIT_GROUP, "MulticastAddress", CrestronConstant.TRANSMITTER),
	TRANSMIT_RECEIVE_STATUS("Status",CrestronConstant.STREAM_TRANSMIT_GROUP, false,CrestronConstant.STREAM_TRANSMIT_GROUP, "Status", CrestronConstant.TRANSMITTER),
	TRANSMIT_RECEIVE_HORIZONTAL_RESOLUTION("HorizontalResolution",CrestronConstant.STREAM_TRANSMIT_GROUP, false,CrestronConstant.STREAM_TRANSMIT_GROUP, "HorizontalResolution", CrestronConstant.TRANSMITTER),
	TRANSMIT_RECEIVE_VERTICAL_RESOLUTION("VerticalResolution",CrestronConstant.STREAM_TRANSMIT_GROUP, false,CrestronConstant.STREAM_TRANSMIT_GROUP, "VerticalResolution", CrestronConstant.TRANSMITTER),

	SUB_UNIQUE_ID("UniqueId",CrestronConstant.STREAM_SUBSCRIPTION_GROUP, false,CrestronConstant.STREAM_SUBSCRIPTION_GROUP, "UniqueId", CrestronConstant.RECEIVER),
	SUB_SESSION_NAME("DeviceName",CrestronConstant.STREAM_SUBSCRIPTION_GROUP, false,CrestronConstant.STREAM_SUBSCRIPTION_GROUP, "SessionName", CrestronConstant.RECEIVER),
	SUB_RTSP_ADDRESS("RTSPAddress",CrestronConstant.STREAM_SUBSCRIPTION_GROUP, false,CrestronConstant.STREAM_SUBSCRIPTION_GROUP, "RtspUri", CrestronConstant.RECEIVER),
	SUB_MULTICAST_ADDRESS("MulticastAddress",CrestronConstant.STREAM_SUBSCRIPTION_GROUP, false,CrestronConstant.STREAM_SUBSCRIPTION_GROUP, "MulticastAddress", CrestronConstant.RECEIVER),
	SUB_ENCRYPTION("Encryption",CrestronConstant.STREAM_SUBSCRIPTION_GROUP, false,CrestronConstant.STREAM_SUBSCRIPTION_GROUP, "Encryption", CrestronConstant.RECEIVER),
	SUB_TRANSPORT("Transport",CrestronConstant.STREAM_SUBSCRIPTION_GROUP, false,CrestronConstant.STREAM_SUBSCRIPTION_GROUP, "Transport", CrestronConstant.RECEIVER),
	SUB_RESOLUTION("Resolution",CrestronConstant.STREAM_SUBSCRIPTION_GROUP, false,CrestronConstant.STREAM_SUBSCRIPTION_GROUP, "Resolution", CrestronConstant.RECEIVER),
	SUB_AUDIO_FORMAT("AudioFormat",CrestronConstant.STREAM_SUBSCRIPTION_GROUP, false,CrestronConstant.STREAM_SUBSCRIPTION_GROUP, "AudioFormat", CrestronConstant.RECEIVER),
	SUB_BITRATE("Bitrate",CrestronConstant.STREAM_SUBSCRIPTION_GROUP, false,CrestronConstant.STREAM_SUBSCRIPTION_GROUP, "Bitrate", CrestronConstant.RECEIVER),
	UNSUBSCRIBE("unsubscribe",CrestronConstant.STREAM_SUBSCRIPTION_GROUP, true,CrestronConstant.STREAM_SUBSCRIPTION_GROUP, "", CrestronConstant.RECEIVER),

	AVAILABLE_UNIQUE_ID("UniqueId",CrestronConstant.STREAM_AVAILABLE_GROUP,false,CrestronConstant.STREAM_AVAILABLE_GROUP, "UniqueId", CrestronConstant.RECEIVER),
	AVAILABLE_SESSION_NAME("DeviceName",CrestronConstant.STREAM_AVAILABLE_GROUP,false,CrestronConstant.STREAM_AVAILABLE_GROUP, "SessionName", CrestronConstant.RECEIVER),
	AVAILABLE_RTSP_ADDRESS("RTSPAddress",CrestronConstant.STREAM_AVAILABLE_GROUP,false,CrestronConstant.STREAM_AVAILABLE_GROUP, "RtspUri", CrestronConstant.RECEIVER),
	AVAILABLE_MULTICAST_ADDRESS("MulticastAddress",CrestronConstant.STREAM_AVAILABLE_GROUP,false,CrestronConstant.STREAM_AVAILABLE_GROUP, "MulticastAddress", CrestronConstant.RECEIVER),
	AVAILABLE_ENCRYPTION("Encryption",CrestronConstant.STREAM_AVAILABLE_GROUP,false,CrestronConstant.STREAM_AVAILABLE_GROUP, "Encryption", CrestronConstant.RECEIVER),
	AVAILABLE_TRANSPORT("Transport",CrestronConstant.STREAM_AVAILABLE_GROUP,false,CrestronConstant.STREAM_AVAILABLE_GROUP, "Transport", CrestronConstant.RECEIVER),
	AVAILABLE_RESOLUTION("Resolution",CrestronConstant.STREAM_AVAILABLE_GROUP,false,CrestronConstant.STREAM_AVAILABLE_GROUP, "Resolution", CrestronConstant.RECEIVER),
	AVAILABLE_AUDIO_FORMAT("AudioFormat",CrestronConstant.STREAM_AVAILABLE_GROUP,false,CrestronConstant.STREAM_AVAILABLE_GROUP, "AudioFormat", CrestronConstant.RECEIVER),
	AVAILABLE_BITRATE("Bitrate",CrestronConstant.STREAM_AVAILABLE_GROUP,false,CrestronConstant.STREAM_AVAILABLE_GROUP, "Bitrate", CrestronConstant.RECEIVER),
	SUBSCRIBE("subscribe",CrestronConstant.STREAM_AVAILABLE_GROUP, true,CrestronConstant.STREAM_AVAILABLE_GROUP, "", CrestronConstant.RECEIVER),

	AUTOMATIC_INPUT_ROUTING("AutomaticInputRouting",CrestronConstant.INPUT_ROUTING_GROUP, true,CrestronConstant.INPUT_ROUTING_GROUP, "AutoInputRoutingEnabled", CrestronConstant.EMPTY),
	AUDIO_SOURCE("AudioSource",CrestronConstant.INPUT_ROUTING_GROUP, true,CrestronConstant.INPUT_ROUTING_GROUP, "AudioSource", CrestronConstant.EMPTY),
	ACTIVE_AUDIO_SOURCE("ActiveAudioSource",CrestronConstant.INPUT_ROUTING_GROUP, false,CrestronConstant.INPUT_ROUTING_GROUP, "ActiveAudioSource", CrestronConstant.EMPTY),
	VIDEO_SOURCE("VideoSource",CrestronConstant.INPUT_ROUTING_GROUP, true,CrestronConstant.INPUT_ROUTING_GROUP, "VideoSource", CrestronConstant.EMPTY),
	ACTIVE_VIDEO_SOURCE("ActiveVideoSource",CrestronConstant.INPUT_ROUTING_GROUP, false,CrestronConstant.INPUT_ROUTING_GROUP, "ActiveVideoSource", CrestronConstant.EMPTY),
	ANALOG_AUDIO_MODE("AnalogAudioMode",CrestronConstant.INPUT_ROUTING_GROUP, false,CrestronConstant.INPUT_ROUTING_GROUP, "AudioMode", CrestronConstant.EMPTY);

	private String name;
	private String group;
	private boolean isControl;
	private String apiGroupName;
	private String apiPropertyName;
	private String deviceMode;

	CrestronPropertyList(String name, String group, boolean isControl,String apiGroupName, String apiPropertyName, String deviceMode) {
		this.name = name;
		this.group = group;
		this.isControl = isControl;
		this.apiGroupName = apiGroupName;
		this.apiPropertyName = apiPropertyName;
		this.deviceMode = deviceMode;
	}

	/**
	 * Retrieves {@link #deviceMode}
	 *
	 * @return value of {@link #deviceMode}
	 */
	public String getDeviceMode() {
		return deviceMode;
	}

	/**
	 * Retrieves {@link #name}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Retrieves {@link #group}
	 *
	 * @return value of {@link #group}
	 */
	public String getGroup() {
		return group;
	}

	/**
	 * Retrieves {@link #isControl}
	 *
	 * @return value of {@link #isControl}
	 */
	public boolean isControl() {
		return isControl;
	}

	/**
	 * Retrieves {@link #apiPropertyName}
	 *
	 * @return value of {@link #apiPropertyName}
	 */
	public String getApiPropertyName() {
		return apiPropertyName;
	}

	/**
	 * Retrieves {@link #apiGroupName}
	 *
	 * @return value of {@link #apiGroupName}
	 */
	public String getApiGroupName() {
		return apiGroupName;
	}
}
