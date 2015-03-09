var summary = angular.module('schemasummaries', []);

summary.controller('Summarization', function ($scope, $http, $location) {
	$scope.selected_graph='Select a dataset'
	
	$http.get('http://' + $location.host() + ':8890/sparql', {
        method: 'GET',
        params: {
            query: "select distinct ?uri where {GRAPH ?uri {?s ?p ?o} . FILTER regex(?uri, 'schemasummaries')}",
            format: 'json'
        }
    }).success(function(res){
        $scope.graphs=res.results.bindings;
    });
	
	$scope.loadPatterns= function(){
		$http.get('http://' + $location.host() + ':8890/sparql', {
	        method: 'GET',
	        params: {
	            query: 'prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ' +
		               'prefix ss:   <http://schemasummaries.org/ontology/> '+
	            	   'select ?subject ?predicate ?object ?frequency where { '+
	            	   '?pattern a ss:AbstractKnowledgePattern . ' +
	            	   '?pattern rdf:subject ?subject . ' +
	            	   '?pattern rdf:predicate ?predicate . ' + 
	            	   '?pattern rdf:object ?object . ' +
	            	   '?pattern ss:has_frequency ?frequency } ' +
	            	   'order by desc(?frequency) ' +
	            	   'limit 20',
	            'default-graph-uri': $scope.selected_graph,
	            format: 'json'
	        }
	    }).success(function(res){
	        $scope.summaries=res.results.bindings;
	    });
	}
});