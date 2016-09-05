package de.skipgraph;

import de.skipgraph.operations.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SkipGraphNode {


	private int minTableSize;
	private int maxTableSize;
	// TODO: list as set or sorted list (or sorted set?)
	private List<SkipGraphElement> elementTable;
	private BigDecimal tableRangeStart;
	private BigDecimal tableRangeEnd;
	private List<SkipGraphContacts> contactsTable;

	public SkipGraphNode(int minTableSize, int maxTableSize) {
		this.minTableSize = minTableSize;
		this.maxTableSize = maxTableSize;
		this.elementTable = new ArrayList<>();
		this.tableRangeStart = BigDecimal.ZERO;
		this.tableRangeEnd = null; // initial null-value means: no upper limit (= infinity)
		this.contactsTable = new LinkedList<>();
	}

	public List<SkipGraphElement> getElementTable() {
		return elementTable;
	}

	public BigDecimal getTableRangeEnd() {
		return tableRangeEnd;
	}

	public List<SkipGraphElement> execute(QueryOperation queryOperation) {
		return queryOperation.execute(this);
	}

	// TODO: intervall nach unten offen
	/**
	 * checks if a given value is below the minimum value of the element table
	 * @param value
	 * @return
	 */
	public boolean isBelowElementTablesMinimum(BigDecimal value) {
		//
		if (value == null) {
			return !(tableRangeStart == null);
		}
		else {
			return !(tableRangeStart != null && tableRangeStart.compareTo(value) <= 0);
		}
	}

	/**
	 * checks if a given value is above the maxmimum value of the element table
	 * @param value
	 * @return
	 */
	public boolean isAboveElementTablesMaximum(BigDecimal value) {
		if (value == null) {
			return !(tableRangeEnd == null);
		}
		else {
			return (tableRangeEnd != null && tableRangeEnd.compareTo(value) >= 0);
		}
	}

	private void findElement(SkipGraphElement element) {

	}

	private List<SkipGraphElement> collect(QueryOperation queryOperation) {
		SearchOperation searchOperation = (SearchOperation)queryOperation;
		return new ArrayList<>();
	}

	public void checkTableSize() {
		if (elementTable.size() < minTableSize) {
			System.out.println("table too small -> leave");
		} else if (elementTable.size() > maxTableSize) {
			System.out.println("table too big -> split");
		}
	}

	private void leave() {

	}

	private void split() {

	}

	public void printTable() {
		for (int i=0; i < elementTable.size(); i++) {
			String index = String.format("%03d -- ", i+1);
			System.out.println(index + elementTable.get(i).toString());
		}
	}

}
