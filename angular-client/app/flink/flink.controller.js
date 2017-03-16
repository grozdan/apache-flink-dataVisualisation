(function (angular) {
  'use strict';

  angular
    .module('angular-client')
    .controller('FlinkController', FlinkController);

  FlinkController.$inject = ['$scope', '$log'];

  /* @ngInject */
  function FlinkController($scope, $log) {
    var vm = this;
    var map;
    var tweetCounter = 0;
    $scope.textArea = "";
    $scope.counter = 0;
    $scope.processedTweets = [];
    $scope.locationCounter = 0;
    var counterLocationTweets = 0;
    var bombs = [];

    Stomp.WebSocketClass = SockJS;

    var mq_username = "guest",
      mq_password = "guest",
      mq_vhost = "/",
      mq_url = 'http://localhost:15674/stomp',
      mq_queue = "/queue/positions4";

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

      var newBuuble = createBubble(message);
      if (newBuuble != undefined) {
        bombs.push(newBuuble);

        map.bubbles(bombs);
      }
    }

    var ws = new SockJS(mq_url);
    var client = Stomp.over(ws);

    window.onload = function () {
      // var txtArea = document.getElementById('textArea');
      // txtArea.scrollTop = txtArea.scrollHeight;
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

    };

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
        var newBuuble = {};
        newBuuble.name = message.name;
        newBuuble.radius = message.radius;
        newBuuble.longitude = message.longitude;
        newBuuble.latitude = message.latitude;
        newBuuble.borderColor = "#ff0000";
        return newBuuble;
      }
      return undefined;
    }

    function getTextForTweets() {

      var text = "";
      for (var i = 0; i < $scope.processedTweets.length; i++) {
        text += $scope.processedTweets[i] + '\n';
      }
      return text
    }
  }
})(angular);

