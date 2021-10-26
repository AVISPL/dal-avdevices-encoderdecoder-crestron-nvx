package com.insightsystems.dal.crestron;

public class NVX_Constants {
    protected static final String[] deviceInfo = new String[]{
        "BuildDate",
        "DeviceVersion",
        "Model",
        "Name",
        "RebootReason",
        "SerialNumber"
    };
    protected static final String[] receiveStreamStats = new String[]{
        "Status",
        "Bitrate",
        "Buffer",
        "CodecReady",
        "ElapsedSeconds",
        "HdcpTransmitterMode",
        "InitiatorAddress",
        "IsAutomaticInitiationEnabled",
        "IsPasswordProtectionEnabled",
        "MulticastAddress",
        "Pause",
        "RtspPort",
        "SessionInitiation",
        "StreamLocation",
        "StreamProfile",
        "StreamType",
        "TcpMode",
        "TransportMode",
        "Volume"
    };
    protected static final String[] transmitStreamStats = new String[]{
        "RtspSessionName",
        "RtspStreamFileName",
        "Bitrate",
        "ActiveBitrate",
        "Status",
        "ElapsedSeconds",
        "MulticastAddress",
        "IsPasswordProtectionEnabled",
        "Pause",
        "SessionInitiation",
        "Start",
        "Stop",
        "StreamLocation",
        "StreamProfile",
        "TransportMode",
        "RtspPort",
        "NumVideoPacketsRcvd",
        "NumAudioPacketsRcvd",
        "IsAdaptiveBitrateMode"
    };

    protected static final String[] receiveStreamTelemetry = new String[]{"NumAudioPacketsDropped","NumAudioPacketsRcvd","NumVideoPacketsDropped","NumVideoPacketsRcvd"};


    protected static final String[] videoSources = new String[]{"None", "Input1","Input2","Stream"};
    protected static final String[] audioSources = new String[]{"AudioFollowsVideo", "Input1", "Input2", "AnalogAudio", "PrimaryStreamAudio", "SecondaryStreamAudio"};
    protected static final String[] naxAudioSources = new String[]{"None", "Auto", "Input1", "Input2", "Analog", "PrimaryAudio", "SecondaryAudio", "DanteAES67"};
    protected static final String[] audioModes = new String[]{"Insert", "Extract"};
    protected static final String[] deviceModes = new String[]{"Transmitter","Receiver"};
}
