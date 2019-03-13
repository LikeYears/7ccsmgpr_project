package com.oasis.onebox.service;

import com.oasis.onebox.entity.CloudFile;
import com.oasis.onebox.exception.CustomException;
import com.oasis.onebox.tool.EncodeTool;
import com.oasis.onebox.tool.StringTool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileService {
    public static final int DEFAULT_BUFFER_SIZE = 10485760;// 10M
    private static final Pattern RANGE_PATTERN = Pattern.compile("bytes=(?<start>\\d*)-(?<end>\\d*)");
    private static final Log LOGGER = LogFactory.getLog(FileService.class);

    private static final Set<String> CAN_PLAY_ONLINE_FILE_TYPES = new HashSet<String>();

    private static final Set<String> CAN_PREVIEW_FILE_TYPES = new HashSet<String>();

    static {
        CAN_PLAY_ONLINE_FILE_TYPES.add("mp4");
        CAN_PLAY_ONLINE_FILE_TYPES.add("flv");

        CAN_PREVIEW_FILE_TYPES.add("bmp");
        CAN_PREVIEW_FILE_TYPES.add("jpg");
        CAN_PREVIEW_FILE_TYPES.add("gif");
        CAN_PREVIEW_FILE_TYPES.add("jpeg");
        CAN_PREVIEW_FILE_TYPES.add("png");
    }


    public static boolean isCanPlayOnline(String filetype) {
        return CAN_PLAY_ONLINE_FILE_TYPES.contains(filetype);
    }


    public static boolean isCanPreview(String filetype) {
        return CAN_PREVIEW_FILE_TYPES.contains(filetype);
    }


    public static List<CloudFile> getFileList(String maindir, String filepath, String accepttype,
                                              final String filterfile) throws CustomException, IOException {
        if (filterfile != null) {
            Path filterFilePath = Paths.get(filterfile);
            if (!Files.exists(filterFilePath, new LinkOption[]{LinkOption.NOFOLLOW_LINKS})) {
                throw new CustomException(400, "the filter file not exist", null);
            }
        }
        List<CloudFile> filelsit = new ArrayList<CloudFile>();
        List<CloudFile> dirlsit = new ArrayList<CloudFile>();
        Files.walkFileTree(Paths.get(filepath), new HashSet<FileVisitOption>(), 1, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (filterfile != null) {
                    String thisfilepath = file.toAbsolutePath().toString();
                    if (thisfilepath.indexOf(filterfile) > -1) {
                        return FileVisitResult.CONTINUE;
                    }
                }

                CloudFile df = new CloudFile(maindir, file, attrs);
                if (attrs.isDirectory()) {
                    dirlsit.add(df);
                } else {
                    filelsit.add(df);
                }
                return FileVisitResult.CONTINUE;
            }
        });
        if (accepttype == null) {
            dirlsit.addAll(filelsit);
            return dirlsit;
        } else if (accepttype.equals("file")) {
            return filelsit;
        } else {
            return dirlsit;
        }
    }

    public static final String calculateDescSize(long fileSize) {
        BigDecimal big1 = new BigDecimal(fileSize);
        int[] sizes = {1073741824, 1024 * 1024, 1024};
        String[] units = {"GB", "MB", "KB"};
        for (int i = 0; i < sizes.length; i++) {
            if (fileSize > sizes[i]) {
                return big1.divide(new BigDecimal(sizes[i]), 2, BigDecimal.ROUND_HALF_EVEN).toString() + units[i];
            }
        }
        return "0KB";
    }


    public static void filePlayOrDownload(boolean playFlag, Path file, HttpServletRequest request, HttpServletResponse response)
            throws CustomException, IOException {
        if (!Files.exists(file, new LinkOption[]{LinkOption.NOFOLLOW_LINKS})) {
            throw new CustomException(500, "file not exist", null);
        }

        long fileSize = file.toFile().length();

        long[] pointArray = FileService.getStartEndPointFromHeaderRange(fileSize, request);

        response.reset();
        if (!StringTool.isNullOrEmpty(request.getHeader("Range"))) {
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            response.setHeader("Content-Range",
                    String.format("bytes %s-%s/%s", pointArray[0], pointArray[1], fileSize));
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
        }

        response.setBufferSize(10485760);
        response.setHeader("Content-Disposition",
                String.format("inline;filename=\"%s\"", file.getFileName().toString()));
        response.setHeader("Accept-Ranges", "bytes");
        response.setDateHeader("Last-Modified", Files.getLastModifiedTime(file).toMillis());
        response.setDateHeader("Expires", System.currentTimeMillis() + 1000 * 60 * 60 * 24);
        response.setContentType(playFlag ? Files.probeContentType(file) : MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader("Content-Length", String.format("%s", pointArray[1] - pointArray[0] + 1));
        SeekableByteChannel input = null;
        OutputStream output = null;
        try {
            input = Files.newByteChannel(file, StandardOpenOption.READ);
            output = response.getOutputStream();
            ByteBuffer buffer = ByteBuffer.allocate(10485760);
            input.position(pointArray[0]);
            int hasRead;
            while ((hasRead = input.read(buffer)) != -1) {
                buffer.clear();
                output.write(buffer.array(), 0, hasRead);
            }
            response.flushBuffer();
        } catch (IllegalStateException e) {
        } finally {
            if (input != null) {
                input.close();
            }
            if (output != null) {
                output.flush();
                output.close();
            }
        }
    }

    public static long[] getStartEndPointFromHeaderRange(long fileSize, HttpServletRequest request) {
        long[] pointArray = new long[2];
        long startPoint = 0;
        long endPoint = fileSize - 1;
        String range = request.getHeader("Range");
        range = StringTool.isNullOrEmpty(range) ? "" : range;
        Matcher matcher = RANGE_PATTERN.matcher(range);
        if (matcher.matches()) {
            String startGroup = matcher.group("start");
            startPoint = StringTool.isNullOrEmpty(startGroup) ? startPoint : Integer.valueOf(startGroup);
            startPoint = startPoint < 0 ? 0 : startPoint;

            String endGroup = matcher.group("end");
            endPoint = StringTool.isNullOrEmpty(endGroup) ? endPoint : Integer.valueOf(endGroup);
            endPoint = endPoint > fileSize - 1 ? fileSize - 1 : endPoint;

        }
        pointArray[0] = startPoint;
        pointArray[1] = endPoint;
        return pointArray;
    }


    public static boolean delFile(String filepath) throws FileNotFoundException {
        Path file = Paths.get(filepath);
        boolean pathExists = Files.exists(file, new LinkOption[]{LinkOption.NOFOLLOW_LINKS});
        if (!pathExists) {
            throw new FileNotFoundException("file not exist");
        } else {
            try {
                Files.walkFileTree(file, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                LOGGER.error(e);
                return false;
            }
            return true;
        }
    }

    public static void moveFile(Path sourceFile, Path targetFile) throws IOException, CustomException {
        if (!Files.exists(sourceFile, new LinkOption[]{LinkOption.NOFOLLOW_LINKS})) {
            throw new CustomException(404, "source file not exist", null);
        }
        if (!Files.exists(targetFile, new LinkOption[]{LinkOption.NOFOLLOW_LINKS})) {
            throw new CustomException(404, "target directory not exist", null);
        }
        if (!Files.isDirectory(targetFile, new LinkOption[]{LinkOption.NOFOLLOW_LINKS})) {
            throw new CustomException(400, "error:cannot move it into a file", null);
        }
        targetFile = targetFile.resolve(sourceFile.getFileName());
        String targetFilePath = targetFile.toAbsolutePath().toString();
        String sourceFilePath = sourceFile.toAbsolutePath().toString();
        if (targetFilePath.indexOf(sourceFilePath) > -1 || targetFilePath.equals(sourceFilePath)) {
            throw new CustomException(400, "error:cannot move it into itself", null);
        }
        if (Files.exists(targetFile, new LinkOption[]{LinkOption.NOFOLLOW_LINKS})) {
            throw new CustomException(400, "already exist in target directory", null);
        }
        Files.move(sourceFile, targetFile);
    }

   public static boolean renameFile(String filepath, String newfilename)
            throws FileNotFoundException, IOException, FileAlreadyExistsException {
        newfilename = new String(EncodeTool.decoderURLBASE64(newfilename), "utf-8");
        Path source = Paths.get(filepath);
        boolean pathExists = Files.exists(source, new LinkOption[]{LinkOption.NOFOLLOW_LINKS});
        if (!pathExists) {
            throw new FileNotFoundException("source file not exist");
        } else {
            Path target = source.resolveSibling(newfilename);
            if (source.equals(target)) {
                throw new FileAlreadyExistsException("rename cancel: new file has same name with the old file");
            }
            try {
                Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
                return true;
            } catch (IOException e) {
                LOGGER.error(e);
                return false;
            }
        }

    }


    public static boolean createNewDir(String filepath, String newfilename)
            throws IOException, FileAlreadyExistsException {
        newfilename = new String(EncodeTool.decoderURLBASE64(newfilename), "utf-8");
        Path source = Paths.get(filepath, newfilename);
        boolean pathExists = Files.exists(source, new LinkOption[]{LinkOption.NOFOLLOW_LINKS});
        if (pathExists) {
            throw new FileAlreadyExistsException("directory exist");
        } else {
            try {
                Files.createDirectory(source);
            } catch (IOException e) {
                LOGGER.error(e);
                return false;
            }
            return true;
        }
    }


    public static boolean createNewFile(String filepath, String newfilename)
            throws IOException, FileAlreadyExistsException {
        newfilename = new String(EncodeTool.decoderURLBASE64(newfilename), "utf-8");
        Path source = Paths.get(filepath, newfilename);
        boolean pathExists = Files.exists(source, new LinkOption[]{LinkOption.NOFOLLOW_LINKS});
        if (pathExists) {
            throw new FileAlreadyExistsException("file exist");
        } else {
            try {
                Files.createFile(source);
            } catch (IOException e) {
                LOGGER.error(e);
                return false;
            }
            return true;
        }
    }
}
