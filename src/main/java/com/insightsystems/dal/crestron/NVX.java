package com.insightsystems.dal.crestron;

import com.avispl.symphony.api.common.error.NotAuthorizedException;
import com.avispl.symphony.api.dal.control.Controller;
import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty;
import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.dto.monitor.Statistics;
import com.avispl.symphony.api.dal.error.CommandFailureException;
import com.avispl.symphony.api.dal.monitor.Monitorable;
import com.avispl.symphony.dal.communicator.RestCommunicator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


import java.util.*;


import static com.insightsystems.dal.crestron.NVX_Constants.*;

/**
 * Device Adapter for Crestron NVX Devices
 * Company: Insight Systems
 * @author Jayden (JaydenL-Insight)
 * @version 0.3
 * @see <a href="https://sdkcon78221.crestron.com/sdk/DM_NVX_REST_API/Content/Topics/Home.htm">NVX REST API Docs</a>
 */
public class NVX extends RestCommunicator implements Monitorable, Controller{
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public NVX(){
        this.setAuthenticationScheme(AuthenticationScheme.None);
        this.setContentType("application/json");
        this.setTrustAllCertificates(true);
    }

    @Override
    protected void authenticate() throws Exception {
        try {
            this.doPost("userlogin.html", "login=" + this.getLogin() + "&passwd=" + this.getPassword());
        } catch (CommandFailureException e){
            if (e.getStatusCode() == 403){
                throw new NotAuthorizedException("Username and password combination is invalid",e);
            }
            throw e;
        }
    }

    @Override
    public List<Statistics> getMultipleStatistics() throws Exception {
        ExtendedStatistics extStats = new ExtendedStatistics();
        Map<String,String> stats = new HashMap<>();
        List<AdvancedControllableProperty> controls = new ArrayList<>();

        JsonNode deviceResponse;
        try {
            deviceResponse = objectMapper.readTree(this.doGet("/Device"));
        } catch(Exception e){ //Retry after authentication
            this.authenticate();
            deviceResponse = objectMapper.readTree(this.doGet("/Device"));
        }

        putAllStatistics(stats,deviceResponse.at("/Device/DeviceInfo"),"DeviceInfo#",deviceInfo);
        getPreviewInfo(stats,controls,deviceResponse);
        getDeviceConfig(stats,controls,deviceResponse);
        getPortInfo(stats,controls,deviceResponse);
        getStreamInfo(stats,controls,deviceResponse);

        String model = deviceResponse.at("/Device/DeviceInfo/Model").asText();
        if (!model.startsWith("DM-NVX-D") && !model.startsWith("DM-NVX-E")){ //If model is not a static encoder or decoder
            createDropdownControl(stats,controls,"DeviceMode",deviceModes,deviceModes,deviceResponse.at("/Device/DeviceSpecific/DeviceMode").asText());
        } else {
            stats.put("DeviceMode",deviceResponse.at("/Device/DeviceSpecific/DeviceMode").asText());
        }

        extStats.setStatistics(stats);
        extStats.setControllableProperties(controls);
        return Collections.singletonList(extStats);
    }

    private void getStreamInfo(Map<String, String> stats, List<AdvancedControllableProperty> controls, JsonNode json) {
        String mode = json.at("/Device/DeviceSpecific/DeviceMode").asText();

        if (mode.equals("Receiver")) {
            ArrayNode receiveStreams = (ArrayNode) json.at("/Device/StreamReceive/Streams");
            for (int i = 0; i < receiveStreams.size(); i++) {
                JsonNode stream = receiveStreams.get(i);
                String prefix = "Receive Streams#Stream" + i;

                putAllStatistics(stats, stream, prefix, receiveStreamStats); //Add all stats we want from this section
                createSwitchControl(stats, controls, prefix + "AutoInitiation", "Disabled", "Enabled", stream.at("/IsAutomaticInitiationEnabled").asBoolean());
                createStringControl(stats, controls, prefix + "StreamLocation", stream.at("/StreamLocation").asText());
                createStringControl(stats, controls, prefix + "StreamName", stream.at("/StreamName").asText());

                //Stream Telemetry
                boolean statsEnabled = stream.at("/IsStatisticsEnabled").asBoolean();
                createSwitchControl(stats, controls, prefix + "StatisticsEnabled", "Disabled", "Enabled", statsEnabled);
                if (statsEnabled) {
                    putAllStatistics(stats, stream, prefix, receiveStreamTelemetry);
                    createButton(stats, controls, prefix + "ResetStatistics", "ResetStatistics", "Resetting..", 5_000L);
                }


                String resolution = stream.at("/HorizontalResolution").asText() + "x" + stream.at("/VerticalResolution") + "@" + stream.at("/FramesPerSecond").asText();
                String audioMode = stream.at("/AudioMode").asText() + " " + stream.at("/AudioChannels").asText() + "CH " + stream.at("/AudioFormat").asText();

                stats.put(prefix + "Resolution", resolution);
                stats.put(prefix + "Audio", audioMode);
            }
        } else if (mode.equals("Transmitter")) {
            ArrayNode transmitStreams = (ArrayNode) json.at("/Device/StreamTransmit/Streams");
            for (int i = 0; i < transmitStreams.size(); i++) {
                JsonNode stream = transmitStreams.get(i);
                String prefix = "Transmit Streams#Stream" + i;

                putAllStatistics(stats, stream, prefix, transmitStreamStats);

                String resolution = stream.at("/HorizontalResolution").asText() + "x" + stream.at("/VerticalResolution") + "@" + stream.at("/FramesPerSecond").asText();
                String audioMode = stream.at("/AudioMode").asText() + " " + stream.at("/AudioChannels").asText() + "CH " + stream.at("/AudioFormat").asText();

                stats.put(prefix + "Resolution", resolution);
                stats.put(prefix + "Audio", audioMode);
            }
        }
    }

