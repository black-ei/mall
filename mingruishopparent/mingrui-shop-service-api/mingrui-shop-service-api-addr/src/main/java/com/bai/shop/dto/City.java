package com.bai.shop.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Table;

@Data
@Table(name = "t_city")
@ApiModel(value = "城市表")
public class City {

  @ApiModelProperty(value = "城市Id")
  private Integer id;
  @ApiModelProperty(value = "城市名字")
  private String name;
  @ApiModelProperty(value = "城市父Id")
  private Integer parentId;
  @ApiModelProperty(value = "城市级别")
  private Integer level;
}
