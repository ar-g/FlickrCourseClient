package ar_g.flickrcourseclient.feed;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ar_g.flickrcourseclient.App;
import ar_g.flickrcourseclient.R;
import ar_g.flickrcourseclient.model.PhotoItem;
import ar_g.flickrcourseclient.model.Result;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static ar_g.flickrcourseclient.feed.FlickrApi.API_KEY;

public class FeedActivity extends AppCompatActivity {
  private Disposable disposable;
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

    disposable = api.listRepos("flickr.photos.getRecent", API_KEY, "json", 1)
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe(new Consumer<Result>() {
        @Override public void accept(Result result) throws Exception {
          List<PhotoItem> photos = result.getPhotos().getPhoto();

          FeedAdapter adapter = new FeedAdapter(photos);

          recyclerView.setAdapter(adapter);
        }
      }, new Consumer<Throwable>() {
        @Override public void accept(Throwable throwable) throws Exception {
          Toast.makeText(FeedActivity.this, throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
      });
  }

  @Override protected void onStop() {
    super.onStop();
    if (disposable != null){
      disposable.dispose();
    }
  }
}
