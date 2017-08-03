'use strict';

angular.module('capappApp')
    .controller('GestureDetailController', function ($scope, $rootScope, $stateParams, entity, Gesture) {
        $scope.gesture = entity;
        $scope.load = function (id) {
            Gesture.get({id: id}, function(result) {
                $scope.gesture = result;
            });
        };
        var unsubscribe = $rootScope.$on('capappApp:gestureUpdate', function(event, result) {
            $scope.gesture = result;
        });
        $scope.$on('$destroy', unsubscribe);

    });
