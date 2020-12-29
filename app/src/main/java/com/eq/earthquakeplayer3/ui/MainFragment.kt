package com.eq.earthquakeplayer3.ui

import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.activityViewModels
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.eq.earthquakeplayer3.BaseFragment
import com.eq.earthquakeplayer3.MainActivity
import com.eq.earthquakeplayer3.R
import com.eq.earthquakeplayer3.custom.MiniPlayerView
import com.eq.earthquakeplayer3.custom.SongPlayerControlView
import com.eq.earthquakeplayer3.custom.SongPlayerView
import com.eq.earthquakeplayer3.data.SongData
import com.eq.earthquakeplayer3.utils.ScreenUtils
import com.eq.earthquakeplayer3.viewmodel.SongPlayerViewModel
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import nl.joery.animatedbottombar.AnimatedBottomBar


class MainFragment : BaseFragment() {
    companion object {
        private const val TAG = "MainFragment"
    }

    private lateinit var pagerAdapter: MainPagerAdapter

    private lateinit var slidingLayout: SlidingUpPanelLayout
    private lateinit var viewPager: ViewPager
    private lateinit var bottomBar: AnimatedBottomBar

    // related to player Ui
    private lateinit var songPlayerLayout: SongPlayerView
    private lateinit var miniPlayerLayout: MiniPlayerView
    private lateinit var closeLayout: View

    private var bottomBarHeight = 0f

    /**
     * fragment간의 데이터 교환을 위해서 사용함
     * SongListFragment에서 세팅한 데이터를 MainFragment의 플레이어에 전달하기 위해서 activityViewModels를 사용한다.
     */
    private val viewModel: SongPlayerViewModel by activityViewModels()

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
        viewModel.songData.observe(viewLifecycleOwner) {
            if (it != null) {
                updatePlayerUi(it)
                updateMiniPlayerUi(it)
            }
        }
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        slidingLayout = view.findViewById(R.id.sliding_layout)
        slidingLayout.run {
            setClickEnable(panelState == SlidingUpPanelLayout.PanelState.COLLAPSED)

            addPanelSlideListener(object : SlidingUpPanelLayout.PanelSlideListener {
                override fun onPanelSlide(panel: View?, slideOffset: Float) {
                    miniPlayerLayout.run {
                        alpha = (1 - slideOffset)
                    }
                    closeLayout.run {
                        alpha = slideOffset
                    }
                    bottomBar.run {
                        translationY = bottomBarHeight * slideOffset
                    }
                }

                override fun onPanelStateChanged(panel: View?, previousState: SlidingUpPanelLayout.PanelState?, newState: SlidingUpPanelLayout.PanelState?) {
                    when (newState) {
                        SlidingUpPanelLayout.PanelState.COLLAPSED -> {
                            setClickEnable(true)
                        }
                        SlidingUpPanelLayout.PanelState.EXPANDED -> {
                            setClickEnable(false)
                        }
                        else -> {
                        }
                    }
                }
            })

            setFadeOnClickListener { // v 버튼 더블클릭 방지 차원
                click2Collapsed()
            }
        }

        viewPager = view.findViewById(R.id.view_pager)
        viewPager.run {
            adapter = pagerAdapter
            offscreenPageLimit = 3
        }

        bottomBar = view.findViewById(R.id.bottom_bar)

        songPlayerLayout = view.findViewById(R.id.song_player_layout)
        miniPlayerLayout = view.findViewById(R.id.mini_player_layout)
        closeLayout = view.findViewById(R.id.close_layout)
        closeLayout.run {
            setOnClickListener {
                click2Collapsed()
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

    private fun setClickEnable(clickable: Boolean) {
        slidingLayout.run {
            isTouchEnabled = clickable
        }
    }

    private fun click2Collapsed() {
        setClickEnable(true)
        slidingLayout.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
    }

    /**
     * mini player
     * close 버튼
     * bottomBar는 애니메이션 처리가 있기 때문에 재설정이 필요함.
     */
    private fun setLayout() {
        miniPlayerLayout.run {
            if (slidingLayout.panelState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                alpha = 1f
            } else if (slidingLayout.panelState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                alpha = 0f
            }
        }
        closeLayout.run {
            if (slidingLayout.panelState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                alpha = 0f
            } else if (slidingLayout.panelState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                alpha = 1f
            }
        }
        bottomBar.run {
            if (slidingLayout.panelState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                translationY = 0f
            } else if (slidingLayout.panelState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                translationY = bottomBarHeight
            }
        }
    }

    private fun performBackPress() {
        if (slidingLayout.panelState == SlidingUpPanelLayout.PanelState.EXPANDED
            || slidingLayout.panelState == SlidingUpPanelLayout.PanelState.ANCHORED
        ) {
            slidingLayout.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
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
                    override fun onPreviousClick() {
                    }

                    override fun onPlayClick() {
                    }

                    override fun onNextClick() {
                    }
                }
            }
        }
    }

    private var isPlaying = false

    private fun updateMiniPlayerUi(songData: SongData) {
        miniPlayerLayout.run {
            setSongName(songData.title)
            setArtistName(songData.artistName)
            listener = object : MiniPlayerView.Callback {
                override fun onPreviousClick() {
                }

                override fun onPlayClick() {
//                    togglePlayOrPause(!isPlaying)
                }

                override fun onNextClick() {
                }
            }
        }
    }
}