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

summary.controller('PropertySimilarity', function ($scope, $http){
	
	$scope.getDomainDistributionForProperty1 = function(){
		var dataset = $scope.query.dataset;
		var p1 = $scope.query.property1;
		
		$scope.p1DomainDistribution = domainDistribution(dataset, p1, $http);
		$scope.p1RangeDistribution = rangeDistribution(dataset, p1, $http);
	};
	
	$scope.getDomainDistributionForProperty2 = function(){
		var dataset = $scope.query.dataset;
		var p2 = $scope.query.property2;
		
		$scope.p2DomainDistribution = domainDistribution(dataset, p2, $http);
		$scope.p2RangeDistribution = rangeDistribution(dataset, p2, $http);
	};
	
	$scope.computeCosineSimilarity=function(){
		var p1Domains = $scope.p1DomainDistribution;
		var p2Domains = $scope.p2DomainDistribution
		
		var dotPDomains = dotProduct(p1Domains, p2Domains);
		var sum1Domains = quadraticSum(p1Domains);
		var sum2Domains = quadraticSum(p2Domains);
		
		var cosineSimilarityDomains = dotPDomains / (Math.sqrt(sum1Domains) * Math.sqrt(sum2Domains));
		$scope.domainSimilarity = cosineSimilarityDomains;
		
		var p1Ranges = $scope.p1RangeDistribution;
		var p2Ranges = $scope.p2RangeDistribution
		
		var dotPRanges = dotProduct(p1Ranges, p2Ranges);
		var sum1Ranges = quadraticSum(p1Ranges);
		var sum2Ranges = quadraticSum(p2Ranges);
		
		var cosineSimilarityRanges = dotPRanges / (Math.sqrt(sum1Ranges) * Math.sqrt(sum2Ranges));
		$scope.rangeSimilarity = cosineSimilarityRanges;
	};
});

quadraticSum = function(propertyDistribution){
	var result = 0;
	for(var i=0; i < propertyDistribution.length; i++){
		var power = propertyDistribution[i]['ratio'] * propertyDistribution[i]['ratio'];
		result += power
	}
	return result;
}

dotProduct = function(p1, p2){
	var p1Map = {};
	for(var i=0; i < p1.length; i++){
		var type = p1[i]['type'];
		p1Map[type] = p1[i]['ratio'];
	}
	var p2Map = {};
	for(var i=0; i < p2.length; i++){
		var type = p2[i]['type'];
		p2Map[type] = p2[i]['ratio'];
	}
	var result = 0;
	for(var type1 in p1Map){
		if(type1 in p2Map){
			var product = p1Map[type1] * p2Map[type1];
			result += product;
		}
	}
	return result;
}

domainDistribution = function(dataset, property, http){
	return distribution(dataset, property, 'rdf:subject', http);
}

rangeDistribution = function(dataset, property, http){
	return distribution(dataset, property, 'rdf:object', http);
}

distribution = function(dataset, property, domainOrRange, http){
	var distribution = [];
	new Sparql(http)
		.query('select ?type ?typeOcc sum(?occ) as ?akpOcc  where {' +
			   '?lp rdfs:seeAlso <' + property + '> . ' + 
			   '?akp rdf:predicate ?lp . ' +
			   '?akp ' + domainOrRange + ' ?ls . ' +
			   '?ls rdfs:seeAlso ?type . ' +
			   '?ls lds:occurrence ?typeOcc . ' +
			   '?akp lds:occurrence ?occ . ' +
			    '} group by ?type ?typeOcc order by ?type')
		.onGraph(dataset)
		.accumulate(function(results){
			angular.forEach(results, function(key, value){
				var element = {};
				element['type'] = key.type.value;
				element['typeOcc'] = key.typeOcc.value;
				element['akpOcc'] = key.akpOcc.value;
				element['ratio'] = key.akpOcc.value / key.typeOcc.value;
				
				this.push(element);
	    	}, distribution);
		});
	return distribution;
}

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
	
	getGraphs($scope, $http, summaries);
});

isDatatype = function(value){
	if(value.indexOf('datatype-property') > -1) return 'DTP';
	return '';
};

isObject = function(value){
	if(value.indexOf('object-property') > -1) return 'OP';
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

getGraphs = function(scope, http, summary){
	summary.startLoading();
	new Sparql(http)
			.query("select distinct ?uri where {GRAPH ?uri {?s ?p ?o} . FILTER regex(?uri, 'ld-summaries')}")
			.accumulate(function(results){
				scope.graphs=results;
				summary.endLoading();
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