    private void getPortInfo(Map<String, String> stats, List<AdvancedControllableProperty> controls, JsonNode json) {
        String mode = json.at("/Device/DeviceSpecific/DeviceMode").asText();
        int hdmiCount = 1;

        if (mode.equals("Transmitter")) {
            ArrayNode inputs = (ArrayNode) json.at("/Device/AudioVideoInputOutput/Inputs");
            for (int i = 0; i < inputs.size(); i++) {
                JsonNode port = inputs.get(i).at("/Ports").get(0);
                String inputName = port.at("/PortType").asText().equalsIgnoreCase("HDMI") ? "Hdmi " + hdmiCount++ + " Input" : port.at("/PortType").asText() + " Input";
                stats.put(inputName + "#SourceDetected", port.at("/IsSourceDetected").asText());
                stats.put(inputName + "#SyncDetected", port.at("/IsSyncDetected").asText());
                stats.put(inputName + "#Resolution", port.at("/HorizontalResolution").asText() + "x" + port.at("/VerticalResolution").asText() + "@" + port.at("/FramesPerSecond").asText() + " " + port.at("/Audio/Digital/Format").asText());
                stats.put(inputName + "#HdcpSupportEnabled", port.at("/Hdmi/IsHdcpSupportEnabled").asText());
                stats.put(inputName + "#SourceHdcpActive", port.at("/Hdmi/IsSourceHdcpActive").asText());
                stats.put(inputName + "#HdcpState", port.at("/Hdmi/HdcpState").asText());
            }

        } else if (mode.equals("Receiver")) {
            ArrayNode outputs = (ArrayNode) json.at("/Device/AudioVideoInputOutput/Outputs");
            for (int i = 0; i < outputs.size(); i++) {
                JsonNode port = outputs.get(i).at("/Ports").get(0);

                // <portType>[count]Output#
                //Eg. Hdmi1Output#
                String outputName = port.at("/PortType").asText().equalsIgnoreCase("HDMI") ? "Hdmi " + hdmiCount++ + " Output" : port.at("/PortType").asText() + " Output#";
                if (port.at("/Hdmi/IsOutputDisabled").asBoolean()) {
                    stats.put(outputName + "Disabled", "true");
                } else {
                    stats.put(outputName + "SinkConnected", port.at("/IsSinkConnected").asText());
                    stats.put(outputName + "HdcpForceDisabled", port.at("/Hdmi/IsHdcpForceDisabled").asText());
                    stats.put(outputName + "Resolution", port.at("/HorizontalResolution").asText() + "x" + port.at("/VerticalResolution").asText() + "@" + port.at("/FramesPerSecond").asText() + " " + port.at("/Audio/Digital/Format").asText());
                    stats.put(outputName + "DisabledByHdcp", port.at("/Hdmi/DisabledByHdcp").asText());
                    stats.put(outputName + "HdcpTransmitterMode", port.at("/Hdmi/HdcpTransmitterMode").asText());
                    stats.put(outputName + "HdcpState", port.at("/Hdmi/HdcpState").asText());
                }
            }
        } else if (this.logger.isWarnEnabled()) {
            this.logger.warn("Unknown device operating mode \"" + mode + "\", skipping port statistics.");
        }
    }

