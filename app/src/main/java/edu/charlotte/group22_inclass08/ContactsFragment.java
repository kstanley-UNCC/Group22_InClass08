// Group22_InClass08
// ContactsFragment.java
// Ken Stanley & Stephanie Karp

package edu.charlotte.group22_inclass08;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import edu.charlotte.group22_inclass08.databinding.FragmentContactsBinding;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ContactsFragment extends Fragment implements DetailsFragment.iListener, AddFragment.iListener {
    private final OkHttpClient client = new OkHttpClient();

    FragmentContactsBinding binding;
    ContactsResponse contactsResponse;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentContactsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshContacts();
    }

    @Override
    public void addContact(Contact contact) {
        contactsResponse.contacts.add(contact);
        refreshContacts();
    }

    @Override
    public void deleteContact(int id) {
        for (Contact contact: contactsResponse.contacts) {
            if (contact.id == id) {
                contactsResponse.contacts.remove(contact);
                refreshContacts();
                break;
            }
        }
    }

    private void refreshContacts() {
        Request request = new Request.Builder()
                .url("https://www.theappsdr.com/contacts/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                requireActivity().runOnUiThread(() -> Toast.makeText(requireActivity(), "Unable to retrieve contacts from the Internet", Toast.LENGTH_LONG).show());
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    requireActivity().runOnUiThread(() -> Toast.makeText(requireActivity(), "Unable to retrieve contacts from the Internet", Toast.LENGTH_LONG).show());
                    return;
                }

                Gson gson = new Gson();
                contactsResponse = gson.fromJson(Objects.requireNonNull(response.body()).string(), ContactsResponse.class);

                requireActivity().runOnUiThread(() -> {
                    binding.contactsList.setAdapter(new ContactsAdapter(
                            requireActivity(),
                            R.layout.fragment_contact_list_row,
                            contactsResponse.contacts
                    ));

                    binding.addContactButton.setOnClickListener(v -> getParentFragmentManager().beginTransaction()
                            .replace(R.id.containerView, new AddFragment(ContactsFragment.this))
                            .addToBackStack(null)
                            .commit());
                });
            }
        });
    }

    public class ContactsAdapter extends ArrayAdapter<Contact> {
        public ContactsAdapter(@NonNull Context context, int resource, @NonNull List<Contact> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_contact_list_row, parent, false);
            }

            Contact contact = getItem(position);

            TextView contactName = convertView.findViewById(R.id.contactName);
            TextView contactEmail = convertView.findViewById(R.id.contactEmail);
            TextView contactPhone = convertView.findViewById(R.id.contactPhone);
            TextView contactPhoneType = convertView.findViewById(R.id.contactPhoneType);
            ImageButton detailsButton = convertView.findViewById(R.id.detailsButton);
            ImageButton deleteButton = convertView.findViewById(R.id.deleteButton);

            contactName.setText(contact.name);
            contactEmail.setText(contact.email);
            contactPhone.setText(contact.phone);
            contactPhoneType.setText(contact.phoneType);

            ContactsFragment context = ContactsFragment.this;
            detailsButton.setOnClickListener(view -> context.getParentFragmentManager().beginTransaction()
                    .replace(R.id.containerView, new DetailsFragment(context, getItem(position)))
                    .addToBackStack(null)
                    .commit());

            deleteButton.setOnClickListener(view -> {
                RequestBody formBody = new FormBody.Builder()
                        .add("id", String.valueOf(contact.id))
                        .build();

                Request request = new Request.Builder()
                        .url("https://www.theappsdr.com/contact/json/delete")
                        .post(formBody)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        requireActivity().runOnUiThread(() -> Toast.makeText(requireActivity(), "Unable to delete the contact at this time.", Toast.LENGTH_LONG).show());
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            requireActivity().runOnUiThread(() -> Toast.makeText(requireActivity(), "Unable to delete the contact at this time.", Toast.LENGTH_LONG).show());
                            return;
                        }

                        context.deleteContact(contact.id);
                    }
                });
            });

            return convertView;
        }
    }
}
