package com.github.hornta.trollskogen;

import java.util.*;
import java.util.stream.Collectors;

public class PrefixMatcher {
  private static final String SYMBOLS = "abcdefghijklmnopqrstuvwxyz_0123456789";
  private static final int NUMBER_OF_SYMBOLS = 37;
  private static final Map<String, Integer> symbolIndicies = Arrays.stream(SYMBOLS.split("")).collect(Collectors.toMap(x -> x, SYMBOLS::indexOf));

  private boolean isLeaf = true;
  private PrefixMatcher[] children = new PrefixMatcher[NUMBER_OF_SYMBOLS];
  private String value;

  private static int getSymbolIndex(char character) {
    return symbolIndicies.get(String.valueOf(character));
  }

  private static boolean haveChildren(PrefixMatcher node) {
    for(int i = 0; i < NUMBER_OF_SYMBOLS; ++i) {
      if(node.children[i] != null) {
        return true;
      }
    }
    return false;
  }

  private static boolean recursivelyDelete(PrefixMatcher parent, int parentChildIndex, PrefixMatcher node, String key) {
    if (node == null) {
      return false;
    }

    if(key.length() > 0) {
      int symbolIndex = getSymbolIndex(key.charAt(0));
      PrefixMatcher child = node.children[symbolIndex];
      if(
        recursivelyDelete(node, symbolIndex, child, key.substring(1)) &&
          !node.isLeaf) {
        if(!haveChildren(node) && parent != null && node.value == null) {
          parent.children[parentChildIndex] = null;
          return true;
        } else {
          return false;
        }
      }
    }

    if(key.length() == 0) {
      if(!node.isLeaf) {
        node.value = null;
        return true;
      }

      if(haveChildren(node)) {
        node.isLeaf = false;
        return false;
      } else {
        parent.children[parentChildIndex] = null;
        return true;
      }
    }

    return false;
  }

  private static List<String> recursivelyGetItems(PrefixMatcher node, List<String> items) {
    if(node.value != null) {
      items.add(node.value);
    }
    for(int i = 0; i < NUMBER_OF_SYMBOLS; ++i) {
      if(node.children[i] != null) {
        items = recursivelyGetItems(node.children[i], items);
      }
    }

    return items;
  }

  public void insert(String key) {
    String lowerCaseKey = key.toLowerCase(Locale.ENGLISH);
    PrefixMatcher current = this;

    for(int level = 0; level < lowerCaseKey.length(); ++level) {
      int index = getSymbolIndex(lowerCaseKey.charAt(level));
      if(current.children[index] == null) {
        current.children[index] = new PrefixMatcher();
        current.isLeaf = false;
      }

      current = current.children[index];
    }

    current.value = key;

    if(!haveChildren(current)) {
      current.isLeaf = true;
    }
  }

  public boolean delete(String key) {
    key = key.toLowerCase(Locale.ENGLISH);
    return recursivelyDelete(null, 0, this, key);
  }

  public List<String> find(String key) {
    key = key.toLowerCase(Locale.ENGLISH);
    List<String> items = new ArrayList<>();
    PrefixMatcher node = this;
    for(int level = 0; level < key.length(); ++level) {
      int index = getSymbolIndex(key.charAt(level));
      node = node.children[index];
      if(node == null) {
        return items;
      }
    }

    return recursivelyGetItems(node, items);
  }

  public boolean isEmpty() {
    return !haveChildren(this);
  }
}
