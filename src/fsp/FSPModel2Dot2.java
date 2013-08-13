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

public class FSPModel2Dot2 implements FSPSpecVisitor {

	private File path;
	
	private StringBuffer dot_program;
	
	private FSPSpecification s;
	
	private Map<String,StringBuffer> progs;
	
	private boolean first = true;
	
	
	public FSPModel2Dot2(String path) {
		this.path = new File(path);
		this.progs = new HashMap<String,StringBuffer>();
		dot_program = new StringBuffer();
	}
	
	public void translate(FSPSpecification s) {
		s.accept(this);
		dot_program.append("}");
		path.mkdirs();

		
		for(Object z : progs.keySet()) {

			
			String x = (String) z;
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
				"//"+n.name()+" \n");
		

		first = false;

	}

	@Override
	public void visit(FSPTask t) {
		dot_program.append("\t" + t.name() + "[shape=box];\n");
		
	
		for(int i = 0; i < t.all_names().size(); i++) {
			String nm = t.all_names().get(i);
			FSPLink l = t.get_link(nm);
			if(l != null) {
				dot_program.append("\t" + t.name() + 
						"->" + l.name() + "[label="+nm.replace('[','_').replace(']','_')+"];\n");
			}		
		}
		

	}

	@Override
	public void visit(FSPLink l) {
		dot_program.append("\t" + l.name() + " [shape=point];\n");

	}

	@Override
	public void visit(FSPCondition c) {
		dot_program.append("\t" + c.name() + "[shape=circle, color=green];\n");
		
		
		for(int i = 0; i < c.all_names().size(); i++) {
			String nm = c.all_names().get(i);
			FSPLink l = c.get_link(nm);
			if(l != null) {
				dot_program.append("\t" + c.name() + 
						"->" + l.name() + "[label="+nm.replace('[','_').replace(']','_')+"];\n");
			}		
		}

	}

	@Override
	public void visit(FSP_YNET a) {
	dot_program.append("\t" + a.name() + "[shape=circle, color=yellow];\n");
		
		
		for(int i = 0; i < a.all_names().size(); i++) {
			String nm = a.all_names().get(i);
			FSPLink l = a.get_link(nm);
			if(l != null) {
				dot_program.append("\t" + a.name() + 
						"->" + l.name() + "[label="+nm.replace('[','_').replace(']','_')+"];\n");
			}		
		}

	}

	@Override
	public void visit(FSP_XorSplit g) {
		dot_program.append("\t" + g.name() + "[shape=triangle, color=red];\n");
		
		
		for(int i = 0; i < g.all_names().size(); i++) {
			String nm = g.all_names().get(i);
			FSPLink l = g.get_link(nm);
			if(l != null) {
				dot_program.append("\t" + g.name() + 
						"->" + l.name() + "[label="+nm.replace('[','_').replace(']','_')+"];\n");
			}		
		}
		
	}

	@Override
	public void visit(FSP_XorJoin g) {
		dot_program.append("\t" + g.name() + "[shape=triangle, color=red];\n");
		
		
		for(int i = 0; i < g.all_names().size(); i++) {
			String nm = g.all_names().get(i);
			FSPLink l = g.get_link(nm);
			if(l != null) {
				dot_program.append("\t" + g.name() + 
						"->" + l.name() + "[label="+nm.replace('[','_').replace(']','_')+"];\n");
			}		
		}
	}

	@Override
	public void visit(FSP_TresholdOrJoin g) {
		dot_program.append("\t" + g.name() + "[shape=triangle, color=red];\n");
		
		
		for(int i = 0; i < g.all_names().size(); i++) {
			String nm = g.all_names().get(i);
			FSPLink l = g.get_link(nm);
			if(l != null) {
				dot_program.append("\t" + g.name() + 
						"->" + l.name() + "[label="+nm.replace('[','_').replace(']','_')+"];\n");
			}		
		}
	}

	@Override
	public void visit(FSP_OrSplit g) {
		dot_program.append("\t" + g.name() + "[shape=triangle, color=red];\n");
		
		
		for(int i = 0; i < g.all_names().size(); i++) {
			String nm = g.all_names().get(i);
			FSPLink l = g.get_link(nm);
			if(l != null) {
				dot_program.append("\t" + g.name() + 
						"->" + l.name() + "[label="+nm.replace('[','_').replace(']','_')+"];\n");
			}		
		}

	}

	@Override
	public void visit(FSP_OrJoin g) {
		dot_program.append("\t" + g.name() + "[shape=triangle, color=red];\n");
		
		
		for(int i = 0; i < g.all_names().size(); i++) {
			String nm = g.all_names().get(i);
			FSPLink l = g.get_link(nm);
			if(l != null) {
				dot_program.append("\t" + g.name() + 
						"->" + l.name() + "[label="+nm.replace('[','_').replace(']','_')+"];\n");
			}		
		}
	}

	@Override
	public void visit(FSP_AndSplit g) {
		dot_program.append("\t" + g.name() + "[shape=triangle, color=red];\n");
		
		
		for(int i = 0; i < g.all_names().size(); i++) {
			String nm = g.all_names().get(i);
			FSPLink l = g.get_link(nm);
			if(l != null) {
				dot_program.append("\t" + g.name() + 
						"->" + l.name() + "[label="+nm.replace('[','_').replace(']','_')+"];\n");
			}		
		}
	}

	@Override
	public void visit(FSP_AndJoin g) {
		dot_program.append("\t" + g.name() + "[shape=triangle, color=red];\n");
		
		
		for(int i = 0; i < g.all_names().size(); i++) {
			String nm = g.all_names().get(i);
			FSPLink l = g.get_link(nm);
			if(l != null) {
				dot_program.append("\t" + g.name() + 
						"->" + l.name() + "[label="+nm.replace('[','_').replace(']','_')+"];\n");
			}		
		}
	}
	
	
	
	private void process_names_linkage(FSPBox b) {
	
	}

	@Override
	public void visit(FSPCancelableTask g) {
		dot_program.append("\t" + g.name() + "[shape=Msquare, color=black];\n");
		
		
		for(int i = 0; i < g.all_names().size(); i++) {
			String nm = g.all_names().get(i);
			FSPLink l = g.get_link(nm);
			if(l != null) {
				dot_program.append("\t" + g.name() + 
						"->" + l.name() + "[label="+nm.replace('[','_').replace(']','_')+"];\n");
			}		
		}
		
	}

	@Override
	public void visit(FSPCancelableConditon g) {
		dot_program.append("\t" + g.name() + "[shape=Mcircle, color=green];\n");
		
		
		for(int i = 0; i < g.all_names().size(); i++) {
			String nm = g.all_names().get(i);
			FSPLink l = g.get_link(nm);
			if(l != null) {
				dot_program.append("\t" + g.name() + 
						"->" + l.name() + "[label="+nm.replace('[','_').replace(']','_')+"];\n");
			}		
		}
	}
	

}
