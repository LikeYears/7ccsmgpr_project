package com.oasis.onebox.download;

import com.oasis.onebox.dao.DownloadTaskDao;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

import java.nio.file.*;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DownloadTask {
    private final static Lock lock = new ReentrantLock();
    private final static Log logger = LogFactory.getLog(DownloadTask.class);

    private static boolean initFlag = false;

    private static final Map<String, Path> USER_DOWNLOAD_DIR = new HashMap<String, Path>();
    private static final Map<String, Map<String, DownloadTaskDao>> DOWNLOAD_TASKS = new HashMap<String, Map<String, DownloadTaskDao>>();


    public static void putUserDownloadDir(String username, String userdir) throws IOException {
        Path userDir = Paths.get(userdir, username + "_Downloads");
        if (!Files.exists(userDir, new LinkOption[]{LinkOption.NOFOLLOW_LINKS }))
        {
            Files.createDirectory(userDir);
        }
        USER_DOWNLOAD_DIR.put(username, userDir);
        DOWNLOAD_TASKS.put(username, new LinkedHashMap<String, DownloadTaskDao>());
    }

}
