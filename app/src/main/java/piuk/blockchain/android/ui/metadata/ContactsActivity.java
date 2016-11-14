package piuk.blockchain.android.ui.metadata;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;

import java.util.List;

import piuk.blockchain.android.R;
import piuk.blockchain.android.databinding.ActivityContactsBinding;
import piuk.blockchain.android.ui.base.BaseAuthActivity;
import piuk.blockchain.android.ui.customviews.ToastCustom;

public class ContactsActivity extends BaseAuthActivity implements ContactsViewModel.DataListener {

    private ActivityContactsBinding binding;
    private ContactsViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contacts);
        viewModel = new ContactsViewModel(this);

        binding.toolbar.setTitle(R.string.contacts_title);
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.fab.setOnClickListener(view -> ContactPairingMethodActivity.start(this));

        setUiState(UI_STATE.LOADING);

        binding.buttonDisplayQr.setOnClickListener(view -> viewModel.onViewQrClicked());

        viewModel.onViewReady();
    }

    @Override
    public void onContactsLoaded(@NonNull List<ContactsListItem> contacts) {
        ContactsListAdapter contactsListAdapter = new ContactsListAdapter(contacts);
        contactsListAdapter.setContactsClickListener(mdid -> viewModel.onContactSelected(mdid));
        binding.contactsList.setAdapter(contactsListAdapter);
        binding.contactsList.setLayoutManager(new LinearLayoutManager(this));

        setUiState(UI_STATE.CONTENT);
    }

    @Override
    public void showContactsLoadingFailed() {
        setUiState(UI_STATE.FAILURE);
        binding.layoutFailure.setOnClickListener(view -> {
            setUiState(UI_STATE.LOADING);
            viewModel.onViewReady();
        });
    }

    @Override
    public void showQrCode(@NonNull Bitmap bitmap) {
        ImageView imageView = new ImageView(this);
        imageView.setImageBitmap(bitmap);

        new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                .setTitle(R.string.app_name)
                .setView(imageView)
                .setPositiveButton(android.R.string.ok, null)
                .create()
                .show();
    }

    @Override
    public void onShowToast(@StringRes int message, @ToastCustom.ToastType String toastType) {
        ToastCustom.makeText(this, getString(message), ToastCustom.LENGTH_SHORT, toastType);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Static method to assist with launching this activity
     */
    public static void start(Context context) {
        Intent starter = new Intent(context, ContactsActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.destroy();
    }

    enum UI_STATE {
        LOADING,
        CONTENT,
        FAILURE
    }

    private void setUiState(UI_STATE uiState) {
        switch (uiState) {
            case LOADING:
                binding.contactsList.setVisibility(View.GONE);
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.layoutFailure.setVisibility(View.GONE);
                break;
            case CONTENT:
                binding.contactsList.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
                binding.layoutFailure.setVisibility(View.GONE);
                break;
            case FAILURE:
                binding.contactsList.setVisibility(View.GONE);
                binding.progressBar.setVisibility(View.GONE);
                binding.layoutFailure.setVisibility(View.VISIBLE);
                break;
        }
    }
}
