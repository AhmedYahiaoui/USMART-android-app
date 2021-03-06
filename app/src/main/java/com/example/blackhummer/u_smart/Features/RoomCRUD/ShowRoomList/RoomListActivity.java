package com.example.blackhummer.u_smart.Features.RoomCRUD.ShowRoomList;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.blackhummer.u_smart.Database.DatabaseQueryClass;
import com.example.blackhummer.u_smart.Features.RoomCRUD.CreateRoom.Room;
import com.example.blackhummer.u_smart.Features.RoomCRUD.CreateRoom.RoomCreateDialogFragment;
import com.example.blackhummer.u_smart.Features.RoomCRUD.CreateRoom.RoomCreateListener;
import com.example.blackhummer.u_smart.R;
import com.example.blackhummer.u_smart.Util.Config;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

public class RoomListActivity extends AppCompatActivity implements RoomCreateListener {

    private DatabaseQueryClass databaseQueryClass = new DatabaseQueryClass(this);

    private List<Room> studentList = new ArrayList<>();

    private TextView summaryTextView;
    private TextView studentListEmptyTextView;
    private RecyclerView recyclerView;
    private RoomListRecyclerViewAdapter roomListRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Logger.addLogAdapter(new AndroidLogAdapter());

        recyclerView = findViewById(R.id.recyclerView);
        summaryTextView = findViewById(R.id.summaryTextView);
        studentListEmptyTextView = findViewById(R.id.emptyListTextView);

        studentList.addAll(databaseQueryClass.getAllStudent());

        roomListRecyclerViewAdapter = new RoomListRecyclerViewAdapter(this, studentList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(roomListRecyclerViewAdapter);

        viewVisibility();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openStudentCreateDialog();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        printSummary();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.action_delete){

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Are you sure, You wanted to delete all students?");
            alertDialogBuilder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            boolean isAllDeleted = databaseQueryClass.deleteAllStudents();
                            if(isAllDeleted){
                                studentList.clear();
                                roomListRecyclerViewAdapter.notifyDataSetChanged();
                                viewVisibility();
                            }
                        }
                    });

            alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        return super.onOptionsItemSelected(item);
    }

    public void viewVisibility() {
        if(studentList.isEmpty())
            studentListEmptyTextView.setVisibility(View.VISIBLE);
        else
            studentListEmptyTextView.setVisibility(View.GONE);
        printSummary();
    }

    private void openStudentCreateDialog() {
        RoomCreateDialogFragment studentCreateDialogFragment = RoomCreateDialogFragment.newInstance("Create Room", this);
        studentCreateDialogFragment.show(getSupportFragmentManager(), Config.CREATE_ROOM);
    }

    private void printSummary() {
        long studentNum = databaseQueryClass.getNumberOfStudent();
        long subjectNum = databaseQueryClass.getNumberOfSubject();

        summaryTextView.setText(getResources().getString(R.string.database_summary, studentNum, subjectNum));
    }

    @Override
    public void onStudentCreated(Room room) {
        studentList.add(room);
        roomListRecyclerViewAdapter.notifyDataSetChanged();
        viewVisibility();
        Logger.d(room.getName());
    }

}
