package com.example.mountaineer.DocSpace;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {


    private List<Messages> mMessageList;
    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;

    public MessagesAdapter(List<Messages> mMessageList) {

        this.mMessageList = mMessageList;

    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout, parent, false);

        mAuth = FirebaseAuth.getInstance();


        return new MessageViewHolder(v);

    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView senderMessageText;
        public TextView receiverMessageText;


        public MessageViewHolder(View view) {
            super(view);

            senderMessageText = (TextView) view.findViewById(R.id.sender_message_text);
            receiverMessageText = (TextView) view.findViewById(R.id.receiver_message_text);

        }
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {

        String messageSenderId = mAuth.getCurrentUser().getUid();

        Messages messages = mMessageList.get(i);

        String from_user = messages.getFrom();
        String message_type = messages.getType();


        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("thumb_image").getValue().toString();

//                viewHolder.displayName.setText(name);
//
//                Picasso.get().load(image).placeholder(R.drawable.photo_female_8).into(mProfileImage);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (message_type.equals("text")) {


            if (from_user.equals(messageSenderId)) {

                viewHolder.senderMessageText.setText(messages.getMessage());
                viewHolder.senderMessageText.setBackgroundResource(R.drawable.sender_messages_layout);

                viewHolder.receiverMessageText.setVisibility(View.GONE);
            }else {

                viewHolder.receiverMessageText.setText(messages.getMessage());
                viewHolder.receiverMessageText.setBackgroundResource(R.drawable.reciever_messages_layout);

                viewHolder.senderMessageText.setVisibility(View.GONE);

            }


        } else {


//            viewHolder.senderMessageText.setVisibility(View.GONE);
//
//
//
//            viewHolder.receiverMessageText.setVisibility(View.VISIBLE);
//            viewHolder.receiverMessageText.setBackgroundResource(R.drawable.reciever_messages_layout);
//            viewHolder.receiverMessageText.setText(messages.getMessage());

        }

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }


}