package yawl.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import yawl.model.*;

public class YModel2Dot {
	
	private File path;
	
	public YModel2Dot(String s) {
		this.path = new File(s);
	}
	
	public void YSpec2Dot2(YSpecification s) {
		String res = "";
		path.mkdirs();
		
		for(YNet n : s.nets()) {
			String dot_code = YNet2Dot(n); 
			try {
				String file = path + "/" + n.name().toLowerCase()
					+ ((n==s.root()?"(ROOT)":"")) + ".dot";

				FileWriter fstream = new FileWriter(file);
				BufferedWriter out = new BufferedWriter(fstream);
				out.write(dot_code);
				out.close();
				
			} catch(IOException e) {
				e.printStackTrace();
			}
			System.out.println(dot_code);

		}
		
		
		
	}
	
	public void YSpec2Dot(YSpecification s) {
		String res = "";
		path.mkdirs();
		
		for(YNet n : s.nets()) {
			String dot_code = YNet2Dot(n); 
			try {
				String file = path + "/" + n.name().toLowerCase()
					+ ((n==s.root()?"(ROOT)":"")) + ".dot";

				FileWriter fstream = new FileWriter(file);
				BufferedWriter out = new BufferedWriter(fstream);
				out.write(dot_code);
				out.close();
				
			} catch(IOException e) {
				e.printStackTrace();
			}
			System.out.println(dot_code);

		}
		
		
		
	}

	public String YNet2Dot(YNet n) {
		String r = "digraph {\n";
		r += "\t// "+n.name()+"\n\n\t// Elements\n";
		LinkedList<YNetElement> st = new LinkedList<YNetElement>();
		LinkedList<YNetElement> visited = new LinkedList<YNetElement>();
		
		for(YNetElement e : n.elements()) {
			r += "\t";
			if(e instanceof YCondition) {
				r += e.name() + " [shape=circle]"; 
			} else if(e instanceof YBasicTask) {
				YBasicTask _e = (YBasicTask) e;
				r += e.name() + " [shape=box]";
			}
			if(e == n.start()) {
				r += "[label=I]";
			}
			if(e == n.end()) {
				r += "[label=O]";
			}
			r += ";\n";
			
			/*if(e instanceof YBasicTask) {
				YBasicTask _e = (YBasicTask) e;
				if(!(_e.join().compareTo(GateType.NONE)==0)) {
					r += "\t" + e.name() + "_join [shape=invtriangle][label="
						+	_e.join().toString() + "];\n";
				}
				if(!(_e.split().compareTo(GateType.NONE)==0)) {
					r += "\t" + e.name() + "_split [shape=triangle][label="
						+	_e.join().toString() + "];\n";
				}
			}*/
			
		}
		r+="\n\t// Arrows\n";
		
		st.push(n.start());
		while(!st.isEmpty()) {
			YNetElement e = st.pop();

			assert n.elements().contains(e);
			if(!visited.contains(e)) {
				
				for(YNetElement next : e.outwardFlows()) {
					st.push(next);
					r += "\t" + e.name() + "->" + next.name() + ";\n";
				}
				for(YNetElement prev : e.inwardFlows()) {
					r += "\t" + e.name() + "->" + prev.name() + "[color=red];\n";
				}
				if (e instanceof YBasicTask) { 
					for(YNetElement next : ((YBasicTask) e).cancelSet()) {
						
						r += "\t" + e.name() + "->" + next.name() + "[style=dotted];\n";
					}
				}
				visited.add(e);
			}			
		}		
		System.out.print("visited = <");
		
		for(YNetElement e : visited) {
			System.out.print(e.name()+" ");
		}
		System.out.println(">");
		System.out.print("elements = <");
		
		for(YNetElement e : n.elements()) {
			System.out.print(e.name()+" ");
		}
		System.out.println(">");
		
System.out.print(r+"}");
		
		assert visited.containsAll(n.elements());
		return r+"}";
	}
	
}
