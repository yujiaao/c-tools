package com.sun.codemodel;

/**
 * @author xwx
 */
public final class JAnnotationStringValue extends JAnnotationValue {
    private final JExpression value;

    JAnnotationStringValue(JExpression value) {
        this.value = value;
    }

    public void generate(JFormatter f) {
        f.g(this.value);
    }

    public String getValue(){
        return value.toString();
    }
}
