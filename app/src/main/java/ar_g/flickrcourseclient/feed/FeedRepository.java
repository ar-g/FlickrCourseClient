package ar_g.flickrcourseclient.feed;

import java.util.List;

import ar_g.flickrcourseclient.model.PhotoItem;
import io.reactivex.Observable;

import static ar_g.flickrcourseclient.feed.FlickrApi.API_KEY;

public class FeedRepository {
  private final FlickrApi api;

  public FeedRepository(FlickrApi api) {this.api = api;}

  public Observable<List<PhotoItem>> recentPhotos(){
    return api.recentPhotos("flickr.photos.getRecent", API_KEY, "json", 1)
      .map(result -> result.getPhotos().getPhoto());
  }

  public Observable<List<PhotoItem>> searchPhotos(String query){
    return api.searchPhotos("flickr.photos.search", API_KEY, "json", 1,query)
        .map(result -> result.getPhotos().getPhoto());
  }
}
