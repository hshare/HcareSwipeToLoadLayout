package com.aspsine.swipetoloadlayout.demo.view.footer;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.SwipeLoadMoreFooterLayout;
import com.aspsine.swipetoloadlayout.template.R;

import static android.view.animation.Animation.RESTART;
import static android.view.animation.Animation.REVERSE;

/**
 * Created by hcare on 2015/9/2.
 */
public class HCareLoadMoreFooterView extends SwipeLoadMoreFooterLayout {

    private TextView tvLoadMore;
    private ImageView ivSuccess;

    private int mFooterHeight;

    private ObjectAnimator objectAnimator;

    public HCareLoadMoreFooterView(Context context) {
        this(context, null);
    }

    public HCareLoadMoreFooterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HCareLoadMoreFooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mFooterHeight = getResources().getDimensionPixelOffset(R.dimen.load_more_footer_height_classic);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        tvLoadMore = (TextView) findViewById(R.id.tvLoadMore);
        ivSuccess = (ImageView) findViewById(R.id.ivSuccess);
        objectAnimator = ObjectAnimator.ofFloat(ivSuccess, "rotation", 360f, 0f);
        objectAnimator.setRepeatCount(-1);
        objectAnimator.setRepeatMode(RESTART);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setDuration(1000);
        objectAnimator.start();
    }

    @Override
    public void onPrepare() {
//        ivSuccess.setVisibility(GONE);
    }

    @Override
    public void onMove(int y, boolean isComplete, boolean automatic) {
        if (!isComplete) {
//            ivSuccess.setVisibility(GONE);
            if (-y >= mFooterHeight) {
                tvLoadMore.setText("释放加载...");
            } else {
                tvLoadMore.setText("上滑加载...");
            }
        }
    }

    @Override
    public void onLoadMore() {
        tvLoadMore.setText("正在加载...");
    }

    @Override
    public void onRelease() {

    }

    @Override
    public void onComplete() {
//        ivSuccess.setVisibility(VISIBLE);
        objectAnimator.cancel();
    }

    @Override
    public void onReset() {
        objectAnimator.start();
//        ivSuccess.setVisibility(GONE);
    }
}
