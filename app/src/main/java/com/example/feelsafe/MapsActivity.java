package com.example.feelsafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.Iterator;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationmanager;
    LocationListener locationListener;
    LatLng latLng;
    Marker currMark;
    Marker markedMarker;
    public ArrLst CritLst;
    public ArrLst SusLst;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        DatabaseReference fb=FirebaseDatabase.getInstance().getReference();
        initList();
        getSupportActionBar().setTitle("Please tell the situation around your area");



    }
    public void initList() {
        new Thread() {
            public void run() {
        DatabaseReference dR=FirebaseDatabase.getInstance().getReference().child("critical");
        dR.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               try { CritLst=dataSnapshot.getValue(ArrLst.class);

                           Iterator i=CritLst.arr.iterator();
                           while(i.hasNext()) { try {
                               String s=i.next().toString();
                               LatLng n=new LatLng(Double.parseDouble(s.substring(0,s.indexOf(','))),Double.parseDouble(s.substring(s.indexOf(',')+1)));
                               mMap.addMarker(new MarkerOptions().position(n).title("Critical Location").
                                       icon(bitmapDescripterFromVector(getApplicationContext(),R.drawable.ic_sentiment_very_dissatisfied_black_24dp))); }
                           catch(Exception E) {}
                           }

               } catch(Exception e) { CritLst=new ArrLst(); }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MapsActivity.this, "Data loading failed!", Toast.LENGTH_SHORT).show();
            }
        });}
        }.start();
        //Initializing suspected list
        new Thread() {
            public void run() {
        DatabaseReference dR=FirebaseDatabase.getInstance().getReference().child("suspected");
        dR.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try { SusLst=dataSnapshot.getValue(ArrLst.class);

                            Iterator i=SusLst.arr.iterator();
                            while(i.hasNext()) { try {
                                String s=i.next().toString();
                                LatLng n=new LatLng(Double.parseDouble(s.substring(0,s.indexOf(','))),Double.parseDouble(s.substring(s.indexOf(',')+1)));
                                mMap.addMarker(new MarkerOptions().position(n).title("Infection Suspected Location").
                                        icon(bitmapDescripterFromVector(getApplicationContext(),R.drawable.ic_healing_black_24dp))); }
                            catch(Exception E) {}
                            }

                } catch(Exception e) {  SusLst=new ArrLst();}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MapsActivity.this, "Data loading failed!", Toast.LENGTH_SHORT).show();
            }
        }); }
        }.start();

    }
    private void addCriticalLocation(String s) {
        CritLst.arr.add(s);
        FirebaseDatabase.getInstance().getReference().child("critical").setValue(CritLst);
    }
    private void removeCriticalPosition(String s) {
        CritLst.arr.remove(s);
        FirebaseDatabase.getInstance().getReference().child("critical").setValue(CritLst);
    }
    private void addSuspectedLocation(String s) {
        SusLst.arr.add(s);
        FirebaseDatabase.getInstance().getReference().child("suspected").setValue(SusLst);
    }
    private void removeSuspectedPosition(String s) {
        SusLst.arr.remove(s);
        FirebaseDatabase.getInstance().getReference().child("suspected").setValue(SusLst);
    }
    /*
    private void markMap() {
        new Thread() {
            public void run() {
                Iterator i=CritLst.arr.iterator();
                while(i.hasNext()) { try {
                    String s=i.next().toString();
                    Toast.makeText(MapsActivity.this, s+"  "+CritLst.arr.size(), Toast.LENGTH_SHORT).show();
                    LatLng n=new LatLng(Double.parseDouble(s.substring(0,s.indexOf(','))),Double.parseDouble(s.substring(s.indexOf(',')+1)));
                    mMap.addMarker(new MarkerOptions().position(n).title("Critical Location").
                            icon(bitmapDescripterFromVector(getApplicationContext(),R.drawable.ic_sentiment_very_dissatisfied_black_24dp))); }
                            catch(Exception E) {}
                }
            }
        }.start();
        new Thread() {
            public void run() {
                Iterator i=SusLst.arr.iterator();
                while(i.hasNext()) { try {
                    String s=i.next().toString(); Toast.makeText(MapsActivity.this, s, Toast.LENGTH_SHORT).show();
                    LatLng n=new LatLng(Double.parseDouble(s.substring(0,s.indexOf(','))),Double.parseDouble(s.substring(s.indexOf(',')+1)));
                    mMap.addMarker(new MarkerOptions().position(n).title("Infection Suspected Location").
                            icon(bitmapDescripterFromVector(getApplicationContext(),R.drawable.ic_healing_black_24dp))); }
                catch(Exception E) {}
                }
            }
        }.start();
    }
    public void mypos() {
        currMark= mMap.addMarker(new MarkerOptions().position(latLng).title("You are here!").
                icon(bitmapDescripterFromVector(getApplicationContext(),R.drawable.ic_directions_walk_black_24dp)));
    }*/


    private void markMap1() {
        new Thread() {
            public void run() {
                DatabaseReference dR=FirebaseDatabase.getInstance().getReference().child("critical");
                dR.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                        ArrLst ob=  dataSnapshot.getValue(ArrLst.class);
                            for(int i=0;i<ob.arr.size()-CritLst.arr.size();i++) { String s=ob.arr.get(CritLst.arr.size()+i);
                                LatLng n=new LatLng(Double.parseDouble(s.substring(0,s.indexOf(','))),Double.parseDouble(s.substring(s.indexOf(',')+1)));
                                mMap.addMarker(new MarkerOptions().position(n).title("Critical Location").
                                        icon(bitmapDescripterFromVector(getApplicationContext(),R.drawable.ic_sentiment_very_dissatisfied_black_24dp)));
                            } CritLst=ob;
                        } catch(Exception e) {}
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(MapsActivity.this, "Please ensure connectivity\nProblem occurred in updating", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }.start();

        new Thread() {
            public void run() {
                DatabaseReference dR=FirebaseDatabase.getInstance().getReference().child("suspected");
                dR.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            ArrLst ob=  dataSnapshot.getValue(ArrLst.class);
                            for(int i=0;i<ob.arr.size()-SusLst.arr.size();i++) {String s=ob.arr.get(SusLst.arr.size()+i);
                            LatLng n=new LatLng(Double.parseDouble(s.substring(0,s.indexOf(','))),Double.parseDouble(s.substring(s.indexOf(',')+1)));
                            mMap.addMarker(new MarkerOptions().position(n).title("Infection Suspected Location").
                                    icon(bitmapDescripterFromVector(getApplicationContext(),R.drawable.ic_healing_black_24dp))); }
                            SusLst=ob;
                        } catch(Exception e) {}
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(MapsActivity.this, "Please ensure connectivity\nProblem occurred in updating", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }.start();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        locationmanager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
                if(currMark!=null) currMark.remove();
                currMark= mMap.addMarker(new MarkerOptions().position(latLng).title("You are here!").
                        icon(bitmapDescripterFromVector(getApplicationContext(),R.drawable.ic_directions_walk_black_24dp)));
                //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        //Asking permission
        askLocationPermission();
        //markMap();
        markMap1();

    }

    private void askLocationPermission() {
        Dexter.withActivity(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location last=locationmanager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                latLng=new LatLng(last.getLatitude(),last.getLongitude());
                mMap.clear();
                currMark= mMap.addMarker(new MarkerOptions().position(latLng).title("you are here").
                        icon(bitmapDescripterFromVector(getApplicationContext(),R.drawable.ic_face_black_24dp)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15f));
                //Toast.makeText(MapsActivity.this, "You are here", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }

    //new Icon
    private BitmapDescriptor bitmapDescripterFromVector(Context context,int vectorResId) {
        Drawable vectorDrawable= ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0,0,vectorDrawable.getIntrinsicWidth(),vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap=Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),vectorDrawable.getIntrinsicHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.my_options,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem i) {
        switch(i.getItemId()) {
            case R.id.str1:
                Toast.makeText(this, "safe place marked", Toast.LENGTH_SHORT).show();
                if(markedMarker!=null) markedMarker.remove();
                markedMarker=mMap.addMarker(new MarkerOptions().position(latLng).title("Safe place").
                        icon(bitmapDescripterFromVector(getApplicationContext(),R.drawable.ic_done_black_24dp)));
                return  true;
            case R.id.str2:
                new AlertDialog.Builder(this).setMessage("Are you sure you want to mark this area suspected of corona virus.\n" +
                        "Please be sure because you may misguide someone.")
                        .setPositiveButton("Mark", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MapsActivity.this, latLng.latitude+"", Toast.LENGTH_SHORT).show();
                                addSuspectedLocation(latLng.latitude+","+latLng.longitude);      //adding to the database
                                if(markedMarker!=null) {
                                    if(markedMarker.getTitle().equals("Critical of CoVID-19"))
                                    {  LatLng n=markedMarker.getPosition(); removeCriticalPosition(n.latitude+","+n.longitude);  }
                                    markedMarker.remove(); }
                                markedMarker=mMap.addMarker(new MarkerOptions().position(latLng).title("Suspected place").
                                        icon(bitmapDescripterFromVector(getApplicationContext(),R.drawable.ic_healing_black_24dp)));
                            }
                        }).setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MapsActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();

                    }
                }).show();

                return  true;
            case R.id.str3:
                new AlertDialog.Builder(this).setMessage("Are you sure you want to mark this area infected from corona virus.\n" +
                        "Please be sure because you may misguide someone.")
                        .setPositiveButton("Mark", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MapsActivity.this, "Marked", Toast.LENGTH_SHORT).show();
                                addCriticalLocation(latLng.latitude+","+latLng.longitude);       //adding to the database
                                if(markedMarker!=null) {
                                    if(markedMarker.getTitle().equals("Suspected place"))
                                    {  LatLng n=markedMarker.getPosition(); removeSuspectedPosition(n.latitude+","+n.longitude);  }
                                    markedMarker.remove();
                                }
                                markedMarker=mMap.addMarker(new MarkerOptions().position(latLng).title("Critical of CoVID-19").
                                        icon(bitmapDescripterFromVector(getApplicationContext(),R.drawable.ic_sentiment_very_dissatisfied_black_24dp)));
                            }
                        }).setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MapsActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();

                    }
                }).show();

                return  true;
            case R.id.str4:
                if(markedMarker==null) Toast.makeText(this, "You have not marked anything now", Toast.LENGTH_SHORT).show();
                else {
                    if(markedMarker.getTitle().equals("Suspected place"))
                    {  LatLng n=markedMarker.getPosition(); removeSuspectedPosition(n.latitude+","+n.longitude);  }
                    if(markedMarker.getTitle().equals("Critical of CoVID-19"))
                    {  LatLng n=markedMarker.getPosition(); removeCriticalPosition(n.latitude+","+n.longitude);  }
                    markedMarker.remove();
                }
                return true;
            case R.id.str5:
                new AlertDialog.Builder(this).setMessage("This app is fully public and open source." +
                        "It is made keeping in mind the present situation. The marker you mark on this map will be available to each person opening the app" +
                        " without any details about the one who marked." +
                        "In South Korea, Govt. used technology to mark out places with growing cases of CoVID-19 so that they would convey to the public to stay safe." +
                        "This helped them in great scale and the result is surely not unknown.Let's follow them. Each marker you place will just help everyone." +
                        " If anyone needs the database it will be happily given(shivendra12.07.98@gmail.com)." +
                        "Help this grow and be proved as a Hero. Information is the only aid now.")
                    .show();
                return true;


            default: return false;
        }

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setMessage("All your concerned markers will be saved for the public.\nNo Info of yours is taken.\n" +
                "You will not be able to unmark afterwards.")
                .setPositiveButton("Quit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).show();

    }
}
