package com.oasis.onebox.entity;

import com.oasis.onebox.exception.CustomException;
import com.oasis.onebox.service.ShareFileService;
import com.oasis.onebox.tool.EncodeTool;
import com.oasis.onebox.tool.FieldAlias;
import com.oasis.onebox.tool.StringTool;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

public class FileShare {

    @FieldAlias
    private String id;// key id
    @FieldAlias
    private String owner;// file owner
    @FieldAlias(alias = "filename")
    private String fileName;
    @FieldAlias(alias = "filetype")
    private String fileType = "";
    @FieldAlias
    private String password;// password for share file
    @FieldAlias(alias = "filepath")
    private String filePath;
    @FieldAlias(alias = "sharedate")
    private long shareDate;// millisecond
    @FieldAlias(alias = "downloadtimes")
    private int downloadTimes;

    public FileShare(User loginUser, String filepath) throws Exception {
        this.id = StringTool.getUUID();
        this.shareDate = new Date().getTime();
        this.filePath = filepath;
        this.owner = loginUser.getUsername();
        this.password = ShareFileService.generatePassword();
        Path file = Paths.get(loginUser.getDirectory(), filepath);
        if (!Files.exists(file, new LinkOption[]{LinkOption.NOFOLLOW_LINKS})) {
            throw new CustomException(400, "The file not exist", null);
        }
        fileName = file.getFileName().toString();
        if (Files.isDirectory(file, new LinkOption[]{LinkOption.NOFOLLOW_LINKS})) {
            this.fileType = "folder";
        } else {
            int indexOf = fileName.lastIndexOf(".");
            if (indexOf > -1) {
                this.fileType = fileName.substring(indexOf + 1).toLowerCase();
            }
        }
    }

    public FileShare() {

    }

    public FileShare(User loginUser) {
        this.owner = loginUser.getUsername();
    }

    public String getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public String getOwner() {
        return owner;
    }

    public String getPassword() {
        return password;
    }

    public String getFilePath() {
        return filePath;
    }

    public long getShareDate() {
        return shareDate;
    }

    public int getDownloadTimes() {
        return downloadTimes;
    }

    public String getDiscShareDate() {
        return EncodeTool.FORMMATTER.format(new Date(shareDate));
    }
}
