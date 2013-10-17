package fsp.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class FSPLink implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4396443120662996078L;

	private String name;
	
	private List<FSPBox> members;
	
	private FSPComposite container;
	
	public FSPLink(String name, FSPComposite cont) {
		members = new LinkedList<FSPBox>();
		this.name = "";
		this.container = cont;
	}
	
	public FSPLink(String name, FSPComposite cont, FSPBox ...boxs) {
		members = new LinkedList<FSPBox>();
		for(FSPBox b : boxs) {
			members.add(b);
		}
		this.name = "";
		this.container = cont;
	}
	
	public String name() {
		return this.name;
	}
	
	public void name(String n) {
		this.name = n;
	}
	
	public FSPComposite container() {
		return this.container;
	}
	
	public void container(FSPComposite c) {
		this.container = c;
	}
	
	public List<FSPBox> members() {
		return members;
	}
	
	public void add_member(FSPBox b) {
		name += "_" + b.name();
		this.members.add(b);
	}

	public void accept(FSPSpecVisitor v) {
		v.visit(this);
	}
	
	
	

}
