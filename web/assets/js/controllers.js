var summary = angular.module('schemasummaries', []);

summary.controller('GraphsList', function ($scope, $http) {
	$http.get('http://siti-rack.siti.disco.unimib.it:8890/sparql', {
        method: 'GET',
        params: {
            query: "select distinct ?uri where {GRAPH ?uri {?s ?p ?o} . FILTER regex(?uri, 'schemasummaries')}",
            format: 'json'
        }
    }).success(function(res){
        $scope.graphs=res.results.bindings
    });
});