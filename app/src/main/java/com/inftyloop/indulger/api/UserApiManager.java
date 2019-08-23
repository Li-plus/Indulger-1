package com.inftyloop.indulger.api;

import com.google.gson.JsonObject;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import java.util.HashMap;

interface UserApiService {
    String BASE_URL = "https://api.inftyloop.tech:3443/";
    @POST("add_user")
    Observable<JsonObject> addUser(@Body HashMap<String, HashMap<String, String>> body);
    @POST("del_user")
    Observable<JsonObject> delUser(@Body HashMap<String, HashMap<String, String>> body);
    @POST("get_user")
    Observable<JsonObject> getUser(@Body HashMap<String, HashMap<String, String>> body);
    @POST("check_user")
    Observable<JsonObject> checkUser(@Body HashMap<String, HashMap<String, String>> body);
    @POST("update_user")
    Observable<JsonObject> updateUser(@Body HashMap<String, HashMap<String, String>> body);
    @POST("put_json")
    Observable<JsonObject> putJson(@Body HashMap<String, HashMap<String, String>> body);
    @POST("get_json")
    Observable<JsonObject> getJson(@Body HashMap<String, HashMap<String, String>> body);
}

@SuppressWarnings("Duplicates")
public class UserApiManager {
    private CompositeSubscription mCompositeSubscription;
    private UserApiService mUserApi;
    private OnUserApiListener mCallbackListener;

    public interface OnUserApiListener {
        void onAddUser(boolean success, String errMsg);
        void onDelUser(boolean success, String errMsg);
        void onGetUser(HashMap<String, String> response, String errMsg);
        void onCheckUser(boolean success, String errMsg);
        void onUpdateUser(boolean success, String errMsg);
        void onPutJson(boolean success, String errMsg);
        void onGetJson(String response, String errMsg);
    }

    public void setCallbackListener(OnUserApiListener listener) {
        mCallbackListener = listener;
    }

    public UserApiManager(OnUserApiListener listener) {
        mCallbackListener = listener;
        mUserApi = ApiRetrofit.buildOrGet("userApi", UserApiService.BASE_URL, UserApiService.class, ApiRetrofit.LOG_INTERCEPTOR);
    }

