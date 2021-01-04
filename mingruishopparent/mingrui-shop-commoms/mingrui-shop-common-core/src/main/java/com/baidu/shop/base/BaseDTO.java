package com.baidu.shop.base;

import com.baidu.shop.validate.group.MingruiOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(value = "BaseDTO用于数据传输,其他dto需要继承此类")
public class BaseDTO {

    @ApiModelProperty(value = "当前页", example = "1")
    @NotNull(message = "当前页不能为空",groups = {MingruiOperation.Search.class})
    private Integer page;

    @ApiModelProperty(value = "每页显示多少条",example = "5")
    @NotNull(message = "显示页数不能为空",groups = {MingruiOperation.Search.class})
    private Integer rows;

    @ApiModelProperty(value = "排序字段")
    private String sort;

    @ApiModelProperty(value = "是否升序")
    private String order;

    @ApiModelProperty(hidden = true)
    public String getOrderByClause(){
        if(null!=sort && !"".equals(sort))return sort + " " + order.replace("false","asc").replace("true","desc");
        return "";
    }

}
