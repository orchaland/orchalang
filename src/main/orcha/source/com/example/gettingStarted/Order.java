package com.example.gettingStarted;

import java.io.Serializable;

public class Order
    implements Serializable
{
    String product;
    Integer id;

    public Order() {
    }

    public void getProduct(String product, Integer id) {
        this.product = product;
        this.id = id;
    }

    public String getProduct() {
        return product;
    }

    public String setProduct() {
        this.product = product;
    }

    public Integer getId() {
        return id;
    }

    public Integer setId() {
        this.id = product;
    }

    @Override
    public String toString() {
    }
}
