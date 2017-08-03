'use strict';

angular.module('capappApp')
  .controller('MainController', function ($scope, Principal) {
    Principal.identity().then(function (account) {
      $scope.account = account;
      $scope.isAuthenticated = Principal.isAuthenticated;
    });
  })
  .factory('CommandStream', function ($http, $timeout, $log) {
    var url = "/api/commandList?startIndex=0";
    var poller = function () {
      return $http.get(url).then(function (data) {
        return data.data;
      });
    };

    return {
      poll: poller
    };
  })
  .controller('commandStreamController', function ($scope, $timeout, CommandStream) {

    function pollData() {
      CommandStream.poll().then(function(data) {
        $scope.messages = data;
        $timeout(pollData, 1000);
      });
    }

    pollData();
  })
  .controller('portList', function($scope, $http, $log, $timeout, AlertService) {
    $scope.refresh = function() {
      $http.get('/api/bluetoothConnect')
        .success(function(data) {
          $log.info("call to get device list successful");
          $scope.items = data;
        });
    };

    $scope.connectPort = function(port) {
      $log.info("connect port called: " + port);

      // How do these alerts work?
      AlertService.success("Connected to " + port);
      var url = '/api/serialPort?portName=';
      url = url + port;
      $http.put(url)
        .then(function successCallback(response) {
          $scope.echoDisabled = false;
          $scope.isConnected = true;
          $scope.connectedDevice = port;
          $http.post('/api/serialPort/clear');
        }, function errorCallback(response){
          // error alert
          AlertService.error("Unable to connect to port: " + port + ". Please refresh devices");
        });
    };

    $scope.disconnect = function() {
      $log.info("disconnect device called");
      var url = '/api/serialPort';

      // Find out what this splice does
      // $scope.rowCollection.splice(0,$scope.rowCollection.length);

      $http.delete(url)
        .then(function successCallback(response) {
          $scope.isConnected = false;
          $scope.connectedDevice = '';
        }, function errorCallback(response){
          // error alert
        });
    };
    $scope.echo = function() {
      $log.info("echo called");
      var url = '/api/serialPort/echo';
      $scope.echoDisabled = true;

      $http.get(url)
        .then(function successCallback(response) {
          console.log("Success");
        }, function errorCallback(response) {
          $scope.echoDisabled = false;
        });
    };

    $scope.refresh();
    $scope.isConnected = false;

    // TODO: Move to service if using. Bind callbacks for different types of events?
    var socket = new WebSocket("ws://localhost:8765");

    var echoEnable = function() {
      $scope.echoDisabled = false;
    };

    var deviceMessage = function(msg) {
      $scope.deviceMessage = msg;
    };

    socket.onmessage = function (event) {
      if (JSON.parse(event.data).type === "echo") {
        deviceMessage("Echo received");
        $timeout(deviceMessage, 2000, true, "");
        $timeout(echoEnable, 2000);
      }
    };
  })

  .controller('gestures', function($scope, $http, $log) {
    var rows = [];
    $scope.rowCollection = rows;

    function getGestures() {
      $http.get('/api/gestures')
        .success(function(data) {
          $log.info("call to get device list successful");
          $scope.items = data;
        });
    }

    $scope.items = getGestures();

    function addGesture(gesture) {
      var gestureName = gesture;
      var trainingCycles = 100;
      var defaultKey = 'None';
      var status = 'Pending';

      return {
        name: gestureName,
        cycles: trainingCycles,
        trainingStatus: status,
        key: {
          display: defaultKey,
          code: 0,
          highThreshold: highThresholdInit,
          lowThreshold: lowThresholdInit
        }
      };
    }

    $scope.addGestureRow = function addGestureRow(gesture) {
      if(gesture && gesture != "") {
        // checking if gesture name is already in list
        var found = rows.some(function (el) {
          return el.name == gesture;
        });

        // If unique add to list
        if(!found){
          rows.push(addGesture(gesture));
        }
      }
    };

    $scope.removeItem = function removeItem(row) {
      var index = rows.indexOf(row);

      if (index !== -1) {
        rows.splice(index, 1);
      }
    };

    var lowThresholdInit = 1;
    var highThresholdInit = 3;

    $scope.onKeyup = function (event, row) {
      var keyCode = event.keyCode;
      var charCode = event.charCode;
      var display;

      switch (keyCode) {
        case 37:
          display = 'Left';
          break;
        case 38:
          display = 'Up';
          break;
        case 39:
          display = 'Right';
          break;
        case 40:
          display = 'Down';
          break;
        default:
          display = String.fromCharCode(keyCode);
          break;
      }

      row.key.display = display;
      row.key.code = event.keyCode || event.charCode;

      $log.info(row.name, 'bound to', display);
    };

    $scope.onKeydown = function (event, row) {
      row.key.display = '';
    };

    var trainingComplete = function() {
      if (!rows.length) {
        return false;
      }

      for (var i = 0; i < rows.length; i++) {
        if (rows[i].trainingStatus != 'Complete') {
          return false;
        }
      }

      return true;
    };

    $scope.doneEnabled = trainingComplete;
    var currentRow;
    var activeRow;
    var streaming = false;
    var model = false;

    $scope.trainGesture = function trainGesture (row) {
      currentRow = row;
      var name = row.name;
      var count = row.cycles;
      var index = rows.indexOf(row);

      $log.info('Training gesture', row);

      // We might want to id the rows themselves instead of using position in collection
      var url = '/api/serialPort/train?gesture=' + index + '&count=' + count;

      $http.post(url)
        .then(
          function successCallback (response) {
            row.trainingStatus = 'In progress';
            $log.info('Training', name, 'started');
          },
          function errorCallback (response) {
            row.trainingStatus = 'Failed';
            $log.error('Training', name, 'unsuccessful');
          });
    };

    var socket = new WebSocket("ws://localhost:8765");

    var previousGestureIndex = -1;
    var activeGestureCount = 0;

    $scope.keyPressEnabled = false;
    $scope.keyPressLabel = "Key Press Off";

    $scope.toggleKeyPress = function () {
      $scope.keyPressEnabled = !$scope.keyPressEnabled;
      toggleKeyLabel($scope.keyPressEnabled);
    };

    function toggleKeyLabel (status) {
      $scope.keyPressLabel = status
        ? 'Key Press On'
        : 'Key Press Off';
    };

    var thresholdReached = false;

    socket.onmessage = function (event) {
      var data = JSON.parse(event.data);

      if (data.type === 'trainingcomplete') {
        currentRow.trainingStatus = 'Complete';
        $log.info('Training complete');
      } else if (data.type === 'prediction') {
        var currentGestureIndex = data.data;
        
        if (data.data !== null && streaming) {
          // if active row is the same as the incoming row
          if (previousGestureIndex === currentGestureIndex) {
            activeGestureCount++;
          } else {
            previousGestureIndex = currentGestureIndex;
            activeGestureCount = 1;
            thresholdReached = false;
          }

          var row = rows[currentGestureIndex];

          // ensure the count is in the sweet spot
          if (activeGestureCount < row.key.lowThreshold) return;
          if (activeGestureCount < row.key.highThreshold && thresholdReached) {
            return;
          }
          // sean is a fucking genius!

          thresholdReached = true;

          // reset the activeGestureCount
          if (activeGestureCount === row.key.highThreshold) activeGestureCount = 1;

          if (activeRow) activeRow.activeGesture = false;
          activeRow = rows[currentGestureIndex];

          var key = activeRow.key.code;
          // POST request keyPress
          if(key !== 'None' && $scope.keyPressEnabled) {
            $http({
              method: 'POST',
              url: '/api/keyPress',
              data: {"keyPress": key}
            }).then(
              function successCallback(response) {
                $log.info('keyPress', key, 'successful');
              },
              function errorCallback(response) {
                $log.info('keyPress', key, 'unsuccessful');
              });
          }

          if (activeRow) activeRow.activeGesture = true;
        }
      }

      // needed because this happens outside of normal execution turns
      $scope.$apply();
    };

    $scope.startStreaming = function startStreaming () {
      $http.post('/api/serialPort/start')
        .then(
          function success (res) {
            $log.info('Streaming started.');
            streaming = true;
            model = true;
          },
          function failure (res) {
            $log.info('Streaming failed.');
          });
    };

    $scope.stopStreaming = function () {
      $http.post('/api/serialPort/stop')
        .then(
          function success (res) {
            $log.info('Streaming stopped.');
            streaming = false;
            for (var i = 0; i < rows.length; ++i) {
              activeRow = null;
              rows[i].activeGesture = false;
            }
          },
          function failure (res) {
            $log.info('Failed to stop streaming.');
          });
    };

    $scope.clearModel = function () {
      if (streaming) $scope.stopStreaming();
      $http.post('/api/serialPort/clear')
        .then(
          function success (res) {
            $log.info('Model cleared.');
            model = false;
            for (var i = 0; i < rows.length; ++i) {
              activeRow = null;
              rows[i].trainingStatus = 'Pending';
              rows[i].activeGesture = false;
            }
          },
          function failure (res) {
            $log.info('Failed to clear model.');
          });
    };

    $scope.isStreaming = function () {
      return streaming;
    };

    $scope.haveModel = function () {
      return model;
    };
  });
