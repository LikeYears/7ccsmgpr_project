var app = angular.module('registerFormApp', []);

var postCfg = {
    headers : {
        'Content-Type' : 'application/x-www-form-urlencoded'
    },
    transformRequest : function(data) {
        return $.param(data);
    }
};
app.controller("register", function($scope, $http) {
    $scope.registerUser = function() {
        var requrl = "api/register";
        $http.get(requrl, null).then(function success(response) {
            if (response) {
                var data = {};
                data.username = RSAEncrypt.encrypt(response.data.result, $scope.username);
                data.password = RSAEncrypt.encrypt(response.data.result, $scope.password);
                $http.post(requrl, data, postCfg).then(function success(response) {
                    if (response) {
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
