package de.skipgraph.operations;

import de.skipgraph.SkipGraphElement;

public abstract class ModifyContentOperation extends QueryOperation {


	private final SkipGraphElement element;

	public ModifyContentOperation(SkipGraphElement element) {
		this.element = element;
	}

	public SkipGraphElement getElement() {
		return element;
	}
}
