package ar_g.flickrcourseclient.feed;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import ar_g.flickrcourseclient.R;
import ar_g.flickrcourseclient.model.PhotoItem;

class FeedViewHolder extends RecyclerView.ViewHolder {
  private ImageView imageView;

  public FeedViewHolder(@NonNull View itemView) {
    super(itemView);
    imageView = itemView.findViewById(R.id.imageView);
  }

  public void bind(PhotoItem photoItem) {
    String url = String.format(
      "https://farm%s.staticflickr.com/%s/%s_%s.jpg",
      photoItem.getFarm(),
      photoItem.getServer(),
      photoItem.getId(),
      photoItem.getSecret()
    );
    Glide.with(imageView)
      .load(url)
      .into(imageView);
  }
}
