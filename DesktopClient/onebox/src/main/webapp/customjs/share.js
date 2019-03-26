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

// Main Controller
app.controller("container", function ($scope, $http) {
    $scope.shareid = getUrlParam('link');

    //get url parameter
    function getUrlParam(name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
        var r = window.location.search.substr(1).match(reg);
        if (r != null)
            return unescape(r[2]);
        return null;
    }
    //load directory
    var array;
    function loadDir(parentdir) {
        var data = {
            "shareid": $scope.shareid,
            "password": $scope.password,
            "parent": parentdir
        }
        $http.post("api/share/access", data, postCfg).then(function (response) {
            $scope.showFile = true;
            $scope.filelist = response.data.result;
            array = response.data.resultInDesc.split("-");
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
    // initial navbar for path
    var navbar = new Array();
    var file = {};
    file.base64FilePath = "";
    file.name = "Home";
    navbar.push(file);
    $scope.dirul = navbar;
    //request share file
    $scope.extractAuth = function () {
        loadDir("");
    }

    // enter the directory
    $scope.enterDir = function (a) {
        loadDir(a.base64FilePath);
        var file = {};
        file.base64FilePath = a.base64FilePath;
        file.name = a.fileName;
        $scope.dirul.push(file);
        $scope.buttonShow = false;
    }
    // back the directory
    $scope.backup = function (li) {
        var index = li.$index;
        var length = $scope.dirul.length;
        $scope.dirul.splice(index + 1, length - index + 1);
        loadDir(li.li.base64FilePath);
        $scope.buttonShow = false;
    }
    // select on list
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
    // cancel select
    $scope.cancelSelect = function (file) {
        $scope.selectedFile = null;
        $scope.buttonShow = false;
    }

    $scope.download = function(){
        // var path64 = Base64.encode(array[1]+Base64.decode($scope.selectedFile.base64FilePath));
        // window.location.href = 'api/files/' + path64 + "/sharedownload"+"&owner="+Base64.encode(array[0]);
        toastr.error("Share file has been timeout")
    }
});

