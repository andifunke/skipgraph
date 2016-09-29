package de.skipgraph;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ElementTable {

	// TODO: list as set or sorted list (or sorted set?)
	private List<Element> elementTable = new ArrayList<>();
	private int minSize;
	private int maxSize;
	// TODO: allow negative values; null for -infinity;
	private BigDecimal rangeStart;
	private BigDecimal rangeEnd; // initial null-value means: no upper limit (= infinity)

	public ElementTable(int minSize, int maxSize) {
		this(new ArrayList<>(), minSize, maxSize, BigDecimal.ZERO, null);
	}

	public ElementTable(int minSize, int maxSize, BigDecimal rangeStart, BigDecimal rangeEnd) {
		this(new ArrayList<>(), minSize, maxSize, rangeStart, rangeEnd);
	}

	public ElementTable(List<Element> elementTable, int minSize, int maxSize, BigDecimal rangeStart, BigDecimal rangeEnd) {
		this.elementTable = elementTable;
		this.minSize = minSize;
		this.maxSize = maxSize;
		this.rangeStart = rangeStart;
		this.rangeEnd = rangeEnd;
	}

	public List<Element> getTable() {
		return elementTable;
	}

	public void setTable(List<Element> elementTable) {
		this.elementTable = elementTable;
	}

	public int getMinSize() {
		return minSize;
	}

	public void setMinSize(int minSize) {
		this.minSize = minSize;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	public BigDecimal getRangeStart() {
		return rangeStart;
	}

	public void setRangeStart(BigDecimal rangeStart) {
		this.rangeStart = rangeStart;
	}

	public BigDecimal getRangeEnd() {
		return rangeEnd;
	}

	public void setRangeEnd(BigDecimal rangeEnd) {
		this.rangeEnd = rangeEnd;
	}

	public int size() {
		return elementTable.size();
	}

	public void sort() {
		Collections.sort(elementTable);
	}

	public Element get(int i) {
		return elementTable.get(i);
	}

	public boolean add(Element element) {
		return elementTable.add(element);
	}

	public boolean remove(Element element) {
		return elementTable.remove(element);
	}

	/**
	 * splits the elementTable in two tables of same size
	 * @return	returns the second table with bigger values
	 */
	public ElementTable split() {
		sort();
		int roundup = 0;
		if (size() % 2 > 0) roundup = 1;
		List<Element> tmp = elementTable.subList(size() / 2 + roundup, size());
		ArrayList<Element> newTable = new ArrayList<>(tmp);
		tmp.clear();
		BigDecimal newStart = newTable.get(0).getValue();
		BigDecimal newEnd = rangeEnd;
		rangeEnd = newStart;
		return new ElementTable(newTable, minSize, maxSize, newStart, newEnd);
	}

	// TODO: should be a QueryOperation
	/**
	 * this method extends the elementTable of this node before the rangeStart of the original table.
	 * @param extension  an ElementTable that will be added before this table.
	 *                   the values of these elements must be less than the rangeStart of the original table.
	 *                   also updates the rangeStart.
	 * @param node
	 */
	public void extendElementTableAtStart(ElementTable extension, Node node) {
		rangeStart = extension.getRangeStart();
		elementTable.addAll(0, extension.getTable());
		node.getContactTable().updateAllContacts();
		// should be redundant
		checkMaxTableSize(node);
	}

	// TODO: should be a QueryOperation
	/**
	 * this method extends the elementTable of this node behind the rangeEnd of the original table.
	 * @param extension  a list of SkipGraphElements that will be added to the end of the table.
	 *                   the values of these elements must be higher than the rangeEnd of the original table.
	 *                   also sets a new rangeEnd to the elementTable. Must be higher than the original rangeEnd
	 * @param node
	 */
	public void extendElementTableAtEnd(ElementTable extension, Node node) {
		rangeEnd = extension.getRangeEnd();
		elementTable.addAll(extension.getTable());
		node.getContactTable().updateAllContacts();
		// should be redundant
		checkMaxTableSize(node);
	}

	// TODO: replace with compareTo ?
	/**
	 * checks if a given value is below or equal to the minimum value of the element table
	 *
	 * @param value
	 * @return
	 */
	public boolean isBelowElementTablesMinimum(BigDecimal value) {
		//System.out.println(String.format("%s (value) <= %s (start) ?", value, elementTableRangeStart));
		boolean ret;
		if (value == null) {
			ret = !(rangeStart == null);
		}
		else {
			ret = !(rangeStart != null && rangeStart.compareTo(value) <= 0);
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
		//System.out.println(String.format("%s (value) > %s (end) ?", value, elementTableRangeEnd));
		boolean ret;
		if (value == null) {
			ret = !(rangeEnd == null);
		}
		else {
			ret = rangeEnd != null && rangeEnd.compareTo(value) < 0;
		}
		//System.out.println(ret);
		return ret;
	}

	// TODO: implement methods in Node (no more need to give node as parameter)
	public void checkTableSize(Node node) {
		checkMinTableSize(node);
		checkMaxTableSize(node);
	}

	public void checkMinTableSize(Node node) {
		if (size() < minSize) {
			System.out.println("  ! table too small -> leave?");
			node.leave();
		}
	}

	public void checkMaxTableSize(Node node) {
		if (size() > maxSize) {
			System.out.println("  ! table too big -> split");
			node.split();
		}
	}

	public int getNumberOfFreeSlots() {
		return maxSize - size();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("--- element table [%s, %s) <size:%d (min:%d, max:%d)>\n",
				getRangeStart(), getRangeEnd() == null ? "inf" : getRangeEnd(), size(), getMinSize(), getMaxSize()));
		sort();
		for (int i = 0; i < size(); i++) {
			String index = String.format("%02d ", i);
			sb.append(index + get(i) + "\n");
		}
		return sb.toString();
	}

}
