package com.oasis.onebox.controller;

import com.oasis.onebox.entity.User;
import com.oasis.onebox.exception.CustomException;
import com.oasis.onebox.interceptor.MobileInterceptor;
import com.oasis.onebox.service.FileService;
import com.oasis.onebox.tool.EncodeTool;
import com.oasis.onebox.tool.FileTool;
import com.oasis.onebox.tool.ResultShowing;
import com.oasis.onebox.tool.StringTool;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Iterator;

@Controller
@RequestMapping("/mobile")
public class MobileFileController {
    //get the list of all files
    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResultShowing getFileLst(@RequestParam(value = "onebox") String token) throws Exception {
        User loginUser = User.checkMobileToken(token);
        String path = loginUser.getDirectory();
        return new ResultShowing("get file list success", FileService.getFileList(loginUser.getDirectory(), path, null, null));
    }


    //get files of specific directory
    @RequestMapping(value = "/{base64filepath}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResultShowing getFileList(@PathVariable("base64filepath") String path,
                                     @RequestParam(value = "onebox") String token,
                                     @RequestParam(value = "accepttype", required = false) String accepttype,
                                     @RequestParam(value = "filterfile", required = false) String filterfile) throws Exception {
        User loginUser = User.checkMobileToken(token);
        if (StringTool.isNullOrEmpty(path)) {
            path = loginUser.getDirectory();
        } else {
            path = loginUser.getDirectory() + "/" + new String(EncodeTool.decoderURLBASE64(path), "utf-8");
        }
        if (filterfile != null) {
            filterfile = loginUser.getDirectory() + "/" + new String(EncodeTool.decoderURLBASE64(filterfile), "utf-8");
            filterfile = Paths.get(filterfile).toAbsolutePath().toString();
        }
        return new ResultShowing("get file list success", FileService.getFileList(loginUser.getDirectory(), path, accepttype, filterfile));
    }

