package yawl.model;

public class YMultipleCompositeTask extends YBasicTask implements YMultiple,YComposite {
	
	private YNet decomposesTo;
	
	private int lowerBound;
	
	private int upperBound;
	
	private int threshold;
	
	private CreationMode cmode;


	/** */
	public YMultipleCompositeTask() {
		super();
	}

	/**
	 * @param name
	 * @param container
	 */
	public YMultipleCompositeTask(String name, YNet container) {
		super(name, container);
	}

	/**
	 * @param name
	 */
	public YMultipleCompositeTask(String name) {
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
	public YNet decomposesTo() {
		return decomposesTo;
	}

	public void decomposesTo(YNet decomposesTo) {
		this.decomposesTo = decomposesTo;
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
