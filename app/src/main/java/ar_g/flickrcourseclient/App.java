package ar_g.flickrcourseclient;

import android.app.Application;
import android.content.Context;

import ar_g.flickrcourseclient.di.AppComponent;
import ar_g.flickrcourseclient.di.DaggerAppComponent;

public class App extends Application {

  AppComponent appComponent;

  public AppComponent getAppComponent() {
    if (appComponent == null){
      appComponent = DaggerAppComponent.builder().build();
    }
    return appComponent;
  }

  public static App getApp(Context context){
    return ((App) context.getApplicationContext());
  }
}
