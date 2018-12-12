package com.Nflicks.netUtils;


import android.support.annotation.NonNull;

import org.json.JSONArray;

import java.io.File;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

public interface RequestInterface {

    String secure_field = "key";
    String secure_value = "1226";

    // todo GetCheckVersion
    @POST("service_sales_executive.php?"+secure_field+"="+secure_value+"&s=48")
    Call<ResponseBody> CheckVersion();

    // todo Login
    @POST("service_user.php?"+secure_field+"="+secure_value+"&s=1&user_type=android")
    Call<ResponseBody> LoginUser(@Query("mobile_no") String mobile_no
    );

    // todo Resend OTP
    @POST("service_user.php?"+secure_field+"="+secure_value+"&s=1&user_type=android")
    Call<ResponseBody> ResendOtp(@Query("mobile_no") String mobile_no
    );

    /* todo OTP Verify */
    @POST("service_user.php?"+secure_field+"="+secure_value+"&s=5&user_type=android")
    Call<ResponseBody> OtpVerify(@Query("mobile_no") String mobile_no,
                                 @Query("otp") String otp,
                                 @Query("imei") String imei,
                                 @Query("refreshToken") String refreshToken
    );

    // todo Regstration user
    @POST("service_user.php?"+secure_field+"="+secure_value+"&s=3&user_type=android")
    Call<ResponseBody> RegUser(@Query("uid") String uid,
                               @Query("firstname") String firstname,
                               @Query("lastname") String lastname
    );

    // todo Regstration user
    @Multipart
    @POST("service_user.php?"+secure_field+"="+secure_value+"&s=3&user_type=android")
    Call<ResponseBody> RegUser(@Query("uid") String uid,
                               @Query("firstname") String firstname,
                               @Query("lastname") String lastname,
                               @Part MultipartBody.Part file
    );

    // todo Update user profile
    @Multipart
    @POST("service_user.php?"+secure_field+"="+secure_value+"&s=3&user_type=android")
    Call<ResponseBody> RegUser(@Query("uid") String uid,
                               @Query("firstname") String firstname,
                               @Query("lastname") String lastname,
                               @Query("birthdate") String birthdate,
                               @Query("email") String email,
                               @Query("mobile_no") String mobile_no,
                               @Query("website") String website,
                               @Query("gender") String gender,
                               @Query("details") String details,
                               @Query("interest_id") String interest_id,
                               @Part MultipartBody.Part file
    );

    // todo Update user profile
    @POST("service_user.php?"+secure_field+"="+secure_value+"&s=3&user_type=android")
    Call<ResponseBody> RegUser(@Query("uid") String uid,
                               @Query("firstname") String firstname,
                               @Query("lastname") String lastname,
                               @Query("birthdate") String birthdate,
                               @Query("email") String email,
                               @Query("mobile_no") String mobile_no,
                               @Query("website") String website,
                               @Query("gender") String gender,
                               @Query("details") String details,
                               @Query("interest_id") String interest_id
    );

    // todo List oF Interest
    @POST("service_user.php?"+secure_field+"="+secure_value+"&s=6&user_type=android")
    Call<ResponseBody> GetInterest(@Query("uid") String uid);

    // todo List oF Interest
    @POST("service_user.php?"+secure_field+"="+secure_value+"&s=7&user_type=android")
    Call<ResponseBody> AddInterest(@Query("uid") String uid,
                                   @Query("interest_id") String interest_id);

    // todo Get Profile
    @POST("service_user.php?"+secure_field+"="+secure_value+"&s=19&user_type=android")
    Call<ResponseBody> Getprofile(@Query("uid") String uid);

    // todo Get User channel
    @POST("service_user.php?"+secure_field+"="+secure_value+"&s=20&user_user_type=android")
    Call<ResponseBody>   GetUserChannel(@Query("uid") String uid,
                                      @Query("ul") int ul,
                                      @Query("ll") int ll);

    // todo Get User channel
    @POST("service_user.php?"+secure_field+"="+secure_value+"&s=20&user_user_type=android")
    Call<ResponseBody> GetUserChannel(@Query("uid") String uid,
                                      @Query("auid") String auid,
                                      @Query("ul") int ul,
                                      @Query("ll") int ll);


