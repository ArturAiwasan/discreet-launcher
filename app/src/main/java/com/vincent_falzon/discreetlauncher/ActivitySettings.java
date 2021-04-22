package com.vincent_falzon.discreetlauncher ;

// License
/*

	This file is part of Discreet Launcher.

	Copyright (C) 2019-2021 Vincent Falzon

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see <https://www.gnu.org/licenses/>.

 */

// Imports
import android.content.Intent ;
import android.content.SharedPreferences ;
import android.content.pm.ActivityInfo ;
import android.content.pm.PackageManager ;
import android.content.pm.ResolveInfo ;
import android.os.Build ;
import android.os.Bundle ;
import android.view.MenuItem ;
import androidx.appcompat.app.AppCompatActivity ;
import androidx.preference.ListPreference ;
import androidx.preference.MultiSelectListPreference ;
import androidx.preference.PreferenceFragmentCompat ;
import androidx.preference.PreferenceManager ;
import com.vincent_falzon.discreetlauncher.core.Application ;
import java.util.ArrayList ;
import java.util.List ;

/**
 * Settings and Help activity.
 */
public class ActivitySettings extends AppCompatActivity
{
	// Attributes
	private static ArrayList<String> iconPacks ;
	private static ArrayList<String> packsNames ;
	private static ArrayList<String> applicationsNames ;
	private static ArrayList<String> applicationsDisplayNames ;
	private static ArrayList<String> hiddenApplicationsNames ;
	private static ArrayList<String> hiddenApplicationsDisplayNames ;