    private void createStringControl(Map<String, String> stats, List<AdvancedControllableProperty> controls, String name, String state) {
        AdvancedControllableProperty.Text text = new AdvancedControllableProperty.Text();
        stats.put(name,state);
        controls.add(new AdvancedControllableProperty(name,new Date(),text,state));
    }

    private void getDeviceConfig(Map<String, String> stats, List<AdvancedControllableProperty> controls, JsonNode json) {
        String prefix = "DeviceConfig#";

        createButton(stats,controls, "Reboot","Reboot","Rebooting..",30_000L);
        createButton(stats,controls, "RestartWebserver","RestartWebserver","Restarting..",30_000L);

        JsonNode devSpecific = json.at("/Device/DeviceSpecific"); //Add source dropdowns
        createDropdownControl(stats,controls,prefix+"AudioSource",audioSources,audioSources,devSpecific.at("/AudioSource").asText());
        createDropdownControl(stats,controls,prefix+"VideoSource",videoSources,videoSources,devSpecific.at("/VideoSource").asText());
        createDropdownControl(stats,controls,prefix+"NaxAudioSource",naxAudioSources,naxAudioSources,devSpecific.at("/NaxAudioSource").asText());
        createDropdownControl(stats,controls,prefix+"AudioMode",audioModes,audioModes,devSpecific.at("/AudioMode").asText());

        JsonNode routeControl = json.at("/Device/AvRouting/RouteControl/");
        createSwitchControl(stats,controls,prefix+"ChangeUsbRemoteDevice","Disabled","Enabled",routeControl.at("/IsChangeUsbRemoteDeviceEnabled").asBoolean());
        createSwitchControl(stats,controls,prefix+"NetworkLayer3","Disabled","Enabled",routeControl.at("/IsLayer3Enabled").asBoolean());
        createSwitchControl(stats,controls,prefix+"SecondaryAudioFollowVideo","Disabled","Enabled",routeControl.at("/IsSecondaryAudioFollowsVideoEnabled").asBoolean());
        createSwitchControl(stats,controls,prefix+"UsbFollowsVideo","Disabled","Enabled",routeControl.at("/IsUsbFollowsVideoEnabled").asBoolean());

        //Timezone
        JsonNode timezones = json.at("/Device/UserInterfaceConfig/DeviceSupport/TimeZones");
        String[] options = new String[timezones.size()];
        String[] labels = new String[timezones.size()];

        int index = 0;
        Iterator<Map.Entry<String, JsonNode>> entries = timezones.fields();
        while(entries.hasNext()){
            Map.Entry<String, JsonNode> entry = entries.next();
            options[index] = entry.getKey();
            labels[index] = entry.getValue().asText();
        }
        createDropdownControl(stats,controls,prefix+"Timezone",options,labels,json.at("/Device/SystemClock/TimeZone").asText());
    }

    private static void createButton(Map<String, String> stats, List<AdvancedControllableProperty> controls, String name, String label, String labelPressed, long gracePeriod) {
        AdvancedControllableProperty.Button button = new AdvancedControllableProperty.Button();
        button.setLabel(label);
        button.setLabelPressed(labelPressed);
        button.setGracePeriod(gracePeriod);

        controls.add(new AdvancedControllableProperty(name,new Date(),button,"0"));
    }

    private void createDropdownControl(Map<String, String> stats, List<AdvancedControllableProperty> controls, String name, String[] options, String[] labels, String selectedOption) {
        AdvancedControllableProperty.DropDown drop = new AdvancedControllableProperty.DropDown();
        drop.setLabels(labels);
        drop.setOptions(options);

        stats.put(name,selectedOption);
        controls.add(new AdvancedControllableProperty(name,new Date(),drop,selectedOption));

    }

    private void getPreviewInfo(Map<String, String> stats, List<AdvancedControllableProperty> controls, JsonNode deviceResponse) {
        JsonNode preview = deviceResponse.at("/Device/Preview");
        String prefix = "Preview#";
        boolean enabled = preview.at("/IsPreviewOutputEnabled").asBoolean();

        createSwitchControl(stats,controls,prefix+"PreviewEnabled","offLabel","onLabel",true);

        if (!enabled) return; //Continue only if Preview is enabled

        stats.put(prefix + "StatusMessage", preview.at("/StatusMessage").asText());

        JsonNode images = preview.at("/ImageList"); //Get highest available quality preview image url
        if (images.has("Image3") && images.at("/Image3/IsImageAvailable").asBoolean(false))
            stats.put(prefix + "ImageUrl",images.at("/Image3/IPv4Path").asText());
         else if (images.has("Image2") && images.at("/Image2/IsImageAvailable").asBoolean(false))
            stats.put(prefix + "ImageUrl",images.at("/Image2/IPv4Path").asText());
         else if (images.has("Image1") && images.at("/Image1/IsImageAvailable").asBoolean(false))
            stats.put(prefix + "ImageUrl",images.at("/Image1/IPv4Path").asText());
         else
            stats.put(prefix + "ImageUrl","");
    }

