package fsp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.jar.Attributes;

import fsp.model.FSPBox;
import fsp.model.FSPCancelableConditon;
import fsp.model.FSPCancelableTask;
import fsp.model.FSPCondition;
import fsp.model.FSPLink;
import fsp.model.FSPNet;
import fsp.model.FSPSpecVisitor;
import fsp.model.FSPSpecification;
import fsp.model.FSPTask;
import fsp.model.FSP_AndJoin;
import fsp.model.FSP_AndSplit;
import fsp.model.FSP_OrJoin;
import fsp.model.FSP_OrSplit;
import fsp.model.FSP_TresholdOrJoin;
import fsp.model.FSP_XorJoin;
import fsp.model.FSP_XorSplit;
import fsp.model.FSP_YNET;

public class FSPModel2Dot implements FSPSpecVisitor {

	private File path;
	
	private StringBuffer dot_program;
	
	private FSPSpecification s;
	
	private Map<String,StringBuffer> progs;
	
	private boolean first = true;
	
	
	public FSPModel2Dot(String path) {
		this.path = new File(path);
		this.progs = new HashMap<String,StringBuffer>();
		dot_program = new StringBuffer();
	}
	
	public void translate(FSPSpecification s) {
		s.accept(this);
		dot_program.append("}");
		path.mkdirs();

		System.out.println("hola");
		
		for(Object z : progs.keySet()) {

			
			String x = (String) z;
			System.out.println(x);
			System.out.println(progs.get(z));
			try {
				
				File file = new File( path + "/" + x.toLowerCase()
					+ ((x==s.root().name()?"(ROOT)":"")) + ".dot");
				

				FileWriter fstream = new FileWriter(file);
				BufferedWriter out = new BufferedWriter(fstream);
				out.write(progs.get(z).toString());
				out.close();
				
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void visit(FSPSpecification s) {
		this.s = s;
	}

	@Override
	public void visit(FSPNet n) {
		if(!first) {
			dot_program.append("}");
		} 
		progs.put(n.name(), new StringBuffer());
		dot_program = progs.get(n.name());
		
		dot_program.append("digraph {\n" +
				"//"+n.name()+" \n\tnode [shape=Mrecord];\n");
		

		first = false;

	}

	@Override
	public void visit(FSPTask t) {
		dot_program.append("\tstruct_" + t.name() + " [label=\""+ t.name() + "|{");
		
		for(int i = 0; i < t.all_names().size(); i++) {
			String s = t.all_names().get(i);
			dot_program.append("<" + s + "> " + s);
			if (i != t.all_names().size() - 1 ) {
				dot_program.append("|");
			}			
		}
		dot_program.append("}\"];\n");
		
		for(int i = 0; i < t.all_names().size(); i++) {
			String nm = t.all_names().get(i);
			FSPLink l = t.get_link(nm);
			if(l != null) {
				dot_program.append("\tstruct_" + t.name() + 
						":" + nm + "->" + l.name() + ";\n");
			}		
		}
		

	}

	@Override
	public void visit(FSPLink l) {
		dot_program.append("\t" + l.name() + " [shape=point];\n");

	}

	@Override
	public void visit(FSPCondition c) {
		dot_program.append("\tstruct_" + c.name() + " [color=green, label=\""+ c.name() + "|{");
		
		for(int i = 0; i < c.all_names().size(); i++) {
			String s = c.all_names().get(i);
			dot_program.append("<" + s.replace('[', '_').replace(']', '_') + "> " + s);
			if (i != c.all_names().size() - 1 ) {
				dot_program.append("|");
			}			
		}
		dot_program.append("}\"];\n");
		
		for(int i = 0; i < c.all_names().size(); i++) {
			String nm = c.all_names().get(i);
			FSPLink l = c.get_link(nm);
			if(l != null) {
				dot_program.append("\tstruct_" + c.name() + 
						":" + nm.replace('[', '_').replace(']', '_') + " -> " + l.name() + ";\n");
			}		
		}

	}

	@Override
	public void visit(FSP_YNET a) {
		dot_program.append("\tstruct_" + a.name() + " [color=yellow, label=\""+ a.name() + "|{");
		
		for(int i = 0; i < a.all_names().size(); i++) {
			String s = a.all_names().get(i);
			dot_program.append("<" + s + "> " + s);
			if (i != a.all_names().size() - 1 ) {
				dot_program.append("|");
			}			
		}
		dot_program.append("}\"];\n");
		
		for(int i = 0; i < a.all_names().size(); i++) {
			String nm = a.all_names().get(i);
			FSPLink l = a.get_link(nm);
			if(l != null) {
				dot_program.append("\tstruct_" + a.name() + 
						":" + nm + "->" + l.name() + ";\n");
			}		
		}

	}

	@Override
	public void visit(FSP_XorSplit g) {
		dot_program.append("\tstruct_" + g.name() + " [color=red, label=\"{"+ 
				g.name() + "| XOR_SPLIT }|{");
		
		for(int i = 0; i < g.all_names().size(); i++) {
			String s = g.all_names().get(i);
			dot_program.append("<" + s + "> " + s);
			if (i != g.all_names().size() - 1 ) {
				dot_program.append("|");
			}			
		}
		dot_program.append("}\"];\n");	
		for(int i = 0; i < g.all_names().size(); i++) {
			String nm = g.all_names().get(i);
			FSPLink l = g.get_link(nm);
			if(l != null) {
				dot_program.append("\tstruct_" + g.name() + 
						":" + nm + "->" + l.name() + ";\n");
			}		
		}
		
	}

	@Override
	public void visit(FSP_XorJoin g) {
		dot_program.append("\tstruct_" + g.name() + " [color=red, label=\"{"+ 
				g.name() + "| XOR_JOIN }|{");
		
		for(int i = 0; i < g.all_names().size(); i++) {
			String s = g.all_names().get(i);
			dot_program.append("<" + s + "> " + s);
			if (i != g.all_names().size() - 1 ) {
				dot_program.append("|");
			}			
		}
		dot_program.append("}\"];\n");	
		for(int i = 0; i < g.all_names().size(); i++) {
			String nm = g.all_names().get(i);
			FSPLink l = g.get_link(nm);
			if(l != null) {
				dot_program.append("\tstruct_" + g.name() + 
						":" + nm + "->" + l.name() + ";\n");
			}		
		}
	}

	@Override
	public void visit(FSP_TresholdOrJoin g) {
		dot_program.append("\tstruct_" + g.name() + " [color=red, label=\"{"+ 
				g.name() + "| TSH_OR_JOIN }|{");
		
		for(int i = 0; i < g.all_names().size(); i++) {
			String s = g.all_names().get(i);
			dot_program.append("<" + s + "> " + s);
			if (i != g.all_names().size() - 1 ) {
				dot_program.append("|");
			}			
		}
		dot_program.append("}\"];\n");	
		for(int i = 0; i < g.all_names().size(); i++) {
			String nm = g.all_names().get(i);
			FSPLink l = g.get_link(nm);
			if(l != null) {
				dot_program.append("\tstruct_" + g.name() + 
						":" + nm + "->" + l.name() + ";\n");
			}		
		}
	}

	@Override
	public void visit(FSP_OrSplit g) {
		dot_program.append("\tstruct_" + g.name() + " [color=red, label=\"{"+ 
				g.name() + "| OR_SPLIT }|{");
		
		for(int i = 0; i < g.all_names().size(); i++) {
			String s = g.all_names().get(i);
			dot_program.append("<" + s + "> " + s);
			if (i != g.all_names().size() - 1 ) {
				dot_program.append("|");
			}			
		}
		dot_program.append("}\"];\n");
		for(int i = 0; i < g.all_names().size(); i++) {
			String nm = g.all_names().get(i);
			FSPLink l = g.get_link(nm);
			if(l != null) {
				dot_program.append("\tstruct_" + g.name() + 
						":" + nm + "->" + l.name() + ";\n");
			}		
		}

	}

	@Override
	public void visit(FSP_OrJoin g) {
		dot_program.append("\tstruct_" + g.name() + " [color=red, label=\"{"+ 
				g.name() + "| OR_JOIN }|{");
		
		for(int i = 0; i < g.all_names().size(); i++) {
			String s = g.all_names().get(i);
			dot_program.append("<" + s + "> " + s);
			if (i != g.all_names().size() - 1 ) {
				dot_program.append("|");
			}			
		}
		dot_program.append("}\"];\n");
		for(int i = 0; i < g.all_names().size(); i++) {
			String nm = g.all_names().get(i);
			FSPLink l = g.get_link(nm);
			if(l != null) {
				dot_program.append("\tstruct_" + g.name() + 
						":" + nm + "->" + l.name() + ";\n");
			}		
		}
	}

	@Override
	public void visit(FSP_AndSplit g) {
		dot_program.append("\tstruct_" + g.name() + " [color=red, label=\"{"+ 
				g.name() + "| AND_SPLIT }|{");
		
		for(int i = 0; i < g.all_names().size(); i++) {
			String s = g.all_names().get(i);
			dot_program.append("<" + s + "> " + s);
			if (i != g.all_names().size() - 1 ) {
				dot_program.append("|");
			}			
		}
		dot_program.append("}\"];\n");	
		for(int i = 0; i < g.all_names().size(); i++) {
			String nm = g.all_names().get(i);
			FSPLink l = g.get_link(nm);
			if(l != null) {
				dot_program.append("\tstruct_" + g.name() + 
						":" + nm + "->" + l.name() + ";\n");
			}		
		}
	}

	@Override
	public void visit(FSP_AndJoin g) {
		dot_program.append("\tstruct_" + g.name() + " [color=red, label=\"{"+ 
				g.name() + "| AND_JOIN }|{");
		
		for(int i = 0; i < g.all_names().size(); i++) {
			String s = g.all_names().get(i);
			dot_program.append("<" + s + "> " + s);
			if (i != g.all_names().size() - 1 ) {
				dot_program.append("|");
			}			
		}
		dot_program.append("}\"];\n");	
		for(int i = 0; i < g.all_names().size(); i++) {
			String nm = g.all_names().get(i);
			FSPLink l = g.get_link(nm);
			if(l != null) {
				dot_program.append("\tstruct_" + g.name() + 
						":" + nm + "->" + l.name() + ";\n");
			}		
		}
	}
	
	
	
	private void process_names_linkage(FSPBox b) {
	
	}

	@Override
	public void visit(FSPCancelableTask fspCancelableTask) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(FSPCancelableConditon fspCancelableConditon) {
		// TODO Auto-generated method stub
		
	}
	

}
