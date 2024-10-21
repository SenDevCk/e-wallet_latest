package com.bih.nic.e_wallet.retrofitPoso;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chandan on 10/18/2024.
 */
public class SmartConsumerDetail {

    @SerializedName("ACT_NO")
    private String actNo;
    @SerializedName("ADDRESS")
    private String address;
    @SerializedName("BILL_NO")
    private String billNo;
    @SerializedName("CATEGORY")
    private String category;
    @SerializedName("COMP_NAME")
    private String comName;
    @SerializedName("CONS_NAME")
    private String conName;
    @SerializedName("CON_ID")
    private String conId;
    @SerializedName("C_FA_HU_NAME")
    private String fatherName;
    @SerializedName("DIST_CODE")
    private String distCode;
    @SerializedName("DIST_NAME")
    private String distName;
    @SerializedName("DIV_ID")
    private String divId;
    @SerializedName("DIV_NAME")
    private String divName;
    @SerializedName("DUE_DT")
    private String dueDate;
    @SerializedName("GROSS_AMT")
    private String grossAmt;
    @SerializedName("LOAD")
    private String load;
    @SerializedName("METER_NO")
    private String meterNo;
    @SerializedName("METER_TYPE")
    private String meterType;
    @SerializedName("MOBILE_NO")
    private String mobNo;
    @SerializedName("MON_YY")
    private String monthYear;
    @SerializedName("NET_AMT")
    private String netAmt;
    @SerializedName("SUB_DIV_ID")
    private String subDivId;
    @SerializedName("PHASE")
    private String phase;
    @SerializedName("PREV_PAID_AMT")
    private String prePaidAmt;
    @SerializedName("PREV_PAID_DT")
    private String prevPaidDt;
    @SerializedName("PROMPT_AMT")
    private String promptAmt;
    @SerializedName("RESPONSE_MESSAGE")
    private String resMsg;
    @SerializedName("RURAL_URBAN")
    private String ruralUrban;
    @SerializedName("SECTION_ID")
    private String sectionId;
    @SerializedName("SECTION_NAME")
    private String sectionName;
    @SerializedName("SMART_METER_VENDOR")
    private String smartMeterVendor;
    @SerializedName("SUB_DIV_NAME")
    private String subDivName;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getComName() {
        return comName;
    }

    public void setComName(String comName) {
        this.comName = comName;
    }

    public String getConName() {
        return conName;
    }

    public void setConName(String conName) {
        this.conName = conName;
    }

    public String getConId() {
        return conId;
    }

    public void setConId(String conId) {
        this.conId = conId;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getDistCode() {
        return distCode;
    }

    public void setDistCode(String distCode) {
        this.distCode = distCode;
    }

    public String getDistName() {
        return distName;
    }

    public void setDistName(String distName) {
        this.distName = distName;
    }

    public String getDivId() {
        return divId;
    }

    public void setDivId(String divId) {
        this.divId = divId;
    }

    public String getDivName() {
        return divName;
    }

    public void setDivName(String divName) {
        this.divName = divName;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getGrossAmt() {
        return grossAmt;
    }

    public void setGrossAmt(String grossAmt) {
        this.grossAmt = grossAmt;
    }

    public String getLoad() {
        return load;
    }

    public void setLoad(String load) {
        this.load = load;
    }

    public String getMeterNo() {
        return meterNo;
    }

    public void setMeterNo(String meterNo) {
        this.meterNo = meterNo;
    }

    public String getMeterType() {
        return meterType;
    }

    public void setMeterType(String meterType) {
        this.meterType = meterType;
    }

    public String getMobNo() {
        return mobNo;
    }

    public void setMobNo(String mobNo) {
        this.mobNo = mobNo;
    }

    public String getMonthYear() {
        return monthYear;
    }

    public void setMonthYear(String monthYear) {
        this.monthYear = monthYear;
    }

    public String getNetAmt() {
        return netAmt;
    }

    public void setNetAmt(String netAmt) {
        this.netAmt = netAmt;
    }

    public String getSubDivId() {
        return subDivId;
    }

    public void setSubDivId(String subDivId) {
        this.subDivId = subDivId;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public String getPrePaidAmt() {
        return prePaidAmt;
    }

    public void setPrePaidAmt(String prePaidAmt) {
        this.prePaidAmt = prePaidAmt;
    }

    public String getPrevPaidDt() {
        return prevPaidDt;
    }

    public void setPrevPaidDt(String prevPaidDt) {
        this.prevPaidDt = prevPaidDt;
    }

    public String getPromptAmt() {
        return promptAmt;
    }

    public void setPromptAmt(String promptAmt) {
        this.promptAmt = promptAmt;
    }

    public String getResMsg() {
        return resMsg;
    }

    public void setResMsg(String resMsg) {
        this.resMsg = resMsg;
    }

    public String getRuralUrban() {
        return ruralUrban;
    }

    public void setRuralUrban(String ruralUrban) {
        this.ruralUrban = ruralUrban;
    }

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getSmartMeterVendor() {
        return smartMeterVendor;
    }

    public void setSmartMeterVendor(String smartMeterVendor) {
        this.smartMeterVendor = smartMeterVendor;
    }

    public String getSubDivName() {
        return subDivName;
    }

    public void setSubDivName(String subDivName) {
        this.subDivName = subDivName;
    }

    public String getActNo() {
        return actNo;
    }

    public void setActNo(String actNo) {
        this.actNo = actNo;
    }
}
