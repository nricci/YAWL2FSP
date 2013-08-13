package yawl.parser;

import java.util.LinkedList;

import org.xml.sax.Attributes;

import yawl.YSpecBuildException;
import yawl.model.GateType;

public class YTaskHandler extends YSAXHandler {

	public YTaskHandler() {
		super("task");
		
		/* JOIN code fetcher */
		this.installHandler(new YSAXHandler("join") {
			
			@Override
			protected void startElementSkip(String uri, String localName, String qName,
					Attributes attributes) {}
			
			@Override
			protected void startElementOwn(String uri, String localName, String qName,
					Attributes attributes)
			{
				values().putValue("join", attributes.getValue("code"));
			}			
			
			@Override
			protected void endElementSkip(String uri, String localName, String qName) {}
			
			@Override
			protected void endElementOwn(String uri, String localName, String qName) {}
			
			@Override
			protected void charactersSkip(char[] ch, int start, int length) {}
			
			@Override
			protected void charactersOwn(char[] ch, int start, int length) {}
		
		});
		/* SPLIT code fetcher */
		this.installHandler(new YSAXHandler("split") {
			
			@Override
			protected void startElementSkip(String uri, String localName, String qName,
					Attributes attributes) {}
			
			@Override
			protected void startElementOwn(String uri, String localName, String qName,
					Attributes attributes) 
			{
				values().putValue("split", attributes.getValue("code"));
			}
			
			@Override
			protected void endElementSkip(String uri, String localName, String qName) {}
			
			@Override
			protected void endElementOwn(String uri, String localName, String qName) {}
			
			@Override
			protected void charactersSkip(char[] ch, int start, int length) {}
			
			@Override
			protected void charactersOwn(char[] ch, int start, int length) {}
		
		});
		/* decomposesTo fetcher */
		this.installHandler(new YSAXHandler("decomposesTo") {
			
			@Override
			protected void startElementSkip(String uri, String localName, String qName,
					Attributes attributes) {}
			
			@Override
			protected void startElementOwn(String uri, String localName, String qName,
					Attributes attributes) 
			{
				values().putValue("decomposesTo", attributes.getValue("id"));
			}
			
			@Override
			protected void endElementSkip(String uri, String localName, String qName) {}
			
			@Override
			protected void endElementOwn(String uri, String localName, String qName) {}
			
			@Override
			protected void charactersSkip(char[] ch, int start, int length) {}
			
			@Override
			protected void charactersOwn(char[] ch, int start, int length) {}
		
		});
		/* Cancel Set fetcher */
		this.installHandler(new YSAXHandler("removesTokens") {
			
			@Override
			protected void startElementSkip(String uri, String localName, String qName,
					Attributes attributes) {}
			
			@Override
			protected void startElementOwn(String uri, String localName, String qName,
					Attributes attributes) 
			{
				LinkedList<String> c_set = (LinkedList<String>)	references().get("cancel_set");
				c_set.add(attributes.getValue("id"));
			}
			
			@Override
			protected void endElementSkip(String uri, String localName, String qName) {}
			
			@Override
			protected void endElementOwn(String uri, String localName, String qName) {}
			
			@Override
			protected void charactersSkip(char[] ch, int start, int length) {}
			
			@Override
			protected void charactersOwn(char[] ch, int start, int length) {}
		
		});
		/* maximum */
		this.installHandler(new YSAXHandler("minimum") {
			
			@Override
			protected void startElementSkip(String uri, String localName, String qName,
					Attributes attributes) {}
			
			@Override
			protected void startElementOwn(String uri, String localName, String qName,
					Attributes attributes) {}
			
			@Override
			protected void endElementSkip(String uri, String localName, String qName) {}
			
			@Override
			protected void endElementOwn(String uri, String localName, String qName) {}
			
			@Override
			protected void charactersSkip(char[] ch, int start, int length) {}
			
			@Override
			protected void charactersOwn(char[] ch, int start, int length) 
			{
				values().putValue("min", new String(ch,start,length));
			}
		
		});
		/* minimum */
		this.installHandler(new YSAXHandler("maximum") {
			
			@Override
			protected void startElementSkip(String uri, String localName, String qName,
					Attributes attributes) {}
			
			@Override
			protected void startElementOwn(String uri, String localName, String qName,
					Attributes attributes) {}
			
			@Override
			protected void endElementSkip(String uri, String localName, String qName) {}
			
			@Override
			protected void endElementOwn(String uri, String localName, String qName) {}
			
			@Override
			protected void charactersSkip(char[] ch, int start, int length) {}
			
			@Override
			protected void charactersOwn(char[] ch, int start, int length) 
			{
				values().putValue("max", new String(ch,start,length));
			}
		
		});
		/* minimum */
		this.installHandler(new YSAXHandler("threshold") {
			
			@Override
			protected void startElementSkip(String uri, String localName, String qName,
					Attributes attributes) {}
			
			@Override
			protected void startElementOwn(String uri, String localName, String qName,
					Attributes attributes) {}
			
			@Override
			protected void endElementSkip(String uri, String localName, String qName) {}
			
			@Override
			protected void endElementOwn(String uri, String localName, String qName) {}
			
			@Override
			protected void charactersSkip(char[] ch, int start, int length) {}
			
			@Override
			protected void charactersOwn(char[] ch, int start, int length) 
			{
				values().putValue("threshold", new String(ch,start,length));
			}
		
		});
		/* minimum */
		this.installHandler(new YSAXHandler("creationMode") {
			
			@Override
			protected void startElementSkip(String uri, String localName, String qName,
					Attributes attributes) {}
			
			@Override
			protected void startElementOwn(String uri, String localName, String qName,
					Attributes attributes) {

				values().putValue("creation-mode", attributes.getValue("code"));
			}
			
			@Override
			protected void endElementSkip(String uri, String localName, String qName) {}
			
			@Override
			protected void endElementOwn(String uri, String localName, String qName) {}
			
			@Override
			protected void charactersSkip(char[] ch, int start, int length) {}
			
			@Override
			protected void charactersOwn(char[] ch, int start, int length) {}
		
		});	
			
		this.installHandler(new YFlowHandler());
		
	}

