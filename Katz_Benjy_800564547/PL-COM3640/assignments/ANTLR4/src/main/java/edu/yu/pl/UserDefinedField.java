package edu.yu.pl;


import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

public class UserDefinedField {

    final private String udfText;
    final private ParseTree parseTree;

    public UserDefinedField(final String udfText) {
        this.udfText = udfText;

        var lexer = new UDFLanguageLexer(
                CharStreams.fromString(udfText));

        var parser = new UDFLanguageParser(
                new CommonTokenStream(lexer));

        this.parseTree = parser.userField();
    }

    public <T> T evaluate(ParseTreeVisitor<T> visitor) {
        return visitor.visit(this.parseTree);
    }

    public String getUdfText() {
        return udfText;
    }

    @Override
    public String toString() {
        return String.format("udf=%s", udfText);
    }
}
