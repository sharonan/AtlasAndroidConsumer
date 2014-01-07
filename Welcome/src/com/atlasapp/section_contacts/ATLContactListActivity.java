//  ==================================================================================================================
//  ATLContactListActivity.java
//  AtlasConsumerAndroid
//  Copyright (c) 2012 AtlasConsumerAndroid Apps. All rights reserved.
//  ==================================================================================================================
//
//  ==================================================================================================================
//  HISTORY
//  YYYY-MM-DD NAME:    Description of changes
//  ==================================================================================================================
//  2012-11-14 HARRIS:    Created Class
//  ==================================================================================================================

package com.atlasapp.section_contacts;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import android.R.layout;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.GroupMembership;
import android.provider.ContactsContract.CommonDataKinds.Note;
import android.provider.ContactsContract.CommonDataKinds.Organization;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.provider.ContactsContract.CommonDataKinds.Website;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.atlasapp.common.ATLAddBarView;
import com.atlasapp.common.ATLAnimationUtils;
import com.atlasapp.common.ATLDragAndDropView;
import com.atlasapp.atlas_database.ATLDBCommon;
import com.atlasapp.common.ATLFriendLocalTable;
import com.atlasapp.common.ATLTabbarView;
import com.atlasapp.common.ATLTabbarViewDelegete;
import com.atlasapp.common.CurrentSessionFriendsList;
import com.atlasapp.common.Utilities;
import com.atlasapp.model.ATLContactModel;
import com.atlasapp.model.ATLFriendModel;
import com.atlasapp.section_alerts.ATLAlertFragment_2;
import com.atlasapp.section_appentry.R;
import com.atlasapp.section_calendar.CalendarDayView;
import com.atlasapp.section_calendar.CalendarEditView;
import com.atlasapp.section_main.ATLMainTabbarActivity;
import com.atlasapp.section_main.ATLMultiSectionListView;
import com.atlasapp.section_main.ATLTopbar;
import com.atlasapp.section_notes.ATLNoteListActivity;
import com.atlasapp.section_settings.Settings;
import com.atlasapp.section_slidemenu.ATLSlideMenuFragment;
import com.atlasapp.section_tasks.ATLTaskListActivity;
import com.atlasapp.section_tasks.EditTask;
import com.atlasapp.section_tasks.Tasks;
import com.ericharlow.DragNDrop.DragListener;
import com.ericharlow.DragNDrop.DropListener;

public class ATLContactListActivity extends FragmentActivity {
	LayoutInflater mInflater;
	Context mContext;
	ViewGroup mLayout;
//	ATLTopbar mTopBar;
	ATLMultiSectionListView listContacts;
	int mode;
	private CurrentSessionFriendsList currentSessionFriendsList;
//	private ATLTabbarView tabbar;
	private static ArrayList<ATLContactModel> unsortedFriendA;
	private String from;
	private String sort;
	private ATLDBCommon atlDBCommon;
	private ImageButton sort_btn;
	private ImageButton alert_btn;
	private View addbar;
	public ATLDragAndDropView contactHolderView;
	public ViewGroup alertHolderView;
	public ViewGroup slideMenuHolderView;
	public View contactViewCover;
	public int topMenuBarHeight;
	public ATLSlideMenuFragment slideMenuFragment;
	public ATLAlertFragment_2 alertFragment;
	public boolean isFragmentShowing;
	private EditText searchBarTextView;
	private ImageView contactsSearchImage;
	private ATLContactListAdapter adapter;
	private ArrayList<ATLContactModel> contactA;
	
	private static final int EMAIL_TYPE_HOME = 1;
	private static final int EMAIL_TYPE_WORK = 2;
	private static final int PHONE_TYPE_MOBILE = 2;
	private static final int PHONE_TYPE_WORK = 3;
	private static final int ADDRESS_TYPE_HOME = 1;
	
	public static final int MODE_PICKER = 10000;
	public static final int MODE_EDITOR = 2000;
	
	public static final int RESULT_FROM_CONTACTLIST = 11111;
	private boolean isShowAllContacts = true;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		mInflater = LayoutInflater.from(this);
		mLayout = (ViewGroup) mInflater.inflate(R.layout.contact_list, null);
		setContentView(mLayout);  
		
		 currentSessionFriendsList = CurrentSessionFriendsList.getSingletonObject();
		 from  = "";
		 sort = "all";
		unsortedFriendA = (unsortedFriendA==null)?currentSessionFriendsList.getCuurentSessionFriends():unsortedFriendA;
  
			Bundle extras = getIntent().getExtras();
			if (extras != null) {
				mode = extras.getInt("mode");
				 sort = extras.getString("sort");
				 from = extras.getString("from");
				 from = (from==null)? "" :from;
				 sort = (sort==null)? "":sort;
				
				if (sort!=null && !sort.equals(""))
				unsortedFriendA =(sort.equals("all"))? currentSessionFriendsList.getCuurentSessionFriends()
						: currentSessionFriendsList.getCuurentATLSessionFriends();
			} else {
				unsortedFriendA = (unsortedFriendA==null)?currentSessionFriendsList.getCuurentSessionFriends():unsortedFriendA;
				mode = MODE_EDITOR;
			}
			    

		initAttributes();
		setListener();
		 
