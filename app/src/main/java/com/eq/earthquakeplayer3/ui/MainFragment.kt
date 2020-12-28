package com.eq.earthquakeplayer3.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.eq.earthquakeplayer3.BaseFragment
import com.eq.earthquakeplayer3.MainActivity
import com.eq.earthquakeplayer3.R
import com.eq.earthquakeplayer3.utils.ScreenUtils
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
    private lateinit var miniPlayerLayout: View
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
}