package fsp.model;

public abstract class FSPAbstractCondition extends FSPAtom {

	private int ins;
	
	private int outs;
	
	
	
	public FSPAbstractCondition() {
		super();
	}

	public FSPAbstractCondition(String n) {
		super(n);
	
	}

	public int ins() {
		return this.ins;
	}
	
	protected void ins(int i) {
		ins = i;
	}
	
	public int outs() {
		return this.outs;
	}
	
	protected void outs(int o) {
		outs = o;
	}
	

}
