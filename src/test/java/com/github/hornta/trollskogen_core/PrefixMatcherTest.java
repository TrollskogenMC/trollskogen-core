package com.github.hornta.trollskogen_core;

import org.junit.Assert;
import org.junit.Test;

public class PrefixMatcherTest {

  @Test
  public void test() {
    PrefixMatcher prefixMatcher = new PrefixMatcher();
    prefixMatcher.insert("a");
    prefixMatcher.insert("ab");
    prefixMatcher.insert("abc");
    prefixMatcher.insert("abcd");
    prefixMatcher.insert("a11111111abcdddddddddd3d");

    Assert.assertEquals(prefixMatcher.find("a").size(), 5);
    Assert.assertEquals(prefixMatcher.find("ab").size(), 3);
    Assert.assertEquals(prefixMatcher.find("abc").size(), 2);
    Assert.assertEquals(prefixMatcher.find("abcd").size(), 1);
    Assert.assertEquals(prefixMatcher.find("abcde").size(), 0);
  }

  @Test
  public void delete() {
    PrefixMatcher prefixMatcher = new PrefixMatcher();
    prefixMatcher.insert("a");
    prefixMatcher.insert("ab");
    prefixMatcher.delete("ab");

    Assert.assertEquals(prefixMatcher.find("a").size(), 1);
  }

  @Test
  public void delete2() {
    PrefixMatcher prefixMatcher = new PrefixMatcher();
    prefixMatcher.insert("a");
    prefixMatcher.insert("ab");
    prefixMatcher.delete("a");

    Assert.assertEquals(prefixMatcher.find("a").size(), 1);
  }

  @Test
  public void testInsertDuplicate() {
    PrefixMatcher prefixMatcher = new PrefixMatcher();
    prefixMatcher.insert("a");
    prefixMatcher.insert("a");
    prefixMatcher.insert("a");
    prefixMatcher.insert("a");

    Assert.assertEquals(prefixMatcher.find("a").size(), 1);
  }
}
