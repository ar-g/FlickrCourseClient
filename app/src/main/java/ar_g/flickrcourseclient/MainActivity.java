package ar_g.flickrcourseclient;

import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

  public static final String API_KEY = "8af1e413005cdf2f8552ea2f690a3676";

  private Executor executor = Executors.newFixedThreadPool(3);
  private TextView tv;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    tv = findViewById(R.id.tv);

    executeUsingHttpUrlConnection();

  }

  private void executeUsingHttpUrlConnection() {
    //1. handle unsubscription
    //2. handle canceling request

    executor.execute(new Runnable() {
      @Override public void run() {
        URL url = null;
        HttpURLConnection urlConnection = null;
        String string = "";
        try {
          url = new URL("https://api.flickr.com/services/rest/?method=flickr.photos.getRecent&api_key=" + API_KEY + "&format=json&nojsoncallback=1");
          urlConnection = (HttpURLConnection) url.openConnection();
          InputStream in = new BufferedInputStream(urlConnection.getInputStream());
           string = getStringFromInputStream(in);

        } catch (MalformedURLException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        } finally {
          if (urlConnection != null) {
            urlConnection.disconnect();
          }
        }

        final String finalString = string;

        runOnUiThread(new Runnable() {
          @Override public void run() {
            tv.setText(finalString);
          }
        });
      }
    });
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
