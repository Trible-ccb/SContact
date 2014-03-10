package com.trible.scontact.components.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.trible.scontact.utils.DeviceUtil;

/**
 * @author Trible Chen
 *
 */
public class MultiFunctionListView extends ListView {

	private final static Integer ITEM_TAG = 100;
	
	OnMoveLeftRightListner moveLeftRightListner;
	OnMoveUpDownListner moveUpDownListner;
	int moveSlop = DeviceUtil.dip2px(getContext(), 100);
	int moveMinVelocity = 200;
	int epsX = DeviceUtil.dip2px(getContext(), 50);//if distance is more than epsx,then is move right-left
	int epsY = DeviceUtil.dip2px(getContext(), 15);//if distance is more than epsy,then is move up-down
	VelocityTracker mVelocityTracker = null;
	
	float downx = 0,downy = 0;
	
	View touchItemView = null;
	int touchItemPos;
	
	boolean isItemMove = false;

	public static int CAN_MOVE_FROM_RIGHT_TO_LEFT 	= 0x00000001;
	public static int CAN_MOVE_FROM_LEFT_TO＿RIGHT 	= 0x00000010;
	public static int CAN_MOVE_FROM_TOP_TO_DOWN 	= 0x00000100;
	public static int CAN_MOVE_FROM_BOTTOM_TO_UP 	= 0x00001000;
	public static int DEFAULT_MOVE_MODE 			= 0x00000000;

	public static int UP_DOWN_MOVING 				= 0x00010000;
	public static int RIGHT_LEFT_MOVING 			= 0x00100000;
	public static int DEFAUL_MOVING 				= 0000000000;
	
	int curMoveMode = DEFAULT_MOVE_MODE;
	int typeOnMoving = DEFAUL_MOVING;
	
	public MultiFunctionListView(Context context) {
		super(context);
	}