    // todo Get More Editor Content
    @POST("service_user.php?"+secure_field+"="+secure_value+"&s=34&user_user_type=android")
    Call<ResponseBody> GetMoreEditerContent(@Query("uid") String uid,
                                            @Query("cnid") String cnid,
                                            @Query("ul") int ul,
                                            @Query("ll") int ll);

    // todo Create flick without image or document
    @POST("service_user.php?"+secure_field+"="+secure_value+"&s=15&user_type=android")
    Call<ResponseBody> CreateFlick(@Query("uid") String uid,
                                   @Query("channel_id") String channel_id,
                                   @Query("flick_name") String flick_name,
                                   @Query("details") String details,
                                   @Query("flick_send_flag") String flick_send_flag
    );

    // todo Create flick with image
    @Multipart
    @POST("service_user.php?"+secure_field+"="+secure_value+"&s=15&user_type=android")
    Call<ResponseBody> CreateFlick(@Query("uid") String uid,
                                   @Query("channel_id") String channel_id,
                                   @Query("flick_name") String flick_name,
                                   @Query("details") String details,
                                   @Query("flick_send_flag") String flick_send_flag,
                                   @Part MultipartBody.Part file
    );

    // todo Create flick with image or document
    @Multipart
    @POST("service_user.php?"+secure_field+"="+secure_value+"&s=15&user_type=android")
    Call<ResponseBody> CreateFlick(@Query("uid") String uid,
                                   @Query("channel_id") String channel_id,
                                   @Query("flick_name") String flick_name,
                                   @Query("details") String details,
                                   @Query("flick_send_flag") String flick_send_flag,
                                   @Part MultipartBody.Part image,
                                   @Part MultipartBody.Part file
    );

    // todo Update flick
    @POST("service_user.php?"+secure_field+"="+secure_value+"&s=43&user_type=android")
    Call<ResponseBody> UpdateFlick(@Query("uid") String uid,
                                   @Query("fid") String fid,
                                   @Query("flick_name") String flick_name,
                                   @Query("details") String details,
                                   @Query("flick_send_flag") String flick_send_flag
    );

    // todo Create channel
    @POST("service_user.php?"+secure_field+"="+secure_value+"&s=8&user_type=android")
    Call<ResponseBody> Create_channel(@Query("uid") String uid,
                                      @Query("channel_name") String channel_name,
                                      @Query("channel_privacy") String channel_privacy,
                                      @Query("details") String details,
                                      @Query("contact_no") String contact_no,
                                      @Query("address") String address,
                                      @Query("email") String email,
                                      @Query("website") String website,
                                      @Query("age") String age,
                                      @Query("tags") String tags
    );

    @Multipart
    @POST("service_user.php?"+secure_field+"="+secure_value+"&s=8&user_type=android")
    Call<ResponseBody> Create_channel(@Query("uid") String uid,
                                      @Query("channel_name") String channel_name,
                                      @Query("channel_privacy") String channel_privacy,
                                      @Query("details") String details,
                                      @Query("contact_no") String contact_no,
                                      @Query("address") String address,
                                      @Query("email") String email,
                                      @Query("website") String website,
                                      @Query("age") String age,
                                      @Query("tags") String tags,
                                      @Part MultipartBody.Part file
    );

    // todo Update channel
    @POST("service_user.php?"+secure_field+"="+secure_value+"&s=9&user_type=android")
    Call<ResponseBody> Update_channel(@Query("uid") String uid,
                                      @Query("id") String id,
                                      @Query("channel_name") String channel_name,
                                      @Query("channel_privacy") String channel_privacy,
                                      @Query("details") String details,
                                      @Query("contact_no") String contact_no,
                                      @Query("address") String address,
                                      @Query("email") String email,
                                      @Query("website") String website,
                                      @Query("age") String age,
                                      @Query("tags") String tags
    );

