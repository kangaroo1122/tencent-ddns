package com.kangaroohy.tencent.ddns.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 类 RecordList 功能描述：<br/>
 *
 * @author kangaroo hy
 * @version 0.0.1
 * @date 2022/11/7 16:19
 */
@Data
public class RecordModify implements Serializable {
    private static final long serialVersionUID = 6335480889798043197L;

    private Status status;

    private Record record;

    @Data
    public static class Record implements Serializable {

        private static final long serialVersionUID = 3801971988536451136L;

        private String id;

        private String name;

        private String type;

        private String value;

        private String status;

    }
}
