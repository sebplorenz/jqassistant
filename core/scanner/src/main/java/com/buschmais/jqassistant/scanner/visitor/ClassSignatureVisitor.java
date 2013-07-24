package com.buschmais.jqassistant.scanner.visitor;

import com.buschmais.jqassistant.scanner.resolver.DescriptorResolverFactory;
import com.buschmais.jqassistant.store.api.model.descriptor.ClassDescriptor;
import org.objectweb.asm.signature.SignatureVisitor;

public class ClassSignatureVisitor extends DependentSignatureVisitor<ClassDescriptor> implements SignatureVisitor {

    protected ClassSignatureVisitor(ClassDescriptor classDescriptor, DescriptorResolverFactory resolverFactory) {
        super(classDescriptor, resolverFactory);
    }

    @Override
    public SignatureVisitor visitSuperclass() {
        return new DependentSignatureVisitor<ClassDescriptor>(getDependentDescriptor(), getResolverFactory()) {

            private ClassDescriptor classDescriptor;

            @Override
            public void visitClassType(String name) {
                classDescriptor = getClassDescriptor(name);
                getDependentDescriptor().setSuperClass(classDescriptor);
            }

            @Override
            public void visitInnerClassType(String name) {
                String innerClassName = classDescriptor.getFullQualifiedName() + "$" + name;
                getDependentDescriptor().setSuperClass(getClassDescriptor(innerClassName));
            }

        };
    }

    @Override
    public SignatureVisitor visitInterface() {
        return new DependentSignatureVisitor<ClassDescriptor>(getDependentDescriptor(), getResolverFactory()) {

            private ClassDescriptor classDescriptor;

            @Override
            public void visitClassType(String name) {
                classDescriptor = getClassDescriptor(name);
                getDependentDescriptor().getInterfaces().add(classDescriptor);
            }

            @Override
            public void visitInnerClassType(String name) {
                String innerClassName = classDescriptor.getFullQualifiedName() + "$" + name;
                getDependentDescriptor().getInterfaces().add(getClassDescriptor(innerClassName));
            }
        };
    }

}
