package com.github.hornta.trollskogen;

import org.junit.Test;

import static org.junit.Assert.*;

public class PrefixMatcherTest {

  @Test
  public void test() {
    PrefixMatcher prefixMatcher = new PrefixMatcher();
    prefixMatcher.insert("a");
    prefixMatcher.insert("ab");
    prefixMatcher.insert("abc");
    prefixMatcher.insert("abcd");
    prefixMatcher.insert("a11111111abcdddddddddd3d");

    assertEquals(prefixMatcher.find("a").size(), 5);
    assertEquals(prefixMatcher.find("ab").size(), 3);
    assertEquals(prefixMatcher.find("abc").size(), 2);
    assertEquals(prefixMatcher.find("abcd").size(), 1);
    assertEquals(prefixMatcher.find("abcde").size(), 0);
  }

  @Test
  public void delete() {
    PrefixMatcher prefixMatcher = new PrefixMatcher();
    prefixMatcher.insert("a");
    prefixMatcher.insert("ab");
    prefixMatcher.delete("ab");

    assertEquals(prefixMatcher.find("a").size(), 1);
  }

  @Test
  public void delete2() {
    PrefixMatcher prefixMatcher = new PrefixMatcher();
    prefixMatcher.insert("a");
    prefixMatcher.insert("ab");
    prefixMatcher.delete("a");

    assertEquals(prefixMatcher.find("a").size(), 1);
  }
}