    private static void createSwitchControl(Map<String, String> stats, List<AdvancedControllableProperty> controls, String name, String offLabel, String onLabel, boolean state) {
        AdvancedControllableProperty.Switch toggle = new AdvancedControllableProperty.Switch();
        toggle.setLabelOff(offLabel);
        toggle.setLabelOn(onLabel);

        stats.put(name,state?"1":"0");
        controls.add(new AdvancedControllableProperty(name,new Date(),toggle,state?"1":"0"));
    }

    /**
     * Add a list of statistics by their Json key, each with the string prefix
     * @param stats Statistics Map to add statistics to
     * @param json JsonNode which is a direct parent of the required statistics
     * @param prefix Name prefix to append to the start of each statistic
     * @param statNameArray Array of JsonNode nodes to retrieve text value of
     */
    private static void putAllStatistics(Map<String, String> stats, JsonNode json, String prefix, String[] statNameArray) {
        for (String stat : statNameArray)
            stats.put(prefix+stat,json.at("/"+stat).asText());
    }

    private static <T> String createControlObject(String propertyPath, Object value, Class<T> type) throws RuntimeException, JsonProcessingException {
        ObjectNode node = objectMapper.createObjectNode();
        String[] path = propertyPath.split("/");

        if (type.equals(String.class)){
            node.put(path[path.length - 1], (String) value);
        } else if (type.equals(boolean.class)){
            node.put(path[path.length - 1], (boolean) value);
        } else if (type.equals(int.class)){
            node.put(path[path.length - 1], (int) value);
        }else{
            throw new RuntimeException("Type: "+type.getSimpleName()+" is not a supported json value");
        }

        //Create Json structure from value node to root
        for (int i=path.length-2; i >=0; i--){
            node = objectMapper.createObjectNode().set(path[i],node);
        }

        return objectMapper.writeValueAsString(node);
    }

    @Override
    public void controlProperty(ControllableProperty cp) throws Exception {
        String prop = cp.getProperty();
        String value = String.valueOf(cp.getValue());

        if (prop.equals("DeviceMode")){

        } else if (prop.startsWith("Transmit Streams#Stream")){
            char streamNum = prop.charAt(23);
            System.out.println("Stream number is " + streamNum);
            //Check if the stream is ready to receive a command
            JsonNode readyResponse = objectMapper.readTree(this.doGet("/Device/StreamTransmit/Streams/"+streamNum));
            if(!readyResponse.at("/Device/StreamTransmit/Streams/"+streamNum+"/Processing").asBoolean()){ //If device is not processing
                //Todo- Handle all Transmit Stream commands here
                return;
            } else {
                if (this.logger.isDebugEnabled())
                    this.logger.debug("Stream is not ready to receive commands");
                throw new RuntimeException("Stream is not ready to receive commands");
            }
        } else if (prop.startsWith("Receive Streams#Stream")){
            char streamNum = prop.charAt(22);

            //Check if the stream is ready to receive a command
            JsonNode readyResponse = objectMapper.readTree(this.doGet("/Device/StreamReceive/Streams/"+streamNum));
            if(!readyResponse.at("/Device/StreamReceive/Streams/"+streamNum+"/Processing").asBoolean()){ //If device is not processing
                //Todo- Handle all Receive Stream commands here
                return;
            } else {
                if (this.logger.isDebugEnabled())
                    this.logger.debug("Stream is not ready to receive commands");
                throw new RuntimeException("Stream is not ready to receive commands");
            }
        }
    }

    @Override
    public void controlProperties(List<ControllableProperty> list) throws Exception {
        for(ControllableProperty cp : list)
            this.controlProperty(cp);
    }

    public static void main(String[] args) throws Exception {
        NVX device = new NVX();
        device.setProtocol("https");
        device.setHost("10.198.79.243");
        device.setLogin("admin");
        device.setPassword("admin");
        device.init();
        Map<String,String> stats  = ((ExtendedStatistics)device.getMultipleStatistics().get(0)).getStatistics();
        stats.forEach((k,v)-> System.out.println(k+" : "+v));
        System.out.println("Total of "+stats.size()+" stats.");
    }
}
