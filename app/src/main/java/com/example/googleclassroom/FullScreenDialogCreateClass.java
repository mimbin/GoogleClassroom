package com.example.googleclassroom ;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.WeakReference;
import java.net.Socket;

public class FullScreenDialogCreateClass extends DialogFragment {

    public static String TAG = "FullScreenDialogCreateClass";

    User user ;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
        user = (User) getArguments().getSerializable("user");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fullscreencreateclass, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close_black_24dp);
        toolbar.setTitle("Create Class");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });




        final EditText ClassName = view.findViewById(R.id.ClassName);
        final EditText ClassDes = view.findViewById(R.id.ClassDes);
        final EditText ClassRoom = view.findViewById(R.id.ClassRoom);
        if (ClassName.length()==0)
            ClassName.setError("Class Name Required");
        if (ClassRoom.length()==0)
            ClassRoom.setError("Class Room Required");



        ClassName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (ClassName.length()==0)
                    ClassName.setError("Class Name Required");
            }
        });

        ClassRoom.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (ClassRoom.length()==0)
                    ClassRoom.setError("Class Room Required");
            }
        });


        toolbar.inflateMenu(R.menu.create_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.createMenu) {


                    if(ClassName.length()!=0 && ClassRoom.length()!=0) {
                        Class aClass = new Class(ClassName.getText().toString(), ClassRoom.getText().toString(), ClassDes.getText().toString());
                        AddClass send = new AddClass(getActivity());
                        send.execute("AddClass", user.username, user.password, aClass);
                        dismiss();
                    }
                    else
                        Toast.makeText(getActivity(), "Check The Errors", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });



        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.AppTheme_Slide);

        }
    }
}


class AddClass extends AsyncTask<Object,Void , String> {
    Socket s ;
    ObjectOutputStream oos ;
    ObjectInputStream ois ;
    DataInputStream dis;
    Boolean answer ;
    Class myclass ;
    WeakReference<FragmentActivity> activityReference ;

    AddClass(FragmentActivity context) {
        activityReference = new WeakReference<>(context);
    }

    @Override
    protected String doInBackground(Object... input) {
        try {
            s = new Socket("10.0.2.2" , 8080);
            oos = new ObjectOutputStream(s.getOutputStream());
            ois = new ObjectInputStream(s.getInputStream());
            String[] strings = {(String) input[0] , (String)input[1] , (String)input[2]};
            oos.writeObject(strings);
            oos.flush();
            oos.writeObject(input[3]);
            oos.flush();
            answer = ois.readBoolean();
            System.out.println(answer);
            myclass = (Class) input[3] ;




            oos.close();
            ois.close();
            s.close();

        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {

        FragmentActivity activity = activityReference.get();
        if (activity== null || activity.isFinishing()) return;

        if (answer) {
            Intent intent = new Intent(activity, ClassActivity.class);
            intent.putExtra("class" , myclass) ;
            activity.startActivity(intent);

        }else {

        }

    }
}

