package com.oasis.onebox;

/**
 * Created by tehaoye on 2019/3/27.
 */

public class CloundFile {
    private boolean isDir;
    private String fileType = "";
    private String fileName;
    private String base64FilePath;
    private long fileSize;
    private String describeFileSize = "";
    private String lastModifiedTime;
    private boolean isPlayOnline;
    private boolean isCanPreview;
    private int image;

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public boolean isDir() {
        return isDir;
    }

    public void setDir(boolean dir) {
        isDir = dir;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getBase64FilePath() {
        return base64FilePath;
    }

    public void setBase64FilePath(String base64FilePath) {
        this.base64FilePath = base64FilePath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getDescribeFileSize() {
        return describeFileSize;
    }

    public void setDescribeFileSize(String describeFileSize) {
        this.describeFileSize = describeFileSize;
    }

    public String getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(String lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public boolean isPlayOnline() {
        return isPlayOnline;
    }

    public void setPlayOnline(boolean playOnline) {
        isPlayOnline = playOnline;
    }

    public boolean isCanPreview() {
        return isCanPreview;
    }

    public void setCanPreview(boolean canPreview) {
        isCanPreview = canPreview;
    }
}
