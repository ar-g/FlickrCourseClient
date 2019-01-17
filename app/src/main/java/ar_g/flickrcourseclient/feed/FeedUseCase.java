package ar_g.flickrcourseclient.feed;

import java.util.List;

import javax.inject.Inject;

import ar_g.flickrcourseclient.model.PhotoItem;
import io.reactivex.Observable;

public class FeedUseCase {
  private final IFeedRepository IFeedRepository;

  @Inject
  public FeedUseCase(IFeedRepository IFeedRepository) {this.IFeedRepository = IFeedRepository;}

  public Observable<List<PhotoItem>> recentPhotos(){
    return IFeedRepository.recentPhotos();
  }

  public Observable<List<PhotoItem>> searchPhotos(String query){
    return IFeedRepository.searchPhotos(query);
  }
}
