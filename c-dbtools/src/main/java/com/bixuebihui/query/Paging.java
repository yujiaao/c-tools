package com.bixuebihui.query;

import com.bixuebihui.db.ActiveRecord;
import com.bixuebihui.jdbc.SqlSort;
import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.*;

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
    Long start=0L;
    Long count=10L;
    List<SqlSort.Sort> sort;
    S searchBean;
    String dateTimeRangeBeginPrefix = "b";
    String dateTimeRangeEndPrefix = "e";

    public String getDateTimeRangeBeginPrefix() {
        return dateTimeRangeBeginPrefix;
    }

    public void setDateTimeRangeBeginPrefix(String dateTimeRangeBeginPrefix) {
        this.dateTimeRangeBeginPrefix = dateTimeRangeBeginPrefix;
    }

    public String getDateTimeRangeEndPrefix() {
        return dateTimeRangeEndPrefix;
    }

    public void setDateTimeRangeEndPrefix(String dateTimeRangeEndPrefix) {
        this.dateTimeRangeEndPrefix = dateTimeRangeEndPrefix;
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
        Map<String, MutablePair<Object, Object>> dateRange = new HashMap<>(16);
        dest.forEach((k, v) -> {
            String key = ((String) k).toLowerCase();
            if (validFields.isEmpty() ||  validFields.contains(key)) {
                if (v instanceof Number || v instanceof Boolean || v instanceof Enum) {
                    ar.eq(key, v);
                } else if (v instanceof Object[] || v instanceof Collection) {
                    ar.in(key, v);
                } else if (v instanceof String) {
                    ar.like(key, (String) v);
                }
            }
            if (v instanceof Date) {
                if (hasKey(validFields, key, dateTimeRangeBeginPrefix)) {
                    String keyWithoutPrefix = key.substring(dateTimeRangeBeginPrefix.length());
                    pushLeft(dateRange, keyWithoutPrefix, v);
                } else if (hasKey(validFields, key, dateTimeRangeEndPrefix)) {
                    String keyWithoutPrefix = key.substring(dateTimeRangeEndPrefix.length());
                    pushRight(dateRange, keyWithoutPrefix, v);
                }
            }
        });

        range(dateRange, ar);

        if(start!=null && count!=null) {
            ar.limit(start.intValue(), count.intValue());
        }

        if(sort!=null) {
            this.sort.forEach(sort1 -> {

                        if (sort1.getOrder().equals(ASC)) {
                            ar.asc(sort1.getProperty());
                        } else {
                            ar.desc(sort1.getProperty());
                        }
                    }
            );
        }

        return ar;
    }


    private boolean hasKey(Set<String> validFields, String key, String prefix) {
        if (!key.startsWith(prefix)) {
            return false;
        }
        String keyWithoutPrefix = key.substring(prefix.length());
        return validFields.contains(keyWithoutPrefix);
    }

    private <T> void range(Map<String, MutablePair<Object, Object>> dateRange, ActiveRecord<T> ar) {
        dateRange.forEach((k, p) -> {
            if (p.getLeft() != null && p.getRight() != null) {
                ar.between(k, p.left, p.right);
            } else if (p.left != null) {
                ar.greaterOrEqualThan(k, p.left);
            } else if (p.right != null) {
                ar.smallerOrEqualThan(k, p.right);
            }
        });
    }

    private <L, R> void pushLeft(Map<String, MutablePair<L, R>> dateRange, String key, L v) {
        MutablePair<L, R> pair = dateRange.getOrDefault(key, new MutablePair<>());
        pair.setLeft(v);
        dateRange.putIfAbsent(key, pair);
    }

    private <L, R> void pushRight(Map<String, MutablePair<L, R>> dateRange, String key, R v) {
        MutablePair<L, R> pair = dateRange.getOrDefault(key, new MutablePair<>());
        pair.setRight(v);
        dateRange.putIfAbsent(key, pair);
    }

}
