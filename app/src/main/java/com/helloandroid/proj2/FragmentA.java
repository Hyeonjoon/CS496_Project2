package com.helloandroid.proj2;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.helloandroid.proj2.data.CallInfo;
import com.helloandroid.proj2.data.CallList;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

public class FragmentA extends Fragment {

    CallAdapter adapter;
    private String[] items = {"연락처 추가","이름 수정","번호 수정", "연락처 삭제"};

    Button button;
    Button button2;

    String fname;
    String fnumber;
    String fcode;
    int user;
    //String user = "test2";

    ListView listView;

    public static FragmentA newInstance(){
        FragmentA fragmentA = new FragmentA();
        return fragmentA;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            int myId = bundle.getInt("index");
            user = myId;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_a, container, false);

        listView = (ListView) v.findViewById(R.id.listView);

        Button button = (Button) v.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestMovielist();
            }
        });

        Button button2 = (Button) v.findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chList();

            }
        });

        if (AppHelper.requestQueue == null) {
            AppHelper.requestQueue = Volley.newRequestQueue(getContext());
        }


        return v;
    }

    public void chList(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("수정 방법");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                String str = "0";
                if (i == 0) {
                    str = "0";
                } else if (i == 1) {
                    str = "1";
                } else if (i == 2) {
                    str = "2";
                } else if (i == 3) {
                    str = "3";
                }
                if (i == 0 || i == 1 || i == 2 || i == 3) {
                    fcode = str;
                    dialog.dismiss();

                    chName();
                }

            }
        });

        builder.show();

    }

    public void chName(){

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("변경할 이름을 입력하세요.");

        final EditText newname = new EditText(getActivity());
        newname.setHint("이름");
        alert.setView(newname);

        alert.setPositiveButton("Next", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = newname.getText().toString();
                fname = name;

                dialog.dismiss();

                chNumber();
            }
        });

        alert.setNeutralButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.show();

    }

    public void chNumber () {

        AlertDialog.Builder alert2 = new AlertDialog.Builder(getActivity());
        alert2.setTitle("변경할 전화번호를 입력하세요.");

        final EditText newnumber = new EditText(getActivity());
        newnumber.setHint("전화번호");
        alert2.setView(newnumber);

        alert2.setPositiveButton("Next", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String number = newnumber.getText().toString();
                fnumber = number;

                dialog.dismiss();

                new JSONTask().execute("http://143.248.140.106:3780/postcontact?user=" + user);
                requestMovielist();
                Toast.makeText(getContext(),"연락처 수정 완료",Toast.LENGTH_SHORT).show();
            }
        });

        alert2.setNeutralButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }});

        alert2.show();

    }

    public class JSONTask extends AsyncTask<String, String, String> {

        String name = fname;
        String number = fnumber;
        String code = fcode;

        @Override
        protected String doInBackground(String... urls) {
            try{
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("name", name);
                jsonObject.accumulate("number", number);
                jsonObject.accumulate("code", code);


                HttpURLConnection con = null;
                BufferedReader reader = null;

                try {
                    URL url = new URL(urls[0]);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");//POST방식으로 보냄
                    con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                    con.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송
                    con.setRequestProperty("Accept", "text/json");//서버에 response 데이터를 html로 받음
                    con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                    con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미

                    con.connect();

                    OutputStream outStream = con.getOutputStream();
                    //버퍼를 생성하고 넣음
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                    writer.write(jsonObject.toString());
                    Log.d(jsonObject.toString(), "debug");
                    writer.flush();
                    writer.close();

                    con.getResponseCode();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    class CallAdapter extends BaseAdapter {
        ArrayList<CallItem> items = new ArrayList<CallItem>(); //데이터 넣고 빼고 할 것

        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(CallItem item){
            items.add(item);
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) { //데이터 관리하는 어댑터가 화면에 보여질 각각의 아이템을 위한 뷰를 만듦 ->레이아웃으로 구성되어야
            CallItemView view = null;
            if (convertView == null) {
                view = new CallItemView(getContext());
            } else {
                view = (CallItemView) convertView;
            }

            CallItem item = items.get(position);
            view.setName(item.getName());
            view.setMobile(item.getMobile());
            view.setImage(item.getResId());

            return view;
        }
    }

    public void requestMovielist() {
        String url = "http://" + AppHelper.host + ":" + AppHelper.port + "/getcontact?user=" + user;

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        processResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "ERROR! " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        request.setShouldCache(false);
        AppHelper.requestQueue.add(request);
    }

    public void processResponse(String response){
        Gson gson = new Gson();

        adapter = new CallAdapter();
        CallList callList = gson.fromJson(response, CallList.class);
        for (int i=0; i < callList.contact.size(); i++) {
            CallInfo callInfo = callList.contact.get(i);
            adapter.addItem(new CallItem(callInfo.name, callInfo.number, R.drawable.ic_launcher_background));
        }

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CallItem item = (CallItem) adapter.getItem(position);
                Toast.makeText(getContext(), item.getName(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
