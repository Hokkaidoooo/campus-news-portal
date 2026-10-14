package com.citywy.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Leftmenu implements Serializable {
    private static final long serialVersionUID = 1L;

    private String LeftmenuID;

    private String LeftmenuName;

    private String LeftmenuContent;

    private Date createTime;
}
