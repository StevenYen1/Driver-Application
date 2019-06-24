package com.example.refresh;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class RestCalls extends AppCompatActivity {

    final static String url = "http://eptsperf.staples.com/TrackIt/package/track/v3/shipment/";
    public static final MediaType xml = MediaType.parse("application/xml; charset=utf-8");
    TextView textViewResult;
    JsonPlaceHolderApi jsonPlaceHolderApi;
    JsonPlaceHolderApi xmlRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest_calls);
        textViewResult = findViewById(R.id.rest_results);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Retrofit xmlPart1 = new Retrofit.Builder()
                .baseUrl("http://eptsperf.staples.com/")
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        this.xmlRequest = xmlPart1.create(JsonPlaceHolderApi.class);

        String myXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "\n" +
                "<ShipmentTrackingRequest>\n" +
                "\n" +
                "<RequestInfo BusinessUnit=\"SBD_US\" ClientChannel=\"web\" ByPassLocal=\"false\" depthRequested=\"HEAD\" readSkus=\"false\">\n" +
                "<ReferenceID RequestType=\"ORD\">9743946803</ReferenceID>\n" +
                "<Shipment ShipmentNumber=\"\">\n" +
                "<Container>\n" +
                "<TrackingID></TrackingID>\n" +
                "<SCAC></SCAC>\n" +
                "<DestZipCode></DestZipCode>\n" +
                "<ShippedDate></ShippedDate>\n" +
                "</Container>\n" +
                "</Shipment>\n" +
                "</RequestInfo>\n" +
                "\n" +
                "</ShipmentTrackingRequest>";

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

                    RestCalls.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(RestCalls.this, ""+response.code(), Toast.LENGTH_SHORT).show();
                            textViewResult.setText(""+myResponse);
                        }
                    });
                }
                else{
                    Toast.makeText(RestCalls.this, "Not successful", Toast.LENGTH_SHORT).show();
                }
            }
        });
        
//        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

//        getPosts();
//        getComments();
//        createPost();

    }

        private void getPosts() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("userId", "1");
        parameters.put("_sort", "id");
        parameters.put("_order", "desc");

        Call<List<Post>> call = jsonPlaceHolderApi.getPosts(parameters);

        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if(!response.isSuccessful()){
                    textViewResult.setText("Error: "+response.code());
                    return;
                }

                List<Post> posts = response.body();
                String text_content = "";
                for(Post post: posts){
                    text_content += "------------------------NEW POST------------------------\n";
                    text_content += "Id: " + post.getId() + "\n";
                    text_content += "UserId: " + post.getUserId() + "\n";
                    text_content += "\n";
                    text_content += "Title:\n" + post.getTitle() + "\n";
                    text_content += "\n";
                    text_content += "Body:\n" + post.getText() + "\n";
                    text_content += "\n";
                    text_content += "\n";
                }

                textViewResult.setText(text_content);
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                textViewResult.setText(t.getMessage());
            }
        });
    }

    private void getComments(){
        Call<List<Comment>> call = jsonPlaceHolderApi.getComments(3);

        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if(!response.isSuccessful()){
                    textViewResult.setText("Code: " + response.code());
                    return;
                }

                List<Comment> comments = response.body();
                String text_content = "";

                for (Comment comment : comments) {
                    text_content += "------------------------NEW POST------------------------\n";
                    text_content += "Post Id: " + comment.getPostId() + "\n";
                    text_content += "Id: " + comment.getId() + "\n";
                    text_content += "\n";
                    text_content += "Name:\n" + comment.getName() + "\n";
                    text_content += "\n";
                    text_content += "Email:\n" + comment.getEmail() + "\n";
                    text_content += "\n";
                    text_content += "Text:\n" + comment.getText() + "\n";
                    text_content += "\n";
                    text_content += "\n";
                }
                textViewResult.setText(text_content);
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                textViewResult.setText(t.getMessage());
            }
        });
    }

    private void createPost() {
        Post post = new Post(23, "New Title", "Next Text");

        Call<Post> call = jsonPlaceHolderApi.createPost(post);

        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if(!response.isSuccessful()){
                    textViewResult.setText("Code: " + response.code());
                    return;
                }
                Post postResponse = response.body();

                String content = "";
                content += "Code: " + response.code() + "\n";
                content += "Id: " + postResponse.getId() + "\n";
                content += "User id: " + postResponse.getUserId() + "\n";
                content += "Title: " + postResponse.getTitle() + "\n";
                content += "Text: " + postResponse.getText() + "\n\n";

                textViewResult.setText(content);
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {

            }
        });
    }
}
