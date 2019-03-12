package com.oasis.onebox.controller;


import com.oasis.onebox.entity.FileShare;
import com.oasis.onebox.entity.User;
import com.oasis.onebox.exception.CustomException;
import com.oasis.onebox.interceptor.LoginInterceptor;
import com.oasis.onebox.interceptor.ShareAccessInterceptor;
import com.oasis.onebox.service.FileService;
import com.oasis.onebox.service.ShareFileService;
import com.oasis.onebox.tool.EncodeTool;
import com.oasis.onebox.tool.ResultShowing;
import com.oasis.onebox.tool.StringTool;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/share")
public class ShareController {
    //share file
    @RequestMapping(value = "/{path}", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public ResultShowing shareFiles(@PathVariable("path") String path, @RequestAttribute(LoginInterceptor.LOGIN_USER) User loginUser)
            throws Exception {
        if (StringTool.isNullOrEmpty(path)) {
            throw new CustomException(400, "parameter is null", null);
        } else {
            FileShare f = new FileShare(loginUser, new String(EncodeTool.decoderURLBASE64(path), "utf-8"));
            f = ShareFileService.addNewShareFile(f);
            if (f != null) {
                return new ResultShowing("share file success", f);
            } else {
                throw new CustomException(500, "share file fail", null);
            }
        }
    }

    //search all share list
    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResultShowing getAllShare(@RequestAttribute(LoginInterceptor.LOGIN_USER) User loginUser) throws Exception {
        return new ResultShowing("search all share files success", ShareFileService.searchAllShare(loginUser.getUsername()));
    }

    //cancel share file
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "application/json")
    @ResponseBody
    public ResultShowing cancelShare(@PathVariable String id, @RequestAttribute(LoginInterceptor.LOGIN_USER) User loginUser)
            throws Exception {
        if (ShareFileService.cancelShare(id, loginUser.getUsername())) {
            return new ResultShowing("cancel share success", null);
        }
        throw new CustomException(500, "cancel share fail", null);
    }

    //search all share files
    @RequestMapping(value = "/access", produces = "application/json")
    @ResponseBody
    public ResultShowing getShareFileByID(@RequestAttribute(ShareAccessInterceptor.SHARE_FILE) FileShare fileShare, @RequestParam(value = "parent", required = false) String parentDir) throws Exception {
        String mainDir = User.getUserDirectory(fileShare.getOwner());
        if (mainDir == null) {
            ShareFileService.cancelShare(fileShare.getId(), fileShare.getOwner());
            throw new CustomException(404, "share file canceled", null);
        }
        Path mainPath = Paths.get(mainDir, fileShare.getFilePath());
        if (!Files.exists(mainPath, new LinkOption[]{LinkOption.NOFOLLOW_LINKS})) {
            throw new CustomException(404, "file not exist", null);
        }
        String parent = "";
        if (parentDir != null) {
            parent = new String(EncodeTool.decoderURLBASE64(parentDir), "utf-8");
        }
        String mainPathString = mainPath.toAbsolutePath().toString();
        return new ResultShowing("search all share files success", FileService.getFileList(mainPathString, mainPathString += parent, null, null));
    }
}
