package com.kangaroohy.tencent.ddns.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 类 RecordList 功能描述：<br/>
 *
 * @author kangaroo hy
 * @version 0.0.1
 * @date 2022/11/7 16:19
 */
@Data
public class RecordList implements Serializable {
    private static final long serialVersionUID = 6335480889798043197L;

    private Status status;

    private List<Record> records;

    @Data
    public static class Record implements Serializable {

        private static final long serialVersionUID = 3801971988536451136L;

        private String id;

        private String name;

        private String type;

        private Long ttl;

        private String value;

        private Boolean enabled;

        private String status;

        private String line;

        private String line_id;

        private Integer mx;
    }
}
