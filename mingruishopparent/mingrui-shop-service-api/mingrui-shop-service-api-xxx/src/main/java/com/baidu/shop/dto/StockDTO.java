package com.baidu.shop.dto;

import com.baidu.shop.validate.group.MingruiOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@ApiModel(value = "库存传输类")
public class StockDTO {
    @ApiModelProperty(value = "sku主键", example = "1")
    private Long skuId;
    @ApiModelProperty(value = "可秒杀库存", example = "1")
    private Integer seckillStock;
    @ApiModelProperty(value = "秒杀总数量", example = "1")
    private Integer seckillTotal;
    @ApiModelProperty(value = "库存数量", example = "1")
    private Integer stock;
}
