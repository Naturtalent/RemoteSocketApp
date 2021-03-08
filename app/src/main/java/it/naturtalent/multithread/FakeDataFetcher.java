package it.naturtalent.multithread;

import android.content.Context;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Xml;
import android.widget.Toast;

import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NetworkException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.naturtalent.databinding.RemoteData;
import it.naturtalent.remotesocketapp.MainActivity;
import it.naturtalent.remotesocketapp.PrefsFragment;
import it.naturtalent.remotesocketapp.R;
import it.naturtalent.remotesocketapp.RemoteDataUtils;

public class FakeDataFetcher {

    static private String SOCKET_DATA_FILE = "socketdata.xml";

    public static class DataFetchException extends Exception {}

    public static class DataPushException extends Exception {}

    public static class ConnectException extends Exception {
        public String message;
        public ConnectException(String message)
        {
            this.message = message;
        }
    }

    private boolean mIsError = true;

    private Context context = MainActivity.getAppContext();

    static public final String SOCKET_TYPE_A = "A";
    static public final String SOCKET_TYPE_B = "B";
    static public final String SOCKET_TYPE_C = "C";


    /*
    @WorkerThread
    public String getData() throws DataFetchException {

        // simulate 2 seconds worth of work
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mIsError = !mIsError; // error response every other time

        if (mIsError) {
            throw new DataFetchException();
        } else {
            return "fake data";
        }
    }
     */

