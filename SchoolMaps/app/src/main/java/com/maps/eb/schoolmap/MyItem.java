package com.maps.eb.schoolmap;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MyItem implements ClusterItem
{
    private final LatLng mPosition;
    private final String mTitle;
    private final String mSnippet, mSnippet2, mSnippet3, mSnippet4, mSnippet5, mSnippet6;

    public MyItem(double latitude, double longitude, String nameOfSchool,
		    String typeOfSchool, String schoolContact,
		    String phoneNumber, String addressOfSchool,
		    String publicOrPrivate, String premiumStatus)
    {

	mPosition = new LatLng(latitude, longitude);
	mTitle = nameOfSchool;
	mSnippet = typeOfSchool;
	mSnippet2 = schoolContact;
	mSnippet3 = phoneNumber;
	mSnippet4 = addressOfSchool;
	mSnippet5 = publicOrPrivate;
	mSnippet6 = premiumStatus;

    }

    @Override public LatLng getPosition()
    {
	return mPosition;
    }

    public String getTitle()
    {
	return mTitle;
    }

    public String getSnippet1()
    {
	return mSnippet;
    }

    public String getSnippet2()
    {
	return mSnippet2;
    }

    public String getSnippet3()
    {
	return mSnippet3;
    }

    public String getSnippet4()
    {
	return mSnippet4;
    }

    public String getSnippet5()
    {
	return mSnippet5;
    }

    public String getSnippet6()
    {
	return mSnippet6;
    }
}