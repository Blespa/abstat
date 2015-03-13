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
				'?pattern a lds:AbstractKnowledgePattern . ' +
	         	'?pattern rdf:' + type + ' ?' + type + ' . ' +
	         	'?' + type + ' rdfs:seeAlso' + ' ?g' + type + ' . ' +
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
			.query("select distinct ?uri where {GRAPH ?uri {?s ?p ?o} . FILTER regex(?uri, 'ld-summaries')}")
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
		.query('select ' + subject + ' as ?subject ' + predicate + ' as ?predicate ' + object + ' as ?object ?frequency ?pattern ?gSubject ?gPredicate ?gObject ?subjectOcc ?subjectSupertype ' +
			   ' where { ' +
					'?pattern a lds:AbstractKnowledgePattern . ' +
					'?pattern rdf:subject ' + subject + ' . ' +
					'?pattern rdf:predicate ' + predicate + ' . ' + 
		         	'?pattern rdf:object ' + object + ' . ' +
		         	'?pattern lds:occurrence ?frequency . ' +
		         	subject + ' rdfs:seeAlso ?gSubject . ' +
		         	predicate +' rdfs:seeAlso ?gPredicate . ' +
		         	object + ' rdfs:seeAlso ?gObject . ' +
		         	'optional { ' +
		         		subject + ' lds:occurrence ?subjectOcc .' +
		         		subject + ' skos:broader ?tmp . ' +
		         		'?tmp rdfs:seeAlso ?subjectSupertype . ' +
		         	'}' +
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
		        	   'prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> ' +
		        	   'prefix lds:   <http://ld-summaries.org/ontology/> '+
		        	   'prefix skos:   <http://www.w3.org/2004/02/skos/core#> '+
	         	       query,
	            'default-graph-uri' : graph,
	            format: 'json'
	        }
	    }).success(function(res){
	    	onSuccess(res.results.bindings);
	    });
	};
};