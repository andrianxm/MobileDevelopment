package com.example.skincure.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.skincure.R
import com.example.skincure.databinding.FragmentFavoriteBinding
import com.example.skincure.di.Injection
import com.example.skincure.ui.ViewModelFactory

class FavoriteFragment : Fragment() {

    private lateinit var favAdapter: FavoriteAdapter
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FavoriteViewModel by viewModels {
        ViewModelFactory(Injection.provideRepository(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObserver()

        viewModel.getAllFavorite()
    }

    private fun setupRecyclerView() {
        binding.favsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        favAdapter = FavoriteAdapter { fav ->
            val bundle = Bundle().apply {
                putString(EXTRA_CAMERAX_IMAGE, fav.imageUri)
                putString(EXTRA_NAME, fav.diseaseName)
                putString(EXTRA_DESCRIPTION, fav.description)
                putString(EXTRA_DATE, fav.timestamp)
                putString(EXTRA_SCORE, fav.predictionScore.toString())
            }
            findNavController().navigate(R.id.action_favorite_to_resultDetail, bundle)
        }
        binding.favsRecyclerView.adapter = favAdapter
    }

    private fun setupObserver() {
        binding.shimmerViewContainer.startShimmer()

        viewModel.favoriteList.observe(viewLifecycleOwner) { favList ->
            binding.shimmerViewContainer.stopShimmer()
            binding.shimmerViewContainer.visibility = View.GONE

            if (favList.isNullOrEmpty()) {
                binding.emptyLayout.visibility = View.VISIBLE
                binding.favsRecyclerView.visibility = View.GONE
            } else {
                binding.emptyLayout.visibility = View.GONE
                binding.favsRecyclerView.visibility = View.VISIBLE
                favAdapter.submitList(favList)
            }
        }
    }

    companion object {
        const val EXTRA_CAMERAX_IMAGE = "CameraX Image"
        const val EXTRA_NAME = "Name"
        const val EXTRA_DESCRIPTION = "Description"
        const val EXTRA_DATE = "Date"
        const val EXTRA_SCORE = "Score"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
