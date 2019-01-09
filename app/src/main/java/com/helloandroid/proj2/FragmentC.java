package com.helloandroid.proj2;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.SparseBooleanArray;
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
import com.helloandroid.proj2.data.MemoInfo;
import com.helloandroid.proj2.data.MemoList;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class FragmentC extends Fragment {

    MemoAdapter adapter;

    Button button;
    Button button2;
    ListView listView;
    MemoList memoCache;

    String fmemo;
    String fcode;
    int fnum = 0;
    int user;

    public static FragmentC newInstance(){
        FragmentC fragmentC = new FragmentC();
        return fragmentC;
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
        View v = inflater.inflate(R.layout.fragment_c, container, false);

        listView = (ListView) v.findViewById(R.id.listView);

        Button button = (Button) v.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fcode = "1";

                MemoList memoRemove;



                SparseBooleanArray booleans = listView.getCheckedItemPositions();
                for (int i = 0; i < listView.getCheckedItemCount(); i++) {
                    if (booleans.get(i)) {
                        fcode = "2";

                        MemoInfo memoInfo2 = memoCache.todo.get(i);
                        fmemo = memoInfo2.content;
                        fnum = Integer.parseInt(memoInfo2.number);

                        ////i번째 리스트 내용 불러오기
                        new FragmentC.JSONTask().execute("http://143.248.140.106:3780/posttodo?user=" + user);
                    }
                }


                requestMemolist();
            }
        });

        Button button2 = (Button) v.findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fcode = "0";
                chMemo();
            }
        });

        if (AppHelper.requestQueue == null) {
            AppHelper.requestQueue = Volley.newRequestQueue(getContext());
        }

        return v;
    }


    public void chMemo () {

        AlertDialog.Builder alert2 = new AlertDialog.Builder(getActivity());
        alert2.setTitle("내용을 입력하세요.");

        final EditText newmemo = new EditText(getActivity());
        newmemo.setHint("내용");
        alert2.setView(newmemo);

        alert2.setPositiveButton("Next", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String memo = newmemo.getText().toString();
                fmemo = memo;

                dialog.dismiss();
                fnum += 1;

                new FragmentC.JSONTask().execute("http://143.248.140.106:3780/posttodo?user=" + user);
                Toast.makeText(getContext(), "YOU CAN DO IT!", Toast.LENGTH_SHORT).show();
                requestMemolist();
            }
        });

        alert2.setNeutralButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert2.show();

    }

    public class JSONTask extends AsyncTask<String, String, String> {

        String memo = fmemo;
        String code = fcode; //0-추가 1-삭제
        String number = String.valueOf(fnum);

        @Override
        protected String doInBackground(String... urls) {
            try{
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("content", memo);
                jsonObject.accumulate("code", code);
                jsonObject.accumulate("number", number);

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

    class MemoAdapter extends BaseAdapter {
        ArrayList<MemoItem> items = new ArrayList<MemoItem>(); //데이터 넣고 빼고 할 것

        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(MemoItem item){
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
            MemoItemView view = null;

            if (convertView == null) {
                view = new MemoItemView(getContext());
            } else {
                view = (MemoItemView) convertView;
            }

            MemoItem item = items.get(position);
            view.setMemo(item.getMemo());
            view.chCheck();

            return view;
        }
    }

    public void requestMemolist() {
        String url = "http://" + AppHelper.host + ":" + AppHelper.port + "/gettodo?user=";
        url += user;

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

        adapter = new MemoAdapter();
        MemoList memoList = gson.fromJson(response, MemoList.class);
        memoCache = memoList;
        for (int i=0; i < memoList.todo.size(); i++) {
            MemoInfo memoInfo = memoList.todo.get(i);
            adapter.addItem(new MemoItem(memoInfo.content, memoInfo.number));
        }

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MemoItem item = (MemoItem) adapter.getItem(position);
                fmemo = item.getMemo();
                fnum = Integer.parseInt(item.getNum());

                /*
                AlertDialog.Builder alert3 = new AlertDialog.Builder(getActivity());
                alert3.setTitle("TO DO");

                final EditText newmemo = new EditText(getActivity());
                newmemo.setText(fmemo);
                alert3.setView(newmemo);

                alert3.setPositiveButton("Next", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fmemo = newmemo.getText().toString();
                        fcode = "1";

                        dialog.dismiss();
                        new FragmentC.JSONTask().execute("http://143.248.140.106:3780/posttodo?user=" + user);
                        requestMemolist();
                    }
                });

                alert3.setNeutralButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alert3.show();
                */
            }
        });
    }

    public void remove(int i){

    }
}
