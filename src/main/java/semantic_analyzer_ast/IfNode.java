package semantic_analyzer_ast;

import lexical_analyzer.IToken;

public class IfNode extends Node {
    private ExpressionNode condition;
    private SentenceNode sentenceNode;
    private ElseNode elseNode;

    public IfNode(IToken token, String line, int row, int column) {
        super(token, line, row, column);
    }

    public ExpressionNode getCondition() {
        return condition;
    }

    public void setCondition(ExpressionNode condition) {
        this.condition = condition;
    }

    public ElseNode getElseNode() {
        return elseNode;
    }

    public void setElseNode(ElseNode elseNode) {
        this.elseNode = elseNode;
    }

    public SentenceNode getSentenceNode() {
        return sentenceNode;
    }

    public void setSentenceNode(SentenceNode sentenceNode) {
        this.sentenceNode = sentenceNode;
    }

    @Override
    public void validate() {

    }
}
