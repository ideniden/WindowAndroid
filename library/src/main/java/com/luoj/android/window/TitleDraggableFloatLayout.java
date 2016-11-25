package com.luoj.android.window;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


/**
 * @author LuoJ
 * @date 2013-8-14
 * @package j.android.library.view - MoveButton.java
 * @Description 自定义可拖拽悬浮控件
 */
public class TitleDraggableFloatLayout extends FrameLayout implements DragMotionProxy.OnDragListener {

	final String TAG="TitleDraggable";

	private WindowManager wm;// 悬浮窗口相关
	private WindowManager.LayoutParams wmParams;// 窗口参数

	private boolean isCanDrag=true;
	private boolean isAutoAttachEdge;//自动贴到边缘
	private boolean isShowing = false;// 是否显示中

	private float showX = 0;// 默认显示的X坐标
	private float showY = 120;// 默认显示的Y坐标

    private boolean rememberLastLocation=true;
    private float lastX=showX;
    private float lastY=showY;

	/**
	 * 拖拽相关
	 */
    private ViewGroup dragPlace;
    private RelativeLayout containerLayout;
	private DragMotionProxy mDragMotionProxy;
	private float endX;// 抬起后X坐标
	private float endY;// 抬起后Y坐标
	private float offsetX;// 点击处距控件的X轴间距
	private float offsetY;// 点击处距控件的Y轴间距

	/**
	 * 构造函数
	 * @param context
	 */
	public TitleDraggableFloatLayout(final Context context) {
		super(context);
		init();
		initWindowParam();
	}

	/**
	 * 构造函数
	 * @param context
	 * @param attrs
	 */
	public TitleDraggableFloatLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
		initWindowParam();
	}

	private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.layout_title_draggable_float_container,this);
		findViewById(R.id.btn_x).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				close();
			}
		});
		dragPlace=(ViewGroup)findViewById(R.id.layout_drag_place);
		containerLayout=(RelativeLayout)findViewById(R.id.layout_container);
		mDragMotionProxy=new DragMotionProxy();
		mDragMotionProxy.set(dragPlace, this);
        mDragMotionProxy.notNeedIntercept();
		// 获取WindowManager
		wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		//设置容器宽高为屏幕的3分之2
		setContainerWidthHeightByCurrentOrientation();
	}

	private void initWindowParam() {
//		setOrientation(LinearLayout.VERTICAL);
		// 设置LayoutParams(全局变量）相关参数
		wmParams = new WindowManager.LayoutParams();
		wmParams.type = WindowManager.LayoutParams.TYPE_PHONE; // 设置window type
		wmParams.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明
		// 不设置这个弹出框的透明遮罩显示为黑色
        //wmParams.format = PixelFormat.TRANSLUCENT;
		/*
		 * http://www.cnblogs.com/mengdd/p/3824782.html
		 * 设置Window flag
		 * 如果设置了FLAG_NOT_FOCUSABLE，弹出的View收不到Back键的事件,不设置的话activity收不到返回事件
		 * FLAG_NOT_TOUCH_MODAL 不阻塞事件传递到后面的窗口
		 */
//		wmParams.flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;//设置之后，返回键无效，点击窗口外部无效

//		wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		// 以屏幕左上角为原点，设置x、y初始值
		wmParams.x = 0;
		wmParams.y = 0;
		wmParams.gravity = Gravity.TOP | Gravity.LEFT;
		// 设置悬浮窗口长宽数据
		wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.d(TAG,"onTitleDraggableFloatLayout key even ->"+keyCode);
                return false;
            }
        });
	}

	public void setContainerWidthHeightByCurrentOrientation(){
		int orientation=getResources().getConfiguration().orientation;
//		LogUtil.e("setContainerWidthHeightByCurrentOrientation->"+orientation);
		if(orientation== Configuration.ORIENTATION_PORTRAIT){
			setContainerWidthHeightByPortrait();
		}else{
			setContainerWidthHeightByLandscape();
		}
	}

    public void setContainerWidthHeightByPortrait(){
        setContainerWidthHeight((2.0f/3.0f));
    }

	public void setContainerWidthHeightByLandscape(){
		setContainerWidthHeight((1.0f/3.0f),(3.0f/4.0f));
	}

	public void setContainerWidthHeight(float ratio){
		setContainerWidthHeight(ratio,ratio);
	}

    public void setContainerWidthHeight(float widthRatio,float heightRatio){
//        int orientation=getResources().getConfiguration().orientation;
        LinearLayout rootLayout= (LinearLayout) findViewById(R.id.layout_root);
        DisplayMetrics metric = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metric);
        float width = metric.widthPixels;     // 屏幕宽度（像素）
        float height = metric.heightPixels;   // 屏幕高度（像素）
