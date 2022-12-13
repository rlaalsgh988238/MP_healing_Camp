package org.techtown.healing_camp;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class InsidePlannerActivity  extends AppCompatActivity {
    ArrayList<Memo> memoList;
    String[] memo,campingPlace,dbCampingPlace;
    String stringMemo;

    Button onClickSearch,onClickEditPlanner,onClickBackLayer;
    EditText writeToSearch;
    Button addCancel,addAccept;
    EditText editAddMemo;
    ListView memoListView;

    //메모 수정 다이얼로그
    Button editCancel,editAccept;
    EditText editMemo;
    //타이틀 수정 다이얼로그
    Button editTitleCancel,editTitleAccept;
    EditText editReName;
    //삭제 다이얼로그
    Button deleteCancel,deleteAccept;
    //저장 다이얼로그
    Button saveCancel,saveAccept;
    LinearLayout editBox;
    //메모 삭제 다이얼로그
    Button deleteMemoCancel, deleteMemoAccept;



    @Override
    protected void onCreate(Bundle savedInstanceState){
        //또 다른 주석
        LocalDB memoDB = new LocalDB(this,1);
        SearchDB searchDB = new SearchDB(this,1);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inside_planner_layout);
        final boolean[] flag = {true};
        int position = getIntent().getExtras().getInt("position");

        onClickEditPlanner = findViewById(R.id.onClickEditPlanner);
        onClickSearch = findViewById(R.id.onClickSearch);
        onClickBackLayer = findViewById(R.id.onClickBackLayer);
        writeToSearch = findViewById(R.id.writeToSearch);
        editBox = findViewById(R.id.editBox);
        memoListView = findViewById(R.id.memoListView);

        memoList = new ArrayList<Memo>();
        String[] nullArray = new String[10];
        for(int i = 0;i<10;i++){
            nullArray[i]=null;
        }

        //캠핑장 요약 뷰 불러오기
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        LinearLayout showPickUpPlace = (LinearLayout)findViewById(R.id.showPickUpPlace);
        View viewResultContainer = layoutInflater.inflate(R.layout.search_result_container_layout,showPickUpPlace,false);
        //캠핑장 설명 객체 선언
        TextView nameCampingPlace =  viewResultContainer.findViewById(R.id.nameCampingPlace);
        TextView infoCampingPlace = viewResultContainer.findViewById(R.id.infoCampingPlace);
        TextView urlCampingPlace = viewResultContainer.findViewById(R.id.urlCampingPlace);
        TextView whereCampingPlace = viewResultContainer.findViewById(R.id.whereCampingPlace);
        TextView telCampingPlace = viewResultContainer.findViewById(R.id.telCampingPlace);
        ImageView imageView = viewResultContainer.findViewById(R.id.imageView);

        //기존에 있던 메모 출력
        stringMemo = memoDB.getContent(position);
        if(!stringMemo.equals("null")){
            memo = stringMemo.split("@");
            for(int i=0;i< memo.length;i++){
                memoList.add(new Memo(memo[i]));
            }
        };

        //기존에 있던 캠핑장소 출력
        dbCampingPlace = searchDB.getResult(position);
        //db에 저장된 캠핑장소가 있는지 확인하기 위한 플래그(있으면 1, 없으면 0)
        int intFlag = searchDB.getFlag(position);
        if (intFlag==1){
            nameCampingPlace.setText(dbCampingPlace[0]);
            infoCampingPlace.setText(dbCampingPlace[1]);
            urlCampingPlace.setText(dbCampingPlace[7]);
            whereCampingPlace.setText(dbCampingPlace[4]+" "+dbCampingPlace[5]);
            telCampingPlace.setText(dbCampingPlace[6]);
            Glide.with(viewResultContainer).load(dbCampingPlace[9])
                    .centerCrop()
                    .fallback(R.drawable.no_image)
                    .into(imageView);
            showPickUpPlace.addView(viewResultContainer);
            campingPlace = dbCampingPlace;
            flag[0] = false;
        };

        // 버튼 애니메이션
        View view = findViewById(R.id.memoListView);
        Animation onePointPopUP = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.pop_up_animation_one_point);
        Animation onePointPopUpExit = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.pop_up_animation_one_point_exit);
        Animation popUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.pop_up_animation);
        onClickEditPlanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editBox.getVisibility()==view.INVISIBLE) {
                    editBox.setVisibility(View.VISIBLE);
                    editBox.startAnimation(onePointPopUP);
                    onClickEditPlanner.setVisibility(View.INVISIBLE);
                }
            }
        });
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(editBox.getVisibility()==view.VISIBLE) {
                    editBox.startAnimation(onePointPopUpExit);
                    editBox.setVisibility(View.INVISIBLE);
                    onClickEditPlanner.setVisibility(View.VISIBLE);
                    onClickEditPlanner.startAnimation(popUp);
                }
                return false;
            }
        });

        //검색 이후 캠핑장 선택 결과 받기-db연결 필요
        ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == 1&& flag[0] ==true){
                    campingPlace = PlannerObject.getResult();
                    nameCampingPlace.setText(campingPlace[0]);
                    infoCampingPlace.setText(campingPlace[1]);
                    urlCampingPlace.setText(campingPlace[7]);
                    whereCampingPlace.setText(campingPlace[4]+" "+campingPlace[5]);
                    telCampingPlace.setText(campingPlace[6]);
                    Glide.with(viewResultContainer).load(campingPlace[9])
                            .centerCrop()
                            .fallback(R.drawable.no_image)
                            .into(imageView);
                    showPickUpPlace.addView(viewResultContainer);
                    flag[0] =false;
                }
                else if (result.getResultCode() == 1&& flag[0] !=true){
                    Toast.makeText(getApplicationContext(),"하나의 캠핑 장소만 선택 가능합니다.",Toast.LENGTH_SHORT).show();
                }
            }
        });
        //캠핑장소 자제히 보기(클릭)
        showPickUpPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InsidePlannerActivity.this,DetailViewActivity.class);
                intent.putExtra("dbList",dbCampingPlace);
                startActivity(intent);
            }
        });
        //캠핑장소 삭제
        showPickUpPlace.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                editBox.setVisibility(View.INVISIBLE);
                onClickEditPlanner.setVisibility(View.VISIBLE);
                onClickEditPlanner.startAnimation(popUp);

                View deleteDialogView = getLayoutInflater().inflate(R.layout.planner_delete_camping_place_dialog,null);
                AlertDialog.Builder builder = new AlertDialog.Builder(InsidePlannerActivity.this);
                AlertDialog dialog = builder.create();
                dialog.setView(deleteDialogView);
                Button deleteCancel = deleteDialogView.findViewById(R.id.deleteCancel);
                Button deleteAccept = deleteDialogView.findViewById(R.id.deleteAccept);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                deleteCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                deleteAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        showPickUpPlace.removeAllViews();
                        flag[0] = true;
                    }
                });
                return false;
            }
        });

        //검색버튼
        onClickSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InsidePlannerActivity.this, SearchCompleteActivity.class);
                intent.putExtra("keyWord",writeToSearch.getText().toString());
                startActivityResult.launch(intent);
            }
        });

        //뒤로가기
        onClickBackLayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlannerObject.setResult(null);
                onBackPressed();
            }
        });

        //메모 추가
        MemoAdapter memoAdapter = new MemoAdapter(this,memoList);
        memoListView.setAdapter(memoAdapter);
        findViewById(R.id.onClickAddMemo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editBox.setVisibility(View.INVISIBLE);
                onClickEditPlanner.setVisibility(View.VISIBLE);
                onClickEditPlanner.startAnimation(popUp);

                View editDialogView = getLayoutInflater().inflate(R.layout.planner_add_memo_dialog,null);
                AlertDialog.Builder builder = new AlertDialog.Builder(InsidePlannerActivity.this);
                AlertDialog dialog = builder.create();
                dialog.setView(editDialogView);
                addCancel = editDialogView.findViewById(R.id.addCancel);
                addAccept = editDialogView.findViewById(R.id.addAccept);
                editAddMemo = editDialogView.findViewById(R.id.addMemo);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                addCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                addAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String memo = editAddMemo.getText().toString();
                        if(memo.equals("")){
                            memo="메모를 작성해 주세요";
                        }
                        memoList.add(new Memo(memo));
                        dialog.dismiss();
                        memoAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
        //메모장 메모 수정
        memoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                editBox.setVisibility(View.INVISIBLE);
                onClickEditPlanner.setVisibility(View.VISIBLE);
                onClickEditPlanner.startAnimation(popUp);

                View editDialogView = getLayoutInflater().inflate(R.layout.planner_edit_memo_dialog,null);
                AlertDialog.Builder builder = new AlertDialog.Builder(InsidePlannerActivity.this);
                AlertDialog dialog = builder.create();
                dialog.setView(editDialogView);
                editCancel = editDialogView.findViewById(R.id.editCancel);
                editAccept = editDialogView.findViewById(R.id.editAccept);
                editMemo = editDialogView.findViewById(R.id.editMemo);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                editCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                editAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String memo = editMemo.getText().toString();
                        if(memo.equals("")){
                            memo="메모를 작성해 주세요";
                        }
                        memoList.set(position,new Memo(memo));
                        dialog.dismiss();
                        memoAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
        //메모장 메모 삭제
        memoListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                editBox.setVisibility(View.INVISIBLE);
                onClickEditPlanner.setVisibility(View.VISIBLE);
                onClickEditPlanner.startAnimation(popUp);

                View editDialogView = getLayoutInflater().inflate(R.layout.planner_delete_memo_dialog,null);
                AlertDialog.Builder builder = new AlertDialog.Builder(InsidePlannerActivity.this);
                AlertDialog dialog = builder.create();
                dialog.setView(editDialogView);
                deleteMemoCancel = editDialogView.findViewById(R.id.deleteMemoCancel);
                deleteMemoAccept = editDialogView.findViewById(R.id.deleteMemoAccept);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                deleteMemoCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                deleteMemoAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        memoList.remove(i);
                        dialog.dismiss();
                        memoAdapter.notifyDataSetChanged();
                    }
                });
                return false;
            }
        });
        //메모장 타이틀 수정
        findViewById(R.id.onClickTitleEdit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editBox.setVisibility(View.INVISIBLE);
                onClickEditPlanner.setVisibility(View.VISIBLE);
                onClickEditPlanner.startAnimation(popUp);

                View editDialogView = getLayoutInflater().inflate(R.layout.planner_rename_dialog,null);
                AlertDialog.Builder builder = new AlertDialog.Builder(InsidePlannerActivity.this);
                AlertDialog dialog = builder.create();
                dialog.setView(editDialogView);
                editTitleCancel = editDialogView.findViewById(R.id.reNameCancel);
                editTitleAccept = editDialogView.findViewById(R.id.reNameAccept);
                editReName = editDialogView.findViewById(R.id.editReName);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                editTitleCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                editTitleAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String title = editReName.getText().toString();
                        PlannerObject.setTitle(title);
                        memoDB.updateTitle(title,position);
                        setResult(1);
                        dialog.dismiss();
                        finish();
                    }
                });
            }
        });
        //메모장 삭제
        findViewById(R.id.onClickDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editBox.setVisibility(View.INVISIBLE);
                onClickEditPlanner.setVisibility(View.VISIBLE);
                onClickEditPlanner.startAnimation(popUp);

                View deleteDialogView = getLayoutInflater().inflate(R.layout.planner_delete_dialog,null);
                AlertDialog.Builder builder = new AlertDialog.Builder(InsidePlannerActivity.this);
                AlertDialog dialog = builder.create();
                dialog.setView(deleteDialogView);
                deleteCancel = deleteDialogView.findViewById(R.id.deleteCancel);
                deleteAccept = deleteDialogView.findViewById(R.id.deleteAccept);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                deleteCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                deleteAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setResult(2);
                        dialog.dismiss();
                        finish();
                    }
                });
            }
        });
        //메모장 저장
        findViewById(R.id.onClickSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editBox.setVisibility(View.INVISIBLE);
                onClickEditPlanner.setVisibility(View.VISIBLE);
                onClickEditPlanner.startAnimation(popUp);

                View deleteDialogView = getLayoutInflater().inflate(R.layout.planner_save_dialog,null);
                AlertDialog.Builder builder = new AlertDialog.Builder(InsidePlannerActivity.this);
                AlertDialog dialog = builder.create();
                dialog.setView(deleteDialogView);
                saveCancel = deleteDialogView.findViewById(R.id.saveCancel);
                saveAccept = deleteDialogView.findViewById(R.id.saveAccept);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                saveCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                saveAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        memo = new String[memoList.size()];
                        for(int i=0;i<memo.length;i++){
                            memo[i]=memoList.get(i).getMemo();
                        }
                        PlannerObject.setResult(campingPlace);
                        //db에 저장해야 할 정보(타이틀, 선택한 캠핑장 정보, 메모 내용)
                        stringMemo = makeString(memo);
                        memoDB.update(memoDB.getTitle(position),position,stringMemo);
                        if(flag[0]==true){
                            searchDB.update(nullArray,position,0);
                        }
                        else searchDB.update(campingPlace,position,1);
                        setResult(3);
                        dialog.dismiss();
                        finish();
                    }
                });
            }
        });

    }
    
    //@를 토큰으로 한줄로 만드는 함수
    String makeString(String[] array){
        String result="";
        if (array.length==0) result = null;
        else {
            for(int i=0;i<array.length-1;i++){
                result += (array[i]+"@");
            }
            result += array[array.length-1];
        }
        return result;
    }
}
