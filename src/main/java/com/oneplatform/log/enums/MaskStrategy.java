package com.oneplatform.log.enums;

public enum MaskStrategy {
    FULL,       // ***
    LAST4,      // ************1234 (good for card numbers)
    PARTIAL     // ag***23 (good for usernames/emails if you ever want partial visibility)
}