// Group22_InClass08
// ContactsResponse.java
// Ken Stanley & Stephanie Karp

package edu.charlotte.group22_inclass08;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class ContactsResponse {
    String status;
    ArrayList<Contact> contacts;

    @NonNull
    @Override
    public String toString() {
        return "ContactsResponse{" +
                "status='" + status + '\'' +
                ", contacts=" + contacts +
                '}';
    }
}
