package yawl.model;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

public abstract class YNetElement {
	
	private String name;
	
	private YNet container;
	
	private YBasicTask canceledBy;
	
	private Vector<YNetElement> outward_flows;

	private Vector<YNetElement> inward_flows;
	
	public YNetElement() {
		this.outward_flows = new Vector<YNetElement>();
		this.inward_flows = new Vector<YNetElement>();
	}
	
	public YNetElement(String name) {
		this.name = name;
		this.outward_flows = new Vector<YNetElement>();
		this.inward_flows = new Vector<YNetElement>();
	}
	
	public YNetElement(String name, YNet container) {
		this.name = name;
		this.container(container);
		this.outward_flows = new Vector<YNetElement>();
		this.inward_flows = new Vector<YNetElement>();
	}	
	
	public String name() {
		return this.name;
	}
	
	public void name(String name) {
		this.name = name;
	}
	
	public YNet container() {
		return this.container;
	}
	
	public void container(YNet n) {
		if(container != n) {
			this.container = n;
			if(n != null)
			if(!n.elements().contains(this))
				n.addElement(this);
		}
	}
		
	public Vector<YNetElement> outwardFlows() {
		return this.outward_flows;
	}

	public void outwardFlows(Vector<YNetElement> f) {
		this.outward_flows = f;
	}
	
	public void addOutwardFlow(YNetElement f) {
		this.outward_flows.add(f);
	}
	
	public Vector<YNetElement> inwardFlows() {
		return this.inward_flows;
	}

	public void inwardFlows(Vector<YNetElement> f) {
		this.inward_flows = f;
	}
	
	public void addInwardFlow(YNetElement f) {
		this.inward_flows.add(f);
	}
	
	public YBasicTask canceledBy() {
		return this.canceledBy;
	}
	
	public void canceledBy(YBasicTask y) {
		this.canceledBy = y;
	}
	
	public abstract void accept(YSpecVisitor v);
	
	
}
