package com.example.society_app.notifications;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAtAufPZM:APA91bEv1s8v2d54AGrM6AM3dbOnYMhNUoPufY-ONe2ccMGlJGABiIsjX6_x1FJzCk2_TEJFFSdg3x_Wcn0pnEIKkpnXXII3Ati46QWoBL2Cx8W5f37ULzn-QMr9I2lW1jUZ4YZdwdB0"

            }
    )

    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);
}
