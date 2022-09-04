package com.bixuebihui.cache;

import com.bixuebihui.sequence.SequenceUtils;
import junit.framework.TestCase;

public class DictionaryCacheTest extends TestCase {

    public void testGetValue() {
        DictionaryCache cache = new DictionaryCache();
        System.out.println(cache.byId("ziduan.KZ_BM").getValue());
        System.out.println(cache.getOptionList("ziduan", null, null));
        System.out.println(cache.getOptionList("ziduan", null, null));


    }

    public void testGetValueStringParams() {
        DictionaryCache cache = new DictionaryCache();
        DictionaryList list = new DictionaryList();
        System.out.println("abcdef=" + SequenceUtils.getInstance().getNextKeyValue("abcdef", list.getDbHelper()));
        System.out.println("abcd=" + SequenceUtils.getInstance().getNextKeyValue("abcd", list.getDbHelper()));


        System.out.println(cache.byId("sequence.abc"));


    }


}
