package cn.edu.uestc.Adhoc.entity;

import java.io.Serializable;

/**
 * Created by walter on 15-12-3.
 */
public class SystemInfo implements Serializable{
    static  final long seriaVersionUID = 42L;
    private int processorCount;
    private long memorySize;

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
}
