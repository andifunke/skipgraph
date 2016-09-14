package de.skipgraph.operations;

import de.skipgraph.Element;

public abstract class ModifyElementsOperation extends QueryOperation {


	private final Element element;

	public ModifyElementsOperation(Element element) {
		this.element = element;
	}

	public Element getElement() {
		return element;
	}
}
