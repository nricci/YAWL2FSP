package yawl.parser;

import org.xml.sax.Attributes;

import yawl.YSpecBuildException;

public class YCondHandler extends YSAXHandler {

	public YCondHandler() {
		super("condition");
		this.installHandler(new YFlowHandler());
	}

	@Override
	protected void charactersOwn(char[] ch, int start, int length) {}

	@Override
	protected void charactersSkip(char[] ch, int start, int length) {}

	@Override
	protected void endElementOwn(String uri, String localName, String qName) 
	{
		values().putValue("net-component", null);
	}

	@Override
	protected void endElementSkip(String uri, String localName, String qName) {}

	@Override
	protected void startElementOwn(String uri, String localName, String qName,
			Attributes attributes) 
	{
		String cond_id = attributes.getValue("id");
		values().putValue("net-component", cond_id);
		try {
			builder().createCondition(cond_id, values().getValue("net"));
		} catch (YSpecBuildException e) {
			e.printStackTrace();
			throw new Error("Fatal error creating condition.");
		}
	}

	@Override
	protected void startElementSkip(String uri, String localName, String qName,
			Attributes attributes) {}

}
