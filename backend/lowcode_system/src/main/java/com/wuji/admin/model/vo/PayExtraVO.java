package com.wuji.admin.model.vo;


public class PayExtraVO {
    public PayExtraVO(String _productId, Integer _type){
        this.productId = _productId;
        this.type = _type;
    }
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    private String productId;
    private Integer type;
}
