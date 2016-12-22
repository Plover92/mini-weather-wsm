package cn.edu.pku.wangsimin.miniweather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.wangsimin.bean.City;
import cn.edu.pku.wangsimin.app.MyApplication;
/**
 * Created by wangsimin on 2016/10/21.
 */
public class SelectCity extends Activity implements View.OnClickListener {

    private ImageView mBackBtn;

    private ListView mListView;
    private List<City> data;

    ArrayList<String> city = new ArrayList<String>();
    ArrayList<String> cityId = new ArrayList<String>();
    ArrayList<String> newCity = new ArrayList<String>();

    private String selectedId;

    private EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);

        mBackBtn = (ImageView) findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);

        mListView = (ListView) findViewById(R.id.city_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                SelectCity.this, android.R.layout.simple_list_item_1, city);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(SelectCity.this,"你单击了:" + city.get(i), Toast.LENGTH_SHORT).show();
                selectedId = cityId.get(i);
            }
        });

        int i=0;
        data = MyApplication.getInstance().getCityList();
        while(i < data.size()){
            city.add(data.get(i).getProvince().toString() + " - " + data.get(i).getCity().toString());
            cityId.add(data.get(i).getNumber().toString());
            i++;
        }

        mEditText = (EditText) findViewById(R.id.search_edit);
        mEditText.addTextChangedListener(mTextWatcher);
    }

    TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            newCity.clear();
            if(mEditText.getText() != null){
                String search_city = mEditText.getText().toString();
                for(int i=0; i<data.size(); i++){
                    if(data.get(i).getCity().contains(search_city)){
                        newCity.add(data.get(i).getCity().toString());
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        SelectCity.this, android.R.layout.simple_list_item_1, newCity);
                mListView.setAdapter(adapter);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                Intent i = new Intent();
                i.putExtra("cityCode", selectedId);
                setResult(RESULT_OK, i);
                finish();
                break;
            default:
                break;
        }
    }
}
