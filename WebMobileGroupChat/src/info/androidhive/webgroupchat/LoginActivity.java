package info.androidhive.webgroupchat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import info.androidhive.webgroupchat.other.DatabaseHandler;
import info.androidhive.webgroupchat.other.UserFunctions;


public class LoginActivity extends Activity {
    
	Button btnLogin;
	TextView btnLinkToRegister;
	EditText inputEmail;
	EditText inputPassword;
	TextView loginErrorMsg;

	// JSON Response node names
	private static String KEY_SUCCESS = "success";
	private static String KEY_ERROR = "error";
	private static String KEY_ERROR_MSG = "error_msg";
	private static String KEY_UID = "uid";
	private static String KEY_NAME = "name";
	private static String KEY_PHONE = "phone"; 
	private static String KEY_EMAIL = "email";
	private static String KEY_CREATED_AT = "created_at";

	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
        if (android.os.Build.VERSION.SDK_INT > 9)

        {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        }
        
        inputEmail = (EditText) findViewById(R.id.LoginEmail);
		inputPassword = (EditText) findViewById(R.id.LoginPassword);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnLinkToRegister = (TextView) findViewById(R.id.link_to_register);
		loginErrorMsg = (TextView) findViewById(R.id.login_error);
        
        // Listening to register new account link
        
        btnLogin.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				String email = inputEmail.getText().toString();
				String password = inputPassword.getText().toString();
				UserFunctions userFunction = new UserFunctions();
				Log.d("Button", "Login");
				JSONObject json = userFunction.loginUser(email, password);

				// check for login response
				try {
					if (json.getString(KEY_SUCCESS) != null) {
						loginErrorMsg.setText("");
						String res = json.getString(KEY_SUCCESS); 
						if(Integer.parseInt(res) == 1){
							// user successfully logged in
							// Store user details in SQLite Database
							DatabaseHandler db = new DatabaseHandler(getApplicationContext());
							JSONObject json_user = json.getJSONObject("user");
							
							// Clear all previous data in database
							userFunction.logoutUser(getApplicationContext());
							db.addUser(json_user.getString(KEY_NAME), json_user.getString(KEY_PHONE), json_user.getString(KEY_EMAIL), json.getString(KEY_UID), json_user.getString(KEY_CREATED_AT));						
							
							// Launch Dashboard Screen
							Intent chatscreen = new Intent(getApplicationContext(), MainActivity.class);
							chatscreen.putExtra("name", email);
							// Close all views before launching Dashboard
							startActivity(chatscreen);
							
							// Close Login Screen
							finish();
						}else{
							// Error in login
							loginErrorMsg.setText("Incorrect username/password");
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});	
    
        
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// Switching to Register screen
				Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
				startActivity(i);
			}
		});
    }
}