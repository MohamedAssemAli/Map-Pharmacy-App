package app.pharmacy.map.com.mappharmacyapp.Utils;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

public class Imageutility {

    public static void circularImage(Context context, ImageView imageView, int imgPath, int placeHolder, int error) {
        Glide.with(context)
                .load(imgPath)
                .transition(new DrawableTransitionOptions().crossFade())
                .apply(new RequestOptions().circleCrop())
                .apply(new RequestOptions().placeholder(placeHolder))
                .apply(new RequestOptions().error(error))
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(imageView);
    }

    public static void circularImage(Context context, ImageView imageView, Uri imgPath, int placeHolder, int error) {
        Glide.with(context)
                .load(imgPath)
                .transition(new DrawableTransitionOptions().crossFade())
                .apply(new RequestOptions().circleCrop())
                .apply(new RequestOptions().placeholder(placeHolder))
                .apply(new RequestOptions().error(error))
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(imageView);
    }

    public static void circularImage(Context context, ImageView imageView, String imgPath, int placeHolder, int error) {
        Glide.with(context)
                .load(imgPath)
                .transition(new DrawableTransitionOptions().crossFade())
                .apply(new RequestOptions().circleCrop())
                .apply(new RequestOptions().placeholder(placeHolder))
                .apply(new RequestOptions().error(error))
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(imageView);
    }

    public static void fitImage(Context context, ImageView imageView, int imgPath, int placeHolder, int error) {
        Glide.with(context)
                .load(imgPath)
                .transition(new DrawableTransitionOptions().crossFade())
                .apply(new RequestOptions().fitCenter())
                .apply(new RequestOptions().placeholder(placeHolder))
                .apply(new RequestOptions().error(error))
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(imageView);
    }

    public static void centerImage(Context context, ImageView imageView, int imgPath, int placeHolder, int error) {
        Glide.with(context)
                .load(imgPath)
                .transition(new DrawableTransitionOptions().crossFade())
                .apply(new RequestOptions().centerInside())
                .apply(new RequestOptions().placeholder(placeHolder))
                .apply(new RequestOptions().error(error))
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(imageView);
    }

    public static void fitImage(Context context, ImageView imageView, String imgPath, int placeHolder, int error) {
        Glide.with(context)
                .load(imgPath)
                .transition(new DrawableTransitionOptions().crossFade())
                .apply(new RequestOptions().fitCenter())
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .apply(new RequestOptions().placeholder(placeHolder))
                .apply(new RequestOptions().error(error))
                .into(imageView);
    }

    public static void fitImage(Context context, ImageView imageView, String imgPath) {
        Glide.with(context)
                .load(imgPath)
                .transition(new DrawableTransitionOptions().crossFade())
                .apply(new RequestOptions().fitCenter())
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(imageView);
    }
}
