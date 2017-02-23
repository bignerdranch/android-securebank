package com.bignerdranch.securebank;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;

import static com.bignerdranch.securebank.LoginActivity.FINGERPRINT_AUTH_STAGE_AUTHENTICATION;

public class FingerprintDialogFragment extends DialogFragment {

    private static final String FINGERPRINT_AUTH_STAGE = "FINGERPRINT_AUTH_STAGE";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ViewDataBinding inflate = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.fingerprint_dialog, null, false);
        return new AlertDialog.Builder(getActivity())
                .setView(inflate.getRoot())
                .setTitle(getTitle())
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> dismiss())
                .create();
    }


    @NonNull
    private String getTitle() {
        String title = "Enroll Fingerprint";
        if (getArguments().getInt(FINGERPRINT_AUTH_STAGE) == FINGERPRINT_AUTH_STAGE_AUTHENTICATION) {
            title = "Authenticate";
        }
        return title;
    }

    public static FingerprintDialogFragment newInstance(int stage) {
        Bundle bundle = new Bundle();
        bundle.putInt(FINGERPRINT_AUTH_STAGE, stage);
        FingerprintDialogFragment fingerprintDialogFragment = new FingerprintDialogFragment();
        fingerprintDialogFragment.setArguments(bundle);
        return fingerprintDialogFragment;
    }

}
