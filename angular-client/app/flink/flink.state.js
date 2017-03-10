(function (angular) {
  'use strict';

  angular
    .module('angular-client')
    .config(registerState);

  registerState.$inject = ['$stateProvider'];

  function registerState($stateProvider) {

    $stateProvider.state('flink', {
      url: '/flink',
      templateUrl: 'app/flink/flink.view.html',
      controller: 'FlinkController',
      controllerAs: 'vm'
    });
  }

})(angular);
