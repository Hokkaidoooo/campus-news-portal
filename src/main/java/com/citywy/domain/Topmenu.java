package com.citywy.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Topmenu implements Serializable {
    private static final long serialVersionUID = 1L;

    private String Id;

    private String TopmenuName;

    private String TopmenuContent;

    private Date createTime;
}
