package com.oasis.onebox.service;

import com.oasis.onebox.entity.FileShare;
import com.oasis.onebox.tool.JDBCTool;
import com.oasis.onebox.tool.StringTool;

import java.util.List;

public class ShareFileService {

    //generate random password
    public static String generatePassword() {
        byte[] bytes = new byte[4];
        byte[] uuid = StringTool.getUUID().getBytes();
        for (byte i = 0; i < 4; i++) {
            bytes[i] = uuid[(int) (Math.random() * 32)];
        }
        return new String(bytes);
    }

    //add new share file
    public static FileShare addNewShareFile(FileShare fileshare) {
        String querysql = "select * from fileshare where owner='%s' and filepath='%s'";
        querysql = String.format(querysql, fileshare.getOwner(), fileshare.getFilePath());
        List<FileShare> shareList = JDBCTool.executeQuery(querysql, FileShare.class);
        if (shareList != null && !shareList.isEmpty()) {
            fileshare = shareList.get(0);
            return fileshare;
        } else {
            String sql = "insert into fileshare values ('%s','%s','%s','%s','%s','%s', %s ,0)";
            sql = String.format(sql, fileshare.getId(), fileshare.getOwner(), fileshare.getPassword(),
                    fileshare.getFileName(), fileshare.getFileType(), fileshare.getFilePath(),
                    fileshare.getShareDate());
            if (JDBCTool.executeUpdate(sql) > 0) {
                return fileshare;
            } else {
                return null;
            }
        }
    }

    //search all share files
    public static List<FileShare> searchAllShare(String owner) {
        String querysql = "select * from fileshare where owner='%s'";
        querysql = String.format(querysql, owner);
        return JDBCTool.executeQuery(querysql, FileShare.class);
    }

    //cancel share
    public static boolean cancelShare(String id, String owner) {
        String deleteSQL = "delete from fileshare where id ='%s' and owner='%s'";
        deleteSQL = String.format(deleteSQL, id, owner);
        return JDBCTool.executeUpdate(deleteSQL) > 0;
    }

    //search share file by id
    public static FileShare searchShareByID(String id) {
        String deleteSQL = "select * from fileshare where id ='%s'";
        deleteSQL = String.format(deleteSQL, id);
        List<FileShare> fileShareList = JDBCTool.executeQuery(deleteSQL, FileShare.class);
        if (fileShareList != null && fileShareList.size() > 0) {
            return fileShareList.get(0);
        } else {
            return null;
        }
    }
}
