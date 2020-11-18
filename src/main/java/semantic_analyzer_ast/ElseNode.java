package semantic_analyzer_ast;

import lexical_analyzer.IToken;

public class ElseNode extends Node {

    private ExpressionNode condition;
    private SentenceNode sentenceNode;
    private ElseNode elseNode;

    public SentenceNode getSentenceNode() {
        return sentenceNode;
    }

    public void setSentenceNode(SentenceNode sentenceNode) {
        this.sentenceNode = sentenceNode;
    }

    public ElseNode(IToken token, String line, int row, int column) {
        super(token, line, row, column);
    }

    public ExpressionNode getCondition() {
        return condition;
    }

    public void setCondition(ExpressionNode condition) {
        this.condition = condition;
    }

    @Override
    public void validate() {

    }
}
