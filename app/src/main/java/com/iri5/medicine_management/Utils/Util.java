package com.iri5.medicine_management.Utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Util {
    private static final double EARTH_RADIUS_KM = 6371.0;

    public static void showToast(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    public static void printSHA1Key(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(),
                    PackageManager.GET_SIGNING_CERTIFICATES
            );

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                for (Signature signature : info.signingInfo.getApkContentsSigners()) {
                    MessageDigest md = MessageDigest.getInstance("SHA-1");
                    md.update(signature.toByteArray());
                    String sha1Key = byteArrayToHex(md.digest());
                    Log.d("SHA1 Key", sha1Key);
                }
            } else {
                for (Signature signature : info.signatures) {
                    MessageDigest md = MessageDigest.getInstance("SHA-1");
                    md.update(signature.toByteArray());
                    String sha1Key = byteArrayToHex(md.digest());
                    Log.d("SHA1 Key", sha1Key);
                }
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            Log.e("SHA1 Key", "Error while getting SHA1 key", e);
        }
    }

    private static String byteArrayToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(String.format("%02X", bytes[i]));
            if (i < bytes.length - 1) {
                sb.append(":");
            }
        }
        return sb.toString();
    }
}
