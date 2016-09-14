package de.skipgraph;

import de.skipgraph.operations.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SkipGraphNode {

	private int minTableSize;
	private int maxTableSize;
	// TODO: list as set or sorted list (or sorted set?)
	// TODO: outsource elementTable as its own class
	private List<SkipGraphElement> elementTable;
	private BigDecimal tableRangeStart;
	private BigDecimal tableRangeEnd;
	private SkipGraphContactTable contactTable;

	public SkipGraphNode(int minTableSize, int maxTableSize) {
		this.minTableSize = minTableSize;
		this.maxTableSize = maxTableSize;
		this.elementTable = new ArrayList<>();
		this.tableRangeStart = BigDecimal.ZERO;
		this.tableRangeEnd = null; // initial null-value means: no upper limit (= infinity)
		this.contactTable = new SkipGraphContactTable();
		SkipGraphContact selfContact = new SkipGraphContact(this, tableRangeStart, tableRangeEnd);
		this.contactTable.addLevel(new SkipGraphContactLevel(selfContact, selfContact));
	}

	public List<SkipGraphElement> getElementTable() {
		return elementTable;
	}

	public void setElementTable(List<SkipGraphElement> elementTable) {
		this.elementTable = elementTable;
	}

	public void setElementTable(List<SkipGraphElement> elementTable, BigDecimal start, BigDecimal end) {
		this.elementTable = elementTable;
		this.tableRangeStart = start;
		this.tableRangeEnd = end;
	}

	/**
	 * this method extends the elementTable of this node before the rangeStart of the original table.
	 * @param newList		a list of SkipGraphElements that will be added before the table.
	 *                   the values of these elements must be less than the rangeStart of the original table.
	 * @param start		sets a new rangeStart to the elementTable. Must be less than the original rangeStart
	 *                   and less than all values of all given SkipGraphElements.
	 */
	public void extendElementTableAtStart(List<SkipGraphElement> newList, BigDecimal start) {
		tableRangeStart = start;
		elementTable.addAll(0, newList);
		// should be redundant
		checkTableSize();
	}

	/**
	 * this method extends the elementTable of this node behind the rangeEnd of the original table.
	 * @param newList		a list of SkipGraphElements that will be added to the end of the table.
	 *                   the values of these elements must be higher than the rangeEnd of the original table.
	 * @param end			sets a new rangeEnd to the elementTable. Must be higher than the original rangeEnd
	 *                   and higher than all values of all given SkipGraphElements.
	 */
	public void extendElementTableAtEnd(List<SkipGraphElement> newList, BigDecimal end) {
		tableRangeEnd = end;
		elementTable.addAll(newList);
		// should be redundant
		checkTableSize();
	}

	public BigDecimal getTableRangeEnd() {
		return tableRangeEnd;
	}

	public SkipGraphContactTable getContactTable() {
		return contactTable;
	}

	public void setContactTable(SkipGraphContactTable contactTable) {
		this.contactTable = contactTable;
	}

	public List<SkipGraphElement> execute(QueryOperation queryOperation) {
		return queryOperation.execute(this);
	}

	// TODO: replace with compareTo ?
	/**
	 * checks if a given value is below or equal to the minimum value of the element table
	 *
	 * @param value
	 * @return
	 */
	public boolean isBelowElementTablesMinimum(BigDecimal value) {
		//System.out.println(String.format("%s (value) <= %s (start) ?", value, tableRangeStart));
		boolean ret;
		if (value == null) {
			ret = !(tableRangeStart == null);
		}
		else {
			ret = !(tableRangeStart != null && tableRangeStart.compareTo(value) <= 0);
		}
		//System.out.println(ret);
		return ret;
	}

	/**
	 * checks if a given value is above the maxmimum value of the element table
	 *
	 * @param value
	 * @return
	 */
	public boolean isAboveElementTablesMaximum(BigDecimal value) {
		//System.out.println(String.format("%s (value) > %s (end) ?", value, tableRangeEnd));
		boolean ret;
		if (value == null) {
			ret = !(tableRangeEnd == null);
		}
		else {
			ret = tableRangeEnd != null && tableRangeEnd.compareTo(value) < 0;
		}
		//System.out.println(ret);
		return ret;
	}

	public void checkTableSize() {
		checkMinTableSize();
		checkMaxTableSize();
	}

	public void checkMinTableSize() {
		if (elementTable.size() < minTableSize) {
			System.out.println("  ! table too small -> leave?");
			leave();
		}
	}

	public void checkMaxTableSize() {
		if (elementTable.size() > maxTableSize) {
			System.out.println("  ! table too big -> split");
			split();
		}
	}

	public int getNumberOfFreeSlots() {
		return maxTableSize - elementTable.size();
	}

	private void leave() {

		// check if successor has enough free space in table
		if (contactTable.getNextNode() != this && contactTable.getNextNode().getNumberOfFreeSlots() > elementTable.size()) {
			contactTable.getNextNode().extendElementTableAtStart(elementTable, tableRangeStart);
			updatingContactsOnLeave();
		}
		// else check if predecessor has enough free space in table
		else if (contactTable.getPrevNode() != this && contactTable.getPrevNode().getNumberOfFreeSlots() > elementTable.size()) {
			contactTable.getNextNode().extendElementTableAtEnd(elementTable, tableRangeEnd);
			updatingContactsOnLeave();
		}
		// TODO: Tabelle aufsplitten und auf prev und next verteilen
		else {
			System.out.println("  ! hold your ground");
		}
	}

	private void updatingContactsOnLeave() {
		// TODO: für alle Level
		contactTable.getNext().getContacts().setPrev(contactTable.getPrev());
		contactTable.getPrev().getContacts().setNext(contactTable.getNext());
		System.out.println("  ! bye bye");
	}

	private void split() {
		Collections.sort(elementTable);
		//printTable();

		// split element table
		int roundup = 0;
		if (elementTable.size() % 2 > 0) roundup = 1;
		List<SkipGraphElement> tmp = elementTable.subList(elementTable.size() / 2 + roundup, elementTable.size());
		ArrayList<SkipGraphElement> newTable = new ArrayList<>(tmp);
		tmp.clear();
		BigDecimal newStart = elementTable.get(elementTable.size() - 1).getValue();
		BigDecimal newEnd = tableRangeEnd;
		tableRangeEnd = newStart;
		//printTable();

		// check if successor can handle split elements
		if (newTable.size() < contactTable.getNextNode().getNumberOfFreeSlots()) {
			contactTable.getNextNode().extendElementTableAtStart(newTable, newStart);
			//contactTable.getNext().printTable();
		}
		else {
			// create new node
			SkipGraphNode newNode = new SkipGraphNode(minTableSize, maxTableSize);
			// TODO: für alle Level implementieren
			newNode.setInitialContacts(new SkipGraphContactLevel(this, contactTable.getNext()));
			newNode.setElementTable(newTable, newStart, newEnd);
			//newNode.printTable();

			// update prev contact of next node
			// TODO: für alle Level
			contactTable.getNext().getContacts().setPrev(newNode);
			// update next contact of this node
			contactTable.setNext(newNode);
		}

	}

	public void printTable() {
		System.out.println(String.format("### printing table (start:%s, end:%s, size:%d, min-size:%d, max-size:%d) ###",
				tableRangeStart, tableRangeEnd == null ? "inf" : tableRangeEnd, elementTable.size(), minTableSize, maxTableSize));
		Collections.sort(elementTable);
		for (int i = 0; i < elementTable.size(); i++) {
			String index = String.format("%03d ", i);
			System.out.println(index + elementTable.get(i));
		}
	}

}
