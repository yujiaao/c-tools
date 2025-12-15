package com.bixuebihui.query;

import com.bixuebihui.db.ActiveRecord;
import com.bixuebihui.jdbc.SqlSort;
import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static com.bixuebihui.jdbc.SqlSort.Sort.ASC;

/**
 * @author xwx
 *
 * <a href="https://cloud.tencent.com/developer/article/1686237">springmvc之使用POJO作为参数</a>
 * <p>
 * springmvc会按请求参数名和POJO属性名进行匹配，自动为该对象填充属性值，支持级联属性。
 * <p>
 * User.java
 * <p>
 * package com.gong.springmvc.entities;
 * <p>
 * public class User {
 * private String username;
 * private String password;
 * private String email;
 * private Integer age;
 * Address address;
 * public String getUsername() {
 * return username;
 * }
 * public void setUsername(String username) {
 * this.username = username;
 * }
 * public String getPassword() {
 * return password;
 * }
 * public void setPassword(String password) {
 * this.password = password;
 * }
 * public String getEmail() {
 * return email;
 * }
 * public void setEmail(String email) {
 * this.email = email;
 * }
 * public Integer getAge() {
 * return age;
 * }
 * public void setAge(Integer age) {
 * this.age = age;
 * }
 * public Address getAddress() {
 * return address;
 * }
 * public void setAddress(Address address) {
 * this.address = address;
 * }
 * @Override public String toString() {
 * return "User [username=" + username + ", password=" + password + ", email=" + email + ", age=" + age
 * + ", address=" + address + "]";
 * }
 * <p>
 * <p>
 * }
 * <p>
 * Address.java
 * <p>
 * package com.gong.springmvc.entities;
 * <p>
 * public class Address {
 * private String city;
 * private String street;
 * public String getCity() {
 * return city;
 * }
 * public void setCity(String city) {
 * this.city = city;
 * }
 * public String getStreet() {
 * return street;
 * }
 * public void setStreet(String street) {
 * this.street = street;
 * }
 * &at;@Override public String toString() {
 * return "Address [city=" + city + ", street=" + street + "]";
 * }
 * <p>
 * }
 * <p>
 * SpringmvcTest.java
 * <p>
 * import com.gong.springmvc.entities.User;
 * @RequestMapping("/springmvc")
 * @Controller public class SpringmvcTest {
 * private static final String SUCCESS = "success";
 * @RequestMapping(value="/testPojo") public String testPojo(User user) {
 * System.out.println("testPojo:"+user);
 * return SUCCESS;
 * }
 * }
 * <p>
 * index.jsp
 *
 * <form action="springmvc/testPojo" method="POST"><br>
 * <span>username:</span><input type="text" name="username"><br>
 * <span>password:</span><input type="password" name="password"><br>
 * <span>email:</span><input type="text" name="password"><br>
 * <span>age:</span><input type="text" name="password"><br>
 * <span>city:</span><input type="text" name="address.city"><br>
 * <span>street:</span><input type="text" name="address.street"><br>
 * <input type="submit" value="submit"><br>
 * </form>
 * <p>
 * 输入相应信息点击提交：
 * 成功为pojo赋值，包括级联属性address。
 */
public class Paging<S> {
    Logger logger = LoggerFactory.getLogger(Paging.class);
    public static final String ORDER_SPLITER = ":";
    Long start = 0L;
    Long count = 10L;
    List<SqlSort.Sort> sort;
    S searchBean;
    String rangeBeginPrefix = "begin_";
    String rangeEndPrefix = "end_";

    public String getRangeBeginPrefix() {
        return rangeBeginPrefix;
    }

    public void setRangeBeginPrefix(String dateTimeRangeBeginPrefix) {
        this.rangeBeginPrefix = dateTimeRangeBeginPrefix;
    }

    public String getRangeEndPrefix() {
        return rangeEndPrefix;
    }

    public void setRangeEndPrefix(String dateTimeRangeEndPrefix) {
        this.rangeEndPrefix = dateTimeRangeEndPrefix;
    }

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public List<SqlSort.Sort> getSort() {
        return sort;
    }

    public void setSort(List<SqlSort.Sort> sort) {
        this.sort = sort;
    }

    public S getSearchBean() {
        return searchBean;
    }

    public void setSearchBean(S searchBean) {
        this.searchBean = searchBean;
    }

