package com.eq.earthquakeplayer3

import androidx.fragment.app.Fragment


abstract class BaseFragment : Fragment() {
    companion object {
        const val TAG = "BaseFragment"
    }

    fun showActionBar(isShow: Boolean) {
        val actionBar = (requireActivity() as MainActivity).supportActionBar
        if (isShow) {
            actionBar?.show()
        } else {
            actionBar?.hide()
        }
    }
}