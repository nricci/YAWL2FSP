package fsp.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;

public abstract class FSPBox implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4009233244662199039L;

	private String name;
	
	private FSPNet container;
	
	/* Ports */
	
	protected SortedMap<String,FSPLink> input_names;
		
	protected SortedMap<String,FSPLink> output_names;
	
	protected SortedMap<String,FSPLink> control_names;
	
	
	
	public FSPBox() {
		this.input_names = new TreeMap<String, FSPLink>();
		this.output_names = new TreeMap<String, FSPLink>();
		this.control_names = new TreeMap<String, FSPLink>();
	}
	
	public FSPBox(String n) {
		this.input_names = new TreeMap<String, FSPLink>();
		this.output_names = new TreeMap<String, FSPLink>();
		this.control_names = new TreeMap<String, FSPLink>();
		this.name = n;
	}
	
	public abstract FSPProgram toFSP(FSPProgram p); 
	
	public final String name() {
		return this.name;
	}
	
	public final void name(String s) {
		this.name = s;
	}
	
	public void container(FSPNet n) {
		this.container = n;
	}
	
	public FSPNet container() {
		return this.container;
	}
	
	
	public final SortedSet<String> input_names() {
		SortedSet<String> s = new TreeSet<String>();
		s.addAll(this.input_names.keySet());
		return s;
	}
	
	public final SortedSet<String> output_names() {
		SortedSet<String> s = new TreeSet<String>();
		s.addAll(this.output_names.keySet());
		return s;
	}
	
	public final SortedSet<String> control_names() {
		SortedSet<String> s = new TreeSet<String>();
		s.addAll(this.control_names.keySet());
		return s;
	}
	
	public final LinkedList<String> all_names() {
		LinkedList<String> s = new LinkedList<String>();
		s.addAll(this.input_names());
		s.addAll(this.output_names());
		s.addAll(this.control_names());
		return s;
	}
	

	public final String first_input_available() {
		String r = null;
		for(String s : input_names()) {
			if(input_names.get(s) == null) {
				r = s;
			}
		}		
		return r;
	}
	
	public final String first_output_available() {
		String r = null;
		for(String s : output_names()) {
			if(output_names.get(s) == null) {
				r = s;
			}
		}		
		return r;
	}	
	
	public final void link_input(String i, FSPLink l) throws Exception {
		if(input_names.containsKey(i)) {
			if (input_names.get(i)==null) {
				input_names.put(i, l);
				l.add_member(this);
			} else {
				throw new Exception("The input name \""+i+"\" is already linked.");
			}
		} else {
			throw new Exception("The input name is non existing");
		}
	}
	
	public final void link_output(String o, FSPLink l) throws Exception {
		if(output_names.containsKey(o)) {
			if(output_names.get(o)==null) {
				output_names.put(o, l);
				l.add_member(this);
			} else {
				throw new Exception("The output name \""+o+"\" is already linked.");
			}
		} else {
			throw new Exception("The output name is non existing.");
		}
	}
	
	public final void link_control(String c, FSPLink l ) throws Exception {
		if(control_names.containsKey(c)) {
			if(control_names.get(c)==null) {
				control_names.put(c, l);
				l.add_member(this);
			} else {
				throw new Exception("The control name \""+c+"\" is already linked.");
			}
		} else {
			throw new Exception("The control name is non existing.");
		}		
	}
	
	public void link(String s, FSPLink l ) throws Exception {
		if(l.container() != this.container()) {
			throw new Exception("The supplied link and the current component belong " +
					"to different nets: link.container = " + l.container() + ", " +
							"this_component.container = " + this.container());
		}
		if(input_names.keySet().contains(s)) {
			link_input(s, l);
		} else if(output_names.keySet().contains(s)) {
			link_output(s, l);
		} else if(control_names.keySet().contains(s)) {
			link_control(s, l);
		} else {
			throw new Exception("The name provided ("+s+") does not exists");
		}
	}
	
	public FSPLink get_link(String name) {
		FSPLink res = null;
		if((res = input_names.get(name)) != null) {
			return res;
		} 
		if((res = output_names.get(name)) != null) {
			return res;
		} 
		if((res = control_names.get(name)) != null) {
			return res;
		} 
		return res;
	}
	
	public boolean is_linked(String name) {
		return (get_link(name) != null);
	}
	
	public abstract void accept(FSPSpecVisitor v);
		
	public void dumpMappings() {
		System.out.println("Mappings for: " + name() +"\n");
		for(String s : this.all_names()) {
			FSPLink l =  this.get_link(s);
			System.out.println(""+s+" -> " + ((l!=null)?(l.name()):l));
		}
	}
	
	
	
}
