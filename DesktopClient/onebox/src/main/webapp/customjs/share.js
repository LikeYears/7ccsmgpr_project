var app = angular.module('sharePageApp', []);
var postCfg = {
    headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
    },
    transformRequest: function (data) {
        return $.param(data);
    }
};
app.directive('fallbackSrc', function () {
    var fallbackSrc = {
        link: function postLink(scope, iElement, iAttrs) {
            iElement.bind('error', function () {
                angular.element(this).attr("src", iAttrs.fallbackSrc);
            });
        }
    }
    return fallbackSrc;
});

// 主面板
app.controller("container", function ($scope, $http) {
    $scope.shareid = getUrlParam('link');

    //获取url参数
    function getUrlParam(name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
        var r = window.location.search.substr(1).match(reg);
        if (r != null)
            return unescape(r[2]);
        return null;
    }
    //加载目录
    function loadDir(parentdir) {
        var data = {
            "shareid": $scope.shareid,
            "password": $scope.password,
            "parent": parentdir
        }
        $http.post("api/share/access", data, postCfg).then(function (response) {
            $scope.showFile = true;
            $scope.filelist = response.data.result;
        },
        function (response) {
            if (response.status == 401) {
                $scope.password = null;
                toastr.error(response.data);
            } else if (response.status == 404) {
                $scope.cancel = true;
            } else {
                toastr.error(response.data);
            }
        });
    }

    $scope.cancel = false;
    $scope.showFile = false;
    $scope.buttonShow = false;
    // 初始化目录导航
    var navbar = new Array();
    var file = {};
    file.base64FilePath = "";
    file.name = "主目录";
    navbar.push(file);
    $scope.dirul = navbar;
    //请求分享文件
    $scope.extractAuth = function () {
        loadDir("");
    }

    // 进入指定目录
    $scope.enterDir = function (a) {
        loadDir(a.base64FilePath);
        var file = {};
        file.base64FilePath = a.base64FilePath;
        file.name = a.fileName;
        $scope.dirul.push(file);
        $scope.buttonShow = false;
    }
    // 返回到指定目录
    $scope.backup = function (li) {
        var index = li.$index;
        var length = $scope.dirul.length;
        $scope.dirul.splice(index + 1, length - index + 1);
        loadDir(li.li.base64FilePath);
        $scope.buttonShow = false;
    }
    //列表选中    
    $scope.select = function (file) {
        if ($scope.selectedFile == file) {
            $scope.selectedFile = null;
            $scope.buttonShow = false;
        } else{
            if(!file.dir){
                $scope.selectedFile = file;
                $scope.buttonShow = true;
            } else{
                $scope.buttonShow = false;
            }
        }

    }
    //取消选择
    $scope.cancelSelect = function (file) {
        $scope.selectedFile = null;
        $scope.buttonShow = false;
    }

    $scope.download = function(){

    }
});

