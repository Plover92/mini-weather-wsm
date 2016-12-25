package cn.edu.pku.wangsimin.miniweather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by wangsimin on 16/11/29.
 */

public class Guide extends Activity implements ViewPager.OnPageChangeListener {
    private ViewPagerAdapter vpAdapter;
    private ViewPager vp;
    private List<View> views;

    private ImageView[] dots;
    private int[] ids = {R.id.iv1,R.id.iv2,R.id.iv3};

    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide);
        SharedPreferences preferences = getSharedPreferences("first_pref",MODE_PRIVATE);
        boolean isFirstIn = preferences.getBoolean("isFirstIn",true);
        if(isFirstIn == true){
            //第一次进入后修改成false,以后进入就不会重新进入欢迎界面了
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isFirstIn",false);
            editor.commit();
        }else{
            Intent intent = new Intent(Guide.this,MainActivity.class);
            startActivity(intent);
            finish();
        }

        initViews();
        initDots();

    }

    void initDots(){
        dots = new ImageView[views.size()];
        for(int i=0; i<views.size(); i++){
            dots[i]=(ImageView)findViewById(ids[i]);
        }
    }

    private void initViews(){
        LayoutInflater inflater = LayoutInflater.from(this);
        views = new ArrayList<View>();
        View view1 = inflater.inflate(R.layout.page1,null);
        View view2 = inflater.inflate(R.layout.page2,null);
        View view3 = inflater.inflate(R.layout.page3,null);
        views.add(view1);
        views.add(view2);
        views.add(view3);
        //views.add(inflater.inflate(R.layout.page1,null));
        //views.add(inflater.inflate(R.layout.page2,null));
        //views.add(inflater.inflate(R.layout.page3,null));
        vpAdapter = new ViewPagerAdapter(views,this);
        vp = (ViewPager)findViewById(R.id.viewpager);
        vp.setAdapter(vpAdapter);
        vp.addOnPageChangeListener(this);

        mButton = (Button) view3.findViewById(R.id.enter_btn);
        mButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(Guide.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int i) {
        for(int a=0;a<ids.length;a++){
            if(a == i){
                dots[a].setImageResource(R.drawable.page_indicator_focused);
            }else {
                dots[a].setImageResource(R.drawable.page_indicator_unfocused);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
