package com.citywy.domain;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
public class News implements Serializable {
    private static final long serialVersionUID = 1L;

    private String newsID;

    private String newsName;

    private String newsContent;

    private Date createTime;
}
