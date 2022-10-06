// Group22_InClass08
// DetailsFragment.java
// Ken Stanley & Stephanie Karp

package edu.charlotte.group22_inclass08;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.IOException;

import edu.charlotte.group22_inclass08.databinding.FragmentDetailsBinding;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DetailsFragment extends Fragment {
    FragmentDetailsBinding binding;

    private final Contact contact;
    private final OkHttpClient client = new OkHttpClient();

    public DetailsFragment(iListener listener, Contact contact) {
        this.listener = listener;
        this.contact = contact;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDetailsBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.contactNameTextView.setText(contact.name);
        binding.contactEmailTextView.setText(contact.email);
        binding.contactPhoneTextView.setText(contact.phone);
        binding.contactPhoneTypeTextView.setText(contact.phoneType);

        binding.backButton.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        binding.deleteContactButton.setOnClickListener(v -> {
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
                        Log.d("demo", "onResponse: " + response.body().string());
                        return;
                    }

                    listener.deleteContact(contact.id);
                    getParentFragmentManager().popBackStack();
                }
            });
        });
    }

    iListener listener;

    public interface iListener {
        void deleteContact(int id);
    }
}
