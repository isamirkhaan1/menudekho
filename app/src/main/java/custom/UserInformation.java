package custom;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

import java.io.IOException;
import java.net.NetworkInterface;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedList;

/**
 * Created by Samir KHan on 9/25/2016.
 */
public class UserInformation {
    Context mContext;

    public UserInformation(Context context) {
        this.mContext = context;
    }

    public String macAddress() {

        String macAddress = "";
        try {
            Collection<String> mac = getAllLocalMacAddresses();
            for (String temp : mac) {
                macAddress = macAddress + temp;
            }

        } catch (Exception exp) {
            macAddress = "nill";
        } finally {
            return macAddress;
        }
    }

    public static Collection<String> getAllLocalMacAddresses() throws IOException {
        final Enumeration<NetworkInterface> inetAddresses = NetworkInterface.getNetworkInterfaces();
        final Collection<String> addresses = new LinkedList<String>();

        while (inetAddresses.hasMoreElements()) {
            final byte[] macBytes = inetAddresses.nextElement().getHardwareAddress();

            if (macBytes == null)
                continue;

            addresses.add(getMacAddress(macBytes));
        }

        return addresses;
    }

    static String getMacAddress(byte[] macBytes) {
        final StringBuilder strBuilder = new StringBuilder();

        for (int i = 0; i < macBytes.length; i++) {
            strBuilder.append(String.format("%02X%s", macBytes[i],
                    (i < macBytes.length - 1) ? ":" : ""));
        }

        return strBuilder.toString().toUpperCase();
    }

    public String emailAdd() {
        AccountManager manager = AccountManager.get(mContext);
        Account[] accs = manager.getAccountsByType("com.google");
        if (accs.length < 1)
            return null;
        else
            return accs[0].name;
    }

}
