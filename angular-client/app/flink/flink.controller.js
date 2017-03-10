(function (angular) {
  'use strict';

  angular
    .module('angular-client')
    .controller('FlinkController', FlinkController);

  FlinkController.$inject = ['$scope', '$log'];

  /* @ngInject */
  function FlinkController($scope, $log) {
    var vm = this;

    // Use SockJS
    Stomp.WebSocketClass = SockJS;

    // Connection parameters
    var mq_username = "guest",
      mq_password = "guest",
      mq_vhost = "/",
      mq_url = 'http://localhost:15674/stomp',
      mq_queue = "/queue/twitterData";

    var output;

    function on_connect() {
      output.innerHTML += 'Connected to RabbitMQ-Web-Stomp<br />';
      console.log(client);
      client.subscribe(mq_queue, on_message);
    }

    function on_connect_error() {
      output.innerHTML += 'Connection failed!<br />';
    }

    function on_message(m) {
      console.log('message received');
      console.log(m);
      output.innerHTML += m.body + '<br />';
    }

    var ws = new SockJS(mq_url);
    var client = Stomp.over(ws);

    window.onload = function () {

      output = document.getElementById("output");

      client.connect(
        mq_username,
        mq_password,
        on_connect,
        on_connect_error,
        mq_vhost);

      // var map = new Datamap({element: document.getElementById('container')});

    }
  }
})(angular);

