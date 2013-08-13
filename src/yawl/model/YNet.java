package yawl.model;

import java.util.Vector;

public class YNet {

	/*
	 *	Attributes 
	*/
	
	/** The name of the net. */
	private String name;
	
	/** The specification that contains this net. */
	private YSpecification container;
	
	/** The net's tasks */
	private Vector<YBasicTask> tasks; 
	
	/** The net's conditions */
	private Vector<YCondition> conds;
	
	/** The input condition */
	private YCondition start;
	
	/** The end condition */
	private YCondition end;

	/*
	 * Constructors 
	*/
	
	public YNet(String name) {
		this.name(name);
		this.tasks = new Vector<YBasicTask>();
		this.conds = new Vector<YCondition>();
	}

	public YNet(String name, YSpecification containerSpec) {
		this.name(name);
		this.container(containerSpec);
		this.tasks = new Vector<YBasicTask>();
		this.conds = new Vector<YCondition>();
	}
	
	/*
	 *  Getters and Setters 
	*/

	public String name() {
		return name;
	}

	public void name(String name) {
		this.name = name;
	}

	public YSpecification container() {
		return container;
	}

	public void container(YSpecification containerSpec) {
		if(container != containerSpec) { 
			this.container = containerSpec;
			if(containerSpec != null)
			if(!container.nets().contains(this)) {
				container.addNet(this);
			}
		}
	}

	public Vector<YBasicTask> tasks() {
		return tasks;
	}

	public void tasks(Vector<YBasicTask> tasks) {
		for(YBasicTask t : this.tasks) t.container(null);
		for(YBasicTask t : tasks) t.container(this);
		this.tasks = tasks;
	}
	
	public void addTask(YTask t) {
		this.tasks.add(t);
		t.container(this);
	}

	public Vector<YCondition> conds() {
		return conds;
	}

	public void conds(Vector<YCondition> conds) {
		for(YCondition c : conds) c.container(this);
		for(YCondition c : this.conds) c.container(null);
		this.conds = conds;
	}
	
	public void addCond(YCondition c) {
		this.conds.add(c);
		c.container(this);
	}

	public YCondition start() {
		return start;
	}

	public void start(YCondition start) {
		this.start = start;
		if(!conds.contains(start)) {
			addCond(start);
		}
	}

	public YCondition end() {
		return end;
	}

	public void end(YCondition end) {
		this.end = end;
		if(!conds.contains(end)) {
			addCond(end);
		}
	}
	
	public void addElement(YNetElement e) {
		if(e instanceof YBasicTask) {
			tasks.add((YBasicTask) e);
		} else if(e instanceof YCondition) {
			conds.add((YCondition) e);
		}
		e.container(this);
	}
	
	public void elements(Vector<YNetElement> els) {
		for(YNetElement e : this.elements()) e.container(null);
		this.tasks = new Vector<YBasicTask>();
		this.conds = new Vector<YCondition>();		
		for(YNetElement e : els) addElement(e);
	}
	
	public Vector<YNetElement> elements() {
		Vector<YNetElement> v = new Vector<YNetElement>();
		v.addAll(tasks);
		v.addAll(conds);
		return v;
	}
	
	public void accept(YSpecVisitor v) {
		v.visit(this);
		this.start.accept(v);	
	}
	
	
	
	
	
	
}
