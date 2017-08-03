'use strict';

angular.module('capappApp')
	.controller('CommandsDeleteController', function($scope, $uibModalInstance, entity, Commands) {

        $scope.commands = entity;
        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.confirmDelete = function (id) {
            Commands.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };

    });
