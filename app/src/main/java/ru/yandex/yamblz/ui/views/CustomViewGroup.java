package ru.yandex.yamblz.ui.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

/**
 * Created by user on 22.07.16.
 */

public class CustomViewGroup extends ViewGroup {

    int deviceWidth;

    public CustomViewGroup(Context context) {
        super(context);
        init(context);
    }

    public CustomViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomViewGroup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context) {
        final Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point deviceDisplay = new Point();
        display.getSize(deviceDisplay);
        deviceWidth = deviceDisplay.x;
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int childCount = getChildCount();
        int leftChild = left;
        View viewChild;
        for (int i = 0; i < childCount; i++) {
            viewChild = getChildAt(i);

            if (viewChild.getVisibility() == GONE) {
                continue;
            }

            int widthChild = leftChild + viewChild.getMeasuredWidth();
            int heightChild = top + viewChild.getMeasuredHeight();
            viewChild.layout(leftChild, top, widthChild, heightChild);
            leftChild = widthChild;
        }
    }


    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int childCount = getChildCount();
        int maxHeight = 0;
        int sumWidth = 0;
        View viewChildWithMatchParent = null;

        View viewChild;
        for (int i = 0; i < childCount; ++i) {
            viewChild = getChildAt(i);

            if (viewChild.getVisibility() == GONE) {
                continue;
            }

            LayoutParams childLayoutParams = viewChild.getLayoutParams();
            if (childLayoutParams.width == LayoutParams.MATCH_PARENT) {
                viewChildWithMatchParent = getChildAt(i);
            } else {
                measureChild(viewChild, widthMeasureSpec, heightMeasureSpec);
                sumWidth += viewChild.getMeasuredWidth();
                maxHeight = Math.max(maxHeight, viewChild.getMeasuredHeight());
            }
        }

        if (viewChildWithMatchParent != null) {
            LayoutParams childMatchParentLayoutParams = viewChildWithMatchParent.getLayoutParams();

            measureChild(viewChildWithMatchParent,
                    MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec) - sumWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(childMatchParentLayoutParams.height, MeasureSpec.EXACTLY)
            );
            sumWidth += viewChildWithMatchParent.getMeasuredWidth();
        }

        setMeasuredDimension(resolveSize(sumWidth, widthMeasureSpec),
                resolveSize(maxHeight, heightMeasureSpec));
    }
}