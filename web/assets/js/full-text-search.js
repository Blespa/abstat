var application = angular.module("root", []);

application.controller("index", function ($scope, $http) {
	$scope.loadPatterns = function(){
		var searchUri = '/solr/indexing/select';		
		$http.get(searchUri,{
			method: 'GET',
			params: {
				wt: 'json',
				q: 'fullTextSearchField:' + $scope.srcStr
			}
		}).success(function(results){
			console.log(results);
			$scope.allDocuments = results.response.docs;
		});
	};
});
