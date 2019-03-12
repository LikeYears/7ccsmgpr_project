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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Iterator;

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
                        Path tmpFile = Paths.get(filePath.toAbsolutePath().toString() + ".upload.tmp");
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
                        throw new CustomException(400, "file exist", null);
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
