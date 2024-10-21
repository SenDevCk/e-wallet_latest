package com.bih.nic.e_wallet.retrofitPoso;

/**
 * Created by chandan on 10/9/2024.
 */
public class SmartMeterBalanceRequest {
    private String ConsumerId;

    public SmartMeterBalanceRequest(String consumerId) {
        this.ConsumerId=consumerId;
    }

    public String getConsumerId() {
        return ConsumerId;
    }

    public void setConsumerId(String consumerId) {
        ConsumerId = consumerId;
    }
}