	/**
	 * Constructor.
	 * @param savedInstanceState To retrieve the context
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// Call the constructor of the parent class
		super.onCreate(savedInstanceState) ;

		// Initializations
		if(iconPacks == null) iconPacks = new ArrayList<>() ;
		if(packsNames == null) packsNames = new ArrayList<>() ;
		if(applicationsNames == null) applicationsNames = new ArrayList<>() ;
		if(applicationsDisplayNames == null) applicationsDisplayNames = new ArrayList<>() ;
		if(hiddenApplicationsNames == null) hiddenApplicationsNames = new ArrayList<>() ;
		if(hiddenApplicationsDisplayNames == null) hiddenApplicationsDisplayNames = new ArrayList<>() ;

		// Prepare the icon pack setting
		iconPacks.clear() ;
		packsNames.clear() ;
		iconPacks.add(Constants.NONE) ;
		packsNames.add(getString(R.string.set_icon_pack_none)) ;
		searchIconPacks() ;

		// Prepare the applications lists
		applicationsNames.clear() ;
		applicationsDisplayNames.clear() ;
		hiddenApplicationsNames.clear() ;
		hiddenApplicationsDisplayNames.clear() ;
		searchApplications() ;
		searchHiddenApplications() ;

		// Load the general settings layout
		setContentView(R.layout.activity_settings) ;

		// Display the settings
		getSupportFragmentManager().beginTransaction().replace(R.id.settings_container, new SettingsFragment()).commit() ;
	}


	/**
	 * Modify the arrow from action bar to allow navigation between fragments.
	 * @param item Selected element
	 * @return <code>true</code> if the event has been consumed, <code>false</code> otherwise
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Make the arrow from the action bar do the same action than the Back button
		if(item.getItemId() == android.R.id.home)
			{
				onBackPressed() ;
				return true ;
			}
		return super.onOptionsItemSelected(item) ;
	}


	/**
	 * Perfom actions when returning to the home screen.
	 */
	@Override
	protected void onDestroy()
	{
		// Fix an Android Oreo 8.1 bug (orientation is sometimes kept from an activity to another)
		if(Build.VERSION.SDK_INT == Build.VERSION_CODES.O_MR1)
			{
				SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this) ;
				if(settings.getBoolean(Constants.FORCE_PORTRAIT, false))
						setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) ;
					else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) ;
			}

		super.onDestroy() ;
	}


	/**
	 * Load the general settings from the XML file and prepare their values.
	 */
	public static class SettingsFragment extends PreferenceFragmentCompat
	{
		/**
		 * Constructor.
		 * @param savedInstanceState To retrieve the context
		 * @param rootKey Root of the settings hierarchy
		 */
		@Override
		public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
		{
			// Load the settings from the XML file
			setPreferencesFromResource(R.xml.settings, rootKey) ;

			// Initialize the setting to hide applications
			MultiSelectListPreference hiddenApplications = findPreference(Constants.HIDDEN_APPLICATIONS) ;
			if(hiddenApplications != null)
				{
					// Build the applications list
					ArrayList<String> displayNames = new ArrayList<>() ;
					ArrayList<String> names = new ArrayList<>() ;
					names.addAll(hiddenApplicationsNames) ;
					names.addAll(applicationsNames) ;
					displayNames.addAll(hiddenApplicationsDisplayNames) ;
					displayNames.addAll(applicationsDisplayNames) ;
					hiddenApplications.setEntries(displayNames.toArray(new CharSequence[0])) ;
					hiddenApplications.setEntryValues(names.toArray(new CharSequence[0])) ;
				}
		}
	}


	/**
	 * Load the display settings from the XML file.
	 */
	public static class DisplayFragment extends PreferenceFragmentCompat
	{
		/**
		 * Constructor.
		 * @param savedInstanceState To retrieve the context
		 * @param rootKey Root of the settings hierarchy
		 */
		@Override
		public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
		{
			// Load the settings from the XML file
			setPreferencesFromResource(R.xml.settings_display, rootKey) ;

			// Initialize the icon pack selector
			ListPreference iconPack = findPreference(Constants.ICON_PACK) ;
			if(iconPack != null)
				{
					iconPack.setEntries(packsNames.toArray(new CharSequence[0])) ;
					iconPack.setEntryValues(iconPacks.toArray(new CharSequence[0])) ;
				}
		}
	}


	/**
	 * Load the notification settings from the XML file and prepare their values.
	 */
	public static class NotificationFragment extends PreferenceFragmentCompat
	{
		/**
		 * Constructor.
		 * @param savedInstanceState To retrieve the context
		 * @param rootKey Root of the settings hierarchy
		 */
		@Override
		public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
		{
			// Load the settings from the XML file
			setPreferencesFromResource(R.xml.settings_notification, rootKey) ;

			// Prepare the applications list
			ArrayList<String> displayNames = new ArrayList<>() ;
			ArrayList<String> names = new ArrayList<>() ;
			names.add(Constants.NONE) ;
			names.addAll(applicationsNames) ;
			displayNames.add(getString(R.string.set_application_none)) ;
			displayNames.addAll(applicationsDisplayNames) ;

			// Initialize the notification applications selectors
			for(int i = 0 ; i < 3 ; i++)
			{
				ListPreference notification_app = findPreference(Constants.NOTIFICATION_APP + (i + 1)) ;
				if(notification_app == null) continue ;
				notification_app.setEntries(displayNames.toArray(new CharSequence[0])) ;
				notification_app.setEntryValues(names.toArray(new CharSequence[0])) ;
			}
		}
	}


	/**
	 * Load the help settings from the XML file.
	 */
	public static class HelpFragment extends PreferenceFragmentCompat
	{
		/**
		 * Constructor.
		 * @param savedInstanceState To retrieve the context
		 * @param rootKey Root of the settings hierarchy
		 */
		@Override
		public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
		{
			// Load the settings from the XML file
			setPreferencesFromResource(R.xml.settings_help, rootKey) ;
		}
	}


	/**
	 * Load the changelog settings from the XML file.
	 */
	public static class ChangelogFragment extends PreferenceFragmentCompat
	{
		/**
		 * Constructor.
		 * @param savedInstanceState To retrieve the context
		 * @param rootKey Root of the settings hierarchy
		 */
		@Override
		public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
		{
			// Load the settings from the XML file
			setPreferencesFromResource(R.xml.settings_changelog, rootKey) ;
		}
	}


	/**
	 * Build a list of the installed icon packs.
	 */
	private void searchIconPacks()
	{
		// Retrieve the list of installed icon packs
		PackageManager apkManager = getPackageManager() ;
		Intent filter = new Intent("org.adw.launcher.THEMES") ;
		List<ResolveInfo> packsList = apkManager.queryIntentActivities(filter, PackageManager.GET_META_DATA) ;

		// Browse the retrieved packs and store their information in the lists
		for(ResolveInfo pack:packsList)
		{
			iconPacks.add(pack.activityInfo.packageName) ;
			packsNames.add(pack.loadLabel(apkManager).toString()) ;
		}
	}


	/**
	 * Build a list of the installed applications.
	 */
	private void searchApplications()
	{
		// Browse the applications list and store their information in the lists
		ArrayList<Application> applicationsList = ActivityMain.getApplicationsList().getApplications(false) ;
		for(Application application : applicationsList)
		{
			applicationsNames.add(application.getDisplayName()
					+ Constants.NOTIFICATION_SEPARATOR + application.getName()
					+ Constants.NOTIFICATION_SEPARATOR + application.getApk()) ;
			applicationsDisplayNames.add(application.getDisplayName()) ;
		}
	}


	/**
	 * Build a list of the hidden applications.
	 */
	private void searchHiddenApplications()
	{
		// Browse the hidden applications list and store their information in the lists
		ArrayList<Application> hiddenApplications = ActivityMain.getApplicationsList().getHidden() ;
		for(Application application : hiddenApplications)
		{
			// Need to store everything for consistency with the applications list
			hiddenApplicationsNames.add(application.getDisplayName()
					+ Constants.NOTIFICATION_SEPARATOR + application.getName()
					+ Constants.NOTIFICATION_SEPARATOR + application.getApk()) ;
			hiddenApplicationsDisplayNames.add(application.getDisplayName()) ;
		}
	}
}
