package ChimeraMonsters.util.matchers;

import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.patcher.Expectation;
import javassist.expr.*;

public class BasicMatchChecker extends ExprEditor {
    private final Matcher matcher;
    private boolean didMatch;

    public BasicMatchChecker(Matcher matcher) {
        this.matcher = matcher;
    }

    protected void doMatch(Expectation expectation, Expr expr) {
        if (matcher instanceof CompoundMatcher && ((CompoundMatcher) matcher).compoundMatch(expectation, expr)) {
            didMatch = true;
        } else if (matcher.getExpectation() == expectation && matcher.match(expr)) {
            didMatch = true;
        }
    }

    public boolean didMatch() {
        return didMatch;
    }

    public void edit(Cast expr) {
        this.doMatch(Expectation.TYPE_CAST, expr);
    }

    public void edit(ConstructorCall expr) {
        this.doMatch(Expectation.CONSTRUCTOR_CALL, expr);
    }

    public void edit(FieldAccess expr) {
        this.doMatch(Expectation.FIELD_ACCESS, expr);
    }

    public void edit(Handler expr) {
        this.doMatch(Expectation.CATCH_CLAUSE, expr);
    }

    public void edit(Instanceof expr) {
        this.doMatch(Expectation.INSTANCEOF, expr);
    }

    public void edit(MethodCall expr) {
        this.doMatch(Expectation.METHOD_CALL, expr);
    }

    public void edit(NewArray expr) {
        this.doMatch(Expectation.ARRAY_CREATION, expr);
    }

    public void edit(NewExpr expr) {
        this.doMatch(Expectation.NEW_EXPRESSION, expr);
    }

}
