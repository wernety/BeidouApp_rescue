package com.beidouapp.model.messages;

import java.util.List;

public class HeartbeatMsg {
    private sysProperty sysProperty;
    private appProperty appProperty;
    private body body;

    public HeartbeatMsg(sysProperty sysProperty, appProperty appProperty, body body) {
        this.sysProperty = sysProperty;
        this.appProperty = appProperty;
        this.body = body;
    }

    public void setSysProperty(sysProperty sysProperty) {
        this.sysProperty = sysProperty;
    }
    public sysProperty getSysProperty() {
        return sysProperty;
    }

    public void setAppProperty(appProperty appProperty) {
        this.appProperty = appProperty;
    }
    public appProperty getAppProperty() {
        return appProperty;
    }

    public void setBody(body body) {
        this.body = body;
    }
    public body getBody() {
        return body;
    }

    public static class sysProperty {
        private String messageType;
        private String productId;

        public sysProperty(String messageType, String productId) {
            this.messageType = messageType;
            this.productId = productId;
        }

        public void setMessageType(String messageType) {
            this.messageType = messageType;
        }
        public String getMessageType() {
            return messageType;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }
        public String getProductId() {
            return productId;
        }
    }

    public static class appProperty {
        private String deviceId;
        private Long dataTimestamp;
        private List<String> dataStreams;

        public appProperty(String deviceId, Long dataTimestamp) {
            this.deviceId = deviceId;
            this.dataTimestamp = dataTimestamp;
        }

        public appProperty(String deviceId, Long dataTimestamp, List<String> dataStreams) {
            this.deviceId = deviceId;
            this.dataTimestamp = dataTimestamp;
            this.dataStreams = dataStreams;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }
        public String getDeviceId() {
            return deviceId;
        }

        public void setDataTimestamp(Long dataTimestamp) {
            this.dataTimestamp = dataTimestamp;
        }
        public Long getDataTimestamp() {
            return dataTimestamp;
        }

        public void setDataStreams(List<String> dataStreams) {
            this.dataStreams = dataStreams;
        }
        public List<String> getDataStreams() {
            return dataStreams;
        }
    }

    public static class body {
        private String event;
        private String eventData;  //前两个是状态包
        private List<position> position;
        private List<alarm> alarm;  //后两个是

        public body(String event, String eventData) {
            this.event = event;
            this.eventData = eventData;
        }
        public body(List<position> position, List<alarm> alarm) {
            this.position = position;
            this.alarm = alarm;
        }

        public void setEvent(String event) {
            this.event = event;
        }
        public String getEvent() {
            return event;
        }

        public void setEventData(String eventData) {
            this.eventData = eventData;
        }
        public String getEventData() {
            return eventData;
        }

        public void setPosition(List<position> position) {
            this.position = position;
        }
        public List<position> getPosition() {
            return position;
        }

        public void setAlarm(List<alarm> alarm) {
            this.alarm = alarm;
        }
        public List<alarm> getAlarm() {
            return alarm;
        }
    }

    public static class position {
        private String lng;
        private String lat;
        private String lngDir;
        private String latDir;
        private boolean valid;
        private double height;
        private double directory;
        private double speed;
        private Long dataTimestamp;

        public position(String lng, String lat, String lngDir, String latDir, boolean valid,
                        double height, double directory, double speed, Long dataTimestamp) {
            this.lng = lng;
            this.lat = lat;
            this.lngDir = lngDir;
            this.latDir = latDir;
            this.valid = valid;
            this.height = height;
            this.directory = directory;
            this.speed = speed;
            this.dataTimestamp = dataTimestamp;
        }

        public void setLng(String lng) {
            this.lng = lng;
        }
        public String getLng() {
            return lng;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }
        public String getLat() {
            return lat;
        }

        public void setLngDir(String lngDir) {
            this.lngDir = lngDir;
        }
        public String getLngDir() {
            return lngDir;
        }

        public void setLatDir(String latDir) {
            this.latDir = latDir;
        }
        public String getLatDir() {
            return latDir;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }
        public boolean isValid() {
            return valid;
        }

        public void setHeight(double height) {
            this.height = height;
        }
        public double getHeight() {
            return height;
        }

        public void setDirectory(double directory) {
            this.directory = directory;
        }
        public double getDirectory() {
            return directory;
        }

        public void setSpeed(double speed) {
            this.speed = speed;
        }
        public double getSpeed() {
            return speed;
        }

        public void setDataTimestamp(Long dataTimestamp) {
            this.dataTimestamp = dataTimestamp;
        }
        public Long getDataTimestamp() {
            return dataTimestamp;
        }

    }

    public static class alarm {
        private String alarmType;
        private String alarmMsg;
        private Long dataTimestamp;

        public alarm (String alarmType, String alarmMsg, Long dataTimestamp) {
            this.alarmType = alarmType;
            this.alarmMsg = alarmMsg;
            this.dataTimestamp = dataTimestamp;
        }

        public void setAlarmType(String alarmType) {
            this.alarmType = alarmType;
        }
        public String getAlarmType() {
            return alarmType;
        }

        public void setAlarmMsg(String alarmMsg) {
            this.alarmMsg = alarmMsg;
        }
        public String getAlarmMsg() {
            return alarmMsg;
        }

        public void setDataTimestamp(Long dataTimestamp) {
            this.dataTimestamp = dataTimestamp;
        }
        public Long getDataTimestamp() {
            return dataTimestamp;
        }

    }
}

