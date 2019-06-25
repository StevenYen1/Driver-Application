package com.example.refresh;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.io.File;
import java.io.StringWriter;

public class RestCalls extends AppCompatActivity {

    final static String url = "http://eptsperf.staples.com/TrackIt/package/track/v3/shipment/";
    public static final MediaType xml = MediaType.parse("application/xml; charset=utf-8");
    public String bigresponse = "";
    TextView textViewResult;
//    String myXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
//            "\n" +
//            "<ShipmentTrackingRequest>\n" +
//            "\n" +
//            "<RequestInfo BusinessUnit=\"SBD_US\" ClientChannel=\"web\" ByPassLocal=\"false\" depthRequested=\"HEAD\" readSkus=\"false\">\n" +
//            "<ReferenceID RequestType=\"ORD\">9743946803</ReferenceID>\n" +
//            "<Shipment ShipmentNumber=\"\">\n" +
//            "<Container>\n" +
//            "<TrackingID></TrackingID>\n" +
//            "<SCAC></SCAC>\n" +
//            "<DestZipCode></DestZipCode>\n" +
//            "<ShippedDate></ShippedDate>\n" +
//            "</Container>\n" +
//            "</Shipment>\n" +
//            "</RequestInfo>\n" +
//            "\n" +
//            "</ShipmentTrackingRequest>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest_calls);
        textViewResult = findViewById(R.id.rest_results);
        sendPostRequest(create_XML());

    }

    private void sendPostRequest(String myXML){
        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(xml, myXML);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, final okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();
                    for (char x : myResponse.toCharArray()){

                        bigresponse+=x;
                        if(x=='>'){
                            bigresponse+="\n";
                        }
                        Log.d("TAG", bigresponse);
                    }

                    RestCalls.this.runOnUiThread(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void run() {
                            Toast.makeText(RestCalls.this, "HTTP Status: "+response.code(), Toast.LENGTH_SHORT).show();
                            textViewResult.setText(""+bigresponse);
                            textViewResult.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                        }
                    });
                }
                else{
                    Toast.makeText(RestCalls.this, "Not successful", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public String create_XML(){
        try {
            DocumentBuilderFactory dbFactory =
                    DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            // root element
            Element rootElement = doc.createElement("ShipmentTrackingRequest");
            doc.appendChild(rootElement);

            // requestinfo element
            Element requestInfo = doc.createElement("RequestInfo");
            rootElement.appendChild(requestInfo);

                // setting attribute to element
                Attr businessUnit = doc.createAttribute("BusinessUnit");
                businessUnit.setValue("SBD_US");
                requestInfo.setAttributeNode(businessUnit);

                Attr clientChannel = doc.createAttribute("ClientChannel");
                clientChannel.setValue("web");
                requestInfo.setAttributeNode(clientChannel);

                Attr byPassLocal = doc.createAttribute("ByPassLocal");
                byPassLocal.setValue("false");
                requestInfo.setAttributeNode(byPassLocal);

                Attr depthRequested = doc.createAttribute("depthRequested");
                depthRequested.setValue("HEAD");
                requestInfo.setAttributeNode(depthRequested);

                Attr readSkus = doc.createAttribute("readSkus");
                readSkus.setValue("false");
                requestInfo.setAttributeNode(readSkus);

            //reference_id element
            Element referenceId = doc.createElement("ReferenceID");
            referenceId.appendChild(doc.createTextNode("9743946803"));
            requestInfo.appendChild(referenceId);

                Attr requestType = doc.createAttribute("RequestType");
                requestType.setValue("ORD");
                referenceId.setAttributeNode(requestType);

            //shipment element
            Element shipment = doc.createElement("Shipment");
            requestInfo.appendChild(shipment);

                Attr shipmentNumber = doc.createAttribute("ShipmentNumber");
                shipmentNumber.setValue("");
                shipment.setAttributeNode(shipmentNumber);

            //container element
            Element container = doc.createElement("Container");
            shipment.appendChild(container);

            //tracking_id element
            Element trackingId = doc.createElement("TrackingID");
            container.appendChild(trackingId);

            //scac element
            Element scac = doc.createElement("SCAC");
            container.appendChild(scac);

            //destination zip code element
            Element destZip = doc.createElement("DestZipCode");
            container.appendChild(destZip);

            //date shipped element
            Element shippedDate = doc.createElement("ShippedDate");
            container.appendChild(shippedDate);

            return xmltoString(doc);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String xmltoString(Document doc){
        try {
            StringWriter sw = new StringWriter();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            transformer.transform(new DOMSource(doc), new StreamResult(sw));
            return sw.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Error converting to String", ex);
        }
    }
}
