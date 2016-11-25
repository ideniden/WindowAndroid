package com.luoj.android.window;

import android.content.Context;
import android.content.res.Configuration;
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
public abstract class BaseGlobalTitleDraggablePopDialog implements IBasePopDialog{

	protected TitleDraggableFloatLayout dsl;

	protected Context mContext;

	protected View rootView;

	public BaseGlobalTitleDraggablePopDialog(Context context) {
		init(context, -2, -2);
	}

	public BaseGlobalTitleDraggablePopDialog(Context context, int popWidth, int popHeight) {
		init(context, popWidth, popHeight);
	}
	
	private final void init(Context context,int popWidth,int popHeight){
		this.mContext=context;
		dsl=new TitleDraggableFloatLayout(context);
		rootView=LayoutInflater.from(context).inflate(setContentLayout(), dsl.getContainer());
//		dsl.addViewToContainer(rootView, popWidth, popHeight);
		initView(rootView);
		setCanDrag();
	}

	public void setCanCancelDialogWithBackPress(){
		dsl.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
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
	
	public TitleDraggableFloatLayout getDialogWindow(){
		return dsl;
	}
	
	public Context getContext(){
		return mContext;
	}
	
	public boolean isShow(){
		return dsl.isShow();
	}
	
	public void setCanDrag(){
		dsl.setCanDrag(true);
	}

    public void setContainerWidthHeight(){
        dsl.setContainerWidthHeightByPortrait();
    }

	public void setContainerWidthHeight(int orientation){
		if(orientation== Configuration.ORIENTATION_PORTRAIT){
			dsl.setContainerWidthHeightByPortrait();
		}else{
			dsl.setContainerWidthHeightByLandscape();
		}
	}

}
