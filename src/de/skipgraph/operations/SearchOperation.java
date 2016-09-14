package de.skipgraph.operations;

import de.skipgraph.Element;
import de.skipgraph.ElementTable;
import de.skipgraph.Node;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SearchOperation extends QueryOperation {

	private String capacity;
	private BigDecimal valueStart;
	private BigDecimal valueEnd;
	private int maxNumberOfVals;

	public SearchOperation(BigDecimal valueStart, BigDecimal valueEnd, int maxNumberOfVals) {
		this(null, valueStart, valueEnd, maxNumberOfVals);
	}

	public SearchOperation(String capacity, BigDecimal valueStart, BigDecimal valueEnd, int maxNumberOfVals) {
		this.capacity = capacity;
		this.valueStart = valueStart;
		this.valueEnd = valueEnd;
		this.maxNumberOfVals = maxNumberOfVals;
	}

	public BigDecimal getValueStart() {
		return valueStart;
	}

	public void setValueStart(BigDecimal valueStart) {
		this.valueStart = valueStart;
	}

	public BigDecimal getValueEnd() {
		return valueEnd;
	}

	public void setValueEnd(BigDecimal valueEnd) {
		this.valueEnd = valueEnd;
	}

	public int getMaxNumberOfVals() {
		return maxNumberOfVals;
	}

	public void setMaxNumberOfVals(int maxNumberOfVals) {
		this.maxNumberOfVals = maxNumberOfVals;
	}

	public String getCapacity() {
		return capacity;
	}

	public void setCapacity(String capacity) {
		this.capacity = capacity;
	}

	@Override
	public List<Element> execute(Node node) {
		// debug infos:
		String endVal = valueEnd != null ? valueEnd.toString() : "infinity";
		String capStr = capacity != null ? " for " + capacity : "";
		String maxStr = maxNumberOfVals > 0 ? " (maximum " + maxNumberOfVals + " values)" : "";
		System.out.println("searching" + capStr + " from " + valueStart + " to " + endVal + maxStr);

		// local variables
		ElementTable elementTable = node.getElementTable();

		// search range is above the node's table range ?
		if (elementTable.isAboveElementTablesMaximum(valueStart)) {
			System.out.print("-> next: ");
			return node.getContactTable().getNextNodeForValue(valueStart).execute(this);
		}
		// search range is below the node's table range ?
		else if (valueEnd != null && elementTable.isBelowElementTablesMinimum(valueEnd)) {
			System.out.print("-> prev: ");
			return node.getContactTable().getPrevNodeForValue(valueStart).execute(this);
		}
		// search starts below the node's table range ?
		else if (valueStart != null && elementTable.isBelowElementTablesMinimum(valueStart)) {
			System.out.print("-> prev: ");
			return node.getContactTable().getPrevNodeForValue(valueStart).execute(this);
		}
		// search starts inside the node's table range.
		else {
			List<Element> retList = new ArrayList<>();

			// search without capacity
			if (capacity == null) {
				// open end
				if (valueEnd == null) {
					for (Element element :
							elementTable.getTable()) {
						if (element.getValue().compareTo(valueStart) >= 0) {
							retList.add(element);
							if (maxNumberOfVals > 0 && retList.size() >= maxNumberOfVals) return retList;
						}
					}
				// closed end
				} else {
					for (Element element :
							elementTable.getTable()) {
						if (element.getValue().compareTo(valueStart) >= 0 &&
								element.getValue().compareTo(valueEnd) <= 0) {
							retList.add(element);
							if (maxNumberOfVals > 0 && retList.size() >= maxNumberOfVals) return retList;
						}
					}
				}
			}

			// search with capacity
			else {
				// open end
				if (valueEnd == null) {
					for (Element element :
							elementTable.getTable()) {
						if (capacity.equals(element.getCapacity()) &&
								element.getValue().compareTo(valueStart) >= 0) {
							retList.add(element);
							if (maxNumberOfVals > 0 && retList.size() >= maxNumberOfVals) return retList;
						}
					}
				// closed end
				} else {
					for (Element element :
							elementTable.getTable()) {
						if (capacity.equals(element.getCapacity()) &&
								element.getValue().compareTo(valueStart) >= 0 &&
								element.getValue().compareTo(valueEnd) <= 0) {
							retList.add(element);
							if (maxNumberOfVals > 0 && retList.size() >= maxNumberOfVals) return retList;
						}
					}
				}
			}

			// give search to next node if required (i.e. valueEnd >= tableRrangeEnd)
			if (elementTable.getRangeEnd() != null) {
				//System.out.println("end: " + node.getElementTableRangeEnd());
				if (valueEnd == null || elementTable.isAboveElementTablesMaximum(valueEnd)) {
					if (maxNumberOfVals > 0) {
						maxNumberOfVals -= retList.size();
					}
					// System.out.println("maximum " + maxNumberOfVals + " values");
					valueStart = elementTable.getRangeEnd();
					System.out.print("-> next: ");
					retList.addAll(node.getContactTable().getNextNode().execute(this));
				}
			}
			return retList;
		}
	}

}
