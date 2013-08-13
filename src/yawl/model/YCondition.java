package yawl.model;

public class YCondition extends YNetElement {

	public YCondition() {
		super();
	}
	
	public YCondition(String name) {
		super(name);
	}
	
	public YCondition(String name, YNet container) {
		super(name,container);
	}
	
	@Override
	public void accept(YSpecVisitor v) {
		v.visit(this);
		for(YNetElement e : outwardFlows()) {
			if(!v.visited(e)) e.accept(v);
		}
	}

}
