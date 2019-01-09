package com.helloandroid.proj2;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
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
import com.helloandroid.proj2.data.ImageInfo;
import com.helloandroid.proj2.data.ImageList;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;


public class FragmentB extends Fragment {

    FragmentB.CallAdapter adapter;

    GridView gridView;
    ImageView imageView;

    String fname;
    String fencoded;
    String fcode;
    int user;
    //String user;


    public static FragmentB newInstance(){
        FragmentB fragmentB = new FragmentB();
        return fragmentB;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            int myId = bundle.getInt("index");
            user = myId;
            //user = "test";
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_b, container, false);

        gridView = (GridView) v.findViewById(R.id.gridView);

        Button button = (Button) v.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fcode = "0";

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);

                //new JSONTask().execute("http://143.248.140.106:3780/postimage?user=test");
            }
        });

        Button button2 = (Button) v.findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fcode = "1";

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);

                new JSONTask().execute("http://143.248.140.106:3780/postimage?user=" + user);
            }
        });

        Button button3 = (Button) v.findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestMovielist();
            }

        });

        if (AppHelper.requestQueue == null) {
            AppHelper.requestQueue = Volley.newRequestQueue(getContext());
        }
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        // Check which request we're responding to
        if (requestCode == 1) {
            try {
                // 선택한 이미지에서 비트맵 생성
                fname = getImageNameToUri(data.getData());
                InputStream in = getActivity().getContentResolver().openInputStream(data.getData());
                Bitmap img = BitmapFactory.decodeStream(in);
                in.close();
                // 이미지 표시
                //imageView.setImageBitmap(img);
                // 이미지 base64 encoding.
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                img.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();

                fencoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

            } catch (Exception e) {
                e.printStackTrace();
            }
            new JSONTask().execute("http://143.248.140.106:3780/postimage?user=" + user);
        }
    }

    public String getImageNameToUri(Uri data)
    {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader cursorLoader = new CursorLoader(getContext(),data,proj,null,null,null);
        Cursor cursor = cursorLoader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        /*String imgPath = cursor.getString(column_index);
        String imgName = imgPath.substring(imgPath.lastIndexOf("/")+1);*/

        return cursor.getString(column_index);
    }

    public class JSONTask extends AsyncTask<String, String, String> {

        String name = fname; //이미지 이름
        String number = fencoded; //인코딩된 이미지 파일
        String code = fcode; //추가인지 삭제인지

        @Override
        protected String doInBackground(String... urls) {
            try{
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("name", name);
                jsonObject.accumulate("content", number);
                jsonObject.accumulate("code", code);
                Log.d(name, "aaa");
                Log.d(number, "aaa");

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
        ArrayList<ImageItem> items = new ArrayList<ImageItem>(); //데이터 넣고 빼고 할 것

        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(ImageItem item){
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
            ImageItemView view = null;
            if (convertView == null) {
                view = new ImageItemView(getContext());
            } else {
                view = (ImageItemView) convertView;
            }

            ImageItem item = items.get(position);
            view.setbt(item.getBt());

            return view;
        }
    }

    public void requestMovielist() {
        String url = "http://" + AppHelper.host + ":" + AppHelper.port + "/getimage?user=" + user;

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

        adapter = new FragmentB.CallAdapter();
        ImageList imageList = gson.fromJson(response, ImageList.class);
        Log.d(response, "asdfgh");

        for (int i=0; i < imageList.image.size(); i++) {
            ImageInfo imageInfo = imageList.image.get(i);


            byte[] decodedString = Base64.decode(imageInfo.content, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            adapter.addItem(new ImageItem(decodedByte));
        }

        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageItem item = (ImageItem) adapter.getItem(position);
            }
        });
    }

}


