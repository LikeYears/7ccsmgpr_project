package com.oasis.onebox.download;

public enum DownloadEnum {
    //error
    ERROR(-1),
    //ready
    READY(0),
    //process
    PROCESS(1),
    //finish
    FINISH(2),
    //stop
    STOP(3);
    private int dlcode;

    DownloadEnum(int code) {
        this.dlcode = code;
    }

    @Override
    public String toString() {
        return String.valueOf(this.dlcode);
    }
}
