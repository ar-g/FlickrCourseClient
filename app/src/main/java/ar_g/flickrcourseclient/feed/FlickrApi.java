package ar_g.flickrcourseclient.feed;

import ar_g.flickrcourseclient.model.Result;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FlickrApi {
  String API_KEY = "90b99338fcc5e746fb61693061bb9b10";

  @GET("services/rest/")
  Observable<Result> listRepos(
    @Query("method") String method,
    @Query("api_key") String apiKey,
    @Query("format") String format,
    @Query("nojsoncallback") int noJsonCallback
  );
}
