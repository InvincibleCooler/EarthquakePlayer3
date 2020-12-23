package com.eq.earthquakeplayer3.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.eq.earthquakeplayer3.BaseFragment
import com.eq.earthquakeplayer3.R
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
    private lateinit var closeIv: ImageView

    private var panelState = SlidingUpPanelLayout.PanelState.COLLAPSED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pagerAdapter = MainPagerAdapter(childFragmentManager)
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
                    Log.d(TAG, "onPanelSlide offset : $slideOffset")
                }

                override fun onPanelStateChanged(panel: View?, previousState: SlidingUpPanelLayout.PanelState?, newState: SlidingUpPanelLayout.PanelState?) {
//                    Log.d(TAG, "onPanelStateChanged previousState : $previousState, newState : $newState")
                    when (newState) {
                        SlidingUpPanelLayout.PanelState.COLLAPSED -> {
                            setClickEnable(true)
                            panelState = newState
                        }
                        SlidingUpPanelLayout.PanelState.EXPANDED -> {
                            setClickEnable(false)
                            panelState = newState
                        }
                        else -> {
                        }
                    }
                }
            })

            setFadeOnClickListener {
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
        closeIv = view.findViewById(R.id.close_iv)
        closeIv.run {
            setOnClickListener {
                click2Collapsed()
            }
        }

        bottomBar.setupWithViewPager(viewPager)
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
            isEnabled = clickable
            isClickable = clickable
            isTouchEnabled = clickable
        }
    }

    private fun click2Collapsed() {
        setClickEnable(true)
        slidingLayout.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
    }
}