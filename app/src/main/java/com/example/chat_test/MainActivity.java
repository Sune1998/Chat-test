package com.example.chat_test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


public class MainActivity extends AppCompatActivity {
    private static int SIGN_IN_REQUEST_CODE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (FirebaseAuth.getInstance().getCurrentUser() == null){
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_REQUEST_CODE);
        } else {
            Toast.makeText(this, "welcome" + FirebaseAuth.getInstance().
                    getCurrentUser().getDisplayName(), Toast.LENGTH_LONG).show();

            displayChatMessages();
        }
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input = findViewById(R.id.input);


                FirebaseDatabase.getInstance().getReference().push()
                        .setValue(new ChatMessage(input.getText().toString(),FirebaseAuth.getInstance()
                        .getCurrentUser().getDisplayName()));

                input.setText("");
            }
        });
    }


    public void displayChatMessages(){
        ListView listOfMessages = findViewById(R.id.list_of_messages);
        Query query = FirebaseDatabase.getInstance().getReference().child("chats");

        FirebaseListOptions<ChatMessage> options = new FirebaseListOptions.Builder<ChatMessage>()
                .setQuery(query, ChatMessage.class).setLayout(android.R.layout.activity_list_item).build();

        FirebaseListAdapter<ChatMessage> adapter = new FirebaseListAdapter<ChatMessage>(options) {
            @Override
            protected void populateView(@NonNull View v, @NonNull ChatMessage model, int position) {
                TextView messageText = v.findViewById(R.id.message_text);
                TextView messageUser = v.findViewById(R.id.message_user);
                TextView messageTime = v.findViewById(R.id.message_time);

                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());

                new DateFormat();
                long date = 0;
                messageTime.setText(DateFormat.format("dd/MM/yyyy hh:mm:ss", date).toString());
            }
        };
        listOfMessages.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        //if (requestCode == SIGN_IN_REQUEST_CODE){
            if (requestCode == RESULT_OK){
                Toast.makeText(this,"Singed in", Toast.LENGTH_LONG).show();
                displayChatMessages();
            } else {
                Toast.makeText(this, "Bruh", Toast.LENGTH_LONG).show();

                finish();
            }
        //}
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.menu_sign_out){
            AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(MainActivity.this, "you have been signed out",
                            Toast.LENGTH_LONG).show();
                    finish();
                }
            });
        }
    return true;
    }
}
