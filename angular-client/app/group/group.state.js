(function (angular) {
  'use strict';

  angular
    .module('angular-client')
    .config(registerState);


  registerState.$inject = ['$stateProvider'];

  function registerState($stateProvider) {

    $stateProvider.state('group', {
      url: '/groups',
      templateUrl: 'app/group/group.view.html',
      controller: 'GroupController',
      controllerAs: 'vm'
    });
  }

})(angular);
