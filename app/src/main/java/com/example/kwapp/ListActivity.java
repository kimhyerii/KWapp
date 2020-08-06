package com.example.kwapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {
    private final String TAG = "ListActivity";
    public FolderModel FolderManager;

    ListView listview;
    ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        final ImageButton addFolderButton = findViewById(R.id.addfolderButton);
        addFolderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertFolderDialog();
            }
        });

        FolderManager = new FolderModel(this.getApplicationContext());

        listview = (ListView)findViewById(R.id.listview1);
        adapter = new ListAdapter() ;

        FolderManager.setFolderlist();

        this.setListonView();
        listview.setAdapter(adapter);

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id){
                Log.i(TAG, "On item long click : " + position);
                ListViewItem item = (ListViewItem) parent.getItemAtPosition(position);
                String name = item.getName();
                deleteFolderDialog(name, position);

                return true;
            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                ListViewItem item = (ListViewItem) parent.getItemAtPosition(position);
                String name = item.getName();

                Intent intent = new Intent(ListActivity.this, MemoActivity.class);
                intent.putExtra("name", name);
                startActivity(intent);
            }
        });

    }

    void setListonView(){
        ArrayList <String> list = FolderManager.getFolderlist();
        if(list != null) {
            for (int i = 0; i < list.size(); i++) {
                adapter.addItem(ContextCompat.getDrawable(this.getApplicationContext(), R.drawable.folder_icon),
                       list.get(i));
            }
        }
        else{
            adapter.addItem(ContextCompat.getDrawable(this, R.drawable.folder_icon),
                    "no item");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_actions, menu);

        return true;
    }

    void insertFolderDialog(){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(ListActivity.this);
        dialog.setTitle("Add folder");
        final EditText edittext = new EditText(ListActivity.this);
        dialog.setView(edittext);
        dialog.setPositiveButton("save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.i(TAG, "Pos button clicked");

                String foldername = edittext.getText().toString();
                Log.i(TAG, "name = " + foldername);
                activityAddFolder(foldername);
                adapter.addItem(ContextCompat.getDrawable(getApplicationContext(),R.drawable.folder_icon),
                        FolderManager.getNewItem());
                adapter.notifyDataSetChanged();
                dialogInterface.dismiss();
            }
        });
        dialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog.show();
    }

    void deleteFolderDialog(final String name, final int position){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(ListActivity.this);
        dialog.setTitle("Delete folder");
        dialog.setPositiveButton("delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.i(TAG, "Pos button clicked");
                activityDeleteFolder(name);
                adapter.listViewItemList.remove(position);
                adapter.notifyDataSetChanged();
                dialogInterface.dismiss();
            }
        });
        dialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog.show();
    }

    void activityAddFolder(String foldername){
        FolderManager.addFolder(foldername);
    }

    void activityDeleteFolder(String foldername){
        FolderManager.deleteFolder(foldername);
    }

    class ListAdapter extends BaseAdapter {
        private ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>() ;
        public ListAdapter(){
        }

        @Override
        public int getCount() {
            return listViewItemList.size() ;
        }

        @Override
        public Object getItem(int position) {
            return listViewItemList.get(position) ;
        }

        @Override
        public long getItemId(int position) {
            return position ;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int pos = position;
            final Context context = parent.getContext();

            // "listview_item" Layout을 inflate하여 convertView 참조 획득.
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.listview, parent, false);
            }

            // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
            ImageView iconImageView = (ImageView) convertView.findViewById(R.id.imageView1) ;
            TextView titleTextView = (TextView) convertView.findViewById(R.id.textView1) ;

            // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
            ListViewItem listViewItem = listViewItemList.get(position);

            // 아이템 내 각 위젯에 데이터 반영
            iconImageView.setImageDrawable(listViewItem.getIcon());
            titleTextView.setText(listViewItem.getName());

            return convertView;
        }

        public void addItem(Drawable icon, String title) {
            ListViewItem item = new ListViewItem();

            item.setIcon(icon);
            item.setName(title);

            listViewItemList.add(item);
        }
    }

    class ListViewItem {
        private Drawable folderIcon ;
        private String foldername ;

        public Drawable getIcon() {
            return this.folderIcon ;
        }

        public void setIcon(Drawable icon) {
            folderIcon = icon ;
        }

        public String getName() {
            return this.foldername ;
        }

        public void setName(String title) {
            foldername = title ;
        }
    }
}