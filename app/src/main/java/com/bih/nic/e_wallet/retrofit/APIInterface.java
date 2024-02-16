package com.bih.nic.e_wallet.retrofit;



import com.bih.nic.e_wallet.entity.UserInfo2;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIInterface {
    @POST("/ewallet/api/nic/vendorLogin?")
    Call<UserInfo2> doLogin(@Query("reqStr") String reqStr);

    @POST("/ewallet/api/nic/firstOTP?")
    Call<String> firstOtp(@Query("reqStr") String reqStr);

    @POST("/ewallet/api/nic/makePayment?")
    Call<String> makePayment(@Query("reqStr") String reqStr);

    @POST("/ewallet/api/nic/verify?")
    Call<String> makeVerify(@Query("reqStr") String reqStr);
    @POST("/ewallet/api/nic/balanceEnq?")
    Call<String> loadBalance(@Query("reqStr") String reqStr);

//
//    @GET("/app/dashboard/m/all/{locationType}/{location}")
//    Call<DashboardResponse> doGetDashboardAllData(@Path("locationType") String locationType, @Path("location") String location);
//
//    @GET("/app/vendor/{vendorId}")
//    Call<VendorDataResponse> doGetVender(@Path("vendorId") String vendorId);
//
//    @GET("/app/manufacturer/{manufacurerId}")
//    Call<ManufacturerPoso> doGetManufacture(@Path("manufacurerId") String manufacurerId);
//
//    @GET("/lmd-api/lmd/getMarketInspectionDetails/{monthSelected}/{yearSelected}/{userid}")
//    Call<MyResponse<List<MarketInspectionDetail>>> doGetMarketInspectionDetails(@Path("monthSelected")int monthSelected, @Path("yearSelected")int yearSelected, @Path("userid")String userid);
//
//    @POST("/lmd-api/lmd/saveMarketInspectionDetails")
//    Call<MyResponse<String>> saveMarketInspectionDetails(@Body List<MarketInspectionDetail> marketInspectionDetail);
//
//    @GET("/lmd-api/lmd/getMonthlyRevenueDetails/{monthSelected}/{yearSelected}/{userid}")
//    Call<MyResponse<RequestForRevenueData>> doGetRevenueReportDetails(@Path("monthSelected")int monthSelected, @Path("yearSelected")int yearSelected, @Path("userid")String userid);
//
//    @POST("/lmd-api/lmd/saveRevenueReportDetails")
//    Call<MyResponse<String>> saveRevenueReport(@Body RequestForRevenueData requestForRevenueData);
//
//    @GET("/lmd-api/lmd/getRenevalRegData/{monthSelected}/{yearSelected}/{userid}")
//    Call<MyResponse<RenevalAndRegistrationFee>> doGetRenRegData(@Path("monthSelected")int monthSelected, @Path("yearSelected")int yearSelected, @Path("userid")String userid);
//
//    @POST("/lmd-api/lmd/saveRenevalResitrationFee")
//    Call<MyResponse<String>> saveRenRegFee(@Body RenevalAndRegistrationFee requestForRevenueData);
//
//    @POST("/app/adalat/saveAdalatDetails")
//    Call<MyResponse<AdalatPayDetails>> uploadingAdalatDetails(@Body AdalatPayDetails payDetails);

    /* @GET("/api/manufacturer/11112")
    Call<ManufacturerPoso> doGetManufacture();*/

    //locationType,loginRole,loginLocation
    //@GET("/api/dashboard/m/all")
    //Call<DashboardResponse> doGetDashboardData();

     /*@POST("/api/users")
    Call<User> createUser(@Body User user);

    @GET("/api/users?")
    Call<UserList> doGetUserList(@Query("page") String page);

    @FormUrlEncoded
    @POST("/api/users?")
    Call<UserList> doCreateUserWithField(@Field("name") String name, @Field("job") String job);*/
}
