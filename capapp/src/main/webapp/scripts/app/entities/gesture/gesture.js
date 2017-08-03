'use strict';

angular.module('capappApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('gesture', {
                parent: 'entity',
                url: '/gestures',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'Gestures'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/gesture/gestures.html',
                        controller: 'GestureController'
                    }
                },
                resolve: {
                }
            })
            .state('gesture.detail', {
                parent: 'entity',
                url: '/gesture/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'Gesture'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/gesture/gesture-detail.html',
                        controller: 'GestureDetailController'
                    }
                },
                resolve: {
                    entity: ['$stateParams', 'Gesture', function($stateParams, Gesture) {
                        return Gesture.get({id : $stateParams.id});
                    }]
                }
            })
            .state('gesture.new', {
                parent: 'gesture',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/gesture/gesture-dialog.html',
                        controller: 'GestureDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    gestureName: null,
                                    gestureTrainingStatus: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('gesture', null, { reload: true });
                    }, function() {
                        $state.go('gesture');
                    })
                }]
            })
            .state('gesture.edit', {
                parent: 'gesture',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/gesture/gesture-dialog.html',
                        controller: 'GestureDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Gesture', function(Gesture) {
                                return Gesture.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('gesture', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            })
            .state('gesture.delete', {
                parent: 'gesture',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/gesture/gesture-delete-dialog.html',
                        controller: 'GestureDeleteController',
                        size: 'md',
                        resolve: {
                            entity: ['Gesture', function(Gesture) {
                                return Gesture.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('gesture', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
