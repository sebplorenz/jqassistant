package com.buschmais.jqassistant.plugin.cdi.test.set.beans.alternative;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Named;

@Alternative
@SessionScoped
@Named
@Retention(RUNTIME)
@Target(TYPE)
public @interface AlternativeStereotype {
}
