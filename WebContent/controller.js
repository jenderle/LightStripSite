var lightApp = angular.module('lightApp', ['ui.bootstrap']);
lightApp.controller('busController', function ($scope, $http) {
    'use strict';
    $scope.ngItems = [];

    $scope.addItem = function() {
		addItem();
    };

    $scope.sendItems = function() {
		sendItems();
	};

    var addItem = function () {
        var animationItem = {};
        animationItem.color = $scope.ngColor;
        animationItem.displayTime = $scope.ngDisplayTime;
        animationItem.transitionTime = $scope.ngTransitionTime;
        animationItem.transitionType = $scope.ngTransitionType;

        $scope.ngItems.push(animationItem);
    };

    var sendItems = function() {
        $http({
            method: 'POST',
            url: './LightWebServlet',
            data: JSON.stringify($scope.ngItems),
            headers: {'Content-Type': 'application/json'}
        }).then(function mySuccess(response) {
			handleSuccess(response);
		}, function myError(response) {
			handleError(response);
		});
    };

    function handleSuccess(response) {
        console.log(response);
    }

    function handleError(response) {
        console.log(response);
    }

});
