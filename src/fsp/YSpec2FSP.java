package fsp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.jar.Attributes;

import fsp.model.*;

import yawl.model.*;


public class YSpec2FSP implements YSpecVisitor {

	private static final int default_multiple_tasks_high_bound = 5;

	private YSpecification s;
	
	private Attributes atts;

	private YNet current_net;
	
	private FSPSpecification _s;
	
	private FSPNet _current_net;
	
	private Map<YNetElement,FSPBox> _components;
	
	private Map<YNet,FSPNet> _nets;
	
	private int phase = 1;
	
	private Set<YNetElement> visited;
	
	
	public YSpec2FSP(YSpecification y, Attributes atts) {
		this.s = y;
		this.atts = atts;
		this._components = new HashMap<YNetElement, FSPBox>();
		this.visited = new HashSet<YNetElement>();
		this._nets = new HashMap<YNet, FSPNet>();
		phase = 1;
	}
	
	public FSPSpecification translate() throws YSpec2FSPException {
		s.accept(this);
		phase = 2;
		visited.clear();		
		s.accept(this);
		phase = 3;
		visited.clear();		
		s.accept(this);
		
		return this._s;
	}

	public HashMap<String, String> get_name_translation_map() {
		HashMap<String, String> res = new HashMap<String, String>();
		
		for(YNetElement x : _components.keySet()) {
			res.put(x.name(), _components.get(x).name());
		}
		
		return res;
	}
	
	@Override
	public void visit(YSpecification s) {
		if(phase == 1) {
			_s = new FSPSpecification(s.name());
		}
	}

	@Override
	public void visit(YNet n) {
		if(phase == 1) {
			FSPNet x = new FSPNet(n.name());
			_s.add_net(x);
			_current_net = x;
			current_net = n;
			_nets.put(n, x);
			if(n.container().root() == n) {
				_s.root(x);
			}
			
			FSP_YNET y = new FSP_YNET("Ynet");
			_current_net.add_component("ynet", y);
	
		}
	}

