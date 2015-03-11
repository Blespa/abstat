var summary = angular.module('schemasummaries', ['ui.bootstrap']);

summary.controller('Summarization', function ($scope, $http, $location) {
	
	$scope.loadPatterns = function(){
		
		loadSummaries($scope, $http, $location);
		
		$scope.autocomplete = {};
		
		fill('subject', $scope.selected_graph, $scope.autocomplete, $http, $location)
		fill('predicate', $scope.selected_graph, $scope.autocomplete, $http, $location)
		fill('object', $scope.selected_graph, $scope.autocomplete, $http, $location)
	};
	
	$scope.filterPatterns = function(){
		
		loadSummaries($scope, $http, $location);
	}

	$scope.selected_graph = 'Select a dataset';
	$scope.describe_uri = endpoint($location) + '/describe/?uri=';
	
	getGraphs($scope, $http, $location);
});

fill = function(type, graph, result, http, location){
	
	result[type] = [];
	
	new Sparql(http, location)
	.query('select distinct(?' + type + ') as ?resource ' + 
			'where { '+
				'?pattern a ss:AbstractKnowledgePattern . ' +
	         	'?pattern rdf:' + type + ' ?' + type + ' . ' +
         	'} ')
     .onGraph(graph)
     .accumulate(function(results){		    	 
    	 angular.forEach(results, function(key, value){
    		 this.push(key.resource.value)
    	 }, result[type]);
     });
};

getGraphs = function(scope, http, location){
	new Sparql(http, location)
			.query("select distinct ?uri where {GRAPH ?uri {?s ?p ?o} . FILTER regex(?uri, 'schemasummaries')}")
			.accumulate(function(results){
				scope.graphs=results;
	});
};

loadSummaries = function(scope, http, location){
	
	var valueOrDefault = function(value, default_value){
		var value_to_return = default_value;
		if(value) value_to_return = '<' + value + '>';
		return value_to_return;
	}
	
	var subject = valueOrDefault(scope.subject, '?subject');
	var predicate = valueOrDefault(scope.predicate, '?predicate');
	var object = valueOrDefault(scope.object, '?object');
	
	new Sparql(http, location)
		.query('select ' + subject + 'as ?subject ' + predicate + ' as ?predicate ' + object + ' as ?object ?frequency ' +
				'where { ' +
					'?pattern a ss:AbstractKnowledgePattern . ' +
					'?pattern rdf:subject ' + subject + ' . ' +
					'?pattern rdf:predicate ' + predicate + ' . ' + 
		         	'?pattern rdf:object ' + object + ' . ' +
		         	'?pattern ss:has_frequency ?frequency . ' +
				'} ' +
				'order by desc(?frequency) ' +
				'limit 10')
		.onGraph(scope.selected_graph)
		.accumulate(function(results){
			scope.summaries=results;
			scope.graph_was_selected=true;
		});
};

endpoint = function(location_service){
	return 'http://' + location_service.host() + ':8890'
}

Sparql = function(http_service, location_service){
	
	var http = http_service;
	var location = location_service;
	var graph = "";
	var query;
	
	this.onGraph = function(target_graph){
		graph = target_graph;
		return this;
	};
	
	this.query = function(query_to_execute){
		query = query_to_execute;
		return this;
	};
	
	this.accumulate = function(onSuccess){
		http.get(endpoint(location) + '/sparql', {
	        method: 'GET',
	        params: {
	            query: 'prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ' +
		        	   'prefix ss:   <http://schemasummaries.org/ontology/> '+
	         	       query,
	            'default-graph-uri' : graph,
	            format: 'json'
	        }
	    }).success(function(res){
	    	onSuccess(res.results.bindings);
	    });
	};
};