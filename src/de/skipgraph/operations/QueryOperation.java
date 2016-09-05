package de.skipgraph.operations;

import de.skipgraph.SkipGraphElement;
import de.skipgraph.SkipGraphNode;

import java.util.List;

public abstract class QueryOperation {

	public enum QueryType {
		GET, SEARCH, INPUT, DELETE
	}

	public abstract List<SkipGraphElement> execute(SkipGraphNode node);

	//private final QueryType queryType;

	// TODO: extend with a requester-contact

	/*public QueryOperation(QueryType queryType) {
		this.queryType = queryType;
	}*/

	/*public QueryType getQueryType() {
		return queryType;
	}*/


}
