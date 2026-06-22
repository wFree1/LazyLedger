package com.lazyledger.app.service;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.util.Log;
import com.lazyledger.app.db.entity.Transaction;
import com.lazyledger.app.repository.TransactionRepository;
import com.lazyledger.app.utils.AccessibilityUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoLedgerService extends AccessibilityService {

    private static final String TAG = "AutoLedgerService";
    private TransactionRepository repository;
    
    private long lastTransactionTime = 0;
    private static final long DEBOUNCE_INTERVAL = 5000;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "Accessibility Service Connected");
        repository = new TransactionRepository(getApplication());
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode == null) return;

        CharSequence packageName = event.getPackageName();
        if (packageName == null) return;

        if (packageName.toString().equals("com.tencent.mm")) {
            processPaymentPage(rootNode, "WeChat");
        } else if (packageName.toString().equals("com.eg.android.AlipayGphone")) {
            processPaymentPage(rootNode, "Alipay");
        }
    }

    private void processPaymentPage(AccessibilityNodeInfo rootNode, String platform) {
        List<String> allTexts = new ArrayList<>();
        AccessibilityUtils.extractAllTexts(rootNode, allTexts);

        boolean isPaymentSuccess = false;
        boolean isRedPacket = false;
        boolean isTransferIncome = false;
        boolean isDetailPage = false;

        for (String text : allTexts) {
            String trimmed = text.trim();
            if (trimmed.equals("支付成功")) isPaymentSuccess = true;
            if (trimmed.contains("已存入零钱")) isRedPacket = true; // 只要存入零钱，肯定是收入
            if (trimmed.contains("转账收款")) isTransferIncome = true;
            
            // 必须要有详情页的特征词，防止聊天记录里的只言片语触发
            if (trimmed.contains("支付方式") || 
                trimmed.contains("交易单号") || 
                trimmed.contains("转账时间") || 
                trimmed.contains("已存入零钱") ||
                trimmed.equals("完成")) {
                isDetailPage = true;
            }
        }

        // 如果既不是支付成功，也没存入零钱，或者根本不是详情页，就跳过
        if (!isDetailPage || (!isPaymentSuccess && !isRedPacket && !isTransferIncome)) {
            return; 
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTransactionTime < DEBOUNCE_INTERVAL) {
            return;
        }

        Log.d(TAG, "========= Detected " + platform + " Transaction =========");
        
        double amount = extractAmount(allTexts);
        if (amount <= 0) return; 

        String merchantName = extractMerchant(allTexts);
        int type = 0; // 0 for Expense, 1 for Income
        String category = "支出";

        if (isRedPacket || isTransferIncome) {
            type = 1; // 真正存入零钱了才是收入！
            category = "收入";
            if (merchantName.isEmpty()) merchantName = "收到钱";
        } else {
            type = 0; // 支出
            category = "消费";
            if (merchantName.isEmpty()) merchantName = "未知商户 (" + platform + ")";
        }

        lastTransactionTime = currentTime;
        saveTransaction(merchantName, amount, platform, type, category);
    }

    private double extractAmount(List<String> texts) {
        Pattern amountPattern = Pattern.compile(".*?(\\d+\\.\\d{2}).*");
        for (String text : texts) {
            if (text.length() > 20) continue;
            Matcher matcher = amountPattern.matcher(text);
            if (matcher.matches()) {
                try {
                    return Double.parseDouble(matcher.group(1));
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Failed to parse amount", e);
                }
            }
        }
        return 0.0;
    }

    private String extractMerchant(List<String> texts) {
        for (String text : texts) {
            String trimmed = text.trim();
            if (trimmed.isEmpty() || 
                trimmed.contains("支付成功") || 
                trimmed.contains("完成") ||
                trimmed.contains("微信支付") ||
                trimmed.contains("已存入") ||
                trimmed.contains("红包") ||
                trimmed.contains("发了一个") ||
                trimmed.matches(".*\\d+\\.\\d{2}.*") || 
                trimmed.length() <= 1) { 
                continue;
            }
            return trimmed;
        }
        return "";
    }

    private void saveTransaction(String merchantName, double amount, String platform, int type, String category) {
        long timestamp = System.currentTimeMillis();
        Transaction t = new Transaction(merchantName, amount, timestamp, platform, type, category);
        repository.insert(t);
        Log.d(TAG, "Saved: " + merchantName + " | " + amount + " | Type: " + type + " | Cat: " + category);
    }

    @Override
    public void onInterrupt() { }
}
