var summary = angular.module('schemasummaries', ['ui.bootstrap']);

summary.filter('escape', function(){
	return window.encodeURIComponent;
});

summary.filter('isDatatype', function(){
	return isDatatype;
});

summary.filter('isObject', function(){
	return isObject;
});

summary.controller('Summarization', function ($scope, $http) {
	
	var summaries = new Summary($scope, $http);
	
	$scope.loadPatterns = function(){
		
		$scope.subject = undefined;
		$scope.object = undefined;
		$scope.predicate = undefined;
		
		$scope.summaries = [];
		summaries.reset();
		summaries.load();
		
		$scope.autocomplete = {};
		
		fill('subject', $scope.selected_graph, $scope.autocomplete, $http)
		fill('predicate', $scope.selected_graph, $scope.autocomplete, $http)
		fill('object', $scope.selected_graph, $scope.autocomplete, $http)
	};
	
	$scope.filterPatterns = function(){
		
		$scope.summaries = [];
		summaries.reset();
		summaries.load();
	}
	
	$scope.loadMore = function(){
		summaries.load();
	};

	$scope.selected_graph = 'select a dataset';
	$scope.describe_uri = '/describe/?uri=';
	
	summary.loadingSummary = true;
	getGraphs($scope, $http);
	summary.loadingSummary = false;
});

isDatatype = function(value){
	if(value.includes('datatype-property')) return 'DTP';
	return '';
};

isObject = function(value){
	if(value.includes('object-property')) return 'OP';
	return '';
};

fill = function(type, graph, result, http){
	
	result[type] = [];
	
	new Sparql(http)
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

getGraphs = function(scope, http){
	new Sparql(http)
			.query("select distinct ?uri where {GRAPH ?uri {?s ?p ?o} . FILTER regex(?uri, 'ld-summaries')}")
			.accumulate(function(results){
				scope.graphs=results;
	});
};

Summary = function(scope_service, http_service){
	
	var offset = 0;
	var limit = 20;
	var scope = scope_service;
	var http = http_service;
	
	this.reset = function(){
		offset = 0;
	}
	
	this.load = function(){
		
		scope.loadingSummary = true;
		
		var localOrDefault = function(value, default_value){
			var value_to_return = default_value;
			if(value) value_to_return = '<' + value.local + '>';
			return value_to_return;
		}
		
		var subject = localOrDefault(scope.subject, '?subject');
		var predicate = localOrDefault(scope.predicate, '?predicate');
		var object = localOrDefault(scope.object, '?object');
		
		new Sparql(http)
			.query('select ' + subject + ' as ?subject ' + predicate + ' as ?predicate ' + object + ' as ?object ?frequency ?pattern ?gSubject ?gPredicate ?gObject ?subjectOcc ?predicateOcc ?objectOcc ' +
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
			         	'} . ' +
			         	'optional { ' +
			         		predicate + ' lds:occurrence ?predicateOcc .' +
			         	'} . ' +
			         	'optional { ' +
		         			object + ' lds:occurrence ?objectOcc . ' +
		         			'FILTER (?objectOcc > 0) ' +
		         		'} . ' +
					'} ' +
					'order by desc(?frequency) ' +
					'limit ' + limit + ' ' +
					'offset ' + offset)
			.onGraph(scope.selected_graph)
			.accumulate(function(results){
				offset = offset + 20;
				for (var i = 0; i < results.length; i++) {
					scope.summaries.push(results[i]);
			    }
				scope.graph_was_selected=true;
				scope.loadingSummary = false;
			});
	}
}

Sparql = function(http_service){
	
	var http = http_service;
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
		http.get('/sparql', {
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