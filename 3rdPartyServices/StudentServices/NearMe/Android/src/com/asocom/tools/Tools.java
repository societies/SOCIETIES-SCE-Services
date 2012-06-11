package com.asocom.tools;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import com.asocom.activities.R;
import com.asocom.model.Manager;

public class Tools {

	/**
	 * 
	 */
	public static int userImage(int nbInage) {

		switch (nbInage) {
		case 0:
			return R.drawable.user_01;
		case 1:
			return R.drawable.user_02;
		case 2:
			return R.drawable.user_03;
		case 3:
			return R.drawable.user_04;
		case 4:
			return R.drawable.user_05;
		case 5:
			return R.drawable.user_06;
		case 6:
			return R.drawable.user_07;
		case 7:
			return R.drawable.user_08;
		case 8:
			return R.drawable.user_09;
		case 9:
			return R.drawable.user_10;
		case 10:
			return R.drawable.user_11;
		case 11:
			return R.drawable.user_12;
		case 12:
			return R.drawable.user_01;
		case 13:
			return R.drawable.grupo_01;
		case 14:
			return R.drawable.grupo_02;
		case 15:
			return R.drawable.grupo_03;
		case 16:
			return R.drawable.grupo_04;
		case 17:
			return R.drawable.grupo_05;
		case 18:
			return R.drawable.grupo_06;
		case 19:
			return R.drawable.grupo_07;
		case 20:
			return R.drawable.grupo_08;
		case 21:
			return R.drawable.all_group;

		default:
			return R.drawable.user_01;
		}

	}

	public static int statusImage(int icon) {

		switch (icon) {
		case 0:
			return R.drawable.status_01;
		case 1:
			return R.drawable.status_02;
		case 2:
			return R.drawable.status_03;
		case 3:
			return R.drawable.status_04;
		default:
			return R.drawable.status_01;
		}
	}

	public static boolean value = false;

	public static void alertDialog(Context context, String titre,
			String setMessage) {
		new AlertDialog.Builder(context).setTitle(titre).setMessage(setMessage)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						return;
					}
				}).show();
		return;
	}

	public static boolean alertDialogBoolean(Context context, String titre,
			String setMessage) {
		new AlertDialog.Builder(context)
				.setTitle(titre)
				.setMessage(setMessage)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						value = true;
					}
				})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								value = false;
							}
						}).show();
		return value;
	}

	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {

		}
		return "000.000.0.0" + Manager.getCurrentPhoneUser().get(4);
	}

}