package com.eq.earthquakeplayer3.ext

import com.google.android.material.bottomsheet.BottomSheetBehavior


inline val BottomSheetBehavior<*>.stateName
    get() = when(state) {
        BottomSheetBehavior.STATE_DRAGGING -> "STATE_DRAGGING"
        BottomSheetBehavior.STATE_SETTLING -> "STATE_SETTLING"
        BottomSheetBehavior.STATE_EXPANDED -> "STATE_EXPANDED"
        BottomSheetBehavior.STATE_COLLAPSED -> "STATE_COLLAPSED"
        BottomSheetBehavior.STATE_HIDDEN -> "STATE_HIDDEN"
        BottomSheetBehavior.STATE_HALF_EXPANDED -> "STATE_HALF_EXPANDED"
        else -> "UNKNOWN_STATE"
    }