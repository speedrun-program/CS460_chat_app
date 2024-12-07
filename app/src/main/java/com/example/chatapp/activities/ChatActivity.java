package com.example.chatapp.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chatapp.R;
import com.example.chatapp.adapters.ChatAdapter;
import com.example.chatapp.databinding.ActivityChatBinding;
import com.example.chatapp.models.ChatMessage;
import com.example.chatapp.models.User;
import com.example.chatapp.utilities.Constants;
import com.example.chatapp.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * chatting activity class
 */
public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private User receiverUser;

    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;


    /**
     * On creation method
     * @param savedInstanceState Default parameter
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadReceiverDetails();
        setListeners();
        init();
        listenMessage();
    }


    /**
     * Initializes variables
     */
    private void init() {
        preferenceManager = new PreferenceManager(getApplicationContext());
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(
                chatMessages,
                getBitmapFromEncodedString(receiverUser.image),
                preferenceManager.getString(Constants.KEY_USER_ID)
        );
        binding.chatRecyclerView.setAdapter(chatAdapter);
        database = FirebaseFirestore.getInstance();
    }


    /**
     * A method that runs when a message is sent
     */
    private void sendMessages() {
        HashMap<String, Object> message = new HashMap<>();

        message.put(
                Constants.KEY_SENDER_ID,
                preferenceManager.getString(Constants.KEY_USER_ID)
        );
        message.put(
                Constants.KEY_RECEIVER_ID,
                receiverUser.id
        );
        message.put(
                Constants.KEY_MESSAGE,
                binding.inputMessage.getText().toString()
        );
        message.put(
                Constants.KEY_TIMESTAMP,
                new Date()
        );

        database.collection(Constants.KEY_COLLECTION_CHAT).add(message);
        binding.inputMessage.setText(null);
    }


    /**
     * A method that the app runs while listening for new messages
     */
    private void listenMessage() {
        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(
                        Constants.KEY_SENDER_ID,
                        preferenceManager.getString(Constants.KEY_USER_ID)
                ).whereEqualTo(
                        Constants.KEY_RECEIVER_ID,
                        receiverUser.id
                ).addSnapshotListener(eventListener);

        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(
                        Constants.KEY_SENDER_ID,
                        receiverUser.id
                ).whereEqualTo(
                        Constants.KEY_RECEIVER_ID,
                        preferenceManager.getString(Constants.KEY_USER_ID)
                ).addSnapshotListener(eventListener);
    }


    private final EventListener<QuerySnapshot> eventListener = ((value, error) -> {
        if (error != null) {
            return;
        }

        if (value != null) {
            int count = chatMessages.size();
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    chatMessage.receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    chatMessage.message = documentChange.getDocument().getString(Constants.KEY_MESSAGE);

                    chatMessage.datetime = getReadableDateTime(
                            documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP)
                    );

                    chatMessage.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);

                    chatMessages.add(chatMessage);
                }
            }

            Collections.sort(chatMessages, (obj1, obj2) -> obj1.dateObject.compareTo(obj2.dateObject));

            if (count == 0) {
                chatAdapter.notifyDataSetChanged();
            } else {
                chatAdapter.notifyItemRangeChanged(chatMessages.size(), chatMessages.size());

                binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size() - 1);
            }

            binding.chatRecyclerView.setVisibility(View.VISIBLE);
        }

        binding.progressBar.setVisibility(View.GONE);
    });


    /**
     * Converts an image stored as a String into a Bitmap
     * @param encodedImage The image stored as a String
     * @return A Bitmap of the image given as the argument
     */
    private Bitmap getBitmapFromEncodedString(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }


    /**
     * Gets the information about the user being chatted with
     */
    private void loadReceiverDetails() {
        receiverUser = (User)getIntent().getSerializableExtra(Constants.KEY_USER);
        String name = receiverUser.firstName + " " + receiverUser.lastName;
        binding.textName.setText(name);
    }


    /**
     * Sets up the click listeners
     */
    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.layoutSend.setOnClickListener(v -> sendMessages());
    }


    /**
     * Returns a String representation of a Date object
     * @param date The Date object to make a String representation from
     * @return A String representation of the date argument
     */
    private String getReadableDateTime(Date date) {
        return new SimpleDateFormat(
                "MMM dd, yyy - hh:mm a",
                Locale.getDefault()
        ).format(date);
    }
}