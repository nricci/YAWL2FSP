package yawl.model;

public class YCompositeTask  extends YBasicTask implements YComposite {
	
	private YNet decomposesTo;
	
	/**
	 * 
	 */
	public YCompositeTask() {
		super();
	}

	/**
	 * @param name
	 * @param container
	 */
	public YCompositeTask(String name, YNet container) {
		super(name, container);
	}

	/**
	 * @param name
	 */
	public YCompositeTask(String name) {
		super(name);
	}

	public YNet decomposesTo() {
		return decomposesTo;
	}

	public void decomposesTo(YNet decomposesTo) {
		this.decomposesTo = decomposesTo;
	}	
	
	@Override
	public void accept(YSpecVisitor v) {
		v.visit(this);
		for(YNetElement e : outwardFlows()) {
			if(!v.visited(e)) e.accept(v);
		}
	}

}
