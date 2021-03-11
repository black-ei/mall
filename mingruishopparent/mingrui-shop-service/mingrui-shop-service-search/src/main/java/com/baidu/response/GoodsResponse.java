package com.baidu.response;

import com.baidu.shop.base.Result;
import com.baidu.shop.document.GoodsDoc;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.status.HTTPStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class GoodsResponse extends Result<List<GoodsDoc>> {

    private Integer total;

    private Integer pageTotal;

    private List<CategoryEntity> categoryList;

    private List<BrandEntity> brandList;

    private Map<String, List<String>> specMap;

    public GoodsResponse(Integer total, Integer pageTotal, List<CategoryEntity> categoryList, List<BrandEntity> brandList,List<GoodsDoc> goodsDocList,Map<String, List<String>> specMap) {
        super(HTTPStatus.OK,"",goodsDocList);
        this.total = total;
        this.pageTotal = pageTotal;
        this.categoryList = categoryList;
        this.brandList = brandList;
        this.specMap = specMap;
    }

    public GoodsResponse(Integer code, String message, Object data, Integer total, Integer pageTotal, List<CategoryEntity> categoryList, List<BrandEntity> brandList) {
        super(code, message, data);
        this.total = total;
        this.pageTotal = pageTotal;
        this.categoryList = categoryList;
        this.brandList = brandList;
    }
}
