package de.skipgraph.operations;

import de.skipgraph.SkipGraphElement;

public class InputOperation extends UpdateOperation {

	public InputOperation(SkipGraphElement element) {
		super(element, QueryType.INPUT);
	}
}
