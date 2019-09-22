package com.robertabela.rss;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Error {
	@XmlElement(name = "attribute")
	private List<XmlAttribute> attributeList;

	public Error() {
		super();
	}

	public Error(Map<String, Object> errorAttributes) {
		super();

		attributeList = new ArrayList<>();
		errorAttributes.forEach((key, value) -> {
			attributeList.add(new XmlAttribute(key, value));
		});
	}

	public List<XmlAttribute> getAttributeList() {
		return attributeList;
	}

	public void setAttributeList(List<XmlAttribute> attributeList) {
		this.attributeList = attributeList;
	}
}
