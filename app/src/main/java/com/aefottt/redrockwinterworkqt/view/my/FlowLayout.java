package com.aefottt.redrockwinterworkqt.view.my;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义流式布局
 */

public class FlowLayout extends ViewGroup {

    public FlowLayout(Context context) {
        super(context);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 获取MarginLayoutParams
     */
    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    /**
     * 设置子控件的测量模式和大小，根据所有子控件来设置每个子控件的宽高
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 获取父容器的测量模式和大小
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);

        // 如果父容器不为wrap_content，则手动获取其宽高
        int width = 0, height = 0;
        // 每一行的宽度和高度，通过不断比较让width获取最大的宽度，通过累加lineHeight获取最终的height值
        int lineWidth = 0, lineHeight = 0;
        // 遍历子控件设置父容器宽高
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            // 测量子控件的长宽
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            // 获取子控件的lp
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            // 获取子控件实际长度与宽度
            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin,
                    childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

            // 如果加上新的child宽度超过最大宽度，则开启新的一行，并且该child为下一行的第一个子控件
            if (lineWidth + childWidth > sizeWidth) {
                width = Math.max(lineWidth, childWidth); //取二者最大值
                lineWidth = childWidth; //开始记录下一行的宽度
                height += lineHeight; //叠加高度
                lineHeight = childHeight; //开始记录下一行的高度
            } else
            // 如果加上新child不超过最大宽度，则累加lineWidth，并记录最大lineHeight
            {
                lineWidth += childWidth;
                lineHeight = Math.max(lineHeight, childHeight);
            }
        }
        // 比较最后一行的宽度，并将最后一行高度添加
        width = Math.max(width, lineWidth);
        height += lineHeight;

        // 设置父控件长宽
        setMeasuredDimension(modeWidth == MeasureSpec.EXACTLY ? sizeWidth : width,
                modeHeight == MeasureSpec.EXACTLY ? sizeHeight : height);
    }

    // 存储所有的View
    private final List<List<View>> allViews = new ArrayList<>();
    // 存储每一行的高度
    private final List<Integer> lineHeights = new ArrayList<>();
    // 存储每一行的子View
    private List<View> lineViews = new ArrayList<>();

    // 布局子控件的位置
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        allViews.clear();
        lineHeights.clear();
        lineViews.clear();
        int width = getWidth();

        // 遍历所有子控件进行数组的初始化
        int lineWidth = 0, lineHeight = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            // 获取子控件的lp
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            // 获取子控件实际长度与宽度
            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin,
                    childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

            // 如果需要换行
            if (lineWidth + childWidth > width) {
                allViews.add(lineViews);
                lineHeights.add(lineHeight);
                // 重置
                lineViews = new ArrayList<>(); //TODO 注意这里不能用lineViews.clear()，坑了我半天
                lineWidth = 0;
            }
            // 更新lineWidth与lineHeight的值
            lineWidth += childWidth;
            lineHeight = Math.max(lineHeight, childHeight);
            lineViews.add(child);
        }
        // 记录最后一行
        lineHeights.add(lineHeight);
        allViews.add(lineViews);

        // 重新布局
        int top = 0, left = 0;
        for (int i = 0; i < allViews.size(); i++) {
            // 获取当行的所有子View
            lineViews = allViews.get(i);
            // 获取当前行的最大高度
            lineHeight = lineHeights.get(i);

            // 遍历当前行的所有子View
            for (int j = 0; j < lineViews.size(); j++) {
                View child = lineViews.get(j);
                if (child.getVisibility() == GONE) {
                    continue;
                }

                // 分别计算childView的left, top, right, bottom
                MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
                int childLeft = left + lp.leftMargin;
                int childTop = top + lp.topMargin;
                int childRight = childLeft + child.getMeasuredWidth();
                int childBottom = childTop + child.getMeasuredHeight();

                // 对子控件进行布局
                child.layout(childLeft, childTop, childRight, childBottom);

                // 更新left长度
                left += child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            }

            // 重置left，并叠加top
            left = 0;
            top += lineHeight;
        }
    }
}
