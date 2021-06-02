package com.sdu.broker.pojo.req;

public class DownloadFile {
    private String name;
    private byte[] bytes;

    public DownloadFile() {
    }

    public DownloadFile(String name, byte[] bytes) {
        this.name = name;
        this.bytes = bytes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