	public MultiFunctionListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public MultiFunctionListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private void addVelocityTrackerEvent(MotionEvent event) {  
        if (mVelocityTracker == null) {  
            mVelocityTracker = VelocityTracker.obtain();  
        }  
        mVelocityTracker.addMovement(event);  
    }  
    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }
    // 获得横向的手�? 
    private int getTouchVelocityX() {  
        if (mVelocityTracker == null)  
            return 0;  
        mVelocityTracker.computeCurrentVelocity(1000);  
        int velocity = (int) mVelocityTracker.getXVelocity();  
        return velocity;  
    }
    
	void initTouchEventToListView(final ListView l){
		
		OnTouchListener touchListener = new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				addVelocityTrackerEvent(event);
				switch (event.getAction()) {
				
				case MotionEvent.ACTION_DOWN:
					downx = event.getX();
					downy = event.getY();
					touchItemPos = pointToPosition((int)downx, (int)downy);
					int fPos = getFirstVisiblePosition();
					touchItemView = getChildAt(touchItemPos - fPos);
					if ( touchItemView == null || touchItemPos == AdapterView.INVALID_POSITION){
//						return l.onTouchEvent(event);
					} else {
						if ( moveLeftRightListner != null ){
							moveLeftRightListner.whenTouchDownOnItemView(touchItemView,touchItemPos);
						}
					}
					isItemMove = false;
					typeOnMoving = DEFAUL_MOVING;
//					if ( moveLeftRightListner != null ){
//						moveLeftRightListner.whenTouchDownOnItemView(touchItemView,touchItemPos);
//					}
					if ( moveUpDownListner != null ){
						moveUpDownListner.whenTouchDown(l);
					}
					if ( isFirstItemVisible() ){
						typeOnMoving |= CAN_MOVE_FROM_TOP_TO_DOWN;
					}
					
					return isItemMove;
				case MotionEvent.ACTION_MOVE:
					float mx = event.getX();
					float my = event.getY();
					float dx = mx - downx;
					float dy = my - downy;
					
					if ( (curMoveMode & CAN_MOVE_FROM_TOP_TO_DOWN) != DEFAULT_MOVE_MODE 
							&& ((typeOnMoving & CAN_MOVE_FROM_TOP_TO_DOWN) != DEFAULT_MOVE_MODE 
							&& dy > epsY)){// if the first item is visible and still moving
						if ( moveUpDownListner != null ){
							if ( dy - epsY > 0 ){
								isItemMove = false;
								moveUpDownListner.whenMovingDown(l,dy);//call back to UI whenMovingDown
								typeOnMoving |= UP_DOWN_MOVING;
							}
							
						}
					}
					
					if ( curMoveMode == DEFAULT_MOVE_MODE ||Math.abs(dx) < epsX && ((typeOnMoving & RIGHT_LEFT_MOVING) != RIGHT_LEFT_MOVING ) 
							&& ((curMoveMode & CAN_MOVE_FROM_LEFT_TO＿RIGHT) != DEFAULT_MOVE_MODE)
							&& ((curMoveMode & CAN_MOVE_FROM_RIGHT_TO_LEFT) != DEFAULT_MOVE_MODE)) {
//						Dog.w("whenMoving right left by default");
						return  l.onTouchEvent(event);//横向滑动�?防抖
					}
					
					if ( touchItemView == null || (Math.abs(dx) < 1.5 * Math.abs(dy)
							&& ((typeOnMoving & RIGHT_LEFT_MOVING) != RIGHT_LEFT_MOVING ))){//if no item view moving
//						Dog.w("no item view moving");
						l.onTouchEvent(event);
						isItemMove = false;
						typeOnMoving |= UP_DOWN_MOVING;
						
					} else if ( ((typeOnMoving & UP_DOWN_MOVING) != UP_DOWN_MOVING
							&& ((curMoveMode & CAN_MOVE_FROM_LEFT_TO＿RIGHT) != DEFAULT_MOVE_MODE)
							&& ((curMoveMode & CAN_MOVE_FROM_RIGHT_TO_LEFT) != DEFAULT_MOVE_MODE))){
//						Dog.w(" item view moving ");
						if ( moveLeftRightListner != null ){
							moveLeftRightListner.whenMovingAtItemView(touchItemView, -dx,touchItemPos);//call back to UI whenMoveAtItemView
						}
						isItemMove = true;
						touchItemView.cancelLongPress();
						typeOnMoving = RIGHT_LEFT_MOVING;
						
						touchItemView.setTag(ITEM_TAG, Integer.valueOf(typeOnMoving));
					}
					
					return isItemMove;
					
				case MotionEvent.ACTION_UP:
					float ux = event.getX();
					float uy = event.getY();
					if ( touchItemView == null ){
						 l.onTouchEvent(event);
//						 return true;
					}
					if ( (typeOnMoving & UP_DOWN_MOVING) != DEFAUL_MOVING ){
						
						if ( moveUpDownListner != null ){
							
							moveUpDownListner.whenMovedListDownOnActionUp(l, uy - downy);//call back to UI whenMoveListDownToControl
							
						}
						return false;
					}
					if (  touchItemView != null && (typeOnMoving & RIGHT_LEFT_MOVING) != DEFAUL_MOVING  ){
						touchItemView.setTag(ITEM_TAG, Integer.valueOf(typeOnMoving));
						if ( !isItemMove ){
							l.onTouchEvent(event);
							 return true;
						}

						if ( moveLeftRightListner != null ){
							boolean fastToOpen = false;
							int vx = getTouchVelocityX();
							if ( Math.abs(vx) >= moveMinVelocity && vx * (ux - downx) > 0){
								fastToOpen = true;
							}
							moveLeftRightListner.whenMovedItemOnActionUp(
									touchItemView, touchItemPos, ux - downx,fastToOpen);
//							touchItemView.setTag(R.id.text_item_root, null);
							return true;
						}
							
					}
					recycleVelocityTracker();
					return false;
				default:
					break;
				}
				return false;
			}
		};
		setOnTouchListener(touchListener);
	}
	
	void initScrollerListner( final ListView listView){
		OnScrollListener sListener = new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				 
				if ( scrollState == OnScrollListener.SCROLL_STATE_IDLE ){
						
					if ( moveUpDownListner != null ){
						moveUpDownListner.whenScrollStop(listView);
					}
				}
					
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if ((firstVisibleItem + visibleItemCount == totalItemCount)  
		                && (totalItemCount != 0)){
					if ( moveUpDownListner != null ){
						moveUpDownListner.whenMoveListOnBottom(listView);
					}
				}
			}
		};
		setOnScrollListener(sListener);
	}
	@Override
	public void setOnItemClickListener(final OnItemClickListener listener) {
		
		OnItemClickListener tmpClickListener = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Integer integer = (Integer) view.getTag(ITEM_TAG);
				if (integer != null && integer == RIGHT_LEFT_MOVING && isItemMove){
					view.setTag(ITEM_TAG, null);
					return;
				}
				listener.onItemClick(parent, view, position, id);
			}
		};
		super.setOnItemClickListener(tmpClickListener);
	}

	@Override
	public void setOnItemLongClickListener(final OnItemLongClickListener listener) {
		
		OnItemLongClickListener tmpItemLongClickListener = new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				Integer integer = (Integer) view.getTag(ITEM_TAG);
				if (integer != null && integer == RIGHT_LEFT_MOVING && isItemMove){
					view.setTag(ITEM_TAG, null);
					return true;
				}
				return listener.onItemLongClick(parent, view, position, id);
			}
		};
		super.setOnItemLongClickListener(tmpItemLongClickListener);
	}

	public boolean isFirstItemVisible() {
		if (this.getCount() == 0) {
			return true;
		} else if (this.getFirstVisiblePosition() == 0) {
			
			final View firstVisibleChild = this.getChildAt(0);
			
			if (firstVisibleChild != null) {
				return firstVisibleChild.getTop() >= 0 && this.getTop() >= 0;
			}
		}
		
		return false;
	}

	public boolean isLastItemVisible() {
		final int count = this.getCount();
		final int lastVisiblePosition = this.getLastVisiblePosition();

		if (count == 0) {
			return true;
		} else if (lastVisiblePosition == count - 1) {

			final int childIndex = lastVisiblePosition - this.getFirstVisiblePosition();
			final View lastVisibleChild = this.getChildAt(childIndex);

			if (lastVisibleChild != null) {
				return lastVisibleChild.getBottom() <= this.getBottom();
			}
		}
		return false;
	}



	public interface OnMoveLeftRightListner {
		
		 /**
		 * @param view the item view which is touched in listview
		 */
		void whenTouchDownOnItemView(View view,int pos);
		
		 /**
		 * @param view
		 * @param distance
		 * 
		 * move action on the touched view have moved a distance
		 */
		void whenMovingAtItemView(View view , float distance,int pos);
		
		 /**
		 * @param view
		 * when the action up or cancel , the touched view had moved more than 20 distance that can do control for the view
		 */
		void whenMovedItemOnActionUp(View view,int pos,float distance,boolean isMovingFast);

		
	}
	
	public interface OnMoveUpDownListner{
		
		void whenTouchDown(ListView view);
		
		/**
		 * @param distance
		 * the moving action still pulling
		 */
		void whenMovingDown(ListView listView,float distance);
		
		/**
		 * do something like refreshing the list data
		 */
		void whenMovedListDownOnActionUp(ListView listView,float distance);
		
		void whenScrollStop(ListView listView);
		/**
		 * 
		 * @param listView
		 * 
		 * the listview had scroll down to bottom,you could do sth like loading more data
		 */
		void whenMoveListOnBottom(ListView listView);
	}
	
	public void setOnMoveLeftRightListner( OnMoveLeftRightListner listner){
		
		moveLeftRightListner = listner;
		curMoveMode |= CAN_MOVE_FROM_LEFT_TO＿RIGHT | CAN_MOVE_FROM_RIGHT_TO_LEFT;
		initTouchEventToListView(this);
	}

	public void setOnMoveUpDownListner( OnMoveUpDownListner listner) {
		
		moveUpDownListner = listner;
		curMoveMode |= CAN_MOVE_FROM_TOP_TO_DOWN | CAN_MOVE_FROM_BOTTOM_TO_UP;
		initTouchEventToListView(this);
		initScrollerListner(this);
	}
	
	/**
	 * @param mode {@link CAN_MOVE_FROM_RIGHT_TO_LEFT,CAN_MOVE_FROM_LEFT_TO＿RIGHT
	 * CAN_MOVE_FROM_TOP_TO_DOWN,CAN_MOVE_FROM_BOTTOM_TO_UP,DEFAULT_MOVE_MODE}
	 */
	public void setMoveMode(int mode){
		curMoveMode = mode;
	}
	
	
	/**
	 * @param mode Dynamic changing moving type 
	 * {@link UP_DOWN_MOVING,RIGHT_LEFT_MOVING,DEFAUL_MOVING}
	 */
	public void setMoveTypeMode(int mode){
		typeOnMoving = mode;
	}
}
