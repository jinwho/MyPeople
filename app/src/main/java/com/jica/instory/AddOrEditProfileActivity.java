package com.jica.instory;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jica.instory.database.AppDatabase;
import com.jica.instory.database.Profile;
import com.jica.instory.database.ProfileDao;

public class AddOrEditProfileActivity extends AppCompatActivity {

    //DB
    private ProfileDao profileDao = AppDatabase.getInstance(this).profileDao();
    //사진,레이팅바,이름,한줄평,버튼
    private ImageView profile_pic;
    private RatingBar ratingBar;
    private TextView name, comment;

    // 수정일 경우 ID가 필요하다.
    // if isEdit = true 수정한다.
    private int id;
    private boolean isEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addedit_profile);

        //뒤로 가기 버튼
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        //actionBar.setDisplayUseLogoEnabled(true);
        //actionBar.setDisplayShowHomeEnabled(true);

        //view 가져오기
        ratingBar = findViewById(R.id.ratingBar);
        name = findViewById(R.id.name);
        comment = findViewById(R.id.comment);
        profile_pic = findViewById(R.id.profile_pic);

        //사진공간이 클릭 되었을 때
        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                //카메라나 갤러리를 호출 한다.
                AlertDialog.Builder builder = new AlertDialog.Builder(AddOrEditProfileActivity.this);
                builder.setTitle(R.string.get_photo).setItems(R.array.photo_choice, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            //카메라 인텐트
                            case 0:
                            //앨범 인텐트
                            case 1:
                        }
                    }
                });
                builder.create().show();
            }
        });

        //수정하기 위해 호출되었다면 extra 데이터가 존재하므로 해당 프로필 객체를 불러온다.
        Intent intent = getIntent();
        if (intent.hasExtra("id")) {
            id = intent.getIntExtra("id", -1);
            Profile profile = profileDao.get(id);
            //가져온 프로필의 값을 view에 할당
            //여기서 사진도 가져와서 보여주어야 한다.
            ratingBar.setRating(profile.getRating());
            name.setText(profile.getName());
            comment.setText(profile.getComment());
            //수정하려는 의도이므로 true
            isEdit = true;
        } else {
            isEdit = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_addedit_menu, menu);
        return true;
    }

    //메뉴 : 뒤로가기 클릭하면 액티비티 종료함
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.add_edit_save:
                onClickConfirm();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //사진을 가져오면 프로필 사진에 사진을 보여준다.
    }

    void onClickConfirm(){
        Profile profile = new Profile();
        String fName = name.getText().toString();
        //이름이 비었다면 토스트메세지를 띄운다.
        if (fName.equals("")) {
            Toast.makeText(getApplicationContext(), "name field is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        //view 에서 값을 얻어와 프로필 객체를 생성한다.

        profile.setRating((int) ratingBar.getRating());
        profile.setName(fName);
        profile.setComment(comment.getText().toString());

        //만약 수정하려고 한다면 삽입대신 업데이트한다.
        if (isEdit) {
            //업데이트 일때만 id를 사용한다.
            profile.setPid(id);
            profileDao.updateAll(profile);
        } else {
            profileDao.insertAll(profile);
        }
        //프로필 id를 얻어서 숫자 폴더안에 profile.jpg 사진을 저장한다.
        //사진이 없다면 그대로 냅둔다.
        finish();
    }


}
