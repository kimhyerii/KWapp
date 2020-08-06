package com.example.kwapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MemoActivity extends AppCompatActivity {
    private final String TAG = "MemoActivity";
    public DataModel DataManager;
    private String foldername;

    ListView listMemoView;
    MemoListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);

        ImageButton addMemoButton = findViewById(R.id.addMemoButton);
        addMemoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertMemoDialog();
            }
        });

        Intent intent = getIntent();
        foldername = intent.getStringExtra("name");

        DataManager = new DataModel(this.getApplicationContext(), foldername);

        listMemoView = (ListView)findViewById(R.id.listview2);
        adapter = new MemoListAdapter();

        DataManager.setMemolist();
        this.setListonView();
        listMemoView.setAdapter(adapter);

        listMemoView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "On item long click : " + position);
                ListMemoViewItem item = (ListMemoViewItem) parent.getItemAtPosition(position);
                String name = item.getKeyword();
                deleteMemoDialog(name, position);

                return false;
            }
        });
    }

    void insertMemoDialog(){
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_addmemo, null);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(MemoActivity.this);
        dialog.setTitle("Add memo");
        final EditText keyword = (EditText)dialogView.findViewById(R.id.kwEditText);
        final EditText content = (EditText)dialogView.findViewById(R.id.ctEditText);
        dialog.setView(dialogView);

        dialog.setPositiveButton("save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.i(TAG, "Pos button clicked");

                String kw = keyword.getText().toString();
                String ct = content.getText().toString();
                activityAddMemo(kw, ct);

                adapter.addItem(kw, ct);
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

    void deleteMemoDialog(final String name, final int position){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(MemoActivity.this);
        dialog.setTitle("Delete [" + name + "]?");

        dialog.setPositiveButton("delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.i(TAG, "Pos button clicked");

                activityDeleteMemo(name);

                adapter.listViewItemList.remove(position);
                adapter.notifyDataSetChanged();;

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

    void activityAddMemo(String KeyWord, String Content){
        DataManager.addMemo(KeyWord, Content);
    }
    void activityDeleteMemo(String Keyword){DataManager.deleteMemo(Keyword);}

    void setListonView(){
        ArrayList <Data> memolist = DataManager.getMemolist();

        if(memolist != null){
            for(int i = 0; i < memolist.size(); i++){
                adapter.addItem(memolist.get(i).getKeyword(), memolist.get(i).getContent());
            }
        }
        else{
            adapter.addItem("no item", "no item");
        }
    }

    class MemoListAdapter extends BaseAdapter implements View.OnLongClickListener{
        private ArrayList<ListMemoViewItem> listViewItemList = new ArrayList<ListMemoViewItem>() ;
        public MemoListAdapter(){
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

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.memoview, parent, false);
            }

            final Button kwbutton = (Button) convertView.findViewById(R.id.kwbutton);
            TextView contentView = (TextView) convertView.findViewById(R.id.contentTxt);

            final ListMemoViewItem listViewItem = listViewItemList.get(pos);

            String keyword = listViewItem.getKeyword();
            boolean visible = DataManager.getVisibility(keyword);
            if(visible){
                kwbutton.setText(listViewItem.getKeyword());
            }
            else{
                kwbutton.setText("");
            }

            kwbutton.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ListMemoViewItem item = (ListMemoViewItem)getItem(pos);
                    String kw = item.getKeyword();
                    if(DataManager.changeVisibility(kw)){
                        kwbutton.setText(listViewItem.getKeyword());
                    }
                    else{
                        kwbutton.setText("");
                    }
                }
            });
            contentView.setText(listViewItem.getContent());
            contentView.setOnLongClickListener(this);
            return convertView;
        }

        public void addItem(String kw, String ct) {
            ListMemoViewItem item = new ListMemoViewItem();

            item.setKeyword(kw);
            item.setContent(ct);

            listViewItemList.add(item);
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }

    class ListMemoViewItem{
        private String keyword;
        private String content;

        public String getKeyword(){
            return this.keyword;
        }
        public void setKeyword(String kw){
            keyword = kw;
        }
        public String getContent(){
            return this.content;
        }
        public void setContent(String ct){
            content = ct;
        }
    }
}