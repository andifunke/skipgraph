package de.skipgraph.operations;

import de.skipgraph.SkipGraphElement;

public abstract class UpdateOperation extends QueryOperation {


	private final SkipGraphElement element;

	public UpdateOperation(SkipGraphElement element) {
		this.element = element;
	}

	public SkipGraphElement getElement() {
		return element;
	}
}
