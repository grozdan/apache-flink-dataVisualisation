(function (angular) {
  'use strict';

  angular
    .module('angular-client')
    .controller('FlinkController', FlinkController);

  FlinkController.$inject = ['$scope', '$window', '$log', '$http', '$state', '$rootScope'];

  /* @ngInject */
  function FlinkController($scope, $window, $log, $http, $state, $rootScope) {
    var vm = this;
    var map;
    var tweetCounter = 0;
    $scope.textArea = "";
    $scope.counter = 0;
    $scope.processedTweets = [];
    $scope.counterInDatabase = 0;
    $scope.total = 0
    var counterLocationTweets = 0;
    var bombs = [];
    var i = 1;
    $scope.tweetsPerCountry = [];
    $scope.tweetsPerCountryPom = []

    Stomp.WebSocketClass = SockJS;

    var mq_username = "guest",
      mq_password = "guest",
      mq_vhost = "/",
      mq_url = 'http://localhost:15674/stomp',
      mq_queue = "/queue/positions17";

    function on_connect() {
      console.log(client);
      client.subscribe(mq_queue, on_message);
    }

    function on_connect_error() {
      console.log('Connection failed!');
    }

    function on_message(m) {
      var message = JSON.parse(m.body);
      $scope.total++;
      createBubble(message);
      console.log(i++ + ". " + message);
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
          //defaultFill: "#000033",
          defaultFill: "#07141f"
        },
        done: function (datamap) {
          datamap.svg.selectAll('.datamaps-subunit').on('click', function (geography) {
            var countryName = geography.properties.name;
            var obj = {};
            obj.country = countryName;
            $rootScope.country = countryName;
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
      // $scope.processedTweets.push(message.name);  text area code
      // if ($scope.processedTweets.length > 50) {
      //   $scope.processedTweets.splice(0, 1);
      // }
      //
      // $scope.$apply(function () {
      //   $scope.textArea = getTextForTweets();
      // });

      if (message.latitude != undefined) {
        counterLocationTweets += 1;
        var url = encodeURI('http://ws.geonames.org/countryCodeJSON?lat=' + message.latitude + '&lng='
          + message.longitude + '&username=goki');

        var newBubble = {};
        newBubble.name = message.name;
        if (bombs.length <= 300) {
          newBubble.radius = 4;
        }
        else if (bombs.length > 300) {
          newBubble.radius = 3;
        }

        newBubble.longitude = message.longitude;
        newBubble.latitude = message.latitude;
        newBubble.borderColor = "#ff0000";
        $http({
          method: 'GET',
          url: url
        }).then(function successCallback(response) {
          newBubble.country = response.data.countryName;
          // console.log(newBubble);
          bombs.push(newBubble);
          addToCountryMap(newBubble);
          map.bubbles(bombs);
        }, function errorCallback(response) {
          //if country is not found
          bombs.push(newBubble);
          map.bubbles(bombs);
          // console.log(response);
          // console.log(newBubble);
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

    function loadTweetsFromDatabase() {
      var url = encodeURI('api/country_tweets');
      $http({
        method: 'GET',
        url: url
      }).then(function successCallback(response) {
        $scope.total += response.data.length;
        for (var i = 0; i < response.data.length; i++) {
          var newBubble = {};
          newBubble.name = response.data[i].tweet;
          if (response.data.length <= 300) {
            newBubble.radius = 4;
          }
          else if (response.data.length > 300) {
            newBubble.radius = 3;
          }
          newBubble.longitude = response.data[i].longitude;
          newBubble.latitude = response.data[i].latitude;
          newBubble.country = response.data[i].country;
          newBubble.borderColor = "#ffffff";//#ffff33
          // console.log(newBubble);
          bombs.push(newBubble);
          addToCountryMap(newBubble);
        }
        map.bubbles(bombs);
      }, function errorCallback(response) {
        console.log(response);
      });
    }

    $window.onunload = function () {
      var url = encodeURI('api/country_tweets/clear_tweets');
      $http({
        method: 'GET',
        url: url
      }).then(function successCallback(response) {

      }, function errorCallback(response) {
        console.log(response);
      });
    }
  }
})(angular);

