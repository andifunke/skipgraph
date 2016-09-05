package de.skipgraph.operations;

import de.skipgraph.SkipGraphNode;

public class GetOperation extends QueryOperation {

	private int index;

	/**
	 * constructor
	 * @param index
	 */
	public GetOperation(int index) {
		//super(QueryType.GET);
		this.index = index;
	}

	public void execute(SkipGraphNode node) {
	}

	}
