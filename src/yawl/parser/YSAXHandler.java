package yawl.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import yawl.YSpecificationBuilder;
import yawl.YSpecificationBuilderImpl;


public abstract class YSAXHandler extends DefaultHandler {


	private final String tag;
	
	private final Vector<YSAXHandler> handlers;
	
	private YSAXHandler forward;
	
	private YSAXHandler backward;
	
	private boolean active;
	
	private java.util.jar.Attributes values;
	
	private Map<String,Object> references;

	private YSpecificationBuilder builder;
	
	/* Constructors
	*/
	
	public YSAXHandler(String tag) {
		super();
		this.handlers = new Vector<YSAXHandler>();
		this.forward = null;
		this.backward = null;
		this.active = false;
		this.tag = tag;
		this.values = new java.util.jar.Attributes();
		this.references = new HashMap<String, Object>();
		this.builder = new YSpecificationBuilderImpl();
	}	
		
	/*	
	 * Default Handler API implementation
	 * 
	 * These methods dispatch the treatment of the stream
	 * to the apropiate handler.
	 * 
	*/
	
	/* Start Element */
	public final void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException 	{
		
		if (forward != null) {
			assert active;
			forward.startElement(uri, localName, qName, attributes);
		} else {
			if (dispatch(qName)) {
				active(true);
				forward.startElement(uri, localName, qName, attributes);
			} else if (this.handlesTag(qName)) {
				active(true);
				this.startElementOwn(uri, localName, qName, attributes);
			} else
				startElementSkip(uri, localName, qName, attributes);
		}
	}
	
	/* End Element */
	public final void endElement(String uri, String localName, String qName) 
			throws SAXException {
		
		if(forward != null) {
			assert active;
			forward.endElement(uri, localName, qName);
		} else if (this.handlesTag(qName)) {
			release();
			this.endElementOwn(uri, localName, qName);	
		} else 
			endElementSkip(uri, localName, qName);
	}

	/* Characters */
	public final void characters(char ch[], int start, int length) 
			throws SAXException {
		int i = 0;
		if(forward != null) {
			forward.characters(ch, start, length);
			i++;
		} else if (active) {
			i++;
			this.charactersOwn(ch, start, length);
		} else {
			charactersSkip(ch, start, length);
			i++;
		}
		assert i==1;
	}



	/*
	 * YSAXHandler-specific implementation
	 * 
	*/
	
	/* These methods implement the 
	 * inner-workings of the class.  
	*/
	
	protected final boolean active() {
		return active;
	}
	
	protected final void active(boolean b) {
		this.active = b;
	}
	
	protected final boolean handlesTag(String _tag) {
		return this.tag.compareTo(_tag)==0; 
	}
	
	protected final boolean dispatch(String tag) {
		boolean r = false;
		for(YSAXHandler y : handlers) {
			if(r = y.handlesTag(tag)) {
				this.forward = y;
				y.backward = this;
				break;
			}
		}		
		return r;
	}	
	
	protected final void release() {
		if (backward != null) 
			backward.forward = null;
		active(false);
	}
	
	protected void installHandler(YSAXHandler o) {
		this.handlers.add(o);
		o.installParent(this);
	}
	
	protected void installParent(YSAXHandler p) {
		this.values(p.values());
		this.references(p.references());
		this.builder(p.builder());
		for(YSAXHandler y : handlers) {
			y.installParent(this);
		}
	}
	
	
	protected java.util.jar.Attributes values() {
		return this.values;
	}
	
	protected void values(java.util.jar.Attributes i) {
		this.values = i;
	}

	protected Map<String,Object> references() {
		return this.references;
	}
	
	protected void references(Map<String,Object> r) {
		this.references = r;
	}	
	
	public YSpecificationBuilder builder() {
		return builder;
	}
	
	
	protected void builder(YSpecificationBuilder b) {
		this.builder = b;
	}
	
		
	/* These methods handle start/end element and
	 * character events when the current tag corresponds
	 * to this handler. 
	 * 
	 * These methods must be overridden in subclasses to
	 * gain handler-specific parsing.  
	*/
	
	/* Skipping Functions */
	
	protected abstract void startElementSkip(String uri, String localName, String qName,
			Attributes attributes) throws SAXException;
	
	protected abstract void endElementSkip(String uri, String localName, 
			String qName) throws SAXException;
	
	protected abstract void charactersSkip(char[] ch, int start,
			int length) throws SAXException;

	
	/* Processing Functions */
	
	protected abstract void startElementOwn(String uri, String localName, String qName,
			Attributes attributes) throws SAXException;	
	
	protected abstract void endElementOwn(String uri, String localName, 
			String qName) throws SAXException;
	
	protected abstract void charactersOwn(char[] ch, int start, 
			int length) throws SAXException;
	
	
}
