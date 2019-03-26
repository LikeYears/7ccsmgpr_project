package com.oasis.onebox.controller;

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
@RequestMapping("/register")
public class RegisterController {

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
        boolean u = User.registerUser(username, password);
        if (u) {
            //There is no need to use token for persistence
			//String token = user.createToken(request);
            return new ResultShowing("register success", "1");
        }else{
            throw  new CustomException(401, "the username may have been used", null);
        }

    }

}



