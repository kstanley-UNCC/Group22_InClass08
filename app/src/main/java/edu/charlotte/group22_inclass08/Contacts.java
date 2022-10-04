// Group22_InClass08
// Contacts.java
// Ken Stanley & Stephanie Karp

package edu.charlotte.group22_inclass08;

import java.util.ArrayList;

public class Contacts {
    String status;
    ArrayList<Contact> contacts;

    @Override
    public String toString() {
        return "Contacts{" +
                "status='" + status + '\'' +
                ", contacts=" + contacts +
                '}';
    }
}
