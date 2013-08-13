package yawl.parser;

import org.xml.sax.Attributes;

public class YPreliminarInfoHandler extends YSAXHandler {

	public YPreliminarInfoHandler() {
		super("decomposition");
	}

	@Override
	protected void charactersOwn(char[] ch, int start, int length) {}

	@Override
	protected void charactersSkip(char[] ch, int start, int length) {}

	@Override
	protected void endElementOwn(String uri, String localName, String qName) {}

	@Override
	protected void endElementSkip(String uri, String localName, String qName) {}

	@Override
	protected void startElementOwn(String uri, String localName, String qName,
			Attributes attributes) {
		
		this.values().putValue(attributes.getValue("id"), attributes.getValue("xsi:type"));
	}

	@Override
	protected void startElementSkip(String uri, String localName, String qName,
			Attributes attributes) {}

}