    @WorkerThread
    public List<RemoteData> getData() throws DataFetchException
    {

        List<RemoteData>remoteData = null;

        // simulate 2 seconds worth of work
        try
        {
            Thread.sleep(2000);

            // die reale Ladefunktion muss im Fehlerfall eine 'InterruptedException e' werfen
            remoteData = doLoadDataList();

        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        // Fehlerantwort fuer jedes zweite Mal (unklar)
        mIsError = !mIsError;

        if (mIsError)
        {
            throw new DataFetchException();
        } else
        {
            return remoteData;
        }
    }

    @WorkerThread
    public void setData(List<RemoteData>remoteData) throws DataPushException
    {
        // simulate 2 seconds worth of work
        try
        {
            Thread.sleep(2000);

            // die reale Ladefunktion muss im Fehlerfall eine 'InterruptedException e' werfen
            doSaveDataList(remoteData);

        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        // Fehlerantwort fuer jedes zweite Mal (unklar)
        mIsError = !mIsError;

        if (mIsError)
        {
            throw new DataPushException();
        }
    }


    /**
     *
     * @return
     */
    public List<RemoteData> doLoadDataList()  throws DataFetchException
    {
        //List<RemoteData>remoteDateList = getDefaultModel();

        List<RemoteData>remoteDateList = loadSockets ();

        /*
        List<RemoteData>remoteDateList = new ArrayList<>();

        int n = 20;
        for(int i = 1; i < n;i++)
        {
            RemoteData socket = new RemoteData("Schalter"+i, "B", "Code", "RemoteCode");
            remoteDateList.add(socket);
        }

         */


        return remoteDateList;
    }


    static private String SOCKET_ELEMENT = "Socket";

    /**
     * RemoteSockets laden
     * @return
     */
    public List<RemoteData> loadSockets ()  throws DataFetchException
    {
        List<RemoteData> sockets = new ArrayList<RemoteData>();
        String parseName = null;

        try
        {
            File file = new File(context.getFilesDir(), SOCKET_DATA_FILE);
            if (file.exists())
            {
                BufferedReader input = new BufferedReader(new InputStreamReader(context.openFileInput(SOCKET_DATA_FILE)));
                String line;
                StringBuffer buffer = new StringBuffer();
                while((line =  input.readLine()) != null)
                    buffer.append(line);

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput( new StringReader( buffer.toString() ) );
                int eventType = xpp.getEventType();

                // sammelt alle geparsten Elemente
                Map<String, String> parseMap = new HashMap<String, String>();

                while (eventType != XmlPullParser.END_DOCUMENT)
                {
                    if(eventType == XmlPullParser.START_TAG)
                    {

                        android.util.Log.i("Parser", "xpp: "+xpp);

                        if(xpp.getName().equals(SOCKET_ELEMENT))
                        {
                            // Start Socket - Map loeschen
                            parseMap.clear();
                        }
                        else
                        {
                            parseName = xpp.getName();
                        }
                    }

                    if(eventType == XmlPullParser.TEXT)
                    {
                        if(parseName != null)
                        {
                            parseMap.put(parseName, xpp.getText());
                            parseName = null;
                        }
                    }

                    if(eventType == XmlPullParser.END_TAG)
                    {
                        if (xpp.getName().equals(SOCKET_ELEMENT))
                        {
                            // jetzt ist XML-Socketdaten komplett geparst
                            String type = parseMap.get("type");

                            // mit den geparsten XML-Socketdaten realen Socket generieren
                            if(type.equals(SOCKET_TYPE_A))
                            {
                                // Type A Socket (Haus- und Remote(Receiver)code
                                RemoteData socket = new RemoteData(parseMap.get("name"), SOCKET_TYPE_A, parseMap.get("houseCode"), parseMap.get("remoteCode"));
                                sockets.add(socket);
                            }
                            else
                            {
                                // @TODO Loesung fuer restliche Types noch erforderlich
                                // Provisorisch
                                RemoteData socket = new RemoteData(parseMap.get("name"), type, parseMap.get("houseCode"), parseMap.get("remoteCode"));
                                sockets.add(socket);

                            }
                        }
                    }
                    eventType = xpp.next();
                }


            } else
            {
                System.out.println("Not find file ");
                sockets = getDefaultModel();
            }

        }
        catch (Exception e)
        {
            throw new DataFetchException();
        }

        return sockets;
    }

/*
    @WorkerThread
    public List<RemoteData> pushData(List<RemoteData>remoteData) throws DataFetchException
    {
        // simulate 2 seconds worth of work
        try
        {
            Thread.sleep(2000);

            // die reale Ladefunktion muss im Fehlerfall eine 'InterruptedException e' werfen
            doPushDataList(remoteData);

        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        // Fehlerantwort fuer jedes zweite Mal (unklar)
        mIsError = !mIsError;

        if (mIsError)
        {
            throw new DataFetchException();
        } else
        {
            return remoteData;
        }
    }

 */

    /**
     *
     * @return
     */
    public void doSaveDataList(List<RemoteData>remoteData)  throws DataPushException
    {

        android.util.Log.i("FragmentAlertDialog", "Save Ok!  ");

        saveSockets(remoteData);

        //List<RemoteData>remoteDateList = getDefaultModel();

        //List<RemoteData>remoteDateList = loadSockets ();

        /*
        List<RemoteData>remoteDateList = new ArrayList<>();

        int n = 20;
        for(int i = 1; i < n;i++)
        {
            RemoteData socket = new RemoteData("Schalter"+i, "B", "Code", "RemoteCode");
            remoteDateList.add(socket);
        }

         */
    }


    /**
     * RemoteSockets in Datei peichern
     *
     * @param sockets
     */
    public void saveSockets(List<RemoteData> sockets)
    {
        if ((sockets != null) && (!sockets.isEmpty()))
        {

            XmlSerializer xmlSerializer = Xml.newSerializer();
            StringWriter writer = new StringWriter();
            try
            {
                xmlSerializer.setOutput(writer);

                // Start Document
                xmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
                xmlSerializer.startDocument("UTF-8", true);

                // Open Tag <sockets>
                xmlSerializer.startTag("", "RemoteSockets");

                for (RemoteData socket : sockets)
                {
                    serializeSocket(xmlSerializer, socket);
                    xmlSerializer.flush();
                }

                // Ende der Sockets
                xmlSerializer.endTag("", "RemoteSockets");

                // Ende des Dokuments
                xmlSerializer.endDocument();


                File file = new File(SOCKET_DATA_FILE);
                if (file.exists())
                {
                    Toast.makeText(context, "Datei kann nicht erzeugt werden, Berechtigung?", Toast.LENGTH_SHORT).show();
                    throw new DataPushException();
                }




                FileOutputStream openFileOutput = context.openFileOutput(SOCKET_DATA_FILE, Context.MODE_PRIVATE);
                openFileOutput.write(writer.toString().getBytes());
                openFileOutput.close();

                file = new File(context.getFilesDir(), SOCKET_DATA_FILE);
                if (file.exists())
                    android.util.Log.i("FragmentAlertDialog", "nicht speichern!");


            } catch (IOException | DataPushException e)
            {
                e.printStackTrace();
            }
        }
    }


    private void serializeSocket(XmlSerializer xmlSerializer, RemoteData socketData) throws IOException
    {

        // start Elemnt Socket
        xmlSerializer.startTag("", SOCKET_ELEMENT);

        // name
        xmlSerializer.startTag("", "name");
        xmlSerializer.text(socketData.getName());
        xmlSerializer.endTag("", "name");

        if (socketData.getType().equals(SOCKET_TYPE_A))
        {
            // type
            xmlSerializer.startTag("", "type");
            xmlSerializer.text(socketData.getType());
            xmlSerializer.endTag("", "type");

            // houseCode
            xmlSerializer.startTag("", "houseCode");
            xmlSerializer.text(socketData.getHouseCode());
            xmlSerializer.endTag("", "houseCode");

            // remoteCode
            xmlSerializer.startTag("", "remoteCode");
            xmlSerializer.text(socketData.getRemoteCode());
            xmlSerializer.endTag("", "remoteCode");
        }
        else // @TODO Loesung fuer restliche Types noch erforderlich
        {
            // Provisorium

            // type
            xmlSerializer.startTag("", "type");
            xmlSerializer.text(socketData.getType());
            xmlSerializer.endTag("", "type");

            // houseCode
            xmlSerializer.startTag("", "houseCode");
            xmlSerializer.text(socketData.getHouseCode());
            xmlSerializer.endTag("", "houseCode");

            // remoteCode
            xmlSerializer.startTag("", "remoteCode");
            xmlSerializer.text(socketData.getRemoteCode());
            xmlSerializer.endTag("", "remoteCode");

        }


        // ende Element Socket
        xmlSerializer.endTag("", SOCKET_ELEMENT);
    }

    /*
    *
            Tinkerforge WiFi Connection
    *
     */

    public List<RemoteData> getDefaultModel()
    {
        List<RemoteData> list = new ArrayList<RemoteData>();
        list.add(new RemoteData("Pumpe1", SOCKET_TYPE_A, "1", "1"));
        list.add(new RemoteData("Pumpe2", SOCKET_TYPE_A, "1", "2"));
        list.add(new RemoteData("Skimmer", SOCKET_TYPE_A, "1", "4"));
        list.add(new RemoteData("Strahler", SOCKET_TYPE_A, "1", "8"));
        return list;
    }

    @WorkerThread
    public IPConnection connectWiFi() throws ConnectException
    {
        IPConnection ipConnection = null;

        // simulate 2 seconds worth of work
        try
        {
            Thread.sleep(2000);

            // die reale Connectfunktion muss im Fehlerfall eine 'InterruptedException e' werfen
            ipConnection = doConnection();

        } catch (InterruptedException | NetworkException | AlreadyConnectedException e)
        {
            throw new ConnectException(e.getMessage());

            //e.printStackTrace();
        }

        // Fehlerantwort fuer jedes zweite Mal (unklar)
        mIsError = !mIsError;

        if (mIsError)
        {
            throw new ConnectException("Multicast Fehler");
        }

        return ipConnection;
    }

    /**
     *
     * @return
     */
    public IPConnection doConnection() throws NetworkException, AlreadyConnectedException
    {
        String host;
        String port;

        host = "NtHost";
        port = "4223";

        // die Parameter 'host' und 'port' aus den Settings lesen
        Resources res = MainActivity.getAppContext().getResources();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(MainActivity.getAppContext());
        host = pref.getString(res.getString(R.string.host_key),res.getString(R.string.host_default));
        port = pref.getString(res.getString(R.string.port_key),res.getString(R.string.port_default));

        // ueber Tinkerforge API WiFi - Verbindung herstellen
        IPConnection ipConnection = new IPConnection();
        ipConnection.connect(host, Integer.parseInt(port));
        return ipConnection;
    }

}
