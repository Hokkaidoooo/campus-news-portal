package com.citywy.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Notice implements Serializable {
    private static final long serialVersionUID = 1L;

    private String NoticeID;

    private String NoticeName;

    private String NoticeContent;

    private Date createTime;
}
