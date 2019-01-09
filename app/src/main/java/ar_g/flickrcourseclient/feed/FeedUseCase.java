package ar_g.flickrcourseclient.feed;

import java.util.List;

import ar_g.flickrcourseclient.model.PhotoItem;
import io.reactivex.Observable;

import static ar_g.flickrcourseclient.feed.FlickrApi.API_KEY;

public class FeedUseCase {
  private final FeedRepository feedRepository;

  public FeedUseCase(FeedRepository feedRepository) {this.feedRepository = feedRepository;}

  public Observable<List<PhotoItem>> recentPhotos(){
    return feedRepository.recentPhotos();
  }

  public Observable<List<PhotoItem>> searchPhotos(String query){
    return feedRepository.searchPhotos(query);
  }
}
