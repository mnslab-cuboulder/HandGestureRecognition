'use strict';

angular.module('capappApp')
    .factory('Gesture', function ($resource, DateUtils) {
        return $resource('api/gestures/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    });
