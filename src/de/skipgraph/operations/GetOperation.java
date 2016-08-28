package de.skipgraph.operations;

public class GetOperation extends QueryOperation {

	private int index;

	/**
	 * constructor
	 * @param index
	 */
	public GetOperation(int index) {
		super(QueryType.GET);
		this.index = index;
	}

}
