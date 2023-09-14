package com.ksyun;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataServerInfo implements Comparable, Serializable {
        private String host;
        private int port;
        private int fileTotal;
        private int capacity;    //单位B
        private int useCapacity;
        private long mTime;

        @Override
        public int compareTo(Object o) {
                DataServerInfo metaServerInfo = (DataServerInfo) o;
                return this.useCapacity-metaServerInfo.getUseCapacity();
        }
}
