package yawl.parser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import yawl.YSpecBuildException;

public class YSpecificationHandler extends YSAXHandler {

	/*	This is the topmost handler. It handles the tag
	 * "specification".
	 * 
	 * This handler has two sub-handlers. One just for fetching
	 * the specification title. The definition of a whole new
	 * class is not justified for this purpose; so the abstract class
	 * is instantiated with a few methods overridden just to meet
	 * this requirement.
	 * 
	 * The other sub-handlers are in charge of retrieving the nets.
	 * A separate YSAXHandler subclass is introduced for that matter. 
	 * 
	 */
	public YSpecificationHandler() {
		super("specification");

		YSAXHandler title_fetcher = new YSAXHandler("title") {

			@Override
			protected void charactersOwn(char[] ch, int start, int length)
			{
				String s = new String(ch,start,length);
				if(s == null || s == "") {s="ProvisionalTitle";} 
				values().putValue("specification",s);
				try {
					
					builder().createSpecification(s);
				} catch (YSpecBuildException e) {
					e.printStackTrace();
					throw new Error("Fatal error creating the specification.");
				}
			}

			@Override
			protected void charactersSkip(char[] ch, int start, int length) {}

			@Override
			protected void endElementOwn(String uri, String localName,
					String qName) {}

			@Override
			protected void endElementSkip(String uri, String localName,
					String qName) {}

			@Override
			protected void startElementOwn(String uri, String localName,
					String qName, Attributes attributes) {}

			@Override
			protected void startElementSkip(String uri, String localName,
					String qName, Attributes attributes) {}
		
		};
		
		installHandler(title_fetcher);
		installHandler(new YDecompositionHandler());
		
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
		values().putValue("specification",attributes.getValue("uri"));
	}	
	
	@SuppressWarnings("unused")
	protected void endElementOwn(String uri, String localName, String qName) {}
	
	@SuppressWarnings("unused")
	protected void charactersOwn(char[] ch, int start, int length) {}
	
	
}
