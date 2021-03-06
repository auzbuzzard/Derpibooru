package derpibooru.derpy.ui.views.htmltextview;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;

import butterknife.Bind;
import butterknife.ButterKnife;
import derpibooru.derpy.R;
import derpibooru.derpy.ui.views.htmltextview.imageactions.EmbeddedImageAction;
import derpibooru.derpy.ui.views.htmltextview.imageactions.ImageAction;

public class ImageActionDialogFragment extends DialogFragment {
    public static final String EXTRAS_IMAGE_ACTION_REPRESENTATION = "derpibooru.derpy.ImageActionStringRepresentation";

    @Bind(R.id.imageView) ImageView imageView;
    @Bind(R.id.buttonViewImage) AppCompatButton buttonViewImage;

    private ImageAction mImageAction;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog d = super.onCreateDialog(savedInstanceState);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return d;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_image_fragment_embedded_image_dialog, container, false);
        ButterKnife.bind(this, v);
        mImageAction = ImageAction.fromStringRepresentation(
                getArguments().getString(EXTRAS_IMAGE_ACTION_REPRESENTATION));
        loadImage();
        if (mImageAction instanceof EmbeddedImageAction) {
            buttonViewImage.setText(String.format(getString(R.string.embedded_image_view_image),
                                                  ((EmbeddedImageAction) mImageAction).getImageId()));
            buttonViewImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(getContext())
                            .setMessage("Coming soon! Stay tuned for the application updates.")
                            .create().show();
                }
            });
        } else {
            ((ViewGroup) v).removeView(buttonViewImage);
            buttonViewImage = null;
        }
        return v;
    }

    private void loadImage() {
        int maxWidth = getResources().getDisplayMetrics().widthPixels;
        int maxHeight = getResources().getDisplayMetrics().heightPixels;
        GlideViewTarget target = new GlideViewTarget(imageView, maxWidth, maxHeight);
        Glide.with(this)
                .load(mImageAction.getImageSource())
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .into(target);
    }

    private class GlideViewTarget extends ViewTarget<ImageView, GlideDrawable> {
        private final View mTarget;
        private final int mMaxTargetWidth;
        private final int mMaxTargetHeight;

        private GlideViewTarget(ImageView target, int maxTargetWidth, int maxTargetHeight) {
            super(target);
            mTarget = target;
            mMaxTargetWidth = maxTargetWidth;
            mMaxTargetHeight = maxTargetHeight;
        }

        @Override
        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
            AdaptiveBoundsGlideResourceDrawable drawable = new AdaptiveBoundsGlideResourceDrawable();
            drawable.setResource(resource, mTarget, mMaxTargetWidth, mMaxTargetHeight);
            if (drawable.isAnimated()) {
                drawable.playAnimated(true);
            }
        }
    }
}
