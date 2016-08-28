package de.skipgraph.operations;

import de.skipgraph.SkipGraphElement;

public class DeleteOperation extends UpdateOperation {

	public DeleteOperation(SkipGraphElement element) {
		super(element, QueryType.DELETE);
	}
}
