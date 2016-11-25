package com.luoj.android.window;

import android.view.View;

/**
 * @author LuoJ
 * @date 2014-11-20
 * @package j.android.library.dialog -- IBasePopDialog.java
 * @Description 
 */
public interface IBasePopDialog {
	
	int setContentLayout();
	
	void initView(View rootView);
	
	void showAtScreenCenter();
	
	void dismiss();
	
}
