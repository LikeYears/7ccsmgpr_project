var errorEvent = function (response) {
	toastr.error(response.data);
	if (response.status == 401) {
		setTimeout("window.location.href = 'login.html'", 1000)
	}
}

var app = angular.module('mainPageApp', ['angularFileUpload', 'ngclipboard']);
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

// navbar
app.controller("container", function ($scope, $http) {
    $scope.username = CookieUtil.getCookie("username").toString();
	$("[data-toggle='popover']").popover();
	$scope.uploadlist = new Array();
	$scope.downloadlist = new Array();
	// token
	var token = {};
	token.onebox = CookieUtil.getCookie("onebox");
	$scope.token = token;
	$scope.showCtrl = 3;
	$scope.showCtrl = 1;
	// Files
	$scope.showFileList = function () {
		$scope.showCtrl = 1;
	}
	// Download
	$scope.showTransport = function () {
		$scope.showCtrl = 0;
	}
	// Share
	$scope.showShare = function () {
		$scope.showCtrl = -1;
	}
	// Logout
	$scope.logout = function () {
		$http.delete("api/login/logout", $scope.$parent.token).then(function success(response) {
			if (response) {
				CookieUtil.delCookie("onebox");
				window.location.href = 'login.html'
			}
		}, errorEvent);
	}
});
// Filelist
app.controller("filelist", ["$scope", "$http", "$location", "FileUploader", function ($scope, $http, $location, FileUploader) {
    var uploader = $scope.uploader = new FileUploader({
        url: 'api/files',
        autoUpload: true,
        removeAfterUpload: false
    });
    //upload file success
    uploader.onAfterAddingFile = function (fileItem) {
        fileItem.id = new Date().getTime();
        $scope.uploadlist.push(fileItem);
    };
    //upload error
    uploader.onErrorItem = function (item, response, status, headers) {
        item.errormsg = response;
    }
    // load directory list when move file
    function moveModalLoadDir() {
        var url = "api/files";
        var movefilepath = $scope.FileModal.path;
        var nowdir = $scope.FileModal.dirul[$scope.FileModal.dirul.length - 1].base64FilePath;
        if (nowdir) {
            url = url + "/" + nowdir + "?accepttype=folder&filterfile=" + movefilepath;
        } else {
            url = url + "?accepttype=folder&filterfile=" + movefilepath;
        }
        $http.get(url, $scope.$parent.token).then(function success(response) {
            if (response) {
                $scope.FileModal.filelist = response.data.result;
            }
        }, errorEvent);
    }

    // load the directory
    function loadDir(requrl) {
        if (requrl) {
            requrl = "api/files/" + requrl;
        } else {
            requrl = "api/files";
        }
        $http.get(requrl, $scope.$parent.token).then(function success(response) {
            if (response) {
                $scope.filelist = response.data.result;
                $scope.selectedFile = null;
            }
        }, errorEvent);
    }
    // monitor the page switching
    $scope.$watch("showCtrl", function (showCtrlValue) {
        if (showCtrlValue == 1) {
            loadDir();
            var length = $scope.dirul.length;
            $scope.dirul.splice(1, length);
            $scope.selectedFile = null;
        }
    });
    //  close the dialog of file list
    $('#FileModal').on('hidden.bs.modal', function (e) {
        $scope.filename = null;
        $scope.selectedFile = null;
        $scope.FileModal = null
        $scope.shareFile = null;
    });
    // move file:initial the navbar of path
    var navbar = new Array();
    var file = {};
    file.base64FilePath = "";
    file.name = "Home";
    navbar.push(file);
    $scope.dirul = navbar;
    // move file:enter the specific path
    $scope.enterDir = function (a) {
        loadDir(a.base64FilePath);
        var file = {};
        file.base64FilePath = a.base64FilePath;
        file.name = a.fileName;
        $scope.dirul.push(file);
    }
    // move file:back to the specific path
    $scope.backup = function (li) {
        var index = li.$index;
        var length = $scope.dirul.length;
        $scope.dirul.splice(index + 1, length - index + 1);
        loadDir(li.li.base64FilePath);
    }
    // file list:select
    $scope.select = function (tr) {
        if ($scope.selectedFile == tr) {
            $scope.selectedFile = null;
        } else {
            $scope.selectedFile = tr;
        }
    }
    // file list:cancel select
    $scope.cancelSelect = function () {
        $scope.selectedFile = null;
    }
    // move file:Modal
    $scope.moveFileModal = function () {
        var modal = {};
        modal.title = "Move File：" + $scope.selectedFile.fileName;
        modal.type = "move";
        modal.path = $scope.selectedFile.base64FilePath;
        modal.dirul = new Array();
        // initial navbar of move file Modal
        var file = {};
        file.base64FilePath = "";
        file.fileName = "Home";
        modal.dirul.push(file);
        $scope.FileModal = modal;
        moveModalLoadDir();
        $("#FileModal").modal();
    }

    $scope.moveModalbackup = function (li) {
        var index = li.$index;
        var length = $scope.FileModal.dirul.length;
        $scope.FileModal.dirul.splice(index + 1, length - index + 1);
        moveModalLoadDir();
    }
    $scope.moveModalEnterDir = function (file) {
        $scope.FileModal.dirul.push(file);
        moveModalLoadDir();
    }
    // rename:Modal
    $scope.renameFileModal = function () {
        var modal = {};
        modal.title = "Rename File：" + $scope.selectedFile.fileName;
        modal.type = "rename";
        $scope.FileModal = modal;
        $("#FileModal").modal();
    }
    // delete:Modal
    $scope.delFileModal = function () {
        var modal = {};
        modal.title = "Delete File：" + $scope.selectedFile.fileName;
        modal.type = "del";
        $scope.FileModal = modal;
        $("#FileModal").modal();
    }
    // newfile:Modal
    $scope.newFileModal = function () {
        var modal = {};
        modal.title = "New File：";
        modal.type = "file";
        $scope.FileModal = modal;
        $("#FileModal").modal();
    }
    // newfloder:Modal
    $scope.newDirModal = function () {
        var modal = {};
        modal.title = "New Folder：";
        modal.type = "directory";
        $scope.FileModal = modal;
        $("#FileModal").modal();
    }
    // httpdownload:Modal
    $scope.addDownloadTask = function () {
        $scope.url = null;
        var modal = {};
        modal.title = "HTTP Download：";
        modal.type = "download";
        $scope.FileModal = modal;
        $("#FileModal").modal();
    }
    // 文件分享Modal
    $scope.shareFileModal = function () {
        $http.post("api/share/" + $scope.selectedFile.base64FilePath, $scope.$parent.token, postCfg).then(function (response) {
            if (response) {
                var url = $location.absUrl();
                response.data.result.link = url.substring(0, url.indexOf("main.html")) + "share.html?link=" + response.data.result.id;
                $scope.shareFile = response.data.result;
                var modal = {};
                modal.title = "分享成功！";
                modal.type = "share";
                $scope.FileModal = modal;
                $("#FileModal").modal();
            }
        }, errorEvent);
    }
    //play file
    $scope.playFile = function () {
        window.open('play.html?url=' + $scope.selectedFile.base64FilePath + "&playtype=" + $scope.selectedFile.fileType);
    }
    // download file
    $scope.dlFile = function () {
        window.location.href = 'api/files/' + $scope.selectedFile.base64FilePath + "/download";
    }
    // upload file
    $scope.uploadFile = function () {
        $("#file_upload").trigger('click');
    }
    // submit Modal
    $scope.fileModalSubmit = function (modal) {
        var success = function (response) {
            if (response) {
                toastr.info(response.data.resultdesc);
                loadDir($scope.dirul[$scope.dirul.length - 1].base64FilePath);
                $("#FileModal").modal('hide');
            }
        }
        switch (modal.type) {
            case "del":
                $http.delete("api/files/" + $scope.selectedFile.base64FilePath, $scope.$parent.token, postCfg).then(success,
                    errorEvent);
                break;
            case "file":
            case "directory":
                if (!$scope.filename) {
                    toastr.error("Please enter the name of file or folder");
                    return;
                }
                var newfilename = Base64.encodeURI($scope.filename);
                var nowdir = $scope.dirul[$scope.dirul.length - 1].base64FilePath;
                if (nowdir) {
                    $http.post("api/files/" + nowdir + "/" + modal.type + "/" + newfilename, $scope.$parent.token, postCfg).then(success,
                        errorEvent);
                } else {
                    $http.post("api/files/" + modal.type + "/" + newfilename, $scope.$parent.token, postCfg).then(success,
                        errorEvent);
                }
                break;
            case "rename":
                if (!$scope.filename) {
                    toastr.error("Please enter the name of file or folder");
                    return;
                }
                var newfilename = Base64.encodeURI($scope.filename);
                $http.patch("api/files/" + $scope.selectedFile.base64FilePath + "/" + newfilename, $scope.$parent.token, postCfg).then(success,
                    errorEvent);
                break;
            case "download":
                if (!$scope.url) {
                    toastr.error("Please enter the link");
                    return;
                }
                var url = Base64.encodeURI($scope.url);
                $http.post("api/download/" + url, $scope.$parent.token, postCfg).then(success,
                    errorEvent);
                break;
            case "move":
                var nowdir = $scope.FileModal.dirul[$scope.FileModal.dirul.length - 1].base64FilePath;
                var filepath = $scope.selectedFile.base64FilePath;
                $http.put("api/files/" + filepath + "/" + nowdir, $scope.$parent.token, postCfg).then(success,
                    errorEvent);
                break;
            case "share":
                toastr.info("copy to clipborad success");
                break;
        }
    }
}]);
// Transport
app.controller("transportlist", function ($scope, $http, $interval) {
    var reqFlag = true;

    var p = $interval(function () {
        if (reqFlag) {
            loadDownload();
            reqFlag = false;
        }
    }, 2000);


    // load download tasks
    function loadDownload() {
        $http.get("api/download", $scope.$parent.token).then(function success(response) {
            if (response) {
                $scope.$parent.downloadlist = response.data.result;
                reqFlag = true;
            }
        }, function (response) {
            reqFlag = true;
        });
    }
    // monitor download
    $scope.$watch("downloadlist", function (downloadlist) {
        $scope.dllist = downloadlist;
    });
    // clean upload tasks
    $scope.delUpload = function (ts) {
        ts.cancel();
        ts.remove();
        var uploadArray = $scope.$parent.uploadlist;
        for (var i = 0; i < uploadArray.length; i++) {
            if (uploadArray[i].id == ts.id) {
                uploadArray.splice(i, 1);
            }
        }
    }
    var success = function (response) {
        if (response) {
            toastr.info(response.data.resultdesc);
        }
    }
    // stop download
    $scope.stopDownload = function (dl) {
        $http.patch("api/download/" + dl.taskid, $scope.$parent.token).then(success, errorEvent);
    }
    // retry download
    $scope.retryDownload = function (dl) {
        $http.put("api/download/" + dl.taskid, $scope.$parent.token).then(success, errorEvent);
    }
    // delete dowoload
    $scope.delDownload = function (dl) {
        $http.delete("api/download/" + dl.taskid, $scope.$parent.token).then(success, errorEvent);
    }
});
// share list
app.controller("sharelist", function ($scope, $http, $location) {
    function loadShareList() {
        $http.get("api/share", $scope.$parent.token).then(function success(response) {
            if (response) {
                var url = $location.absUrl();
                url = url.substring(0, url.indexOf("main.html")) + "share.html?link=";
                var result = response.data.result;
                for (var i = 0; i < result.length; i++) {
                    result[i].link = url + result[i].id;
                }
                $scope.sharelist = result;
            }
        }, errorEvent);
    }

    // page change
    $scope.$watch("showCtrl", function (showCtrlValue) {
        if (showCtrlValue == -1) {
            loadShareList();
        }
    });
    // cancel share
    $scope.cancelShare = function (file) {
        $http.delete("api/share/" + file.id, $scope.$parent.token).then(function success(response) {
            if (response) {
                toastr.info(response.data.resultdesc);
                loadShareList();
            }
        }, errorEvent);
    }

    // copy share link
    $scope.copyShareLink = function () {
        toastr.info("copy to clipborad success");
    }
});
