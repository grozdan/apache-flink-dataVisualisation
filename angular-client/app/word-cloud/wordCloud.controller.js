(function (angular) {
  'use strict';

  angular
    .module('angular-client')
    .controller('WordCloudController', WordCloudController);

  WordCloudController.$inject = ['$scope', '$timeout', '$stateParams', '$state', '$rootScope', '$http'];

  function WordCloudController($scope, $timeout, $stateParams, $state, $rootScope, $http) {
    var vm = this;
    $scope.words = [];
    $scope.barChartData = [];
    $scope.slider = {};
    $scope.selectedCountry = "";

    // Stomp.WebSocketClass = SockJS;
    // var mq_username = "guest",
    //   mq_password = "guest",
    //   mq_vhost = "/",
    //   mq_url = 'http://localhost:15674/stomp',
    //   mq_queue = "/queue/wordCloud4";

    // function on_connect() {
    //   console.log(client);
    //   client.subscribe(mq_queue, on_message);
    // }
    //
    // function on_connect_error() {
    //   console.log('Connection failed!');
    // }
    //
    // function on_message(m) {
    //   var jsonObj = JSON.parse('[' + m.body + ']');
    //   $scope.words = [];
    //   $scope.$apply(function () {
    //     $scope.words = jsonObj;
    //   });
    // }

    // var ws = new SockJS(mq_url);
    // var client = Stomp.over(ws);

    //slider code
    $scope.sliderOnChage = function () {
      $timeout(function () {
        console.log("min value: ", $scope.slider.minValue, " Max value: ", $scope.slider.maxValue);
        createSliderWordCloudRequest($scope.slider.minValue, $scope.slider.maxValue);
      }, 500);
    };

    $scope.initSliderValuesAndGetCloud = function () {
      var url = encodeURI('api/word_cloud/get_dates_for_slider');
      $http({
        method: 'GET',
        url: url
      }).then(function successCallback(response) {
        console.log('dates', response);
        if (response.data.length > 2) {
          $scope.slider.minValue = response.data[0];
          $scope.slider.maxValue = response.data[1];
          $scope.slider.options.floor = response.data[0];
          $scope.slider.options.ceil = response.data[response.data.length - 1];
          $scope.slider.options.stepsArray = response.data;

          createSliderWordCloudRequest($scope.slider.minValue, $scope.slider.maxValue);
        }
      }, function errorCallback(response) {
        console.log('ERROR DATES');
      });
    };

    $scope.getCloud = function () {
      // var url = encodeURI('api/word_cloud');
      //
      // $http({
      //   method: 'GET',
      //   url: url
      // }).then(function successCallback(response) {
      //   console.log('WORDS', response.data);
      //   $scope.words = [];
      //   $scope.barChartData = [];
      //   $scope.barChartData.push(response.data.barChart);
      //   for (var i = 0; i < response.data.wordCloud.length; i++) {
      //     $scope.words.push(response.data.wordCloud[i]);
      //   }
      // }, function errorCallback(response) {
      //   console.log('ERROR');
      // });
      console.log('VALUEEESSS', $scope.slider.minValue, $scope.slider.maxValue);

    };

    $scope.hasCountry = function () {
      return !!$stateParams.country;
    };

    (function init() {

      $scope.slider = {
        // minValue: 1,
        // maxValue: 20,
        options: {
          // floor: 1,
          // ceil: 20,
          vertical: true,

          step: 1,
          noSwitching: true,
          showTicksValues: true,
          onChange: $scope.sliderOnChage
        }
      };

      if ($stateParams.country) {
        $scope.selectedCountry = $stateParams.country;
        var url = encodeURI('api/country_tweets/get_tweets_for_country?country=' + $scope.selectedCountry);
        $http({
          method: 'GET',
          url: url
        }).then(function successCallback(response) {
          // $scope.words = [];
          // for (var i = 0; i < response.data.length; i++) {
          //   $scope.words.push(JSON.parse(response.data[i]));
          // }
          // console.log('WORDS', $scope.words);

          console.log('WORDS', response.data);
          $scope.words = [];
          $scope.barChartData = [];
          $scope.barChartData.push(response.data.barChart);
          for (var i = 0; i < response.data.wordCloud.length; i++) {
            $scope.words.push(response.data.wordCloud[i]);
          }
        }, function errorCallback(response) {
          console.log('ERROR');
        });
      } else {
        // loadMap();
        $scope.initSliderValuesAndGetCloud();

      }
    })();

    // function loadMap() {
    //   client.heartbeat.outgoing = 0;
    //   client.heartbeat.incoming = 0;
    //   client.connect(
    //     mq_username,
    //     mq_password,
    //     on_connect_error,
    //     mq_vhost);
    // }

    //barchart options
    $scope.options = {
      chart: {
        type: 'multiBarHorizontalChart',
        height: 800,
        margin: {
          top: 20,
          right: 20,
          bottom: 60,
          left: 150
        },
        showControls: true,
        x: function (d) {
          return d.label;
        },
        y: function (d) {
          return d.value;
        },
        showValues: true,
        valueFormat: function (d) {
          return d3.format(',.0f')(d);
        },
        zoom: {
          enabled: true,
        }
      }
    };

    function createSliderWordCloudRequest(fromTime, toTime) {
      if (fromTime.value) {
        fromTime = fromTime.value;
      }
      if (toTime.value) {
        toTime = toTime.value;
      }

      $http.get("api/word_cloud/slider_values", {params: {"fromTime": fromTime, "toTime": toTime}})
        .then(function successCallback(response) {
          console.log('WORDS', response.data);
          $scope.words = [];
          $scope.barChartData = [];
          $scope.barChartData.push(response.data.barChart);
          for (var i = 0; i < response.data.wordCloud.length; i++) {
            $scope.words.push(response.data.wordCloud[i]);
          }
        }, function errorCallback(response) {
          console.log('ERROR');
        });
    }

    $scope.getAllTweetsWordCloud = function () {
      $stateParams.country = undefined;
      $scope.hasCountry();
      $scope.initSliderValuesAndGetCloud();
    }

    $scope.setBoldStyles = function () {
      alert("called");
      angular.element('.tick').css('font-weight', 700);
      angular.element('.text').css('font-weight', 700);
    }
  }
})
(angular);
