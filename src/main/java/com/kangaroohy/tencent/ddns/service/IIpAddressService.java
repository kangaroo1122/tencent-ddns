package com.kangaroohy.tencent.ddns.service;

import com.kangaroohy.tencent.ddns.entity.DomainListVO;

import java.util.List;

/**
 * 类 IIpAddressService 功能描述：<br/>
 *
 * @author kangaroo hy
 * @version 0.0.1
 * @date 2022/11/7 14:52
 */
public interface IIpAddressService {

    /**
     * 刷新IP解析，无关本地ip缓存
     *
     * @param subDomainName 二级域名
     * @return
     */
    String refresh(String subDomainName);

    /**
     * 刷新IP解析
     */
    void autoRefresh();

    /**
     * 解析列表
     *
     * @return
     */
    List<DomainListVO> findDomainList();
}
