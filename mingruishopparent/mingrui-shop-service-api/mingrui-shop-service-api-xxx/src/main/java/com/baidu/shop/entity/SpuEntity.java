package com.baidu.shop.entity;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "tb_spu")
@ApiModel(value = "spu是一个抽象性商品的描述")
public class SpuEntity {
    @Id
    private Integer id;
    private String title;
    private String subTitle;
    private Integer cid1;
    private Integer cid2;
    private Integer cid3;
    private Integer brandId;
    private Integer saleable;
    private Integer valid;
    private Date createTime;
    private Date lastUpdateTime;
}
