package org.example.common.constant;

/**
 * Redis Key 常量类
 * 公众号：马丁玩编程，回复：加群，添加马哥微信（备注：link）获取项目资料
 */
public class RedisKeyConstant {

    /**
     * 短链接跳转前缀 Key
     */
    public static final String GOTO_SHORT_LINK_KEY = "short-link:goto:%s";


    /**
     * 短链接跳转锁前缀 Key
     */
    public static final String LOCK_GOTO_SHORT_LINK_KEY = "short-link:lock:goto:%s";

    public static final String GOTO_IS_NULL_SHORT_LINK_KEY = "short-link:is-null:goto_%s";

}
