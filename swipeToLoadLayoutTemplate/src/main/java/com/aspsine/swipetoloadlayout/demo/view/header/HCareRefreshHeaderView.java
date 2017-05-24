package com.aspsine.swipetoloadlayout.demo.view.header;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.SwipeLoadMoreFooterLayout;
import com.aspsine.swipetoloadlayout.SwipeRefreshHeaderLayout;
import com.aspsine.swipetoloadlayout.template.R;

import static android.view.animation.Animation.RESTART;

/**
 * Created by hcare on 2015/9/2.
 */
public class HCareRefreshHeaderView extends SwipeRefreshHeaderLayout {

    private TextView tvLoadMore;
    private ImageView ivSuccess;

    private int mFooterHeight;

    private ObjectAnimator objectAnimator;

    public HCareRefreshHeaderView(Context context) {
        this(context, null);
    }

    public HCareRefreshHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HCareRefreshHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mFooterHeight = getResources().getDimensionPixelOffset(R.dimen.load_more_footer_height_classic);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        tvLoadMore = (TextView) findViewById(R.id.tvLoadMore);
        ivSuccess = (ImageView) findViewById(R.id.ivSuccess);
        objectAnimator = ObjectAnimator.ofFloat(ivSuccess, "rotation", 0f, 360f);
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
            if (y >= mFooterHeight) {
                tvLoadMore.setText("释放加载...");
            } else {
                tvLoadMore.setText("下滑加载...");
            }
        }
    }

    @Override
    public void onRefresh() {
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
