package com.example.chatapp.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.databinding.ItemContainerUserBinding;
import com.example.chatapp.listeners.UserListener;
import com.example.chatapp.models.User;

import java.util.List;

/**
 * User select adapter class
 */
public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    private final List<User> users;
    private final UserListener userListener;


    /**
     * Constructor
     * @param users The users that can be chatted with
     * @param userListener The UserListener to use
     */
    public UsersAdapter(List<User> users, UserListener userListener) {
        this.users = users;
        this.userListener = userListener;
    }


    /**
     * On create method
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return A new UserViewHolder object
     */
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerUserBinding itemContainerUserBinding = ItemContainerUserBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new UserViewHolder(itemContainerUserBinding);
    }


    /**
     * On bind method
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.setUserData(users.get(position));
    }


    /**
     * Returns how many users are available to chat with
     * @return The number of users available to chat with
     */
    @Override
    public int getItemCount() {
        return users.size();
    }


    /**
     * inner user view holder class
     */
    class UserViewHolder extends RecyclerView.ViewHolder {

        ItemContainerUserBinding binding;


        /**
         * Constructor
         * @param itemContainerUserBinding The binding object
         */
        public UserViewHolder(ItemContainerUserBinding itemContainerUserBinding) {
            super(itemContainerUserBinding.getRoot());
            binding = itemContainerUserBinding;
        }


        /**
         * Shows the user's info
         * @param user The user to display the info of
         */
        void setUserData(User user) {
            String fullName = user.firstName + " " + user.lastName;
            binding.textName.setText(fullName);
            binding.textEmail.setText(user.email);
            binding.imageProfile.setImageBitmap(getUserImage(user.image));

            binding.getRoot().setOnClickListener(v -> userListener.onUserClicked(user));
        }
    }


    /**
     * Converts the user's profile image from a String to a Bitmap
     * @param encodedImage The user's profile image stored as a String
     * @return A bitmap created from the String storing the user's profile image
     */
    private Bitmap getUserImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

}
