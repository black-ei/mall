package com.baidu.shop.utils;

import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ESHighLightUtil {
    //获取HighlightBuilder 参数(要创建高亮的字段)
    public static HighlightBuilder getHighlightBuilderBuilder(String... fields){
        HighlightBuilder highlightBuilder = new HighlightBuilder();//创建高亮查询类
        Arrays.asList(fields).forEach(field ->{
            highlightBuilder.field(field);//设置查询字段
            highlightBuilder.preTags("<font style='color:red'>");//高亮效果前缀
            highlightBuilder.postTags("</font>");//高亮效果后缀
        });
        return highlightBuilder;
    }

    //重新给字段赋值
    public static <T> SearchHits<T>  getHighLightHit(SearchHits<T> searchHits){

          searchHits.getSearchHits().stream().forEach(hit ->{//遍历SearchHits

            hit.getHighlightFields().forEach((k,y) ->{//获得map类型的Field
                T content = hit.getContent();//获取上下文映射实体
                List<String> highlightField = hit.getHighlightField(k);//根据k获得值
                try {
                    Method method = content.getClass().getMethod("set" + firstCharUpper(k),String.class);//反射获得
                    method.invoke(content, highlightField.get(0));//给数据重新赋值
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            });
        });
         return searchHits;
    }

    private static String firstCharUpper(String str){
        char[] chars = str.toCharArray();
        chars[0] -= 32;
        return String.valueOf(chars);
    }

}
