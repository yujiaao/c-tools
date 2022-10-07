package com.bixuebihui.db;

import javax.validation.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.Collections;
import java.util.Set;

/**
 * <p>PojoValidator class.</p>
 *
 * @author xingwx
 * @version $Id: $Id
 */
public class PojoValidator<T> {
    static final Logger logger = LoggerFactory.getLogger(PojoValidator.class);

    Validator validator;

    public PojoValidator() {
        try {
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            validator = factory.getValidator();
        }catch (Exception e){
            logger.warn("Validator init error, annotation constrains will not work", e);
            e.printStackTrace();
        }

    }

    /**
     * <p>validate.</p>
     *
     * @param pojo a T object.
     * @return a {@link java.util.Set} object.
     */
    public Set<ConstraintViolation<T>> validate(T pojo){
        if(validator!=null) {
            return validator.validate(pojo);
        }
        return Collections.emptySet();
    }

    /**
     * <p>asureValid.</p>
     *
     * @param pojo a T object.
     */
    public void asureValid(T pojo){
        Set<ConstraintViolation<T>> res = validate(pojo);
        if(res.size()==0) {
            return;
        }

        String message  = formMessage(res);

        throw new ValidationException(message);
    }

    private String formMessage(Set<ConstraintViolation<T>> res) {
        //ConstraintViolationImpl{interpolatedMessage='最小不能小于10000',
        //propertyPath=id, rootBeanClass=class com.demo.superma.freemarker.Site,
        //messageTemplate='{javax.validation.constraints.Min.message}'}
        StringBuilder sb = new StringBuilder();
        for(ConstraintViolation<T> cv: res){
            sb.append(transBean(cv.getRootBeanClass().getName(), cv.getPropertyPath().toString()))
                    .append(" ").append(cv.getMessage())
                    .append(valueObject2Text(cv.getInvalidValue()));
        }

        return sb.toString();
    }

    /**
     * <p>valueObject2Text.</p>
     *
     * @param obj a {@link java.lang.Object} object.
     * @return a {@link java.lang.String} object.
     */
    protected String valueObject2Text(Object obj){
        if(obj==null) {
            return "";
        }

        String str = obj.toString();
        return ", 实际传入值为:"+
                StringEscapeUtils.escapeHtml4(StringUtils.abbreviate(str, 1000)
                +"\n 长度为:"+str.length());
    }


    /**
     * <p>transBean.</p>
     *
     * @param rootBeanClass a {@link java.lang.String} object.
     * @param propertyPath a {@link java.lang.String} object.
     * @return a {@link java.lang.Object} object.
     */
    public Object transBean(String rootBeanClass, String propertyPath) {
        return "对象 "+rootBeanClass+" 的属性 "+propertyPath;
    }

    /**
     * <p>validate.</p>
     *
     * @param beanType a {@link java.lang.Class} object.
     * @param fieldNames an array of {@link java.lang.String} objects.
     * @param params an array of {@link java.lang.Object} objects.
     * @return a {@link java.util.Set} object.
     */
    public Set<ConstraintViolation<T>> validate(Class<T> beanType,
                                                String[] fieldNames, Object[] params){
        if(validator ==null){
            return Collections.emptySet();
        }

        Set<ConstraintViolation<T>> res=null;
        int i=0;
        for(String propertyName:fieldNames){

            Set<ConstraintViolation<T>> err =
                    validator.validateValue(beanType,propertyName, params[i] );
            if(i==0) {
                res = err;
            }else {
                if(res==null) {
                    res = err;
                }else {
                    res.addAll(err);
                }
            }
            i++;
        }
        return res;
    }
}
