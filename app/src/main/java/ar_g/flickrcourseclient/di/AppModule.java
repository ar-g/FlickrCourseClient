package ar_g.flickrcourseclient.di;

import ar_g.flickrcourseclient.feed.FeedRepository;
import ar_g.flickrcourseclient.feed.FlickrApi;
import ar_g.flickrcourseclient.feed.IFeedRepository;
import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class AppModule {
  @Provides public static FlickrApi provideFlickrApi() {
    Retrofit retrofit = new Retrofit.Builder()
      .baseUrl("https://api.flickr.com/")
      .addConverterFactory(GsonConverterFactory.create())
      .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
      .build();
    return retrofit.create(FlickrApi.class);
  }

  @Provides public static IFeedRepository provideFeedRepository(FlickrApi flickrApi){
    return new FeedRepository(flickrApi);
  }
}
