package com.oasis.onebox.download;

import com.oasis.onebox.dao.DownloadTaskDao;
import com.oasis.onebox.exception.CustomException;
import com.oasis.onebox.tool.StringTool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;

import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DownloadTask {
    private final static Lock lock = new ReentrantLock();
    private final static Log logger = LogFactory.getLog(DownloadTask.class);

    private static boolean initFlag = false;

    private static final Map<String, Path> USER_DOWNLOAD_DIR = new HashMap<String, Path>();
    private static final Map<String, Map<String, DownloadTaskDao>> DOWNLOAD_TASKS = new HashMap<String, Map<String, DownloadTaskDao>>();


    public static void createUserDownloadDir(String username, String userdir) throws IOException {
        Path userDir = Paths.get(userdir, username + "_Downloads");
        if (!Files.exists(userDir, new LinkOption[]{LinkOption.NOFOLLOW_LINKS }))
        {
            Files.createDirectory(userDir);
        }
        USER_DOWNLOAD_DIR.put(username, userDir);
        DOWNLOAD_TASKS.put(username, new LinkedHashMap<String, DownloadTaskDao>());
    }

    public static void initDownloadTasks() {
        if (!initFlag) {
            new DownloadTask().new InitDownloadTask().start();
        }
        initFlag = true;
    }

    private static synchronized Map<String, DownloadTaskDao> getTaskMapByUsername(String username) {
        if (DOWNLOAD_TASKS.containsKey(username)) {
            return DOWNLOAD_TASKS.get(username);
        } else {
            Map<String, DownloadTaskDao> userTask = new LinkedHashMap<String, DownloadTaskDao>();
            DOWNLOAD_TASKS.put(username, userTask);
            return userTask;
        }
    }

    public static List<Map<String, String>> getDownloadStatus(String username) {
        List<Map<String, String>> tasklist = new ArrayList<Map<String, String>>();
        Map<String, DownloadTaskDao> userTask = getTaskMapByUsername(username);
        Iterator<String> taskIDs = userTask.keySet().iterator();
        while (taskIDs.hasNext()) {
            String taskID = taskIDs.next();
            tasklist.add(userTask.get(taskID).getStatus());
        }
        return tasklist;
    }

    public static String addDownloadTask(String username, String url) throws CustomException {
        try {
            lock.lock();
            String taskID = StringTool.getUUID();
            String userDir = USER_DOWNLOAD_DIR.get(username).toAbsolutePath().toString();
            if (url.startsWith("http") || url.startsWith("https")) {
                HttpDownload httpDownload = new HttpDownload(userDir, url,taskID);
                new Thread(httpDownload).start();
                getTaskMapByUsername(username).put(taskID, httpDownload);
            } else if (url.startsWith("ftp")) {

            } else {
                throw new CustomException(400, "not support this link", null);
            }
            return taskID;
        } finally {
            lock.unlock();
        }
    }

    public static boolean deleteDownload(String username, String taskID) {
        try {
            lock.lock();
            Map<String, DownloadTaskDao> userTask = getTaskMapByUsername(username);
            if (userTask.containsKey(taskID)) {
                userTask.get(taskID).delete();
                userTask.remove(taskID);
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    public static boolean retryDownload(String username, String taskID) throws CustomException {
        Map<String, DownloadTaskDao> userTask = getTaskMapByUsername(username);
        if (userTask.containsKey(taskID)) {
            String url = userTask.get(taskID).getStatus().get("url");
            deleteDownload(username, taskID);
            addDownloadTask(username, url);
            return true;
        }
        return false;
    }

    public static boolean stopDownload(String username, String taskID) {
        try {
            lock.lock();
            Map<String, DownloadTaskDao> userTask = getTaskMapByUsername(username);
            if (userTask.containsKey(taskID)) {
                userTask.get(taskID).stop();
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }
    class InitDownloadTask extends Thread {
        public void run() {
            Iterator<String> userList = USER_DOWNLOAD_DIR.keySet().iterator();
            while (userList.hasNext()) {
                String username = userList.next();
                Path path = USER_DOWNLOAD_DIR.get(username);
                try {
                    Files.walkFileTree(path, new HashSet<FileVisitOption>(), 1, new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            if (!attrs.isDirectory() && file.getFileName().toString().endsWith(".pcd.dl.cfg")) {
                                List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
                                if (lines != null && !lines.isEmpty()) {
                                    String url = lines.get(0).trim();
                                    if (!"".equals(url)) {
                                        try {
                                            DownloadTask.addDownloadTask(username, url);
                                        } catch (CustomException e) {
                                            logger.error(e);
                                        }
                                    }
                                }
                            }
                            return FileVisitResult.CONTINUE;
                        }
                    });
                } catch (IOException e) {
                    logger.error(e);
                }
            }
        }
    }


}
