package com.ns.geotracker;

import android.location.Address;
import java.util.ArrayList;
import android.os.AsyncTask;
import android.content.Context;
import android.location.Location;
import android.location.Geocoder;
import java.util.List;
import java.util.Locale;
import java.io.IOException;
import android.text.TextUtils;


class ReverseGeo extends AsyncTask<Location, Void, String> {

    private Context mContext;
 private OnTaskComplete mListener;

    ReverseGeo(Context applicationContext, OnTaskComplete listener) {
        mListener = listener;
        mContext = applicationContext;
    }

    @Override
    protected void onPostExecute(String address) {
        mListener.onTaskComplete(address);
        super.onPostExecute(address);
    }

 @Override
    protected String doInBackground(Location... params) {
      Geocoder mGeocoder = new Geocoder(mContext,
                Locale.getDefault());
      Location location = params[0];
     List<Address> addresses = null;
      String printAddress = "";
     try {
            addresses = mGeocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1);
     } catch (IOException ioException) {
            printAddress = mContext.getString(R.string.no_address);
        }
      if (addresses.size() == 0) {
            if (printAddress.isEmpty()) {
               printAddress = mContext.getString(R.string.no_address);
            }
        } else {
         Address address = addresses.get(0);
            ArrayList<String> addressList = new ArrayList<>();
            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressList.add(address.getAddressLine(i));
            }
            printAddress = TextUtils.join(
                    ",",
                    addressList);

        }
        return printAddress;
    }

//Create the OnTaskComplete interface, which takes a String as an argument//

    interface OnTaskComplete {
        void onTaskComplete(String result);
    }
}
