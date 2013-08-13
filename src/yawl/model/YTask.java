package yawl.model;

public class YTask extends YBasicTask {

	public YTask() {
		super();
	}

	public YTask(String name) {
		super(name);
	}

	public YTask(String name, YNet container) {
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
