// Group22_InClass08
// ContactsFragment.java
// Ken Stanley & Stephanie Karp

package edu.charlotte.group22_inclass08;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import edu.charlotte.group22_inclass08.databinding.FragmentContactsBinding;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
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
    }

    @Override
    public void addContact(Contact contact) {
        contactsResponse.contacts.add(contact);
    }

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

                    binding.contactsList.setOnItemClickListener((parent, v, position, id) -> getParentFragmentManager().beginTransaction()
                            .replace(R.id.containerView, new DetailsFragment(ContactsFragment.this, contactsResponse.contacts.get(position)))
                            .addToBackStack(null)
                            .commit());

                    binding.addContactButton.setOnClickListener(v -> getParentFragmentManager().beginTransaction()
                            .replace(R.id.containerView, new AddFragment(ContactsFragment.this))
                            .addToBackStack(null)
                            .commit());
                });
            }
        });
    }

    @Override
    public void deleteContact(int id) {
        for (Contact contact: contacts.contacts) {
            if (contact.id == id) {
                contacts.contacts.remove(contact);
                break;
            }
        }
    }

    static public class ContactsAdapter extends ArrayAdapter<Contact> {
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

            contactName.setText(contact.name);
            contactEmail.setText(contact.email);
            contactPhone.setText(contact.phone);
            contactPhoneType.setText(contact.phoneType);

            return convertView;
        }
    }
}
