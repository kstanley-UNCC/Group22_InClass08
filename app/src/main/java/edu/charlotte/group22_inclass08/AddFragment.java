// Group22_InClass08
// AddFragment.java
// Ken Stanley & Stephanie Karp

package edu.charlotte.group22_inclass08;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Objects;

import edu.charlotte.group22_inclass08.databinding.FragmentAddBinding;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddFragment extends Fragment {
    private final OkHttpClient client = new OkHttpClient();

    FragmentAddBinding binding;

    public AddFragment(iListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.addCancelButton.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        binding.addSaveButton.setOnClickListener(v -> {
            EditText name = binding.nameEditText;
            EditText email = binding.emailEditText;
            EditText phone = binding.phoneEditText;
            RadioGroup type = binding.phoneTypeGroup;

            if (name.length() == 0) {
                Toast.makeText(requireActivity(), "A name must be provided.", Toast.LENGTH_LONG).show();
                name.findFocus();
                return;
            }

            if (email.length() == 0) {
                Toast.makeText(requireActivity(), "An email must be provided.", Toast.LENGTH_LONG).show();
                email.findFocus();
                return;
            }

            if (phone.length() == 0) {
                Toast.makeText(requireActivity(), "A phone number must be provided.", Toast.LENGTH_LONG).show();
                phone.findFocus();
                return;
            }

            if (type.getCheckedRadioButtonId() == -1) {
                Toast.makeText(requireActivity(), "A phone number type must be selected.", Toast.LENGTH_LONG).show();
                type.findFocus();
                return;
            }

            RadioButton selectedType = requireActivity().findViewById(type.getCheckedRadioButtonId());

            RequestBody formBody = new FormBody.Builder()
                    .add("name", name.getText().toString())
                    .add("email", email.getText().toString())
                    .add("phone", phone.getText().toString())
                    .add("type", selectedType.getText().toString())
                    .build();

            Request request = new Request.Builder()
                    .url("https://www.theappsdr.com/contact/json/create")
                    .post(formBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    requireActivity().runOnUiThread(() -> Toast.makeText(requireActivity(), "Unable to add a new contact at this time.", Toast.LENGTH_LONG).show());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        requireActivity().runOnUiThread(() -> Toast.makeText(requireActivity(), "Unable to add a new contact at this time.", Toast.LENGTH_LONG).show());
                        return;
                    }

                    Gson gson = new Gson();
                    ContactResponse contactResponse = gson.fromJson(Objects.requireNonNull(response.body()).string(), ContactResponse.class);
                    listener.addContact(contactResponse.contact);
                    getParentFragmentManager().popBackStack();
                }
            });
        });
    }

    iListener listener;

    public interface iListener {
        void addContact(Contact contact);
    }
}
