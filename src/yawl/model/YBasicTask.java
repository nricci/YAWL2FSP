package yawl.model;

import java.util.Vector;

public abstract class YBasicTask extends YNetElement {
	
	private GateType join;
	
	private GateType split;	
	
	private YNetElement defaultFlow;
	
	private Vector<YNetElement> cancelSet;
	
	
	
	
	public YBasicTask() {
		super();
		this.cancelSet = new Vector<YNetElement>();
	}

	public YBasicTask(String name) {
		super(name);
		this.cancelSet = new Vector<YNetElement>();
	}
	
	public YBasicTask(String name, YNet container) {
		super(name,container);
		this.cancelSet = new Vector<YNetElement>();
	}	
	
	
	
	public GateType join() {
		return join;
	}

	public void join(GateType join) {
		this.join = join;
	}

	public GateType split() {
		return split;
	}

	public void split(GateType split) {
		this.split = split;
	}

	public YNetElement defaultFlow() {
		return defaultFlow;
	}

	public void defaultFlow(YNetElement defaultFlow) {
		this.defaultFlow = defaultFlow;
	}

	public Vector<YNetElement> cancelSet() {
		return cancelSet;
	}

	public void cancelSet(Vector<YNetElement> cancelSet) {
		this.cancelSet = cancelSet;
	}
	

	
	

}
