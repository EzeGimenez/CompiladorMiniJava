package semantic_analyzer_ast.sentence_nodes;

import exceptions.SemanticException;
import semantic_analyzer.IType;
import semantic_analyzer.SymbolTable;
import semantic_analyzer_ast.expression_nodes.ExpressionNode;
import semantic_analyzer_ast.visitors.VisitorSentence;

public class ReturnNode extends SentenceNode {
    private ExpressionNode expressionNode;

    public ReturnNode(String line, int row, int column) {
        super(line, row, column);
    }

    public ExpressionNode getExpressionNode() {
        return expressionNode;
    }

    public void setExpressionNode(ExpressionNode expressionNode) {
        this.expressionNode = expressionNode;
    }

    @Override
    public void validate() throws SemanticException {
        if (expressionNode != null) {
            expressionNode.validate();

            if (isConstructor()) {
                throw new SemanticException(expressionNode, "sentencia de retorno con una expresion dentro de un constructor");
            }

            IType requiredType = SymbolTable.getInstance().getCurrMethod().getReturnType();
            if (!expressionNode.getType().acceptTypeChecker(requiredType.getTypeChecker())) {
                throw new SemanticException(expressionNode, "no es del mismo tipo que el requerido para el retorno");
            }
        } else {
            if (!(SymbolTable.getInstance().getCurrMethod().getReturnType().getName().equals("void"))
                    && !isConstructor()) {
                throw new SemanticException(this, "el metodo requiere tipo de retorno");
            }
        }
    }

    public boolean isConstructor() {
        return SymbolTable.getInstance().getCurrMethod() == SymbolTable.getInstance().getCurrClass().getConstructor();
    }

    @Override

    public void acceptVisitor(VisitorSentence v) {
        v.visit(this);
    }
}