//        float density = metric.density;      // 屏幕密度（0.75 / 1.0 / 1.5）
//        int densityDpi = metric.densityDpi;  // 屏幕密度DPI（120 / 160 / 240）
        int containerWidth=(int)(width*widthRatio);
        int containerHeight=(int)(height*heightRatio);
		ViewGroup.LayoutParams lp=rootLayout.getLayoutParams();
		lp.width=containerWidth;
		lp.height=containerHeight;
//        FrameLayout.LayoutParams lp=new FrameLayout.LayoutParams(containerWidth,containerHeight);
        rootLayout.setLayoutParams(lp);
        Log.d(TAG,"View containerWidth->"+containerWidth+"\ncontainerHeight->"+containerHeight+"\norientation->"+getResources().getConfiguration().orientation);
    }

    public void setContainerHeightEqualWidth(float ratio){
      LinearLayout rootLayout= (LinearLayout) findViewById(R.id.layout_root);
      DisplayMetrics metric = new DisplayMetrics();
      wm.getDefaultDisplay().getMetrics(metric);
      float width = metric.widthPixels;     // 屏幕宽度（像素）
      int containerWidth=(int)(width*ratio);
      int containerHeight=containerWidth;
		ViewGroup.LayoutParams lp=rootLayout.getLayoutParams();
		lp.width=containerWidth;
		lp.height=containerHeight;
//      FrameLayout.LayoutParams lp=new FrameLayout.LayoutParams(containerWidth,containerHeight);
      rootLayout.setLayoutParams(lp);
      Log.d(TAG,"View containerWidth->"+containerWidth+"\ncontainerHeight->"+containerHeight+"\norientation->"+getResources().getConfiguration().orientation);
    }

    public void setContainerHeightWidthWrap(){
        LinearLayout rootLayout= (LinearLayout) findViewById(R.id.layout_root);
        LayoutParams lp=new LayoutParams(-2,-2);
        rootLayout.setLayoutParams(lp);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
		setContainerWidthHeightByCurrentOrientation();
        super.onConfigurationChanged(newConfig);
    }

    public void addViewToContainer(View v,int width,int height){
        containerLayout.addView(v, width, height);
		Log.d(TAG,"container "+containerLayout.getWidth()+" "+containerLayout.getHeight());
    }

