package yawl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.jar.Attributes;

import com.sun.mirror.type.PrimitiveType.Kind;

import yawl.model.*;
import yawl.parser.TranslationInfo;


public class YSpecificationBuilderImpl implements YSpecificationBuilder {

	@SuppressWarnings("unused")
	private final static boolean debug = true;
	
	private final static boolean transformNames = true;
	
	private enum ElemTypes {
		_ANY, ICOND, OCOND, COND, TASK, CTASK,
		MTASK, MCTASK;
		
		private boolean isTask() {
			return (this.equals(TASK) ||
					this.equals(CTASK) ||
					this.equals(MTASK) ||
					this.equals(MCTASK)
					);
		}
		
		private boolean isCond() {
			return (this.equals(COND) ||
					this.equals(ICOND) ||
					this.equals(OCOND)
					);
		}
		
		private boolean promotes(ElemTypes o) {
			if(this.equals(_ANY)) return true;
			if(this.equals(COND)) 
			{
				if(o.equals(ICOND)) return true;
				if(o.equals(OCOND)) return true;
			}
			if(this.equals(TASK)) 
			{
				if(o.equals(CTASK)) return true;
				if(o.equals(MTASK)) return true;
			}			
			if(this.equals(CTASK)) 
			{
				if(o.equals(MCTASK)) return true;
			}
			if(this.equals(MTASK)) 
			{
				if(o.equals(MCTASK)) return true;
			}			
			return false;
		}
		
		private ElemTypes makeComposite() {
			if(this.isTask()) {
				if(this.equals(TASK))
					return ElemTypes.CTASK;
				if(this.equals(MTASK))
					return ElemTypes.MCTASK;
				return this;
			}
			return null;
		}
		
		private ElemTypes makeMultiple() {
			if(this.isTask()) {
				if(this.equals(TASK))
					return ElemTypes.MTASK;
				if(this.equals(CTASK))
					return ElemTypes.MCTASK;
				return this;
			}
			return null;
		}
	}
	
	
	private String spec;
	
	private String rootnet;
	
	private LinkedList<String> nets;
	
	// net -> cond
	private Map<String,String> iconds;

	private Map<String,String> oconds;
	
	private Set<String> elems;

	// elem -> net
	private Map<String,String> containerNet;
	
	// elem -> net
	private Map<String,String> decomposesTo;

	private Map<String,ElemTypes> kinds;
	
	private Map<String,GateType> joins;
	
	private Map<String,GateType> splits;
	
	private Map<String,LinkedList<String>> flows;
	
	private Map<String,LinkedList<String>> flowsTr;
	
	private Map<String,String> defaultFlows;
	
	private Map<String,LinkedList<String>> cancelSets;
	
	private Map<String,String> canceledBy;
	
	private Map<String,Integer> lowerBounds;
	
	private Map<String,Integer> upperBounds;
	
	private Map<String,Integer> thresholds;
	
	private Map<String,String> multi_modes;
	
	private TranslationInfo t_info;
	
		
	public YSpecificationBuilderImpl() {
		super();
		this.spec = null;
		this.rootnet = null;
		this.nets = new LinkedList<String>();
		this.elems = new HashSet<String>();
		this.containerNet = new HashMap<String, String>();
		this.kinds = new HashMap<String, ElemTypes>();
		this.flows = new HashMap<String, LinkedList<String>>();
		this.flowsTr = new HashMap<String, LinkedList<String>>();
		this.iconds = new HashMap<String, String>();
		this.oconds = new HashMap<String, String>();
		this.joins = new HashMap<String, GateType>();
		this.splits = new HashMap<String, GateType>();
		this.decomposesTo = new HashMap<String, String>();
		this.cancelSets = new HashMap<String, LinkedList<String>>();
		this.canceledBy = new HashMap<String, String>();
		this.lowerBounds = new HashMap<String, Integer>();
		this.upperBounds = new HashMap<String, Integer>();
		this.defaultFlows = new HashMap<String, String>();
		this.thresholds = new HashMap<String, Integer>();
		this.multi_modes = new HashMap<String, String>();
	}

	@Override
	public void createSpecification(String id) throws YSpecBuildException {
		if(spec == null) {
			this.spec = id;
		} else if (spec.compareTo(id) != 0) {
			throw new YSpecBuildException("Double distinct specifications.");
		}
	}
		