    public void addUser(String username, String pwd, String email) {
        HashMap<String, HashMap<String, String>> body = new HashMap<>();
        HashMap<String, String> req = new HashMap<>();
        req.put("username", username);
        req.put("password", pwd);
        req.put("email", email);
        body.put("payload", req);
        addSubscription(mUserApi.addUser(body), new Subscriber<JsonObject>(){
            @Override
            public void onCompleted() {}

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                if(mCallbackListener != null)
                    mCallbackListener.onAddUser(false, "Failed to request");
            }

            @Override
            public void onNext(JsonObject jsonObject) {
                int code = jsonObject.get("status").getAsInt();
                if(code == 0) {
                    if(mCallbackListener != null)
                        mCallbackListener.onAddUser(true, "");
                } else {
                    if(mCallbackListener != null)
                        mCallbackListener.onAddUser(false, code == -1 ? "Invalid request" : "Invalid username or password");
                }
            }
        });
    }

    public void delUser(String username, String pwd) {
        HashMap<String, HashMap<String, String>> body = new HashMap<>();
        HashMap<String, String> req = new HashMap<>();
        req.put("username", username);
        req.put("password", pwd);
        body.put("payload", req);
        addSubscription(mUserApi.delUser(body), new Subscriber<JsonObject>(){
            @Override
            public void onCompleted() {}

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                if(mCallbackListener != null)
                    mCallbackListener.onDelUser(false, "Failed to request");
            }

            @Override
            public void onNext(JsonObject jsonObject) {
                int code = jsonObject.get("status").getAsInt();
                if(code == 0) {
                    if(mCallbackListener != null)
                        mCallbackListener.onDelUser(true, "");
                } else {
                    if(mCallbackListener != null)
                        mCallbackListener.onDelUser(false, code == -1 ? "Invalid request" : "Invalid username or password");
                }
            }
        });
    }

    public void getUser(String username, String pwd) {
        HashMap<String, HashMap<String, String>> body = new HashMap<>();
        HashMap<String, String> req = new HashMap<>();
        req.put("username", username);
        req.put("password", pwd);
        body.put("payload", req);
        addSubscription(mUserApi.getUser(body), new Subscriber<JsonObject>(){
            @Override
            public void onCompleted() {}

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                if(mCallbackListener != null)
                    mCallbackListener.onGetUser(null, "Failed to request");
            }

            @Override
            public void onNext(JsonObject jsonObject) {
                int code = jsonObject.get("status").getAsInt();
                if(code == 0) {
                    if(mCallbackListener != null) {
                        HashMap<String, String> response = new HashMap<>();
                        JsonObject payload = jsonObject.get("payload").getAsJsonObject();
                        response.put("email",payload.get("email").getAsString());
                        response.put("register_date", payload.get("register_date").getAsString());
                        mCallbackListener.onGetUser(response, "");
                    }
                } else {
                    if(mCallbackListener != null)
                        mCallbackListener.onGetUser(null, code == -1 ? "Invalid request" : "Invalid username or password");
                }
            }
        });
    }

    public void checkUser(String username, String pwd) {
        HashMap<String, HashMap<String, String>> body = new HashMap<>();
        HashMap<String, String> req = new HashMap<>();
        req.put("username", username);
        req.put("password", pwd);
        body.put("payload", req);
        addSubscription(mUserApi.checkUser(body), new Subscriber<JsonObject>(){
            @Override
            public void onCompleted() {}

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                if(mCallbackListener != null)
                    mCallbackListener.onCheckUser(false, "Failed to request");
            }

            @Override
            public void onNext(JsonObject jsonObject) {
                int code = jsonObject.get("status").getAsInt();
                if(code == 0) {
                    if(mCallbackListener != null)
                        mCallbackListener.onCheckUser(true, "");
                } else {
                    if(mCallbackListener != null)
                        mCallbackListener.onCheckUser(false, code == -1 ? "Invalid request" : "Invalid username or password");
                }
            }
        });
    }

    public void updateUser(String username, String pwd, String new_pwd, String email) {
        HashMap<String, HashMap<String, String>> body = new HashMap<>();
        HashMap<String, String> req = new HashMap<>();
        req.put("username", username);
        req.put("password", pwd);
        req.put("new_password", new_pwd);
        req.put("email", email);
        body.put("payload", req);
        addSubscription(mUserApi.updateUser(body), new Subscriber<JsonObject>(){
            @Override
            public void onCompleted() {}

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                if(mCallbackListener != null)
                    mCallbackListener.onUpdateUser(false, "Failed to request");
            }

            @Override
            public void onNext(JsonObject jsonObject) {
                int code = jsonObject.get("status").getAsInt();
                if(code == 0) {
                    if(mCallbackListener != null)
                        mCallbackListener.onUpdateUser(true, "");
                } else {
                    if(mCallbackListener != null)
                        mCallbackListener.onUpdateUser(false, code == -1 ? "Invalid request" : "Invalid username or password");
                }
            }
        });
    }

    public void putJson(String username, String pwd, String varname, String json) {
        HashMap<String, HashMap<String, String>> body = new HashMap<>();
        HashMap<String, String> req = new HashMap<>();
        req.put("username", username);
        req.put("password", pwd);
        req.put("varname", varname);
        req.put("json", json);
        body.put("payload", req);
        addSubscription(mUserApi.putJson(body), new Subscriber<JsonObject>(){
            @Override
            public void onCompleted() {}

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                if(mCallbackListener != null)
                    mCallbackListener.onPutJson(false, "Failed to request");
            }

            @Override
            public void onNext(JsonObject jsonObject) {
                int code = jsonObject.get("status").getAsInt();
                if(code == 0) {
                    if(mCallbackListener != null)
                        mCallbackListener.onPutJson(true, "");
                } else {
                    if(mCallbackListener != null)
                        mCallbackListener.onPutJson(false, code == -1 ? "Invalid request" : "Invalid username or password");
                }
            }
        });
    }

    public void getJson(String username, String pwd, String varname) {
        HashMap<String, HashMap<String, String>> body = new HashMap<>();
        HashMap<String, String> req = new HashMap<>();
        req.put("username", username);
        req.put("password", pwd);
        req.put("varname", varname);
        body.put("payload", req);
        addSubscription(mUserApi.getJson(body), new Subscriber<JsonObject>(){
            @Override
            public void onCompleted() {}

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                if(mCallbackListener != null)
                    mCallbackListener.onGetJson(null, "Failed to request");
            }

            @Override
            public void onNext(JsonObject jsonObject) {
                int code = jsonObject.get("status").getAsInt();
                if(code == 0) {
                    if(mCallbackListener != null) {
                        mCallbackListener.onGetJson(jsonObject.get("payload").getAsJsonObject().get("json").getAsString(), "");
                    }
                } else {
                    if(mCallbackListener != null)
                        mCallbackListener.onGetJson(null, code == -1 ? "Invalid request" : "Invalid username or password");
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    public void addSubscription(Observable observable, Subscriber subscriber) {
        if(mCompositeSubscription == null)
            mCompositeSubscription = new CompositeSubscription();
        mCompositeSubscription.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber));
    }

    public void unSubscribe() {
        if(mCompositeSubscription != null && mCompositeSubscription.hasSubscriptions())
            mCompositeSubscription.unsubscribe();
    }
}
