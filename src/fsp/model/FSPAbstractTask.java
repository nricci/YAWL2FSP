package fsp.model;

public abstract class FSPAbstractTask extends FSPAtom {

	
	private FSPNet decomposesTo;
	
	private FSPJoin join;
	
	private FSPSplit split;
	
	public FSPAbstractTask() {
	}

	public FSPAbstractTask(String n) {
		super(n);
	}
	
	public FSPNet decomposesTo() {
		return this.decomposesTo;
	}
	
	public void decomposesTo(FSPNet n) {
		this.decomposesTo = n;
	}
	
	public FSPJoin join() {
		return this.join;
	}
	
	public void join(FSPJoin n) {
		this.join = n;
	}
	
	public FSPSplit split() {
		return this.split;
	}
	
	public void split(FSPSplit n) {
		this.split = n;
	}

	@Override
	public void accept(FSPSpecVisitor v) {
		// TODO Auto-generated method stub

	}

}