	@Override
	public void createNet(String id, String specId) throws YSpecBuildException {
		// Checking for errors
		if(spec == null)
			throw new YSpecBuildException("Spec is null.");
		if(spec.compareTo(specId) != 0)
			throw new YSpecBuildException("The specified specId does not " +
					"correspond to an existing specification.");
		if(nets.contains(id))
			throw new YSpecBuildException("The specified net id already " +
					"exists.");
		
		// Creation of the net
		this.nets.add(id);
	}		
	
	@Override
	public void makeNetRoot(String netId) throws YSpecBuildException {
		if(netId == null)
			throw new YSpecBuildException("netId must not be null.");
		if(!this.nets.contains(netId))
			throw new YSpecBuildException("The supplied net id does not" +
					"correspond to an existing net");
		
		this.rootnet = netId;			
	}
	
	@Override
	public void createTask(String id, String netId) throws YSpecBuildException {
		if(id == null)
			throw new YSpecBuildException("id must not be null.");
		if(netId == null)
			throw new YSpecBuildException("netId must not be null.");		
		if(!nets.contains(netId))
			throw new YSpecBuildException("The supplied net id does" +
					"not exists");
		
		if(kinds.get(id) != null) {
			if(!kinds.get(id).promotes(ElemTypes.TASK)) {
				throw new YSpecBuildException("The id allready belongs " +
						"to an element that cannot be promoted to a task");
			}				
		}
		
		this.elems.add(id);
		this.containerNet.put(id, netId);
		this.kinds.put(id, ElemTypes.TASK);		
	}
	
	@Override
	public void createCondition(String condId, String netId) throws YSpecBuildException {
		if(condId == null)
			throw new YSpecBuildException("condId must not be null.");
		if(netId == null)
			throw new YSpecBuildException("netId must not be null.");		
		if(!nets.contains(netId))
			throw new YSpecBuildException("The supplied net id does" +
					"not exists");
		
		if(kinds.get(condId) != null) {
			if(!kinds.get(condId).promotes(ElemTypes.COND)) {
				throw new YSpecBuildException("The id allready belongs " +
						"to an element that cannot be promoted to a condition");
			}				
		}
		
		this.elems.add(condId);
		this.containerNet.put(condId, netId);
		this.kinds.put(condId, ElemTypes.COND);					
	}	
	
	@Override
	public void makeInput(String condId) throws YSpecBuildException {
		if(condId == null)
			throw new YSpecBuildException("condId must not be null.");
		
		if(!elems.contains(condId))
			throw new YSpecBuildException("condId does not correspond" +
					" to an existing element");
		
		if(kinds.get(condId) != null) {
			if(!kinds.get(condId).promotes(ElemTypes.ICOND)) {
				throw new YSpecBuildException("The id allready belongs " +
						"to an element that cannot be promoted to a condition");
			}				
		}
		
		assert containerNet.get(condId) != null
			:"Consistency check failed: the condition has been created" +
					" but its container net was not set. ";
		
		iconds.put(containerNet.get(condId), condId);		
		kinds.put(condId, ElemTypes.ICOND);
	}
	
	@Override
	public void makeOutput(String condId) throws YSpecBuildException {
		if(condId == null)
			throw new YSpecBuildException("condId must not be null.");
		
		if(!elems.contains(condId))
			throw new YSpecBuildException("condId does not correspond" +
					" to an existing element");
		
		if(kinds.get(condId) != null) {
			if(!kinds.get(condId).promotes(ElemTypes.OCOND)) {
				throw new YSpecBuildException("The id allready belongs " +
						"to an element that cannot be promoted to a condition");
			}				
		}
		
		assert containerNet.get(condId) != null
			:"Consistency check failed: the condition has been created" +
					" but its container net was not set. ";
		
		oconds.put(containerNet.get(condId), condId);		
		kinds.put(condId, ElemTypes.OCOND);
	}
	
	@Override
	public void setInputGate(String taskId, GateType t) throws YSpecBuildException {
		if(taskId == null)
			throw new YSpecBuildException("taskId must not be null.");
		
		if(!elems.contains(taskId))
			throw new YSpecBuildException("taskId does not correspond " +
				"to an existing task.");
			
		if(kinds.get(taskId) != null) {
			if(!kinds.get(taskId).isTask()) {
				throw new YSpecBuildException("The id allready belongs " +
						"to an element that is not a task");
			}				
		}
		
		joins.put(taskId, t);
	}

