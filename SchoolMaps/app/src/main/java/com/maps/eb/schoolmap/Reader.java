package com.maps.eb.schoolmap;

import android.content.Context;
import com.opencsv.CSVReader;
import org.apache.commons.io.IOUtils;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Reader
{

    private static String[] nameOfSchool, addressOfSchool, schoolContact, typeOfSchool, publicOrPrivate, phoneNumber, premiumStatus, latitudeString, longitudeString;

    public Reader(Context context)
    {

	List<String> nameList = new ArrayList<>();
	List<String> contactList = new ArrayList<>();
	List<String> addressList = new ArrayList<>();
	List<String> publicOrPrivateList = new ArrayList<>();
	List<String> typeList = new ArrayList<>();
	List<String> phoneList = new ArrayList<>();
	List<String> latitudeList = new ArrayList<>();
	List<String> longitudeList = new ArrayList<>();
	List<String> premiumList = new ArrayList<>();
	FileInputStream fileInputStream = null;
	byte[] bytes = new byte[0];
	try
	{
	    fileInputStream = context.openFileInput("List3");
	}
	catch(FileNotFoundException e)
	{
	    e.printStackTrace();
	}
	try
	{
	    if(fileInputStream != null)
	    {
		bytes = IOUtils.toByteArray(fileInputStream);
		fileInputStream.close();
	    }
	}
	catch(IOException e)
	{
	    e.printStackTrace();
	}

	InputStream inputStream;
	BufferedReader bufferedReader;
	try
	{
	    inputStream = new ByteArrayInputStream(bytes);
	    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
	    CSVReader reader = new CSVReader(bufferedReader);
	    String[] nextLine;

	    while((nextLine = reader.readNext()) != null)
	    {
		nameList.add(nextLine[0]);
		contactList.add(nextLine[1]);
		addressList.add(nextLine[2]);
		typeList.add(nextLine[3]);
		publicOrPrivateList.add(nextLine[4]);
		phoneList.add(nextLine[5]);
		latitudeList.add(nextLine[6]);
		longitudeList.add(nextLine[7]);
		premiumList.add(nextLine[8]);
	    }
	    int length = nameList.size();
	    nameOfSchool = nameList.toArray(new String[length]);
	    addressOfSchool = addressList.toArray(new String[length]);
	    schoolContact = contactList.toArray(new String[length]);
	    typeOfSchool = typeList.toArray(new String[length]);
	    publicOrPrivate = publicOrPrivateList.toArray(new String[length]);
	    phoneNumber = phoneList.toArray(new String[length]);
	    latitudeString = latitudeList.toArray(new String[length]);
	    longitudeString = longitudeList.toArray(new String[length]);
	    premiumStatus = premiumList.toArray(new String[length]);

	}
	catch(IOException e)
	{
	    e.printStackTrace();
	}
    }

    public static String[] getPublicOrPrivate() { return publicOrPrivate; }

    public static String[] getNameOfSchool() { return nameOfSchool; }

    public static String[] getLatitudeString()
    {
	return latitudeString;
    }

    public static String[] getLongitudeString() {return longitudeString;}

    public static String[] getAddressOfSchool()
    {
	return addressOfSchool;
    }

    public static String[] getSchoolContact()
    {
	return schoolContact;
    }

    public static String[] getTypeOfSchool()
    {
	return typeOfSchool;
    }

    public static String[] getPhoneNumber()
    {
	return phoneNumber;
    }

    public static String[] getPremiumStatus()
    {
	return premiumStatus;
    }

}
