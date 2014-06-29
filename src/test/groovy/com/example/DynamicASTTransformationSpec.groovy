package com.example

import com.example.domain.Service
import org.codehaus.groovy.control.CompilerConfiguration
import spock.lang.Specification

import static org.codehaus.groovy.control.customizers.builder.CompilerCustomizationBuilder.withConfig

class DynamicASTTransformationSpec extends Specification {
    final static SAME_CL = 'sameClassLoader'
    final static DIFF_CL = 'differentClassLoader'
    GroovyShell shell

    void setup() {
        def config = withConfig(new CompilerConfiguration()) { imports { normal Dynamic, Service } }
        Binding binding = new Binding(
                (DIFF_CL): loadFromDistinctClassloader(Service).newInstance(),
                (SAME_CL): new Service()
        )

        shell = new GroovyShell(binding, config)
    }

    def "Mixing types from different class loaders result in cast exception, due to Groovy inserting casts, even in non-static code "() {
        setup:
        def c = loadFromDistinctClassloader(Service)

        when:
        Service i = c.newInstance()

        then:
        thrown(ClassCastException)
    }

    def "Variable initialization: same classloader works"() {
        when: "the assigned value is loaded from the same classloader as the code declaring the variable"
        shell.evaluate("Service foo = $SAME_CL")

        then: "no exception is thrown"
        noExceptionThrown()
    }

    def "Variable initialization: different classloader fails"() {
        when: "the assigned value is loaded from a different classloader"
        shell.evaluate("Service foo = $DIFF_CL")

        then: "Groovy inserts auto-cast"
        thrown(ClassCastException)
    }

    def "@Dynamic variable initialization: different classloader works"() {
        when: "the assigned value is loaded from a different classloader, but the variable declaration is annotated with @Dynamic"
        shell.evaluate("@Dynamic Service foo = $DIFF_CL // initialize in declaration")

        then: "no exception is thrown, but for the purposes of IDE and source-level analysis, the code is typed"
        noExceptionThrown()
    }

    def "@Dynamic variable assignment: different classloader works"() {
        when: "the assigned value is loaded from a different classloader, but the variable declaration is annotated with @Dynamic"
        shell.evaluate("""
            @Dynamic Service foo
            foo = $DIFF_CL
        """)

        then: "no exception is thrown, but for the purposes of IDE and source-level analysis, the code is typed"
        noExceptionThrown()
    }


    private <T> Class<T> loadFromDistinctClassloader(Class<T> classPrototype) {
        def ucpField = URLClassLoader.getDeclaredField('ucp')
        ucpField.accessible = true
        def ucl = getClass().classLoader
        def childFirst = new IsolatingClassLoader(ucpField.get(ucl).URLs, ucl, ~/com\.example\..*/)
        def clazz = childFirst.loadClass(classPrototype.name)
        return clazz as Class<T>
    }
}
