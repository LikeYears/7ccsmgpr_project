package com.oasis.onebox.entity;

import com.oasis.onebox.service.FileService;
import com.oasis.onebox.tool.EncodeTool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class CloudFile {
    private boolean isDir;// check if is directory
    private String fileType = "";
    private String fileName;
    private String base64FilePath;// base64 of file path
    private long fileSize;// bytes
    private String describeFileSize = "";
    private String lastModifiedTime;
    private boolean isPlayOnline;
    private boolean isCanPreview;

    public CloudFile(String mainDir, Path fileArg, BasicFileAttributes attrs) throws IOException {
        mainDir = mainDir.replace("\\", "/");
        String relativeFilePath = fileArg.toAbsolutePath().toString().replace("\\", "/").replaceFirst(mainDir, "");
        base64FilePath = EncodeTool.encoderURLBASE64((relativeFilePath).getBytes());
        fileName = fileArg.getFileName().toString();
        lastModifiedTime = EncodeTool.FORMMATTER.format(attrs.lastModifiedTime().toMillis());
        if (isDir = attrs.isDirectory()) {
            fileType = "folder";
        } else {
            int indexOf = fileName.lastIndexOf(".");
            if (indexOf > -1) {
                fileType = fileName.substring(indexOf + 1).toLowerCase();
            }
            isPlayOnline = FileService.isCanPlayOnline(fileType);
            isCanPreview = FileService.isCanPreview(fileType);
            fileSize = Files.size(fileArg);
            describeFileSize = FileService.calculateDescSize(fileSize);
        }
    }

    public boolean isDir() {
        return isDir;
    }

    public String getFileType() {
        return fileType;
    }

    public String getFileName() {
        return fileName;
    }

    public String getBase64FilePath() {
        return base64FilePath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public String getDescribeFileSize() {
        return describeFileSize;
    }

    public String getLastModifiedTime() {
        return lastModifiedTime;
    }

    public boolean isPlayOnline() {
        return isPlayOnline;
    }

    public boolean isCanPreview() {
        return isCanPreview;
    }
}
