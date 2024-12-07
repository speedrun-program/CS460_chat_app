package com.example.chatapp.listeners;

import com.example.chatapp.models.User;

/**
 * Interface for clickable users
 */
public interface UserListener {

    /**
     * A function that runs when the user is clicked
     * @param user The user that was clicked
     */
    void onUserClicked(User user);
}
