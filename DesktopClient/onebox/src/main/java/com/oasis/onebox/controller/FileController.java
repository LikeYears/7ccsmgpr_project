package com.oasis.onebox.controller;

import com.oasis.onebox.entity.User;
import com.oasis.onebox.exception.CustomException;
import com.oasis.onebox.interceptor.LoginInterceptor;
import com.oasis.onebox.service.FileService;
import com.oasis.onebox.tool.EncodeTool;
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
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Iterator;
import com.oasis.onebox.tool.FileTool;
@Controller
@RequestMapping("/files")
public class FileController {

   //get the list of all files
    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResultShowing getFileLst(@RequestAttribute(LoginInterceptor.LOGIN_USER) User loginUser,
                                    @RequestParam(value = "accepttype", required = false) String filtertype,
                                    @RequestParam(value = "filterfile", required = false) String filterfile) throws Exception {
        return getFileList(null, loginUser, filtertype, filterfile);
    }


    //get files of specific directory
    @RequestMapping(value = "/{base64filepath}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResultShowing getFileList(@PathVariable("base64filepath") String path,
                          @RequestAttribute(LoginInterceptor.LOGIN_USER) User loginUser,
                          @RequestParam(value = "accepttype", required = false) String accepttype,
                          @RequestParam(value = "filterfile", required = false) String filterfile) throws Exception {
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
    public ResultShowing fileUpload(HttpServletRequest request, @RequestAttribute(LoginInterceptor.LOGIN_USER) User loginUser)
            throws Exception {
        Path uploadDir = Paths.get(loginUser.getDirectory(), "uploads");
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

//                        Files.move(tmpFile, filePath);





// 需要上传文件  然后 获取路径path1，文件可用 FileTool.getVersion("1.2.0") 加版本号。
//                      得到文件类型 if 文本文件：  String A = FileTool.txtCompare(path1, path2);
//                        if 文件
//                      String f1 = "/Users/lxt/Desktop/a/1.zip";
//                      String f2 = "/Users/lxt/Desktop/b/1.zip";
//                      boolean compare = isSameMd5(f1,f2);
//                        if 图片文件：
//                        File file1 = new File(path1);
//                        file1.getAbsolutePath();
//                        String b = multiRequest.getAbsolutePath();

//                        String A = FileTool.compareImage(path1, path2);
//                        System.out.println("-A--");
//                        System.out.println(A);
//                      如果A 相似度不等于100 ，保留文件，else 提示请勿上传相同文件。
//                        System.out.println("-path2--");
//                        System.out.println(path2);
//                        System.out.println("-path1--");
//                        System.out.println(path1);
//                        if (FileTool.compareImage( path1, path2) ) {
//                            throw new CustomException(400, "file exist", null);}
//                        else{

//                        throw new CustomException(400, "can not upload", null);

//                    }
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
                             @RequestAttribute(LoginInterceptor.LOGIN_USER) User loginUser, HttpServletRequest request,
                             HttpServletResponse response) throws Exception {
        Path file = Paths.get(loginUser.getDirectory() + "/" + new String(EncodeTool.decoderURLBASE64(path), "utf-8"));
        FileService.filePlayOrDownload(false, file, request, response);
    }

    //delete file
    @RequestMapping(value = "/{base64filepath}", method = RequestMethod.DELETE, produces = "application/json")
    @ResponseBody
    public ResultShowing delFile(@PathVariable("base64filepath") String path,
                      @RequestAttribute(LoginInterceptor.LOGIN_USER) User loginUser) throws Exception {
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
                       @RequestAttribute(LoginInterceptor.LOGIN_USER) User loginUser) throws Exception {
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
                        @RequestAttribute(LoginInterceptor.LOGIN_USER) User loginUser) throws Exception {
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
                            @RequestAttribute(LoginInterceptor.LOGIN_USER) User loginUser) throws Exception {
        return createNewFile(null, filetype, filename, loginUser);
    }

    //create new file on specific directory
    @RequestMapping(value = "/{base64filepath}/{filetype}/{base64filename}", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public ResultShowing createNewFile(@PathVariable("base64filepath") String path, @PathVariable("filetype") String filetype,
                            @PathVariable("base64filename") String filename, @RequestAttribute(LoginInterceptor.LOGIN_USER) User loginUser)
            throws Exception {
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

    @RequestMapping(value = "/{base64filepath}/play", method = RequestMethod.GET, produces = "application/json")
    public void filePlay(@PathVariable("base64filepath") String path,
                         @RequestAttribute(LoginInterceptor.LOGIN_USER) User loginUser, HttpServletRequest request,
                         HttpServletResponse response) throws Exception {
        Path file = Paths.get(loginUser.getDirectory() + "/" + new String(EncodeTool.decoderURLBASE64(path), "utf-8"));
        FileService.filePlayOrDownload(true, file, request, response);
    }
}
