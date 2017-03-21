(function (angular) {
  'use strict';

  angular
    .module('angular-client')
    .controller('FlinkController', FlinkController);

  FlinkController.$inject = ['$scope', '$log', '$http'];

  /* @ngInject */
  function FlinkController($scope, $log, $http) {
    var vm = this;
    var map;
    var tweetCounter = 0;
    $scope.textArea = "";
    $scope.counter = 0;
    $scope.processedTweets = [];
    $scope.locationCounter = 0;
    var counterLocationTweets = 0;
    var bombs = [];
    $scope.tweetsPerCountry = [];

    Stomp.WebSocketClass = SockJS;

    var mq_username = "guest",
      mq_password = "guest",
      mq_vhost = "/",
      mq_url = 'http://localhost:15674/stomp',
      mq_queue = "/queue/positions6";

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
    })();

    function loadMap() {
      map = new Datamap({
        element: document.getElementById('container'),
        done: function (datamap) {
          datamap.svg.selectAll('.datamaps-subunit').on('click', function (geography) {
            alert(geography.properties.name);
            console.log(geography);
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
      if ($scope.processedTweets.length > 100) {
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
  }
})(angular);

