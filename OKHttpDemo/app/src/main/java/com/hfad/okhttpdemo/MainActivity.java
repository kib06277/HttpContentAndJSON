package com.hfad.okhttpdemo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity
{
    private TextView textView;
    private Button okHttp_synchronousGet , okHttp_asynchronousGet ,GetSucces , PostSucces , PostError , Json , clean;
    private ExecutorService service;
    private OkHttpClient client;
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        initView();
        onclick();
    }

    private void initData()
    {
        client = new OkHttpClient();
        service = Executors.newSingleThreadExecutor();
    }

    private void initView()
    {
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.textView);

        clean = (Button)findViewById(R.id.clean);
        GetSucces = (Button)findViewById(R.id.GetSucces);
        PostSucces = (Button)findViewById(R.id.PostSucces);
        PostError = (Button)findViewById(R.id.PostError);
        Json = (Button)findViewById(R.id.Json);
        okHttp_synchronousGet = (Button)findViewById(R.id.okHttp_synchronousGet);
        okHttp_asynchronousGet = (Button)findViewById(R.id.okHttp_asynchronousGet);
    }

    private void onclick()
    {
        //同步 get
        okHttp_synchronousGet.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                okHttp_synchronousGet("https://www.google.com.tw/");
            }
        });

        //非同步 get
        okHttp_asynchronousGet.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                okHttp_asynchronousGet("https://www.google.com.tw/");
            }
        });

        //Post 成功
        PostSucces.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                handleRequestInBackground("rdec-key-123-45678-011121314");
            }
        });

        //Post key 錯誤失敗
        PostError.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                handleRequestInBackground("rdec-key-123-45678-011121315");
            }
        });

        //get 成功
        GetSucces.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                handleRequest();
            }
        });

        //讀取 json 分析
        Json.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                handleJson();
            }
        });

        clean.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                textView.setText("");
            }
        });
    }

    private void handleJson()
    {
        Request request = new Request.Builder().url("https://mdn.github.io/learning-area/javascript/oojs/json/superheroes.json").build();
        client.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException
            {
                final String resStr = response.body().string();
                mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(new Runnable()
                {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void run()
                    {
                        // code to interact with UI
                        final StringBuffer sb = new StringBuffer();
                        sb.append(resStr);
                        sb.append("===================================");
                        sb.append("\n");
                        try
                        {
                            JSONObject jsonObject = new JSONObject(resStr);
                            sb.append(jsonObject.get("squadName"));
                            sb.append("\n");
                            sb.append(jsonObject.get("homeTown"));
                            sb.append("\n");
                            sb.append(jsonObject.get("formed"));
                            sb.append("\n");;
                            sb.append(jsonObject.get("secretBase"));
                            sb.append("\n");
                            sb.append(jsonObject.get("active"));
                            sb.append("\n");
                            sb.append("-----------------解 members ----------------------");
                            sb.append("\n");
                            JSONArray array = jsonObject.getJSONArray("members");
                            for (int i = 0; i < array.length(); i++)
                            {
                                sb.append("==================第 " + i + " 筆 ----------------------");
                                sb.append("\n");
                                JSONObject jsonObject1 = array.getJSONObject(i);
                                sb.append("\t" + jsonObject1.getString("name"));
                                sb.append("\n");
                                sb.append("\t" + jsonObject1.getString("age"));
                                sb.append("\n");
                                sb.append("\t" + jsonObject1.getString("secretIdentity"));
                                sb.append("\n");

                                JSONArray array1 = jsonObject.getJSONArray("members").getJSONObject(i).getJSONArray("powers");
                                for(int j = 0 ; j < array1.length() ; j++)
                                {
                                    sb.append("***************" + j + "**********************");
                                    sb.append("\n");
                                    sb.append(array1.get(j));
                                    sb.append("\n");
                                }
                            }
                            textView.setText(sb.toString()); // must be inside run()
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }


                    }
                });
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull final IOException e)
            {

                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mainHandler = new Handler(Looper.getMainLooper());
                        mainHandler.post(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                // code to interact with UI
                                textView.setText(e.getMessage()); // must be inside run()
                            }
                        });
                    }
                });
            }
        });
    }

    private void handleRequest()
    {
        Request request = new Request.Builder().url("https://www.google.com/").build();
        client.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException
            {
                final String resStr = response.body().string();
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mainHandler = new Handler(Looper.getMainLooper());
                        mainHandler.post(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                // code to interact with UI
                                textView.setText(resStr); // must be inside run()
                            }
                        });
                    }
                });
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull final IOException e)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mainHandler = new Handler(Looper.getMainLooper());
                        mainHandler.post(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                // code to interact with UI
                                textView.setText(e.getMessage()); // must be inside run()
                            }
                        });
                    }
                });
            }
        });
    }

    private void handleRequestInBackground(final String key)
    {
        service.submit(new Runnable()
        {
            @Override
            public void run()
            {
                HttpUrl.Builder builder = HttpUrl.parse("https://opendata.cwb.gov.tw/api/v1/rest/datastore/E-A0015-001?").newBuilder();
                builder.addQueryParameter("Authorization",key);
                Request request = new Request.Builder().url(builder.toString()).build();
                try
                {
                    final Response response = client.newCall(request).execute();
                    final String resStr = response.body().string();
                    mainHandler = new Handler(Looper.getMainLooper());
                    mainHandler.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            // code to interact with UI
                            textView.setText(resStr); // must be inside run()
                        }
                    });
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 同步 Get 方法
     */
    private void okHttp_synchronousGet(final String url)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    OkHttpClient client = new OkHttpClient(); //建立 OkHttpClient
                    Request request = new Request.Builder().url(url).build(); //建立請求 URL
                    final okhttp3.Response response = client.newCall(request).execute(); //發送請求

                    //請求成功
                    if (response.isSuccessful())
                    {
                        mainHandler = new Handler(Looper.getMainLooper());
                        mainHandler.post(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                // code to interact with UI
                                try
                                {
                                    textView.setText(response.body().string()); // must be inside run()
                                }
                                catch (IOException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    //請求失敗
                    else
                    {
                        mainHandler = new Handler(Looper.getMainLooper());
                        mainHandler.post(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                // code to interact with UI
                                textView.setText("okHttp is request error"); // must be inside run()
                            }
                        });
                    }
                }
                catch (IOException e)
                {
                    Log.i("AA","e = " + e);
                }
            }
        }).start();
    }

    /**
     * 非同步 Get
     */
    private void okHttp_asynchronousGet(final String url)
    {
        try
        {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            client.newCall(request).enqueue(new okhttp3.Callback()
            {
                @Override
                public void onFailure(okhttp3.Call call, final IOException e)
                {
                    mainHandler = new Handler(Looper.getMainLooper());
                    mainHandler.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            mainHandler = new Handler(Looper.getMainLooper());
                            mainHandler.post(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    // code to interact with UI
                                    textView.setText(e.getMessage()); // must be inside run()
                                }
                            });
                        }
                    });
                }
                @Override
                public void onResponse(okhttp3.Call call, final okhttp3.Response response) throws IOException
                {
                    // 注：該回撥是子執行緒，非主執行緒
                    mainHandler = new Handler(Looper.getMainLooper());
                    mainHandler.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            // code to interact with UI
                            try
                            {
                                textView.setText("callback thread id is " + Thread.currentThread().getId() + "\t\n" + response.body().string()); // must be inside run()
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
