var CookieUtil = {
    delCookie : function(key) {
        var exp = new Date();
        exp.setTime(exp.getTime() - 1);
        var cval = CookieUtil.getCookie(name);
        if (cval != null) {
            document.cookie = key + "=" + cval + ";expires="
                + exp.toGMTString();
        }
    },
    setCookie : function(key, value) {
        CookieUtil.delCookie(key);
        var exp = new Date();
        exp.setTime(exp.getTime() + 30*60*1000);
        document.cookie = key + "=" + value + ";expires="+ exp.toGMTString();
    },
    getCookie : function(key) {
        var arr, reg = new RegExp("(^| )" + key + "=([^;]*)(;|$)");
        if (arr = document.cookie.match(reg))
            return unescape(arr[2]);
        else
            return null;
    }
}