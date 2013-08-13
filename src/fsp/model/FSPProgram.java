package fsp.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FSPProgram {
	
	private String name;

	private Map<String,String> definitions;
	
	private Map<String,String> prelude_definitions;
	
	private Map<String,Set<String>> dependencies;
	
	public FSPProgram(String s) {
		this.definitions = new HashMap<String, String>();
		this.dependencies = new HashMap<String, Set<String>>();
		this.prelude_definitions = new HashMap<String, String>();
		this.name = s;
	}
	
	public void add_prelude_definition(String id, String def) {
		if(!prelude_definitions.containsKey(id)) {
			this.prelude_definitions.put(id, def);
		} else {
			//System.out.println("Program already had a definition for " + id + " ... definition dropped...");
		}
	}
	
	public void add_definition(String id, String def) {
		if(!definitions.containsKey(id)) {
			this.definitions.put(id, def);
			this.dependencies.put(id, new HashSet<String>());
		} else {
			//System.out.println("Program already had a definition for " + id + " ... definition dropped...");
		}
	}
	
	public void make_dependent(String x, String y) {
		this.dependencies.get(x).add(y);
	}
	
	public boolean depends(String x, String y) {
		return this.dependencies.get(x).contains(y);
	}
	
	
	
	public String dump() {
		StringBuilder b = new StringBuilder();
		
		for(String x : prelude_definitions.values()) {
			b.append(x);
			b.append("\n");
		}
		
		for(String x : definitions.values()) {
			b.append(x);
			b.append("\n");
			b.append("\n");
		}
		
		
		return b.toString();
	}
	
}
