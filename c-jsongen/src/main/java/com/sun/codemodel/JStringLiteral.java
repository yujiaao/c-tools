package com.sun.codemodel;

public class JStringLiteral extends JExpressionImpl {
    public final String str;

    JStringLiteral(String what) {
        this.str = what;
    }

    public void generate(JFormatter f) {
        f.p(JExpr.quotify('"', this.str));
    }

    @Override
    public String toString() {
        return str;
    }
}
