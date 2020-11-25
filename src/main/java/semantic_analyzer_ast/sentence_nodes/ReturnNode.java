package semantic_analyzer_ast.sentence_nodes;

import exceptions.SemanticException;
import semantic_analyzer.SymbolTable;
import semantic_analyzer.TypeVoid;
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
            if (!expressionNode.getType().equals(SymbolTable.getInstance().getCurrMethod().getReturnType())) {
                throw new SemanticException(this, "no es del mismo tipo que el requerido para el retorno");
            }
        } else {
            //TODO change this ugly instanceof
            if (!(SymbolTable.getInstance().getCurrMethod().getReturnType() instanceof TypeVoid)) {
                throw new SemanticException(this, "el metodo requiere tipo de retorno");
            }
        }
        //TODO check for missing return statement
    }

    @Override
    public void acceptVisitor(VisitorSentence v) {
        v.visit(this);
    }
}
