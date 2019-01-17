package ar_g.flickrcourseclient.di;

import ar_g.flickrcourseclient.feed.FeedActivity;
import dagger.Component;

@Component(modules = AppModule.class)
public interface AppComponent {

  void inject(FeedActivity feedActivity);
}
