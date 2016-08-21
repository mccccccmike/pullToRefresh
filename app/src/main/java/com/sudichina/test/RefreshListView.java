package com.sudichina.test;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mike on 2016/8/20.
 */
public class RefreshListView extends ListView implements AbsListView.OnScrollListener {
    private final Context mContext;
    private View mHeader;
    private int headerHeight;
    private Toast toast;

    //    private Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            refreshComplete();
//        }
//    };

    private OnRefreshListener mListener;

    public void setOnRefreshListener(OnRefreshListener listener) {
        mListener = listener;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.mFirstVisibleItem = firstVisibleItem;
    }

    public interface OnRefreshListener {
        void onRefresh();
    }


    private int mFirstVisibleItem;
    private int mScrollState;
    private boolean isTop;//是否是listView的最顶部

    private int state = NORMAL;
    private float startY;
    private static final int NORMAL = 0;
    private static final int PULL = 1;
    private static final int RELEASE = 2;
    private static final int REFRESHING = 3;

    public RefreshListView(Context context) {
        this(context, null);
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        addHeader(context);
        setOnScrollListener(this);
        mContext = context;

    }

    private void toast(String msg) {
        if (toast == null) {
            toast = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
        }
        toast.setText(msg);
        toast.show();
    }

    private void addHeader(Context context) {
        mHeader = LayoutInflater.from(context).inflate(R.layout.header, null);
        measureHeader(mHeader);
        headerHeight = mHeader.getMeasuredHeight();
        Toast.makeText(RefreshListView.this.getContext(), "headerHeight = " + headerHeight, Toast.LENGTH_SHORT).show();
        Log.d("tag", "headerHeight" + headerHeight);
        topPadding(-headerHeight);
        addHeaderView(mHeader);

    }

    private void topPadding(int topPadding) {
        mHeader.setPadding(mHeader.getPaddingLeft(), topPadding, mHeader.getPaddingRight(), mHeader.getPaddingBottom());
        invalidate();
    }

    private void measureHeader(View view) {
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp == null) {
            lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int widthSpec = ViewGroup.getChildMeasureSpec(0, 0, lp.width);
//        int heightSpec = ViewGroup.getChildMeasureSpec(0,0,lp.height);
        int heightSpec;
        int tempHeight = lp.height;
        if (tempHeight > 0) {
            heightSpec = MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.EXACTLY);
        } else {
            heightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }

        view.measure(widthSpec, heightSpec);
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d("hlm", "down");
                Log.d("hlm", "firstVisibleItem = " + mFirstVisibleItem);
                if (mFirstVisibleItem == 0) {
                    isTop = true;
                    startY = ev.getY();
                    Log.d("hlm", "处于listview top");
                    toast("处于listview top");
                }
                break;
            case MotionEvent.ACTION_MOVE:
                onMove(ev);
                break;
            case MotionEvent.ACTION_UP:
                if (state == RELEASE) {
                    state = REFRESHING;
                    Log.d("hlm", "REFRESHING");
                    toast("REFRESHING");
                    refreshViewState();
                    mListener.onRefresh();
//                    refreshComplete();
                } else {
                    state = NORMAL;
                    Log.d("hlm", "NORMAL");
                    toast("NORMAL");
                    isTop = false;
                    refreshViewState();
                }
                break;
        }

        return super.onTouchEvent(ev);
    }

    private void onMove(MotionEvent ev) {
        if (!isTop) {
            return;
        }

        float currentY = ev.getY();
        int space = (int) (currentY - startY);
        int topPadding = space - headerHeight;
        switch (state) {
            case NORMAL:
                if (space > 0) {
                    state = PULL;
                    refreshViewState();
                    Log.d("hlm", "NORMAL --> PULL");

                }
                break;
            case PULL:
                Log.d("hlm", "PULL");
                toast("PULL");
                topPadding(topPadding);
                if (space > headerHeight + 30) {
                    state = RELEASE;
                    Log.d("hlm", "PULL --> RELEASE");
                    toast("PULL --> RELEASE");
                    refreshViewState();
                }
                break;
            case RELEASE:
                Log.d("hlm", "RELEASE");
                toast("RELEASE");
                topPadding(topPadding);
                if (space < headerHeight + 30) {
                    state = PULL;
                    Log.d("hlm", "RELEASE --> PULL");
                    toast("RELEASE --> PULL");
                    refreshViewState();
                } else if (space <= 0) {
                    state = NORMAL;
                    Log.d("hlm", " RELEASE --> NORMAL");
                    toast("RELEASE --> NORMAL");
                    isTop = false;
                    refreshViewState();
                }
                break;

        }

    }

    private void refreshViewState() {
        TextView tip = (TextView) mHeader.findViewById(R.id.tip);
        ImageView arrow = (ImageView) mHeader.findViewById(R.id.arrow);
        ProgressBar progress = (ProgressBar) mHeader.findViewById(R.id.progress);
        RotateAnimation anim = new RotateAnimation(0, 180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(500);
        anim.setFillAfter(true);

        RotateAnimation anim2 = new RotateAnimation(180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(500);
        anim.setFillAfter(true);
        switch (state) {
            case NORMAL:
                arrow.clearAnimation();
                topPadding(-headerHeight);
                break;
            case PULL:
                arrow.setVisibility(VISIBLE);
                progress.setVisibility(GONE);
                tip.setText("下拉可以刷新");
                arrow.clearAnimation();
                arrow.setAnimation(anim2);
                break;
            case RELEASE:
                arrow.setVisibility(VISIBLE);
                progress.setVisibility(GONE);
                tip.setText("松开可以刷新");
                arrow.clearAnimation();
                arrow.setAnimation(anim);
                break;
            case REFRESHING:
                topPadding(0);
                arrow.setVisibility(GONE);
                progress.setVisibility(VISIBLE);
                tip.setText("正在刷新");
                arrow.clearAnimation();
                break;
        }
    }

    public void refreshComplete() {
        state = NORMAL;
        isTop = false;
        refreshViewState();

        TextView tv = (TextView) mHeader.findViewById(R.id.last_update_time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 hh:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String time = format.format(date);
        tv.setText(time);
    }
}
