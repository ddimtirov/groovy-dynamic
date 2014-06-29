package com.example;

import org.codehaus.groovy.transform.GroovyASTTransformationClass;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD, ElementType.LOCAL_VARIABLE})
@GroovyASTTransformationClass("com.example.DynamicASTTransformation")
public @interface Dynamic {
    /**
     * If the annotating a method, defines which types should be
     * suppressed within this method.
     *
     * If annotating a field, defines which types should be suppressed
     * if reached/passed to a statement involving this field.
     */
    Class[] value() default {};
}
