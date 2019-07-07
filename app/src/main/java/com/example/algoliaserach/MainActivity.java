package com.example.algoliaserach;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static final String ALGOLIA_APP_ID = "2IRDE8GWI2";
    static final String ALGOLIA_INDEX = "products";
    static final String ALGOLIA_API_KEY = "c251493aba2032356766ef5a1a2a2403";

    Client client = new Client(ALGOLIA_APP_ID, ALGOLIA_API_KEY);
    Index index = client.getIndex(ALGOLIA_INDEX);
    AppCompatEditText search;
    CompletionHandler completionHandler;
    ListView output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        output = findViewById(R.id.list);
        search = findViewById(R.id.search);

        completionHandler = new CompletionHandler() {
            @Override
            public void requestCompleted(JSONObject content, AlgoliaException error) {
                Log.e("requestCompleted: ", String.valueOf(content));
                try {
                    JSONArray hits=content.getJSONArray("hits");
                    List<String> list=new ArrayList<>();
                    for (int i=0;i<hits.length();i++){
                        JSONObject jsonObject=hits.getJSONObject(i);;
                        JSONObject jsonObject1=jsonObject.getJSONObject("_highlightResult");
                        JSONObject jsonObject2=jsonObject1.getJSONObject("title");
                        JSONObject jsonObject3=jsonObject1.getJSONObject("description");

                        String title=jsonObject2.getString("value");
                        String description=jsonObject3.getString("value");
                        list.add(title+"\n"+description);

                    }
                    ArrayAdapter<String> stringArrayAdapter=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,list);
                    output.setAdapter(stringArrayAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };


        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.e("beforeTextChanged: ", s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("onTextChanged: ", s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e("afterTextChanged: ", s.toString());
                if (s.toString().equals("")) {

                } else {
                 Query query=  new Query(s.toString()).setHitsPerPage(10);
                    index.searchAsync(query, completionHandler);
                }
            }
        });


    }
}