	@Override
	public void setOutputGate(String taskId, GateType t) throws YSpecBuildException {
		if(taskId == null)
			throw new YSpecBuildException("taskId must not be null.");
		
		if(!elems.contains(taskId))
			throw new YSpecBuildException("taskId does not correspond " +
				"to an existing task.");
			
		if(kinds.get(taskId) != null) {
			if(!kinds.get(taskId).isTask()) {
				throw new YSpecBuildException("The id allready belongs " +
						"to an element that is not a task");
			}				
		}
		
		splits.put(taskId, t);		
	}
	
	@Override
	public void makeComposite(String taskId, String decompNetId) throws YSpecBuildException {
		if(taskId == null)
			throw new YSpecBuildException("taskId must not be null.");
		if(decompNetId == null)
			throw new YSpecBuildException("decompNetId must not be null.");
		
		//if(!nets.contains(decompNetId))
		//	throw new YSpecBuildException("The specified net does not exists.");
		
		if(kinds.get(taskId) != null) {
			if(!kinds.get(taskId).isTask()) {
				throw new YSpecBuildException("The id allready belongs " +
						"to an element that is not a task");
			}				
		}
		kinds.put(taskId, kinds.get(taskId).makeComposite());
		decomposesTo.put(taskId, decompNetId);		
	}
	
	@Override
	public void addCancelSet(String taskId, LinkedList<String> cancelSetIds) throws YSpecBuildException {
		if(taskId == null)
			throw new YSpecBuildException("taskId must not be null.");
		if(cancelSetIds == null)
			throw new YSpecBuildException("cancelSetIds must not be null.");
		
		if(kinds.get(taskId) != null) {
			if(!kinds.get(taskId).isTask()) {
				throw new YSpecBuildException("The id allready belongs " +
						"to an element that is not a task");
			}				
		}
		
		assert containerNet.get(taskId) != null;
		for(String s : cancelSetIds) {
			if(!elems.contains(s)) {
				this.elems.add(s);
				this.containerNet.put(s, containerNet.get(taskId));
				this.kinds.put(s, ElemTypes._ANY);
			}
			
			canceledBy.put(s, taskId);
		}
		cancelSets.put(taskId, cancelSetIds);
	}

	@Override
	public void addFlow(String idFrom, String idTo) throws YSpecBuildException {
		if(idFrom == null)
			throw new YSpecBuildException("idFrom must not be null.");
		if(idTo == null)
			throw new YSpecBuildException("idTo must not be null.");
		
		
		if(flows.get(idFrom) == null)
			flows.put(idFrom, new LinkedList<String>());
		if(flowsTr.get(idTo) == null)
			flowsTr.put(idTo, new LinkedList<String>());
		
		
		LinkedList<String> f = flows.get(idFrom);
		LinkedList<String> ft = flowsTr.get(idTo);
		f.add(idTo);
		ft.add(idFrom);
		
		flows.put(idFrom,f);
		flowsTr.put(idTo,ft);
	}

	@Override
	public void makeMultiple(String taskId, int low, int high, int thresh, String mode) throws YSpecBuildException {
		if(taskId == null)
			throw new YSpecBuildException("taskId must not be null.");
				
		if(kinds.get(taskId) != null) {
			if(!kinds.get(taskId).isTask()) {
				throw new YSpecBuildException("The id allready belongs " +
						"to an element that is not a task");
			}				
		}
		kinds.put(taskId, kinds.get(taskId).makeMultiple());
		lowerBounds.put(taskId, new Integer(low));
		upperBounds.put(taskId, new Integer(high));
		thresholds.put(taskId, new Integer(thresh));
		multi_modes.put(taskId, mode);
		
	}

	@Override
	public void setDefaultFlow(String taskId, String otherId) throws YSpecBuildException {
		if(taskId == null)
			throw new YSpecBuildException("taskId must not be null.");
		
		if(otherId == null)
			throw new YSpecBuildException("otherId must not be null.");
		
		/*if(kinds.get(taskId) != null) {
			if(!kinds.get(taskId).isTask()) {
				throw new YSpecBuildException("The id allready belongs " +
						"to an element that is not a task");
			}				
		}
		if(kinds.get(otherId) != null) {
			if(!kinds.get(otherId).isTask()) {
				throw new YSpecBuildException("The id allready belongs " +
						"to an element that is not a task");
			}				
		}*/
		defaultFlows.put(taskId, otherId);		
	}
	