		bindingData();
		
	
		
		if (!currentSessionFriendsList.isFriendUpdateComplete())
			Toast.makeText(getApplicationContext(), "Retreiving new contacts...", Toast.LENGTH_LONG).show();
		//setMainMenuListener();
		
	}
	
	@Override
	public void onResume(){
		super.onResume();
		if(isShowAllContacts){
			contactsSearchImage.setBackgroundResource(R.drawable.contacts_group_users_0);
		}
		else{
			contactsSearchImage.setBackgroundResource(R.drawable.contacts_group_users_1);
		}
		
	}
  
	 private void backgroundProcesses() {
			currentSessionFriendsList.setFriendsUpdateComplete(false);

			/////SET ALL CURRENT EMAIL'S (USER NAMES) FROM ATLAS DB (PARSE)
			atlDBCommon = ATLDBCommon.getSingletonObject(null);
			atlDBCommon.setCurrentSessionUsersOnATLASInBackground(true);
		
			ATLFriendModel.readAllLocalFriendsInBackground();
	 
	 }
	private void initAttributes() {
		if (unsortedFriendA==null || (unsortedFriendA!=null && unsortedFriendA.size()==0))
		{
			backgroundProcesses();
		}
		else{
			unsortedFriendA = currentSessionFriendsList.getCuurentSessionFriends();
		}
		// TODO Auto-generated method stub
		
		//==========================================================
		// 2013-03-17 TAN: Search bar # START autoCompleteTextView
		//==========================================================
		searchBarTextView = (EditText)mLayout.findViewById(R.id.autoCompleteTextView);
		searchBarTextView.addTextChangedListener(mTextWatcher);
		contactsSearchImage = (ImageView) mLayout.findViewById(R.id.contacts_group_users_image);
		contactsSearchImage.setBackgroundResource(R.drawable.contacts_group_users_0);
		isShowAllContacts = true;
		//==========================================================
		// 2013-03-17 TAN: Search bar # END
		//==========================================================
		//==========================================================
		// 2013-02-23 TAN: new slide out # START
		//==========================================================
		contactHolderView = (ATLDragAndDropView)mLayout.findViewById(R.id.contact_content);
		alertHolderView = (ViewGroup)mLayout.findViewById(R.id.calendar_day_view_alert_fragment);
		slideMenuHolderView = (ViewGroup)mLayout.findViewById(R.id.calendar_day_view_cal_selects_fragment);
		contactViewCover = (View)mLayout.findViewById(R.id.calendar_day_view_cover_view);
		contactViewCover.setOnTouchListener(contactHolderView);
	    // top menu bar height
	    topMenuBarHeight = (int) Math.ceil(50 * this.getResources().getDisplayMetrics().density);
	    
		 if (contactHolderView instanceof ATLDragAndDropView) {
			 ATLDragAndDropView.topMenuBarHeight = topMenuBarHeight;
			 contactHolderView.setDropListener(mDropListener);
			 contactHolderView.setDragListener(mDragListener);
	     }
		 
		slideMenuFragment = new ATLSlideMenuFragment();
			getSupportFragmentManager().beginTransaction()
			.add(R.id.calendar_day_view_cal_selects_fragment, slideMenuFragment).commit();
		alertFragment = ATLAlertFragment_2.getInstance();
			getSupportFragmentManager().beginTransaction()
			.add(R.id.calendar_day_view_alert_fragment, alertFragment).commit();
		//==========================================================
		// 2013-02-23 TAN: new slide out # END
		//==========================================================
//		mTopBar = (ATLTopbar) findViewById(R.id.topBarMenu); // TAN comment out, dont use it anymore
//		mTopBar.setType(ATLMainTabbarActivity.TAB_CONTACTS);
		listContacts = (ATLMultiSectionListView) findViewById(R.id.listContacts);
//		listContacts.setPinnedHeaderView(LayoutInflater.from(this).inflate(
//				R.layout.listview_header, listContacts, false));
//		
		//2013-02-08 TAN: do not show tabbar when call from event or task editing
		sort_btn = (ImageButton) mLayout.findViewById(R.id.sortButton);
		alert_btn = (ImageButton) mLayout.findViewById(R.id.alliesbtn);
		if(!from.equals("event")){
//			tabbar = new ATLTabbarView(this);
//		    tabbar.delegate = this;
		    addbar = new ATLAddBarView(this);
		    contactHolderView.addView(addbar);
//		    tabbar.setVisibility(View.VISIBLE);
		    addbar.setVisibility(View.VISIBLE);
		    sort_btn.setVisibility(View.VISIBLE);
			alert_btn.setVisibility(View.VISIBLE);
		    
		}else{
			sort_btn.setVisibility(View.GONE);
		    alert_btn.setVisibility(View.GONE);
		}
		//2013-02-08 TAN: do not show tabbar when call from event or task editing
	    
	    Button allContactBtn = (Button)findViewById(R.id.all_contacts_btn);
	    allContactBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				2013-01-29 TAN: COMMENT OUT: no need intent again 
//				Intent intenAllSort = new Intent(getBaseContext(),
//						ATLContactListActivity.class);
//				 intenAllSort.putExtra("sort", "all");
//				 //intenAllSort.putExtra("mode", mode);
//				intenAllSort.putExtra("from", from);
//				intenAllSort.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//				startActivity(intenAllSort);
//				2013-01-29 TAN: COMMENT OUT: no need intent again # end
				isShowAllContacts = true;
				from = "from";
				sort = "all";
				contactsSearchImage.setBackgroundResource(R.drawable.contacts_group_users_0);
				unsortedFriendA = currentSessionFriendsList.getCuurentSessionFriends();
				bindingData();
						
			}
		});
	
	    Button atlContactBtn = (Button)findViewById(R.id.atlas_contacts_btn);
	    atlContactBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				2013-01-29 TAN: COMMENT OUT: no need intent again 
