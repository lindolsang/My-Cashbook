package kr.lindol.mycashbook.util;

import static com.google.common.base.Preconditions.checkNotNull;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class ActivityUtils {
    public static void addFragmentToActivity(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment, int frameId) {
        checkNotNull(fragmentManager, "fragmentManager cannot be null");
        checkNotNull(fragment, "fragment cannot be null");

        fragmentManager.beginTransaction().add(frameId, fragment).commit();
    }
}
