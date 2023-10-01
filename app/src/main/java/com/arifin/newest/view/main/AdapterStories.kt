    package com.arifin.newest.view.main

    import android.app.Activity
    import android.content.Intent
    import android.view.LayoutInflater
    import android.view.ViewGroup
    import androidx.paging.PagingDataAdapter
    import androidx.core.app.ActivityOptionsCompat
    import androidx.core.util.Pair
    import androidx.recyclerview.widget.DiffUtil
    import androidx.recyclerview.widget.RecyclerView
    import com.arifin.newest.data.response.ListStoryItem
    import com.arifin.newest.databinding.ItemListBinding
    import com.arifin.newest.view.detail.DetailActivity
    import com.bumptech.glide.Glide

    class StoriesAdapter : PagingDataAdapter<ListStoryItem, StoriesAdapter.ViewHolder>(diffCallback) {

        class ViewHolder(private val binding: ItemListBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bind(story: ListStoryItem) {
                Glide.with(itemView)
                    .load(story.photoUrl)
                    .into(binding.ivItemPhoto)
                binding.tvItemName.text = story.name
                binding.tvStoryDesc.text = story.description

                itemView.setOnClickListener {
                    val intent = Intent(itemView.context, DetailActivity::class.java)
                    intent.putExtra(DetailActivity.NAME, story.name)
                    intent.putExtra(DetailActivity.DESCRIPTION, story.description)
                    intent.putExtra(DetailActivity.PHOTO_URL, story.photoUrl)

                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            itemView.context as Activity,
                            Pair(binding.ivItemPhoto, "image"),
                            Pair(binding.tvItemName, "name"),
                            Pair(binding.tvStoryDesc, "description"),
                        )
                    itemView.context.startActivity(intent, optionsCompat.toBundle())
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val story = getItem(position)
            if (story != null) {
                holder.bind(story)
            }
        }

        companion object {
             val diffCallback = object : DiffUtil.ItemCallback<ListStoryItem>() {
                override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                    return oldItem == newItem
                }
            }
        }
    }