	@Override
	public String dumpPlainInfo() {
		return "YSpecificationBuilderImpl [" +
				"\n\t" + "spec = " + spec +
				"\n\t" + "root = " + rootnet +
				"\n\t" + "nets = " + nets +
				"\n\t" + "containerNet = " + containerNet +
				"\n\t" + "elems = " + elems +
				"\n\t" + "kinds = " + kinds +
				"\n\t" + "iconds = " + iconds +
				"\n\t" + "oconds = " + oconds +
				"\n\t" + "flows = " + flows +
				"\n\t" + "flowsTr = " + flowsTr +
				"\n\t" + "defFlows = " + defaultFlows +
				"\n\t" + "joins = " + joins +
				"\n\t" + "splits = " + splits +
				"\n\t" + "decomposesTo = " + decomposesTo +
				"\n\t" + "cancelSets = " + cancelSets +
				"\n\t" + "canceledBy = " + canceledBy +
				"\n\t" + "lowerBounds = " + lowerBounds +
				"\n\t" + "upperBounds = " + upperBounds +
				"\n\t" + "threshold = " + thresholds +
				"\n\t" + "multi_modes = " + multi_modes +
				"\n]";
	}


	@Override
	public YSpecification buildSpecification() {
		Map<String, YSpecification> _spec = new HashMap<String, YSpecification>();
		Map<String, YNet> _nets = new HashMap<String, YNet>();
		Map<String, YNetElement> _elems = new HashMap<String, YNetElement>();
		
		
		pre_build_consistency_checks();
		//System.out.println(dumpPlainInfo());
		//change_names();
		change_names_to_lower_case();
		normalize_gates();
		pre_build_consistency_checks();
		

		//System.out.println(dumpPlainInfo());
		
		
		/*
		 *	Creating Specification and Nets
		*/
		_spec.put(spec, new YSpecification(spec));
		for(String n : nets) {
			_nets.put(n,(new YNet(n, _spec.get(spec))));
		}
		
		/*
		 *	Creating Elements
		*/
		for(String e : elems) {
			YNetElement _e = null;
			YNet container = _nets.get(containerNet.get(e));
			ElemTypes k = kinds.get(e); 
			if(k.isCond()) {
				_e = new YCondition(e, container);
				if(k.equals(ElemTypes.ICOND))
					container.start((YCondition)_e);
				if(k.equals(ElemTypes.OCOND))
					container.end((YCondition)_e);			
			} else if(k.isTask()) {
				if(k.equals(ElemTypes.TASK)) {
					_e = new YTask(e,container);
				} else if (k.equals(ElemTypes.CTASK)) {
					YCompositeTask ce = new YCompositeTask(e,container);
					YNet y = _nets.get(decomposesTo.get(e));
					ce.decomposesTo(y);
					_e = ce;
				} else if (k.equals(ElemTypes.MTASK)) {
					YMultipleTask me = new YMultipleTask(e,container);
					int l = lowerBounds.get(e);
					int h = upperBounds.get(e);
					me.lowerBound(l);
					me.upperBound(h);
					if(thresholds.get(e) != null)
						me.threshold(thresholds.get(e).intValue());
					if(multi_modes.get(e).compareTo("static")==0) {
						me.creationMode(CreationMode.STATIC);
					} else if(multi_modes.get(e).compareTo("dynamic")==0) {
						me.creationMode(CreationMode.DYNAMIC);
					} else {
						assert false:"Unrecognized creation mode: " + multi_modes.get(e)
						+ " for task: " + e;				
					}
					_e = me;
				} else if (k.equals(ElemTypes.MCTASK)) {
					YMultipleCompositeTask mce = new YMultipleCompositeTask(e,container);
					YNet y = _nets.get(decomposesTo.get(e));
					mce.decomposesTo(y);
					int l = lowerBounds.get(e);
					int h = upperBounds.get(e);
					mce.lowerBound(l);
					mce.upperBound(h);
					if(thresholds.get(e) != null)
						mce.threshold(thresholds.get(e).intValue());
					if(multi_modes.get(e).compareTo("static")==0) {
						mce.creationMode(CreationMode.STATIC);
					} else if(multi_modes.get(e).compareTo("dynamic")==0) {
						mce.creationMode(CreationMode.DYNAMIC);
					} else {
						assert false:"Unrecognized creation mode: " + multi_modes.get(e)
						+ " for task: " + e;				
					}_e = mce;
				} else {
					throw new Error("Invalid element type.");
				}
				((YBasicTask) _e).join(joins.get(e));
				((YBasicTask) _e).split(splits.get(e));
			}
			_elems.put(e, _e);			
		}
		
		/*
		 *	Adding flows, cancel sets and inverted flows.
		*/
		for(String e : _elems.keySet()) {
			if(flows.get(e) != null)
				for(String to : flows.get(e)) {
					_elems.get(e).outwardFlows().add(_elems.get(to));
				}
			if(flowsTr.get(e) != null)
				for(String to : flowsTr.get(e)) {
					_elems.get(e).inwardFlows().add(_elems.get(to));
				}
			if(cancelSets.get(e) != null) {
				for(String x : cancelSets.get(e)) {
					((YBasicTask)_elems.get(e)).cancelSet().add(_elems.get(x));
				}
			}
			if(defaultFlows.get(e) != null) {
				((YBasicTask)_elems.get(e)).defaultFlow(_elems.get(defaultFlows.get(e)));
			}
			if(canceledBy.get(e) != null) {				
				YBasicTask ct = (YBasicTask) _elems.get(canceledBy.get(e));
				
				((YNetElement)_elems.get(e)).canceledBy(ct);
			}
		}		
		_spec.get(spec).root(_nets.get(rootnet));
		post_build_checks(_spec.get(spec));
		return _spec.get(spec);
	}

	
	
