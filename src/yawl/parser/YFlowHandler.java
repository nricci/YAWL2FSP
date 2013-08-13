package yawl.parser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import yawl.YSpecBuildException;

public class YFlowHandler extends YSAXHandler {

	public YFlowHandler() {
		super("flowsInto");
		YSAXHandler x = new YSAXHandler("nextElementRef") {
			
			@Override
			protected void startElementSkip(String uri, String localName, String qName,
					Attributes attributes) {}
			
			@Override
			protected void startElementOwn(String uri, String localName, String qName,
					Attributes attributes)
			{
				values().putValue("flow-to", attributes.getValue("id"));			
			}
			
			@Override
			protected void endElementSkip(String uri, String localName, String qName) {}
			
			@Override
			protected void endElementOwn(String uri, String localName, String qName) {}
			
			@Override
			protected void charactersSkip(char[] ch, int start, int length) {}
			
			@Override
			protected void charactersOwn(char[] ch, int start, int length) {}
		};		
		this.installHandler(x);
		YSAXHandler z = new YSAXHandler("isDefaultFlow") {
			@Override
			protected void startElementSkip(String uri, String localName, String qName,
					Attributes attributes) {}
			
			@Override
			protected void startElementOwn(String uri, String localName, String qName,
					Attributes attributes)
			{
				values().putValue("default-flow", "true");		
			}
			
			@Override
			protected void endElementSkip(String uri, String localName, String qName) {}
			
			@Override
			protected void endElementOwn(String uri, String localName, String qName) 
			{
				
			}
			
			@Override
			protected void charactersSkip(char[] ch, int start, int length) {}
			
			@Override
			protected void charactersOwn(char[] ch, int start, int length) {}			
		};
		this.installHandler(z);
		
	}

	@Override
	protected void charactersOwn(char[] ch, int start, int length) {}

	@Override
	protected void charactersSkip(char[] ch, int start, int length) {}

	@Override
	protected void endElementOwn(String uri, String localName, String qName) 
	{
		String from = values().getValue("net-component");
		String to = values().getValue("flow-to");				
		try {
			builder().addFlow(from,to);
		} catch (YSpecBuildException e) {
			e.printStackTrace();
			throw new Error("Fatal error setting a flow.");
		}	
		if(values().getValue("default-flow") != null) {
			try {
				builder().setDefaultFlow(from, to);
			} catch (YSpecBuildException e) {
				e.printStackTrace();
				throw new Error("Fatal error setting default flow.");
			}
		}
		values().putValue("default-flow", null);
	}

	@Override
	protected void endElementSkip(String uri, String localName, String qName) {}

	@Override
	protected void startElementOwn(String uri, String localName, String qName,
			Attributes attributes) 
	{
		values().putValue("default-flow", null);
	}

	@Override
	protected void startElementSkip(String uri, String localName, String qName,
			Attributes attributes) {}

}
