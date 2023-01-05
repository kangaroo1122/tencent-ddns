package com.kangaroohy.tencent.ddns.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 类 Status 功能描述：<br/>
 *
 * @author kangaroo hy
 * @version 0.0.1
 * @date 2022/11/7 16:55
 */
@Data
public class Status implements Serializable {
    private static final long serialVersionUID = 2887277754622292621L;

    private String code;

    private String message;

    private Date created_at;
}
