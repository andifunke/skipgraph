package de.skipgraph.operations;

import de.skipgraph.SkipGraphNode;

public abstract class QueryOperation {

	public enum QueryType {
		GET, SEARCH, INPUT, DELETE
	}

	public abstract void execute(SkipGraphNode node);

	//private final QueryType queryType;

	// TODO: extend with a requester-contact

	/*public QueryOperation(QueryType queryType) {
		this.queryType = queryType;
	}*/

	/*public QueryType getQueryType() {
		return queryType;
	}*/


}
