package ChimeraMonsters.util.matchers;

import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.patcher.Expectation;
import javassist.expr.Expr;

import java.util.HashMap;

public class CompoundMatcher extends Matcher {
    HashMap<Matcher, Boolean> matchers = new HashMap<>();
    boolean pedantic;

    public CompoundMatcher(Matcher... matchers) {
        this(true, matchers);
    }

    public CompoundMatcher(boolean pedantic, Matcher... matchers) {
        super(Expectation.CATCH_CLAUSE);
        this.pedantic = pedantic;
        for (Matcher m : matchers) {
            this.matchers.put(m, false);
        }
    }

    public boolean compoundMatch(Expectation expectation, Expr expr) {
        if (pedantic) {
            return matchers.keySet().stream().allMatch(m -> m.getExpectation() == expectation && m.match(expr));
        }
        for (Matcher m : matchers.keySet()) {
            if (m.getExpectation() == expectation && m.match(expr)) {
                matchers.put(m, true);
            }
        }
        return matchers.keySet().stream().allMatch(m -> matchers.get(m));
    }

    public void reset() {
        matchers.replaceAll((m, v) -> false);
    }

    @Override
    public boolean match(Expr expr) {
        return false;
    }
}
