package com.oasis.onebox.controller;

import com.oasis.onebox.download.DownloadTask;
import com.oasis.onebox.tool.RSA;
import com.oasis.onebox.tool.ResultShowing;
import com.oasis.onebox.tool.StringTool;
import com.oasis.onebox.entity.User;
import com.oasis.onebox.exception.CustomException;
import com.oasis.onebox.interceptor.LoginInterceptor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/login")
public class LoginController {

    //send public key to front end
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResultShowing getpubkey() {
        return new ResultShowing("", RSA.getInstance().getPubKey());
    }

    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public ResultShowing auth(HttpServletRequest request) throws CustomException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        if (StringTool.isNullOrEmpty(username) || StringTool.isNullOrEmpty(password)) {
            throw new CustomException(401, "username or password is null", null);
        }
        username = RSA.getInstance().decryptByPriKey(username);
        password = RSA.getInstance().decryptByPriKey(password);
        User u = User.loginAuth(username, password);
        if (u != null) {
            String token = u.createToken(request);
            return new ResultShowing("login success", token);
        }
        throw new CustomException(401, "username or password not correct", null);
    }


    @RequestMapping(value = "/logout", method = RequestMethod.DELETE, produces = "application/json")
    @ResponseBody
    public ResultShowing logout(@RequestAttribute(LoginInterceptor.LOGIN_USER) User loginUser) {
        loginUser.setToken(null);
        loginUser.setExpirationtime(-1);
        return new ResultShowing("ok", null);
    }
}