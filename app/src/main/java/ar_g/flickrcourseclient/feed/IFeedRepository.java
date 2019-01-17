package ar_g.flickrcourseclient.feed;

import java.util.List;

import ar_g.flickrcourseclient.model.PhotoItem;
import io.reactivex.Observable;

public interface IFeedRepository {
  Observable<List<PhotoItem>> recentPhotos();
  Observable<List<PhotoItem>> searchPhotos(String query);
}