    public <T> ActiveRecord<T> toActiveRecordParams(ActiveRecord<T> ar, Set<String> validFields) {
        BeanMap dest = new BeanMap(searchBean);
        Map<String, MutablePair<Object, Object>> range = new HashMap<>(16);

        Map<String, String> fields = validFields.stream().collect(Collectors.toMap(this::unifyField, y -> y, (x, y) -> x));

        dest.forEach((k, v) -> {

            String key = unifyField(((String) k));

            if (((v instanceof Date)  ||  (v instanceof Number) || (v instanceof String) )
                            && (hasKey(fields.keySet(), key, rangeBeginPrefix) ||
                            hasKey(fields.keySet(), key, rangeEndPrefix))) {

                if (hasKey(fields.keySet(), key, rangeBeginPrefix)) {
                    String keyWithoutPrefix = key.substring(rangeBeginPrefix.length());
                    String originalKey = fields.get(keyWithoutPrefix);
                    pushLeft(range, originalKey, v);
                } else if (hasKey(fields.keySet(), key, rangeEndPrefix)) {
                    String keyWithoutPrefix = key.substring(rangeEndPrefix.length());
                    String originalKey = fields.get(keyWithoutPrefix);
                    pushRight(range, originalKey, v);
                }

            } else if (fields.isEmpty() || fields.containsKey(key)) {
                String originalKey = fields.get(key);

                if (v instanceof Number || v instanceof Boolean || v instanceof Enum) {
                    ar.eq(originalKey, v);
                } else if (v instanceof Object[] || v instanceof Collection) {
                    //检查不为空
                    if((v instanceof Object[] && ((Object[]) v).length == 0) ||
                            (v instanceof Collection && ((Collection<?>) v).isEmpty())) {
                        return;
                    }
                    ar.in(originalKey, v);
                } else if (v instanceof String) {
                    String s = (String) v;
                    if (s.endsWith("%") || s.endsWith("*")) {
                        ar.startWith(originalKey, s.substring(0, s.length() - 1));
                    } else if (s.startsWith("%") || s.startsWith("*")) {
                        ar.like(originalKey, s.substring(1));
                    } else {
                        ar.eq(originalKey, s);
                    }
                }
            }

        });

        range(range, ar);

        if (start != null && count != null) {
            ar.limit(start.intValue(), count.intValue());
        }

        if (sort != null) {
            this.sort.forEach(sort1 -> {
                String fieldName = propertyNameToFieldName(unifyField(sort1.getProperty()), fields);
                if (fieldName == null) {
                    logger.error("Invalid sort field: {}", sort1.getProperty());
                   // throw new IllegalArgumentException("Invalid sort field: " + sort1.getProperty());
                }else  if (sort1.getOrder().equals(ASC)) {
                            ar.asc(fieldName);
                        } else {
                            ar.desc(fieldName);
                        }
                    }
            );
        }

        return ar;
    }

    String propertyNameToFieldName(String propertyName, Map<String, String> validFields){
        return validFields.get(propertyName);
    }


    String unifyField(String s) {
        if (s.startsWith(rangeBeginPrefix)) {
            return rangeBeginPrefix + s.substring(rangeBeginPrefix.length()).toLowerCase().replace("_", "");
        } else if (s.startsWith(rangeEndPrefix)) {
            return rangeEndPrefix + s.substring(rangeEndPrefix.length()).toLowerCase().replace("_", "");
        }
        return s.toLowerCase().replace("_", "");
    }


    private boolean hasKey(Set<String> validFields, String key, String prefix) {
        if (!key.startsWith(prefix)) {
            return false;
        }
        String keyWithoutPrefix = key.substring(prefix.length());
        return validFields.contains(keyWithoutPrefix);
    }

    private <T> void range(Map<String, MutablePair<Object, Object>> range, ActiveRecord<T> ar) {
        range.forEach((k, p) -> {
            if (p.getLeft() != null && p.getRight() != null) {
                if(p.getLeft().equals(p.getRight())) {
                    if(p.getLeft() instanceof Date) {
                        //如果没有设置时间
                        // 例如：2023-10-01 00:00:00 和 2023-10-01 23:59:59
                        // 这种情况，实际上是一个范围
                        Date d = (Date) p.getLeft();
                        if(d.getHours() == 0 && d.getMinutes() == 0 && d.getSeconds() == 0) {
                            // 如果是00:00:00，表示当天的开始时间
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(d);
                            calendar.add(Calendar.DATE, 1);
                            ar.greaterOrEqualThan(k, p.left)
                                    .smallerThan(k, calendar.getTime());
                        } else {
                            ar.between(k, p.left, p.right);
                        }

                    } else {
                        ar.eq(k, p.left);
                    }
                } else {
                    ar.between(k, p.left, p.right);
                }
            } else if (p.left != null) {
                ar.greaterOrEqualThan(k, p.left);
            } else if (p.right != null) {
                ar.smallerOrEqualThan(k, p.right);
            }
        });
    }

    private <L, R> void pushLeft(Map<String, MutablePair<L, R>> range, String key, L v) {
        MutablePair<L, R> pair = range.getOrDefault(key, new MutablePair<>());
        pair.setLeft(v);
        range.putIfAbsent(key, pair);
    }

    private <L, R> void pushRight(Map<String, MutablePair<L, R>> range, String key, R v) {
        MutablePair<L, R> pair = range.getOrDefault(key, new MutablePair<>());
        pair.setRight(v);
        range.putIfAbsent(key, pair);
    }



    /**
     * 排序 sortBy=abc:asc,def:desc
     *
     * @param sortBy
     */
    public void parseSort(String sortBy) {
        if (!StringUtils.isEmpty(sortBy)) {
            sort = new ArrayList<>();
            Arrays.stream(sortBy.split(",")).forEach(sub -> {
                if (sub.contains(ORDER_SPLITER)) {
                    String[] pair = sub.split(ORDER_SPLITER);
                    sort.add(new SqlSort.Sort(pair[0], pair[1]));
                } else {
                    sort.add(new SqlSort.Sort(sub, ASC));
                }
            });
        }

    }
}
