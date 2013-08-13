package yawl.parser;

import org.xml.sax.Attributes;

import yawl.YSpecBuildException;

public class YDecompositionHandler extends YSAXHandler {

	public YDecompositionHandler() {
		super("decomposition");		
		
		YSAXHandler pctrl = new YSAXHandler("processControlElements") {
			
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
			protected void charactersOwn(char[] ch, int start, int length) {}
		};
		
		this.installHandler(pctrl);
		pctrl.installHandler(new YICondHandler());
		pctrl.installHandler(new YOCondHandler());
		pctrl.installHandler(new YCondHandler());
		pctrl.installHandler(new YTaskHandler());
				
	}
		
	/* Skipping Functions */
	
	@SuppressWarnings("unused")
	protected void startElementSkip(String uri, String localName, String qName,
			Attributes attributes) {}
	
	@SuppressWarnings("unused")
	protected void endElementSkip(String uri, String localName, String qName) {}
	
	@SuppressWarnings("unused")
	protected void charactersSkip(char[] ch, int start, int length) {}

	
	/* Processing Functions */
	
	@SuppressWarnings("unused")
	protected void startElementOwn(String uri, String localName, String qName,
			Attributes attributes) {
		
		/*	We only want nets which type is "NetFactsType" 		
		*/			
		if(attributes.getValue("xsi:type").compareTo("NetFactsType")==0) {
			String new_net_id = attributes.getValue("id");
			values().putValue("net",new_net_id);
			try {
				builder().createNet(new_net_id, values().getValue("specification"));
			} catch (YSpecBuildException e) {
				e.printStackTrace();
				throw new Error("Fatal error creating net.");
			}
			if(attributes.getValue("isRootNet") != null) {
				try {
					builder().makeNetRoot(new_net_id);
				} catch (YSpecBuildException e) {
					e.printStackTrace();
					throw new Error("Fatal error making net root.");
				}
			}
		} else {
			release();
		}
		
	}	
	
	@SuppressWarnings("unused")
	protected void endElementOwn(String uri, String localName, String qName) {
		//System.out.println("Element Ended : " + qName);
	}
	
	@SuppressWarnings("unused")
	protected void charactersOwn(char[] ch, int start, int length) {}	
	
	
}
