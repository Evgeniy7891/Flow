package ru.netology.nmedia.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentPhotoBinding
import ru.netology.nmedia.util.StringArg


class PhotoFragment : Fragment() {


    private var _binding : FragmentPhotoBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPhotoBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    //    val urlPhoto = arguments?.getString("MyArg")
        val args: PhotoFragmentArgs by navArgs()
        val urlPhoto = args.myArg
        Glide.with(binding.photoAttachment)
            .load("http://192.168.0.104:9999/media/${urlPhoto}")
            .timeout(10_000)
            .into(binding.photoAttachment)
        binding.photoAttachment.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}