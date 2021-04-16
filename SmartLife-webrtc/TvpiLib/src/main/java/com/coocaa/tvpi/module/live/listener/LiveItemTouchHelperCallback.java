package com.coocaa.tvpi.module.live.listener;

import android.graphics.Canvas;
import android.graphics.Color;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.coocaa.tvpilib.R;


/**
 * @ClassName LiveItemTouchHelperCallback
 * @Description 直播收藏条目的拖动删除callback
 * @User heni
 * @Date 2019/7/4
 */
public class LiveItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private ItemTouchMoveListener moveListener;

    public LiveItemTouchHelperCallback(ItemTouchMoveListener moveListener) {
        this.moveListener = moveListener;
    }

    /**
     * Callback回调监听时先调用的，用来判断当前是什么动作，比如判断方向
     * 作用：哪个方向的拖动
     *
     * @param recyclerView
     * @param viewHolder
     * @return
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        //我要监听的拖拽方向是哪个方向
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        //我要监听的swipe侧滑方向是哪个方向
        int swipeFlags = 0; //ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;

        int flags = makeMovementFlags(dragFlags, swipeFlags);
        return flags;
    }

    /**
     * 是否打开长按拖拽效果
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    /**
     * 当上下移动的时候回调的方法
     */
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder srcHolder,
                          RecyclerView.ViewHolder targetHolder) {
        // 在拖拽过程中不断地调用adapter.notifyItemMoved(from,to);
        if (srcHolder.getItemViewType() != targetHolder.getItemViewType()) {
            return false;
        }
        //在拖拽的过程中不断调用adapter.notifyItemMoved(from,to);
        boolean result = moveListener.onItemMove(srcHolder.getAdapterPosition(), targetHolder.getAdapterPosition());
        return result;
    }

    /**
     * 侧滑的时候回调的方法
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        //监听侧滑，1.删除数据 2.调用adapter.notifyItemRemove(position);
        moveListener.onItemRemove(viewHolder.getAdapterPosition());
    }

    /**
     * 设置滑动item的背景
     */
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        //判断选中状态
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            viewHolder.itemView.setBackgroundColor(viewHolder.itemView.getContext().getResources().getColor(R.color.b_tvlive));
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    //清除滑动item的背景
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        // 恢复
        viewHolder.itemView.setBackgroundColor(Color.TRANSPARENT);

        //防止出现复用问题 而导致条目不显示 方式一
        viewHolder.itemView.setAlpha(1);//1-0
        //设置滑出大小
//            viewHolder.itemView.setScaleX(1);
//            viewHolder.itemView.setScaleY(1);
        super.clearView(recyclerView, viewHolder);
    }

    //设置滑动时item的背景透明度
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        //dX:水平方向移动的增量(负：往左；正：往右) 0-view.getWidth()
        float alpha = 1 - Math.abs(dX) / viewHolder.itemView.getWidth();
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

            //透明度动画
            viewHolder.itemView.setAlpha(alpha);//1-0
            //设置滑出大小
//            viewHolder.itemView.setScaleX(alpha);
//            viewHolder.itemView.setScaleY(alpha);
        }
//        //防止出现复用问题 而导致条目不显示 方式二
//        if(alpha==0){
//            viewHolder.itemView.setAlpha(1);//1-0
//            //设置滑出大小
////            viewHolder.itemView.setScaleX(1);
////            viewHolder.itemView.setScaleY(1);
//        }
        //此super方法自动处理setTranslationX
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

    }
}
