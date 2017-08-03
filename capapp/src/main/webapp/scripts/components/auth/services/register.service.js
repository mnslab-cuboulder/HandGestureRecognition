'use strict';

angular.module('capappApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


