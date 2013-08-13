package fsp.model;

import java.util.LinkedList;

public class FSPSpecification {

	private String name;
	
	private FSPNet root_net;
	
	private LinkedList<FSPNet> nets;
	
	
	public FSPSpecification(String name) {
		this.name = name;
		this.nets = new LinkedList<FSPNet>();
	}

	public String name() {
		return name;
	}
	
	public void name(String name) {
		this.name = name;
	}
	
	public FSPNet root() {
		return this.root_net;
	}
	
	public void root(FSPNet n) {
		this.root_net = n;
	}
	
	public LinkedList<FSPNet> nets() {
		return nets;
	}
	
	public void nets(LinkedList<FSPNet> ns) {
		this.nets = ns;
	}
	
	public void add_net(FSPNet n) {
		nets.add(n);
	}
	
	public void accept(FSPSpecVisitor v) {
		v.visit(this);
		for(FSPNet n : nets) {
			n.accept(v);
		}
	}
	
	public FSPProgram toFSP(FSPProgram p) {
		return root_net.toFSP(new FSPProgram(name));
	}	
	
	
}