	private void pre_build_consistency_checks()
	{
		assert nets.contains(rootnet);
		
		assert nets.containsAll(iconds.keySet());
		assert nets.containsAll(oconds.keySet());
		assert nets.containsAll(decomposesTo.values());
		assert nets.containsAll(containerNet.values());
		
		assert kinds.keySet().containsAll(elems);
		assert elems.containsAll(kinds.keySet());

		assert elems.containsAll(iconds.values());
		assert elems.containsAll(oconds.values());
		assert elems.containsAll(containerNet.keySet());
		assert elems.containsAll(decomposesTo.keySet());
		assert elems.containsAll(joins.keySet());
		assert elems.containsAll(splits.keySet());
		assert elems.containsAll(flows.keySet());
		assert elems.containsAll(flowsTr.keySet());
		assert elems.containsAll(canceledBy.keySet());
		assert elems.containsAll(cancelSets.keySet());
		assert elems.containsAll(lowerBounds.keySet());
		assert elems.containsAll(upperBounds.keySet());
		assert elems.containsAll(defaultFlows.keySet());
		
		assert elems.containsAll(canceledBy.values());
		assert elems.containsAll(defaultFlows.values());
		
		
		for(String s : iconds.values()) {
			assert kinds.get(s).equals(ElemTypes.ICOND);
		}
		for(String s : oconds.values()) {
			assert kinds.get(s).equals(ElemTypes.OCOND);
		}
		
		for(String e : elems) {
			
			assert !kinds.get(e).equals(ElemTypes._ANY);
			
			if(kinds.get(e).equals(ElemTypes.ICOND)) {
				assert iconds.values().contains(e);
			}
			if(kinds.get(e).equals(ElemTypes.OCOND)) {
				assert oconds.values().contains(e);
			}
			if(kinds.get(e).equals(ElemTypes.COND)) {
				assert !iconds.values().contains(e);
				assert !oconds.values().contains(e);
			}
			if(kinds.get(e).equals(ElemTypes.TASK)) {
				assert !decomposesTo.keySet().contains(e);
				assert !lowerBounds.keySet().contains(e);
				assert !upperBounds.keySet().contains(e);
			}
			if(kinds.get(e).equals(ElemTypes.CTASK)) {
				assert decomposesTo.keySet().contains(e);
				assert !lowerBounds.keySet().contains(e);
				assert !upperBounds.keySet().contains(e);
			}
			if(kinds.get(e).equals(ElemTypes.MTASK)) {
				assert !decomposesTo.keySet().contains(e);
				assert lowerBounds.keySet().contains(e);
				assert upperBounds.keySet().contains(e);
			}
			if(kinds.get(e).equals(ElemTypes.MCTASK)) {
				assert decomposesTo.keySet().contains(e);
				assert lowerBounds.keySet().contains(e);
				assert upperBounds.keySet().contains(e);
			}			
		}
			
	}
	
