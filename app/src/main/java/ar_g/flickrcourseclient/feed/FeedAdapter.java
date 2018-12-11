package ar_g.flickrcourseclient.feed;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ar_g.flickrcourseclient.R;
import ar_g.flickrcourseclient.model.PhotoItem;

public class FeedAdapter extends RecyclerView.Adapter<FeedViewHolder> {
  private final List<PhotoItem> photos;

  public FeedAdapter(List<PhotoItem> photos) {this.photos = photos;}

  @NonNull @Override public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
    LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
    View view = layoutInflater.inflate(R.layout.feed_item, viewGroup, false);
    return new FeedViewHolder(view);
  }

  @Override public void onBindViewHolder(@NonNull FeedViewHolder feedViewHolder, int position) {
    feedViewHolder.bind(photos.get(position));
  }

  @Override public int getItemCount() {
    return photos.size();
  }
}