    //upload file
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ResultShowing fileUpload(HttpServletRequest request, @RequestParam(value = "onebox") String token)
            throws Exception {
        User loginUser = User.checkMobileToken(token);
        Path uploadDir = Paths.get(loginUser.getDirectory(), "Uploads");
        boolean uploadDirExists = Files.exists(uploadDir, new LinkOption[]{LinkOption.NOFOLLOW_LINKS});
        if (!uploadDirExists) {
            Files.createDirectory(uploadDir);
        }
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
                request.getSession().getServletContext());
        if (multipartResolver.isMultipart(request)) {
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
            Iterator<String> iter = multiRequest.getFileNames();

            while (iter.hasNext()) {
                MultipartFile file = multiRequest.getFile(iter.next().toString());
                if (file != null) {
                    Path filePath = Paths.get(uploadDir.toAbsolutePath().toString(),
                            new String(file.getOriginalFilename()));
                    boolean fileExists = Files.exists(filePath);
                    if (!fileExists) {
                        Path tmpFile = Paths.get(filePath.toAbsolutePath().toString() + ".upload.tmp"  + FileTool.getVersion("1.0.0"));
                        Files.deleteIfExists(tmpFile);
                        Files.createFile(tmpFile);
                        byte[] readBuffer = new byte[10485760];//10mb
                        InputStream is = file.getInputStream();
                        int hasReadSize;
                        while ((hasReadSize = is.read(readBuffer)) != -1) {
                            Files.write(tmpFile, Arrays.copyOf(readBuffer, hasReadSize), StandardOpenOption.APPEND);
                        }
                        Files.move(tmpFile, filePath, StandardCopyOption.REPLACE_EXISTING);
                    }
                    else {

                        Path tmpFile = Paths.get(filePath.toAbsolutePath().toString()+ FileTool.getVersion("0.0.0") );
                        Files.deleteIfExists(tmpFile);
                        Files.createFile(tmpFile);
                        System.out.println(tmpFile);

                        byte[] readBuffer1 = new byte[10485760];//10mb
                        InputStream is = file.getInputStream();
                        int hasReadSize;
                        while ((hasReadSize = is.read(readBuffer1)) != -1) {
                            Files.write(tmpFile, Arrays.copyOf(readBuffer1, hasReadSize), StandardOpenOption.APPEND);

                            String path1 =tmpFile.toString();
                            System.out.println(path1);
                            String path2 =filePath.toString();
                            System.out.println(path2);

                            BufferedImage image = ImageIO.read(new File(path1));
                            System.out.println(image);
                            if (image == null) {
                                boolean A = FileTool.isSameMd5(path1, path2);
                                System.out.println(A);
                                if (A == true){
                                    FileService.delFile(path1);
                                    System.out.println("please do not upload the same file!");
                                    throw new CustomException(400, "please do not upload the same file!", null);
                                }
                            }
                            else   {
                                String A = FileTool.compareImage(path1, path2);
                                System.out.println(A);

                                int length=A.length();
                                if(length>=3){
                                    String str=A.substring(length-4,length);
                                    System.out.println(str);
                                    if (str == "100%"){
                                        FileService.delFile(path1);
                                        System.out.println("please do not upload the same file!");
                                        throw new CustomException(400, "please do not upload the same file!", null);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return new ResultShowing("upload file success", null);
        }
        throw new CustomException(400, "request error", null);
    }

    //download file
    @RequestMapping(value = "/{base64filepath}/download")
    public void fileDownload(@PathVariable("base64filepath") String path,
                             @RequestParam(value = "onebox") String token, HttpServletRequest request,
                             HttpServletResponse response) throws Exception {
        User loginUser = User.checkMobileToken(token);
        Path file = Paths.get(loginUser.getDirectory() + "/" + new String(EncodeTool.decoderURLBASE64(path), "utf-8"));
        FileService.filePlayOrDownload(false, file, request, response);
    }

////    download sharefile
//    @RequestMapping(value = "/{base64filepath}/sharedownload")
//    public void fileDownload(@PathVariable("base64filepath") String path, @PathVariable("owner") String owner, HttpServletRequest request,
//                             HttpServletResponse response) throws Exception {
//        Path file = Paths.get(User.getUserDirectory(EncodeTool.decoderBASE64(owner).toString()) + "/" + new String(EncodeTool.decoderURLBASE64(path), "utf-8"));
//        FileService.filePlayOrDownload(false, file, request, response);
//    }

    //delete file
    @RequestMapping(value = "/{base64filepath}", method = RequestMethod.DELETE, produces = "application/json")
    @ResponseBody
    public ResultShowing delFile(@PathVariable("base64filepath") String path,
                                 @RequestParam(value = "onebox") String token) throws Exception {
        User loginUser = User.checkMobileToken(token);
        if (StringTool.isNullOrEmpty(path)) {
            throw new CustomException(400, "delete file:parameter is null", null);
        } else {
            path = loginUser.getDirectory() + "/" + new String(EncodeTool.decoderURLBASE64(path), "utf-8");
        }
        try {
            if (FileService.delFile(path)) {
                return new ResultShowing("delete file success", null);
            } else {
                throw new CustomException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "delete file fail", null);
            }
        } catch (FileNotFoundException e) {
            throw new CustomException(HttpServletResponse.SC_NOT_FOUND, "file not exist", e);
        }
    }

    //move file
    @RequestMapping(value = "/{base64filepath}/{newdirpath}", method = RequestMethod.PUT, produces = "application/json")
    @ResponseBody
    public ResultShowing moveFile(@PathVariable("base64filepath") String filepath, @PathVariable("newdirpath") String newdirpath,
                                  @RequestParam(value = "onebox") String token) throws Exception {
        User loginUser = User.checkMobileToken(token);
        filepath = loginUser.getDirectory() + "/" + new String(EncodeTool.decoderURLBASE64(filepath), "utf-8");
        if (StringTool.isNullOrEmpty(newdirpath)) {
            newdirpath = loginUser.getDirectory();
        } else {
            newdirpath = loginUser.getDirectory() + "/" + new String(EncodeTool.decoderURLBASE64(newdirpath), "utf-8");
        }
        Path sourceFile = Paths.get(filepath);
        Path targetFile = Paths.get(newdirpath);
        FileService.moveFile(sourceFile, targetFile);
        return new ResultShowing("move file success", null);
    }

    //rename
    @RequestMapping(value = "/{base64filepath}/{base64newfilename}", method = RequestMethod.PATCH, produces = "application/json")
    @ResponseBody
    public ResultShowing renameFile(@PathVariable("base64filepath") String path,
                                    @PathVariable("base64newfilename") String newfilename,
                                    @RequestParam(value = "onebox") String token) throws Exception {
        User loginUser = User.checkMobileToken(token);
        if (StringTool.isNullOrEmpty(path)) {
            throw new CustomException(400, "parameter is null", null);
        } else {
            path = loginUser.getDirectory() + "/" + new String(EncodeTool.decoderURLBASE64(path), "utf-8");
        }
        try {
            if (FileService.renameFile(path, newfilename)) {
                return new ResultShowing("rename success", null);
            } else {
                throw new CustomException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "rename fail", null);
            }
        } catch (FileNotFoundException e1) {
            throw new CustomException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e1.getMessage(), null);
        } catch (FileAlreadyExistsException e2) {
            throw new CustomException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e2.getMessage(), null);
        }
    }

    //create new file on main directory
    @RequestMapping(value = "/{filetype}/{base64filename}", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public ResultShowing createNewFile(@PathVariable("base64filename") String filename, @PathVariable("filetype") String filetype,
                                       @RequestParam(value = "onebox") String token) throws Exception {
        return createNewFile(null, filetype, filename, token);
    }

    //create new file on specific directory
    @RequestMapping(value = "/{base64filepath}/{filetype}/{base64filename}", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public ResultShowing createNewFile(@PathVariable("base64filepath") String path, @PathVariable("filetype") String filetype,
                                       @PathVariable("base64filename") String filename, @RequestParam(value = "onebox") String token)
            throws Exception {
        User loginUser = User.checkMobileToken(token);

        if (StringTool.isNullOrEmpty(path)) {
            path = loginUser.getDirectory();
        } else {
            path = loginUser.getDirectory() + "/" + new String(EncodeTool.decoderURLBASE64(path), "utf-8");
        }

        boolean finishFlag = false;
        try {
            if (filetype.equals("directory")) {
                finishFlag = FileService.createNewDir(path, filename);
            } else if (filetype.equals("file")) {
                finishFlag = FileService.createNewFile(path, filename);
            } else {
                throw new CustomException(400, "operation not allowed", null);
            }
        } catch (FileAlreadyExistsException e1) {
            throw new CustomException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e1.getMessage(), null);
        }

        if (finishFlag) {
            return new ResultShowing("new file success", null);
        } else {
            throw new CustomException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "create file/directory fail", null);
        }
    }

}