'use strict';

angular.module('capappApp').controller('CommandsDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Commands',
        function($scope, $stateParams, $uibModalInstance, entity, Commands) {

        $scope.commands = entity;
        $scope.load = function(id) {
            Commands.get({id : id}, function(result) {
                $scope.commands = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('capappApp:commandsUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.commands.id != null) {
                Commands.update($scope.commands, onSaveSuccess, onSaveError);
            } else {
                Commands.save($scope.commands, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
}]);
