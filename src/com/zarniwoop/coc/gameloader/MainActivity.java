package com.zarniwoop.coc.gameloader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.exceptions.RootDeniedException;
import com.stericson.RootTools.execution.CommandCapture;

public class MainActivity extends ListActivity {
	
	private CredentialDAO credDAO;
	
	private SecurePreferences storage;
	private static final String LOW = "Low_PROD2";
	private static final String HIGH = "High_PROD2";
	private static final String PASS = "Pass_PROD2";
	private static final String LEVEL = "THLevel";
	private static final String LOCALE = "LOCALE_KEY";
	
	private Context context;
	private TextView out;
	private Button newGame;
	private Button saveGame;

	
	private String thisDataDir;
	private String thisPrefsDir;
	private String thatDataDir;
	private String thatPrefsDir;
	private ApplicationInfo thisAppInfo;
	private ApplicationInfo thatAppInfo;

    ArrayList<String> accounts = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.context = getApplicationContext();
		this.credDAO = new CredentialDAO(context);
		this.out = (TextView) findViewById(R.id.tvOut);
		this.newGame = (Button) findViewById(R.id.btnNewGame);
		this.saveGame = (Button) findViewById(R.id.btnSaveGame);
		
		newGame.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				newGame();
			}
		});
		saveGame.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				saveGame();
			}
		});
		
        adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                accounts);
        setListAdapter(adapter);
		
		loadGames();

		checkRoot(context);
		
		thisAppInfo = context.getApplicationInfo();
		
		try {
			thatAppInfo = getPackageManager().getPackageInfo("com.supercell.clashofclans", 0).applicationInfo;
		} catch (NameNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.exit(255);
		}
		
		String prefsDir = "/shared_prefs";
		thisDataDir = thisAppInfo.dataDir;
		thisPrefsDir = thisDataDir.concat(prefsDir);
		thatDataDir = thatAppInfo.dataDir;
		thatPrefsDir = thatDataDir.concat(prefsDir);
		
		String[] commands = new String[] { "mkdir " + thisPrefsDir,
				"chown " + thisAppInfo.uid + " " + thisPrefsDir,
				"chgrp " + thisAppInfo.uid + " " + thisPrefsDir,
				"chmod 771 " + thisPrefsDir,
				"cp " + thatPrefsDir + "/storage.xml " + thisPrefsDir,
				"chown " + thisAppInfo.uid + " " + thisPrefsDir + "/storage.xml",
				"chgrp " + thisAppInfo.uid + " " + thisPrefsDir + "/storage.xml",
				"chmod 660 " + thisPrefsDir + "/storage.xml" };
		CommandCapture command = new CommandCapture(0, commands) {
			@Override
			public void commandCompleted(int id, int exitcode) {
				loadPrefs(context);
				newGame.setEnabled(true);
				saveGame.setEnabled(true);
			}
		};
		run(command);
	}
	
	public void sniff(SecurePreferences pref) {
		for (Map.Entry<String, String> entry : pref.getAllDecrypted().entrySet()) {
			out.append(entry.getKey());
			out.append(" = ");
			out.append(entry.getValue());
			out.append("\n");
		}
	}

	public static String getGameDataKey(Context paramContext) {
		String str = Settings.Secure.getString(
				paramContext.getContentResolver(), "android_id");
		if ((str == null) || (str == "")) {
			str = paramContext.getPackageName().toUpperCase(Locale.ENGLISH);
		}
		return str;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void loadPrefs(Context localContext) {
		String gameDataKey = getGameDataKey(this);
		this.storage = new SecurePreferences(this, "storage", gameDataKey);
	}

	public static void checkRoot(Context context) {
		// Check if root exists and if access is given
		if (!RootTools.isRootAvailable()) {
			Toast.makeText(context, "Root doesn't exist! Are you rooted?",
					Toast.LENGTH_LONG).show();
			System.exit(255);
		} else if (!RootTools.isAccessGiven()) {
			Toast.makeText(context, "Root access denied! Was root given?",
					Toast.LENGTH_LONG).show();
			System.exit(255);
		}
	}
	
	public static void run(CommandCapture command) {
		try {
			RootTools.getShell(true).add(command);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RootDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void newGame() {
		// delete storage.xml
		String thatDataDir = thatAppInfo.dataDir;
		String thatPrefsDir = thatDataDir.concat("/shared_prefs");

		String[] commands = new String[] { "rm " + thatPrefsDir + "/storage.xml" };
		CommandCapture command = new CommandCapture(0, commands) {
			@Override
			public void commandCompleted(int id, int exitcode) {
				launchGame();
			}
		};
		run(command);
	}
	
	public void loadGames() {
		for (Credential cred : credDAO.getAll()) {
			//out.append(cred.getName());
			//out.append("\n");
			addCredentialToList(cred);
		}
	}
	
	public void launchGame() {
		Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage("com.supercell.clashofclans");
		startActivity(LaunchIntent);
	}
	
	public void saveGame() {
		//out.append("*************** storage\n");
		//sniff(storage);
		//out.append("*************** localPrefs\n");
		//sniff(localPrefs);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Title");

		// Set up the input
		final EditText input = new EditText(this);
		// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
		input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
		builder.setView(input);

		// Set up the buttons
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        String name = input.getText().toString();
				String low = storage.getString(LOW);
				String high = storage.getString(HIGH);
				String pass = storage.getString(PASS);
				String thLevel = storage.getString(LEVEL);
				String locale = storage.getString(LOCALE);
				
				Credential cred = credDAO.create(name, low, high, pass, thLevel, locale);
				addCredentialToList(cred);
		    }
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        dialog.cancel();
		    }
		});

		builder.show();
	}
	
	public void addCredentialToList(Credential cred) {
        accounts.add(cred.getName());
        adapter.notifyDataSetChanged();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id){
	    super.onListItemClick(l, v, position, id);

	    String name = accounts.get(position);
	    Credential cred = credDAO.get(name);
	    
	    storage.put(HIGH, cred.getHigh());
	    storage.put(LOW, cred.getLow());
	    storage.put(PASS, cred.getPass());
	    storage.put(LEVEL, cred.getThLevel());
	    storage.put(LOCALE, cred.getLocale());

		String[] commands = new String[] { "cp -f " + thisPrefsDir + "/storage.xml " + thatPrefsDir,
				"chown " + thatAppInfo.uid + " " + thatPrefsDir + "/storage.xml",
				"chgrp " + thatAppInfo.uid + " " + thatPrefsDir + "/storage.xml",
				"chmod 660 " + thatPrefsDir + "/storage.xml" };
		CommandCapture command = new CommandCapture(0, commands) {
			@Override
			public void commandCompleted(int id, int exitcode) {
				launchGame();
			}
		};
		run(command);
	}
}
