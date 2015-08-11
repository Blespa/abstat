var application = angular.module("schemasummaries", []);

application.controller("search", function ($scope, $http) {
	
	escape = function(string){
		return string.toLowerCase().replace(/([&+-^!:{}()|\[\]\/\\])/g, "").replace(/and/g, "").replace(/or/g, "");
	};
	
	$scope.loadPatterns = function(){
		var searchUri = '/solr/indexing/select';		
		$http.get(searchUri,{
			method: 'GET',
			params: {
				wt: 'json',
				q: 'fullTextSearchField:' + escape($scope.srcStr)
			}
		}).success(function(results){
			$scope.allDocuments = results.response.docs;
		});
	};
});
