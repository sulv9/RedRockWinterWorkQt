package com.aefottt.redrockwinterworkqt.base;

public class BasePresenter<V extends BaseView> {
    protected V mView; //Presenter所持有的View

    /**
     * 绑定View，一般在初始化中使用
     */
    public void attachView(V view){
        this.mView = view;
    }

    /**
     * 解除绑定，一般在onDestroy()中使用
     */
    public void detachView(){
        this.mView = null;
    }

    /**
     * View是否已绑定
     */
    public boolean isViewAttached(){
        return mView != null;
    }
}
