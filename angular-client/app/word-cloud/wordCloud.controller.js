(function (angular) {
  'use strict';

  angular
    .module('angular-client')
    .controller('WordCloudController', WordCloudController);

  WordCloudController.$inject = ['$scope', '$location'];

  /* @ngInject */
  function WordCloudController($scope, $location) {
    var vm = this;

    $scope.words = [];

    $scope.wordClicked = function (word) {
      alert('text: ' + word.text + ',size: ' + word.size);
    };

    Stomp.WebSocketClass = SockJS;

    var mq_username = "guest",
      mq_password = "guest",
      mq_vhost = "/",
      mq_url = 'http://localhost:15674/stomp',
      mq_queue = "/queue/wordCloud";

    function on_connect() {
      console.log(client);
      client.subscribe(mq_queue, on_message);
    }

    function on_connect_error() {
      console.log('Connection failed!');
    }

    function on_message(m) {
      var message = JSON.parse(m.body);
      var result = true;
      if ($scope.words.length > 1) {
        result = sortAndDeleteIfMoreWordsThan(message, 20);
      }
      if (result) {
        var zname = 1;
        for (var i = 0; i < $scope.words.length; i++) {
          if ($scope.words[i].text == message.text) {
            var newValue = parseInt($scope.words[i].size) + parseInt(message.size);
            $scope.$apply(function () {
              $scope.words[i].size = newValue;
            });
            zname = 0;
          }
        }
        if (zname) {
          $scope.$apply(function () {
            $scope.words.push(message);
          });
        }
      }
    }

    var ws = new SockJS(mq_url);
    var client = Stomp.over(ws);

    (function init() {
      loadMap();
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

    };

    function sortAndDeleteIfMoreWordsThan(incomingMessage, numWords) {
      console.log("GORE ", $scope.words.length);
      $scope.words.sort(function (first, second) {
        return parseInt(first.size) - parseInt(second.size);
      });
      if ($scope.words.length > numWords) {
        if (incomingMessage.size > $scope.words[0].size) {
          if (!containsWord(incomingMessage)) {
            $scope.words.splice(0, 1);
          }
          return true;
        } else if (incomingMessage.size <= $scope.words[0].size) {
          if (containsWord(incomingMessage)) {
            return true;
          }
          return false;
        }
      }
      else {
        return true;
      }
    }

    function containsWord(message) {
      for (var i = 0; i < $scope.words.length; i++) {
        if ($scope.words[i].text == message.text) {
          return true
        }
      }
      return false;
    }

  }
})(angular);

