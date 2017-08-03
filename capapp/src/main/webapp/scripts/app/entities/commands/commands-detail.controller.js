'use strict';

angular.module('capappApp')
    .controller('CommandsDetailController', function ($scope, $rootScope, $stateParams, entity, Commands) {
        $scope.commands = entity;
        $scope.load = function (id) {
            Commands.get({id: id}, function(result) {
                $scope.commands = result;
            });
        };
        var unsubscribe = $rootScope.$on('capappApp:commandsUpdate', function(event, result) {
            $scope.commands = result;
        });
        $scope.$on('$destroy', unsubscribe);

    });
