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
                        window.location.href = "login.html";
                        window.alert("Register Success!")
                    }
                }, function error(response) {
                    toastr.error("Username Already Exist");
                });
            } else {
                toastr.error("Fail To Connect Server");
            }
        }, function error(response) {
            toastr.error("Fail To Connect Server:"+response.data);
        });
    }
});
