package fsp.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.Map.Entry;

import flwa_to_fsp.FLWAtoFSP;
import flwa_to_fsp.FLWAtoFSPSingleton;


public class FSPNet extends FSPComposite  {

	private int link_num;
	
	/**
	 * 
	 */
	public FSPNet() {
		super();
		this.input_names.put("i_cond", null);
		this.input_names.put("o_cond", null);
	}

	/**
	 * @param n
	 */
	public FSPNet(String n) {
		super(n);
	}


	
	public void add_component(String prefix, FSPAtom b) {
		this.components().put(prefix, b);
		b.container(this);
	}
	
	
	/*	Linking
	*/
	
	public FSPLink link(String from, String from_port, String to, String to_port) throws Exception {
		
		if(from == null || to == null || from_port == null || to_port == null) {
			throw new Exception("Neither of the arguments must be null.");
		}
		
		FSPBox _from = this.component_by_prefix(from);
		FSPBox _to = this.component_by_prefix(to);
		
		if(_from == null) {
			throw new Exception("Non existing component: from - " + from + ".");
		}
		if(_to == null) {
			throw new Exception("Non existing component: to - " + to + ".");
		}
		
		link(_from,from_port,_to,to_port);
			
		return null;
	}
	
	
	public FSPLink link(FSPBox from, String from_port, FSPBox to, String to_port) throws Exception {
		
		if(from == null || to == null || from_port == null || to_port == null) {
			throw new Exception("Neither of the arguments must be null.");
		}
		
		if(!components().containsValue(from)) {
			throw new Exception("The supplied component does not belong to the net.");
		}
		if(!components().containsValue(to)) {
			throw new Exception("The supplied component does not belong to the net.");
		}
		
		FSPLink l = null;
		boolean a = from.is_linked(from_port);
		boolean b = to.is_linked(to_port);
		
		if(!a && !b) {
			// Neither is linked... proceed to link them	
			l = new_link();
			from.link(from_port, l);
			to.link(to_port, l);
		} else if (a && !b) {
			l = from.get_link(from_port);
			to.link(to_port, l);
		} else if (b && !a) {
			l = to.get_link(to_port);
			from.link(from_port, l);
		} else {
			if(from.get_link(from_port) != to.get_link(to_port)) {
				throw new Exception("Incompatible linking.");
			}
		}
		assert l != null;
					
		return l;
	}
	
	
	
	
	@Deprecated
	public FSPLink link(String ... names_ports) throws Exception {
		if(names_ports.length % 2 != 0) throw 
			new Exception("The list of parameters must be a multiple of 2");
		
		FSPLink l = new_link();
		
		LinkedList<FSPBox> boxes = new LinkedList<FSPBox>();
		for(int i = 0; i < names_ports.length; i = i+2) {
			FSPBox b = components().get(names_ports[i]) ; 
			if(b != null) {
				b.link(names_ports[i+1], l);
			}
		}
		this.links().add(l);	
		return l;
	}
	
	@Deprecated
	public FSPLink link_to_out(String comp, String comp_port) throws Exception {
		FSPLink l = components().get("").get_link("o_cond");
		FSPBox b = components().get(comp) ; 
		if(b != null) {
			b.link(comp_port, l);
		} else {
			throw new Exception("The specified component does not exist.");
		}
		return l;
	}	
	
	private FSPLink new_link() {
		FSPLink l = new FSPLink("link_" + link_num++,this);
		this.links().add(l);
		return l;
	}
	
	@Override
	public void accept(FSPSpecVisitor v) {
		v.visit(this);
		for(FSPBox b : components().values()) {
			b.accept(v);
		}
		for(FSPLink l : links()) {
			l.accept(v);
		}
		
	}
	
	
	
