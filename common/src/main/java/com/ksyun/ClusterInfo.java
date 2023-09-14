package com.ksyun;

import java.util.ArrayList;
import java.util.List;

public class ClusterInfo {
    private MetaServerMsg masterMetaServer;
    private MetaServerMsg slaveMetaServer;
    private List<DataServerMsg> dataServer;

    public MetaServerMsg getMasterMetaServer() {
        return masterMetaServer;
    }

    public void setMasterMetaServer(List<MetaServerInfo> list) {
        if (list.size() == 0) {
            this.masterMetaServer = null;
            return;
        }
        MetaServerMsg masterMetaServer = new MetaServerMsg();
        MetaServerInfo metaInfo = list.get(0);
        masterMetaServer.setHost(metaInfo.getIP());
        masterMetaServer.setPort(metaInfo.getPort());
        this.masterMetaServer = masterMetaServer;
    }

    public MetaServerMsg getSlaveMetaServer() {
        return slaveMetaServer;
    }

    public void setSlaveMetaServer(List<MetaServerInfo> list) {
        if (list.size() <= 1) {
            this.slaveMetaServer = null;
            return;
        }
        MetaServerMsg slaveMetaServer = new MetaServerMsg();
        MetaServerInfo metaInfo = list.get(1);
        slaveMetaServer.setHost(metaInfo.getIP());
        slaveMetaServer.setPort(metaInfo.getPort());
        this.slaveMetaServer = slaveMetaServer;
    }

    public List<DataServerMsg> getDataServer() {
        return dataServer;
    }

    public void setDataServer(List<DataServerInfo> list) {
        List<DataServerMsg> dataServer = new ArrayList<>();
        for (DataServerInfo d : list) {
            dataServer.add(new DataServerMsg(d.getHost(), d.getPort(), d.getFileTotal(), d.getCapacity(), d.getUseCapacity()));
        }
        this.dataServer = dataServer;
    }

    public class MetaServerMsg {
        private String host;
        private int port;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }
    }

    public class DataServerMsg {
        private String host;
        private int port;
        private int fileTotal;
        private int capacity;
        private int useCapacity;

        public DataServerMsg(String host, int port, int fileTotal, int capacity, int useCapacity) {
            this.host = host;
            this.port = port;
            this.fileTotal = fileTotal;
            this.capacity = capacity;
            this.useCapacity = useCapacity;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public int getFileTotal() {
            return fileTotal;
        }

        public void setFileTotal(int fileTotal) {
            this.fileTotal = fileTotal;
        }

        public int getCapacity() {
            return capacity;
        }

        public void setCapacity(int capacity) {
            this.capacity = capacity;
        }

        public int getUseCapacity() {
            return useCapacity;
        }

        public void setUseCapacity(int useCapacity) {
            this.useCapacity = useCapacity;
        }
    }

    @Override
    public String toString() {
        return "ClusterInfo{" +
                "masterMetaServer=" + masterMetaServer +
                ", slaveMetaServer=" + slaveMetaServer +
                ", dataServer=" + dataServer +
                '}';
    }
}
