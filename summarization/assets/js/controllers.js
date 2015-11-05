var summary = angular.module('schemasummaries', ['ui.bootstrap']);

summary.filter('escape', function(){
	return window.encodeURIComponent;
});

summary.filter('isDatatype', function(){
	return function(value){
		if(value.indexOf('datatype-property') > -1) return 'DTP';
		return '';
	};
});

summary.filter('isObject', function(){
	return function(value){
		if(value.indexOf('object-property') > -1) return 'OP';
		return '';
	};
});

summary.filter('describe', function(){
	return function(objectToDescribe){
		return objectToDescribe.URI.join(' - ') + " (" + objectToDescribe.occurrence + ")";
	};
});

summary.filter('asLabel', function(){
	return function(type){
		if(type == 'concept' || type == 'datatype') return 'success';
		if(type.indexOf('Property') > -1) return 'danger';
		if(type.indexOf('Akp')) return 'warning';
	};
});

summary.filter('asIcon', function(){
	return function(subtype){
		if(subtype.indexOf('external') > -1) return 'full';
		return 'small'
	};
});

summary.controller('browse', function ($scope, $http) {
	var summaries = new Summary($scope, $http, '');
	
	bootstrapControllerFor($scope, $http, 'select a dataset', summaries, '');
	
	summaries.startLoading();
	$http.get('/api/v1/datasets', {method: 'GET', params:{}})
		 .success(function(results){
			$scope.graphs = results['datasets'];
			summaries.endLoading();
		 });
});

summary.controller("search", function ($scope, $http) {
	
	bootstrapSearchController($scope, $http, '');
});

summary.controller('experiment-browse', function ($scope, $http) {
	var summaries = new Summary($scope, $http, '?pattern a lds:Internal . ');
	
	bootstrapControllerFor($scope, $http, 'http://ld-summaries.org/dbpedia-3.9-infobox', summaries, '?pattern a lds:Internal . ');
	
	$scope.loadPatterns();
});

summary.controller("experiment-search", function ($scope, $http) {
	
	bootstrapSearchController($scope, $http, 'dbpedia-3.9-infobox');
});

bootstrapSearchController = function(scope, http, dataset){
	
	scope.loadPatterns = function(){
		var solr = new Solr(http);
		if(!scope.searchInExternalResources){
			solr.withFilter('subtype: internal');
		}
		if(dataset){
			solr.withFilter('dataset:' + dataset);
		}
		solr.search(scope.srcStr)
			.accumulate(function(results){
					scope.allDocuments = results.response.docs;
				});
	};
}

bootstrapControllerFor = function(scope, http, graph, summaries, filter){
	
	scope.loadPatterns = function(){
		
		scope.subject = undefined;
		scope.object = undefined;
		scope.predicate = undefined;
		
		scope.summaries = [];
		summaries.reset();
		summaries.load();
		
		scope.autocomplete = {};
		
		fill('subject', scope.selected_graph, scope.autocomplete, http, filter)
		fill('predicate', scope.selected_graph, scope.autocomplete, http, filter)
		fill('object', scope.selected_graph, scope.autocomplete, http, filter)
	};
	scope.filterPatterns = function(){
		
		scope.summaries = [];
		summaries.reset();
		summaries.load();
	}
	scope.loadMore = function(){
		summaries.load();
	};
	scope.selected_graph = graph;
	scope.describe_uri = '/describe/?uri=';
};

fill = function(type, graph, result, http, filter){
	
	result[type] = [];
	
	new Sparql(http)
	.query('select distinct(?' + type + ') ?g' + type + ' ' + 
			'where { '+
				'?pattern a lds:AbstractKnowledgePattern . ' +
				 filter +
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

Summary = function(scope_service, http_service, filter){
	
	var offset = 0;
	var limit = 20;
	var scope = scope_service;
	var http = http_service;
	var internalConstraint = filter;
	
	this.reset = function(){
		offset = 0;
	}
	
	this.startLoading = function(){
		scope.loadingSummary = true;
	}
	
	this.endLoading = function(){
		scope.loadingSummary = false;
	}
	
	this.load = function(){
		
		var localOrDefault = function(value, default_value){
			var value_to_return = default_value;
			if(value) value_to_return = '<' + value.local + '>';
			return value_to_return;
		}
		
		var subject = localOrDefault(scope.subject, '?subject');
		var predicate = localOrDefault(scope.predicate, '?predicate');
		var object = localOrDefault(scope.object, '?object');
		
		this.startLoading();
		endLoading = this.endLoading;
		
		new Sparql(http)
			.query('select ' + subject + ' as ?subject ' + predicate + ' as ?predicate ' + object + ' as ?object ?frequency ?pattern ?gSubject ?gPredicate ?gObject ?subjectOcc ?predicateOcc ?objectOcc ' +
				   ' where { ' +
						'?pattern a lds:AbstractKnowledgePattern . ' +
						internalConstraint +
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
				endLoading();
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

Solr = function(connector){
	
	var http = connector;
	var textToSearch;
	var filters = [];
	
	var escape = function(string){
		return string.toLowerCase()
					 .replace(/([&+-^!:{}()|\[\]\/\\])/g, "")
					 .replace(/ and /g, " ")
					 .replace(/ or /g, " ")
					 .replace(/ /g, " AND ");
	};
	
	this.search = function(text){
		textToSearch = text;
		return this;
	}
	
	this.withFilter = function(filter_to_add){
		filters.push(filter_to_add);
		return this;
	};
	
	this.accumulate = function(callback){
		http.get('/solr/indexing/select', {
			method: 'GET',
			params: {
				wt: 'json',
				q: 'fullTextSearchField:(' + escape(textToSearch) + ')',
				rows: 100,
				fq: filters,
				sort: 'occurrence desc'
			}})
		.success(callback);
	}
};