    @Multipart
    @POST("service_user.php?"+secure_field+"="+secure_value+"&s=9&user_type=android")
    Call<ResponseBody> Update_channel(@Query("uid") String uid,
                                      @Query("id") String id,
                                      @Query("channel_name") String channel_name,
                                      @Query("channel_privacy") String channel_privacy,
                                      @Query("details") String details,
                                      @Query("contact_no") String contact_no,
                                      @Query("address") String address,
                                      @Query("email") String email,
                                      @Query("website") String website,
                                      @Query("age") String age,
                                      @Query("tags") String tags,
                                      @Part MultipartBody.Part file
    );

    // todo get all channel
    @POST("service_user.php?"+secure_field+"="+secure_value+"&s=23&user_type=android")
    Call<ResponseBody> getAllChannel(@Query("uid") String uid
    );

    // todo get all Get Contacts
    @POST("service_user.php?"+secure_field+"="+secure_value+"&s=42&user_type=android")
    Call<ResponseBody> getAllContactChannel(@Query("uid") String uid,
                                            @Query("device_token") String imei
    );

    // todo get all channel flick
    @POST("service_user.php?"+secure_field+"="+secure_value+"&s=18&user_type=android")
    Call<ResponseBody> getFlick(@Query("uid") String uid,
                                @Query("channel_id") String channel_id,
                                @Query("ul") int ul,
                                @Query("ll") int ll
    );

    // todo Channel Follow Unfollow
    @POST("service_user.php?"+secure_field+"="+secure_value+"&s=10&user_type=android")
    Call<ResponseBody> channel_Follow_Unfollow(@Query(" uid") String uid,
                                               @Query("channel_id") String channel_id,
                                               @Query("flag") String flag,
                                               @Query("priority") String priority
    );

    // todo Get Location
    @POST("service_general.php?"+secure_field+"="+secure_value+"&s=21&user_type=android")
    Call<ResponseBody> getLocation(@Query("uid") String uid,
                                   @Query("query") String query
    );

    // todo Get Targate audaince
    @POST("service_general.php?"+secure_field+"="+secure_value+"&s=22&user_type=android")
    Call<ResponseBody>  getAudience(@Query("uid") String uid,
                                    @Query("query") String query
    );

    // todo Get Channel detail
    @POST("service_user.php?"+secure_field+"="+secure_value+"&s=13&user_type=android")
    Call<ResponseBody> getChannelDetail(@Query("uid") String uid,
                                        @Query("channel_id") String channel_id
    );

    // todo Get Editor Content
    @POST("service_user.php?"+secure_field+"="+secure_value+"&s=27&user_type=android")
    Call<ResponseBody> getDefaultSearch(@Query("uid") String uid
    );

    // todo Get Search Channel With Query
    @POST("service_user.php?"+secure_field+"="+secure_value+"&s=33&user_type=android")
    Call<ResponseBody> getSearchWithQuery(@Query("uid") String uid,
                                          @Query("query") String query,
                                          @Query("tr") String tr,
                                          @Query("uf") String uf
    );

    // todo Get Search Channel With Query
    @POST("service_user.php?"+secure_field+"="+secure_value+"&s=33&user_type=android")
    Call<ResponseBody> getSearchWithQuery(@Query("uid") String uid,
                                          @Query("query") String query,
                                          @Query("tr") String tr,
                                          @Query("uf") String uf,
                                          @Query("ul") int ul,
                                          @Query("ll") int ll
    );

    // todo Get Search History
    @POST("service_user.php?"+secure_field+"="+secure_value+"&s=29&user_type=android")
    Call<ResponseBody> getSearch_history(@Query("uid") String uid);

    // todo Get Channel From Barcode
    @POST("service_user.php?"+secure_field+"="+secure_value+"&s=40&user_type=android")
    Call<ResponseBody> getChannelFromBarcode(@Query("uid") String uid,
                                             @Query("qrcode") String qrcode
    );

    // todo Update Search History
    @POST("service_user.php?"+secure_field+"="+secure_value+"&s=28&user_type=android")
    Call<ResponseBody> UpdateSearchHistory(@Query("uid") String uid,@Query("cid") String cid);

    // todo Bookmark Flick
    @POST("service_user.php?"+secure_field+"="+secure_value+"&s=24&user_type=android")
    Call<ResponseBody> SaveFlick(@Query("uid") String uid,@Query("fid") String fid);

