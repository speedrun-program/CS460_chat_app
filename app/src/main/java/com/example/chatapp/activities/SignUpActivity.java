package com.example.chatapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chatapp.R;
import com.example.chatapp.databinding.ActivitySignUpBinding;
import com.example.chatapp.utilities.Constants;
import com.example.chatapp.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;


/**
 * Sign in class
 */
public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private PreferenceManager preferenceManager;


    private String encodeImage;


    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Uri imageUri = result.getData().getData();
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                        binding.imageProfile.setImageBitmap(bitmap);
                        binding.textAddImage.setVisibility(View.GONE);
                        encodeImage = encodeImage(bitmap);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
    );


    /**
     * Sets up the ActivitySignUpBinding and PreferenceManager objects and sets button listeners
     * @param savedInstanceState Default savedInstanceState argument
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
    }


    /**
     * Sets up the button listeners
     */
    private void setListeners() {
        binding.textSignIn.setOnClickListener(v -> onBackPressed());

        binding.buttonSignUp.setOnClickListener(v -> {
            if (isValidSignUpDetails()) {
                SignUp();
            }
        });

        binding.layoutImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
    }


    /**
     * Shows a toast message
     * @param message The message to show
     */
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


    /**
     * Tries to sign up the user
     */
    private void SignUp() {
        loading(true);

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, String> user = new HashMap<>();
        user.put(Constants.KEY_FIRST_NAME, binding.inputFirstName.getText().toString());
        user.put(Constants.KEY_LAST_NAME, binding.inputLastName.getText().toString());
        user.put(Constants.KEY_EMAIL, binding.inputEmail.getText().toString());
        user.put(Constants.KEY_PASSWORD, binding.inputPassword.getText().toString());

        user.put(Constants.KEY_IMAGE, encodeImage);

        database.collection(Constants.KEY_COLLECTION_USERS)
                .add(user)
                .addOnSuccessListener(documentReference -> {

                    loading(false);

                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                    user.put(Constants.KEY_FIRST_NAME, binding.inputFirstName.getText().toString());
                    user.put(Constants.KEY_LAST_NAME, binding.inputLastName.getText().toString());
                    preferenceManager.putString(Constants.KEY_IMAGE, encodeImage);

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }).addOnFailureListener(exception -> {
                    loading(false);
                    showToast(exception.getMessage());
        });
    }


    /**
     * Encodes an image to JPEG format and returns it as a String object
     * @param bitmap The image to encode.
     * @return The image as a String object
     */
    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();

        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);

        byte[] bytes = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }


    /**
     * Sets loading graphics visible or not visible
     * @param isLoading Whether or not loading is happening
     */
    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.buttonSignUp.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.buttonSignUp.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }


    /**
     * Checks if the user's sign up details are valid
     * @return true if the user's sign up details are valid, otherwise false
     */
    private Boolean isValidSignUpDetails() {
        if (encodeImage == null) {
            showToast("please include an image");
            return false;
        }
        if (binding.inputFirstName.getText().toString().trim().isEmpty()) {
            showToast("please enter your first name");
            return false;
        }
        if (binding.inputLastName.getText().toString().trim().isEmpty()) {
            showToast("please enter your last name");
            return false;
        }
        if (binding.inputEmail.getText().toString().trim().isEmpty()) {
            showToast("please enter your email");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()) {
            showToast("please enter a valid email");
            return false;
        }

        String password = binding.inputPassword.getText().toString().trim();

        if (password.isEmpty()) {
            showToast("please enter your password");
            return false;
        }

        String confirmedPassword = binding.inputConfirmPassword.getText().toString().trim();

        if (confirmedPassword.isEmpty()) {
            showToast("please confirm your password");
            return false;
        }
        if (!password.equals(confirmedPassword)) {
            showToast("password and confirmed password don't match");
            return false;
        }

        return true;
    }
}