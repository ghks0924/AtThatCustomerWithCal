package com.example.atthatcustomerwithcal;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class EachMember extends Activity_Base implements View.OnClickListener {

    //Log.d TAG
    String TAG_EachMember = "tag_eachmember";
    int REQUEST_EDIT = 9999;

    //기본적인 뷰 요소 생성
    ImageView cameraIv, downIv, upIv;
    TextView nameTv, noShowTv, numberTv, gradeTv, pointTv;
    Boolean img_isClicked = false;
    Button extraBtn, loadImageBtn;
    RelativeLayout historyRl;
    ListView historyListView;
    EditText memoEt;//메모장
    String memoContents; //메모를 저장하는 변수4

    boolean historyIsVisible;

    TextView title;

    //이전 액티비티에서 받아오는 정보 받을 객체
    String customer_name;
    String customer_number;
    int customer_noShowCount;

    //고객정보 받아오는 ArrayList
    ArrayList<Customers> eachCustomerData = new ArrayList<>();
    //고객의 예약 이력을 받아오는 ArrayList
    ArrayList<Events> eventsHistory = new ArrayList<>();
    MyAdapter adapter;

    //Camera 관련 요소
    Uri photoURI;
    static final int REQUEST_TAKE_PHOTO = 1;
    //이미지 요소
    ArrayList<PhotoContent> photoContentArrayList = new ArrayList<>();
    ArrayList<Bitmap> thumbnailArrayList = new ArrayList<>();
    //그리드뷰
    GridView gridView;
    customGridAdapter customGridAdapter;

    //백그라운드 실행 AsyncTask
//    BackgroundTask task;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_each_member2);

        getDataFromPrevActivity();
        loadEachCustomersData();
//        //사진 정보 받아오기(외부 저장소에 저장된)
        getImagesData();
//        //썸네일 데이터
        getThumbnailData();
        //고객별 예약 이력 받아오기
        eventsHistory = collectEventByName(customer_name, customer_number);
        Collections.sort(eventsHistory, comparator_events_date);

        //button 요소
        cameraIv = findViewById(R.id.cameraIv);
        cameraIv.setOnClickListener(this);
        extraBtn = findViewById(R.id.extraBtn);
        extraBtn.setOnClickListener(this);
        loadImageBtn = findViewById(R.id.loadImageBtn);
        loadImageBtn.setOnClickListener(this);
        historyRl = findViewById(R.id.historyRl);
        historyRl.setOnClickListener(this);
        downIv = findViewById(R.id.downArrowIv);
        upIv = findViewById(R.id.upArrowIv);
        //listview 요소
        historyListView = findViewById(R.id.historyListView);
        adapter = new MyAdapter(this);
        historyListView.setAdapter(adapter);
        //아이템 클릭 리스너, ItemClickListenr
        historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ShowEventMemo.class);
                intent.putExtra("reserv_memo", eventsHistory.get(position).SHORTMEMO);
                intent.putExtra("material_memo", eventsHistory.get(position).MATERIALMEMO);
                intent.putExtra("surgery_memo",eventsHistory.get(position).CONTENTMEMO);
                startActivity(intent);
            }
        });

        // toolbar
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        title = findViewById(R.id.title);
        title.setText(customer_name);

        noShowTv = findViewById(R.id.noShowTv);
        noShowTv.setText(String.valueOf(customer_noShowCount));
        numberTv = findViewById(R.id.numberTv);
        numberTv.setText(eachCustomerData.get(0).NUMBER);
        numberTv.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        numberTv.setTextColor(Color.BLACK);
        numberTv.setOnClickListener(this);
        gradeTv = findViewById(R.id.gradeTv);
        gradeTv.setText("방문횟수");
        gradeTv.setTextColor(Color.BLACK);
        pointTv = findViewById(R.id.pointTv);
        pointTv.setText(eachCustomerData.get(0).POINT + " 원");

        //메모장
        memoEt = findViewById(R.id.eachMember_memoEt);
        memoEt.setText(eachCustomerData.get(0).MEMO);

        //그리드뷰
        gridView = findViewById(R.id.gridview);
        customGridAdapter = new customGridAdapter(this);
        gridView.setAdapter(customGridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), PhotoGalleryActivity.class);
                startActivity(intent);
            }
        });


        //뒤로가기 버튼, 디폴트값임
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //앱 제목 없애기
        getSupportActionBar().setDisplayShowTitleEnabled(false);


    } //end of onCreate

    //뒤로가기 버튼에 기능 추가
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { //toolbar의 back키 눌렀을 때 동작

                SaveMemo(customer_name, customer_number, memoEt.getText().toString());
                finish();

                return true;

            }

            case R.id.action_btn1:
                Intent intent = new Intent(getApplicationContext(), EditMemberData.class);
                intent.putExtra("customername", customer_name);
                intent.putExtra("number", customer_number);
                startActivityForResult(intent, REQUEST_EDIT);
                return true;


        }
        return super.onOptionsItemSelected(item);
    }

    //toolbar에 버튼 추가하려고
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menueachmember, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.numberTv:
                ShowAlertDialogWithListview();
                break;
            case R.id.cameraIv:
                dispatchTakePictureIntent();
                break;

            case R.id.loadImageBtn:
                /* 갤러리에서 사진 클릭하면 파읿명 수정해서 불러오기 해야하고
                어댑터노는 noti해야함함
                 */
                break;

            case R.id.extraBtn:

                CustomDialog_HowToAddEvents dialog_howToAddEvents = new CustomDialog_HowToAddEvents(EachMember.this);
                dialog_howToAddEvents.setDialogListener(new CustomDialog_HowToAddEvents.CustomDialogListener() {
                    @Override
                    public void onPositiveClicked() {
                        Intent intent = new Intent(getApplicationContext(), Add_Event_FromMemList.class);
                        intent.putExtra("customer_name", customer_name);
                        intent.putExtra("customer_number", customer_number);
                        intent.putExtra("retouch", "");
                        startActivity(intent);
                    }

                    @Override
                    public void onNegativeClicked() {
                        Intent intent = new Intent(getApplicationContext(), Add_Event_FromMemList_retouch.class);
                        intent.putExtra("customer_name", customer_name);
                        intent.putExtra("customer_number", customer_number);
                        intent.putExtra("retouch", "리터치");
                        startActivity(intent);

                    }
                });
                dialog_howToAddEvents.show();


                break;

            case R.id.historyRl:
                if (!historyIsVisible) {
                    historyIsVisible = true;
                    downIv.setVisibility(View.INVISIBLE);
                    upIv.setVisibility(View.VISIBLE);
                    historyListView.setVisibility(View.VISIBLE);
                } else {
                    historyIsVisible = false;
                    downIv.setVisibility(View.VISIBLE);
                    upIv.setVisibility(View.INVISIBLE);
                    historyListView.setVisibility(View.GONE);
                }
        }

    }

    //======================메모 관련======================

    @Override
    public void onBackPressed() {
        if (memoEt.isFocused()) {
            SaveMemo(customer_name, customer_number, memoEt.getText().toString());
            memoEt.clearFocus();
        } else {
            finish();
        }
    }

    //다른곳 클릭시 키보드 내리기고 저장하기
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View focusView = getCurrentFocus();
        if (focusView != null) {
            Rect rect = new Rect();
            focusView.getGlobalVisibleRect(rect);
            int x = (int) ev.getX(), y = (int) ev.getY();
            if (!rect.contains(x, y)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
                SaveMemo(customer_name, customer_number, memoEt.getText().toString());
                focusView.clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }


    //이전 액티비티에서 받아오는 정보
    private void getDataFromPrevActivity() {
        Intent intent = getIntent();
        customer_name = intent.getStringExtra("customername");
        customer_number = intent.getStringExtra("number");
        customer_noShowCount = intent.getIntExtra("noshow", 0);
    }

    //회원정보 불러오는 메서드
    public ArrayList<Customers> loadEachCustomersData() {
        eachCustomerData.clear();

        Log.d("tag", ownername);

        ArrayList<Customers> arrayList = new ArrayList<>();
        dbOpenHelper_customer = new DBOpenHelper_Customer(EachMember.this);
        SQLiteDatabase database = dbOpenHelper_customer.getReadableDatabase();
        Cursor cursor = dbOpenHelper_customer.ReadEachMember(customer_name, customer_number, database);
        while (cursor.moveToNext()) {
            String ownername2 = cursor.getString(cursor.getColumnIndex(DBStructure_Customer.OWNER_NAME));
            String customername = cursor.getString(cursor.getColumnIndex(DBStructure_Customer.CUSTOMER_NAME));
            String number = cursor.getString(cursor.getColumnIndex(DBStructure_Customer.NUMBER));
            String grade = cursor.getString(cursor.getColumnIndex(DBStructure_Customer.GRADE));
            String recommend = cursor.getString(cursor.getColumnIndex(DBStructure_Customer.RECOMMEND));
            String point = cursor.getString(cursor.getColumnIndex(DBStructure_Customer.POINT));
            String visit = cursor.getString(cursor.getColumnIndex(DBStructure_Customer.VISIT));
            String memo = cursor.getString(cursor.getColumnIndex(DBStructure_Customer.MEMO));
            String savedate = cursor.getString(cursor.getColumnIndex(DBStructure_Customer.SAVEDATE));
            int noshowcount = cursor.getInt(cursor.getColumnIndex(DBStructure_Customer.NOSHOWCOUNT));
            Customers customers = new Customers(ownername2, customername, number, grade, recommend, point, visit, memo, savedate, noshowcount);
            arrayList.add(customers);

        }
        cursor.close();

        eachCustomerData.addAll(arrayList);

        return eachCustomerData;

    }

    //============================카메라 관련==============================

    //화면간 데이터 전달 결과 정리
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            if (photoURI != null) {
                Bitmap bitmap = loadBitmapFromMediaStoreBy(photoURI);
//                iv.setImageBitmap(bitmap);
                photoURI = null;
                getImagesData();
                getThumbnailData();
                customGridAdapter.notifyDataSetChanged();
            }
        }

        if (requestCode == REQUEST_EDIT && resultCode == RESULT_OK){
            customer_name = data.getStringExtra("customer_name");
            customer_number = data.getStringExtra("customer_number");
            loadEachCustomersData();
            pointTv.setText(eachCustomerData.get(0).POINT + " 원");
            gradeTv.setText("방문횟수");
            title.setText(customer_name);
            numberTv.setText(customer_number);
        }
    }

    private Uri createImageUri(String filename, String mimeType) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, filename);
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, mimeType);

        ContentResolver contentResolver = getContentResolver();
        Uri uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Log.d("photoUri", String.valueOf(MediaStore.Images.Media.EXTERNAL_CONTENT_URI));

        return uri;
    }

    private String newFileName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String fileName = customer_name + "_" + customer_number + "_" + sdf.format(System.currentTimeMillis());

        return fileName + ".jpg";
    }

    private Bitmap loadBitmapFromMediaStoreBy(Uri photoURI) {
        Bitmap image = null;
        ContentResolver contentResolver = getContentResolver();

        try {
            if (Build.VERSION.SDK_INT > 27) {
                ImageDecoder.Source source = ImageDecoder.createSource(contentResolver, photoURI);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                image = MediaStore.Images.Media.getBitmap(contentResolver, photoURI);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            photoURI = createImageUri(newFileName(), "image/*");
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }

    }

    //===========================사진 앨범 관련===========================

    //사진 데이터를 담아두는 클래스 정의
    protected class PhotoContent {
        String ID,
                BUCKET_DISPLAY_NAME,
                DISPLAY_NAME,
                DATE_TAKEN;

        public PhotoContent(String ID, String BUCKET_DISPLAY_NAME, String DISPLAY_NAME, String DATE_TAKEN) {
            this.ID = ID;
            this.BUCKET_DISPLAY_NAME = BUCKET_DISPLAY_NAME;
            this.DISPLAY_NAME = DISPLAY_NAME;
            this.DATE_TAKEN = DATE_TAKEN;
        }

    }

    //사진 데이터 받아오기
    protected ArrayList<PhotoContent> getImagesData() {

        photoContentArrayList.clear();

        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.MediaColumns.DATA
        };

        String[] selectionArgs = {customer_name + "%"};

        ContentResolver contentResolver = getContentResolver();
        Cursor imageCursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection
                , MediaStore.Images.Media.DISPLAY_NAME + " LIKE ? ", selectionArgs, MediaStore.Images.Media.DATE_TAKEN, null);

        if (imageCursor == null || !imageCursor.moveToFirst()) {
            Log.e(TAG_EachMember, "cursor null or cursor is empty");
        } else {
            do {
                String ID = imageCursor.getString(0);
                String BUCKET_DISPLAY_NAME = imageCursor.getString(1);
                String DISPLAY_NAME = imageCursor.getString(2);
                String DATE_TAKEN = imageCursor.getString(3);
                String DATA = imageCursor.getString(4);

//                String refinedID = convertFilename(ID);
                //사진 정보 받아오기
                photoContentArrayList.add(new PhotoContent(ID, BUCKET_DISPLAY_NAME, DISPLAY_NAME, DATE_TAKEN));

                Log.d(TAG_EachMember, "URI : " + MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                Log.d(TAG_EachMember, "ID : " + ID);
                Log.d(TAG_EachMember, "BUCKET_DISPLAY_NAME : " + BUCKET_DISPLAY_NAME);
                Log.d(TAG_EachMember, "DISPLAY_NAME : " + DISPLAY_NAME);
                Log.d(TAG_EachMember, "DATE_TAKEN : " + DATE_TAKEN);
                Log.d(TAG_EachMember, "DATA : " + DATA);

//            //사진 각각의 파일 Bitmap 구하기
//            String contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI + "/" + imageCursor.getString(0);
//            File imageFile = new File(contentUri);
//
//            try {
//                InputStream is = contentResolver.openInputStream(Uri.parse(contentUri));
//
//                if (is != null) {
//                    Bitmap bitmap = BitmapFactory.decodeStream(is);
//                    thumbnailArrayList.add(bitmap);
//                    is.close();
//                }
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            } while (imageCursor.moveToNext());
        }

        imageCursor.close();

        Log.d(TAG_EachMember, photoContentArrayList.size() + "??");

        return photoContentArrayList;

    }

    //파일에 쓸 수 없는 문자로 연락처에 저장한경우를 위한 파일명 변경 메서드
    public String convertFilename(String orgnStr) {
        String restrictChars = "|\\\\?*<\":>/";
        String regExpr = "[" + restrictChars + "]";

        // 파일명으로 사용 불가능한 특수문자 제거
        String tmpStr = orgnStr.replaceAll(regExpr, "_");

        // 공백문자 "_"로 치환
        return tmpStr;
    }

    public void getThumbnailData() {
        String contentUri;
        for (int i = 0; i < photoContentArrayList.size(); i++) {
            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI + "/" + photoContentArrayList.get(i).ID;
            try {
                InputStream is = getContentResolver().openInputStream(Uri.parse(contentUri));

                if (is != null) {
                    int degree = getExifOrientation(contentUri);
                    Log.d(TAG_EachMember, "degree: " + degree);
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    bitmap = rotateBitmap(bitmap, degree);
                    thumbnailArrayList.add(bitmap);
                    is.close();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    //회전 각도 구하기
    private int getExifOrientation(String filePath) {

        ExifInterface exif = null;

        try {
            exif = new ExifInterface(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (exif != null) {
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            if (orientation != -1) {
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        return 90;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        return 180;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        return 270;
                }
            }
        }

        return 0;
    }

    //이미지 회전하기
    private Bitmap getRotatedBitmap(Bitmap bitmap, int degree) {
        if (degree != 0 && bitmap != null) {
            Matrix matrix = new Matrix();
            matrix.setRotate(degree, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);

            try {
                Bitmap tmpBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                if (bitmap != tmpBitmap) {
                    bitmap.recycle();
                    bitmap = tmpBitmap;
                }
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }

        return bitmap;
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }


    //=================앨범 커스텀 그리드뷰====================

    class RowHolder2 {
        TextView dateTvHolder;
        ImageView ivHolder;
    }

    class customGridAdapter extends ArrayAdapter {
        LayoutInflater lnf;

        public customGridAdapter(android.app.Activity context) {
            super(context, R.layout.single_cell_layout_album, photoContentArrayList);
            lnf = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return photoContentArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return photoContentArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            RowHolder2 viewHolder;
            if (convertView == null) {
                convertView = lnf.inflate(R.layout.single_cell_layout_album, parent, false);
                viewHolder = new RowHolder2();

                viewHolder.dateTvHolder = convertView.findViewById(R.id.dateTv);
                viewHolder.ivHolder = convertView.findViewById(R.id.thumbnailIv);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (RowHolder2) convertView.getTag();
            }

            String dateOfPhoto = photoContentArrayList.get(position).DISPLAY_NAME;
            String target = "_20";
            int tatgetNum = dateOfPhoto.indexOf(target);
            String dateString = dateOfPhoto.substring(tatgetNum + 1, tatgetNum + 9);

            viewHolder.dateTvHolder.setText(dateString.substring(0, 4) + "-"
                    + dateString.substring(4, 6) + "-"
                    + dateString.substring(6));

            viewHolder.ivHolder.setImageBitmap(thumbnailArrayList.get(position));

            return convertView;
        }
    }

    //=================방문이력 커스텀 리스트뷰=================

    class RowHolder {
        TextView reserveTvHolder;
        TextView completeTvHolder;
    }

    class MyAdapter extends ArrayAdapter {
        LayoutInflater lnf;

        public MyAdapter(android.app.Activity context) {
            super(context, R.layout.custom_listview_history_row, eventsHistory);
            lnf = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return eventsHistory.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return eventsHistory.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            RowHolder viewHolder;
            if (convertView == null) {
                convertView = lnf.inflate(R.layout.custom_listview_history_row, parent, false);
                viewHolder = new RowHolder();

                viewHolder.reserveTvHolder = convertView.findViewById(R.id.reserve_tv);
                viewHolder.completeTvHolder = convertView.findViewById(R.id.complete_tv);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (RowHolder) convertView.getTag();
            }

            viewHolder.reserveTvHolder.setText(eventsHistory.get(position).DATE
                    + " " + eventsHistory.get(position).TIME);

            //=== 리터치인지 아닌지 나누기
            if (eventsHistory.get(position).RETOUCH.equals("리터치")) {
                if (eventsHistory.get(position).COMPLETE == true && eventsHistory.get(position).NOSHOW == true) {
                    viewHolder.completeTvHolder.setText("리터치_" + "노쇼");
                } else if (eventsHistory.get(position).COMPLETE == true && eventsHistory.get(position).NOSHOW == false) {
                    viewHolder.completeTvHolder.setText("리터치_" + "시술완료");
                } else if (eventsHistory.get(position).COMPLETE == false) {
                    viewHolder.completeTvHolder.setText("리터치_" + "시술 전");
                }
            } else {
                if (eventsHistory.get(position).COMPLETE == true && eventsHistory.get(position).NOSHOW == true) {
                    viewHolder.completeTvHolder.setText("노쇼");
                } else if (eventsHistory.get(position).COMPLETE == true && eventsHistory.get(position).NOSHOW == false) {
                    viewHolder.completeTvHolder.setText("시술완료");
                } else if (eventsHistory.get(position).COMPLETE == false) {
                    viewHolder.completeTvHolder.setText("시술 전");
                }
            }


            return convertView;
        }
    }

//    //================================BackgroundTask================================
//    class BackgroundTask extends AsyncTask<Boolean, Boolean, Boolean> {
//
//        boolean isDone;
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            isDone = false;
//            progressBar.setVisibility(View.VISIBLE);
//        }
//
//        @Override
//        protected Boolean doInBackground(Boolean ... values) {
//            while (isDone == false){
//                getImagesData();
//                getThumbnailData();
//                if (value >= 100){
//
//                    break;
//                } else {
//                    publishProgress(value);
//                }
//
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            return value;
//        }
//
//        @Override
//        protected void onProgressUpdate(Boolean... values) {
//            super.onProgressUpdate(values);
//            progressBar.setProgress(values[0].intValue());
//        }
//
//        @Override
//        protected void onPostExecute(Boolean integer) {
//            super.onPostExecute(integer);
//
//            progressBar.setVisibility(View.GONE);
//        }
//
//        @Override
//        protected void onCancelled() {
//            super.onCancelled();
//        }
//    }

    //=================================전화번호 클릭시 기능=================================
    public void ShowAlertDialogWithListview() {
        List<String> numberMethod = new ArrayList<String>();
        numberMethod.add("전화걸기 " + "[" + customer_name + "]");
        numberMethod.add("SMS 보내기 " + "[" + customer_name + "]");
        numberMethod.add("복사");

        //Create sequence of items
        final CharSequence[] Animals = numberMethod.toArray(new String[numberMethod.size()]);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(customer_number);
        dialogBuilder.setItems(Animals, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                String selectedText = Animals[item].toString(); //Selected item in listview
                if (selectedText.startsWith("전화")) {
                    Uri uri = Uri.parse("tel:" + customer_number);
                    Intent intent = new Intent(Intent.ACTION_DIAL, uri);

                    startActivity(intent);
                } else if (selectedText.startsWith("SMS")) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    Uri uri = Uri.parse("sms:" + customer_number);

                    intent.setData(uri);
//                    intent.putExtra("sms_body", customer_number);

                    startActivity(intent);
                } else {
                    //클립보드에 문자열 저장하기
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("number data", customer_number);
                    clipboard.setPrimaryClip(clipData);

                    Toast toast = Toast.makeText(getApplicationContext(), "복사하였습니다", Toast.LENGTH_SHORT);

                    toast.setGravity(Gravity.TOP, 0, 500);

                    toast.show();
                }
            }
        });
        //Create alert dialog object via builder
        AlertDialog alertDialogObject = dialogBuilder.create();
        //Show the dialog
        alertDialogObject.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        eventsHistory = collectEventByName(customer_name, customer_number);
        Collections.sort(eventsHistory, comparator_events_date);
        adapter = new MyAdapter(this);
        historyListView.setAdapter(adapter);
    }
}