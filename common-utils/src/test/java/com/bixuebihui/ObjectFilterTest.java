package com.bixuebihui;

import junit.framework.TestCase;
import org.apache.commons.lang3.time.StopWatch;

import java.util.ArrayList;
import java.util.List;

public class ObjectFilterTest extends TestCase {

    static  class TestObject {
        private String name;
        private int age;
        private String address;
        private String phone;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }

    public void testFilterObject() {
        TestObject obj = new TestObject();
        obj.setName("test");
        obj.setAge(10);
        obj.setAddress("address");
        obj.setPhone("123456");

        List<String> excludeFields = new ArrayList<>();
        excludeFields.add("name");

        StopWatch sw = new StopWatch();
        sw.start();
        List<Object> result = ObjectFilter.filterObject(obj, excludeFields);
        sw.stop();
        System.out.println("first filterObject cost: " + sw.getNanoTime() + "ns");

        System.out.printf("result: %s\n", result);

        sw.reset();
        sw.start();
        List<Object> result1 = ObjectFilter.filterObject(obj, excludeFields);
        sw.stop();
        System.out.println("second filterObject cost: " + sw.getNanoTime() + "ns");



        assertEquals(3, result1.size());
        assertEquals("address", result1.get(0));
        assertEquals(10, result1.get(1));
        assertEquals("123456", result1.get(2));

    }
}
