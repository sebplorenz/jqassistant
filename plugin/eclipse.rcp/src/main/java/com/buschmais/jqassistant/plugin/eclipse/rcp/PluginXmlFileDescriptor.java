package com.buschmais.jqassistant.plugin.eclipse.rcp;

import com.buschmais.jqassistant.core.store.api.descriptor.FileDescriptor;
import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;
import com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;

@Label("PluginXml")
public interface PluginXmlFileDescriptor extends FileDescriptor {
	
	@Relation("CONTAINS")
	@Outgoing
	XmlElement getRoot();

	void setRoot(XmlElement root);
}