    // todo Remove Bookmark Flick
    @POST("service_user.php?"+secure_field+"="+secure_value+"&s=26&user_type=android")
    Call<ResponseBody> RemoveSaveFlick(@Query("uid") String uid,@Query("fid") String fid);

    // todo Get Bookmarked Flicks
    @POST("service_user.php?"+secure_field+"="+secure_value+"&s=25&user_type=android")
    Call<ResponseBody> getSaveFlick(@Query("uid") String uid,
                                    @Query("ul") int ul,
                                    @Query("ll") int ll
    );

    // todo Get User Detail
    @POST("service_user.php?"+secure_field+"="+secure_value+"&s=19&user_type=android")
    Call<ResponseBody> getUserdetail(@Query("uid") String uid
    );

    // todo Update Channel URL
    @POST("service_user.php?"+secure_field+"="+secure_value+"&s=32&user_type=android")
    Call<ResponseBody> Updatechannelurl(@Query("uid") String uid,
                                        @Query("cid") String cid,
                                        @Query("url") String url
    );

    // todo Report Spam
    @POST("service_user.php?"+secure_field+"="+secure_value+"&s=37&user_type=android")
    Call<ResponseBody> ReportSpam(@Query("uid") String uid,@Query("fid") String fid);

    // todo Report Inappropriate
    @POST("service_user.php?"+secure_field+"="+secure_value+"&s=38&user_type=android")
    Call<ResponseBody> ReportInappropriate(@Query("uid") String uid,@Query("fid") String fid);

    // todo delete flick
    @POST("service_user.php?"+secure_field+"="+secure_value+"&s=16&user_type=android")
    Call<ResponseBody> DeleteFlick(@Query("uid") String uid,@Query("fid") String fid);

    // todo .Get user notificatinion
    @POST("service_user.php?"+secure_field+"="+secure_value+"&s=36&user_type=android")
    Call<ResponseBody> getNotification(@Query("uid") String uid,
                                       @Query("ul") int ul,
                                       @Query("ll") int ll);

    // todo .Get user notificatinion
    @POST("service_user.php?"+secure_field+"="+secure_value+"&s=36&user_type=android")
    Call<ResponseBody> getNotification(@Query("uid") String uid,
                                       @Query("nid") String last_notiflick_id);

    // todo Accept/ Reject Follow
    @POST("service_user.php?"+secure_field+"="+secure_value+"&s=39&user_type=android")
    Call<ResponseBody> acceptORreject(@Query("uid") String uid,
                                      @Query("refid") String refid,
                                      @Query("flag") String flag);

    // todo Contact Sync
    @POST("service_user.php?"+secure_field+"="+secure_value+"&s=41&user_type=android")
    Call<ResponseBody> ContactSync(@Query("uid") String uid,
                                   @Query("device_token") String imei,
                                   @Body JSONArray contacts
    );

    // todo Channel Delete
    @POST("service_user.php?"+secure_field+"="+secure_value+"&s=44&user_type=android")
    Call<ResponseBody> channelDelete(@Query("uid") String uid,
                                   @Query("cid") String cid
    );

    // todo Channel isActive
    @POST("service_user.php?"+secure_field+"="+secure_value+"&s=45&user_type=android")
    Call<ResponseBody> channel_isActive(@Query("uid") String uid,
                                     @Query("cid") String cid,
                                     @Query("status") int status
    );

    // todo Channel ID
    @POST("service_user.php?"+secure_field+"="+secure_value+"&s=47&user_type=android")
    Call<ResponseBody> getChannelId(@Query("uid") String uid,
                                        @Query("url") String url
    );


    // todo getAllflick
    @POST("service_user.php?"+secure_field+"="+secure_value+"&s=48&user_type=android")
    Call<ResponseBody> getAllFlick(@Query("uid") String uid,
                                    @Query("fid") String  last_flick_id,
                                    @Query("ul") int ul,
                                    @Query("ll") int ll
    );

    // todo getAllflick
    @POST("service_user.php?"+secure_field+"="+secure_value+"&s=48&user_type=android")
    Call<ResponseBody> getAllFlick(@Query("uid") String uid,
                                   @Query("fid") String  last_flick_id
    );

}