	private void post_build_checks(YSpecification s) {
		for(YNet n : s.nets()) {
			for(YBasicTask t : n.tasks()) {
				if(t.split() != GateType.NONE) {
					assert t.outwardFlows().size() > 1;
					if(t.split() == GateType.OR) {
						assert t.defaultFlow() != null;
						assert t.outwardFlows().contains(t.defaultFlow());
					}
				}
			}
		}		
	}
	
	
	
	private void something() {
		
		for(String n : nets) {
			System.out.println("net = " + n);
			
			for(String e : pre_img(n, containerNet)) {
				System.out.println(" \telement = " + e + " type = " + kinds.get(e));
				System.out.println("\tjoin = " + joins.get(e));
				System.out.println("\tsplit = " + splits.get(e));
				System.out.println("\tflows to:");
				if(flows.get(e) != null) {
					for(String i : flows.get(e)) {
						System.out.println("\t\t"+i);
					}
				}
				System.out.println("\tflows from:");
				if(flowsTr.get(e) != null) {
					for(String i : flowsTr.get(e)) {
						System.out.println("\t\t"+i);
					}			
				}
				System.out.println("");
				System.out.println("");
				
			}
		}	
	}
	
	private void normalize_gates() {
		for(String e : elems) {
			if(flows.get(e) != null)
			if(flows.get(e).size() <= 1) {
				splits.put(e, GateType.NONE);
			}
			if(flowsTr.get(e) != null)
			if(flowsTr.get(e).size() <= 1) {
				joins.put(e, GateType.NONE);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void change_names() {
		this.t_info = new TranslationInfo();
		
		String _spec = spec.toUpperCase().replace(' ', '_');
		t_info.getSpecsMap().put(_spec, spec);
		this.spec = _spec;
		
		LinkedList<String> _nets = new LinkedList<String>();
		for(String n : nets) {
			String _n = n.toUpperCase().replace(' ', '_');
			t_info.getNetsMap().put(_n, n);
			_nets.add(_n);
		}
		nets = _nets;		
		rootnet = pre_img(rootnet,t_info.getNetsMap()).getFirst();
		/*
		Map<String,String> _containerNet = new HashMap<String, String>();
		for(String k : containerNet.keySet()) {
			String newnet = pre_img(containerNet.get(k),t_info.getNetsMap()).getFirst();
			_containerNet.put(k, newnet);
		}
		containerNet = _containerNet;
		*/
		HashSet<String> _elems = new HashSet<String>();
		int ec = 0;
		int cc = 0;
		for(String e : elems) {
			String _e;
			if(kinds.get(e).isTask()) {
				_e = "t" + ec++;
			} else if(kinds.get(e).isCond()) {
				_e = "c" + cc++;
			} else {
				throw new Error("Fatal error! consistency checks" +
						" has been performed and yet there are " +
						"elements that are neither task nor cond.");
			}
			t_info.getElemsMap().put(_e, e);
			_elems.add(_e);	
		}
		elems = _elems;
		
		Map<String,String> e2e = t_info.getElemsMap();
		Map<String,String> n2n = t_info.getNetsMap();
		
		containerNet = compose(compose(e2e,containerNet),invert(n2n));
		decomposesTo = compose(compose(e2e,decomposesTo),invert(n2n));
		kinds = compose(e2e,kinds);
		iconds = compose(compose(n2n,iconds),invert(e2e));
		oconds = compose(compose(n2n,oconds),invert(e2e));
		joins = compose(e2e,joins);
		splits = compose(e2e,splits);
		canceledBy = compose(compose(e2e,canceledBy),invert(e2e));
		upperBounds = compose(e2e,upperBounds);
		lowerBounds = compose(e2e,lowerBounds);
		thresholds = compose(e2e,thresholds);
		multi_modes = compose(e2e,multi_modes);
		defaultFlows = compose(compose(e2e,defaultFlows),invert(e2e));
		flows = repalceInMultiMap(invert(e2e),flows);
		flowsTr = repalceInMultiMap(invert(e2e),flowsTr);
		cancelSets = repalceInMultiMap(invert(e2e),cancelSets);
	}
	
	@SuppressWarnings("unchecked")
	private void change_names_to_lower_case() {
		this.t_info = new TranslationInfo();
		
		String _spec = spec.toUpperCase().replace(' ', '_');
		t_info.getSpecsMap().put(_spec, spec);
		this.spec = _spec;
		
		LinkedList<String> _nets = new LinkedList<String>();
		for(String n : nets) {
			String _n = n.toUpperCase().replace(' ', '_');
			t_info.getNetsMap().put(_n, n);
			_nets.add(_n);
		}
		nets = _nets;		
		rootnet = pre_img(rootnet,t_info.getNetsMap()).getFirst();
		/*
		Map<String,String> _containerNet = new HashMap<String, String>();
		for(String k : containerNet.keySet()) {
			String newnet = pre_img(containerNet.get(k),t_info.getNetsMap()).getFirst();
			_containerNet.put(k, newnet);
		}
		containerNet = _containerNet;
		*/
		HashSet<String> _elems = new HashSet<String>();
		int ec = 0;
		int cc = 0;
		for(String e : elems) {
			String _e;
			_e = e.toLowerCase();
			t_info.getElemsMap().put(_e, e);
			_elems.add(_e);	
		}
		elems = _elems;
		
		Map<String,String> e2e = t_info.getElemsMap();
		Map<String,String> n2n = t_info.getNetsMap();
		
		containerNet = compose(compose(e2e,containerNet),invert(n2n));
		decomposesTo = compose(compose(e2e,decomposesTo),invert(n2n));
		kinds = compose(e2e,kinds);
		iconds = compose(compose(n2n,iconds),invert(e2e));
		oconds = compose(compose(n2n,oconds),invert(e2e));
		joins = compose(e2e,joins);
		splits = compose(e2e,splits);
		canceledBy = compose(compose(e2e,canceledBy),invert(e2e));
		upperBounds = compose(e2e,upperBounds);
		lowerBounds = compose(e2e,lowerBounds);
		thresholds = compose(e2e,thresholds);
		multi_modes = compose(e2e,multi_modes);
		defaultFlows = compose(compose(e2e,defaultFlows),invert(e2e));
		flows = repalceInMultiMap(invert(e2e),flows);
		flowsTr = repalceInMultiMap(invert(e2e),flowsTr);
		cancelSets = repalceInMultiMap(invert(e2e),cancelSets);
	}
	
	
	
	
	
	
	
	private Map<String,LinkedList<String>> repalceInMultiMap(
			Map<String,String> names,Map<String,LinkedList<String>> x
			) 
	{
		Map<String,LinkedList<String>> _new = new HashMap<String, LinkedList<String>>();
		
		for(String s : x.keySet()) {
			LinkedList<String> l = new LinkedList<String>();
			for(String z : x.get(s)) {
				l.add(names.get(z));
			}
			_new.put(names.get(s), l);
			
		}	
		return _new;
	}
	
	@SuppressWarnings("unchecked")
	private Map compose(Map m1, Map m2) {
		HashMap<Object,Object> n = new HashMap<Object, Object>();
		for(Object s : m1.keySet()) {
			Object i = m1.get(s);
			if(i!=null && m2.get(i) != null) {
				n.put(s, m2.get(i));
		
			}
		}
		return n;		
	}
	
	@SuppressWarnings("unchecked")
	private Map invert(Map m1) {
		HashMap<Object,Object> n = new HashMap<Object, Object>();
		for(Object s : m1.keySet()) {
			n.put(m1.get(s), s);
		}
		return n;
	} 
	
	private LinkedList<String> pre_img(String s, Map<String,String> m) {
		LinkedList<String> l = new LinkedList<String>();
		for(String k : m.keySet()) {
			if(m.get(k).compareTo(s)==0) {
				l.add(k);
			}
		}		
		return l;
	}
	
	@Override
	public TranslationInfo translation_info() {
		return this.t_info;
	}
	
}
