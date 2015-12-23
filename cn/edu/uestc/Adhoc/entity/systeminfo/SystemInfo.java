package cn.edu.uestc.Adhoc.entity.systeminfo;

import cn.edu.uestc.Adhoc.utils.MessageUtils;

/**
 * Created by walter on 15-12-3.
 */
public class SystemInfo {
    private int processorCount;
    private int memorySize;
//    private  String os;
//
//    public String getOs() {
//        return os;
//    }
//
//    public void setOs(String os) {
//        this.os = os;
//    }

    public SystemInfo() {
    }

    public SystemInfo(int processorCount, int memorySize) {
        this.processorCount = processorCount;
        this.memorySize = memorySize;
    }

    public int getProcessorCount() {
        return processorCount;
    }

    public void setProcessorCount(int processorCount) {
        this.processorCount = processorCount;
    }

    public int getMemorySize() {
        return memorySize;
    }

    public void setMemorySize(int memorySize) {
        this.memorySize = memorySize;
    }

    public byte[] getBytes() {
        byte[] sysInfo = new byte[3];
        sysInfo[0] = (byte) processorCount;
        sysInfo[1] = MessageUtils.IntToBytes(memorySize)[0];
        sysInfo[2] = MessageUtils.IntToBytes(memorySize)[1];
        return sysInfo;
    }

    public static SystemInfo recoverSysInfo(byte[] bytes) {
        return new SystemInfo(bytes[0], MessageUtils.BytesToInt(new byte[]{bytes[1],bytes[2]}));
    }

    @Override
    public String toString() {
        return "处理器个数:"
                + processorCount +
                ", 内存大小:" + memorySize;
    }
}
