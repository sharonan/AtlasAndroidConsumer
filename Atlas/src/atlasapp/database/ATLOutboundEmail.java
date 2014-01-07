package atlasapp.database;

import java.util.HashMap;
import java.util.List;

import atlasapp.common.ATLConstants.EmailTemplateType;
import atlasapp.database.DatabaseConstants.TABLES_NAME;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class ATLOutboundEmail extends AtlasServerTable implements ParseDBCallBackInterface{

	public ATLOutboundEmailCallBackInterface atlOutboundEmailListener;
	
	public ATLOutboundEmail(ATLOutboundEmailCallBackInterface atlOutboundEmailListener)
	{
		TABLE_NAME = TABLES_NAME.OUTBOUND_EMAIL.getTableName();
		atlasServerTable = new ParseObject(TABLE_NAME);
		userQuery = new ParseQuery(TABLE_NAME);
		parseQuery = new ParseQuery(TABLE_NAME);
		// sign Event to database call back's methods
		parseCallBackDeleget = this;
		this.atlOutboundEmailListener = atlOutboundEmailListener;
	}
	
	private EmailTemplateType emailTemplateType;
	public void saveOutboundEmailRecordOnParse(OutboundEmailProperties outboundEmailProperties,
			EmailTemplateType emailTemplateType)
	{
		if (outboundEmailProperties!=null)
		{
			this.emailTemplateType = emailTemplateType;
			HashMap<String, Object> outboundEmailColumns = new HashMap<String, Object>();

			
			outboundEmailColumns.put("body", outboundEmailProperties.body);
			outboundEmailColumns.put("from", outboundEmailProperties.from);
		
			outboundEmailColumns.put("from_name", outboundEmailProperties.fromName);
			outboundEmailColumns.put("reply_to", outboundEmailProperties.replyTo);
			outboundEmailColumns.put("subject", outboundEmailProperties.subject);
			outboundEmailColumns.put("to", outboundEmailProperties.to);
			outboundEmailColumns.put("has_error", outboundEmailProperties.hasError);
			outboundEmailColumns.put("is_pending",outboundEmailProperties.isPending);
			outboundEmailColumns.put("is_sent", outboundEmailProperties.isSent);
			/// wait for getSaveCallBack method...
				put(outboundEmailColumns);
			
			
		}
	}
	@Override
	public void getSaveCallBack(boolean saved, ParseObject parseObjectSaved)
	{
		if (saved)
		{
			
		}
		else
		{
			
		}
		if (atlOutboundEmailListener!=null)
			atlOutboundEmailListener.savedNewOutboundEmailCallBack(saved,emailTemplateType);
		
	}
	
	@Override
	public void getFindQueryCallBack(List<ParseObject> foundQueryList,
			boolean found) {
		// TODO Auto-generated method stub
		
	}

	

	@Override
	public void getObjectIdCallBack(ParseObject retreivedObjectId,
			boolean success) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getDataCallBack(byte[] fileRetreived, boolean success) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getSuccessCallBack(boolean success) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void registerSuccess(boolean signUpSuccess) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void signInSuccess(boolean success) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveFileCallBack(boolean success, ParseObject psrseObjectSaved) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getUserEmailOnParseCallBack(
			HashMap<String, String> userParseLogin, boolean success) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getFriendEmailOnParseCallBack(
			List<ParseObject> loginProperties, boolean success) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getFacebookFriendIdOnParseCallBack(
			List<ParseObject> findResult, boolean success) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getAllAtlasUsersCallBack(List<ParseObject> findResult,
			boolean success) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getAllFBAtlasUsersFriendsCallBack(List<ParseObject> findResult,
			boolean success) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void friendSignInSuccess(boolean success) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getAtlasNewFriendsByEmailCallBack(List<ParseObject> findResult,
			boolean success) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getUpateCallBack(boolean success) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void signInNewFriendUserSuccess(boolean success, ParseUser user) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void refreshUserPictureOnTheBackgroundCallBack(boolean success,
			String imageUrl) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void isEmailVerifiedCallBack(boolean verified) {
		// TODO Auto-generated method stub
		
	}

}