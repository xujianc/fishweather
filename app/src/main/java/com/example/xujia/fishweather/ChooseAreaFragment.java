package com.example.xujia.fishweather;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xujia.fishweather.db.City;
import com.example.xujia.fishweather.db.County;
import com.example.xujia.fishweather.db.Province;
import com.example.xujia.fishweather.utils.HttpUtil;
import com.example.xujia.fishweather.utils.Utility;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by xujia on 2017/7/25.
 */

public class ChooseAreaFragment extends Fragment {
    public static final String TAG = "ChooseAreaFragment";

    private static final int LEVEL_PROVINCE = 0;
    private static final int LEVEL_CITY = 1;
    private static final int LEVEL_COUNTY = 2;

    private ProgressDialog dialog;
    private TextView titleText;
    private Button backButton;
    private ListView listView;

    private ArrayAdapter<String> arrayAdapter;
    private List<String> areaList = new ArrayList<>();
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;

    private Province selectedProvince;
    private City selectedCity;

    private int currentLevel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView");
        View view = inflater.inflate(R.layout.choose_area,container,false);
        titleText = (TextView) view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        listView = (ListView) view.findViewById(R.id.list_view);

        arrayAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,areaList);
        listView.setAdapter(arrayAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG,"onActivityCreated");
        LitePal.getDatabase();
        queryProvince();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(position);
                    queryCity();
                } else if (currentLevel == LEVEL_CITY){
                    selectedCity = cityList.get(position);
                    queryCounty();
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTY){
                    queryCity();
                } else if (currentLevel == LEVEL_CITY){
                    queryProvince();
                }
            }
        });
    }

    private void queryCounty() {
        Log.d(TAG,"queryCounty");

        countyList = DataSupport.where("cityId = ?",String.valueOf(selectedCity.getId())).find(County.class);
        Log.d(TAG,"size:"+countyList.size());
        if (countyList.size() > 0){
            titleText.setText(selectedCity.getCityName());
            backButton.setVisibility(View.VISIBLE);
            areaList.clear();
            for (County county : countyList){
                areaList.add(county.getCountyName());
            }
            arrayAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        } else {
            String url = "http://guolin.tech/api/china/" + selectedProvince.getProvinceCode() + "/" + selectedCity.getCityCode();
            Log.d(TAG,"queryCounty:"+url);
            queryFromService(url, LEVEL_COUNTY);
        }
    }

    private void queryCity() {
        Log.d(TAG,"queryCity");

        cityList = DataSupport.where("provinceId = ?",String.valueOf(selectedProvince.getId())).find(City.class);
        if (cityList.size() > 0){
            titleText.setText(selectedProvince.getProvinceName());
            backButton.setVisibility(View.VISIBLE);
            areaList.clear();
            for (City city : cityList){
                areaList.add(city.getCityName());
            }
            arrayAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {
            String url = "http://guolin.tech/api/china/" + selectedProvince.getProvinceCode();
            Log.d(TAG,"queryCity:"+url);
            queryFromService(url,LEVEL_CITY);
        }
    }

    private void queryProvince(){
        Log.d(TAG,"queryProvince");
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);

        provinceList = DataSupport.findAll(Province.class);
        //数据库中有数据，则查询数据库中的数据并显示在listView
        if (provinceList.size() > 0){
            areaList.clear();
            for (Province province : provinceList){
                areaList.add(province.getProvinceName());
            }
            arrayAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        } else {
            //从网络中获取中国所有省市的数据
            String url = "http://guolin.tech/api/china/";
            Log.d(TAG,"queryProvince:"+url);
            queryFromService(url,LEVEL_PROVINCE);
        }
    }

    private void queryFromService(String url, final int type) {
        Log.d(TAG,"queryFromService");
        //显示提示对话框
        showProgressDialog();
        HttpUtil.sendOKHttpRequest(url, new okhttp3.Callback() {
            /**
             * Called when the request could not be executed due to cancellation, a connectivity problem or
             * timeout. Because networks can fail during an exchange, it is possible that the remote server
             * accepted the request before the failure.
             *
             * @param call
             * @param e
             */
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cancelProgressDialog();
                        Toast.makeText(getContext(), "获取数据失败，请稍后再试...", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            /**
             * Called when the HTTP response was successfully returned by the remote server. The callback may
             * proceed to read the response body with {@link Response#body}. The response is still live until
             * its response body is ResponseBody closed. The recipient of the callback may
             * consume the response body on another thread.
             * <p>
             * <p>Note that transport-layer success (receiving a HTTP response code, headers and body) does
             * not necessarily indicate application-layer success: {@code response} may still indicate an
             * unhappy HTTP response code like 404 or 500.
             *
             * @param call
             * @param response
             */
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String responseData = response.body().string();
                Log.d(TAG,"responseData:"+responseData);
                boolean result = false;
                switch (type){
                    case LEVEL_PROVINCE:
                        Log.d(TAG,"parseProvince");
                        result = Utility.handleProvinceResponse(responseData);
                        break;
                    case LEVEL_CITY:
                        Log.d(TAG,"parseCity");
                        result = Utility.handleCityResponse(responseData,selectedProvince.getId());
                        break;
                    case LEVEL_COUNTY:
                        Log.d(TAG,"parseCounty");
                        result = Utility.handleCountyResponse(responseData,selectedCity.getId());
                        Log.d(TAG,"result:"+ result);
                        break;
                    default:
                        break;
                }

                if (result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cancelProgressDialog();
                            switch (type){
                                case LEVEL_PROVINCE:
                                    queryProvince();
                                    break;
                                case LEVEL_CITY:
                                    queryCity();
                                    break;
                                case LEVEL_COUNTY:
                                    queryCounty();
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
                }
            }
        });
    }

    private void showProgressDialog(){
        if (dialog == null) {
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("正在加载...");
            dialog.setCanceledOnTouchOutside(false);
        }
        dialog.show();
    }

    private void cancelProgressDialog(){
        if (dialog != null){
            dialog.dismiss();
        }
    }
}
