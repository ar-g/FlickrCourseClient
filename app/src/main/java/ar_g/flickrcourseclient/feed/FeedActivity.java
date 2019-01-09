package ar_g.flickrcourseclient.feed;

import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import ar_g.flickrcourseclient.App;
import ar_g.flickrcourseclient.R;
import ar_g.flickrcourseclient.model.PhotoItem;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static ar_g.flickrcourseclient.feed.FlickrApi.API_KEY;

public class FeedActivity extends AppCompatActivity {
  private CompositeDisposable compositeDisposable = new CompositeDisposable();
  private RecyclerView recyclerView;
  private EditText etSearch;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    recyclerView = findViewById(R.id.rv);
    etSearch = findViewById(R.id.etSearch);

    GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
    recyclerView.setLayoutManager(gridLayoutManager);


    FeedViewModel feedViewModel = ViewModelProviders.of(this, new ViewModelProvider.Factory() {
      @NonNull @Override public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T)
          new FeedViewModel(
            new FeedUseCase(
              new FeedRepository(
                App.getApp(FeedActivity.this).getFlickrApi()
              )
            )
          );
      }
    }).get(FeedViewModel.class);

    feedViewModel.loadRecentPhotos();
    feedViewModel.getPhotosLiveData().observe(this, photoItems -> {
      populateAdapter(photoItems);
    });

    feedViewModel.bindToQuery();
    TextWatcher watcher = new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
        feedViewModel.updateQuery(s.toString());
      }

      @Override public void afterTextChanged(Editable s) {

      }
    };
    etSearch.addTextChangedListener(watcher);
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

        //todo переподписаться на изменения текста
      })
      .show();
  }

  private void showErrorMsg(Throwable throwable) {
    Toast.makeText(FeedActivity.this, throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    compositeDisposable.clear();
  }
}
