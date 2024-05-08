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
	MODEL("Model", CrestronConstant.EMPTY, false ,CrestronConstant.DEVICE_INFO_API_GROUP, "Model"),
	FIRMWARE_VERSION("FirmwareVersion", CrestronConstant.EMPTY, false ,CrestronConstant.DEVICE_INFO_API_GROUP,"DeviceVersion"),
	FIRMWARE_BUILD_DATE("FirmwareBuildDate", CrestronConstant.EMPTY, false ,CrestronConstant.DEVICE_INFO_API_GROUP,  "BuildDate"),
	SERIAL_NUMBER("SerialNumber", CrestronConstant.EMPTY, false ,CrestronConstant.DEVICE_INFO_API_GROUP,  "SerialNumber"),
	DEVICE_MANUFACTURER("DeviceManufacturer", CrestronConstant.EMPTY, false ,CrestronConstant.DEVICE_INFO_API_GROUP,  "Manufacturer"),
	DEVICE_ID("DeviceID", CrestronConstant.EMPTY, false ,CrestronConstant.DEVICE_INFO_API_GROUP,  "DeviceId"),
	DEVICE_NAME("DeviceName", CrestronConstant.EMPTY, false ,CrestronConstant.DEVICE_INFO_API_GROUP,  "Name"),
	PUF_VERSION("PUFVersion", CrestronConstant.EMPTY, false ,CrestronConstant.DEVICE_INFO_API_GROUP,  "PufVersion"),
	DEVICE_KEY("Devicekey", CrestronConstant.EMPTY, false ,CrestronConstant.DEVICE_INFO_API_GROUP,  "Devicekey"),
	REBOOT_REASON("RebootReason", CrestronConstant.EMPTY, false ,CrestronConstant.DEVICE_INFO_API_GROUP, "RebootReason"),
	//	REBOOT_BUTTON("RebootButton", ""),
	DEVICE_READY("DeviceReady", CrestronConstant.EMPTY, false ,CrestronConstant.DEVICE_SPECIFIC_API_GROUP,  "DeviceReady"),
	FRONT_PANEL_LOCKOUT("FrontPanelLockout",CrestronConstant.EMPTY, false ,CrestronConstant.DEVICE_SPECIFIC_API_GROUP,  "IsFrontPanelLockoutEnabled"),
	FIRMWARE_UPGRADED_STATUS("FirmwareUpgradedStatus", CrestronConstant.EMPTY, false ,CrestronConstant.DEVICE_OPERATION_API_GROUP, "UpgradeStatus"),
	// Network group
	HOST_NAME("HostName",CrestronConstant.NETWORK_GROUP, false,CrestronConstant.NETWORK_GROUP, "HostName"),
	DOMAIN_NAME("DomainName",CrestronConstant.NETWORK_GROUP, false,CrestronConstant.NETWORK_GROUP, "DomainName"),
	DHCP_ENABLED("DHCPEnabled",CrestronConstant.NETWORK_GROUP, false,CrestronConstant.NETWORK_GROUP, "IsDhcpEnabled"),
	IP_ADDRESS("IPAddress",CrestronConstant.NETWORK_GROUP, false,CrestronConstant.NETWORK_GROUP, "Address"),
	SUBNET_MASK("SubnetMask",CrestronConstant.NETWORK_GROUP, false,CrestronConstant.NETWORK_GROUP, "SubnetMask"),
	DEFAULT_GATEWAY("DefaultGateway",CrestronConstant.NETWORK_GROUP, false,CrestronConstant.NETWORK_GROUP, "DefaultGateway"),
	PRIMARY_STATIC_DNS("PrimaryStaticDNS",CrestronConstant.NETWORK_GROUP, false,CrestronConstant.NETWORK_GROUP, "StaticDns"),
	SECONDARY_STATIC_DNS("SecondaryStaticDNS",CrestronConstant.NETWORK_GROUP, false,CrestronConstant.NETWORK_GROUP, "StaticDns"),
	LINK_ACTIVE("LinkActive",CrestronConstant.NETWORK_GROUP, false,CrestronConstant.NETWORK_GROUP, "LinkStatus"),
	MAC_ADDRESS("MacAddress",CrestronConstant.NETWORK_GROUP, false,CrestronConstant.NETWORK_GROUP, "MacAddress"),
	IGMP_SUPPORT("IGMPSupport",CrestronConstant.NETWORK_GROUP, false,CrestronConstant.NETWORK_GROUP, "IgmpVersion"),
	CLOUD_CONFIGURATION_SERVICE_CONNECTION("CloudConfigurationServiceConnection",CrestronConstant.NETWORK_GROUP, false, CrestronConstant.DEVICE_XIO_CLOUD_API_GROUP,"IsEnabled"),
  // Control System
	ENCRYPT_CONNECTION("EncryptConnection",CrestronConstant.CONTROL_SYSTEM_GROUP, false,CrestronConstant.CONTROL_SYSTEM_GROUP, "EncryptConnection"),
	IP_ID("IPID",CrestronConstant.CONTROL_SYSTEM_GROUP, false,CrestronConstant.CONTROL_SYSTEM_GROUP, "IpId"),
	ROOM_ID("RoomID",CrestronConstant.CONTROL_SYSTEM_GROUP, false,CrestronConstant.CONTROL_SYSTEM_GROUP, "RoomID"),
	IP_ADDRESS_HOSTNAME("IPAddress",CrestronConstant.CONTROL_SYSTEM_GROUP, false,CrestronConstant.CONTROL_SYSTEM_GROUP, "Address"),
	TYPE("Type",CrestronConstant.CONTROL_SYSTEM_GROUP, false,CrestronConstant.CONTROL_SYSTEM_GROUP, "Type"),
	SERVER_PORT("ServerPort",CrestronConstant.CONTROL_SYSTEM_GROUP, false,CrestronConstant.CONTROL_SYSTEM_GROUP, "Port"),
	CONNECTION("Connection",CrestronConstant.CONTROL_SYSTEM_GROUP, false,CrestronConstant.CONTROL_SYSTEM_GROUP, "ConnectionType"),
	STATUS("Status",CrestronConstant.CONTROL_SYSTEM_GROUP, false,CrestronConstant.CONTROL_SYSTEM_GROUP, "Status"),
	// Input
	INPUT_NAME("Name",CrestronConstant.INPUT_GROUP, false ,CrestronConstant.DEVICE_AUDIO_VIDEO_INPUT_OUTPUT_API_GROUP, "Name"),
	SYNC_DETECTED("SyncDetected",CrestronConstant.INPUT_GROUP, false ,CrestronConstant.DEVICE_AUDIO_VIDEO_INPUT_OUTPUT_API_GROUP,"IsSyncDetected"),
	HORIZONTAL_RESOLUTION("HorizontalResolution",CrestronConstant.INPUT_GROUP, false ,CrestronConstant.DEVICE_AUDIO_VIDEO_INPUT_OUTPUT_API_GROUP, "HorizontalResolution"),
	VERTICAL_RESOLUTION("VerticalResolution",CrestronConstant.INPUT_GROUP, false ,CrestronConstant.DEVICE_AUDIO_VIDEO_INPUT_OUTPUT_API_GROUP, "VerticalResolution"),
	INPUT_SOURCE_HDCP("HDCPState",CrestronConstant.INPUT_GROUP, false ,CrestronConstant.DEVICE_AUDIO_VIDEO_INPUT_OUTPUT_API_GROUP, "HdcpState"),
	SOURCE_CONTENT_STREAM_TYPE("ContentStreamType",CrestronConstant.INPUT_GROUP, false ,CrestronConstant.DEVICE_AUDIO_VIDEO_INPUT_OUTPUT_API_GROUP, "ContentStreamType"), // unknown
	INTERLACED("Interlaced",CrestronConstant.INPUT_GROUP, false ,CrestronConstant.DEVICE_AUDIO_VIDEO_INPUT_OUTPUT_API_GROUP, "IsInterlacedDetected"),
	INPUT_ASPECT_RATIO("AspectRatio",CrestronConstant.INPUT_GROUP, false ,CrestronConstant.DEVICE_AUDIO_VIDEO_INPUT_OUTPUT_API_GROUP, "AspectRatio"),
	AUDIO_FORMAT("AudioFormat",CrestronConstant.INPUT_GROUP, false ,CrestronConstant.DEVICE_AUDIO_VIDEO_INPUT_OUTPUT_API_GROUP, "Format"),
	AUDIO_CHANNELS("AudioChannel",CrestronConstant.INPUT_GROUP, false ,CrestronConstant.DEVICE_AUDIO_VIDEO_INPUT_OUTPUT_API_GROUP, "Channels"),
	EDID("EDID",CrestronConstant.INPUT_GROUP, false ,CrestronConstant.DEVICE_AUDIO_VIDEO_INPUT_OUTPUT_API_GROUP, "CurrentEdid"),
	HDCP_RECEIVER_CAPABILITY("HdcpReceiverCapability",CrestronConstant.INPUT_GROUP, false ,CrestronConstant.DEVICE_AUDIO_VIDEO_INPUT_OUTPUT_API_GROUP, "HdcpReceiverCapability"),
	INPUT_UUID("InputUUID",CrestronConstant.INPUT_GROUP, false ,CrestronConstant.DEVICE_AUDIO_VIDEO_INPUT_OUTPUT_API_GROUP, "Uuid"),
	PORT_UUID("PortUUID",CrestronConstant.INPUT_GROUP, false ,CrestronConstant.DEVICE_AUDIO_VIDEO_INPUT_OUTPUT_API_GROUP, "Uuid"),
	// Output
	OUTPUT_NAME("Name",CrestronConstant.OUTPUT_GROUP, false,CrestronConstant.DEVICE_AUDIO_VIDEO_INPUT_OUTPUT_API_GROUP,  "Name"),
	SINK_CONNECTED("SinkConnected",CrestronConstant.OUTPUT_GROUP, false,CrestronConstant.DEVICE_AUDIO_VIDEO_INPUT_OUTPUT_API_GROUP,  "IsSinkConnected"),
	RESOLUTION("Resolution",CrestronConstant.OUTPUT_GROUP, false,CrestronConstant.DEVICE_AUDIO_VIDEO_INPUT_OUTPUT_API_GROUP,  "Resolution"),
	OUTPUT_SOURCE_HDCP("HDCPState",CrestronConstant.OUTPUT_GROUP, false,CrestronConstant.DEVICE_AUDIO_VIDEO_INPUT_OUTPUT_API_GROUP,  "HdcpState"),
	DISABLED_BY_HDCP("DisabledByHDCP",CrestronConstant.OUTPUT_GROUP, false,CrestronConstant.DEVICE_AUDIO_VIDEO_INPUT_OUTPUT_API_GROUP,  "DisabledByHdcp"),
	OUTPUT_ASPECT_RATIO("AspectRatio",CrestronConstant.OUTPUT_GROUP, false,CrestronConstant.DEVICE_AUDIO_VIDEO_INPUT_OUTPUT_API_GROUP,  "AspectRatio"),
	ANALOG_AUDIO_VOLUME("AnalogAudioVolume",CrestronConstant.OUTPUT_GROUP, false,CrestronConstant.DEVICE_AUDIO_VIDEO_INPUT_OUTPUT_API_GROUP,  "volume"),
	OUTPUT_UUID("OutputUUID",CrestronConstant.OUTPUT_GROUP, false,CrestronConstant.DEVICE_AUDIO_VIDEO_INPUT_OUTPUT_API_GROUP,  "Uuid"),
	OUTPUT_PORT_UUID("PortUUID",CrestronConstant.OUTPUT_GROUP, false,CrestronConstant.DEVICE_AUDIO_VIDEO_INPUT_OUTPUT_API_GROUP,  "Uuid"),
	// Auto update
	AUTO_UPDATE("AutoUpdate",CrestronConstant.AUTO_UPDATE_GROUP, false,CrestronConstant.AUTO_UPDATE_GROUP, "IsEnabled"),
	CUSTOM_URL("CustomUrl",CrestronConstant.AUTO_UPDATE_GROUP, false,CrestronConstant.AUTO_UPDATE_GROUP, "IsCustomUrlEnabled"),
	CUSTOM_URL_PATH("CustomUrlPath",CrestronConstant.AUTO_UPDATE_GROUP, false,CrestronConstant.AUTO_UPDATE_GROUP, "ManifestPath"),
	SCHEDULE_DAY_OF_WEEK("ScheduleDayOfWeek",CrestronConstant.AUTO_UPDATE_GROUP, false,CrestronConstant.AUTO_UPDATE_GROUP, "DayOfWeek"),
	SCHEDULE_TIME_OF_DAY( "ScheduleTimeOfDay",CrestronConstant.AUTO_UPDATE_GROUP, false,CrestronConstant.AUTO_UPDATE_GROUP, "TimeOfDay"),
	SCHEDULE_POLL_INTERVAL("SchedulePollInterval",CrestronConstant.AUTO_UPDATE_GROUP, false,CrestronConstant.AUTO_UPDATE_GROUP, "CheckInterval"),

	TIMEZONE("TimeZone",CrestronConstant.DATE_TIME_GROUP, false,CrestronConstant.DATE_TIME_GROUP, "TimeZone"),
	SYNCHRONIZE("Synchronize",CrestronConstant.DATE_TIME_GROUP, false,CrestronConstant.DATE_TIME_GROUP, ""),
	CURRENT_TIME("CurrentDateTime",CrestronConstant.DATE_TIME_GROUP, false,CrestronConstant.DATE_TIME_GROUP, "CurrentTime"),
	NTPTIMESERVERS("NTPTimeServers",CrestronConstant.DATE_TIME_GROUP, false,CrestronConstant.DATE_TIME_GROUP, "Address"),

	DISCOVERY_AGENT("DiscoveryAgent",CrestronConstant.DISCOVERY_CONFIG_GROUP, false,CrestronConstant.DISCOVERY_CONFIG_GROUP,  "DiscoveryAgent"),
	TTL("TTL",CrestronConstant.DISCOVERY_CONFIG_GROUP, false,CrestronConstant.DISCOVERY_CONFIG_GROUP,  "Ttl"),

	RECEIVE_UUID("UUID",CrestronConstant.STREAM_RECEIVE_GROUP, false,CrestronConstant.STREAM_RECEIVE_GROUP, "UUID"),
	RECEIVE_MODE("Mode",CrestronConstant.STREAM_RECEIVE_GROUP, false,CrestronConstant.STREAM_RECEIVE_GROUP, "TransportMode"),
	RECEIVE_STREAM_LOCATION("StreamLocation",CrestronConstant.STREAM_RECEIVE_GROUP, false,CrestronConstant.STREAM_RECEIVE_GROUP, "StreamLocation"),
	RECEIVE_MULTICAST_ADDRESS("MulticastAddress",CrestronConstant.STREAM_RECEIVE_GROUP, false,CrestronConstant.STREAM_RECEIVE_GROUP, "MulticastAddress"),
	RECEIVE_RECEIVE_STATUS("Status",CrestronConstant.STREAM_RECEIVE_GROUP, false,CrestronConstant.STREAM_RECEIVE_GROUP, "Status"),
	RECEIVE_RECEIVE_HORIZONTAL_RESOLUTION("HorizontalResolution",CrestronConstant.STREAM_RECEIVE_GROUP, false,CrestronConstant.STREAM_RECEIVE_GROUP, "HorizontalResolution"),
	RECEIVE_RECEIVE_VERTICAL_RESOLUTION("VerticalResolution",CrestronConstant.STREAM_RECEIVE_GROUP, false,CrestronConstant.STREAM_RECEIVE_GROUP, "VerticalResolution"),

	TRANSMIT_UUID("UUID",CrestronConstant.STREAM_TRANSMIT_GROUP, false,CrestronConstant.STREAM_TRANSMIT_GROUP, "UUID"),
	TRANSMIT_MODE("Mode",CrestronConstant.STREAM_TRANSMIT_GROUP, false,CrestronConstant.STREAM_TRANSMIT_GROUP, "TransportMode"),
	TRANSMIT_STREAM_LOCATION("StreamLocation",CrestronConstant.STREAM_TRANSMIT_GROUP, false,CrestronConstant.STREAM_TRANSMIT_GROUP, "StreamLocation"),
	TRANSMIT_MULTICAST_ADDRESS("MulticastAddress",CrestronConstant.STREAM_TRANSMIT_GROUP, false,CrestronConstant.STREAM_TRANSMIT_GROUP, "MulticastAddress"),
	TRANSMIT_RECEIVE_STATUS("Status",CrestronConstant.STREAM_TRANSMIT_GROUP, false,CrestronConstant.STREAM_TRANSMIT_GROUP, "Status"),
	TRANSMIT_RECEIVE_HORIZONTAL_RESOLUTION("HorizontalResolution",CrestronConstant.STREAM_TRANSMIT_GROUP, false,CrestronConstant.STREAM_TRANSMIT_GROUP, "HorizontalResolution"),
	TRANSMIT_RECEIVE_VERTICAL_RESOLUTION("VerticalResolution",CrestronConstant.STREAM_TRANSMIT_GROUP, false,CrestronConstant.STREAM_TRANSMIT_GROUP, "VerticalResolution"),

	SUB_UNIQUE_ID("UniqueId",CrestronConstant.STREAM_SUBSCRIPTION_GROUP, false,CrestronConstant.STREAM_SUBSCRIPTION_GROUP, "UniqueId"),
	SUB_SESSION_NAME("SessionName",CrestronConstant.STREAM_SUBSCRIPTION_GROUP, false,CrestronConstant.STREAM_SUBSCRIPTION_GROUP, "SessionName"),
	SUB_RTSP_ADDRESS("RTSPAddress",CrestronConstant.STREAM_SUBSCRIPTION_GROUP, false,CrestronConstant.STREAM_SUBSCRIPTION_GROUP, "RtspUri"),
	SUB_MULTICAST_ADDRESS("MulticastAddress",CrestronConstant.STREAM_SUBSCRIPTION_GROUP, false,CrestronConstant.STREAM_SUBSCRIPTION_GROUP, "MulticastAddress"),
	SUB_RESOLUTION("Resolution",CrestronConstant.STREAM_SUBSCRIPTION_GROUP, false,CrestronConstant.STREAM_SUBSCRIPTION_GROUP, "Resolution"),
	SUB_AUDIO_FORMAT("AudioFormat",CrestronConstant.STREAM_SUBSCRIPTION_GROUP, false,CrestronConstant.STREAM_SUBSCRIPTION_GROUP, "AudioFormat"),
	SUB_BITRATE("Bitrate",CrestronConstant.STREAM_SUBSCRIPTION_GROUP, false,CrestronConstant.STREAM_SUBSCRIPTION_GROUP, "Bitrate"),

	AVAILABLE_UNIQUE_ID("UniqueId",CrestronConstant.STREAM_AVAILABLE_GROUP,false,CrestronConstant.STREAM_AVAILABLE_GROUP, "UniqueId"),
	AVAILABLE_SESSION_NAME("SessionName",CrestronConstant.STREAM_AVAILABLE_GROUP,false,CrestronConstant.STREAM_AVAILABLE_GROUP, "SessionName"),
	AVAILABLE_RTSP_ADDRESS("RTSPAddress",CrestronConstant.STREAM_AVAILABLE_GROUP,false,CrestronConstant.STREAM_AVAILABLE_GROUP, "RtspUri"),
	AVAILABLE_MULTICAST_ADDRESS("MulticastAddress",CrestronConstant.STREAM_AVAILABLE_GROUP,false,CrestronConstant.STREAM_AVAILABLE_GROUP, "MulticastAddress"),
	AVAILABLE_RESOLUTION("Resolution",CrestronConstant.STREAM_AVAILABLE_GROUP,false,CrestronConstant.STREAM_AVAILABLE_GROUP, "Resolution"),
	AVAILABLE_AUDIO_FORMAT("AudioFormat",CrestronConstant.STREAM_AVAILABLE_GROUP,false,CrestronConstant.STREAM_AVAILABLE_GROUP, "AudioFormat"),
	AVAILABLE_BITRATE("Bitrate",CrestronConstant.STREAM_AVAILABLE_GROUP,false,CrestronConstant.STREAM_AVAILABLE_GROUP, "Bitrate"),

	INPUT_ROUTING_UNIQUE_ID("UniqueId",CrestronConstant.INPUT_ROUTING_GROUP, false,CrestronConstant.INPUT_ROUTING_GROUP, "UniqueId"),
	AUTOMATIC_STREAM_ROUTING("AutomaticInputRouting",CrestronConstant.INPUT_ROUTING_GROUP, false,CrestronConstant.INPUT_ROUTING_GROUP, "AutomaticStreamRoutingEnabled"),
	AUDIO_SOURCE("AudioSource",CrestronConstant.INPUT_ROUTING_GROUP, false,CrestronConstant.INPUT_ROUTING_GROUP, "AudioSource"),
	VIDEO_SOURCE("VideoSource",CrestronConstant.INPUT_ROUTING_GROUP, false,CrestronConstant.INPUT_ROUTING_GROUP, "VideoSource");

	private String name;
	private String group;
	private boolean isControl;
	private String apiGroupName;
	private String apiPropertyName;

	CrestronPropertyList(String name, String group, boolean isControl,String apiGroupName, String apiPropertyName) {
		this.name = name;
		this.group = group;
		this.isControl = isControl;
		this.apiGroupName = apiGroupName;
		this.apiPropertyName = apiPropertyName;
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
