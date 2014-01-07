//  ==================================================================================================================
//  EditTask.java
//  ATLAS
//  Copyright (c) 2012 AtlasConsumerAndroid Apps. All rights reserved.
//  ==================================================================================================================
//
//  ==================================================================================================================
//  HISTORY
//  YYYY-MM-DD NAME:    Description of changes
//  ==================================================================================================================
//  2012-11-09 TAN:     init class
//  ==================================================================================================================

package com.atlasapp.section_tasks;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Set;
import java.util.TimeZone;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import kankan.wheel.widget.adapters.NumericWheelAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.atlasapp.atlas_database.AtlasServerConnect;
import com.atlasapp.common.ATLAlertDialogUtils;
import com.atlasapp.common.ATLAnimationUtils;
import com.atlasapp.common.ATLConstants.EventResponseType;
import com.atlasapp.common.ATLConstants.TASK_CATEGORY;
import com.atlasapp.common.ATLConstants.TaskResponseType;
import com.atlasapp.common.CalendarUtilities;
import com.atlasapp.common.ContactCard;
import com.atlasapp.common.DateTimeUtilities;
import com.atlasapp.model.ATLContactModel;
import com.atlasapp.model.ATLCalendarModel;
import com.atlasapp.model.ATLTaskModel;
import com.atlasapp.section_appentry.R;
import com.atlasapp.section_calendar.ATLCalendarIntentKeys;
import com.atlasapp.section_calendar.ATLCalendarManager;
import com.atlasapp.section_calendar.ATLCalendarScrollListAdapter;
import com.atlasapp.section_calendar.CalendarEditView;
import com.atlasapp.section_calendar.CalendarMonthView;
import com.atlasapp.section_contacts.ATLContactListActivity;
import com.atlasapp.section_settings.InviteContact;
public class EditTask extends Activity implements OnClickListener,
		OnWheelChangedListener {
	private static final int CONTACT_PICKER_RESULT = 1001;
	private static final String DEBUG_TAG = "EditTask";
	private static final String CUSTOM_DAY_TEXT_FORMAT = "E \n MMM dd";
	private static final String CUSTOM_DAY_TEXT_FORMAT_WK_DAY = "E";
	private static final String CUSTOM_DAY_TEXT_FORMAT_MTH_DAY = "MMM dd";
	LayoutInflater mInflater;
	Context mContext;
	View mLayout;
	ImageButton saveBtn;
	ImageButton cancelBtn;
	EditText title;//2013-01-08 TAN: Change static to private 
	EditText description;//2013-01-08 TAN: Change static to private 
	private static TwoStateButton todayBtn;
	private static TwoStateButton tomorrowBtn;
	private static TwoStateButton customBtn;
	private static TwoStateButton priorityHigh;
	private static TwoStateButton priorityMedium;
	private static TwoStateButton priorityLow;
	private static TextView assigneeName;
	private static TextView customDateText;
	private static TextView titleText;
	private static WheelView hours;
	private static WheelView mins;
	private static WheelView amPM;
	private static WheelView calList;
	private static WheelView reminders;
	protected static Calendar dueDate;
	private static ArrayList<ATLCalendarModel> calListModel;
	private  ATLTaskCellData taskTemp;
	// SHARON
	private String message;
	private ATLContactModel invitee;
	private static int currentPriority = 1;
	private AtlasServerConnect parseConnect;
	private TextView customDateText1;
	private View navBarView;
	private LinearLayout whoTableRow;
	private static TASK_CATEGORY taskCategory;

	// END SHARON
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		mInflater = LayoutInflater.from(this);
		mLayout = (View) mInflater.inflate(R.layout.task_edit_2, null);
		setContentView(mLayout);
		taskTemp = EditTaskModel.getInstance().getTaskCellData();
		initAttributes();
		bindingData();
		setListener();

// ///SHARON EDIT
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			message = extras.getString("message");

			invitee = extras
					.getParcelable("com.atlasapp.model.ATLContactModel");

			if (invitee != null)
				assigneeName.setText(invitee.displayName());
			// message = "Hello "+invitee.getFirstname()+" ! ";
			// if (!message.equals(""))
			// alertUser("", message);

			setInfoFromEdit();

		}
		// //SHARON END SECTION

	}
	
	private void disableAllButton(){
		saveBtn.setClickable(false);
		cancelBtn.setClickable(false);
		todayBtn.imgButton.setClickable(false);
		tomorrowBtn.imgButton.setClickable(false);
		customBtn.imgButton.setClickable(false);
		priorityHigh.imgButton.setClickable(false);
		priorityMedium.imgButton.setClickable(false);
		priorityLow.imgButton.setClickable(false);
		assigneeName.setClickable(false);
		whoTableRow.setClickable(false);
	}
	
	private void enableAllButton(){
		saveBtn.setClickable(true);
		cancelBtn.setClickable(true);
		todayBtn.imgButton.setClickable(true);
		tomorrowBtn.imgButton.setClickable(true);
		customBtn.imgButton.setClickable(true);
		priorityHigh.imgButton.setClickable(true);
		priorityMedium.imgButton.setClickable(true);
		priorityLow.imgButton.setClickable(true);
		assigneeName.setClickable(true);
		whoTableRow.setClickable(true);
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		enableAllButton();
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		ATLAnimationUtils.hideKeyboard(mContext, title);
		ATLAnimationUtils.hideKeyboard(mContext, description);
	}
	
	private void setInfoFromEdit() {

		title.setText(taskTemp.taskCellTitle);
	//	description.setText(taskTemp.taskCellDescription);
		if(taskTemp.taskCellDueDate != null){//2013-01-08 TAN: fix crash when null pointer
			dueDate.setTime(taskTemp.taskCellDueDate);
		}
		else{//2013-01-08 TAN: fix crash when null pointer
			taskTemp.taskCellDueDate = new Date();
			dueDate.setTime(taskTemp.taskCellDueDate);
		}

//		priorityHigh.resetState();
//		priorityMedium.resetState();
//		priorityLow.resetState();
		currentPriority = (priorityHigh.state==1)? 1: (priorityMedium.state==1)? 2 : 3;
		switch (currentPriority) {
		case 1:
			priorityHigh.stateChanged();
			break;
		case 2:
			priorityMedium.stateChanged();
			break;
		case 3:
			priorityLow.stateChanged();
			break;

		}

	}

	private void setListener() {
		// TODO Auto-generated method stub
		// //SHARON SECTION for connecting to Parse

		saveBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				disableAllButton();
				//setInfoFromEdit(); For what //2013-01-08 TAN: fix crash when null pointer
				save();// Tan: update current data
				
				if(taskTemp.taskCellTitle.trim().equals("")){
					String msg = "Please fill in task title";
					String title = "Alert";
					ATLAlertDialogUtils.showAlert(mContext, title, msg);
					enableAllButton();
					return;
				}
				saveOnDB();// send to server and save to database
			}
		});

				// //END OF SHARON SECTION
		cancelBtn.setOnClickListener(this);

		todayBtn.imgButton.setOnClickListener(this);
		tomorrowBtn.imgButton.setOnClickListener(this);
		customBtn.imgButton.setOnClickListener(this);

		priorityHigh.imgButton.setOnClickListener(this);
		priorityMedium.imgButton.setOnClickListener(this);
		priorityLow.imgButton.setOnClickListener(this);

		assigneeName.setOnClickListener(this);
		
		// Who TableRow
		whoTableRow = (LinearLayout) findViewById(R.id.whoTableRow);
		if(whoTableRow != null){
			whoTableRow.setOnClickListener(new OnClickListener() {
	
				@Override
				public void onClick(View v) {
					 disableAllButton();
					 Intent intent = new Intent(mLayout.getContext(), ATLContactListActivity.class);
					 intent.putExtra("from", "event");
					 intent.putExtra("mode", ATLContactListActivity.MODE_PICKER);
					 startActivityForResult(intent, CONTACT_PICKER_RESULT);
				}
			}); 
		}
		
	
	}
	@SuppressWarnings("deprecation")
	private void alertUser(String messageTitle, String message) {
		AlertDialog alertDialog = new AlertDialog.Builder(EditTask.this)
				.create();

		// Setting Dialog Title
		alertDialog.setTitle(messageTitle);

		// Setting Dialog Message
		alertDialog.setMessage(message);

		// Setting Icon to Dialog
		// alertDialog.setIcon(R.drawable.tick);

		// Setting OK Button
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				// Write your code here to execute after dialog closed
				// Toast.makeText(getApplicationContext(), "You clicked on OK",
				// Toast.LENGTH_SHORT).show();
				finish();//2013-01-08 TAN: fix crash when null pointer
				((Activity)mContext).overridePendingTransition(R.anim.stand_by, R.anim.out_to_bottom);
			}
		});
		alertDialog.setCanceledOnTouchOutside(false);// 2013-02-02 TAN: Avoid tap outside to dismiss
		// Showing Alert Message
		alertDialog.show();
	}
	private void initAttributes() {
		// TODO Auto-generated method stub
		navBarView = (View) findViewById(R.id.topMenuBar);
		titleText = (TextView) findViewById(R.id.task_edit_title_text);
		saveBtn = (ImageButton) findViewById(R.id.btnSave);
		cancelBtn = (ImageButton) findViewById(R.id.btnCancel);
		title = (EditText) findViewById(R.id.edit_task_what_text_new);
		description = (EditText) findViewById(R.id.edit_task_description_etxt);
		// 2012-11-12 TAN: Singleton Unit test
		Log.v("EditTask", "Singleton: EditTaskModel Title : "
				+ taskTemp.taskCellTitle);
		// Init due date buttons
		todayBtn = new TwoStateButton(this, R.drawable.task_edit_icon_today,
				R.drawable.task_edit_icon_today_select,
				(ImageButton) findViewById(R.id.taskBtnToday));

		tomorrowBtn = new TwoStateButton(this,
				R.drawable.task_edit_icon_tomorrow,
				R.drawable.task_edit_icon_tomorrow_select,
				(ImageButton) findViewById(R.id.taskBtnTomorrow));

		customBtn = new TwoStateButton(this, R.drawable.task_edit_icon_custom,
				R.drawable.task_edit_icon_custom_select,
				(ImageButton) findViewById(R.id.taskBtnCustom));
		customDateText = (TextView) findViewById(R.id.edit_task_custom_due_text);
		customDateText1 = (TextView) findViewById(R.id.edit_task_custom_due_text1);

		// Init priority button
		priorityHigh = new TwoStateButton(this, R.drawable.task_edit_icon_high,
				R.drawable.task_edit_icon_high_select,
				(ImageButton) findViewById(R.id.taskBtnHigh));

		priorityMedium = new TwoStateButton(this,
				R.drawable.task_edit_icon_medium,
				R.drawable.task_edit_icon_medium_select,
				(ImageButton) findViewById(R.id.taskBtnMedium));

		priorityLow = new TwoStateButton(this, R.drawable.task_edit_icon_low,
				R.drawable.task_edit_icon_low_select,
				(ImageButton) findViewById(R.id.taskBtnLow));

		// Init who view
		assigneeName = (TextView) findViewById(R.id.edit_task_who);

		hours = (WheelView) findViewById(R.id.edit_task_due_hour_wheel);
		hours.setViewAdapter(new NumericWheelAdapter(this, 1, 12));

		mins = (WheelView) findViewById(R.id.edit_task_due_minute_wheel);
		mins.setViewAdapter(new NumericWheelAdapter(this, 0, 59, "%02d"));
		mins.setCyclic(true);

		String amPM_arr[] = new String[] { "AM", "PM" };
		String reminderNames[] = new String[] { "Off", "0min", "5min", "15min",
				"30min", "1hour", "2hours" };
		amPM = (WheelView) findViewById(R.id.edit_task_due_AMPM_wheel);
		amPM.setViewAdapter(new ArrayWheelAdapter<String>(this, amPM_arr));

		reminders = (WheelView) findViewById(R.id.edit_task_reminder_names);
		reminders.setViewAdapter(new ArrayWheelAdapter<String>(this,
				reminderNames));
		reminders.setCurrentItem(taskTemp.taskCellRemider);

		//================================================================
		// 2013-02-04 TAN: move the code to a new method setUpCalendarWheel()# start
		//================================================================
		calList = (WheelView) findViewById(R.id.edit_task_calendar_names);
		setUpCalendarWheel();
		
		//================================================================
		// 2013-02-04 TAN: move the code to a new method setUpCalendarWheel() # end
		//================================================================
		
	}

	private void setUpCalendarWheel() {
		// TODO Auto-generated method stub
		calListModel = ATLCalendarManager.getCalendars(this);
		calList.setViewAdapter(new ATLCalendarScrollListAdapter(calListModel,
				this));
		calList.addChangingListener(this);

		int index = 0;
		for (ATLCalendarModel calModel : calListModel) {
			if (taskTemp.taskCellCalendarName == null
					|| taskTemp.taskCellCalendarName.equals("")) {// Default
				// value
				calList.setCurrentItem(0);
				break;
			} else if (taskTemp.taskCellCalendarName.equals(calModel.name)) {
				calList.setCurrentItem(index);
				break;
			}
			index++;
		}
	}

	private void bindingData() {
		// TODO Auto-generated method stub

		title.setText(taskTemp.taskCellTitle);
		description.setText(taskTemp.taskCellDescription);
		dueDate = Calendar.getInstance();

		if (EditTaskModel.isNewTask) { // New Task
			// Show keyboard when new task
			title.postDelayed(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					ATLAnimationUtils.showKeyboard(mContext);
					setUpCalendarWheel();// TAN : fixed, sometime show blank calendar item
				}
			}, 300);
			
			
			titleText.setText(getResources().getString(
					R.string.task_edit_add_title));
			todayBtn.stateChanged();
			priorityHigh.stateChanged();

			// set current time
			Calendar c = Calendar.getInstance();
			int curHours = c.get(Calendar.HOUR_OF_DAY);
			int curMinutes = c.get(Calendar.MINUTE);

			hours.setCurrentItem(determineClockHour(curHours) - 1);
			mins.setCurrentItem(curMinutes);
			amPM.setCurrentItem(determineAmPm(curHours));
			dueDate.setTime(new Date());
			setDueDate();
			//TAN set default value at index 0
			calList.setCurrentItem(0);
			taskTemp.taskCellCalendarColor = calListModel.get(0).color;
			taskTemp.calendarId = calListModel.get(0).id;
			taskTemp.taskCellCalendarName = calListModel.get(0).name;
			// hours.setCurrentItem(2, true);
			// mins.setCurrentItem(15, true);
			// amPM.setCurrentItem(0, true);
		} else { // Edit Task

			navBarView.setBackgroundResource(R.drawable.navbar);
			saveBtn.setImageResource(R.drawable.btn_done);
			cancelBtn.setImageResource(R.drawable.btn_cancel);
			// Date dueDate =
			// taskTemp.taskCellDate;
			titleText.setText(getResources()
					.getString(R.string.task_edit_title));
//			assigneeName.setText(taskTemp.taskDelegatedName);
//			
//			Log.v("EditTask", "setDue load:" + dueDate.getTime().toString());
			if (taskTemp.taskCellDueDate == null) {
				dueDate.setTime(new Date());
			} else {
				dueDate.setTime(taskTemp.taskCellDueDate);
			}
			Date today = new Date();
			// set current time
			Calendar calToday = Calendar.getInstance();
			calToday.setTime(today);
			// int curHours = calToday.get(Calendar.HOUR_OF_DAY);
			// int curMinutes = calToday.get(Calendar.MINUTE);

			// Calendar calDue = new GregorianCalendar(TimeZone.getDefault());

			int dueHours = dueDate.get(Calendar.HOUR_OF_DAY);
			int dueMinutes = dueDate.get(Calendar.MINUTE);

//			if (dueDate.get(Calendar.YEAR) == calToday.get(Calendar.YEAR)
//					&& dueDate.get(Calendar.MONTH) == calToday
//							.get(Calendar.MONTH)) {
				if (CalendarUtilities.isToday(dueDate.getTime())) {
					todayBtn.stateChanged();
					customDateText.setText(DateFormat.format(CUSTOM_DAY_TEXT_FORMAT_WK_DAY,
							dueDate.getTime()));
					customDateText1.setText(DateFormat.format(CUSTOM_DAY_TEXT_FORMAT_MTH_DAY,
							dueDate.getTime()));
				} else if (CalendarUtilities.isTomorrow(dueDate.getTime())) {
					tomorrowBtn.stateChanged();
					customDateText.setText(DateFormat.format(CUSTOM_DAY_TEXT_FORMAT_WK_DAY,
							dueDate.getTime()));
					customDateText1.setText(DateFormat.format(CUSTOM_DAY_TEXT_FORMAT_MTH_DAY,
							dueDate.getTime()));
				} else {
					customBtn.stateChanged();
					customDateText.setText(DateFormat.format(CUSTOM_DAY_TEXT_FORMAT_WK_DAY,
							dueDate.getTime()));
					customDateText1.setText(DateFormat.format(CUSTOM_DAY_TEXT_FORMAT_MTH_DAY,
							dueDate.getTime()));
				}
//
//			} else {
//				customBtn.stateChanged();
//				customDateText.setText(DateFormat.format(CUSTOM_DAY_TEXT_FORMAT_WK_DAY,
//						dueDate.getTime()));
//				customDateText1.setText(DateFormat.format(CUSTOM_DAY_TEXT_FORMAT_MTH_DAY,
//						dueDate.getTime()));
//			}

			hours.setCurrentItem(determineClockHour(dueHours) - 1);
			mins.setCurrentItem(dueMinutes);
			amPM.setCurrentItem(determineAmPm(dueHours));
			switch (taskTemp.taskCellPriorityInt) {
			case ATLTaskPriorityScrollListAdapter.TASK_PRIORITY_INDEX_HIGH:
				// resourceID = R.drawable.tasks_flame_blue;
				priorityHigh.stateChanged();
				break;
			case ATLTaskPriorityScrollListAdapter.TASK_PRIORITY_INDEX_MEDIUM:
				// resourceID = R.drawable.tasks_very_important_blue;
				priorityMedium.stateChanged();
				break;
			case ATLTaskPriorityScrollListAdapter.TASK_PRIORITY_INDEX_LOW:
				// resourceID = R.drawable.tasks_important_blue;
				priorityLow.stateChanged();
				break;

			}

		}

	}

	// Calendar currentDate = Calendar.getInstance();
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

