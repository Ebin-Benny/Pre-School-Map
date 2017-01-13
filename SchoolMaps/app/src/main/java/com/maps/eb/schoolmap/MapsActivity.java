package com.maps.eb.schoolmap;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.ClusterRenderer;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements
		ClusterManager.OnClusterItemInfoWindowClickListener<MyItem>
{

    private static final String URL_OF_ASSETS = "http://www.scoilscamaill.com/Assets/List.csv";
    private static final String FILE_NAME = "List";
    private GoogleMap mMap;
    private ClusterManager<MyItem> mClusterManager;
    private MyItem clickedClusterItem;
    private boolean goingToInfoWindow;
    private String[] nameOfSchool, addressOfSchool, schoolContact, typeOfSchool, publicOrPrivate, phoneNumber, 
    			premiumStatus, latitudeString, longitudeString;
    private Double[] latitude, longitude;

    @Override protected void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.loading);
	new getFile().execute(URL_OF_ASSETS);
    }

    @Override protected void onResume()
    {
	super.onResume();
    }

    private void setUpMapIfNeeded()
    {
	if(mMap == null)
	{
	    mMap = ((SupportMapFragment) getSupportFragmentManager()
			    .findFragmentById(R.id.map)).getMap();
	    if(mMap != null) setUpMap();
	}
    }

    public void setUpInformation()
    {
	new Reader(this);
	nameOfSchool = Reader.getNameOfSchool();
	addressOfSchool = Reader.getAddressOfSchool();
	schoolContact = Reader.getSchoolContact();
	typeOfSchool = Reader.getTypeOfSchool();
	publicOrPrivate = Reader.getPublicOrPrivate();
	phoneNumber = Reader.getPhoneNumber();
	premiumStatus = Reader.getPremiumStatus();
	latitudeString = Reader.getLatitudeString();
	longitudeString = Reader.getLongitudeString();
	latitude = new Double[latitudeString.length];
	longitude = new Double[longitudeString.length];
	for(int ii = 0; ii < latitudeString.length; ii++)
	{
	    latitude[ii] = Double.parseDouble(latitudeString[ii]);
	    longitude[ii] = Double.parseDouble(longitudeString[ii]);
	}
    }

    private void setUpMap()
    {
	setUpInformation();
	mMap.moveCamera(CameraUpdateFactory
			.newLatLngZoom(new LatLng(53.489525, - 7.733240), 7));
	mMap.setMyLocationEnabled(true);
	mClusterManager = new ClusterManager<>(this, mMap);
	MultiListener multiListener = new MultiListener();
	mMap.setOnCameraChangeListener(multiListener);
	multiListener.registerListener(mClusterManager);
	multiListener.registerListener(new GoogleMap.OnCameraChangeListener()
	{
	    @Override public void onCameraChange(CameraPosition cameraPosition)
	    {
		if(goingToInfoWindow) goingToInfoWindow = false;
		else
		{
		    LatLngBounds bounds = mMap.getProjection()
				    .getVisibleRegion().latLngBounds;
		    mClusterManager.clearItems();
		    for(int uu = 0; uu < nameOfSchool.length; uu++)
		    {
			try
			{
			    if(bounds.contains(new LatLng(latitude[uu],
					    longitude[uu])))
			    {
				MyItem clickedClusterItem = new MyItem(
						latitude[uu], longitude[uu],
						nameOfSchool[uu],
						typeOfSchool[uu],
						schoolContact[uu],
						phoneNumber[uu],
						addressOfSchool[uu],
						publicOrPrivate[uu],
						premiumStatus[uu]);
				mClusterManager.addItem(clickedClusterItem);
			    }

			}
			catch(Exception e)
			{
			    e.printStackTrace();
			}
		    }
		}
		mClusterManager.cluster();
	    }
	});
	MultiClicker mc = new MultiClicker();
	mMap.setOnMarkerClickListener(mc);
	mc.registerListener(mClusterManager);
	mc.registerListener(new GoogleMap.OnMarkerClickListener()
	{
	    public boolean onMarkerClick(Marker marker)
	    {
		goingToInfoWindow = true;
		return true;
	    }
	});
	mMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());
	mMap.setOnInfoWindowClickListener(mClusterManager);
	mClusterManager.setOnClusterItemInfoWindowClickListener(this);
	mClusterManager.setOnClusterItemClickListener(
			new ClusterManager.OnClusterItemClickListener<MyItem>()
			{
			    @Override public boolean onClusterItemClick(
					    MyItem item)
			    {
				clickedClusterItem = item;
				return false;
			    }
			});
	addItems();
	mClusterManager.getMarkerCollection()
			.setOnInfoWindowAdapter(new MyCustomAdapterForItems());
    }

    private void addItems()
    {
	for(int uu = 0; uu < nameOfSchool.length; uu++)
	{
	    try
	    {
		MyItem clickedClusterItem = new MyItem(latitude[uu],
				longitude[uu], nameOfSchool[uu],
				typeOfSchool[uu], schoolContact[uu],
				phoneNumber[uu], addressOfSchool[uu],
				publicOrPrivate[uu], premiumStatus[uu]);
		mClusterManager.addItem(clickedClusterItem);
	    }
	    catch(Exception e)
	    {
		e.printStackTrace();
	    }
	}
    }

    public boolean fileExists(String name)
    {
	File file = getApplicationContext().getFileStreamPath(name);
	return file.exists();
    }

    @Override public void onClusterItemInfoWindowClick(MyItem myItem)
    {
    }

    class getFile extends AsyncTask<String, Void, Void>
    {
	@Override protected Void doInBackground(String... params)
	{
	    URL downloadURL;
	    HttpURLConnection connection;
	    InputStream inputStream = null;
	    FileOutputStream fileOutputStream;
	    byte[] byteArray;
	    if(fileExists(FILE_NAME))
		getApplicationContext().deleteFile(FILE_NAME);
	    try
	    {
		downloadURL = new URL(params[0]);
		connection = (HttpURLConnection) downloadURL.openConnection();
		connection.setDoInput(true);
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Accept-Encoding", "identity");
		inputStream = connection.getInputStream();
		byteArray = IOUtils.toByteArray(inputStream);
		fileOutputStream = openFileOutput(FILE_NAME,
				Context.MODE_PRIVATE);
		connection.getInputStream().close();
		fileOutputStream.write(byteArray);
		fileOutputStream.close();
		inputStream.close();
	    }
	    catch(MalformedURLException e)
	    {
		e.printStackTrace();
	    }
	    catch(ProtocolException e)
	    {
		e.printStackTrace();
	    }
	    catch(FileNotFoundException e)
	    {
		e.printStackTrace();
	    }
	    catch(IOException e)
	    {
		e.printStackTrace();
	    }
	    finally
	    {
		if(inputStream != null)
		{
		    try
		    {
			inputStream.close();
		    }
		    catch(IOException e)
		    {
			e.printStackTrace();
		    }
		}
	    }
	    return null;
	}

	@Override protected void onPostExecute(Void aVoid)
	{
	    if(fileExists(FILE_NAME))
	    {
		setContentView(R.layout.activity_maps);
		setUpMapIfNeeded();
	    }
	    else setContentView(R.layout.failedloading);
	}
    }

    public class MultiClicker implements GoogleMap.OnMarkerClickListener
    {
	private List<GoogleMap.OnMarkerClickListener> mListeners = new ArrayList<>();

	public void registerListener(GoogleMap.OnMarkerClickListener listener)
	{
	    mListeners.add(listener);
	}

	@Override public boolean onMarkerClick(Marker marker)
	{
	    for(GoogleMap.OnMarkerClickListener ccl : mListeners)
	    {
		ccl.onMarkerClick(marker);
	    }
	    return false;
	}
    }

    public class MultiListener implements GoogleMap.OnCameraChangeListener
    {
	private List<GoogleMap.OnCameraChangeListener> mListeners = new ArrayList<>();

	public void registerListener(GoogleMap.OnCameraChangeListener listener)
	{
	    mListeners.add(listener);
	}

	@Override public void onCameraChange(CameraPosition cameraPosition)
	{
	    for(GoogleMap.OnCameraChangeListener ccl : mListeners)
	    {
		ccl.onCameraChange(cameraPosition);
	    }
	}
    }

    public class MyCustomAdapterForItems implements GoogleMap.InfoWindowAdapter
    {

	private final View myContentsView;

	MyCustomAdapterForItems()
	{
	    myContentsView = getLayoutInflater()
			    .inflate(R.layout.infowindow, null);
	}

	@Override public View getInfoWindow(Marker marker)
	{
	    TextView tvTitle = ((TextView) myContentsView
			    .findViewById(R.id.tv1));
	    TextView tvSnippet1 = ((TextView) myContentsView
			    .findViewById(R.id.tv2));
	    TextView tvSnippet2 = ((TextView) myContentsView
			    .findViewById(R.id.tv3));
	    TextView tvSnippet3 = ((TextView) myContentsView
			    .findViewById(R.id.tv4));
	    TextView tvSnippet4 = ((TextView) myContentsView
			    .findViewById(R.id.tv5));
	    TextView tvSnippet5 = ((TextView) myContentsView
			    .findViewById(R.id.tv6));
	    TableLayout table = ((TableLayout) myContentsView
			    .findViewById(R.id.table2));
	    if(clickedClusterItem.getSnippet6().equals("N"))
		table.setVisibility(View.GONE);
	    else if(clickedClusterItem.getSnippet6().equals("Y"))
		table.setVisibility(View.VISIBLE);
	    tvTitle.setText(clickedClusterItem.getTitle());
	    tvSnippet1.setText(clickedClusterItem.getSnippet1());
	    tvSnippet2.setText(clickedClusterItem.getSnippet2());
	    tvSnippet3.setText(clickedClusterItem.getSnippet3());
	    tvSnippet4.setText(clickedClusterItem.getSnippet4());
	    tvSnippet5.setText(clickedClusterItem.getSnippet5());
	    return myContentsView;
	}

	@Override public View getInfoContents(Marker marker)
	{
	    return null;
	}
    }

}
