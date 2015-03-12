var summary = angular.module('schemasummaries', ['ui.bootstrap']);

summary.filter('escape', function(){
	return window.encodeURIComponent;
});

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
	.query('select distinct(?' + type + ') ?g' + type + ' ' + 
			'where { '+
				'?pattern a ss:AbstractKnowledgePattern . ' +
	         	'?pattern rdf:' + type + ' ?' + type + ' . ' +
	         	'?' + type + ' owl:sameAs' + ' ?g' + type + ' . ' +
         	'} ')
     .onGraph(graph)
     .accumulate(function(results){		    	 
    	 angular.forEach(results, function(key, value){
    		 var result = {};
    		 result['local'] = key[type].value;
    		 result['global'] = key['g' + type].value;
    		 
    		 this.push(result)
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
	
	var localOrDefault = function(value, default_value){
		var value_to_return = default_value;
		if(value) value_to_return = '<' + value.local + '>';
		return value_to_return;
	}
	
	var subject = localOrDefault(scope.subject, '?subject');
	var predicate = localOrDefault(scope.predicate, '?predicate');
	var object = localOrDefault(scope.object, '?object');
	
	scope.summaries = [];
	
	new Sparql(http, location)
		.query('select ' + subject + ' as ?subject ' + predicate + ' as ?predicate ' + object + ' as ?object ?frequency ?pattern ?gSubject ?gPredicate ?gObject' +
			   ' where { ' +
				   	subject + ' owl:sameAs ?gSubject . ' +
		         	predicate +' owl:sameAs ?gPredicate . ' +
		         	object + ' owl:sameAs ?gObject . ' +
					'?pattern a ss:AbstractKnowledgePattern . ' +
					'?pattern rdf:subject ' + subject + ' . ' +
					'?pattern rdf:predicate ' + predicate + ' . ' + 
		         	'?pattern rdf:object ' + object + ' . ' +
		         	'?pattern ss:instanceOccurrence ?frequency . ' +
				'} ' +
				'order by desc(?frequency) ' +
				'limit 20')
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
		        	   'prefix owl:   <http://www.w3.org/2002/07/owl#> ' +
	         	       query,
	            'default-graph-uri' : graph,
	            format: 'json'
	        }
	    }).success(function(res){
	    	onSuccess(res.results.bindings);
	    });
	};
};