package com.eq.earthquakeplayer3.ui

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.eq.earthquakeplayer3.BaseFragment
import com.eq.earthquakeplayer3.MainActivity
import com.eq.earthquakeplayer3.R
import com.eq.earthquakeplayer3.custom.MiniPlayerView
import com.eq.earthquakeplayer3.custom.SongPlayerControlView
import com.eq.earthquakeplayer3.custom.SongPlayerView
import com.eq.earthquakeplayer3.data.SongData
import com.eq.earthquakeplayer3.ext.isPlayEnabled
import com.eq.earthquakeplayer3.ext.isPlaying
import com.eq.earthquakeplayer3.ext.stateName
import com.eq.earthquakeplayer3.utils.ScreenUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import nl.joery.animatedbottombar.AnimatedBottomBar


class MainFragment : BaseFragment() {
    companion object {
        private const val TAG = "MainFragment"
    }

    private lateinit var pagerAdapter: MainPagerAdapter

    private lateinit var behavior: BottomSheetBehavior<*>
    private lateinit var viewPager: ViewPager
    private lateinit var bottomBar: AnimatedBottomBar

    // related to player Ui
    private lateinit var songPlayerLayout: SongPlayerView
    private lateinit var miniPlayerLayout: MiniPlayerView
    private lateinit var closeLayout: View

    private var bottomBarHeight = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pagerAdapter = MainPagerAdapter(childFragmentManager)
        bottomBarHeight = ScreenUtils.dipToPixel(context, 62f).toFloat()

        requireActivity().onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                performBackPress()
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        songPlayerViewModel.songData.observe(viewLifecycleOwner) {
            if (it != null) {
                updatePlayerUi(it)
                updateMiniPlayerUi(it)
            }
        }
        songPlayerViewModel.musicServiceConnection.playbackState.observe(viewLifecycleOwner) {
            if (it.state == PlaybackStateCompat.STATE_BUFFERING) {
                songPlayerViewModel.musicServiceConnection.transportControls.play()
            }

            if (it.isPlaying) {
                updatePlayButton(true)
            } else if (it.state == PlaybackStateCompat.STATE_PAUSED) {
                updatePlayButton(false)
            }
        }
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        behavior = BottomSheetBehavior.from(view.findViewById(R.id.bottom_sheet_player_layout)).apply {
            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    Log.d(TAG, "newState : $stateName")
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    miniPlayerLayout.run {
                        alpha = (1 - slideOffset)
                    }
                    closeLayout.run {
                        alpha = slideOffset
                        if (alpha == 0f) {
                            visibility = View.GONE
                        } else {
                            if (visibility == View.GONE) {
                                visibility = View.VISIBLE
                            }
                        }
                    }
                    bottomBar.run {
                        translationY = bottomBarHeight * slideOffset
                    }
                }
            })
        }

        viewPager = view.findViewById(R.id.view_pager)
        viewPager.run {
            adapter = pagerAdapter
            offscreenPageLimit = 3
        }

        bottomBar = view.findViewById(R.id.bottom_bar)

        songPlayerLayout = view.findViewById(R.id.song_player_layout)
        miniPlayerLayout = view.findViewById(R.id.mini_player_layout)
        miniPlayerLayout.run {
            setOnClickListener {
                if (behavior.state == BottomSheetBehavior.STATE_COLLAPSED)
                    click2Collapsed(false)
            }
        }

        closeLayout = view.findViewById(R.id.close_layout)
        closeLayout.run {
            setOnClickListener {
                click2Collapsed(true)
            }
        }

        bottomBar.setupWithViewPager(viewPager)

        // UI refresh
        setLayout()
    }

    private inner class MainPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getCount() = 3

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> SongListFragment.newInstance()
                else -> DummyFragment.newInstance()
            }
        }
    }

    private fun click2Collapsed(isCollapsed: Boolean) {
        behavior.state = if (isCollapsed) BottomSheetBehavior.STATE_COLLAPSED else BottomSheetBehavior.STATE_EXPANDED
    }

    /**
     * mini player
     * close 버튼
     * bottomBar는 애니메이션 처리가 있기 때문에 재설정이 필요함.
     */
    private fun setLayout() {
        if (behavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
            behavior.peekHeight = bottomBarHeight.toInt()
        }

        miniPlayerLayout.run {
            if (behavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                alpha = 1f
            } else if (behavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                alpha = 0f
            }
        }
        closeLayout.run {
            if (behavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                alpha = 0f
                visibility = View.GONE
            } else if (behavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                alpha = 1f
                visibility = View.VISIBLE
            }
        }
        bottomBar.run {
            if (behavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                translationY = 0f
            } else if (behavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                translationY = bottomBarHeight
            }
        }
    }

    private fun performBackPress() {
        if (behavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            (activity as MainActivity).finish()
        }
    }

    private fun updatePlayerUi(songData: SongData) {
        songPlayerLayout.run {
            context?.let {
                Glide.with(it).load(songData.albumPath).into(thumbIv)
            }
            setSongName(songData.title)
            setArtistName(songData.artistName)
            setUpdateTime("0:00")
            setTotalTime(DateUtils.formatElapsedTime(songData.duration / 1000))

            controlView.run {
                listener = object : SongPlayerControlView.Callback {
                    override fun onClickPrevious() {
                        skipToPrevious()
                    }

                    override fun onClickPlay() {
                        playMedia(songData.data)
                    }

                    override fun onClickNext() {
                        skipToNext()
                    }
                }
            }
        }
    }

    private fun updateMiniPlayerUi(songData: SongData) {
        miniPlayerLayout.run {
            setSongName(songData.title)
            setArtistName(songData.artistName)
            listener = object : MiniPlayerView.Callback {
                override fun onClickPrevious() {
                    skipToPrevious()
                }

                override fun onClickPlay() {
                    playMedia(songData.data)
                }

                override fun onClickNext() {
                    skipToNext()
                }
            }
        }
    }

    private fun playMedia(uriPath: String?) {
        songPlayerViewModel.playMedia(uriPath)
    }

    private fun skipToPrevious() {
        songPlayerViewModel.skipToPrevious()
    }

    private fun skipToNext() {
        songPlayerViewModel.skipToNext()
    }

    private fun updatePlayButton(isPlaying: Boolean) {
        songPlayerLayout.controlView.updatePlayButton(isPlaying)
        miniPlayerLayout.updatePlayButton(isPlaying)
    }
}