//				Intent intenAllSort = new Intent(getBaseContext(),
//						ATLContactListActivity.class);
//				 intenAllSort.putExtra("sort", "atl");
//				// intenAllSort.putExtra("mode", mode);
//				 intenAllSort.putExtra("from", from);
//				intenAllSort.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//				startActivity(intenAllSort);
//				2013-01-29 TAN: COMMENT OUT: no need intent again # end
				isShowAllContacts = false;
				from = "from";
				sort = "atl";
				contactsSearchImage.setBackgroundResource(R.drawable.contacts_group_users_1);
				unsortedFriendA = currentSessionFriendsList.getCuurentATLSessionFriends();
				bindingData();
			}
		});
	    	
	}

	private void bindingData() {
		// ATLFriendModel testFriend = new ATLFriendModel();
		// testFriend.setFirstname("testFirst");
		// testFriend.setLastname("testLast");
		// testFriend.setPhoneNumber("testPhone");
		// testFriend.setEmailAddress("testEmail");
		// testFriend.setPicPath("testPicPath");
		// testFriend.setFromFacebook(true);
		// testFriend.setAtlasId("testAtlasId");
		// testFriend.write();

		Log.i("harris","contactList");
		//HARRIS:
	//	ArrayList<ATLFriendModel> unsortedFriendA = ATLFriendModel.readAll();
	///
		///sharon
		/////
		
		
		
		if (unsortedFriendA!=null && unsortedFriendA.size()>0){
			contactA = new ArrayList<ATLContactModel>(
				unsortedFriendA.size());
		
		
		for (ATLFriendModel friend : unsortedFriendA) {
			contactA.add(new ATLContactModel(friend));
		}
		
		///HARRIS
//		ArrayList<ATLContactModel> addressBookContactA = contactListFromAddressBook();
//		
//		contactA.addAll(addressBookContactA);
//		
		
		
		
		
		

		for (ATLContactModel contact : contactA) {
			contact.setSortValue(contact.displayName());
		}

		Collections.sort(contactA, new Comparator<ATLContactModel>() {
			public int compare(ATLContactModel a, ATLContactModel b) {
				return a.getSortValue().compareToIgnoreCase(b.getSortValue());
			}
		});

		// ArrayList<ATLContactModel> contactA = unsortedContactA
		adapter = new ATLContactListAdapter(contactA,
				this);
		setSearchNameController(searchBarTextView.getText().toString());
//		listContacts.setAdapter(adapter);
		}
	}

	public void selectContact(ATLContactModel contact) {
		
		////SHARON COMMENTING HARRIS ....
		
		
//		Log.i("harris", "selectContact");
//
//		String id = contact.getAddressBookId();  
//		
//		contact.setUrl(retrieveContactProperty(id,Website.URL,Website.CONTENT_ITEM_TYPE));
//		
//		contact.setNotes(retrieveContactProperty(id,Note.NOTE,Note.CONTENT_ITEM_TYPE));
//		
//		Log.i("harris", "****getting phones ");
//		ArrayList<HashMap<String,String>> phoneA = retrieveContactPropertyA(id,ContactsContract.CommonDataKinds.Phone.CONTENT_URI,Phone.DATA,Phone.TYPE);
//		for(HashMap<String,String> h : phoneA){
//			if (Integer.parseInt(Utilities.replaceNull(h.get("type"), "0")) == PHONE_TYPE_MOBILE){
//				contact.setPhoneNumber(h.get("property"));
//			} else if (Integer.parseInt(Utilities.replaceNull(h.get("type"), "0")) == PHONE_TYPE_WORK){
//				contact.setPhoneNumberWork(h.get("property"));
//			}
//			//Log.i("harris", "****//phone=" + h.get("property"));
//			//Log.i("harris", "****type=" + h.get("type"));
//		}
//
//		Log.i("harris", "****getting emails");
//		ArrayList<HashMap<String,String>> emailA = retrieveContactPropertyA(id,ContactsContract.CommonDataKinds.Email.CONTENT_URI,Email.DATA,Email.TYPE);
//		for(HashMap<String,String> h : emailA){
//			if (Integer.parseInt(Utilities.replaceNull(h.get("type"), "0")) == EMAIL_TYPE_HOME){
//				contact.setEmailAddress(h.get("property"));
//			} else if (Integer.parseInt(Utilities.replaceNull(h.get("type"), "0")) == EMAIL_TYPE_WORK){
//				contact.setEmailAddressWork(h.get("property"));
//			}
//			//Log.i("harris", "****//email=" + h.get("property"));
//			//Log.i("harris", "****type=" + h.get("type"));
//		}
//		
//		Log.i("harris", "****getting addresses");
//		ArrayList<HashMap<String,String>> addressA = retrieveContactPropertyA(id,ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,StructuredPostal.DATA,StructuredPostal.TYPE);
//		for(HashMap<String,String> h : addressA){
//			//Log.i("harris", "****//address=" + h.get("property"));
//			//Log.i("harris", "****type=" + h.get("type"));
//			if (Integer.parseInt(Utilities.replaceNull(h.get("type"), "0")) == ADDRESS_TYPE_HOME){
//				contact.setAddressStreet(h.get("property"));
//				//TODO - how to parse out street,city,state,zip
//			} 
//		}
		////END SECTION SHARON COMMENTING	
		
		  
		if (contact!=null){
		if (mode == MODE_PICKER || (from != null && (from.equals("event")||from.equals("task")))){
			Intent intent ;
			
			
			if (from != null &&  from.equals("event"))
				intent = new Intent(this, CalendarEditView.class);
			
			else
				intent = new Intent(this, EditTask.class);

			
			intent.putExtra("com.atlasapp.model.ATLContactModel", contact);
			
			setResult(RESULT_FROM_CONTACTLIST, intent);// 2013-01-29 TAN: SET RESULT CODE
//			startActivity(intent); // 2013-01-29 TAN: COMMENT OUT
			overridePendingTransition(R.anim.stand_by, R.anim.out_to_bottom);
			finish();
		} else {
			
			Intent intent = new Intent(this, ATLContactActivity.class);
			intent.putExtra("com.atlasapp.model.ATLContactModel", contact);
			startActivity(intent);
			overridePendingTransition(0, R.anim.in_from_bottom);
		}
		}  
		// SAMPLE CODE FOR CALLING ACTIVITY
		// ** define this constant
		// private final static int CHOOSE_CONTACT = 1;
		//
		// ** call this activity like this
		// Intent intent = new Intent(this, ATLContactListActivity.class);
		// startActivityForResult(intent, CHOOSE_CONTACT);
		//
		// **use this method to retrieve the selected contact
		// protected void onActivityResult(int requestCode, int resultCode,
		// Intent data){
		// switch (requestCode) {
		// case CHOOSE_CONTACT:
		//
		// ATLContactModel contact =
		// (ATLContactModel)intent..getExtras().getParcelable("com.atlasapp.model.ATLContactModel");
		//
		// default:
		// break;
		// }
		// }
	}

