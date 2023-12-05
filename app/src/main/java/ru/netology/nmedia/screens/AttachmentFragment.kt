package ru.netology.nmedia.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentAttachmentBinding
import ru.netology.nmedia.screens.FeedFragment.Companion.textArg

class AttachmentFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAttachmentBinding.inflate(inflater, container, false)

        val url = arguments?.textArg ?: ""
        val image = "http://10.0.2.2:9999/media/${url}"

        Glide.with(binding.attachmentImage)
            .load(image)
            .error(R.drawable.ic_error_outline_48)
            .timeout(10_000)
            .into(binding.attachmentImage)

        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_attachmentFragment_to_feedFragment)
        }

        return binding.root
    }
}