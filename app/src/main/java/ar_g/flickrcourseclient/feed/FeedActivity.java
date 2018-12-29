package ar_g.flickrcourseclient.feed;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ar_g.flickrcourseclient.App;
import ar_g.flickrcourseclient.R;
import ar_g.flickrcourseclient.model.PhotoItem;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static ar_g.flickrcourseclient.feed.FlickrApi.API_KEY;

public class FeedActivity extends AppCompatActivity {
  private CompositeDisposable compositeDisposable = new CompositeDisposable();
  private RecyclerView recyclerView;
  private EditText etSearch;
  private List<PhotoItem> photos = new ArrayList<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    recyclerView = findViewById(R.id.rv);
    etSearch = findViewById(R.id.etSearch);

    GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
    recyclerView.setLayoutManager(gridLayoutManager);


    observeTextChanges();

    getPhotosViaRetrofit();
  }

  private void observeTextChanges() {
    Observable<String> textChangesStream = Observable.create(emitter -> {
      TextWatcher watcher = new TextWatcher() {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override public void afterTextChanged(Editable s) {
          if (!emitter.isDisposed()) {
            emitter.onNext(s.toString());
          }
        }
      };
      etSearch.addTextChangedListener(watcher);

      emitter.setCancellable(() -> etSearch.removeTextChangedListener(watcher));
    });

    FlickrApi flickrApi = App.getApp(this).getFlickrApi();

    compositeDisposable.add(
      textChangesStream
        .observeOn(Schedulers.io())
        .map(s -> s.trim())
        .filter(s -> s.length() > 3)
        .debounce(500, TimeUnit.MILLISECONDS)
        .switchMap(s -> flickrApi.searchPhotos("flickr.photos.search", API_KEY, "json", 1, s))
        .map(result -> result.getPhotos().getPhoto())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(photos -> populateAdapter(photos), throwable -> showSnackBar(throwable))
    );
  }
  private void populateAdapter(List<PhotoItem> photos) {
    FeedAdapter adapter = new FeedAdapter(photos);
    recyclerView.setAdapter(adapter);
  }

  private void showSnackBar(Throwable throwable) {
    Snackbar.make(etSearch, throwable.getLocalizedMessage(), Snackbar.LENGTH_INDEFINITE)
      .setAction("Повторить запрос", v -> {
        FlickrApi flickrApi = App.getApp(this).getFlickrApi();

        flickrApi.searchPhotos("flickr.photos.search", API_KEY, "json", 1, etSearch.getText().toString())
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .map(result -> result.getPhotos().getPhoto())
          .subscribe(photos -> populateAdapter(photos), t -> showSnackBar(t));

        observeTextChanges();
      })
      .show();
  }


  private void getPhotosViaRetrofit(){
    FlickrApi api = App.getApp(this).getFlickrApi();

    //Observable<Long> interval = Observable.interval(3, TimeUnit.SECONDS);
    Observable<List<PhotoItem>> apiRequest = api.recentPhotos("flickr.photos.getRecent", API_KEY, "json", 1)
      .map(result -> result.getPhotos().getPhoto());
    Observable<List<PhotoItem>> photosInMemory = Observable.just(photos);

    Observable<List<PhotoItem>> streamOfPhotos = Observable.concat(photosInMemory, apiRequest);

    compositeDisposable.add( /*interval
      .flatMap(__ -> apiRequest)*/
      streamOfPhotos
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe(photos -> {
        populateAdapter(photos);
        }, throwable -> showErrorMsg(throwable)
      )
    );
  }
  private void showErrorMsg(Throwable throwable) {
    Toast.makeText(FeedActivity.this, throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    compositeDisposable.clear();
  }
}
