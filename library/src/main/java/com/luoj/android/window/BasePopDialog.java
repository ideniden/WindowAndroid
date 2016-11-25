package com.luoj.android.window;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

/**
 * @author LuoJ
 * @date 2014-11-14
 * @package j.android.library.dialog -- BasePopDialog.java
 * @Description 
 */
public abstract class BasePopDialog implements IBasePopDialog{

	protected PopupWindow mPop;

	protected int animationStyle=-1;
	
	protected Context mContext;
	
	protected View rootView;
	protected View activityRootView;

	public BasePopDialog(Context context) {
		init(context, -1, -1);
	}
	
	public BasePopDialog(Context context,int popWidth,int popHeight) {
		init(context, popWidth, popHeight);
	}
	
	@SuppressWarnings("deprecation")
	private final void init(Context context,int popWidth,int popHeight){
		this.mContext=context;
		rootView=LayoutInflater.from(context).inflate(setContentLayout(), null);
		mPop=new PopupWindow(rootView, popWidth, popHeight);
		mPop.setBackgroundDrawable(new BitmapDrawable(context.getResources()));
		mPop.setFocusable(true);
		mPop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		initView(rootView);
	}

	public Context getContext(){
		return mContext;
	}

	/**
	 * 
	 */
	public void setCantCancelDialogWithBackPress(){
		mPop.setFocusable(false);//暂只能依赖这行代码实现
		rootView.setFocusableInTouchMode(true);
		rootView.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode==KeyEvent.KEYCODE_BACK) {
					return true;
				}
				return false;
			}
		});
	}
	
	@Override
	public void showAtScreenCenter(){
		showAtScreenCenter(activityRootView!=null?activityRootView:rootView);
	}

	public void showAtScreenCenter(View parent){
		if (!mPop.isShowing()) {
			if(-1!=animationStyle)mPop.setAnimationStyle(animationStyle);
			mPop.showAtLocation(parent, Gravity.CENTER, 0, 0);
		}
	}

	public boolean isShow(){
		return mPop.isShowing();
	}

	@Override
	public void dismiss(){
		mPop.dismiss();
	}
	
	public void setOnDismissListener(OnDismissListener onDismissListener){
		mPop.setOnDismissListener(onDismissListener);
	}
	
	public PopupWindow getPopupWindow(){
		return mPop;
	}

	public void setAnimationStyle(int resId){
		animationStyle=resId;
	}

}


