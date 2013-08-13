package fsp.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public abstract class FSPComposite extends FSPBox {

	private Map<String,FSPAtom> components;
	
	private LinkedList<FSPLink> links; 
	
	public FSPComposite() {
		super();
		this.components = new HashMap<String,FSPAtom>();
		this.links = new LinkedList<FSPLink>();
	}
	
	public FSPComposite(String n) {
		super(n);
		this.components = new HashMap<String,FSPAtom>();
		this.links = new LinkedList<FSPLink>();
	}	
	
	public Map<String,FSPAtom> components() {
		return this.components;
	}

	public void components(Map<String,FSPAtom> c) {
		this.components = c;
	}
	

	
	public FSPBox component_by_prefix(String id) {
		return components.get(id);
	}
	
	public LinkedList<FSPLink> links() {
		return this.links;
	}

	public void links(LinkedList<FSPLink> l) {
		this.links = l;
	}	
	
	
}
