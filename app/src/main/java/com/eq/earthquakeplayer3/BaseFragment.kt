package com.eq.earthquakeplayer3

import android.content.ComponentName
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.eq.earthquakeplayer3.playback.MusicService
import com.eq.earthquakeplayer3.playback.MusicServiceConnection
import com.eq.earthquakeplayer3.viewmodel.SongPlayerViewModel


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

    /**
     * fragment간의 데이터 교환을 위해서 사용함
     * SongListFragment에서 세팅한 데이터를 MainFragment의 플레이어에 전달하기 위해서 owner를 activity로 사용한다.
     */
    protected val songPlayerViewModel: SongPlayerViewModel by lazy {
        val context = requireActivity()
        val musicServiceConnection = MusicServiceConnection.getInstance(context, ComponentName(context, MusicService::class.java))
        ViewModelProvider(context, SongPlayerViewModel.Factory(EqApp.instance, musicServiceConnection)).get(SongPlayerViewModel::class.java)
    }
}