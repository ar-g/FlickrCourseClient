package ar_g.flickrcourseclient.feed;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.List;

import ar_g.flickrcourseclient.App;
import ar_g.flickrcourseclient.R;
import ar_g.flickrcourseclient.model.PhotoItem;
import ar_g.flickrcourseclient.model.Result;

import static ar_g.flickrcourseclient.feed.FlickrApi.API_KEY;

public class MainActivity extends AppCompatActivity {
  private retrofit2.Call<Result> callConnection;
  private RecyclerView recyclerView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    recyclerView = findViewById(R.id.rv);

    GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
    recyclerView.setLayoutManager(gridLayoutManager);

    getPhotosViaRetrofit();
  }

  private void getPhotosViaRetrofit(){
    FlickrApi api = App.getApp(this).getFlickrApi();

    callConnection = api.listRepos("flickr.photos.getRecent", API_KEY, "json", 1);
    callConnection.enqueue(new retrofit2.Callback<Result>() {
        @Override public void onResponse(retrofit2.Call<Result> call, retrofit2.Response<Result> response) {
          List<PhotoItem> photos = response.body().getPhotos().getPhoto();

          FeedAdapter adapter = new FeedAdapter(photos);

          recyclerView.setAdapter(adapter);
        }

        @Override public void onFailure(retrofit2.Call<Result> call, Throwable t) { }
      }
    );
  }

  @Override protected void onStop() {
    super.onStop();
    if (callConnection != null){
      callConnection.cancel();
    }
  }
}
