var app = angular.module('loginFormApp', []);

var postCfg = {
    headers : {
        'Content-Type' : 'application/x-www-form-urlencoded'
    },
    transformRequest : function(data) {
        return $.param(data);
    }
};
app.controller("login", function($scope, $http) {
    $scope.loginAuth = function() {
        var requrl = "api/login";
        $http.get(requrl, null).then(function success(response) {
            if (response) {
                var data = {};
                data.username = RSAEncrypt.encrypt(response.data.result, $scope.username);
                data.password = RSAEncrypt.encrypt(response.data.result, $scope.password);
                $http.post(requrl, data, postCfg).then(function success(response) {
                    if (response) {
                        toastr.success("Login Success!");
                        CookieUtil.delCookie("onebox");
                        CookieUtil.setCookie("onebox", response.data.result);
                        CookieUtil.setCookie("username", $scope.username);
                        window.location.href = "main.html";
                    }
                }, function error(response) {
                    toastr.error(response.data);
                });
            } else {
                toastr.error("ERROR");
            }
        }, function error(response) {
            toastr.error("ERROR");
        });
    }
});
