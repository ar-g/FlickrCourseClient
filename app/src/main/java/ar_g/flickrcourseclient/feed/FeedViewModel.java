package ar_g.flickrcourseclient.feed;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import ar_g.flickrcourseclient.model.PhotoItem;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class FeedViewModel extends ViewModel {
  private MutableLiveData<List<PhotoItem>> photosLiveData = new MutableLiveData<>();
  private PublishSubject<String> querySubject = PublishSubject.create();
  private CompositeDisposable compositeDisposable = new CompositeDisposable();
  private final FeedUseCase feedUseCase;

  public FeedViewModel(FeedUseCase feedUseCase) {
    this.feedUseCase = feedUseCase;
  }

  public void loadRecentPhotos(){
    compositeDisposable.add(
      feedUseCase.recentPhotos()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(photoItems -> photosLiveData.setValue(photoItems))
      //todo добавить обработку ошибок
    );
  }

  public void bindToQuery(){
    compositeDisposable.add(
      querySubject
        .observeOn(Schedulers.io())
        .distinctUntilChanged()
        .map(s -> s.trim())
        .filter(s -> s.length() > 3)
        .debounce(500, TimeUnit.MILLISECONDS)
        .switchMap(s -> feedUseCase.searchPhotos(s))
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(photoItems -> photosLiveData.setValue(photoItems))
      //todo добавить обработку ошибок
    );
  }

  public void updateQuery(String query){
    querySubject.onNext(query);
  }

  public MutableLiveData<List<PhotoItem>> getPhotosLiveData() {
    return photosLiveData;
  }

  @Override protected void onCleared() {
    super.onCleared();
    compositeDisposable.clear();
  }
}
