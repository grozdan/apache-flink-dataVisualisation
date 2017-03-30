(function (angular) {
  'use strict';

  angular
    .module('angular-client')
    .controller('FlinkController', FlinkController);

  FlinkController.$inject = ['$scope', '$log', '$http', '$state', '$rootScope'];

  /* @ngInject */
  function FlinkController($scope, $log, $http, $state, $rootScope) {
    var vm = this;
    var map;
    var tweetCounter = 0;
    $scope.textArea = "";
    $scope.counter = 0;
    $scope.processedTweets = [];
    $scope.locationCounter = 0;
    var counterLocationTweets = 0;
    var bombs = [];
    var oldBombs = [];
    $scope.tweetsPerCountry = [];

    Stomp.WebSocketClass = SockJS;

    var mq_username = "guest",
      mq_password = "guest",
      mq_vhost = "/",
      mq_url = 'http://localhost:15674/stomp',
      mq_queue = "/queue/positions8";

    function on_connect() {
      console.log(client);
      client.subscribe(mq_queue, on_message);
    }

    function on_connect_error() {
      console.log('Connection failed!');
    }

    function on_message(m) {
      var message = JSON.parse(m.body);
      tweetCounter += 1;
      $scope.$apply(function () {
        $scope.counter = tweetCounter;
      });

      createBubble(message);
    }

    var ws = new SockJS(mq_url);
    var client = Stomp.over(ws);

    (function init() {
      loadMap();
      loadTweetsFromDatabase();
    })();

    function loadMap() {
      map = new Datamap({
        element: document.getElementById('container'),
        fills: {
          defaultFill: "#000033",
          authorHasTraveledTo: "#00b300"
        },
        done: function (datamap) {
          datamap.svg.selectAll('.datamaps-subunit').on('click', function (geography) {
            var countryName = geography.properties.name;
            var obj = {};
            obj.country = countryName;
            $rootScope.country = countryName
            $state.go('wordCloud', obj);
          });
        }
      });
      map.bubbles(bombs);

      client.heartbeat.outgoing = 0;
      client.heartbeat.incoming = 0;
      client.connect(
        mq_username,
        mq_password,
        on_connect,
        on_connect_error,
        mq_vhost);

    }

    function createBubble(message) {
      $scope.processedTweets.push(message.name);
      if ($scope.processedTweets.length > 50) {
        $scope.processedTweets.splice(0, 1);
      }

      $scope.$apply(function () {
        $scope.textArea = getTextForTweets();
      });

      if (message.latitude != undefined) {
        counterLocationTweets += 1;
        $scope.$apply(function () {
          $scope.locationCounter = counterLocationTweets;
        });
        var url = encodeURI('http://ws.geonames.org/countryCodeJSON?lat=' + message.latitude + '&lng='
          + message.longitude + '&username=goki');

        var newBubble = {};
        newBubble.name = message.name;
        newBubble.radius = message.radius;
        newBubble.longitude = message.longitude;
        newBubble.latitude = message.latitude;
        newBubble.borderColor = "#ff0000";
        $http({
          method: 'GET',
          url: url
        }).then(function successCallback(response) {
          newBubble.country = response.data.countryName;
          console.log(newBubble);
          bombs.push(newBubble);
          addToCountryMap(newBubble);
          map.bubbles(bombs);
        }, function errorCallback(response) {
          bombs.push(newBubble);
          map.bubbles(bombs);
          console.log(response);
          console.log(newBubble);
        });
      }
    }

    // $scope.clickBtn = function () {
    //   console.log("called");
    //   $http({
    //     method: 'get',
    //     url: '/api/country_tweets'
    //   }).then(function successCallback(response) {
    //     console.log(response);
    //   }, function errorCallback(response) {
    //     console.log(response);
    //   });
    // };

    function getTextForTweets() {
      var text = "";
      for (var i = 0; i < $scope.processedTweets.length; i++) {
        text += $scope.processedTweets[i] + '\n';
      }
      return text
    }

    function addToCountryMap(newBubble) {
      var zname = true;
      var country = {};
      country.name = newBubble.country;

      for (var i = 0; i < $scope.tweetsPerCountry.length; i++) {
        if ($scope.tweetsPerCountry[i].name == newBubble.country) {
          var currentSize = parseInt($scope.tweetsPerCountry[i].size);
          $scope.tweetsPerCountry.splice(i, 1);
          country.size = currentSize + 1;
          zname = false;
        }
      }
      if (zname) {
        country.size = 1;
      }
      $scope.tweetsPerCountry.push(country);

      $scope.tweetsPerCountry.sort(function (first, second) {
        if (parseInt(second.size) != parseInt(first.size)) {
          return parseInt(second.size) - parseInt(first.size);
        }
        return first.name - second.name;
      });

    }

    function loadTweetsFromDatabase() {
      var url = encodeURI('api/country_tweets');
      $http({
        method: 'GET',
        url: url
      }).then(function successCallback(response) {
        for (var i = 0; i < response.data.length; i++) {
          var newBubble = {};
          newBubble.name = response.data[i].tweet;
          newBubble.radius = response.data[i].radius;
          newBubble.longitude = response.data[i].longitude;
          newBubble.latitude = response.data[i].latitude;
          newBubble.country = response.data[i].country;
          newBubble.borderColor = "#ffff33";
          console.log(newBubble);
          bombs.push(newBubble);
          counterLocationTweets += 1;
          $scope.locationCounter = counterLocationTweets;
          addToCountryMap(newBubble);
          map.bubbles(bombs);
        }
      }, function errorCallback(response) {

        console.log(response);

      });
    }
  }
})(angular);

