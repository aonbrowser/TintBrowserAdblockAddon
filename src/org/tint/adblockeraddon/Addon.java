package org.tint.adblockeraddon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.tint.addons.BaseAddon;
import org.tint.addons.framework.AddonAction;
import org.tint.addons.framework.AddonCallbacks;
import org.tint.addons.framework.AddonResponse;

import android.app.Service;
import android.content.Intent;
import android.os.RemoteException;
import android.util.Log;

public class Addon extends BaseAddon {
	
	private String mAdSweep = null;

	public Addon(Service service) {
		super(service);
	}
	
	@Override
	public int getCallbacks() throws RemoteException {
		return AddonCallbacks.PAGE_FINISHED | AddonCallbacks.HAS_PREFERENCES_PAGE;
		//return AddonCallbacks.PAGE_FINISHED | AddonCallbacks.HAS_PREFERENCES_PAGE | AddonCallbacks.CONTRIBUTE_MAIN_MENU | AddonCallbacks.CONTRIBUTE_LINK_CONTEXT_MENU;
	}

	@Override
	public void onBind() throws RemoteException {
		if (mAdSweep == null) {
			InputStream is = mService.getResources().openRawResource(R.raw.adsweep);
			if (is != null) {
				StringBuilder sb = new StringBuilder();
				String line;

				try {
					BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
					while ((line = reader.readLine()) != null) {
						if ((line.length() > 0) &&
								(!line.startsWith("//"))) {
							sb.append(line).append("\n");
						}
					}
				} catch (IOException e) {
					Log.w(mService.getString(R.string.AddonName), "Unable to load AdSweep: " + e.getMessage());
				} finally {
					try {
						is.close();
					} catch (IOException e) {
						Log.w(mService.getString(R.string.AddonName), "Unable to load AdSweep: " + e.getMessage());
					}
				}
				mAdSweep = sb.toString();
			} else {        
				mAdSweep = null;
			}
		}
	}

	@Override
	public void onUnbind() throws RemoteException {	}

	@Override
	public AddonResponse onPageStarted(String url) throws RemoteException {
		return null;
	}
	
	@Override
	public AddonResponse onPageFinished(String url) throws RemoteException {
		if ((url != null) &&
				(!isUrlInAdblockerWhiteList(url))) {
			if (mAdSweep != null) {
				AddonResponse response = new AddonResponse();
				response.addAction(new AddonAction(AddonAction.ACTION_LOAD_URL, mAdSweep));

				return response;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
	
	@Override
	public String getContributedMainMenuItem() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
//		return "AdblockerMainMenuItem";
	}

	@Override
	public AddonResponse onContributedMainMenuItemSelected(String currentTitle, String currentUrl) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
//		AddonResponse response = new AddonResponse();
//		response.addAction(new AddonAction(AddonAction.ACTION_SHOW_TOAST, "Adblocker: " + currentUrl));
//		
//		return response;
	}

	@Override
	public String getContributedLinkContextMenuItem() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
//		return "AdblockerLinkMenuItem";
	}

	@Override
	public AddonResponse onContributedLinkContextMenuItemSelected(int hitTestResult, String url) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
		
//		AddonResponse response = new AddonResponse();
//		response.addAction(new AddonAction(AddonAction.ACTION_SHOW_TOAST, "Adblocker: " + Integer.toString(hitTestResult)));
//		
//		return response;
	}
	
	@Override
	public String getContributedHistoryBookmarksMenuItem() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AddonResponse onContributedHistoryBookmarksMenuItemSelected() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getContributedBookmarkContextMenuItem() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AddonResponse onContributedBookmarkContextMenuItemSelected(String title, String url) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getContributedHistoryContextMenuItem() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AddonResponse onContributedHistoryContextMenuItemSelected(String title, String url) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public AddonResponse onUserAnswerQuestion(String questionId, boolean positiveAnswer) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void showAddonPreferenceActivity() throws RemoteException {
		Intent i = new Intent(mService, Preferences.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		mService.startActivity(i);
	}	
	
	private boolean isUrlInAdblockerWhiteList(String url) {
		List<String> whiteList = Controller.getInstance().getAdblockerWhiteList(mService);
		
		for (String s : whiteList) {
			if (url.contains(s)) {
				return true;
			}
		}
		
		return false;
	}

}
