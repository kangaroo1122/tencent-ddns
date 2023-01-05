package com.kangaroohy.tencent.ddns.service.impl;

import com.alibaba.fastjson.JSON;
import com.kangaroohy.tencent.ddns.config.DdnsToken;
import com.kangaroohy.tencent.ddns.entity.RecordList;
import com.kangaroohy.tencent.ddns.entity.RecordModify;
import com.kangaroohy.tencent.ddns.enums.RecordType;
import com.kangaroohy.tencent.ddns.service.IIpAddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 类 IpAddressServiceImpl 功能描述：<br/>
 *
 * @author kangaroo hy
 * @version 0.0.1
 * @date 2022/11/7 14:54
 */
@Service
@Slf4j
public class IpAddressServiceImpl implements IIpAddressService {
    private static final String RECORD_LIST = "https://dnsapi.cn/Record.List";

    private static final String RECORD_DDNS = "https://dnsapi.cn/Record.Ddns";

    private static final String UserAgent = "kangaroohy ddns/1.0.0(326170945@qq.com)";

    @Autowired
    private DdnsToken ddnsToken;

    @Autowired
    private RestTemplate restTemplate;

    private Map<String, String> currentDdnsIp = new ConcurrentHashMap<>();

    /**
     * 3分钟执行一次
     */
    @Scheduled(cron = "${ddns.tencent.cron:0 0/3 * * * ?}")
    private void configureTasks() {
        autoRefresh();
    }

    /**
     * 强制刷新，无关缓存ip
     */
    @Override
    public String refresh(String subDomainName) {
        List<DdnsToken.Domain> domainList = ddnsToken.getDomain();
        DdnsToken.Domain domain = domainList.stream().filter(item -> item.getSubDomainName().equals(subDomainName)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("未找到对应的二级域名配置信息：" + subDomainName));
        String currentIp = getIpAddress(domain);
        this.updateDomainDnsRecord(this.getDomainDnsRecord(domain), domain, currentIp);
        return currentIp;
    }

    @Override
    public void autoRefresh() {
        List<DdnsToken.Domain> domainList = ddnsToken.getDomain();
        for (DdnsToken.Domain domain : domainList) {
            String currentIp = getIpAddress(domain);
            if (currentDdnsIp.containsKey(domain.getSubDomainName())) {
                if (!currentDdnsIp.get(domain.getSubDomainName()).equals(currentIp)) {
                    log.info("当前本地IP：{}，解析IP：{}，不一致，需更新", currentIp, currentDdnsIp.get(domain.getSubDomainName()));
                    // 刷新解析记录
                    this.updateDomainDnsRecord(this.getDomainDnsRecord(domain), domain, currentIp);
                }
            } else {
                RecordList.Record domainDnsRecord = this.getDomainDnsRecord(domain);
                if (!domainDnsRecord.getValue().equals(currentIp)) {
                    log.info("当前本地IP：{}，解析IP：{}，不一致，需更新", currentIp, domainDnsRecord.getValue());
                    // 刷新解析记录
                    this.updateDomainDnsRecord(domainDnsRecord, domain, currentIp);
                }
            }
        }
    }

    private RecordList.Record getDomainDnsRecord(DdnsToken.Domain domain) {
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("record_type", domain.getRecordType().getType());
        params.add("sub_domain", domain.getSubDomainName());
        String forObject = postForObject(RECORD_LIST, domain, params);
        RecordList recordList = JSON.parseObject(forObject, RecordList.class);
        if ("1".equals(recordList.getStatus().getCode()) && !recordList.getRecords().isEmpty()) {
            return recordList.getRecords().get(0);
        }
        throw new RuntimeException("获取解析记录失败！");
    }

    private RecordModify updateDomainDnsRecord(RecordList.Record dns, DdnsToken.Domain domain, String newIp) {
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("record_id", dns.getId());
        params.add("sub_domain", domain.getSubDomainName());
        params.add("record_line", dns.getLine());
        params.add("record_line_id", dns.getLine_id());
        params.add("value", newIp);
        String forObject = postForObject(RECORD_DDNS, domain, params);
        RecordModify modify = JSON.parseObject(forObject, RecordModify.class);
        log.info("修改解析结果：{}", modify);
        if ("1".equals(modify.getStatus().getCode())) {
            currentDdnsIp.put(domain.getSubDomainName(), newIp);
        }
        return modify;
    }

    private String postForObject(String url, DdnsToken.Domain domain, MultiValueMap<String, Object> params) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        params.add("login_token", ddnsToken.getTokenId() + "," + ddnsToken.getToken());
        params.add("format", "json");
        params.add("domain", domain.getDomainName());
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        return response.getBody();
    }

    private String getIpAddress(DdnsToken.Domain domain) {
        String currentIp;
        if (domain.getRecordType().equals(RecordType.A)) {
            currentIp = this.getIpv4Address();
        } else {
            currentIp = this.getIpv6Address();
        }
        if (currentIp == null) {
            throw new IllegalArgumentException("获取本地IP失败");
        }
        return currentIp;
    }

    private String getIpv4Address() {
        return restTemplate.getForObject(ddnsToken.getIpv4QueryDomain(), String.class);
    }

    private String getIpv6Address() {
        return restTemplate.getForObject(ddnsToken.getIpv6QueryDomain(), String.class);
    }
}