//	private ArrayList<ATLContactModel> contactListFromAddressBookOLD() {
//
//		retrieveContacts();
//
//		Uri uri = ContactsContract.Contacts.CONTENT_URI;
//		String[] projection = new String[] { ContactsContract.Data._ID,
//				ContactsContract.Contacts.DISPLAY_NAME
//		// StructuredName.GIVEN_NAME,
//		// StructuredName.FAMILY_NAME,
//		// Organization.COMPANY,
//		// GroupMembership.GROUP_SOURCE_ID,
//		// Phone.NUMBER,
//		// Email.ADDRESS,
//		// Website.URL,
//		// StructuredPostal.CITY,
//		// Note.NOTE,
//		// Photo.PHOTO
//		};
//		String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '"
//				+ ("1") + "'";
//		String[] selectionArgs = null;
//		String sortOrder = ContactsContract.Contacts.DISPLAY_NAME
//				+ " COLLATE LOCALIZED ASC";
//		Cursor cursor = getContentResolver().query(uri, projection, selection,
//				selectionArgs, sortOrder);
//
//		ArrayList<ATLContactModel> contactA = new ArrayList<ATLContactModel>();
//		while (cursor.moveToNext()) {
//			ATLContactModel contact = new ATLContactModel();
//			// Log.i("harris",cursor.getString(cursor.getColumnIndex(StructuredName.FAMILY_NAME)));
//			// contact.setFirstname(cursor.getString(cursor.getColumnIndex(StructuredName.GIVEN_NAME)));
//			contact.setFirstname(cursor.getString(cursor
//					.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
//			contactA.add(contact);
//		}
//		cursor.close();
//		return contactA;
//
//	}

	private void setListener() {
		// TODO Auto-generated method stub
		sort_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//==========================================================
				// 2013-02-23 TAN: new slide out # START
				//==========================================================
				if(!isFragmentShowing){
					alertHolderView.setVisibility(View.GONE);
					slideMenuHolderView.setVisibility(View.VISIBLE);
					contactViewCover.setVisibility(View.VISIBLE);
					contactHolderView.setX(contactHolderView.LEFTRIGHT_WINDOW_WIDTH);
					ATLDragAndDropView.topMenuBarHeight = contactHolderView.getHeight();
					ATLDragAndDropView.isAtRight = true;
					ATLDragAndDropView.isAtLeft = false;
					contactHolderView.startAnimation(ATLAnimationUtils.mainFragmentMoveRight(contactHolderView.LEFTRIGHT_WINDOW_WIDTH));
					isFragmentShowing = true;
				}
				else{
					contactViewCover.setVisibility(View.GONE);
					contactHolderView.setX(0);
					ATLDragAndDropView.topMenuBarHeight = topMenuBarHeight;
					ATLDragAndDropView.isAtRight = false;
					ATLDragAndDropView.isAtLeft = false;
					contactHolderView.startAnimation(ATLAnimationUtils.mainFragmentBackFromRight(contactHolderView.LEFTRIGHT_WINDOW_WIDTH));
					isFragmentShowing = false;
					onResume();
				}
				//==========================================================
				// 2013-02-23 TAN: new slide out # END
				//==========================================================
			}
		});
		
		alert_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//==========================================================
				// 2013-02-23 TAN: new slide out # START
				//==========================================================
				if(!isFragmentShowing){
					alertHolderView.setVisibility(View.VISIBLE);
					slideMenuHolderView.setVisibility(View.GONE);
					contactViewCover.setVisibility(View.VISIBLE);
					ATLDragAndDropView.topMenuBarHeight = contactHolderView.getHeight();
					contactHolderView.setX(-contactHolderView.LEFTRIGHT_WINDOW_WIDTH);
					contactHolderView.startAnimation(ATLAnimationUtils.mainFragmentMoveLeft(contactHolderView.LEFTRIGHT_WINDOW_WIDTH));
					alertFragment.onResume();// Reload view data
					isFragmentShowing = true;
					ATLDragAndDropView.isAtRight = false;
					ATLDragAndDropView.isAtLeft = true;
				}
				else{
					ATLDragAndDropView.topMenuBarHeight = topMenuBarHeight;
					contactViewCover.setVisibility(View.GONE);
					contactHolderView.setX(0);
					contactHolderView.startAnimation(ATLAnimationUtils.mainFragmentBackFromLeft());
					isFragmentShowing = false;
					ATLDragAndDropView.isAtRight = false;
					ATLDragAndDropView.isAtLeft = false;
				}
				//==========================================================
				// 2013-02-23 TAN: new slide out # END
				//==========================================================
			}
		});
	}

	private void setMainMenuListener() {

		((RadioGroup) findViewById(R.id.main_btns_group))
				.setVisibility(View.VISIBLE);

		RadioGroup mainBtns = (RadioGroup) findViewById(R.id.main_btns_group);

		// holder.rdgCategory = (RadioGroup)row.findViewById(R.id.radiogroup);

		RadioGroup.OnCheckedChangeListener rdGrpCheckedListener = new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				// ViewFlipper vf = (ViewFlipper)findViewById(R.id.content_vf);
				// ImageView mainTitle =
				// (ImageView)findViewById(R.id.mainTitleLabel);
				// mainTitle.setVisibility(0);

				// TODO Auto-generated method stub
				// setCategoryinList(position, checkedId);
				switch (checkedId) {
				case R.id.btnCal:
					Intent intent = new Intent(getBaseContext(),
							CalendarDayView.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
					// intent.putExtra("EXTRA_SESSION_ID", sessionId);
					startActivity(intent);

					break;
				case R.id.btnNotes:
					Intent intentNotes = new Intent(getBaseContext(),
							ATLNoteListActivity.class);
					intentNotes.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
					// //intent.putExtra("EXTRA_SESSION_ID", sessionId);
					startActivity(intentNotes);

					break;
				case R.id.btnSettings:
					Intent intentSettings = new Intent(getBaseContext(),
							Settings.class);
					intentSettings.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
					// //intent.putExtra("EXTRA_SESSION_ID", sessionId);
					startActivity(intentSettings);

					break;
				case R.id.btnToday:
					Intent intentToday = new Intent(getBaseContext(),
							ATLContactListActivity.class);
					// intentToday.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
					// //intent.putExtra("EXTRA_SESSION_ID", sessionId);
					startActivity(intentToday);

					break;
				case R.id.btnToDo:
					Intent intentTasks = new Intent(getBaseContext(),
							Tasks.class);
					intentTasks.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
					// //intent.putExtra("EXTRA_SESSION_ID", sessionId);
					startActivity(intentTasks);

					break;
				default:

					break;
				}
			}
		};

		mainBtns.setOnCheckedChangeListener(rdGrpCheckedListener);

	}

	private ArrayList<ATLContactModel> contactListFromAddressBook() {
		
		ArrayList<ATLContactModel> contactA = new ArrayList<ATLContactModel>();
		
		ContentResolver cr = this.getApplicationContext().getContentResolver();
		Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
				null, null, null);
		int testCtr = 0;
		String id = "";
		if (cursor.getCount() > 0) {

			while (cursor.moveToNext()) {
				id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

				ATLContactModel contact = new ATLContactModel();
				
				/////// Retreive Phone Numbers
				ArrayList<String> phones = new ArrayList<String>();
				
				
    //            Cursor phoneCur = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,null, null);

//				Cursor phoneCur = cr.query( 
//		          		ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
//		          		null,
//		          		ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", 
//		          		new String[]{id}, null); 
                        int index = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER); 
                        int size = cursor.getColumnCount();
//                        if (index!=-1 && cursor.getColumnCount()>=index){
//                        	
						String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

						if ( hasPhone.equalsIgnoreCase("1"))
								hasPhone = "true";
						else
								hasPhone = "false" ;

						if (Boolean.parseBoolean(hasPhone)) 
						{
							Cursor phonesCur = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,null, null);
							while (phonesCur.moveToNext()) 
							{
								phones.add(phonesCur.getString(phonesCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
							}
							phonesCur.close();
						}
				
                     //   }
				////////////
				
				
				
				

				//TODO - get phone types, email types, group names
				// - also, address is not broken into city, state etc. how handle that?
				// - also, this is too slow right now.  was much quicker when I used contactListFromAddressBookOLD
				
				contact.setAddressBookId(id);
				
				contact.setFirstname(retrieveContactProperty(id,StructuredName.GIVEN_NAME,StructuredName.CONTENT_ITEM_TYPE));
				
				contact.setLastname(retrieveContactProperty(id,StructuredName.FAMILY_NAME,StructuredName.CONTENT_ITEM_TYPE));
					
				contact.setFromAddressBook(true);
				
				
			//	ArrayList<String> phones = getContactPhoneLNumberListFromAddressBook(id);
				String phoneNumber = (phones.size()!=0 && phones.get(0).length()>=10)? phones.get(0):"";  
				phoneNumber = (phoneNumber!=null)?phoneNumber:"";
				contact.setPhoneNumber(phoneNumber);
				
				
//				ArrayList<String> emailAddresses = getContactEmailsFromAddressBook(id);
//				String emailAddress = (emailAddresses.size()!=0)?emailAddresses.get(0):"";
//				emailAddress = (emailAddress!=null)?emailAddress:"";
//				contact.setEmailAddress(emailAddress);
//				
				
				
				contact.setCompany(retrieveContactProperty(id,Organization.COMPANY,Organization.CONTENT_ITEM_TYPE));

				contact.setGroup(retrieveContactProperty(id,GroupMembership.GROUP_SOURCE_ID,GroupMembership.CONTENT_ITEM_TYPE));
							
 				contact.setImage(retrieveContactPhoto(id));
 				
 				
 				if (contact.getEmailAddress()!=null && contact.getFirstname()!=null && !contact.getFirstname().equals("") && 
 						(!contact.getEmailAddress().equals("") || !contact.getPhoneNumber().equals(""))
 						)
 				  
 				
 					contactA.add(contact);
			}
		}
		cursor.close();

		return contactA;

	}

	private String retrieveContactProperty(String contactId, String columnName,
			String mimetype) {
		String property = "";
		Cursor cursor = getContentResolver().query(
				ContactsContract.Data.CONTENT_URI,
				new String[] { columnName },
				ContactsContract.Data.CONTACT_ID + " = " + contactId
						+ " AND ContactsContract.Data.MIMETYPE = '"
						+ mimetype + "'", null, null);
		try {
			cursor.moveToNext();
			property = cursor.getString(cursor.getColumnIndexOrThrow(columnName));
		} catch (Exception e) {
			//Log.i("harris", "****property error=" + e.getLocalizedMessage());
		} 
			cursor.close();
		
		return property;
	}
	
	
	
	private ArrayList<String> getContactEmailsFromAddressBook(String contactId)
	{
		ContentResolver cr = getContentResolver();
		 ArrayList<String> emails = new ArrayList<String>();
      //   String emailType = "";
          	Cursor emailCur = cr.query( 
          		ContactsContract.CommonDataKinds.Email.CONTENT_URI, 
          		null,
          		ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", 
          		new String[]{contactId}, null); 
          	while (emailCur.moveToNext() )
          	{ 
          	    // This would allow you get several email addresses
                      // if the email addresses were stored in an array
          		emails.add(emailCur.getString(
                                emailCur.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.DATA)));
//           	     emailType = emailCur.getString(
//                                emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE)); 
//           	     
           	 //   contact[0] = id;
           	  //  contact = email;
           	     
           //	    currentABContactsEmailAdd.add(email);
           	    
           	    
           	} 
           	emailCur.close();
           	return emails;
      }
		
	private ArrayList<String> getContactPhoneLNumberListFromAddressBook(String contactId)
	{
		ContentResolver cr = getContentResolver();
		 ArrayList<String> phones = new ArrayList<String>();
			Cursor phoneCur = cr.query( 
	          		ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
	          		null,
	          		ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", 
	          		new String[]{contactId}, null); 
		 String hasPhone = phoneCur.getString(phoneCur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
		 phoneCur.close();
         if ( hasPhone.equalsIgnoreCase("1"))
             hasPhone = "true";
         else
             hasPhone = "false" ;

         if (Boolean.parseBoolean(hasPhone)) 
         {
          Cursor phonesCur = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId,null, null);
          while (phonesCur.moveToNext()) 
          {
        	  phones.add(phonesCur.getString(phonesCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
          }
          phonesCur.close();
         }
		 
		 return phones;
		 
		 
		 
	}
	
	
	private ArrayList<HashMap<String,String>> retrieveContactPropertyA(String contactId, Uri contentUri, String columnName, String typeColumnName) {
		ArrayList<HashMap<String,String>> propertyA = new ArrayList<HashMap<String,String>>();
		HashMap<String,String> h;
		Cursor cursor = getContentResolver().query(
				contentUri,
				null,
				ContactsContract.Data.CONTACT_ID + " = " + contactId, 
				null, null);
            while (cursor.moveToNext()) { 
            	try{
            		h = new HashMap<String,String>(2);
            		h.put("property", cursor.getString(cursor.getColumnIndexOrThrow(columnName)));            	
            		h.put("type", cursor.getString(cursor.getColumnIndexOrThrow(typeColumnName)));
            		propertyA.add(h);
            	}catch (Exception e){
            		
            	}
            	
            }

			cursor.close();

		return propertyA;
	}
	
	private Bitmap retrieveContactPhoto(String contactID) {

		Bitmap photo = null;

		try {
			InputStream inputStream = ContactsContract.Contacts
					.openContactPhotoInputStream(getContentResolver(),
							ContentUris.withAppendedId(
									ContactsContract.Contacts.CONTENT_URI,
									Long.valueOf(contactID)));

			if (inputStream != null) {
				photo = BitmapFactory.decodeStream(inputStream);
			}

			assert inputStream != null;
			if (inputStream != null)
				inputStream.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return photo;

	}
	// ===============================================================================
	// 2013-02-03 TAN: Implement Swipe left and right # START
	// ===============================================================================
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			contactViewCover.setVisibility(View.GONE);
			contactHolderView.setX(0);
			contactHolderView.setVisibility(View.VISIBLE);
			ATLDragAndDropView.topMenuBarHeight = topMenuBarHeight;
			
			if(ATLDragAndDropView.isAtRight){
				contactHolderView.startAnimation(ATLAnimationUtils.mainFragmentBackFromRight(contactHolderView.LEFTRIGHT_WINDOW_WIDTH));
				ATLDragAndDropView.isAtRight = false;
				ATLDragAndDropView.isAtLeft = false;
				isFragmentShowing = false;
				onResume();
				return true;
			}
			else if(ATLDragAndDropView.isAtLeft){
				contactHolderView.startAnimation(ATLAnimationUtils.mainFragmentBackFromLeft());
				ATLDragAndDropView.isAtRight = false;
				ATLDragAndDropView.isAtLeft = false;
				isFragmentShowing = false;
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
		
	}

	private DragListener mDragListener =
		new DragListener() {
			public void onDrag(int x, int y, ListView listView) {
				// TODO Auto-generated method stub
				
				if(x <= 0){
					//isSwipeLeft = true;
					alertHolderView.setVisibility(View.VISIBLE);
					slideMenuHolderView.setVisibility(View.GONE);
				}
				else{
					//isSwipeLeft = false;
					alertHolderView.setVisibility(View.GONE);
					slideMenuHolderView.setVisibility(View.VISIBLE);
				}
				
			}
	
			public void onStartDrag(View itemView) {
				contactHolderView.setVisibility(View.GONE);
			}
	
			public void onStopDrag(View itemView) {
				contactHolderView.setVisibility(View.VISIBLE);
				float x = itemView.getX();
				float y = itemView.getY();
				Log.v("CalendarDayView", "onStopDrag  =====   "+ x +" ===  "+y);
			}
			
	};	

	private DropListener mDropListener = 
		new DropListener() {
        

		public void onDrop(int from, int to) {
        	if(contactHolderView.getVisibility() != View.VISIBLE){
        		contactHolderView.setVisibility(View.VISIBLE);
        	}
        	
        	if(ATLDragAndDropView.isTouched){
        		ATLDragAndDropView.isTouched = false;
        		contactViewCover.setVisibility(View.GONE);
    			contactHolderView.setX(0);
    			contactHolderView.setVisibility(View.VISIBLE);
    			ATLDragAndDropView.topMenuBarHeight = topMenuBarHeight;
    			
    			if(ATLDragAndDropView.isAtRight){
    				contactHolderView.startAnimation(ATLAnimationUtils.mainFragmentBackFromRight(contactHolderView.LEFTRIGHT_WINDOW_WIDTH));
    				ATLDragAndDropView.isAtRight = false;
    				ATLDragAndDropView.isAtLeft = false;
    				isFragmentShowing = false;
    				onResume();
    			}
    			else if(ATLDragAndDropView.isAtLeft){
    				contactHolderView.startAnimation(ATLAnimationUtils.mainFragmentBackFromLeft());
    				ATLDragAndDropView.isAtRight = false;
    				ATLDragAndDropView.isAtLeft = false;
    				isFragmentShowing = false;
					onResume();
    			}
        	}
        	else if(ATLDragAndDropView.isAtLeft){
        		if(from > to){
        		
        		}
        		else if(to > from && to > 300){
        			ATLDragAndDropView.topMenuBarHeight = topMenuBarHeight;
					contactViewCover.setVisibility(View.GONE);
					contactHolderView.setX(0);
					contactHolderView.startAnimation(ATLAnimationUtils.mainFragmentBackFromLeftAt(to, contactHolderView.LEFTRIGHT_WINDOW_WIDTH));
					isFragmentShowing = false;
					ATLDragAndDropView.isAtRight = false;
					ATLDragAndDropView.isAtLeft = false;
					onResume();
    	        }
			}else if(ATLDragAndDropView.isAtRight){
				if(from < to){
	        		
        		}
        		else if(from > to && to < 450){
        			contactViewCover.setVisibility(View.GONE);
					contactHolderView.setX(0);
					ATLDragAndDropView.topMenuBarHeight = topMenuBarHeight;
					ATLDragAndDropView.isAtRight = false;
					ATLDragAndDropView.isAtLeft = false;
					contactHolderView.startAnimation(ATLAnimationUtils.mainFragmentBackFromRightAt(to 
							- (from - contactHolderView.LEFTRIGHT_WINDOW_WIDTH)));
					isFragmentShowing = false;
					onResume();
    	        }
			}else{
				if(from > to && to < 360){
	        		
	        		alertHolderView.setVisibility(View.VISIBLE);
					slideMenuHolderView.setVisibility(View.GONE);
					contactViewCover.setVisibility(View.VISIBLE);
					contactHolderView.setX(-contactHolderView.LEFTRIGHT_WINDOW_WIDTH);
					contactHolderView.startAnimation(ATLAnimationUtils.mainFragmentMoveLeftAt(from - to, contactHolderView.LEFTRIGHT_WINDOW_WIDTH));
					ATLDragAndDropView.topMenuBarHeight = contactHolderView.getHeight();
					alertFragment.onResume();// Reload view data
					isFragmentShowing = true;
					ATLDragAndDropView.isAtRight = false;
					ATLDragAndDropView.isAtLeft = true;
					//onResume();
					
	        	}
	        	else if(to > from && to > 360){
	        		
	        		alertHolderView.setVisibility(View.GONE);
					slideMenuHolderView.setVisibility(View.VISIBLE);
					contactViewCover.setVisibility(View.VISIBLE);
					contactHolderView.setX(contactHolderView.LEFTRIGHT_WINDOW_WIDTH);
					contactHolderView.startAnimation(ATLAnimationUtils.mainFragmentMoveRightAt(to - from, contactHolderView.LEFTRIGHT_WINDOW_WIDTH));
					ATLDragAndDropView.topMenuBarHeight = contactHolderView.getHeight();
					isFragmentShowing = true;
					ATLDragAndDropView.isAtRight = true;
					ATLDragAndDropView.isAtLeft = false;
					//onResume();
	        	}
			}
        	
        }
	};
	// ===============================================================================
	// 2013-02-03 TAN: Implement Swipe left and right # END
	// ===============================================================================	
	long startCount = 0;
	long endCount = 0;
	public TextWatcher mTextWatcher = new TextWatcher() {

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		public void afterTextChanged(final Editable s) {
			if(s.length() == 0){
				if(contactA != null && contactA.size()>0){
					adapter = new ATLContactListAdapter(contactA, mLayout.getContext());
					listContacts.setAdapter(adapter);
				}
			}
			else if (s != null && s.length() > 0) {
				// TAN: Delay 500 ms then searching
				startCount = System.currentTimeMillis();
				mLayout.postDelayed(new Runnable() {

					public void run() {
						// TODO Auto-generated method stub
						endCount = System.currentTimeMillis();
						if (endCount - startCount >= 500) {
							// Toast.makeText(view.getContext(), "Count :" +
							// count++,
							// Toast.LENGTH_SHORT).show();
							if (s != null && s.length() > 0) {
								setSearchNameController(s.toString());
							}
						}

					}
				}, 500);
				
			}
		}
	};
	public void setSearchNameController(String searchStr) {
		String searchString = searchStr.toUpperCase();
		ArrayList<ATLContactModel> contactTempArr = new ArrayList<ATLContactModel>(); 
		ArrayList<ATLContactModel> contactRemainingArr = new ArrayList<ATLContactModel>(); 
		ArrayList<ATLContactModel> contactRemainingLastNameArr = new ArrayList<ATLContactModel>(); 
		//Search by First name
		if(contactA != null && contactA.size()>0){
			for(ATLContactModel aContact : contactA){
				if(aContact.getFirstname() != null){
					Log.i("CONTACT", aContact.getFirstname().toUpperCase());
					if(aContact.getFirstname().toUpperCase().indexOf(searchString) != -1){
						contactTempArr.add(aContact);
					}
					else{
						contactRemainingArr.add(aContact);
					}
				}
			}
			
			//Search by Last name
			for(ATLContactModel aContact : contactRemainingArr){
				if(aContact.getLastname() != null){
					Log.i("CONTACT", aContact.getLastname().toUpperCase());
					if(aContact.getLastname().toUpperCase().indexOf(searchString) != -1){
						contactTempArr.add(aContact);
					}
					else{
						contactRemainingLastNameArr.add(aContact);
					}
				}
			}
			
			//Search by Email
			for(ATLContactModel aContact : contactRemainingLastNameArr){
	//			firstName, lastName, email
				if(aContact.getEmailAddress() != null || aContact.getEmailAddressWork() != null){
					if((aContact.getEmailAddress() != null 
							&& aContact.getEmailAddress().toUpperCase().indexOf(searchString) != -1) 
							|| (aContact.getEmailAddressWork() != null 
									&&	aContact.getEmailAddressWork().toUpperCase().indexOf(searchString) != -1)){
						contactTempArr.add(aContact);
					}
				}
			}
			if(contactTempArr != null){
				adapter = new ATLContactListAdapter(contactTempArr, mLayout.getContext());
			}
	//		else if(searchStr == null || searchStr.equals("")){
	//			adapter = new ATLContactListAdapter(contactA, mLayout.getContext());
	//		}
	//		else{
	//			adapter = new ATLContactListAdapter(contactA, mLayout.getContext());
	//		}
			
			listContacts.setAdapter(adapter);
		}
	}
}