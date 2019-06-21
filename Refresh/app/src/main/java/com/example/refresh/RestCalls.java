package com.example.refresh;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestCalls extends AppCompatActivity {

    TextView textViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest_calls);
        textViewResult = findViewById(R.id.rest_results);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        Call<List<Post>> call = jsonPlaceHolderApi.getPosts();

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
}