	@Override
	public void visit(YCondition t) {
		visited.add(t);
		if(phase == 1) {
			assert current_net == t.container();
			assert current_net.start() != current_net.end();
			assert _current_net.component_by_prefix("ynet") != null;
			try {
				if(t==current_net.start()) {
					FSPCondition c = new FSPCondition(t.name(), 1, t.outwardFlows().size());
					_current_net.add_component(c.name(), c);
					_current_net.link(c.name(),"in[1]","ynet","i_cond");
					_components.put(t,c);
		
				} else if(t==current_net.end()) {
					FSPCondition c = new FSPCondition(t.name(), t.inwardFlows().size(), 1);
					_current_net.add_component(c.name(), c);
					_current_net.link(c.name(),"out[1]","ynet","o_cond");
					_components.put(t,c);				
	
				} else {
					int i = (t==current_net.start())?1:t.inwardFlows().size();
					int o = (t==current_net.end())?1:t.outwardFlows().size();
					if(t.canceledBy() == null) {
						FSPCondition c = new FSPCondition(t.name(), i, o);
						_current_net.add_component(c.name(), c);
						
						_components.put(t,c);
					} else {
						FSPCancelableConditon c = new FSPCancelableConditon(t.name(), i, o);	
						_current_net.add_component(c.name(), c);
						_components.put(t,c);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				assert false;
			}
			
		} else if (phase == 2){
			process_outgoing_links(t);
		} else if (phase == 3) {
			process_cancel_regions(t);
		} else {}
	}

	@Override
	public void visit(YTask t) {
		visited.add(t);
		if(phase == 1) {
			FSPAbstractTask c;
			if(t.canceledBy() != null) {
				c = new FSPCancelableTask(t.name());
			} else {
				c = new FSPTask(t.name());
			}
			c.container(_current_net);
			_current_net.components().put(c.name(), c);
			_components.put(t,c);
			
			assert t.name() == c.name();

			process_gates(t, c);
			
		} else if (phase == 2){
			process_outgoing_links(t);
		} else if (phase == 3) {
			process_cancel_regions(t);
		} else {}
	}

	@Override
	public void visit(YCompositeTask t) {
		visited.add(t);
		if(phase == 1) {
			FSPTask c = new FSPTask(t.name());
			c.container(_current_net);
			_current_net.components().put(c.name(), c);
			_components.put(t,c);
			
			assert t.name() == c.name();
			
			process_gates(t, c);

		} else if (phase == 2) {
			((FSPAbstractTask)_components.get(t)).decomposesTo(_nets.get(t.decomposesTo()));
			process_outgoing_links(t);
			 
		} else if (phase == 3) {
			//process_cancel_regions(t);
		} else {}
	}

	private void limit_multiple_tasks(YMultiple t) {
		String x = atts.getValue("multiple_tasks_high_bound");
		int max;
		if(x == null) {
			max = this.default_multiple_tasks_high_bound;
		} else {
			max = Integer.parseInt(x);
		}
		if(t.upperBound() > max) {
			t.upperBound(max);
		}
		if(t.lowerBound() > max) {
			t.lowerBound(max);
		}
		if(t.threshold() > max) {
			t.threshold(max);
		}
	
	}
	
	
	@Override
	public void visit(YMultipleTask t) {
		visited.add(t);	
		if(phase == 1) {			
			FSPTask c = new FSPTask(t.name());
			c.container(_current_net);
			_current_net.components().put(c.name(), c);
			_components.put(t,c);			
			assert t.name() == c.name();			
			process_gates(t, c);
/*
			System.out.println("Multiple Task PreLimit");
			System.out.println("t = "+t.name());
			System.out.println("mode = "+t.creationMode());
			System.out.println("low = "+t.lowerBound());
			System.out.println("high = "+t.upperBound());
			System.out.println("trsh = "+t.threshold());
*/			
			limit_multiple_tasks(t);
/*			
			System.out.println("Multiple Task PostLimit");
			System.out.println("t = "+t.name());
			System.out.println("mode = "+t.creationMode());
			System.out.println("low = "+t.lowerBound());
			System.out.println("high = "+t.upperBound());
			System.out.println("trsh = "+t.threshold());
*/			
			/* Assembling net that simulates the 
			 * behavior of the multiple instances
			 */
			if(t.creationMode().equals(CreationMode.STATIC)) {
				try {
					FSPNet deco = new FSPNet(t.name().toUpperCase()+"_DECOMP");
					_s.add_net(deco);
					c.decomposesTo(deco);
								
					FSP_YNET y = new FSP_YNET("Ynet");
					deco.add_component("ynet", y);
					
					// Input condition
					FSPCondition input = new FSPCondition("input_condition", 1, 1);
					deco.add_component("input_condition", input);
					deco.link("input_condition", "in[1]", "ynet", "i_cond");
					
					// Output condition
					FSPCondition output = new FSPCondition("output_condition", 1, 1);
					deco.add_component("output_condition", output);
					deco.link("output_condition", "out[1]", "ynet", "o_cond");
				
					// Spliter
					FSPTask spliter = new FSPTask("spliter");
					deco.add_component("spliter", spliter);
					deco.link("spliter", "start", "input_condition", "out[1]");
										
					FSP_AndSplit spliter_split = new FSP_AndSplit("spliter_split", t.upperBound());
					deco.add_component("spliter_split", spliter_split);
					deco.link("spliter_split", "in", "spliter", "end");
					deco.link("spliter_split", "o_cond", "ynet", "o_cond");
					
					// Merger
					FSPTask merger = new FSPTask("merger");
					deco.add_component("merger", merger);
					deco.link("merger", "end", "output_condition", "in[1]");
					
					FSP_AndJoin merger_join = new FSP_AndJoin("merger_join", t.upperBound());
					deco.add_component("merger_join", merger_join);
					deco.link("merger_join", "out", "merger", "start");
					deco.link("merger_join", "o_cond", "ynet", "o_cond");
					
					for(int index = 1; index<=t.upperBound(); index++) {
						FSPTask multi = new FSPTask("multi_"+index);
						deco.add_component("multi"+index, multi);
						deco.link("multi"+index, "start", "spliter_split", "out["+index+"]");
						deco.link("multi"+index, "end", "merger_join", "in["+index+"]");
					}					
				
				} catch (Exception e) {
					e.printStackTrace();
					assert false;
				}
			}
			if(t.creationMode().equals(CreationMode.DYNAMIC)) {
				try {
					
					FSPNet deco = new FSPNet(t.name().toUpperCase()+"_DECOMP");
					_s.add_net(deco);
					c.decomposesTo(deco);
								
					FSP_YNET y = new FSP_YNET("Ynet");
					deco.add_component("ynet", y);
					
					// Input condition
					FSPCondition input = new FSPCondition("input_condition", 1, 1);
					deco.add_component("input_condition", input);
					deco.link("input_condition", "in[1]", "ynet", "i_cond");
					
					// Output condition
					FSPCondition output = new FSPCondition("output_condition", 1, 1);
					deco.add_component("output_condition", output);
					deco.link("output_condition", "out[1]", "ynet", "o_cond");
				
					// SpliterI
					FSPTask spliter1 = new FSPTask("spliter1");
					deco.add_component("spliter1", spliter1);
					deco.link("spliter1", "start", "input_condition", "out[1]");
										
					FSP_AndSplit spliter1_split = new FSP_AndSplit("spliter1_split", t.lowerBound()+1);
					deco.add_component("spliter1_split", spliter1_split);
					deco.link("spliter1_split", "in", "spliter1", "end");
					deco.link("spliter1_split", "o_cond", "ynet", "o_cond");
					
					// SpliterII
					FSPTask spliter2 = new FSPTask("spliter2");
					deco.add_component("spliter2", spliter2);
					deco.link("spliter2", "start", "spliter1_split", "out["+(t.lowerBound()+1)+"]");
										
					FSP_OrSplit spliter2_split = new FSP_OrSplit("spliter2_split", t.upperBound()-t.lowerBound()-1);
					deco.add_component("spliter2_split", spliter2_split);
					deco.link("spliter2_split", "in", "spliter2", "end");
					deco.link("spliter2_split", "o_cond", "ynet", "o_cond");
					
					// Merger
					FSPTask merger = new FSPTask("merger");
					deco.add_component("merger", merger);
					deco.link("merger", "end", "output_condition", "in[1]");
					
					FSP_TresholdOrJoin merger_join = new FSP_TresholdOrJoin("merger_join",t.upperBound(), t.threshold());
					deco.add_component("merger_join", merger_join);
					deco.link("merger_join", "out", "merger", "start");
					deco.link("merger_join", "o_cond", "ynet", "o_cond");
					
					int index;
					for(index = 1; index<=t.lowerBound(); index++) {
						FSPTask multi = new FSPTask("multi_"+index);
						deco.add_component("multi"+index, multi);
						deco.link("multi"+index, "start", "spliter1_split", "out["+index+"]");
						deco.link("multi"+index, "end", "merger_join", "in["+index+"]");
					}	
					int z = 1;
					for(; index<=t.upperBound()-1; index++) {
						FSPTask multi = new FSPTask("multi_"+index);
						deco.add_component("multi"+index, multi);
						deco.link("multi"+index, "start", "spliter2_split", "out["+z+"]");
						deco.link("multi"+index, "end", "merger_join", "in["+index+"]");
						z++;
					}
					FSPTask multi = new FSPTask("multi_"+t.upperBound());
					deco.add_component("multi"+t.upperBound(), multi);
					deco.link("multi"+t.upperBound(), "start", "spliter2_split", "default_out");
					deco.link("multi"+t.upperBound(), "end", "merger_join", "in["+index+"]");
					
				
				} catch (Exception e) {
					e.printStackTrace();
					assert false;
				}
			}
			
			
		}  else if (phase == 2){
			process_outgoing_links(t);
		} else if (phase == 3) {
			//process_cancel_regions(t);
		} else {}
	}

	@Override
	public void visit(YMultipleCompositeTask t) {
		visited.add(t);	
		if(phase == 1) {			
			FSPTask c = new FSPTask(t.name());
			c.container(_current_net);
			_current_net.components().put(c.name(), c);
			_components.put(t,c);			
			assert t.name() == c.name();			
			process_gates(t, c);
/*
			System.out.println("Multiple Task PreLimit");
			System.out.println("t = "+t.name());
			System.out.println("mode = "+t.creationMode());
			System.out.println("low = "+t.lowerBound());
			System.out.println("high = "+t.upperBound());
			System.out.println("trsh = "+t.threshold());
*/			
			limit_multiple_tasks(t);
/*			
			System.out.println("Multiple Task PostLimit");
			System.out.println("t = "+t.name());
			System.out.println("mode = "+t.creationMode());
			System.out.println("low = "+t.lowerBound());
			System.out.println("high = "+t.upperBound());
			System.out.println("trsh = "+t.threshold());
*/			
			/* Assembling net that simulates the 
			 * behavior of the multiple instances
			 */
			if(t.creationMode().equals(CreationMode.STATIC)) {
				try {
					FSPNet deco = new FSPNet(t.name().toUpperCase()+"_DECOMP");
					_s.add_net(deco);
					c.decomposesTo(deco);
								
					FSP_YNET y = new FSP_YNET("Ynet");
					deco.add_component("ynet", y);
					
					// Input condition
					FSPCondition input = new FSPCondition("input_condition", 1, 1);
					deco.add_component("input_condition", input);
					deco.link("input_condition", "in[1]", "ynet", "i_cond");
					
					// Output condition
					FSPCondition output = new FSPCondition("output_condition", 1, 1);
					deco.add_component("output_condition", output);
					deco.link("output_condition", "out[1]", "ynet", "o_cond");
				
					// Spliter
					FSPTask spliter = new FSPTask("spliter");
					deco.add_component("spliter", spliter);
					deco.link("spliter", "start", "input_condition", "out[1]");
										
					FSP_AndSplit spliter_split = new FSP_AndSplit("spliter_split", t.upperBound());
					deco.add_component("spliter_split", spliter_split);
					deco.link("spliter_split", "in", "spliter", "end");
					deco.link("spliter_split", "o_cond", "ynet", "o_cond");
					
					// Merger
					FSPTask merger = new FSPTask("merger");
					deco.add_component("merger", merger);
					deco.link("merger", "end", "output_condition", "in[1]");
					
					FSP_AndJoin merger_join = new FSP_AndJoin("merger_join", t.upperBound());
					deco.add_component("merger_join", merger_join);
					deco.link("merger_join", "out", "merger", "start");
					deco.link("merger_join", "o_cond", "ynet", "o_cond");
					
					for(int index = 1; index<=t.upperBound(); index++) {
						FSPTask multi = new FSPTask("multi_"+index);
						deco.add_component("multi"+index, multi);
						deco.link("multi"+index, "start", "spliter_split", "out["+index+"]");
						deco.link("multi"+index, "end", "merger_join", "in["+index+"]");
					}					
				
				} catch (Exception e) {
					e.printStackTrace();
					assert false;
				}
			}
			if(t.creationMode().equals(CreationMode.DYNAMIC)) {
				try {
					
					FSPNet deco = new FSPNet(t.name().toUpperCase()+"_DECOMP");
					_s.add_net(deco);
					c.decomposesTo(deco);
								
					FSP_YNET y = new FSP_YNET("Ynet");
					deco.add_component("ynet", y);
					
					// Input condition
					FSPCondition input = new FSPCondition("input_condition", 1, 1);
					deco.add_component("input_condition", input);
					deco.link("input_condition", "in[1]", "ynet", "i_cond");
					
					// Output condition
					FSPCondition output = new FSPCondition("output_condition", 1, 1);
					deco.add_component("output_condition", output);
					deco.link("output_condition", "out[1]", "ynet", "o_cond");
				
					// SpliterI
					FSPTask spliter1 = new FSPTask("spliter1");
					deco.add_component("spliter1", spliter1);
					deco.link("spliter1", "start", "input_condition", "out[1]");
										
					FSP_AndSplit spliter1_split = new FSP_AndSplit("spliter1_split", t.lowerBound()+1);
					deco.add_component("spliter1_split", spliter1_split);
					deco.link("spliter1_split", "in", "spliter1", "end");
					deco.link("spliter1_split", "o_cond", "ynet", "o_cond");
					
					// SpliterII
					FSPTask spliter2 = new FSPTask("spliter2");
					deco.add_component("spliter2", spliter2);
					deco.link("spliter2", "start", "spliter1_split", "out["+(t.lowerBound()+1)+"]");
										
					FSP_OrSplit spliter2_split = new FSP_OrSplit("spliter2_split", t.upperBound()-t.lowerBound()-1);
					deco.add_component("spliter2_split", spliter2_split);
					deco.link("spliter2_split", "in", "spliter2", "end");
					deco.link("spliter2_split", "o_cond", "ynet", "o_cond");
					
					// Merger
					FSPTask merger = new FSPTask("merger");
					deco.add_component("merger", merger);
					deco.link("merger", "end", "output_condition", "in[1]");
					
					FSP_TresholdOrJoin merger_join = new FSP_TresholdOrJoin("merger_join",t.upperBound(), t.threshold());
					deco.add_component("merger_join", merger_join);
					deco.link("merger_join", "out", "merger", "start");
					deco.link("merger_join", "o_cond", "ynet", "o_cond");
					
					int index;
					for(index = 1; index<=t.lowerBound(); index++) {
						FSPTask multi = new FSPTask("multi_"+index);
						deco.add_component("multi"+index, multi);
						deco.link("multi"+index, "start", "spliter1_split", "out["+index+"]");
						deco.link("multi"+index, "end", "merger_join", "in["+index+"]");
					}	
					int z = 1;
					for(; index<=t.upperBound()-1; index++) {
						FSPTask multi = new FSPTask("multi_"+index);
						deco.add_component("multi"+index, multi);
						deco.link("multi"+index, "start", "spliter2_split", "out["+z+"]");
						deco.link("multi"+index, "end", "merger_join", "in["+index+"]");
						z++;
					}
					FSPTask multi = new FSPTask("multi_"+t.upperBound());
					deco.add_component("multi"+t.upperBound(), multi);
					deco.link("multi"+t.upperBound(), "start", "spliter2_split", "default_out");
					deco.link("multi"+t.upperBound(), "end", "merger_join", "in["+index+"]");
					
				
				} catch (Exception e) {
					e.printStackTrace();
					assert false;
				}
			}
			
			
		}  else if (phase == 2){
			FSPNet deco_net = null;
			for(FSPNet n_ : _s.nets()) {
				if(n_.name().compareTo(t.name().toUpperCase()+"_DECOMP")==0) {
					deco_net = n_;
				}			
			}
			assert deco_net != null;
			for(FSPAtom _t : deco_net.components().values()) {
				if(_t instanceof FSPTask) {
					if(_t.name().startsWith("multi")) {
						((FSPAbstractTask)_t).decomposesTo(_nets.get(t.decomposesTo()));
					}
				}
			}
			
			
			
			process_outgoing_links(t);
		} else if (phase == 3) {
			//process_cancel_regions(t);
		} else {}
	}
	

	@Override
	public boolean visited(YNetElement e) {
		return visited.contains(e);
	}
	
	
	private void process_cancel_regions(YNetElement e) {
		if(e.canceledBy() != null) {
			assert _components.get(e) != null;
			
			FSPBox canceled = _components.get(e);
			FSPBox canceler = _components.get(e.canceledBy());
			try {
				canceler.container().link(canceled, "cancel", canceler, "end");
				canceled.container().link(canceled.name(), "o_cond", "ynet", "o_cond");
			} catch (Exception e1) {
				e1.printStackTrace();
				assert false;
			}
			
		}		
	}
	
	
	private void process_outgoing_links(YNetElement e) {
		FSPBox from =  get_sending_component(e);
		
		if(from instanceof FSP_XorSplit || 
				from instanceof FSP_OrSplit) {
			
			YBasicTask ee = (YBasicTask) e;
			FSPBox to = get_receiving_component(e, ee.defaultFlow());
			String to_port = to.first_input_available();
			try {
				
				from.container().link(from.name(),"default_out",to.name(),to_port);
			} catch (Exception e1) {				
				e1.printStackTrace();
				assert false;
			}
			
			
		}
		
		
		for(YNetElement _e : e.outwardFlows()) {
			if(e instanceof YBasicTask) {
				if(_e == ((YBasicTask) e).defaultFlow()) {
					continue;
				}
			}
					
			FSPBox to = get_receiving_component(e, _e);
			assert to != null;
			
			String from_port = from.first_output_available();
			String to_port = to.first_input_available();
			
			try {
				from.container().link(from.name(),from_port,to.name(),to_port);
				
			} catch (Exception e1) {
				from.dumpMappings();
				to.dumpMappings();
				
				System.out.println();
				System.out.println(to);
				System.out.println();
				
				System.out.println(from.name());
				System.out.println(from_port);
				System.out.println(to.name());
				System.out.println(to_port);
				
				e1.printStackTrace();
				assert false;
			}
			
			
		}
	}
	
	
	
	private FSPBox get_receiving_component(YNetElement e, YNetElement _e) {
		FSPBox to = null;
		// _e is a succesor of e
		assert e.outwardFlows().contains(_e);
		// both e and _e have already been mapped to fsp comps.
		assert _components.get(e) != null;
		assert _components.get(_e) != null;
		
		if(_components.get(_e) instanceof FSPAbstractTask) {
			if(((FSPAbstractTask) _components.get(_e)).join() != null) {
				to = ((FSPAbstractTask) _components.get(_e)).join();
			} else {
				to = _components.get(_e);
			}
		} else {
			to = _components.get(_e);
		}

		
		return to;
	}
	
	
	
	private FSPBox get_sending_component(YNetElement e) {
		FSPBox to = null;
		
		if(_components.get(e) instanceof FSPAbstractTask) {
			if(((FSPAbstractTask) _components.get(e)).split() != null) {
				to = ((FSPAbstractTask) _components.get(e)).split();
			} else {
				to = _components.get(e);
			}
		} else {
			to = _components.get(e);
		}

		return to;
	} 
	
	
	
	private void process_gates(YBasicTask t, FSPAbstractTask c) {
		if(t.join() != GateType.NONE) {
			FSPJoin join = null;
			switch(t.join()) {
			case AND:
				join = new FSP_AndJoin(t.name()+"_join", t.inwardFlows().size());
				break;
			case XOR:
				join = new FSP_XorJoin(t.name()+"_join", t.inwardFlows().size());
				break;
			case OR:
				join = new FSP_OrJoin(t.name()+"_join", t.inwardFlows().size());
				break;
			default:
			}
			assert join != null;
			_current_net.components().put(t.name()+"_join", join);	
			join.container(_current_net);
			c.join(join);
			try {
				_current_net.link(c.name(),"start",join.name(),"out");
				_current_net.link(join.name(),"o_cond","ynet","o_cond");
				//_current_net.link_to_out(join.name(), "o_cond");
			} catch (Exception e) {
				e.printStackTrace();
				assert false;
			}
		}
		
		if(t.split() != GateType.NONE) {
			FSPSplit split = null;
			switch(t.split()) {
			case AND:
				split = new FSP_AndSplit(t.name()+"_split", t.outwardFlows().size());
				break;
			case XOR:
				split = new FSP_XorSplit(t.name()+"_split", t.outwardFlows().size()-1);
				break;
			case OR:
				split = new FSP_OrSplit(t.name()+"_split", t.outwardFlows().size()-1);
				break;
			default:
			}
			assert split != null;
			_current_net.components().put(t.name()+"_split", split);
			split.container(_current_net);
			c.split(split);
			try {
				_current_net.link(c.name(),"end",split.name(),"in");
				_current_net.link(split.name(),"o_cond","ynet","o_cond");
				//_current_net.link_to_out(split.name(), "o_cond");
			} catch (Exception e) {
				e.printStackTrace();
				assert false;
			}			
		}		
		
	}
	
	
	private void process_gates_for_multi_tasks(YBasicTask t, FSPAbstractTask spl, FSPAbstractTask mrg) {
		if(t.join() != GateType.NONE) {
			FSPJoin join = null;
			switch(t.join()) {
			case AND:
				join = new FSP_AndJoin(t.name()+"_join", t.inwardFlows().size());
				break;
			case XOR:
				join = new FSP_XorJoin(t.name()+"_join", t.inwardFlows().size());
				break;
			case OR:
				join = new FSP_OrJoin(t.name()+"_join", t.inwardFlows().size());
				break;
			default:
			}
			assert join != null;
			_current_net.components().put(t.name()+"_join", join);	
			join.container(_current_net);
			spl.join(join);
			try {
				_current_net.link(spl.name(),"start",join.name(),"out");
				_current_net.link(join.name(),"o_cond","ynet","o_cond");
				//_current_net.link_to_out(join.name(), "o_cond");
			} catch (Exception e) {
				e.printStackTrace();
				assert false;
			}
		}
		
		if(t.split() != GateType.NONE) {
			FSPSplit split = null;
			switch(t.split()) {
			case AND:
				split = new FSP_AndSplit(t.name()+"_split", t.outwardFlows().size());
				break;
			case XOR:
				split = new FSP_XorSplit(t.name()+"_split", t.outwardFlows().size()-1);
				break;
			case OR:
				split = new FSP_OrSplit(t.name()+"_split", t.outwardFlows().size()-1);
				break;
			default:
			}
			assert split != null;
			_current_net.components().put(t.name()+"_split", split);
			split.container(_current_net);
			mrg.split(split);
			try {
				_current_net.link(mrg.name(),"end",split.name(),"in");
				_current_net.link(split.name(),"o_cond","ynet","o_cond");
				//_current_net.link_to_out(split.name(), "o_cond");
			} catch (Exception e) {
				e.printStackTrace();
				assert false;
			}			
		}		
		
	}

	
}
