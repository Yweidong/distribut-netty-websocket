package com.thought.monkey.entity;

import lombok.Data;

/**
 * @Auther: wei_tung
 * @Description: 1371690483@qq.com
 *
 * 通知的bean
 *
 */
@Data
public class Notification<T> {

    public static final int SESSION_ON = 10; //上线通知
    public static final int SESSION_OFF = 20; //下线通知

    public static final int CONNECT_FINISHED = 30; //节点啊链接成功


    private int type;

    private T data;

    public Notification(T data) {
        this.data = data;
    }

    public Notification() {

    }

    //包装 处理用户的下线
    public static Notification<ContentWrapper> wrapContent(String content) {
        ContentWrapper contentWrapper = new ContentWrapper();
        contentWrapper.setContent(content);
        return new Notification<>(contentWrapper);
    }

    @Data
    public static class ContentWrapper {
        String content;
    }

    public String getWrapContent() {
        if (data instanceof ContentWrapper) {
            return ((ContentWrapper) data).getContent();
        }
        return null;
    }

    //包装，处理用户的上线
    public static Notification<NodeCacheWrapper> wrapNode(SessionNodeCache sessionNodeCache) {
        NodeCacheWrapper nodeCacheWrapper = new NodeCacheWrapper();
        nodeCacheWrapper.setSessionNodeCache(sessionNodeCache);
        return new Notification<>(nodeCacheWrapper);
    }

    @Data
    public static class NodeCacheWrapper {
        SessionNodeCache sessionNodeCache;
    }

    public SessionNodeCache getWrapNode() {
        if (data instanceof NodeCacheWrapper) {
            return ((NodeCacheWrapper) data).getSessionNodeCache();
        }
        return null;
    }



}
