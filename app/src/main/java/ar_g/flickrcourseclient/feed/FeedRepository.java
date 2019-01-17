package ar_g.flickrcourseclient.feed;

import java.util.List;

import javax.inject.Inject;

import ar_g.flickrcourseclient.model.PhotoItem;
import io.reactivex.Observable;

import static ar_g.flickrcourseclient.feed.FlickrApi.API_KEY;

public class FeedRepository implements IFeedRepository {
  private final FlickrApi api;

  @Inject
  public FeedRepository(FlickrApi api) {this.api = api;}

  @Override public Observable<List<PhotoItem>> recentPhotos(){
    return api.recentPhotos("flickr.photos.getRecent", API_KEY, "json", 1)
      .map(result -> result.getPhotos().getPhoto());
  }

  @Override public Observable<List<PhotoItem>> searchPhotos(String query){
    return api.searchPhotos("flickr.photos.search", API_KEY, "json", 1,query)
        .map(result -> result.getPhotos().getPhoto());
  }
}
