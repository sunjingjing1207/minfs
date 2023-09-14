package com.ksyun;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class MetaServerInfo implements Comparable, Serializable {
    private String IP;
    private Integer port;
    private Long mTime;

    @Override
    public int compareTo(Object o) {
        MetaServerInfo metaServerInfo = (MetaServerInfo) o;
        return this.port-metaServerInfo.getPort();
    }
}
