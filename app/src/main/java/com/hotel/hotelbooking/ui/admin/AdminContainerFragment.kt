package com.hotel.hotelbooking.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.hotel.hotelbooking.R
import com.hotel.hotelbooking.databinding.FragmentAdminContainerBinding
import com.hotel.hotelbooking.ui.util.applySystemBarBottomPadding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminContainerFragment : Fragment() {

    private var _binding: FragmentAdminContainerBinding? = null
    private val binding get() = _binding!!

    private lateinit var childNavController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminContainerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navHostFragment = childFragmentManager
            .findFragmentById(R.id.admin_nav_host) as NavHostFragment
        childNavController = navHostFragment.navController

        binding.bottomNav.setupWithNavController(childNavController)
        binding.bottomNav.applySystemBarBottomPadding()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
