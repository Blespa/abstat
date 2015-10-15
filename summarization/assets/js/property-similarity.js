var summary = angular.module('schemasummaries', ['ui.bootstrap']);

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