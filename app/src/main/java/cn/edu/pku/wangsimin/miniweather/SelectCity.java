package cn.edu.pku.wangsimin.miniweather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;
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
    private TextView tvCityName;
    private ListView mListView;
    private List<City> mCityList;
    private ArrayList<String> city = new ArrayList<String>();
    private ArrayList<String> cityId = new ArrayList<String>();
    private String selectedId;
    private String selectedCity;
    private EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);

        mBackBtn = (ImageView) findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);
        tvCityName = (TextView) findViewById(R.id.title_name);
        mListView = (ListView) findViewById(R.id.city_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                SelectCity.this, android.R.layout.simple_list_item_1, city);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(SelectCity.this,"你单击了:" + city.get(i), Toast.LENGTH_SHORT).show();
                tvCityName.setText("选择城市:"+city.get(i).substring(5));
                selectedId = cityId.get(i);
                selectedCity = city.get(i);
            }
        });

        int i=0;
        mCityList = MyApplication.getInstance().getCityList();
        while(i < mCityList.size()){
            city.add(mCityList.get(i).getProvince().toString() + " - " + mCityList.get(i).getCity().toString());
            cityId.add(mCityList.get(i).getNumber().toString());
            i++;
        }

        mEditText = (EditText) findViewById(R.id.search_edit);
        mEditText.addTextChangedListener(mTextWatcher);
    }

    TextWatcher mTextWatcher = new TextWatcher() {
        private CharSequence temp;
        private int editStart;
        private int editEnd;
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            temp = s;
            Log.d("myWeather","beforeTextChanged:"+temp);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            final ArrayList<String> newCity = new ArrayList<String>();
            final ArrayList<String> newCityId = new ArrayList<String>();

            if(mEditText.getText() != null){
                String search_city = mEditText.getText().toString();
                for(int i=0; i<mCityList.size(); i++){
                    if(mCityList.get(i).getCity().contains(search_city)){
                        newCity.add(mCityList.get(i).getCity());
                        newCityId.add(mCityList.get(i).getNumber());
                    }
                }
            }
            ListView mListView = (ListView) findViewById(R.id.city_list);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    SelectCity.this, android.R.layout.simple_list_item_1, newCity);
            mListView.setAdapter(adapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i,long l){
                    selectedId = newCityId.get(i);
                    selectedCity = newCity.get(i);
                    tvCityName.setText("选择城市:"+newCity.get(i));
                }
            });
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                if(selectedId!=null){
                    SharedPreferences.Editor editor=getSharedPreferences("config",MODE_PRIVATE).edit();
                    editor.putString("main_city-code",selectedId);
                    editor.commit();
                }else{
                    SharedPreferences sharedPreferences = getSharedPreferences("config",MODE_PRIVATE);
                    selectedId = sharedPreferences.getString("main_city-code","101010100");
                }
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
