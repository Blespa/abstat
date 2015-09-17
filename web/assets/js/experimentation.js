var application = angular.module("schemasummaries", []);

application.filter('describe', function(){
	return function(objectToDescribe){
		return objectToDescribe.join(' - ');
	};
});

application.filter('clean', function(){
	return function(type){
		return type.toLowerCase().replace(/object/g, "").replace(/datatype/g, "");
	};
});

application.filter('asLabel', function(){
	return function(type){
		if(type == 'concept') return 'success';
		if(type == 'property') return 'danger';
		if(type == 'akp') return 'warning';
	};
});

application.filter('asIcon', function(){
	return function(subtype){
		if(subtype.indexOf('external') > -1) return 'full';
		return 'small'
	};
});

application.controller("search", function ($scope, $http) {
	
	escape = function(string){
		return string.toLowerCase().replace(/([&+-^!:{}()|\[\]\/\\])/g, "").replace(/ and /g, " ").replace(/ or /g, " ");
	};
	
	$scope.internalResources = function(){
		var searchUri = '/solr/indexing/select';		
		$http.get(searchUri,{
			method: 'GET',
			params: {
				wt: 'json',
				q: 'fullTextSearchField:(' + escape($scope.srcStr) + ')',
				rows: 100,
				fq: 'subtype: internalConcept<OR>internalDatatypeProperty<OR>internalObjectProperty<OR>internalDatatypeAKP<OR>internalObjectAKP'
			}
		}).success(function(results){
			$scope.allDocuments = results.response.docs;
		});
	};

	$scope.alsoExternalResources = function(){
		var searchUri = '/solr/indexing/select';		
		$http.get(searchUri,{
			method: 'GET',
			params: {
				wt: 'json',
				q: 'fullTextSearchField:(' + escape($scope.srcStr) + ')',
				rows: 100
			}
		}).success(function(results){
			$scope.allDocuments = results.response.docs;
		});
	};
});
