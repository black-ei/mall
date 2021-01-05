package com.baidu.shop.dto;

import com.baidu.shop.base.BaseDTO;
import com.baidu.shop.validate.group.MingruiOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@ApiModel(value = "spu数据传输DTO")
public class SpuDTO extends BaseDTO{

    @ApiModelProperty(value = "主键",example = "1")
    @NotNull(message = "主键不能为空",groups = {MingruiOperation.update.class})
    private Integer id;
    @ApiModelProperty(value = "标题")
    @NotBlank(message = "标题不能为空",groups = {MingruiOperation.update.class,MingruiOperation.add.class})
    private String title;
    @ApiModelProperty(value = "子标题")
    private String subTitle;
    @ApiModelProperty(value = "1级类目id", example = "1")
    @NotNull(message = "1级类目id不能为空",groups = {MingruiOperation.add.class,MingruiOperation.update.class})
    private Integer cid1;
    @ApiModelProperty(value = "1级类目id", example = "1")
    @NotNull(message = "2级类目id不能为空",groups = {MingruiOperation.add.class,MingruiOperation.update.class})
    private Integer cid2;
    @ApiModelProperty(value = "1级类目id", example = "1")
    @NotNull(message = "3级类目id不能为空",groups = {MingruiOperation.add.class,MingruiOperation.update.class})
    private Integer cid3;
    @ApiModelProperty(value = "商品所属品牌id",example = "1")
    @NotNull(message = "商品所属品牌id不能为空",groups = {MingruiOperation.add.class,MingruiOperation.update.class})
    private Integer brandId;
    @ApiModelProperty(value = "是否上架,0下架,1上架",example = "1")//不需要验证,新增时直接设置默认值
    private Integer saleable;
    //不需要验证,新增时直接设置默认值
    @ApiModelProperty(value = "是否有效，0失效，1有效", example = "1")
    private Integer valid;
    //不需要验证,新增时直接设置默认值
    @ApiModelProperty(value = "添加时间")
    private Date createTime;
    //不需要验证,新增时直接设置默认值,修改时使用java代码赋值
    @ApiModelProperty(value = "最后修改时间")
    private Date lastUpdateTime;

}
