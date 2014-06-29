package com.example

import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.DeclarationExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

@GroovyASTTransformation(phase= CompilePhase.SEMANTIC_ANALYSIS)
public class DynamicASTTransformation implements ASTTransformation {
    @Override
    public void visit(ASTNode[] nodes, SourceUnit source) {
        assert nodes.length==2 && nodes[0].getClass()==AnnotationNode

        def annotation = nodes[0] as AnnotationNode
        assert annotation.classNode.name==Dynamic.name

        def target = nodes[1]
        switch (target) {
            case DeclarationExpression:
                def declaration = target as DeclarationExpression
                assert declaration.leftExpression.getClass()==VariableExpression
                valueNotSupported(annotation)

                VariableExpression original = declaration.leftExpression
                declaration.leftExpression = new VariableExpression(original.name)

        }

    }

    private void valueNotSupported(AnnotationNode annotation) {
        if (annotation.members.containsKey('value')) {
            throw new UnsupportedOperationException("You can not specify 'value' when using @Dynamic with field, variable or parameter target.")
        }
    }
}
