package org.example.service;

/**
 * URL 标题接口层
 */
public interface UrlTitleService {

    /**
     * 根据 URL 获取标题
     */
    String getTitleByUrl(String url);
}