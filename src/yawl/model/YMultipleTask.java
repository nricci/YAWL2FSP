package yawl.model;

public class YMultipleTask extends YBasicTask implements YMultiple {	
	
	private int lowerBound;
	
	private int upperBound;
	
	private int threshold;
	
	private CreationMode cmode;
	
	/**
	 * 
	 */
	public YMultipleTask() {
		super();
	}

	/**
	 * @param name
	 * @param container
	 */
	public YMultipleTask(String name, YNet container) {
		super(name, container);
	}

	/**
	 * @param name
	 */
	public YMultipleTask(String name) {
		super(name);
	}

	public int lowerBound() {
		return lowerBound;
	}

	public void lowerBound(int lowerBound) {
		this.lowerBound = lowerBound;
	}

	public int upperBound() {
		return upperBound;
	}

	public void upperBound(int upperBound) {
		this.upperBound = upperBound;
	}
	
	@Override
	public CreationMode creationMode() {
		return this.cmode;
	}

	@Override
	public void creationMode(CreationMode c) {
		this.cmode = c;
	}

	@Override
	public int threshold() {
		return this.threshold;
	}

	@Override
	public void threshold(int t) {
		this.threshold = t;
	}
	
	
	
	
	@Override
	public void accept(YSpecVisitor v) {
		v.visit(this);
		for(YNetElement e : outwardFlows()) {
			if(!v.visited(e)) e.accept(v);
		}
	}

	
	
	
}
