angular.module('starter.controllers', [])

.controller('DashCtrl', function($scope) {
  //Update - keeps current content of node and updates the specified children
  firebase.database().ref("update_node").update({
    web_connection_test: "JS Working"
  });
  //Set - removes content of node and inserts content in {}
  firebase.database().ref("set_node").set({
    web_connection_test: "JS Working"
  });
  //Push - adds a new node with a unique ID inside the ref node
  firebase.database().ref("push_node").push({
    web_connection_test: "JS Working"
  });
  //Retrieve - use "on" for real time data; use "once" for time data
  firebase.database().ref("push_node").on("value", function(snapshot){
    console.log(snapshot.val());
  });
})

.controller('ChatsCtrl', function($scope, Chats) {
  // With the new view caching in Ionic, Controllers are only called
  // when they are recreated or on app start, instead of every page change.
  // To listen for when this page is active (for example, to refresh data),
  // listen for the $ionicView.enter event:
  //
  //$scope.$on('$ionicView.enter', function(e) {
  //});

  $scope.chats = Chats.all();
  $scope.remove = function(chat) {
    Chats.remove(chat);
  };
})

.controller('ChatDetailCtrl', function($scope, $stateParams, Chats) {
  $scope.chat = Chats.get($stateParams.chatId);
})

.controller('AccountCtrl', function($scope) {
  $scope.settings = {
    enableFriends: true
  };
});
