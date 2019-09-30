package com.sczp.system.moudl;

public enum EventStatus {
    /**
     * 表面服务器已经接受到了消息
     */
    NEW,
    /**消费成功
     */
    PUBLISHED,
    /**
     * 消费失败
     */
    PUBLISHED_FAIL,

}
