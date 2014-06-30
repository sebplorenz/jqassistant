package com.buschmais.jqassistant.plugin.eclipse.rcp;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.core.scanner.api.Scope;
import com.buschmais.jqassistant.core.store.api.Store;
import com.buschmais.jqassistant.core.store.api.descriptor.FileDescriptor;
import com.buschmais.jqassistant.plugin.common.impl.scanner.AbstractScannerPlugin;
import com.buschmais.jqassistant.plugin.java.api.scanner.JavaScope;

public class EclipseRcpProjectScannerPlugin extends AbstractScannerPlugin<InputStream> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EclipseRcpProjectScannerPlugin.class);

	@Override
	public boolean accepts(InputStream arg0, String path, Scope scope) throws IOException {
		LOGGER.info("Accepts scope: " + scope + " path: " + path);
		return JavaScope.CLASSPATH.equals(scope) && "/plugin.xml".equals(path);
	}

	@Override
	public Class<? super InputStream> getType() {
		return InputStream.class;
	}

	@Override
	public Iterable<? extends FileDescriptor> scan(InputStream input, String path, Scope arg2, Scanner arg3)
			throws IOException {
		
		LOGGER.info("Scanning plugin.xml");
		
		SAXReader reader = new SAXReader();
		Document document = null;
		try {
			document = reader.read(input);
		} catch (DocumentException e) {
			throw new IOException(e);
		}

		Store store = getStore();
		PluginXmlFileDescriptor result = store.create(PluginXmlFileDescriptor.class);
		result.setFileName("plugin.xml");
		result.setRoot(readElement(document.getRootElement(), store));

		return Arrays.asList(result);
	}

	XmlElement readElement(Element element, Store store) {
		XmlElement current = store.create(XmlElement.class);
		current.setName(element.getName());

		for (Iterator i = element.attributeIterator(); i.hasNext();) {
			Attribute a = (Attribute) i.next();
			XmlAttribute attribute = store.create(XmlAttribute.class);
			attribute.setName(a.getName());
			attribute.setValue(a.getValue());
			current.getAttributes().add(attribute);
		}

		for (Iterator i = element.elementIterator(); i.hasNext();)
			current.getElements().add(readElement((Element) i.next(), store));

		return current;
	}

	@Override
	protected void initialize() {
		//
	}

}
