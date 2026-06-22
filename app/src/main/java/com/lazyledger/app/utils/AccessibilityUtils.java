package com.lazyledger.app.utils;

import android.view.accessibility.AccessibilityNodeInfo;
import java.util.ArrayList;
import java.util.List;

public class AccessibilityUtils {

    /**
     * Finds the first node containing the specific text.
     */
    public static AccessibilityNodeInfo findNodeByText(AccessibilityNodeInfo root, String text) {
        if (root == null) return null;
        List<AccessibilityNodeInfo> nodes = root.findAccessibilityNodeInfosByText(text);
        if (nodes != null && !nodes.isEmpty()) {
            return nodes.get(0);
        }
        return null;
    }

    /**
     * Traverses all nodes to collect text content. Useful for analyzing page structure.
     */
    public static void printAllTextNodes(AccessibilityNodeInfo node) {
        if (node == null) return;
        if (node.getText() != null) {
            android.util.Log.d("A11yText", "Found text: " + node.getText().toString());
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            printAllTextNodes(node.getChild(i));
        }
    }

    /**
     * Extracts all text nodes into a list.
     */
    public static void extractAllTexts(AccessibilityNodeInfo node, List<String> texts) {
        if (node == null) return;
        if (node.getText() != null) {
            texts.add(node.getText().toString());
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            extractAllTexts(node.getChild(i), texts);
        }
    }
}
