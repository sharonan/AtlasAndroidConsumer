//  ==================================================================================================================
//  ATLCalendarListMoveEventAdapter.java
//  ATLAS
//  Copyright (c) 2012 ATLAS Apps. All rights reserved.
//  ==================================================================================================================
//
//  ==================================================================================================================
//  HISTORY
//  YYYY-MM-DD NAME:    Description of changes
//  ==================================================================================================================
//  2012-12-14 TAN:     Create class
//  ==================================================================================================================

package com.atlasapp.section_calendar;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;

public class ATLCalendarListMoveEventAdapter extends BaseAdapter{
	ATLCalCellList mCalCellList;
	int size;
	Context mContext;
	OnClickListener onClickListener;
	

	public ATLCalendarListMoveEventAdapter(ATLCalCellList calCellList, Context ctx) {

		// TODO Auto-generated constructor stub
		mCalCellList = calCellList;
		mContext = ctx;
		mCalCellList.load(ctx);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		size = mCalCellList.getCalListArray().size();
		// Hard code for basic view phase
		return size > 24 ? size : 24;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mCalCellList.getCalListArray().get(position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		CalendarMoveEventCell cell;
		if (convertView == null) {
			cell = new CalendarMoveEventCell(mContext);
			convertView = (View)cell;
			convertView.setTag(cell);
		}
		else{
			cell = (CalendarMoveEventCell)convertView.getTag();
		}
		ATLCalCellData cellData = (ATLCalCellData) mCalCellList.getCalListArray().get(position);
		cell.index = position;
		cell.setCellData(cellData);
		
		return convertView;
	}

	
	

}