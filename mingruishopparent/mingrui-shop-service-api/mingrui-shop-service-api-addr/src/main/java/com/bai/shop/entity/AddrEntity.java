package com.bai.shop.entity;

import com.baidu.shop.validate.group.MingruiOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Table(name = "t_addr")
@ApiModel(value = "地址表")
public class AddrEntity {
    @ApiModelProperty(value = "主键")
    @Id
    private Integer addrId;
    @NotNull(groups = {MingruiOperation.update.class})
    @ApiModelProperty(value = "用户Id")
    private Integer userId;
    @ApiModelProperty(value = "收货人名称")
    @NotEmpty
    private String addrName;
    @ApiModelProperty(value = "所在地区")
    @NotEmpty
    private String addrRegion;
    @ApiModelProperty(value = "详细地址")
    @NotEmpty
    private String addrDetailed;
    @ApiModelProperty(value = "联系电话")
    @NotEmpty
    private String addrPhone;
    @ApiModelProperty(value = "邮箱")
    private String addrMail;
    @ApiModelProperty(value = "地址别名")
    private String addrAlias;
    @ApiModelProperty(value = "地址状态 //0为默认")
    private Boolean addrStatus;

    public static void main(String[] args) {
          int[] arr = {1,35,5,6,4};
        for (int i =0;i<arr.length-1;i++){
            for(int j =0;j<arr.length-1-i;j++){
                if(arr[j]<arr[j+1]){
                    int t = arr[j];
                    arr[j] = arr[j+1];
                    arr[j+1] = t;
                }
            }
        }
        for(int i =0;i<arr.length;i++){
            System.out.print(arr[i]+" ");
        }
    }
}
