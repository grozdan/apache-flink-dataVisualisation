(function (angular) {
  'use strict';

  angular
    .module('angular-client')
    .controller('WordCloudController', WordCloudController);

  WordCloudController.$inject = ['$scope', '$stateParams', '$state', '$rootScope', '$http'];

  function WordCloudController($scope, $stateParams, $state, $rootScope, $http) {
    var vm = this;
    $scope.words = [];

    Stomp.WebSocketClass = SockJS;
    var mq_username = "guest",
      mq_password = "guest",
      mq_vhost = "/",
      mq_url = 'http://localhost:15674/stomp',
      mq_queue = "/queue/wordCloud4";

    function on_connect() {
      console.log(client);
      client.subscribe(mq_queue, on_message);
    }

    function on_connect_error() {
      console.log('Connection failed!');
    }

    function on_message(m) {
      var jsonObj = JSON.parse('[' + m.body + ']');
      $scope.words = [];
      $scope.$apply(function () {
        $scope.words = jsonObj;
      });
    }

    var ws = new SockJS(mq_url);
    var client = Stomp.over(ws);

    (function init() {
      var country = $rootScope.country;
      $rootScope.country = undefined;
      if (country != undefined) {
        var url = encodeURI('api/country_tweets/get_tweets_for_country?country=' + country);
        $http({
          method: 'GET',
          url: url
        }).then(function successCallback(response) {
          $scope.words = [];
          for (var i = 0; i < response.data.length; i++) {
            $scope.words.push(JSON.parse(response.data[i]));
          }
          console.log('WORDS', $scope.words);
        }, function errorCallback(response) {
          console.log('ERROR');
        });
      } else {
        loadMap();
      }

    })();

    function loadMap() {
      client.heartbeat.outgoing = 0;
      client.heartbeat.incoming = 0;
      client.connect(
        mq_username,
        mq_password,
        on_connect,
        on_connect_error,
        mq_vhost);
    }
  }
})(angular);

