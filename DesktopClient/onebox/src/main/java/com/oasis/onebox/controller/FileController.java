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
import com.oasis.onebox.tool.copy;
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
        Path uploadDir = Paths.get(loginUser.getDirectory(), "old version");
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
//                    boolean fileExists = Files.exists(filePath);

                    Path versionDir1 = Paths.get(loginUser.getDirectory(), "new version");
                    boolean versionDirDir1Exists = Files.exists(versionDir1, new LinkOption[]{LinkOption.NOFOLLOW_LINKS});
                    if (!versionDirDir1Exists) {
                        Files.createDirectory(versionDir1);
                    }
                    Path tmpFile61 = Paths.get(filePath.toAbsolutePath().toString() );
                    String path71 =tmpFile61.toString();
                    String tmpFile81 = new StringBuffer(path71).reverse().toString();
                    System.out.println(tmpFile81);

                    String str11 = tmpFile81.substring(0, tmpFile81.indexOf("\\"));
                    System.out.println(str11);
                    String d1 = new StringBuffer(str11).reverse().toString();
                    System.out.println("d1：");
                    Path tmpFile91 = Paths.get(d1);
                    System.out.println("filename1：");
                    System.out.println(tmpFile91);

                    Path tmpFile01= versionDir1 ;
                    String path41 =tmpFile01.toString();
                    String path81 =tmpFile91.toString();
                    StringBuffer s11 = new StringBuffer(path41);
                    StringBuffer s21 = new StringBuffer("\\");
                    StringBuffer s31 = new StringBuffer(path81);
                    s11.append(s21);
                    s11.append(s31);
                    System.out.println("s11:");
                    System.out.println(s11);

                    String tmpFile11 = new StringBuffer(s11).reverse().toString();
