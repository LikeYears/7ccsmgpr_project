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
    $scope.username = CookieUtil.getCookie("username");
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
