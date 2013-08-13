package yawl.model;

import java.util.Vector;



public class YSpecification {

	
	/*	
	 *  Attributes
	*/
	
	/** The specificaion's name */
	private String name;
	
	/** The specification's nets */
	private Vector<YNet> nets;
	
	/** The specification's root net */
	private YNet root;
	
	/*
	 *	Constructors 
	*/

	public YSpecification() {
		this.nets = new Vector<YNet>();
	}

	public YSpecification(String specName) {
		this.name(specName);
		this.nets = new Vector<YNet>();
	}

	public YSpecification(String specName, YNet root) {
		this.name(specName);
		this.nets = new Vector<YNet>();
		this.root(root);
	}
	
	
	/*
	 *	Getters and Setters 
	*/

	/**
	 * "name" getter.
	 * @return the name of the specification.
	 */
	public String name() {
		return name;
	}	

	/**
	 * "name" setter.
	 * @param name  specification's new name
	 */
	public void name(String name) {
		this.name = name;
	}

	/**
	 * "nets" getter
	 * @return the set of nets of the specification.
	 */
	public Vector<YNet> nets() {
		return nets;
	}

	/**
	 * "nets" setter.
	 * @param nets  the new set of nets.
	 */
	public void nets(Vector<YNet> nets) {
		for(YNet n : this.nets) n.container(null);
		for(YNet n : nets) n.container(this);
		this.nets = nets;
	}

	/**
	 * "root" getter.
	 * @return the specification's root net.
	 */
	public YNet root() {
		return root;
	}

	/**
	 * "root" setter.
	 * @param root  the new root net.
	 */
	public void root(YNet root) {
		this.root = root;
	}
	
	/**
	 * Adds a net to the specification's set
	 * of nets.
	 * @param n  net to add to the specification.
	 */
	public void addNet(YNet n) {
		this.nets.add(n);
		if(n.container() != this) { 
			n.container(this);
		}
	}
	
	public void accept(YSpecVisitor v) {
		v.visit(this);
		for(YNet n : nets) {
			n.accept(v);
		}
	}
	
	
	
}
