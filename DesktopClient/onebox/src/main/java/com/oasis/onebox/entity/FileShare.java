package com.oasis.onebox.entity;

import com.oasis.onebox.exception.CustomException;
import com.oasis.onebox.service.ShareFileService;
import com.oasis.onebox.tool.EncodeTool;
import com.oasis.onebox.tool.StringTool;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.Date;

public class FileShare {


    private String id;// key id
    private String owner;// file owner
    private String filename;
    private String filetype;
    private String password;// password for share file
    private String filepath;
    private String sharedate;// millisecond
    private int downloadtimes;


    public FileShare(User loginUser, String filepath) throws Exception {
        this.id = StringTool.getUUID();
        this.sharedate = ""+new Date().getTime();
        this.filepath = filepath;
        this.owner = loginUser.getUsername();
        this.password = ShareFileService.generatePassword();
        Path file = Paths.get(loginUser.getDirectory(), filepath);
        if (!Files.exists(file, new LinkOption[]{LinkOption.NOFOLLOW_LINKS})) {
            throw new CustomException(400, "The file not exist", null);
        }
        filename = file.getFileName().toString();
        if (Files.isDirectory(file, new LinkOption[]{LinkOption.NOFOLLOW_LINKS})) {
            this.filetype = "folder";
        } else {
            int indexOf = filename.lastIndexOf(".");
            if (indexOf > -1) {
                this.filetype = filename.substring(indexOf + 1).toLowerCase();
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
        return filename;
    }

    public String getFileType() {
        return filetype;
    }

    public String getOwner() {
        return owner;
    }

    public String getPassword() {
        return password;
    }

    public String getFilePath() {
        return filepath;
    }

    public String getShareDate(String a) {
        return sharedate;
    }
    public String getShareDate() {
        return EncodeTool.FORMMATTER.format(new Date(Long.parseLong(sharedate)));
    }

    public int getDownloadTimes() {
        return downloadtimes;
    }

    public String getDiscShareDate() {
        return EncodeTool.FORMMATTER.format(new Date(Long.parseLong(sharedate)));
    }
}
