package com.buschmais.jqassistant.plugin.java.api.scanner;

import com.buschmais.jqassistant.core.scanner.api.ScannerContext;
import com.buschmais.jqassistant.core.scanner.api.Scope;

/**
 * Defines the scopes for java.
 */
public enum JavaScope implements Scope {

    CLASSPATH {
        @Override
        public void create(ScannerContext context) {
            context.push(TypeResolver.class, TypeResolverFactory.createTypeResolver(context));
        }

        @Override
        public void destroy(ScannerContext context) {
            context.pop(TypeResolver.class);
        }
    };

    @Override
    public String getPrefix() {
        return "java";
    }

    @Override
    public String getName() {
        return name();
    }
}
