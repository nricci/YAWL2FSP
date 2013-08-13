package fsp.model;

public abstract class FSPAtom extends FSPBox {

	public FSPAtom() {}

	public FSPAtom(String n) {
		super(n);
	}
	
	protected abstract String fsp_name();
	
	protected abstract String fsp_name_w_mult();
	
	
}
