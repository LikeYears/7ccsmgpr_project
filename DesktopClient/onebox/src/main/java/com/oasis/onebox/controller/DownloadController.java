package com.oasis.onebox.controller;

import com.oasis.onebox.download.DownloadTask;
import com.oasis.onebox.entity.User;
import com.oasis.onebox.exception.CustomException;
import com.oasis.onebox.interceptor.LoginInterceptor;
import com.oasis.onebox.tool.EncodeTool;
import com.oasis.onebox.tool.ResultShowing;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RequestMapping("/download")
@Controller
public class DownloadController {

    //get the list of download tasks
    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResultShowing getDownloadList(@RequestAttribute(LoginInterceptor.LOGIN_USER) User loginUser) throws Exception {
        return new ResultShowing("get download list success", DownloadTask.getDownloadStatus(loginUser.getUsername()));
    }

    //add download task
    @RequestMapping(value = "/{url}", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public ResultShowing addDownloadTask(@PathVariable("url") String url,
                              @RequestAttribute(LoginInterceptor.LOGIN_USER) User loginUser) throws Exception {
        String taskID = DownloadTask.addDownloadTask(loginUser.getUsername(), new String(EncodeTool.decoderURLBASE64(url)));
        return new ResultShowing("add download task success", taskID);
    }

    //stop download
    @RequestMapping(value = "/{taskid}", method = RequestMethod.PATCH, produces = "application/json")
    @ResponseBody
    public ResultShowing stopDownload(@PathVariable("taskid") String taskid,
                           @RequestAttribute(LoginInterceptor.LOGIN_USER) User loginUser) throws Exception {
        if (DownloadTask.stopDownload(loginUser.getUsername(), taskid)) {
            return new ResultShowing("stop download success", null);
        } else {
            throw new CustomException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "stop download fail", null);
        }
    }

    //retry download
    @RequestMapping(value = "/{taskid}", method = RequestMethod.PUT, produces = "application/json")
    @ResponseBody
    public ResultShowing retryDownload(@PathVariable("taskid") String taskid,
                            @RequestAttribute(LoginInterceptor.LOGIN_USER) User loginUser) throws Exception {
        if (DownloadTask.retryDownload(loginUser.getUsername(), taskid)) {
            return new ResultShowing("retry download success", null);
        } else {
            throw new CustomException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "retry download fail", null);
        }
    }

    //delete download
    @RequestMapping(value = "/{taskid}", method = RequestMethod.DELETE, produces = "application/json")
    @ResponseBody
    public ResultShowing deleteDownload(@PathVariable("taskid") String taskid,
                             @RequestAttribute(LoginInterceptor.LOGIN_USER) User loginUser) throws Exception {
        if (DownloadTask.deleteDownload(loginUser.getUsername(), taskid)) {
            return new ResultShowing("delete download success", null);
        } else {
            throw new CustomException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "delete download fail", null);
        }
    }
}
