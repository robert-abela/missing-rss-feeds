package com.robertabela.rss;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class XmlAttribute {
	private String key;
	private String value;

	public XmlAttribute() {
		super();
	}
	
	public XmlAttribute(String key, Object value) {
		super();
		this.key = key;
		this.value = value == null ? "" : value.toString();
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
