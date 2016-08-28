package de.skipgraph.operations;

public abstract class QueryOperation {

	public enum QueryType {
		GET, SEARCH, INPUT, DELETE
	}

	private final QueryType queryType;

	public QueryOperation(QueryType queryType) {
		this.queryType = queryType;
	}

	public QueryType getQueryType() {
		return queryType;
	}


}
