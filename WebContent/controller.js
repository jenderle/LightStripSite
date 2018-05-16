var lightApp = angular.module('lightApp', ['ui.bootstrap']);
lightApp.controller('busController', function ($scope, $http) {
    'use strict';
    $scope.ngItems = [];

    $scope.ngColor = '#FFFFFF';
    $scope.ngDisplayTime = 1000;
    $scope.ngTransitionTime = 1000;
    $scope.ngTransitionType = 'type1';   

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
        $scope.alerts = [];
        $scope.alerts.push({ type: 'success', msg: 'Animation data sent!' });
    }

    function handleError(response) {
        console.log(response);
        $scope.alerts = [];
        $scope.alerts.push({ type: 'danger', msg: 'Unable to send animation data.' });
    }
    
    $scope.closeAlert = function(index) {
        $scope.alerts.splice(index, 1);
    };

});
