package com.baidu.shop.constant;

public class MqMessageConstant {
    //spu交换机，routingkey
    public static final String SPU_ROUT_KEY_SAVE="spu.save";
    public static final String SPU_ROUT_KEY_UPDATE="spu.update";
    public static final String SPU_ROUT_KEY_DELETE="spu.delete";
    //spu-es的队列
    public static final String SPU_QUEUE_SEARCH_SAVE="spu_queue_es_save";
    public static final String SPU_QUEUE_SEARCH_UPDATE="spu_queue_es_update";
    public static final String SPU_QUEUE_SEARCH_DELETE="spu_queue_es_delete";
    //spu-page的队列
    public static final String SPU_QUEUE_PAGE_SAVE="spu_queue_page_save";
    public static final String SPU_QUEUE_PAGE_UPDATE="spu_queue_page_update";
    public static final String SPU_QUEUE_PAGE_DELETE="spu_queue_page_delete";
    public static final String ALTERNATE_EXCHANGE = "exchange.ae";
    public static final String EXCHANGE = "exchange.mr";
    //Dead Letter Exchanges
    public static final String EXCHANGE_DLX = "exchange.dlx";//死信交换机
    public static final String EXCHANGE_DLRK = "dlx.rk";
    public static final Integer MESSAGE_TIME_OUT = 5000;
    public static final String QUEUE = "queue.mr";
    public static final String QUEUE_AE = "queue.ae";
    public static final String QUEUE_DLX = "queue.dlx";
    public static final String ROUTING_KEY = "mrkey";

    //给Queue起名时,可以起一个贴合业务的名字
    /**延迟队列名*/
    public static final String DELAY_QUEUE = "delay.order";
    /**延迟队列(死信队列)交换器名*/
    public  static final String DELAY_EXCHANGE = "delay.exchange";
    /**处理业务的队列(死信队列)*/
    public static final String BIZ_QUEUE = "biz.order";
    /**ttl(存活时间为,单位为毫秒)*/
    public static final long DELAY_TTL = 60000;


}
