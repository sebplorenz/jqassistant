package com.buschmais.jqassistant.plugin.java.api.model;

import static com.buschmais.jqassistant.plugin.java.api.report.Java.JavaLanguageElement.Package;

import com.buschmais.jqassistant.core.store.api.model.FullQualifiedNameDescriptor;
import com.buschmais.jqassistant.plugin.common.api.model.DirectoryDescriptor;
import com.buschmais.jqassistant.plugin.common.api.model.FileContainerDescriptor;
import com.buschmais.jqassistant.plugin.java.api.report.Java;
import com.buschmais.xo.neo4j.api.annotation.Label;

/**
 * Describes a Java package.
 */
@Java(Package)
@Label(value = "Package", usingIndexedPropertyOf = FullQualifiedNameDescriptor.class)
public interface PackageDescriptor extends PackageMemberDescriptor, DirectoryDescriptor, FileContainerDescriptor {
}
