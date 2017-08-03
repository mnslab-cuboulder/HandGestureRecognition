'use strict';

angular.module('capappApp')
	.controller('GestureDeleteController', function($scope, $uibModalInstance, entity, Gesture) {

        $scope.gesture = entity;
        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.confirmDelete = function (id) {
            Gesture.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };

    });
