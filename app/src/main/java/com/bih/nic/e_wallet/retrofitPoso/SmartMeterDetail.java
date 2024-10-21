package com.bih.nic.e_wallet.retrofitPoso;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chandan on 10/9/2024.
 */
public class SmartMeterDetail {


    @SerializedName("MeterBalance")
    private Double meterBalance;
    @SerializedName("LastRechargeAmount")
    private String lastRechargeAmount;
    @SerializedName("ConsumerId")
    private String consumerId;
    @SerializedName("LastReceiptNo")
    private String lastReceiptNo;
    @SerializedName("LastRechargeDate")
    private String lastRechargeDate;
    @SerializedName("ResultCodeDescription")
    private String resultCodeDescription;
    @SerializedName("ConnectionStatus")
    private Integer connectionStatus;
    @SerializedName("ResultCode")
    private Integer resultCode;

    public Double getMeterBalance() {
        return meterBalance;
    }

    public void setMeterBalance(Double meterBalance) {
        this.meterBalance = meterBalance;
    }

    public String getLastRechargeAmount() {
        return lastRechargeAmount;
    }

    public void setLastRechargeAmount(String lastRechargeAmount) {
        this.lastRechargeAmount = lastRechargeAmount;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }

    public String getLastReceiptNo() {
        return lastReceiptNo;
    }

    public void setLastReceiptNo(String lastReceiptNo) {
        this.lastReceiptNo = lastReceiptNo;
    }

    public String getLastRechargeDate() {
        return lastRechargeDate;
    }

    public void setLastRechargeDate(String lastRechargeDate) {
        this.lastRechargeDate = lastRechargeDate;
    }

    public String getResultCodeDescription() {
        return resultCodeDescription;
    }

    public void setResultCodeDescription(String resultCodeDescription) {
        this.resultCodeDescription = resultCodeDescription;
    }

    public Integer getResultCode() {
        return resultCode;
    }

    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }


    public Integer getConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(Integer connectionStatus) {
        this.connectionStatus = connectionStatus;
    }
}