	@Override
	public FSPProgram toFSP(FSPProgram p) {
		//System.out.println(name());
		StringBuilder s = new StringBuilder();
		Set<String> dependencies = new HashSet<String>(); 
		LinkedList<String> low_prio = new LinkedList<String>();
				
		s.append("||" + this.name() + " = (\n");
		boolean first = true;
		for(Entry<String, FSPAtom> e : this.components().entrySet()) {
			
			String comp_kind;
			if(e.getValue() instanceof FSPAbstractTask) {
				if(((FSPAbstractTask) e.getValue()).decomposesTo() != null) {
					//System.out.println(e.getValue().name());
					p = ((FSPAbstractTask) e.getValue()).decomposesTo().toFSP(p);
					comp_kind = ((FSPAbstractTask) e.getValue()).decomposesTo().name();
				} else {
					comp_kind = e.getValue().fsp_name_w_mult();
				}
			} else {
				comp_kind = e.getValue().fsp_name_w_mult();
			}
			
			if(first) {
				s.append("\t\t" + e.getKey() + " : " + comp_kind);
				first = false;

			} else {
		
				s.append("\t||\t" + e.getKey() + " : " + comp_kind);
			}
			s.append('\n');
			
			if(e.getValue() instanceof FSP_OrJoin) {
				low_prio.add(e.getValue().get_link("out").name());
			}
			if(e.getValue() instanceof FSP_TresholdOrJoin) {
				low_prio.add(e.getValue().get_link("out").name());
			}
			
			
			p = e.getValue().toFSP(p);
		}
		s.append(") / {\n");
		
		/*
		 * 	Names linkage
		 */
		
		LinkedList<String> mmm = new LinkedList<String>();
		
		// For every element
		for(Entry<String, FSPAtom> e : this.components().entrySet()) {
			// For every name 
			for(String z : e.getValue().all_names()) {
				if(e.getValue().get_link(z)==null) {
					System.out.println("Warning: name ("+z+") not linked in component ("+e.getValue()+")\n");
				}
				assert e.getValue().get_link(z).members().size() > 1;
				// Patch
				//
				// e is a task that decomposes to something and
				// z is either "start" or "end" they have to be 
				// rewired to be i_cond and o_cond of the resp subnet...
				if(e.getValue() instanceof FSPAbstractTask) {
					if(((FSPAbstractTask) e.getValue()).decomposesTo() != null) {
						if(z=="start") {
							String link = e.getValue().get_link(z).name();
							String decomp_start = ((FSPAbstractTask) e.getValue()).decomposesTo().component_by_prefix("ynet").get_link("i_cond").name();
							s.append("\t" + link + "/" + e.getKey() + "." + decomp_start +",\n");
							// saving renaming info for FLWA
							FLWAtoFSP t_info = FLWAtoFSPSingleton.get_instance();
							t_info.renamesMap.put(e.getKey() + "." + "start"/*decomp_start*/, link);
							mmm.add(link);
							continue;
						}
						if(z=="end") {
							String link = e.getValue().get_link(z).name();
							String decomp_end = ((FSPAbstractTask) e.getValue()).decomposesTo().component_by_prefix("ynet").get_link("o_cond").name();
							s.append("\t" + link + "/" + e.getKey() + "." + decomp_end +",\n");
							// saving renaming info for FLWA
							FLWAtoFSP t_info = FLWAtoFSPSingleton.get_instance();
							t_info.renamesMap.put(e.getKey() + "." + "end"/*decomp_end*/, link);
							mmm.add(link);
							continue;
						}				
					}
				}
				// End of Patch
				
				
				String link = e.getValue().get_link(z).name();
				s.append("\t" + link + "/" + e.getKey() + "." + z +",\n");
				// saving renaming info for FLWA
				FLWAtoFSP t_info = FLWAtoFSPSingleton.get_instance();
				t_info.renamesMap.put(e.getKey() + "." + z,link);
				
				mmm.add(link);
			}
			s.append("\n");
		
		}
		
		HashMap<String,Integer> xxx = new HashMap<String, Integer>();
	
		for(String fff : mmm) {
			xxx.put(fff, new Integer(0));
		}
		for(String fff : mmm) {
			xxx.put(fff, new Integer(xxx.get(fff).intValue() + 1 ));
		}
		assert !xxx.values().contains(new Integer(1));
		
		
		s.deleteCharAt(s.length()-1);
		s.deleteCharAt(s.length()-1);
		s.deleteCharAt(s.length()-1);
		s.append("\n");
		
		s.append("}");
		
		if(!low_prio.isEmpty()) {
			s.append(">> {\n\t" + low_prio.pop());
			while(!low_prio.isEmpty()) {
				s.append(", " + low_prio.pop());
			}
			s.append("\n}");
		}
		
		s.append(".");
		p.add_definition(this.name(), s.toString());
		for(String d : dependencies) {
			p.make_dependent(this.name(), d);
		}
		return p;
	}

	
	

}
