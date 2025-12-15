package com.bixuebihui;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ObjectFilter<T> {

    //相比生成代码，这里返回的属性顺序是不确定的
    public static <T> List<Object> filterObject(T obj, List<String> excludeFields) {
        List<Object> resultList = new ArrayList<>();

        // 获取对象的类
        Class<?> clazz = obj.getClass();
        Set<String> exSet = excludeFields.stream().map(String::toLowerCase).collect(Collectors.toSet());

        // 获取类的所有属性名（这里简化处理，假设都是简单属性，通过getter方法获取）
        for (var method : clazz.getMethods()) {
            String name = method.getName();
            if ((name.startsWith("get") || name.startsWith("is")) && !name.equals("getClass")
                    && method.getParameterCount() == 0) {

                String propertyName = name.startsWith("get") ? name.substring(3) :
                    name.substring(2);

                if (!exSet.contains(propertyName.toLowerCase())) {
                    try {
                        Object propertyValue = method.invoke(obj);
                        resultList.add(propertyValue);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return resultList;
    }
}
