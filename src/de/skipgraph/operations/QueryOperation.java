package de.skipgraph.operations;

import de.skipgraph.Element;
import de.skipgraph.Node;

import java.util.List;

public abstract class QueryOperation {

	public enum QueryType {
		GET, SEARCH, INPUT, DELETE
	}

	/**
	 *
	 * @param node	parameter should be the node executing the query, so the query can call methodes on the node
	 * @return		returns a list of skip graph elements (for search-queries). may be null for other queries.
	 */
	public abstract List<Element> execute(Node node);

	//private final QueryType queryType;

	// TODO: extend with a requester-contact

	/*public QueryOperation(QueryType queryType) {
		this.queryType = queryType;
	}*/

	/*public QueryType getQueryType() {
		return queryType;
	}*/


}
