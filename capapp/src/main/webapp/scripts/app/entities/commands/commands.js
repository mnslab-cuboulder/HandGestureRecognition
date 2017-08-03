'use strict';

angular.module('capappApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('commands', {
                parent: 'entity',
                url: '/commandss',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'Commandss'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/commands/commandss.html',
                        controller: 'CommandsController'
                    }
                },
                resolve: {
                }
            })
            .state('commands.detail', {
                parent: 'entity',
                url: '/commands/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'Commands'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/commands/commands-detail.html',
                        controller: 'CommandsDetailController'
                    }
                },
                resolve: {
                    entity: ['$stateParams', 'Commands', function($stateParams, Commands) {
                        return Commands.get({id : $stateParams.id});
                    }]
                }
            })
            .state('commands.new', {
                parent: 'commands',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/commands/commands-dialog.html',
                        controller: 'CommandsDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    id: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('commands', null, { reload: true });
                    }, function() {
                        $state.go('commands');
                    })
                }]
            })
            .state('commands.edit', {
                parent: 'commands',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/commands/commands-dialog.html',
                        controller: 'CommandsDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Commands', function(Commands) {
                                return Commands.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('commands', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            })
            .state('commands.delete', {
                parent: 'commands',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/commands/commands-delete-dialog.html',
                        controller: 'CommandsDeleteController',
                        size: 'md',
                        resolve: {
                            entity: ['Commands', function(Commands) {
                                return Commands.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('commands', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