//		if (v == saveBtn) {
//			// Call back
//			save();
//			setResult(ATLTaskIntentKeys.RESULT_FROM_TASK_EDIT);
//			finish();
//		} else
		if (v == cancelBtn) {
			// EditTaskModel.getInstance().destroy();
			disableAllButton();
			setResult(ATLTaskIntentKeys.RESULT_FROM_TASK_EDIT_CANCEL);
			finish();
			overridePendingTransition(R.anim.stand_by, R.anim.out_to_bottom);
		} else if (v == todayBtn.imgButton || v == tomorrowBtn.imgButton
				|| v == customBtn.imgButton) {
			resetAllDueDateStatus();
			if (v == todayBtn.imgButton) {
				todayBtn.stateChanged();
				Date today = new Date();
				dueDate.setTime(today);
				customDateText.setText(DateFormat.format(CUSTOM_DAY_TEXT_FORMAT_WK_DAY,
						dueDate.getTime()));
				customDateText1.setText(DateFormat.format(CUSTOM_DAY_TEXT_FORMAT_MTH_DAY,
						dueDate.getTime()));
			} else if (v == tomorrowBtn.imgButton) {
				tomorrowBtn.stateChanged();
				Calendar tdDate = Calendar.getInstance(TimeZone.getDefault());
				tdDate.setTime(new Date());
				dueDate.set(Calendar.YEAR, tdDate.get(Calendar.YEAR));
				dueDate.set(Calendar.DAY_OF_YEAR,
						tdDate.get(Calendar.DAY_OF_YEAR) + 1);

				customDateText.setText(DateFormat.format(CUSTOM_DAY_TEXT_FORMAT_WK_DAY,
						dueDate.getTime()));
				customDateText1.setText(DateFormat.format(CUSTOM_DAY_TEXT_FORMAT_MTH_DAY,
						dueDate.getTime()));
			} else if (v == customBtn.imgButton) {
				customBtn.stateChanged();
				Intent intent = new Intent(getBaseContext(),
						CalendarMonthView.class);
				intent.putExtra(ATLCalendarIntentKeys.KEY_CURRENT_DAY_VIEW,
						dueDate.getTimeInMillis());
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				// //intent.putExtra("EXTRA_SESSION_ID", sessionId);
				startActivityForResult(intent,
						ATLCalendarIntentKeys.CALL_FROM_TASK_EDIT);
				// chooseDate();

			}
		} else if (v == priorityHigh.imgButton || v == priorityLow.imgButton
				|| v == priorityMedium.imgButton) {
			resetAllPriorityStatus();
			if (v == priorityHigh.imgButton) {
				priorityHigh.stateChanged();
			} else if (v == priorityLow.imgButton) {
				priorityLow.stateChanged();
			} else if (v == priorityMedium.imgButton) {
				priorityMedium.stateChanged();
			}
		} 
		else if (v == assigneeName) {
			disableAllButton();
			save();// / SAVE ALL TASK INFO TILL NOW...
			
			//HARRIS
			Intent intent = new Intent(this, ATLContactListActivity.class);
			 intent.putExtra("mode", ATLContactListActivity.MODE_PICKER);
			 intent.putExtra("from", "event");
			 startActivityForResult(intent, CONTACT_PICKER_RESULT);
			
			
			//Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
			//		Contacts.CONTENT_URI);
			//startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
		}
		
		
	}

	private void resetAllPriorityStatus() {
		// TODO Auto-generated method stub
		priorityHigh.resetState(); 
		priorityLow.resetState();
		priorityMedium.resetState();
	}

	private void resetAllDueDateStatus() {
		// TODO Auto-generated method stub
		todayBtn.resetState();
		tomorrowBtn.resetState();
		customBtn.resetState();
	}
	
	private void setDueDate(){
		customDateText.setText(DateFormat.format(CUSTOM_DAY_TEXT_FORMAT_WK_DAY,
				dueDate.getTime()));
		customDateText1.setText(DateFormat.format(CUSTOM_DAY_TEXT_FORMAT_MTH_DAY,
				dueDate.getTime()));
	}

	private void chooseDate() {
		// TODO Auto-generated method stub
		// Calendar dueDate = Calendar.getInstance(TimeZone.getDefault());
		new DatePickerDialog(this, dateListener, dueDate.get(Calendar.YEAR),
				dueDate.get(Calendar.MONTH), dueDate.get(Calendar.DAY_OF_MONTH))
				.show();

	}

	private DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			dueDate.set(year, monthOfYear, dayOfMonth);
			setDueDate();
		}
	};
	

	/**
	 * 
	 * @author sharon nachum
	 */
	private void saveOnDB() {
		
		if(invitee != null){
		parseConnect = AtlasServerConnect.getSingletonObject(this);
		taskCategory = (currentPriority==1)? TASK_CATEGORY.High: 
			 		   (currentPriority==2)? TASK_CATEGORY.Medium: 
			 		   (currentPriority==3)? TASK_CATEGORY.Low: 
			 		   TASK_CATEGORY.High;   
		SimpleDateFormat lv_formatter;  
		lv_formatter = new SimpleDateFormat("yyyy:DD:MM HH:mm:ss");
		lv_formatter.setTimeZone(TimeZone.getTimeZone("UTC"));  
		String taskContent = description.getText().toString();
		Date taskDate = taskTemp.taskCellDueDate;
		Date anotherDate = new Date(taskDate.getTime() );
		String lv_dateFormateInUTC = lv_formatter.format(anotherDate); 
		
//		parseConnect.taskAssign(invitee, title.getText().toString(), 
//				taskContent, taskCategory, anotherDate,lv_dateFormateInUTC );
		
		}
		/*
		 * Nghia: save alert status to AlertView.
		 * @Sharon: We need call back method to handle save to server successfully or not successfully
		 */
		
		taskTemp.taskCellModifiedDate = new Date();
		taskTemp.taskResponseStatus = TaskResponseType.taskResponseType_AssignmentsPending;
		taskTemp.save();
		

		if (invitee != null) {// 2013-01-08 TAN: fix crash when null pointer
			alertUser("Task assignment",
					"Task assigned to " + invitee.getFirstname()
							+ " was sent");
		} else {
			finish();
			overridePendingTransition(R.anim.stand_by, R.anim.out_to_bottom);
		}
	}
    
	private void save() {
		// TODO Auto-generated method stub
		taskTemp.deNull();
		taskTemp.taskCellTitle = title.getText().toString();
		taskTemp.taskCellDescription = description.getText().toString();
		taskTemp.taskCellReceiverName = assigneeName.getText().toString();
//		taskTemp.taskCellDescription = description
//				.getText().toString();
		taskTemp.taskCategory = taskCategory;
		Calendar c = new GregorianCalendar(TimeZone.getDefault());
		c.setTime(new Date());
		int amPmInt = amPM.getCurrentItem();
		int curMinutes = mins.getCurrentItem();
		int today = c.get(Calendar.DAY_OF_MONTH);
		int month = c.get(Calendar.MONTH);
		int year = c.get(Calendar.YEAR);
		int tommorow = today + 1;

		int curHours = hours.getCurrentItem() + 1;
		if (amPmInt == 0) {
			if (curHours == 12) {
				curHours = 0;
			}
		} else {
			if (curHours != 12) {
				curHours += 12;
			}
		}

		if (todayBtn.state == 1) {
			Calendar c1 = Calendar.getInstance(TimeZone.getDefault());
			c1.set(year, month, today, curHours, curMinutes, 0);

			taskTemp.taskCellDueDate = c1.getTime();
			// Log.v("EditTask", "setDue save c1:" + c1.getTime().toString());
		} else if (tomorrowBtn.state == 1) {
			Calendar c2 = Calendar.getInstance(TimeZone.getDefault());
			c2.set(year, month, tommorow, curHours, curMinutes, 0);
			taskTemp.taskCellDueDate = c2.getTime();
			// Log.v("EditTask", "setDue save c2:" + c2.getTime().toString());

		} else if (customBtn.state == 1) {
			Calendar c3 = Calendar.getInstance(TimeZone.getDefault());
			c3.set(dueDate.get(Calendar.YEAR), dueDate.get(Calendar.MONTH),
					dueDate.get(Calendar.DAY_OF_MONTH), curHours, curMinutes, 0);
			taskTemp.taskCellDueDate = c3.getTime();
			// Log.v("EditTask", "setDue save c3:" + c3.getTime().toString());
		}

		if (priorityHigh.state == TwoStateButton.ACTIVE_STATE) {
			taskTemp.taskCellPriorityInt = ATLTaskPriorityScrollListAdapter.TASK_PRIORITY_INDEX_HIGH;
			taskTemp.taskCellPriority = "High";
		} else if (priorityMedium.state == TwoStateButton.ACTIVE_STATE) {
			taskTemp.taskCellPriorityInt = ATLTaskPriorityScrollListAdapter.TASK_PRIORITY_INDEX_MEDIUM;
			taskTemp.taskCellPriority = "Medium";
		} else if (priorityLow.state == TwoStateButton.ACTIVE_STATE) {
			taskTemp.taskCellPriority = "Low";
			taskTemp.taskCellPriorityInt = ATLTaskPriorityScrollListAdapter.TASK_PRIORITY_INDEX_LOW;
		}
		taskTemp.taskCellRemider = reminders.getCurrentItem();
		setResult(ATLTaskIntentKeys.RESULT_FROM_TASK_EDIT);//2012-01-08 TAN: reload when back to Tasks
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		switch (requestCode) {
//		case CONTACT_PICKER_RESULT:
			if (resultCode == ATLContactListActivity.RESULT_FROM_CONTACTLIST) {
				
				ATLContactModel contact = (ATLContactModel) data.getExtras()
						.getParcelable("com.atlasapp.model.ATLContactModel");
				if(contact != null){
					invitee = contact;
					taskTemp.taskCellAttendee = contact; // NGHIA add to currentCellData
					assigneeName.setText(contact.displayName());
					ImageView imageContact = (ImageView) findViewById(R.id.imageContact);
					imageContact.setImageBitmap(contact.getImage());
				}
				// //SHARON MODIFICATION....
				//Uri contactData = data.getData();
				//pickContactFromAddressBook(contactData);

				// ////////////END SHARON SECTION
				// //TAN PREVIOUS VERSION
				// Cursor cursor = null;
				// String phone = "";
				// try {
				// Bundle extras = data.getExtras();
				// Set<String> keys = extras.keySet();
				// Iterator<String> iterate = keys.iterator();
				// while (iterate.hasNext()) {
				// String key = iterate.next();
				// Log.v(DEBUG_TAG, key + "[" + extras.get(key) + "]");
				// }
				//
				// Uri result = data.getData();
				// Log.v(DEBUG_TAG,
				// "Got a contact result: " + result.toString());
				//
				// // get the contact id from the Uri
				// String id = result.getLastPathSegment();
				//
				// cursor = getContentResolver().query(Phone.CONTENT_URI,
				// null, Phone.CONTACT_ID + "=?", new String[] { id },
				// null);
				//
				// int PhoneIdx = cursor.getColumnIndex(Phone.DATA);
				//
				// if (cursor.moveToFirst()) {
				//
				// phone = cursor.getString(PhoneIdx);
				//
				// Log.v(DEBUG_TAG, "Got number: " + phone);
				//
				// } else {
				// Log.w(DEBUG_TAG, "No results");
				// }
				// } catch (Exception e) {
				// Log.e(DEBUG_TAG, "Failed to Number", e);
				// } finally {
				// if (cursor != null) {
				// cursor.close();
				// }
				//
				// assigneeName.setText(phone);
				//
				// if (phone.length() == 0) {
				// Toast.makeText(this, "No number found for contact.",
				// Toast.LENGTH_LONG).show();
				// }
				//
				// }////////END TAN PREVIOUS VERSION
			}
			//	break;
			
		   if( requestCode== ATLCalendarIntentKeys.CALL_FROM_TASK_EDIT){
				if (resultCode == ATLCalendarIntentKeys.MONTH_VIEW_RETURN_KEY) {
					SimpleDateFormat dateFormatter = new SimpleDateFormat(
							"dd-MMM-yyyy");
					String returnDateStr = data.getExtras().getString(
							ATLCalendarIntentKeys.MONTH_VIEW_RETURN_DAY);
					Date taskTempDate = new Date();
					try {
						taskTempDate = dateFormatter.parse(returnDateStr);
						// Toast.makeText(this, taskTempDate.toString(),
						// Toast.LENGTH_SHORT).show();
						Calendar cal = Calendar.getInstance();
						cal.setTime(taskTempDate);
						dueDate.set(cal.get(Calendar.YEAR),
								cal.get(Calendar.MONTH),
								cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
						// Toast.makeText(this, dueDate.getTime().toString(),
						// Toast.LENGTH_SHORT).show();
						customDateText.setText(DateFormat.format(CUSTOM_DAY_TEXT_FORMAT_WK_DAY,
								dueDate.getTime()));
						customDateText1.setText(DateFormat.format(CUSTOM_DAY_TEXT_FORMAT_MTH_DAY,
								dueDate.getTime()));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} 
			}
			//else if (resultCode == RESULT_OK) {
//
//				Cursor cursor = null;
//				String phone = "";
//				try {
//					Bundle extras = data.getExtras();
//					Set<String> keys = extras.keySet();
//					Iterator<String> iterate = keys.iterator();
//					while (iterate.hasNext()) {
//						String key = iterate.next();
//						Log.v(DEBUG_TAG, key + "[" + extras.get(key) + "]");
//					}
//
//					Uri result = data.getData();
//					Log.v(DEBUG_TAG,
//							"Got a contact result: " + result.toString());
//
//					// get the contact id from the Uri
//					String id = result.getLastPathSegment();
//
//					cursor = getContentResolver().query(Phone.CONTENT_URI,
//							null, Phone.CONTACT_ID + "=?", new String[] { id },
//							null);
//
//					int PhoneIdx = cursor.getColumnIndex(Phone.DATA);
//
//					if (cursor.moveToFirst()) {
//
//						phone = cursor.getString(PhoneIdx);
//
//						Log.v(DEBUG_TAG, "Got number: " + phone);
//
//					} else {
//						Log.w(DEBUG_TAG, "No results");
//					}
//				} catch (Exception e) {
//					Log.e(DEBUG_TAG, "Failed to Number", e);
//				} finally {
//					if (cursor != null) {
//						cursor.close();
//					}
////					assigneeName.setText(phone);
////					if (phone.length() == 0) {
////						Toast.makeText(this, "No number found for contact.",
////								Toast.LENGTH_LONG).show();
////					}
//
//				}
//			}
		//}

	}
	/**
	 * 
	 * 
	 * 
	 * @author sharon nachum
	 * @param contactData
	 */
	private void pickContactFromAddressBook(Uri contactData) {
		Cursor cursor = managedQuery(contactData, null, null, null, null);
		String name = "";
		String phoneNumber = "";
		String emailAddress = "";
		String contactId = "";
		String key = "";
		if (cursor.moveToFirst()) {
			contactId = cursor.getString(cursor
					.getColumnIndex(ContactsContract.Contacts._ID));
			key = cursor.getString(cursor
					.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
			name = cursor
					.getString(cursor
							.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));

			String hasPhone = cursor
					.getString(cursor
							.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

			if (hasPhone.equalsIgnoreCase("1"))
				hasPhone = "true";
			else
				hasPhone = "false";

			if (Boolean.parseBoolean(hasPhone)) {
				Cursor phones = getContentResolver().query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						null,
						ContactsContract.CommonDataKinds.Phone.CONTACT_ID
								+ " = " + contactId, null, null);
				while (phones.moveToNext()) {
					phoneNumber = phones
							.getString(phones
									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				}
				phones.close();
			}

			// Find Email Addresses
			Cursor emails = getContentResolver().query(
					ContactsContract.CommonDataKinds.Email.CONTENT_URI,
					null,
					ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = "
							+ contactId, null, null);
			while (emails.moveToNext()) {
				emailAddress = emails
						.getString(emails
								.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
			}
			emails.close();
		}
		name = (name != null) ? name : "";
		phoneNumber = (phoneNumber != null) ? phoneNumber : "";
		emailAddress = (emailAddress != null) ? emailAddress : "";

		// final Bitmap photo = retrieveContactPhoto(id);
		Bitmap contactPhoto = null;
		byte[] photoArray = null;
		try {
			InputStream inputStream = ContactsContract.Contacts
					.openContactPhotoInputStream(getContentResolver(),
							ContentUris.withAppendedId(
									ContactsContract.Contacts.CONTENT_URI,
									new Long(contactId)));

			if (inputStream != null) {
				contactPhoto = BitmapFactory.decodeStream(inputStream);

				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				contactPhoto.compress(Bitmap.CompressFormat.PNG, 100, stream);
				photoArray = stream.toByteArray();

				// ImageView imageView = (ImageView)
				// findViewById(R.id.img_contact);
				// imageView.setImageBitmap(photo);
			}

			assert inputStream != null;
			if (inputStream != null)
				inputStream.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		// Intent intentInvite = new Intent(getBaseContext(),
		// InviteContact.class);
		Intent intentInvite = new Intent(getBaseContext(), ContactCard.class);
		intentInvite.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		intentInvite.putExtra("contact_name", name);
		intentInvite.putExtra("contact_email", emailAddress);
		intentInvite.putExtra("contact_mobile", phoneNumber);
		intentInvite.putExtra("contact_photo", photoArray);
		intentInvite.putExtra("invite_type", "TaskAssign");
		startActivity(intentInvite);
	}
	public class TwoStateButton extends RelativeLayout implements
			OnClickListener {
		public static final int ACTIVE_STATE = 1;
		public static final int IN_ACTIVE_STATE = 0;
		public ImageButton imgButton;
		int imgID0;
		int imgID1;
		public int state = IN_ACTIVE_STATE;

		public TwoStateButton(Context context, int imgID0, int imgID1,
				ImageButton imgButon) {
			super(context);
			// TODO Auto-generated constructor stub
			imgButton = new ImageButton(context);
			this.imgID0 = imgID0;
			this.imgID1 = imgID1;
			this.imgButton = imgButon;
		    imgButton.setImageResource(imgID0);
			// imgButton.setBackgroundColor(Color.TRANSPARENT);
//			imgButton.setBackgroundResource(imgID0);
			// imgButton.setBackgroundColor(Color.TRANSPARENT);
			// this.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
			// LayoutParams.WRAP_CONTENT));
			// imgButton.setOnClickListener(EditTask.this);
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (state == IN_ACTIVE_STATE) {
				state = ACTIVE_STATE;
				 imgButton.setImageResource(imgID1);
				// imgButton.setBackgroundColor(Color.TRANSPARENT);
//				imgButton.setBackgroundResource(imgID1);
			}

		}

		public void resetState() {
			state = IN_ACTIVE_STATE;
			 imgButton.setImageResource(imgID0);
			// imgButton.setBackgroundColor(Color.TRANSPARENT);
//			imgButton.setBackgroundResource(imgID0);
		}

		public void stateChanged() {
			if (state == IN_ACTIVE_STATE) {
				state = ACTIVE_STATE;
				 imgButton.setImageResource(imgID1);
				// imgButton.setBackgroundColor(Color.TRANSPARENT);
//				imgButton.setBackgroundResource(imgID1);
			}
		}

	}

	private int determineClockHour(int hour) {
		if (hour == 0) {
			return 12;
		}
		if (hour > 12) {
			return hour - 12;
		}
		return hour;
	}

	private int determineAmPm(int hour) {
		return hour < 12 ? 0 : 1;
	}

	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue) {
		// TODO Auto-generated method stub
		if (wheel == calList) {
			taskTemp.taskCellCalendarColor = calListModel.get(newValue).color;
			taskTemp.calendarId = calListModel.get(newValue).id;
			taskTemp.taskCellCalendarName = calListModel.get(newValue).name;
		}
	}

}