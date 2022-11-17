package ru.netology.nmedia.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.application.adapter.OnInteractionListener
import ru.netology.nmedia.application.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.PostViewModel

@AndroidEntryPoint
class FeedFragment : Fragment() {

   // private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)

    private val viewModel: PostViewModel by activityViewModels()

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(inflater, container, false)

        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onPhoto(post: Post) {
//                with Bundle
//                val bundle = Bundle()
                val photo = post.attachment?.url
//                bundle.putString("MyArg", photo)
                val action = FeedFragmentDirections.actionFeedFragmentToPhotoFragment(photo!!)
                // findNavController().navigate(R.id.action_feedFragment_to_photoFragment, bundle)
                findNavController().navigate(action)

            }

            override fun onEdit(post: Post) {
                viewModel.edit(post)
            }

            override fun onLike(post: Post) {
                if (authViewModel.autorized) {
                    viewModel.likeById(post.id)
                } else {
                    createDialog()
                }
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onShare(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }

                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
            }
        })
        binding.list.adapter = adapter

        viewModel.dataState.observe(viewLifecycleOwner, { state ->
            binding.progress.isVisible = state.loading
            binding.swiperefresh.isRefreshing = state.refreshing
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) { viewModel.loadPosts() }
                    .show()
            }
        })

        viewModel.data.observe(viewLifecycleOwner) { state ->
            val newPost = state.posts.size > adapter.currentList.size
            adapter.submitList(state.posts) {
                if (newPost) {
                    binding.list.scrollToPosition(0)
                }
            }
            binding.emptyText.isVisible = state.empty
            // скролл вверх
            // CoroutineScope(Dispatchers.Main).launch {
            // delay(100)
            // binding.list.scrollToPosition(0)
            // }
        }

        viewModel.newerCount.observe(viewLifecycleOwner) {
            //подсчет непрочитанных постов
            with(binding.buttonNewPost) {
                if (it > 0) {
                    text = "Новый пост - $it"
                    visibility = View.VISIBLE
                }
            }
        }
        binding.buttonNewPost.setOnClickListener {
            // убирается плашка после нажатия
            binding.buttonNewPost.visibility = View.GONE
            // обновления статуса сообщения
            viewModel.newPosts()
            viewModel.loadPosts()
        }

        binding.swiperefresh.setOnRefreshListener {
            viewModel.refreshPosts()
        }

        binding.fab.setOnClickListener {
            if (authViewModel.autorized) {
                findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
            } else {
                createDialog()
            }
        }

        return binding.root
    }
    private fun createDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("You are not logged in. ")
        builder.setMessage("Do you want to come in?")
        builder.setNegativeButton("No") { dialog, i ->
            findNavController().navigate(R.id.feedFragment)
        }
        builder.setPositiveButton("Yes") { dialog, i ->
            findNavController().navigate(R.id.action_feedFragment_to_authorizationFragment)
        }
        builder.show()
    }
}
