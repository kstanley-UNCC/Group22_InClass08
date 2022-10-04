// Group22_InClass08
// Contact.java
// Ken Stanley & Stephanie Karp

package edu.charlotte.group22_inclass08;

import com.google.gson.annotations.SerializedName;

public class Contact {
    @SerializedName("Cid")
    Integer id;

    @SerializedName("Name")
    String name;
    @SerializedName("Email")
    String email;
    @SerializedName("Phone")
    String phone;
    @SerializedName("PhoneTYpe")
    String phoneType;

    @Override
    public String toString() {
        return "Contact{" +
                "Cid=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", phoneType='" + phoneType + '\'' +
                '}';
    }
}
