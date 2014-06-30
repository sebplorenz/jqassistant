package com.buschmais.jqassistant.plugin.eclipse.rcp;

import java.util.List;

import com.buschmais.jqassistant.core.store.api.descriptor.NamedDescriptor;
import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;
import com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;

@Label("XmlElement")
public interface XmlElement extends NamedDescriptor {

	@Relation("HAS")
	@Outgoing
	List<XmlAttribute> getAttributes();

	@Relation("CONTAINS")
	@Outgoing
	List<XmlElement> getElements();
}
