package com.kangaroohy.tencent.ddns.enums;

import lombok.Getter;

/**
 * 类 RecordType 功能描述：<br/>
 *
 * @author kangaroo hy
 * @version 0.0.1
 * @date 2022/11/7 23:44
 */
@Getter
public enum RecordType {
    /**
     * 记录类型
     */
    A("A"),
    AAAA("AAAA");

    private final String type;

    RecordType(String type) {
        this.type = type;
    }
}
