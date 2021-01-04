package com.baidu.shop.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "tb_spec_param")
public class SpecParamEntity {

    @Id
    private Integer id;
    private Integer cid;
    private Integer groupId;
    private String name;
    /*numeric是mysql关键字 需要加 ``
      声明为普通字段,否则sql无法执行成功
    * */
    @Column(name = "`numeric`")
    private Boolean numeric;
    private String unit;
    private Boolean generic;
    private Boolean searching;
    private String segments;
}
