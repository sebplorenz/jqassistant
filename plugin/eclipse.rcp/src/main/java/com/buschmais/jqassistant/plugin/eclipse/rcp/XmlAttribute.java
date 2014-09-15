package com.buschmais.jqassistant.plugin.eclipse.rcp;

import com.buschmais.jqassistant.core.store.api.type.NamedDescriptor;
import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Property;

@Label("XmlAttribute")
public interface XmlAttribute extends NamedDescriptor {

	@Property("value")
	String getValue();

	void setValue(String value);
}
