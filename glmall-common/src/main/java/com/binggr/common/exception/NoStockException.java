package com.binggr.common.exception;

/**
 * @author: bing
 * @date: 2020/12/9 14:05
 */
public class NoStockException extends RuntimeException{
    private Long skuId;
    public NoStockException(Long skuId){
        super("商品："+skuId+"没有足够的库存了");
    }
    public Long getSkuId(){
        return skuId;
    }
    public void setSkuId(Long skuId){
        this.skuId = skuId;
    }
    public NoStockException(String msg) {
        super(msg);
    }
}
