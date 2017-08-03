'use strict';

angular.module('capappApp').controller('GestureDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Gesture',
        function($scope, $stateParams, $uibModalInstance, entity, Gesture) {

        $scope.gesture = entity;
        $scope.load = function(id) {
            Gesture.get({id : id}, function(result) {
                $scope.gesture = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('capappApp:gestureUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.gesture.id != null) {
                Gesture.update($scope.gesture, onSaveSuccess, onSaveError);
            } else {
                Gesture.save($scope.gesture, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
}]);
