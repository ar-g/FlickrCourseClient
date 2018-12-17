package ar_g.flickrcourseclient;

import android.app.Application;
import android.content.Context;

import ar_g.flickrcourseclient.feed.FlickrApi;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class App extends Application {

  private FlickrApi flickrApi;

  public FlickrApi getFlickrApi(){
    if (flickrApi == null) {
      Retrofit retrofit = new Retrofit.Builder()
        .baseUrl("https://api.flickr.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build();

      flickrApi = retrofit.create(FlickrApi.class);
    }
    return flickrApi;
  }

  public static App getApp(Context context){
    return ((App) context.getApplicationContext());
  }
}
