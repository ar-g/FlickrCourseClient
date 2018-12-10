package ar_g.flickrcourseclient.feed;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import ar_g.flickrcourseclient.R;
import ar_g.flickrcourseclient.model.PhotoItem;
import ar_g.flickrcourseclient.model.Result;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class MainActivity extends AppCompatActivity {

  public static final String API_KEY = "90b99338fcc5e746fb61693061bb9b10";

  private Executor executor = Executors.newSingleThreadExecutor();

  private TextView tv;
  private Handler handler;
  private Runnable displayResult;
  private retrofit2.Call<Result> callConnection;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    handler = new Handler(Looper.getMainLooper());

    tv = findViewById(R.id.tv);

    getPhotosViaRetrofit();
  }

  private void getPhotosViaRetrofit(){
    Retrofit retrofit = new Retrofit.Builder()
      .baseUrl("https://api.flickr.com/")
      .addConverterFactory(GsonConverterFactory.create())
      .build();

    GitHubService gitHubService = retrofit.create(GitHubService.class);

    callConnection = gitHubService.listRepos("flickr.photos.getRecent", API_KEY, "json", 1);

    callConnection.enqueue(new retrofit2.Callback<Result>() {
        @Override public void onResponse(retrofit2.Call<Result> call, retrofit2.Response<Result> response) {
          StringBuilder builder = new StringBuilder();

          List<PhotoItem> photos = response.body().getPhotos().getPhoto();

          for (int i = 0; i < photos.size() && i < 3; i++) {
            builder.append(photos.get(i).getTitle());
          }
          tv.setText(builder.toString());
        }

        @Override public void onFailure(retrofit2.Call<Result> call, Throwable t) {

        }
      }
    );
  }

  public interface GitHubService {
    @GET("services/rest/")
    retrofit2.Call<Result> listRepos(
      @Query("method") String method,
      @Query("api_key") String apiKey,
      @Query("format") String format,
      @Query("nojsoncallback") int noJsonCallback
    );


  }

  private void getPhotosViaOkHttp() {
    final OkHttpClient client = new OkHttpClient();

    Request request = new Request.Builder()
      .url("https://api.flickr.com/services/rest/?" +
        "method=flickr.photos.getRecent&" +
        "api_key=" + API_KEY + "&" +
        "format=json&" +
        "nojsoncallback=1")
      .get()
      .build();

    client.newCall(request).enqueue(new Callback() {
      @Override public void onFailure(Call call, IOException e) {

        e.printStackTrace();
      }

      @Override public void onResponse(Call call, Response response) throws IOException {
        ResponseBody body = response.body();
        Gson gson = new Gson();

        Result result = gson.fromJson(new String(body.bytes()), Result.class);

        StringBuilder builder = new StringBuilder();
        List<PhotoItem> photos = result.getPhotos().getPhoto();

        for (int i = 0; i < photos.size() && i < 3; i++) {
          builder.append(photos.get(i).getTitle());
        }

        final String titles = builder.toString();

        displayResult = new Runnable() {
          @Override public void run() {
            tv.setText(titles);
          }
        };
        handler.post(displayResult);
      }
    });
  }

  private void getPhotosViaHttpUrlConnection() {
    executor.execute(new Runnable() {
      @Override public void run() {
        InputStream inputStream = null;
        try {
          URL url = new URL("https://api.flickr.com/services/rest/?" +
            "method=flickr.photos.getRecent&" +
            "api_key=" + API_KEY + "&" +
            "format=json&" +
            "nojsoncallback=1");
          HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
          inputStream = urlConnection.getInputStream();
          final String json = getStringFromInputStream(inputStream);

          Gson gson = new Gson();

          Result result = gson.fromJson(json, Result.class);

          StringBuilder builder = new StringBuilder();
          List<PhotoItem> photos = result.getPhotos().getPhoto();

          for (int i = 0; i < photos.size() && i < 3; i++) {
            builder.append(photos.get(i).getTitle());
          }

          final String titles = builder.toString();

          displayResult = new Runnable() {
            @Override public void run() {
              tv.setText(titles);
            }
          };
          handler.post(displayResult);

        } catch (MalformedURLException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        } finally {
          if (inputStream != null){
            try {
              inputStream.close();
            } catch (IOException ignored) { }
          }
        }
      }
    });
  }

  @Override protected void onStop() {
    super.onStop();
    if (displayResult != null){
      handler.removeCallbacks(displayResult);
    }
    if (callConnection != null){
      callConnection.cancel();
    }
  }

  public static String getStringFromInputStream(InputStream stream) throws IOException {
    int n = 0;
    char[] buffer = new char[1024 * 4];
    InputStreamReader reader = new InputStreamReader(stream, "UTF8");
    StringWriter writer = new StringWriter();
    while (-1 != (n = reader.read(buffer))) writer.write(buffer, 0, n);
    return writer.toString();
  }
}
