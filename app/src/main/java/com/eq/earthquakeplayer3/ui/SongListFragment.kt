package com.eq.earthquakeplayer3.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.eq.earthquakeplayer3.BaseFragment
import com.eq.earthquakeplayer3.EqApp
import com.eq.earthquakeplayer3.R
import com.eq.earthquakeplayer3.data.SongData
import com.eq.earthquakeplayer3.viewmodel.SongListViewModel


class SongListFragment : BaseFragment() {
    companion object {
        private const val TAG = "SongListFragment"

        fun newInstance(): SongListFragment = SongListFragment()
    }

    private lateinit var progressBar: ProgressBar
    private lateinit var songAdapter: SongAdapter

    private val viewModel: SongListViewModel by lazy {
        ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(EqApp.instance)).get(SongListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        songAdapter = SongAdapter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel.isLoading.observe(viewLifecycleOwner) {
            progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }
        viewModel.dataList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                songAdapter.items = it
                songAdapter.notifyDataSetChanged()
            }
        }
        return inflater.inflate(R.layout.fragment_song_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar = view.findViewById(R.id.loading_pb)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.run {
            layoutManager = LinearLayoutManager(context)
            adapter = songAdapter
            setHasFixedSize(true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.cancelJob()
    }

    private inner class SongAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        private val viewTypeItem = 1

        var items = mutableListOf<SongData>()
            set(value) {
                field.clear()
                field = value
            }

        override fun getItemCount() = items.size
        override fun getItemViewType(position: Int) = viewTypeItem

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return ItemViewHolder(
                LayoutInflater.from(context).inflate(R.layout.fragment_song_listitem, parent, false)
            )
        }

        override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
            when (viewHolder.itemViewType) {
                viewTypeItem -> {
                    val vh = viewHolder as ItemViewHolder
                    val data = items[position]

                    val songName = data.title
                    val artistName = data.artistName
                    val contentUri = data.data
                    val albumArtUri = data.albumPath

                    context?.let {
                        Glide.with(it).load(albumArtUri).into(vh.thumbIv)
                    }

                    vh.songNameTv.text = songName
                    vh.artistNameTv.text = artistName

                    vh.itemView.setOnClickListener {

                    }
                }
            }
        }

        private inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val thumbContainer: View = view.findViewById(R.id.thumb_container)
            val thumbIv: ImageView = thumbContainer.findViewById(R.id.iv_thumb)
            val songNameTv: TextView = view.findViewById(R.id.song_name_tv)
            val artistNameTv: TextView = view.findViewById(R.id.artist_name_tv)
        }
    }
}