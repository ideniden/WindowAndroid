package com.luoj.android.window;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;

/**
 * @author LuoJ
 * @date 2014-11-14
 * @package j.android.library.dialog -- BaseGlobalPopDialog.java
 * @Description
 */
public abstract class BaseGlobalPopDialog implements IBasePopDialog{
	
	protected DraggableSuspendLayout dsl;
	
	protected Context mContext;
	
	protected View rootView;

	public BaseGlobalPopDialog(Context context) {
		init(context, -2, -2);
	}
	
	public BaseGlobalPopDialog(Context context,int popWidth,int popHeight) {
		init(context, popWidth, popHeight);
	}
	
	private final void init(Context context,int popWidth,int popHeight){
		this.mContext=context;
		dsl=new DraggableSuspendLayout(context);
		if(popWidth==-1&&popHeight==-1)dsl.setFullScreen();
		rootView=LayoutInflater.from(context).inflate(setContentLayout(), null);
		initView(rootView);
		dsl.addView(rootView, popWidth, popHeight);
	}

	public void setCantCancelDialogWithBackPress(){
		dsl.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode==KeyEvent.KEYCODE_BACK) {
					dsl.dismiss();
					return true;
				}
				return false;
			}
		});
	}
	
	@Override
	public void showAtScreenCenter(){
		if (!dsl.isShow()) {
			dsl.showAtScreenCenter();
		}
	}
	
	@Override
	public void dismiss(){
		dsl.dismiss();
	}
	
	public void setOnDismissListener(OnDragLayoutDismissListener onDragLayoutDismissListener){
		dsl.setOnDragLayoutDismissListener(onDragLayoutDismissListener);
	}
	
	public DraggableSuspendLayout getDialogWindow(){
		return dsl;
	}
	
	public Context getContext(){
		return mContext;
	}
	
	public boolean isShow(){
		return dsl.isShow();
	}
	
	public void setCanlDrag(){
		dsl.setCanDrag(true);
	}
	
}