//    @Override
//    public void addView(View child) {
//        addView(child, -2, -2);
//    }

    public void setWindowWidth(int width){
		wmParams.width = width;
	}

	public void setWindowHeight(int height){
		wmParams.height = height;
	}

	public void setWindowFullScreen(){
		setWindowWidth(-1);
		setWindowHeight(-1);
	}

	public void setWindowGravity(int gravity){
		wmParams.gravity = gravity;
	}

	/**
	 * 显示悬浮控件
	 * @param x
	 * @param y
	 */
	private void showWindowView(float x, float y) {
		if (null != this) {
			WindowManager.LayoutParams params = (WindowManager.LayoutParams) this.getLayoutParams();
//			params.y = (int) y - (this.getHeight() / 2);
            params.y = (int) y;
			params.x = (int) x;
			this.setLayoutParams(params);
			if (null != wm) {
				wm.updateViewLayout(this, wmParams);
			}
		}
	}

	/**
	 * 初始化相对于屏幕的位置
	 * @param x
	 * @param y
	 */
	public void initLocationOfScreen(int x,int y){
		this.showX=x;
		this.showY=y;
	}

	/**
	 * 显示窗口.
	 */
	public void show() {
		if (!isShowing) {
			wm.addView(this, wmParams);
            if(rememberLastLocation){
                showWindowView(lastX, lastY);
            }else{
                showWindowView(showX, showY);
            }
			isShowing = true;
		}else{
			Log.d(TAG,"悬浮布局已经显示，不必再次调用show()");
		}
	}

	public void showAtScreenCenter(){
//		DisplayMetrics outMetrics=new DisplayMetrics();
//		wm.getDefaultDisplay().getMetrics(outMetrics);
//		int screenWidth = outMetrics.widthPixels;
//		int screenHeight=outMetrics.heightPixels;
//		//
//		int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
//		int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
//		measure(w, h);
//		int layoutHeight = getMeasuredHeight();
//		int layoutWidth = getMeasuredWidth();
//		showX=(screenWidth/2)-(layoutWidth/2);
//		showY=(screenHeight/2)-(layoutHeight/2);
		show();
	}

	/**
	 * 显示窗口在指定位置.
	 * 强制调整位置显示
	 * @param x
	 * @param y
	 */
	public void show(int x,int y){
		dismiss();
		if (!isShowing) {
			wm.addView(this, wmParams);
			showWindowView(x, y);
			isShowing = true;
		}
	}

	/**
	 * 隐藏悬浮布局.
	 */
	public void dismiss() {
		if (isShowing) {
			wm.removeView(this);
			isShowing=false;
			if(null!=mOnDragLayoutDismissListener)mOnDragLayoutDismissListener.onDismiss();
		}
	}

	public void close() {
		if(null!=mOnDragLayoutCloseListener)mOnDragLayoutCloseListener.onClose();
		dismiss();
	}

	/**
	 * 判断悬浮布局是否显示
	 * @return
	 */
	public boolean isShow() {
		return isShowing;
	}

	/**
	 * 是否可以拖拽
	 * @return
	 */
	public boolean isCanDrag() {
		return isCanDrag;
	}

	/**
	 * 设置是否可拖拽
	 * @param
	 */
	public void setCanDrag(boolean isCanDrag) {
		this.isCanDrag = isCanDrag;
	}

	/**
	 * 是否在拖拽完松开后，依附到屏幕边缘.
	 * @return
	 */
	public boolean isAutoAttachEdge() {
		return isAutoAttachEdge;
	}

	/**
	 * 设置是否在拖拽完松开后，依附到屏幕边缘.
	 * @param isAutoAttachEdge
	 */
	public void setAutoAttachEdge(boolean isAutoAttachEdge) {
		this.isAutoAttachEdge = isAutoAttachEdge;
	}

	public int getContainerHeight(){
		return containerLayout.getHeight();
	}

	public int getContainerWidth(){
		return containerLayout.getWidth();
	}

	public ViewGroup getContainer(){
		return containerLayout;
	}

	@Override
	public void onActionDown(MotionEvent ev) {
		// 计算控件左上角和点击控件的间距
		int[] location = new int[2];
		this.getLocationOnScreen(location);//获取拖拽组件在屏幕的坐标点
		offsetX = ev.getRawX() - location[0];
		offsetY = ev.getRawY() - location[1];
	}
	
	@Override
	public void onActionMove(MotionEvent ev) {
		// 更新控件的位置
		moveWindowView(ev.getRawX(), ev.getRawY());
	}

    /**
     * 移动悬浮布局
     * @param x
     * @param y
     */
    private void moveWindowView(float x, float y) {
        if (null != this) {
            WindowManager.LayoutParams params = (WindowManager.LayoutParams) this.getLayoutParams();
            params.x = (int) (x - offsetX);
            params.y = (int) (y - offsetY);
            if (rememberLastLocation){
                lastX=params.x;
                lastY=params.y;
            }
            this.setLayoutParams(params);
            if (null != wm) {
                wm.updateViewLayout(this, wmParams);
            }
        }
    }

	@Override
	public void onActionUp(MotionEvent ev) {
		endX = ev.getRawX();
		endY = ev.getRawY();
		@SuppressWarnings("deprecation")
		float windowWidth = wm.getDefaultDisplay().getWidth();
		if (isAutoAttachEdge) {// 如果在屏幕左侧，自动贴到左侧，反之右侧
			if (endX < (windowWidth / 2)) {
				showWindowView(0, endY);
				showX = 0;
			} else {
				showWindowView(windowWidth - this.getWidth(), endY);
				showX = windowWidth - this.getWidth();
			}
		}
		showY = endY;
	}

	private OnDragLayoutDismissListener mOnDragLayoutDismissListener;
	public void setOnDragLayoutDismissListener(OnDragLayoutDismissListener onDragLayoutDismissListener){
		mOnDragLayoutDismissListener=onDragLayoutDismissListener;
	}

	private OnDragLayoutCloseListener mOnDragLayoutCloseListener;
	public void setOnDragLayoutCloseListener(OnDragLayoutCloseListener mOnDragLayoutCloseListener){
		this.mOnDragLayoutCloseListener=mOnDragLayoutCloseListener;
	}

}
