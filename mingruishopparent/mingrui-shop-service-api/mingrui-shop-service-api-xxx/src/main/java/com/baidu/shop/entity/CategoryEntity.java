package com.baidu.shop.entity;

import com.baidu.shop.validate.group.MingruiOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.aspectj.bridge.IMessage;
import org.omg.CORBA.INTERNAL;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@ApiModel(value = "商品类目类")
@Data
@Table(name = "tb_category")
public class CategoryEntity {

    @Id
    @ApiModelProperty(value = "类目主键",example = "1")
    @NotNull(message = "ID不能为NULL",groups = {MingruiOperation.update.class})
    private Integer id;

    @ApiModelProperty(value = "类目名称")
    @NotBlank(message = "类目名称不能为NULL",groups = {MingruiOperation.update.class,MingruiOperation.add.class})
    private String name;

    @ApiModelProperty(value = "父类目id,0为顶级目录",example = "1")
    @NotNull(message =  "父级ID不能为NULl",groups = {MingruiOperation.add.class})
    private Integer parentId;

    @ApiModelProperty(value = "是否为父节点，0为否，1为是",example = "1")
    @NotNull(message = "是否为父级节点不能为NULL",groups = {MingruiOperation.add.class})
    private Integer isParent;

    @ApiModelProperty(value = "排序指数,越小越往前",example = "1")
    @NotNull(message = "排序指数不能为NULL",groups = {MingruiOperation.add.class})
    private Integer sort;

}