//                        System.out.println(tmpFile1);

                    StringBuilder stringBuilder21=new StringBuilder(tmpFile11);
                    int index11=stringBuilder21.indexOf(".");
                    System.out.println(index11);

                    String index111 = stringBuilder21.insert(index11+1,")1.0.1(-"  ).toString();

                    String c1 = new StringBuffer(index111).reverse().toString();
                    System.out.println("C1");
                    System.out.println(c1);
                    Path dd = Paths.get(c1);
                    System.out.println("dd");
                    System.out.println(dd);



                    boolean fileExists = Files.exists(dd);

                    if (!fileExists) {
                        System.out.println("no such file");
                        Path tmpFile = Paths.get(dd.toAbsolutePath().toString() + ".upload.tmp"  + FileTool.getVersion("1.0.0"));
                        System.out.println(tmpFile);
                        Files.deleteIfExists(tmpFile);
                        Files.createFile(tmpFile);
                        byte[] readBuffer = new byte[10485760];//10mb
                        InputStream is = file.getInputStream();
                        int hasReadSize;
                        while ((hasReadSize = is.read(readBuffer)) != -1) {
                            Files.write(tmpFile, Arrays.copyOf(readBuffer, hasReadSize), StandardOpenOption.APPEND);
                    }
                    Files.move(tmpFile, dd, StandardCopyOption.REPLACE_EXISTING);
                }
                else {
                        Path versionDir = Paths.get(loginUser.getDirectory(), "new version");
                        boolean versionDirDirExists = Files.exists(versionDir, new LinkOption[]{LinkOption.NOFOLLOW_LINKS});
                        if (!versionDirDirExists) {
                            Files.createDirectory(versionDir);
                        }


//                       get the name of the file.
                        Path tmpFile6 = Paths.get(filePath.toAbsolutePath().toString() );
                        String path7 =tmpFile6.toString();
                        String tmpFile8 = new StringBuffer(path7).reverse().toString();
                        System.out.println(tmpFile8);

                        String str1 = tmpFile8.substring(0, tmpFile8.indexOf("\\"));
                        System.out.println(str1);
                        String d = new StringBuffer(str1).reverse().toString();
                        Path tmpFile9 = Paths.get(d);
                        System.out.println("filename：");
                        System.out.println(tmpFile9);



                        Path tmpFile0= versionDir ;
                        String path4 =tmpFile0.toString();
                        String path8 =tmpFile9.toString();
                        StringBuffer s1 = new StringBuffer(path4);
                        StringBuffer s2 = new StringBuffer("\\");
                        StringBuffer s3 = new StringBuffer(path8);
                        s1.append(s2);
                        s1.append(s3);
                        System.out.println("s1:");
                        System.out.println(s1);


                        String tmpFile1 = new StringBuffer(s1).reverse().toString();
//                        System.out.println(tmpFile1);

                        StringBuilder stringBuilder2=new StringBuilder(tmpFile1);
                        int index=stringBuilder2.indexOf(".");
                        System.out.println(index);

                        String index1 = stringBuilder2.insert(index+1,")"+FileTool.getVersion("1.0.1" )+"(-" ).toString();

                        String c = new StringBuffer(index1).reverse().toString();
                        Path tmpFile = Paths.get(c);
                        System.out.println("111");
                        System.out.println(c);
                        System.out.println("222");
                        System.out.println(tmpFile);


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

//                      whether exist old version, if not exist copy, delete and upload
                        Path uploadfile = filePath;
                        System.out.println("uploadfile");
                        System.out.println(uploadfile);

                        File file2 = new File(path1);
//                        get filename txt
                        String filename = file2.getName();
                        String x = new StringBuffer(filename).reverse().toString();
                        String v = x.substring(0, x.indexOf("."));
                        System.out.println("v");
                        System.out.println(v);
                        System.out.println("x");
                        System.out.println(x);

                        boolean uploadfileExists = Files.exists(uploadfile, new LinkOption[]{LinkOption.NOFOLLOW_LINKS});
                        if (!uploadfileExists) {
                            String cc = dd.toString();
                            System.out.println("cc");
                            System.out.println(cc);
                            System.out.println(path2);
                            System.out.println(path1);
                            copy.copyFile(cc, path2);
                            System.out.print("upload the file success0!");
                            FileService.delFile(cc);
                            System.out.print("upload the file success1!");


                            File file22=new File(path1);
                            file22.renameTo(new File(cc));
                            System.out.print("upload the file success2!");

                        }

                        else{
                            BufferedImage image = ImageIO.read(new File(path1));
                            System.out.println(image);
//                            if the file is image
                            if (image != null) {
                                String cc = dd.toString();
                                String A = FileTool.compareImage(path1, cc);
                                String B = FileTool.compareImage(path1, path2);
                                String G = FileTool.compareImage(path2, cc);
                                System.out.println(path1);
                                System.out.println(path2);
                                System.out.println(cc);
                                System.out.println(A);
                                System.out.println(B);
                                System.out.println(G);

                                String C = new StringBuffer(A).reverse().toString();
                                System.out.println(C);
                                String D = C.substring(1, C.indexOf("：率"));
                                System.out.println(D);
                                String D1 = new StringBuffer(D).reverse().toString();
                                System.out.println("D1");
                                System.out.println(D1);

                                String E = new StringBuffer(B).reverse().toString();
                                String F = E.substring(1, E.indexOf("：率"));
                                String F1 = new StringBuffer(F).reverse().toString();
                                System.out.println("F1");
                                System.out.println(F1);

                                String H = new StringBuffer(G).reverse().toString();
                                String J = H.substring(1, H.indexOf("：率"));
                                String J1 = new StringBuffer(J).reverse().toString();
                                System.out.println("J1");
                                System.out.println(J1);

                                int t = Integer.parseInt(D1);
                                int i = Integer.parseInt(F1);
                                int q = Integer.parseInt(J1);
                                System.out.print("T");
                                System.out.print(t);
                                System.out.print("i");
                                System.out.print(i);
                                System.out.print("q");
                                System.out.print(q);

                                if (D1.equals("100")) {
                                    FileService.delFile(path1);
                                    System.out.print("please do not upload the same file 1!");
                                    throw new CustomException(400, "please do not upload the same file!", null);
                                } else if (i == t && !D1.equals("100")) {
                                    copy.copyFile(cc, path2);
                                    FileService.delFile(cc);
                                    File file22 = new File(path1);

                                    file22.renameTo(new File(cc));
                                    System.out.print("upload the file success2!5");
                                } else if (i < q) {

                                    copy.copyFile(cc, path2);
                                    FileService.delFile(cc);
                                    File file22 = new File(path1);

                                    file22.renameTo(new File(cc));
                                    System.out.print("upload the file success2!");
//                                    Files.createFile(dd);
//
//                                    byte[] readBuffer12 = new byte[10485760];//10mb
//                                    InputStream is1 = file.getInputStream();
//                                    int hasReadSize1;
//                                    while ((hasReadSize1 = is1.read(readBuffer12)) != -1) {
//                                        Files.write(dd, Arrays.copyOf(readBuffer1, hasReadSize1), StandardOpenOption.APPEND);
//                                        System.out.print("upload the file success1!");
//                                    }


                                } else if (i > q && !F1.equals("100")) {
                                    FileService.delFile(path1);
                                    Files.createFile(filePath);

                                    byte[] readBuffer12 = new byte[10485760];//10mb
                                    InputStream is1 = file.getInputStream();
                                    int hasReadSize1;
                                    while ((hasReadSize1 = is1.read(readBuffer12)) != -1) {
                                        Files.write(filePath, Arrays.copyOf(readBuffer1, hasReadSize1), StandardOpenOption.APPEND);
                                        System.out.print("upload the file success3!");
                                    }
                                } else {
                                    FileService.delFile(path1);
                                    System.out.print("please do not upload the old version!");
                                    throw new CustomException(400, "please do not upload the old version!", null);
                                }
                            }
//                            if the file is text



                            else if (v.equals("txt")) {
                                String l = "";
                                FileTool.txtCompare("dd","1.txt",l);
//                                MySimHash.
                                System.out.println("l");
                                System.out.println(l);

                            }
//                            the other types of file


                            else {
                                String cc = dd.toString();
                                boolean A = FileTool.isSameMd5(path1, cc);
                                boolean B = FileTool.isSameMd5(path1,path2);
                                boolean C = FileTool.isSameMd5(path2,cc);
                                System.out.println(path1);
                                System.out.println(path2);
                                System.out.println(cc);
                                System.out.println(A);
                                if (A) {
                                    FileService.delFile(path1);
                                    System.out.print("please do not upload the same file !");
                                    throw new CustomException(400, "please do not upload the same file!", null);

                                }
                                else if (!A && B){
                                    FileService.delFile(path1);
                                    System.out.print("please do not upload the old version file !");
                                    throw new CustomException(400, "please do not upload the old version file !", null);


                                }
                                else {
//                                    String cc = dd.toString();
                                    copy.copyFile(cc, path2);
                                    FileService.delFile(cc);
                                    File file22 = new File(path1);

                                    file22.renameTo(new File(cc));
                                    System.out.print("upload the file success!");
                                    return new ResultShowing("upload file success", null);
                                }
                            }
                            }


//                            int length=A.length();
//                            if(length>=3){
//                                String str=A.substring(length-4,length);
//                                System.out.println(str);
//                                String one = "100%";
//                                if (str .equals(one)){
//                                    FileService.delFile(path1);
//                                    System.out.print("please do not upload the same file 1!");
//                                    throw new CustomException(400, "please do not upload the same file!", null);
//                                }
//                                else{
//                                    System.out.print("upload the file success!");
//                                    return new ResultShowing("upload file success", null);}
//                                }
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
