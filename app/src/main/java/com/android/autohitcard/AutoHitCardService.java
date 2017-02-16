package com.android.autohitcard;

import android.accessibilityservice.AccessibilityService;
import android.graphics.Rect;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.io.File;
import java.io.IOException;

public class AutoHitCardService extends AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        AccessibilityNodeInfo sourceInfo = event.getSource();
        if (null == sourceInfo) return;
        clickButton(find(sourceInfo, "工作"));
        scrollDown(find(sourceInfo, "公告"));
        scrollDown(find(sourceInfo, "常用应用"));
        scrollDown(find(sourceInfo, "企业服务"));
        scrollDown(find(sourceInfo, "其他服务"));
        clickButton(find(sourceInfo, "考勤打卡"));
        clickEx(findEx(sourceInfo, "上班打卡"));
    }

    @Override
    public void onInterrupt() {
    }

    private AccessibilityNodeInfo find(AccessibilityNodeInfo nodeInfo, String text) {
        for (AccessibilityNodeInfo node : nodeInfo.findAccessibilityNodeInfosByText(text)) {
            if (text.equals(node.getText())) {
                return node;
            }
        }
        return null;
    }

    private AccessibilityNodeInfo findEx(AccessibilityNodeInfo nodeInfo, String text) {
        for (int i = 0, n = nodeInfo.getChildCount(); i < n; ++i) {
            AccessibilityNodeInfo node = nodeInfo.getChild(i);
            if (null == node) continue;
            if (text.equals(node.getContentDescription())) {
                return node;
            }
            AccessibilityNodeInfo childNode = findEx(node, text);
            if (null != childNode) return childNode;
        }
        return null;
    }

    private boolean scrollDown(AccessibilityNodeInfo nodeInfo) {
        if (null == nodeInfo) return false;
        while ((nodeInfo = nodeInfo.getParent()) != null && !nodeInfo.isScrollable()) ;
        if (null != nodeInfo && nodeInfo.isScrollable()) {
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
            return true;
        }
        return false;
    }

    private boolean clickButton(AccessibilityNodeInfo nodeInfo) {
        if (null == nodeInfo) return false;
        while ((nodeInfo = nodeInfo.getParent()) != null && !nodeInfo.isClickable()) ;
        if (null != nodeInfo && nodeInfo.isClickable()) {
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            return true;
        }
        return false;
    }

    private boolean clickEx(AccessibilityNodeInfo nodeInfo) {
        if(null == nodeInfo) return false;
        Rect rect = new Rect();
        nodeInfo.getBoundsInScreen(rect);
        return sendTap(rect.centerX(), rect.centerY());
    }

    private boolean sendTap(int x, int y) {
        try {
            String cmd = String.format("input tap %d %d\n", x, y);
            ProcessBuilder builder = new ProcessBuilder("sh");
            builder.redirectErrorStream(true);
            builder.directory(new File("/"));
            Process process = builder.start();
            process.getOutputStream().write("su\n".getBytes());
            process.getOutputStream().write(cmd.getBytes());
            process.getOutputStream().flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
