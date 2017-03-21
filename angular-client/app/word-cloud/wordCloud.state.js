(function (angular) {
  'use strict';

  angular
    .module('angular-client')
    .config(registerState);

  registerState.$inject = ['$stateProvider'];

  function registerState($stateProvider) {

    $stateProvider.state('wordCloud', {
      url: '/wordCloud',
      templateUrl: 'app/word-cloud/wordCloud.view.html',
      controller: 'WordCloudController',
      controllerAs: 'vm'
    });
  }

})(angular);