	@Override
	protected void charactersOwn(char[] ch, int start, int length) {}

	@Override
	protected void charactersSkip(char[] ch, int start, int length) {}

	@SuppressWarnings("unchecked")
	@Override
	protected void endElementOwn(String uri, String localName, String qName) 
	{
		String task_id = values().getValue("net-component");
		
		// Creation of the task
		try {
			builder().createTask(task_id,values().getValue("net"));
		} catch (YSpecBuildException e) {
			e.printStackTrace();
			throw new Error("Fatal error creating task.");
		}
		
		String x;
		
		// Gates
		x = values().getValue("join");
		try {
			if(x != null) {
				if(x.compareTo("xor")==0) {
					builder().setInputGate(task_id,GateType.XOR);
				} else if(x.compareTo("or")==0) {
					builder().setInputGate(task_id,GateType.OR);
				} else if(x.compareTo("and")==0) {
					builder().setInputGate(task_id,GateType.AND);
				}
			}
		} catch (YSpecBuildException e) {
			e.printStackTrace();
			throw new Error("Fatal error setting join gate.");
		}
		x = values().getValue("split");
		try {
			if(x != null) {
				if(x.compareTo("xor")==0) {
					builder().setOutputGate(task_id,GateType.XOR);
				} else if(x.compareTo("or")==0) {
					builder().setOutputGate(task_id,GateType.OR);
				} else if(x.compareTo("and")==0) {
					builder().setOutputGate(task_id,GateType.AND);
				}
			}		
		} catch (YSpecBuildException e) {
			e.printStackTrace();
			throw new Error("Fatal error setting join gate.");
		}
		
		// Composite
		x = values().getValue("decomposesTo");
		if(x != null) {
			java.util.jar.Attributes z = (java.util.jar.Attributes) this.references().get("decomposition_types");
			if(z.getValue(x).compareTo("NetFactsType") == 0) {
				try {
					builder().makeComposite(task_id, x);
				} catch (YSpecBuildException e) {
					e.printStackTrace();
					throw new Error("Fatal error setting decomposition.");
				}
			}
		}
		
		// Multiple
		x = values().getValue("max");
		Integer max = null, min = null, tsh = null;
		if(x != null) {
			max = new Integer(x);
		}
		x = values().getValue("min");
		if(x != null) {
			min = new Integer(x);
		}
		x = values().getValue("threshold");
		if(x != null) {
			tsh = new Integer(x);
		}
		
		if(min != null && max != null) {
			try {
				builder().makeMultiple(task_id, min.intValue(), max.intValue(), tsh.intValue(), values().getValue("creation-mode"));
			} catch (YSpecBuildException e) {
				e.printStackTrace();
				throw new Error("Fatal error adding bounds.");
			}
		}
		
		LinkedList<String> l = (LinkedList<String>) references().get("cancel_set");
		if(l != null) {
			try {
				builder().addCancelSet(task_id, l);
			} catch (YSpecBuildException e) {
				e.printStackTrace();
				throw new Error("Fatal error adding cancel set.");
			}
		}
		
		
		values().putValue("max", null);
		values().putValue("min", null);
		values().putValue("decomposesTo", null);
		values().putValue("join", null);
		values().putValue("split", null);
		values().putValue("net-component", null);
		references().put("cancel_set", null);
	}

	
	@Override
	protected void endElementSkip(String uri, String localName, String qName) {}

	@Override
	protected void startElementOwn(String uri, String localName, String qName,
			Attributes attributes) {
		
		references().put("cancel_set", new LinkedList<String>());
		values().putValue("net-component", attributes.getValue("id"));
	}

	@Override
	protected void startElementSkip(String uri, String localName, String qName,
			Attributes attributes) {}

}
