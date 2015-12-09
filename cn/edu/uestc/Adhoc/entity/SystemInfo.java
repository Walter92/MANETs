package cn.edu.uestc.Adhoc.entity;

import java.io.Serializable;

/**
 * Created by walter on 15-12-3.
 */
public class SystemInfo implements Serializable{
    static  final long seriaVersionUID = 42L;
    private int processorCount;
    private long memorySize;
//    private  String os;
//
//    public String getOs() {
//        return os;
//    }
//
//    public void setOs(String os) {
//        this.os = os;
//    }

    public int getProcessorCount() {
        return processorCount;
    }

    public void setProcessorCount(int processorCount) {
        this.processorCount = processorCount;
    }

    public long getMemorySize() {
        return memorySize;
    }

    public void setMemorySize(long memorySize) {
        this.memorySize = memorySize;
    }

    @Override
    public String toString() {
        return "处理器个数:"
                 + processorCount +
                ", 内存大小:" + memorySize;
    }
}
