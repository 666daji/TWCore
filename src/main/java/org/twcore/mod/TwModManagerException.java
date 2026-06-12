package org.twcore.mod;

/**
 * 用于表示 TW 模组注册或管理中心内部错误的异常。
 */
public class TwModManagerException extends RuntimeException {
    private final String messageText;

    public TwModManagerException(String messageText) {
        super(messageText);
        this.messageText = messageText;
    }

    public static TwModManagerException of(String modId, int modVersion, int required) {
        String messageText = String.format("Mod '%s' is outdated! Loaded version: %d, required at least: %d. Please update the mod.",
                modId, modVersion, required);

        return new TwModManagerException(messageText);
    }

    public String getMessageText() {
        return messageText;
    }
}